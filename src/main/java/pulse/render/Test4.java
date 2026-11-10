package pulse.render;

import java.lang.reflect.Method;
import net.minecraft.client.network.ClientPlayerEntity;

public class Test4 {
   public static void main(String[] args) {
      try {
         Method m = ClientPlayerEntity.class.getMethod("getItemCooldownManager");
         System.out.println("Return type: " + m.getReturnType().getName());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
