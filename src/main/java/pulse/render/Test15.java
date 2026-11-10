package pulse.render;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Test15 {
   public static void main(String[] args) {
      try {
         URL url = new File("C:/Users/Administrator/.gradle/caches/fabric-loom/1.21.11/net.fabricmc.yarn.1_21_11.1.21.11+build.6-v2/clientOnly-unpicked.jar")
            .toURI()
            .toURL();
         URLClassLoader loader = new URLClassLoader(new URL[]{url});
         Class<?> cls = loader.loadClass("net.minecraft.client.render.command.OrderedRenderCommandQueue");
         System.out.println("Interfaces of OrderedRenderCommandQueue:");

         for (Class<?> i : cls.getInterfaces()) {
            System.out.println(i.getName());
         }

         System.out.println("Methods:");

         for (Method m : cls.getDeclaredMethods()) {
            System.out.println(m.getName() + " : " + m.toString());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
