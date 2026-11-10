// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

import meteordevelopment.orbit.ICancellable;

public abstract class CancellableDercoVisualsEvent extends DercoVisualsEvent implements ICancellable {
   private boolean cancelled;

   public void a() {
      this.setCancelled(true);
   }

   public void b() {
      this.setCancelled(false);
   }

   public boolean c() {
      return this.cancelled;
   }

   public void a(boolean z) {
      this.setCancelled(z);
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean z) {
      this.cancelled = z;
   }
}
