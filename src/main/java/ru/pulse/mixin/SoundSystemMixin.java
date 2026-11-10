package ru.pulse.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pulse.events.EventBusService;
import pulse.events.SoundPlayEvent;
import pulse.util.sound.VolumeScaledSoundInstance;

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
