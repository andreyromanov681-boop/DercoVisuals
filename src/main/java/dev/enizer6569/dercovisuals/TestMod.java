// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.Click;

public class TestMod implements ModInitializer {
   public void onInitialize() {
      System.out.println("[DercoVisuals Test] Mod initialized!");

      try {
         Click click = null;
         System.out.println("Click class exists! " + click);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
