package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import net.minecraft.client.render.BuiltBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import pulse.render.shader.PulseShaderProgram;
import pulse.render.shader.ShaderLibrary;

public class BufferRenderer {
   private static int vao = -1;
   private static int vbo = -1;
   private static int ebo = -1;
   private static int maxIndices = 0;

   private static void init(int quadCount) {
      if (vao == -1) {
         vao = GL30.glGenVertexArrays();
         vbo = GL30.glGenBuffers();
         ebo = GL30.glGenBuffers();
         GL30.glBindVertexArray(vao);
         GL30.glBindBuffer(34962, vbo);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 3, 5126, false, 24, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, 24, 12L);
         GL20.glEnableVertexAttribArray(2);
         GL20.glVertexAttribPointer(2, 4, 5121, true, 24, 20L);
         GL30.glBindVertexArray(0);
         GL30.glBindBuffer(34962, 0);
      }

      if (quadCount * 6 > maxIndices) {
         maxIndices = Math.max(quadCount * 6, 6000);
         IntBuffer ib = BufferUtils.createIntBuffer(maxIndices);

         for (int i = 0; i < maxIndices / 6; i++) {
            int base = i * 4;
            ib.put(base);
            ib.put(base + 1);
            ib.put(base + 2);
            ib.put(base);
            ib.put(base + 2);
            ib.put(base + 3);
         }

         ib.flip();
         GL30.glBindBuffer(34963, ebo);
         GL15.glBufferData(34963, ib, 35044);
         GL30.glBindBuffer(34963, 0);
      }
   }

   public static void drawWithGlobalProgram(BuiltBuffer builtBuffer) {
      if (builtBuffer != null) {
         try {
            ByteBuffer buffer = builtBuffer.getBuffer();
            int vertexCount = builtBuffer.getDrawParameters().comp_750();
            if (buffer != null && vertexCount >= 4) {
               int quadCount = vertexCount / 4;
               init(quadCount);
               int prevVao = GL30.glGetInteger(36006);
               int prevVbo = GL30.glGetInteger(34229);
               boolean boundPassthrough = false;
               Optional<PulseShaderProgram> shaderOpt = ShaderLibrary.getRegistry().find("passthrough_color");
               if (!PulseShaderProgram.isCustomShaderBound && shaderOpt.isPresent()) {
                  PulseShaderProgram shader = shaderOpt.get();
                  shader.d();
                  shader.b("Sampler0", 0);
                  boundPassthrough = true;
               }

               GL30.glBindVertexArray(vao);
               GL30.glBindBuffer(34962, vbo);
               if (buffer.remaining() == 0 && buffer.position() > 0) {
                  buffer.flip();
               }

               GL15.glBufferData(34962, buffer, 35048);
               int stride = vertexCount > 0 ? buffer.remaining() / vertexCount : 24;
               GL20.glEnableVertexAttribArray(0);
               GL20.glVertexAttribPointer(0, 3, 5126, false, stride, 0L);
               GL20.glEnableVertexAttribArray(1);
               GL20.glVertexAttribPointer(1, 2, 5126, false, stride, 12L);
               if (stride >= 24) {
                  GL20.glEnableVertexAttribArray(2);
                  GL20.glVertexAttribPointer(2, 4, 5121, true, stride, 20L);
               } else {
                  GL20.glDisableVertexAttribArray(2);
                  GL20.glVertexAttrib4f(2, 1.0F, 1.0F, 1.0F, 1.0F);
               }

               GL30.glBindBuffer(34963, ebo);
               GL11.glDrawElements(4, quadCount * 6, 5125, 0L);
               GL30.glBindVertexArray(prevVao);
               GL30.glBindBuffer(34962, prevVbo);
               if (boundPassthrough) {
                  shaderOpt.get().e();
               }
            }
         } catch (Throwable var12) {
         } finally {
            builtBuffer.close();
         }
      }
   }

   public static void draw(BuiltBuffer builtBuffer) {
      drawWithGlobalProgram(builtBuffer);
   }
}
