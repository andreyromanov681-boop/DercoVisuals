package pulse.module;

import java.util.ArrayList;
import java.util.List;
import pulse.modules.hud.ArmorHud;
import pulse.modules.hud.BossbarHud;
import pulse.modules.hud.ClientColor;
import pulse.modules.hud.CooldownsHud;
import pulse.modules.hud.EffectNotify;
import pulse.modules.hud.HotkeysHud;
import pulse.modules.hud.InventoryHud;
import pulse.modules.hud.PotionsHud;
import pulse.modules.hud.SaturationHud;
import pulse.modules.hud.ScoreboardHud;
import pulse.modules.hud.TargetHud;
import pulse.modules.hud.TntTimer;
import pulse.modules.hud.TotemsHud;
import pulse.modules.hud.Watermark;
import pulse.modules.utilities.ArmorNotifier;
import pulse.modules.utilities.AucHelper;
import pulse.modules.utilities.AutoDuel;
import pulse.modules.utilities.AutoEat;
import pulse.modules.utilities.AutoInvest;
import pulse.modules.utilities.AutoLeave;
import pulse.modules.utilities.AutoPotion;
import pulse.modules.utilities.AutoReconnect;
import pulse.modules.utilities.AutoReissue;
import pulse.modules.utilities.AutoRespawn;
import pulse.modules.utilities.ChatHelper;
import pulse.modules.utilities.ChestSorter;
import pulse.modules.utilities.CommandSafe;
import pulse.modules.utilities.Cooldowns;
import pulse.modules.utilities.DeathMarker;
import pulse.modules.utilities.ElytraSwap;
import pulse.modules.utilities.FakePlayer;
import pulse.modules.utilities.FastEXP;
import pulse.modules.utilities.FastSwap;
import pulse.modules.utilities.FcHelper;
import pulse.modules.utilities.FreeLook;
import pulse.modules.utilities.FtHelper;
import pulse.modules.utilities.GpsCommand;
import pulse.modules.utilities.HealingHelper;
import pulse.modules.utilities.HwHelper;
import pulse.modules.utilities.ItemHighlighter;
import pulse.modules.utilities.ItemPickupLogger;
import pulse.modules.utilities.ItemScroller;
import pulse.modules.utilities.ItemSwap;
import pulse.modules.utilities.LockSlot;
import pulse.modules.utilities.MaceHelper;
import pulse.modules.utilities.Optimization;
import pulse.modules.utilities.Predictions;
import pulse.modules.utilities.PvpSafe;
import pulse.modules.utilities.RwHelper;
import pulse.modules.utilities.RwJoiner;
import pulse.modules.utilities.ShiftTab;
import pulse.modules.utilities.SoundController;
import pulse.modules.utilities.Sprint;
import pulse.modules.utilities.StreamerMode;
import pulse.modules.utilities.TapeMouse;
import pulse.modules.utilities.TotemTracker;
import pulse.modules.utilities.TriggerBot;
import pulse.modules.utilities.Zoom;
import pulse.modules.visuals.Animations;
import pulse.modules.visuals.ArrowNametag;
import pulse.modules.visuals.AspectRatio;
import pulse.modules.visuals.Atmosphere;
import pulse.modules.visuals.BlockOverlay;
import pulse.modules.visuals.BodyGlow;
import pulse.modules.visuals.ChinaHat;
import pulse.modules.visuals.Crosshair;
import pulse.modules.visuals.CustomHand;
import pulse.modules.visuals.FullBright;
import pulse.modules.visuals.HitBubble;
import pulse.modules.visuals.HitColor;
import pulse.modules.visuals.HitRange;
import pulse.modules.visuals.HitSounds;
import pulse.modules.visuals.HitboxCustomizer;
import pulse.modules.visuals.ItemPhysics;
import pulse.modules.visuals.JumpCircles;
import pulse.modules.visuals.KillEffect;
import pulse.modules.visuals.MotionBlur;
import pulse.modules.visuals.Nimb;
import pulse.modules.visuals.NoFluid;
import pulse.modules.visuals.Particles;
import pulse.modules.visuals.RenderTweaks;
import pulse.modules.visuals.SelfNametag;
import pulse.modules.visuals.ShulkerPreview;
import pulse.modules.visuals.TabCustomizer;
import pulse.modules.visuals.TargetEsp;
import pulse.modules.visuals.Trails;
import pulse.modules.visuals.WorldCustomizer;
import pulse.modules.visuals.WorldParticles;

