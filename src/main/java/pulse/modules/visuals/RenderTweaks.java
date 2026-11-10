package pulse.modules.visuals;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Render Tweaks", b = "Отключает различные элементы рендеринга", c = ModuleCategory.VISUALS)
public class RenderTweaks extends ClientModule {
   private static final String[] e = new String[]{
      "Погода", "Тряска урона", "Оверлей огня", "Анимация тотема", "Скорборд", "Боссбар", "Черные сердца", "Пузырьки воздуха", "Свечение игроков"
   };
   private final ModeSetting f = new ModeSetting("Твики", e, new int[]{0, 1, 2, 3, 6, 7, 8});
   public static int a;
   public static boolean b;

   public boolean n() {
      return this.k() && this.f.c("Погода");
   }

   public boolean o() {
      return this.k() && this.f.c("Тряска урона");
   }

   public boolean p() {
      return this.k() && this.f.c("Оверлей огня");
   }

   public boolean q() {
      return this.k() && this.f.c("Анимация тотема");
   }

   public boolean r() {
      return this.k() && this.f.c("Скорборд");
   }

   public boolean s() {
      return this.k() && this.f.c("Боссбар");
   }

   public boolean t() {
      return this.k() && this.f.c("Черные сердца");
   }

   public boolean u() {
      return this.k() && this.f.c("Пузырьки воздуха");
   }

   public boolean v() {
      return this.k() && this.f.c("Свечение игроков");
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
