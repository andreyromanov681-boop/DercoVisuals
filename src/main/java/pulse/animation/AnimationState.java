package pulse.animation;

public class AnimationState {
   private long startTimeMs;
   private double durationMs;
   private double startValue;
   private double targetValue;
   private double currentValue;
   private EasingFunction easing;
   private AnimationMode mode;

   public AnimationState() {
      this(Easing.f, AnimationMode.EASING);
   }

   public AnimationState(EasingFunction easingFunction, AnimationMode animationMode) {
      this.easing = easingFunction;
      this.mode = animationMode;
   }

   public AnimationState a(double d, double d2, EasingFunction easingFunction, boolean z) {
      if (this.a(z, d)) {
         return this;
      }

      this.a(AnimationMode.EASING).a(easingFunction).a(d2 * 1000.0).a(System.currentTimeMillis()).b(this.currentValue).c(d);
      return this;
   }

   public AnimationState a(double d, double d2) {
      return this.a(d, d2, Easing.f, false);
   }

   public AnimationState a(double d, double d2, EasingFunction easingFunction) {
      return this.a(d, d2, easingFunction, false);
   }

   public AnimationState a(double d, double d2, boolean z) {
      return this.a(d, d2, Easing.f, z);
   }

   public boolean a() {
      if (this.b()) {
         this.e(this.a(this.startValue, this.targetValue, this.easing.ease(this.e())));
         return true;
      } else {
         this.d(this.targetValue);
         this.a(0L);
         return false;
      }
   }

   public boolean b() {
      return !this.d();
   }

   public boolean c() {
      return this.j() > 0.0;
   }

   public boolean d() {
      return this.e() >= 1.0;
   }

   public double e() {
      return this.durationMs > 0.0 ? (System.currentTimeMillis() - this.startTimeMs) / this.durationMs : 1.0;
   }

   public boolean a(boolean z, double d) {
      return z && this.b() && (d == this.startValue || d == this.targetValue || d == this.currentValue);
   }

   public double a(double d, double d2, double d3) {
      return d + (d2 - d) * d3;
   }

   public AnimationState a(long j) {
      this.startTimeMs = j;
      return this;
   }

   public AnimationState a(double d) {
      this.durationMs = d;
      return this;
   }

   public AnimationState b(double d) {
      this.startValue = d;
      return this;
   }

   public AnimationState c(double d) {
      this.targetValue = d;
      return this;
   }

   public AnimationState d(double d) {
      this.currentValue = d;
      this.startValue = d;
      this.targetValue = d;
      return this;
   }

   private AnimationState e(double d) {
      this.currentValue = d;
      return this;
   }

   public AnimationState a(EasingFunction easingFunction) {
      this.easing = easingFunction;
      return this;
   }

   public AnimationState a(AnimationMode animationMode) {
      this.mode = animationMode;
      return this;
   }

   public long f() {
      return this.startTimeMs;
   }

   public double g() {
      return this.durationMs;
   }

   public double h() {
      return this.startValue;
   }

   public double i() {
      return this.targetValue;
   }

   public double j() {
      return this.currentValue;
   }

   public EasingFunction k() {
      return this.easing;
   }

   public AnimationMode l() {
      return this.mode;
   }
}
