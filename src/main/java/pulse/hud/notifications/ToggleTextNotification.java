package pulse.hud.notifications;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.util.ColorUtils;

public class ToggleTextNotification extends HudNotification {
   private static final Color m = new Color(87, 215, 106);
   private static final Color n = new Color(215, 87, 87);
   private static final Color o = new Color(255, 255, 255);
   private static final float p = 6.5F;
   private static final float q = 9.5F;
   private static final float r = 13.0F;
   private static final float s = 10.0F;
   private final String t;
   private boolean u;
   private final AnimationState v = new AnimationState();
   public static int a;
   public static boolean b;

   public ToggleTextNotification(String str, boolean z) {
      this.t = str;
      this.u = z;
      this.g = 8.0F;
      this.h = 4.5F;
      this.j = 2000L;
      this.v.d(!z ? 0.0 : 1.0);
   }

   public String m() {
      return this.t;
   }

   public boolean n() {
      return this.u;
   }

   public void a(boolean z) {
      if (this.u != z) {
         this.u = z;
         this.v.a(!z ? 0.0 : 1.0, 0.15, Easing.h);
         this.k = System.currentTimeMillis();
         this.l = false;
      }
   }

   @Override
   public void e() {
      super.e();
      this.v.a();
   }

   @Override
   public float a() {
      FontRenderer fontRenderer = FontManager.b[14];
      FontRenderer fontRenderer2 = FontManager.a[12];
      float fA = fontRenderer.a(this.t);
      return fA + 10.0F + fontRenderer2.a("Выключен") + 13.0F + 15.0F;
   }

   @Override
   public float b() {
      return 13.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[14];
      FontRenderer fontRenderer2 = FontManager.a[12];
      int i = (int)(f5 * 255.0F);
      float f6 = f2 + f4 / 2.0F;
      Color color = new Color(o.getRed(), o.getGreen(), o.getBlue(), i);
      fontRenderer.a(this.t, f, f6 - fontRenderer.b(this.t) / 4.0F, color, MatrixStackVar);
      float fJ = (float)this.v.j();
      String strA = !this.u ? "Выключен" : "Включен";
      float fA = fontRenderer2.a(strA) + 13.0F;
      Color colorA = ColorUtils.a(n, m, fJ);
      Color color2 = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), i);
      float rectX = f + f3 - fA + 2.0F;
      renderer2D.a(rectX, f6 - 6.5F, fA, 13.0F, 6.5F, color2, MatrixStackVar);
      fontRenderer2.a(strA, rectX + 3.25F + 1.0F, f6 - fontRenderer2.b(strA) / 4.0F, color, MatrixStackVar);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
