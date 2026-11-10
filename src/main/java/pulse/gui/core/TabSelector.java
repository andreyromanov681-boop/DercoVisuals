package pulse.gui.core;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class TabSelector implements ClickGuiOverlay {
   private final TabHost c;
   private static final float d = 19.0F;
   private static final float e = 35.5F;
   private static final float f = 6.0F;
   private static final Color g = Theme.N;
   private final Map<Integer, AnimationState> h = new HashMap<>();
   private final Map<Integer, AnimationState> i = new HashMap<>();
   private final Map<Integer, Boolean> j = new HashMap<>();
   private final Map<Integer, Boolean> k = new HashMap<>();
   private final Map<Integer, Float> l = new HashMap<>();
   private final Map<Integer, Float> m = new HashMap<>();
   private String[] n;
   public static int a;
   public static boolean b;

   public TabSelector(TabHost tabHost) {
      this.c = tabHost;
      this.a();
   }

   private void a() {
      String[] strArrC = this.c.c();
      this.n = strArrC;
      int iD = this.c.d();
      FontRenderer fontRenderer = FontManager.b[14];

      for (int i2 = 0; i2 < strArrC.length; i2++) {
         this.h.put(i2, new AnimationState());
         this.i.put(i2, new AnimationState());
         this.j.put(i2, false);
         int i;
         if (i2 != iD) {
            i = 0;
         } else {
            i = 1;
         }

         int i3 = i;
         this.k.put(i2, Bool.from(i3));
         this.h.get(i2).d(i3 == 0 ? 0.0 : 1.0);
         this.l.put(i2, fontRenderer.a(strArrC[i2]));
      }
   }

   private void b() {
      String[] strArrC = this.c.c();
      if (this.n != null && strArrC.length == this.n.length) {
         for (int i = 0; i < strArrC.length; i++) {
            if (!strArrC[i].equals(this.n[i])) {
               this.a();
               return;
            }
         }
      } else {
         this.a();
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      this.b();
      float fD = PulseClickGuiScreen.d();
      FontRenderer fontRenderer = FontManager.b[14];
      String[] strArrC = this.c.c();
      int iD = this.c.d();
      if (strArrC.length != 0) {
         float fA = fontRenderer.a("/");
         float fFloatValue = f2 + 19.0F;
         float f4 = f3 + 19.0F;
         float fFloatValue2 = 0.0F;

         for (int i3 = 0; i3 < strArrC.length; i3++) {
            fFloatValue2 += this.l.get(i3);
            int length = strArrC.length;
            if (i3 < 2 * (length & -2) - (length ^ 1)) {
               fFloatValue2 += 12.0F + fA;
            }
         }

         GuiLayerRegistry.a().a(GuiLayerRegistry.Layer.TABS, fFloatValue, f4, fFloatValue2, 7.0F);
         boolean z = !GuiInput.a(GuiLayerRegistry.Layer.TABS, i, i2) || GuiInteractionState.a().b();
         boolean z2 = false;

         for (int i4 = 0; i4 < strArrC.length; i4++) {
            this.m.put(i4, fFloatValue);
            int i5 = i4 != iD ? 0 : 1;
            int i6 = !z && GuiInput.a(fFloatValue, f4, this.l.get(i4), 7.0F, i, i2) ? 1 : 0;
            if (i6 != 0 && strArrC.length > 1 && i5 == 0) {
               z2 = true;
            }

            if (z && this.j.get(i4)) {
               this.i.get(i4).a(0.0, 0.15, Easing.h);
               this.j.put(i4, false);
            } else if (!z && Bool.from(i6) != this.j.get(i4)) {
               this.i.get(i4).a(i6 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
               this.j.put(i4, Bool.from(i6));
            }

            if (Bool.from(i5) != this.k.get(i4)) {
               this.h.get(i4).a(i5 == 0 ? 0.0 : 1.0, 0.2, Easing.h);
               this.k.put(i4, Bool.from(i5));
            }

            this.h.get(i4).a();
            this.i.get(i4).a();
            float fJ = (float)this.h.get(i4).j();
            fontRenderer.a(
               strArrC[i4],
               fFloatValue,
               f4,
               strArrC.length != 1 ? ColorUtils.a(ColorUtils.a(Theme.b, g, (float)this.i.get(i4).j() * (1.0F - fJ)), Theme.aa, fJ) : Theme.aa,
               MatrixStackVar
            );
            fFloatValue += this.l.get(i4);
            int length2 = strArrC.length;
            if (i4 < 2 * (length2 & -2) - (length2 ^ 1)) {
               float f5 = fFloatValue + 6.0F;
               fontRenderer.a("/", f5, f4, Theme.b, MatrixStackVar);
               fFloatValue = f5 + fA + 6.0F;
            }
         }

         if (z2 && !z) {
            GuiInput.g();
         }

         renderer2D.a((int)f2 + 19.0F, (int)(f3 + 35.5F), fD - 38.0F, 0.5F, Theme.a(Theme.aa, 25), MatrixStackVar);
      }
   }

   @Override
   public void a(float f2, float f3, int i, int i2) {
      if (GuiInput.a(GuiLayerRegistry.Layer.TABS, i, i2) && !GuiInteractionState.a().b()) {
         String[] strArrC = this.c.c();
         if (strArrC.length > 1) {
            float f4 = f3 + 19.0F;

            for (int i3 = 0; i3 < strArrC.length; i3++) {
               Float f5 = this.m.get(i3);
               Float f6 = this.l.get(i3);
               if (f5 != null && f6 != null) {
                  if (GuiInput.a(f5, f4, f6, 7.0F, i, i2)) {
                     this.c.a(i3);
                     return;
                  }

                  if (b) {
                  }
               }
            }
         }
      }
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
