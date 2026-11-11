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
    source = source.replace('ru.pulse', 'net.dercovisuals')
    source = source.replace('pulse.', 'net.dercovisuals.')
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
    'net/dercovisuals/auth/DeviceAuthClient.java',
    'net/dercovisuals/discord/DercoVisualsDiscordRPC.java',
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

# Restore collection element types lost by the upstream decompiler.
patch_generated_java('net/dercovisuals/discord/CosmeticHelper.java', (
    ('Map var7 = CosmeticRepository.a().b();',
     'Map<Integer, CosmeticModel> var7 = CosmeticRepository.a().b();'),
))
patch_generated_java('net/dercovisuals/hud/elements/HotkeysHudElement.java', (
    ('private List<HotkeysHudElement.HotkeySnapshot> x() {\n      ArrayList arrayList = new ArrayList();',
     'private List<HotkeysHudElement.HotkeySnapshot> x() {\n      ArrayList<HotkeysHudElement.HotkeySnapshot> arrayList = new ArrayList<>();'),
))
patch_generated_java('net/dercovisuals/hud/elements/PotionsHudElement.java', (
    ('private List<PotionsHudElement.EffectEntry> y() {\n      ArrayList arrayList = new ArrayList();',
     'private List<PotionsHudElement.EffectEntry> y() {\n      ArrayList<PotionsHudElement.EffectEntry> arrayList = new ArrayList<>();'),
))
patch_generated_java('net/dercovisuals/gui/settings/ModeSettingWidget.java', (
    ('ArrayList arrayList = new ArrayList<>(this.v);',
     'ArrayList<Integer> arrayList = new ArrayList<>(this.v);'),
))
patch_generated_java('net/dercovisuals/gui/modules/ModulesTab.java', (
    ('ArrayList arrayList = new ArrayList();',
     'ArrayList<ModulesTab.ModuleEntry> arrayList = new ArrayList<>();'),
    ('Comparator.comparing(moduleEntry -> moduleEntry.a().toLowerCase())',
     'Comparator.comparing((ModulesTab.ModuleEntry moduleEntry) -> moduleEntry.a().toLowerCase())'),
    ('Comparator.comparing(moduleEntry2 -> moduleEntry2.a().toLowerCase())',
     'Comparator.comparing((ModulesTab.ModuleEntry moduleEntry2) -> moduleEntry2.a().toLowerCase())'),
))
patch_generated_java('net/dercovisuals/effects/ParticleRenderService.java', (
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
patch_generated_java('net/dercovisuals/mixin/DebugHudMixin.java', (
    ('''((List)callbackInfoReturnable.getReturnValue()).stream().filter(o -> {
            String str = (String)o;''',
     '''callbackInfoReturnable.getReturnValue().stream().filter((String str) -> {'''),
))

# Exclude the weapon-specific Mace helper and all of its visual hooks.
(java_root / 'net/dercovisuals/modules/utilities/MaceHelper.java').unlink(missing_ok=True)
(java_root / 'net/dercovisuals/render/MaceHelperHandConsumer.java').unlink(missing_ok=True)
patch_generated_java('net/dercovisuals/module/ModuleRegistry.java', (
    ('import net.dercovisuals.modules.utilities.MaceHelper;\n', ''),
    ('   public static final MaceHelper MACE_HELPER = new MaceHelper();\n', ''),
    ('         MACE_HELPER,\n', ''),
))
patch_generated_java('net/dercovisuals/render/HandRenderState.java', (
    ('   public static boolean renderingMace = false;\n', ''),
))
patch_generated_java('net/dercovisuals/mixin/HeldItemRendererMixin.java', (
    ('import net.dercovisuals.modules.utilities.MaceHelper;\n', ''),
    ('      HandRenderState.renderingMace = MaceHelper.isMace(ItemStackVar);\n', ''),
    ('         HandRenderState.renderingMace = false;\n', ''),
))
patch_generated_java('net/dercovisuals/mixin/ItemRendererMixin.java', (
    ('import net.dercovisuals.modules.utilities.MaceHelper;\n', ''),
    ('import net.dercovisuals.render.MaceHelperHandConsumer;\n', ''),
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
patch_generated_java('net/dercovisuals/mixin/DrawContextMixin.java', (
    ('import net.dercovisuals.modules.utilities.MaceHelper;\n', ''),
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
trigger_bot = java_root / 'net/dercovisuals/modules/utilities/TriggerBot.java'
trigger_bot.unlink(missing_ok=True)
patch_generated_java('net/dercovisuals/module/ModuleRegistry.java', (
    ('import net.dercovisuals.modules.utilities.TriggerBot;\n', ''),
    ('   public static final TriggerBot TRIGGER_BOT = new TriggerBot();\n', ''),
    ('         KILL_EFFECT,\n         TRIGGER_BOT\n', '         KILL_EFFECT\n'),
))

# EntityFinder deliberately returns Entity; each predicate below already narrows to
# PlayerEntity, so cast only after the result has passed that predicate.
for relative_path in (
    'net/dercovisuals/modules/utilities/HwHelper.java',
    'net/dercovisuals/modules/utilities/FtHelper.java',
    'net/dercovisuals/modules/utilities/FcHelper.java',
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

# Player removal moved to PlayerRemoveS2CPacket in 1.21.11. Keep join/leave
# notifications by handling the corresponding packet explicitly.
player_list = java_root / 'net/dercovisuals/mixin/PlayerListMixin.java'
player_list_source = '''package net.dercovisuals.mixin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.dercovisuals.network.packet.s2c.play.PlayerRemoveS2CPacket;
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
import net.dercovisuals.events.EventBusService;
import net.dercovisuals.events.PlayerJoinEvent;
import net.dercovisuals.events.PlayerLeaveEvent;

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
    'net.dercovisuals.network.packet.s2c.play.PlayerRemoveS2CPacket',
    'net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket'
)
player_list.write_text(player_list_source, encoding='utf-8')
# Rebrand resource namespace and remove Pulse-only auth/brand assets where not referenced.
for p in (SRC / 'resources').rglob('*'):
    if p.is_file() and p.suffix.lower() in ('.json', '.fsh', '.vsh', '.frag', '.glsl', '.txt'):
        try: t = p.read_text(encoding='utf-8')
        except UnicodedecodeError: continue
        t = t.replace('assets/pulse', 'assets/dercovisuals').replace('pulse:', 'dercovisuals:').replace('"pulse"', '"dercovisuals"').replace('ru.pulse', 'net.dercovisuals').replace('pulse.', 'net.dercovisuals.')
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
