package pulse.inventory;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import pulse.client.MinecraftContext;

public final class InventoryFinder implements MinecraftContext {
   public static int a;
   public static boolean b;

   public static Optional<Integer> a(Predicate<ItemStack> predicate, boolean z, boolean z2) {
      PlayerInventory PlayerInventoryVarGetInventory = c.player.getInventory();
      int i2 = !z ? 9 : 36;

      for (int i3 = 0; i3 < i2; i3++) {
         if (predicate.test(PlayerInventoryVarGetInventory.getStack(i3))) {
            if (z && !z2) {
               int i;
               if (i3 >= 9) {
                  i = i3;
               } else {
                  int i4 = i3;
                  i = (i4 ^ 36) + 2 * (i4 & 36);
               }

               return Optional.of(i);
            }

            return Optional.of(i3);
         }
      }

      return Optional.empty();
   }

   @Generated
   private InventoryFinder() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
