# DercoVisuals

Client-side Fabric project for Minecraft Java Edition **1.21.11**.

- **Project name:** DercoVisuals
- **Author metadata:** enizer6569
- **Minecraft:** 1.21.11
- **Java:** 21
- **Fabric Loader:** 0.20.1
- **Fabric API:** 0.141.6+1.21.11

## Build

Run `./gradlew build` on macOS/Linux or `gradlew.bat build` on Windows.

## Compatibility notes

The project display name and distributable JAR name are DercoVisuals. The existing internal Java package and Fabric resource namespace `pulse` are intentionally retained so existing source references, mixins, textures, and saved configuration paths continue to work.

The Events page has been removed from the click GUI. The shared internal event-dispatching package remains because other client features depend on it.
