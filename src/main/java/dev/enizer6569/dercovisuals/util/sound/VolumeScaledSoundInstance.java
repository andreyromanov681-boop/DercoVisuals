// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.util.sound;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundCategory;
import net.minecraft.SoundInstance.class_1114;

public class VolumeScaledSoundInstance implements SoundInstance {
   private final SoundInstance c;
   private final float d;
   public static int a;

   public VolumeScaledSoundInstance(SoundInstance SoundInstanceVar, float f) {
      this.c = SoundInstanceVar;
      this.d = f;
   }

   public Identifier getId() {
      return this.c.getId();
   }

   public WeightedSoundSet getSoundSet(SoundManager SoundManagerVar) {
      return this.c.getSoundSet(SoundManagerVar);
   }

   public Sound getSound() {
      return this.c.getSound();
   }

   public SoundCategory getCategory() {
      return this.c.getCategory();
   }

   public boolean isRepeatable() {
      return this.c.isRepeatable();
   }

   public boolean isRelative() {
      return this.c.isRelative();
   }

   public int getRepeatDelay() {
      return this.c.getRepeatDelay();
   }

   public float getVolume() {
      return this.c.getVolume() * this.d;
   }

   public float getPitch() {
      return this.c.getPitch();
   }

   public double getX() {
      return this.c.getX();
   }

   public double getY() {
      return this.c.getY();
   }

   public double getZ() {
      return this.c.getZ();
   }

   public class_1114 getAttenuationType() {
      return this.c.getAttenuationType();
   }

   public boolean shouldAlwaysPlay() {
      return this.c.shouldAlwaysPlay();
   }

   public boolean canPlay() {
      return this.c.canPlay();
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
