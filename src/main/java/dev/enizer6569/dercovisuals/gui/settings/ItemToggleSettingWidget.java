// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.settings;

import java.awt.Color;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.widgets.ColorPickerPopup;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.settings.ItemToggleSetting;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.ColorUtils;
import dev.enizer6569.dercovisuals.util.MarqueeText;

public class ItemToggleSettingWidget implements SettingWidget {
   public static final float a = 20.0F;
   private static final float d = 8.0F;
   private static final float e = 18.0F;
   private static final float f = 10.0F;
   private static final float g = 5.0F;
   private static final float h = 14.0F;
   private static final float i = 6.0F;
   private static final float j = 11.0F;
   private static final float k = 4.0F;
   private final String l;
   private final Item m;
   private boolean n;
   private Color o;
   private final Color p;
   private final ItemToggleSetting q;
   private final AnimationState r = new AnimationState();
   private final AnimationState s = new AnimationState();
   private final MarqueeText t = new MarqueeText();
   private boolean u = false;
   private boolean v = false;
   private float w;
   private float x;
   private float y = 1.0F;
   private boolean z = false;
   public static int b;
   public static boolean c;

   public ItemToggleSettingWidget(ItemToggleSetting itemToggleSetting) {
      this.q = itemToggleSetting;
      this.l = itemToggleSetting.f();
      this.m = itemToggleSetting.c();
      this.n = itemToggleSetting.a();
      this.o = itemToggleSetting.d();
      this.p = itemToggleSetting.d();
      this.v = this.n;
      this.r.d(!this.n ? 0.0 : 1.0);
   }

   public ItemToggleSettingWidget(String str, Item ItemVar, boolean z, Color color) {
      this.q = null;
      this.l = str;
      this.m = ItemVar;
      this.n = z;
      this.o = color;
      this.p = color;
      this.v = z;
      this.r.d(!z ? 0.0 : 1.0);
   }

   @Override
   public String a() {
      return this.l;
   }

   @Override
   public float b() {
      return 20.0F;
   }

   public Item c() {
      return this.m;
   }

   public boolean e() {
      return this.n;
   }

   public void a(boolean z) {
      this.n = z;
      if (this.q != null) {
         this.q.a(z);
      }
   }

   public void f() {
      this.n = Bool.from(this.n ? 0 : 1);
      if (this.q != null) {
         this.q.a(this.n);
      }
   }

   public Color g() {
      return this.o;
   }

   public void a(Color color) {
      this.o = color;
      if (this.q != null) {
         this.q.a(color);
      }
   }

   public Color h() {
      return this.p;
   }

   public float i() {
      return Color.RGBtoHSB(this.o.getRed(), this.o.getGreen(), this.o.getBlue(), (float[])null)[0];
   }

   public float m() {
      return Color.RGBtoHSB(this.o.getRed(), this.o.getGreen(), this.o.getBlue(), (float[])null)[1];
   }

   public float n() {
      return Color.RGBtoHSB(this.o.getRed(), this.o.getGreen(), this.o.getBlue(), (float[])null)[2];
   }

   public void a(float f2, float f3, float f4) {
      this.o = Color.getHSBColor(f2, f3, f4);
   }

   public void o() {
      this.o = this.p;
   }

   public boolean p() {
      return this.z && ColorPickerPopup.a().c();
   }

