// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render;

import java.awt.Color;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.MatrixStack.class_4665;
import dev.enizer6569.dercovisuals.modules.visuals.CustomHand;

public class CustomHandShaderConsumer implements VertexConsumer {
   private final VertexConsumer delegate;
   private final CustomHand module;

   public CustomHandShaderConsumer(VertexConsumer delegate, CustomHand module) {
      this.delegate = delegate;
      this.module = module;
   }

   public VertexConsumer vertex(float x, float y, float z) {
      return this.delegate.vertex(x, y, z);
   }

   public VertexConsumer color(int red, int green, int blue, int alpha) {
      Color c = this.shaderColor(0.0F, 0.0F, 0.0F, this.module.shaderOpacity.a());
      if (this.module.shaderOnly.a()) {
         return this.delegate.color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * this.module.shaderOpacity.a()));
      }

      float factor = this.module.shaderOpacity.a();
      int nr = (int)(red * (1.0F - factor) + c.getRed() * factor);
      int ng = (int)(green * (1.0F - factor) + c.getGreen() * factor);
      int nb = (int)(blue * (1.0F - factor) + c.getBlue() * factor);
      return this.delegate.color(nr, ng, nb, alpha);
   }

   public VertexConsumer color(int argb) {
      int r = argb >> 16 & 0xFF;
      int g = argb >> 8 & 0xFF;
      int b = argb & 0xFF;
      int a = argb >> 24 & 0xFF;
      return this.color(r, g, b, a);
   }

   public VertexConsumer texture(float u, float v) {
      return this.delegate.texture(u, v);
   }

   public VertexConsumer overlay(int u, int v) {
      return this.delegate.overlay(u, v);
   }

   public VertexConsumer light(int u, int v) {
      return this.delegate.light(u, v);
   }

   public VertexConsumer normal(float x, float y, float z) {
      return this.delegate.normal(x, y, z);
   }

   public VertexConsumer lineWidth(float width) {
      return this.delegate.lineWidth(width);
   }

   public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
      Color c = this.shaderColor(x, y, z, this.module.shaderOpacity.a());
      int shaderArgb = ColorHelper.fromFloats(this.module.shaderOpacity.a(), c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F);
      int newColor;
      if (this.module.shaderOnly.a()) {
         newColor = shaderArgb;
      } else {
         float factor = this.module.shaderOpacity.a();
         int origA = color >> 24 & 0xFF;
         int origR = color >> 16 & 0xFF;
         int origG = color >> 8 & 0xFF;
         int origB = color & 0xFF;
         int sr = c.getRed();
         int sg = c.getGreen();
         int sb = c.getBlue();
         int nr = (int)(origR * (1.0F - factor) + sr * factor);
         int ng = (int)(origG * (1.0F - factor) + sg * factor);
         int nb = (int)(origB * (1.0F - factor) + sb * factor);
         newColor = origA << 24 | nr << 16 | ng << 8 | nb;
      }

      this.delegate.vertex(x, y, z, newColor, u, v, overlay, light, nx, ny, nz);
   }

   public void quad(class_4665 matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay) {
      super.quad(matrixEntry, quad, red, green, blue, alpha, light, overlay);
   }

   public void quad(
      class_4665 matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay
   ) {
      super.quad(matrixEntry, quad, brightnesses, red, green, blue, alpha, lights, overlay);
   }

   private Color shaderColor(float f, float f2, float f3, float f4) {
      f *= 20.0F;
      f2 *= 20.0F;
      f3 *= 20.0F;
      float fCurrentTimeMillis = (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.module.shaderSpeed.a();
      String strD = this.module.shaderMode.d();
      if ("Stars".equals(strD)) {
         int iRound = 90 + Math.round((float)((Math.sin(f * 7.0F + f2 * 11.0F + f3 * 13.0F + fCurrentTimeMillis * 4.0F) + 1.0) * 0.5) * 165.0F);
         return new Color(iRound, iRound, 255, Math.round(f4 * 255.0F));
      }

      if (!"Web".equals(strD)) {
         return "Plasma".equals(strD)
            ? this.withAlpha(
               Color.getHSBColor(
                  this.wrap((float)(Math.sin(f * 2.4F + fCurrentTimeMillis) + Math.cos(f3 * 2.8F - fCurrentTimeMillis)) * 0.12F + fCurrentTimeMillis * 0.08F),
                  0.9F,
                  1.0F
               ),
               f4
            )
            : this.withAlpha(
               Color.getHSBColor(
                  this.wrap(0.64F + (float)Math.sin((f + f2 + f3) * 1.7F + fCurrentTimeMillis) * 0.08F + fCurrentTimeMillis * 0.03F), 0.75F, 1.0F
               ),
               f4
            );
      }

      int iRound2 = 170 + Math.round(Math.abs((float)Math.sin((f + f3) * 8.0F + fCurrentTimeMillis * 2.0F)) * 85.0F);
      return new Color(iRound2, iRound2, iRound2, Math.round(f4 * 255.0F));
   }

   private Color withAlpha(Color c, float a) {
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, (int)(c.getAlpha() * a))));
   }

   private float wrap(float f) {
      float f2 = f % 1.0F;
      if (f2 < 0.0F) {
         f2++;
      }

      return f2;
   }
}
