package pulse.render;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Test6 {
   public static void main(String[] args) {
      try {
         URL url = new File(
               "C:/Users/Administrator/.gradle/caches/fabric-loom/1.21.11/net.fabricmc.yarn.1_21_11.1.21.11+build.5-v2/minecraft-project-@-merged.jar"
            )
            .toURI()
            .toURL();
         URLClassLoader loader = new URLClassLoader(new URL[]{url});
         Class<?> cls = loader.loadClass("net.minecraft.client.render.item.HeldItemRenderer");

         for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().contains("render")) {
               System.out.println(m.getName());

               for (Class<?> param : m.getParameterTypes()) {
                  System.out.println("  " + param.getName());
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
