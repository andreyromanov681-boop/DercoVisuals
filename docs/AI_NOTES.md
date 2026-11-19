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


---

## 2026-11-10 — Pulse foundation migration started

- User confirmed that the Pulse source authors granted permission to use the archive as the project base; no written proof was available in the chat. Treat that as user-provided authorization context, and preserve any author attribution/conditions if they are later supplied.
- Created development branch `pulse-foundation`; stable `main` and its current release are intentionally untouched until the new base builds and is reviewed.
- Added `tools/prepare_pulse_foundation.py`. It unpacks the existing `pulse-src.zip` during developer/CI builds, rebrands Java package/class identifiers and resource namespace to DercoVisuals, and keeps the archive untouched.
- Replaced the upstream account/backend bootstrap with a local-first DercoVisuals entrypoint. The account helper types remain in the source tree for compile compatibility, but the new entrypoint does not initialize remote login or Discord RPC.
- Removed the mine-events GUI tab and event panel from the generated tree; added removal of the two mine/quarry marker aliases. Generic marker, event-bus, module, settings, HUD, and rendering architecture is retained.
- Removed Pulse test harnesses and experimental Test render classes from the generated player source set.
- Updated Gradle dependencies for Lombok and bundled helper libraries. Added a CI workflow for the `pulse-foundation` branch; it generates sources from the archive and compiles against Minecraft 1.21.11 / Java 21.
- Initial CI run was superseded while the migration script was corrected. The latest workflow run must be checked for extraction and compilation errors.
- Important: this is a migration bootstrap, not a finished port. The upstream project configuration targets Minecraft 1.21.4 while its metadata claims 1.21.11, so API/mixin incompatibilities are expected and must be fixed iteratively. Do not publish this branch as the `latest` player release until CI passes and in-game smoke tests succeed.
- Next: inspect the current CI logs; fix the first compile/mixin errors; replace Pulse visual branding in the GUI; verify every module category; audit all remaining event/marker code for mine-specific behavior; keep `main` stable until the new branch is ready.


---

## 2026-11-19 — FontManager javac synthetic-field collision

- Assistant/tool: ChatGPT automation run.
- Verified the current `pulse-foundation` head at `f2b804e30d6c83f46071ae36ee1048bb93bff162`; no other branch was modified.
- Confirmed from GitHub Actions run [#36](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/43103299692) that source generation succeeds and `:compileJava` fails because generated `ai/derco/render/font/FontManager.java` declares `$assertionsDisabled`, which conflicts with javac's compiler-synthesized assertion field.
- Updated `tools/prepare_pulse_foundation.py` to require exactly one expected decompiler-generated field declaration, then rename that field and its references within `FontManager.java` to `$dercoAssertionsDisabled`. The original `desiredAssertionStatus()` initializer is preserved. The migration fails fast if the source shape differs.
- Verification at edit time: patch insertion and regex escaping were checked against the current generator source. Full Gradle build is pending the branch CI run; no local Gradle build was available through this GitHub-only environment.
- Unresolved: confirm the new CI build passes and inspect any subsequent compiler errors; in-game font rendering has not been tested.
- Next action: inspect the workflow run triggered by this commit and continue only with the first confirmed failure.


---

## 2026-11-19 — Refine FontManager assertion-field fix

- Assistant/tool: ChatGPT automation run.
- Inspected the generated-source artifact from CI run [#37](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44494501573), specifically `ai/derco/render/font/FontManager.java`. The class contains the real Java statement `assert resourceAsStream != null;`; the decompiler-emitted `$assertionsDisabled` field has no references in the class.
- Refined `tools/prepare_pulse_foundation.py`: instead of renaming the redundant field, remove exactly one matching decompiler-generated declaration, preserve the Java `assert` statement so javac generates its own synthetic field, and fail fast if the expected statement/declaration shape differs or any reference remains.
- No runtime behavior is intentionally changed; assertion enablement remains controlled by Java's normal `desiredAssertionStatus()` handling.
- Verification: generated artifact from run #37 confirms the field is unused and the assertion statement exists. Final Gradle/CI verification is pending the follow-up push; no local Gradle build was available.
- Unresolved: check the follow-up CI result and manually smoke-test font loading/rendering in Minecraft.
- Next action: inspect the follow-up workflow run for compilation and build results.


---

## 2026-11-19 — Tighten FontManager source guard

- Assistant/tool: ChatGPT automation run.
- Replaced the regex-based field-line matcher with exact line-content matching in `tools/prepare_pulse_foundation.py`. This avoids escaping ambiguity while still requiring exactly one known decompiler field, preserving the actual Java `assert` statement, and rejecting any leftover `$assertionsDisabled` reference.
- This is a follow-up implementation correction; final verification remains the CI run for the current `pulse-foundation` head.


---

## 2026-11-19 — FontManager fix verified

- Final source-generation fix is on `pulse-foundation` at commit `c3d44fffd74c6ee43b1e99ce9a9a5a501ab40843`.
- GitHub Actions [run #39](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44494791779) completed successfully. `Generate DercoVisuals sources`, `compileJava`, `jar`, `remapJar`, `assemble`, and `build` all succeeded; build time reported 1m 16s.
- `test` and `compileTestJava` reported `NO-SOURCE`; there are no automated Java tests in this build yet.
- Inspected the generated `FontManager.java` artifact: the manual decompiler field is absent while `assert resourceAsStream != null;` remains. Inspected the built JAR with `javap`: the remaining `$assertionsDisabled` field is javac's expected synthetic field.
- The CI uploaded `DercoVisuals-1.0.0.jar` and its sources. No changes were made to `main` or any other branch.
- Remaining follow-up: investigate Loom's non-fatal "Cannot remap" warnings for several mixin targets/methods and perform in-game font/render smoke tests. Also add automated tests where practical.
