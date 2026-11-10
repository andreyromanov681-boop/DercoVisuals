// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import dev.enizer6569.dercovisuals.events.PlayerDeathEvent;
import dev.enizer6569.dercovisuals.markers.MapMarker;
import dev.enizer6569.dercovisuals.markers.MarkerManager;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ModeSetting;
import dev.enizer6569.dercovisuals.settings.StringSetting;

@ModuleInfo(a = "Death Marker", b = "Автоматически ставит метку на место смерти.", c = ModuleCategory.UTILITIES)
public class DeathMarker extends ClientModule {
   private final BooleanSetting keepPrevious = new BooleanSetting("Keep Previous", false);
   private final ModeSetting color = new ModeSetting("Color", new String[]{"Red", "White", "Yellow", "Cyan"}, "Red");
   private final StringSetting label = new StringSetting("Label", "Death");

   public DeathMarker() {
      this.collectSettings();
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent playerDeathEvent) {
      if (c.player != null && playerDeathEvent.player() == c.player) {
         int iX = (int)playerDeathEvent.x();
         int iY = (int)playerDeathEvent.y();
         int iZ = (int)playerDeathEvent.z();
         if (!this.keepPrevious.get()) {
            MarkerManager.a().stream().filter(v0 -> v0.h()).forEach(MarkerManager::b);
         }
         MarkerManager.a(new MapMarker(this.label.get().isBlank() ? "Death" : this.label.get(), iX, iY, iZ, switch (this.color.d()) {
            case "White" -> new Color(255, 255, 255);
            case "Yellow" -> new Color(255, 220, 50);
            case "Cyan" -> new Color(50, 220, 255);
            default -> new Color(220, 50, 50);
         }, MapMarker.Icon.DEATH));
      }
   }
}
