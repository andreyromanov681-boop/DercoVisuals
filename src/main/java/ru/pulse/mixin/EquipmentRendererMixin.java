package ru.pulse.mixin;

import java.awt.Color;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import pulse.module.ModuleRegistry;
import pulse.modules.visuals.HitColor;
import pulse.render.ArmorRenderState;
import pulse.util.ColorUtils;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
   @Unique
   private static final Identifier HIT_COLOR_TEXTURE = Identifier.of("pulse", "textures/misc/white.png");

   @ModifyArg(
      require = 0,
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/RenderCommandQueue;submitModel(Lnet/minecraft/Model;Ljava/lang/Object;Lnet/minecraft/MatrixStack;Lnet/minecraft/RenderLayer;IIILnet/minecraft/Sprite;ILnet/minecraft/ModelCommandRenderer$class_11792;)V"
      ),
      index = 6
   )
   private int modifyArmorColor(int originalColor) {
      HitColor hitColor = ModuleRegistry.HIT_COLOR;
      if (hitColor != null && hitColor.o() && hitColor.p() && ArmorRenderState.a()) {
         Color colorN = hitColor.n();
         return ColorUtils.a(colorN.getRed(), colorN.getGreen(), colorN.getBlue(), 255);
      } else {
         return originalColor;
      }
   }
}
