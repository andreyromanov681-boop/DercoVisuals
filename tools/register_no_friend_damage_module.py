#!/usr/bin/env python3
"""Expose NoFriendDamage as an always-on PvP module in the ClickGUI."""
from __future__ import annotations
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src" / "main" / "java"

def fail(message: str) -> None:
    print(f"[derco-pvp-module] ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)

def patch(relative: str, old: str, new: str, description: str) -> None:
    path = JAVA / relative
    if not path.is_file():
        fail(f"{relative} not found")
    source = path.read_text(encoding="utf-8")
    if new in source:
        return
    if source.count(old) != 1:
        fail(f"expected exactly one {description} anchor in {relative}, found {source.count(old)}")
    path.write_text(source.replace(old, new, 1), encoding="utf-8", newline="\n")
    print(f"[derco-pvp-module] updated {relative}: {description}")

MODULE = '''package ai.derco.modules.pvp;

import ai.derco.module.ClientModule;
import ai.derco.module.ModuleCategory;
import ai.derco.module.ModuleInfo;

/**
 * UI representation of the always-active NoFriendDamage client protection.
 * The attack filter itself is installed by NoFriendDamageMixin and is never
 * disabled by this module's UI or by a saved module configuration.
 */
@ModuleInfo(
        a = "NoFriendDamage",
        b = "Always blocks locally initiated attacks against players in your friends list.",
        c = ModuleCategory.PVP
)
public final class NoFriendDamageModule extends ClientModule {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isSubscribed() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        setEnabledState(true);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        super.setEnabledState(true);
    }

    @Override
    public void setEnabledStateQuiet(boolean enabled) {
        super.setEnabledStateQuiet(true);
    }

    @Override
    public void setSubscribed(boolean subscribed) {
        super.setSubscribed(true);
    }

    @Override
    public void toggle() {
        setEnabledState(true);
    }

    @Override
    public void syncState() {
        setEnabledState(true);
    }
}
'''

def main() -> None:
    category = "ai/derco/module/ModuleCategory.java"
    patch(
        category,
        "public enum ModuleCategory {\n   VISUALS",
        "public enum ModuleCategory {\n   PVP(\"PvP\"),\n   VISUALS",
        "PvP category",
    )

    modules_tab = "ai/derco/gui/modules/ModulesTab.java"
    patch(
        modules_tab,
        'private static final String[] a = new String[]{"Visuals", "HUD", "Utilities"};',
        'private static final String[] a = new String[]{"PvP", "Visuals", "HUD", "Utilities"};',
        "PvP category label",
    )
    patch(
        modules_tab,
        "private static final ModuleCategory[] b = new ModuleCategory[]{ModuleCategory.VISUALS, ModuleCategory.HUD, ModuleCategory.UTILITIES};",
        "private static final ModuleCategory[] b = new ModuleCategory[]{ModuleCategory.PVP, ModuleCategory.VISUALS, ModuleCategory.HUD, ModuleCategory.UTILITIES};",
        "PvP category selector",
    )

    registry = "ai/derco/module/ModuleRegistry.java"
    patch(
        registry,
        "import ai.derco.modules.utilities.PvpSafe;\n",
        "import ai.derco.modules.utilities.PvpSafe;\nimport ai.derco.modules.pvp.NoFriendDamageModule;\n",
        "NoFriendDamage module import",
    )
    patch(
        registry,
        "   public static final PvpSafe PVP_SAFE = new PvpSafe();\n",
        "   public static final PvpSafe PVP_SAFE = new PvpSafe();\n   public static final NoFriendDamageModule NO_FRIEND_DAMAGE = new NoFriendDamageModule();\n",
        "NoFriendDamage module instance",
    )
    patch(
        registry,
        "         PVP_SAFE,\n",
        "         PVP_SAFE,\n         NO_FRIEND_DAMAGE,\n",
        "NoFriendDamage registration",
    )
    patch(
        registry,
        "      CLIENT_COLOR.collectSettings();\n",
        "      NO_FRIEND_DAMAGE.setEnabledState(true);\n      CLIENT_COLOR.collectSettings();\n",
        "force-enabled NoFriendDamage module",
    )

    row = "ai/derco/gui/modules/ModuleRowRenderer.java"
    patch(
        row,
        "      this.a(MatrixStackVar, renderer2D, f6, f7, fJ2);\n",
        '      if (!"NoFriendDamage".equalsIgnoreCase(guiEntry.a())) {\n         this.a(MatrixStackVar, renderer2D, f6, f7, fJ2);\n      }\n',
        "hide toggle for always-on protection",
    )

    module_path = JAVA / "ai/derco/modules/pvp/NoFriendDamageModule.java"
    module_path.parent.mkdir(parents=True, exist_ok=True)
    module_path.write_text(MODULE, encoding="utf-8", newline="\n")
    print("[derco-pvp-module] wrote NoFriendDamageModule.java")
    print("[derco-pvp-module] PvP category and always-on UI module ready")

if __name__ == "__main__":
    main()
