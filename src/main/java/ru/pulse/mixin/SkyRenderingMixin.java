package ru.pulse.mixin;

import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.module.ModuleRegistry;
import pulse.modules.visuals.WorldCustomizer;

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
