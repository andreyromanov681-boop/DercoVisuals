#!/usr/bin/env python3
"""Apply the requested three-tab ClickGUI, module categories, badge and friend migration."""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src" / "main" / "java"
RES = ROOT / "src" / "main" / "resources"


def fail(message: str) -> None:
    print(f"[derco-ui-update] ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def replace_once(relative: str, old: str, new: str, description: str, expected: int = 1) -> None:
    path = JAVA / relative
    if not path.is_file():
        fail(f"{relative} not found")
    source = path.read_text(encoding="utf-8")
    if new in source:
        return
    count = source.count(old)
    if count != expected:
        fail(f"expected {expected} {description} anchor(s) in {relative}, found {count}")
    path.write_text(source.replace(old, new), encoding="utf-8", newline="\n")
    print(f"[derco-ui-update] patched {relative}: {description}")


def patch_category(relative: str, category: str) -> None:
    path = JAVA / "ai/derco/modules" / relative
    if not path.is_file():
        fail(f"module source not found: {path.relative_to(ROOT)}")
    source = path.read_text(encoding="utf-8")
    target = f"c = ModuleCategory.{category}"
    if target in source:
        return
    old = "c = ModuleCategory.UTILITIES"
    if source.count(old) != 1:
        fail(f"expected one utility category annotation in {path.relative_to(ROOT)}")
    path.write_text(source.replace(old, target, 1), encoding="utf-8", newline="\n")


def update_locale(locale: str, values: dict[str, str]) -> None:
    path = RES / "assets/dercovisuals/lang" / f"{locale}.json"
    path.parent.mkdir(parents=True, exist_ok=True)
    if path.is_file():
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except json.JSONDecodeError as error:
            fail(f"invalid {locale}.json: {error}")
    else:
        data = {}
    data.update(values)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"[derco-ui-update] updated {locale}.json")


