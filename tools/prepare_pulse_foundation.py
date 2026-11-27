#!/usr/bin/env python3
"""Generate the DercoVisuals source tree from the authorized Pulse source archive.

The archive is kept as an untouched upstream snapshot. This script creates a clean,
rebranded working tree for the DercoVisuals build; it never edits the archive.
"""
from pathlib import Path
from zipfile import ZipFile
import json, re, shutil, sys, runpy

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
for rel in ('pulse/TestMod.java', 'Test.java'):
    (JAVA / rel).unlink(missing_ok=True)

# Remove the two mine-specific marker aliases, while retaining the generic marker feature.
for rel, phrase in (
    ('pulse/markers/providers/ActiveEventMarkerProvider.java', 'смертельная шахта'),
    ('pulse/markers/providers/EventsCommandMarkerProvider.java', 'пороховой карьер'),
):
    marker_file = JAVA / rel
    if marker_file.exists():
        lines = marker_file.read_text(encoding='utf-8').splitlines()
        marker_file.write_text('\n'.join(line for line in lines if phrase not in line) + '\n', encoding='utf-8')

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
# The archive mixes named Yarn outer classes with intermediary IDs for nested types.
# These nested mappings are verified against FabricMC/yarn 1.21.11 (build 3).
NESTED_YARN_TYPES = {
    'class_240': 'Type',
    'class_242': 'FluidHandling',
    'class_3960': 'ShapeType',
    'class_2705': 'Entry',
    'class_5893': 'Action',
    'class_1963': 'Precipitation',
    'class_5271': 'Properties',
    'class_5859': 'ChatRestriction',
    'class_4598': 'Immediate',
    'class_5245': 'Visitor',
    'class_5246': 'StyledVisitor',
    'class_2585': 'Literal',
    'class_10445': 'Glint',
    'class_10726': 'TextureAssetInfo',
    'class_5618': 'Context',
    'class_7590': 'Visible',
    'class_7177': 'DoubleSliderCallbacks',
    'class_1012': 'Format',
    'class_11900': 'Category',
    'class_307': 'Type',
    'class_2847': 'Action',
    'class_5529': 'RemovalReason',
    'class_1114': 'AttenuationType',
    'class_5596': 'DrawMode',
}
# Earlier decompilation flattened the package of the outer class to
# net.minecraft.<Class>. Restore the exact Yarn 1.21.11 package names.
YARN_OUTER_PACKAGES = {
    'net.minecraft.HitResult.': 'net.minecraft.util.hit.HitResult.',
    'net.minecraft.RaycastContext.': 'net.minecraft.world.RaycastContext.',
    'net.minecraft.PlayerListS2CPacket.': 'net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.',
    'net.minecraft.Biome.': 'net.minecraft.world.biome.Biome.',
    'net.minecraft.ClientWorld.': 'net.minecraft.client.world.ClientWorld.',
    'net.minecraft.MinecraftClient.': 'net.minecraft.client.MinecraftClient.',
    'net.minecraft.VertexConsumerProvider.': 'net.minecraft.client.render.VertexConsumerProvider.',
    'net.minecraft.StringVisitable.': 'net.minecraft.text.StringVisitable.',
    'net.minecraft.PlainTextContent.': 'net.minecraft.text.PlainTextContent.',
    'net.minecraft.ItemRenderState.': 'net.minecraft.client.render.item.ItemRenderState.',
    'net.minecraft.AssetInfo.': 'net.minecraft.util.AssetInfo.',
    'net.minecraft.EntityRendererFactory.': 'net.minecraft.client.render.entity.EntityRendererFactory.',
    'net.minecraft.ChatHudLine.': 'net.minecraft.client.gui.hud.ChatHudLine.',
    'net.minecraft.SimpleOption.': 'net.minecraft.client.option.SimpleOption.',
    'net.minecraft.NativeImage.': 'net.minecraft.client.texture.NativeImage.',
    'net.minecraft.KeyBinding.': 'net.minecraft.client.option.KeyBinding.',
    'net.minecraft.InputUtil.': 'net.minecraft.client.util.InputUtil.',
    'net.minecraft.PlayerActionC2SPacket.': 'net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.',
    'net.minecraft.Entity.': 'net.minecraft.entity.Entity.',
    'net.minecraft.SoundInstance.': 'net.minecraft.client.sound.SoundInstance.',
    'net.minecraft.MatrixStack.': 'net.minecraft.client.util.math.MatrixStack.',
}

java_root = SRC / 'java'
for p in sorted(java_root.rglob('*.java')):
    source = p.read_text(encoding='utf-8', errors='replace')
    source = source.replace('ru.pulse', 'ai.derco')
    source = source.replace('pulse.', 'ai.derco.')
    source = source.replace('Pulse', 'DercoVisuals')
    for intermediary, named in NESTED_YARN_TYPES.items():
        source = re.sub(rf'\b{intermediary}\b', named, source)
    for flattened, mapped in YARN_OUTER_PACKAGES.items():
        source = source.replace(flattened, mapped)
    p.write_text(source, encoding='utf-8')

# Rebrand class filenames, then make the filesystem package layout match each
# declaration. This removes stale pulse/ and ru/pulse/ source paths.
for p in sorted(java_root.rglob('*.java'), reverse=True):
    if 'Pulse' in p.name:
        p.rename(p.with_name(p.name.replace('Pulse', 'DercoVisuals')))

for p in sorted(list(java_root.rglob('*.java'))):
    source = p.read_text(encoding='utf-8', errors='replace')
    package_match = re.search(r'^\s*package\s+([A-Za-z0-9_.]+)\s*;', source, re.M)
    if not package_match:
        # Default-package scratch/test sources at the archive root are not valid
        # Fabric mod sources and cannot be referenced from packaged Java code.
        if p.parent == java_root:
            p.unlink()
            continue
        raise SystemExit(f'Java source has no package declaration: {p.relative_to(ROOT)}')
    target = java_root.joinpath(*package_match.group(1).split('.'), p.name)
    if target != p:
        if target.exists():
            raise SystemExit(f'Package relocation would overwrite {target.relative_to(ROOT)}')
        target.parent.mkdir(parents=True, exist_ok=True)
        p.replace(target)

for directory in sorted((d for d in java_root.rglob('*') if d.is_dir()), reverse=True):
    if not any(directory.iterdir()):
        directory.rmdir()

# Apply named Yarn 1.21.11 record accessors and repair decompiler artefacts.
# These replacements are based on Yarn 1.21.11+build.3 mappings.
API_REPLACEMENTS = (
    ('.comp_1102().comp_1090()', '.body().content()'),
    ('.comp_4798()', '.x()'),
    ('.comp_4799()', '.y()'),
    ('.comp_4795()', '.key()'),
    ('.comp_4796()', '.scancode()'),
    ('.comp_4797()', '.modifiers()'),
    ('.comp_4793()', '.codepoint()'),
    ('.comp_4794()', '.modifiers()'),
    ('.comp_4801()', '.button()'),
    ('.comp_763()', '.content()'),
    ('.comp_349()', '.value()'),
    ('.comp_2378()', '.potion()'),
    ('.comp_2380()', '.customEffects()'),
    ('.comp_3174()', '.slot()'),
    ('.comp_3837()', '.syncId()'),
    ('.comp_3839()', '.contents()'),
    ('.comp_1626()', '.body()'),
    ('.comp_1627()', '.cape()'),
    ('.comp_1628()', '.elytra()'),
    ('.comp_1629()', '.model()'),
    ('.comp_1630()', '.secure()'),
    ('.comp_3626()', '.id()'),
    ('.comp_3627()', '.texturePath()'),
    ('.comp_750()', '.vertexCount()'),
    ('.comp_3159()', '.forward()'),
    ('.comp_3160()', '.backward()'),
    ('.comp_3161()', '.left()'),
    ('.comp_3162()', '.right()'),
    ('.comp_3163()', '.jump()'),
    ('.comp_3164()', '.sneak()'),
    ('.comp_3165()', '.sprint()'),
    ('.comp_2127()', '.name()'),
    ('.comp_4932()', '.pos()'),
)
for p in sorted(java_root.rglob('*.java')):
    source = p.read_text(encoding='utf-8', errors='replace')
    for old, new in API_REPLACEMENTS:
        source = source.replace(old, new)
    # MatrixStack.Entry must stay qualified: the source also imports Map.Entry.
    source = re.sub(r'\bclass_4665\b', 'MatrixStack.Entry', source)
    # The intermediary nested class replacement can leave a duplicated outer name.
    source = source.replace(
        'import net.minecraft.client.util.math.MatrixStack.MatrixStack.Entry;\n',
        'import net.minecraft.client.util.math.MatrixStack;\n'
    )
    source = source.replace(
        'import net.minecraft.client.util.math.MatrixStack.Entry;\n',
        'import net.minecraft.client.util.math.MatrixStack;\n'
    )
    if p.name == 'CustomHandShaderConsumer.java':
        source = source.replace('super.quad(', 'VertexConsumer.super.quad(')
    if p.parent.name == 'mixin':
        # Mixin source classes are transformed into their target classes at runtime.
        # Cast through Object so javac accepts the mixin/target type relationship.
        source = re.sub(r'\((?!Object\))([A-Za-z0-9_$.<>?]+)\)this\b', r'(\1)(Object)this', source)
        source = source.replace('this instanceof ', '((Object)this) instanceof ')
        source = source.replace('this == ', '(Object)this == ')
    p.write_text(source, encoding='utf-8')

# The old device-login and Discord IPC clients are not referenced by the local-only
# foundation bootstrap. Keep the unrelated cosmetic renderer in the discord package.
for rel in (
    'ai/derco/auth/DeviceAuthClient.java',
    'ai/derco/discord/DercoVisualsDiscordRPC.java',
):
    (java_root / rel).unlink(missing_ok=True)

def patch_generated_java(relative_path, replacements):
    path = java_root / relative_path
    if not path.exists():
        raise SystemExit(f'Expected generated source is missing: {relative_path}')
    source = path.read_text(encoding='utf-8', errors='replace')
    for old, new in replacements:
        if old not in source:
            raise SystemExit(f'Could not apply migration patch in {relative_path}: {old}')
        source = source.replace(old, new)
    path.write_text(source, encoding='utf-8')


def patch_generated_java_idempotent(relative_path, replacements):
    """Apply one exact migration, accepting either its original or fixed state."""
    path = java_root / relative_path
    if not path.exists():
        raise SystemExit(f'Expected generated source is missing: {relative_path}')
    source = path.read_text(encoding='utf-8', errors='replace')
    changed = False
    for old, new in replacements:
        old_count = source.count(old)
        new_count = source.count(new)
        if old_count == 1 and new_count == 0:
            source = source.replace(old, new, 1)
            changed = True
        elif old_count == 0 and new_count == 1:
            continue
        else:
            raise SystemExit(
                f'Expected exactly one old or fixed selector in {relative_path}; '
                f'old={old_count}, fixed={new_count}'
            )
    if changed:
        path.write_text(source, encoding='utf-8')


# Correct Camera invocation owners using the exact Yarn 1.21.11 descriptors.
# The old owner omitted client/render; require=0 silently disabled both camera hooks.
patch_generated_java('ai/derco/mixin/CameraMixin.java', (
    ('@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;setRotation(FF)V", ordinal = 0), require = 0)',
     '@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0), require = 1)'),
    ('@ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;clipToSpace(F)F"), index = 0, require = 0)',
     '@ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"), index = 0, require = 1)'),
))

patch_generated_java_idempotent('ai/derco/mixin/ChatHudMixin.java', (
    ('''   @Redirect(
      require = 0,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/DrawContext;drawTextWithShadow(Lnet/minecraft/TextRenderer;Lnet/minecraft/OrderedText;III)V")
   )''',
     '''   @Redirect(
      require = 1,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)V")
   )'''),
))

patch_generated_java_idempotent('ai/derco/mixin/PlayerListHudMixin.java', (
    ('''   @Redirect(
      require = 0,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/DrawContext;drawTextWithShadow(Lnet/minecraft/TextRenderer;Lnet/minecraft/Text;III)V")
   )''',
     '''   @Redirect(
      require = 1,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V")
   )'''),
    ('@Redirect(require = 0, method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/ClientConnection;isEncrypted()Z"))',
     '@Redirect(require = 1, method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))'),
    ('''   @Redirect(
      require = 0,
      method = "render",
      at = @At(value = "FIELD", target = "Lnet/minecraft/PlayerListHud;footer:Lnet/minecraft/Text;", opcode = 180)
   )''',
     '''   @Redirect(
      require = 1,
      method = "render",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;footer:Lnet/minecraft/text/Text;", opcode = 180)
   )'''),
))

# Correct the ItemCooldownManager.Entry accessor against Yarn 1.21.11+build.3.
# The decompiler emitted a hybrid target name and intermediary record component names;
# the named target and fields below are the exact mapped names for this Minecraft version.
patch_generated_java('ai/derco/mixin/accessor/ItemCooldownEntryAccessor.java', (
    ('@Mixin(targets = "net/minecraft/ItemCooldownManager$class_1797")',
     '@Mixin(targets = "net/minecraft/entity/player/ItemCooldownManager$Entry")'),
    ('@Accessor("comp_3083")', '@Accessor("startTick")'),
    ('@Accessor("comp_3084")', '@Accessor("endTick")'),
))

# TextMixin never reads the decompiler-emitted record field. Remove the unused
# intermediary @Shadow instead of carrying an invalid named-namespace field target.
patch_generated_java('ai/derco/mixin/TextMixin.java', (
    ('import org.spongepowered.asm.mixin.Final;\n', ''),
    ('import org.spongepowered.asm.mixin.Shadow;\n', ''),
    ('   @Shadow\n   @Final\n   private String comp_737;\n', ''),
))

# javac synthesizes $assertionsDisabled when compiling Java assert statements.
# The decompiler also emitted a redundant copy of that field into FontManager.
# Keep the real assert statement and remove only the unused decompiler artifact.
font_manager_path = java_root / 'ai/derco/render/font/FontManager.java'
if not font_manager_path.exists():
    raise SystemExit('Expected generated source is missing: ai/derco/render/font/FontManager.java')
font_manager_source = font_manager_path.read_text(encoding='utf-8', errors='replace')
if 'assert resourceAsStream != null;' not in font_manager_source:
    raise SystemExit('FontManager assertion migration precondition failed; expected Java assert statement is missing')
assertion_field = 'static final boolean $assertionsDisabled = !FontManager.class.desiredAssertionStatus();'
font_manager_lines = font_manager_source.splitlines(keepends=True)
matching_assertion_fields = [line for line in font_manager_lines if line.strip() == assertion_field]
if len(matching_assertion_fields) != 1:
    raise SystemExit('FontManager assertion migration precondition failed; expected exactly one decompiler-generated field')
font_manager_source = ''.join(line for line in font_manager_lines if line.strip() != assertion_field)
if '$assertionsDisabled' in font_manager_source:
    raise SystemExit('FontManager assertion migration left a reference to the removed decompiler field')
font_manager_path.write_text(font_manager_source, encoding='utf-8')

# The embedded icon-font files use the DercoVisuals resource names in the archive.
# The original decompiled FontManager still asked for the old Pulse filenames, causing
# Font.createFont(null) to throw IOException during class initialization on game startup.
patch_generated_java('ai/derco/render/font/FontManager.java', (
    ('"/assets/pulse/font/"', '"/assets/dercovisuals/font/"'),
    ('load(i4, "pulseicon.ttf")', 'load(i4, "dercovisualsicon.ttf")'),
    ('load(i5, "pulse_icons.ttf")', 'load(i5, "dercovisuals_icons.ttf")'),
    ('throw new RuntimeException(e2);',
     'System.err.println("[DercoVisuals] Could not load font resource " + str + ": " + e2.getMessage() + "; using SansSerif fallback.");\n'
     '         Font fallbackFont = new Font(Font.SANS_SERIF, Font.PLAIN, 1).deriveFont(Font.PLAIN, f / 2.0F);\n'
     '         return new FontRenderer(fallbackFont, f / 2.0F);'),
))

# Avoid parsing the same embedded TTF once per requested UI size.
# Java Font objects are immutable; deriving each size from one shared base Font keeps
# the existing public arrays/API while reusing the parsed font face.
font_manager_path.write_text(r'''package ai.derco.render.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public final class FontManager {
   public static final FontRenderer[] REGULAR = new FontRenderer[65];
   public static final FontRenderer[] MEDIUM = new FontRenderer[65];
   public static final FontRenderer[] ICONS = new FontRenderer[65];
   public static final FontRenderer[] PULSE_ICONS = new FontRenderer[265];
   public static final FontRenderer[] SMALL_ICONS = new FontRenderer[65];
   public static final FontRenderer[] a;
   public static final FontRenderer[] b;
   public static final FontRenderer[] c;
   public static final FontRenderer[] d;
   public static final FontRenderer[] e;

   private static Font loadBase(String name) {
      String path = "/assets/dercovisuals/font/" + name;
      try (InputStream input = FontManager.class.getResourceAsStream(path)) {
         if (input == null) {
            throw new IOException("Missing font resource: " + path);
         }
         return Font.createFont(Font.TRUETYPE_FONT, input);
      } catch (IOException | FontFormatException failure) {
         System.err.println("[DercoVisuals] Could not load font resource " + name
            + ": " + failure.getMessage() + "; using SansSerif fallback.");
         return new Font(Font.SANS_SERIF, Font.PLAIN, 1);
      }
   }

   private static void fill(FontRenderer[] renderers, Font baseFont, int firstSize, int endSizeExclusive) {
      for (int size = firstSize; size < endSizeExclusive; size++) {
         renderers[size] = new FontRenderer(baseFont, size / 2.0F);
      }
   }

   static {
      Font regular = loadBase("sfprodisplayregular.ttf");
      Font medium = loadBase("sfprodisplay-medium-test.ttf");
      Font icons = loadBase("icons.ttf");
      Font visualIcons = loadBase("dercovisualsicon.ttf");
      Font smallIcons = loadBase("dercovisuals_icons.ttf");

      fill(REGULAR, regular, 6, 65);
      fill(MEDIUM, medium, 6, 65);
      fill(ICONS, icons, 6, 65);
      fill(PULSE_ICONS, visualIcons, 6, 265);
      fill(SMALL_ICONS, smallIcons, 6, 65);

      a = REGULAR;
      b = MEDIUM;
      c = ICONS;
      d = PULSE_ICONS;
      e = SMALL_ICONS;
   }

   private FontManager() {
   }
}
''', encoding='utf-8')

# The draw path used to allocate a fresh Matrix4f for every string, but the batch
# methods never read that value; DrawContext already owns the active transform.
patch_generated_java('ai/derco/render/font/FontRenderer.java', (
    ('Matrix4f matrix4fGetPositionMatrix = this.getMatrix(MatrixStackVar);',
     'Matrix4f matrix4fGetPositionMatrix = null;'),
    ('char[] charArray = str.toCharArray();', 'int charLength = str.length();'),
    ('charArray.length', 'charLength'),
    ('charArray[i3]', 'str.charAt(i3)'),
    ('charArray[i4]', 'str.charAt(i4)'),
))

# Cache one fallback face per glyph atlas instead of allocating a new AWT Font for
# every unsupported character in a 256-codepoint page.
patch_generated_java('ai/derco/render/font/FontGlyphAtlas.java', (
    ('private final Font baseFont;',
     'private final Font baseFont;\n   private final Font fallbackFont;'),
    ('this.baseFont = font;\n      this.a = IdentifierVar;',
     'this.baseFont = font;\n      this.fallbackFont = new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(1, font.getSize()));\n      this.a = IdentifierVar;'),
    ('return this.baseFont.canDisplay(c) ? this.baseFont : new Font("SansSerif", 0, this.baseFont.getSize());',
     'return this.baseFont.canDisplay(c) ? this.baseFont : this.fallbackFont;'),
))

