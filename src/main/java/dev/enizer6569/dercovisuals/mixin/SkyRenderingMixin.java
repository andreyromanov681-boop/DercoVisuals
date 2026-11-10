// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.WorldCustomizer;

@Mixin(SkyRendering.class)
public class SkyRenderingMixin {
   @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderStars(CallbackInfo ci) {
      WorldCustomizer wc = ModuleRegistry.WORLD_CUSTOMIZER;
      if (wc != null && wc.k() && wc.enableShader.a()) {
         ci.cancel();
      }
   }

   @Inject(method = "renderCelestialBodies", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderCelestialBodies(CallbackInfo ci) {
      WorldCustomizer wc = ModuleRegistry.WORLD_CUSTOMIZER;
      if (wc != null && wc.k() && wc.enableShader.a()) {
         ci.cancel();
      }
   }
}
