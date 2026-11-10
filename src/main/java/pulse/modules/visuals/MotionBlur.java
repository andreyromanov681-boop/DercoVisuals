package pulse.modules.visuals;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Motion Blur", b = "Плавное размытие движения", c = ModuleCategory.VISUALS)
public class MotionBlur extends ClientModule {
   public final SliderSetting blurAmount = new SliderSetting("Сила", 50.0F, 1.0F, 100.0F, 1.0F);
}
