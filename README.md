# DercoVisuals

**DercoVisuals** is a free, client-side visual mod for **Minecraft Java 1.21.11 + Fabric**.
The project is maintained for players first: no paid features, subscriptions, telemetry, or hidden installers.

## What is included
- A redesigned modular GUI with category navigation, module cards, status indicators, and a details column.
- HUD: FPS and coordinates, armor display, animated keystrokes, active-module list, compass, and local clock.
- Player: hold-to-zoom.
- Render: Fullbright toggle with gamma restoration.
- Module states saved in `config/dercovisuals.properties`.
- English and Russian keybind names.

The attached starter archive did not contain mine-event code. The separate `pulse-src.zip` remains untouched in this repository; its binary contents could not be audited in this editing environment, so this revision does not claim to have removed anything inside that archive.

## Build requirements
- Java 21 (JDK)
- Git, when publishing source changes from a developer checkout
- Internet access for the first Gradle dependency download

Run `1.bat` from a cloned repository on Windows. It synchronizes the lightweight source files to `main`, builds the mod, copies the JAR into `%APPDATA%\.minecraft\mods`, and opens Minecraft Launcher.

Gradle and libraries are downloaded to the normal Gradle cache. They are not committed to GitHub. Build outputs, caches, and game files are not uploaded.

For a player-only installation, download the JAR from the [GitHub Releases page](https://github.com/andreyromanov681-boop/DercoVisuals/releases) and place it in `.minecraft/mods` alongside Fabric API.

## Keybinds
| Key | Action |
|---|---|
| Right Shift | Open DercoVisuals menu |
| B | Toggle Fullbright |
| Hold C | Zoom |

Keybinds can be changed in Minecraft Controls.

## Build manually
```powershell
.\gradlew.bat build
```
The distributable JAR is written to `build/libs/DercoVisuals-1.0.0.jar`.

## Publishing a release
Push source changes to `main`, then create and push a version tag, for example:
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions builds the project and attaches the distributable JAR to a GitHub Release.

Read [AGENTS.md](AGENTS.md), [CONTRIBUTORS.md](CONTRIBUTORS.md), and [LICENSE](LICENSE) before contributing. Keep the project free for players, preserve the lightweight source-only repository structure, and document changes for future maintainers.
