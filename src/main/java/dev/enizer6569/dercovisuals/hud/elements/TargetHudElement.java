// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.elements;

import java.awt.Color;
import java.util.Locale;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.hud.core.HudElement;
import dev.enizer6569.dercovisuals.hud.core.HudServiceRegistry;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;

public class TargetHudElement extends HudElement {
   private static final float WIDTH = 130.0F;
   private static final float HEIGHT = 46.0F;
   private static final float AVATAR_SIZE = 28.0F;
   private static final float PADDING = 5.0F;
   private static final float RADIUS = 5.0F;
   private static final Color BACKGROUND = new Color(15, 15, 18, 245);
   private static final Color HEALTH_TEXT = new Color(182, 182, 191, 245);
   private static final Color BAR_BACKGROUND = new Color(21, 18, 28, 245);
   private static final Color BAR_COLOR = new Color(95, 65, 240, 255);
   private static final Color BAR_GLOW = new Color(95, 65, 240, 60);
   private final AnimationState visibilityAnimation = new AnimationState();
   private final AnimationState healthAnimation = new AnimationState();
   private LivingEntity target;
   private String targetName = "Target";
   private float targetHealth;
   private float targetMaxHealth = 20.0F;
   private Identifier skinTexture;
   private boolean visible;
   private boolean settingsBound;

   public TargetHudElement(float x, float y) {
      super(x, y);
      this.visibilityAnimation.d(0.0);
      this.healthAnimation.d(1.0);
      this.d = 130.0F;
      this.e = 46.0F;
   }

   @Override
   protected void a() {
      this.bindSettings();
      float scale = this.g();
      this.d = 130.0F * scale;
      this.e = 46.0F * scale;
   }

