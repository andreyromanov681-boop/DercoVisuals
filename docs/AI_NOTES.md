# AI / maintainer handoff notes

## Current baseline
- Project name and mod id: `DercoVisuals` / `dercovisuals`.
- Target: Minecraft Java 1.21.11, Fabric, Java 21.
- Repository: https://github.com/andreyromanov681-boop/DercoVisuals
- Owner: enizer6569.
- The project is intended to remain free and player-first.

## Work recorded in this revision
- Replaced the old MyVisuals naming with DercoVisuals in Gradle metadata, Java packages, mod metadata, keybind ids, and translations.
- Replaced the single vertical toggle list with a three-column modular menu: category rail, module cards, and selected-module details.
- Added persisted module toggles and a stored original gamma value using `config/dercovisuals.properties`.
- Added Compass and World Clock HUD modules; retained Info HUD, Armor HUD, Keystrokes, Active Modules, Fullbright, and Zoom.
- Added Russian and English keybind translations.
- Added project rules, AI handoff instructions, contributor acknowledgements, and a community license.
- Added a Windows `1.bat` workflow and GitHub Actions build/release workflows.

## Removed features / archive limitation
- No mine-event code existed in the attached 8.5 KB starter archive, so there was no mine-event handler to remove in those files.
- The repository contains `pulse-src.zip` (~15.7 MB). The GitHub file connector can inspect UTF-8 text but cannot return this binary archive, and this environment had no outbound network path to download it. It was deliberately left untouched. Do not claim it was audited or mined for visuals until a maintainer provides an unpacked source tree or readable archive copy.

## Verification status
- Source and metadata were inspected from the attached starter ZIP.
- Project structure and source were generated in this revision.
- A full Gradle build could not be run in this environment: Gradle is not installed and outbound dependency downloads are unavailable.
- The first successful GitHub Actions run should be treated as the required compiler/API verification. Check it before tagging a public release.

## Next maintainer tasks
1. Run `1.bat` from a real clone with GitHub push access and Java 21 installed.
2. Check the GitHub Actions build result. Fix any mapping/API compile errors before release.
3. Test the menu at 16:9 and smaller window sizes; add scroll support if the module library grows beyond the visible area.
4. Test Fullbright restore and Zoom restore after toggling, screen changes, and disconnects.
5. Ask the owner for an unpacked `pulse-src.zip` tree if visuals/features from that archive still need to be integrated or mine events need a precise removal audit.

## Note format for future AI sessions
Append a dated entry with: assistant/tool identity, files changed, behavior changed, build/test result, unresolved issues, and exact next action. Never overwrite previous handoff entries.
