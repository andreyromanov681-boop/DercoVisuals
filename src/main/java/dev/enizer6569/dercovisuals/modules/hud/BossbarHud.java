// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.hud;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "BossbarHud", b = "Отображает Bossbar", c = ModuleCategory.HUD)
public class BossbarHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);

   public BossbarHud() {
      this.collectSettings();
   }

   public SliderSetting getScale() {
      return this.scale;
   }
}
