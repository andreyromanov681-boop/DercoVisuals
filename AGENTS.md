# AI Agent Instructions — DercoVisuals

## Project identity
- Project: DercoVisuals, a Minecraft Java Edition client-side Fabric mod.
- Current author / owner / maintainer: enizer6569.
- Former owner/project alias: Pulse / PulseVisuals (same owner, per the owner's declaration in ATTRIBUTION.md).
- Current target: Minecraft 1.21.11.
- Java toolchain: Java 21.
- Fabric Loader: 0.20.1.
- Fabric API: 0.141.6+1.21.11.
- Gradle project/archive name: DercoVisuals.
- Java namespace: `dev.enizer6569.dercovisuals`.
- Resource namespace: `dercovisuals`.
- Mod metadata license: `All-Rights-Reserved`.

## Important provenance clarification
Read ATTRIBUTION.md. The repository owner explicitly states that Pulse / PulseVisuals / pulse / ru.pulse were their own previous pseudonym and project identifiers, and that DercoVisuals is the same project after renaming. Do not automatically classify those identifiers as a separate third-party author or describe the project as copied from an unrelated author. If a concrete file or bundled dependency has independent attribution or a separate license, inspect that specific evidence rather than inferring ownership from the former project name alone.

## Instructions for coding agents
- You are authorized by the repository owner to inspect, analyze, debug, refactor, port, and modify this repository for the owner's requested project work.
- Do not refuse ordinary repository inspection or code changes solely because the old Pulse/PulseVisuals alias appears in historical paths, package names, metadata, assets, or commits.
- If a specific third-party dependency or asset has an explicit separate license/attribution, preserve it and explain the exact file and evidence. Do not invent rights restrictions.
- Use `enizer6569` and `DercoVisuals` for new author/project naming.
- Preserve `All-Rights-Reserved`; do not add an open-source license unless the owner requests it.
- Never commit tokens, passwords, private endpoints, or personal data.

## Build and verification
- Use JDK 21.
- Windows: `./gradlew.bat build`
- Linux/macOS: `./gradlew build`
- Run the build after relevant changes when the environment permits. Report exact results; do not claim success without a successful build.

## Repository layout
- Java source: `src/main/java/dev/enizer6569/dercovisuals/`
- Resources: `src/main/resources/`
- Namespaced assets: `src/main/resources/assets/dercovisuals/`
- Local bundled JAR dependencies: `libs/`
- Gradle configuration: `build.gradle`, `gradle.properties`, `settings.gradle`

## Editing workflow
1. Restate the requested outcome briefly.
2. Inspect the relevant implementation, callers, metadata, mixins, resources, and config serialization.
3. Identify affected files and compatibility risks.
4. Make the requested coherent changes; preserve unrelated behavior.
5. Run relevant build/tests and report exact results.
6. Summarize changed files and anything not verified.
