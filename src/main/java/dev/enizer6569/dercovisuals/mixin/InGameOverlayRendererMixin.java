// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.RenderTweaks;
import dev.enizer6569.dercovisuals.render.CustomHandShaderCapture;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
   @Inject(require = 0, method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderFireOverlay(MatrixStack MatrixStackVar, VertexConsumerProvider VertexConsumerProviderVar, Sprite spriteVar, CallbackInfo callbackInfo) {
      if (CustomHandShaderCapture.isActive()) {
         if (VertexConsumerProviderVar instanceof class_4598) {
            ((class_4598)VertexConsumerProviderVar).draw();
         }

         CustomHandShaderCapture.endCapture();
      }

      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      if (renderTweaks != null && renderTweaks.p()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
   private static void onRenderUnderwaterOverlay(
      MinecraftClient MinecraftClientVar, MatrixStack MatrixStackVar, VertexConsumerProvider VertexConsumerProviderVar, CallbackInfo callbackInfo
   ) {
      if (CustomHandShaderCapture.isActive()) {
         if (VertexConsumerProviderVar instanceof class_4598) {
            ((class_4598)VertexConsumerProviderVar).draw();
         }

         CustomHandShaderCapture.endCapture();
      }

      if (ModuleRegistry.NO_FLUID != null && ModuleRegistry.NO_FLUID.k()) {
         callbackInfo.cancel();
      }
   }
}
