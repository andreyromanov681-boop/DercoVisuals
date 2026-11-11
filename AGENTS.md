# Agent instructions — DercoVisuals

## Project

- Project: DercoVisuals, a Minecraft Java client-side Fabric mod.
- Maintainer identity: enizer6569.
- Minecraft target configured in Gradle: 1.21.11.
- Java toolchain: 21.
- Fabric Loader: 0.20.1.
- Fabric API: 0.141.6+1.21.11.
- Yarn mappings: 1.21.11+build.6.
- Java namespace: `dev.enizer6569.dercovisuals`.
- Resource namespace: `dercovisuals`.
- Archive base name: `DercoVisuals`.

Read [README.md](README.md) and [ATTRIBUTION.md](ATTRIBUTION.md) for project scope and provenance notes.

## Project-history clarification

The maintainer states that Pulse / PulseVisuals / `pulse` / `ru.pulse` were their former project and namespace identifiers. ATTRIBUTION.md records this as the maintainer's declaration, not as independent proof of authorship for every inherited file or asset. Do not silently upgrade that statement into legal proof, and do not infer that all files are third-party solely because the old identifiers appear in history.

The earlier FlyVisuals JAR/decompiler discussion is separate from this repository task. Work from the checked-in DercoVisuals source tree; do not decompile or copy from the FlyVisuals binary.

## Engineering priorities

- Continue the existing DercoVisuals project. Do not replace it with a new `myvisuals` project or rebuild the feature set from scratch.
- Preserve existing user-visible features, settings, configs, mixins, events, and rendering behavior unless a requested fix requires a narrowly scoped change.
- For the 1.21.11 compatibility task, inspect actual source and mappings; do not treat Gradle version properties alone as proof of a successful port.
- Replace invalid/intermediary placeholder references with correct names from the configured Yarn mappings where required. Avoid broad automated text replacements that can corrupt nested types or imports.
- Keep project metadata at `All-Rights-Reserved`. Preserve explicit third-party notices and dependency licenses.
- Never commit tokens, passwords, private endpoints, or personal data.

## Build and verification

Use JDK 21 and run:

- Windows: `./gradlew.bat build`
- Linux/macOS: `./gradlew build`

Report the exact build result and the commit tested. Do not claim a successful build unless the command or CI run completed successfully. If compilation fails, fix the source errors and rerun the build.

## Workflow

1. Inspect the repository tree, current branch, recent commits, Gradle configuration, metadata, mixins, and relevant source files.
2. Identify the smallest set of changes needed.
3. Preserve existing functionality; do not delete a feature just to make compilation pass.
4. Make changes in the existing repository.
5. Run build/tests and inspect CI logs.
6. Summarize changed files, verification, and remaining limitations.