def patch_friend_store_migration() -> None:
    relative = "ai/derco/gui/friends/FriendStore.java"
    path = JAVA / relative
    if not path.is_file():
        fail(f"{relative} not found")
    source = path.read_text(encoding="utf-8")

    already_migrated = "migrateLegacyFriendHistory();" in source
    if (
        already_migrated
        and "private static synchronized boolean ensureLoaded()" in source
        and source.count("if (!ensureLoaded()) return List.of();") == 1
        and source.count("if (!ensureLoaded()) return false;") == 3
    ):
        return

    import_anchor = "package ai.derco.gui.friends;\n\n"
    import_block = """package ai.derco.gui.friends;

import ai.derco.config.ConfigJsonCodec;
import ai.derco.config.ConfigState;
import net.minecraft.client.MinecraftClient;
"""
    if source.count(import_anchor) != 1:
        fail("FriendStore package declaration changed")
    source = source.replace(import_anchor, import_block, 1)

    old_loader = """    private static synchronized void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        Path path = file();
        if (!Files.isRegularFile(path)) return;
        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String name = clean(line);
                if (isValidName(name)) FRIENDS.putIfAbsent(key(name), name);
            }
        } catch (IOException exception) {
            LOGGER.warn("Could not read local friends list at {}", path, exception);
        }
    }
"""
    new_loader = """    private static synchronized boolean ensureLoaded() {
        if (loaded) return true;
        Path path = file();
        if (Files.exists(path)) {
            try {
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    String name = clean(line);
                    if (isValidName(name)) FRIENDS.putIfAbsent(key(name), name);
                }
                loaded = true;
                return true;
            } catch (IOException exception) {
                LOGGER.warn("Could not read local friends list at {}", path, exception);
                return false;
            }
        }
        if (!Files.notExists(path)) {
            LOGGER.warn("Could not determine whether local friends list exists at {}", path);
            return false;
        }

        migrateLegacyFriendHistory();
        // The presence of friends.txt marks that the one-time legacy migration was attempted,
        // including installations that had no old friend entries.
        loaded = true;
        save();
        return true;
    }

    private static void migrateLegacyFriendHistory() {
        for (Path directory : legacyConfigDirectories()) {
            if (!Files.isDirectory(directory)) continue;
            java.util.LinkedHashSet<Path> candidates = new java.util.LinkedHashSet<>();
            Path autosave = directory.resolve("ai.derco.cfg");
            if (Files.isRegularFile(autosave)) candidates.add(autosave);
            try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json")) {
                for (Path candidate : stream) {
                    if (Files.isRegularFile(candidate)) candidates.add(candidate);
                }
            } catch (IOException exception) {
                LOGGER.debug("Could not list legacy config directory {}", directory, exception);
            }

            for (Path candidate : candidates) {
                try {
                    String json = Files.readString(candidate, StandardCharsets.UTF_8);
                    if (json.indexOf("friends") < 0) continue;
                    ConfigState legacyState = ConfigJsonCodec.a(json);
                    for (ConfigState.FriendHistoryState entry : legacyState.d()) {
                        if (entry == null) continue;
                        String name = clean(entry.a());
                        if (isValidName(name)) FRIENDS.putIfAbsent(key(name), name);
                    }
                } catch (Exception exception) {
                    LOGGER.debug("Skipping non-legacy friend config {}", candidate, exception);
                }
            }
        }
        if (!FRIENDS.isEmpty()) {
            LOGGER.info("Migrated {} unique friend(s) from legacy ConfigState.FriendHistoryState", FRIENDS.size());
        }
    }

    private static List<Path> legacyConfigDirectories() {
        java.util.LinkedHashSet<Path> directories = new java.util.LinkedHashSet<>();
        directories.add(FabricLoader.getInstance().getConfigDir().resolve("dercovisuals"));

        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.isBlank()) {
            directories.add(Path.of(appData, "DercoVisuals", "config"));
        }

        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.runDirectory != null) {
                directories.add(client.runDirectory.toPath().resolve("config").resolve("dercovisuals"));
            }
        } catch (RuntimeException exception) {
            LOGGER.debug("Minecraft run directory is not available during friend migration", exception);
        }
        return List.copyOf(directories);
    }"""
    new_ensure = new_loader.split("\n\n    private static void migrateLegacyFriendHistory()", 1)[0]
    if already_migrated:
        if "private static synchronized void ensureLoaded()" in source:
            ensure_start = source.find("    private static synchronized void ensureLoaded() {")
            ensure_end = source.find("\n    private static void migrateLegacyFriendHistory()", ensure_start)
            if ensure_start < 0 or ensure_end < 0:
                fail("expected existing FriendStore migration loader boundary not found")
            source = source[:ensure_start] + new_ensure + source[ensure_end:]
        elif "private static synchronized boolean ensureLoaded()" not in source:
            fail("existing FriendStore migration loader has an unknown signature")
    else:
        if source.count(old_loader) != 1:
            fail("expected FriendStore one-time loader block not found")
        source = source.replace(old_loader, new_loader, 1)

    method_guards = (
        ("""    public static synchronized List<String> getNames() {
        ensureLoaded();
        return List.copyOf(FRIENDS.values());
    }""",
         """    public static synchronized List<String> getNames() {
        if (!ensureLoaded()) return List.of();
        return List.copyOf(FRIENDS.values());
    }"""),
        ("""    public static synchronized boolean isFriendName(String name) {
        ensureLoaded();
        String normalized = key(name);
        return !normalized.isEmpty() && FRIENDS.containsKey(normalized);
    }""",
         """    public static synchronized boolean isFriendName(String name) {
        if (!ensureLoaded()) return false;
        String normalized = key(name);
        return !normalized.isEmpty() && FRIENDS.containsKey(normalized);
    }"""),
        ("""    public static synchronized boolean add(String name) {
        ensureLoaded();
        String value = clean(name);""",
         """    public static synchronized boolean add(String name) {
        if (!ensureLoaded()) return false;
        String value = clean(name);"""),
        ("""    public static synchronized boolean remove(String name) {
        ensureLoaded();
        String normalized = key(name);
        String previous = FRIENDS.remove(normalized);
        if (previous == null) return false;
        if (save()) return true;
        FRIENDS.put(normalized, previous);
        return false;
    }""",
         """    public static synchronized boolean remove(String name) {
        if (!ensureLoaded()) return false;
        String normalized = key(name);
        String previous = FRIENDS.remove(normalized);
        if (previous == null) return false;
        if (save()) return true;
        FRIENDS.put(normalized, previous);
        return false;
    }"""),
    )
    for old_method, new_method in method_guards:
        if new_method in source:
            continue
        if source.count(old_method) != 1:
            fail("expected exactly one FriendStore load guard target")
        source = source.replace(old_method, new_method, 1)
    path.write_text(source, encoding="utf-8", newline="\n")
    print("[derco-ui-update] added one-time ConfigState.FriendHistoryState migration with read-failure guards")


