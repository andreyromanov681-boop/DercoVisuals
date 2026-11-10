package ru.pulse.mixin;

import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
   @Inject(require = 0, method = "getDarknessFactor", at = @At("HEAD"), cancellable = true)
   private void getDarknessFactor(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (ModuleRegistry.FULL_BRIGHT.k()) {
         callbackInfoReturnable.setReturnValue(0.0F);
      }
   }

   @Inject(require = 0, method = "getBrightness", at = @At("HEAD"), cancellable = true)
   private static void onGetBrightness(CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (ModuleRegistry.FULL_BRIGHT.k()) {
         callbackInfoReturnable.setReturnValue(1.0F);
      }
   }

   @Inject(require = 0, method = "getBrightness", at = @At("HEAD"), cancellable = true)
   private static void onGetBrightnessAmbient(CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (ModuleRegistry.FULL_BRIGHT.k()) {
         callbackInfoReturnable.setReturnValue(1.0F);
      }
   }
}
