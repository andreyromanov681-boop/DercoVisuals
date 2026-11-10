// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.settings;

import java.awt.Color;
import java.util.Locale;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.settings.SliderSetting;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.MarqueeText;

public class SliderSettingWidget implements SettingWidget {
   public static final float a = 28.0F;
   private static final float d = 8.0F;
   private static final float e = 6.0F;
   private static final float f = 20.0F;
   private static final float g = 3.0F;
   private static final float h = 7.0F;
   private static final float i = 8.0F;
   private static final Color j = Theme.P;
   private static final Color k = Theme.Q;
   private static final Color l = Theme.R;
   private final String m;
   private float n;
   private final float o;
   private final float p;
   private final float q;
   private final SliderSetting r;
   private final AnimationState s = new AnimationState();
   private final AnimationState t = new AnimationState();
   private final AnimationState u = new AnimationState();
   private final AnimationState v = new AnimationState();
   private final AnimationState w = new AnimationState();
   private final AnimationState x = new AnimationState();
   private final MarqueeText y = new MarqueeText();
   private boolean z = false;
   private boolean A = false;
   private float B;
   private float C = 0.0F;
   private float D = 0.0F;
   private boolean E = false;
   private float F;
   private final AnimationState G = new AnimationState();
   private final AnimationState H = new AnimationState();
   public static int b;
   public static boolean c;

   public SliderSettingWidget(String str, float f2, float f3, float f4, float f5) {
      this(null, str, f2, f3, f4, f5);
   }

   public SliderSettingWidget(SliderSetting sliderSetting) {
      this(sliderSetting, sliderSetting.f(), sliderSetting.a(), sliderSetting.c(), sliderSetting.d(), sliderSetting.e());
   }

   private SliderSettingWidget(SliderSetting sliderSetting, String str, float f2, float f3, float f4, float f5) {
      this.r = sliderSetting;
      this.m = str;
      this.n = f2;
      this.o = f3;
      this.p = f4;
      this.q = f5;
      this.B = f2;
      this.F = f2;
      this.w.d(this.i());
      this.G.d(this.i());
      this.H.d(f2);
      this.u.d(0.0);
      this.v.d(0.0);
      this.x.d(0.0);
   }

   public void a(boolean z) {
      this.E = z;
   }

   public boolean c() {
      return this.E;
   }

   @Override
   public String a() {
      return this.m;
   }

   @Override
   public float b() {
      return 28.0F;
   }

   public float e() {
      if (this.r != null && !this.A) {
         float fA = this.r.a();
         if (Math.abs(this.n - fA) > 0.001F) {
            this.n = fA;
            this.F = this.n;
            if (this.E) {
               this.H.d(this.n);
            }

            this.w.d(this.i());
            this.G.d(this.i());
            this.B = this.n;
         }
      }

      return !this.E ? this.n : (float)this.H.j();
   }

   public void a(float f2) {
      this.n = Math.max(this.o, Math.min(this.p, f2));
      if (this.r != null) {
         this.r.a(this.n);
      }
   }

   public float f() {
      return this.o;
   }

   public float g() {
      return this.p;
   }

   public float h() {
      return this.q;
   }

   private float i() {
      return this.p == this.o ? 0.0F : ((this.E && this.A ? this.F : this.n) - this.o) / (this.p - this.o);
   }

   private float m() {
      return this.E && this.A ? this.F : this.n;
   }

