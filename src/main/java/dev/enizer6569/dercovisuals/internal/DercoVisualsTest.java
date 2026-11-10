// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.internal;

import java.lang.reflect.Field;
import net.minecraft.client.gl.RenderPipelines;

public class DercoVisualsTest {
   public static void main(String[] args) {
      for (Field f : RenderPipelines.class.getDeclaredFields()) {
         System.out.println(f.getName() + " " + f.getType().getName());
      }
   }
}
