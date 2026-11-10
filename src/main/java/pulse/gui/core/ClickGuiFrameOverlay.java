package pulse.gui.core;

import org.joml.Matrix3x2fStack;
import pulse.render.Renderer2D;
import pulse.theme.Theme;

public class ClickGuiFrameOverlay implements ClickGuiOverlay {
   private static final float c = 90.0F;
   private static final float d = 0.5F;
   public static int a;
   public static boolean b;

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      float fD = PulseClickGuiScreen.d();
      float fE = PulseClickGuiScreen.e();
      float f3 = PulseClickGuiScreen.f();
      renderer2D.a(f - 0.5F, f2 - 0.5F, fD + 1.0F, fE + 1.0F, f3, Theme.w, Theme.w, Theme.w, Theme.w, MatrixStackVar);
      renderer2D.a(f, f2, fD, fE, f3, Theme.e, Theme.e, Theme.f, Theme.f, MatrixStackVar);
   }

   public void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      float fD = PulseClickGuiScreen.d();
      float fE = PulseClickGuiScreen.e();
      renderer2D.b().a(f, f2, fD, fE, PulseClickGuiScreen.f(), MatrixStackVar);
      renderer2D.a(f + fD / 2.0F, f2 - 25.0F, 115.0F, Theme.a(Theme.y, 42), MatrixStackVar);
      renderer2D.b().a(MatrixStackVar);
   }

   @Override
   public void a(float f, float f2, int i, int i2) {
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
