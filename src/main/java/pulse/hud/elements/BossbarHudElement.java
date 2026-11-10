package pulse.hud.elements;

import java.awt.Color;
import org.joml.Matrix3x2fStack;
import pulse.hud.core.HudElement;
import pulse.hud.core.HudElementManager;
import pulse.module.ModuleRegistry;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import ru.pulse.mixin.accessor.BossBarHudAccessor;

public class BossbarHudElement extends HudElement {
   private boolean settingsBound;
   private float lastRealWidth = 182.0F;
   private float lastRealHeight = 20.0F;
   private float userOffsetX = 0.0F;
   private float userOffsetY = 0.0F;
   private boolean defaultPositionSet = false;

   public BossbarHudElement(float f, float f2) {
      super(f, f2);
      this.d = 182.0F;
      this.e = 20.0F;
   }

   @Override
   protected void a() {
      this.bindSettings();
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.BOSSBAR_HUD != null) {
         this.f().a(ModuleRegistry.BOSSBAR_HUD);
         this.settingsBound = true;
      }
   }

   public void updateRealSize(float w, float h) {
      this.lastRealWidth = w;
      this.lastRealHeight = h;
   }

   public float getScale() {
      return ModuleRegistry.BOSSBAR_HUD != null ? ModuleRegistry.BOSSBAR_HUD.getScale().get() : this.g();
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
         this.d = Math.max(182.0F * scale * cs, this.lastRealWidth * scale * cs);
         this.e = Math.max(20.0F * scale * cs, this.lastRealHeight * scale * cs);
         boolean isEditMode = HudElementManager.a().isEditing();
         boolean hasBossbars = !((BossBarHudAccessor)this.a.inGameHud.getBossBarHud()).getBossBars().isEmpty();
         if (isEditMode && !hasBossbars) {
            if (!this.defaultPositionSet) {
               this.b = screenW / 2.0F - 91.0F * cs;
               this.c = 12.0F * cs;
               this.defaultPositionSet = true;
            }

            this.d = 182.0F * scale * cs;
            this.e = 30.0F * scale * cs;
            Color bgColor = new Color(20, 20, 20, 100);
            renderer2D.a(this.b, this.c, this.d, this.e, 4.0F * scale, bgColor, MatrixStackVar);
            FontRenderer font = FontManager.b[Math.max(6, Math.min(64, Math.round(18.0F * scale)))];
            float txtW = font.b("Bossbar");
            float txtH = font.a("Bossbar");
            font.a("Bossbar", this.b + (this.d - txtW) / 2.0F, this.c + (this.e - txtH) / 2.0F, new Color(255, 255, 255, 100), MatrixStackVar);
         }
      }
   }
}
