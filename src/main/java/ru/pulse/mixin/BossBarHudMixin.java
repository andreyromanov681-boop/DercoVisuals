package ru.pulse.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.hud.core.HudElementManager;
import pulse.hud.elements.BossbarHudElement;
import pulse.module.ModuleRegistry;
import pulse.modules.visuals.RenderTweaks;
import ru.pulse.mixin.accessor.BossBarHudAccessor;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
   @Unique
   private boolean pulse$matrixPushed;

   @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRender(DrawContext DrawContextVar, CallbackInfo callbackInfo) {
      this.pulse$matrixPushed = false;
      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      if (renderTweaks != null && renderTweaks.s()) {
         callbackInfo.cancel();
      } else {
         BossbarHudElement bbElement = HudElementManager.a().getBossbarHud();
         if (bbElement != null) {
            float vanillaX = DrawContextVar.getScaledWindowWidth() / 2 - 91;
            float vanillaY = 12.0F;
            bbElement.syncWithVanilla(vanillaX, vanillaY);
            boolean hasBossbars = !((BossBarHudAccessor)this).getBossBars().isEmpty();
            if (hasBossbars) {
               float scale = bbElement.getScale();
               float offsetX = bbElement.getUserOffsetX();
               float offsetY = bbElement.getUserOffsetY();
               boolean needsTransform = offsetX != 0.0F || offsetY != 0.0F || scale != 1.0F;
               if (needsTransform) {
                  DrawContextVar.getMatrices().pushMatrix();
                  this.pulse$matrixPushed = true;
                  DrawContextVar.getMatrices().translate(vanillaX + offsetX, vanillaY + offsetY);
                  DrawContextVar.getMatrices().scale(scale, scale);
                  DrawContextVar.getMatrices().translate(-vanillaX, -vanillaY);
               }
            }
         }
      }
   }

   @Inject(method = "render", at = @At("RETURN"), require = 0)
   private void afterRender(DrawContext DrawContextVar, CallbackInfo callbackInfo) {
      if (this.pulse$matrixPushed) {
         DrawContextVar.getMatrices().popMatrix();
         this.pulse$matrixPushed = false;
      }
   }
}
