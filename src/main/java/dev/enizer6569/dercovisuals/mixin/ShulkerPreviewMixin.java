// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.screen.slot.Slot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.ShulkerPreview;
import dev.enizer6569.dercovisuals.modules.visuals.ShulkerPreviewRenderer;

@Mixin(HandledScreen.class)
public class ShulkerPreviewMixin {
   @Shadow
   protected Slot focusedSlot;
   @Shadow
   protected int x;
   @Shadow
   protected int y;
   @Shadow
   protected int backgroundWidth;

   @Inject(require = 0, method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
   private void pulse$cancelShulkerTooltip(DrawContext DrawContextVar, int i, int i2, CallbackInfo callbackInfo) {
      ShulkerPreview shulkerPreview = ModuleRegistry.SHULKER_PREVIEW;
      if (shulkerPreview != null
         && shulkerPreview.k()
         && shulkerPreview.inventoryPreview.a()
         && this.focusedSlot != null
         && this.focusedSlot.hasStack()
         && ShulkerPreviewRenderer.isShulkerBox(this.focusedSlot.getStack())) {
         callbackInfo.cancel();
         ShulkerPreviewRenderer.renderInventoryHover(this.focusedSlot, DrawContextVar, this.x + this.backgroundWidth + 6, this.y, true);
      }
   }
}
