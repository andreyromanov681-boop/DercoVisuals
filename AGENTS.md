# DercoVisuals — contributor and AI handoff rules

These instructions apply to every human or AI assistant that edits this repository.

## Project identity
- Project: **DercoVisuals**
- Minecraft: **Java 1.21.11**
- Platform: **Fabric, client-side**
- Repository: https://github.com/andreyromanov681-boop/DercoVisuals
- Project owner / maintainer: **enizer6569**
- Primary goal: make attractive, dependable visuals for players. The project is free; do not add paid tiers, subscriptions, ads, telemetry, account gates, or artificial feature locks.

## Non-negotiable workflow
1. Inspect the existing code and configuration before changing them.
2. Preserve working features unless the owner explicitly asks to remove them.
3. Keep the interface modular: categories on the left, module cards in the center, selected-module details on the right. Do not regress to a single long list of buttons.
4. Keep client-only code client-only. Do not add server requirements for visual features.
5. Do not add mining-event features or restore mine-related UI unless the owner explicitly requests them.
6. Do not delete user files, archives, configs, or features without a clear request. Keep `pulse-src.zip` untouched unless the owner asks otherwise.
7. Never commit build outputs, Gradle caches, game files, credentials, tokens, local paths, or personal configuration. Commit source, documentation, workflows, and small resources only.
8. Never put GitHub credentials in scripts. Use the maintainer's normal Git authentication.
9. Build and test after code changes when the environment permits. If dependencies or network access block a build, say so plainly and leave exact diagnostics in the handoff notes.
10. Before finishing, update `docs/AI_NOTES.md` with changes, tests, known limitations, and next task.

## Code conventions
- Java 21, Fabric 1.21.11, Yarn `1.21.11+build.3` unless the owner approves a toolchain migration.
- Keep module metadata, enabled state, settings, and rendering responsibilities separated.
- Every visible module must perform its advertised action. Do not ship placeholder toggles.
- Save user preferences under the Minecraft config directory, not inside the JAR or repository.
- Restore temporary game settings (for example gamma or FOV) when a module is disabled.
- Prefer simple, readable Java and small dependencies. No bundled runtime or large assets.
- Keep `en_us.json` and `ru_ru.json` in sync when adding keybinds or translated labels.

## UI quality bar
- Use a consistent dark visual system with restrained accent colors, clear spacing, readable contrast, and responsive panel sizing.
- Group modules into meaningful categories.
- Show enabled/disabled state clearly.
- Explain the selected module and its shortcut in the details panel.
- Avoid clutter, oversized decorations, misleading controls, and unnecessary animations.

## Notes for AI assistants
- Read this file and `docs/AI_NOTES.md` at the start of each task.
- State what changed, what was tested, and what could not be verified.
- Do not claim that code was built, pushed, or released unless tool output confirms it.
- AI tools are assistants, not owners. Copyright and project decisions remain with **enizer6569**.
- Acknowledgement: this codebase has received AI-assisted development support, including OpenAI ChatGPT (GPT-6) and Claude where used by the maintainer. This acknowledgement does not transfer ownership.
