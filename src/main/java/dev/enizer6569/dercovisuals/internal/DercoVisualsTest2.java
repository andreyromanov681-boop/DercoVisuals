// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.internal;

import java.lang.reflect.Method;

public class DercoVisualsTest2 {
   public static void main(String[] args) throws Exception {
      Class<?> clazz = Class.forName("com.mojang.blaze3d.systems.RenderSystem");

      for (Method m : clazz.getDeclaredMethods()) {
         if (m.getName().toLowerCase().contains("shader")) {
            System.out.print(m.getName() + "(");

            for (Class<?> p : m.getParameterTypes()) {
               System.out.print(p.getName() + ", ");
            }

            System.out.println(")");
         }
      }
   }
}
