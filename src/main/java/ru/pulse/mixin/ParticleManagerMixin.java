package ru.pulse.mixin;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.Optimization;
import pulse.modules.visuals.RenderTweaks;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
   @Inject(require = 0, method = "addParticle", at = @At("HEAD"), cancellable = true)
   private void onAddParticle(
      ParticleEffect ParticleEffectVar, double d, double d2, double d3, double d4, double d5, double d6, CallbackInfoReturnable<Particle> callbackInfoReturnable
   ) {
      if (!Optimization.shouldSpawnParticle(d, d2, d3)) {
         callbackInfoReturnable.setReturnValue((Particle)null);
      } else {
         RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
         if (renderTweaks != null && renderTweaks.u()) {
            if (ParticleEffectVar.getType() == ParticleTypes.BUBBLE
               || ParticleEffectVar.getType() == ParticleTypes.BUBBLE_COLUMN_UP
               || ParticleEffectVar.getType() == ParticleTypes.BUBBLE_POP
               || ParticleEffectVar.getType() == ParticleTypes.CURRENT_DOWN
               || ParticleEffectVar.getType() == ParticleTypes.UNDERWATER) {
               callbackInfoReturnable.setReturnValue((Particle)null);
            }
         }
      }
   }
}
