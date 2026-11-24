#!/usr/bin/env python3
"""Fail CI if any registered DercoVisuals texture is missing from the built JAR."""
from __future__ import annotations

from io import BytesIO
import hashlib
from pathlib import Path
from zipfile import ZipFile
import json
import re
import sys

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
BUILD_LIBS = ROOT / "build" / "libs"
REGISTRY = ROOT / "src" / "main" / "java" / "ai" / "derco" / "render" / "icons" / "IconTextureRegistry.java"
MANIFEST = ROOT / "tools" / "assets" / "png" / "asset-manifest.json"
REQUIRED = {
    "textures/icon.png",
    "textures/ai.derco.png",
    "textures/derco_logo.png",
    "textures/clickgui/derco_water.png",
    "textures/clickgui/derco_ico.png",
    "textures/account_modal.png",
}
PREFIX = "assets/dercovisuals/"


def fail(message: str) -> None:
    raise SystemExit("DercoVisuals JAR verification failed: " + message)


def main() -> None:
    jars = sorted(
        path for path in BUILD_LIBS.glob("DercoVisuals-*.jar")
        if not path.name.endswith("-sources.jar")
    )
    if len(jars) != 1:
        fail(f"expected exactly one runtime JAR in {BUILD_LIBS}, found {len(jars)}")

    if not REGISTRY.is_file():
        fail("generated IconTextureRegistry.java is missing")
    if not MANIFEST.is_file():
        fail("generated asset-manifest.json is missing")

    registry_source = REGISTRY.read_text(encoding="utf-8")
    refs = set(re.findall(
        r'register\(\s*"[^"]+"\s*,\s*"([^"]+)"\s*\)',
        registry_source,
    ))
    manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
    generated = {path for group in manifest.values() for path in group}

    if len(generated) != 42:
        fail(f"manifest must describe 42 distinct PNGs, got {len(generated)}")
    if len(refs) < 1:
        fail("no texture paths were found in IconTextureRegistry")
    if not REQUIRED.issubset(generated):
        fail("asset manifest is missing one or more mandatory legacy texture paths")

    generated_java = ROOT / "src" / "main" / "java" / "ai" / "derco"
    tab_enum = (generated_java / "gui/core/ClickGuiTabType.java").read_text(encoding="utf-8")
    tab_values = re.findall(r"^\s*(MODULES|FRIENDS|SETTINGS)\(", tab_enum, re.M)
    if tab_values != ["MODULES", "FRIENDS", "SETTINGS"]:
        fail(f"ClickGUI must expose exactly MODULES, FRIENDS, SETTINGS in order; got {tab_values}")

    category_enum = (generated_java / "module/ModuleCategory.java").read_text(encoding="utf-8")
    category_values = re.findall(r"^\s*(PVP|VISUALS|UTILITIES|HUD|HELPER|OTHER)\(", category_enum, re.M)
    if category_values != ["PVP", "VISUALS", "UTILITIES", "HUD", "HELPER", "OTHER"]:
        fail(f"module category order is unexpected: {category_values}")

    modules_tab = (generated_java / "gui/modules/ModulesTab.java").read_text(encoding="utf-8")
    expected_categories = ["PvP", "Visuals", "Utility", "HUD", "Helper", "Other"]
    if not all(f'"{label}"' in modules_tab for label in expected_categories):
        fail("ModulesTab does not expose all six requested categories")

    potions_module = (generated_java / "modules/hud/PotionsHud.java").read_text(encoding="utf-8")
    expected_potion_sort_modes = (
        'new String[]{"Скоро закончатся", "Дольше действуют", "По названию"}, 0'
    )
    if (
        "import ai.derco.settings.ModeSetting;" not in potions_module
        or '"Сортировка"' not in potions_module
        or expected_potion_sort_modes not in potions_module
        or "public int sortMode()" not in potions_module
        or "this.sortMode.k()" not in potions_module
    ):
        fail("Potion HUD sorting setting is missing or has unexpected modes/default")

    potions_element = (generated_java / "hud/elements/PotionsHudElement.java").read_text(encoding="utf-8")
    if "ArrayList<PotionsHudElement.EffectEntry> arrayList = new ArrayList<>();" not in potions_element:
        fail("Potion HUD effect collection must retain its typed list declaration")
    render_start = potions_element.find(
        "public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2)"
    )
    render_update = potions_element.find("this.a();", render_start)
    render_preview = potions_element.find("boolean zW = this.previewPlaceholder;", render_start)
    if (
        "private boolean isEditingScreen()" not in potions_element
        or "private boolean previewPlaceholder = false;" not in potions_element
        or "this.isEditingScreen() && listY.isEmpty()" not in potions_element
        or "this.previewPlaceholder = zW;" not in potions_element
        or potions_element.count("this.y()") != 1
        or "this.w()" in potions_element
        or render_start < 0
        or render_update < render_start
        or render_preview < render_update
    ):
        fail("Potion HUD must build one active-effect snapshot per frame and reuse its preview state")

    potion_sort_start = potions_element.find(
        "int sortMode = potionsHud != null ? potionsHud.sortMode() : 0;"
    )
    potion_shortest = potions_element.find(
        "arrayList.sort(Comparator.comparingInt(effectEntry ->",
        potion_sort_start,
    )
    potion_infinite_guard = potions_element.find(
        "effectEntry.d < 0 ? Integer.MAX_VALUE : effectEntry.d",
        potion_sort_start,
    )
    potion_longest = potions_element.find(
        "effectEntry.d < 0 ? Integer.MAX_VALUE : effectEntry.d).reversed()",
        potion_sort_start,
    )
    potion_alphabetical = potions_element.find(
        "Comparator.comparing((PotionsHudElement.EffectEntry effectEntry) -> effectEntry.b, String.CASE_INSENSITIVE_ORDER)",
        potion_sort_start,
    )
    potion_limit = potions_element.find(
        "arrayList.subList(maxVisibleEffects, arrayList.size()).clear();",
        potion_sort_start,
    )
    potion_return = potions_element.find("return arrayList;", potion_limit)
    if (
        potion_sort_start < 0
        or min(potion_shortest, potion_longest, potion_alphabetical) < potion_sort_start
        or potion_infinite_guard < potion_sort_start
        or potion_limit < max(potion_shortest, potion_longest, potion_alphabetical)
        or potion_return < potion_limit
    ):
        fail("Potion HUD must support all three sort modes before applying its visible-entry limit")

    target_hud = (generated_java / "hud/elements/TargetHudElement.java").read_text(encoding="utf-8")
    target_update_start = target_hud.find("private void updateTarget() {")
    target_update_end = target_hud.find("private void updateCachedHealthText()", target_update_start)
    target_identity_start = target_hud.find("private void updateTargetIdentity(LivingEntity entity) {")
    target_identity_end = target_hud.find("private LivingEntity validTarget", target_identity_start)
    if min(target_update_start, target_update_end, target_identity_start, target_identity_end) < 0:
        fail("Target HUD identity-cache methods are missing")
    target_update_body = target_hud[target_update_start:target_update_end]
    target_identity_body = target_hud[target_identity_start:target_identity_end]
    if (
        "import net.minecraft.text.Text;" not in target_hud
        or "private LivingEntity cachedNameEntity;" not in target_hud
        or "private Text cachedCustomName;" not in target_hud
        or target_update_body.count("this.updateTargetIdentity(currentTarget);") != 1
        or target_update_body.find("this.updateTargetIdentity(currentTarget);") < target_update_body.find("this.targetMaxHealth =")
        or "this.cachedNameEntity = null;" not in target_update_body
        or "this.cachedCustomName = null;" not in target_update_body
        or "if (entity != this.cachedNameEntity || customName != this.cachedCustomName)" not in target_identity_body
        or target_identity_body.count("entity.getName().getString()") != 1
        or "      }\n      this.skinTexture =" not in target_identity_body
    ):
        fail("Target HUD must cache stable name text while continuing to refresh asynchronously loaded skin textures")

    camera_mixin = (generated_java / "mixin/CameraMixin.java").read_text(encoding="utf-8")
    expected_camera_rotation = (
        '@Redirect(method = "update", at = @At(value = "INVOKE", '
        'target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0), require = 1)'
    )
    expected_camera_distance = (
        '@ModifyArg(method = "update", at = @At(value = "INVOKE", '
        'target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"), index = 0, require = 1)'
    )
    if (
        camera_mixin.count(expected_camera_rotation) != 1
        or camera_mixin.count(expected_camera_distance) != 1
        or "Lnet/minecraft/Camera;" in camera_mixin
    ):
        fail("FreeLook and perspective-distance hooks must target the exact Yarn 1.21.11 Camera methods")

    chat_hud_mixin = (generated_java / "mixin/ChatHudMixin.java").read_text(encoding="utf-8")
    expected_chat_redirect = """   @Redirect(
      require = 1,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)V")
   )"""
    if (
        chat_hud_mixin.count(expected_chat_redirect) != 1
        or "Lnet/minecraft/DrawContext;drawTextWithShadow" in chat_hud_mixin
    ):
        fail("Chat message animation must redirect the exact Yarn 1.21.11 DrawContext OrderedText overload")

    tab_mixin = (generated_java / "mixin/PlayerListHudMixin.java").read_text(encoding="utf-8")
    expected_tab_draw = """   @Redirect(
      require = 1,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V")
   )"""
    expected_tab_encrypted = '@Redirect(require = 1, method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))'
    expected_tab_footer = """   @Redirect(
      require = 1,
      method = "render",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;footer:Lnet/minecraft/text/Text;", opcode = 180)
   )"""
    if (
        tab_mixin.count(expected_tab_draw) != 1
        or tab_mixin.count(expected_tab_encrypted) != 1
        or tab_mixin.count(expected_tab_footer) != 1
        or "Lnet/minecraft/DrawContext;drawTextWithShadow" in tab_mixin
        or "Lnet/minecraft/ClientConnection;isEncrypted" in tab_mixin
        or "Lnet/minecraft/PlayerListHud;footer" in tab_mixin
    ):
        fail("Tab customization must use exact Yarn 1.21.11 DrawContext, ClientConnection, and PlayerListHud selectors")

    cooldowns_module = (generated_java / "modules/hud/CooldownsHud.java").read_text(encoding="utf-8")
    if (
        '"Макс. кулдаунов"' not in cooldowns_module
        or "4.0F, 1.0F, 8.0F, 1.0F" not in cooldowns_module
        or "public int maxVisibleCooldowns()" not in cooldowns_module
        or "this.maxVisibleCooldowns.roundedInt()" not in cooldowns_module
    ):
        fail("Cooldowns HUD configurable visible-entry limit is missing or has unexpected bounds")

    cooldowns_element = (generated_java / "hud/elements/CooldownsHudElement.java").read_text(encoding="utf-8")
    cooldown_sort = cooldowns_element.find(
        "arrayList.sort(Comparator.comparingInt(cooldownSnapshot -> cooldownSnapshot.e));"
    )
    cooldown_limit = cooldowns_element.find(
        "arrayList.subList(maxVisibleCooldowns, arrayList.size()).clear();"
    )
    cooldown_return = cooldowns_element.find("return arrayList;", cooldown_limit)
    if (
        "ModuleRegistry.COOLDOWNS_HUD" not in cooldowns_element
        or cooldown_sort < 0
        or cooldown_limit < cooldown_sort
        or cooldown_return < cooldown_limit
    ):
        fail("Cooldowns HUD must sort by remaining time before applying the configured visible-entry limit")

    cooldown_snapshot_assignment = cooldowns_element.find("this.Y = listX;")
    cooldown_update_preview = cooldowns_element.find("boolean zV = this.v();", cooldown_snapshot_assignment)
    cooldown_render_start = cooldowns_element.find(
        "public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2)"
    )
    cooldown_render_update = cooldowns_element.find("this.a();", cooldown_render_start)
    cooldown_render_preview = cooldowns_element.find("boolean zV = this.v();", cooldown_render_update)
    if (
        cooldowns_element.count("this.x()") != 1
        or "&& this.Y.isEmpty()" not in cooldowns_element
        or "&& this.x().isEmpty()" in cooldowns_element
        or cooldown_snapshot_assignment < 0
        or cooldown_update_preview < cooldown_snapshot_assignment
        or cooldown_render_start < 0
        or cooldown_render_update < cooldown_render_start
        or cooldown_render_preview < cooldown_render_update
    ):
        fail("Cooldowns HUD must reuse its single collected cooldown snapshot for preview state and rendering")

    friends_store = (generated_java / "gui/friends/FriendStore.java").read_text(encoding="utf-8")
    if "migrateLegacyFriendHistory()" not in friends_store or "ConfigJsonCodec.a(json)" not in friends_store:
        fail("FriendStore legacy ConfigState.FriendHistoryState migration is missing")

    friends_panel = (generated_java / "gui/friends/FriendsPanel.java").read_text(encoding="utf-8")
    remove_signature = "public void b(HistoryEntry historyEntry) {"
    if friends_panel.count(remove_signature) != 1:
        fail("FriendsPanel friend-removal handler is missing or duplicated")
    remove_start = friends_panel.find(remove_signature)
    remove_end = friends_panel.find(chr(10) + "   }", remove_start)
    if remove_end < 0:
        fail("FriendsPanel friend-removal handler is incomplete")
    remove_body = friends_panel[remove_start:remove_end]
    persistence_guard = "if (!FriendStore.remove(name)) return;"
    guard_index = remove_body.find(persistence_guard)
    if guard_index < 0:
        fail("FriendsPanel must confirm persistent friend removal before updating the UI")
    ui_removals = [remove_body.find(anchor) for anchor in ("this.r.removeIf", "this.s.removeIf")]
    if any(position < 0 or position < guard_index for position in ui_removals):
        fail("FriendsPanel removes friend cards before persistent removal succeeds")

    row_renderer = (generated_java / "gui/modules/ModuleRowRenderer.java").read_text(encoding="utf-8")
    if "module.dercovisuals.no_friend_damage.always_enabled" not in row_renderer:
        fail("NoFriendDamage always-enabled badge is missing from the module row renderer")

    with ZipFile(jars[0]) as jar:
        names = set(jar.namelist())
        required_classes = {
            "ai/derco/gui/friends/FriendStore.class",
            "ai/derco/gui/friends/FriendLookup.class",
            "ai/derco/gui/friends/FriendsPanel.class",
            "ai/derco/protection/NoFriendDamage.class",
            "ai/derco/mixin/NoFriendDamageMixin.class",
            "ai/derco/modules/pvp/NoFriendDamageModule.class",
            "ai/derco/gui/core/SettingsTab.class",
            "ai/derco/mixin/CameraMixin.class",
            "ai/derco/mixin/ChatHudMixin.class",
            "ai/derco/mixin/PlayerListHudMixin.class",
        }
        missing_classes = sorted(required_classes - names)
        if missing_classes:
            fail("friend protection classes absent from runtime JAR: " + ", ".join(missing_classes))

        mixin_config_name = "dercovisuals.mixins.json"
        if mixin_config_name not in names:
            fail("dercovisuals.mixins.json is missing from the runtime JAR")
        mixin_config = json.loads(jar.read(mixin_config_name))
        client_mixins = mixin_config.get("client")
        if mixin_config.get("package") != "ai.derco.mixin" or not isinstance(client_mixins, list):
            fail("runtime Mixin config has an unexpected package/client list")
        if client_mixins.count("NoFriendDamageMixin") != 1:
            fail("NoFriendDamageMixin must be registered exactly once in the client Mixin list")
        if client_mixins.count("CameraMixin") != 1:
            fail("CameraMixin must be registered exactly once in the client Mixin list")
        if client_mixins.count("ChatHudMixin") != 1:
            fail("ChatHudMixin must be registered exactly once in the client Mixin list")
        if client_mixins.count("PlayerListHudMixin") != 1:
            fail("PlayerListHudMixin must be registered exactly once in the client Mixin list")
        missing = sorted(PREFIX + ref for ref in refs if PREFIX + ref not in names)
        if missing:
            fail("registered resources absent from runtime JAR: " + ", ".join(missing))

        missing_generated = sorted(PREFIX + path for path in generated if PREFIX + path not in names)
        if missing_generated:
            fail("generated PNGs absent from runtime JAR: " + ", ".join(missing_generated))

        metadata_name = "fabric.mod.json"
        if metadata_name not in names:
            fail("fabric.mod.json is missing from the runtime JAR")
        metadata = json.loads(jar.read(metadata_name))
        icon_path = metadata.get("icon")
        if not isinstance(icon_path, str) or icon_path not in names:
            fail(f"Fabric metadata icon does not resolve inside the JAR: {icon_path!r}")
        if icon_path != PREFIX + "textures/icon.png":
            fail(f"Fabric metadata icon points to an unexpected path: {icon_path!r}")

        for locale, expected_label in (("en_us", "Always enabled"), ("ru_ru", "Всегда включён")):
            lang_path = f"assets/dercovisuals/lang/{locale}.json"
            if lang_path not in names:
                fail(f"{locale}.json is missing from the runtime JAR")
            lang_data = json.loads(jar.read(lang_path))
            if lang_data.get("module.dercovisuals.no_friend_damage.always_enabled") != expected_label:
                fail(f"{locale}.json does not contain the expected NoFriendDamage badge translation")

        for relative in sorted(refs | generated):
            path = PREFIX + relative
            try:
                data = jar.read(path)
                with Image.open(BytesIO(data)) as image:
                    if image.format != "PNG" or image.width < 1 or image.height < 1:
                        fail(f"resource is not a non-empty PNG: {path}")
                    image.verify()
            except Exception as exc:  # Pillow raises several format-specific exceptions.
                fail(f"invalid PNG resource {path}: {exc}")

        generated_hashes = {}
        for relative in sorted(generated):
            digest = hashlib.sha256(jar.read(PREFIX + relative)).hexdigest()
            if digest in generated_hashes:
                fail(
                    f"duplicate generated PNGs: {generated_hashes[digest]} and {relative}"
                )
            generated_hashes[digest] = relative

    print(
        f"Verified {jars[0].name}: {len(refs)} registered texture paths, "
        f"{len(generated)} original PNG assets, friend store/protection classes and Mixin registration, all PNG payloads valid, "
        f"Fabric icon={icon_path}"
    )


if __name__ == "__main__":
    main()
