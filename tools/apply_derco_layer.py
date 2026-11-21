#!/usr/bin/env python3
"""Apply DercoVisuals local friends and NoFriendDamage after source generation."""
from __future__ import annotations
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
JAVA = ROOT / "src" / "main" / "java"
RES = ROOT / "src" / "main" / "resources"

def fail(message: str) -> None:
    print(f"[derco-layer] ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)

FRIEND_STORE = r'''package ai.derco.gui.friends;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** Local-only persistent friends list. No server or network calls are made. */
public final class FriendStore {
    private static final Logger LOGGER = LoggerFactory.getLogger("DercoVisuals/Friends");
    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");
    private static final Map<String, String> FRIENDS = new LinkedHashMap<>();
    private static boolean loaded;
    private FriendStore() { }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("dercovisuals").resolve("friends.txt");
    }
    private static String clean(String name) { return name == null ? "" : name.trim(); }
    private static String key(String name) { return clean(name).toLowerCase(Locale.ROOT); }

    public static boolean isValidName(String name) {
        return name != null && VALID_NAME.matcher(name.trim()).matches();
    }

    private static synchronized void ensureLoaded() {
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

    private static synchronized boolean save() {
        Path path = file();
        Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            Files.createDirectories(path.getParent());
            Files.write(temporary, new ArrayList<>(FRIENDS.values()), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException exception) {
            LOGGER.warn("Could not save local friends list at {}", path, exception);
            try { Files.deleteIfExists(temporary); } catch (IOException ignored) { }
            return false;
        }
    }

    public static synchronized List<String> getNames() {
        ensureLoaded();
        return List.copyOf(FRIENDS.values());
    }

    public static synchronized boolean isFriendName(String name) {
        ensureLoaded();
        String normalized = key(name);
        return !normalized.isEmpty() && FRIENDS.containsKey(normalized);
    }

    public static synchronized boolean add(String name) {
        ensureLoaded();
        String value = clean(name);
        if (!isValidName(value)) return false;
        String normalized = key(value);
        if (FRIENDS.containsKey(normalized)) return false;
        FRIENDS.put(normalized, value);
        if (save()) return true;
        FRIENDS.remove(normalized);
        return false;
    }

    public static synchronized boolean remove(String name) {
        ensureLoaded();
        String normalized = key(name);
        String previous = FRIENDS.remove(normalized);
        if (previous == null) return false;
        if (save()) return true;
        FRIENDS.put(normalized, previous);
        return false;
    }
}
'''

FRIEND_LOOKUP = '''package ai.derco.gui.friends;

/** Shared friend-name lookup used by client-side features. */
public final class FriendLookup {
    private FriendLookup() { }
    public static boolean isFriendName(String name) { return FriendStore.isFriendName(name); }
    public static boolean a(String name) { return isFriendName(name); }
}
'''

NO_FRIEND_DAMAGE = '''package ai.derco.protection;

import ai.derco.gui.friends.FriendLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/** Always-on local protection for players in the saved friends list. */
public final class NoFriendDamage {
    private NoFriendDamage() { }
    public static boolean shouldBlock(Entity target) {
        if (!(target instanceof PlayerEntity player)) return false;
        String name = player.getName().getString();
        return name != null && !name.isBlank() && FriendLookup.isFriendName(name);
    }
}
'''

NO_FRIEND_DAMAGE_MIXIN = '''package ai.derco.mixin;

import ai.derco.protection.NoFriendDamage;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class NoFriendDamageMixin {
    @Inject(
            method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dercovisuals$blockFriendAttack(PlayerEntity attacker, Entity target, CallbackInfo ci) {
        if (NoFriendDamage.shouldBlock(target)) ci.cancel();
    }
}
'''

def patch_friend_panel() -> None:
    path = JAVA / "ai/derco/gui/friends/FriendsPanel.java"
    if not path.is_file(): fail("FriendsPanel.java not found; run prepare_pulse_foundation.py first")
    source = path.read_text(encoding="utf-8")
    old_methods = """   public void a(HistoryEntry historyEntry) {
   }

   public void b(HistoryEntry historyEntry) {
   }
"""
    new_methods = """   public void a(HistoryEntry historyEntry) {
      if (historyEntry == null || !FriendStore.add(historyEntry.a())) return;
      this.r.add(historyEntry);
      FriendCard card = new FriendCard(historyEntry);
      card.a(this::b);
      this.s.add(card);
      this.e();
      this.v.e();
   }

   public void b(HistoryEntry historyEntry) {
      if (historyEntry == null) return;
      String name = historyEntry.a();
      this.r.removeIf(entry -> entry.a().equalsIgnoreCase(name));
      this.s.removeIf(card -> card.a().a().equalsIgnoreCase(name));
      FriendStore.remove(name);
      this.e();
      this.v.e();
   }
"""
    if new_methods not in source:
        if old_methods not in source: fail("expected FriendsPanel add/remove stubs not found")
        source = source.replace(old_methods, new_methods, 1)

    old_init = "      this.w = new PanelFadeOverlay(25, 5.0F, 9.0F);\n"
    new_init = old_init + """      for (String name : FriendStore.getNames()) {
         HistoryEntry entry = new HistoryEntry(name);
         this.r.add(entry);
         FriendCard card = new FriendCard(entry);
         card.a(this::b);
         this.s.add(card);
      }
      this.e();
"""
    if "FriendStore.getNames()" not in source:
        if old_init not in source: fail("expected FriendsPanel constructor block not found")
        source = source.replace(old_init, new_init, 1)

    old_add = "   private void g() {\n   }\n"
    new_add = """   private void g() {
      String name = this.u.a();
      if (FriendStore.isValidName(name)) {
         this.a(new HistoryEntry(name.trim()));
         this.u.setRawValue("");
      }
   }
"""
    if new_add not in source:
        if old_add not in source: fail("expected FriendsPanel add-button stub not found")
        source = source.replace(old_add, new_add, 1)
    path.write_text(source, encoding="utf-8", newline="\n")
    print("[derco-layer] wired Friends tab add/remove/load")

def register_mixin() -> None:
    path = RES / "dercovisuals.mixins.json"
    if not path.is_file(): fail("dercovisuals.mixins.json not found")
    try: config = json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError as error: fail(f"invalid Mixin JSON: {error}")
    if config.get("package") != "ai.derco.mixin": fail("unexpected Mixin package")
    client = config.get("client")
    if not isinstance(client, list): fail("Mixin config has no client list")
    if "NoFriendDamageMixin" not in client:
        client.append("NoFriendDamageMixin")
        path.write_text(json.dumps(config, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        print("[derco-layer] registered NoFriendDamageMixin")

def main() -> None:
    for relative in ("ai/derco/gui/friends/HistoryEntry.java", "ai/derco/gui/friends/FriendLookup.java"):
        if not (JAVA / relative).is_file(): fail(f"{relative} not found; run prepare_pulse_foundation.py first")
    patch_friend_panel()
    sources = {
        "ai/derco/gui/friends/FriendStore.java": FRIEND_STORE,
        "ai/derco/gui/friends/FriendLookup.java": FRIEND_LOOKUP,
        "ai/derco/protection/NoFriendDamage.java": NO_FRIEND_DAMAGE,
        "ai/derco/mixin/NoFriendDamageMixin.java": NO_FRIEND_DAMAGE_MIXIN,
    }
    for relative, source in sources.items():
        path = JAVA / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(source, encoding="utf-8", newline="\n")
        print(f"[derco-layer] wrote {relative}")
    register_mixin()
    print("[derco-layer] local friends and NoFriendDamage layer ready")

if __name__ == "__main__":
    main()
