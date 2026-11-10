package pulse.render.motionblur;

import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pulse.module.ModuleRegistry;
import pulse.modules.visuals.MotionBlur;

public class MotionBlurManager {
   public static final MotionBlurManager INSTANCE = new MotionBlurManager();
   private final PostEffectShader motionBlurShader;
   private final Matrix4f tempPrevModelView = new Matrix4f();
   private final Matrix4f tempPrevProjection = new Matrix4f();
   private final Matrix4f tempProjInverse = new Matrix4f();
   private final Matrix4f tempMvInverse = new Matrix4f();

   private MotionBlurManager() {
      this.motionBlurShader = new PostEffectShader(Identifier.of("pulse", "motion_blur"), shader -> {
         MotionBlur mod = ModuleRegistry.MOTION_BLUR;
         float strength = mod != null ? mod.blurAmount.k() / 100.0F : 0.5F;
         shader.setBlendFactor(strength);
      });
   }

   public void setFrameMotionBlur(
      Matrix4f modelView, Matrix4f prevModelView, Matrix4f projection, Matrix4f prevProjection, Vector3f cameraPos, Vector3f prevCameraPos
   ) {
      this.tempMvInverse.set(modelView).invert();
      this.tempProjInverse.set(projection).invert();
      this.motionBlurShader.setMvInverse(this.tempMvInverse);
      this.motionBlurShader.setProjInverse(this.tempProjInverse);
      this.motionBlurShader.setPrevModelView(prevModelView);
      this.motionBlurShader.setPrevProjection(prevProjection);
      this.motionBlurShader.setCameraPos(cameraPos.x, cameraPos.y, cameraPos.z);
      this.motionBlurShader.setPrevCameraPos(prevCameraPos.x, prevCameraPos.y, prevCameraPos.z);
   }

   public void applyMotionBlurBeforeHands() {
      MotionBlur mod = ModuleRegistry.MOTION_BLUR;
      if (mod != null && mod.k()) {
         MinecraftClient client = MinecraftClient.getInstance();
         if (client.getFramebuffer() != null) {
            float strength = mod.blurAmount.k() / 50.0F;
            int sampleAmount = 100;
            this.motionBlurShader.setBlendFactor(strength);
            this.motionBlurShader.setViewRes(client.getFramebuffer().textureWidth, client.getFramebuffer().textureHeight);
            this.motionBlurShader.setMotionBlurSamples(sampleAmount);
            this.motionBlurShader.setHalfSamples(sampleAmount / 2);
            this.motionBlurShader.setInverseSamples(1.0F / sampleAmount);
            this.motionBlurShader.setBlurAlgorithm(1);
            this.motionBlurShader.setHandDepthThreshold(0.56F);
            this.motionBlurShader.render(0.0F);
         }
      }
   }
}
