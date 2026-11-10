package pulse.gui.core;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.KeyBinding.class_11900;
import net.minecraft.InputUtil.class_307;

public final class ClickGuiKeyBinding {
   public static KeyBinding OPEN_KEY;

   private ClickGuiKeyBinding() {
   }

   public static void register() {
      OPEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Click GUI", class_307.KEYSYM, 344, class_11900.MISC));
   }
}
