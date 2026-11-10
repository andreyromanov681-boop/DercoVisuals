package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.ModeSetting;
import pulse.settings.SliderSetting;
import pulse.settings.TokenSetting;

@ModuleInfo(a = "Auto Eat", b = "Automatically eats food at the selected hunger level.", c = ModuleCategory.UTILITIES)
public class AutoEat extends ClientModule {
   private final ModeSetting mode = new ModeSetting("Mode", new String[]{"Hand", "Command"}, "Hand");
   private final TokenSetting command = new TokenSetting("Command", TokenSetting.TokenType.COMMAND, "/feed", "").a(() -> this.mode.b("Command"));
   private final SliderSetting hungerLevel = new SliderSetting("Hunger Level", 15.0F, 1.0F, 20.0F, 1.0F);

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
   }

   private boolean a(ItemStack ItemStackVar) {
      return ItemStackVar != null && ItemStackVar.getComponents().contains(DataComponentTypes.FOOD);
   }

   @Override
   public void f() {
      super.f();
   }
}
