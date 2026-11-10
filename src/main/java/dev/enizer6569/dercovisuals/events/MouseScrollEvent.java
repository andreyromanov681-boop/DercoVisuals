// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

public class MouseScrollEvent extends CancellableDercoVisualsEvent {
   private final double horizontal;
   private final double vertical;

   public MouseScrollEvent(double d, double d2) {
      this.horizontal = d;
      this.vertical = d2;
   }

   public double horizontal() {
      return this.horizontal;
   }

   public double vertical() {
      return this.vertical;
   }

   public double d() {
      return this.horizontal;
   }

   public double e() {
      return this.vertical;
   }
}
