// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.elements;

import java.awt.Color;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.hud.core.HudElement;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;

public class ScoreboardHudElement extends HudElement {
   private boolean settingsBound;
   private float lastRealWidth = 100.0F;
   private float lastRealHeight = 100.0F;
   private float userOffsetX = 0.0F;
   private float userOffsetY = 0.0F;
   private boolean defaultPositionSet = false;

   public ScoreboardHudElement(float f, float f2) {
      super(f, f2);
      this.d = 100.0F;
      this.e = 100.0F;
   }

   @Override
   protected void a() {
      this.bindSettings();
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.SCOREBOARD_HUD != null) {
         this.f().a(ModuleRegistry.SCOREBOARD_HUD);
         this.settingsBound = true;
      }
   }

   public void updateRealSize(float w, float h) {
      this.lastRealWidth = w;
      this.lastRealHeight = h;
   }

   public float getScale() {
      return ModuleRegistry.SCOREBOARD_HUD != null ? ModuleRegistry.SCOREBOARD_HUD.getScale().get() : this.g();
   }

   public float getUserOffsetX() {
      return this.userOffsetX;
   }

   public float getUserOffsetY() {
      return this.userOffsetY;
   }

   private float getCoordScale() {
      return (float)(this.a.getWindow().getScaleFactor() / 2.0);
   }

   public void syncWithVanilla(float vanillaX, float vanillaY) {
      float cs = this.getCoordScale();
      if (!this.defaultPositionSet && this.hasScreenPosition()) {
         this.userOffsetX = this.l() / cs - vanillaX;
         this.userOffsetY = this.m() / cs - vanillaY;
      }

      this.defaultPositionSet = true;
      if (this.i()) {
         float elementVanillaX = this.l() / cs;
         float elementVanillaY = this.m() / cs;
         this.userOffsetX = elementVanillaX - vanillaX;
         this.userOffsetY = elementVanillaY - vanillaY;
      } else {
         this.b = (vanillaX + this.userOffsetX) * cs;
         this.c = (vanillaY + this.userOffsetY) * cs;
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float screenW, float screenH) {
      if (this.a.player != null) {
         this.a();
         float scale = this.getScale();
         float cs = this.getCoordScale();
         this.d = this.lastRealWidth * scale * cs;
         this.e = this.lastRealHeight * scale * cs;
         boolean isEditMode = HudElementManager.a().isEditing();
         boolean hasScoreboard = this.a.world != null && this.a.world.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) != null;
         if (isEditMode && !hasScoreboard) {
            if (!this.defaultPositionSet) {
               if (!this.hasScreenPosition()) {
                  this.b = screenW - 140.0F;
                  this.c = screenH / 2.0F - 40.0F;
               }

               this.defaultPositionSet = true;
            }

            this.d = 120.0F * scale * cs;
            this.e = 80.0F * scale * cs;
            Color bgColor = new Color(20, 20, 20, 100);
            renderer2D.a(this.b, this.c, this.d, this.e, 4.0F * scale, bgColor, MatrixStackVar);
            FontRenderer font = FontManager.b[Math.max(6, Math.min(64, Math.round(18.0F * scale)))];
            float txtW = font.b("Scoreboard");
            float txtH = font.a("Scoreboard");
            font.a("Scoreboard", this.b + (this.d - txtW) / 2.0F, this.c + (this.e - txtH) / 2.0F, new Color(255, 255, 255, 100), MatrixStackVar);
         }
      }
   }
}
