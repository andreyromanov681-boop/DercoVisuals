// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.hud;

import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Inventory HUD", b = "Displays item inventory.", c = ModuleCategory.HUD)
public class InventoryHud extends ClientModule {
   private final SliderSetting scale = new SliderSetting("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);
   private final BooleanSetting overallBackground = new BooleanSetting("Общий фон", true);
   private final BooleanSetting slotBackground = new BooleanSetting("Фон слотов", false);

   public InventoryHud() {
      this.collectSettings();
   }

   public SliderSetting getScale() {
      return this.scale;
   }

   public BooleanSetting getOverallBackground() {
      return this.overallBackground;
   }

   public BooleanSetting getSlotBackground() {
      return this.slotBackground;
   }
}
