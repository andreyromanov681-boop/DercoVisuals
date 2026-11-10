package pulse.modules.hud;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Hotkeys", b = "Displays active key bindings.", c = ModuleCategory.HUD)
public class HotkeysHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);

   public HotkeysHud() {
      this.collectSettings();
   }

   public SliderSetting n() {
      return this.scale;
   }
}
