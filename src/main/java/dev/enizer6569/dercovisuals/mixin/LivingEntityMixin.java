// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.events.EntityJumpEvent;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.PlayerDeathEvent;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.CustomHand;
import dev.enizer6569.dercovisuals.modules.visuals.RenderTweaks;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
   @Overwrite
   private int getHandSwingDuration() {
      LivingEntity LivingEntityVar = (LivingEntity)this;
      if (StatusEffectUtil.hasHaste(LivingEntityVar)) {
         return 6 - (1 + StatusEffectUtil.getHasteAmplifier(LivingEntityVar));
      } else {
         CustomHand customHand = ModuleRegistry.CUSTOM_HAND;
         if (customHand != null && customHand.k()) {
            int iB = customHand.animationSpeed.b();
            return LivingEntityVar.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? iB + (1 + LivingEntityVar.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : iB;
         } else {
            return LivingEntityVar.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + LivingEntityVar.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
         }
      }
   }

   @Inject(require = 0, method = "jump", at = @At("HEAD"))
   private void onJump(CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new EntityJumpEvent((LivingEntity)this));
   }

   @Inject(require = 0, method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/LivingEntity;getDeathSound()Lnet/minecraft/SoundEvent;"))
   public void handleStatus(byte b, CallbackInfo callbackInfo) {
      LivingEntity LivingEntitySelf = (LivingEntity)this;
      if (LivingEntitySelf instanceof ClientPlayerEntity ClientPlayerEntityVar2) {
         EventBusService.EVENT_BUS
            .post(
               new PlayerDeathEvent(
                  ClientPlayerEntityVar2, ClientPlayerEntityVar2.getX(), ClientPlayerEntityVar2.getY(), ClientPlayerEntityVar2.getZ()
               )
            );
      }
   }

   @Inject(require = 0, method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
   private void onHasStatusEffect(RegistryEntry<StatusEffect> RegistryEntryVar, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (this instanceof ClientPlayerEntity) {
         RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
         if (ModuleRegistry.RENDER_TWEAKS != null && RegistryEntryVar == StatusEffects.WITHER && renderTweaks.t()) {
            callbackInfoReturnable.setReturnValue(false);
         }
      }
   }
}
