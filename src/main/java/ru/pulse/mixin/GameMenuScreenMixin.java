package ru.pulse.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.gui.menu.PulseGameMenuScreen;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
   @Inject(require = 0, method = "init", at = @At("HEAD"), cancellable = true)
   private void onInitHead(CallbackInfo callbackInfo) {
      if (!(this instanceof PulseGameMenuScreen)) {
         MinecraftClient.getInstance().setScreen(new PulseGameMenuScreen());
         callbackInfo.cancel();
      }
   }
}
