package pulse.events;

public class MovementInputEvent extends CancellablePulseEvent {
   private float forward;
   private float sideways;
   private boolean jumping;
   private boolean sneaking;
   private double sneakSlowdownMultiplier;

   public MovementInputEvent(float f, float f2, boolean z, boolean z2, double d) {
      this.forward = f;
      this.sideways = f2;
      this.jumping = z;
      this.sneaking = z2;
      this.sneakSlowdownMultiplier = d;
   }

   public float forward() {
      return this.forward;
   }

   public float sideways() {
      return this.sideways;
   }

   public boolean jumping() {
      return this.jumping;
   }

   public boolean sneaking() {
      return this.sneaking;
   }

   public double sneakSlowdownMultiplier() {
      return this.sneakSlowdownMultiplier;
   }

   public void setForward(float f) {
      this.forward = f;
   }

   public void setSideways(float f) {
      this.sideways = f;
   }

   public void setJumping(boolean z) {
      this.jumping = z;
   }

   public void setSneaking(boolean z) {
      this.sneaking = z;
   }

   public void setSneakSlowdownMultiplier(double d) {
      this.sneakSlowdownMultiplier = d;
   }

   public float d() {
      return this.forward;
   }

   public float e() {
      return this.sideways;
   }

   public boolean f() {
      return this.jumping;
   }

   public boolean g() {
      return this.sneaking;
   }

   public double h() {
      return this.sneakSlowdownMultiplier;
   }

   public void a(float f) {
      this.forward = f;
   }

   public void b(float f) {
      this.sideways = f;
   }

   public void b(boolean z) {
      this.jumping = z;
   }

   public void c(boolean z) {
      this.sneaking = z;
   }

   public void a(double d) {
      this.sneakSlowdownMultiplier = d;
   }
}
