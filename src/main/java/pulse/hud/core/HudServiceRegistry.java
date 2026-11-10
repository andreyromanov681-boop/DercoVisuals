package pulse.hud.core;

import java.util.ArrayList;
import java.util.List;
import pulse.effects.ParticleRenderService;
import pulse.effects.PotionEffectService;
import pulse.markers.MapMarkerModule;
import pulse.markers.MapMarkerRenderer;
import pulse.media.MediaSessionService;
import pulse.module.ModuleAccessService;
import pulse.player.TargetTracker;

public final class HudServiceRegistry {
   private static final List<HudService> SERVICES = new ArrayList<>();
   public static final MediaSessionService MEDIA = new MediaSessionService();
   public static final TargetTracker TARGETS = new TargetTracker();
   public static final MapMarkerRenderer MAP_MARKERS = new MapMarkerRenderer();
   public static final MapMarkerModule MAP_MARKER_MODULE = new MapMarkerModule();
   public static final ModuleAccessService MODULE_ACCESS = new ModuleAccessService();
   public static final ParticleRenderService PARTICLES = new ParticleRenderService();
   public static final PotionEffectService POTION_EFFECTS = new PotionEffectService();
   public static final MediaSessionService a = MEDIA;
   public static final TargetTracker b = TARGETS;
   public static final MapMarkerRenderer c = MAP_MARKERS;
   public static final MapMarkerModule d = MAP_MARKER_MODULE;
   public static final ModuleAccessService e = MODULE_ACCESS;
   public static final ParticleRenderService f = PARTICLES;
   public static final PotionEffectService g = POTION_EFFECTS;

   private HudServiceRegistry() {
   }

   public static void a() {
      SERVICES.clear();
      register(MEDIA);
      register(TARGETS);
      register(MAP_MARKERS);
      register(MAP_MARKER_MODULE);
      register(MODULE_ACCESS);
      register(PARTICLES);
      register(POTION_EFFECTS);
   }

   private static void register(HudService hudService) {
      SERVICES.add(hudService);
      hudService.a();
   }

   public static List<HudService> b() {
      return new ArrayList<>(SERVICES);
   }
}
