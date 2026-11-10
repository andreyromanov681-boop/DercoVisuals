// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Motion Blur", b = "Плавное размытие движения", c = ModuleCategory.VISUALS)
public class MotionBlur extends ClientModule {
   public final SliderSetting blurAmount = new SliderSetting("Сила", 50.0F, 1.0F, 100.0F, 1.0F);
}
