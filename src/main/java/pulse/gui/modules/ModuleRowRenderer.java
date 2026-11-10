package pulse.gui.modules;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiEntry;
import pulse.gui.core.GuiInput;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class ModuleRowRenderer {
   private static final float c = 9.5F;
   private static final float d = 11.0F;
   private static final float e = 18.0F;
   private static final float f = 10.0F;
   private static final float g = 5.0F;
   private static final float h = 12.0F;
   private static final float i = 4.0F;
   private static final Color j = Theme.C;
   private static final Color k = Theme.D;
   private static final Color l = Theme.N;
   private final Map<GuiEntry, ModuleRowRenderer.RowAnimationState> m = new HashMap<>();
   public static int a;
   public static boolean b;

   public void a(GuiEntry guiEntry) {
      if (!this.m.containsKey(guiEntry)) {
         ModuleRowRenderer.RowAnimationState rowAnimationState = new ModuleRowRenderer.RowAnimationState();
         rowAnimationState.a.d(!guiEntry.b() ? 0.0 : 1.0);
         rowAnimationState.g = guiEntry.b();
         this.m.put(guiEntry, rowAnimationState);
      }
   }

   public ModuleRowRenderer.RowRenderResult a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      GuiEntry guiEntry,
      float f2,
      float f3,
      float f4,
      float f5,
      int i2,
      int i3,
      boolean z,
      boolean z2,
      boolean z3,
      boolean z4
   ) {
      this.a(guiEntry);
      ModuleRowRenderer.RowAnimationState rowAnimationState = this.m.get(guiEntry);
      FontRenderer fontRenderer = FontManager.b[14];
      float f6 = f2 + f4 - 11.0F - 18.0F;
      float f7 = f3 + f5 / 2.0F - 5.0F;
      float f8 = f6 - 4.0F - 12.0F;
      float f9 = f3 + f5 / 2.0F - 6.0F - 0.5F;
      boolean z5 = guiEntry.g() || guiEntry.h();
      int i5 = !z && GuiInput.a(f2, f3, f4, f5, i2, i3) ? 1 : 0;
      int i6 = z5 && !z && GuiInput.a(f8, f9, 12.0F, 12.0F, i2, i3) && i5 != 0 ? 1 : 0;
      int i4;
      if (i5 != 0 && i6 == 0) {
         i4 = 1;
      } else {
         i4 = 0;
      }

      int i7 = i4;
      int i8 = z5 && i5 != 0 && guiEntry.d() ? 1 : 0;
      boolean z6 = z4 && !z3;
      boolean z7 = !z4 && z3;
      if (z5) {
         if (z4) {
            if (z2) {
               if (z6) {
                  rowAnimationState.b.a(0.0, 0.15, Easing.h);
                  rowAnimationState.d.a(1.0, 0.15, Easing.h);
               }

               rowAnimationState.c.d(1.0);
            } else if (z6 && rowAnimationState.e) {
               rowAnimationState.b.a(0.0, 0.15, Easing.h);
               rowAnimationState.c.a(0.0, 0.2, Easing.h);
               rowAnimationState.e = false;
            }
         } else if (z7) {
            if (i5 != 0) {
               rowAnimationState.b.a(1.0, 0.15, Easing.h);
               rowAnimationState.c.a(1.0, 0.2, Easing.h);
               rowAnimationState.e = true;
            }

            if (i6 == 0) {
               rowAnimationState.d.a(0.0, 0.15, Easing.h);
               rowAnimationState.f = false;
            }
         } else if (Bool.from(i8) != rowAnimationState.e) {
            rowAnimationState.b.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
            rowAnimationState.c.a(i5 == 0 ? 0.0 : 1.0, 0.2, Easing.h);
            rowAnimationState.e = Bool.from(i8);
         }
      } else if (Bool.from(i5) != rowAnimationState.e) {
         rowAnimationState.b.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         rowAnimationState.e = Bool.from(i5);
      }

      if (z5 && Bool.from(i6) != rowAnimationState.f) {
         rowAnimationState.d.a(i6 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         rowAnimationState.f = Bool.from(i6);
      }

      boolean zB = guiEntry.b();
      if (zB != rowAnimationState.g) {
         rowAnimationState.a.a(!zB ? 0.0 : 1.0, 0.2, Easing.h);
         rowAnimationState.g = zB;
      }

      rowAnimationState.b.a();
      rowAnimationState.a.a();
      rowAnimationState.c.a();
      rowAnimationState.d.a();
      float fJ = (float)rowAnimationState.b.j();
      float fJ2 = (float)rowAnimationState.a.j();
      float fJ3 = (float)rowAnimationState.c.j();
      float fJ4 = (float)rowAnimationState.d.j();
      this.a(MatrixStackVar, renderer2D, f2, f3, f4, f5, fJ);
      Color disabledColor = new Color(60, 58, 75);
      Color enabledColor = Color.WHITE;
      Color textColor = ColorUtils.a(disabledColor, enabledColor, fJ2);
      if (fontRenderer != null) {
         fontRenderer.a(MatrixStackVar, guiEntry.a(), f2 + 12.0F, f3 + f5 / 2.0F - 4.0F, textColor);
      } else {
         TextRenderer tr = MinecraftClient.getInstance().textRenderer;
         DrawContext dc = Renderer2DImpl.currentDrawContext;
         if (dc != null) {
            dc.drawText(tr, guiEntry.a(), (int)(f2 + 12.0F), (int)(f3 + f5 / 2.0F - 4.0F), textColor.getRGB(), false);
         }
      }

      if (z5 && fJ3 > 0.01F) {
         this.a(MatrixStackVar, renderer2D, f8, f9, fJ3, fJ4, guiEntry);
      }

      this.a(MatrixStackVar, renderer2D, f6, f7, fJ2);
      return new ModuleRowRenderer.RowRenderResult(Bool.from(i7), Bool.from(i6), f8, f9);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      Color cardBorder = new Color(25, 25, 33);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 9.5F, cardBorder, cardBorder, cardBorder, cardBorder, MatrixStackVar);
      Color cardBg = new Color(18, 18, 24);
      renderer2D.a(f2, f3, f4, f5, 9.5F, cardBg, cardBg, cardBg, cardBg, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, GuiEntry guiEntry) {
      FontManager.e[24].a("\ue900", f2, f3, Theme.a(ColorUtils.a(Theme.b, l, f5), (int)(255.0F * f4)), MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4) {
      float f5 = f2 + 0.5F + 8.0F * f4;
      float f6 = f3 + 0.5F;
      Color disabledTrackBorder1 = new Color(24, 23, 32);
      Color disabledTrackBorder2 = new Color(20, 19, 28);
      Color colorA = ColorUtils.a(disabledTrackBorder1, j, f4);
      Color colorA2 = ColorUtils.a(disabledTrackBorder2, k, f4);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 19.0F, 11.0F, 5.0F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      Color disabledTrack1 = new Color(13, 12, 17);
      Color disabledTrack2 = new Color(10, 10, 14);
      Color colorA3 = ColorUtils.a(disabledTrack1, Theme.y, f4);
      Color colorA4 = ColorUtils.a(disabledTrack2, Theme.z, f4);
      renderer2D.a(f2, f3, 18.0F, 10.0F, 5.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      Color disabledKnob = new Color(48, 46, 62);
      renderer2D.a(f5, f6, 9.0F, 9.0F, 9.0F, ColorUtils.a(disabledKnob, Theme.a, f4), MatrixStackVar);
   }

   public static float a() {
      return 18.0F;
   }

   public static float b() {
      return 11.0F;
   }

   public static float c() {
      return 12.0F;
   }

   public static float d() {
      return 4.0F;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   private static class RowAnimationState {
      final AnimationState a = new AnimationState();
      final AnimationState b = new AnimationState();
      final AnimationState c = new AnimationState();
      final AnimationState d = new AnimationState();
      boolean e = false;
      boolean f = false;
      boolean g = false;
      public static int h;
      public static boolean i;

      public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
         return null;
      }
   }

   public static class RowRenderResult {
      public final boolean a;
      public final boolean b;
      public final float c;
      public final float d;
      public static int e;
      public static boolean f;

      public RowRenderResult(boolean z, boolean z2, float f2, float f3) {
         this.a = z;
         this.b = z2;
         this.c = f2;
         this.d = f3;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
