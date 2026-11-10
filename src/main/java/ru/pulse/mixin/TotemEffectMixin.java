package ru.pulse.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.module.ModuleRegistry;

@Mixin(InGameHud.class)
public abstract class TotemEffectMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Final
   private static Identifier TOTEM_OF_UNDYING_TEXTURE;
   @Shadow
   private int heldItemTooltipFade;
   @Shadow
   private ItemStack currentStack;

   @Inject(require = 0, method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
   private void onRenderHeldItemTooltip(DrawContext DrawContextVar, CallbackInfo callbackInfo) {
      if (ModuleRegistry.RENDER_TWEAKS.q() && this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "tick", at = @At("HEAD"))
   private void onTick(CallbackInfo callbackInfo) {
      if (ModuleRegistry.RENDER_TWEAKS.q() && this.heldItemTooltipFade > 0) {
         this.heldItemTooltipFade = 0;
      }
   }
}
