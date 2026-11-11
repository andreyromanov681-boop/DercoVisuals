# DercoVisuals

**Maintainer:** enizer6569  
**Minecraft:** Java Edition 1.21.11  
**Platform:** Fabric  
**Java toolchain:** 21

DercoVisuals is the project in this repository. The current source tree uses the Java namespace `dev.enizer6569.dercovisuals` and resource namespace `dercovisuals`.

## Project history and scope

The maintainer states that Pulse, PulseVisuals, `pulse`, and `ru.pulse` were their former pseudonym, project, and namespace identifiers, and that DercoVisuals is the continuation under the current name. This is the maintainer's declaration; this file does not claim that the declaration is independent legal proof of authorship for every source file or bundled asset.

The separate FlyVisuals JAR and its decompiler output mentioned in an earlier conversation are not the source input for the current DercoVisuals task. Work on this repository from its checked-in source tree. Do not assume DercoVisuals and FlyVisuals are the same product.

## Compatibility configuration

The Gradle configuration currently targets:

- Minecraft 1.21.11
- Fabric Loader 0.20.1
- Fabric API 0.141.6+1.21.11
- Yarn 1.21.11+build.6
- Java 21

These are configured versions, not a claim that the current source has already compiled successfully. Check the latest GitHub Actions result before treating a build as verified.

## Build

Windows:

```powershell
.\gradlew.bat build
```

Linux/macOS:

```bash
./gradlew build
```

Expected Gradle output: `build/libs/DercoVisuals-1.0.0.jar`.

## Attribution and licensing

See [ATTRIBUTION.md](ATTRIBUTION.md). The mod metadata currently declares `All-Rights-Reserved`; no open-source license is granted by this repository.
