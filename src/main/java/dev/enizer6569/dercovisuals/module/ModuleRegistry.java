// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.module;

import java.util.ArrayList;
import java.util.List;
import dev.enizer6569.dercovisuals.modules.hud.ArmorHud;
import dev.enizer6569.dercovisuals.modules.hud.BossbarHud;
import dev.enizer6569.dercovisuals.modules.hud.ClientColor;
import dev.enizer6569.dercovisuals.modules.hud.CooldownsHud;
import dev.enizer6569.dercovisuals.modules.hud.EffectNotify;
import dev.enizer6569.dercovisuals.modules.hud.HotkeysHud;
import dev.enizer6569.dercovisuals.modules.hud.InventoryHud;
import dev.enizer6569.dercovisuals.modules.hud.PotionsHud;
import dev.enizer6569.dercovisuals.modules.hud.SaturationHud;
import dev.enizer6569.dercovisuals.modules.hud.ScoreboardHud;
import dev.enizer6569.dercovisuals.modules.hud.TargetHud;
import dev.enizer6569.dercovisuals.modules.hud.TntTimer;
import dev.enizer6569.dercovisuals.modules.hud.TotemsHud;
import dev.enizer6569.dercovisuals.modules.hud.Watermark;
import dev.enizer6569.dercovisuals.modules.utilities.ArmorNotifier;
import dev.enizer6569.dercovisuals.modules.utilities.AucHelper;
import dev.enizer6569.dercovisuals.modules.utilities.AutoDuel;
import dev.enizer6569.dercovisuals.modules.utilities.AutoEat;
import dev.enizer6569.dercovisuals.modules.utilities.AutoInvest;
import dev.enizer6569.dercovisuals.modules.utilities.AutoLeave;
import dev.enizer6569.dercovisuals.modules.utilities.AutoPotion;
import dev.enizer6569.dercovisuals.modules.utilities.AutoReconnect;
import dev.enizer6569.dercovisuals.modules.utilities.AutoReissue;
import dev.enizer6569.dercovisuals.modules.utilities.AutoRespawn;
import dev.enizer6569.dercovisuals.modules.utilities.ChatHelper;
import dev.enizer6569.dercovisuals.modules.utilities.ChestSorter;
import dev.enizer6569.dercovisuals.modules.utilities.CommandSafe;
import dev.enizer6569.dercovisuals.modules.utilities.Cooldowns;
import dev.enizer6569.dercovisuals.modules.utilities.DeathMarker;
import dev.enizer6569.dercovisuals.modules.utilities.ElytraSwap;
import dev.enizer6569.dercovisuals.modules.utilities.FakePlayer;
import dev.enizer6569.dercovisuals.modules.utilities.FastEXP;
import dev.enizer6569.dercovisuals.modules.utilities.FastSwap;
import dev.enizer6569.dercovisuals.modules.utilities.FcHelper;
import dev.enizer6569.dercovisuals.modules.utilities.FreeLook;
import dev.enizer6569.dercovisuals.modules.utilities.FtHelper;
import dev.enizer6569.dercovisuals.modules.utilities.GpsCommand;
import dev.enizer6569.dercovisuals.modules.utilities.HealingHelper;
import dev.enizer6569.dercovisuals.modules.utilities.HwHelper;
import dev.enizer6569.dercovisuals.modules.utilities.ItemHighlighter;
import dev.enizer6569.dercovisuals.modules.utilities.ItemPickupLogger;
import dev.enizer6569.dercovisuals.modules.utilities.ItemScroller;
import dev.enizer6569.dercovisuals.modules.utilities.ItemSwap;
import dev.enizer6569.dercovisuals.modules.utilities.LockSlot;
import dev.enizer6569.dercovisuals.modules.utilities.MaceHelper;
import dev.enizer6569.dercovisuals.modules.utilities.Optimization;
import dev.enizer6569.dercovisuals.modules.utilities.Predictions;
import dev.enizer6569.dercovisuals.modules.utilities.PvpSafe;
import dev.enizer6569.dercovisuals.modules.utilities.RwHelper;
import dev.enizer6569.dercovisuals.modules.utilities.RwJoiner;
import dev.enizer6569.dercovisuals.modules.utilities.ShiftTab;
import dev.enizer6569.dercovisuals.modules.utilities.SoundController;
import dev.enizer6569.dercovisuals.modules.utilities.Sprint;
import dev.enizer6569.dercovisuals.modules.utilities.StreamerMode;
import dev.enizer6569.dercovisuals.modules.utilities.TapeMouse;
import dev.enizer6569.dercovisuals.modules.utilities.TotemTracker;
import dev.enizer6569.dercovisuals.modules.utilities.TriggerBot;
import dev.enizer6569.dercovisuals.modules.utilities.Zoom;
import dev.enizer6569.dercovisuals.modules.visuals.Animations;
import dev.enizer6569.dercovisuals.modules.visuals.ArrowNametag;
import dev.enizer6569.dercovisuals.modules.visuals.AspectRatio;
import dev.enizer6569.dercovisuals.modules.visuals.Atmosphere;
import dev.enizer6569.dercovisuals.modules.visuals.BlockOverlay;
import dev.enizer6569.dercovisuals.modules.visuals.BodyGlow;
import dev.enizer6569.dercovisuals.modules.visuals.ChinaHat;
import dev.enizer6569.dercovisuals.modules.visuals.Crosshair;
import dev.enizer6569.dercovisuals.modules.visuals.CustomHand;
import dev.enizer6569.dercovisuals.modules.visuals.FullBright;
import dev.enizer6569.dercovisuals.modules.visuals.HitBubble;
import dev.enizer6569.dercovisuals.modules.visuals.HitColor;
import dev.enizer6569.dercovisuals.modules.visuals.HitRange;
import dev.enizer6569.dercovisuals.modules.visuals.HitSounds;
import dev.enizer6569.dercovisuals.modules.visuals.HitboxCustomizer;
import dev.enizer6569.dercovisuals.modules.visuals.ItemPhysics;
import dev.enizer6569.dercovisuals.modules.visuals.JumpCircles;
import dev.enizer6569.dercovisuals.modules.visuals.KillEffect;
import dev.enizer6569.dercovisuals.modules.visuals.MotionBlur;
import dev.enizer6569.dercovisuals.modules.visuals.Nimb;
import dev.enizer6569.dercovisuals.modules.visuals.NoFluid;
import dev.enizer6569.dercovisuals.modules.visuals.Particles;
import dev.enizer6569.dercovisuals.modules.visuals.RenderTweaks;
import dev.enizer6569.dercovisuals.modules.visuals.SelfNametag;
import dev.enizer6569.dercovisuals.modules.visuals.ShulkerPreview;
import dev.enizer6569.dercovisuals.modules.visuals.TabCustomizer;
import dev.enizer6569.dercovisuals.modules.visuals.TargetEsp;
import dev.enizer6569.dercovisuals.modules.visuals.Trails;
import dev.enizer6569.dercovisuals.modules.visuals.WorldCustomizer;
import dev.enizer6569.dercovisuals.modules.visuals.WorldParticles;

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
