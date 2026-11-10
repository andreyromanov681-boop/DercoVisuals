package ru.pulse.mixin;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.render.ArmorRenderState;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {
   @Inject(method = "render", at = @At("HEAD"), require = 0)
   private void captureState(
      MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, int i, BipedEntityRenderState BipedEntityRenderStateVar, float f, float f2, CallbackInfo callbackInfo
   ) {
      ArmorRenderState.a(BipedEntityRenderStateVar.hurt);
   }

   @Inject(method = "render", at = @At("RETURN"), require = 0)
   private void clearState(
      MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, int i, BipedEntityRenderState BipedEntityRenderStateVar, float f, float f2, CallbackInfo callbackInfo
   ) {
      ArmorRenderState.b();
   }
}
