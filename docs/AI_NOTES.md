# AI / maintainer handoff notes

## Project baseline

- Project and mod id: `DercoVisuals` / `dercovisuals`.
- Target: Minecraft Java 1.21.11, Fabric, Java 21.
- Repository: https://github.com/andreyromanov681-boop/DercoVisuals
- Owner: `enizer6569`.
- Keep the project free, player-focused, and free of ads, telemetry, paywalls, and account gates.

## Work recorded in this revision

- Rebranded the starter from MyVisuals to DercoVisuals across Gradle, packages, mod metadata, keybind IDs, and translations.
- Replaced the old vertical control list with an independently implemented modular GUI: category rail, three-column module grid, active-state switches, selected-module inspector, and mouse-wheel scrolling for smaller screen areas.
- Categories: Visuals, HUD, and Player.
- HUD modules: Info panel, Armor display with durability, Keystrokes including jump, Module list, Direction, Local clock, Potion effects, and Speed meter.
- Visuals: Fullbright with original-gamma restoration. Player: hold-to-zoom with FOV restoration.
- Saved module states and Fullbright restore gamma in `config/dercovisuals.properties`.
- Added the `1.bat` two-path workflow: maintainers push only lightweight source/docs and build locally; players download only the current release JAR. The player path does not clone Git or download Pulse sources.
- Added an automatically updated `latest` release and tagged release workflow.
- Added owner/contributor notes, community license, contributor instructions, and the Pulse source audit document.

## Pulse source audit and mine-event exclusion

- The repository's `pulse-src.zip` was unpacked through a temporary GitHub Actions artifact: 641 files, about 25 MB unpacked.
- Reviewed high-level structure: GUI tabs, module category list, card grid, typed settings widgets, theme/render helpers, and HUD service patterns. DercoVisuals uses a clean, original implementation; no Pulse Java classes or bundled libraries are included in the DercoVisuals build.
- Pulse `fabric.mod.json` declares `All-Rights-Reserved`, and no standalone LICENSE/COPYING file was found. Its `gradle.properties` targets Minecraft 1.21.4 despite the `fabric.mod.json` declaration of 1.21.11. Do not copy Pulse source files or dependencies into DercoVisuals without confirmed permission and a deliberate 1.21.11 port.
- Mine-event references found: `EventInfo.EventState.MINE`, `EventInfo.EventKind.MINE`, mine factory/display logic, the `Шахты` tab in `EventsTab`, a `смертельная шахта` marker entry in `ActiveEventMarkerProvider`, and a `пороховой карьер` marker entry in `EventsCommandMarkerProvider`.
- DercoVisuals does not import or expose Pulse's event UI/marker code. No events screen, mine events, mine/quarry markers, or mine-event parser are included in the DercoVisuals product. The original `pulse-src.zip` remains untouched as a reference archive and is excluded from the player JAR.

## Verification

- The initial DercoVisuals source build and rolling-release workflow passed in GitHub Actions.
- The Pulse extraction/inventory workflow succeeded and produced a short-lived audit artifact, which was inspected.
- The expanded modular GUI/HUD revision at commit `5cc229a9447ea76acda04ed7ff8a17bc2cb0d4f4` compiled successfully in GitHub Actions. Its `Publish rolling latest release` and CI artifact upload steps also succeeded in run `42847480400`.
- The `latest` release now contains `DercoVisuals-1.0.0.jar` (28,195 bytes).
- JSON mod metadata and language resources were parsed locally.
- Local Gradle could not resolve `services.gradle.org` in this environment (`curl: (6) Could not resolve host`), so the new Minecraft API calls must be checked by GitHub Actions.
- The in-game visual layout, HUD positions, scrolling, inventory preview, cooldown progress, and keybind behavior have not yet been manually tested inside Minecraft.

## Next tasks

1. Test the three-column GUI at 16:9 and smaller GUI scales; verify mouse-wheel scrolling.
2. Verify Potion Effects, Speed HUD, armor durability, Fullbright restoration, and Zoom restoration in-game.
3. Test `1.bat` both from a maintainer Git checkout and from a player-only folder; ensure the latter fetches only the release JAR.

## Required note format for future AI sessions

Append a dated entry with assistant/tool identity, files changed, behavior changed, build/test result, unresolved issues, and exact next action. Never overwrite previous handoff entries.
