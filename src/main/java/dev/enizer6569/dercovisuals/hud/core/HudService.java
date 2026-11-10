// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.core;

import lombok.Generated;
import dev.enizer6569.dercovisuals.events.EventBusService;

public abstract class HudService {
   private boolean enabled;

   public void a() {
      HudServiceInfo hudServiceInfo = this.getClass().getAnnotation(HudServiceInfo.class);
      this.a(hudServiceInfo == null || hudServiceInfo.enabledByDefault());
   }

   public void a(boolean z) {
      if (this.enabled != z) {
         this.enabled = z;
         if (z) {
            EventBusService.EVENT_BUS.subscribe(this);
         } else {
            EventBusService.EVENT_BUS.unsubscribe(this);
         }
      }
   }

   public void b() {
      this.a(!this.enabled);
   }

   @Generated
   public boolean c() {
      return this.enabled;
   }
}
