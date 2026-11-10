package ru.pulse.mixin;

import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.events.BlockOutlineEvent;
import pulse.events.EventBusService;
import pulse.module.ModuleRegistry;

@Mixin(WorldRenderer.class)
public class BlockOutlineMixin {
   @Inject(method = {"drawBlockOutline*", "drawBlockOutline*"}, at = @At("HEAD"), cancellable = true, require = 0)
   private void onDrawBlockOutline(
      MatrixStack MatrixStackVar,
      VertexConsumer VertexConsumerVar,
      double d,
      double d2,
      double d3,
      OutlineRenderState outlineRenderState,
      int i,
      float f,
      CallbackInfo callbackInfo
   ) {
      if (ModuleRegistry.BLOCK_OVERLAY != null && ModuleRegistry.BLOCK_OVERLAY.k()) {
         BlockPos pos = outlineRenderState != null ? outlineRenderState.comp_4932() : null;
         MinecraftClient client = MinecraftClient.getInstance();
         BlockState state = client.world != null && pos != null ? client.world.getBlockState(pos) : null;
         BlockOutlineEvent blockOutlineEvent = new BlockOutlineEvent(MatrixStackVar, VertexConsumerVar, null, d, d2, d3, pos, state);
         EventBusService.EVENT_BUS.post(blockOutlineEvent);
         if (blockOutlineEvent.c()) {
            callbackInfo.cancel();
         }
      }
   }
}