def patch_clickgui_navigation() -> None:
    replace_once(
        "ai/derco/gui/core/ClickGuiTabType.java",
        '''   MODULES("modules", "\\ue916"),
   MARKERS("markers", "\\ue915"),
   FRIENDS("friends", "\\ue90d"),
   CONFIGS("configs", "\\ue905");''',
        '''   MODULES("modules", "\\ue916"),
   FRIENDS("friends", "\\ue90d"),
   SETTINGS("settings", "\\ue905");''',
        "three top-level ClickGUI tabs",
    )

    screen = "ai/derco/gui/core/DercoVisualsClickGuiScreen.java"
    replace_once(
        screen,
        '''      this.l.put(ClickGuiTabType.MODULES, new ModulesTab());
      this.l.put(ClickGuiTabType.MARKERS, new MarkersTab());
      this.l.put(ClickGuiTabType.FRIENDS, new FriendsTab());
      this.l.put(ClickGuiTabType.CONFIGS, new ConfigsTab());''',
        '''      this.l.put(ClickGuiTabType.MODULES, new ModulesTab());
      this.l.put(ClickGuiTabType.FRIENDS, new FriendsTab());
      this.l.put(ClickGuiTabType.SETTINGS, new SettingsTab());''',
        "ClickGUI tab registration",
    )

    old_exit = '''         if (clickGuiTabA instanceof MarkersTab) {
            ((MarkersTab)clickGuiTabA).e();
         }
'''
    new_exit = '''         if (clickGuiTabA instanceof SettingsTab settingsTab) {
            settingsTab.onTabExit();
         }
'''
    replace_once(screen, old_exit, new_exit, "settings cleanup on top-level tab switch", expected=2)

    old_extras = '''         if (clickGuiTabA instanceof MarkersTab) {
            ((MarkersTab)clickGuiTabA).a(MatrixStackVarGetMatrices, render, iA, iB);
         }

         if (clickGuiTabA instanceof ConfigsTab) {
            ((ConfigsTab)clickGuiTabA).a(MatrixStackVarGetMatrices, render);
         }
'''
    new_extras = '''         if (clickGuiTabA instanceof SettingsTab settingsTab) {
            settingsTab.renderOverlay(MatrixStackVarGetMatrices, render, iA, iB);
         }
'''
    replace_once(screen, old_extras, new_extras, "Settings sections overlay routing")

    old_cleanup = '''      if (clickGuiTabA instanceof MarkersTab) {
         ((MarkersTab)clickGuiTabA).e();
         ((MarkersTab)clickGuiTabA).f();
      }
'''
    new_cleanup = '''      if (clickGuiTabA instanceof SettingsTab settingsTab) {
         settingsTab.onTabExit();
      }
'''
    replace_once(screen, old_cleanup, new_cleanup.replace("onTabExit", "onScreenClose"), "settings cleanup on close/reset", expected=2)

    old_char = '''      } else if (clickGuiTabA instanceof MarkersTab && ((MarkersTab)clickGuiTabA).a(c2, i)) {
         return true;
      } else if (clickGuiTabA instanceof FriendsTab && ((FriendsTab)clickGuiTabA).a(c2, i)) {
'''
    new_char = '''      } else if (clickGuiTabA instanceof SettingsTab settingsTab && settingsTab.a(c2, i)) {
         return true;
      } else if (clickGuiTabA instanceof FriendsTab && ((FriendsTab)clickGuiTabA).a(c2, i)) {
'''
    replace_once(screen, old_char, new_char, "Settings keyboard text routing")

    old_getter = '''   public ConfigsTab i() {
      ClickGuiTab clickGuiTab = this.l.get(ClickGuiTabType.CONFIGS);
      return clickGuiTab instanceof ConfigsTab ? (ConfigsTab)clickGuiTab : null;
   }
'''
    new_getter = '''   public ConfigsTab i() {
      ClickGuiTab clickGuiTab = this.l.get(ClickGuiTabType.SETTINGS);
      return clickGuiTab instanceof SettingsTab settingsTab ? settingsTab.configsTab() : null;
   }
'''
    replace_once(screen, old_getter, new_getter, "legacy ConfigsTab accessor")

    # Retarget the existing marker/config pages into the Settings top-level tab.
    replace_once(
        "ai/derco/gui/markers/MarkersTab.java",
        "return ClickGuiTabType.MARKERS;",
        "return ClickGuiTabType.SETTINGS;",
        "MarkersTab enum ownership",
    )
    replace_once(
        "ai/derco/gui/config/ConfigsTab.java",
        "return ClickGuiTabType.CONFIGS;",
        "return ClickGuiTabType.SETTINGS;",
        "ConfigsTab enum ownership",
    )

    # Marker sub-tabs stay visible as a second row beneath Settings' own section selector.
    markers = "ai/derco/gui/markers/MarkersTab.java"
    replace_once(
        markers,
        'private static final String[] c = new String[]{"Метки", "Настройки"};',
        'private static final String[] c = new String[]{"Список меток", "Настройки меток"};',
        "clarify nested marker section labels",
    )
    replace_once(
        markers,
        "   private boolean o = false;\n",
        "   private boolean o = false;\n   private boolean embedded = false;\n",
        "embedded marker selector flag",
    )
    replace_once(
        markers,
        "      this.h.a(MatrixStackVar, renderer2D, f2, f3, i, i2);",
        "      this.h.a(MatrixStackVar, renderer2D, f2, f3 + (this.embedded ? 17.0F : 0.0F), i, i2);",
        "nested marker selector render offset",
    )
    replace_once(
        markers,
        "      this.h.a(f2, f3, i, i2);",
        "      this.h.a(f2, f3 + (this.embedded ? 17.0F : 0.0F), i, i2);",
        "nested marker selector click offset",
    )
    marker_setter = '''   public void setEmbedded(boolean embedded) {
      this.embedded = embedded;
   }

'''
    setter_anchor = "   public void e() {\n      this.l.c();\n   }\n"
    replace_once(markers, setter_anchor, marker_setter + setter_anchor, "marker embedded-mode setter")

    configs = "ai/derco/gui/config/ConfigsTab.java"
    replace_once(
        configs,
        "   private boolean t = false;\n",
        "   private boolean t = false;\n   private boolean embedded = false;\n",
        "embedded config selector flag",
    )
    replace_once(
        configs,
        "      this.k.a(MatrixStackVar, renderer2D, f2, f3, i, i2);",
        "      if (!this.embedded) this.k.a(MatrixStackVar, renderer2D, f2, f3, i, i2);",
        "hide redundant nested config selector",
    )
    replace_once(
        configs,
        "         this.k.a(f2, f3, i, i2);",
        "         if (!this.embedded) this.k.a(f2, f3, i, i2);",
        "hide redundant nested config selector input",
    )
    config_setter = '''   public void setEmbedded(boolean embedded) {
      this.embedded = embedded;
   }

'''
    setter_anchor = "   public void e() {\n      this.h();\n   }\n"
    replace_once(configs, setter_anchor, config_setter + setter_anchor, "config embedded-mode setter")

    # Add the persistent Settings wrapper, delegating the existing pages without duplicating their features.
    settings_tab = r'''package ai.derco.gui.core;

import org.joml.Matrix3x2fStack;
import ai.derco.gui.config.ConfigsTab;
import ai.derco.gui.markers.MarkersTab;
import ai.derco.render.Renderer2D;

/** Combines the former Markers and Configs top-level pages under Settings. */
public final class SettingsTab implements TabHost, ClickGuiTab {
    private static final String[] SECTIONS = {"Метки", "Конфигурации"};
    private final TabSelector selector = new TabSelector(this);
    private final MarkersTab markers = new MarkersTab();
    private final ConfigsTab configs = new ConfigsTab();
    private int selected;

    public SettingsTab() {
        markers.setEmbedded(true);
        configs.setEmbedded(true);
    }

    private ClickGuiTab selectedTab() {
        return selected == 0 ? markers : configs;
    }

    @Override
    public String[] c() {
        return SECTIONS;
    }

    @Override
    public int d() {
        return selected;
    }

    @Override
    public void a(int index) {
        if (index < 0 || index >= SECTIONS.length || selected == index) return;
        if (selected == 0) {
            markers.e();
            markers.f();
        }
        selected = index;
    }

    @Override
    public void a(Matrix3x2fStack matrices, Renderer2D renderer, float x, float y, int mouseX, int mouseY) {
        selector.a(matrices, renderer, x, y, mouseX, mouseY);
        selectedTab().a(matrices, renderer, x, y, mouseX, mouseY);
    }

    public void renderOverlay(Matrix3x2fStack matrices, Renderer2D renderer, int mouseX, int mouseY) {
        if (selected == 0) {
            markers.a(matrices, renderer, mouseX, mouseY);
        } else {
            configs.a(matrices, renderer);
        }
    }

    @Override
    public void a(float x, float y, int mouseX, int mouseY) {
        int previous = selected;
        selector.a(x, y, mouseX, mouseY);
        if (previous != selected) return;
        selectedTab().a(x, y, mouseX, mouseY);
    }

    @Override
    public void b(float x, float y, int mouseX, int mouseY) {
        selectedTab().b(x, y, mouseX, mouseY);
    }

    @Override
    public void c(float x, float y, int mouseX, int mouseY) {
        selectedTab().c(x, y, mouseX, mouseY);
    }

    @Override
    public void a(float x, float y, int mouseX, int mouseY, double deltaX, double deltaY) {
        selectedTab().a(x, y, mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void a(float amount) {
        selectedTab().a(amount);
    }

    @Override
    public boolean a(int keyCode, int scanCode, int modifiers) {
        return selectedTab().a(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean b() {
        return selectedTab().b();
    }

    public boolean a(char character, int modifiers) {
        return selected == 0 ? markers.a(character, modifiers) : configs.a(character, modifiers);
    }

    public void onTabExit() {
        markers.e();
    }

    public void onScreenClose() {
        markers.e();
        markers.f();
    }

    public ConfigsTab configsTab() {
        return configs;
    }

    @Override
    public ClickGuiTabType a() {
        return ClickGuiTabType.SETTINGS;
    }
}
'''
    settings_path = JAVA / "ai/derco/gui/core/SettingsTab.java"
    if settings_path.exists():
        existing = settings_path.read_text(encoding="utf-8")
        if existing != settings_tab:
            fail("SettingsTab.java already exists with different contents")
    else:
        settings_path.parent.mkdir(parents=True, exist_ok=True)
        settings_path.write_text(settings_tab, encoding="utf-8", newline="\n")

    print("[derco-ui-update] added SettingsTab wrapper with Markers/Configs sections")


