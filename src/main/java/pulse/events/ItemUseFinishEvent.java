package pulse.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemUseFinishEvent extends CancellablePulseEvent {
   private final ItemStack stack;
   private final World world;
   private final LivingEntity user;

   public ItemUseFinishEvent(ItemStack ItemStackVar, World WorldVar, LivingEntity LivingEntityVar) {
      this.stack = ItemStackVar;
      this.world = WorldVar;
      this.user = LivingEntityVar;
   }

   public ItemStack stack() {
      return this.stack;
   }

   public World world() {
      return this.world;
   }

   public LivingEntity user() {
      return this.user;
   }

   public ItemStack d() {
      return this.stack;
   }

   public World e() {
      return this.world;
   }

   public LivingEntity f() {
      return this.user;
   }
}
