package pulse.modules.visuals;

import java.awt.Color;
import pulse.core.Bool;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;

@ModuleInfo(a = "Hit Color", b = "Изменяет цвет сущностей при получении урона", c = ModuleCategory.VISUALS)
public class HitColor extends ClientModule {
   private final BooleanSetting f = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting g = new ColorSetting("Кастомный цвет", Color.RED).a(() -> Bool.from(this.f.k() ? 0 : 1));
   public static int a;
   public static boolean b;

   public Color n() {
      return !this.f.k() ? this.g.k() : ModuleRegistry.CLIENT_COLOR.n();
   }

   public boolean o() {
      return this.k();
   }

   public boolean p() {
      return true;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
