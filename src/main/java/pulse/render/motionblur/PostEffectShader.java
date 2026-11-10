package pulse.render.motionblur;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.GpuBuffer.MappedView;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.client.gl.PostEffectPipeline;
import org.joml.Matrix4f;
import ru.pulse.mixin.PostEffectPassAccessor;
import ru.pulse.mixin.PostEffectProcessorAccessor;

public class PostEffectShader {
   private final Identifier location;
   private final Consumer<PostEffectShader> initCallback;
   private PostEffectProcessor processor;
   private boolean initialized = false;
   private boolean errored = false;
   private GpuBuffer paramsUbo;
   private final Matrix4f mvInverse = new Matrix4f();
   private final Matrix4f projInverse = new Matrix4f();
   private final Matrix4f prevModelView = new Matrix4f();
   private final Matrix4f prevProjection = new Matrix4f();
   private float cameraPosX;
   private float cameraPosY;
   private float cameraPosZ;
   private float prevCameraPosX;
   private float prevCameraPosY;
   private float prevCameraPosZ;
   private float viewResX = 1.0F;
   private float viewResY = 1.0F;
   private float blendFactor = 0.5F;
   private float inverseSamples = 1.0F;
   private float handDepthThreshold = 0.56F;
   private int motionBlurSamples = 0;
   private int halfSamples = 0;
   private int blurAlgorithm = 0;

   public PostEffectShader(Identifier location, Consumer<PostEffectShader> initCallback) {
      this.location = location;
      this.initCallback = initCallback;
   }

   public PostEffectShader(Identifier location) {
      this(location, s -> {});
   }

