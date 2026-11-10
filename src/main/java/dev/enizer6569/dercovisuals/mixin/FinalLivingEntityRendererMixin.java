// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import java.awt.Color;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.HitColor;
import dev.enizer6569.dercovisuals.util.ColorUtils;

@Mixin(LivingEntityRenderer.class)
public abstract class FinalLivingEntityRendererMixin<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
   @Shadow
   protected M model;
   @Unique
   private static VertexConsumerProvider currentVertexConsumers;
   @Unique
   private static boolean currentStateHurt = false;
   @Unique
   private static final Identifier HIT_COLOR_TEXTURE = Identifier.of("dercovisuals", "textures/misc/white.png");

   @Inject(require = 0, method = "render", at = @At("HEAD"))
   private void captureState(S s, MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, CameraRenderState cameraRenderState, CallbackInfo callbackInfo) {
      currentStateHurt = s.hurt;
   }

   @Inject(require = 0, method = "render", at = @At("RETURN"))
   private void clearState(S s, MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, CameraRenderState cameraRenderState, CallbackInfo callbackInfo) {
      currentStateHurt = false;
      currentVertexConsumers = null;
   }

   @ModifyArg(
      require = 0,
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/RenderCommandQueue;submitModel(Lnet/minecraft/Model;Ljava/lang/Object;Lnet/minecraft/MatrixStack;Lnet/minecraft/RenderLayer;IIILnet/minecraft/Sprite;ILnet/minecraft/ModelCommandRenderer$class_11792;)V"
      ),
      index = 3
   )
   private RenderLayer modifyRenderLayer(RenderLayer renderLayer) {
      HitColor hitColor = ModuleRegistry.HIT_COLOR;
      return hitColor != null && hitColor.o() && currentStateHurt && hitColor.p() ? RenderLayers.itemEntityTranslucentCull(HIT_COLOR_TEXTURE) : renderLayer;
   }

   @ModifyArg(
      require = 0,
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/RenderCommandQueue;submitModel(Lnet/minecraft/Model;Ljava/lang/Object;Lnet/minecraft/MatrixStack;Lnet/minecraft/RenderLayer;IIILnet/minecraft/Sprite;ILnet/minecraft/ModelCommandRenderer$class_11792;)V"
      ),
      index = 6
   )
   private int modifyColor(int originalColor) {
      HitColor hitColor = ModuleRegistry.HIT_COLOR;
      if (hitColor != null && hitColor.o() && currentStateHurt) {
         Color colorN = hitColor.n();
         int alpha = (int)(colorN.getAlpha() * (hitColor.p() ? 1.0 : 0.6));
         return ColorUtils.a(colorN.getRed(), colorN.getGreen(), colorN.getBlue(), Math.min(alpha, 255));
      } else {
         return originalColor;
      }
   }
}
