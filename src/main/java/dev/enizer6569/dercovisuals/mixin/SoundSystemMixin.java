// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.SoundPlayEvent;
import dev.enizer6569.dercovisuals.util.sound.VolumeScaledSoundInstance;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
   @ModifyVariable(require = 0, method = "play", at = @At("HEAD"), argsOnly = true)
   private SoundInstance modifySound(SoundInstance SoundInstanceVar) {
      if (SoundInstanceVar != null && !(SoundInstanceVar instanceof VolumeScaledSoundInstance)) {
         SoundPlayEvent soundPlayEvent = new SoundPlayEvent(SoundInstanceVar);
         EventBusService.EVENT_BUS.post(soundPlayEvent);
         if (soundPlayEvent.c()) {
            return null;
         } else {
            return soundPlayEvent.e() != 1.0F ? new VolumeScaledSoundInstance(SoundInstanceVar, soundPlayEvent.e()) : SoundInstanceVar;
         }
      } else {
         return SoundInstanceVar;
      }
   }
}