public final class ModuleRegistry {
   private static final List<ClientModule> MODULES = new ArrayList<>();
   public static final FullBright FULL_BRIGHT = new FullBright();
   public static final Trails TRAILS = new Trails();
   public static final Particles PARTICLES = new Particles();
   public static final WorldParticles WORLD_PARTICLES = new WorldParticles();
   public static final JumpCircles JUMP_CIRCLES = new JumpCircles();
   public static final CustomHand CUSTOM_HAND = new CustomHand();
   public static final AspectRatio ASPECT_RATIO = new AspectRatio();
   public static final RenderTweaks RENDER_TWEAKS = new RenderTweaks();
   public static final TargetEsp TARGET_ESP = new TargetEsp();
   public static final HitboxCustomizer HITBOX_CUSTOMIZER = new HitboxCustomizer();
   public static final WorldCustomizer WORLD_CUSTOMIZER = new WorldCustomizer();
   public static final Crosshair CROSSHAIR = new Crosshair();
   public static final HitColor HIT_COLOR = new HitColor();
   public static final NoFluid NO_FLUID = new NoFluid();
   public static final BlockOverlay BLOCK_OVERLAY = new BlockOverlay();
   public static final HitBubble HIT_BUBBLE = new HitBubble();
   public static final SelfNametag SELF_NAMETAG = new SelfNametag();
   public static final HitSounds HIT_SOUNDS = new HitSounds();
   public static final ShulkerPreview SHULKER_PREVIEW = new ShulkerPreview();
   public static final Animations ANIMATIONS = new Animations();
   public static final Sprint SPRINT = new Sprint();
   public static final ElytraSwap ELYTRA_SWAP = new ElytraSwap();
   public static final FtHelper FT_HELPER = new FtHelper();
   public static final HealingHelper HEALING_HELPER = new HealingHelper();
   public static final Predictions PREDICTIONS = new Predictions();
   public static final ItemSwap ITEM_SWAP = new ItemSwap();
   public static final FastSwap FAST_SWAP = new FastSwap();
   public static final PvpSafe PVP_SAFE = new PvpSafe();
   public static final Cooldowns COOLDOWNS = new Cooldowns();
   public static final AutoEat AUTO_EAT = new AutoEat();
   public static final AutoInvest AUTO_INVEST = new AutoInvest();
   public static final AutoPotion AUTO_POTION = new AutoPotion();
   public static final AutoRespawn AUTO_RESPAWN = new AutoRespawn();
   public static final AutoReconnect AUTO_RECONNECT = new AutoReconnect();
   public static final AutoLeave AUTO_LEAVE = new AutoLeave();
   public static final FcHelper FC_HELPER = new FcHelper();
   public static final StreamerMode STREAMER_MODE = new StreamerMode();
   public static final HwHelper HW_HELPER = new HwHelper();
   public static final AutoReissue AUTO_REISSUE = new AutoReissue();
   public static final AucHelper AUC_HELPER = new AucHelper();
   public static final ChestSorter CHEST_SORTER = new ChestSorter();
   public static final ChatHelper CHAT_HELPER = new ChatHelper();
   public static final ArmorNotifier ARMOR_NOTIFIER = new ArmorNotifier();
   public static final TapeMouse TAPE_MOUSE = new TapeMouse();
   public static final FreeLook FREE_LOOK = new FreeLook();
   public static final ItemScroller ITEM_SCROLLER = new ItemScroller();
   public static final LockSlot LOCK_SLOT = new LockSlot();
   public static final Zoom ZOOM = new Zoom();
   public static final TotemTracker TOTEM_TRACKER = new TotemTracker();
   public static final ItemHighlighter ITEM_HIGHLIGHTER = new ItemHighlighter();
   public static final AutoDuel AUTO_DUEL = new AutoDuel();
   public static final RwHelper RW_HELPER = new RwHelper();
   public static final ShiftTab SHIFT_TAB = new ShiftTab();
   public static final SoundController SOUND_CONTROLLER = new SoundController();
   public static final RwJoiner RW_JOINER = new RwJoiner();
   public static final FastEXP FAST_EXP = new FastEXP();
   public static final ItemPickupLogger ITEM_PICKUP_LOGGER = new ItemPickupLogger();
   public static final PotionsHud POTIONS_HUD = new PotionsHud();
   public static final TargetHud TARGET_HUD = new TargetHud();
   public static final EffectNotify EFFECT_NOTIFY = new EffectNotify();
   public static final ClientColor CLIENT_COLOR = new ClientColor();
   public static final ArmorHud ARMOR_HUD = new ArmorHud();
   public static final HotkeysHud HOTKEYS_HUD = new HotkeysHud();
   public static final Watermark WATERMARK = new Watermark();
   public static final ChinaHat CHINA_HAT = new ChinaHat();
   public static final Nimb NIMB = new Nimb();
   public static final CooldownsHud COOLDOWNS_HUD = new CooldownsHud();
   public static final SaturationHud SATURATION_HUD = new SaturationHud();
   public static final InventoryHud INVENTORY_HUD = new InventoryHud();
   public static final FakePlayer FAKE_PLAYER = new FakePlayer();
   public static final DeathMarker DEATH_MARKER = new DeathMarker();
   public static final GpsCommand GPS_COMMAND = new GpsCommand();
   public static final ScoreboardHud SCOREBOARD_HUD = new ScoreboardHud();
   public static final BossbarHud BOSSBAR_HUD = new BossbarHud();
   public static final TntTimer TNT_TIMER = new TntTimer();
   public static final TotemsHud TOTEMS_HUD = new TotemsHud();
   public static final Optimization OPTIMIZATION = new Optimization();
   public static final MaceHelper MACE_HELPER = new MaceHelper();
   public static final BodyGlow BODY_GLOW = new BodyGlow();
   public static final ArrowNametag ARROW_NAMETAG = new ArrowNametag();
   public static final ItemPhysics ITEM_PHYSICS = new ItemPhysics();
   public static final HitRange HIT_RANGE = new HitRange();
   public static final CommandSafe COMMAND_SAFE = new CommandSafe();
   public static final TabCustomizer TAB_CUSTOMIZER = new TabCustomizer();
   public static final MotionBlur MOTION_BLUR = new MotionBlur();
   public static final Atmosphere ATMOSPHERE = new Atmosphere();
   public static final KillEffect KILL_EFFECT = new KillEffect();
   public static final TriggerBot TRIGGER_BOT = new TriggerBot();

