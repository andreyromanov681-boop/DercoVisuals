// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.events.CriticalHitEvent;
import dev.enizer6569.dercovisuals.events.EventBusService;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
   @Inject(method = "addCritParticles", at = @At("HEAD"), require = 0)
   private void onCriticalHit(Entity EntityVar, CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new CriticalHitEvent(EntityVar));
   }
}
