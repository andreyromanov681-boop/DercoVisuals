package pulse.modules.visuals;

import meteordevelopment.orbit.EventHandler;
import pulse.events.HudRenderPostEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import ru.pulse.Pulse;

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
      ShulkerPreviewRenderer.renderWorldTarget(c, hudRenderPostEvent.a(), Pulse.getInstance().getRender());
   }
}
