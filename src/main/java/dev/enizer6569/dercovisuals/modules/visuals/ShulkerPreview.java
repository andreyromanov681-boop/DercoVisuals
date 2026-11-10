// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import meteordevelopment.orbit.EventHandler;
import dev.enizer6569.dercovisuals.events.HudRenderPostEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.DercoVisuals;

@ModuleInfo(
   a = "ShulkerPreview",
   b = "Просмотр содержимого шалкеров без открытия (в инвентаре при наводении и когда шалкер дропнут).",
   c = ModuleCategory.VISUALS
)
public class ShulkerPreview extends ClientModule {
   public final BooleanSetting inventoryPreview = new BooleanSetting("Inventory Hover", true);
   public final BooleanSetting worldPreview = new BooleanSetting("Dropped Items", true);

   public ShulkerPreview() {
      this.collectSettings();
   }

   @EventHandler
   public void onHudRenderPost(HudRenderPostEvent hudRenderPostEvent) {
      ShulkerPreviewRenderer.renderWorldTarget(c, hudRenderPostEvent.a(), DercoVisuals.getInstance().getRender());
   }
}
