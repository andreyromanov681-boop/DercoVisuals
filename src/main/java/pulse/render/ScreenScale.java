package pulse.render;

import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import pulse.client.MinecraftContext;

public final class ScreenScale implements MinecraftContext {
   private static float e = 1.0F;
   public static int a;
   public static boolean b;

   public static void a(double d) {
      e = (float)d;
   }

   public static void a() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client != null && client.getWindow() != null) {
         e = client.getWindow().getScaleFactor();
      } else {
         e = 1.0F;
      }
   }

   public static ScreenPoint a(int i, int i2) {
      MinecraftClient client = MinecraftClient.getInstance();
      double dGetScaleFactor = client != null && client.getWindow() != null ? client.getWindow().getScaleFactor() : 2.0;
      return new ScreenPoint((int)(i * dGetScaleFactor / 2.0), (int)(i2 * dGetScaleFactor / 2.0));
   }

   public static ScreenPoint a(double d, double d2) {
      MinecraftClient client = MinecraftClient.getInstance();
      double dGetScaleFactor = client != null && client.getWindow() != null ? client.getWindow().getScaleFactor() : 2.0;
      return new ScreenPoint((int)(d * dGetScaleFactor / 2.0), (int)(d2 * dGetScaleFactor / 2.0));
   }

   public static void b() {
   }

   public static void c() {
   }

   public static void a(float f2, float f3, float f4) {
   }

   public static void b(float f2, float f3, float f4) {
   }

   @Generated
   public static float d() {
      return e;
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
