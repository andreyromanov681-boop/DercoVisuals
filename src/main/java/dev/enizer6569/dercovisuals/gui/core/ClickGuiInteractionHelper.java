// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.core;

import net.minecraft.client.gui.screen.Screen;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.gui.modules.ModuleCard;
import dev.enizer6569.dercovisuals.gui.modules.ModulesTab;
import dev.enizer6569.dercovisuals.module.ClientModule;

public final class ClickGuiInteractionHelper implements MinecraftContext {
   private ClickGuiInteractionHelper() {
   }

   public static boolean handleRightClick(double d, double d2) {
      Screen ScreenVar = c.currentScreen;
      if (!(ScreenVar instanceof DercoVisualsClickGuiScreen)) {
         return false;
      } else if (!(((DercoVisualsClickGuiScreen)ScreenVar).a() instanceof ModulesTab modulesTab)) {
         return true;
      } else {
         ModuleCard moduleCardFindCardAtBounds;
         ClientModule clientModuleE;
         if ((moduleCardFindCardAtBounds = modulesTab.findCardAtBounds((int)d, (int)d2)) != null
            && (clientModuleE = moduleCardFindCardAtBounds.l().e()) != null
            && !clientModuleE.m().isEmpty()) {
            modulesTab.openSettingsForCard(moduleCardFindCardAtBounds);
            return true;
         } else {
            return true;
         }
      }
   }
}
