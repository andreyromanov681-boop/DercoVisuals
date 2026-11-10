package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Cooldowns HUD", b = "Displays item cooldowns on screen.", c = ModuleCategory.HUD)
public class CooldownsHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);

   public CooldownsHud() {
      this.collectSettings();
   }

   public SliderSetting n() {
      return this.scale;
   }
}
