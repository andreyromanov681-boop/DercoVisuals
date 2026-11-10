import java.lang.reflect.Field;
import net.minecraft.client.gl.RenderPipelines;

public class Test {
   public static void main(String[] args) {
      for (Field f : RenderPipelines.class.getDeclaredFields()) {
         System.out.println(f.getName() + " " + f.getType().getName());
      }
   }
}
