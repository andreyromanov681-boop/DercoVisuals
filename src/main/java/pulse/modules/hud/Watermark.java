package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;

@ModuleInfo(a = "Watermark", b = "Displays the client watermark", c = ModuleCategory.HUD)
public class Watermark extends ClientModule {
   private final BooleanSetting notifications = new BooleanSetting("Уведомления", true);
   private final BooleanSetting music = new BooleanSetting("Музыка", true);
   private final BooleanSetting fpsAndPing = new BooleanSetting("FPS & Ping", true);
   private final BooleanSetting events = new BooleanSetting("Ивенты", true);
   private final BooleanSetting functions = new BooleanSetting("Функции", true);

   public Watermark() {
      this.collectSettings();
   }

   public BooleanSetting notifications() {
      return this.notifications;
   }

   public BooleanSetting music() {
      return this.music;
   }

   public BooleanSetting fpsAndPing() {
      return this.fpsAndPing;
   }

   public BooleanSetting events() {
      return this.events;
   }

   public BooleanSetting functions() {
      return this.functions;
   }
}