# The bundled media provider is optional. If it is absent, avoid starting a polling
# loop that wakes 125 times/second and allocates a zero-level array every 16 ms.
patch_generated_java('ai/derco/media/MediaSessionService.java', (
    ('private static final AtomicReference<float[]> j;',
     'private static final float[] ZERO_LEVELS = new float[]{0.0F, 0.0F, 0.0F};\n   private static final AtomicReference<float[]> j;'),
    ('private final float[] o = new float[6];',
     'private final float[] o = new float[6];\n   private final float[] v = new float[6];'),
    ('   private static void k() {\n      long r8 = 0L;',
     '   private static void k() {\n      if (!q()) {\n         i.set(null);\n         j.set(ZERO_LEVELS);\n         f.set(false);\n         System.out.println("WMedia provider unavailable; polling disabled.");\n         return;\n      }\n\n      long r8 = 0L;'),
    ('float[] fArr = new float[]{0.0F, 0.0F, 0.0F};\n         j.set(fArr);',
     'j.set(ZERO_LEVELS);'),
    ('float[] fArr2 = new float[]{0.0F, 0.0F, 0.0F};\n               j.set(fArr2);',
     'j.set(ZERO_LEVELS);'),
    ('float[] fArr3 = new float[]{0.0F, 0.0F, 0.0F};\n            j.set(fArr3);',
     'j.set(ZERO_LEVELS);'),
    ('float[] fArr2 = new float[]{\n            (fMin2 * 0.6F + f5 * 0.3F) * fSin,\n            (fMin2 * 0.85F + fMin * 0.3F + f5 * 0.5F) * fSin2,\n            (fMin2 * 0.5F + fMin * 0.7F + f5 * 0.7F) * fSin3,\n            (fMin3 * 0.5F + fMin * 0.7F + f5 * 0.7F) * fSin,\n            (fMin3 * 0.85F + fMin * 0.3F + f5 * 0.5F) * fSin3,\n            (fMin3 * 0.6F + f5 * 0.3F) * fSin2\n         };',
     'float[] fArr2 = this.v;\n         fArr2[0] = (fMin2 * 0.6F + f5 * 0.3F) * fSin;\n         fArr2[1] = (fMin2 * 0.85F + fMin * 0.3F + f5 * 0.5F) * fSin2;\n         fArr2[2] = (fMin2 * 0.5F + fMin * 0.7F + f5 * 0.7F) * fSin3;\n         fArr2[3] = (fMin3 * 0.5F + fMin * 0.7F + f5 * 0.7F) * fSin;\n         fArr2[4] = (fMin3 * 0.85F + fMin * 0.3F + f5 * 0.5F) * fSin3;\n         fArr2[5] = (fMin3 * 0.6F + f5 * 0.3F) * fSin2;'),
    ('      float[] fArr = new float[]{0.0F, 0.0F, 0.0F};\n      j = new AtomicReference<>(fArr);',
     '      j = new AtomicReference<>(ZERO_LEVELS);'),
))

# Bound hit-sound work to one reusable daemon worker. Clips play asynchronously after
# opening, so the worker can start the next sound without blocking for the clip duration.
patch_generated_java('ai/derco/audio/SoundPlayer.java', (
    ('''import java.util.Random;''',
     '''import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;'''),
    ('''private static final String[] MOAN_SOUNDS = new String[]{"moan1", "moan2", "moan3", "moan4"};''',
     '''private static final String[] MOAN_SOUNDS = new String[]{"moan1", "moan2", "moan3", "moan4"};
   private static final ThreadPoolExecutor AUDIO_EXECUTOR = new ThreadPoolExecutor(
      1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(12),
      runnable -> {
         Thread worker = new Thread(runnable, "DercoVisuals Audio Worker");
         worker.setDaemon(true);
         return worker;
      },
      new ThreadPoolExecutor.DiscardOldestPolicy()
   );'''),
    ('''Thread thread = new Thread(() -> {''', '''AUDIO_EXECUTOR.execute(() -> {'''),
    ('''      }, "DercoVisuals Sound Player");
      thread.setDaemon(true);
      thread.start();''', '''      });'''),
))

# Restore collection element types lost by the upstream decompiler.
patch_generated_java('ai/derco/discord/CosmeticHelper.java', (
    ('Map var7 = CosmeticRepository.a().b();',
     'Map<Integer, CosmeticModel> var7 = CosmeticRepository.a().b();'),
))
patch_generated_java('ai/derco/hud/elements/HotkeysHudElement.java', (
    ('private List<HotkeysHudElement.HotkeySnapshot> x() {\n      ArrayList arrayList = new ArrayList();',
     'private List<HotkeysHudElement.HotkeySnapshot> x() {\n      ArrayList<HotkeysHudElement.HotkeySnapshot> arrayList = new ArrayList<>();'),
))
patch_generated_java('ai/derco/hud/elements/PotionsHudElement.java', (
    ('private List<PotionsHudElement.EffectEntry> y() {\n      ArrayList arrayList = new ArrayList();',
     'private List<PotionsHudElement.EffectEntry> y() {\n      ArrayList<PotionsHudElement.EffectEntry> arrayList = new ArrayList<>();'),
))
patch_generated_java('ai/derco/modules/hud/PotionsHud.java', (
    ('import ai.derco.settings.SliderSetting;\n',
     'import ai.derco.settings.ModeSetting;\nimport ai.derco.settings.SliderSetting;\n'),
    ('   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);\n\n   public PotionsHud() {',
     '''   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);
    private final SliderSetting maxVisibleEffects = new SliderSetting(
       "Макс. эффектов", "Количество активных эффектов, одновременно отображаемых в HUD.", 6.0F, 1.0F, 12.0F, 1.0F
    );
    private final ModeSetting sortMode = new ModeSetting(
       "Сортировка", "Порядок отображения активных эффектов.",
       new String[]{"Скоро закончатся", "Дольше действуют", "По названию"}, 0
    );

    public PotionsHud() {'''),
    ('   public SliderSetting n() {\n      return this.scale;\n   }\n}',
     '''   public SliderSetting n() {
       return this.scale;
    }

    public int maxVisibleEffects() {
       return this.maxVisibleEffects.roundedInt();
    }

    public int sortMode() {
       return this.sortMode.k();
    }
 }'''),
))
patch_generated_java('ai/derco/hud/elements/PotionsHudElement.java', (
    ('      arrayList.sort(Comparator.comparingInt(effectEntry -> effectEntry.d));\n      return arrayList;',
     '''      PotionsHud potionsHud = ModuleRegistry.POTIONS_HUD;
      int sortMode = potionsHud != null ? potionsHud.sortMode() : 0;
      if (sortMode == 1) {
         arrayList.sort(Comparator.comparingInt((PotionsHudElement.EffectEntry effectEntry) ->
            effectEntry.d < 0 ? Integer.MAX_VALUE : effectEntry.d).reversed());
      } else if (sortMode == 2) {
         arrayList.sort(Comparator.comparing((PotionsHudElement.EffectEntry effectEntry) -> effectEntry.b, String.CASE_INSENSITIVE_ORDER));
      } else {
         arrayList.sort(Comparator.comparingInt(effectEntry ->
            effectEntry.d < 0 ? Integer.MAX_VALUE : effectEntry.d));
      }
      if (potionsHud != null) {
         int maxVisibleEffects = potionsHud.maxVisibleEffects();
         if (arrayList.size() > maxVisibleEffects) {
            arrayList.subList(maxVisibleEffects, arrayList.size()).clear();
         }
      }
      return arrayList;'''),
    ('''      if (i <= 0) {
         return "--:--";
      }''',
     '''      if (i == StatusEffectInstance.INFINITE) {
         return "∞";
      }
      if (i <= 0) {
         return "--:--";
      }'''),
))
patch_generated_java('ai/derco/modules/hud/CooldownsHud.java', (
    ('   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);\n\n   public CooldownsHud() {',
     '''   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);
   private final SliderSetting maxVisibleCooldowns = new SliderSetting(
      "Макс. кулдаунов", "Количество активных кулдаунов, одновременно отображаемых в HUD.", 4.0F, 1.0F, 8.0F, 1.0F
   );

   public CooldownsHud() {'''),
    ('   public SliderSetting n() {\n      return this.scale;\n   }\n}',
     '''   public SliderSetting n() {
      return this.scale;
   }

   public int maxVisibleCooldowns() {
      return this.maxVisibleCooldowns.roundedInt();
   }
}'''),
))

patch_generated_java('ai/derco/hud/elements/CooldownsHudElement.java', (
    ('      arrayList.sort(Comparator.comparingInt(cooldownSnapshot -> cooldownSnapshot.e));\n      return arrayList;',
     '''      arrayList.sort(Comparator.comparingInt(cooldownSnapshot -> cooldownSnapshot.e));
      var cooldownsHud = ModuleRegistry.COOLDOWNS_HUD;
      if (cooldownsHud != null) {
         int maxVisibleCooldowns = cooldownsHud.maxVisibleCooldowns();
         if (arrayList.size() > maxVisibleCooldowns) {
            arrayList.subList(maxVisibleCooldowns, arrayList.size()).clear();
         }
      }
      return arrayList;'''),
))

# Reuse the active item-cooldown snapshot for preview state and rendering.
# In editing screens the old preview predicate called x() independently from the
# update and render paths, rebuilding the same cooldown list up to three times.
patch_generated_java('ai/derco/hud/elements/CooldownsHudElement.java', (
    ('''   private boolean v() {
      return Bool.from((this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen) && this.x().isEmpty() ? 1 : 0);
   }''',
     '''   private boolean v() {
      return Bool.from((this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen) && this.Y.isEmpty() ? 1 : 0);
   }'''),
    ('''      this.w();
      boolean zV = this.v();
      List<CooldownsHudElement.CooldownSnapshot> listX = this.x();
      this.Y = listX;''',
     '''      this.w();
      List<CooldownsHudElement.CooldownSnapshot> listX = this.x();
      this.Y = listX;
      boolean zV = this.v();'''),
))


# Cooldown snapshots only change when the local player's tick advances. The HUD
# update method is called from render, so cache the inventory scan/sort by player
# identity and age while leaving animation/layout updates per frame.
patch_generated_java('ai/derco/hud/elements/CooldownsHudElement.java', (
    ('''   private List<CooldownsHudElement.CooldownSnapshot> Y = List.of();''',
     '''   private List<CooldownsHudElement.CooldownSnapshot> Y = List.of();
   private Object cachedSnapshotPlayer;
   private int cachedSnapshotTick = Integer.MIN_VALUE;'''),
    ('''      this.w();
      List<CooldownsHudElement.CooldownSnapshot> listX = this.x();
      this.Y = listX;
      boolean zV = this.v();''',
     '''      this.w();
      List<CooldownsHudElement.CooldownSnapshot> listX = this.currentSnapshots();
      boolean zV = this.v();'''),
    ('''   private List<CooldownsHudElement.CooldownSnapshot> x() {''',
     '''   private List<CooldownsHudElement.CooldownSnapshot> currentSnapshots() {
      if (this.a.player == null) {
         this.Y = List.of();
         this.cachedSnapshotPlayer = null;
         this.cachedSnapshotTick = Integer.MIN_VALUE;
         return this.Y;
      }

      if (this.cachedSnapshotPlayer != this.a.player || this.cachedSnapshotTick != this.a.player.age) {
         this.Y = this.x();
         this.cachedSnapshotPlayer = this.a.player;
         this.cachedSnapshotTick = this.a.player.age;
      }

      return this.Y;
   }

   private List<CooldownsHudElement.CooldownSnapshot> x() {''')
))

# Reuse the render reconciliation scratch set and iterate the live values view.
# The loop only marks entries for fade-out; structural removals happen in the following
# removeIf call, so copying the map values into a new ArrayList is unnecessary.
patch_generated_java('ai/derco/hud/elements/CooldownsHudElement.java', (
    ('''   private int cachedSnapshotTick = Integer.MIN_VALUE;''',
     '''   private int cachedSnapshotTick = Integer.MIN_VALUE;
   private final HashSet<Item> activeCooldownItems = new HashSet<>();'''),
    ('''         HashSet hashSet = new HashSet();
         int idx = 0;''',
     '''         HashSet<Item> hashSet = this.activeCooldownItems;
         hashSet.clear();
         int idx = 0;'''),
    ('''         for (CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry3 : new ArrayList<>(this.O.values())) {''',
     '''         for (CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry3 : this.O.values()) {'''),
))

patch_generated_java('ai/derco/gui/settings/ModeSettingWidget.java', (
    ('ArrayList arrayList = new ArrayList<>(this.v);',
     'ArrayList<Integer> arrayList = new ArrayList<>(this.v);'),
))
patch_generated_java('ai/derco/gui/modules/ModulesTab.java', (
    ('ArrayList arrayList = new ArrayList();',
     'ArrayList<ModulesTab.ModuleEntry> arrayList = new ArrayList<>();'),
    ('Comparator.comparing(moduleEntry -> moduleEntry.a().toLowerCase())',
     'Comparator.comparing((ModulesTab.ModuleEntry moduleEntry) -> moduleEntry.a().toLowerCase())'),
    ('Comparator.comparing(moduleEntry2 -> moduleEntry2.a().toLowerCase())',
     'Comparator.comparing((ModulesTab.ModuleEntry moduleEntry2) -> moduleEntry2.a().toLowerCase())'),
))
patch_generated_java('ai/derco/effects/ParticleRenderService.java', (
    ('final float rotation = ThreadLocalRandom.current().nextFloat() * 360.0F;',
     'float rotation = ThreadLocalRandom.current().nextFloat() * 360.0F;'),
    ('''(new Vec3d(Math.sin(this.wobblePhase) * this.wobbleAmplitude, 0.0, Math.cos(this.wobblePhase) * this.wobbleAmplitude)).pos = this.pos
               .add(this);''',
     '''this.pos = this.pos.add(new Vec3d(
               Math.sin(this.wobblePhase) * this.wobbleAmplitude,
               0.0,
               Math.cos(this.wobblePhase) * this.wobbleAmplitude
            ));'''),
))
patch_generated_java('ai/derco/mixin/DebugHudMixin.java', (
    ('''((List)callbackInfoReturnable.getReturnValue()).stream().filter(o -> {
            String str = (String)o;''',
     '''callbackInfoReturnable.getReturnValue().stream().filter((String str) -> {'''),
))

# Exclude the weapon-specific Mace helper and all of its visual hooks.
(java_root / 'ai/derco/modules/utilities/MaceHelper.java').unlink(missing_ok=True)
(java_root / 'ai/derco/render/MaceHelperHandConsumer.java').unlink(missing_ok=True)
patch_generated_java('ai/derco/module/ModuleRegistry.java', (
    ('import ai.derco.modules.utilities.MaceHelper;\n', ''),
    ('   public static final MaceHelper MACE_HELPER = new MaceHelper();\n', ''),
    ('         MACE_HELPER,\n', ''),
))
patch_generated_java('ai/derco/render/HandRenderState.java', (
    ('   public static boolean renderingMace = false;\n', ''),
))
patch_generated_java('ai/derco/mixin/HeldItemRendererMixin.java', (
    ('import ai.derco.modules.utilities.MaceHelper;\n', ''),
    ('      HandRenderState.renderingMace = MaceHelper.isMace(ItemStackVar);\n', ''),
    ('         HandRenderState.renderingMace = false;\n', ''),
))
patch_generated_java('ai/derco/mixin/ItemRendererMixin.java', (
    ('import ai.derco.modules.utilities.MaceHelper;\n', ''),
    ('import ai.derco.render.MaceHelperHandConsumer;\n', ''),
    ('''      MaceHelper maceHelper = ModuleRegistry.MACE_HELPER;
      if ((HandRenderState.renderingMace || HandRenderState.renderingHand)
         && maceHelper != null
         && maceHelper.k()
         && maceHelper.handHighlight.get()
         && MaceHelper.isHoldingMace()) {
         return new MaceHelperHandConsumer(original, MaceHelper.getChargeColor());
      }

''', ''),
    ('''         MaceHelper maceHelper = ModuleRegistry.MACE_HELPER;
         if (maceHelper != null && maceHelper.k() && maceHelper.hotbarHighlight.get() && MaceHelper.isMace(ItemStackVar)) {
            Color colorB = MaceHelper.getChargeColor();
            RenderSystemHelper.disableDepthTest();
            RenderSystemHelper.enableBlend();
            RenderSystemHelper.defaultBlendFunc();
            this.renderGuiQuad(
               Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR),
               i,
               i2,
               16,
               16,
               colorB.getRed(),
               colorB.getGreen(),
               colorB.getBlue(),
               120
            );
            RenderSystemHelper.enableDepthTest();
         }

''', ''),
))
patch_generated_java('ai/derco/mixin/DrawContextMixin.java', (
    ('import ai.derco.modules.utilities.MaceHelper;\n', ''),
    ('      MaceHelper maceHelper = ModuleRegistry.MACE_HELPER;\n', ''),
    ('''      if (maceHelper != null && maceHelper.k() && maceHelper.hotbarHighlight.get() && MaceHelper.isMace(ItemStackVar)) {
         Color colorA = MaceHelper.getChargeColor();
         int rgb = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), 120).getRGB();
         Matrix3x2fStack matrices = ((DrawContext)(Object)this).getMatrices();
         matrices.pushMatrix();
         matrices.translate(0.0F, 0.0F);
         this.fill(i, i2, i + 16, i2 + 16, rgb);
         matrices.popMatrix();
      } else if (itemHighlighter != null''',
     '''      if (itemHighlighter != null'''),
))

# Do not port automated attack logic. Remove the module and unregister it cleanly.
trigger_bot = java_root / 'ai/derco/modules/utilities/TriggerBot.java'
trigger_bot.unlink(missing_ok=True)
patch_generated_java('ai/derco/module/ModuleRegistry.java', (
    ('import ai.derco.modules.utilities.TriggerBot;\n', ''),
    ('   public static final TriggerBot TRIGGER_BOT = new TriggerBot();\n', ''),
    ('         KILL_EFFECT,\n         TRIGGER_BOT\n', '         KILL_EFFECT\n'),
))

# EntityFinder deliberately returns Entity; each predicate below already narrows to
# PlayerEntity, so cast only after the result has passed that predicate.
for relative_path in (
    'ai/derco/modules/utilities/HwHelper.java',
    'ai/derco/modules/utilities/FtHelper.java',
    'ai/derco/modules/utilities/FcHelper.java',
):
    path = java_root / relative_path
    source = path.read_text(encoding='utf-8', errors='replace')
    pattern = re.compile(
        r'^(?P<indent>[ \t]*)for \(PlayerEntity PlayerEntityVar : EntityFinder\.a\((?P<args>.*?)\)\) \{',
        re.M | re.S,
    )
    def narrow_player_loop(match):
        indent = match.group('indent')
        return (
            f"{indent}for (Entity EntityFinderVar : EntityFinder.a({match.group('args')})) {{\n"
            f"{indent}   PlayerEntity PlayerEntityVar = (PlayerEntity)EntityFinderVar;"
        )
    source, count = pattern.subn(narrow_player_loop, source)
    if count == 0:
        raise SystemExit(f'No EntityFinder player loops migrated in {relative_path}')
    if 'import net.minecraft.entity.Entity;' not in source:
        source = re.sub(
            r'^(package [^;]+;\n)',
            r'\1\nimport net.minecraft.entity.Entity;\n',
            source,
            count=1,
            flags=re.M,
        )
    path.write_text(source, encoding='utf-8')

# The local-only bootstrap has no updater; remove the stale upstream update hook.
patch_generated_java('ai/derco/mixin/TitleScreenMixin.java', (
    ('''   @Inject(require = 0, method = "init", at = @At("TAIL"))
   private void onInit(CallbackInfo callbackInfo) {
      DercoVisuals.checkAndShowUpdate((TitleScreen)(Object)this);
   }

''', ''),
))


