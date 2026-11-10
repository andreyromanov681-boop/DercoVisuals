// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.module;

import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.hud.core.HudService;
import dev.enizer6569.dercovisuals.hud.core.HudServiceInfo;

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
