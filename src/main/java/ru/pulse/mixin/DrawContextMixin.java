package ru.pulse.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.events.EventBusService;
import pulse.events.ItemOverlayRenderEvent;
import pulse.inventory.CooldownInfo;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.AutoReissue;
import pulse.modules.utilities.Cooldowns;
import pulse.modules.utilities.HealingHelper;
import pulse.modules.utilities.ItemHighlighter;
import pulse.modules.utilities.MaceHelper;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
   @Shadow
   @Final
   private MinecraftClient client;

   @Shadow
   public abstract void drawText(TextRenderer var1, @Nullable String var2, int var3, int var4, int var5, boolean var6);

   @Shadow
   public abstract void fill(int var1, int var2, int var3, int var4, int var5);

   @Inject(method = "drawItem", at = @At("HEAD"))
   private void renderItemHighlight(LivingEntity LivingEntityVar, World WorldVar, ItemStack ItemStackVar, int i, int i2, int i3, CallbackInfo callbackInfo) {
      ItemHighlighter itemHighlighter = ModuleRegistry.ITEM_HIGHLIGHTER;
      HealingHelper healingHelper = ModuleRegistry.HEALING_HELPER;
      MaceHelper maceHelper = ModuleRegistry.MACE_HELPER;
      if (maceHelper != null && maceHelper.k() && maceHelper.hotbarHighlight.get() && MaceHelper.isMace(ItemStackVar)) {
         Color colorA = MaceHelper.getChargeColor();
         int rgb = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), 120).getRGB();
         Matrix3x2fStack matrices = ((DrawContext)this).getMatrices();
         matrices.pushMatrix();
         matrices.translate(0.0F, 0.0F);
         this.fill(i, i2, i + 16, i2 + 16, rgb);
         matrices.popMatrix();
      } else if (itemHighlighter != null && itemHighlighter.k() && itemHighlighter.a(ItemStackVar)) {
         Color colorA = itemHighlighter.b(ItemStackVar);
         int rgb = new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), itemHighlighter.n()).getRGB();
         Matrix3x2fStack matrices = ((DrawContext)this).getMatrices();
         matrices.pushMatrix();
         matrices.translate(0.0F, 0.0F);
         this.fill(i, i2, i + 16, i2 + 16, rgb);
         matrices.popMatrix();
      } else if (healingHelper != null && healingHelper.k() && healingHelper.a(ItemStackVar)) {
         Stream<Item> stream = healingHelper.n().stream();
         float fClamp = MathHelper.clamp(healingHelper.a(ItemStackVar.getItem()) / stream.mapToInt(healingHelper::a).max().orElse(1), 0.0F, 1.0F);
         int rgb = new Color(
               (int)(255.0F * (1.0F - fClamp)), (int)(255.0F * fClamp), 0, (int)((Math.sin(System.currentTimeMillis() / 100.0) * 0.5 + 0.5) * 100.0) + 50
            )
            .getRGB();
         Matrix3x2fStack matrices = ((DrawContext)this).getMatrices();
         matrices.pushMatrix();
         matrices.translate(0.0F, 0.0F);
         this.fill(i, i2, i + 16, i2 + 16, rgb);
         matrices.popMatrix();
      }
   }

   @Inject(require = 0, method = "drawStackOverlay", at = @At("HEAD"), cancellable = true)
   private void onDrawStackOverlay(TextRenderer TextRendererVar, ItemStack ItemStackVar, int i, int i2, @Nullable String str, CallbackInfo callbackInfo) {
      if (!ItemStackVar.isEmpty()) {
         Matrix3x2fStack matrices = ((DrawContext)this).getMatrices();
         matrices.pushMatrix();
         if (ItemStackVar.getCount() != 1 || str != null) {
            String strValueOf = str == null ? String.valueOf(ItemStackVar.getCount()) : str;
            matrices.translate(0.0F, 0.0F);
            this.drawText(TextRendererVar, strValueOf, i + 19 - 2 - TextRendererVar.getWidth(strValueOf), i2 + 6 + 3, -1, true);
         }

         if (ItemStackVar.isItemBarVisible()) {
            int iGetItemBarStep = ItemStackVar.getItemBarStep();
            int iGetItemBarColor = ItemStackVar.getItemBarColor();
            int i3 = i + 2;
            int i4 = i2 + 13;
            this.fill(i3, i4, i3 + 13, i4 + 2, -16777216);
            this.fill(i3, i4, i3 + iGetItemBarStep, i4 + 1, ColorHelper.fullAlpha(iGetItemBarColor));
         }

         ClientPlayerEntity ClientPlayerEntityVar = MinecraftClient.getInstance().player;
         Item ItemVarGetItem = ItemStackVar.getItem();
         float fC = 0.0F;
         if (ClientPlayerEntityVar != null) {
            fC = CooldownInfo.a(ClientPlayerEntityVar.getItemCooldownManager(), ItemVarGetItem).c();
         }

         Cooldowns cooldowns = ModuleRegistry.COOLDOWNS;
         boolean zA = Cooldowns.a(ItemStackVar);
         if (cooldowns != null && cooldowns.k() && zA) {
            float fA = cooldowns.a(ItemStackVar.getItem());
            if (fA > fC) {
               fC = fA;
            }
         }

         if (fC == 0.0F && ClientPlayerEntityVar != null) {
            fC = ClientPlayerEntityVar.getItemCooldownManager().getCooldownProgress(ItemStackVar, MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true));
         }

         if (fC > 0.0F) {
            boolean z = true;
            if (ItemStackVar.getItem() instanceof PotionItem) {
               z = zA;
            }

            if (z) {
               GlStateManager._disableDepthTest();
               GlStateManager._enableBlend();
               GlStateManager._blendFuncSeparate(770, 771, 1, 0);
               int iCeil = MathHelper.ceil(16.0F * fC);
               int iFloor = i2 + MathHelper.floor(16.0F * (1.0F - fC));
               this.fill(i, iFloor, i + 16, iFloor + iCeil, new Color(255, 255, 255, 127).getRGB());
               GlStateManager._enableDepthTest();
            }

            float secondsRemaining = 0.0F;
            if (ClientPlayerEntityVar != null) {
               CooldownInfo.ItemCooldownSnapshot snapshot = CooldownInfo.a(ClientPlayerEntityVar.getItemCooldownManager(), ItemVarGetItem);
               if (snapshot != null) {
                  secondsRemaining = snapshot.b();
               }
            }

            if (cooldowns != null && cooldowns.k() && zA && cooldowns.a(ItemVarGetItem) > 0.0F) {
               float customSeconds = cooldowns.getRemainingSeconds(ItemVarGetItem);
               if (customSeconds > secondsRemaining) {
                  secondsRemaining = customSeconds;
               }
            }

            if (secondsRemaining > 0.0F) {
               boolean convert = cooldowns != null ? cooldowns.n() : true;
               String text;
               if (convert) {
                  if (secondsRemaining < 10.0F) {
                     text = String.format(Locale.US, "%.1f", secondsRemaining);
                  } else {
                     text = String.format(Locale.US, "%.0f", secondsRemaining);
                  }
               } else {
                  text = String.valueOf(Math.round(secondsRemaining * 20.0F));
               }

               float fontSize = cooldowns != null && cooldowns.k() ? cooldowns.q().k() : 1.0F;
               matrices.pushMatrix();
               matrices.translate(i + 8.0F, i2 + 8.0F);
               matrices.scale(fontSize, fontSize);
               int textWidth = TextRendererVar.getWidth(text);
               GlStateManager._disableDepthTest();
               ((DrawContext)this).drawText(TextRendererVar, text, -textWidth / 2, -4, new Color(255, 50, 50).getRGB(), true);
               GlStateManager._enableDepthTest();
               matrices.popMatrix();
            }
         }

         if (ItemStackVar.getItem() == Items.CLOCK) {
            AutoReissue autoReissue = ModuleRegistry.AUTO_REISSUE;
            if (ModuleRegistry.AUTO_REISSUE != null && autoReissue.k()) {
               MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
               if (MinecraftClientVarGetInstance.currentScreen != null && MinecraftClientVarGetInstance.player.currentScreenHandler != null) {
                  try {
                     MinecraftClientVarGetInstance.currentScreen.getTitle().getString();
                  } catch (Exception var23) {
                  }

                  if (autoReissue.overlayActive) {
                     long j = autoReissue.timer.a(0L) ? autoReissue.durationMs : 0L;

                     for (int i5 = 1; i5 <= autoReissue.durationMs / 1000 + 1; i5++) {
                        if (!autoReissue.timer.a(i5 * 1000)) {
                           j = (i5 - 1) * 1000;
                           break;
                        }
                     }

                     int iMax = Math.max(0, (autoReissue.durationMs - (int)j) / 1000);
                     float fMax = Math.max(0.0F, 1.0F - (float)(j / autoReissue.durationMs));
                     if (iMax > 0) {
                        int iMethod_153862 = MathHelper.ceil(16.0F * fMax);
                        int i6 = i2 + (16 - iMethod_153862);
                        this.fill(i, i6, i + 16, i6 + iMethod_153862, new Color(255, 255, 255, 127).getRGB());
                        String strValueOf2 = String.valueOf(iMax);
                        matrices.pushMatrix();
                        matrices.translate(0.0F, 0.0F);
                        this.drawText(TextRendererVar, strValueOf2, i + 8 - TextRendererVar.getWidth(strValueOf2) / 2, i2 + 8 - 4, -1, true);
                        matrices.popMatrix();
                     }
                  }
               }
            }
         }

         EventBusService.EVENT_BUS.post(new ItemOverlayRenderEvent(ItemStackVar, i, i2));
         matrices.popMatrix();
         callbackInfo.cancel();
      }
   }
}
