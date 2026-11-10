package pulse.modules.visuals;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.ModeSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Aspect Ratio", b = "Changes the screen aspect ratio", c = ModuleCategory.VISUALS)
public class AspectRatio extends ClientModule {
   private static final String MODE_16_9 = "16:9";
   private static final String MODE_4_3 = "4:3";
   private static final String MODE_21_9 = "21:9";
   private static final String MODE_1_1 = "1:1";
   private static final String MODE_CUSTOM = "Custom";
   public final ModeSetting a = new ModeSetting("Mode", new String[]{"16:9", "4:3", "21:9", "1:1", "Custom"}, "16:9");
   public final SliderSetting b = new SliderSetting("Custom Ratio", 1.78F, 0.5F, 3.0F, 0.01F).a(() -> this.a.b("Custom"));

   public float n() {
      switch (this.a.d()) {
         case "4:3":
            return 1.3333334F;
         case "21:9":
            return 2.3333333F;
         case "1:1":
            return 1.0F;
         case "Custom":
            return this.b.a();
         default:
            return 1.7777778F;
      }
   }
}
