// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import dev.enizer6569.dercovisuals.render.NativeImagePointerAccessor;

@Mixin(NativeImage.class)
public class NativeImageMixin implements NativeImagePointerAccessor {
   @Shadow
   private long pointer;

   @Override
   public long pointer() {
      return this.pointer;
   }
}
