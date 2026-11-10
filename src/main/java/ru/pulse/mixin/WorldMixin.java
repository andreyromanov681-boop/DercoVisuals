package ru.pulse.mixin;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.module.ModuleRegistry;
import pulse.modules.visuals.Atmosphere;
import pulse.modules.visuals.RenderTweaks;

@Mixin(World.class)
public class WorldMixin {
   @Inject(require = 0, method = "isRaining", at = @At("HEAD"), cancellable = true)
   private void onIsRaining(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isWeatherCustom()) {
         String mode = atmosphere.getWeatherMode();
         if (mode.equals("Ясно")) {
            callbackInfoReturnable.setReturnValue(false);
            return;
         }

         if (mode.equals("Дождь") || mode.equals("Снег") || mode.equals("Гроза")) {
            callbackInfoReturnable.setReturnValue(true);
            return;
         }
      }

      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      if (renderTweaks != null && renderTweaks.n()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(require = 0, method = "isThundering", at = @At("HEAD"), cancellable = true)
   private void onIsThundering(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isWeatherCustom()) {
         String mode = atmosphere.getWeatherMode();
         if (mode.equals("Гроза")) {
            callbackInfoReturnable.setReturnValue(true);
         } else {
            callbackInfoReturnable.setReturnValue(false);
         }
      } else {
         RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
         if (renderTweaks != null && renderTweaks.n()) {
            callbackInfoReturnable.setReturnValue(false);
         }
      }
   }

   @Inject(require = 0, method = "hasRain", at = @At("HEAD"), cancellable = true)
   private void onHasRain(BlockPos BlockPosVar, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isWeatherCustom()) {
         String mode = atmosphere.getWeatherMode();
         if (mode.equals("Ясно")) {
            callbackInfoReturnable.setReturnValue(false);
            return;
         }

         if (mode.equals("Дождь") || mode.equals("Снег") || mode.equals("Гроза")) {
            callbackInfoReturnable.setReturnValue(true);
            return;
         }
      }

      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      if (renderTweaks != null && renderTweaks.n()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(require = 0, method = "getRainGradient", at = @At("HEAD"), cancellable = true)
   private void onGetRainGradient(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isWeatherCustom()) {
         String mode = atmosphere.getWeatherMode();
         if (mode.equals("Ясно")) {
            callbackInfoReturnable.setReturnValue(0.0F);
            return;
         }

         if (mode.equals("Дождь") || mode.equals("Снег") || mode.equals("Гроза")) {
            callbackInfoReturnable.setReturnValue(1.0F);
            return;
         }
      }

      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      if (renderTweaks != null && renderTweaks.n()) {
         callbackInfoReturnable.setReturnValue(0.0F);
      }
   }

   @Inject(require = 0, method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
   private void onGetThunderGradient(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isWeatherCustom()) {
         String mode = atmosphere.getWeatherMode();
         if (mode.equals("Гроза")) {
            callbackInfoReturnable.setReturnValue(1.0F);
         } else {
            callbackInfoReturnable.setReturnValue(0.0F);
         }
      } else {
         RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
         if (renderTweaks != null && renderTweaks.n()) {
            callbackInfoReturnable.setReturnValue(0.0F);
         }
      }
   }

   @Inject(require = 0, method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
   private void onGetTimeOfDay(CallbackInfoReturnable<Long> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isTimeCustom()) {
         callbackInfoReturnable.setReturnValue(atmosphere.getCustomTime());
      }
   }

   @Inject(require = 0, method = "getTime", at = @At("HEAD"), cancellable = true)
   private void onGetTime(CallbackInfoReturnable<Long> callbackInfoReturnable) {
      Atmosphere atmosphere = ModuleRegistry.ATMOSPHERE;
      if (atmosphere != null && atmosphere.k() && atmosphere.isTimeCustom()) {
         callbackInfoReturnable.setReturnValue(atmosphere.getCustomTime());
      }
   }
}