# DercoVisualsMainMenuScreen extends vanilla TitleScreen. The TitleScreen init
# mixin therefore runs again while Minecraft initializes the replacement screen.
# Restrict replacement to the vanilla TitleScreen instance to prevent recursive
# setScreen(new DercoVisualsMainMenuScreen()) calls and StackOverflowError.
title_screen_mixin_path = java_root / 'ai/derco/mixin/TitleScreenMixin.java'
title_screen_mixin_source = title_screen_mixin_path.read_text(encoding='utf-8', errors='replace')
if 'DercoVisualsMainMenuScreen' not in title_screen_mixin_source:
    raise SystemExit('Expected custom main-menu reference was not found in TitleScreenMixin.java')
head_init_pattern = re.compile(r'(\bvoid\s+onInitHead\s*\([^)]*\)\s*\{)')
if len(head_init_pattern.findall(title_screen_mixin_source)) != 1:
    raise SystemExit('Expected exactly one onInitHead injection in TitleScreenMixin.java')
main_menu_guard = 'if (((Object)this).getClass() != net.minecraft.client.gui.screen.TitleScreen.class) return;'
if main_menu_guard not in title_screen_mixin_source:
    title_screen_mixin_source, guard_count = head_init_pattern.subn(
        lambda match: match.group(1) + '\n      ' + main_menu_guard,
        title_screen_mixin_source,
        count=1,
    )
    if guard_count != 1:
        raise SystemExit('Could not insert the TitleScreen recursion guard')
title_screen_mixin_path.write_text(title_screen_mixin_source, encoding='utf-8')

# Player removal moved to PlayerRemoveS2CPacket in 1.21.11. Keep join/leave
# notifications by handling the corresponding packet explicitly.
player_list = java_root / 'ai/derco/mixin/PlayerListMixin.java'
player_list_source = '''package ai.derco.mixin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import ai.derco.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ai.derco.events.EventBusService;
import ai.derco.events.PlayerJoinEvent;
import ai.derco.events.PlayerLeaveEvent;

@Mixin(ClientPlayNetworkHandler.class)
public class PlayerListMixin {
   @Unique
   private static final ConcurrentHashMap<UUID, String> uuidToNameCache = new ConcurrentHashMap<>();

   @Inject(require = 0, method = "onPlayerList", at = @At("HEAD"))
   private void onPlayerListUpdate(PlayerListS2CPacket packet, CallbackInfo ci) {
      if (!packet.getActions().contains(Action.ADD_PLAYER)) {
         return;
      }
      for (Entry entry : packet.getPlayerAdditionEntries()) {
         if (entry.profile() == null || entry.profile().id() == null || entry.profile().name() == null) {
            continue;
         }
         UUID uuid = entry.profile().id();
         String name = entry.profile().name();
         String previous = uuidToNameCache.putIfAbsent(uuid, name);
         if (previous == null) {
            EventBusService.EVENT_BUS.post(new PlayerJoinEvent(name, uuid));
         }
      }
   }

   @Inject(require = 0, method = "onPlayerRemove", at = @At("HEAD"))
   private void onPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
      for (UUID uuid : packet.profileIds()) {
         String name = uuidToNameCache.remove(uuid);
         EventBusService.EVENT_BUS.post(new PlayerLeaveEvent(name == null ? "" : name, uuid));
      }
   }

   @Inject(require = 0, method = "onGameJoin", at = @At("HEAD"))
   private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
   }
}
'''
player_list_source = player_list_source.replace(
    'ai.derco.network.packet.s2c.play.PlayerRemoveS2CPacket',
    'net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket'
)
player_list.write_text(player_list_source, encoding='utf-8')

# The upstream player tracker silently uploads the local username to a remote
# Supabase table on multiplayer joins. It also creates a fresh single-thread
# executor for each request without shutting it down. DercoVisuals is explicitly
# offline-first and must not register or track player names remotely.
tracker_path = java_root / 'ai/derco/player/DercoVisualsPlayerTracker.java'
if not tracker_path.exists():
    raise SystemExit('Expected upstream player-registration tracker was not found')
tracker_path.unlink()
patch_generated_java('ai/derco/DercoVisuals.java', (
    ('import ai.derco.player.DercoVisualsPlayerTracker;\n', ''),
    ('        DercoVisualsPlayerTracker.get().init();\n', ''),
))
patch_generated_java('ai/derco/mixin/EntityRendererMixin.java', (
    ('import ai.derco.player.DercoVisualsPlayerTracker;\n', ''),
    ('boolean has = PlayerListCache.c().a(name) || DercoVisualsPlayerTracker.get().has(name);',
     'boolean has = PlayerListCache.c().a(name);'),
))
patch_generated_java('ai/derco/mixin/PlayerListHudMixin.java', (
    ('import ai.derco.player.DercoVisualsPlayerTracker;\n', ''),
    ('import ai.derco.modules.visuals.TabCustomizer;\n',
     'import ai.derco.modules.visuals.TabCustomizer;\nimport ai.derco.player.PlayerListCache;\n'),
    ('&& DercoVisualsPlayerTracker.get().has(str2)) {',
     '&& PlayerListCache.c().a(str2)) {'),
))

# Fail the source-generation step if the remote player tracker is accidentally
# reintroduced by a future archive refresh or source migration.
for generated_java in java_root.rglob('*.java'):
    generated_source = generated_java.read_text(encoding='utf-8', errors='replace')
    if any(marker in generated_source for marker in (
        'DercoVisualsPlayerTracker',
        '.supabase.co',
        'rest/v1/derco_users',
    )):
        raise SystemExit(
            f'Unexpected remote player-registration code remains in {generated_java.relative_to(java_root)}'
        )

# Rebrand resource namespace and remove Pulse-only auth/brand assets where not referenced.
for p in (SRC / 'resources').rglob('*'):
    if p.is_file() and p.suffix.lower() in ('.json', '.fsh', '.vsh', '.frag', '.glsl', '.txt'):
        try: t = p.read_text(encoding='utf-8')
        except UnicodedecodeError: continue
        t = t.replace('assets/pulse', 'assets/dercovisuals').replace('pulse:', 'dercovisuals:').replace('"pulse"', '"dercovisuals"').replace('ru.pulse', 'ai.derco').replace('pulse.', 'ai.derco.')
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
    'version': '${version}',
    'icon': 'assets/dercovisuals/textures/icon.png',
    'description': 'A free, modular, client-side visual suite for Minecraft 1.21.11.',
    'authors': ['enizer6569'], 'license': 'LicenseRef-DercoVisuals-Community',
    'environment': 'client',
    'entrypoints': {
        'main': ['ai.derco.DercoVisuals'],
        'preLaunch': ['ai.derco.crash.CrashLogBootstrap']
    },
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

# Remove the unused compatibility stub left by the upstream account integration.
(java_root / 'aethereal' / 'Session.java').unlink(missing_ok=True)
try:
    (java_root / 'aethereal').rmdir()
except OSError:
    pass

# Finish the rebrand inside generated Java: config paths, resource identifiers,
# mixin-owned helper names, logger names, and remaining user-facing labels.
for p in sorted(java_root.rglob('*.java')):
    source = p.read_text(encoding='utf-8', errors='replace')
    source = source.replace('pulse', 'derco')
    source = source.replace('assets/derco/', 'assets/dercovisuals/')
    source = source.replace('/assets/derco/', '/assets/dercovisuals/')
    source = source.replace('derco:', 'dercovisuals:')
    source = source.replace('Identifier.of("derco",', 'Identifier.of("dercovisuals",')
    source = source.replace('() -> "derco"', '() -> "dercovisuals"')
    source = source.replace('LogManager.getLogger("derco/config")', 'LogManager.getLogger("dercovisuals/config")')
    source = source.replace('FOLDER_NAME = "derco"', 'FOLDER_NAME = "dercovisuals"')
    source = source.replace('resolve("derco")', 'resolve("dercovisuals")')
    source = source.replace('"derco-autosave"', '"dercovisuals-autosave"')
    source = source.replace('"derco_config_v1"', '"dercovisuals_config_v1"')
    source = source.replace('EVENT_BUS.registerLambdaFactory("derco"', 'EVENT_BUS.registerLambdaFactory("ai.derco"')
    source = source.replace('".derco"', '".derco"')
    source = source.replace('DercoVisualsVisuals', 'DercoVisuals')
    source = source.replace('derco @Alex_sigma', 'DercoVisuals')
    p.write_text(source, encoding='utf-8')

# Rename any remaining resource filenames that still carry the upstream brand.
for p in sorted((SRC / 'resources').rglob('*'), key=lambda item: len(item.parts), reverse=True):
    if p.exists() and 'pulse' in p.name.lower():
        target = p.with_name(re.sub('pulse', 'dercovisuals', p.name, flags=re.I))
        if target.exists():
            raise SystemExit(f'Resource rename would overwrite {target.relative_to(ROOT)}')
        p.rename(target)


# Generate the original DercoVisuals illustrations inside tools/assets, then copy the
# PNG source files into the final Minecraft namespace. The upstream archive is untouched.
ASSET_SCRIPT = ROOT / 'tools' / 'assets' / 'generate_derco_assets.py'
if not ASSET_SCRIPT.is_file():
    raise SystemExit(f'Missing DercoVisuals artwork source: {ASSET_SCRIPT}')
runpy.run_path(str(ASSET_SCRIPT), run_name='__main__')
ASSET_SOURCE_ROOT = ROOT / 'tools' / 'assets' / 'png'
asset_manifest_path = ASSET_SOURCE_ROOT / 'asset-manifest.json'
if not asset_manifest_path.is_file():
    raise SystemExit('DercoVisuals artwork generator did not create asset-manifest.json')
asset_manifest = json.loads(asset_manifest_path.read_text(encoding='utf-8'))
asset_paths = [asset for group in asset_manifest.values() for asset in group]
if len(asset_paths) != 42 or len(set(asset_paths)) != 42:
    raise SystemExit(f'Expected 42 distinct DercoVisuals assets; manifest declares {len(asset_paths)}')
asset_resource_root = SRC / 'resources' / 'assets' / 'dercovisuals'
for relative_asset in asset_paths:
    source_asset = ASSET_SOURCE_ROOT / relative_asset
    if not source_asset.is_file() or source_asset.stat().st_size == 0:
        raise SystemExit(f'DercoVisuals source PNG is missing or empty: {relative_asset}')
    destination_asset = asset_resource_root / relative_asset
    destination_asset.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source_asset, destination_asset)

# Re-target visual hooks to the Minecraft 1.21.11 Yarn API.
# These changes are applied to generated sources so every CI build and local
# build receives the same working mixins instead of stale decompiler targets.

lightmap_mixin_path = java_root / 'ai/derco/mixin/LightmapTextureManagerMixin.java'
lightmap_mixin_source = lightmap_mixin_path.read_text(encoding='utf-8', errors='replace')
if 'method = "getDarknessFactor"' not in lightmap_mixin_source:
    raise SystemExit('Expected legacy FullBright hook was not found in LightmapTextureManagerMixin.java')
lightmap_mixin_path.write_text('''package ai.derco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ai.derco.module.ModuleRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.render.LightmapTextureManager;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
   @Inject(method = "getDarkness(Lnet/minecraft/entity/LivingEntity;FF)F", at = @At("HEAD"), cancellable = true)
   private void derco$disableDarkness(LivingEntity entity, float factor, float tickProgress, CallbackInfoReturnable<Float> cir) {
      if (ModuleRegistry.FULL_BRIGHT != null && ModuleRegistry.FULL_BRIGHT.k()) {
         cir.setReturnValue(0.0F);
      }
   }

   @Inject(method = "getBrightness(Lnet/minecraft/world/dimension/DimensionType;I)F", at = @At("HEAD"), cancellable = true)
   private static void derco$fullDimensionBrightness(DimensionType dimensionType, int lightLevel, CallbackInfoReturnable<Float> cir) {
      if (ModuleRegistry.FULL_BRIGHT != null && ModuleRegistry.FULL_BRIGHT.k()) {
         cir.setReturnValue(1.0F);
      }
   }

   @Inject(method = "getBrightness(FI)F", at = @At("HEAD"), cancellable = true)
   private static void derco$fullAmbientBrightness(float ambientLight, int lightLevel, CallbackInfoReturnable<Float> cir) {
      if (ModuleRegistry.FULL_BRIGHT != null && ModuleRegistry.FULL_BRIGHT.k()) {
         cir.setReturnValue(1.0F);
      }
   }
}
''', encoding='utf-8')

# DebugHud no longer exposes getLeftText/getRightText in 1.21.11.
# Filter the actual list passed to drawText, which is used for both columns.
debug_hud_path = java_root / 'ai/derco/mixin/DebugHudMixin.java'
debug_hud_source = debug_hud_path.read_text(encoding='utf-8', errors='replace')
if 'method = "getLeftText"' not in debug_hud_source or 'method = "getRightText"' not in debug_hud_source:
    raise SystemExit('Expected legacy Streamer Mode DebugHud hooks were not found')
debug_hud_path.write_text('''package ai.derco.mixin;

import java.util.List;
import java.util.stream.Collectors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ai.derco.module.ModuleRegistry;
import ai.derco.modules.utilities.StreamerMode;
import net.minecraft.client.gui.hud.DebugHud;

@Mixin(DebugHud.class)
public class DebugHudMixin {
   @ModifyVariable(method = "drawText", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private List<String> derco$hideSensitiveDebugLines(List<String> lines) {
      StreamerMode streamerMode = ModuleRegistry.STREAMER_MODE;
      if (lines == null || streamerMode == null || !streamerMode.k() || !streamerMode.hideDebugInfo.a()) {
         return lines;
      }

      return lines.stream().filter(line ->
         !line.contains("XYZ:")
            && !line.contains("Block:")
            && !line.contains("Chunk:")
            && !line.contains("Facing:")
            && !line.contains("Targeted Block:")
            && !line.contains("Targeted Fluid:")
            && !line.contains("Targeted Entity")
      ).collect(Collectors.toList());
   }
}
''', encoding='utf-8')

# World#getTime was removed; time control is applied through getTimeOfDay.
world_mixin_path = java_root / 'ai/derco/mixin/WorldMixin.java'
world_mixin_source = world_mixin_path.read_text(encoding='utf-8', errors='replace')
obsolete_time_hook = '''   @Inject(require = 0, method = "getTime", at = @At("HEAD"), cancellable = true)
   private void onGetTime(CallbackInfoReturnable<Long> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isTimeCustom()) {
         callbackInfoReturnable.setReturnValue(atmosphere.getCustomTime());
      }
   }
'''
if obsolete_time_hook not in world_mixin_source:
    raise SystemExit('Expected obsolete World#getTime hook was not found')
world_mixin_path.write_text(world_mixin_source.replace(obsolete_time_hook, ''), encoding='utf-8')

# Cloud tinting moved from ClientWorld#getCloudsColor to CloudRenderer#renderClouds.
client_world_mixin_path = java_root / 'ai/derco/mixin/ClientWorldMixin.java'
client_world_mixin_source = client_world_mixin_path.read_text(encoding='utf-8', errors='replace')
if 'method = "getCloudsColor"' not in client_world_mixin_source:
    raise SystemExit('Expected legacy ClientWorld cloud-color hook was not found')
client_world_mixin_path.write_text('''package ai.derco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ai.derco.module.ModuleRegistry;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
   @Inject(method = "getLightningTicksLeft", at = @At("HEAD"), cancellable = true)
   private void derco$hideWeatherLightning(CallbackInfoReturnable<Integer> cir) {
      if (ModuleRegistry.RENDER_TWEAKS != null && ModuleRegistry.RENDER_TWEAKS.n()) {
         cir.setReturnValue(0);
      }
   }
}
''', encoding='utf-8')

cloud_renderer_path = java_root / 'ai/derco/mixin/CloudRendererMixin.java'
cloud_renderer_path.write_text('''package ai.derco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ai.derco.module.ModuleRegistry;
import ai.derco.modules.visuals.RenderTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.client.render.CloudRenderer;

@Mixin(CloudRenderer.class)
public class CloudRendererMixin {
   @ModifyVariable(method = "renderClouds", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private int derco$overrideCloudColor(int originalColor) {
      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      MinecraftClient client = MinecraftClient.getInstance();
      if (renderTweaks == null || !renderTweaks.n() || client == null || client.world == null) {
         return originalColor;
      }

      long timeOfDay = client.world.getTimeOfDay();
      float daylight = Math.max(
         0.0F,
         Math.min(1.0F, (float)(Math.cos((float)(timeOfDay % 24000L) / 24000.0F * Math.PI * 2.0) * 2.0 + 0.5))
      ) * 0.9F + 0.1F;
      return ColorHelper.fromFloats(1.0F, daylight, daylight, daylight * 0.85F + 0.15F);
   }
}
''', encoding='utf-8')

# Register the replacement cloud renderer mixin in the client mixin list.
mixin_config_path = SRC / 'resources' / 'dercovisuals.mixins.json'
if not mixin_config_path.exists():
    raise SystemExit('Expected dercovisuals.mixins.json was not found')
mixin_config = json.loads(mixin_config_path.read_text(encoding='utf-8'))
client_mixins = mixin_config.setdefault('client', [])
if 'CloudRendererMixin' not in client_mixins:
    try:
        insert_at = client_mixins.index('ClientWorldMixin') + 1
    except ValueError:
        insert_at = len(client_mixins)
    client_mixins.insert(insert_at, 'CloudRendererMixin')
mixin_config_path.write_text(json.dumps(mixin_config, indent=2) + '\n', encoding='utf-8')

# Disambiguate DrawContext's overloaded drawItem method. In Yarn 1.21.11+build.3,
# the intended private implementation takes (LivingEntity, World, ItemStack, x, y, seed).
# A name-only selector also matches drawItem(ItemStack, x, y), which caused the runtime
# InvalidInjectionException observed with Fabric Loader 0.19.5.
patch_generated_java('ai/derco/mixin/DrawContextMixin.java', (
    ('@Inject(method = "drawItem", at = @At("HEAD"))',
     '@Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V", at = @At("HEAD"))'),
))

# Keep the optional stack-count text handler attached to the five-argument overload.
# A name-only selector resolves to the four-argument overload on 1.21.11 and produces
# an InvalidInjectionException because the handler has an additional String parameter.
patch_generated_java('ai/derco/mixin/DrawContextMixin.java', (
    ('@Inject(require = 0, method = "drawStackOverlay", at = @At("HEAD"), cancellable = true)',
     '@Inject(require = 0, method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)'),
))

# InGameHud has two renderScoreboardSidebar overloads in Yarn 1.21.11:
# the outer (DrawContext, RenderTickCounter) dispatcher and the inner
# (DrawContext, ScoreboardObjective) renderer. These handlers use the objective,
# so target only the inner overload instead of letting the name-only selector
# bind the handler to the dispatcher and fail with an InvalidInjectionException.
patch_generated_java('ai/derco/mixin/InGameHudMixin.java', (
    ('@Inject(require = 0, method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)',
     '@Inject(require = 0, method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"), cancellable = true)'),
    ('@Inject(require = 0, method = "renderScoreboardSidebar", at = @At("TAIL"))',
     '@Inject(require = 0, method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("TAIL"))'),
))

# Minecraft 1.21.11 moved vanilla entity hitbox rendering out of EntityRenderManager.
# F3+B now delegates to EntityHitboxDebugRenderer.render(...); suppress that renderer
# only while DercoVisuals' own Hitbox Customizer is enabled.
# Yarn 1.21.11+build.3 selector:
# render(DDD, DebugDataStore, Frustum, float) -> V.
patch_generated_java('ai/derco/mixin/EntityRendererHitboxMixin.java', (
    ('import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;',
     'import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;'),
    ('import ai.derco.module.ModuleRegistry;',
     'import ai.derco.module.ModuleRegistry;\nimport net.minecraft.client.render.Frustum;\nimport net.minecraft.world.debug.DebugDataStore;'),
    ('@Mixin(targets = "net/minecraft/client/render/entity/EntityRenderDispatcher")',
     '@Mixin(targets = "net/minecraft/client/render/debug/EntityHitboxDebugRenderer")'),
    ('   @Inject(method = "shouldRenderHitboxes", at = @At("HEAD"), cancellable = true)\n'
     '   private void suppressVanillaHitbox(CallbackInfoReturnable<Boolean> cir) {\n'
     '      if (ModuleRegistry.HITBOX_CUSTOMIZER != null && ModuleRegistry.HITBOX_CUSTOMIZER.k()) {\n'
     '         cir.setReturnValue(false);\n'
     '      }\n'
     '   }',
     '   @Inject(\n'
     '      method = "render(DDDLnet/minecraft/world/debug/DebugDataStore;Lnet/minecraft/client/render/Frustum;F)V",\n'
     '      at = @At("HEAD"),\n'
     '      cancellable = true\n'
     '   )\n'
     '   private void suppressVanillaHitbox(double cameraX, double cameraY, double cameraZ, DebugDataStore store,\n'
     '         Frustum frustum, float tickProgress, CallbackInfo ci) {\n'
     '      if (ModuleRegistry.HITBOX_CUSTOMIZER != null && ModuleRegistry.HITBOX_CUSTOMIZER.k()) {\n'
     '         ci.cancel();\n'
     '      }\n'
     '   }'),
))

