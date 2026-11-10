package pulse.gui.config;

import java.awt.Color;
import java.util.function.Consumer;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.client.MinecraftContext;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiInteractionState;
import pulse.gui.widgets.TextInput;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class ConfigNameDialog {
   private static final float c = 178.5F;
   private static final float d = 94.0F;
   private static final float e = 9.5F;
   private static final float f = 12.0F;
   private static final float g = 3.0F;
   private static final float h = 11.5F;
   private static final float i = 22.0F;
   private static final float j = 9.5F;
   private static final float k = 18.0F;
   private static final float l = 6.0F;
   private static final float m = 7.0F;
   private boolean n = false;
   private final AnimationState o = new AnimationState();
   private final AnimationState p = new AnimationState();
   private final AnimationState q = new AnimationState();
   private boolean r = false;
   private boolean s = false;
   private final TextInput t = new TextInput(TextInput.InputType.KEY, "", "XXXX-XXXX-XXXX");
   private Consumer<String> u;
   private Runnable v;
   public static int a;
   public static boolean b;

   public ConfigNameDialog() {
      this.t.a(9.5F);
      this.t.a(Theme.S, Theme.a);
      this.t.a(true);
      this.t.b(true);
   }

   public void a() {
      this.n = true;
      this.o.d(0.0);
      this.o.a(1.0, 0.25, Easing.F);
      this.t.b("");
      GuiInteractionState.a().e(true);
      GuiInput.a(0.0F, 0.0F, 10000.0F, 10000.0F);
   }

   public void b() {
      if (this.n) {
         this.o.a(0.0, 0.15, Easing.h);
         this.t.c(false);
         GuiInteractionState.a().e(false);
         GuiInput.a();
      }
   }

   public boolean c() {
      return this.n;
   }

   public boolean d() {
      return !this.n || this.o.j() < 0.01;
   }

   public void a(Consumer<String> consumer) {
      this.u = consumer;
   }

   public void a(Runnable runnable) {
      this.v = runnable;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3) {
      this.o.a();
      this.p.a();
      this.q.a();
      float fJ = (float)this.o.j();
      if (fJ < 0.01F) {
         if (this.n && this.o.d()) {
            this.n = false;
            if (this.v != null) {
               this.v.run();
            }
         }
      } else {
         int i4 = (int)(255.0F * fJ);
         float f4 = (f2 - 178.5F) / 2.0F;
         float f5 = (f3 - 94.0F) / 2.0F;
         float f6 = 0.9F + 0.1F * fJ;
         float f7 = 178.5F * f6;
         float f8 = 94.0F * f6;
         float f9 = f4 + (178.5F - f7) / 2.0F;
         float f10 = f5 + (94.0F - f8) / 2.0F;
         Color colorA = Theme.a(new Color(17, 17, 23, 204), i4);
         Color colorA2 = Theme.a(new Color(13, 13, 17, 204), i4);
         renderer2D.a(f9, f10, f7, f8, 9.5F * f6, colorA, colorA, colorA2, colorA2, MatrixStackVar);
         FontRenderer fontRenderer = FontManager.b[15];
         float fA = f9 + (f7 - fontRenderer.a("Активация конфига")) / 2.0F;
         float f11 = f10 + 3.0F * f6;
         fontRenderer.a("Активация конфига", fA, f11 + 5.0F, Theme.a(Theme.a, i4), MatrixStackVar);
         float f12 = f9 + 12.0F * f6;
         float fB = f11 + fontRenderer.b("Активация конфига") + 11.5F * f6;
         float f13 = f7 - 24.0F * f6;
         float f14 = 22.0F * f6;
         this.t.a(MatrixStackVar, renderer2D, f12, fB, f13, f14, i2, i3, fJ);
         float f15 = fB + f14 + 11.5F * f6;
         float f16 = (f13 - 7.0F * f6) / 2.0F;
         float f17 = f12 + f16 + 7.0F * f6;
         boolean zA = GuiInput.a(f12, f15, f16, 18.0F * f6, i2, i3);
         boolean zA2 = GuiInput.a(f17, f15, f16, 18.0F * f6, i2, i3);
         if (zA != this.r) {
            this.p.a(!zA ? 0.0 : 1.0, 0.15, Easing.h);
            this.r = zA;
         }

         if (zA2 != this.s) {
            this.q.a(!zA2 ? 0.0 : 1.0, 0.15, Easing.h);
            this.s = zA2;
         }

         this.a(MatrixStackVar, renderer2D, f12, f15, f16, 18.0F * f6, i4);
         this.b(MatrixStackVar, renderer2D, f17, f15, f16, 18.0F * f6, i4);
         if (zA || zA2) {
            GuiInput.g();
         }
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2) {
      float fJ = (float)this.p.j();
      Color colorA = ColorUtils.a(Theme.y, Theme.B, fJ);
      Color colorA2 = ColorUtils.a(Theme.z, Theme.y, fJ);
      Color colorA3 = Theme.a(colorA, i2);
      Color colorA4 = Theme.a(colorA2, i2);
      renderer2D.a(f2, f3, f4, f5, 6.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      FontRenderer fontRendererApply = FontManager.a[12];
      fontRendererApply.a(
         "Активировать",
         f2 + (f4 - fontRendererApply.a("Активировать")) / 2.0F,
         f3 + (f5 - fontRendererApply.b("Активировать")) / 2.0F + 3.5F,
         Theme.a(Theme.aa, i2),
         MatrixStackVar
      );
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2) {
      float fJ = (float)this.q.j();
      Color colorB = Theme.b(Theme.q, i2 / 255.0F);
      Color colorB2 = Theme.b(Theme.n, i2 / 255.0F);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 6.0F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = ColorUtils.a(Theme.e, Theme.I, fJ);
      Color colorA2 = ColorUtils.a(Theme.f, Theme.J, fJ);
      Color colorA3 = Theme.a(colorA, i2);
      Color colorA4 = Theme.a(colorA2, i2);
      renderer2D.a(f2, f3, f4, f5, 6.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      FontRenderer fontRendererCancel = FontManager.a[12];
      fontRendererCancel.a(
         "Отмена",
         f2 + (f4 - fontRendererCancel.a("Отмена")) / 2.0F,
         f3 + (f5 - fontRendererCancel.b("Отмена")) / 2.0F + 3.5F,
         Theme.a(ColorUtils.a(Theme.b, Theme.a, fJ), i2),
         MatrixStackVar
      );
   }

   public boolean a(float f2, float f3, int i2, int i3) {
      if (this.n && !(this.o.j() < 0.5)) {
         float fGetWidth = (MinecraftContext.getWidth() / 2.0F - 178.5F) / 2.0F;
         float fGetHeight = (MinecraftContext.getHeight() / 2.0F - 94.0F) / 2.0F;
         if (!GuiInput.a(fGetWidth, fGetHeight, 178.5F, 94.0F, i2, i3)) {
            this.b();
            return true;
         }

         if (this.t.a(i2, i3)) {
            return true;
         }

         FontRenderer fontRenderer = FontManager.b[15];
         float f4 = fGetWidth + 12.0F;
         float fB = fGetHeight + 3.0F + fontRenderer.b("Активация конфига") + 11.5F + 22.0F + 11.5F;
         float f5 = 73.75F;
         float f6 = f4 + f5 + 7.0F;
         if (GuiInput.a(f4, fB, f5, 18.0F, i2, i3)) {
            if (this.u != null) {
               this.u.accept(this.t.a());
            }

            this.b();
            return true;
         } else {
            if (!GuiInput.a(f6, fB, f5, 18.0F, i2, i3)) {
               return true;
            }

            this.b();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean a(int i2, int i3, int i4) {
      return this.n ? this.t.b(i2, i3, i4) : false;
   }

   public boolean a(char c2, int i2) {
      return this.n ? this.t.a(c2, i2) : false;
   }

   public boolean e() {
      return Bool.from(this.n && this.t.d() ? 1 : 0);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
