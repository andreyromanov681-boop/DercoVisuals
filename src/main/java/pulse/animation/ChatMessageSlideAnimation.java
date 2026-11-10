package pulse.animation;

public class ChatMessageSlideAnimation {
   private float value;
   private float startValue;
   private float targetValue;
   private long durationMs;
   private final EasingFunction easing = Easing.h;
   private long startTimeMs = System.currentTimeMillis();

   public ChatMessageSlideAnimation(float f, float f2, long j) {
      this.value = f;
      this.startValue = f;
      this.targetValue = f2;
      this.durationMs = Math.max(1L, j);
   }

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

   public float b() {
      return this.value;
   }

   public boolean c() {
      return this.startTimeMs != 0L;
   }

   public boolean d() {
      return this.startTimeMs == 0L && this.value == this.targetValue;
   }
}
