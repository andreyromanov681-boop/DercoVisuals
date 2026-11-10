package ru.pulse.mixin;

import net.minecraft.util.math.Direction;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.modules.utilities.Optimization;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
   @Inject(method = "isSideInvisible", at = @At("HEAD"), cancellable = true, require = 0)
   private void onIsSideInvisible(BlockState state, BlockState stateFrom, Direction direction, CallbackInfoReturnable<Boolean> cir) {
      if (Optimization.INSTANCE != null
         && Optimization.INSTANCE.l()
         && Optimization.INSTANCE.simpleLeaves.get()
         && stateFrom.getBlock() instanceof LeavesBlock) {
         cir.setReturnValue(true);
      }
   }
}
