// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.BufferRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.gui.config.ConfigBoundsDialog;
import dev.enizer6569.dercovisuals.player.PlayerListCache;
import dev.enizer6569.dercovisuals.player.DercoVisualsPlayerTracker;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
   @Unique
   private static final int ICON_SIZE = 8;
   @Unique
   private static final int ICON_PADDING = 2;
   @Unique
   private static final int BACKGROUND_HEIGHT = 9;
   @Unique
   private static final float BG_Z = 0.0F;
   @Unique
   private static final float ICON_Z = 0.0F;
   @Unique
   private static final float SEE_THROUGH_ALPHA = 0.1254902F;
   @Unique
   private static final float SNEAK_DARKEN = 0.5F;
   @Unique
   private boolean shouldRenderIcon = false;
   @Unique
   private boolean isRenderingName = false;
   @Unique
   private int yOffset = 0;
   @Unique
   private float textWidth = 0.0F;
   @Unique
   private boolean isEntitySneaking = false;
   @Unique
   private boolean isSeeThroughMode = false;
   @Unique
   private static final ConcurrentHashMap<String, long[]> ICON_CACHE = new ConcurrentHashMap<>();
   @Unique
   private static final long ICON_CACHE_TTL_MS = 2000L;

   @Shadow
   public abstract TextRenderer getTextRenderer();

   @Unique
   private static boolean isDercoVisualsUser(String name) {
      long now = System.currentTimeMillis();
      long[] cached = ICON_CACHE.get(name);
      if (cached != null && now - cached[0] < 2000L) {
         return cached[1] == 1L;
      }

      boolean has = PlayerListCache.c().a(name) || DercoVisualsPlayerTracker.get().has(name);
      ICON_CACHE.put(name, new long[]{now, has ? 1L : 0L});
      return has;
   }

   @Inject(require = 0, method = "renderLabelIfPresent", at = @At("HEAD"))
   private void onLabelStart(
      EntityRenderState configboundsdialog, MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, CameraRenderState cameraRenderState, CallbackInfo callbackInfo
   ) {
      this.shouldRenderIcon = false;
      this.isRenderingName = true;
      this.isEntitySneaking = configboundsdialog.sneaking;
      this.isSeeThroughMode = !this.isEntitySneaking;
      this.yOffset = 0;
      if (configboundsdialog instanceof PlayerEntityRenderState PlayerEntityRenderStateVar) {
         String playerName = PlayerEntityRenderStateVar.displayName != null ? PlayerEntityRenderStateVar.displayName.getString() : null;
         if (playerName != null) {
            if ("deadmau5".equals(playerName)) {
               this.yOffset = -10;
            }

            if (isDercoVisualsUser(playerName)) {
               this.shouldRenderIcon = true;
            }
         }
      }
   }

   @Unique
   private float shiftedX(float f) {
      return this.shouldRenderIcon && this.isRenderingName ? f + 5.0F : f;
   }

   @ModifyArg(
      require = 0,
      method = "renderLabelIfPresent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/TextRenderer;draw(Lnet/minecraft/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/VertexConsumerProvider;Lnet/minecraft/TextRenderer$class_6415;II)I",
         ordinal = 0
      ),
      index = 1
   )
   private float shiftTextFirst(float f) {
      return this.shiftedX(f);
   }

   @ModifyArg(
      require = 0,
      method = "renderLabelIfPresent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/TextRenderer;draw(Lnet/minecraft/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/VertexConsumerProvider;Lnet/minecraft/TextRenderer$class_6415;II)I",
         ordinal = 1
      ),
      index = 1
   )
   private float shiftTextSecond(float f) {
      return this.shiftedX(f);
   }

   @Unique
   private int maybeKillBackground(int i) {
      return this.shouldRenderIcon && this.isRenderingName ? 0 : i;
   }

   @ModifyArg(
      require = 0,
      method = "renderLabelIfPresent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/TextRenderer;draw(Lnet/minecraft/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/VertexConsumerProvider;Lnet/minecraft/TextRenderer$class_6415;II)I",
         ordinal = 0
      ),
      index = 8
   )
   private int killBgFirst(int i) {
      return this.maybeKillBackground(i);
   }

   @ModifyArg(
      require = 0,
      method = "renderLabelIfPresent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/TextRenderer;draw(Lnet/minecraft/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/VertexConsumerProvider;Lnet/minecraft/TextRenderer$class_6415;II)I",
         ordinal = 1
      ),
      index = 8
   )
   private int killBgSecond(int i) {
      return this.maybeKillBackground(i);
   }

   @Inject(
      require = 0,
      method = "renderLabelIfPresent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/TextRenderer;draw(Lnet/minecraft/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/VertexConsumerProvider;Lnet/minecraft/TextRenderer$class_6415;II)I",
         ordinal = 0
      )
   )
   private void renderCustomBackgroundAndIcon(
      ConfigBoundsDialog configboundsdialog,
      Text TextVar,
      MatrixStack MatrixStackVar,
      VertexConsumerProvider VertexConsumerProviderVar,
      int i,
      CallbackInfo callbackInfo
   ) {
      if (this.shouldRenderIcon && this.isRenderingName) {
         float f = 10.0F + this.textWidth;
         float f2 = -f / 2.0F;
         int iGetTextBackgroundOpacity = ((int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) & 0xFF) << 24;
         Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
         GlStateManager._depthMask(false);

         try {
            this.renderCustomBackground(matrix4fGetPositionMatrix, f2, f, iGetTextBackgroundOpacity, this.isSeeThroughMode);
            float f3 = this.yOffset + 0.5F;
            if (this.isSeeThroughMode) {
               this.renderIconTwoPass(matrix4fGetPositionMatrix, f2, f3);
            } else {
               this.renderIconSingleNormal(matrix4fGetPositionMatrix, f2, f3);
            }
         } finally {
            GlStateManager._depthMask(true);
            GlStateManager._depthFunc(515);
            GlStateManager._enableDepthTest();
            GlStateManager._disableBlend();
         }
      }
   }

   @Unique
   private void renderCustomBackground(Matrix4f matrix4f, float f, float f2, int i, boolean z) {
      int i2 = i >>> 24 & 0xFF;
      int i3 = i >>> 16 & 0xFF;
      int i4 = i >>> 8 & 0xFF;
      int i5 = i & 0xFF;
      GlStateManager._enableBlend();
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(z ? 519 : 515);
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
      BufferBuilder BufferBuilderVarBegin = Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR);
      float f3 = f - 1.0F;
      float f4 = f + f2 + 1.0F;
      float f5 = this.yOffset - 1;
      float f6 = this.yOffset + 9 + 1;
      BufferBuilderVarBegin.vertex(matrix4f, f3, f5, 0.0F).color(i3, i4, i5, i2);
      BufferBuilderVarBegin.vertex(matrix4f, f3, f6, 0.0F).color(i3, i4, i5, i2);
      BufferBuilderVarBegin.vertex(matrix4f, f4, f6, 0.0F).color(i3, i4, i5, i2);
      BufferBuilderVarBegin.vertex(matrix4f, f4, f5, 0.0F).color(i3, i4, i5, i2);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
   }

   @Unique
   private void renderIconTwoPass(Matrix4f matrix4f, float f, float f2) {
      Identifier IdentifierVar = IconTextureRegistry.get("logo");
      GlStateManager._enableBlend();
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(519);
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
      this.drawIconQuad(matrix4f, f, f2, 1.0F, 1.0F, 1.0F, 0.1254902F);
      GlStateManager._depthFunc(515);
      this.drawIconQuad(matrix4f, f, f2, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Unique
   private void renderIconSingleNormal(Matrix4f matrix4f, float f, float f2) {
      Identifier IdentifierVar = IconTextureRegistry.get("logo");
      GlStateManager._enableBlend();
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(515);
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
      this.drawIconQuad(matrix4f, f, f2, 0.5F, 0.5F, 0.5F, 1.0F);
   }

   @Unique
   private void drawIconQuad(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6) {
      int i = (int)(f3 * 255.0F);
      int i2 = (int)(f4 * 255.0F);
      int i3 = (int)(f5 * 255.0F);
      int i4 = (int)(f6 * 255.0F);
      BufferBuilder BufferBuilderVarBegin = Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      BufferBuilderVarBegin.vertex(matrix4f, f, f2, 0.0F).texture(0.0F, 0.0F).color(i, i2, i3, i4);
      BufferBuilderVarBegin.vertex(matrix4f, f, f2 + 8.0F, 0.0F).texture(0.0F, 1.0F).color(i, i2, i3, i4);
      BufferBuilderVarBegin.vertex(matrix4f, f + 8.0F, f2 + 8.0F, 0.0F).texture(1.0F, 1.0F).color(i, i2, i3, i4);
      BufferBuilderVarBegin.vertex(matrix4f, f + 8.0F, f2, 0.0F).texture(1.0F, 0.0F).color(i, i2, i3, i4);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
   }
}
