#!/usr/bin/env python3
"""Generate the DercoVisuals source tree from the authorized Pulse source archive.

The archive is kept as an untouched upstream snapshot. This script creates a clean,
rebranded working tree for the DercoVisuals build; it never edits the archive.
"""
from pathlib import Path
from zipfile import ZipFile
import json, re, shutil, sys

ROOT = Path(__file__).resolve().parents[1]
ARCHIVE = ROOT / 'pulse-src.zip'
TMP = ROOT / 'build' / 'pulse-foundation-unpack'
SRC = ROOT / 'src' / 'main'
if not ARCHIVE.exists():
    raise SystemExit(f'Missing source archive: {ARCHIVE}')
if TMP.exists(): shutil.rmtree(TMP)
TMP.mkdir(parents=True)
with ZipFile(ARCHIVE) as z:
    z.extractall(TMP)
UP = TMP / 'pulse-src'
JAVA = UP / 'src' / 'main' / 'java'
RES = UP / 'src' / 'main' / 'resources'
if not JAVA.exists() or not RES.exists():
    raise SystemExit('Pulse archive layout was not recognized')

# Keep the original module/config/HUD/rendering architecture, but drop the mine/events
# screen completely. Other event-bus primitives remain available to non-mine modules.
for rel in ('pulse/gui/events',):
    shutil.rmtree(JAVA / rel, ignore_errors=True)

# Test harnesses and optional account/Discord integrations are not part of this free,
# offline-first visual client foundation.
for rel in ('pulse/TestMod.java',):
    (JAVA / rel).unlink(missing_ok=True)

# Remove the two mine-specific marker aliases, while retaining the generic marker feature.
for rel, phrase in (
    ('pulse/markers/providers/ActiveEventMarkerProvider.java', 'смертельная шахта'),
    ('pulse/markers/providers/EventsCommandMarkerProvider.java', 'пороховой карьер'),
):
    marker_file = JAVA / rel
    if marker_file.exists():
        lines = marker_file.read_text(encoding='utf-8').splitlines()
        marker_file.write_text('\\n'.join(line for line in lines if phrase not in line) + '\\n', encoding='utf-8')

# Remove Java-only experimental render test classes.
for p in (JAVA / 'pulse' / 'render').glob('Test*.java'):
    p.unlink()

# Remove the mine/events GUI tab wiring and its enum entry.
screen = JAVA / 'pulse' / 'gui' / 'core' / 'PulseClickGuiScreen.java'
if screen.exists():
    s = screen.read_text(encoding='utf-8')
    s = re.sub(r'^import pulse\.gui\.events\.EventsTab;\s*\n', '', s, flags=re.M)
    s = re.sub(r'^\s*this\.l\.put\(ClickGuiTabType\.EVENTS, new EventsTab\(\)\);\s*\n', '', s, flags=re.M)
    s = re.sub(r'^\s*if \(clickGuiTabA instanceof EventsTab\) \{.*?^\s*\}\s*\n', '', s, flags=re.M|re.S)
    s = re.sub(r'\s*return clickGuiTabA instanceof EventsTab && \(\(EventsTab\)clickGuiTabA\)\.a\(c2, i\)\s*\? true\s*:\s*clickGuiTabA instanceof ConfigsTab && \(\(ConfigsTab\)clickGuiTabA\)\.a\(c2, i\);', '\n         return clickGuiTabA instanceof ConfigsTab && ((ConfigsTab)clickGuiTabA).a(c2, i);', s)
    screen.write_text(s, encoding='utf-8')
enum = JAVA / 'pulse' / 'gui' / 'core' / 'ClickGuiTabType.java'
if enum.exists():
    s = enum.read_text(encoding='utf-8')
    s = s.replace('   EVENTS("events", "\\ue90a"),\n', '')
    enum.write_text(s, encoding='utf-8')

# Keep the original Pulse lifecycle API for internal callers but remove Pulse cloud/auth
# and Discord dependencies by supplying a small local-only initializer.
entry = JAVA / 'ru' / 'pulse' / 'Pulse.java'
if not entry.exists():
    raise SystemExit('Could not find Pulse entrypoint')
