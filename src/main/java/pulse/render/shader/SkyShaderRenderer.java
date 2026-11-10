package pulse.render.shader;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import pulse.module.ModuleRegistry;
import pulse.modules.visuals.WorldCustomizer;
import pulse.render.system.ClientPipelines;

public class SkyShaderRenderer {
   public static final SkyShaderRenderer INSTANCE = new SkyShaderRenderer();
   private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static final Vector3f MODEL_OFFSET = new Vector3f(0.0F, 0.0F, 0.0F);
   private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
   private static final int BUFFER_SIZE = 64;
   private GpuBuffer uniformBuffer;
   private GpuBuffer dummyVertexBuffer;
   private ByteBuffer dataBuffer;
   private boolean initialized = false;
   private long startMillis = -1L;

   private SkyShaderRenderer() {
   }

   public void renderShader() {
      MinecraftClient mc = MinecraftClient.getInstance();
      WorldCustomizer wc = ModuleRegistry.WORLD_CUSTOMIZER;
      if (wc != null && wc.k() && wc.enableShader.a()) {
         if (mc.player != null && mc.world != null && mc.getWindow() != null) {
            if (this.startMillis < 0L) {
               this.startMillis = System.currentTimeMillis();
            }

            float time = (float)(System.currentTimeMillis() - this.startMillis) / 1000.0F;
            int w = mc.getWindow().getFramebufferWidth();
            int h = mc.getWindow().getFramebufferHeight();
            if (w > 0 && h > 0) {
               Color color = new Color(wc.s());
               float yawRad = 0.0F;
               float pitchRad = 0.0F;
               if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
                  Camera cam = mc.gameRenderer.getCamera();
                  yawRad = (float)Math.toRadians(-cam.getYaw());
                  pitchRad = (float)Math.toRadians(cam.getPitch());
               }

               float fov = 70.0F;
               if (mc.options != null && mc.options.getFov() != null) {
                  fov = ((Integer)mc.options.getFov().getValue()).intValue();
               }

               String mode = wc.shaderMode.d();
               GpuTextureView screenTexture = mc.getFramebuffer().getColorAttachmentView();
               if (screenTexture != null) {
                  this.renderToTarget(screenTexture, mode, w, h, yawRad, pitchRad, color, time, 1.0F, 1.0F, 5.0F, 0.02F, fov);
               }
            }
         }
      }
   }

   private void renderToTarget(
      GpuTextureView target,
      String mode,
      int w,
      int h,
      float yaw,
      float pitch,
      Color color,
      float time,
      float alpha,
      float speed,
      float scale,
      float intensity,
      float fov
   ) {
      if (target != null && w > 0 && h > 0) {
         this.ensureInitialized();
         this.prepareUniformData(w, h, yaw, pitch, color, time, alpha, speed, scale, intensity, fov);
         CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
         this.uploadUniform(encoder);
         GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
            .write(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
         if (mode == null) {
            mode = "Nebula";
         }

         String passName;
         RenderPipeline pipeline;
         switch (mode) {
            case "Galaxy":
            case "Lava":
            case "Sunset":
               passName = "pulse:sky_plasma";
               pipeline = ClientPipelines.SKY_PLASMA_PIPELINE;
               break;
            case "Cosmos":
            case "Bloom":
               passName = "pulse:sky_bloom";
               pipeline = ClientPipelines.SKY_BLOOM_PIPELINE;
               break;
            case "Water":
            case "Caustic":
               passName = "pulse:sky_caustic";
               pipeline = ClientPipelines.SKY_CAUSTIC_PIPELINE;
               break;
            case "BlackHole":
            case "Drain":
               passName = "pulse:sky_drain";
               pipeline = ClientPipelines.SKY_DRAIN_PIPELINE;
               break;
            case "Aurora":
            case "Nebula":
            default:
               passName = "pulse:sky_nebula";
               pipeline = ClientPipelines.SKY_NEBULA_PIPELINE;
         }

         RenderPass renderPass = encoder.createRenderPass(() -> passName, target, OptionalInt.empty());

         try {
            renderPass.setPipeline(pipeline);
            renderPass.setVertexBuffer(0, this.dummyVertexBuffer);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setUniform("ShaderFogData", this.uniformBuffer);
            renderPass.draw(0, 6);
         } catch (Throwable var22) {
            if (renderPass != null) {
               try {
                  renderPass.close();
               } catch (Throwable var21) {
                  var22.addSuppressed(var21);
               }
            }

            throw var22;
         }

         if (renderPass != null) {
            renderPass.close();
         }
      }
   }

   private void ensureInitialized() {
      if (!this.initialized) {
         this.dataBuffer = MemoryUtil.memAlloc(64);
         this.dummyVertexBuffer = createDummyVertexBuffer("pulse:sky_shader_dummy_vertex");
         this.initialized = true;
      }
   }

   private void prepareUniformData(
      float w, float h, float yaw, float pitch, Color color, float time, float alpha, float speed, float scale, float intensity, float fov
   ) {
      this.dataBuffer.clear();
      this.dataBuffer.putFloat(w);
      this.dataBuffer.putFloat(h);
      this.dataBuffer.putFloat(yaw);
      this.dataBuffer.putFloat(pitch);
      this.dataBuffer.putFloat(color.getRed() / 255.0F);
      this.dataBuffer.putFloat(color.getGreen() / 255.0F);
      this.dataBuffer.putFloat(color.getBlue() / 255.0F);
      this.dataBuffer.putFloat(time);
      this.dataBuffer.putFloat(alpha);
      this.dataBuffer.putFloat(speed);
      this.dataBuffer.putFloat(scale);
      this.dataBuffer.putFloat(intensity);
      this.dataBuffer.putFloat(fov);
      this.dataBuffer.putFloat(0.0F);
      this.dataBuffer.putFloat(0.0F);
      this.dataBuffer.putFloat(0.0F);
      this.dataBuffer.flip();
   }

   private void uploadUniform(CommandEncoder encoder) {
      int size = this.dataBuffer.remaining();
      if (this.uniformBuffer == null || this.uniformBuffer.size() < size) {
         if (this.uniformBuffer != null) {
            this.uniformBuffer.close();
         }

         this.uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "pulse:sky_shader_uniform", 136, size);
      }

      encoder.writeToBuffer(this.uniformBuffer.slice(), this.dataBuffer);
   }

   private static GpuBuffer createDummyVertexBuffer(String name) {
      ByteBuffer dummyData = MemoryUtil.memAlloc(4);

      try {
         dummyData.putInt(0);
         dummyData.flip();
         return RenderSystem.getDevice().createBuffer(() -> name, 32, dummyData);
      } finally {
         MemoryUtil.memFree(dummyData);
      }
   }

   public void close() {
      if (this.uniformBuffer != null) {
         this.uniformBuffer.close();
         this.uniformBuffer = null;
      }

      if (this.dummyVertexBuffer != null) {
         this.dummyVertexBuffer.close();
         this.dummyVertexBuffer = null;
      }

      if (this.dataBuffer != null) {
         MemoryUtil.memFree(this.dataBuffer);
         this.dataBuffer = null;
      }

      this.initialized = false;
      this.startMillis = -1L;
   }
}
