package pulse.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import pulse.render.RenderSystemHelper;
import ru.pulse.Pulse;

public class PulseShaderProgram {
   private static final FloatBuffer g = BufferUtils.createFloatBuffer(16);
   private static int e = -1;
   private static int f = -1;
   private final int c = GL30.glCreateProgram();
   private boolean d = true;
   private final Map<String, Integer> h = new HashMap<>();
   public static boolean isCustomShaderBound = false;
   private static FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(1024);

   public int a() {
      return this.c;
   }

   public boolean b() {
      return this.d;
   }

   public void a(String str, int i) {
      int iGlCreateShader = GL30.glCreateShader(i);
      GL30.glShaderSource(iGlCreateShader, str);
      GL30.glCompileShader(iGlCreateShader);
      if (GL30.glGetShaderi(iGlCreateShader, 35713) != 0) {
         GL30.glAttachShader(this.c, iGlCreateShader);
      } else {
         Pulse.getLOGGER().error("Shader {} compile error: \n{}", iGlCreateShader, GL30.glGetShaderInfoLog(iGlCreateShader));
         this.d = false;
      }
   }

   public void c() {
      GL30.glBindAttribLocation(this.c, 0, "Position");
      GL30.glBindAttribLocation(this.c, 1, "UV0");
      GL30.glBindAttribLocation(this.c, 2, "Color");
      GL30.glLinkProgram(this.c);
      if (GL30.glGetProgrami(this.c, 35714) == 0) {
         Pulse.getLOGGER().error("Program {} link error: \n{}", this.c, GL30.glGetProgramInfoLog(this.c));
         this.d = false;
      }
   }

   public void d() {
      this.d(new Matrix4f());
   }

   public void d(Matrix4f modelViewMatrix) {
      if (this.d) {
         isCustomShaderBound = true;
         GlStateManager._disableDepthTest();
         GlStateManager._disableScissorTest();
         GlStateManager._disableCull();
         GlStateManager._enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         GL30.glUseProgram(this.c);
         Matrix4f projectionMatrix = RenderSystemHelper.getProjectionMatrix();
         projectionMatrix.get(g);
         g.rewind();
         int iA = this.a("ProjMat");
         if (iA != -1) {
            GL30.glUniformMatrix4fv(iA, false, g);
         } else {
            Pulse.getLOGGER().error("[Pulse DEBUG] ProjMat uniform NOT FOUND!");
         }

         if (!Pulse.getLOGGER().isDebugEnabled()) {
         }

         float m00 = g.get(0);
         float m11 = g.get(5);
         float m22 = g.get(10);
         float m30 = g.get(12);
         float m31 = g.get(13);
         float m32 = g.get(14);
         System.out.println("[Pulse DEBUG] ProjMat: m00=" + m00 + " m11=" + m11 + " m22=" + m22 + " m30=" + m30 + " m31=" + m31 + " m32=" + m32);
         modelViewMatrix.get(g);
         g.rewind();
         int iA2 = this.a("ModelViewMat");
         if (iA2 != -1) {
            GL30.glUniformMatrix4fv(iA2, false, g);
         } else {
            Pulse.getLOGGER().error("[Pulse DEBUG] ModelViewMat uniform NOT FOUND!");
         }

         MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
         this.a("resolution", MinecraftClientVarGetInstance.getWindow().getScaledWidth(), MinecraftClientVarGetInstance.getWindow().getScaledHeight());
      }
   }

   public void e() {
      GlStateManager._glUseProgram(0);
      RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      isCustomShaderBound = false;
   }

   public static void a(float f2, float f3, float f4, float f5) {
      float[] fArr = new float[]{f2, f3, 0.0F, 0.0F, 0.0F, f2, f3 + f5, 0.0F, 0.0F, 1.0F, f2 + f4, f3 + f5, 0.0F, 1.0F, 1.0F, f2 + f4, f3, 0.0F, 1.0F, 0.0F};
      a(fArr, 6);
   }

   public static void a(float[] fArr, int i) {
      if (e == -1) {
         f();
      }

      if (vboBuffer.capacity() < fArr.length) {
         vboBuffer = BufferUtils.createFloatBuffer(fArr.length * 2);
      }

      GL30.glBindVertexArray(e);
      GL30.glBindBuffer(34962, f);
      vboBuffer.clear();
      vboBuffer.put(fArr);
      vboBuffer.flip();
      GL30.glBufferData(34962, vboBuffer, 35048);
      GL30.glEnableVertexAttribArray(0);
      GL30.glVertexAttribPointer(0, 3, 5126, false, 20, 0L);
      GL30.glEnableVertexAttribArray(1);
      GL30.glVertexAttribPointer(1, 2, 5126, false, 20, 12L);
      int mode = i != 4 && i != 7 && i != 6 ? i : 6;
      GL30.glDrawArrays(mode, 0, fArr.length / 5);
      GL30.glBindBuffer(34962, 0);
      GL30.glBindVertexArray(0);
   }

   private static void f() {
      int prevVao = GL30.glGetInteger(36006);
      int prevVbo = GL30.glGetInteger(34229);
      e = GL30.glGenVertexArrays();
      f = GL30.glGenBuffers();
      GL30.glBindVertexArray(e);
      GL30.glBindBuffer(34962, f);
      GL30.glEnableVertexAttribArray(0);
      GL30.glVertexAttribPointer(0, 3, 5126, false, 20, 0L);
      GL30.glEnableVertexAttribArray(1);
      GL30.glVertexAttribPointer(1, 2, 5126, false, 20, 12L);
      GL30.glBindVertexArray(prevVao);
      GL30.glBindBuffer(34962, prevVbo);
   }

   public int a(String str) {
      if (!this.h.containsKey(str)) {
         this.h.put(str, GL30.glGetUniformLocation(this.c, str));
      }

      return this.h.get(str);
   }

   public void a(String str, float f2) {
      int loc = this.a(str);
      if (loc != -1) {
         GL30.glUniform1f(loc, f2);
      }
   }

   public void b(String str, int i) {
      int loc = this.a(str);
      if (loc != -1) {
         GL30.glUniform1i(loc, i);
      }
   }

   public void a(String str, float f2, float f3) {
      int loc = this.a(str);
      if (loc != -1) {
         GL30.glUniform2f(loc, f2, f3);
      }
   }

   public void a(String str, float f2, float f3, float f4) {
      int loc = this.a(str);
      if (loc != -1) {
         GL30.glUniform3f(loc, f2, f3, f4);
      }
   }

   public void a(String str, float f2, float f3, float f4, float f5) {
      int loc = this.a(str);
      if (loc != -1) {
         GL30.glUniform4f(loc, f2, f3, f4, f5);
      }
   }

   public void a(String str, Color color) {
      if (color != null) {
         int loc = this.a(str);
         if (loc != -1) {
            GL30.glUniform4f(loc, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
         }
      }
   }
}
