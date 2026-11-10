package com.wmedia;

public final class AudioLevels {
   private final float masterPeak;
   private final float leftPeak;
   private final float rightPeak;

   public AudioLevels(float f, float f2, float f3) {
      this.masterPeak = f;
      this.leftPeak = f2;
      this.rightPeak = f3;
   }

   public float getMasterPeak() {
      return this.masterPeak;
   }

   public float getLeftPeak() {
      return this.leftPeak;
   }

   public float getRightPeak() {
      return this.rightPeak;
   }
}
