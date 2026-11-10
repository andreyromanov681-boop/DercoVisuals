// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render;

public final class BlendRenderScope {
   private BlendRenderScope() {
   }

   public static void runBlended(Runnable runnable) {
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.disableDepthTest();
      RenderSystemHelper.disableCull();
      RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      try {
         runnable.run();
      } finally {
         RenderSystemHelper.disableBlend();
         RenderSystemHelper.enableDepthTest();
         RenderSystemHelper.enableCull();
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
