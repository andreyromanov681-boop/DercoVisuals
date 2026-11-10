package pulse.events;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.VertexConsumerProvider.class_4598;

public class WorldRenderEvent extends PulseEvent {
   private final MatrixStack matrices;
   private final class_4598 bufferSource;
   private final float tickDelta;

   public WorldRenderEvent(MatrixStack matrices, float tickDelta) {
      this(matrices, null, tickDelta);
   }

   public WorldRenderEvent(MatrixStack matrices, class_4598 bufferSource, float tickDelta) {
      this.matrices = matrices;
      this.bufferSource = bufferSource;
      this.tickDelta = tickDelta;
   }

   public MatrixStack matrices() {
      return this.matrices;
   }

   public class_4598 bufferSource() {
      return this.bufferSource;
   }

   public float tickDelta() {
      return this.tickDelta;
   }

   public MatrixStack a() {
      return this.matrices;
   }

   public float b() {
      return this.tickDelta;
   }
}
