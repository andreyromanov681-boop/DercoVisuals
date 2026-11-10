package pulse.gui.events;

import java.awt.Color;
import java.util.function.Consumer;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiInteractionState;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class EventCard {
   public static final float a = 36.5F;
   private static final float d = 6.5F;
   private static final float e = 1.5F;
   private static final float f = 19.0F;
   private static final float g = 2.0F;
   private static final float h = 6.5F;
   private static final float i = 9.0F;
   private static final float j = -6.0F;
   private static final float k = 19.0F;
   private static final float l = 5.0F;
   private final EventInfo m;
   private final AnimationState n = new AnimationState();
   private final AnimationState o = new AnimationState();
   private boolean p = false;
   private boolean q = false;
   private Consumer<EventInfo> r;
   public static int b;
   public static boolean c;

   public EventCard(EventInfo eventInfo) {
      this.m = eventInfo;
   }

   public EventInfo a() {
      return this.m;
   }

   public void a(Consumer<EventInfo> consumer) {
      this.r = consumer;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5) {
      this.n.a();
      this.o.a();
      int i6 = (int)(255.0F * f5);
      boolean zB = GuiInteractionState.a().b();
      int i4 = !zB && GuiInput.a(f2, f3, f4, 36.5F, i2, i3) ? 1 : 0;
      float rightEdge = f2 + f4 - 9.0F - 19.0F;
      float topEdge = f3 + 8.75F + 0.7F;
      int i5 = !zB && GuiInput.a(rightEdge, topEdge, 19.0F, 19.0F, i2, i3) ? 1 : 0;
      if (zB && this.p) {
         this.n.a(0.0, 0.15, Easing.h);
         this.p = false;
      } else if (!zB && Bool.from(i4) != this.p) {
         this.n.a(i4 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.p = Bool.from(i4);
      }

      if (zB && this.q) {
         this.o.a(0.0, 0.15, Easing.h);
         this.q = false;
      } else if (!zB && Bool.from(i5) != this.q) {
         this.o.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.q = Bool.from(i5);
      }

      float fJ = (float)this.n.j();
      Color color = new Color(17, 17, 22);
      Color color2 = new Color(14, 14, 19);
      Color color3 = new Color(22, 22, 29);
      Color colorA = ColorUtils.a(color, Theme.g, fJ);
      Color colorA2 = ColorUtils.a(color2, Theme.h, fJ);
      Color colorA3 = Theme.a(colorA, i6);
      Color colorA4 = Theme.a(colorA2, i6);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, 37.5F, 6.5F, color3, color3, color3, color3, MatrixStackVar);
      renderer2D.a(f2, f3, f4, 36.5F, 6.5F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      FontRenderer fontRenderer = FontManager.a[11];
      FontRenderer fontRenderer2 = FontManager.a[15];
      float f6 = f2 + 6.5F + 3.0F;
      String strValueOf;
      String strA;
      if (this.m.c() == EventInfo.EventState.UPCOMING) {
         strValueOf = this.m.h();
         strA = this.m.j();
      } else {
         strValueOf = String.valueOf(this.m.b());
         strA = this.m.a();
      }

      float fB = fontRenderer.b(strValueOf);
      float fB2 = f3 + (36.5F - (fB + -6.0F + fontRenderer2.b(strA) - 8.0F)) / 2.0F;
      float f7 = fB2 + fB + -6.0F;
      Color colorA5 = Theme.a(Theme.b, i6);
      Color colorA6 = Theme.a(Theme.a, i6);
      fontRenderer.a(strValueOf, f6, fB2, colorA5, MatrixStackVar);
      fontRenderer2.a(strA, f6, f7, colorA6, MatrixStackVar);
      String text = this.m.g();
      fontRenderer.a(text, rightEdge - 5.0F - fontRenderer.a(text), f3 + 18.25F - fontRenderer.b(text) / 4.0F, colorA5, MatrixStackVar);
      this.a(MatrixStackVar, renderer2D, (int)rightEdge, (int)topEdge, f5);
      if (i5 != 0 && !zB) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4) {
      int i2 = (int)(255.0F * f4);
      float fJ = (float)this.o.j();
      Color colorA = ColorUtils.a(Theme.f, Theme.I, fJ);
      Color colorA2 = ColorUtils.a(Theme.e, Theme.K, fJ);
      Color colorA3 = Theme.a(colorA, i2);
      Color colorA4 = Theme.a(colorA2, i2);
      Color color = new Color(19, 19, 25);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 20.0F, 20.0F, 6.0F, color, color, color, color, MatrixStackVar);
      renderer2D.a(f2, f3, 19.0F, 19.0F, 6.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      Color colorA5 = Theme.a(ColorUtils.a(Theme.b, Theme.O, fJ), i2);
      FontRenderer fontRenderer = FontManager.e[12];
      float fA = fontRenderer.a("\ue91a");
      float fB = fontRenderer.b("\ue91a");
      fontRenderer.a("\ue91a", f2 + (19.0F - fA) / 2.0F, f3 + (19.0F - fB) / 2.0F + 2.5F, colorA5, MatrixStackVar);
   }

   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      if (!GuiInput.a(f2 + f4 - 9.0F - 19.0F, f3 + 8.75F, 19.0F, 19.0F, i2, i3)) {
         return false;
      }

      if (this.r != null) {
         this.r.accept(this.m);
      }

      return true;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