entry.write_text('''package ru.pulse;\n\nimport net.fabricmc.api.ModInitializer;\nimport net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;\nimport net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;\nimport net.minecraft.client.gui.screen.Screen;\nimport org.apache.logging.log4j.LogManager;\nimport org.apache.logging.log4j.Logger;\nimport pulse.auth.UserInfo;\nimport pulse.config.LocalConfigManager;\nimport pulse.gui.core.ClickGuiKeyBinding;\nimport pulse.gui.core.PulseClickGuiScreen;\nimport pulse.hud.core.HudServiceRegistry;\nimport pulse.module.ModuleRegistry;\nimport pulse.player.PulsePlayerTracker;\nimport pulse.render.Renderer2D;\nimport pulse.render.Renderer2DImpl;\nimport pulse.render.icons.IconTextureRegistry;\nimport pulse.render.shader.ShaderLibrary;\n\n/** DercoVisuals local-first client bootstrap. No Pulse account/backend integration. */\npublic final class Pulse implements ModInitializer {\n    private static final Pulse INSTANCE = new Pulse();\n    private static final Logger LOGGER = LogManager.getLogger("DercoVisuals");\n    private Renderer2D renderer;\n    private Screen clickGui;\n    private boolean initialized;\n\n    @Override public void onInitialize() {\n        ClickGuiKeyBinding.register();\n        ClientLifecycleEvents.CLIENT_STARTED.register(client -> init());\n        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> LocalConfigManager.get().flushSaveNow("disconnect"));\n        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> LocalConfigManager.get().flushSaveNow("client-stop"));\n        LOGGER.info("DercoVisuals bootstrap registered (local-only mode)");\n    }\n\n    public void init() {\n        if (initialized) return;\n        IconTextureRegistry.load();\n        ShaderLibrary.loadDefaultShaders();\n        renderer = new Renderer2DImpl();\n        HudServiceRegistry.a();\n        ModuleRegistry.init();\n        try { LocalConfigManager.get().init(); }\n        catch (Exception e) { LOGGER.warn("Could not load local config", e); }\n        PulsePlayerTracker.get().init();\n        clickGui = new PulseClickGuiScreen();\n        initialized = true;\n        LOGGER.info("DercoVisuals initialized");\n    }\n\n    public static Pulse getInstance() { return INSTANCE; }\n    public static Logger getLOGGER() { return LOGGER; }\n    public static UserInfo getUserInfo() { return null; }\n    public Renderer2D getRender() { return renderer; }\n    public Screen getClickGui() { return clickGui; }\n}\n''', encoding='utf-8')

# Copy clean Java sources and resources to the real project source set.
shutil.rmtree(SRC / 'java', ignore_errors=True)
shutil.rmtree(SRC / 'resources', ignore_errors=True)
shutil.copytree(JAVA, SRC / 'java')
shutil.copytree(RES, SRC / 'resources')

# Rebrand Java packages, references, filenames and resource identifiers.
for p in sorted((SRC / 'java').rglob('*.java')):
    text = p.read_text(encoding='utf-8', errors='replace')
    text = text.replace('ru.pulse', 'net.dercovisuals')
    text = text.replace('pulse.', 'net.dercovisuals.')
    text = text.replace('Pulse', 'DercoVisuals')
    p.write_text(text, encoding='utf-8')
for p in sorted((SRC / 'java').rglob('*.java'), reverse=True):
    if 'Pulse' in p.name:
        target = p.with_name(p.name.replace('Pulse', 'DercoVisuals'))
        p.rename(target)

# Rebrand resource namespace and remove Pulse-only auth/brand assets where not referenced.
for p in (SRC / 'resources').rglob('*'):
    if p.is_file() and p.suffix.lower() in ('.json', '.fsh', '.vsh', '.frag', '.glsl', '.txt'):
        try: t = p.read_text(encoding='utf-8')
        except UnicodedecodeError: continue
        t = t.replace('assets/pulse', 'assets/dercovisuals').replace('pulse:', 'dercovisuals:').replace('"pulse"', '"dercovisuals"')
        t = t.replace('pulse.mixins.json', 'dercovisuals.mixins.json')
        p.write_text(t, encoding='utf-8')
assets = SRC / 'resources' / 'assets'
if (assets / 'pulse').exists(): (assets / 'pulse').rename(assets / 'dercovisuals')
mi = SRC / 'resources' / 'pulse.mixins.json'
if mi.exists(): mi.rename(SRC / 'resources' / 'dercovisuals.mixins.json')
meta = SRC / 'resources' / 'fabric.mod.json'
metadata = json.loads(meta.read_text(encoding='utf-8'))
metadata.update({
    'id': 'dercovisuals', 'name': 'DercoVisuals',
    'description': 'A free, modular, client-side visual suite for Minecraft 1.21.11.',
    'authors': ['enizer6569'], 'license': 'LicenseRef-DercoVisuals-Community',
    'environment': 'client',
    'entrypoints': {'main': ['net.dercovisuals.DercoVisuals']},
    'depends': {'fabricloader': '>=0.18.1', 'fabric': '*', 'minecraft': '~1.21.11', 'java': '>=21'}
})
metadata['jars'] = [{'file': f'META-INF/jars/{jar.name}'} for jar in sorted((UP / 'libs').glob('*.jar'))]
metadata['mixins'] = ['dercovisuals.mixins.json'] if (SRC / 'resources' / 'dercovisuals.mixins.json').exists() else []
meta.write_text(json.dumps(metadata, indent=2) + '\n', encoding='utf-8')

# Bring bundled upstream helper libraries into the local build input (not source archive caches).
libs = UP / 'libs'
if libs.exists():
    target = ROOT / 'libs'
    target.mkdir(exist_ok=True)
    for jar in libs.glob('*.jar'):
        shutil.copy2(jar, target / jar.name)

print(f'Prepared DercoVisuals Pulse foundation: {len(list((SRC / "java").rglob("*.java")))} Java files')
