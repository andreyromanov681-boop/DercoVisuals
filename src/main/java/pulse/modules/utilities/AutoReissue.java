package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import pulse.events.ClientTickEvent;
import pulse.events.PacketEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.util.ElapsedTimer;

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
