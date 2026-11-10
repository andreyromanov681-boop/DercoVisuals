// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.awt.Color;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.ItemRenderState.class_10445;
import net.minecraft.client.render.BufferRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.ItemOverlayRenderEvent;
import dev.enizer6569.dercovisuals.inventory.CooldownInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.utilities.AutoReissue;
import dev.enizer6569.dercovisuals.modules.utilities.Cooldowns;
import dev.enizer6569.dercovisuals.modules.utilities.HealingHelper;
import dev.enizer6569.dercovisuals.modules.utilities.ItemHighlighter;
import dev.enizer6569.dercovisuals.modules.utilities.MaceHelper;
import dev.enizer6569.dercovisuals.modules.visuals.CustomHand;
import dev.enizer6569.dercovisuals.render.CustomHandShaderConsumer;
import dev.enizer6569.dercovisuals.render.HandRenderState;
import dev.enizer6569.dercovisuals.render.MaceHelperHandConsumer;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
   @Inject(method = "renderItem", at = @At("HEAD"))
   private static void onRenderItemHead(
      ItemDisplayContext context,
      MatrixStack matrices,
      VertexConsumerProvider provider,
      int light,
      int overlay,
      int[] colors,
      List list,
      RenderLayer renderLayer,
      class_10445 glint,
      CallbackInfo ci
   ) {
      if (context.isFirstPerson()) {
         HandRenderState.renderingHand = true;
      }

      if (context == ItemDisplayContext.GROUND && ModuleRegistry.ITEM_PHYSICS != null && ModuleRegistry.ITEM_PHYSICS.isEnabled()) {
         matrices.translate(0.0F, 0.47F, 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
      }
   }

   @Inject(method = "renderItem", at = @At("TAIL"))
   private static void onRenderItemTail(
      ItemDisplayContext context,
      MatrixStack matrices,
      VertexConsumerProvider provider,
      int light,
      int overlay,
      int[] colors,
      List list,
      RenderLayer renderLayer,
      class_10445 glint,
      CallbackInfo ci
   ) {
      HandRenderState.renderingHand = false;
   }

   @ModifyVariable(method = "renderBakedItemQuads", at = @At("HEAD"), argsOnly = true, ordinal = 0, index = 1)
   private static VertexConsumer modifyVertexConsumerInItemRenderer(VertexConsumer original) {
      MaceHelper maceHelper = ModuleRegistry.MACE_HELPER;
      if ((HandRenderState.renderingMace || HandRenderState.renderingHand)
         && maceHelper != null
         && maceHelper.k()
         && maceHelper.handHighlight.get()
         && MaceHelper.isHoldingMace()) {
         return new MaceHelperHandConsumer(original, MaceHelper.getChargeColor());
      }

      CustomHand module = ModuleRegistry.CUSTOM_HAND;
      return HandRenderState.renderingHand && module != null && module.k() && module.shaderEnabled.a()
         ? new CustomHandShaderConsumer(original, module)
         : original;
   }

   public void renderGuiItemOverlay(DrawContext DrawContextVar, TextRenderer TextRendererVar, ItemStack ItemStackVar, int i, int i2) {
      this.renderGuiItemOverlay(DrawContextVar, TextRendererVar, ItemStackVar, i, i2, null);
   }

   public void renderGuiItemOverlay(DrawContext DrawContextVar, TextRenderer TextRendererVar, ItemStack ItemStackVar, int i, int i2, @Nullable String str) {
      if (!ItemStackVar.isEmpty()) {
         Item ItemVarGetItem = ItemStackVar.getItem();
         ItemHighlighter itemHighlighter = ModuleRegistry.ITEM_HIGHLIGHTER;
         if (itemHighlighter != null && itemHighlighter.k() && itemHighlighter.a(ItemStackVar)) {
            Color colorB = itemHighlighter.b(ItemStackVar);
            RenderSystemHelper.disableDepthTest();
            RenderSystemHelper.enableBlend();
            RenderSystemHelper.defaultBlendFunc();
            this.renderGuiQuad(
               Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR),
               i,
               i2,
               16,
               16,
               colorB.getRed(),
               colorB.getGreen(),
               colorB.getBlue(),
               itemHighlighter.n()
            );
            RenderSystemHelper.enableDepthTest();
         }

         MaceHelper maceHelper = ModuleRegistry.MACE_HELPER;
         if (maceHelper != null && maceHelper.k() && maceHelper.hotbarHighlight.get() && MaceHelper.isMace(ItemStackVar)) {
            Color colorB = MaceHelper.getChargeColor();
            RenderSystemHelper.disableDepthTest();
            RenderSystemHelper.enableBlend();
            RenderSystemHelper.defaultBlendFunc();
            this.renderGuiQuad(
               Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR),
               i,
               i2,
               16,
               16,
               colorB.getRed(),
               colorB.getGreen(),
               colorB.getBlue(),
               120
            );
            RenderSystemHelper.enableDepthTest();
         }

         if (ItemStackVar.getCount() != 1 || str != null) {
            String strValueOf = str == null ? String.valueOf(ItemStackVar.getCount()) : str;
            DrawContextVar.drawText(TextRendererVar, strValueOf, i + 19 - 2 - TextRendererVar.getWidth(strValueOf), i2 + 6 + 3, 16777215, true);
         }

         if (ItemStackVar.isDamaged()) {
            RenderSystemHelper.disableDepthTest();
            RenderSystemHelper.enableBlend();
            RenderSystemHelper.defaultBlendFunc();
            BufferBuilder BufferBuilderVarBegin = Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR);
            float fGetDamage = ItemStackVar.getDamage();
            float fGetMaxDamage = ItemStackVar.getMaxDamage();
            int iRound = Math.round(13.0F - fGetDamage * 13.0F / fGetMaxDamage);
            int iHsvToRgb = MathHelper.hsvToRgb(Math.max(0.0F, (fGetMaxDamage - fGetDamage) / fGetMaxDamage) / 3.0F, 1.0F, 1.0F);
            this.renderGuiQuad(BufferBuilderVarBegin, i + 2, i2 + 13, 13, 2, 0, 0, 0, 255);
            this.renderGuiQuad(BufferBuilderVarBegin, i + 2, i2 + 13, iRound, 1, iHsvToRgb >> 16 & 0xFF, iHsvToRgb >> 8 & 0xFF, iHsvToRgb & 0xFF, 255);
            RenderSystemHelper.enableDepthTest();
         }

         ClientPlayerEntity ClientPlayerEntityVar = MinecraftClient.getInstance().player;
         float fC = 0.0F;
         if (ClientPlayerEntityVar != null) {
            fC = CooldownInfo.a(ClientPlayerEntityVar.getItemCooldownManager(), ItemVarGetItem).c();
         }

         Cooldowns cooldowns = ModuleRegistry.COOLDOWNS;
         if (cooldowns != null && cooldowns.k()) {
            float fA = cooldowns.a(ItemVarGetItem);
            if (fA > fC) {
               fC = fA;
            }
         }

         if (fC > 0.0F) {
            RenderSystemHelper.disableDepthTest();
            RenderSystemHelper.enableBlend();
            RenderSystemHelper.defaultBlendFunc();
            this.renderGuiQuad(
               Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR),
               i,
               i2 + MathHelper.floor(16.0F * (1.0F - fC)),
               16,
               MathHelper.ceil(16.0F * fC),
               255,
               255,
               255,
               127
            );
            RenderSystemHelper.enableDepthTest();
         }

         HealingHelper healingHelper = ModuleRegistry.HEALING_HELPER;
         if (healingHelper != null && healingHelper.k() && healingHelper.a(ItemStackVar)) {
            Stream<Item> stream = healingHelper.n().stream();
            int[] colorByPriority = this.getColorByPriority(healingHelper.a(ItemVarGetItem), stream.mapToInt(healingHelper::a).max().orElse(1));
            RenderSystemHelper.disableDepthTest();
            RenderSystemHelper.enableBlend();
            RenderSystemHelper.defaultBlendFunc();
            this.renderGuiQuad(
               Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR),
               i,
               i2,
               16,
               16,
               colorByPriority[0],
               colorByPriority[1],
               colorByPriority[2],
               (int)((Math.sin(System.currentTimeMillis() / 100.0) * 0.5 + 0.5) * 100.0) + 50
            );
            RenderSystemHelper.enableDepthTest();
         }

         if (ItemStackVar.getItem() == Items.CLOCK) {
            AutoReissue autoReissue = ModuleRegistry.AUTO_REISSUE;
            if (ModuleRegistry.AUTO_REISSUE != null && autoReissue.k()) {
               MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
               if (MinecraftClientVarGetInstance.currentScreen != null && MinecraftClientVarGetInstance.player.currentScreenHandler != null) {
                  try {
                     MinecraftClientVarGetInstance.currentScreen.getTitle().getString();
                  } catch (Exception var24) {
                  }

                  if (autoReissue.overlayActive) {
                     long j = autoReissue.timer.a(0L) ? autoReissue.durationMs : 0L;

                     for (int i3 = 1; i3 <= autoReissue.durationMs / 1000 + 1; i3++) {
                        if (!autoReissue.timer.a(i3 * 1000)) {
                           j = (i3 - 1) * 1000;
                           break;
                        }
                     }

                     int iMax = Math.max(0, (autoReissue.durationMs - (int)j) / 1000);
                     float fMax = Math.max(0.0F, 1.0F - (float)(j / autoReissue.durationMs));
                     if (iMax > 0) {
                        RenderSystemHelper.disableDepthTest();
                        RenderSystemHelper.enableBlend();
                        RenderSystemHelper.defaultBlendFunc();
                        BufferBuilder BufferBuilderVarMethod_608272 = Tessellator.getInstance().begin(class_5596.QUADS, VertexFormats.POSITION_COLOR);
                        int iCeil = MathHelper.ceil(16.0F * fMax);
                        this.renderGuiQuad(BufferBuilderVarMethod_608272, i, i2 + (16 - iCeil), 16, iCeil, 255, 255, 255, 127);
                        RenderSystemHelper.enableDepthTest();
                        String iMaxStr = String.valueOf(iMax);
                        DrawContextVar.drawText(
                           TextRendererVar, iMaxStr, (int)(i + 8 - TextRendererVar.getWidth(iMaxStr) / 2.0F), (int)(i2 + 8 - 4.5F), 16777215, true
                        );
                     }
                  }
               }
            }
         }

         EventBusService.EVENT_BUS.post(new ItemOverlayRenderEvent(ItemStackVar, i, i2));
      }
   }

   private void renderGuiQuad(BufferBuilder BufferBuilderVar, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
      BufferBuilderVar.vertex(i + 0, i2 + 0, 0.0F).color(i5, i6, i7, i8);
      BufferBuilderVar.vertex(i + 0, i2 + i4, 0.0F).color(i5, i6, i7, i8);
      BufferBuilderVar.vertex(i + i3, i2 + i4, 0.0F).color(i5, i6, i7, i8);
      BufferBuilderVar.vertex(i + i3, i2 + 0, 0.0F).color(i5, i6, i7, i8);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVar.end());
   }

   private int[] getColorByPriority(int i, int i2) {
      float f = i / i2;
      return new int[]{(int)(255.0F * (1.0F - f)), (int)(255.0F * f), 0};
   }
}
