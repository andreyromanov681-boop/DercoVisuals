// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.elements;

import java.awt.Color;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.hud.core.HudElement;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;

public class SaturationHudElement extends HudElement {
   private static final float WIDTH = 118.0F;
   private static final float HEIGHT = 28.0F;
   private final AnimationState visibilityAnimation = new AnimationState();
   private final AnimationState saturationAnimation = new AnimationState();
   private boolean settingsBound;

   public SaturationHudElement(float f, float f2) {
      super(f, f2);
      this.visibilityAnimation.a(1.0, 1.0);
      this.saturationAnimation.a(1.0, 1.0);
      this.d = 81.0F;
      this.e = 9.0F;
   }

   @Override
   protected void a() {
      this.bindSettings();
      float fScale = this.scale();
      this.d = 81.0F * fScale;
      this.e = 9.0F * fScale;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      if (this.a.player != null) {
         if (HudElementManager.a().isEditing() || !this.a.player.isCreative() && !this.a.player.isSpectator()) {
            this.a();
            float fScale = this.scale();
            float fJ = (float)this.visibilityAnimation.j();
            if (!(fJ < 0.01F)) {
               this.visibilityAnimation.a();
               float fGetSaturationLevel = this.a.player.getHungerManager().getSaturationLevel();
               DrawContext currentDrawContext = Renderer2DImpl.currentDrawContext;
               if (currentDrawContext != null) {
                  RenderSystemHelper.enableBlend();
                  RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, fJ);
                  Matrix3x2fStack matrices = currentDrawContext.getMatrices();
                  matrices.pushMatrix();
                  matrices.translate(this.b, this.c);
                  matrices.scale(fScale, fScale);
                  Identifier emptyTex = Identifier.of("minecraft", "hud/food_empty");
                  Identifier halfTex = Identifier.of("minecraft", "hud/food_half");
                  Identifier fullTex = Identifier.of("minecraft", "hud/food_full");
                  boolean isEdit = HudElementManager.a().isEditing();

                  for (int i = 0; i < 10; i++) {
                     int x = 72 - i * 8;
                     int y = 0;
                     float saturationHere = fGetSaturationLevel - i * 2.0F;
                     if (saturationHere >= 1.0F) {
                        currentDrawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, fullTex, x, y, 9, 9);
                     } else if (saturationHere > 0.0F) {
                        currentDrawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, halfTex, x, y, 9, 9);
                     } else if (isEdit && fGetSaturationLevel <= 0.01F) {
                        currentDrawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED, fullTex, x, y, 9, 9);
                     }
                  }

                  matrices.popMatrix();
                  RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               }
            }
         }
      }
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.SATURATION_HUD != null) {
         this.f().a(ModuleRegistry.SATURATION_HUD);
         this.settingsBound = true;
      }
   }

   private float scale() {
      return ModuleRegistry.SATURATION_HUD != null ? ModuleRegistry.SATURATION_HUD.n().get() : 1.0F;
   }

   private static Color withAlpha(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f))));
   }
}
