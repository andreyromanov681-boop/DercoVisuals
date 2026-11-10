package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;

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
