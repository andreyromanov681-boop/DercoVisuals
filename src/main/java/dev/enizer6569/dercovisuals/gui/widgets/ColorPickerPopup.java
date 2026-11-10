// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.widgets;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;
import dev.enizer6569.dercovisuals.theme.Theme;

public class ColorPickerPopup {
   private static final ColorPickerPopup c = new ColorPickerPopup();
   private static final float d = 84.5F;
   private static final float e = 10.5F;
   private static final float f = 7.0F;
   private static final float g = 9.5F;
   private static final float h = 63.5F;
   private static final float i = 41.5F;
   private static final float j = 7.0F;
   private static final float k = 4.0F;
   private static final float l = 2.0F;
   private static final float m = 9.0F;
   private static final float n = 1.5F;
   private static final float o = 3.0F;
   private static final float p = 7.0F;
   private static final float q = 1.0F;
   private float t;
   private float u;
   private float y;
   private float z;
   private float A;
   private Consumer<Color> B;
   private float E;
   private float F;
   private float G;
   private float H;
   private float I;
   private float J;
   private float K;
   private float L;
   private float M;
   private float N;
   private float O;
   private float P;
   private float Q;
   private float R;
   private float S;
   public static int a;
   public static boolean b;
   private boolean r = false;
   private final AnimationState s = new AnimationState();
   private ColorPickerPopup.Edge v = ColorPickerPopup.Edge.BOTTOM;
   private String w = "";
   private float x = 1.0F;
   private boolean C = false;
   private boolean D = false;
   private String[] modes;
   private int modeIdx = -1;
   private IntConsumer modeCB;
   private float modesY;
   private float modesH;
   private static final float MODE_ROW_H = 13.0F;
   private static final float MODE_GAP = 4.0F;

   private ColorPickerPopup() {
   }

   public static ColorPickerPopup a() {
      return c;
   }

   public void a(float f2, float f3, ColorPickerPopup.Edge edge, String str, Color color, float f4, Consumer<Color> consumer) {
      this.a(f2, f3, edge, str, color, f4, consumer, null, -1, null);
   }

   public void a(
      float f2,
      float f3,
      ColorPickerPopup.Edge edge,
      String str,
      Color color,
      float f4,
      Consumer<Color> consumer,
      String[] strArr,
      int i2,
      IntConsumer intConsumer
   ) {
      if (this.r) {
         this.b();
      }

      this.t = f2;
      this.u = f3;
      this.v = edge != null ? edge : ColorPickerPopup.Edge.BOTTOM;
      this.w = str != null ? str : "";
      this.x = f4;
      this.B = consumer;
      this.modes = strArr;
      this.modeIdx = i2;
      this.modeCB = intConsumer;
      float[] fArrRGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      this.y = fArrRGBtoHSB[0];
      this.z = fArrRGBtoHSB[1];
      this.A = fArrRGBtoHSB[2];
      this.r = true;
      this.s.d(0.0);
      this.s.a(1.0, 0.2, Easing.h);
   }

   public void a(float f2, float f3, String str, Color color, float f4, Consumer<Color> consumer) {
      this.a(f2, f3, ColorPickerPopup.Edge.BOTTOM, str, color, f4, consumer);
   }

   public void b() {
      if (this.r) {
         this.r = false;
         this.s.a(0.0, 0.15, Easing.h);
         this.C = false;
         this.D = false;
         this.modes = null;
         this.modeIdx = -1;
         this.modeCB = null;
      }
   }

   public boolean c() {
      return this.r || this.s.j() > 0.01;
   }

   public boolean d() {
      return this.r && !(this.s.j() <= 0.5);
   }

   public Color e() {
      return Color.getHSBColor(this.y, this.z, this.A);
   }

   public float[] f() {
      if (!this.c()) {
         return null;
      }

      float fJ = (float)this.s.j();
      float f2 = this.G * fJ;
      float f3 = this.H * fJ;
      return new float[]{this.I - f2 / 2.0F, this.J - f3 / 2.0F, f2, f3};
   }

   private float a(float f2, float f3, float f4) {
      return f3 + (f2 - f3) * f4;
   }

