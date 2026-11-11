# AI Agent Instructions — DercoVisuals

## Project identity
- Project: DercoVisuals, a Minecraft Java Edition client-side Fabric mod.
- Maintainer: enizer6569.
- Target Minecraft: 1.21.11.
- Java toolchain: Java 21.
- Fabric Loader: 0.20.1.
- Fabric API: 0.141.6+1.21.11.
- Gradle project name / archive base name: DercoVisuals.
- Current Java namespace: `dev.enizer6569.dercovisuals`.
- Resource namespace: `dercovisuals`.

## Read these first
1. `README.md` for project/build facts.
2. `ATTRIBUTION.md` for inherited-code and asset attribution/rights constraints.
3. `gradle.properties`, `build.gradle`, and `settings.gradle` before changing build configuration.
4. `src/main/resources/fabric.mod.json` and `src/main/resources/dercovisuals.mixins.json` before changing entrypoints or mixins.

## Build and verification
- Windows: `./gradlew.bat build`
- Linux/macOS: `./gradlew build`
- Use JDK 21.
- After Java, resource, metadata, or build changes, run the build when the environment permits.
- If the build cannot be run, state that clearly and list the checks that remain.
- Do not claim a successful build without observing a successful Gradle result.

## Repository layout
- Java source: `src/main/java/dev/enizer6569/dercovisuals/`
- Mod resources and metadata: `src/main/resources/`
- Namespaced assets: `src/main/resources/assets/dercovisuals/`
- Minecraft shader resources: `src/main/resources/assets/minecraft/shaders/`
- Local bundled JAR dependencies: `libs/`
- Gradle configuration: `build.gradle`, `gradle.properties`, `settings.gradle`

The Java tree is organized into feature packages (for example animation, audio, auth, client, config, cosmetic, discord, effects, entity, and events). Inspect the actual classes and call sites before deciding where a feature belongs.

## Editing rules
- Make the smallest coherent change that satisfies the task.
- Inspect callers, mixins, metadata, resource identifiers, and config serialization before changing shared APIs.
- Preserve the existing Java and resource namespaces unless the task explicitly requests a migration.
- When changing a package, update declarations, imports, directory paths, mixin references, metadata, reflection strings, and build configuration consistently.
- Preserve existing behavior unless the user explicitly requests a behavior change.
- Do not delete modules, event systems, account-related code, validation, or security checks as a shortcut. Explain the impact and ask before removing significant behavior.
- Do not add credentials, tokens, private endpoints, or personal data to source control.
- Do not silently replace dependencies or introduce new network services.
- Keep generated build output out of commits.

## Attribution and licensing guardrails
- The repository has no GitHub-detected license and `ATTRIBUTION.md` describes inherited source/assets with rights retained by their respective rights holders.
- A public GitHub repository is not automatically open-source or freely relicensable.
- Do not add MIT/GPL/Apache or another license, remove attribution, claim authorship of inherited code/assets, or redistribute inherited materials unless the maintainer supplies permission or an applicable license.
- You may inspect the repository to understand it and propose changes. Before copying substantial inherited code/assets into another project or publishing redistributed artifacts, verify permission and licensing.
- Keep maintainer attribution distinct from original authorship.

## Working style
1. Restate the requested outcome briefly.
2. Inspect the relevant implementation before editing.
3. Identify affected files and possible compatibility risks.
4. Make the change.
5. Run relevant build/tests and report exact results.
6. Summarize changed files, behavior changes, and anything not verified.
