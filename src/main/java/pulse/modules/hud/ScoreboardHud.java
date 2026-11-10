package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "ScoreboardHud", b = "Отображает Scoreboard", c = ModuleCategory.HUD)
public class ScoreboardHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);

   public ScoreboardHud() {
      this.collectSettings();
   }

   public SliderSetting getScale() {
      return this.scale;
   }
}