# Early crash snapshotter. Fabric invokes preLaunch immediately before starting the game,
# before main/client entrypoints. It leaves a marker; CLIENT_STOPPING removes it on
# a clean exit. A remaining marker means the previous process ended unexpectedly.
crash_package = java_root / 'ai' / 'derco' / 'crash'
crash_package.mkdir(parents=True, exist_ok=True)
(crash_package / 'CrashLogBootstrap.java').write_text(r'''package ai.derco.crash;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public final class CrashLogBootstrap implements PreLaunchEntrypoint {
    private static final String SETTINGS_FILE = "dercovisuals-crash-upload.properties";
    private static final String ACTIVE_MARKER = "dercovisuals-session-active";
    private static final String QUEUE_DIR = "dercovisuals-crash-queue";
    private static final String RESULT_FILE = "dercovisuals-last-crash-paste.txt";
    private static final long MAX_LOG_BYTES = 2L * 1024L * 1024L;
    private static final String API_URL = "https://pastebin.com/api/api_post.php";

    @Override
    public void onPreLaunch() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path marker = configDir.resolve(ACTIVE_MARKER);
        try {
            Files.createDirectories(configDir);
            Path queue = configDir.resolve(QUEUE_DIR);
            Files.createDirectories(queue);
            ensureSettingsFile(configDir.resolve(SETTINGS_FILE));

            if (Files.exists(marker)) {
                snapshotLatestLog(queue);
                System.out.println("[DercoVisuals] Previous session ended unexpectedly; checking crash-log upload settings.");
            }

            Settings settings = loadSettings(configDir.resolve(SETTINGS_FILE));
            if (settings.enabled() && !settings.apiDevKey().isBlank()) {
                startUploader(configDir, queue, settings.apiDevKey());
            } else if (settings.enabled()) {
                System.out.println("[DercoVisuals] Crash upload is enabled but apiDevKey is empty. Configure " +
                    configDir.resolve(SETTINGS_FILE) + " to enable Pastebin uploads.");
            }

            Files.writeString(marker, Long.toString(System.currentTimeMillis()), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (Files.exists(marker)) {
                        snapshotLatestLog(queue);
                    }
                } catch (Exception exception) {
                    System.err.println("[DercoVisuals] Could not snapshot crash log during shutdown: " + exception.getMessage());
                }
            }, "DercoVisuals-CrashLogSnapshot"));
        } catch (Exception exception) {
            System.err.println("[DercoVisuals] Crash-log watcher could not initialize: " + exception.getMessage());
        }
    }

    public static void markCleanShutdown() {
        try {
            Path marker = FabricLoader.getInstance().getConfigDir().resolve(ACTIVE_MARKER);
            Files.deleteIfExists(marker);
        } catch (IOException exception) {
            System.err.println("[DercoVisuals] Could not clear session marker: " + exception.getMessage());
        }
    }

    private static void ensureSettingsFile(Path settingsFile) throws IOException {
        if (Files.exists(settingsFile)) return;
        String template = "# DercoVisuals crash-log upload settings\n" +
            "# Upload is attempted only after an unexpected previous-session exit.\n" +
            "# Pastebin links are UNLISTED (anyone with the link can view them) and expire after one week.\n" +
            "# Add your own Pastebin Developer API key below. Never commit this file.\n" +
            "enabled=true\n" +
            "apiDevKey=\n";
        Files.writeString(settingsFile, template, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
    }

    private static Settings loadSettings(Path settingsFile) throws IOException {
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(settingsFile, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true").trim());
        String apiDevKey = properties.getProperty("apiDevKey", "").trim();
        return new Settings(enabled, apiDevKey);
    }

    private static void snapshotLatestLog(Path queue) throws IOException {
        Path latestLog = FabricLoader.getInstance().getGameDir().resolve("logs").resolve("latest.log");
        if (!Files.isRegularFile(latestLog)) return;

        Path snapshot = queue.resolve("crash-" + System.currentTimeMillis() + ".log");
        try (RandomAccessFile input = new RandomAccessFile(latestLog.toFile(), "r")) {
            long length = input.length();
            if (length <= 0L) return;
            long start = Math.max(0L, length - MAX_LOG_BYTES);
            int byteCount = (int) (length - start);
            byte[] bytes = new byte[byteCount];
            input.seek(start);
            input.readFully(bytes);

            String log = new String(bytes, StandardCharsets.UTF_8);
            if (start > 0L) log = "[DercoVisuals] Log truncated to last 2 MiB.\n" + log;
            log = redactSensitiveValues(log);
            Files.writeString(snapshot, log, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        }

        trimQueue(queue, 5);
    }

    private static String redactSensitiveValues(String log) {
        String home = System.getProperty("user.home", "");
        if (home != null && !home.isBlank()) log = log.replace(home, "<USER_HOME>");
        String user = System.getProperty("user.name", "");
        if (user != null && user.length() >= 3) log = log.replace(user, "<USER>");

        log = log.replaceAll("(?i)(authorization\\s*[:=]\\s*bearer\\s+)[^\\s]+", "$1<redacted>");
        log = log.replaceAll(
            "(?i)((?:access|refresh|session|auth|api)[_-]?(?:token|key)|password|secret)(\\s*[=:]\\s*)([^\\s,;\"']+)",
            "$1$2<redacted>"
        );
        return log;
    }

    private static void trimQueue(Path queue, int maximumFiles) throws IOException {
        List<Path> files = listQueuedFiles(queue);
        int excess = files.size() - maximumFiles;
        for (int index = 0; index < excess; index++) {
            Files.deleteIfExists(files.get(index));
        }
    }

    private static List<Path> listQueuedFiles(Path queue) throws IOException {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(queue, "crash-*.log")) {
            for (Path file : stream) files.add(file);
        }
        files.sort(Comparator.comparing(path -> path.getFileName().toString()));
        return files;
    }

    private static void startUploader(Path configDir, Path queue, String apiDevKey) {
        Thread uploader = new Thread(() -> {
            try {
                for (Path pendingLog : listQueuedFiles(queue)) {
                    String pasteUrl = uploadToPastebin(pendingLog, apiDevKey);
                    Files.writeString(configDir.resolve(RESULT_FILE), pasteUrl + System.lineSeparator(),
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    Files.deleteIfExists(pendingLog);
                    System.out.println("[DercoVisuals] Previous crash log uploaded to Pastebin: " + pasteUrl);
                }
            } catch (Exception exception) {
                System.err.println("[DercoVisuals] Pastebin upload failed; the log is kept for retry: " +
                    exception.getMessage());
            }
        }, "DercoVisuals-PastebinCrashUploader");
        uploader.setDaemon(true);
        uploader.start();
    }

    private static String uploadToPastebin(Path logFile, String apiDevKey) throws IOException {
        String pasteCode = Files.readString(logFile, StandardCharsets.UTF_8);
        String title = "DercoVisuals crash log " + Instant.now();
        String form = "api_option=paste" +
            "&api_dev_key=" + encode(apiDevKey) +
            "&api_paste_code=" + encode(pasteCode) +
            "&api_paste_private=1" +
            "&api_paste_name=" + encode(title) +
            "&api_paste_expire_date=1W" +
            "&api_paste_format=text";

        byte[] body = form.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setFixedLengthStreamingMode(body.length);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }

            int status = connection.getResponseCode();
            var responseStream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String response;
            if (responseStream == null) {
                response = "";
            } else {
                try (responseStream) {
                    response = new String(responseStream.readNBytes(4096), StandardCharsets.UTF_8).trim();
                }
            }

            if (status != 200 || !response.startsWith("https://pastebin.com/")) {
                throw new IOException("Pastebin returned HTTP " + status + ": " +
                    response.replace('\n', ' ').replace('\r', ' '));
            }
            return response;
        } finally {
            connection.disconnect();
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private record Settings(boolean enabled, String apiDevKey) { }
}
''', encoding='utf-8')

# Mark sessions clean only when the client reaches its normal stopping callback.
patch_generated_java('ai/derco/DercoVisuals.java', (
    ('import ai.derco.auth.UserInfo;', 'import ai.derco.auth.UserInfo;\nimport ai.derco.crash.CrashLogBootstrap;\nimport ai.derco.media.MediaSessionService;'),
    ('ClientLifecycleEvents.CLIENT_STOPPING.register(client -> LocalConfigManager.get().flushSaveNow("client-stop"));',
     'ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {\n'
     '            LocalConfigManager.get().flushSaveNow("client-stop");\n'
     '            MediaSessionService.h();\n'
     '            CrashLogBootstrap.markCleanShutdown();\n'
     '        });'),
))

# Emit the reusable DercoVisuals theme API after all upstream rebranding and patches.
# These classes are generated from this script, so generated src/main/java is never the
# source of truth for the design system.
theme_sources = {
    'ThemeColors.java': r'''package ai.derco.theme;

import java.util.Objects;

/** Central ARGB design tokens for every DercoVisuals surface. */
public final class ThemeColors {
    public enum Token {
        ACCENT, ACCENT_HOVER, ACCENT_PRESSED, ACCENT_SOFT,
        SECONDARY, SECONDARY_SOFT,
        BACKGROUND, SURFACE, SURFACE_ELEVATED, SURFACE_HOVER,
        BORDER, BORDER_FOCUS,
        TEXT_PRIMARY, TEXT_SECONDARY, TEXT_MUTED, TEXT_DISABLED,
        SUCCESS, WARNING, ERROR
    }

    public static final int DEFAULT_ACCENT = 0xFF347BFA;
    public static final int DEFAULT_SECONDARY = 0xFF39D9F2;

    public static final int BACKGROUND = 0xFF080F20;
    public static final int SURFACE = 0xFF0E1A32;
    public static final int SURFACE_ELEVATED = 0xFF172746;
    public static final int SURFACE_HOVER = 0xFF20385D;
    public static final int BORDER = 0xFF2A4168;

    public static final int TEXT_PRIMARY = 0xFFF4F7FF;
    public static final int TEXT_SECONDARY = 0xFFC1D0EB;
    public static final int TEXT_MUTED = 0xFF95A9CD;
    public static final int TEXT_DISABLED = 0xFF91A5C7;

    public static final int SUCCESS = 0xFF55D6A5;
    public static final int WARNING = 0xFFFFC66D;
    public static final int ERROR = 0xFFFF7188;

    private ThemeColors() {
    }

    public static int resolve(Token token, ThemeSettings settings) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(settings, "settings");
        return switch (token) {
            case ACCENT -> settings.accentColor();
            case ACCENT_HOVER -> blend(settings.accentColor(), 0xFFFFFFFF, 0.14f);
            case ACCENT_PRESSED -> blend(settings.accentColor(), 0xFF000000, 0.16f);
            case ACCENT_SOFT -> withAlpha(settings.accentColor(), 58);
            case SECONDARY -> settings.secondaryColor();
            case SECONDARY_SOFT -> withAlpha(settings.secondaryColor(), 48);
            case BACKGROUND -> BACKGROUND;
            case SURFACE -> SURFACE;
            case SURFACE_ELEVATED -> SURFACE_ELEVATED;
            case SURFACE_HOVER -> SURFACE_HOVER;
            case BORDER -> BORDER;
            case BORDER_FOCUS -> settings.accentColor();
            case TEXT_PRIMARY -> TEXT_PRIMARY;
            case TEXT_SECONDARY -> TEXT_SECONDARY;
            case TEXT_MUTED -> TEXT_MUTED;
            case TEXT_DISABLED -> TEXT_DISABLED;
            case SUCCESS -> SUCCESS;
            case WARNING -> WARNING;
            case ERROR -> ERROR;
        };
    }

    /** Linearly mixes two ARGB colors. Amount is clamped to [0, 1]. */
    public static int blend(int from, int to, float amount) {
        float t = clamp(amount, 0.0f, 1.0f);
        int a = mixChannel((from >>> 24) & 0xFF, (to >>> 24) & 0xFF, t);
        int r = mixChannel((from >>> 16) & 0xFF, (to >>> 16) & 0xFF, t);
        int g = mixChannel((from >>> 8) & 0xFF, (to >>> 8) & 0xFF, t);
        int b = mixChannel(from & 0xFF, to & 0xFF, t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int withAlpha(int argb, int alpha) {
        return (clamp(alpha, 0, 255) << 24) | (argb & 0x00FFFFFF);
    }

    public static int opaque(int argb) {
        return argb | 0xFF000000;
    }

    static float clamp(float value, float min, float max) {
        if (!Float.isFinite(value)) return min;
        return Math.max(min, Math.min(max, value));
    }

    private static int mixChannel(int from, int to, float amount) {
        return Math.round(from + (to - from) * amount);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
''',
    'ThemeSettings.java': r'''package ai.derco.theme;

/**
 * Immutable theme preferences. UI modules own their behavior; this object only
 * describes shared presentation settings and can later be serialized by config code.
 */
public record ThemeSettings(
    int accentColor,
    int secondaryColor,
    boolean reducedMotion,
    float animationScale
) {
    public ThemeSettings {
        accentColor = ThemeColors.opaque(accentColor);
        secondaryColor = ThemeColors.opaque(secondaryColor);
        animationScale = ThemeColors.clamp(animationScale, 0.0f, 4.0f);
    }

    public static ThemeSettings defaults() {
        return new ThemeSettings(
            ThemeColors.DEFAULT_ACCENT,
            ThemeColors.DEFAULT_SECONDARY,
            false,
            1.0f
        );
    }

    public ThemeSettings withAccentColor(int color) {
        return new ThemeSettings(color, secondaryColor, reducedMotion, animationScale);
    }

    public ThemeSettings withSecondaryColor(int color) {
        return new ThemeSettings(accentColor, color, reducedMotion, animationScale);
    }

    public ThemeSettings withReducedMotion(boolean enabled) {
        return new ThemeSettings(accentColor, secondaryColor, enabled, animationScale);
    }

    public ThemeSettings withAnimationScale(float scale) {
        return new ThemeSettings(accentColor, secondaryColor, reducedMotion, scale);
    }
}
''',
    'DercoTheme.java': r'''package ai.derco.theme;

import java.util.Objects;

/** Small, allocation-free facade shared by ClickGUI, HUD and future screens. */
public final class DercoTheme {
    private static volatile ThemeSettings settings = ThemeSettings.defaults();

    private DercoTheme() {
    }

    public static ThemeSettings settings() {
        return settings;
    }

    public static int color(ThemeColors.Token token) {
        return ThemeColors.resolve(token, settings);
    }

    /** Atomically replaces the active immutable theme settings. */
    public static synchronized void apply(ThemeSettings next) {
        settings = Objects.requireNonNull(next, "next");
    }

    public static synchronized void setAccentColor(int argb) {
        settings = settings.withAccentColor(argb);
    }

    public static synchronized void setSecondaryColor(int argb) {
        settings = settings.withSecondaryColor(argb);
    }

    public static synchronized void reset() {
        settings = ThemeSettings.defaults();
    }

    public static ThemeEffects.Shadow shadow(ThemeEffects.Elevation elevation) {
        return ThemeEffects.forElevation(elevation);
    }

    public static float radius(ThemeMetrics.Corner corner) {
        return ThemeMetrics.radius(corner);
    }

    public static int readableText(int background, int preferred) {
        return ThemeContrast.readableTextOn(background, preferred);
    }
}
''',
    'ThemeMetrics.java': r'''package ai.derco.theme;

/** Shared shape and spacing scale. Values are GUI pixels before UI scaling. */
public final class ThemeMetrics {
    public enum Corner {
        NONE, EXTRA_SMALL, SMALL, MEDIUM, LARGE, EXTRA_LARGE, PILL
    }

    public static final int SPACE_1 = 4;
    public static final int SPACE_2 = 8;
    public static final int SPACE_3 = 12;
    public static final int SPACE_4 = 16;
    public static final int SPACE_5 = 20;
    public static final int SPACE_6 = 24;

    public static final int CONTROL_HEIGHT = 28;
    public static final int ROW_HEIGHT = 32;
    public static final int CARD_MIN_WIDTH = 148;

    private ThemeMetrics() {
    }

    public static float radius(Corner corner) {
        return switch (corner) {
            case NONE -> 0.0f;
            case EXTRA_SMALL -> 3.0f;
            case SMALL -> 5.0f;
            case MEDIUM -> 8.0f;
            case LARGE -> 11.0f;
            case EXTRA_LARGE -> 15.0f;
            case PILL -> 999.0f;
        };
    }

    /** Four-pixel spacing grid: spacing(3) returns 12 pixels. */
    public static int spacing(int units) {
        return Math.max(0, units) * SPACE_1;
    }
}
''',
    'ThemeEffects.java': r'''package ai.derco.theme;

/** Renderer-independent shadow and glow descriptors. No blur buffers or threads are created. */
public final class ThemeEffects {
    public enum Elevation {
        NONE, CARD, FLOATING, DIALOG, ACCENT_GLOW
    }

    public record Shadow(
        float blurRadius,
        float offsetX,
        float offsetY,
        int shadowColor,
        int glowColor,
        float glowStrength
    ) {
    }

    private ThemeEffects() {
    }

    public static Shadow forElevation(Elevation elevation) {
        return switch (elevation) {
            case NONE -> new Shadow(0.0f, 0.0f, 0.0f, 0x00000000, 0x00000000, 0.0f);
            case CARD -> new Shadow(8.0f, 0.0f, 3.0f, 0x70000000, 0x00000000, 0.0f);
            case FLOATING -> new Shadow(13.0f, 0.0f, 5.0f, 0x99000000, 0x00000000, 0.0f);
            case DIALOG -> new Shadow(18.0f, 0.0f, 8.0f, 0xB0000000, 0x00000000, 0.0f);
            case ACCENT_GLOW -> new Shadow(
                12.0f, 0.0f, 2.0f, 0x80000000,
                DercoTheme.color(ThemeColors.Token.ACCENT), 0.22f
            );
        };
    }
}
''',
    'ThemeGradients.java': r'''package ai.derco.theme;

/** Lightweight ARGB gradient math. Renderers remain responsible for drawing pixels. */
public final class ThemeGradients {
    public enum Axis {
        HORIZONTAL, VERTICAL, DIAGONAL
    }

    public record Gradient(int startColor, int endColor, Axis axis) {
        public int colorAt(float progress) {
            return ThemeColors.blend(startColor, endColor, progress);
        }
    }

    private ThemeGradients() {
    }

    public static Gradient horizontal(int start, int end) {
        return new Gradient(start, end, Axis.HORIZONTAL);
    }

    public static Gradient vertical(int top, int bottom) {
        return new Gradient(top, bottom, Axis.VERTICAL);
    }

    public static Gradient diagonal(int start, int end) {
        return new Gradient(start, end, Axis.DIAGONAL);
    }

    public static Gradient accent() {
        return horizontal(
            DercoTheme.color(ThemeColors.Token.ACCENT),
            DercoTheme.color(ThemeColors.Token.SECONDARY)
        );
    }

    public static Gradient accentSoft() {
        return horizontal(
            DercoTheme.color(ThemeColors.Token.ACCENT_SOFT),
            DercoTheme.color(ThemeColors.Token.SECONDARY_SOFT)
        );
    }

    public static int sample(int start, int end, float progress) {
        return ThemeColors.blend(start, end, progress);
    }
}
''',
    'ThemeAnimation.java': r'''package ai.derco.theme;

/** Frame-driven animation helpers. Pass the renderer's frame delta; no timer or thread is used. */
public final class ThemeAnimation {
    public enum Easing {
        LINEAR, EASE_OUT_CUBIC, EASE_IN_OUT_CUBIC
    }

    private ThemeAnimation() {
    }

    public static float ease(Easing easing, float progress) {
        float t = ThemeColors.clamp(progress, 0.0f, 1.0f);
        return switch (easing) {
            case LINEAR -> t;
            case EASE_OUT_CUBIC -> 1.0f - (float) Math.pow(1.0f - t, 3.0);
            case EASE_IN_OUT_CUBIC -> t < 0.5f
                ? 4.0f * t * t * t
                : 1.0f - (float) Math.pow(-2.0f * t + 2.0f, 3.0) / 2.0f;
        };
    }

    public static float progress(float elapsedSeconds, float durationSeconds) {
        ThemeSettings settings = DercoTheme.settings();
        if (settings.reducedMotion() || settings.animationScale() == 0.0f) return 1.0f;
        float duration = Math.max(0.001f, durationSeconds * settings.animationScale());
        return ThemeColors.clamp(elapsedSeconds / duration, 0.0f, 1.0f);
    }

    public static float lerp(float from, float to, float progress) {
        float t = ThemeColors.clamp(progress, 0.0f, 1.0f);
        return from + (to - from) * t;
    }

    /** Per-widget animation state. Call update once per frame and reuse the instance. */
    public static final class AnimatedFloat {
        private float value;

        public AnimatedFloat(float initialValue) {
            this.value = initialValue;
        }

        public float value() {
            return value;
        }

        public void snapTo(float nextValue) {
            value = nextValue;
        }

        public float update(float target, float deltaSeconds, float durationSeconds) {
            ThemeSettings settings = DercoTheme.settings();
            if (settings.reducedMotion() || settings.animationScale() == 0.0f) {
                value = target;
                return value;
            }

            float delta = ThemeColors.clamp(deltaSeconds, 0.0f, 0.1f);
            float duration = Math.max(0.001f, durationSeconds * settings.animationScale());
            float blend = 1.0f - (float) Math.exp(-delta * 8.0f / duration);
            value = lerp(value, target, blend);
            if (Math.abs(value - target) < 0.0005f) value = target;
            return value;
        }
    }
}
''',
    'ThemeContrast.java': r'''package ai.derco.theme;

/** WCAG relative-luminance and text-contrast helpers for ARGB colors. */
public final class ThemeContrast {
    public static final double WCAG_AA_NORMAL_TEXT = 4.5;
    public static final double WCAG_AA_LARGE_TEXT = 3.0;

    private ThemeContrast() {
    }

    public static double ratio(int foreground, int background) {
        int opaqueForeground = composite(foreground, background);
        double first = luminance(opaqueForeground);
        double second = luminance(background | 0xFF000000);
        return (Math.max(first, second) + 0.05) / (Math.min(first, second) + 0.05);
    }

    public static boolean meetsWcagAA(int foreground, int background) {
        return ratio(foreground, background) >= WCAG_AA_NORMAL_TEXT;
    }

    public static boolean meetsWcagAALargeText(int foreground, int background) {
        return ratio(foreground, background) >= WCAG_AA_LARGE_TEXT;
    }

    /** Keeps the preferred color when accessible, otherwise selects the stronger neutral. */
    public static int readableTextOn(int background, int preferred) {
        if (meetsWcagAA(preferred, background)) return preferred;
        int light = ThemeColors.TEXT_PRIMARY;
        int dark = 0xFF080F20;
        return ratio(light, background) >= ratio(dark, background) ? light : dark;
    }

    /** Verifies the built-in text tokens against every standard dark surface. */
    public static boolean defaultPalettePasses() {
        int[] surfaces = {
            ThemeColors.BACKGROUND,
            ThemeColors.SURFACE,
            ThemeColors.SURFACE_ELEVATED,
            ThemeColors.SURFACE_HOVER
        };
        int[] textColors = {
            ThemeColors.TEXT_PRIMARY,
            ThemeColors.TEXT_SECONDARY,
            ThemeColors.TEXT_MUTED,
            ThemeColors.TEXT_DISABLED
        };
        for (int surface : surfaces) {
            for (int text : textColors) {
                if (!meetsWcagAA(text, surface)) return false;
            }
        }
        return true;
    }

    private static double luminance(int color) {
        double red = linear(((color >>> 16) & 0xFF) / 255.0);
        double green = linear(((color >>> 8) & 0xFF) / 255.0);
        double blue = linear((color & 0xFF) / 255.0);
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    private static double linear(double channel) {
        return channel <= 0.04045
            ? channel / 12.92
            : Math.pow((channel + 0.055) / 1.055, 2.4);
    }

    private static int composite(int foreground, int background) {
        int alpha = (foreground >>> 24) & 0xFF;
        if (alpha == 255) return foreground;
        double a = alpha / 255.0;
        int red = mix((foreground >>> 16) & 0xFF, (background >>> 16) & 0xFF, a);
        int green = mix((foreground >>> 8) & 0xFF, (background >>> 8) & 0xFF, a);
        int blue = mix(foreground & 0xFF, background & 0xFF, a);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    private static int mix(int foreground, int background, double alpha) {
        return (int) Math.round(foreground * alpha + background * (1.0 - alpha));
    }
}
'''
}

