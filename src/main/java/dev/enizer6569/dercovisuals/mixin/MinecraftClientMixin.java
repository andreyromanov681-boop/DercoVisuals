// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.RunArgs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.MinecraftClient.class_5859;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.events.AttackEntityEvent;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.WorldChangeEvent;
import dev.enizer6569.dercovisuals.gui.menu.DercoVisualsMainMenuScreen;
import dev.enizer6569.dercovisuals.render.RenderFrameTimer;
import dev.enizer6569.dercovisuals.DercoVisuals;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftContext {
   @Inject(require = 0, method = "joinWorld", at = @At("RETURN"))
   private void onWorldChange(ClientWorld ClientWorldVar, CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new WorldChangeEvent());
   }

   @Inject(require = 0, method = "<init>", at = @At("TAIL"))
   public void init(RunArgs RunArgsVar, CallbackInfo callbackInfo) {
      DercoVisuals.getInstance().init();
   }

   @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/MinecraftClient;fpsCounter:I", opcode = 181, ordinal = 0), require = 0)
   private void render(boolean z, CallbackInfo callbackInfo) {
      try {
         Class.forName("ru.pulse.DercoVisuals").getMethod("runtimeBrandingTick").invoke(null);
      } catch (Throwable var4) {
      }

      RenderFrameTimer.b();
   }

   @Inject(require = 0, method = "isMultiplayerEnabled", at = @At("HEAD"), cancellable = true)
   public void allowsMultiplayer(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      callbackInfoReturnable.setReturnValue(true);
   }

   @Inject(require = 0, method = "getChatRestriction", at = @At("HEAD"), cancellable = true)
   public void allowsChat(CallbackInfoReturnable<class_5859> callbackInfoReturnable) {
      callbackInfoReturnable.setReturnValue(class_5859.ENABLED);
   }

   @Inject(require = 0, method = "isRealmsEnabled", at = @At("HEAD"), cancellable = true)
   public void allowsRealms(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      callbackInfoReturnable.setReturnValue(false);
   }

   @Inject(
      require = 0,
      method = "doAttack",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/PlayerEntity;Lnet/minecraft/Entity;)V")
   )
   private void onAttack(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (c.targetedEntity != null) {
         EventBusService.EVENT_BUS.post(new AttackEntityEvent(c.targetedEntity));
      }
   }

   @Inject(require = 0, method = "getSession", at = @At("HEAD"), cancellable = true)
   private void onGetSession(CallbackInfoReturnable<Session> cir) {
      if (DercoVisualsMainMenuScreen.selectedAccount != null && !DercoVisualsMainMenuScreen.selectedAccount.trim().isEmpty()) {
         Session s = DercoVisualsMainMenuScreen.createSession(DercoVisualsMainMenuScreen.selectedAccount);
         if (s != null) {
            cir.setReturnValue(s);
         }
      }
   }
}
