# Pulse source audit — DercoVisuals integration notes

## Archive facts

The repository's `pulse-src.zip` was unpacked in a temporary GitHub Actions runner for review. The archive contains 641 files and expands to approximately 25 MB. The extracted copy was a short-lived CI artifact; the repository's original archive was not changed.

## What informed the new UI

The review identified the following broad architectural ideas:

- category-based module library;
- compact module cards arranged in a grid;
- a separate inspector for the selected module;
- typed settings widgets and persistent module state;
- independently positioned HUD elements.

DercoVisuals now implements its own category rail, three-column module-card grid, inspector, toggle behavior, scroll handling, and HUD widgets. No Pulse Java class or proprietary implementation was copied into the DercoVisuals build.

## Compatibility and rights warning

The Pulse metadata declares `All-Rights-Reserved`, and no separate LICENSE/COPYING file was present in the archive. Its build properties target Minecraft 1.21.4 even though its `fabric.mod.json` declares Minecraft 1.21.11. Do not import its classes, bundled libraries, or render pipeline into DercoVisuals without confirmed permission from the rights holder and a careful 1.21.11 port.

## Mine-event feature deliberately excluded

The source audit found mine-specific behavior in these reference components:

- `pulse/gui/events/EventInfo.java`: `EventState.MINE`, `EventKind.MINE`, a mine-event factory, and mine-specific display logic;
- `pulse/gui/events/EventsTab.java`: the `Шахты` category/tab;
- `pulse/markers/providers/ActiveEventMarkerProvider.java`: a `смертельная шахта` marker entry;
- `pulse/markers/providers/EventsCommandMarkerProvider.java`: a `пороховой карьер` marker entry.

These features are not included in DercoVisuals. The new client has no events screen, mine events, mine/quarry markers, or mine-event parsing. The original reference archive remains intact for provenance and is never included in the lightweight player JAR.

## Player download policy

The player `1.bat` flow fetches only the latest DercoVisuals release JAR. The maintainer Git-checkout flow may push the lightweight project sources, but does not stage the Pulse archive, Gradle caches, build outputs, or Minecraft files.
