package pulse.modules.hud;

import java.awt.Color;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Client Color", b = "Настройка основного цвета клиента", c = ModuleCategory.VISUALS)
public class ClientColor extends ClientModule {
   private static final int GRADIENT_DURATION_MS = 4000;
   private static final int ASTOLFO_DURATION_MS = 6000;
   private static final int RAINBOW_DURATION_MS = 6000;
   public final ModeSetting a;
   public final ColorSetting b;
   public final ColorSetting e;
   public final ColorSetting f;
   private static final String MODE_STATIC = "Статичный";
   private static final String MODE_GRADIENT = "Градиент";
   private static final String MODE_ASTOLFO = "Астольфо";
   private static final String MODE_RAINBOW = "Радуга";
   private static final String[] MODES = new String[]{"Статичный", "Градиент", "Астольфо", "Радуга"};
   private static final Color DEFAULT_COLOR = new Color(138, 43, 226);
   private static final Color DEFAULT_GRADIENT_START = Color.WHITE;
   private static final Color DEFAULT_GRADIENT_END = new Color(0, 125, 245);
   private static final Color[] ASTOLFO_COLORS = new Color[]{new Color(255, 121, 198), new Color(170, 70, 255), new Color(80, 181, 255)};

   public ClientColor() {
      this.a = new ModeSetting("Режим", MODES, "Статичный");
      this.b = new ColorSetting("Цвет", DEFAULT_COLOR).a(() -> this.a.b("Статичный"));
      this.e = new ColorSetting("Цвет #1", DEFAULT_GRADIENT_START).a(() -> this.a.b("Градиент"));
      this.f = new ColorSetting("Цвет #2", DEFAULT_GRADIENT_END).a(() -> this.a.b("Градиент"));
      this.collectSettings();
   }

   public Color n() {
      String strD = this.a.d();
      return "Статичный".equals(strD)
         ? this.b.a()
         : (
            "Градиент".equals(strD)
               ? this.animatedGradient()
               : (
                  "Астольфо".equals(strD)
                     ? cycleColors(ASTOLFO_COLORS, 6000)
                     : ("Радуга".equals(strD) ? Color.getHSBColor(linearProgress(6000), 1.0F, 1.0F) : this.b.a())
               )
         );
   }

   public int o() {
      return this.n().getRGB();
   }

   private Color animatedGradient() {
      return interpolate(this.e.a(), this.f.a(), sineProgress(4000));
   }

   private static Color cycleColors(Color[] colorArr, int i) {
      if (colorArr.length == 0) {
         return Color.WHITE;
      }

      float fLinearProgress = linearProgress(i) * colorArr.length;
      int length = (int)fLinearProgress % colorArr.length;
      return interpolate(colorArr[length], colorArr[(length + 1) % colorArr.length], fLinearProgress - (int)fLinearProgress);
   }

   private static float sineProgress(int i) {
      return (float)(0.5 * (1.0 + Math.sin(System.currentTimeMillis() % i * ((Math.PI * 2) / i))));
   }

   private static float linearProgress(int i) {
      return (float)(System.currentTimeMillis() % i / i);
   }

   private static Color interpolate(Color color, Color color2, float f) {
      float fMax = Math.max(0.0F, Math.min(1.0F, f));
      return new Color(
         lerp(color.getRed(), color2.getRed(), fMax),
         lerp(color.getGreen(), color2.getGreen(), fMax),
         lerp(color.getBlue(), color2.getBlue(), fMax),
         lerp(color.getAlpha(), color2.getAlpha(), fMax)
      );
   }

   private static int lerp(int i, int i2, float f) {
      return (int)(i + (i2 - i) * f);
   }
}
