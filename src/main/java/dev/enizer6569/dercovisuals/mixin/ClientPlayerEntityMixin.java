// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.DropItemEvent;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.mixin.accessor.PlayerInventoryAccessor;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
   @Inject(require = 0, method = "tick", at = @At("HEAD"))
   private void onTick(CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new ClientTickEvent());
   }

   @Inject(require = 0, method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
   private void onDropSelectedItem(boolean z, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      DropItemEvent dropItemEvent = new DropItemEvent(((PlayerInventoryAccessor)((ClientPlayerEntity)this).getInventory()).getSelectedSlot(), z);
      EventBusService.EVENT_BUS.post(dropItemEvent);
      if (dropItemEvent.c()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}
