package ru.pulse.mixin;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.events.DropItemEvent;
import pulse.events.EventBusService;
import pulse.events.ItemUseEvent;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
   @Inject(require = 0, method = "interactItem", at = @At("HEAD"), cancellable = true)
   private void onRightClick(PlayerEntity PlayerEntityVar, Hand HandVar, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
      ItemUseEvent itemUseEvent = new ItemUseEvent(PlayerEntityVar, PlayerEntityVar.getEntityWorld(), HandVar);
      EventBusService.EVENT_BUS.post(itemUseEvent);
      if (itemUseEvent.c()) {
         callbackInfoReturnable.setReturnValue(ActionResult.FAIL);
      }
   }

   @Inject(require = 0, method = "clickSlot", at = @At("HEAD"), cancellable = true)
   private void onClickSlot(int i, int i2, int i3, SlotActionType SlotActionTypeVar, PlayerEntity player, CallbackInfo callbackInfo) {
      if (SlotActionTypeVar == SlotActionType.THROW) {
         int i4 = i2;
         if (i2 >= 0 && i2 < 9) {
            i4 = i2 + 27;
         } else if (i2 >= 36 && i2 < 45) {
            i4 = i2 - 36;
         }

         DropItemEvent dropItemEvent = new DropItemEvent(i4, i3 == 1);
         EventBusService.EVENT_BUS.post(dropItemEvent);
         if (dropItemEvent.c()) {
            callbackInfo.cancel();
         }
      }
   }
}
