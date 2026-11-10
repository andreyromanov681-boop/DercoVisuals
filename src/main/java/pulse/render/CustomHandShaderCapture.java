package pulse.render;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.MinecraftClient;

public final class CustomHandShaderCapture {
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static boolean e = false;

   private CustomHandShaderCapture() {
   }

   public static void beginCapture() {
   }

   public static void endCapture() {
   }

   public static boolean isActive() {
      return false;
   }

   public static boolean guiOpen() {
      return a.currentScreen != null;
   }

   public static Framebuffer getFramebuffer() {
      return null;
   }

   public static void resetForGui() {
   }
}
