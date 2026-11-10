package pulse.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import net.minecraft.client.util.Window;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class RenderSystemHelper {
   public static void enableBlend() {
      GlStateManager._enableBlend();
   }

   public static void disableBlend() {
      GlStateManager._disableBlend();
   }

   public static void defaultBlendFunc() {
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
   }

   public static void applyBlendState() {
   }

   public static void blendFunc(int srcFactor, int dstFactor) {
      GlStateManager._blendFuncSeparate(srcFactor, dstFactor, 1, 0);
   }

   public static void blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      GlStateManager._blendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
   }

   public static void enableDepthTest() {
      GlStateManager._enableDepthTest();
   }

   public static void disableDepthTest() {
      GlStateManager._disableDepthTest();
   }

   public static void depthMask(boolean mask) {
      GlStateManager._depthMask(mask);
   }

   public static void depthFunc(int func) {
      GlStateManager._depthFunc(func);
   }

   public static void enableCull() {
      GlStateManager._enableCull();
   }

   public static void disableCull() {
      GlStateManager._disableCull();
   }

   public static void setShaderColor(float r, float g, float b, float a) {
      try {
         for (Method m : RenderSystem.class.getMethods()) {
            if (m.getName().equalsIgnoreCase("setShaderColor") && m.getParameterCount() == 4) {
               m.invoke(null, r, g, b, a);
               return;
            }
         }
      } catch (Throwable ignored) {
         ignored.printStackTrace();
      }
   }

   public static void setShaderTexture(int unit, int textureId) {
      GlStateManager._activeTexture(33984 + unit);
      GlStateManager._bindTexture(textureId);
   }

   public static void setShaderTexture(int unit, Identifier texture) {
      try {
         GlStateManager._activeTexture(33984 + unit);
         AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(texture);
         if (tex != null) {
            int glId = getGlIdFromTexture(tex);
            if (glId > 0) {
               GlStateManager._bindTexture(glId);
            }
         }
      } catch (Throwable var4) {
      }
   }

   public static int getGlIdFromTexture(AbstractTexture tex) {
      if (tex == null) {
         return 0;
      }

      try {
         for (Method m : tex.getClass().getMethods()) {
            if ((m.getName().equalsIgnoreCase("getGlId") || m.getName().equalsIgnoreCase("getGlTextureId") || m.getName().equalsIgnoreCase("getTextureId"))
               && m.getParameterCount() == 0
               && m.getReturnType() == int.class) {
               return (Integer)m.invoke(tex);
            }
         }
      } catch (Throwable var9) {
      }

      try {
         Object glTex = tex.getGlTexture();
         if (glTex != null) {
            for (Method m : glTex.getClass().getMethods()) {
               if ((m.getName().equalsIgnoreCase("getGlId") || m.getName().equalsIgnoreCase("getGlTextureId") || m.getName().equalsIgnoreCase("getTextureId"))
                  && m.getParameterCount() == 0
                  && m.getReturnType() == int.class) {
                  return (Integer)m.invoke(glTex);
               }
            }

            for (Field f : glTex.getClass().getDeclaredFields()) {
               if (f.getType() == int.class) {
                  f.setAccessible(true);
                  int val = f.getInt(glTex);
                  if (val > 0) {
                     return val;
                  }
               }
            }
         }
      } catch (Throwable var8) {
      }

      try {
         for (Field f : tex.getClass().getDeclaredFields()) {
            if (f.getType() == int.class) {
               f.setAccessible(true);
               int val = f.getInt(tex);
               if (val > 0) {
                  return val;
               }
            }
         }
      } catch (Throwable var7) {
      }

      return 0;
   }

   public static void setShader(Object shader) {
      try {
         for (Method m : RenderSystem.class.getMethods()) {
            if (m.getName().equals("setShader") && m.getParameterCount() == 1) {
               m.invoke(null, shader);
               return;
            }
         }
      } catch (Throwable var5) {
      }
   }

   public static void setShader(Supplier<?> shader) {
      setShader((Object)shader);
   }

   public static Matrix4f getProjectionMatrix() {
      try {
         Window window = MinecraftClient.getInstance().getWindow();
         if (window != null && window.getFramebufferWidth() > 0 && window.getFramebufferHeight() > 0) {
            float w = window.getScaledWidth();
            float h = window.getScaledHeight();
            return new Matrix4f().setOrtho(0.0F, w, h, 0.0F, -10000.0F, 10000.0F);
         }
      } catch (Throwable var3) {
      }

      return new Matrix4f().setOrtho(0.0F, 960.0F, 540.0F, 0.0F, -10000.0F, 10000.0F);
   }

   public static void setClearColor(float r, float g, float b, float a) {
   }

   public static void recordRenderCall(Runnable runnable) {
      runnable.run();
   }

   public static void lineWidth(float width) {
      try {
         GL11.glLineWidth(width);
      } catch (Throwable var2) {
      }
   }
}
