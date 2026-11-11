# GitHub Copilot instructions — DercoVisuals

Read [AGENTS.md](../AGENTS.md), [README.md](../README.md), and [ATTRIBUTION.md](../ATTRIBUTION.md).

- Work in the existing DercoVisuals repository; do not create a replacement project.
- Work from checked-in DercoVisuals sources. Do not use a separate FlyVisuals binary or decompiler output as source input.
- The maintainer states that Pulse / PulseVisuals / `pulse` / `ru.pulse` were former identifiers. This is a maintainer declaration, not independent proof of ownership of every source file or asset.
- Review explicit dependency and asset notices individually. Do not infer provenance solely from historical names.
- Configured target: Minecraft 1.21.11, Fabric Loader 0.20.1, Fabric API 0.141.6+1.21.11, Yarn 1.21.11+build.6, Java 21.
- Preserve features and behavior while fixing compatibility issues. Do not remove mixins, events, modules, or resources just to silence compile errors.
- Keep `All-Rights-Reserved` and preserve third-party notices.
- Report build results accurately; never claim a successful build without a passing build.
