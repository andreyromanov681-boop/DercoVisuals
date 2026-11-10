package pulse.util;

import java.awt.Color;
import lombok.Generated;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.render.Renderer2D;
import pulse.render.font.FontRenderer;

public class MarqueeText {
   public static final long a = 750L;
   public static final long b = 500L;
   public static final long c = 2500L;
   public static final float d = 0.2F;
   private long g = 0L;
   private boolean h = false;
   private boolean i = false;
   private float j = 0.0F;
   private final AnimationState k = new AnimationState();
   public static int e;

   public float a(String str, FontRenderer fontRenderer, float f2, float f3) {
      float fA = fontRenderer.a(str) * f3;
      if (this.h && !(fA <= f2)) {
         float f4 = 0.0F;
         if (fA > f2) {
            long jCurrentTimeMillis = (System.currentTimeMillis() - this.g) % 2500L;
            f4 = -(fA - f2)
               * (
                  jCurrentTimeMillis >= 750L
                     ? (jCurrentTimeMillis >= 1250L ? (jCurrentTimeMillis >= 2000L ? 0.0F : 1.0F - (float)(jCurrentTimeMillis - 1250L) / 750.0F) : 1.0F)
                     : (float)jCurrentTimeMillis / 750.0F
               );
         }

         float fJ;
         if (this.i) {
            this.k.a();
            fJ = (float)this.k.j();
            if (this.k.d()) {
               this.h = false;
               this.i = false;
               fJ = 0.0F;
            }
         } else {
            this.j = f4;
            fJ = f4;
         }

         return fJ;
      } else {
         return 0.0F;
      }
   }

   public void a(boolean z) {
      if (z && !this.h) {
         this.h = true;
         this.g = System.currentTimeMillis();
         this.i = false;
      }

      if (!z && this.h && !this.i) {
         this.i = true;
         this.k.d(this.j);
         this.k.a(0.0, 0.2F, Easing.h);
      }
   }

   public void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      String str,
      float f2,
      float f3,
      float f4,
      float f5,
      Color color,
      float f6
   ) {
      float fA = fontRenderer.a(str) * f5;
      float fA2 = this.a(str, fontRenderer, f4, f5);
      Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(255.0F * f6));
      if (fA > f4) {
         renderer2D.b().a(f2, f3 - 5.0F, f4, 20.0F, MatrixStackVar);
      }

      fontRenderer.a(str, f2 + fA2, f3, color2, MatrixStackVar);
      if (fA > f4) {
         renderer2D.b().a(MatrixStackVar);
      }
   }

   @Generated
   public long a() {
      return this.g;
   }

   @Generated
   public boolean b() {
      return this.h;
   }

   @Generated
   public boolean c() {
      return this.i;
   }

   @Generated
   public float d() {
      return this.j;
   }

   @Generated
   public AnimationState e() {
      return this.k;
   }

   @Generated
   public void b(boolean z) {
      this.h = z;
   }

   @Generated
   public void c(boolean z) {
      this.i = z;
   }

   @Generated
   public void a(float f2) {
      this.j = f2;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
