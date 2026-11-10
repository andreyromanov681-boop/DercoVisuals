package pulse.render;

import java.lang.reflect.Method;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;

public class Test14 {
   public static void main(String[] args) {
      try {
         Class<?> cls = OrderedRenderCommandQueue.class;
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
