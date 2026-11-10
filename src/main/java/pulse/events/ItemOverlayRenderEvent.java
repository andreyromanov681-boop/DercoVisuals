package pulse.events;

import net.minecraft.item.ItemStack;

public class ItemOverlayRenderEvent extends PulseEvent {
   private final ItemStack stack;
   private final int x;
   private final int y;

   public ItemOverlayRenderEvent(ItemStack ItemStackVar, int i, int i2) {
      this.stack = ItemStackVar;
      this.x = i;
      this.y = i2;
   }

   public ItemStack stack() {
      return this.stack;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public ItemStack a() {
      return this.stack;
   }

   public int b() {
      return this.x;
   }

   public int c() {
      return this.y;
   }
}
