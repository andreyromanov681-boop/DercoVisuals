// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.gui.Click;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiInteractionHelper;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;

@Mixin(DercoVisualsClickGuiScreen.class)
public class DercoVisualsClickGuiScreenMixin {
   @Inject(require = 0, method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void pulse$rightClickOpensSettings(Click click, boolean bl, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (click.button() == 1) {
         ClickGuiInteractionHelper.handleRightClick(click.comp_4798(), click.comp_4799());
         callbackInfoReturnable.setReturnValue(true);
      }
   }
}
