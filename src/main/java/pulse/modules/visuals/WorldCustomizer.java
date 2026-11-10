package pulse.modules.visuals;

import java.awt.Color;
import pulse.core.Bool;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "World Customizer", b = "Настройка мира", c = ModuleCategory.VISUALS)
public class WorldCustomizer extends ClientModule {
   private final SettingGroup e = new SettingGroup("Туман");
   private final BooleanSetting f = new BooleanSetting("Настроить туман", true);
   private final BooleanSetting g = new BooleanSetting("Кастомная дальность", false).a(() -> this.f.a());
   private final SliderSetting h = new SliderSetting("Дальность тумана", 0.3F, 0.1F, 1.0F, 0.01F).a(() -> this.f.a() && this.g.a() ? true : false);
   public final BooleanSetting removeFog = new BooleanSetting("Убрать туман", false);
   private final SettingGroup shaderGroup = new SettingGroup("Шейдер неба");
   public final BooleanSetting enableShader = new BooleanSetting("Включить шейдер", false);
   public final ModeSetting shaderMode = new ModeSetting("Режим", new String[]{"Nebula", "Aurora", "Galaxy", "Sunset", "Lava", "Cosmos", "BlackHole"}, 0)
      .a(() -> this.enableShader.a());
   private final SettingGroup i = new SettingGroup("Небо");
   private final BooleanSetting j = new BooleanSetting("Настроить небо", true);
   private final SettingGroup k = new SettingGroup("Цвет");
   private final BooleanSetting l = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting m = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> {
      int i;
      if (this.l.a()) {
         i = 0;
      } else {
         i = 1;
      }

      return Bool.from(i);
   });
   public static int a;
   public static boolean b;

   public boolean n() {
      return this.k() && this.f.a();
   }

   public boolean o() {
      return Bool.from(this.k() && this.f.a() && this.g.a() ? 1 : 0);
   }

   public float p() {
      return 0.0F;
   }

   public float q() {
      return this.h.a() * 250.0F;
   }

   public boolean r() {
      return this.k() && this.j.a();
   }

   public int s() {
      return this.u().getRGB();
   }

   public int t() {
      return this.u().getRGB();
   }

   private Color u() {
      return !this.l.a() ? this.m.a() : ModuleRegistry.CLIENT_COLOR.n();
   }

   @Override
   public void f() {
      super.f();
      if (c.worldRenderer != null) {
         c.worldRenderer.reload();
      }
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
