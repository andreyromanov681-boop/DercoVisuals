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
    ('   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);\n\n   public PotionsHud() {',
     '''   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);
   private final SliderSetting maxVisibleEffects = new SliderSetting(
      "Макс. эффектов", "Количество активных эффектов, одновременно отображаемых в HUD.", 6.0F, 1.0F, 12.0F, 1.0F
   );

   public PotionsHud() {'''),
    ('   public SliderSetting n() {\n      return this.scale;\n   }\n}',
     '''   public SliderSetting n() {
      return this.scale;
   }

   public int maxVisibleEffects() {
      return this.maxVisibleEffects.roundedInt();
   }
}'''),
))
patch_generated_java('ai/derco/hud/elements/PotionsHudElement.java', (
    ('      arrayList.sort(Comparator.comparingInt(effectEntry -> effectEntry.d));\n      return arrayList;',
     '''      arrayList.sort(Comparator.comparingInt(effectEntry -> effectEntry.d));
      PotionsHud potionsHud = ModuleRegistry.POTIONS_HUD;
      if (potionsHud != null) {
         int maxVisibleEffects = potionsHud.maxVisibleEffects();
         if (arrayList.size() > maxVisibleEffects) {
            arrayList.subList(maxVisibleEffects, arrayList.size()).clear();
         }
      }
      return arrayList;'''),
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
    'icon': 'assets/dercovisuals/textures/logo.png',
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
    ('import ai.derco.auth.UserInfo;', 'import ai.derco.auth.UserInfo;\nimport ai.derco.crash.CrashLogBootstrap;'),
    ('ClientLifecycleEvents.CLIENT_STOPPING.register(client -> LocalConfigManager.get().flushSaveNow("client-stop"));',
     'ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {\n'
     '            LocalConfigManager.get().flushSaveNow("client-stop");\n'
     '            CrashLogBootstrap.markCleanShutdown();\n'
     '        });'),
))

# ---------------------------------------------------------------------------
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

refmap_name = generated_mixin_config.get('refmap')
if refmap_name and not (SRC / 'resources' / refmap_name).is_file():
    raise SystemExit(f'Mixin refmap is missing: {refmap_name}')

print(f'Prepared DercoVisuals: {len(list((SRC / "java").rglob("*.java")))} Java files')
