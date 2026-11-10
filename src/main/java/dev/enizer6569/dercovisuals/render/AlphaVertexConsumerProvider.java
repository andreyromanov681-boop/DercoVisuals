// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

public class AlphaVertexConsumerProvider implements VertexConsumerProvider {
   private final VertexConsumerProvider delegate;
   private final float alphaMultiplier;

   public AlphaVertexConsumerProvider(VertexConsumerProvider VertexConsumerProviderVar, float f) {
      this.delegate = VertexConsumerProviderVar;
      this.alphaMultiplier = f;
   }

   public VertexConsumer getBuffer(RenderLayer RenderLayerVar) {
      return new AlphaVertexConsumer(this.delegate.getBuffer(RenderLayerVar), this.alphaMultiplier);
   }
}
