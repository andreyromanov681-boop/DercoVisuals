package ru.pulse.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.AssetInfo.class_10726;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.cosmetic.CapeResolver;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
   @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true, require = 0)
   private void pulse$useCustomCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
      AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)this;
      if (CapeResolver.b(player)) {
         Identifier capeTex = CapeResolver.a(player);
         if (capeTex != null) {
            SkinTextures original = (SkinTextures)cir.getReturnValue();
            if (original != null) {
               class_10726 customCapeAsset = new class_10726(capeTex, capeTex);
               cir.setReturnValue(new SkinTextures(original.comp_1626(), customCapeAsset, customCapeAsset, original.comp_1629(), original.comp_1630()));
            }
         }
      }
   }
}
