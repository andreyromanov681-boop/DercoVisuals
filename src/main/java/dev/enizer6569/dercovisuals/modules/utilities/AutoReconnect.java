// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;

@ModuleInfo(a = "Auto Reconnect", b = "Keeps reconnect state available for the reconnect screen.", c = ModuleCategory.UTILITIES)
public class AutoReconnect extends ClientModule {
   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
   }

   public boolean n() {
      return false;
   }

   public int o() {
      return 0;
   }
}
