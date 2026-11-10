package pulse.hud.notifications;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;

public class TextNotification extends HudNotification {
   private static final Color m = new Color(87, 215, 106);
   private static final Color n = new Color(255, 255, 255);
   private static final Color o = new Color(223, 223, 243);
   private static final Color p = new Color(255, 255, 255, 77);
   private static final float q = 12.0F;
   private static final float r = 3.0F;
   private static final float s = 3.0F;
   private final String t;
   private final String u;
   private final String v;
   public static int a;
   public static boolean b;

   public TextNotification(String str, String str2, String str3) {
      this.t = str;
      this.u = str2;
      this.v = str3;
      this.g = 8.0F;
      this.h = 4.5F;
      this.j = 3000L;
   }

   @Override
   public float a() {
      FontRenderer fontRenderer = FontManager.b[15];
      float fA = 15.0F + FontManager.a[15].a(this.t);
      return fA + 15.0F + fontRenderer.a(this.u) + 3.0F + fontRenderer.a("ᗴ∪ᗎ") + 3.0F + fontRenderer.a(this.v) + 20.0F;
   }

   @Override
   public float b() {
      return 13.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[15];
      FontRenderer fontRenderer2 = FontManager.a[15];
      float f6 = f2 + f4 / 2.0F;
      int i = (int)(f5 * 255.0F);
      Color color = new Color(223, 223, 255, i);
      Color color2 = new Color(255, 255, 255, i);
      Color color3 = new Color(p.getRed(), p.getGreen(), p.getBlue(), (int)(p.getAlpha() * f5));
      float fB = f6 - fontRenderer2.b(this.t) / 4.0F;
      float f7 = f6 - 6.0F;
      FontManager.e[22].a("crypt", f, f7 + 0.5F, new Color(m.getRed(), m.getGreen(), m.getBlue(), i), MatrixStackVar);
      fontRenderer2.a(this.t, f + 15.0F, fB, color, MatrixStackVar);
      float fA = fontRenderer.a("crypt");
      float fA2 = fontRenderer.a(this.u);
      float fA3 = f + f3 - (fA2 + 3.0F + fA + 3.0F + fontRenderer.a(this.v));
      fontRenderer.a(this.u, fA3, fB, color2, MatrixStackVar);
      float separatorX = fA3 + fA2 + 3.0F;
      fontRenderer.a("crypt", separatorX, fB, color3, MatrixStackVar);
      fontRenderer.a(this.v, separatorX + fA + 3.0F, fB, color2, MatrixStackVar);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