theme_root = java_root / 'ai' / 'derco' / 'theme'
theme_root.mkdir(parents=True, exist_ok=True)
for theme_name, theme_source in theme_sources.items():
    (theme_root / theme_name).write_text(theme_source.strip() + '\n', encoding='utf-8')

expected_theme_files = tuple(theme_root / name for name in theme_sources)
if len(expected_theme_files) != 8 or any(not path.is_file() for path in expected_theme_files):
    raise SystemExit('DercoVisuals theme API generation was incomplete')

# DercoVisuals HUD visual system. Keep all feature logic in its original HUD classes;
# this palette only standardizes surfaces, text, borders, and blue/cyan accents.
hud_theme_path = java_root / 'ai' / 'derco' / 'hud' / 'core' / 'HudTheme.java'
hud_theme_path.parent.mkdir(parents=True, exist_ok=True)
hud_theme_path.write_text(r'''package ai.derco.hud.core;

import java.awt.Color;

/** Shared restrained navy / blue HUD palette for all DercoVisuals overlays. */
public final class HudTheme {
    public static final Color BACKGROUND_TOP = new Color(10, 24, 42, 238);
    public static final Color BACKGROUND_BOTTOM = new Color(5, 15, 28, 242);
    public static final Color PANEL = new Color(9, 25, 42, 218);
    public static final Color SURFACE = new Color(14, 33, 54, 242);
    public static final Color SURFACE_RAISED = new Color(19, 43, 68, 242);
    public static final Color SURFACE_DEEP = new Color(6, 17, 30, 242);
    public static final Color SLOT_BG = new Color(5, 17, 31, 188);
    public static final Color BORDER = new Color(48, 104, 148, 112);
    public static final Color SLOT_BORDER = new Color(49, 105, 148, 96);
    public static final Color TEXT = new Color(244, 249, 255, 255);
    public static final Color MUTED = new Color(178, 202, 223, 245);
    public static final Color ACCENT = new Color(53, 185, 255, 255);
    public static final Color ACCENT_DARK = new Color(22, 112, 190, 255);
    public static final Color ACCENT_GLOW = new Color(53, 185, 255, 58);
    public static final Color GUIDE = new Color(53, 185, 255, 190);
    public static final Color GUIDE_CENTER = new Color(22, 112, 190, 190);
    public static final Color GUIDE_EDGE = new Color(105, 219, 255, 190);

    private HudTheme() { }

    public static Color withAlpha(Color color, float alpha) {
        float clamped = Math.max(0.0F, Math.min(1.0F, alpha));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
            Math.max(0, Math.min(255, Math.round(color.getAlpha() * clamped))));
    }
}
''', encoding='utf-8')

patch_generated_java('ai/derco/hud/core/HudElement.java', (
    ('new Color(130, 105, 255, (int)(25.0F * fH))',
     'ai.derco.hud.core.HudTheme.withAlpha(ai.derco.hud.core.HudTheme.ACCENT_GLOW, fH)'),
))

patch_generated_java('ai/derco/hud/core/HudElementManager.java', (
    ('private static final Color m = new Color(100, 180, 255, 180);',
     'private static final Color m = ai.derco.hud.core.HudTheme.GUIDE;'),
    ('private static final Color n = new Color(200, 100, 255, 180);',
     'private static final Color n = ai.derco.hud.core.HudTheme.GUIDE_CENTER;'),
    ('private static final Color o = new Color(255, 150, 80, 180);',
     'private static final Color o = ai.derco.hud.core.HudTheme.GUIDE_EDGE;'),
    ('this.f = new HotkeysHudElement(10.0F, 110.0F);',
     'this.f = new HotkeysHudElement(10.0F, 238.0F);'),
    ('this.g = new CooldownsHudElement(Math.max(10.0F, width - 130.0F), 10.0F);',
     'this.g = new CooldownsHudElement(Math.max(10.0F, width - 142.0F), 10.0F);'),
    ('this.h = new TargetHudElement(Math.max(10.0F, (width - 130.0F) / 2.0F), Math.max(10.0F, height - 61.0F));',
     'this.h = new TargetHudElement(Math.max(10.0F, width - 142.0F), Math.max(10.0F, height - 64.0F));'),
    ('this.saturationHud = new SaturationHudElement(width / 2.0F + 10.0F, Math.max(10.0F, height - 49.0F));',
     'this.saturationHud = new SaturationHudElement(Math.max(10.0F, width / 2.0F - 91.0F), Math.max(10.0F, height - 49.0F));'),
    ('this.inventoryHud = new InventoryHudElement(Math.max(10.0F, (width - 226.0F) / 2.0F), Math.max(10.0F, height - 145.0F));',
     'this.inventoryHud = new InventoryHudElement(10.0F, Math.max(10.0F, height - 92.0F));'),
    ('this.scoreboardHud = new ScoreboardHudElement(Math.max(10.0F, width - 130.0F), Math.max(10.0F, (height - 80.0F) / 2.0F));',
     'this.scoreboardHud = new ScoreboardHudElement(Math.max(10.0F, width - 142.0F), Math.max(10.0F, (height - 80.0F) / 2.0F));'),
))

patch_generated_java('ai/derco/hud/elements/WatermarkHudElement.java', (
    ('private static final Color CARD_TOP = new Color(15, 15, 26, 232);',
     'private static final Color CARD_TOP = ai.derco.hud.core.HudTheme.BACKGROUND_TOP;'),
    ('private static final Color CARD_BOTTOM = new Color(9, 9, 18, 236);',
     'private static final Color CARD_BOTTOM = ai.derco.hud.core.HudTheme.BACKGROUND_BOTTOM;'),
    ('withAlpha(new Color(130, 95, 255), 1.0F)',
     'withAlpha(ai.derco.hud.core.HudTheme.ACCENT, 1.0F)'),
))

patch_generated_java('ai/derco/hud/elements/TargetHudElement.java', (
    ('private static final Color BACKGROUND = new Color(15, 15, 18, 245);',
     'private static final Color BACKGROUND = ai.derco.hud.core.HudTheme.SURFACE;'),
    ('private static final Color HEALTH_TEXT = new Color(182, 182, 191, 245);',
     'private static final Color HEALTH_TEXT = ai.derco.hud.core.HudTheme.MUTED;'),
    ('private static final Color BAR_BACKGROUND = new Color(21, 18, 28, 245);',
     'private static final Color BAR_BACKGROUND = ai.derco.hud.core.HudTheme.SURFACE_DEEP;'),
    ('private static final Color BAR_COLOR = new Color(95, 65, 240, 255);',
     'private static final Color BAR_COLOR = ai.derco.hud.core.HudTheme.ACCENT;'),
    ('private static final Color BAR_GLOW = new Color(95, 65, 240, 60);',
     'private static final Color BAR_GLOW = ai.derco.hud.core.HudTheme.ACCENT_GLOW;'),
    ('''private final AnimationState healthAnimation = new AnimationState();''',
     '''private final AnimationState healthAnimation = new AnimationState();
   private int cachedHealthTenths = Integer.MIN_VALUE;
   private String cachedHealthText = "HP / 20,0";
'''),
    ('''            String health = "HP / " + String.format(Locale.ROOT, "%.1f", Math.max(0.0F, this.targetHealth)).replace('.', ',');
            healthFont.a(health, textX, y + 22.0F * scale, withAlpha(HEALTH_TEXT, alpha), matrices);''',
     '''            healthFont.a(this.cachedHealthText, textX, y + 22.0F * scale, withAlpha(HEALTH_TEXT, alpha), matrices);'''),
    ('''         this.targetHealth = Math.max(0.0F, currentTarget.getHealth());
         this.targetMaxHealth = Math.max(1.0F, currentTarget.getMaxHealth());''',
     '''         this.targetHealth = Math.max(0.0F, currentTarget.getHealth());
         this.updateCachedHealthText();
         this.targetMaxHealth = Math.max(1.0F, currentTarget.getMaxHealth());'''),
    ('''   private void updateTargetIdentity(LivingEntity entity) {''',
     '''   private void updateCachedHealthText() {
      int tenths = Math.round(Math.max(0.0F, this.targetHealth) * 10.0F);
      if (tenths == this.cachedHealthTenths) return;
      this.cachedHealthTenths = tenths;
      this.cachedHealthText = "HP / " + String.format(Locale.ROOT, "%.1f", Math.max(0.0F, this.targetHealth)).replace('.', ',');
   }

   private void updateTargetIdentity(LivingEntity entity) {'''),

    ('import net.minecraft.entity.player.PlayerEntity;',
     'import net.minecraft.entity.player.PlayerEntity;\nimport net.minecraft.text.Text;'),
    ('''private int cachedHealthTenths = Integer.MIN_VALUE;
   private String cachedHealthText = "HP / 20,0";''',
     '''private int cachedHealthTenths = Integer.MIN_VALUE;
   private String cachedHealthText = "HP / 20,0";
   private LivingEntity cachedNameEntity;
   private Text cachedCustomName;'''),
    ('''         if (currentTarget != this.target) {
            this.target = currentTarget;
            this.updateTargetIdentity(currentTarget);
            float healthProgress = clamp01(currentTarget.getHealth() / Math.max(1.0F, currentTarget.getMaxHealth()));''',
     '''         if (currentTarget != this.target) {
            this.target = currentTarget;
            float healthProgress = clamp01(currentTarget.getHealth() / Math.max(1.0F, currentTarget.getMaxHealth()));'''),
    ('''      if (!shouldBeVisible) {
         this.target = null;
      }''',
     '''      if (!shouldBeVisible) {
         this.target = null;
         this.cachedNameEntity = null;
         this.cachedCustomName = null;
      }'''),
    ('''   private void updateTargetIdentity(LivingEntity entity) {
      this.targetName = entity.getName().getString();
      this.skinTexture = entity instanceof PlayerEntity player ? this.skinFor(player) : DefaultSkinHelper.getTexture();
   }''',
     '''   private void updateTargetIdentity(LivingEntity entity) {
      Text customName = entity.getCustomName();
      if (entity != this.cachedNameEntity || customName != this.cachedCustomName) {
         this.cachedNameEntity = entity;
         this.cachedCustomName = customName;
         this.targetName = entity.getName().getString();
      }
      this.skinTexture = entity instanceof PlayerEntity player ? this.skinFor(player) : DefaultSkinHelper.getTexture();
   }'''),


))


patch_generated_java('ai/derco/modules/hud/TargetHud.java', (
    ('''import ai.derco.settings.SliderSetting;''',
     '''import ai.derco.settings.BooleanSetting;
import ai.derco.settings.SliderSetting;'''),
    ('''   private final SliderSetting targetLifeTime = new SliderSetting("Время жизни таргета", 2.0F, 0.5F, 5.0F, 0.5F);''',
     '''   private final SliderSetting targetLifeTime = new SliderSetting("Время жизни таргета", 2.0F, 0.5F, 5.0F, 0.5F);
   private final BooleanSetting showAbsorption = new BooleanSetting("Показывать поглощение", true);'''),
    ('''   public SliderSetting targetLifeTime() {
      return this.targetLifeTime;
   }''',
     '''   public SliderSetting targetLifeTime() {
      return this.targetLifeTime;
   }

   public BooleanSetting showAbsorption() {
      return this.showAbsorption;
   }'''),
))

