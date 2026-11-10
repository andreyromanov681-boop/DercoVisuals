# DercoVisuals

**DercoVisuals** is a free, client-side visual mod for **Minecraft Java 1.21.11 + Fabric**.
The project is maintained for players first: no paid features, subscriptions, telemetry, or hidden installers.

## What is included

- A redesigned modular GUI with category navigation, module cards, status indicators, and a details panel.
- HUD: FPS and coordinates, armor durability, animated keystrokes, active-module list, compass, local clock, potion effects, and movement speed.
- Visuals: Fullbright toggle with gamma restoration.
- Player: hold-to-zoom.
- Module states saved in `config/dercovisuals.properties`.
- English and Russian keybind names.

The old mine-event feature set is not part of DercoVisuals. The Pulse reference archive was audited: mine-event screens, mine/quarry markers, and mine-event parsing are not included in this mod. The original `pulse-src.zip` remains unchanged in the repository as a reference archive and is excluded from the player JAR. See [the source audit](docs/PULSE_SOURCE_AUDIT.md) for details and licensing notes.

## Build requirements

- Java 21 (JDK)
- Git, when publishing source changes from a developer checkout
- Internet access for the first Gradle dependency download

Run `1.bat` on Windows. In a maintainer Git checkout it commits and pushes only lightweight source/documentation files, builds locally, installs the JAR into `%APPDATA%\.minecraft\mods`, and opens Minecraft Launcher. In a player-only folder it downloads only the latest public release JAR; players do not need GitHub write access, Git, or a JDK.

The Gradle distribution and libraries are downloaded to the normal Gradle cache for maintainer builds. They are not committed to GitHub. Build outputs, caches, and the game itself are not uploaded from the maintainer checkout; CI attaches only the distributable JAR to the rolling `latest` release.

For a manual player installation, download the JAR from the [GitHub Releases page](https://github.com/andreyromanov681-boop/DercoVisuals/releases) and place it in the `.minecraft/mods` folder alongside Fabric API.

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

Push source changes to `main`. GitHub Actions builds the project after each push to `main` and updates the rolling `latest` release with the distributable JAR. Version tags such as `v1.0.0` create separate versioned releases.

## Project rules

Read [AGENTS.md](AGENTS.md), [CONTRIBUTORS.md](CONTRIBUTORS.md), and [LICENSE](LICENSE) before contributing. Keep the project free for players, preserve the lightweight source-only repository structure, and document changes for future AI or human maintainers.
