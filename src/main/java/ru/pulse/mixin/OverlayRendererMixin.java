package ru.pulse.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.module.ModuleRegistry;

@Mixin(InGameOverlayRenderer.class)
public class OverlayRendererMixin {
   @Inject(require = 0, method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderFireOverlay(MatrixStack MatrixStackVar, VertexConsumerProvider VertexConsumerProviderVar, Sprite spriteVar, CallbackInfo callbackInfo) {
      if (ModuleRegistry.RENDER_TWEAKS.p()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderUnderwaterOverlay(
      MinecraftClient MinecraftClientVar, MatrixStack MatrixStackVar, VertexConsumerProvider VertexConsumerProviderVar, CallbackInfo callbackInfo
   ) {
      if (ModuleRegistry.NO_FLUID.k()) {
         callbackInfo.cancel();
      }
   }
}
