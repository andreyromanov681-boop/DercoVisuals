#!/usr/bin/env python3
"""Rebrand the maintained DercoVisuals fork and prepare its Minecraft 1.21.11 build."""
from __future__ import annotations
import json
import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA_ROOT = ROOT / "src/main/java"
RES_ROOT = ROOT / "src/main/resources"
BASE_PACKAGE = "dev.enizer6569.dercovisuals"
MAINTAINER_NOTE = (
    "// Maintainer: enizer6569 (DercoVisuals). "
    "Original authorship and rights remain with their respective holders.\n"
)

def move_tree(source: Path, destination: Path, rename_classes: bool = True) -> None:
    if not source.exists():
        return
    for old in sorted(p for p in source.rglob("*") if p.is_file()):
        parts = list(old.relative_to(source).parts)
        if rename_classes:
            parts = [part.replace("Pulse", "DercoVisuals") for part in parts]
        new = destination.joinpath(*parts)
        if new.exists():
            raise RuntimeError(f"Refusing to overwrite existing file: {new}")
        new.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(old), str(new))
    shutil.rmtree(source, ignore_errors=True)

def replace_text(text: str) -> str:
    text = text.replace("package ru.pulse", f"package {BASE_PACKAGE}")
    text = text.replace("import ru.pulse.", f"import {BASE_PACKAGE}.")
    text = text.replace("package pulse;", f"package {BASE_PACKAGE};")
    text = text.replace("package pulse.", f"package {BASE_PACKAGE}.")
    text = text.replace("import static pulse.", f"import static {BASE_PACKAGE}.")
    text = text.replace("import pulse.", f"import {BASE_PACKAGE}.")
    text = text.replace("package com.wmedia;", f"package {BASE_PACKAGE}.media.platform;")
    text = text.replace("import com.wmedia.", f"import {BASE_PACKAGE}.media.platform.")
    text = text.replace("package aethereal;", f"package {BASE_PACKAGE}.session;")
    text = text.replace("import aethereal.", f"import {BASE_PACKAGE}.session.")
    text = text.replace("PulseVisuals", "DercoVisuals")
    text = text.replace("Pulse", "DercoVisuals")
    text = text.replace('"pulse"', '"dercovisuals"')
    text = text.replace('"Pulse"', '"DercoVisuals"')
    text = text.replace("pulse:", "dercovisuals:")
    text = text.replace("assets/pulse/", "assets/dercovisuals/")
    text = text.replace("pulse_icons", "dercovisuals_icons")
    text = text.replace("pulseicon", "dercovisualsicon")
    text = text.replace("pulse_", "dercovisuals_")
    text = text.replace("pulse.cfg", "dercovisuals.cfg")
    text = text.replace("pulse-autosave", "dercovisuals-autosave")
    text = text.replace("pulse/config", "dercovisuals/config")
    text = text.replace("Pulse/config", "DercoVisuals/config")
    return text

def rewrite_text_file(path: Path) -> None:
    try:
        content = path.read_text(encoding="utf-8")
    except (UnicodeDecodeError, OSError):
        return
    updated = replace_text(content)
    if path.suffix == ".java" and path.is_relative_to(JAVA_ROOT / "dev/enizer6569/dercovisuals"):
        if not updated.startswith(MAINTAINER_NOTE):
            updated = MAINTAINER_NOTE + updated
    if updated != content:
        path.write_text(updated, encoding="utf-8", newline="\n")

