package pulse.animation;

public class PlayerListScaleAnimation {
   private float value;
   private float startValue;
   private long startTimeMs;
   private boolean shown;
   private boolean renderable;
   private float targetValue = 1.0F;
   private long durationMs = 200L;
   private final EasingFunction easing = Easing.h;

   public void a(long j) {
      this.durationMs = Math.max(1L, j);
      this.startValue = this.value;
      this.targetValue = 1.0F;
      this.startTimeMs = System.currentTimeMillis();
      this.shown = true;
      this.renderable = true;
   }

   public void b(long j) {
      this.durationMs = Math.max(1L, j);
      this.startValue = this.value;
      this.targetValue = 0.0F;
      this.startTimeMs = System.currentTimeMillis();
      this.shown = false;
      this.renderable = true;
   }

   public void a() {
      if (this.startTimeMs != 0L) {
         long jCurrentTimeMillis = System.currentTimeMillis() - this.startTimeMs;
         if (jCurrentTimeMillis < this.durationMs) {
            this.value = (float)(this.startValue + (this.targetValue - this.startValue) * this.easing.ease((double)jCurrentTimeMillis / this.durationMs));
         } else {
            this.value = this.targetValue;
            this.startTimeMs = 0L;
            if (this.shown) {
               return;
            }

            this.renderable = false;
         }
      }
   }

   public float b() {
      return this.value;
   }

   public boolean c() {
      return this.shown;
   }

   public boolean d() {
      return this.renderable;
   }

   public boolean e() {
      return this.startTimeMs != 0L;
   }

   public void f() {
      this.value = 1.0F;
      this.startTimeMs = 0L;
      this.shown = true;
      this.renderable = true;
   }
}