   private ModuleRegistry() {
   }

   public static void init() {
      MODULES.clear();
      register(
         FULL_BRIGHT,
         TRAILS,
         PARTICLES,
         WORLD_PARTICLES,
         JUMP_CIRCLES,
         CUSTOM_HAND,
         ASPECT_RATIO,
         RENDER_TWEAKS,
         TARGET_ESP,
         HITBOX_CUSTOMIZER,
         WORLD_CUSTOMIZER,
         CROSSHAIR,
         HIT_COLOR,
         NO_FLUID,
         BLOCK_OVERLAY,
         HIT_BUBBLE,
         SELF_NAMETAG,
         HIT_SOUNDS,
         SHULKER_PREVIEW,
         ANIMATIONS,
         SPRINT,
         ELYTRA_SWAP,
         FT_HELPER,
         HEALING_HELPER,
         PREDICTIONS,
         ITEM_SWAP,
         FAST_SWAP,
         PVP_SAFE,
         COOLDOWNS,
         AUTO_EAT,
         AUTO_INVEST,
         AUTO_POTION,
         AUTO_RESPAWN,
         AUTO_RECONNECT,
         AUTO_LEAVE,
         FC_HELPER,
         STREAMER_MODE,
         HW_HELPER,
         AUTO_REISSUE,
         AUC_HELPER,
         CHAT_HELPER,
         ARMOR_NOTIFIER,
         TAPE_MOUSE,
         FREE_LOOK,
         ITEM_SCROLLER,
         LOCK_SLOT,
         ZOOM,
         TOTEM_TRACKER,
         ITEM_HIGHLIGHTER,
         AUTO_DUEL,
         RW_HELPER,
         SHIFT_TAB,
         SOUND_CONTROLLER,
         RW_JOINER,
         FAST_EXP,
         ITEM_PICKUP_LOGGER,
         POTIONS_HUD,
         TARGET_HUD,
         EFFECT_NOTIFY,
         ARMOR_HUD,
         HOTKEYS_HUD,
         WATERMARK,
         CHINA_HAT,
         NIMB,
         COOLDOWNS_HUD,
         SATURATION_HUD,
         INVENTORY_HUD,
         FAKE_PLAYER,
         DEATH_MARKER,
         GPS_COMMAND,
         TNT_TIMER,
         TOTEMS_HUD,
         OPTIMIZATION,
         MACE_HELPER,
         BODY_GLOW,
         ARROW_NAMETAG,
         ITEM_PHYSICS,
         HIT_RANGE,
         COMMAND_SAFE,
         TAB_CUSTOMIZER,
         MOTION_BLUR,
         ATMOSPHERE,
         KILL_EFFECT,
         TRIGGER_BOT
      );
      CLIENT_COLOR.collectSettings();
   }

   public static List<ClientModule> all() {
      return MODULES;
   }

   public static List<ClientModule> byCategory(ModuleCategory moduleCategory) {
      ArrayList arrayList = new ArrayList();

      for (ClientModule clientModule : MODULES) {
         if (clientModule.category() == moduleCategory) {
            arrayList.add(clientModule);
         }
      }

      return arrayList;
   }

   public static ClientModule findByName(String str) {
      for (ClientModule clientModule : MODULES) {
         if (clientModule.name().equalsIgnoreCase(str)) {
            return clientModule;
         }
      }

      return null;
   }

   public static <ConfigTextDialog extends ClientModule> ConfigTextDialog get(Class<ConfigTextDialog> cls) {
      for (ClientModule clientModule : MODULES) {
         if (cls.isInstance(clientModule)) {
            return cls.cast(clientModule);
         }
      }

      return null;
   }

   private static void register(ClientModule... clientModuleArr) {
      for (ClientModule clientModule : clientModuleArr) {
         MODULES.add(clientModule);
         clientModule.collectSettings();
      }
   }
}
