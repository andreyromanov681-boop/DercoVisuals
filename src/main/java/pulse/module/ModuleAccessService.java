package pulse.module;

import pulse.client.MinecraftContext;
import pulse.hud.core.HudService;
import pulse.hud.core.HudServiceInfo;

@HudServiceInfo(enabledByDefault = true)
public class ModuleAccessService extends HudService implements MinecraftContext {
   @Override
   public void a() {
      this.initialize();
   }

   public void initialize() {
   }

   public boolean isModuleAllowed(String str) {
      return true;
   }

   public boolean a(String str) {
      return this.isModuleAllowed(str);
   }
}