   public boolean q() {
      return this.z && ColorPickerPopup.a().c();
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5, float f6) {
      FontRenderer fontRenderer = FontManager.a[14];
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      float f7 = 20.0F * f6;
      float f8 = 8.0F * f6;
      this.y = f6;
      boolean zA = GuiInput.a(f2, f3, f4, f7, i2, i3);
      this.t.a(zA);
      if (zA != this.u) {
         this.s.a(!zA ? 0.0 : 1.0, 0.15, Easing.h);
         this.u = zA;
      }

      if (this.n != this.v) {
         this.r.a(!this.n ? 0.0 : 1.0, 0.2, Easing.h);
         this.v = this.n;
      }

      this.s.a();
      this.r.a();
      float fJ = (float)this.r.j();
      int i4 = (int)(255.0F * f5);
      float f9 = 16.0F * f6;
      float f10 = f2 + f8 - 3.0F;
      float f11 = f3 + f7 / 2.0F - f9 / 2.0F;
      if (f5 > 0.01F) {
         this.a(MatrixStackVar, new ItemStack(this.m), f10, f11, f9, f5, MinecraftClientVarGetInstance);
      }

      float f12 = 14.0F * f6;
      float f13 = f2 + f4 - f8 - f12;
      float f14 = f3 + f7 / 2.0F - f12 / 2.0F;
      float f15 = 18.0F * f6;
      float f16 = 10.0F * f6;
      float f17 = f13 - f15 - 6.0F * f6;
      float f18 = f3 + f7 / 2.0F - f16 / 2.0F;
      float f19 = f2 + f8 + f9 - 1.0F * f6;
      this.t
         .a(MatrixStackVar, renderer2D, fontRenderer, this.l, f19, f3 + f7 / 2.0F - fontRenderer.b(this.l) * f6 / 4.0F, f17 - f19 - 4.0F * f6, f6, Theme.a, f5);
      this.w = f13;
      this.x = f14;
      this.a(MatrixStackVar, renderer2D, f17, f18, fJ, f5, f6);
      renderer2D.a(f13, f14, f12, f12, 6.0F * f6, Theme.a(Theme.V, i4), MatrixStackVar);
      float f20 = 11.0F * f6;
      renderer2D.a(f13 + (f12 - f20) / 2.0F, f14 + (f12 - f20) / 2.0F, f20, f20, 4.0F * f6, Theme.a(this.o, i4), MatrixStackVar);
      boolean zA2 = GuiInput.a(f17, f18, f15, f16, i2, i3);
      boolean zA3 = GuiInput.a(f13, f14, f12, f12, i2, i3);
      if (zA2 || zA3) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      int i2 = (int)(255.0F * f5);
      float f7 = 18.0F * f6;
      float f8 = 10.0F * f6;
      float f9 = 5.0F * f6;
      float f10 = 9.0F * f6;
      float f11 = f2 + 0.5F * f6 + (f7 - f10 - f6) * f4;
      float f12 = f3 + 0.5F * f6;
      Color colorA = ColorUtils.a(Theme.F, Theme.C, f4);
      Color colorA2 = ColorUtils.a(Theme.G, Theme.D, f4);
      Color colorA3 = Theme.a(colorA, i2);
      Color colorA4 = Theme.a(colorA2, i2);
      renderer2D.a(f2 - 0.5F * f6, f3 - 0.5F * f6, f7 + f6, f8 + f6, f9, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      Color colorA5 = ColorUtils.a(Theme.f, Theme.y, f4);
      Color colorA6 = ColorUtils.a(Theme.H, Theme.z, f4);
      Color colorA7 = Theme.a(colorA5, i2);
      Color color = new Color(
         colorA6.getRed(), colorA6.getGreen(), colorA6.getBlue(), Math.min(255, (int)(colorA6.getAlpha() + (255 - colorA6.getAlpha()) * f4 * f5))
      );
      renderer2D.a(f2, f3, f7, f8, f9, colorA7, colorA7, color, color, MatrixStackVar);
      renderer2D.a(f11, f12, f10, f10, f10, Theme.a(ColorUtils.a(Theme.toggleOffKnob, Theme.a, f4), i2), MatrixStackVar);
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      float f5 = f2 + f4 - 8.0F - 14.0F;
      float f6 = f3 + 10.0F - 7.0F;
      if (GuiInput.a(f5 - 18.0F - 6.0F, f3 + 10.0F - 5.0F, 18.0F, 10.0F, i2, i3)) {
         this.f();
         return true;
      }

      if (!GuiInput.a(f5, f6, 14.0F, 14.0F, i2, i3)) {
         return false;
      }

      if (this.p()) {
         this.r();
      } else {
         this.u();
      }

      return true;
   }

   private void u() {
      float f2 = 14.0F * this.y;
      int i2 = (int)(this.w + f2 / 2.0F);
      int i3 = (int)(this.x + f2 + 5.0F * this.y);
      this.z = true;
      ColorPickerPopup.a().a(i2, i3, ColorPickerPopup.Edge.BOTTOM, this.l, this.o, this.y, this::b);
   }

   public void r() {
      this.z = false;
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
      return Bool.from(this.z && ColorPickerPopup.a().c() ? 1 : 0);
   }

   public void s() {
      if (this.z) {
         this.r();
      }
   }

   public boolean b(int i2, int i3) {
      return this.z ? ColorPickerPopup.a().c(i2, i3) : false;
   }

   public float[] t() {
      return this.z ? ColorPickerPopup.a().f() : null;
   }

   private void a(Matrix3x2fStack MatrixStackVar, ItemStack ItemStackVar, float f2, float f3, float f4, float f5, MinecraftClient MinecraftClientVar) {
      if (MinecraftClientVar.player != null && !ItemStackVar.isEmpty() && !(f5 < 0.01F)) {
         DrawContext DrawContextVar = Renderer2DImpl.currentDrawContext != null
            ? Renderer2DImpl.currentDrawContext
            : new DrawContext(
               MinecraftClientVar, new GuiRenderState(), MinecraftClientVar.getWindow().getScaledWidth(), MinecraftClientVar.getWindow().getScaledHeight()
            );
         Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
         float f6 = f4 / 16.0F;
         MatrixStackVarGetMatrices.pushMatrix();
         MatrixStackVarGetMatrices.translate(f2, f3);
         MatrixStackVarGetMatrices.scale(f6, f6);
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, f5);
         DrawContextVar.drawItem(ItemStackVar, 0, 0);
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         MatrixStackVarGetMatrices.popMatrix();
      }
   }

   @Override
   public boolean d() {
      return Bool.from(this.q != null && !this.q.m() ? 0 : 1);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
