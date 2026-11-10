// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.PacketEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.util.ElapsedTimer;

@ModuleInfo(a = "Auto Reissue", b = "Tracks auction reissue cooldowns.", c = ModuleCategory.UTILITIES)
public class AutoReissue extends ClientModule {
   private final BooleanSetting autoReissue = new BooleanSetting("Auto Reissue", true);
   private final BooleanSetting showCooldownOverlay = new BooleanSetting("Show Cooldown Overlay", true);
   public boolean overlayActive = false;
   public ElapsedTimer timer = new ElapsedTimer();
   public int durationMs = 15000;

   @EventHandler
   public void a(PacketEvent packetEvent) {
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
   }

   @Override
   public void f() {
      super.f();
      this.overlayActive = false;
      this.timer.b();
   }
}
