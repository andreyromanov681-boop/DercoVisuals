package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

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
