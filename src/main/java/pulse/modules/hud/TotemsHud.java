package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Totems HUD", b = "Удобное отображение количества тотемов.", c = ModuleCategory.HUD)
public class TotemsHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Scale", 1.0F, 0.8F, 2.0F, 0.1F);

   public TotemsHud() {
      this.collectSettings();
   }

   public SliderSetting getScale() {
      return this.scale;
   }
}