patch_generated_java('ai/derco/hud/elements/TargetHudElement.java', (
    ('''private static final Color BAR_GLOW = ai.derco.hud.core.HudTheme.ACCENT_GLOW;''',
     '''private static final Color BAR_GLOW = ai.derco.hud.core.HudTheme.ACCENT_GLOW;
   private static final Color ABSORPTION_COLOR = new Color(255, 190, 55, 245);'''),
    ('''private final AnimationState healthAnimation = new AnimationState();''',
     '''private final AnimationState healthAnimation = new AnimationState();
   private final AnimationState absorptionAnimation = new AnimationState();'''),
    ('''private int cachedHealthTenths = Integer.MIN_VALUE;
   private String cachedHealthText = "HP / 20,0";''',
     '''private int cachedHealthTenths = Integer.MIN_VALUE;
   private int cachedAbsorptionTenths = Integer.MIN_VALUE;
   private boolean cachedShowAbsorption = true;
   private String cachedHealthText = "HP / 20,0";'''),
    ('''private float targetHealth;
   private float targetMaxHealth = 20.0F;''',
     '''private float targetHealth;
   private float targetAbsorption;
   private float targetMaxHealth = 20.0F;'''),
    ('''this.healthAnimation.d(1.0);''',
     '''this.healthAnimation.d(1.0);
      this.absorptionAnimation.d(0.0);'''),
    ('''   private void updateTarget() {
      LivingEntity currentTarget = this.validTarget(HudServiceRegistry.TARGETS.currentTarget());
      if (currentTarget == null && this.isEditing()) {
         currentTarget = this.a.player;
      }

      if (currentTarget != null) {
         if (currentTarget != this.target) {
            this.target = currentTarget;
            float healthProgress = clamp01(currentTarget.getHealth() / Math.max(1.0F, currentTarget.getMaxHealth()));
            this.healthAnimation.d(healthProgress);
         }

         this.targetHealth = Math.max(0.0F, currentTarget.getHealth());
         this.updateCachedHealthText();
         this.targetMaxHealth = Math.max(1.0F, currentTarget.getMaxHealth());
         this.updateTargetIdentity(currentTarget);
      }

      boolean shouldBeVisible = currentTarget != null;
      if (!shouldBeVisible) {
         this.target = null;
         this.cachedNameEntity = null;
         this.cachedCustomName = null;
      }

      if (shouldBeVisible != this.visible) {
         this.visible = shouldBeVisible;
         this.visibilityAnimation.a(shouldBeVisible ? 1.0 : 0.0, 0.2, Easing.h);
      }

      float healthProgress = clamp01(this.targetHealth / this.targetMaxHealth);
      if (Math.abs(this.healthAnimation.i() - healthProgress) > 0.01) {
         this.healthAnimation.a(healthProgress, 0.15, Easing.h);
      }

      this.visibilityAnimation.a();
      this.healthAnimation.a();
   }''',
     '''   private void updateTarget() {
      LivingEntity currentTarget = this.validTarget(HudServiceRegistry.TARGETS.currentTarget());
      if (currentTarget == null && this.isEditing()) {
         currentTarget = this.a.player;
      }

      boolean showAbsorption = ModuleRegistry.TARGET_HUD != null
         && ModuleRegistry.TARGET_HUD.showAbsorption().get();
      if (currentTarget != null) {
         float currentHealth = Math.max(0.0F, currentTarget.getHealth());
         float currentAbsorption = showAbsorption
            ? Math.max(0.0F, currentTarget.getAbsorptionAmount())
            : 0.0F;
         float effectiveMaxHealth = Math.max(1.0F, currentTarget.getMaxHealth()) + currentAbsorption;

         if (currentTarget != this.target) {
            this.target = currentTarget;
            this.healthAnimation.d(clamp01(currentHealth / effectiveMaxHealth));
            this.absorptionAnimation.d(clamp01(currentAbsorption / effectiveMaxHealth));
         }

         this.targetHealth = currentHealth;
         this.targetAbsorption = currentAbsorption;
         this.targetMaxHealth = effectiveMaxHealth;
         this.updateCachedHealthText(showAbsorption);
         this.updateTargetIdentity(currentTarget);
      }

      boolean shouldBeVisible = currentTarget != null;
      if (!shouldBeVisible) {
         this.target = null;
         this.cachedNameEntity = null;
         this.cachedCustomName = null;
      }

      if (shouldBeVisible != this.visible) {
         this.visible = shouldBeVisible;
         this.visibilityAnimation.a(shouldBeVisible ? 1.0 : 0.0, 0.2, Easing.h);
      }

      float healthProgress = clamp01(this.targetHealth / this.targetMaxHealth);
      float absorptionProgress = clamp01(this.targetAbsorption / this.targetMaxHealth);
      if (Math.abs(this.healthAnimation.i() - healthProgress) > 0.01) {
         this.healthAnimation.a(healthProgress, 0.15, Easing.h);
      }
      if (Math.abs(this.absorptionAnimation.i() - absorptionProgress) > 0.01) {
         this.absorptionAnimation.a(absorptionProgress, 0.15, Easing.h);
      }

      this.visibilityAnimation.a();
      this.healthAnimation.a();
      this.absorptionAnimation.a();
   }'''),
    ('''   private void updateCachedHealthText() {
      int tenths = Math.round(Math.max(0.0F, this.targetHealth) * 10.0F);
      if (tenths == this.cachedHealthTenths) return;
      this.cachedHealthTenths = tenths;
      this.cachedHealthText = "HP / " + String.format(Locale.ROOT, "%.1f", Math.max(0.0F, this.targetHealth)).replace('.', ',');
   }''',
     '''   private void updateCachedHealthText(boolean showAbsorption) {
      int healthTenths = Math.round(Math.max(0.0F, this.targetHealth) * 10.0F);
      int absorptionTenths = Math.round(Math.max(0.0F, this.targetAbsorption) * 10.0F);
      if (healthTenths == this.cachedHealthTenths
         && absorptionTenths == this.cachedAbsorptionTenths
         && showAbsorption == this.cachedShowAbsorption) return;
      this.cachedHealthTenths = healthTenths;
      this.cachedAbsorptionTenths = absorptionTenths;
      this.cachedShowAbsorption = showAbsorption;
      this.cachedHealthText = "HP / "
         + String.format(Locale.ROOT, "%.1f", healthTenths / 10.0F).replace('.', ',');
      if (showAbsorption && absorptionTenths > 0) {
         this.cachedHealthText += " +"
            + String.format(Locale.ROOT, "%.1f", absorptionTenths / 10.0F).replace('.', ',');
      }
   }'''),
    ('''            float healthProgress = clamp01((float)this.healthAnimation.j());
            if (healthProgress > 0.0F) {
               float filledWidth = Math.min(barWidth, Math.max(barHeight, barWidth * healthProgress));
               float glow = 1.0F * scale;
               renderer.a(
                  barX - glow,
                  barY - glow,
                  filledWidth + glow * 2.0F,
                  barHeight + glow * 2.0F,
                  (barHeight + glow * 2.0F) / 2.0F,
                  withAlpha(BAR_GLOW, alpha),
                  matrices
               );
               renderer.a(barX, barY, filledWidth, barHeight, barRadius, withAlpha(BAR_COLOR, alpha), matrices);
            }''',
     '''            float healthProgress = clamp01((float)this.healthAnimation.j());
            float healthWidth = 0.0F;
            if (healthProgress > 0.0F) {
               healthWidth = Math.min(barWidth, Math.max(barHeight, barWidth * healthProgress));
               float glow = 1.0F * scale;
               renderer.a(
                  barX - glow,
                  barY - glow,
                  healthWidth + glow * 2.0F,
                  barHeight + glow * 2.0F,
                  (barHeight + glow * 2.0F) / 2.0F,
                  withAlpha(BAR_GLOW, alpha),
                  matrices
               );
               renderer.a(barX, barY, healthWidth, barHeight, barRadius, withAlpha(BAR_COLOR, alpha), matrices);
            }
            float absorptionProgress = clamp01((float)this.absorptionAnimation.j());
            float absorptionWidth = Math.min(Math.max(0.0F, barWidth - healthWidth), barWidth * absorptionProgress);
            if (absorptionWidth > 0.0F) {
               renderer.a(barX + healthWidth, barY, absorptionWidth, barHeight,
                  Math.min(barRadius, absorptionWidth / 2.0F), withAlpha(ABSORPTION_COLOR, alpha), matrices);
            }'''),
))

patch_generated_java('ai/derco/hud/elements/InventoryHudElement.java', (
    ('private static final Color OVERALL_BG_COLOR = new Color(25, 27, 32, 110);',
     'private static final Color OVERALL_BG_COLOR = ai.derco.hud.core.HudTheme.PANEL;'),
    ('private static final Color OVERALL_BORDER_COLOR = new Color(40, 45, 55, 90);',
     'private static final Color OVERALL_BORDER_COLOR = ai.derco.hud.core.HudTheme.BORDER;'),
    ('private static final Color SLOT_BG_COLOR = new Color(5, 5, 5, 91);',
     'private static final Color SLOT_BG_COLOR = ai.derco.hud.core.HudTheme.SLOT_BG;'),
    ('private static final Color SLOT_BORDER_COLOR = new Color(40, 45, 55, 80);',
     'private static final Color SLOT_BORDER_COLOR = ai.derco.hud.core.HudTheme.SLOT_BORDER;'),
    ('renderer2D.a(this.b, this.c, this.d, this.e, 4.0F * fScale, cBg, MatrixStackVar);',
     'renderer2D.a(this.b, this.c, this.d, this.e, 4.0F * fScale, cBg, MatrixStackVar);\n'
     '               renderer2D.a(this.b, this.c, this.d, 1.0F * fScale, 0.0F,\n'
     '                  ai.derco.hud.core.HudTheme.withAlpha(ai.derco.hud.core.HudTheme.ACCENT, 0.42F * fAlpha), MatrixStackVar);'),
))

patch_generated_java('ai/derco/hud/elements/TotemsHudElement.java', (
    ('float fScale = this.scale();\n'
     '               Matrix3x2fStack matrices = currentDrawContext.getMatrices();',
     'float fScale = this.scale();\n'
     '               renderer2D.a(this.b, this.c, this.d, this.e, 3.0F * fScale,\n'
     '                  ai.derco.hud.core.HudTheme.PANEL, MatrixStackVar);\n'
     '               renderer2D.a(this.b, this.c, this.d, 1.0F * fScale, 0.0F,\n'
     '                  ai.derco.hud.core.HudTheme.ACCENT_GLOW, MatrixStackVar);\n'
     '               Matrix3x2fStack matrices = currentDrawContext.getMatrices();'),
))

patch_generated_java('ai/derco/hud/elements/BossbarHudElement.java', (
    ('Color bgColor = new Color(20, 20, 20, 100);',
     'Color bgColor = ai.derco.hud.core.HudTheme.PANEL;'),
))

patch_generated_java('ai/derco/hud/elements/ScoreboardHudElement.java', (
    ('Color bgColor = new Color(20, 20, 20, 100);',
     'Color bgColor = ai.derco.hud.core.HudTheme.PANEL;'),
))

# Reuse the active-effect snapshot for preview state and rendering. Previously, the
# preview check and renderer each called y(), rebuilding and sorting the same list.
patch_generated_java('ai/derco/hud/elements/PotionsHudElement.java', (
    ('   private boolean ModelPart = false;\n   public static boolean n;',
     '   private boolean ModelPart = false;\n   private boolean previewPlaceholder = false;\n   public static boolean n;'),
    ('   private boolean w() {\n      return (this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen) && this.y().isEmpty();\n   }',
     '   private boolean isEditingScreen() {\n      return this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen;\n   }'),
    ('      this.u();\n      boolean zW = this.w();\n      List<PotionsHudElement.EffectEntry> listY = this.y();',
     '      this.u();\n      List<PotionsHudElement.EffectEntry> listY = this.y();\n      boolean zW = this.isEditingScreen() && listY.isEmpty();\n      this.previewPlaceholder = zW;'),
    ('            boolean zW = this.w();',
     '            boolean zW = this.previewPlaceholder;'),
    ('private static final Color o = new Color(13, 13, 17);',
     'private static final Color o = ai.derco.hud.core.HudTheme.BACKGROUND_BOTTOM;'),
    ('private static final Color p = new Color(17, 17, 23);',
     'private static final Color p = ai.derco.hud.core.HudTheme.BACKGROUND_TOP;'),
    ('private static final Color r = new Color(223, 223, 243);',
     'private static final Color r = ai.derco.hud.core.HudTheme.MUTED;'),
    ('private static final Color s = new Color(20, 20, 28);',
     'private static final Color s = ai.derco.hud.core.HudTheme.BORDER;'),
    ('private static final Color t = new Color(16, 16, 21);',
     'private static final Color t = ai.derco.hud.core.HudTheme.SURFACE_DEEP;'),
    ('private static final Color u = new Color(17, 17, 23);',
     'private static final Color u = ai.derco.hud.core.HudTheme.SURFACE;'),
    ('private static final Color v = new Color(23, 23, 30);',
     'private static final Color v = ai.derco.hud.core.HudTheme.SURFACE_RAISED;'),
    ('private static final Color w = new Color(18, 18, 24);',
     'private static final Color w = ai.derco.hud.core.HudTheme.SURFACE;'),
    ('private static final Color y = new Color(115, 83, 255);',
     'private static final Color y = ai.derco.hud.core.HudTheme.ACCENT;'),
    ('private static final Color z = new Color(70, 46, 174);',
     'private static final Color z = ai.derco.hud.core.HudTheme.ACCENT_DARK;'),
))

patch_generated_java('ai/derco/hud/elements/CooldownsHudElement.java', (
    ('private static final Color m = new Color(13, 13, 17);',
     'private static final Color m = ai.derco.hud.core.HudTheme.BACKGROUND_BOTTOM;'),
    ('private static final Color n = new Color(17, 17, 23);',
     'private static final Color n = ai.derco.hud.core.HudTheme.BACKGROUND_TOP;'),
    ('private static final Color p = new Color(223, 223, 243);',
     'private static final Color p = ai.derco.hud.core.HudTheme.MUTED;'),
    ('private static final Color q = new Color(20, 20, 28);',
     'private static final Color q = ai.derco.hud.core.HudTheme.BORDER;'),
    ('private static final Color s = new Color(17, 17, 23);',
     'private static final Color s = ai.derco.hud.core.HudTheme.SURFACE;'),
    ('private static final Color t = new Color(23, 23, 30);',
     'private static final Color t = ai.derco.hud.core.HudTheme.SURFACE_RAISED;'),
    ('private static final Color u = new Color(18, 18, 24);',
     'private static final Color u = ai.derco.hud.core.HudTheme.SURFACE_DEEP;'),
    ('private static final Color v = new Color(23, 23, 30);',
     'private static final Color v = ai.derco.hud.core.HudTheme.SURFACE_RAISED;'),
    ('private static final Color w = new Color(18, 18, 24);',
     'private static final Color w = ai.derco.hud.core.HudTheme.SURFACE_DEEP;'),
))

patch_generated_java('ai/derco/hud/elements/HotkeysHudElement.java', (
    ('static final Color o = new Color(13, 13, 17);',
     'static final Color o = ai.derco.hud.core.HudTheme.BACKGROUND_BOTTOM;'),
    ('static final Color p = new Color(17, 17, 23);',
     'static final Color p = ai.derco.hud.core.HudTheme.BACKGROUND_TOP;'),
    ('private static final Color r = new Color(223, 223, 243);',
     'private static final Color r = ai.derco.hud.core.HudTheme.MUTED;'),
    ('private static final Color s = new Color(20, 20, 28);',
     'private static final Color s = ai.derco.hud.core.HudTheme.BORDER;'),
    ('private static final Color t = new Color(16, 16, 21);',
     'private static final Color t = ai.derco.hud.core.HudTheme.SURFACE_DEEP;'),
    ('private static final Color u = new Color(17, 17, 23);',
     'private static final Color u = ai.derco.hud.core.HudTheme.SURFACE;'),
    ('private static final Color v = new Color(23, 23, 30);',
     'private static final Color v = ai.derco.hud.core.HudTheme.SURFACE_RAISED;'),
    ('private static final Color w = new Color(18, 18, 24);',
     'private static final Color w = ai.derco.hud.core.HudTheme.SURFACE_DEEP;'),
    ('private static final Color x = new Color(115, 83, 255);',
     'private static final Color x = ai.derco.hud.core.HudTheme.ACCENT;'),
    ('private static final Color y = new Color(70, 46, 174);',
     'private static final Color y = ai.derco.hud.core.HudTheme.ACCENT_DARK;'),
    ('if (clientModule.k() && clientModule.j() != 0) {\n'
     '            arrayList.add(new HotkeysHudElement.HotkeySnapshot(clientModule, clientModule.g(), "  " + this.b(clientModule.j()) + "  "));',
     'if (clientModule.k()) {\n'
     '            String keyName = clientModule.j() == 0 ? "—" : this.b(clientModule.j());\n'
     '            arrayList.add(new HotkeysHudElement.HotkeySnapshot(clientModule, clientModule.g(), "  " + keyName + "  "));'),
))

patch_generated_java('ai/derco/modules/hud/HotkeysHud.java', (
    ('@ModuleInfo(a = "Hotkeys", b = "Displays active key bindings.", c = ModuleCategory.HUD)',
     '@ModuleInfo(a = "Hotkeys", b = "Displays active modules and their key bindings.", c = ModuleCategory.HUD)'),
))

patch_generated_java('ai/derco/modules/hud/ClientColor.java', (
    ('private static final Color DEFAULT_COLOR = new Color(138, 43, 226);',
     'private static final Color DEFAULT_COLOR = new Color(53, 145, 235);'),
    ('private static final Color DEFAULT_GRADIENT_START = Color.WHITE;',
     'private static final Color DEFAULT_GRADIENT_START = new Color(95, 215, 255);'),
    ('private static final Color DEFAULT_GRADIENT_END = new Color(0, 125, 245);',
     'private static final Color DEFAULT_GRADIENT_END = new Color(19, 105, 205);'),
    ('private static final Color[] ASTOLFO_COLORS = new Color[]{new Color(255, 121, 198), new Color(170, 70, 255), new Color(80, 181, 255)};',
     'private static final Color[] ASTOLFO_COLORS = new Color[]{new Color(53, 185, 255), new Color(22, 112, 190), new Color(105, 219, 255)};'),
))

patch_generated_java('ai/derco/hud/notifications/HudNotificationCenter.java', (
    ('private static final Color c = new Color(19, 19, 25);',
     'private static final Color c = ai.derco.hud.core.HudTheme.SURFACE;'),
    ('private static final Color d = new Color(19, 19, 26);',
     'private static final Color d = ai.derco.hud.core.HudTheme.SURFACE_RAISED;'),
    ('private static final Color e = new Color(13, 13, 17);',
     'private static final Color e = ai.derco.hud.core.HudTheme.BACKGROUND_BOTTOM;'),
    ('private static final Color f = new Color(17, 17, 23);',
     'private static final Color f = ai.derco.hud.core.HudTheme.BACKGROUND_TOP;'),
))

patch_generated_java('ai/derco/hud/notifications/MediaPlaybackNotification.java', (
    ('private static final Color n = new Color(223, 223, 243);',
     'private static final Color n = ai.derco.hud.core.HudTheme.MUTED;'),
    ('private static final Color p = new Color(76, 46, 212, 100);',
     'private static final Color p = ai.derco.hud.core.HudTheme.ACCENT_GLOW;'),
    ('private static final Color q = new Color(115, 83, 255);',
     'private static final Color q = ai.derco.hud.core.HudTheme.ACCENT;'),
    ('private static final Color r = new Color(70, 46, 174);',
     'private static final Color r = ai.derco.hud.core.HudTheme.ACCENT_DARK;'),
))

patch_generated_java('ai/derco/hud/notifications/ActionNotification.java', (
    ('CLOUD("\\ue904", new Color(100, 72, 227), new Color(100, 72, 227))',
     'CLOUD("\\ue904", ai.derco.hud.core.HudTheme.ACCENT, ai.derco.hud.core.HudTheme.ACCENT_DARK)'),
))

patch_generated_java('ai/derco/hud/notifications/TextNotification.java', (
    ('private static final Color o = new Color(223, 223, 243);',
     'private static final Color o = ai.derco.hud.core.HudTheme.MUTED;'),
    ('new Color(223, 223, 255, i)',
     'ai.derco.hud.core.HudTheme.withAlpha(ai.derco.hud.core.HudTheme.ACCENT, i / 255.0F)'),
))

patch_generated_java('ai/derco/hud/notifications/WatermarkOverlay.java', (
    ('private static final Color CARD_TOP = new Color(8, 8, 11, 235);',
     'private static final Color CARD_TOP = ai.derco.hud.core.HudTheme.BACKGROUND_TOP;'),
    ('private static final Color CARD_BOTTOM = new Color(5, 5, 8, 240);',
     'private static final Color CARD_BOTTOM = ai.derco.hud.core.HudTheme.BACKGROUND_BOTTOM;'),
    ('private static final Color MUTED = new Color(145, 145, 160, 220);',
     'private static final Color MUTED = ai.derco.hud.core.HudTheme.MUTED;'),
    ('''   private void drawSegments(Matrix3x2fStack MatrixStackVar, float f, float f2, float f3) {
      FontRenderer fontRenderer = FontManager.MEDIUM[14];
      float fB = f2 - fontRenderer.b("DercoVisuals") / 4.0F;
      if (ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.fpsAndPing().a()) {
         this.drawPart(
            fontRenderer,
            this.getFps() + " FPS",
            this.drawPart(
               fontRenderer,
               " / ",
               this.drawPart(
                  fontRenderer,
                  this.getPingMs() + " ms",
                  this.drawPart(
                     fontRenderer, " / ", this.drawPart(fontRenderer, "DercoVisuals", f, fB, TEXT, f3, MatrixStackVar), fB, MUTED, f3, MatrixStackVar
                  ),
                  fB,
                  TEXT,
                  f3,
                  MatrixStackVar
               ),
               fB,
               MUTED,
               f3,
               MatrixStackVar
            ),
            fB,
            TEXT,
            f3,
            MatrixStackVar
         );
      } else {
         this.drawPart(fontRenderer, "DercoVisuals", f, fB, TEXT, f3, MatrixStackVar);
      }
   }''',
     '''   private void drawSegments(Matrix3x2fStack MatrixStackVar, float f, float f2, float f3) {
      FontRenderer fontRenderer = FontManager.MEDIUM[14];
      float fB = f2 - fontRenderer.b("DercoVisuals") / 4.0F;
      float cursor = this.drawPart(fontRenderer, "DercoVisuals", f, fB, TEXT, f3, MatrixStackVar);
      if (this.coordinatesEnabled()) {
         cursor = this.drawPart(fontRenderer, " / ", cursor, fB, MUTED, f3, MatrixStackVar);
         cursor = this.drawPart(fontRenderer, this.getCoordinates(), cursor, fB,
            ai.derco.hud.core.HudTheme.ACCENT, f3, MatrixStackVar);
      }

      if (ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.fpsAndPing().a()) {
         cursor = this.drawPart(fontRenderer, " / ", cursor, fB, MUTED, f3, MatrixStackVar);
         cursor = this.drawPart(fontRenderer, this.getPingMs() + " ms", cursor, fB, TEXT, f3, MatrixStackVar);
         cursor = this.drawPart(fontRenderer, " / ", cursor, fB, MUTED, f3, MatrixStackVar);
         this.drawPart(fontRenderer, this.getFps() + " FPS", cursor, fB,
            ai.derco.hud.core.HudTheme.ACCENT, f3, MatrixStackVar);
      }
   }

   private boolean coordinatesEnabled() {
      return ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.coordinates().a();
   }

   private String getCoordinates() {
      if (c.player == null) return "X 0 Y 0 Z 0";
      return "X " + (int)Math.floor(c.player.getX())
         + " Y " + (int)Math.floor(c.player.getY())
         + " Z " + (int)Math.floor(c.player.getZ());
   }'''),
    ('''   private float measureTextWidth() {
      FontRenderer fontRenderer = FontManager.MEDIUM[14];
      return ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.fpsAndPing().a()
         ? 23.0F
            + fontRenderer.a("DercoVisuals")
            + fontRenderer.a(" / ")
            + fontRenderer.a(this.getPingMs() + " ms")
            + fontRenderer.a(" / ")
            + fontRenderer.a(this.getFps() + " FPS")
         : 23.0F + fontRenderer.a("DercoVisuals");
   }''',
     '''   private float measureTextWidth() {
      FontRenderer fontRenderer = FontManager.MEDIUM[14];
      float width = 23.0F + fontRenderer.a("DercoVisuals");
      if (this.coordinatesEnabled()) {
         width += fontRenderer.a(" / ") + fontRenderer.a(this.getCoordinates());
      }

      if (ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.fpsAndPing().a()) {
         width += fontRenderer.a(" / ")
            + fontRenderer.a(this.getPingMs() + " ms")
            + fontRenderer.a(" / ")
            + fontRenderer.a(this.getFps() + " FPS");
      }
      return width;
   }'''),
))