   private String b(float f2) {
      return this.q < 1.0F ? (this.q < 0.1F ? String.format(Locale.US, "%.2f", f2) : String.format(Locale.US, "%.1f", f2)) : String.valueOf((int)f2);
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5, float f6) {
      if (this.r != null && !this.A && Math.abs(this.n - this.r.a()) > 0.001F) {
         this.n = this.r.a();
         this.F = this.n;
         this.w.d(this.i());
         this.G.d(this.i());
         this.H.d(this.n);
         this.B = this.n;
      }

      FontRenderer fontRenderer = FontManager.b[14];
      float f7 = 8.0F * f6;
      boolean zA = GuiInput.a(f2, f3, f4, 28.0F * f6, i2, i3);
      this.y.a(zA);
      if (zA != this.z) {
         this.s.a(!zA ? 0.0 : 1.0, 0.15, Easing.h);
         this.z = zA;
      }

      if (this.n != this.B) {
         this.w.a(this.i(), 0.15, Easing.C);
         this.x.d(1.0);
         this.x.a(0.0, 0.4, Easing.h);
         this.B = this.n;
      }

      this.s.a();
      this.t.a();
      this.u.a();
      this.v.a();
      this.w.a();
      this.x.a();
      this.H.a();
      this.G.a();
      float fJ2 = (float)this.s.j();
      float fJ3 = (float)this.t.j();
      float fJ4 = (float)this.u.j();
      float fJ;
      if (this.E && this.A) {
         this.G.a(this.i(), 0.1, Easing.h);
         fJ = (float)this.G.j();
      } else {
         fJ = (float)this.w.j();
         this.G.d(fJ);
      }

      int i4 = (int)(255.0F * f5);
      String strB = this.b(this.m());
      float fA = fontRenderer.a(strB) * f6;
      float f8 = fA + 5.0F * f6 * 2.0F;
      float f9 = f2 + f4 - f7 - f8;
      float f10 = f9 - (f2 + f7) - 4.0F * f6;
      float f11 = f3 + 6.0F * f6;
      this.y.a(MatrixStackVar, renderer2D, fontRenderer, this.m, (int)(f2 + f7), (int)f11 - 1, f10, f6, Color.WHITE, f5);
      float fB = 9.0F * f6;
      float f12 = 14.0F * f6;
      float fB2 = f11 + 9.0F * f6 / 2.0F - f12 / 2.0F - 5.0F * f6;
      float f13 = 4.0F * f6;
      Color valueBg = new Color(26, 23, 36, 230);
      renderer2D.a(f9, fB2, f8, f12, f13, valueBg, valueBg, valueBg, valueBg, MatrixStackVar);
      float f14 = f9 + f8 / 2.0F;
      float f15 = fB2 + f12 / 2.0F;
      float fRound = Math.round(f14 - fA / 2.0F);
      float fRound2 = Math.round(f15 - fB / 2.0F);
      Color valueTextColor = new Color(200, 195, 220);
      fontRenderer.a(strB, fRound, fRound2 + 1.0F, valueTextColor, MatrixStackVar);
      float f16 = f3 + 20.0F * f6;
      float f17 = f4 - f7 * 2.0F;
      float f18 = 3.0F * f6;
      float f19 = f18 / 2.0F;
      float f20 = f2 + f7;
      this.C = f20;
      this.D = f17;
      Color trackBg = new Color(34, 30, 48, 220);
      renderer2D.a(f20, f16, f17, f18, f19, trackBg, MatrixStackVar);
      float f21 = f17 * fJ;
      if (f21 > 0.0F) {
         Color purpleFill = new Color(139, 92, 246);
         if (f21 < f19 * 2.0F) {
            renderer2D.a(f20, f16, f21, f18, f21 / 2.0F, purpleFill, purpleFill, purpleFill, purpleFill, MatrixStackVar);
         } else {
            renderer2D.a(f20, f16, f21, f18, f19, purpleFill, purpleFill, purpleFill, purpleFill, MatrixStackVar);
         }
      }

      float f22 = (7.0F + 1.0F * fJ4) * f6;
      renderer2D.a(
         Math.max(f20 - f22 / 2.0F, Math.min(f20 + f21 - f22 / 2.0F, f20 + f17 - f22 / 2.0F)),
         f16 + f18 / 2.0F - f22 / 2.0F,
         f22,
         f22,
         f22,
         Color.WHITE,
         MatrixStackVar
      );
      if (GuiInput.a(f20 - f22 / 2.0F, f16 - 5.0F * f6, f17 + f22, f18 + 10.0F * f6, i2, i3) || this.A) {
         GuiInput.g();
      }
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      float f5 = f4 - 16.0F;
      float f6 = f2 + 8.0F;
      this.C = f6;
      this.D = f5;
      if (!GuiInput.a(f6 - 3.5F, f3 + 20.0F - 5.0F, f5 + 7.0F, 13.0F, i2, i3)) {
         return false;
      }

      this.A = true;
      if (this.E) {
         this.F = this.n;
      }

      this.t.a(1.0, 0.1, Easing.h);
      this.u.a(1.0, 0.15, Easing.F);
      this.v.a(1.0, 0.2, Easing.h);
      this.a(f6, f5, i2);
      return true;
   }

   @Override
   public void a(int i2, int i3) {
      if (this.A) {
         if (this.E) {
            this.n = this.F;
            if (this.r != null) {
               this.r.a(this.n);
            }

            this.w.d(this.G.j());
            this.B = this.n;
            this.H.a(this.n, 0.2, Easing.C);
         }

         this.A = false;
         this.t.a(0.0, 0.2, Easing.h);
         this.u.a(0.0, 0.25, Easing.F);
         this.v.a(0.0, 0.3, Easing.h);
      }
   }

   @Override
   public void a(int i2, int i3, double d2, double d3) {
      if (this.A) {
         if (this.D > 0.0F) {
            this.a(this.C, this.D, i2);
         } else if (c) {
         }
      }
   }

   private void a(float f2, float f3, int i2) {
      float fMax = Math.max(
         this.o, Math.min(this.p, Math.round((this.o + (this.p - this.o) * Math.max(0.0F, Math.min(1.0F, (i2 - f2) / f3))) / this.q) * this.q)
      );
      if (this.E) {
         this.F = fMax;
      } else {
         this.n = fMax;
         if (this.r != null) {
            this.r.a(fMax);
         }
      }
   }

   @Override
   public boolean d() {
      int i2;
      if (this.r != null && !this.r.m()) {
         i2 = 0;
      } else {
         i2 = 1;
      }

      return Bool.from(i2);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
