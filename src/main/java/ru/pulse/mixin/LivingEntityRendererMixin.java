package ru.pulse.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.EntityRendererFactory.class_5618;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.modules.utilities.Optimization;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {
   @Shadow
   protected M model;
   private static LivingEntity capturedEntity;

   protected LivingEntityRendererMixin() {
      super((class_5618)null);
   }

   @Inject(require = 0, method = "updateRenderState", at = @At("HEAD"))
   private void captureEntity(T t, S s, float f, CallbackInfo callbackInfo) {
      capturedEntity = t;
   }

   @Inject(require = 0, method = "render", at = @At("HEAD"), cancellable = true)
   private void onRenderEntity(S s, MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, CameraRenderState cameraRenderState, CallbackInfo callbackInfo) {
      if (capturedEntity != null && !Optimization.shouldRenderEntity(capturedEntity)) {
         callbackInfo.cancel();
      }
   }

   private static Text getServerDisplayName(MinecraftClient MinecraftClientVar, String str) {
      PlayerListEntry PlayerListEntryVarGetPlayerListEntry;
      return (Text)(MinecraftClientVar.getNetworkHandler() != null
            && MinecraftClientVar.player != null
            && (PlayerListEntryVarGetPlayerListEntry = MinecraftClientVar.getNetworkHandler().getPlayerListEntry(MinecraftClientVar.player.getUuid())) != null
            && PlayerListEntryVarGetPlayerListEntry.getDisplayName() != null
         ? PlayerListEntryVarGetPlayerListEntry.getDisplayName()
         : Text.literal(str));
   }
}
