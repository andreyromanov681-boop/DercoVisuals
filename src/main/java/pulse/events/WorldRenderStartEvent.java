package pulse.events;

public class WorldRenderStartEvent extends PulseEvent {
   private final float tickDelta;

   public WorldRenderStartEvent(float f) {
      this.tickDelta = f;
   }

   public float tickDelta() {
      return this.tickDelta;
   }

   public float a() {
      return this.tickDelta;
   }
}
