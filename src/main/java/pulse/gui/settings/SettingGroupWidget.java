package pulse.gui.settings;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import pulse.core.Bool;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.settings.SettingGroup;
import pulse.theme.Theme;

public class SettingGroupWidget implements SettingWidget {
   public static final float a = 18.0F;
   private static final float d = 8.0F;
   private static final float e = 6.0F;
   private static final float f = 1.0F;
   private final String g;
   private final SettingGroup h;
   public static int b;

   public SettingGroupWidget(SettingGroup settingGroup) {
      this.h = settingGroup;
      this.g = settingGroup.f();
   }

   public SettingGroupWidget(String str) {
      this.h = null;
      this.g = str;
   }

   @Override
   public String a() {
      return this.g;
   }

   @Override
   public float b() {
      return 18.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i, int i2, float f5, float f6) {
      FontRenderer fontRenderer = FontManager.a[16];
      float f7 = 18.0F * f6;
      float f8 = 8.0F * f6;
      float f9 = 6.0F * f6;
      float f10 = 1.0F * f6;
      int i3 = (int)(255.0F * f5);
      float fA = fontRenderer.a(this.g) * f6;
      float f11 = f2 + f4 / 2.0F - fA / 2.0F;
      float fB = f3 + f7 / 2.0F - fontRenderer.b(this.g) * f6 / 4.0F;
      Color colorA = Theme.a(Theme.b, i3 / 1.5F);
      MatrixStackVar.pushMatrix();
      MatrixStackVar.translate(f11, fB);
      MatrixStackVar.scale(f6, f6);
      MatrixStackVar.translate(-f11, -fB);
      fontRenderer.a(this.g, f11, fB, colorA, MatrixStackVar);
      MatrixStackVar.popMatrix();
      float f12 = f3 + f7 / 2.0F - f10 / 2.0F;
      Color colorA2 = Theme.a(new Color(20, 20, 28), i3);
      float f13 = f2 + f8;
      float f14 = f11 - f9;
      if (f14 > f13) {
         renderer2D.a(f13, f12, f14 - f13, f10, colorA2, MatrixStackVar);
      }

      float f15 = f11 + fA + f9;
      float f16 = f2 + f4 - f8;
      if (f16 > f15) {
         renderer2D.a(f15, f12, f16 - f15, f10, colorA2, MatrixStackVar);
      }
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i, int i2) {
      return false;
   }

   @Override
   public boolean d() {
      return Bool.from(this.h != null && !this.h.m() ? 0 : 1);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
