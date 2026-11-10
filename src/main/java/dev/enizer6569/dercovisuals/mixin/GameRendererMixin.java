// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.WorldRenderEndEvent;
import dev.enizer6569.dercovisuals.events.WorldRenderStartEvent;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.utilities.Zoom;
import dev.enizer6569.dercovisuals.modules.visuals.AspectRatio;
import dev.enizer6569.dercovisuals.render.CustomHandShaderCapture;
import dev.enizer6569.dercovisuals.render.motionblur.MotionBlurManager;
import dev.enizer6569.dercovisuals.render.world.WorldToScreen;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
   @Unique
   private Matrix4f prevModelView = new Matrix4f();
   @Unique
   private Matrix4f prevProjection = new Matrix4f();
   @Unique
   private Vector3f prevCameraPos = new Vector3f();

   @Shadow
   public abstract float getFarPlaneDistance();

   @Inject(require = 0, method = "renderWorld", at = @At("HEAD"))
   private void onRenderWorldStart(RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new WorldRenderStartEvent(RenderTickCounterVar.getTickProgress(true)));
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.world != null && mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         Camera camera = mc.gameRenderer.getCamera();
         float tickDelta = RenderTickCounterVar.getTickProgress(true);
         float fov = ((GameRendererAccessor)this).invokeGetFov(camera, tickDelta, true);
         Matrix4f projection = this.calcProjectionMatrix(fov);
         Matrix4f modelView = new Matrix4f().rotation(camera.getRotation().conjugate(new Quaternionf()));
         WorldToScreen.a.set(projection);
         MotionBlurManager.INSTANCE
            .setFrameMotionBlur(
               modelView,
               this.prevModelView,
               projection,
               this.prevProjection,
               new Vector3f(
                  (float)(camera.getCameraPos().x % 30000.0),
                  (float)(camera.getCameraPos().y % 30000.0),
                  (float)(camera.getCameraPos().z % 30000.0)
               ),
               this.prevCameraPos
            );
      }
   }

   private Matrix4f calcProjectionMatrix(float fov) {
      if (Zoom.a) {
         fov = (float)Zoom.b;
      }

      float aspect = (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / MinecraftClient.getInstance().getWindow().getFramebufferHeight();
      AspectRatio aspectRatio = ModuleRegistry.ASPECT_RATIO;
      if (aspectRatio.k()) {
         aspect = aspectRatio.n();
      }

      return new Matrix4f().perspective(fov * (float) (Math.PI / 180.0), aspect, 0.05F, this.getFarPlaneDistance());
   }

   @Inject(require = 0, method = "renderHand", at = @At("HEAD"))
   private void onRenderHandStart(float f, boolean z, Matrix4f matrix4f, CallbackInfo callbackInfo) {
   }

   @Inject(require = 0, method = "renderHand", at = @At("RETURN"))
   private void onRenderHandEnd(float f, boolean z, Matrix4f matrix4f, CallbackInfo callbackInfo) {
      CustomHandShaderCapture.endCapture();
   }

   @Inject(require = 0, method = "renderWorld", at = @At("RETURN"))
   private void onRenderWorldEnd(RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      CustomHandShaderCapture.endCapture();
      EventBusService.EVENT_BUS.post(new WorldRenderEndEvent(new MatrixStack(), RenderTickCounterVar.getTickProgress(true), ((GameRenderer)this).getCamera()));
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.world != null) {
         MotionBlurManager.INSTANCE.applyMotionBlurBeforeHands();
         if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
            Camera camera = mc.gameRenderer.getCamera();
            float tickDelta = RenderTickCounterVar.getTickProgress(true);
            float fov = ((GameRendererAccessor)this).invokeGetFov(camera, tickDelta, true);
            this.prevProjection = this.calcProjectionMatrix(fov);
            this.prevModelView = new Matrix4f().rotation(camera.getRotation().conjugate(new Quaternionf()));
            this.prevCameraPos = new Vector3f(
               (float)(camera.getCameraPos().x % 30000.0),
               (float)(camera.getCameraPos().y % 30000.0),
               (float)(camera.getCameraPos().z % 30000.0)
            );
         }
      }
   }

   @Inject(require = 0, method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
   private void onBobViewWhenHurt(MatrixStack MatrixStackVar, float f, CallbackInfo callbackInfo) {
      if (ModuleRegistry.RENDER_TWEAKS.o()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "bobView", at = @At("HEAD"), cancellable = true)
   private void onBobView(MatrixStack MatrixStackVar, float f, CallbackInfo callbackInfo) {
      if (ModuleRegistry.RENDER_TWEAKS.p()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
   private void onShowFloatingItem(CallbackInfo callbackInfo) {
      if (ModuleRegistry.RENDER_TWEAKS.q()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "renderWorld", at = @At("TAIL"))
   public void renderWorld(RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      Camera CameraVarGetCamera = MinecraftClient.getInstance().gameRenderer.getCamera();
      if (CameraVarGetCamera != null) {
         WorldToScreen.b.identity();
         WorldToScreen.e.rotation(CameraVarGetCamera.getRotation().conjugate(new Quaternionf()));
      }

      if (CustomHandShaderCapture.guiOpen()) {
         CustomHandShaderCapture.resetForGui();
      } else {
         CustomHandShaderCapture.beginCapture();
      }
   }

   @Inject(require = 0, method = "getBasicProjectionMatrix", at = @At("HEAD"), cancellable = true)
   public void getBasicProjectionMatrix(float f, CallbackInfoReturnable<Matrix4f> callbackInfoReturnable) {
      if (Zoom.a) {
         f = (float)Zoom.b;
      }

      MatrixStack MatrixStackVar = new MatrixStack();
      MatrixStackVar.peek().getPositionMatrix().identity();
      float fGetFramebufferWidth = (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / MinecraftClient.getInstance().getWindow().getFramebufferHeight();
      AspectRatio aspectRatio = ModuleRegistry.ASPECT_RATIO;
      if (aspectRatio.k()) {
         fGetFramebufferWidth = aspectRatio.n();
      }

      callbackInfoReturnable.setReturnValue(
         MatrixStackVar.peek().getPositionMatrix().perspective(f * (float) (Math.PI / 180.0), fGetFramebufferWidth, 0.05F, this.getFarPlaneDistance())
      );
   }
}
