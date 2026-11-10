package ru.pulse.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.animation.PerspectiveDistanceAnimation;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.FreeLook;
import pulse.modules.visuals.Animations;

@Mixin(Camera.class)
public abstract class CameraMixin {
   @Shadow
   protected abstract void setRotation(float var1, float var2);

   @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;setRotation(FF)V", ordinal = 0), require = 0)
   private void freeLookSetRotation(Camera cameraVar, float yaw, float pitch) {
      if (FreeLook.active && MinecraftClient.getInstance().getCameraEntity() instanceof ClientPlayerEntity) {
         this.setRotation(FreeLook.cameraYaw, FreeLook.cameraPitch);
      } else {
         this.setRotation(yaw, pitch);
      }
   }

   @Unique
   private boolean isPerspectiveAnimationEnabled() {
      return ModuleRegistry.ANIMATIONS != null && ModuleRegistry.ANIMATIONS.k() && ModuleRegistry.ANIMATIONS.i.a();
   }

   @Inject(method = "update", at = @At("HEAD"), require = 0)
   private void onUpdateHead(World worldVar, Entity EntityVar, boolean z, boolean z2, float f, CallbackInfo callbackInfo) {
      if (this.isPerspectiveAnimationEnabled()) {
         PerspectiveDistanceAnimation perspectiveDistanceAnimationQ = Animations.q();
         perspectiveDistanceAnimationQ.a(z, (long)(ModuleRegistry.ANIMATIONS.j.k() * 1.5F));
         perspectiveDistanceAnimationQ.a();
      }
   }

   @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/Camera;clipToSpace(F)F"), index = 0, require = 0)
   private float modifyCameraDistance(float f) {
      return this.isPerspectiveAnimationEnabled() && MinecraftClient.getInstance().options.getPerspective() != Perspective.FIRST_PERSON ? Animations.q().b() : f;
   }
}
