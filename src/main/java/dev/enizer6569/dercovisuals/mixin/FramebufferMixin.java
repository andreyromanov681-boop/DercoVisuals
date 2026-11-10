// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.render.CustomHandShaderCapture;

@Mixin(Framebuffer.class)
public class FramebufferMixin {
   @Inject(require = 0, method = "beginWrite", at = @At("HEAD"), cancellable = true)
   private void redirectToHandFramebuffer(boolean z, CallbackInfo callbackInfo) {
      MinecraftClient MinecraftClientVarGetInstance;
      Framebuffer FramebufferVarGetFramebuffer;
      Framebuffer framebuffer;
      if (!CustomHandShaderCapture.guiOpen()
         && CustomHandShaderCapture.isActive()
         && (MinecraftClientVarGetInstance = MinecraftClient.getInstance()) != null
         && (FramebufferVarGetFramebuffer = MinecraftClientVarGetInstance.getFramebuffer()) != null
         && this == FramebufferVarGetFramebuffer
         && (framebuffer = CustomHandShaderCapture.getFramebuffer()) != null
         && framebuffer != FramebufferVarGetFramebuffer) {
         callbackInfo.cancel();
      }
   }
}
