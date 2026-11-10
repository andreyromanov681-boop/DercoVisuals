// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Item Scroller", b = "Quickly moves items while Shift and left mouse are held.", c = ModuleCategory.UTILITIES)
public class ItemScroller extends ClientModule {
   public final SliderSetting delay = new SliderSetting("Delay", 50.0F, 10.0F, 500.0F, 10.0F);
}
