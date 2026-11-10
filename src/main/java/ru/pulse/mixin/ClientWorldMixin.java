package ru.pulse.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
   @Inject(require = 0, method = "getCloudsColor", at = @At("HEAD"), cancellable = true)
   private void onGetCloudsColor(float f, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
      if (ModuleRegistry.RENDER_TWEAKS != null && ModuleRegistry.RENDER_TWEAKS.n()) {
         float fMax = Math.max(
                  0.0F, Math.min(1.0F, (float)(Math.cos((float)(((ClientWorld)this).getTimeOfDay() % 24000L) / 24000.0F * Math.PI * 2.0) * 2.0 + 0.5))
               )
               * 0.9F
            + 0.1F;
         callbackInfoReturnable.setReturnValue(ColorHelper.fromFloats(1.0F, fMax, fMax, fMax * 0.85F + 0.15F));
      }
   }

   @Inject(require = 0, method = "getLightningTicksLeft", at = @At("HEAD"), cancellable = true)
   private void onGetLightningTicks(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
      if (ModuleRegistry.RENDER_TWEAKS != null && ModuleRegistry.RENDER_TWEAKS.n()) {
         callbackInfoReturnable.setReturnValue(0);
      }
   }
}