patch_generated_java('ai/derco/modules/hud/Watermark.java', (
    ('private final BooleanSetting fpsAndPing = new BooleanSetting("FPS & Ping", true);',
     'private final BooleanSetting fpsAndPing = new BooleanSetting("FPS & Ping", true);\n'
     '   private final BooleanSetting coordinates = new BooleanSetting("Координаты", true);'),
    ('   public BooleanSetting events() {',
     '   public BooleanSetting coordinates() {\n'
     '      return this.coordinates;\n'
     '   }\n\n'
     '   public BooleanSetting events() {'),
))

patch_generated_java('ai/derco/mixin/InGameHudMixin.java', (
    ('            int i = ClientPlayerEntityVar.getMainArm().getOpposite() == Arm.RIGHT && !ClientPlayerEntityVar.getOffHandStack().isEmpty() ? 24 : 0;',
     '            int i = ClientPlayerEntityVar.getMainArm().getOpposite() == Arm.RIGHT && !ClientPlayerEntityVar.getOffHandStack().isEmpty() ? 24 : 0;\n'
     '            int armorHudX = iGetScaledWindowWidth + 92 + 10 + i;\n'
     '            int armorHudY = DrawContextVar.getScaledWindowHeight() - 16 - 3;\n'
     '            DrawContextVar.fill(armorHudX - 2, armorHudY - 2, armorHudX + 80, armorHudY + 18, 0xD909192B);\n'
     '            DrawContextVar.fill(armorHudX - 2, armorHudY - 2, armorHudX + 80, armorHudY - 1, 0xFF35B9FF);'),
))


# DercoVisuals ClickGUI redesign — generator-owned source transformation.
# Module registry, setting models, and input dispatch are intentionally retained.
# ---------------------------------------------------------------------------
palette_migrations = {
    (139, 92, 246): (35, 145, 255),
    (115, 83, 255): (30, 140, 255),
    (126, 90, 254): (42, 156, 255),
    (106, 70, 215): (15, 108, 218),
    (70, 46, 174): (12, 82, 180),
    (140, 110, 255): (70, 172, 255),
    (135, 95, 255): (48, 156, 255),
    (95, 65, 200): (20, 112, 218),
    (110, 70, 255): (30, 140, 255),
    (80, 60, 150): (18, 90, 165),
    (52, 40, 95): (17, 58, 102),
    (38, 30, 72): (12, 35, 65),
    (55, 50, 85): (16, 55, 100),
    (60, 55, 95): (20, 64, 110),
    (50, 50, 75): (16, 48, 82),
    (36, 30, 56): (16, 48, 72),
    (38, 34, 52): (24, 70, 118),
    (18, 16, 23): (8, 18, 32),
    (25, 25, 33): (16, 36, 60),
    (18, 18, 24): (9, 20, 35),
    (24, 23, 32): (17, 39, 64),
    (20, 19, 28): (12, 28, 48),
    (13, 12, 17): (6, 14, 27),
    (10, 10, 14): (4, 10, 20),
    (26, 25, 34): (16, 39, 65),
    (15, 14, 19): (8, 17, 31),
    (22, 21, 28): (12, 27, 46),
    (28, 28, 40): (14, 31, 52),
    (26, 26, 38): (11, 24, 42),
    (34, 30, 48): (13, 30, 54),
}
palette_files = [java_root / 'ai/derco/theme/Theme.java']
palette_files.extend(sorted((java_root / 'ai/derco/gui').rglob('*.java')))
for palette_path in palette_files:
    if not palette_path.is_file():
        continue
    palette_source = palette_path.read_text(encoding='utf-8', errors='replace')
    for old_rgb, new_rgb in palette_migrations.items():
        old_pattern = re.compile(
            rf'new Color\(\s*{old_rgb[0]}\s*,\s*{old_rgb[1]}\s*,\s*{old_rgb[2]}'
        )
        new_color = f'new Color({new_rgb[0]}, {new_rgb[1]}, {new_rgb[2]}'
        palette_source = old_pattern.sub(new_color, palette_source)
    palette_path.write_text(palette_source, encoding='utf-8')

# Responsive frame: keep the existing ScreenScale and all input callbacks intact.
patch_generated_java('ai/derco/gui/core/DercoVisualsClickGuiScreen.java', (
    ('private static final float c = 411.0F;', 'private static final float c = 520.0F;'),
    ('private static final float d = 243.5F;', 'private static final float d = 270.0F;'),
    ('return GuiInput.a(f2, f3, 411.0F, 243.5F, i, i2);',
     'return GuiInput.a(f2, f3, d(), e(), i, i2);'),
    ('MinecraftContext.getWidth() / 4.0F - 205.5F',
     'MinecraftContext.getWidth() / 4.0F - d() / 2.0F'),
    ('MinecraftContext.getHeight() / 4.0F - 121.75F',
     'MinecraftContext.getHeight() / 4.0F - e() / 2.0F'),
    ('''   public static float d() {
      return 411.0F;
   }''',
     '''   public static float d() {
      return Math.max(320.0F, Math.min(520.0F, MinecraftContext.getWidth() / 2.0F - 32.0F));
   }'''),
    ('''   public static float e() {
      return 243.5F;
   }''',
     '''   public static float e() {
      return Math.max(180.0F, Math.min(270.0F, MinecraftContext.getHeight() / 2.0F - 28.0F));
   }'''),
))

# Dock positions follow the responsive frame rather than old hard-coded dimensions.
dock_path = java_root / 'ai/derco/gui/core/DockPanelController.java'
dock_source = dock_path.read_text(encoding='utf-8', errors='replace')
dock_source = dock_source.replace('411.0F', 'DercoVisualsClickGuiScreen.d()')
dock_source = dock_source.replace('243.5F', 'DercoVisualsClickGuiScreen.e()')
dock_path.write_text(dock_source, encoding='utf-8')

# Dock the selected module's settings card inside the right edge of the main frame.
patch_generated_java('ai/derco/gui/modules/ModuleCardList.java', (
    ('''   public boolean hasOpenPanelAt(int i, int i2) {
      for (ModuleCard moduleCard : this.c) {
         if (!moduleCard.f() && (moduleCard.d(i, i2) || moduleCard.f(i, i2))) {
            return true;
         }
      }

      return false;
   }''',
     '''   public boolean hasOpenPanelAt(int i, int i2) {
      for (ModuleCard moduleCard : this.c) {
         if (!moduleCard.f() && (moduleCard.d(i, i2) || moduleCard.f(i, i2))) {
            return true;
         }
      }

      return false;
   }

   public boolean hasSettingsPanelOpen() {
      for (ModuleCard moduleCard : this.c) {
         if (!moduleCard.f() && !moduleCard.g()) {
            return true;
         }
      }

      return false;
   }'''),
    ('MinecraftContext.getWidth() / 4.0F - 205.5F',
     'MinecraftContext.getWidth() / 4.0F - DercoVisualsClickGuiScreen.d() / 2.0F'),
    ('float panelX = z ? rightGui + 20.0F : leftGui - 20.0F - 150.0F;',
     'float panelX = rightGui - 160.0F;'),
))

# Reserve grid width for the inspector, keeping existing setting widgets and input.
patch_generated_java('ai/derco/gui/modules/ModuleGrid.java', (
    ('''   public ModuleGrid(GuiEntrySource guiEntrySource) {
      this.a = guiEntrySource;
   }

   public List<ModuleCard> a() {''',
     '''   public ModuleGrid(GuiEntrySource guiEntrySource) {
      this.a = guiEntrySource;
   }

   private float contentWidth() {
      return Math.max(150.0F, DercoVisualsClickGuiScreen.d() - (this.i.hasSettingsPanelOpen() ? 166.0F : 0.0F));
   }

   public boolean hasSettingsPanelOpen() {
      return this.i.hasSettingsPanelOpen();
   }

   public List<ModuleCard> a() {'''),
    ('''   private float k() {
      int iF = this.a.f();
      float fD = DercoVisualsClickGuiScreen.d() - 38.0F;
      return iF == 1 ? fD - 2.0F : (fD - 6.5F) / 2.0F - 1.0F;
   }''',
     '''   private float k() {
      int columns = Math.max(1, this.a.f());
      float available = this.contentWidth() - 38.0F;
      return Math.max(72.0F, (available - 6.5F * (columns - 1)) / columns - 1.0F);
   }'''),
    ('GuiLayerRegistry.a().a(GuiLayerRegistry.Layer.CONTENT, f2, f4, DercoVisualsClickGuiScreen.d(), fE);',
     'GuiLayerRegistry.a().a(GuiLayerRegistry.Layer.CONTENT, f2, f4, this.contentWidth(), fE);'),
    ('renderer2D.b().a(f2, f4, DercoVisualsClickGuiScreen.d(), fE, MatrixStackVar);',
     'renderer2D.b().a(f2, f4, this.contentWidth(), fE, MatrixStackVar);'),
    ('this.h.a(MatrixStackVar, renderer2D, f2 + DercoVisualsClickGuiScreen.d() - 2.0F, f4, fE, fCeil, fE, i, i2, false);',
     'this.h.a(MatrixStackVar, renderer2D, f2 + this.contentWidth() - 2.0F, f4, fE, fCeil, fE, i, i2, false);'),
    ('int i6 = i5 % 2;', 'int i6 = i5 % iF;'),
    ('float f11 = f9 + i5 / 2 * 34.0F + 1.0F;', 'float f11 = f9 + i5 / iF * 34.0F + 1.0F;'),
))

patch_generated_java('ai/derco/gui/modules/ModulesTab.java', (
    ('''   @Override
   public int f() {
      return 2;
   }''',
     '''   @Override
   public int f() {
      float availableWidth = DercoVisualsClickGuiScreen.d() - (this.m.hasSettingsPanelOpen() ? 166.0F : 0.0F);
      return Math.max(1, Math.min(3, (int)((availableWidth - 38.0F) / 145.0F)));
   }'''),
    ('private static final float c = 117.5F;', 'private static final float c = 140.0F;'),
    ('new SearchBox(117.5F, 15.0F)', 'new SearchBox(140.0F, 15.0F)'),
    ('fD - 4.0F - 117.5F', 'fD - 4.0F - 140.0F'),
))

# Rebuild module cards with a subtle navy gradient and animated electric-blue edge.
patch_generated_java('ai/derco/gui/modules/ModuleRowRenderer.java', (
    ('''      Color cardBorder = new Color(16, 36, 60);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 9.5F, cardBorder, cardBorder, cardBorder, cardBorder, MatrixStackVar);
      Color cardBg = new Color(9, 20, 35);
      renderer2D.a(f2, f3, f4, f5, 9.5F, cardBg, cardBg, cardBg, cardBg, MatrixStackVar);''',
     '''      Color cardBorder = new Color(20, 76, 126, 118 + (int)(f6 * 120.0F));
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 9.5F, cardBorder, cardBorder, cardBorder, cardBorder, MatrixStackVar);
      Color cardTop = new Color(12, 31, 53);
      Color cardBottom = new Color(8, 19, 34);
      renderer2D.a(f2, f3, f4, f5, 9.5F, cardTop, cardTop, cardBottom, cardBottom, MatrixStackVar);
      if (f6 > 0.01F) {
         Color hoverGlow = new Color(35, 145, 255, (int)(58.0F * f6));
         renderer2D.a(f2 + 8.0F, f3 + 1.0F, Math.max(0.0F, f4 - 16.0F), 0.8F, hoverGlow, MatrixStackVar);
      }'''),
))

# Category buttons gain clear rounded selected/hover treatments; hit testing is unchanged.
patch_generated_java('ai/derco/gui/core/TabSelector.java', (
    ('''            fontRenderer.a(
               strArrC[i4],''',
     '''            Color pillBorder = i5 != 0
               ? new Color(42, 151, 255, 190)
               : new Color(26, 67, 104, (int)(105.0F + 50.0F * this.i.get(i4).j()));
            Color pillBackground = i5 != 0
               ? new Color(18, 91, 158, 125)
               : new Color(8, 22, 38, 185);
            renderer2D.a(
               fFloatValue - 6.0F,
               f4 - 5.0F,
               this.l.get(i4) + 12.0F,
               17.0F,
               6.0F,
               pillBorder, pillBorder, pillBackground, pillBackground,
               MatrixStackVar
            );
            fontRenderer.a(
               strArrC[i4],'''),
))


# Keep the Totems HUD count accurate and expose the full inventory total beside the icon row.
patch_generated_java('ai/derco/hud/elements/TotemsHudElement.java', (
    ('this.d = 81.0F;', 'this.d = 110.0F;'),
    ('this.d = 81.0F * fScale;', 'this.d = 110.0F * fScale;'),
    ('for (int i = 0; i < this.a.player.getInventory().size(); i++) {',
     'int inventorySize = Math.min(this.a.player.getInventory().size(), net.minecraft.entity.player.PlayerInventory.OFF_HAND_SLOT);\n\n               for (int i = 0; i < inventorySize; i++) {'),
    ('if (this.a.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {\n                  totems += this.a.player.getOffHandStack().getCount();\n               }',
     'ItemStack offHandStack = this.a.player.getOffHandStack();\n               if (offHandStack.getItem() == Items.TOTEM_OF_UNDYING) {\n                  totems += offHandStack.getCount();\n               }'),
    ('               matrices.popMatrix();\n               matrices.popMatrix();',
     '               matrices.popMatrix();\n               currentDrawContext.drawText(this.a.textRenderer, "x" + totems, 83, 0, ai.derco.hud.core.HudTheme.TEXT.getRGB(), true);\n               matrices.popMatrix();'),
))

# Keep one display-only totem stack per HUD element instead of allocating it on every render.
patch_generated_java('ai/derco/hud/elements/TotemsHudElement.java', (
    ('public class TotemsHudElement extends HudElement {\n   private boolean settingsBound;',
     'public class TotemsHudElement extends HudElement {\n   private final ItemStack displayTotemStack = new ItemStack(Items.TOTEM_OF_UNDYING);\n   private boolean settingsBound;'),
    ('ItemStack totemStack = new ItemStack(Items.TOTEM_OF_UNDYING);',
     'ItemStack totemStack = this.displayTotemStack;'),
))

# Register the owned icon family and wire representative art into the live GUI.
# Existing Pulse-derived GUI logic is preserved; only icons and tiny text offsets change.
def replace_generated_once(path, old, new, label):
    current = path.read_text(encoding='utf-8')
    count = current.count(old)
    if count != 1:
        raise SystemExit('Could not patch ' + label + ': expected one source anchor, found ' + str(count))
    path.write_text(current.replace(old, new), encoding='utf-8')

registry_path = java_root / 'ai' / 'derco' / 'render' / 'icons' / 'IconTextureRegistry.java'
registry_extra = (
    '          register("category_visuals", "textures/categories/visuals.png");\n'
    '          register("category_hud", "textures/categories/hud.png");\n'
    '          register("category_utilities", "textures/categories/utilities.png");\n'
    '          register("category_combat", "textures/categories/combat.png");\n'
    '          register("category_movement", "textures/categories/movement.png");\n'
    '          register("category_render", "textures/categories/render.png");\n'
    '          register("category_player", "textures/categories/player.png");\n'
    '          register("category_world", "textures/categories/world.png");\n'
    '          register("category_misc", "textures/categories/misc.png");\n'
    '          register("hud_armor", "textures/hud/armor.png");\n'
    '          register("hud_bossbar", "textures/hud/bossbar.png");\n'
    '          register("hud_cooldowns", "textures/hud/cooldowns.png");\n'
    '          register("hud_effects", "textures/hud/effects.png");\n'
    '          register("hud_hotkeys", "textures/hud/hotkeys.png");\n'
    '          register("hud_inventory", "textures/hud/inventory.png");\n'
    '          register("hud_potions", "textures/hud/potions.png");\n'
    '          register("hud_saturation", "textures/hud/saturation.png");\n'
    '          register("hud_scoreboard", "textures/hud/scoreboard.png");\n'
    '          register("hud_target", "textures/hud/target.png");\n'
    '          register("hud_totems", "textures/hud/totems.png");\n'
    '          register("hud_watermark", "textures/hud/watermark.png");\n'
    '          register("hud_tnt_timer", "textures/hud/tnt_timer.png");\n'
    '          register("module_crosshair", "textures/modules/crosshair.png");\n'
    '          register("module_fullbright", "textures/modules/fullbright.png");\n'
    '          register("module_trails", "textures/modules/trails.png");\n'
    '          register("module_particles", "textures/modules/particles.png");\n'
    '          register("module_hitbox", "textures/modules/hitbox.png");\n'
    '          register("module_jump_circles", "textures/modules/jump_circles.png");\n'
    '          register("module_zoom", "textures/modules/zoom.png");\n'
    '          register("module_sprint", "textures/modules/sprint.png");\n'
    '          register("module_elytra", "textures/modules/elytra.png");\n'
    '          register("module_world", "textures/modules/world.png");\n'
    '          register("module_sound", "textures/modules/sound.png");\n'
    '          register("module_inventory_sort", "textures/modules/inventory_sort.png");\n'
    '          register("panel_selected_module", "textures/panels/selected_module.png");\n'
    '          register("panel_detail_accent", "textures/panels/detail_accent.png");\n'
)
replace_generated_once(
    registry_path,
    '         register("example_group", "textures/clickgui/example_group.png");\n         registerPotionAliases();',
    '         register("example_group", "textures/clickgui/example_group.png");\n' + registry_extra + '         registerPotionAliases();',
    'icon texture registry'
)

