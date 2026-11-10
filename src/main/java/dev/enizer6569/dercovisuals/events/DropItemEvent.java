// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

public class DropItemEvent extends CancellableDercoVisualsEvent {
   private final int slot;
   private final boolean dropEntireStack;

   public DropItemEvent(int i, boolean z) {
      this.slot = i;
      this.dropEntireStack = z;
   }

   public int slot() {
      return this.slot;
   }

   public boolean dropEntireStack() {
      return this.dropEntireStack;
   }

   public int d() {
      return this.slot;
   }

   public boolean e() {
      return this.dropEntireStack;
   }
}
