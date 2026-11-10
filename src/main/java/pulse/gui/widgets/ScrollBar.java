package pulse.gui.widgets;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiInteractionState;
import pulse.render.Renderer2D;
import pulse.theme.Theme;

public class ScrollBar {
   private static final float c = 2.0F;
   private static final float d = 10.0F;
   private static final float e = 15.0F;
   private static final float f = 25.0F;
   private float g = 0.0F;
   private float h = 0.0F;
   private final AnimationState i = new AnimationState();
   private boolean j = false;
   private float k = 0.0F;
   private float l = 0.0F;
   private final AnimationState m = new AnimationState();
   private boolean n = false;
   private float o = 0.8F;
   private float p = 10.0F;
   private float q = 15.0F;
   private float r = 25.0F;
   private Color s = Theme.b;
   private Color t = Theme.d;
   private float u;
   private float v;
   private float w;
   private float x;
   private float y;
   public static int a;
   public static boolean b;

   public ScrollBar() {
   }

   public ScrollBar(float f2, float f3) {
      this.o = f2 / 2.5F;
      this.r = f3;
   }

   public void a(float f2) {
      this.o = f2;
   }

   public void b(float f2) {
      this.p = f2;
   }

   public void c(float f2) {
      this.q = f2;
   }

   public void d(float f2) {
      this.r = f2;
   }

   public void a(Color color) {
      this.s = color;
   }

   public void b(Color color) {
      this.t = color;
   }

   public void a() {
      if (!this.j) {
         this.i.a();
         this.g = (float)this.i.j();
      }
   }

   public boolean a(float f2, float f3) {
      return Bool.from(f2 <= f3 ? 0 : 1);
   }

   public float b() {
      return this.g;
   }

   public void e(float f2) {
      this.g = f2;
      this.h = f2;
      this.i.d(f2);
   }

   public boolean c() {
      return this.j;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6, int i, int i2, boolean z) {
      if (f5 > f6) {
         this.u = f2;
         this.v = f3;
         this.w = f4;
         this.x = f5;
         this.y = f6;
         float f7 = this.g / (f5 - f6);
         float fMax = Math.max(this.q, f4 * (f6 / f5));
         float f8 = f3 + (f4 - fMax) * f7;
         float renderX = f2 + (2.0F - this.o);
         boolean zB = GuiInteractionState.a().b();
         boolean z2 = !z && !zB && GuiInput.a(renderX - this.p / 2.0F, f3, this.o + this.p, f4, i, i2);
         int i3;
         if (!this.j && !z2) {
            i3 = 0;
         } else {
            i3 = 1;
         }

         int i5 = i3;
         if (zB && this.n) {
            this.m.a(0.0, 0.15, Easing.h);
            this.n = false;
         } else if (!zB && Bool.from(i5) != this.n) {
            this.m.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
            this.n = Bool.from(i5);
         }

         this.m.a();
         renderer2D.a(renderX, f8, this.o, fMax, this.o / 2.0F, new Color(76, 72, 99), MatrixStackVar);
      }
   }

   public boolean a(float f2, float f3, float f4, float f5, float f6, int i, int i2) {
      float renderX = f2 + (2.0F - this.o);
      if (f5 > f6 && GuiInput.a(renderX - this.p / 2.0F, f3, this.o + this.p, f4, i, i2)) {
         this.j = true;
         this.k = i2;
         this.l = this.g;
         return true;
      } else {
         return false;
      }
   }

   public void d() {
      if (this.j) {
         this.j = false;
         this.h = this.g;
         this.i.d(this.g);
      }
   }

   public void a(int i, float f2, float f3) {
      if (this.j && !(f2 <= f3)) {
         float f4 = f2 - f3;
         float f5 = this.w;
         this.g = this.l + (i - this.k) / (f5 - Math.max(this.q, f5 * (f3 / f2))) * f4;
         float fMax = Math.max(0.0F, Math.min(this.g, f4));
         this.g = fMax;
         this.h = fMax;
         this.i.d(this.g);
      }
   }

   public void a(float f2, float f3, float f4) {
      if (!this.j && !(f3 <= f4)) {
         float fMax = Math.max(0.0F, f3 - f4);
         this.h = this.h - f2 * this.r;
         this.h = Math.max(0.0F, Math.min(this.h, fMax));
         this.i.a(this.h, 0.15, Easing.h);
      }
   }

   public void e() {
      this.g = 0.0F;
      this.h = 0.0F;
      this.i.d(0.0);
      this.j = false;
   }

   public void b(float f2, float f3) {
      if (f2 <= f3) {
         if (this.g > 0.0F || this.h > 0.0F) {
            this.h = 0.0F;
            this.i.a(0.0, 0.15, Easing.h);
         }
      } else {
         float f4 = f2 - f3;
         if (this.g > f4 || this.h > f4) {
            this.h = f4;
            this.i.a(f4, 0.15, Easing.h);
         }
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
