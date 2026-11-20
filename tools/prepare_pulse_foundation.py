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
    'description': 'A free, modular, client-side visual suite for Minecraft 1.21.11.',
    'authors': ['enizer6569'], 'license': 'LicenseRef-DercoVisuals-Community',
    'environment': 'client',
    'entrypoints': {'main': ['ai.derco.DercoVisuals']},
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
mixin_config_path.write_text(json.dumps(mixin_config, indent=2) + '\\n', encoding='utf-8')

print(f'Prepared DercoVisuals: {len(list((SRC / "java").rglob("*.java")))} Java files')
