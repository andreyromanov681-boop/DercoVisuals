package pulse.render;

import java.lang.reflect.Method;
import net.minecraft.client.render.item.HeldItemRenderer;

public class Test5 {
   public static void main(String[] args) {
      try {
         for (Method m : HeldItemRenderer.class.getDeclaredMethods()) {
            if (m.getName().contains("render")) {
               System.out.println(m.getName() + " : " + m.toString());
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
