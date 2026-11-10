package ru.pulse.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;

@Mixin(FogRenderer.class)
public class BackgroundRendererMixin {
   @Inject(method = "getFogColor", at = @At("TAIL"), cancellable = true, require = 0)
   private void onGetFogColor(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir) {
      if (ModuleRegistry.NO_FLUID.k()) {
         CameraSubmersionType submersion = camera.getSubmersionType();
         if (submersion == CameraSubmersionType.WATER || submersion == CameraSubmersionType.LAVA) {
            cir.setReturnValue(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F));
            return;
         }
      }

      if (ModuleRegistry.WORLD_CUSTOMIZER.o()) {
         int color = ModuleRegistry.WORLD_CUSTOMIZER.s();
         cir.setReturnValue(new Vector4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F));
      }
   }

   @Inject(method = "applyFog", at = @At("RETURN"), cancellable = true, require = 0)
   private void onApplyFog(
      Camera camera, int viewDistance, RenderTickCounter tickCounter, float skyDarkness, ClientWorld world, CallbackInfoReturnable<Vector4f> cir
   ) {
      Vector4f v = (Vector4f)cir.getReturnValue();
      if (v != null && ModuleRegistry.WORLD_CUSTOMIZER.k()) {
         if (ModuleRegistry.WORLD_CUSTOMIZER.removeFog.a()) {
            v.x = 9999999.0F;
            v.y = 9999999.0F;
            cir.setReturnValue(v);
         } else if (ModuleRegistry.WORLD_CUSTOMIZER.o()) {
            float customDist = ModuleRegistry.WORLD_CUSTOMIZER.q();
            v.y = customDist;
            v.x = customDist * 0.5F;
            cir.setReturnValue(v);
         }
      }
   }
}
