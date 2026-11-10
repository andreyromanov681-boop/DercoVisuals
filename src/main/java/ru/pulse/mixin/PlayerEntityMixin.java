package ru.pulse.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.events.CriticalHitEvent;
import pulse.events.EventBusService;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
   @Inject(method = "addCritParticles", at = @At("HEAD"), require = 0)
   private void onCriticalHit(Entity EntityVar, CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new CriticalHitEvent(EntityVar));
   }
}
