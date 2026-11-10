// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.hud;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;

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