def patch_categories() -> None:
    replace_once(
        "ai/derco/module/ModuleCategory.java",
        '''public enum ModuleCategory {
   PVP("PvP"),
   VISUALS("Visuals"),
   HUD("HUD"),
   UTILITIES("Utilities");''',
        '''public enum ModuleCategory {
   PVP("PvP"),
   VISUALS("Visuals"),
   UTILITIES("Utility"),
   HUD("HUD"),
   HELPER("Helper"),
   OTHER("Other");''',
        "ModuleCategory order and values",
    )

    replace_once(
        "ai/derco/gui/modules/ModulesTab.java",
        'private static final String[] a = new String[]{"PvP", "Visuals", "HUD", "Utilities"};',
        'private static final String[] a = new String[]{"PvP", "Visuals", "Utility", "HUD", "Helper", "Other"};',
        "module category labels",
    )
    replace_once(
        "ai/derco/gui/modules/ModulesTab.java",
        "private static final ModuleCategory[] b = new ModuleCategory[]{ModuleCategory.PVP, ModuleCategory.VISUALS, ModuleCategory.HUD, ModuleCategory.UTILITIES};",
        "private static final ModuleCategory[] b = new ModuleCategory[]{ModuleCategory.PVP, ModuleCategory.VISUALS, ModuleCategory.UTILITIES, ModuleCategory.HUD, ModuleCategory.HELPER, ModuleCategory.OTHER};",
        "module category order",
    )

    assignments = {
        "PVP": [
            "utilities/PvpSafe.java",
            "utilities/AutoDuel.java",
            "utilities/Predictions.java",
            "utilities/ShiftTab.java",
        ],
        "HELPER": [
            "utilities/AucHelper.java",
            "utilities/ChatHelper.java",
            "utilities/FcHelper.java",
            "utilities/FtHelper.java",
            "utilities/HealingHelper.java",
            "utilities/HwHelper.java",
            "utilities/RwHelper.java",
            "utilities/GpsCommand.java",
            "utilities/ArmorNotifier.java",
            "utilities/ItemHighlighter.java",
            "utilities/ItemPickupLogger.java",
            "utilities/TotemTracker.java",
            "utilities/DeathMarker.java",
        ],
        "OTHER": [
            "utilities/AutoReconnect.java",
            "utilities/AutoReissue.java",
            "utilities/AutoInvest.java",
            "utilities/FakePlayer.java",
            "utilities/FreeLook.java",
            "utilities/StreamerMode.java",
            "utilities/SoundController.java",
            "utilities/CommandSafe.java",
            "utilities/RwJoiner.java",
        ],
    }
    for category, modules in assignments.items():
        for relative in modules:
            patch_category(relative, category)

    # Category selector icons use the already packaged combat / misc texture aliases.
    replace_once(
        "ai/derco/gui/core/TabSelector.java",
        '''      return switch (label) {
         case "Visuals" -> "category_visuals";
         case "HUD" -> "category_hud";
         case "Utilities" -> "category_utilities";
         default -> null;
      };''',
        '''      return switch (label) {
         case "PvP" -> "category_combat";
         case "Visuals" -> "category_visuals";
         case "Utility" -> "category_utilities";
         case "HUD" -> "category_hud";
         case "Helper", "Other" -> "category_misc";
         default -> null;
      };''',
        "category selector icon mapping",
    )

    replace_once(
        "ai/derco/render/icons/IconTextureRegistry.java",
        '         register("configs", "textures/clickgui/configs.png");\n',
        '         register("configs", "textures/clickgui/configs.png");\n         register("settings", "textures/clickgui/settings.png");\n',
        "Settings dock icon registration",
    )


