package pulse.client;

import net.minecraft.client.util.Window;
import net.minecraft.client.MinecraftClient;

public interface MinecraftContext {
   MinecraftClient CLIENT = MinecraftClient.getInstance();
   Window WINDOW = CLIENT.getWindow();
   MinecraftClient c = CLIENT;
   Window d = WINDOW;
   MinecraftClient mc = CLIENT;
   Window window = WINDOW;

   static int getWidth() {
      MinecraftClient client = MinecraftClient.getInstance();
      return client != null && client.getWindow() != null ? client.getWindow().getFramebufferWidth() : 640;
   }

   static int getHeight() {
      MinecraftClient client = MinecraftClient.getInstance();
      return client != null && client.getWindow() != null ? client.getWindow().getFramebufferHeight() : 360;
   }
}
