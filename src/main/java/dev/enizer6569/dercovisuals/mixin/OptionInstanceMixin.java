// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import java.util.Optional;
import net.minecraft.SimpleOption.class_7177;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_7177.class)
public class OptionInstanceMixin {
   @Inject(require = 0, method = "validate", at = @At("HEAD"), cancellable = true)
   private void unlimitGamma(Double d, CallbackInfoReturnable<Optional<Double>> callbackInfoReturnable) {
      callbackInfoReturnable.setReturnValue(Optional.of(d));
   }
}
