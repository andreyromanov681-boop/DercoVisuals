// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.hud;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Target HUD", b = "Shows target information.", c = ModuleCategory.HUD)
public class TargetHud extends ClientModule {
   private final SliderSetting targetLifeTime = new SliderSetting("Время жизни таргета", 2.0F, 0.5F, 5.0F, 0.5F);

   public TargetHud() {
      this.collectSettings();
   }

   public SliderSetting targetLifeTime() {
      return this.targetLifeTime;
   }
}
