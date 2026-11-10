package pulse.render;

import net.minecraft.client.render.VertexConsumer;

public class AlphaVertexConsumer implements VertexConsumer {
   private final VertexConsumer delegate;
   private final float alphaMultiplier;

   public AlphaVertexConsumer(VertexConsumer VertexConsumerVar, float f) {
      this.delegate = VertexConsumerVar;
      this.alphaMultiplier = f;
   }

   public VertexConsumer vertex(float f, float f2, float f3) {
      this.delegate.vertex(f, f2, f3);
      return this;
   }

   public VertexConsumer color(int i, int i2, int i3, int i4) {
      this.delegate.color(i, i2, i3, (int)(i4 * this.alphaMultiplier));
      return this;
   }

   public VertexConsumer texture(float f, float f2) {
      this.delegate.texture(f, f2);
      return this;
   }

   public VertexConsumer overlay(int i, int i2) {
      this.delegate.overlay(i, i2);
      return this;
   }

   public VertexConsumer light(int i, int i2) {
      this.delegate.light(i, i2);
      return this;
   }

   public VertexConsumer normal(float f, float f2, float f3) {
      this.delegate.normal(f, f2, f3);
      return this;
   }

   public VertexConsumer color(int argb) {
      this.delegate.color(argb);
      return this;
   }

   public VertexConsumer lineWidth(float width) {
      this.delegate.lineWidth(width);
      return this;
   }
}
