package pulse;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.Click;

public class TestMod implements ModInitializer {
   public void onInitialize() {
      System.out.println("[Pulse Test] Mod initialized!");

      try {
         Click click = null;
         System.out.println("Click class exists! " + click);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