   private float a(float f2, float f3) {
      return f2 * f3;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i2, int i3, float f2) {
      this.s.a();
      float fJ = (float)this.s.j();
      this.S = fJ;
      if (fJ >= 0.01F) {
         FontRenderer fontRenderer = FontManager.a[14];
         float f3 = 84.5F * this.x;
         float f4 = 10.5F * this.x;
         float f5 = 7.0F * this.x;
         float f6 = 9.5F * this.x;
         float f7 = 63.5F * this.x;
         float f8 = 41.5F * this.x;
         float f9 = 4.0F * this.x;
         float fB = fontRenderer.b(this.w) * this.x + 2.0F * this.x;
         this.modesH = this.modes != null && this.modes.length != 0 ? this.modes.length * 13.0F * this.x + 4.0F * this.x : 0.0F;
         float f10 = fB + this.modesH + f8 + f5 + f9 + f4 * 2.0F;
         this.G = f3;
         this.H = f10;
         this.b(f3, f10);
         this.I = this.E + f3 / 2.0F;
         this.J = this.F + f10 / 2.0F;
         float f11Final = this.E + f4;
         float f12Final = this.F + f4;
         float f14Final = f12Final + fB + this.modesH;
         float f15Final = f14Final + f8 + f5;
         this.K = f11Final;
         this.L = f14Final;
         this.M = f7;
         this.N = f8;
         this.O = f11Final;
         this.P = f15Final;
         this.Q = f7;
         this.R = f9;
         float fA = this.a(this.E, this.I, fJ);
         float fA2 = this.a(this.F, this.J, fJ);
         float fA3 = this.a(f3, fJ);
         float fA4 = this.a(f10, fJ);
         float fA5 = this.a(f6, fJ);
         this.a(f4, fJ);
         this.a(f5, fJ);
         this.a(MatrixStackVar, renderer2D, fJ, f2);
         Color colorA = this.a(Theme.x, f2 * fJ);
         Color colorA2 = this.a(Theme.x, f2 * fJ);
         renderer2D.a(
            fA - 0.5F * this.x * fJ, fA2 - 0.5F * this.x * fJ, fA3 + this.x * fJ, fA4 + this.x * fJ, fA5, colorA, colorA, colorA2, colorA2, MatrixStackVar
         );
         Color colorA3 = this.a(Theme.e, f2 * fJ);
         Color colorA4 = this.a(Theme.f, f2 * fJ);
         renderer2D.a(fA, fA2, fA3, fA4, fA5, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
         if (fJ > 0.3F) {
            float fMin = Math.min(1.0F, (fJ - 0.3F) / 0.7F);
            int i4 = (int)(255.0F * f2 * fMin);
            renderer2D.b().a(fA, fA2, fA3, fA4, fA5, MatrixStackVar);
            float f11 = this.E + f4;
            float f12 = this.F + f4;
            float fA6 = this.a(f11, this.I, fJ);
            float fA7 = this.a(f12, this.J, fJ);
            this.a(fB, fJ);
            float f13 = this.x * fJ;
            Color colorA5 = Theme.a(Theme.a, i4);
            MatrixStackVar.pushMatrix();
            MatrixStackVar.translate(fA6, fA7);
            MatrixStackVar.scale(f13, f13);
            MatrixStackVar.translate(-fA6, -fA7);
            fontRenderer.a(this.w, fA6, fA7, colorA5, MatrixStackVar);
            MatrixStackVar.popMatrix();
            float f14 = f12 + fB + this.modesH;
            this.modesY = f12 + fB;
            if (this.modes != null && this.modes.length > 0 && fMin > 0.01F) {
               this.drawModes(MatrixStackVar, renderer2D, fontRenderer, f11, this.I, this.modesY, this.J, fJ, fMin * f2, f4, i4);
            }

            float Kdraw = this.a(f11, this.I, fJ);
            float Ldraw = this.a(f14, this.J, fJ);
            float Mdraw = this.a(f7, fJ);
            float Ndraw = this.a(f8, fJ);
            this.a(MatrixStackVar, renderer2D, Kdraw, Ldraw, Mdraw, Ndraw, this.a(7.0F * this.x, fJ), fMin * f2);
            this.a(MatrixStackVar, renderer2D, this.a(f11 + this.z * f7, this.I, fJ), this.a(f14 + (1.0F - this.A) * f8, this.J, fJ), fJ, fMin * f2);
            float f15 = f14 + f8 + f5;
            float Odraw = this.a(f11, this.I, fJ);
            float Pdraw = this.a(f15, this.J, fJ);
            float Qdraw = this.a(f7, fJ);
            float Rdraw = this.a(f9, fJ);
            this.b(MatrixStackVar, renderer2D, Odraw, Pdraw, Qdraw, Rdraw, this.a(2.0F * this.x, fJ), fMin * f2);
            this.b(MatrixStackVar, renderer2D, this.a(f11 + this.y * f7, this.I, fJ), this.a(f15 + f9 / 2.0F, this.J, fJ), fJ, fMin * f2);
            renderer2D.b().a(MatrixStackVar);
         }

         if (!this.r) {
            if (1703664633 < a) {
               return;
            }

            return;
         }

         if (fJ > 0.8F) {
            boolean zA = GuiInput.a(this.K, this.L, this.M, this.N, i2, i3);
            boolean zA2 = GuiInput.a(this.O, this.P - 4.0F * this.x * fJ, this.Q, this.R + 8.0F * this.x * fJ, i2, i3);
            if (zA || zA2 || this.C || this.D) {
               GuiInput.g();
            }
         }
      }
   }

   private void b(float f2, float f3) {
      IconTextureRegistry.TextureInfo arrowInfo = IconTextureRegistry.getInfo("arrow_v");
      float fB = arrowInfo != null ? arrowInfo.b() / 2.0F * this.x : 0.0F;
      switch (this.v) {
         case TOP:
            this.E = this.t - f2 / 2.0F;
            this.F = this.u - f3 - fB - 1.0F * this.x;
            break;
         case BOTTOM:
            this.E = this.t - f2 / 2.0F;
            this.F = this.u + fB + 1.0F * this.x;
            break;
         case LEFT:
            this.E = this.t - f2 - fB - 1.0F * this.x;
            this.F = this.u - f3 / 2.0F;
            break;
         case RIGHT:
            this.E = this.t + fB + 1.0F * this.x;
            this.F = this.u - f3 / 2.0F;
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3) {
      IconTextureRegistry.TextureInfo info = IconTextureRegistry.getInfo("arrow_v");
      if (info != null) {
         Identifier IdentifierVarA = info.a();
         RenderSystemHelper.setShaderTexture(0, IdentifierVarA);
         float fB = info.b() / 2.0F * this.x * f2;
         float fC = info.c() / 2.0F * this.x * f2;
         Color colorA = Theme.a(Theme.aa, f3 * f2);
         float fA = this.a(this.E, this.I, f2);
         float fA2 = this.a(this.F, this.J, f2) + 2.5F;
         float fA3 = this.a(this.G, f2);
         float fA4 = this.a(this.H, f2);
         float f7 = 1.0F * this.x * f2;
         float f4;
         float f5;
         switch (this.v) {
            case TOP: {
               f4 = fA + fA3 / 2.0F;
               f5 = fA2 + fA4 + fC / 2.0F + f7;
               float f6 = 90.0F;
               break;
            }
            case BOTTOM: {
               f4 = fA + fA3 / 2.0F;
               f5 = fA2 - fC / 2.0F - f7;
               float f6 = -90.0F;
               break;
            }
            case LEFT: {
               f4 = fA + fA3 + fB / 2.0F + f7;
               f5 = fA2 + fA4 / 2.0F;
               float f6 = 0.0F;
               break;
            }
            default: {
               f4 = fA - fB / 2.0F - f7;
               f5 = fA2 + fA4 / 2.0F;
               float f6 = 180.0F;
            }
         }

         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(f4, f5);
         MatrixStackVar.translate(-f4, -f5);
         renderer2D.a(IdentifierVarA, f4 - fB / 2.0F, f5 - fC / 2.0F, fB, fC, colorA, MatrixStackVar);
         MatrixStackVar.popMatrix();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6, float f7) {
      int alpha = (int)(255.0F * f7);
      int steps = (int)Math.max(1.0F, f4);

      for (int i = 0; i < steps; i++) {
         float sat = (float)i / steps;
         Color col = Color.getHSBColor(this.y, sat, 1.0F);
         Color colWithAlpha = new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
         float x1 = f2 + i;
         float x2 = f2 + i + 1.0F;
         if (i == steps - 1) {
            x2 = f2 + f4;
         }

         renderer2D.a(x1, f3, x2 - x1, f5, colWithAlpha, MatrixStackVar);
      }

      if (Renderer2DImpl.currentDrawContext != null) {
         Renderer2DImpl.currentDrawContext.fillGradient((int)f2, (int)f3, (int)(f2 + f4), (int)(f3 + f5), 0, alpha << 24);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5) {
      float f6 = 9.0F * this.x * f4;
      float f7 = f6 - 1.5F * this.x * f4 * 2.0F;
      int i2 = (int)(255.0F * f5);
      renderer2D.a(f2 - f7 / 2.0F - 2.0F, f3 - f7 / 2.0F - 2.0F, 5.0F, Theme.ad, MatrixStackVar);
      renderer2D.a(f2 - f6 / 2.0F, f3 - f6 / 2.0F, f6, f6, f6, Theme.a(Theme.aa, i2), MatrixStackVar);
      renderer2D.a(f2 - f7 / 2.0F, f3 - f7 / 2.0F, f7, f7, f7, Theme.a(this.e(), i2), MatrixStackVar);
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6, float f7) {
      renderer2D.a(f2, f3, f4, f5, f6, f7, MatrixStackVar);
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5) {
      float f6 = 3.0F * this.x * f4;
      float f7 = 7.0F * this.x * f4;
      renderer2D.a(f2 - f6 / 2.0F, f3 - f7 / 2.0F, f6, f7, f6, Theme.a(Theme.aa, f5), MatrixStackVar);
   }

   private void drawModes(
      Matrix3x2fStack ms,
      Renderer2D r2d,
      FontRenderer fr,
      float startX,
      float centerX,
      float startY,
      float centerY,
      float anim,
      float alpha,
      float pad,
      int alphaInt255
   ) {
      float rowHOrig = 13.0F * this.x;
      float rowWOrig = 84.5F * this.x - pad * 2.0F;
      float radiusOrig = 3.5F * this.x;

      for (int mi = 0; mi < this.modes.length; mi++) {
         float rowYOrig = startY + mi * rowHOrig;
         float fAx = this.a(startX, centerX, anim);
         float fAy = this.a(rowYOrig, centerY, anim);
         float wScaled = this.a(rowWOrig, anim);
         float hScaled = this.a(rowHOrig - 1.5F, anim);
         float radiusScaled = this.a(radiusOrig, anim);
         boolean sel = mi == this.modeIdx;
         Color bg1 = Theme.a(sel ? Theme.C : Theme.q, alphaInt255);
         Color bg2 = Theme.a(sel ? Theme.D : Theme.r, alphaInt255);
         r2d.a(fAx, fAy, wScaled, hScaled, radiusScaled, bg1, bg1, bg2, bg2, ms);
         Color txc = Theme.a(sel ? Theme.aa : Theme.b, alphaInt255);
         String label = this.modes[mi];
         float labelHalfH = fr.b(label) * this.x * anim / 2.0F;
         float tx = Math.round(fAx + 5.0F * anim);
         float ty = Math.round(fAy + (hScaled / 2.0F - labelHalfH));
         float scale = this.x * anim;
         ms.pushMatrix();
         ms.translate(tx, ty);
         ms.scale(scale, scale);
         ms.translate(-tx, -ty);
         fr.a(label, tx, ty + 4.0F, txc, ms);
         ms.popMatrix();
      }
   }

   public boolean a(int i2, int i3) {
      if (!this.r) {
         return false;
      }

      if (this.modes != null && this.modes.length > 0) {
         float rowH = 13.0F * this.x;
         float rowW = this.G - 4.0F * this.x * 2.0F;

         for (int mi = 0; mi < this.modes.length; mi++) {
            float rowY = this.modesY + mi * rowH;
            if (GuiInput.a(this.E + 4.0F * this.x, rowY, rowW, rowH - 1.5F, i2, i3)) {
               this.modeIdx = mi;
               if (this.modeCB != null) {
                  this.modeCB.accept(mi);
               }

               return true;
            }
         }
      }

      if (GuiInput.a(this.K, this.L, this.M, this.N, i2, i3)) {
         this.C = true;
         this.d(i2, i3);
         return true;
      }

      float f2 = 4.0F * this.x * this.S;
      if (!GuiInput.a(this.O, this.P - f2, this.Q, this.R + f2 * 2.0F, i2, i3)) {
         return this.c(i2, i3);
      }

      this.D = true;
      this.a(i2);
      return true;
   }

   public void b(int i2, int i3) {
      this.C = false;
      this.D = false;
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (this.r) {
         if (this.C) {
            this.d(i2, i3);
         }

         if (this.D) {
            this.a(i2);
         }
      }
   }

   private void d(int i2, int i3) {
      float f2 = i2 - this.K;
      float f3 = i3 - this.L;
      this.z = Math.max(0.0F, Math.min(1.0F, f2 / this.M));
      this.A = Math.max(0.0F, Math.min(1.0F, 1.0F - f3 / this.N));
      this.g();
   }

   private void a(int i2) {
      this.y = Math.max(0.0F, Math.min(1.0F, (i2 - this.O) / this.Q));
      this.g();
   }

   private void g() {
      if (this.B != null) {
         this.B.accept(this.e());
      }
   }

   public boolean c(int i2, int i3) {
      if (!this.c()) {
         return false;
      }

      float fJ = (float)this.s.j();
      float f2 = this.G * fJ;
      float f3 = this.H * fJ;
      return GuiInput.a(this.I - f2 / 2.0F, this.J - f3 / 2.0F, f2, f3, i2, i3);
   }

   private Color a(Color color, float f2) {
      return Theme.b(color, f2);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   public enum Edge {
      TOP,
      BOTTOM,
      LEFT,
      RIGHT;

      public static int e;
      public static boolean f;

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }
}
