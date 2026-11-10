// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.settings;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.ColorUtils;
import dev.enizer6569.dercovisuals.util.MarqueeText;

public class BooleanSettingWidget implements SettingWidget {
   public static final float a = 20.0F;
   private static final float d = 8.0F;
   private static final float e = 18.0F;
   private static final float f = 10.0F;
   private static final float g = 5.0F;
   private static final Color h = Theme.C;
   private static final Color i = Theme.D;
   private final String j;
   private boolean k;
   private final BooleanSetting l;
   private final AnimationState m = new AnimationState();
   private final AnimationState n = new AnimationState();
   private final MarqueeText o = new MarqueeText();
   private boolean p = false;
   private boolean q = false;
   public static int b;
   public static boolean c;

   public BooleanSettingWidget(String str, boolean z) {
      this(null, str, z);
   }

   public BooleanSettingWidget(BooleanSetting booleanSetting) {
      this(booleanSetting, booleanSetting.f(), booleanSetting.a());
   }

   private BooleanSettingWidget(BooleanSetting booleanSetting, String str, boolean z) {
      this.l = booleanSetting;
      this.j = str;
      this.k = z;
      this.q = z;
      this.m.d(!z ? 0.0 : 1.0);
   }

   @Override
   public String a() {
      return this.j;
   }

   @Override
   public float b() {
      return 20.0F;
   }

   public boolean c() {
      if (this.l != null && this.k != this.l.a()) {
         this.k = this.l.a();
      }

      return this.k;
   }

   public void a(boolean z) {
      this.k = z;
      if (this.l != null) {
         this.l.a(z);
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5, float f6) {
      if (this.l != null && this.k != this.l.a()) {
         this.k = this.l.a();
      }

      FontRenderer fontRenderer = FontManager.b[14];
      float f7 = 20.0F * f6;
      float f8 = 8.0F * f6;
      boolean zA = GuiInput.a(f2, f3, f4, f7, i2, i3);
      this.o.a(zA);
      if (zA != this.p) {
         this.n.a(!zA ? 0.0 : 1.0, 0.15, Easing.h);
         this.p = zA;
      }

      if (this.k != this.q) {
         this.m.a(!this.k ? 0.0 : 1.0, 0.2, Easing.h);
         this.q = this.k;
      }

      this.n.a();
      this.m.a();
      float fJ = (float)this.m.j();
      float f9 = 28.0F * f6;
      float f10 = f2 + f4 - f8 - f9;
      this.o
         .a(
            MatrixStackVar,
            renderer2D,
            fontRenderer,
            this.j,
            f2 + f8,
            f3 + f7 / 2.0F - fontRenderer.b(this.j) * f6 / 4.0F,
            f10 - (f2 + f8) - 4.0F * f6,
            f6,
            Color.WHITE,
            f5
         );
      float f11 = 14.0F * f6;
      float f12 = f3 + f7 / 2.0F - f11 / 2.0F;
      this.a(MatrixStackVar, renderer2D, f10, f12, fJ, f5, f6);
      if (GuiInput.a(f10, f12, f9, f11, i2, i3)) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      float trackW = 18.0F * f6;
      float trackH = 10.0F * f6;
      float trackRadius = 5.0F * f6;
      float knobSize = 9.0F * f6;
      Color offTrack = new Color(34, 30, 48, 220);
      Color onTrack = new Color(139, 92, 246);
      Color currentTrack = ColorUtils.a(offTrack, onTrack, f4);
      Color offKnob = Theme.toggleOffKnob;
      Color onKnob = Color.WHITE;
      Color currentKnob = ColorUtils.a(offKnob, onKnob, f4);
      renderer2D.a(f2, f3, trackW, trackH, trackRadius, currentTrack, currentTrack, currentTrack, currentTrack, MatrixStackVar);
      float knobX = f2 + 0.5F * f6 + 8.0F * f6 * f4;
      float knobY = f3 + 0.5F * f6;
      renderer2D.a(knobX, knobY, knobSize, knobSize, knobSize, currentKnob, MatrixStackVar);
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      if (!GuiInput.a(f2 + f4 - 8.0F - 18.0F, f3 + 10.0F - 5.0F, 18.0F, 10.0F, i2, i3)) {
         return false;
      }

      int i4;
      if (this.k) {
         i4 = 0;
      } else {
         i4 = 1;
      }

      this.k = Bool.from(i4);
      if (this.l != null) {
         this.l.a(this.k);
      }

      return true;
   }

   @Override
   public boolean d() {
      int i2;
      if (this.l != null && !this.l.m()) {
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
