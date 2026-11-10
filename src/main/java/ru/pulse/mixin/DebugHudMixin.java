package ru.pulse.mixin;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.StreamerMode;

@Mixin(DebugHud.class)
public class DebugHudMixin {
   @Inject(require = 0, method = "getLeftText", at = @At("RETURN"), cancellable = true)
   private void hideLeftCoordinates(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
      StreamerMode streamerMode = ModuleRegistry.STREAMER_MODE;
      if (streamerMode.k() && streamerMode.hideDebugInfo.a()) {
         callbackInfoReturnable.setReturnValue(((List)callbackInfoReturnable.getReturnValue()).stream().filter(o -> {
            String str = (String)o;
            return !str.contains("XYZ:") && !str.contains("Block:") && !str.contains("Chunk:") && !str.contains("Facing:");
         }).collect(Collectors.toList()));
      }
   }

   @Inject(require = 0, method = "getRightText", at = @At("RETURN"), cancellable = true)
   private void hideRightCoordinates(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
      StreamerMode streamerMode = ModuleRegistry.STREAMER_MODE;
      if (streamerMode.k() && streamerMode.hideDebugInfo.a()) {
         callbackInfoReturnable.setReturnValue(((List)callbackInfoReturnable.getReturnValue()).stream().filter(o -> {
            String str = (String)o;
            return !str.contains("Targeted Block:") && !str.contains("Targeted Fluid:") && !str.contains("Targeted Entity");
         }).collect(Collectors.toList()));
      }
   }
}
