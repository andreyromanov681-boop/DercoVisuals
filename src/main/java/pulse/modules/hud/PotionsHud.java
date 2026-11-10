package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Potions", b = "Shows active potion effects.", c = ModuleCategory.HUD)
public class PotionsHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);

   public PotionsHud() {
      this.collectSettings();
   }

   public SliderSetting n() {
      return this.scale;
   }
}
