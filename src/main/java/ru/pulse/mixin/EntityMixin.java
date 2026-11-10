package ru.pulse.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.FreeLook;

@Mixin(Entity.class)
public abstract class EntityMixin {
   @Shadow
   private boolean glowing;

   @Inject(require = 0, method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
   public void changeLookDirection(double d, double d2, CallbackInfo callbackInfo) {
      if (FreeLook.active && this instanceof ClientPlayerEntity) {
         FreeLook.cameraYaw += (float)(d * 0.15);
         FreeLook.cameraPitch = MathHelper.clamp(FreeLook.cameraPitch + (float)(d2 * 0.15), -90.0F, 90.0F);
         callbackInfo.cancel();
      }
   }

   @Shadow
   public abstract boolean isGlowing();

   @Inject(require = 0, method = "isGlowing", at = @At("HEAD"), cancellable = true)
   private void onIsGlowing(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (ModuleRegistry.RENDER_TWEAKS.v()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}
