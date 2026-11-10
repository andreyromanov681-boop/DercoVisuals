// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.ModeSetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;
import dev.enizer6569.dercovisuals.settings.TokenSetting;

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
