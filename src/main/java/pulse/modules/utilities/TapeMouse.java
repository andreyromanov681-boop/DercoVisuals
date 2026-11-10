package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.ModeSetting;
import pulse.settings.SliderSetting;
import ru.pulse.mixin.accessor.MinecraftClientAccessor;

@ModuleInfo(a = "Tape Mouse", b = "Автокликер", c = ModuleCategory.UTILITIES)
public class TapeMouse extends ClientModule {
   private final SliderSetting e = new SliderSetting("Задержка (тики)", 10.0F, 1.0F, 100.0F, 1.0F);
   private final ModeSetting f = new ModeSetting("Кнопка мыши", new String[]{"Левая", "Правая"}, "Левая");
   private int g = 0;
   public static int a;
   public static boolean b;

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null && c.world != null && !c.player.isUsingItem()) {
         if (this.g > 0) {
            int i = this.g;
            this.g = (i & -2) - (~i & 1);
         } else {
            if (this.f.b("Левая")) {
               ((MinecraftClientAccessor)c).invokeDoAttack();
            } else {
               ((MinecraftClientAccessor)c).invokeDoItemUse();
            }

            this.g = this.e.b();
         }
      }
   }

   @Override
   public void e() {
      this.g = 0;
      super.e();
   }

   @Override
   public void f() {
      this.g = 0;
      super.f();
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
