package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Saturation HUD", b = "Удобное отображение уровня сытости.", c = ModuleCategory.HUD)
public class SaturationHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Scale", 1.0F, 0.8F, 2.0F, 0.1F);

   public SaturationHud() {
      this.collectSettings();
   }

   public SliderSetting n() {
      return this.scale;
   }
}
