package pulse.gui.core;

import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import pulse.core.Bool;

public class GuiInput {
   private static float j;
   private static float k;
   private static float l;
   private static float m;
   public static int a;
   public static boolean b;
   private static long e = 0L;
   private static long f = 0L;
   private static final double[] c = new double[1];
   private static final double[] d = new double[1];
   private static boolean g = false;
   private static boolean h = false;
   private static boolean i = false;

   public static void a(float f2, float f3, float f4, float f5) {
      i = true;
      j = f2;
      k = f3;
      l = f4;
      m = f5;
   }

   public static void a() {
      i = false;
   }

   public static void b() {
      h = true;
   }

   public static void c() {
      h = false;
   }

   public static boolean d() {
      return h;
   }

   public static boolean a(double d2, double d3) {
      return !i ? true : Bool.from(!(d2 < j) && !(d3 < k) && !(d2 > j + l) && !(d3 > k + m) ? 1 : 0);
   }

   public static boolean a(double d2, double d3, double d4, double d5, double d6, double d7) {
      if (!(d6 >= d2) || !(d7 >= d3) || !(d6 <= d2 + d4) || !(d7 <= d3 + d5)) {
         return false;
      } else {
         return !i ? true : Bool.from(!(d6 < j) && !(d7 < k) && !(d6 > j + l) && !(d7 > k + m) ? 1 : 0);
      }
   }

   public static boolean a(float f2, float f3, float f4, float f5, double d2, double d3) {
      return a((double)f2, (double)f3, (double)f4, (double)f5, d2, d3);
   }

   public static boolean a(GuiLayerRegistry.Layer layer, double d2, double d3) {
      return !i || d2 >= j && d3 >= k && d2 <= j + l && d3 <= k + m ? GuiLayerRegistry.a().a(layer, d2, d3) : false;
   }

   public static boolean a(GuiLayerRegistry.Layer layer, float f2, float f3, float f4, float f5, double d2, double d3) {
      return a(f2, f3, f4, f5, d2, d3) ? a(layer, d2, d3) : false;
   }

   public static boolean b(GuiLayerRegistry.Layer layer, double d2, double d3) {
      return GuiLayerRegistry.a().d(layer, d2, d3);
   }

   public static double e() {
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.getWindow() == null) {
         return 0.0;
      }

      GLFW.glfwGetCursorPos(MinecraftClientVarGetInstance.getWindow().getHandle(), c, d);
      return c[0] * MinecraftClientVarGetInstance.getWindow().getScaledWidth() / MinecraftClientVarGetInstance.getWindow().getWidth();
   }

   public static double f() {
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.getWindow() == null) {
         return 0.0;
      }

      GLFW.glfwGetCursorPos(MinecraftClientVarGetInstance.getWindow().getHandle(), c, d);
      return d[0] * MinecraftClientVarGetInstance.getWindow().getScaledHeight() / MinecraftClientVarGetInstance.getWindow().getHeight();
   }

   public static void g() {
      if (!h) {
         g = true;
      }
   }

   public static void h() {
      g = false;
   }

   public static void i() {
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.getWindow() != null) {
         long jGetHandle = MinecraftClientVarGetInstance.getWindow().getHandle();
         if (g) {
            if (e == 0L) {
               e = GLFW.glfwCreateStandardCursor(221188);
            }

            GLFW.glfwSetCursor(jGetHandle, e);
         } else {
            if (f == 0L) {
               f = GLFW.glfwCreateStandardCursor(221185);
            }

            GLFW.glfwSetCursor(jGetHandle, f);
         }
      }
   }

   public static void j() {
      g = false;
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.getWindow() != null) {
         if (f == 0L) {
            f = GLFW.glfwCreateStandardCursor(221185);
         }

         GLFW.glfwSetCursor(MinecraftClientVarGetInstance.getWindow().getHandle(), f);
      }
   }

   @Generated
   public static boolean k() {
      return i;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
