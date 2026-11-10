// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;

@ModuleInfo(a = "Streamer Mode", b = "Hides player and server information.", c = ModuleCategory.UTILITIES)
public class StreamerMode extends ClientModule {
   public final BooleanSetting hidePlayerName = new BooleanSetting("Hide Player Name", true);
   public final BooleanSetting hideServerNumber = new BooleanSetting("Hide Server Number", true);
   public final BooleanSetting hideDebugInfo = new BooleanSetting("Hide Debug Info", true);
}
