#!/usr/bin/env python3
"""Fail CI if any registered DercoVisuals texture is missing from the built JAR."""
from __future__ import annotations

from io import BytesIO
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

    with ZipFile(jars[0]) as jar:
        names = set(jar.namelist())
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

        for path in sorted(PREFIX + item for item in generated):
            try:
                data = jar.read(path)
                with Image.open(BytesIO(data)) as image:
                    image.verify()
            except Exception as exc:  # Pillow raises several format-specific exceptions.
                fail(f"invalid PNG resource {path}: {exc}")

    print(
        f"Verified {jars[0].name}: {len(refs)} registered texture paths, "
        f"{len(generated)} original PNG assets, all PNG payloads valid, "
        f"Fabric icon={icon_path}"
    )


if __name__ == "__main__":
    main()
