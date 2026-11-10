// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.Biome.class_1963;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.Atmosphere;

@Mixin(Biome.class)
public class BiomeMixin {
   @Inject(require = 0, method = "getPrecipitation", at = @At("HEAD"), cancellable = true)
   private void onGetPrecipitation(BlockPos pos, int i, CallbackInfoReturnable<class_1963> cir) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isWeatherCustom()) {
         String mode = atmosphere.getWeatherMode();
         if (mode.equals("Снег")) {
            cir.setReturnValue(class_1963.SNOW);
         } else if (mode.equals("Дождь") || mode.equals("Гроза")) {
            cir.setReturnValue(class_1963.RAIN);
         } else if (mode.equals("Ясно")) {
            cir.setReturnValue(class_1963.NONE);
         }
      }
   }
}
