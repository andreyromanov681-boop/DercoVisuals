package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

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
