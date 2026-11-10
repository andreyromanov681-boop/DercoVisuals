package pulse.gui.config;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.config.ConfigEntry;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.widgets.ScrollBar;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class ConfigTextDialog {
   private static final float a = 178.5F;
   private static final float b = 9.5F;
   private static final float c = 12.0F;
   private static final float d = 11.0F;
   private static final float e = 10.0F;
   private static final float f = 22.0F;
   private static final float g = 3.0F;
   private static final float h = 9.5F;
   private static final float i = 18.0F;
   private static final float j = 6.0F;
   private static final float k = 7.0F;
   private static final float l = 2.0F;
   private static final int m = 5;
   private ConfigEntry q;
   private Runnable z;
   private boolean n = false;
   private String o = "";
   private String[] p = new String[0];
   private boolean r = false;
   private final ScrollBar s = new ScrollBar(2.0F, 20.0F);
   private final AnimationState t = new AnimationState();
   private final AnimationState u = new AnimationState();
   private final AnimationState v = new AnimationState();
   private final AnimationState w = new AnimationState();
   private boolean x = false;
   private boolean y = false;

   public void a(String str, ConfigEntry configEntry) {
      this.q = configEntry;
      String strA;
      if (configEntry != null) {
         strA = configEntry.a();
         if (strA == null) {
            strA = str;
         }
      } else {
         strA = str;
      }

      if (strA == null) {
         strA = "";
      }

      this.o = strA;
      String[] strArr = new String[]{str == null ? "" : str};
      this.p = strArr;
      this.n = true;
      this.s.e();
      this.t.a(true, 1.0);
      this.u.a(true, 1.0);
      this.v.a(true, 1.0);
      this.w.a(true, 1.0);
   }

   private float e() {
      return 41.0F + (Math.min(this.p.length, 5) * 25.0F - 3.0F) + 10.0F + 18.0F + 11.0F;
   }

   private float f() {
      return Math.min(this.p.length, 5) * 25.0F - 3.0F;
   }

   private float g() {
      return this.p.length * 25.0F - 3.0F;
   }

   private boolean h() {
      return Bool.from(this.p.length > 5 ? 1 : 0);
   }

   public void a() {
   }

   public boolean b() {
      return this.n;
   }

   public boolean c() {
      return Bool.from(this.n && !(this.t.j() < 0.01) ? 0 : 1);
   }

   public void a(Runnable runnable) {
      this.z = runnable;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3) {
      this.t.a();
      this.u.a();
      this.v.a();
      this.w.a();
      this.s.a();
      float fJ = (float)this.t.j();
      if (fJ < 0.01F) {
         if (this.n && this.t.d()) {
            this.n = false;
            if (this.z != null) {
               this.z.run();
            }
         }
      } else {
         int i4 = (int)(255.0F * fJ);
         float fE = this.e();
         float f4 = (f2 - 178.5F) / 2.0F;
         float f5 = (f3 - fE) / 2.0F;
         float f6 = 0.9F + 0.1F * fJ;
         float f7 = 178.5F * f6;
         float f8 = fE * f6;
         float f9 = f4 + (178.5F - f7) / 2.0F;
         float f10 = f5 + (fE - f8) / 2.0F;
         Color colorA = Theme.a(new Color(17, 17, 23, 204), i4);
         Color colorA2 = Theme.a(new Color(13, 13, 17, 204), i4);
         renderer2D.a(f9, f10, f7, f8, 9.5F * f6, colorA, colorA, colorA2, colorA2, MatrixStackVar);
         FontRenderer fontRenderer = FontManager.b[15];
         FontRenderer fontRenderer2 = FontManager.a[12];
         String str = this.p.length > 1 ? "Ключи успешно созданы" : "Ключ успешно создан";
         float fA = f9 + (f7 - fontRenderer.a(str)) / 2.0F;
         float f11 = f10 + 11.0F * f6;
         fontRenderer.a(str, fA, f11, Theme.a(Theme.a, i4), MatrixStackVar);
         float fB = f11 + fontRenderer.b(str) / 2.0F - 6.0F;
         if (this.q != null) {
            String strHeader = "Конфиг: " + this.q.a() + (this.p.length > 1 ? " (" + this.p.length + " шт.)" : "");
            fontRenderer2.a(strHeader, f9 + (f7 - fontRenderer2.a(strHeader)) / 2.0F, fB + 8.0F, Theme.a(Theme.b, i4), MatrixStackVar);
         }

         float f12 = f9 + 12.0F * f6;
         float f13 = f7 - 24.0F * f6;
         float fB2 = fB + fontRenderer2.b("A") + 10.0F * f6;
         float f14 = this.f() * f6;
         float fG = this.g() * f6;
         if (this.h()) {
            renderer2D.b().a(f12, fB2, f13 + 10.0F, f14, MatrixStackVar);
         }

         float fB3 = this.s.b();

         for (int i5 = 0; i5 < this.p.length; i5++) {
            float f15 = fB2 + i5 * 25.0F * f6 - fB3;
            if (f15 + 22.0F * f6 >= fB2 && f15 <= fB2 + f14) {
               this.a(MatrixStackVar, renderer2D, f12, f15, f13, 22.0F * f6, i4, this.p[i5]);
            }
         }

         if (this.h()) {
            renderer2D.b().a(MatrixStackVar);
            this.s.a(MatrixStackVar, renderer2D, f9 + f7 - 12.0F * f6 / 2.0F - 2.0F, fB2, f14, fG, f14, i2, i3, false);
         }

         float f16 = fB2 + f14 + 10.0F * f6;
         float f17 = (f13 - 7.0F * f6) / 2.0F;
         float f18 = f12 + f17 + 7.0F * f6;
         boolean zA = GuiInput.a(f12, f16, f17, 18.0F * f6, i2, i3);
         boolean zA2 = GuiInput.a(f18, f16, f17, 18.0F * f6, i2, i3);
         if (zA != this.x) {
            this.u.a(zA ? 1.0 : 0.0, 0.15, Easing.h);
            this.x = zA;
         }

         if (zA2 != this.y) {
            this.v.a(zA2 ? 1.0 : 0.0, 0.15, Easing.h);
            this.y = zA2;
         }

         this.a(MatrixStackVar, renderer2D, f12, f16, f17, 18.0F * f6, i4);
         this.b(MatrixStackVar, renderer2D, f18, f16, f17, 18.0F * f6, i4);
         if (zA || zA2) {
            GuiInput.g();
         }
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, String str) {
      Color colorB = Theme.b(Theme.q, i2 / 255.0F);
      Color colorB2 = Theme.b(Theme.n, i2 / 255.0F);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 9.5F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = Theme.a(new Color(12, 12, 20, 153), i2);
      Color colorA2 = Theme.a(new Color(13, 13, 16, 153), i2);
      renderer2D.a(f2, f3, f4, f5, 9.5F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      FontRenderer fontRenderer = FontManager.a[12];
      float fB = f3 + (f5 - fontRenderer.b(str)) / 2.0F + 3.5F;
      String strSubstring = str;
      if (fontRenderer.a(strSubstring) > f4 - 16.0F) {
         while (fontRenderer.a(strSubstring + "...") > f4 - 16.0F && strSubstring.length() > 0) {
            int length = strSubstring.length();
            strSubstring = strSubstring.substring(0, 2 * (length & -2) - (length ^ 1));
         }

         strSubstring = strSubstring + "...";
      }

      fontRenderer.a(strSubstring, f2 + (f4 - fontRenderer.a(strSubstring)) / 2.0F, fB, Theme.a(Theme.a, i2), MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2) {
      float fJ = (float)this.u.j();
      float fJ2 = (float)this.w.j();
      Color colorA = ColorUtils.a(Theme.y, new Color(34, 197, 94), fJ2);
      Color colorA2 = ColorUtils.a(Theme.z, new Color(22, 163, 74), fJ2);
      Color colorA3 = ColorUtils.a(Theme.B, new Color(74, 222, 128), fJ2);
      Color colorA4 = ColorUtils.a(Theme.y, new Color(34, 197, 94), fJ2);
      Color colorA5 = ColorUtils.a(colorA, colorA3, fJ);
      Color colorA6 = ColorUtils.a(colorA2, colorA4, fJ);
      Color colorA7 = Theme.a(colorA5, i2);
      Color colorA8 = Theme.a(colorA6, i2);
      renderer2D.a(f2, f3, f4, f5, 6.0F, colorA7, colorA7, colorA8, colorA8, MatrixStackVar);
      FontRenderer fontRenderer = FontManager.a[12];
      String strCopy = this.r ? "Скопировано" : "Копировать";
      fontRenderer.a(
         strCopy, f2 + (f4 - fontRenderer.a(strCopy)) / 2.0F, f3 + (f5 - fontRenderer.b(strCopy)) / 2.0F + 3.5F, Theme.a(Theme.aa, i2), MatrixStackVar
      );
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2) {
      float fJ = (float)this.v.j();
      Color colorB = Theme.b(Theme.q, i2 / 255.0F);
      Color colorB2 = Theme.b(Theme.n, i2 / 255.0F);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 6.0F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = ColorUtils.a(Theme.e, Theme.I, fJ);
      Color colorA2 = ColorUtils.a(Theme.f, Theme.J, fJ);
      Color colorA3 = Theme.a(colorA, i2);
      Color colorA4 = Theme.a(colorA2, i2);
      renderer2D.a(f2, f3, f4, f5, 6.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      FontRenderer fontRendererClose = FontManager.a[12];
      fontRendererClose.a(
         "Закрыть",
         f2 + (f4 - fontRendererClose.a("Закрыть")) / 2.0F,
         f3 + (f5 - fontRendererClose.b("Закрыть")) / 2.0F + 3.5F,
         Theme.a(ColorUtils.a(Theme.b, Theme.a, fJ), i2),
         MatrixStackVar
      );
   }

   public boolean a(float f2, float f3, int i2, int i3) {
      return this.n;
   }

   public void a(float f2, int i2, int i3) {
      if (this.n && this.h()) {
         float f3 = this.f();
         this.s.a(f2, this.g(), f3);
      }
   }

   public void a(int i2, int i3) {
      this.s.d();
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (this.s.c()) {
         float f2 = this.f();
         this.s.a(i3, this.g(), f2);
      }
   }

   public boolean a(int i2, int i3, int i4) {
      if (!this.n) {
         return false;
      }

      if (i2 == 259) {
         if (this.o == null) {
            this.o = "";
            return true;
         }

         if (this.o.isEmpty()) {
            return true;
         }

         this.o = this.o.substring(0, this.o.length() - 1);
         if (this.q == null) {
            return true;
         }

         this.q.a(this.o);
         return true;
      } else if (i2 != 257 && i2 != 335) {
         if (i2 != 256) {
            return true;
         }

         this.n = false;
         return true;
      } else {
         if (this.q != null) {
            this.q.a(this.o);
         }

         this.n = false;
         if (this.z == null) {
            return true;
         }

         this.z.run();
         return true;
      }
   }

   public boolean a(char c2, int i2) {
      if (!this.n) {
         return false;
      }

      if (c2 < ' ') {
         return true;
      }

      this.o = (this.o == null ? "" : this.o) + c2;
      if (this.q == null) {
         return true;
      }

      this.q.a(this.o);
      return true;
   }

   public boolean d() {
      return false;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