def migrate_config_manager(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    start = text.find("   private Path getLegacyConfigDir() {")
    end = text.find("   private Path getAutosavePath() {")
    if start < 0 or end < 0 or end <= start:
        raise RuntimeError("Could not find LocalConfigManager legacy config section")
    section = r'''   private Path getLegacyConfigDir() {
      MinecraftClient client = MinecraftClient.getInstance();
      return client == null ? null : client.runDirectory.toPath().resolve("config").resolve("pulse");
   }

   private Path getLegacyAppDataConfigDir() {
      String appData = System.getenv("APPDATA");
      if (appData != null && !appData.isBlank()) {
         return Path.of(appData, "Pulse", "config");
      }
      return Path.of(System.getProperty("user.home"), "Pulse", "config");
   }

   private void migrateLegacyConfigIfNeeded() {
      Path configDir = this.getConfigDir();
      Path[] legacyDirs = new Path[] { this.getLegacyAppDataConfigDir(), this.getLegacyConfigDir() };

      for (Path legacyDir : legacyDirs) {
         if (legacyDir == null || !Files.isDirectory(legacyDir) || legacyDir.equals(configDir)) {
            continue;
         }
         try {
            Files.createDirectories(configDir);
            Path oldAutosave = legacyDir.resolve("pulse.cfg");
            Path newAutosave = configDir.resolve("dercovisuals.cfg");
            if (Files.exists(oldAutosave) && !Files.exists(newAutosave)) {
               Files.copy(oldAutosave, newAutosave, StandardCopyOption.COPY_ATTRIBUTES);
               LOG.info("[DercoVisuals] Migrated autosave {} -> {}", oldAutosave, newAutosave);
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(legacyDir, "*.json")) {
               for (Path oldProfile : stream) {
                  Path newProfile = configDir.resolve(oldProfile.getFileName());
                  if (!Files.exists(newProfile)) {
                     Files.copy(oldProfile, newProfile, StandardCopyOption.COPY_ATTRIBUTES);
                     LOG.info("[DercoVisuals] Migrated profile {}", oldProfile.getFileName());
                  }
               }
            }
         } catch (IOException e) {
            LOG.warn("[DercoVisuals] Legacy config migration from {} failed", legacyDir, e);
         }
      }
   }

'''
    path.write_text(text[:start] + section + text[end:], encoding="utf-8", newline="\n")

def main() -> None:
    destination = JAVA_ROOT / "dev/enizer6569/dercovisuals"
    destination.mkdir(parents=True, exist_ok=True)
    move_tree(JAVA_ROOT / "pulse", destination)
    move_tree(JAVA_ROOT / "ru/pulse", destination)
    ru_root = JAVA_ROOT / "ru"
    if ru_root.exists() and not any(ru_root.iterdir()):
        ru_root.rmdir()

    move_tree(JAVA_ROOT / "com/wmedia", destination / "media/platform")
    com_root = JAVA_ROOT / "com"
    if com_root.exists() and not any(com_root.iterdir()):
        com_root.rmdir()
    move_tree(JAVA_ROOT / "aethereal", destination / "session")

    # Rename all remaining project class filenames containing the old brand.
    for old_path in sorted(destination.rglob("*")):
        if old_path.is_file() and "Pulse" in old_path.name:
            new_path = old_path.with_name(old_path.name.replace("Pulse", "DercoVisuals"))
            if new_path.exists():
                raise RuntimeError(f"Refusing to overwrite {new_path}")
            old_path.rename(new_path)

    internal = destination / "internal"
    internal.mkdir(parents=True, exist_ok=True)
    for number in ("", "2", "3"):
        old = JAVA_ROOT / f"Test{number}.java"
        if old.exists():
            new = internal / f"DercoVisualsTest{number}.java"
            if new.exists():
                raise RuntimeError(f"Refusing to overwrite {new}")
            source = old.read_text(encoding="utf-8")
            source = re.sub(rf"public class Test{number}\b", f"public class DercoVisualsTest{number}", source)
            new.write_text(f"package {BASE_PACKAGE}.internal;\n\n" + source, encoding="utf-8")
            old.unlink()

    old_assets = RES_ROOT / "assets/pulse"
    new_assets = RES_ROOT / "assets/dercovisuals"
    if old_assets.exists():
        if new_assets.exists():
            raise RuntimeError("Both old and new asset roots exist")
        old_assets.rename(new_assets)
    if new_assets.exists():
        for path in sorted(new_assets.rglob("*"), reverse=True):
            if path.is_file() and path.name.startswith("pulse"):
                path.rename(path.with_name("dercovisuals" + path.name[len("pulse"):]))
            elif path.is_file() and path.name.startswith("Pulse"):
                path.rename(path.with_name("DercoVisuals" + path.name[len("Pulse"):]))

    old_mixins = RES_ROOT / "pulse.mixins.json"
    new_mixins = RES_ROOT / "dercovisuals.mixins.json"
    if old_mixins.exists():
        old_mixins.rename(new_mixins)

    text_suffixes = {
        ".java", ".json", ".fsh", ".frag", ".vsh", ".vert", ".glsl",
        ".txt", ".properties", ".xml", ".mcmeta", ".gradle", ".py",
        ".bat", ".md", ".yml"
    }
    scan_roots = [
        destination, RES_ROOT, ROOT / "build.gradle", ROOT / "gradle.properties",
        ROOT / "settings.gradle", ROOT / "build.py", ROOT / "start.bat"
    ]
    for root in scan_roots:
        paths = [root] if root.is_file() else (root.rglob("*") if root.exists() else [])
        for path in paths:
            if path.is_file() and path.suffix.lower() in text_suffixes:
                rewrite_text_file(path)

    descriptor_path = RES_ROOT / "fabric.mod.json"
    descriptor = json.loads(descriptor_path.read_text(encoding="utf-8"))
    descriptor.update({
        "id": "dercovisuals",
        "name": "DercoVisuals",
        "version": "1.0.0",
        "description": "DercoVisuals client for Minecraft 1.21.11.",
        "authors": ["enizer6569 (DercoVisuals maintainer)"],
        "icon": "assets/dercovisuals/textures/logo.png",
        "entrypoints": {
            "client": [],
            "main": [f"{BASE_PACKAGE}.DercoVisuals", f"{BASE_PACKAGE}.TestMod"]
        },
        "mixins": ["dercovisuals.mixins.json"],
        "depends": {
            "fabricloader": ">=0.20.1",
            "fabric": "*",
            "minecraft": "1.21.11",
            "java": ">=21"
        }
    })
    descriptor_path.write_text(json.dumps(descriptor, indent=2) + "\n", encoding="utf-8")

    mixin_path = RES_ROOT / "dercovisuals.mixins.json"
    mixin = json.loads(mixin_path.read_text(encoding="utf-8"))
    mixin["package"] = f"{BASE_PACKAGE}.mixin"
    mixin_path.write_text(json.dumps(mixin, indent=2) + "\n", encoding="utf-8")

    props_path = ROOT / "gradle.properties"
    props = props_path.read_text(encoding="utf-8")
    updates = {
        "minecraft_version": "1.21.11",
        "yarn_mappings": "1.21.11+build.6",
        "loader_version": "0.20.1",
        "fabric_version": "0.141.6+1.21.11",
        "maven_group": "dev.enizer6569",
        "archives_base_name": "DercoVisuals"
    }
    for key, value in updates.items():
        props = re.sub(rf"^{key}=.*$", f"{key}={value}", props, flags=re.M)
    props_path.write_text(props, encoding="utf-8", newline="\n")

    gradle_path = ROOT / "build.gradle"
    gradle = gradle_path.read_text(encoding="utf-8")
    gradle = re.sub(
        r"id ['\"](?:fabric-loom|net\.fabricmc\.fabric-loom-remap)['\"] version ['\"][^'\"]+['\"]",
        "id 'net.fabricmc.fabric-loom-remap' version '1.18.4'",
        gradle,
        count=1
    )
    gradle_path.write_text(gradle, encoding="utf-8", newline="\n")

    settings_path = ROOT / "settings.gradle"
    settings = settings_path.read_text(encoding="utf-8")
    settings = re.sub(r"rootProject\.name\s*=\s*['\"].*?['\"]", "rootProject.name = 'DercoVisuals'", settings)
    settings_path.write_text(settings, encoding="utf-8", newline="\n")

    migrate_config_manager(destination / "config/LocalConfigManager.java")

    build_py_path = ROOT / "build.py"
    build_py = build_py_path.read_text(encoding="utf-8")
    build_py = re.sub(
        r"dercovisuals_run_jar\s*=\s*r?['\"].*?['\"]",
        'dercovisuals_run_jar = os.path.join(root_dir, "run", "DercoVisuals.jar")',
        build_py
    )
    if "if res.returncode != 0:" not in build_py:
        build_py = build_py.replace(
            "res = subprocess.run(cmd)\n",
            "res = subprocess.run(cmd)\n"
            "if res.returncode != 0:\n"
            "    print('[ERROR] Compilation failed; packaging was cancelled.')\n"
            "    sys.exit(res.returncode)\n"
        )
    build_py_path.write_text(build_py, encoding="utf-8", newline="\n")

    (ROOT / "README.md").write_text(
        """# DercoVisuals

Maintainer: enizer6569  
Minecraft: Java Edition 1.21.11  
Platform: Fabric  
Java: 21

DercoVisuals is the maintained and ported project in this repository. Project-owned Java packages, entrypoints, mixin configuration, resources, and build outputs use the dev.enizer6569.dercovisuals / dercovisuals namespace.

## Build

On Windows: .\\gradlew.bat build  
On Linux/macOS: bash ./gradlew build

The Gradle output JAR is build/libs/DercoVisuals-1.0.0.jar.

## Compatibility

This branch targets Minecraft 1.21.11, Fabric Loader 0.20.1, Fabric API 0.141.6+1.21.11, and Yarn 1.21.11+build.6.

## Attribution

enizer6569 is the maintainer and integrator of this fork. Inherited code and bundled visual/audio/font assets came from a codebase that used the Pulse/PulseVisuals identifiers. Their original authorship and rights remain with the respective rights holders; this repository does not imply a transfer of copyright or a new license. See ATTRIBUTION.md.
""",
        encoding="utf-8",
        newline="\n"
    )
    (ROOT / "ATTRIBUTION.md").write_text(
        """# Attribution and rights

The DercoVisuals repository is maintained by enizer6569.

The source tree and bundled resources in the starting revision used the identifiers Pulse, PulseVisuals, pulse, and ru.pulse, and included service URLs under pulsevisuals.pro. The repository contained no separate project LICENSE file and its Fabric metadata used All-Rights-Reserved.

This port changes the project namespace, build configuration, and Minecraft target. It does not establish that the maintainer created every inherited source file, font, sound, texture, shader, or other bundled asset, and it does not transfer ownership of those materials. Existing rights and licensing restrictions remain with the respective rights holders. Do not redistribute or relicense inherited materials without the appropriate permissions.

The enizer6569 maintainer comments identify who maintains this fork; they are not a claim that every inherited line or visual asset was originally authored by that maintainer.
""",
        encoding="utf-8",
        newline="\n"
    )

    workflow = ROOT / ".github/workflows/build.yml"
    workflow.parent.mkdir(parents=True, exist_ok=True)
    workflow.write_text(
        """name: Build DercoVisuals

on:
  push:
    branches: [ main ]
  pull_request:
  workflow_dispatch:

permissions:
  contents: write

jobs:
  rebrand-and-build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '25'
      - name: Apply DercoVisuals namespace and Minecraft 1.21.11 port
        run: python .github/scripts/rebrand.py
      - name: Commit migration
        run: |
          if [[ -n "$(git status --porcelain)" ]]; then
            git config user.name "enizer6569"
            git config user.email "enizer6569@users.noreply.github.com"
            git add -A
            git commit -m "Move DercoVisuals to enizer6569 namespace and Minecraft 1.21.11"
            git push origin HEAD:main
          fi
      - name: Build with Gradle
        run: bash ./gradlew build --no-daemon
      - name: Upload mod artifact
        if: success()
        uses: actions/upload-artifact@v4
        with:
          name: DercoVisuals-1.21.11
          path: build/libs/*.jar
          if-no-files-found: error
""",
        encoding="utf-8",
        newline="\n"
    )
    print("DercoVisuals namespace and Minecraft 1.21.11 preparation applied.")

if __name__ == "__main__":
    main()
