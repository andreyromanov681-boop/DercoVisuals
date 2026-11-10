// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;

@ModuleInfo(a = "Sprint", b = "Automatically holds the sprint key.", c = ModuleCategory.UTILITIES)
public class Sprint extends ClientModule {
   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null) {
         c.options.sprintKey.setPressed(c.player.age > 3);
      }
   }
}