def patch_no_friend_damage_badge() -> None:
    relative = "ai/derco/gui/modules/ModuleRowRenderer.java"
    path = JAVA / relative
    source = path.read_text(encoding="utf-8")
    if "module.dercovisuals.no_friend_damage.always_enabled" in source:
        return

    import_anchor = "import net.minecraft.client.gui.DrawContext;\n"
    if source.count(import_anchor) != 1:
        fail("ModuleRowRenderer DrawContext import anchor not found")
    source = source.replace(import_anchor, import_anchor + "import net.minecraft.text.Text;\n", 1)

    old = '''      if (!"NoFriendDamage".equalsIgnoreCase(guiEntry.a())) {
         this.a(MatrixStackVar, renderer2D, f6, f7, fJ2);
      }
'''
    new = '''      if ("NoFriendDamage".equalsIgnoreCase(guiEntry.a())) {
         String badgeText = Text.translatable("module.dercovisuals.no_friend_damage.always_enabled").getString();
         FontRenderer badgeFont = FontManager.b[14];
         float badgeWidth = badgeFont.a(badgeText) + 8.0F;
         float badgeX = f2 + 12.0F;
         float badgeY = f3 + f5 / 2.0F + 2.0F;
         Color badgeBorder = new Color(42, 151, 255, 180);
         Color badgeBackground = new Color(10, 46, 82, 225);
         renderer2D.a(badgeX, badgeY, badgeWidth, 9.0F, 4.5F,
               badgeBorder, badgeBorder, badgeBackground, badgeBackground, MatrixStackVar);
         badgeFont.a(MatrixStackVar, badgeText, badgeX + 4.0F, badgeY + 1.0F,
               new Color(177, 218, 255));
      } else {
         this.a(MatrixStackVar, renderer2D, f6, f7, fJ2);
      }
'''
    if source.count(old) != 1:
        fail("NoFriendDamage hidden-switch render anchor not found")
    source = source.replace(old, new, 1)
    path.write_text(source, encoding="utf-8", newline="\n")
    print("[derco-ui-update] replaced NoFriendDamage empty switch with localized always-enabled badge")


def main() -> None:
    patch_friend_store_migration()
    patch_clickgui_navigation()
    patch_categories()
    patch_no_friend_damage_badge()

    update_locale("en_us", {
        "module.dercovisuals.no_friend_damage.always_enabled": "Always enabled",
    })
    update_locale("ru_ru", {
        "module.dercovisuals.no_friend_damage.always_enabled": "Всегда включён",
    })
    print("[derco-ui-update] requested GUI and category changes ready")


if __name__ == "__main__":
    main()
