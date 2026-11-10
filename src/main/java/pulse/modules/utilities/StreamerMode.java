package pulse.modules.utilities;

import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;

@ModuleInfo(a = "Streamer Mode", b = "Hides player and server information.", c = ModuleCategory.UTILITIES)
public class StreamerMode extends ClientModule {
   public final BooleanSetting hidePlayerName = new BooleanSetting("Hide Player Name", true);
   public final BooleanSetting hideServerNumber = new BooleanSetting("Hide Server Number", true);
   public final BooleanSetting hideDebugInfo = new BooleanSetting("Hide Debug Info", true);
}
