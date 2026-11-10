// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.MovementInputEvent;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
   @Shadow
   @Final
   private GameOptions settings;

   @Inject(require = 0, method = "tick", at = @At("HEAD"), cancellable = true)
   private void onTick(CallbackInfo callbackInfo) {
      this.playerInput = new PlayerInput(
         this.settings.forwardKey.isPressed(),
         this.settings.backKey.isPressed(),
         this.settings.leftKey.isPressed(),
         this.settings.rightKey.isPressed(),
         this.settings.jumpKey.isPressed(),
         this.settings.sneakKey.isPressed(),
         this.settings.sprintKey.isPressed()
      );
      float forward = getMovementMultiplier(this.playerInput.comp_3159(), this.playerInput.comp_3160());
      float sideways = getMovementMultiplier(this.playerInput.comp_3161(), this.playerInput.comp_3162());
      MovementInputEvent movementInputEvent = new MovementInputEvent(forward, sideways, this.playerInput.comp_3163(), this.playerInput.comp_3164(), 0.3F);
      EventBusService.EVENT_BUS.post(movementInputEvent);
      if (movementInputEvent.c()) {
         this.movementVector = new Vec2f(0.0F, 0.0F);
         this.playerInput = new PlayerInput(false, false, false, false, false, false, this.playerInput.comp_3165());
      } else {
         this.movementVector = new Vec2f(movementInputEvent.e(), movementInputEvent.d());
         this.playerInput = new PlayerInput(
            movementInputEvent.d() > 0.0F,
            movementInputEvent.d() < 0.0F,
            movementInputEvent.e() > 0.0F,
            movementInputEvent.e() < 0.0F,
            movementInputEvent.f(),
            movementInputEvent.g(),
            this.playerInput.comp_3165()
         );
      }

      callbackInfo.cancel();
   }

   @Unique
   private static float getMovementMultiplier(boolean z, boolean z2) {
      if (z == z2) {
         return 0.0F;
      } else {
         return z ? 1.0F : -1.0F;
      }
   }
}
