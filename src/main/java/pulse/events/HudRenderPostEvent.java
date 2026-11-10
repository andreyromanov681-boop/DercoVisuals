package pulse.events;

import org.joml.Matrix3x2fStack;

public class HudRenderPostEvent extends PulseEvent {
   private final Matrix3x2fStack matrices;
   private final int width;
   private final int height;
   private final float tickDelta;

   public HudRenderPostEvent(Matrix3x2fStack matrices, int i, int i2, float f) {
      this.matrices = matrices;
      this.width = i;
      this.height = i2;
      this.tickDelta = f;
   }

   public Matrix3x2fStack matrices() {
      return this.matrices;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public float tickDelta() {
      return this.tickDelta;
   }

   public Matrix3x2fStack a() {
      return this.matrices;
   }

   public int b() {
      return this.width;
   }

   public int c() {
      return this.height;
   }

   public float d() {
      return this.tickDelta;
   }
}
