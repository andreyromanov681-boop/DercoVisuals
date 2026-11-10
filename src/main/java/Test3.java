import java.io.PrintWriter;
import java.lang.reflect.Method;

public class Test3 {
   public static void main(String[] args) throws Exception {
      PrintWriter out = new PrintWriter("rendersystem_methods.txt");
      Class<?> clazz = Class.forName("com.mojang.blaze3d.systems.RenderSystem");

      for (Method m : clazz.getDeclaredMethods()) {
         out.print(m.getName() + "(");

         for (Class<?> p : m.getParameterTypes()) {
            out.print(p.getName() + ", ");
         }

         out.println(")");
      }

      out.close();
      System.out.println("Done");
   }
}
