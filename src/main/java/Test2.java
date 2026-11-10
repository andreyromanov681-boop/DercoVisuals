import java.lang.reflect.Method;

public class Test2 {
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