tab_selector_path = java_root / 'ai' / 'derco' / 'gui' / 'core' / 'TabSelector.java'
replace_generated_once(
    tab_selector_path,
    'import ai.derco.render.font.FontRenderer;\n',
    'import ai.derco.render.font.FontRenderer;\nimport ai.derco.render.icons.IconTextureRegistry;\n',
    'category icon import'
)
replace_generated_once(
    tab_selector_path,
    'this.l.put(i2, fontRenderer.a(strArrC[i2]));',
    'this.l.put(i2, fontRenderer.a(strArrC[i2]) + (iconForLabel(strArrC[i2]) == null ? 0.0F : 12.0F));',
    'category rail sizing'
)
replace_generated_once(
    tab_selector_path,
    '            fontRenderer.a(\n               strArrC[i4],\n               fFloatValue,\n               f4,',
    '            String iconKey = iconForLabel(strArrC[i4]);\n'
    '            if (iconKey != null) {\n'
    '               renderer2D.a(IconTextureRegistry.get(iconKey), fFloatValue, f4 - 1.0F, 9.0F, 9.0F, Color.WHITE, MatrixStackVar);\n'
    '            }\n'
    '            fontRenderer.a(\n'
    '               strArrC[i4],\n'
    '               fFloatValue + (iconKey == null ? 0.0F : 12.0F),\n'
    '               f4,',
    'category rail icon rendering'
)
replace_generated_once(
    tab_selector_path,
    '   public static String b(String str, String str2, int i, int i2, int i3, int i4) {',
    '   private static String iconForLabel(String label) {\n'
    '      if (label == null) return null;\n'
    '      return switch (label) {\n'
    '         case "Visuals" -> "category_visuals";\n'
    '         case "HUD" -> "category_hud";\n'
    '         case "Utilities" -> "category_utilities";\n'
    '         default -> null;\n'
    '      };\n'
    '   }\n\n'
    '   public static String b(String str, String str2, int i, int i2, int i3, int i4) {',
    'category icon mapping'
)

module_rows_path = java_root / 'ai' / 'derco' / 'gui' / 'modules' / 'ModuleRowRenderer.java'
replace_generated_once(
    module_rows_path,
    'import ai.derco.render.Renderer2D;\n',
    'import ai.derco.render.Renderer2D;\nimport ai.derco.render.icons.IconTextureRegistry;\n',
    'module icon import'
)
replace_generated_once(
    module_rows_path,
    '      if (fontRenderer != null) {\n'
    '         fontRenderer.a(MatrixStackVar, guiEntry.a(), f2 + 12.0F, f3 + f5 / 2.0F - 4.0F, textColor);\n'
    '      } else {\n'
    '         TextRenderer tr = MinecraftClient.getInstance().textRenderer;\n'
    '         DrawContext dc = Renderer2DImpl.currentDrawContext;\n'
    '         if (dc != null) {\n'
    '            dc.drawText(tr, guiEntry.a(), (int)(f2 + 12.0F), (int)(f3 + f5 / 2.0F - 4.0F), textColor.getRGB(), false);\n'
    '         }\n'
    '      }',
    '      String moduleIconKey = moduleIconForName(guiEntry.a());\n'
    '      float textOffset = moduleIconKey == null ? 12.0F : 26.0F;\n'
    '      if (moduleIconKey != null) {\n'
    '         renderer2D.a(IconTextureRegistry.get(moduleIconKey), f2 + 2.0F, f3 + f5 / 2.0F - 5.0F, 10.0F, 10.0F, Color.WHITE, MatrixStackVar);\n'
    '      }\n'
    '      if (fontRenderer != null) {\n'
    '         fontRenderer.a(MatrixStackVar, guiEntry.a(), f2 + textOffset, f3 + f5 / 2.0F - 4.0F, textColor);\n'
    '      } else {\n'
    '         TextRenderer tr = MinecraftClient.getInstance().textRenderer;\n'
    '         DrawContext dc = Renderer2DImpl.currentDrawContext;\n'
    '         if (dc != null) {\n'
    '            dc.drawText(tr, guiEntry.a(), (int)(f2 + textOffset), (int)(f3 + f5 / 2.0F - 4.0F), textColor.getRGB(), false);\n'
    '         }\n'
    '      }',
    'module card icon rendering'
)
replace_generated_once(
    module_rows_path,
    '   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {',
    '   private static String moduleIconForName(String name) {\n'
    '      if (name == null) return null;\n'
    '      return switch (name.toLowerCase(java.util.Locale.ROOT)) {\n'
    '         case "crosshair" -> "module_crosshair";\n'
    '         case "full bright" -> "module_fullbright";\n'
    '         case "trails" -> "module_trails";\n'
    '         case "particles", "world particles" -> "module_particles";\n'
    '         case "hitbox customizer" -> "module_hitbox";\n'
    '         case "jump circles" -> "module_jump_circles";\n'
    '         case "zoom" -> "module_zoom";\n'
    '         case "sprint" -> "module_sprint";\n'
    '         case "elytra swap" -> "module_elytra";\n'
    '         case "world customizer" -> "module_world";\n'
    '         case "sound controller" -> "module_sound";\n'
    '         case "chest sorter" -> "module_inventory_sort";\n'
    '         default -> null;\n'
    '      };\n'
    '   }\n\n'
    '   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {',
    'module icon mapping'
)

settings_panel_path = java_root / 'ai' / 'derco' / 'gui' / 'modules' / 'ModuleSettingsPanel.java'
replace_generated_once(
    settings_panel_path,
    '            renderer2D.a(f8, f9, f6, f7, 12.0F * fMax, bg, bg, bg, bg, MatrixStackVar);',
    '            renderer2D.a(f8, f9, f6, f7, 12.0F * fMax, bg, bg, bg, bg, MatrixStackVar);\n'
    '            float brandArtHeight = Math.min(f7, 28.0F * fMax);\n'
    '            if (brandArtHeight > 1.0F) {\n'
    '               renderer2D.a(IconTextureRegistry.get("panel_selected_module"), f8, f9, f6, brandArtHeight, new Color(255, 255, 255, Math.min(120, i3)), MatrixStackVar);\n'
    '            }',
    'selected-module panel art'
)

# Validate every hard-coded registry path after all brand assets have been copied.
registry_source = registry_path.read_text(encoding='utf-8')
for registered_resource in re.findall(r'register\(\s*"[^"]+"\s*,\s*"([^"]+)"\s*\)', registry_source):
    resource_path = SRC / 'resources' / 'assets' / 'dercovisuals' / registered_resource
    if not resource_path.is_file():
        raise SystemExit('IconTextureRegistry points to a missing asset: ' + registered_resource)

# Wire the legacy GUI and HUD palette aliases into the shared DercoTheme tokens.
# Existing public Color fields remain source/binary compatible; only their source values
# now derive from the shared blue theme rather than parallel hard-coded palettes.
patch_generated_java('ai/derco/theme/Theme.java', (
    ('''   public static Color b = new Color(245, 247, 252, 255);''',
     '''   public static Color b = token(ThemeColors.Token.TEXT_PRIMARY);'''),
    ('''   public static Color e = new Color(40, 42, 54, 220);''',
     '''   public static Color e = tokenAlpha(ThemeColors.Token.BACKGROUND, 220);'''),
    ('''   public static Color f = new Color(52, 54, 68, 200);''',
     '''   public static Color f = tokenAlpha(ThemeColors.Token.SURFACE, 200);'''),
    ('''   public static Color m = new Color(35, 35, 48, 215);''',
     '''   public static Color m = tokenAlpha(ThemeColors.Token.SURFACE, 215);'''),
    ('''   public static Color n = new Color(45, 45, 60, 210);''',
     '''   public static Color n = tokenAlpha(ThemeColors.Token.SURFACE_ELEVATED, 210);'''),
    ('''   public static Color q = new Color(50, 53, 68, 225);''',
     '''   public static Color q = tokenAlpha(ThemeColors.Token.SURFACE_ELEVATED, 225);'''),
    ('''   public static Color r = new Color(62, 65, 82, 225);''',
     '''   public static Color r = tokenAlpha(ThemeColors.Token.SURFACE_HOVER, 225);'''),
    ('''   public static Color C = new Color(110, 160, 240, 255);''',
     '''   public static Color C = token(ThemeColors.Token.ACCENT);'''),
    ('''   public static Color D = new Color(85, 135, 215, 255);''',
     '''   public static Color D = token(ThemeColors.Token.ACCENT_PRESSED);'''),
    ('''   public static Color I = new Color(110, 160, 240, 255);''',
     '''   public static Color I = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''   public static Color K = new Color(130, 180, 255, 255);''',
     '''   public static Color K = token(ThemeColors.Token.ACCENT_HOVER);'''),
    ('''   public static Color a = new Color(223, 223, 243);''',
     '''   public static Color a = token(ThemeColors.Token.TEXT_SECONDARY);'''),
    ('''   public static Color c = new Color(30, 140, 255);''',
     '''   public static Color c = token(ThemeColors.Token.ACCENT);'''),
    ('''   public static Color d = new Color(80, 80, 110);''',
     '''   public static Color d = token(ThemeColors.Token.TEXT_MUTED);'''),
    ('''   public static Color g = new Color(22, 22, 30);''',
     '''   public static Color g = token(ThemeColors.Token.BACKGROUND);'''),
    ('''   public static Color h = new Color(9, 20, 35);''',
     '''   public static Color h = token(ThemeColors.Token.SURFACE);'''),
    ('''   public static Color i = new Color(25, 25, 45);''',
     '''   public static Color i = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''   public static Color j = new Color(19, 19, 35);''',
     '''   public static Color j = token(ThemeColors.Token.SURFACE);'''),
    ('''   public static Color k = new Color(20, 20, 32);''',
     '''   public static Color k = token(ThemeColors.Token.BACKGROUND);'''),
    ('''   public static Color l = new Color(16, 16, 26);''',
     '''   public static Color l = token(ThemeColors.Token.BACKGROUND);'''),
    ('''   public static Color o = new Color(21, 21, 28);''',
     '''   public static Color o = token(ThemeColors.Token.SURFACE);'''),
    ('''   public static Color p = new Color(30, 30, 40);''',
     '''   public static Color p = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''   public static Color s = new Color(37, 37, 67);''',
     '''   public static Color s = token(ThemeColors.Token.SURFACE_HOVER);'''),
    ('''   public static Color t = new Color(21, 21, 38);''',
     '''   public static Color t = token(ThemeColors.Token.SURFACE);'''),
    ('''   public static Color u = new Color(30, 30, 50);''',
     '''   public static Color u = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''   public static Color v = new Color(18, 18, 32);''',
     '''   public static Color v = token(ThemeColors.Token.BACKGROUND);'''),
    ('''   public static Color x = new Color(9, 20, 35);''',
     '''   public static Color x = token(ThemeColors.Token.SURFACE);'''),
    ('''   public static Color y = new Color(30, 140, 255);''',
     '''   public static Color y = token(ThemeColors.Token.ACCENT);'''),
    ('''   public static Color z = new Color(12, 82, 180);''',
     '''   public static Color z = token(ThemeColors.Token.ACCENT_PRESSED);'''),
    ('''   public static Color A = new Color(30, 140, 255, 100);''',
     '''   public static Color A = tokenAlpha(ThemeColors.Token.ACCENT, 100);'''),
    ('''   public static Color B = new Color(70, 172, 255);''',
     '''   public static Color B = token(ThemeColors.Token.SECONDARY);'''),
    ('''   public static Color E = new Color(48, 156, 255);''',
     '''   public static Color E = token(ThemeColors.Token.ACCENT_HOVER);'''),
    ('''   public static Color Q = new Color(70, 172, 255);''',
     '''   public static Color Q = token(ThemeColors.Token.SECONDARY);'''),
    ('''   public static Color R = new Color(20, 112, 218);''',
     '''   public static Color R = token(ThemeColors.Token.ACCENT_PRESSED);'''),
    ('''   private Theme() {''',
     '''   private static Color token(ThemeColors.Token token) {
      return new Color(DercoTheme.color(token), true);
   }

   private static Color tokenAlpha(ThemeColors.Token token, int alpha) {
      return new Color(ThemeColors.withAlpha(DercoTheme.color(token), alpha), true);
   }

   private Theme() {'''),
    ('''      b = new Color(200, 205, 220);''',
     '''      b = token(ThemeColors.Token.TEXT_PRIMARY);'''),
    ('''      e = new Color(8, 17, 31, 255);''',
     '''      e = token(ThemeColors.Token.BACKGROUND);'''),
    ('''      f = new Color(6, 14, 27, 255);''',
     '''      f = token(ThemeColors.Token.BACKGROUND);'''),
    ('''      m = new Color(31, 31, 40);''',
     '''      m = token(ThemeColors.Token.SURFACE);'''),
    ('''      n = new Color(16, 36, 60);''',
     '''      n = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''      q = new Color(30, 30, 42);''',
     '''      q = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''      r = new Color(38, 38, 52);''',
     '''      r = token(ThemeColors.Token.SURFACE_HOVER);'''),
    ('''      C = new Color(42, 156, 255);''',
     '''      C = token(ThemeColors.Token.ACCENT);'''),
    ('''      D = new Color(15, 108, 218);''',
     '''      D = token(ThemeColors.Token.ACCENT_PRESSED);'''),
    ('''      I = new Color(16, 36, 60);''',
     '''      I = token(ThemeColors.Token.SURFACE_ELEVATED);'''),
    ('''      K = new Color(20, 20, 26);''',
     '''      K = token(ThemeColors.Token.SURFACE);'''),
))

patch_generated_java('ai/derco/hud/core/HudTheme.java', (
    ('''import java.awt.Color;''',
     '''import java.awt.Color;
import ai.derco.theme.DercoTheme;
import ai.derco.theme.ThemeColors;'''),
    ('''    public static final Color BACKGROUND_TOP = new Color(10, 24, 42, 238);''',
     '''    public static final Color BACKGROUND_TOP = withAlpha(token(ThemeColors.Token.SURFACE), 238.0F / 255.0F);'''),
    ('''    public static final Color BACKGROUND_BOTTOM = new Color(5, 15, 28, 242);''',
     '''    public static final Color BACKGROUND_BOTTOM = withAlpha(token(ThemeColors.Token.BACKGROUND), 242.0F / 255.0F);'''),
    ('''    public static final Color PANEL = new Color(9, 25, 42, 218);''',
     '''    public static final Color PANEL = withAlpha(token(ThemeColors.Token.SURFACE), 218.0F / 255.0F);'''),
    ('''    public static final Color SURFACE = new Color(14, 33, 54, 242);''',
     '''    public static final Color SURFACE = withAlpha(token(ThemeColors.Token.SURFACE_ELEVATED), 242.0F / 255.0F);'''),
    ('''    public static final Color SURFACE_RAISED = new Color(19, 43, 68, 242);''',
     '''    public static final Color SURFACE_RAISED = withAlpha(token(ThemeColors.Token.SURFACE_HOVER), 242.0F / 255.0F);'''),
    ('''    public static final Color SURFACE_DEEP = new Color(6, 17, 30, 242);''',
     '''    public static final Color SURFACE_DEEP = withAlpha(token(ThemeColors.Token.BACKGROUND), 242.0F / 255.0F);'''),
    ('''    public static final Color SLOT_BG = new Color(5, 17, 31, 188);''',
     '''    public static final Color SLOT_BG = withAlpha(token(ThemeColors.Token.BACKGROUND), 188.0F / 255.0F);'''),
    ('''    public static final Color BORDER = new Color(48, 104, 148, 112);''',
     '''    public static final Color BORDER = withAlpha(token(ThemeColors.Token.BORDER), 112.0F / 255.0F);'''),
    ('''    public static final Color SLOT_BORDER = new Color(49, 105, 148, 96);''',
     '''    public static final Color SLOT_BORDER = withAlpha(token(ThemeColors.Token.BORDER), 96.0F / 255.0F);'''),
    ('''    public static final Color TEXT = new Color(244, 249, 255, 255);''',
     '''    public static final Color TEXT = token(ThemeColors.Token.TEXT_PRIMARY);'''),
    ('''    public static final Color MUTED = new Color(178, 202, 223, 245);''',
     '''    public static final Color MUTED = token(ThemeColors.Token.TEXT_SECONDARY);'''),
    ('''    public static final Color ACCENT = new Color(53, 185, 255, 255);''',
     '''    public static final Color ACCENT = token(ThemeColors.Token.ACCENT);'''),
    ('''    public static final Color ACCENT_DARK = new Color(22, 112, 190, 255);''',
     '''    public static final Color ACCENT_DARK = token(ThemeColors.Token.ACCENT_PRESSED);'''),
    ('''    public static final Color ACCENT_GLOW = new Color(53, 185, 255, 58);''',
     '''    public static final Color ACCENT_GLOW = token(ThemeColors.Token.ACCENT_SOFT);'''),
    ('''    public static final Color GUIDE = new Color(53, 185, 255, 190);''',
     '''    public static final Color GUIDE = withAlpha(token(ThemeColors.Token.SECONDARY), 190.0F / 255.0F);'''),
    ('''    public static final Color GUIDE_CENTER = new Color(22, 112, 190, 190);''',
     '''    public static final Color GUIDE_CENTER = withAlpha(token(ThemeColors.Token.ACCENT), 190.0F / 255.0F);'''),
    ('''    public static final Color GUIDE_EDGE = new Color(105, 219, 255, 190);''',
     '''    public static final Color GUIDE_EDGE = withAlpha(token(ThemeColors.Token.SECONDARY), 190.0F / 255.0F);'''),
    ('''    private HudTheme() { }''',
     '''    private HudTheme() { }

    private static Color token(ThemeColors.Token token) {
        return new Color(DercoTheme.color(token), true);
    }'''),
))

# Validate generated JSON resources, Mixin source references, and the version token
# before handing the source tree to Gradle. Compilation does not parse these runtime
# descriptors, so a successful javac run alone cannot catch malformed metadata.
for json_path in sorted((SRC / 'resources').rglob('*.json')):
    try:
        json.loads(json_path.read_text(encoding='utf-8'))
    except (OSError, json.JSONDecodeError) as exc:
        raise SystemExit(f'Invalid generated JSON resource {json_path.relative_to(ROOT)}: {exc}')

generated_metadata = json.loads(meta.read_text(encoding='utf-8'))
if 'ai.derco.crash.CrashLogBootstrap' not in generated_metadata.get('entrypoints', {}).get('preLaunch', []):
    raise SystemExit('CrashLogBootstrap preLaunch entrypoint is missing')
if not (java_root / 'ai' / 'derco' / 'crash' / 'CrashLogBootstrap.java').is_file():
    raise SystemExit('CrashLogBootstrap source was not generated')
if generated_metadata.get('version') != chr(36) + '{version}':
    raise SystemExit('fabric.mod.json version placeholder must be present for Gradle resource expansion')
icon_path = generated_metadata.get('icon')
if not icon_path or not (SRC / 'resources' / icon_path).is_file():
    raise SystemExit(f'fabric.mod.json icon resource is missing: {icon_path}')
font_resource_dir = SRC / 'resources' / 'assets' / 'dercovisuals' / 'font'
for font_name in (
    'sfprodisplayregular.ttf',
    'sfprodisplay-medium-test.ttf',
    'icons.ttf',
    'dercovisualsicon.ttf',
    'dercovisuals_icons.ttf',
):
    if not (font_resource_dir / font_name).is_file():
        raise SystemExit(f'Required FontManager resource is missing: {font_name}')
generated_mixin_config = json.loads(mixin_config_path.read_text(encoding='utf-8'))
mixin_source_root = java_root / 'ai' / 'derco' / 'mixin'
for mixin_name in (*generated_mixin_config.get('mixins', []), *generated_mixin_config.get('client', [])):
    mixin_source = mixin_source_root.joinpath(*mixin_name.split('.')).with_suffix('.java')
    if not mixin_source.is_file():
        raise SystemExit(f'Mixin config entry has no Java source: {mixin_name}')

# Loom 1.14 remaps Mixin selectors in-place with TinyRemapper by default.
# The upstream Pulse refmap is keyed by ru/pulse and contains stale 1.21.1
# descriptors, so neither its file nor its config reference may ship.
stale_refmap_name = generated_mixin_config.pop('refmap', 'Minecraft-refmap.json')
if not isinstance(stale_refmap_name, str) or not stale_refmap_name:
    raise SystemExit('Mixin refmap filename must be a non-empty string')
stale_refmap_path = SRC / 'resources' / stale_refmap_name
if stale_refmap_path.is_file():
    stale_refmap_path.unlink()
mixin_config_path.write_text(
    json.dumps(generated_mixin_config, indent=2) + '\n',
    encoding='utf-8',
)

print(f'Prepared DercoVisuals: {len(list((SRC / "java").rglob("*.java")))} Java files, {len(asset_paths)} owned PNG assets')
