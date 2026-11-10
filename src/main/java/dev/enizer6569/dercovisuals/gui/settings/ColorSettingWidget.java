// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.settings;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.widgets.ColorPickerPopup;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.settings.ColorSetting;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.MarqueeText;

public class ColorSettingWidget implements SettingWidget {
   public static final float a = 20.0F;
   private static final float d = 8.0F;
   private static final float e = 14.0F;
   private static final float f = 6.0F;
   private static final float g = 11.0F;
   private static final float h = 4.0F;
   private static final Color i = Theme.V;
   private final String j;
   private float k;
   private float l;
   private float m;
   private final ColorSetting n;
   private final AnimationState o = new AnimationState();
   private final MarqueeText p = new MarqueeText();
   private boolean q = false;
   private float r;
   private float s;
   private float t = 1.0F;
   private boolean u = false;
   public static int b;
   public static boolean c;

   public ColorSettingWidget(ColorSetting colorSetting) {
      this.n = colorSetting;
      this.j = colorSetting.f();
      this.k = colorSetting.c();
      this.l = colorSetting.d();
      this.m = colorSetting.e();
   }

   public ColorSettingWidget(String str, Color color) {
      this.n = null;
      this.j = str;
      float[] fArrRGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      this.k = fArrRGBtoHSB[0];
      this.l = fArrRGBtoHSB[1];
      this.m = fArrRGBtoHSB[2];
   }

   public ColorSettingWidget(String str, float f2, float f3, float f4) {
      this.n = null;
      this.j = str;
      this.k = f2;
      this.l = f3;
      this.m = f4;
   }

   @Override
   public String a() {
      return this.j;
   }

   @Override
   public float b() {
      return 20.0F;
   }

   public Color c() {
      return Color.getHSBColor(this.k, this.l, this.m);
   }

   public void a(Color color) {
      float[] fArrRGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      this.k = fArrRGBtoHSB[0];
      this.l = fArrRGBtoHSB[1];
      this.m = fArrRGBtoHSB[2];
      if (this.n != null) {
         this.n.a(color);
      }
   }

   public float e() {
      return this.k;
   }

   public float f() {
      return this.l;
   }

   public float g() {
      return this.m;
   }

   public boolean h() {
      return this.u && ColorPickerPopup.a().c();
   }

   public boolean i() {
      return Bool.from(this.u && ColorPickerPopup.a().c() ? 1 : 0);
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5, float f6) {
      FontRenderer fontRenderer = FontManager.a[14];
      float f7 = 20.0F * f6;
      float f8 = 8.0F * f6;
      this.t = f6;
      this.p.a(GuiInput.a(f2, f3, f4, f7, i2, i3));
      this.o.a();
      int i4 = (int)(255.0F * f5);
      float f9 = 14.0F * f6;
      float f10 = f2 + f4 - f8 - f9;
      this.p
         .a(
            MatrixStackVar,
            renderer2D,
            fontRenderer,
            this.j,
            f2 + f8,
            f3 + f7 / 2.0F - fontRenderer.b(this.j) * f6 / 4.0F,
            f10 - (f2 + f8) - 4.0F * f6,
            f6,
            Theme.a,
            f5
         );
      float f11 = f3 + f7 / 2.0F - f9 / 2.0F;
      this.r = f10;
      this.s = f11;
      boolean zA = GuiInput.a(f10, f11, f9, f9, i2, i3);
      if (zA != this.q) {
         this.o.a(!zA ? 0.0 : 1.0, 0.15, Easing.h);
         this.q = zA;
      }

      Color colorC = this.c();
      renderer2D.a(f10, f11, f9, f9, 6.0F * f6, Theme.a(i, i4), MatrixStackVar);
      float f12 = 11.0F * f6;
      renderer2D.a(f10 + (f9 - f12) / 2.0F, f11 + (f9 - f12) / 2.0F, f12, f12, 4.0F * f6, Theme.a(colorC, i4), MatrixStackVar);
      if (zA) {
         GuiInput.g();
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, int i3, float f6, float f7) {
   }

   @Override
   public boolean j() {
      return false;
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      float f5 = 14.0F * this.t;
      if (!GuiInput.a(this.r, this.s, f5, f5, i2, i3)) {
         return false;
      }

      if (this.u) {
         this.k();
      } else {
         this.o();
      }

      return true;
   }

   private void o() {
      float f2 = 14.0F * this.t;
      int i2 = (int)(this.r + f2 / 2.0F);
      int i3 = (int)(this.s + f2 + 5.0F * this.t);
      this.u = true;
      ColorPickerPopup.a().a(i2, i3, ColorPickerPopup.Edge.BOTTOM, this.j, this.c(), this.t, this::b);
   }

   public void k() {
      this.u = false;
      ColorPickerPopup.a().b();
   }

   private void b(Color color) {
      this.a(color);
   }

   @Override
   public void a(int i2, int i3) {
   }

   @Override
   public void a(int i2, int i3, double d2, double d3) {
   }

   @Override
   public boolean l() {
      return Bool.from(this.u && ColorPickerPopup.a().c() ? 1 : 0);
   }

   public void m() {
      if (this.u) {
         this.k();
      }
   }

   public boolean b(int i2, int i3) {
      return this.u ? ColorPickerPopup.a().c(i2, i3) : false;
   }

   public float[] n() {
      return this.u ? ColorPickerPopup.a().f() : null;
   }

   @Override
   public boolean d() {
      return Bool.from(this.n != null && !this.n.m() ? 0 : 1);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
