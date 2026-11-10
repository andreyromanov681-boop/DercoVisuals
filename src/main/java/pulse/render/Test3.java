package pulse.render;

import java.lang.reflect.Method;
import net.minecraft.client.gui.DrawContext;

public class Test3 {
   public static void main(String[] args) {
      test();
   }

   public static void test() {
      for (Method m : DrawContext.class.getMethods()) {
         if (m.getName().contains("drawText") || m.getName().contains("getMatrices")) {
            System.out.println(m.getName() + " -> " + m.getReturnType().getSimpleName());
         }
      }
   }
}
