// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.ItemUseFinishEvent;

@Mixin(ItemStack.class)
public class ItemStackMixin {
   @Inject(require = 0, method = "finishUsing", at = @At("HEAD"))
   private void onItemUseFinish(World WorldVar, LivingEntity LivingEntityVar, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
      ItemUseFinishEvent itemUseFinishEvent = new ItemUseFinishEvent((ItemStack)this, WorldVar, LivingEntityVar);
      EventBusService.EVENT_BUS.post(itemUseFinishEvent);
      if (itemUseFinishEvent.c()) {
         callbackInfoReturnable.cancel();
      }
   }
}
