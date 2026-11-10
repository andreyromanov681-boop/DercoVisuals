package pulse.events;

import net.minecraft.client.sound.SoundInstance;

public class SoundPlayEvent extends CancellablePulseEvent {
   private final SoundInstance sound;
   private float volume;
   private float pitch;

   public SoundPlayEvent(SoundInstance SoundInstanceVar) {
      this.sound = SoundInstanceVar;
      this.volume = 1.0F;

      try {
         this.pitch = SoundInstanceVar.getPitch();
      } catch (NullPointerException e) {
         this.pitch = 1.0F;
      }
   }

   public SoundInstance sound() {
      return this.sound;
   }

   public float volume() {
      return this.volume;
   }

   public void setVolume(float f) {
      this.volume = f;
   }

   public float pitch() {
      return this.pitch;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public SoundInstance d() {
      return this.sound;
   }

   public float e() {
      return this.volume;
   }

   public void a(float f) {
      this.volume = f;
   }

   public float f() {
      return this.pitch;
   }

   public void b(float f) {
      this.pitch = f;
   }
}
