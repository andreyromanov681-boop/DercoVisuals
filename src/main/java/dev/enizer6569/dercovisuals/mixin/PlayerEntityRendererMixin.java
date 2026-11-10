// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.EntityRendererFactory.class_5618;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends EntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState> {
   protected PlayerEntityRendererMixin() {
      super((class_5618)null);
   }

   @Inject(
      method = {"hasLabel*", "hasLabel*", "hasLabel*", "hasLabel*", "hasLabel*", "hasLabel*", "hasLabel*"},
      at = @At("HEAD"),
      cancellable = true,
      require = 0
   )
   private void onHasLabel(PlayerLikeEntity entity, double distance, CallbackInfoReturnable<Boolean> cir) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null
         && entity instanceof Entity e
         && e.getId() == mc.player.getId()
         && ModuleRegistry.SELF_NAMETAG != null
         && ModuleRegistry.SELF_NAMETAG.k()
         && !mc.options.getPerspective().isFirstPerson()) {
         cir.setReturnValue(true);
      }
   }
}
