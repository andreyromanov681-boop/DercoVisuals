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

    friends_store = (generated_java / "gui/friends/FriendStore.java").read_text(encoding="utf-8")
    if "migrateLegacyFriendHistory()" not in friends_store or "ConfigJsonCodec.a(json)" not in friends_store:
        fail("FriendStore legacy ConfigState.FriendHistoryState migration is missing")

    friends_panel = (generated_java / "gui/friends/FriendsPanel.java").read_text(encoding="utf-8")
    remove_signature = "public void b(HistoryEntry historyEntry) {"
    if friends_panel.count(remove_signature) != 1:
        fail("FriendsPanel friend-removal handler is missing or duplicated")
    remove_start = friends_panel.find(remove_signature)
    remove_end = friends_panel.find("\\n   }", remove_start)
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
