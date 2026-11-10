package ru.pulse.mixin;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pulse.module.ModuleRegistry;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
   @Redirect(require = 0, method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
   private void redirectMultiply(MatrixStack instance, Quaternionf quaternion) {
      if (ModuleRegistry.ITEM_PHYSICS == null || !ModuleRegistry.ITEM_PHYSICS.isEnabled()) {
         instance.multiply(quaternion);
      }
   }
}
