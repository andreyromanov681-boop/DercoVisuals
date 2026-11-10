package pulse.gui.widgets;

import java.awt.Color;
import java.util.function.Consumer;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiInteractionState;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;

public class SettingsIconButton {
   private static final float c = 16.0F;
   private float d;
   private float e;
   private float f;
   private final AnimationState g = new AnimationState();
   private boolean h = false;
   private Consumer<Void> i;
   public static int a;
   public static boolean b;

   public void a(Consumer<Void> consumer) {
      this.i = consumer;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, int i, int i2) {
      this.d = f;
      this.e = f2;
      this.f = f3;
      this.g.a();
      boolean zB = GuiInteractionState.a().b();
      int i3 = !zB && GuiInput.a(f, f2, f3, f3, i, i2) ? 1 : 0;
      if (zB && this.h) {
         this.g.a(0.0, 0.15, Easing.h);
         this.h = false;
      } else if (!zB && Bool.from(i3) != this.h) {
         this.g.a(i3 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.h = Bool.from(i3);
      }

      this.a(MatrixStackVar, renderer2D, f, f2, f3, (float)this.g.j());
      if (i3 != 0 && !zB) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4) {
      FontRenderer fontRenderer = FontManager.e[16];
      float fA = fontRenderer.a("\ue926");
      float fB = fontRenderer.b("\ue926");
      float f5 = f + (f3 - fA) / 2.0F;
      float f6 = f2 + (f3 - fB) / 2.0F + 4.0F;
      int i = (int)(f4 * 40.0F);
      fontRenderer.a(
         "\ue926",
         f5,
         f6,
         new Color(Math.min(255, (49 | i) + (49 & i)), Math.min(255, (49 ^ i) + 2 * (49 & i)), Math.min(255, 2 * (69 | i) - (69 ^ i))),
         MatrixStackVar
      );
   }

   public boolean a(int i, int i2) {
      boolean zB = GuiInteractionState.a().b();
      if (GuiInput.a(this.d, this.e, this.f, this.f, i, i2) && !zB) {
         if (this.i != null) {
            this.i.accept(null);
         }

         return true;
      } else {
         return false;
      }
   }

   public float a() {
      return this.d;
   }

   public float b() {
      return this.e;
   }

   public float c() {
      return this.f;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