   @Override
   public void a(Matrix3x2fStack matrices, Renderer2D renderer, float screenWidth, float screenHeight) {
      if (this.a.player != null) {
         this.a();
         this.updateTarget();
         float alpha = clamp01((float)this.visibilityAnimation.j());
         if (!(alpha < 0.01F)) {
            float scale = this.g();
            float x = this.b;
            float y = this.c;
            float radius = 5.0F * scale;
            renderer.a(x, y, this.d, this.e, radius, withAlpha(BACKGROUND, alpha), matrices);
            float avatarX = x + 5.0F * scale;
            float avatarY = y + 5.0F * scale;
            float avatarSize = 28.0F * scale;
            Identifier texture = this.skinTexture != null ? this.skinTexture : DefaultSkinHelper.getTexture();
            Color textureColor = withAlpha(Color.WHITE, alpha);
            RenderSystemHelper.setShaderTexture(0, texture);
            renderer.a(texture, avatarX, avatarY, avatarSize, avatarSize, 4.0F * scale, 0.125F, 0.125F, 0.125F, 0.125F, textureColor, matrices);
            renderer.a(texture, avatarX, avatarY, avatarSize, avatarSize, 4.0F * scale, 0.625F, 0.125F, 0.125F, 0.125F, textureColor, matrices);
            if (this.target != null && this.target.hurtTime > 0) {
               float hurtAlpha = Math.min(1.0F, this.target.hurtTime / 10.0F) * alpha;
               renderer.a(avatarX, avatarY, avatarSize, avatarSize, 4.0F * scale, new Color(255, 70, 70, Math.round(90.0F * hurtAlpha)), matrices);
            }

            float textX = avatarX + avatarSize + 6.0F * scale;
            String name = this.targetName != null && !this.targetName.isBlank() ? this.targetName : "Target";
            FontRenderer nameFont = FontManager.b[fontSize(13.0F, scale)];
            FontRenderer healthFont = FontManager.b[fontSize(10.0F, scale)];
            nameFont.a(name, textX, y + 9.0F * scale, withAlpha(Color.WHITE, alpha), matrices);
            String health = "HP / " + String.format(Locale.ROOT, "%.1f", Math.max(0.0F, this.targetHealth)).replace('.', ',');
            healthFont.a(health, textX, y + 22.0F * scale, withAlpha(HEALTH_TEXT, alpha), matrices);
            float barX = x + 5.0F * scale;
            float barY = y + 38.0F * scale;
            float barWidth = this.d - 10.0F * scale;
            float barHeight = 2.0F * scale;
            float barRadius = barHeight / 2.0F;
            renderer.a(barX, barY, barWidth, barHeight, barRadius, withAlpha(BAR_BACKGROUND, alpha), matrices);
            float healthProgress = clamp01((float)this.healthAnimation.j());
            if (healthProgress > 0.0F) {
               float filledWidth = Math.min(barWidth, Math.max(barHeight, barWidth * healthProgress));
               float glow = 1.0F * scale;
               renderer.a(
                  barX - glow,
                  barY - glow,
                  filledWidth + glow * 2.0F,
                  barHeight + glow * 2.0F,
                  (barHeight + glow * 2.0F) / 2.0F,
                  withAlpha(BAR_GLOW, alpha),
                  matrices
               );
               renderer.a(barX, barY, filledWidth, barHeight, barRadius, withAlpha(BAR_COLOR, alpha), matrices);
            }
         }
      }
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.TARGET_HUD != null) {
         this.f().a(ModuleRegistry.TARGET_HUD);
         this.settingsBound = true;
      }
   }

   private void updateTarget() {
      LivingEntity currentTarget = this.validTarget(HudServiceRegistry.TARGETS.currentTarget());
      if (currentTarget == null && this.isEditing()) {
         currentTarget = this.a.player;
      }

      if (currentTarget != null) {
         if (currentTarget != this.target) {
            this.target = currentTarget;
            this.updateTargetIdentity(currentTarget);
            float healthProgress = clamp01(currentTarget.getHealth() / Math.max(1.0F, currentTarget.getMaxHealth()));
            this.healthAnimation.d(healthProgress);
         }

         this.targetHealth = Math.max(0.0F, currentTarget.getHealth());
         this.targetMaxHealth = Math.max(1.0F, currentTarget.getMaxHealth());
         this.updateTargetIdentity(currentTarget);
      }

      boolean shouldBeVisible = currentTarget != null;
      if (!shouldBeVisible) {
         this.target = null;
      }

      if (shouldBeVisible != this.visible) {
         this.visible = shouldBeVisible;
         this.visibilityAnimation.a(shouldBeVisible ? 1.0 : 0.0, 0.2, Easing.h);
      }

      float healthProgress = clamp01(this.targetHealth / this.targetMaxHealth);
      if (Math.abs(this.healthAnimation.i() - healthProgress) > 0.01) {
         this.healthAnimation.a(healthProgress, 0.15, Easing.h);
      }

      this.visibilityAnimation.a();
      this.healthAnimation.a();
   }

   private void updateTargetIdentity(LivingEntity entity) {
      this.targetName = entity.getName().getString();
      this.skinTexture = entity instanceof PlayerEntity player ? this.skinFor(player) : DefaultSkinHelper.getTexture();
   }

   private LivingEntity validTarget(LivingEntity entity) {
      return entity != null && entity != this.a.player && entity.isAlive() && !entity.isRemoved() ? entity : null;
   }

   private boolean isEditing() {
      return this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen;
   }

   private Identifier skinFor(PlayerEntity player) {
      try {
         if (player instanceof AbstractClientPlayerEntity clientPlayer) {
            Identifier texture = clientPlayer.getSkin().comp_1626().comp_3626();
            if (texture != null) {
               return texture;
            }
         }
      } catch (Throwable var4) {
      }

      return DefaultSkinHelper.getTexture();
   }

   private static int fontSize(float size, float scale) {
      return Math.max(6, Math.min(64, Math.round(size * scale)));
   }

   private static float clamp01(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }

   private static Color withAlpha(Color color, float alpha) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, Math.round(color.getAlpha() * clamp01(alpha)))));
   }
}
