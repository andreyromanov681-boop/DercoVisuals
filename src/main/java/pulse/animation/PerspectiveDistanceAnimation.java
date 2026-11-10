package pulse.animation;

public class PerspectiveDistanceAnimation {
   private static final float FIRST_PERSON_DISTANCE = 1.0F;
   private static final float THIRD_PERSON_DISTANCE = 4.0F;
   private long startTimeMs;
   private boolean thirdPerson;
   private float value = 4.0F;
   private float startValue = 4.0F;
   private float targetValue = 4.0F;
   private long durationMs = 300L;
   private final EasingFunction easing = Easing.C;

   public void a() {
      if (this.startTimeMs != 0L) {
         long jCurrentTimeMillis = System.currentTimeMillis() - this.startTimeMs;
         if (jCurrentTimeMillis >= this.durationMs) {
            this.value = this.targetValue;
            this.startTimeMs = 0L;
         } else {
            this.value = (float)(this.startValue + (this.targetValue - this.startValue) * this.easing.ease((double)jCurrentTimeMillis / this.durationMs));
         }
      }
   }

   public void a(boolean z, long j) {
      this.durationMs = Math.max(1L, j);
      if (z && !this.thirdPerson) {
         this.startValue = 1.0F;
         this.targetValue = 4.0F;
         this.value = 1.0F;
         this.startTimeMs = System.currentTimeMillis();
      } else if (!z && this.thirdPerson) {
         this.startValue = this.value;
         this.targetValue = 1.0F;
         this.startTimeMs = System.currentTimeMillis();
      }

      this.thirdPerson = z;
   }

   public float b() {
      return this.value;
   }

   public boolean c() {
      return this.startTimeMs != 0L;
   }

   public void d() {
      this.value = 4.0F;
      this.startValue = 4.0F;
      this.targetValue = 4.0F;
      this.startTimeMs = 0L;
      this.thirdPerson = false;
   }
}
