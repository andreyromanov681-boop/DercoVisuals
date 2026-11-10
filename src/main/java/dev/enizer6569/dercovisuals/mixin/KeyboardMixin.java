// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.input.KeyInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.HitResult.class_240;
import net.minecraft.RaycastContext.class_242;
import net.minecraft.RaycastContext.class_3960;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.KeyInputEvent;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiKeyBinding;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.GuiInteractionState;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.gui.menu.DercoVisualsKeybindManagerScreen;
import dev.enizer6569.dercovisuals.markers.MapMarker;
import dev.enizer6569.dercovisuals.markers.MarkerManager;
import dev.enizer6569.dercovisuals.markers.MarkerSettings;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.DercoVisuals;

@Mixin(Keyboard.class)
public class KeyboardMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Unique
   private static final double MAX_RAYCAST_DISTANCE = 500.0;

   @Inject(require = 0, method = "onKey", at = @At("HEAD"))
   private void onKey(long window, int action, KeyInput keyInput, CallbackInfo callbackInfo) {
      if (window == this.client.getWindow().getHandle()) {
         int i = keyInput.comp_4795();
         int i2 = keyInput.comp_4796();
         int i3 = action;
         int i4 = keyInput.comp_4797();
         EventBusService.EVENT_BUS.post(new KeyInputEvent(i, i2, i3, i4));
         if (MinecraftContext.c.player == null || MinecraftContext.c.world == null || i3 != 1) {
            return;
         }

         if (MinecraftContext.c.currentScreen != null) {
            if (ClickGuiKeyBinding.OPEN_KEY.getDefaultKey().getCode() == i) {
               if (MinecraftContext.c.currentScreen instanceof DercoVisualsClickGuiScreen pulseClickGuiScreen) {
                  if (GuiInteractionState.a().b()) {
                     pulseClickGuiScreen.c();
                     return;
                  }

                  GuiInput.j();
                  MinecraftContext.c.setScreen((Screen)null);
                  return;
               }

               return;
            }

            return;
         }

         if (ClickGuiKeyBinding.OPEN_KEY.getDefaultKey().getCode() == i) {
            System.out.println("[DercoVisuals DEBUG] Right Shift pressed! Setting screen to ClickGui: " + DercoVisuals.getInstance().getClickGui());
            MinecraftContext.c.setScreen(DercoVisuals.getInstance().getClickGui());
            return;
         }

         DercoVisualsKeybindManagerScreen.executeKeyBind(i);
         int iB = MarkerSettings.b();
         if (MarkerSettings.a() && iB != 0 && iB == i) {
            this.createQuickMarker();
            return;
         }

         for (ClientModule clientModule : ModuleRegistry.all()) {
            if (clientModule.j() == i) {
               clientModule.d();
            }
         }
      }
   }

   @Unique
   private void createQuickMarker() {
      if (MinecraftContext.c.player != null && MinecraftContext.c.world != null) {
         Vec3d Vec3dVarGetCameraPosVec = MinecraftContext.c.player.getCameraPosVec(1.0F);
         BlockHitResult BlockHitResultVarRaycast = MinecraftContext.c
            .world
            .raycast(
               new RaycastContext(
                  Vec3dVarGetCameraPosVec,
                  Vec3dVarGetCameraPosVec.add(MinecraftContext.c.player.getRotationVec(1.0F).multiply(500.0)),
                  class_3960.OUTLINE,
                  class_242.NONE,
                  MinecraftContext.c.player
               )
            );
         if (BlockHitResultVarRaycast.getType() == class_240.BLOCK) {
            BlockPos BlockPosVarGetBlockPos = BlockHitResultVarRaycast.getBlockPos();
            MarkerManager.a(
               new MapMarker(
                  "Быстрая метка",
                  BlockPosVarGetBlockPos.getX(),
                  BlockPosVarGetBlockPos.getY(),
                  BlockPosVarGetBlockPos.getZ(),
                  Theme.X,
                  MapMarker.Icon.FAST
               )
            );
         }
      }
   }
}
