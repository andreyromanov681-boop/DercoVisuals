// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.awt.Color;
import java.lang.reflect.Field;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.NativeImage.class_1012;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Renderer2DImpl implements Renderer2D {
   private final ScissorStack a = new ScissorStack(this);
   public static DrawContext currentDrawContext;
   private static Identifier ROUNDED_CORNERS_TEXTURE = null;
   private static boolean textureRegistered = false;
   private static Identifier RADIAL_GLOW_TEXTURE = null;
   private static boolean glowTextureRegistered = false;

   public void setDrawContext(DrawContext drawContext) {
      currentDrawContext = drawContext;
   }

   public DrawContext getDrawContext() {
      return currentDrawContext;
   }

   private Matrix4f getMatrix(Object MatrixStackVar) {
      Matrix4f m = new Matrix4f();
      if (MatrixStackVar instanceof Matrix3x2fStack m32) {
         m.m00(m32.m00());
         m.m01(m32.m01());
         m.m10(m32.m10());
         m.m11(m32.m11());
         m.m30(m32.m20());
         m.m31(m32.m21());
      } else if (MatrixStackVar instanceof MatrixStack ms) {
         m.set(ms.peek().getPositionMatrix());
      }

      return m;
   }

   private void ensureRoundedTexture() {
      if (!textureRegistered) {
         textureRegistered = true;

         try {
            ROUNDED_CORNERS_TEXTURE = Identifier.of("dercovisuals", "gui/rounded_corners");
            NativeImage image = new NativeImage(class_1012.RGBA, 64, 64, false);

            for (int y = 0; y < 64; y++) {
               for (int x = 0; x < 64; x++) {
                  double dx = x < 32 ? 31.5 - x : x - 31.5;
                  double dy = y < 32 ? 31.5 - y : y - 31.5;
                  double dist = Math.sqrt(dx * dx + dy * dy);
                  int alpha = 255;
                  if (dist > 30.5) {
                     alpha = (int)Math.round(255.0 * (32.0 - dist) / 1.5);
                     if (alpha < 0) {
                        alpha = 0;
                     }

                     if (alpha > 255) {
                        alpha = 255;
                     }
                  }

                  image.setColorArgb(x, y, alpha << 24 | 16777215);
               }
            }

            NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> "dercovisuals", image);
            texture.upload();
            MinecraftClient.getInstance().getTextureManager().registerTexture(ROUNDED_CORNERS_TEXTURE, texture);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void ensureRadialGlowTexture() {
      if (!glowTextureRegistered) {
         glowTextureRegistered = true;

         try {
            RADIAL_GLOW_TEXTURE = Identifier.of("dercovisuals", "gui/radial_glow");
            NativeImage image = new NativeImage(class_1012.RGBA, 256, 256, false);

            for (int y = 0; y < 256; y++) {
               for (int x = 0; x < 256; x++) {
                  double dx = x - 127.5;
                  double dy = y - 127.5;
                  double dist = Math.sqrt(dx * dx + dy * dy);
                  int alpha = 0;
                  if (dist <= 127.5) {
                     double t = dist / 127.5;
                     double smooth = t * t * (3.0 - 2.0 * t);
                     alpha = (int)Math.round(255.0 * Math.pow(1.0 - smooth, 1.2));
                     if (alpha < 0) {
                        alpha = 0;
                     }

                     if (alpha > 255) {
                        alpha = 255;
                     }
                  }

                  image.setColorArgb(x, y, alpha << 24 | 16777215);
               }
            }

            NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> "dercovisuals", image);
            texture.upload();
            MinecraftClient.getInstance().getTextureManager().registerTexture(RADIAL_GLOW_TEXTURE, texture);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private void drawRoundedRect(float f, float f2, float f3, float f4, float f5, int color) {
      if (currentDrawContext != null) {
         int x = Math.round(f);
         int y = Math.round(f2);
         int w = Math.round(f3);
         int h = Math.round(f4);
         int r = Math.min(Math.round(f5), Math.min(w / 2, h / 2));
         if (r <= 0) {
            currentDrawContext.fill(x, y, x + w, y + h, color);
         } else {
            this.ensureRoundedTexture();
            if (ROUNDED_CORNERS_TEXTURE == null) {
               currentDrawContext.fill(x, y, x + w, y + h, color);
            } else {
               currentDrawContext.fill(x, y + r, x + w, y + h - r, color);
               currentDrawContext.fill(x + r, y, x + w - r, y + r, color);
               currentDrawContext.fill(x + r, y + h - r, x + w - r, y + h, color);
               setTextureFilter(ROUNDED_CORNERS_TEXTURE, true);
               float scale = r / 32.0F;
               Matrix3x2fStack matrices = currentDrawContext.getMatrices();
               matrices.pushMatrix();
               matrices.translate(x, y);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 0.0F, 0.0F, 32, 32, 64, 64, color);
               matrices.popMatrix();
               matrices.pushMatrix();
               matrices.translate(x + w - r, y);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 32.0F, 0.0F, 32, 32, 64, 64, color);
               matrices.popMatrix();
               matrices.pushMatrix();
               matrices.translate(x, y + h - r);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 0.0F, 32.0F, 32, 32, 64, 64, color);
               matrices.popMatrix();
               matrices.pushMatrix();
               matrices.translate(x + w - r, y + h - r);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 32.0F, 32.0F, 32, 32, 64, 64, color);
               matrices.popMatrix();
            }
         }
      }
   }

   private void drawRoundedGradient(float f, float f2, float f3, float f4, float f5, Color c1, Color c2, Color c3, Color c4) {
      if (currentDrawContext != null && c1 != null && c3 != null) {
         int x = Math.round(f);
         int y = Math.round(f2);
         int w = Math.round(f3);
         int h = Math.round(f4);
         int r = Math.min(Math.round(f5), Math.min(w / 2, h / 2));
         if (r <= 0) {
            currentDrawContext.fillGradient(x, y, x + w, y + h, c1.getRGB(), c3.getRGB());
         } else {
            this.ensureRoundedTexture();
            if (ROUNDED_CORNERS_TEXTURE == null) {
               currentDrawContext.fillGradient(x, y, x + w, y + h, c1.getRGB(), c3.getRGB());
            } else {
               currentDrawContext.fillGradient(x, y + r, x + w, y + h - r, c1.getRGB(), c3.getRGB());
               currentDrawContext.fillGradient(x + r, y, x + w - r, y + r, c1.getRGB(), c2 != null ? c2.getRGB() : c1.getRGB());
               currentDrawContext.fillGradient(x + r, y + h - r, x + w - r, y + h, c4 != null ? c4.getRGB() : c3.getRGB(), c3.getRGB());
               setTextureFilter(ROUNDED_CORNERS_TEXTURE, true);
               float scale = r / 32.0F;
               Matrix3x2fStack matrices = currentDrawContext.getMatrices();
               matrices.pushMatrix();
               matrices.translate(x, y);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 0.0F, 0.0F, 32, 32, 64, 64, c1.getRGB());
               matrices.popMatrix();
               matrices.pushMatrix();
               matrices.translate(x + w - r, y);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(
                  RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 32.0F, 0.0F, 32, 32, 64, 64, c2 != null ? c2.getRGB() : c1.getRGB()
               );
               matrices.popMatrix();
               matrices.pushMatrix();
               matrices.translate(x, y + h - r);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(
                  RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 0.0F, 32.0F, 32, 32, 64, 64, c4 != null ? c4.getRGB() : c3.getRGB()
               );
               matrices.popMatrix();
               matrices.pushMatrix();
               matrices.translate(x + w - r, y + h - r);
               matrices.scale(scale, scale);
               currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, ROUNDED_CORNERS_TEXTURE, 0, 0, 32.0F, 32.0F, 32, 32, 64, 64, c3.getRGB());
               matrices.popMatrix();
            }
         }
      }
   }

   @Override
   public void a(float f, float f2, float f3, float f4, float f5, Color color, Object MatrixStackVar) {
      if (color != null && color.getAlpha() != 0 && currentDrawContext != null) {
         this.drawRoundedRect(f, f2, f3, f4, f5, color.getRGB());
      }
   }

   @Override
   public void a(float f, float f2, float f3, float f4, Object MatrixStackVar) {
      if (currentDrawContext != null) {
         currentDrawContext.fill((int)f, (int)f2, (int)(f + f3), (int)(f2 + f4), -1);
      }
   }

   @Override
   public Window a() {
      return MinecraftClient.getInstance().getWindow();
   }

   @Override
   public void b(float f, float f2, float f3, float f4, float f5, float f6, Color color, Object MatrixStackVar) {
      if (currentDrawContext != null && color != null) {
         currentDrawContext.fill((int)f, (int)f2, (int)f3, (int)f4, color.getRGB());
      }
   }

   @Override
   public void a(int i, float f, float f2, float f3, float f4, Color color, Object MatrixStackVar) {
      if (currentDrawContext != null && color != null) {
         currentDrawContext.fill((int)f, (int)f2, (int)(f + f3), (int)(f2 + f4), color.getRGB());
      }
   }

   public static void setTextureFilter(Identifier id, boolean linear) {
      if (id != null) {
         try {
            AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(id);
            if (texture != null) {
               FilterMode mode = linear ? FilterMode.LINEAR : FilterMode.NEAREST;
               GpuSampler newSampler = RenderSystem.getSamplerCache().getRepeated(mode);
               if (texture.getSampler() != newSampler) {
                  for (Field field : AbstractTexture.class.getDeclaredFields()) {
                     if (GpuSampler.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        field.set(texture, newSampler);
                        break;
                     }
                  }
               }
            }
         } catch (Throwable var9) {
         }
      }
   }

   @Override
   public void a(Identifier IdentifierVar, float f, float f2, float f3, float f4, Color color, Object MatrixStackVar) {
      if (IdentifierVar != null && currentDrawContext != null) {
         int c = color != null ? color.getRGB() : -1;
         currentDrawContext.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            IdentifierVar,
            Math.round(f),
            Math.round(f2),
            0.0F,
            0.0F,
            Math.round(f3),
            Math.round(f4),
            Math.round(f3),
            Math.round(f4),
            c
         );
      }
   }

   @Override
   public void a(float f, float f2, float f3, Color color, Object MatrixStackVar) {
      if (color != null && color.getAlpha() != 0 && currentDrawContext != null) {
         this.ensureRadialGlowTexture();
         if (RADIAL_GLOW_TEXTURE != null) {
            setTextureFilter(RADIAL_GLOW_TEXTURE, true);
            int x = Math.round(f - f3);
            int y = Math.round(f2 - f3);
            int size = Math.round(f3 * 2.0F);
            currentDrawContext.drawTexture(RenderPipelines.GUI_TEXTURED, RADIAL_GLOW_TEXTURE, x, y, 0.0F, 0.0F, size, size, size, size, color.getRGB());
         }
      }
   }

   @Override
   public void a(float f, float f2, float f3, float f4, float f5, float f6, Color color, Object MatrixStackVar) {
      if (color != null && currentDrawContext != null) {
         this.drawRoundedRect(f, f2, f3, f4, f5, color.getRGB());
      }
   }

   @Override
   public void a(
      Identifier IdentifierVar, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, Color color, Object MatrixStackVar
   ) {
      if (IdentifierVar != null && currentDrawContext != null) {
         int c = color != null ? color.getRGB() : -1;
         if (f8 != 1.0F && f8 != -1.0F || f9 != 1.0F && f9 != -1.0F) {
            if (f8 > 0.0F && f8 <= 1.0F && f9 > 0.0F && f9 <= 1.0F) {
               float texSize = 64.0F;
               float u = f6 * texSize;
               float v = f7 * texSize;
               float regionW = f8 * texSize;
               float regionH = f9 * texSize;
               Matrix3x2fStack matrices = currentDrawContext.getMatrices();
               matrices.pushMatrix();
               matrices.translate(f, f2);
               matrices.scale(f3 / regionW, f4 / regionH);
               currentDrawContext.drawTexture(
                  RenderPipelines.GUI_TEXTURED, IdentifierVar, 0, 0, u, v, Math.round(regionW), Math.round(regionH), (int)texSize, (int)texSize, c
               );
               matrices.popMatrix();
            } else {
               int tw = Math.round(f8);
               int th = Math.round(f9);
               if (tw <= 0 || th <= 0) {
                  tw = Math.round(f3);
                  th = Math.round(f4);
                  f6 = 0.0F;
                  f7 = 0.0F;
               }

               currentDrawContext.drawTexture(
                  RenderPipelines.GUI_TEXTURED, IdentifierVar, Math.round(f), Math.round(f2), f6, f7, Math.round(f3), Math.round(f4), tw, th, c
               );
            }
         } else {
            currentDrawContext.drawTexture(
               RenderPipelines.GUI_TEXTURED,
               IdentifierVar,
               Math.round(f),
               Math.round(f2),
               0.0F,
               0.0F,
               Math.round(f3),
               Math.round(f4),
               Math.round(f3),
               Math.round(f4),
               c
            );
         }
      }
   }

   @Override
   public void b(float f, float f2, float f3, float f4, Object MatrixStackVar) {
      Window WindowVarGetWindow = MinecraftClient.getInstance().getWindow();
      float fD = ScreenScale.d();
      GL11.glEnable(3089);
      GL11.glScissor((int)(f * fD), (int)(WindowVarGetWindow.getFramebufferHeight() - (f2 + f4) * fD), (int)(f3 * fD), (int)(f4 * fD));
      if (currentDrawContext != null) {
         currentDrawContext.enableScissor((int)f, (int)f2, (int)(f + f3), (int)(f2 + f4));
      }
   }

   @Override
   public void a(float f, float f2, float f3, float f4, float f5, Object MatrixStackVar) {
      this.b(f, f2, f3, f4, MatrixStackVar);
   }

   @Override
   public void a(Object MatrixStackVar) {
      GL11.glDisable(3089);
      if (currentDrawContext != null) {
         currentDrawContext.disableScissor();
      }
   }

   @Override
   public ScissorStack b() {
      return this.a;
   }

   @Override
   public void a(
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      Color color,
      Color color2,
      Color color3,
      Color color4,
      Object MatrixStackVar
   ) {
      if (color != null && color.getAlpha() != 0 && currentDrawContext != null) {
         this.drawRoundedGradient(f, f2, f3, f4, f5, color, color2, color3, color4);
      }
   }

   @Override
   public void a(float f, float f2, float f3, float f4, float f5, float f6, Object MatrixStackVar) {
      if (currentDrawContext != null) {
         int alpha = f6 > 1.0F ? (int)Math.min(255.0F, f6) : (int)(Math.max(0.0F, Math.min(1.0F, f6)) * 255.0F);
         if (alpha <= 0) {
            alpha = 255;
         }

         int steps = (int)Math.max(1.0F, f3);

         for (int i = 0; i < steps; i++) {
            float hue = (float)i / steps;
            Color col = Color.getHSBColor(hue, 1.0F, 1.0F);
            int rgb = (alpha & 0xFF) << 24 | col.getRed() << 16 | col.getGreen() << 8 | col.getBlue();
            int x1 = (int)(f + i);
            int x2 = (int)(f + i + 1.0F);
            if (i == steps - 1) {
               x2 = (int)(f + f3);
            }

            currentDrawContext.fill(x1, (int)f2, x2, (int)(f2 + f4), rgb);
         }
      }
   }

   @Override
   public void c(float f, float f2, float f3, float f4, Object MatrixStackVar) {
      if (MatrixStackVar instanceof Matrix3x2fStack ms) {
         FramebufferCapture.begin(f, f2, f3, f4);
      }
   }

   @Override
   public void a(float f, Object MatrixStackVar) {
      if (MatrixStackVar instanceof Matrix3x2fStack ms) {
         FramebufferCapture.end(this, f, ms);
      }
   }
}
