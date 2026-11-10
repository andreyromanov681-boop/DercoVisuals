// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.ClientWorld.class_5271;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.Atmosphere;

@Mixin(class_5271.class)
public class ClientWorldPropertiesMixin {
   @Inject(require = 0, method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
   private void onGetTimeOfDay(CallbackInfoReturnable<Long> cir) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isTimeCustom()) {
         cir.setReturnValue(atmosphere.getCustomTime());
      }
   }

   @Inject(require = 0, method = "getTime", at = @At("HEAD"), cancellable = true)
   private void onGetTime(CallbackInfoReturnable<Long> cir) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isTimeCustom()) {
         cir.setReturnValue(atmosphere.getCustomTime());
      }
   }
}
