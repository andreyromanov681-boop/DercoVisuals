// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.gui.menu.DercoVisualsGameMenuScreen;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
   @Inject(require = 0, method = "init", at = @At("HEAD"), cancellable = true)
   private void onInitHead(CallbackInfo callbackInfo) {
      if (!(this instanceof DercoVisualsGameMenuScreen)) {
         MinecraftClient.getInstance().setScreen(new DercoVisualsGameMenuScreen());
         callbackInfo.cancel();
      }
   }
}
