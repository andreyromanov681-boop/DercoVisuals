package pulse.hud.core;

public class HudElementPlacement {
   private float normalizedCenterX;
   private float normalizedCenterY;

   public void update(float f, float f2, float f3, float f4, float f5, float f6) {
      this.normalizedCenterX = clamp01((f + f3 / 2.0F) / f5);
      this.normalizedCenterY = clamp01((f2 + f4 / 2.0F) / f6);
   }

   public float resolveX(float f, float f2) {
      return this.normalizedCenterX * f2 - f / 2.0F;
   }

   public float resolveY(float f, float f2) {
      return this.normalizedCenterY * f2 - f / 2.0F;
   }

   public float normalizedCenterX() {
      return this.normalizedCenterX;
   }

   public float normalizedCenterY() {
      return this.normalizedCenterY;
   }

   public void setNormalizedCenterX(float f) {
      this.normalizedCenterX = clamp01(f);
   }

   public void setNormalizedCenterY(float f) {
      this.normalizedCenterY = clamp01(f);
   }

   public void a(float f, float f2, float f3, float f4, float f5, float f6) {
      this.update(f, f2, f3, f4, f5, f6);
   }

   public float a(float f, float f2) {
      return this.resolveX(f, f2);
   }

   public float b(float f, float f2) {
      return this.resolveY(f, f2);
   }

   public float a() {
      return this.normalizedCenterX();
   }

   public float b() {
      return this.normalizedCenterY();
   }

   public void a(float f) {
      this.setNormalizedCenterX(f);
   }

   public void b(float f) {
      this.setNormalizedCenterY(f);
   }

   private static float clamp01(float f) {
      return Math.max(0.0F, Math.min(1.0F, f));
   }
}
