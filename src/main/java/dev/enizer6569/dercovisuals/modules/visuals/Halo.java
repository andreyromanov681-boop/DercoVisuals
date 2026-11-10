// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import dev.enizer6569.dercovisuals.events.LivingEntityRenderEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ColorSetting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Halo", b = "Draws a halo above the player.", c = ModuleCategory.VISUALS)
public class Halo extends ClientModule {
   private final SliderSetting lineWidth = new SliderSetting("Line Width", 0.05F, 0.01F, 0.2F, 0.01F);
   private final SliderSetting radius = new SliderSetting("Radius", 0.5F, 0.3F, 1.5F, 0.05F);
   private final SliderSetting yOffset = new SliderSetting("Y Offset", -0.1F, -1.0F, 0.5F, 0.05F);
   private final SettingGroup colorGroup = new SettingGroup("Color");
   private final BooleanSetting useClientColor = new BooleanSetting("Use Client Color", true);
   private final ColorSetting color = new ColorSetting("Custom Color", Color.WHITE).a(() -> !this.useClientColor.a());

   @EventHandler
   public void a(LivingEntityRenderEvent livingEntityRenderEvent) {
   }

   private Color n() {
      return this.useClientColor.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.color.a();
   }
}
