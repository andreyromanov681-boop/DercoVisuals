// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;

@Mixin(targets = "net/minecraft/client/render/entity/EntityRenderDispatcher")
public abstract class EntityRendererHitboxMixin {
   @Inject(method = "shouldRenderHitboxes", at = @At("HEAD"), cancellable = true)
   private void suppressVanillaHitbox(CallbackInfoReturnable<Boolean> cir) {
      if (ModuleRegistry.HITBOX_CUSTOMIZER != null && ModuleRegistry.HITBOX_CUSTOMIZER.k()) {
         cir.setReturnValue(false);
      }
   }
}
