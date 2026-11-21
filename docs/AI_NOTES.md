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


## 2026-11-21 — Remove automatic player-name registration

- Inspected the generated Java artifact from the last successful pulse-foundation CI run and confirmed that DercoVisualsPlayerTracker was initialized during client startup, fetched a remote Supabase user list, and POSTed the local player's username on multiplayer joins.
- The tracker created a new daemon single-thread executor for each fetch/register operation without shutting it down. Repeated joins could therefore leave idle worker threads behind; the outbound username registration also contradicted the project's documented offline-first / no-telemetry policy.
- Updated tools/prepare_pulse_foundation.py (the source of truth) to omit the tracker class and initialization. Nameplate and tab-list badge checks now use only the existing local PlayerListCache lookup; no remote registration path is retained.
- Added a fail-fast generator guard that rejects the tracker class reference and its Supabase endpoint/table markers if they reappear in generated Java.
- Validation: applied the exact patch to the prior 436-file generated-source artifact before committing. GitHub Actions run [#44](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44878056739) on commit `e3a82d2e69118de98070a5b02383527151db4794` completed successfully: source generation produced 435 Java files, `compileJava`, `jar`, `remapJar`, `assemble`, and `build` passed under Java 21 / Gradle 9.2.1. The generated-source artifact contained no tracker class, Supabase URL, or tracker endpoint; the built JAR and sources JAR also contained no `DercoVisualsPlayerTracker` class. `compileTestJava` and `test` were `NO-SOURCE`, so automated Java test coverage remains absent. Loom emitted existing non-fatal remapping warnings and Gradle reported deprecation warnings. No in-game runtime smoke test was available.
- Next: verify the new branch CI build and manually smoke-test player nameplate/tab icons in Minecraft 1.21.11.


## 2026-11-21 — Repair ItemCooldownManager entry mixin mapping

- Assistant/tool: ChatGPT scheduled bug-fix run.
- Inspected the generated Java artifact from CI run [#44878293814](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44878293814) and confirmed that `ItemCooldownEntryAccessor` used the invalid hybrid target `net/minecraft/ItemCooldownManager$class_1797` plus intermediary record-component names `comp_3083` / `comp_3084`. Yarn 1.21.11+build.3 maps this record to `net/minecraft/entity/player/ItemCooldownManager$Entry`, with fields `startTick` and `endTick`.
- Updated the source-of-truth generator to emit the correct named target and accessor field names. Also removed the unused intermediary `@Shadow comp_737` field from `TextMixin`, which generated a separate remapping warning and was never read by the mixin.
- Validation before commit: applied the exact guarded patches to the generated Java artifact from run #44878293814; verified the expected target and named accessor fields and confirmed that the stale intermediary names were absent. Full CI for this change is pending; no local Minecraft runtime test was available.
- Next: inspect the CI run triggered by this commit, confirm the Mixin annotation processor no longer reports the cooldown/text remapping warnings, then smoke-test the Cooldown Tracker in Minecraft 1.21.11.


- Follow-up validation note (2026-11-21): post-commit inspection caught over-escaped newline literals in the new TextMixin cleanup guard. Corrected the generator patch so its exact source snippets match real line breaks; the earlier local check had validated the intended Java edits, not the committed Python escape representation. The replacement CI run is the authoritative build check.

- Final verification: GitHub Actions [run #44892154034](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44892154034) for commit `b2624cf3c24ef74b44f173c28ced2c2a6d60e670` completed successfully. Source generation, `:compileJava`, `:jar`, `:remapJar`, `:assemble`, and `:build` passed under Java 21 / Gradle 9.2.1. `:compileTestJava` and `:test` remain `NO-SOURCE`; there are no automated tests yet. The built JAR remaps the accessor target to `net/minecraft/class_1796$class_1797` and its fields to `comp_3083` / `comp_3084`, as expected for runtime intermediary mappings. The prior `comp_737`, `comp_3083`, and `comp_3084` remapping warnings are gone. Remaining warnings concern `modifiers` and selectors in `MapMarkerDistanceMixin` / `FramebufferMixin`; the latter two classes are not registered in the active mixin config. No in-game runtime smoke test was available.


## 2026-11-21 — Fix runtime metadata and Mixin descriptor generation

- Assistant/tool: ChatGPT bug-fix session.
- Inspected the actual distributable JAR from GitHub Actions rather than relying on a successful Java compile. Found `dercovisuals.mixins.json` had literal backslash-n characters after the closing JSON object, so it was not valid JSON even though Gradle `build` passed. The source-of-truth fix is in `tools/prepare_pulse_foundation.py`: write a real newline after the JSON document.
- The generated `fabric.mod.json` also retained `1.0-SNAPSHOT` because it did not contain the `${version}` placeholder expected by Gradle `processResources`. Added the placeholder so the built mod metadata expands to `1.0.0`.
- Added generator preflight checks to parse every generated JSON resource, verify every configured common/client Mixin has a corresponding Java source, check the declared refmap exists, and check the mod icon resource exists. This catches malformed runtime descriptors before Gradle compilation.
- Fixed the metadata icon path: the old `assets/dercovisuals/icon.png` did not exist in the JAR; `assets/dercovisuals/textures/logo.png` is present and is now the configured icon.
- Validation: GitHub Actions run [#50](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44900935849) completed successfully for commit `f33e0f1a7dfe7a02e36579f0c1a7c729ba1e4d71`. Source generation, `compileJava`, `jar`, `remapJar`, `assemble`, `build`, and artifact upload passed under Java 21 / Gradle 9.2.1. The downloaded JAR was inspected: all 18 JSON files parse; metadata version expands to `1.0.0`; all 67 configured Mixin source entries and the refmap are present. This commit's build was performed before the final icon-path adjustment, so rerun CI and inspect the resulting JAR after that adjustment.
- The first guard-edit attempt produced a Python syntax error and failed in CI run [#49](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44900898890); it was corrected before run #50. That failed run did not alter generated source or produce a mod JAR.
- Automated Java tests remain `NO-SOURCE`. A successful CI build does not prove that Minecraft starts or that every injection works in-game; no client runtime smoke test was performed in this session.
- Next: verify the final post-icon-fix CI artifact, then smoke-test startup, the 67 active mixins, HUD/GUI input, and config persistence inside Minecraft 1.21.11.


## 2026-11-21 — Fix DrawContext item-highlight injection crash

- Assistant/tool: ChatGPT bug-fix session using the user's Fabric 1.21.11 crash log.
- Confirmed primary failure: `DrawContextMixin.renderItemHighlight` used `@Inject(method = "drawItem")` with handler parameters `(LivingEntity, World, ItemStack, int, int, int, CallbackInfo)`. Because `DrawContext.drawItem` is overloaded, the name-only selector also selected the shorter `drawItem(ItemStack, int, int)` overload; Mixin reported an expected handler descriptor `(ItemStack, int, int, CallbackInfo)` and aborted the game during mixin application.
- Verified the intended private overload against FabricMC Yarn `1.21.11+build.3`: `DrawContext.drawItem(LivingEntity, World, ItemStack, int, int, int)`, intermediary selector `method_51425(Lnet/minecraft/class_1309;Lnet/minecraft/class_1937;Lnet/minecraft/class_1799;III)V`.
- Updated `tools/prepare_pulse_foundation.py` (the source of truth) to patch the injector with the full named descriptor `drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V`. The generator patch fails fast if the old annotation is not found.
- Validation: GitHub Actions [run #52](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44902376016) for commit `b64a968ea9b9b1900cd1a04106ecd711cb30e133` completed successfully. Source generation, `compileJava`, `jar`, `remapJar`, `assemble`, `build`, and artifact upload passed with Java 21 / Gradle 9.2.1. `compileTestJava` and `test` remain `NO-SOURCE`.
- Inspected the actual remapped `DercoVisuals-1.0.0.jar`: `DrawContextMixin` contains the exact intermediary selector `method_51425(Lnet/minecraft/class_1309;Lnet/minecraft/class_1937;Lnet/minecraft/class_1799;III)V`; all 18 generated JSON resources parse; metadata version is `1.0.0`; the configured icon exists. JAR size: 16,011,886 bytes; SHA-256: `88796cc0e28f6564f7a2b3b97b80e9781f9145ae030b3969907c62f8391a41ba`.
- Separate non-fatal issue from the user's log remains: `EntityRendererHitboxMixin` still targets the old `EntityRenderDispatcher` class name. Yarn 1.21.11 renames the class to `EntityRenderManager`, and the old hitbox API needs a deliberate port rather than a blind rename. Existing non-fatal remapping warnings for `modifiers`, `MapMarkerDistanceMixin`, and `FramebufferMixin` also remain.
- No direct Minecraft client runtime launch was available in this session. The replacement JAR is CI-built and bytecode-inspected, but the user's next launch is still required to confirm the startup crash is gone in their 205-mod environment.
- Next: have the user test this JAR, collect the new `latest.log` if another DercoVisuals mixin fails, then port the hitbox customizer against the current 1.21.11 rendering API.


## 2026-11-21 — Fix overloaded DrawContext stack-overlay injection

- Assistant/tool: ChatGPT bug-fix session using the user's second Fabric 1.21.11 crash log.
- Confirmed the crash is a second overload-selection issue in `DrawContextMixin.onDrawStackOverlay`: the handler includes the optional `String stackCountText` parameter, but the name-only `drawStackOverlay` selector also resolves to the four-argument overload. Mixin reports expected `(TextRenderer, ItemStack, int, int, CallbackInfo)` but finds `(TextRenderer, ItemStack, int, int, String, CallbackInfo)`.
- Yarn `1.21.11+build.3` documents both overloads: `drawStackOverlay(TextRenderer, ItemStack, int, int)` and `drawStackOverlay(TextRenderer, ItemStack, int, int, String)`. Updated the generator to target the exact five-argument descriptor `drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V`, preserving the handler's nullable custom count text behavior.
- Source-of-truth change: `tools/prepare_pulse_foundation.py`; no generated output or original `pulse-src.zip` was manually edited.
- CI/build and remapped JAR inspection are pending. A successful build does not prove the user's full modpack runtime starts; the next launch log remains the runtime check.
- Next: verify CI artifact and remapped Mixin target, send the JAR, then inspect any subsequent DercoVisuals Mixin error from the user's next launch.


## 2026-11-21 — Configurable potion HUD effect limit

- Assistant/tool: ChatGPT scheduled feature run.
- Inspected the generated Java sources from the latest successful `pulse-foundation` CI artifact and confirmed that the Potions HUD displayed every active status effect without a user-configurable cap. The effects are already sorted by remaining duration, so the soonest-expiring effects can be kept visible first.
- Added a `Макс. эффектов` slider to the Potions HUD module: default 6, minimum 1, maximum 12, step 1. The module's existing settings collector and local config serializer automatically include the new setting.
- Updated the source-of-truth generator so `PotionsHudElement` trims the sorted active-effect list to the configured limit. Existing entry animations handle effects entering and leaving the visible list.
- Validation before commit: applied the exact patch anchors to the current CI-generated source artifact; verified both anchors match exactly once, the existing imports are present, and the setting is collected by the module constructor. The current baseline CI run [#54](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44903107708) passed under Java 21 / Gradle 9.2.1. The new commit's CI result is pending.
- Limitations: this environment cannot launch the Minecraft client for an in-game GUI/persistence smoke test. The repository currently has no Java test sources (`test` is `NO-SOURCE`).
- Next: verify the new setting in the in-game Potions HUD settings panel, confirm its value persists after restart, and continue with the next independent visual improvement.


## 2026-11-21 — Fix InGameHud scoreboard overload injection

- Assistant/tool: ChatGPT bug-fix session using the user's third Fabric 1.21.11 crash log.
- The client now passed both earlier `DrawContextMixin` injection points and reached HUD initialization. The new failure was `InGameHudMixin.onRenderScoreboardSidebar`: the handler takes `(DrawContext, ScoreboardObjective, CallbackInfo)`, while Mixin selected the other same-name overload `renderScoreboardSidebar(DrawContext, RenderTickCounter)` and expected `(DrawContext, RenderTickCounter, CallbackInfo)`.
- Confirmed Yarn `1.21.11+build.3` exposes both overloads separately: the outer dispatcher maps to intermediary `method_55803(DrawContext, RenderTickCounter)`; the inner renderer maps to `method_1757(DrawContext, ScoreboardObjective)`.
- Fixed the source-of-truth generator `tools/prepare_pulse_foundation.py` to give both HEAD and TAIL injections the full named descriptor `renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V`. This keeps the existing scoreboard objective calculations and matrix push/pop behavior attached to the inner renderer, rather than rewriting the feature.
- The previous `pulse-foundation` changes were preserved, including the configurable potion-HUD effect limit and both DrawContext overload fixes. The original `pulse-src.zip` remains untouched.
- CI/build and remapped-JAR bytecode inspection for this commit are pending. A successful build does not prove in-game runtime behavior; the user's next launch will confirm whether another Mixin error remains.
- Next: inspect CI and verify both scoreboard injections remap to `method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V`, then send the rebuilt JAR.


## 2026-11-21 — Port Hitbox Customizer suppression to 1.21.11

- Assistant/tool: ChatGPT maintenance session.
- Audited the active client mixins after the three startup crash fixes. Found the remaining non-fatal missing-target warning: `EntityRendererHitboxMixin` still targeted the removed `EntityRenderDispatcher.shouldRenderHitboxes()` API.
- Verified Yarn `1.21.11+build.3`: the class is now `net.minecraft.client.render.debug.EntityHitboxDebugRenderer`, and its renderer method is `render(DDD Lnet/minecraft/world/debug/DebugDataStore; Lnet/minecraft/client/render/Frustum; F)V` (without spaces in the Mixin selector). This renderer is the current F3+B entity-hitbox path.
- Updated only the source-of-truth generator `tools/prepare_pulse_foundation.py`. It now retargets the mixin to `EntityHitboxDebugRenderer.render(...)` and cancels that renderer only while DercoVisuals' own Hitbox Customizer is enabled. The module's custom box/line rendering and settings remain untouched; the vanilla debug renderer continues working when the module is disabled.
- Pre-commit validation: applied all four guarded source replacements to the latest generated-source artifact; each old anchor matched exactly once and the resulting Java source uses the current class, exact method descriptor, and `CallbackInfo` cancellation. Yarn API documentation confirms the target class and method.
- CI/build and remapped JAR verification are pending for this source-generator change. No Minecraft client runtime test is available here. The original `pulse-src.zip`, `main`, and other branches were not modified.
- Next: verify CI source generation/compile, inspect the remapped mixin annotation in the actual JAR, and confirm the missing-target warning is gone.


- Follow-up correction (2026-11-21): inspection of the committed generator caught over-escaped newline tokens in the newly added hitbox source replacement strings. Corrected the Python literals so they match actual Java source line breaks; the initial in-memory source transformation had been checked, but the serialized generator representation also needed this fix. The replacement CI run is authoritative.


## 2026-11-21 — Add opt-in Pastebin crash-log upload

- Assistant/tool: ChatGPT maintenance session after user requested automatic upload of the previous Minecraft `latest.log` following a crash.
- Added `ai.derco.crash.CrashLogBootstrap` through the source-of-truth generator `tools/prepare_pulse_foundation.py` and registered it as a Fabric `preLaunch` entrypoint. Fabric calls this entrypoint shortly before the game starts, allowing a session marker and shutdown snapshot hook to exist before main/client initialization and early Mixin application.
- The bootstrap writes `config/dercovisuals-session-active`; the normal Fabric `CLIENT_STOPPING` callback removes it. If the marker remains on the next launch, the previous run is treated as an unexpected exit and the last `logs/latest.log` is snapshotted into a local queue. A JVM shutdown hook makes a best-effort snapshot before process exit. This is intended to catch early startup failures after preLaunch as well as later crashes; forced termination may still prevent the shutdown hook from running.
- Added local-only `config/dercovisuals-crash-upload.properties` with `enabled=true` and a blank `apiDevKey`. No user API key or other credential is embedded in source, build metadata, or the JAR. Upload occurs only after the user locally supplies a Pastebin Developer API key; setting `enabled=false` disables it.
- Upload sends the saved log to the official Pastebin API as an **unlisted** paste (`api_paste_private=1\)) with a one-week expiry. Unlisted means anyone with the resulting URL can read it. Before snapshotting, the uploader caps each file at the last 2 MiB, replaces the local home directory / username, and redacts common bearer tokens, API keys, passwords, and secrets. Up to five queued crash logs are retained; successful uploads delete their local queue entry and save the latest URL to `config/dercovisuals-last-crash-paste.txt`. Failed uploads keep the file for retry. Upload runs on a daemon thread with network timeouts, so it does not block game startup.
- The user's supplied Developer API key was deliberately not copied into the repository or artifact because it is a credential. The user should rotate it before adding a fresh key to the local config file.
- The user also referred to a crash log, but no new log attachment was present in conversation files; the supplied Pastebin raw URL could not be retrieved by available web access. Therefore no crash diagnosis was claimed in this change.
- Verification: GitHub Actions run [#66](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44911480117) for source commit `c3c1dbe879d3ac93dd2066132fa10914b902ccb4` completed successfully (source generation, compile/build, and artifact upload). The downloaded remapped JAR was inspected: `fabric.mod.json` declares `preLaunch: ai.derco.crash.CrashLogBootstrap`; `ai/derco/crash/CrashLogBootstrap.class` exists; `DercoVisuals` bytecode references `markCleanShutdown`; mod version is `1.0.0`. JAR SHA-256: `196d2b124a2efbfb008d176a1a52df6aa60343bcbf40cb172c336d220d61efee`. A whole-JAR scan confirms the Developer API key supplied in chat is not embedded. No Minecraft client runtime or real Pastebin upload test was performed.
- Next: inspect generator output and build result; verify the preLaunch metadata and uploader class inside the remapped JAR; document the exact local config path and verify upload response parsing.


- Follow-up correction (2026-11-21): the first uploader CI attempt stopped in Python parsing because the new `entrypoints` dictionary was serialized with literal backslash-n tokens. Corrected the dictionary to normal Python source lines. This was a generator syntax issue, not a Java compile or Pastebin API failure; CI must be rerun before the feature is considered build-verified.

- Local setup after installing the JAR: launch once to create `config/dercovisuals-crash-upload.properties`, then put a newly rotated key on the `apiDevKey=` line. Uploads are enabled by default when a key is present; use `enabled=false` to disable. On an unexpected exit the log is saved locally and uploaded on the next launch; the resulting URL is printed and saved in `config/dercovisuals-last-crash-paste.txt`. The Pastebin paste is unlisted and expires after one week.
 

## 2026-11-21 — Fix FontManager startup crash and icon-font resource paths

- Assistant/tool: ChatGPT bug-fix session using the user's mclo.gs crash report (https://mclo.gs/1/raw/92Kh51y).
- Confirmed primary failure during Minecraft initialization: java.lang.ExceptionInInitializerError in FontManager.<clinit>, caused by FontManager.load() calling Font.createFont(0, null). The reported root exception was java.io.IOException: Problem reading font data, reached while ChestSorter constructed its first HUD notification (WatermarkOverlay).
- Inspected the run #67 remapped JAR and its embedded font assets. FontManager requested dercoicon.ttf and derco_icons.ttf, while the resource rebranding step renames the actual files to dercovisualsicon.ttf and dercovisuals_icons.ttf. The streams were therefore null even though the icon-font assets were packaged. This reproduces the same IOException with Java 21's Font.createFont.
- Updated only tools/prepare_pulse_foundation.py, the source of truth. The early FontManager migration now fixes the final classpath root /assets/dercovisuals/font/ and uses the exact final filenames dercovisualsicon.ttf and dercovisuals_icons.ttf before the later rebranding pass. FontManager.load() now logs the resource failure and falls back to AWT SansSerif instead of aborting the entire client when a font is absent or unreadable.
- Added generation-time validation that all five font resources used by FontManager exist in src/main/resources/assets/dercovisuals/font/.
- Validation: CI run #70 (https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44912378174) successfully completed source generation and the generated-Java diagnostic artifact was inspected. It contains the corrected resource root, both correct icon-font filenames, and the fallback code. At the time of this note, the compile/build step was still running; final JAR bytecode/resource inspection and a Minecraft runtime smoke test remain pending.
- The first two intermediate CI attempts (#68 and #69) stopped in the generator's guarded patch stage because the initial patch expected post-rebrand filenames before that transformation had run. The replacement patch now uses the actual upstream pulseicon.ttf and pulse_icons.ttf anchors and passes source generation.
- pulse-src.zip, main, and other branches were not modified. No credentials or Pastebin API keys were added.
- Next: verify the final CI build artifact, inspect the remapped FontManager resource strings and packaged font entries, then ask the user to test startup with the new JAR.

## 2026-11-21 — Add shared blue theme system foundation

- Assistant/tool: ChatGPT GPT-6 via GitHub integration.
- Inspected `AGENTS.md`, this handoff file, the `pulse-foundation` branch, the source generator, Gradle configuration, and CI workflow before editing. Base branch head: `cc6f86c314e5fe78752c1e2d025449f427fe5dfd`; its latest observed CI run was #71 and passed.
- Created isolated branch `agent/blue-theme-system` from that exact base. `main`, `pulse-foundation`, and `pulse-src.zip` were not edited.
- Added eight renderer-independent Java API classes under generated package `ai.derco.theme`: `ThemeColors` (ARGB tokens), `ThemeSettings` (immutable user theme preferences), `DercoTheme` (shared facade and accent override), `ThemeMetrics` (corner radii/spacing), `ThemeEffects` (shadow/glow descriptors), `ThemeGradients` (gradient endpoint/interpolation helpers), `ThemeAnimation` (frame-delta transitions/reduced-motion behavior), and `ThemeContrast` (WCAG contrast checking and readable text selection).
- The API is emitted from `tools/prepare_pulse_foundation.py` after the archive rebrand/patch steps. Generated `src/main/java` is not treated as the source of truth. No ClickGUI/HUD implementations were rewritten, no dependencies or worker threads were added.
- Added `docs/THEME_SYSTEM.md` with compile-time API examples and integration boundaries. The default text palette's minimum calculated contrast across the four standard surfaces is approximately 4.71:1; the Java API also exposes `ThemeContrast.defaultPalettePasses()` and per-color WCAG checks.
- Validation: PR #2 GitHub Actions run [#72](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44913719971) completed successfully on commit `777a99a3b9a44d137ba6f282f9bd722edba0b68c`. The generator and generated-source upload passed; Java 21 / Gradle 9.2.1 completed `:compileJava`, `:jar`, `:remapJar`, `:remapSourcesJar`, `:assemble`, and `:build`. Final log: `BUILD SUCCESSFUL in 1m 35s`; CI uploaded the remapped JAR and sources JAR (artifact ID `14710723150`).
- Automated test source remains absent: `:compileTestJava` and `:test` reported `NO-SOURCE`. Loom emitted non-fatal remap warnings for existing unrelated targets and Gradle reported deprecation warnings; no build task failed.
- No in-game UI/runtime smoke test was performed in the original theme-only PR session. PR #2 was subsequently merged into `pulse-foundation`; the visual integrations are recorded below.

## 2026-11-21 — Integrate blue visual overhaul, HUD, ClickGUI and original icons

- Assistant/tool: ChatGPT using the connected GitHub integration.
- The shared theme API from PR #2, HUD redesign from PR #3, ClickGUI redesign from PR #4, and original 42-image icon pack from PR #5 are integrated in `pulse-foundation` commit `3a3d5fc6314b335bfea8640c546c7b0c803d691d`.
- The sole source of generated Java changes remains `tools/prepare_pulse_foundation.py`. The shared design API, HUD presentation/layout changes, responsive ClickGUI, icon registration, and representative GUI icon rendering are all generated from that source.
- Original artwork is generated by `tools/assets/generate_derco_assets.py`. CI installs the pinned Pillow requirement and runs `tools/verify_derco_jar.py` against the final remapped JAR.
- Combined GitHub Actions run [#93](https://github.com/andreyromanov681-boop/DercoVisuals/actions/runs/44919663532) passed: source generation, Java 21 compilation, remap/build, final texture/metadata verification, and artifact upload.
- Downloaded runtime artifact: `DercoVisuals-1.0.0.jar`, 16,628,710 bytes. Independent inspection confirmed all 42 generated PNGs are present, valid and unique; all six legacy texture paths, both icon fonts, theme classes, HUD theme, ClickGUI screen, and icon registry are packaged. SHA-256: `e555e8b509feedb3bcd52fea3fce2cfb292e27fdeeae88add24a1420366ccb9a`.
- PR #2 is merged. PRs #3, #4, and #5 were closed as integrated after comments linking this commit and the successful combined CI run. `main` and `pulse-src.zip` were not modified.
- No Minecraft runtime screenshot or in-game smoke test was performed for the combined redesign. The supplied HUD mockup remains a concept reference; Right Shift, GUI scale, drag/resize, saved layouts, and live HUD placement still need manual testing.
- The performance-memory agent branch had no commits or PR at integration time, so memory/performance changes remain pending until that agent submits work.
