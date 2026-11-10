// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

public class WorldRenderStartEvent extends DercoVisualsEvent {
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
