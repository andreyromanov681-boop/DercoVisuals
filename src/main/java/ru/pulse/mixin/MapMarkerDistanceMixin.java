package ru.pulse.mixin;

import java.lang.reflect.Method;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.markers.MapMarker;
import pulse.markers.MapMarkerRenderer;
import pulse.render.Renderer2D;

@Mixin(value = MapMarkerRenderer.class, remap = false)
public abstract class MapMarkerDistanceMixin {
   @Inject(require = 0, method = "a(D)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
   private void fixDistanceFormat(double d, CallbackInfoReturnable<String> callbackInfoReturnable) {
      if (d < 1.0) {
         callbackInfoReturnable.setReturnValue("<1 м");
      } else if (d < 1000.0) {
         callbackInfoReturnable.setReturnValue((int)d + " м");
      } else {
         callbackInfoReturnable.setReturnValue(String.format("%.1f км", d / 1000.0));
      }
   }

   @Redirect(
      require = 0,
      method = "a(Lnet/minecraft/MatrixStack;Lpulse/render/Renderer2D;Lpulse/markers/MapMarker;)V",
      at = @At(value = "INVOKE", target = "Lpulse/markers/MapMarkerRenderer;a(Lnet/minecraft/MatrixStack;Lpulse/render/Renderer2D;FF)V")
   )
   private void skipBulbIconForLocalMarkers(
      MapMarkerRenderer mapMarkerRenderer,
      MatrixStack MatrixStackVar,
      Renderer2D renderer2D,
      float f,
      float f2,
      MatrixStack MatrixStackVar2,
      Renderer2D renderer2D2,
      MapMarker mapMarker
   ) {
      if (mapMarker.j()) {
         renderBulbIcon(mapMarkerRenderer, MatrixStackVar, renderer2D, f, f2);
      }
   }

   private static void renderBulbIcon(MapMarkerRenderer mapMarkerRenderer, MatrixStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      try {
         Method declaredMethod = MapMarkerRenderer.class.getDeclaredMethod("a", MatrixStack.class, Renderer2D.class, float.class, float.class);
         declaredMethod.setAccessible(true);
         declaredMethod.invoke(mapMarkerRenderer, MatrixStackVar, renderer2D, f, f2);
      } catch (Exception var6) {
      }
   }
}
