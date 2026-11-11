# Claude Code guide — DercoVisuals

Start by reading [AGENTS.md](AGENTS.md), [README.md](README.md), and [ATTRIBUTION.md](ATTRIBUTION.md).

This task concerns the existing **DercoVisuals** repository and its checked-in source tree. Do not create a replacement `myvisuals` project and do not use a separate compiled FlyVisuals JAR or decompiler output as source input.

The maintainer states that Pulse / PulseVisuals / `pulse` / `ru.pulse` were their former project identifiers. ATTRIBUTION.md records this as a maintainer declaration, not independent legal proof about every source file or bundled asset. Review concrete dependency and asset notices individually; do not assume either that every file is owned by the maintainer or that every file is third-party based only on its old name.

The Gradle configuration is set to Minecraft 1.21.11, Fabric Loader 0.20.1, Fabric API 0.141.6+1.21.11, Yarn 1.21.11+build.6, and Java 21. This is configuration only: check the current CI result and run the build before claiming the port works.

Main objective: make the existing source compile and run on Fabric 1.21.11 while preserving existing features and behavior. Inspect the actual errors, mappings, mixins, and callers. Do not remove functionality or rewrite the mod from scratch merely to obtain a green build. Keep `All-Rights-Reserved` and preserve third-party notices.