   private void ensureInitialized() {
      if (!this.initialized && !this.errored) {
         try {
            MinecraftClient client = MinecraftClient.getInstance();
            Identifier jsonPath = Identifier.of(this.location.getNamespace(), "post_effect/" + this.location.getPath() + ".json");
            Optional<Resource> resource = client.getResourceManager().getResource(jsonPath);
            if (resource.isPresent()) {
               try (Reader reader = resource.get().getReader()) {
                  JsonElement jsonElement = StrictJsonParser.parse(reader);
                  PostEffectPipeline pipeline = (PostEffectPipeline)PostEffectPipeline.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new);
                  ProjectionMatrix2 proj = new ProjectionMatrix2("post", 0.1F, 1000.0F, false);
                  this.processor = PostEffectProcessor.parseEffect(pipeline, client.getTextureManager(), Set.of(PostEffectProcessor.MAIN), this.location, proj);
                  if (this.processor != null) {
                     this.initialized = true;
                     this.initCallback.accept(this);
                  }
               }
            }
         } catch (Throwable e) {
            e.printStackTrace();
            this.errored = true;
         }
      }
   }

   public void render(float tickDelta) {
      this.ensureInitialized();
      if (this.processor != null) {
         this.replaceUniformBuffer();
         MinecraftClient client = MinecraftClient.getInstance();
         Framebuffer fb = client.getFramebuffer();
         if (fb != null) {
            try {
               for (Method m : this.processor.getClass().getDeclaredMethods()) {
                  if (m.getParameterCount() == 2 && Framebuffer.class.isAssignableFrom(m.getParameterTypes()[0])) {
                     Class<?> allocClass = m.getParameterTypes()[1];
                     Object allocator = this.getOrCreateAllocator(allocClass);
                     m.setAccessible(true);
                     m.invoke(this.processor, fb, allocator);
                     break;
                  }
               }
            } catch (Throwable t) {
               t.printStackTrace();
            }
         }
      }
   }

   private Object getOrCreateAllocator(Class<?> allocClass) {
      if (!allocClass.isInterface()) {
         return null;
      }

      for (Field f : allocClass.getDeclaredFields()) {
         if (Modifier.isStatic(f.getModifiers()) && allocClass.isAssignableFrom(f.getType())) {
            try {
               f.setAccessible(true);
               Object val = f.get(null);
               if (val != null) {
                  return val;
               }
            } catch (Exception var7) {
            }
         }
      }

      return Proxy.newProxyInstance(allocClass.getClassLoader(), new Class[]{allocClass}, (proxy, method, args) -> {
         if (args != null && args.length > 0 && args[0] != null) {
            Object factory = args[0];

            for (Method fm : factory.getClass().getDeclaredMethods()) {
               if (fm.getParameterCount() == 0 && !fm.getReturnType().equals(void.class)) {
                  try {
                     fm.setAccessible(true);
                     return fm.invoke(factory);
                  } catch (Exception var9) {
                  }
               }
            }
         }

         return null;
      });
   }

   private void replaceUniformBuffer() {
      if (this.processor != null) {
         List<PostEffectPass> passes = ((PostEffectProcessorAccessor)this.processor).getPasses();
         if (!passes.isEmpty()) {
            PostEffectPass motionBlurPass = passes.get(0);
            Map<String, GpuBuffer> uniformBuffers = ((PostEffectPassAccessor)motionBlurPass).getUniformBuffers();
            if (uniformBuffers.containsKey("MotionBlurParams")) {
               if (this.paramsUbo == null) {
                  this.paramsUbo = RenderSystem.getDevice().createBuffer(() -> "motion_blur MotionBlurParams UBO", 130, 320L);
                  uniformBuffers.put("MotionBlurParams", this.paramsUbo);
               } else if (uniformBuffers.get("MotionBlurParams") != this.paramsUbo) {
                  uniformBuffers.put("MotionBlurParams", this.paramsUbo);
               }

               try {
                  MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.paramsUbo, false, true);

                  try {
                     Std140Builder builder = Std140Builder.intoBuffer(view.data());
                     builder.putMat4f(this.mvInverse);
                     builder.putMat4f(this.projInverse);
                     builder.putMat4f(this.prevModelView);
                     builder.putMat4f(this.prevProjection);
                     builder.putVec3(this.cameraPosX, this.cameraPosY, this.cameraPosZ);
                     builder.putVec3(this.prevCameraPosX, this.prevCameraPosY, this.prevCameraPosZ);
                     builder.putVec2(this.viewResX, this.viewResY);
                     builder.putFloat(this.blendFactor);
                     builder.putFloat(this.inverseSamples);
                     builder.putFloat(this.handDepthThreshold);
                     builder.putInt(this.motionBlurSamples);
                     builder.putInt(this.halfSamples);
                     builder.putInt(this.blurAlgorithm);
                  } catch (Throwable var9) {
                     if (view != null) {
                        try {
                           view.close();
                        } catch (Throwable var8) {
                           var9.addSuppressed(var8);
                        }
                     }

                     throw var9;
                  }

                  if (view != null) {
                     view.close();
                  }
               } catch (Throwable e) {
                  try {
                     this.paramsUbo.close();
                  } catch (Throwable var7) {
                  }

                  this.paramsUbo = null;
               }
            }
         }
      }
   }

   public void setBlendFactor(float value) {
      this.blendFactor = value;
   }

   public void setViewRes(float x, float y) {
      this.viewResX = x;
      this.viewResY = y;
   }

   public void setMotionBlurSamples(int value) {
      this.motionBlurSamples = value;
   }

   public void setHalfSamples(int value) {
      this.halfSamples = value;
   }

   public void setInverseSamples(float value) {
      this.inverseSamples = value;
   }

   public void setBlurAlgorithm(int value) {
      this.blurAlgorithm = value;
   }

   public void setHandDepthThreshold(float value) {
      this.handDepthThreshold = value;
   }

   public void setMvInverse(Matrix4f mat) {
      this.mvInverse.set(mat);
   }

   public void setProjInverse(Matrix4f mat) {
      this.projInverse.set(mat);
   }

   public void setPrevModelView(Matrix4f mat) {
      this.prevModelView.set(mat);
   }

   public void setPrevProjection(Matrix4f mat) {
      this.prevProjection.set(mat);
   }

   public void setCameraPos(float x, float y, float z) {
      this.cameraPosX = x;
      this.cameraPosY = y;
      this.cameraPosZ = z;
   }

   public void setPrevCameraPos(float x, float y, float z) {
      this.prevCameraPosX = x;
      this.prevCameraPosY = y;
      this.prevCameraPosZ = z;
   }

   public boolean isInitialized() {
      return this.initialized;
   }
}
