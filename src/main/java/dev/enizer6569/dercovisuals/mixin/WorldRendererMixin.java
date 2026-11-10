// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.WorldRenderEvent;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.WorldCustomizer;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.shader.SkyShaderRenderer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
   @Shadow
   @Final
   private BufferBuilderStorage bufferBuilders;
   @Shadow
   @Final
   private DefaultFramebufferSet framebufferSet;
   @Shadow
   private WorldRenderState worldRenderState;

   @Inject(method = "renderBlockDamage", at = @At("TAIL"), require = 0)
   private void renderBlockDamage(MatrixStack matrices, class_4598 immediate, WorldRenderState renderStates, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.world != null) {
         class_4598 imm = this.bufferBuilders.getEntityVertexConsumers();
         this.restoreRenderState();
         EventBusService.EVENT_BUS.post(new WorldRenderEvent(matrices, imm, mc.getRenderTickCounter().getTickProgress(true)));
         this.restoreRenderState();
      }
   }

   @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderSky(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice gpuBufferSlice, CallbackInfo ci) {
      WorldCustomizer wc = ModuleRegistry.WORLD_CUSTOMIZER;
      if (wc != null && wc.k() && wc.enableShader.a()) {
         FramePass framePass = frameGraphBuilder.createPass("dercovisuals_sky_shader");
         this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
         framePass.setRenderer(() -> {
            RenderSystem.setShaderFog(gpuBufferSlice);
            SkyShaderRenderer.INSTANCE.renderShader();
         });
         ci.cancel();
      } else {
         if (this.worldRenderState != null && this.worldRenderState.skyRenderState != null && wc != null && wc.r()) {
            this.worldRenderState.skyRenderState.skyColor = wc.t();
         }
      }
   }

   @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderClouds(CallbackInfo ci) {
      WorldCustomizer wc = ModuleRegistry.WORLD_CUSTOMIZER;
      if (wc != null && wc.k() && wc.enableShader.a()) {
         ci.cancel();
      }
   }

   private void restoreRenderState() {
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthFunc(515);
      RenderSystemHelper.depthMask(true);
      RenderSystemHelper.enableCull();
      RenderSystemHelper.disableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
