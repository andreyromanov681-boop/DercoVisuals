package pulse.animation;

public class ChatInputSlideAnimation {
   private static final float START_OFFSET = 14.0F;
   private float startValue;
   private float targetValue;
   private long startTimeMs;
   private float value = Float.NaN;
   private long durationMs = 160L;
   private final EasingFunction easing = Easing.h;

   public void a() {
      if (!Float.isNaN(this.value) && this.startTimeMs != 0L) {
         long jCurrentTimeMillis = System.currentTimeMillis() - this.startTimeMs;
         if (jCurrentTimeMillis >= this.durationMs) {
            this.value = this.targetValue;
            this.startTimeMs = 0L;
         } else {
            this.value = (float)(this.startValue + (this.targetValue - this.startValue) * this.easing.ease((double)jCurrentTimeMillis / this.durationMs));
         }
      }
   }

   public void a(long j) {
      this.durationMs = Math.max(1L, j);
      if (Float.isNaN(this.value)) {
         this.value = 14.0F;
         this.startValue = 14.0F;
         this.targetValue = 0.0F;
         this.startTimeMs = System.currentTimeMillis();
      } else {
         if (this.value != this.targetValue) {
            this.startValue = this.value;
            this.targetValue = 0.0F;
            this.startTimeMs = System.currentTimeMillis();
         }
      }
   }

   public float b() {
      return Float.isNaN(this.value) ? 0.0F : this.value;
   }

   public boolean c() {
      return this.b() > 0.0F;
   }

   public void d() {
      this.value = Float.NaN;
      this.startTimeMs = 0L;
   }
}
