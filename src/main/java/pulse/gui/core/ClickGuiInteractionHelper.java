package pulse.gui.core;

import net.minecraft.client.gui.screen.Screen;
import pulse.client.MinecraftContext;
import pulse.gui.modules.ModuleCard;
import pulse.gui.modules.ModulesTab;
import pulse.module.ClientModule;

public final class ClickGuiInteractionHelper implements MinecraftContext {
   private ClickGuiInteractionHelper() {
   }

   public static boolean handleRightClick(double d, double d2) {
      Screen ScreenVar = c.currentScreen;
      if (!(ScreenVar instanceof PulseClickGuiScreen)) {
         return false;
      } else if (!(((PulseClickGuiScreen)ScreenVar).a() instanceof ModulesTab modulesTab)) {
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
