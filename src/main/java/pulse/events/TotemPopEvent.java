package pulse.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TotemPopEvent extends PulseEvent {
   private final LivingEntity entity;
   private final ItemStack totemStack;

   public TotemPopEvent(LivingEntity LivingEntityVar, ItemStack ItemStackVar) {
      this.entity = LivingEntityVar;
      this.totemStack = ItemStackVar;
   }

   public LivingEntity entity() {
      return this.entity;
   }

   public ItemStack totemStack() {
      return this.totemStack;
   }

   public LivingEntity a() {
      return this.entity;
   }

   public ItemStack b() {
      return this.totemStack;
   }
}
