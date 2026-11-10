// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import java.awt.Color;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ColorSetting;

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
