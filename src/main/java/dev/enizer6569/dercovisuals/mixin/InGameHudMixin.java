// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Arm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.animation.HotbarSelectionAnimation;
import dev.enizer6569.dercovisuals.animation.PlayerListScaleAnimation;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.events.HudRenderPostEvent;
import dev.enizer6569.dercovisuals.events.HudRenderPreEvent;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.hud.elements.ScoreboardHudElement;
import dev.enizer6569.dercovisuals.inventory.CooldownInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.utilities.Cooldowns;
import dev.enizer6569.dercovisuals.modules.utilities.FastSwap;
import dev.enizer6569.dercovisuals.modules.visuals.Animations;
import dev.enizer6569.dercovisuals.modules.visuals.Crosshair;
import dev.enizer6569.dercovisuals.modules.visuals.RenderTweaks;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.ScreenPoint;
import dev.enizer6569.dercovisuals.render.ScreenScale;
import dev.enizer6569.dercovisuals.util.KeyNameFormatter;
import dev.enizer6569.dercovisuals.DercoVisuals;
import dev.enizer6569.dercovisuals.mixin.accessor.PlayerInventoryAccessor;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
   @Shadow
   @Final
   private static Identifier HOTBAR_SELECTION_TEXTURE;
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private PlayerListHud playerListHud;
   @Unique
   private int deferredHighlightY;
   @Unique
   private boolean deferHighlight = false;
   @Unique
   private boolean prevTabShown = false;
   @Unique
   private boolean pulse$scoreboardMatrixPushed = false;

   @Shadow
   private void renderHotbarItem(
      DrawContext DrawContextVar, int i, int i2, RenderTickCounter RenderTickCounterVar, PlayerEntity PlayerEntityVar, ItemStack ItemStackVar, int i3
   ) {
   }

   @Inject(require = 0, method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
   private void onRenderCrosshair(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      Crosshair crosshair = ModuleRegistry.CROSSHAIR;
      if (crosshair != null && crosshair.k()) {
         callbackInfo.cancel();
      }
   }

   @Unique
   private boolean isHotbarAnimationEnabled() {
      return ModuleRegistry.ANIMATIONS != null && ModuleRegistry.ANIMATIONS.k() && ModuleRegistry.ANIMATIONS.g.a();
   }

   @Inject(require = 0, method = "renderHotbar", at = @At("HEAD"))
   private void onRenderHotbarHead(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      if (this.isHotbarAnimationEnabled() && this.client.player != null) {
         HotbarSelectionAnimation hotbarSelectionAnimationP = Animations.p();
         hotbarSelectionAnimationP.a(
            ((PlayerInventoryAccessor)this.client.player.getInventory()).getSelectedSlot(),
            DrawContextVar.getScaledWindowWidth() / 2,
            (long)ModuleRegistry.ANIMATIONS.h.k().floatValue()
         );
         hotbarSelectionAnimationP.a();
      }
   }

   @Redirect(
      require = 0,
      method = "renderHotbar",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/Identifier;IIII)V",
         ordinal = 1
      )
   )
   private void redirectHotbarSelection(DrawContext DrawContextVar, RenderPipeline renderPipeline, Identifier IdentifierVar, int i, int i2, int i3, int i4) {
      if (!this.isHotbarAnimationEnabled()) {
         DrawContextVar.drawGuiTexture(renderPipeline, IdentifierVar, i, i2, i3, i4);
      } else {
         this.deferredHighlightY = i2;
         this.deferHighlight = true;
      }
   }

   @Inject(require = 0, method = "renderHotbar", at = @At("RETURN"))
   private void renderDeferredHighlight(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      if (this.deferHighlight) {
         this.deferHighlight = false;
         int iRound = Math.round(Animations.p().b());
         DrawContextVar.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, iRound, this.deferredHighlightY, 24, 23);
      }
   }

   @Inject(require = 0, method = "renderHotbar", at = @At("RETURN"))
   private void renderArmorHud(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      if (ModuleRegistry.ARMOR_HUD.k() && MinecraftClient.getInstance().player != null) {
         ClientPlayerEntity ClientPlayerEntityVar = this.client.player;
         if (this.client.player != null) {
            int iGetScaledWindowWidth = DrawContextVar.getScaledWindowWidth() / 2;
            int i = ClientPlayerEntityVar.getMainArm().getOpposite() == Arm.RIGHT && !ClientPlayerEntityVar.getOffHandStack().isEmpty() ? 24 : 0;

            for (int i2 = 0; i2 < 4; i2++) {
               this.renderHotbarItem(
                  DrawContextVar,
                  iGetScaledWindowWidth + 92 + 10 + i2 * 20 + 2 + i,
                  DrawContextVar.getScaledWindowHeight() - 16 - 3,
                  RenderTickCounterVar,
                  ClientPlayerEntityVar,
                  ClientPlayerEntityVar.getInventory().getStack(36 + (3 - i2)),
                  i2 + 1
               );
            }

            return;
         }
      }
   }

   @Inject(require = 0, method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
   private void onRenderScoreboardSidebar(DrawContext DrawContextVar, ScoreboardObjective ScoreboardObjectiveVar, CallbackInfo callbackInfo) {
      this.pulse$scoreboardMatrixPushed = false;
      RenderTweaks renderTweaks = ModuleRegistry.RENDER_TWEAKS;
      if (renderTweaks != null && renderTweaks.r()) {
         callbackInfo.cancel();
      } else {
         ScoreboardHudElement sbElement = HudElementManager.a().getScoreboardHud();
         if (sbElement != null) {
            int width = this.client.textRenderer.getWidth(ScoreboardObjectiveVar.getDisplayName());
            Collection<ScoreboardEntry> entries = ScoreboardObjectiveVar.getScoreboard().getScoreboardEntries(ScoreboardObjectiveVar);
            List<ScoreboardEntry> list = entries.stream().filter(entry -> !entry.hidden()).limit(15L).collect(Collectors.toList());

            for (ScoreboardEntry entry : list) {
               int entryWidth = this.client.textRenderer.getWidth(entry.comp_2127()) + 24;
               if (entryWidth > width) {
                  width = entryWidth;
               }
            }

            float vanillaX = DrawContextVar.getScaledWindowWidth() - width - 3;
            float vanillaYBottom = DrawContextVar.getScaledWindowHeight() / 2 + list.size() * 9 / 3;
            float height = list.size() * 9 + 15;
            float vanillaYTop = vanillaYBottom - height;
            sbElement.updateRealSize(width + 10, height);
            sbElement.syncWithVanilla(vanillaX, vanillaYTop);
            float scale = sbElement.getScale();
            float offsetX = sbElement.getUserOffsetX();
            float offsetY = sbElement.getUserOffsetY();
            boolean needsTransform = offsetX != 0.0F || offsetY != 0.0F || scale != 1.0F;
            if (needsTransform) {
               DrawContextVar.getMatrices().pushMatrix();
               this.pulse$scoreboardMatrixPushed = true;
               DrawContextVar.getMatrices().translate(vanillaX + offsetX, vanillaYTop + offsetY);
               DrawContextVar.getMatrices().scale(scale, scale);
               DrawContextVar.getMatrices().translate(-vanillaX, -vanillaYTop);
            }
         }
      }
   }

   @Inject(require = 0, method = "renderScoreboardSidebar", at = @At("TAIL"))
   private void afterRenderScoreboardSidebar(DrawContext DrawContextVar, ScoreboardObjective ScoreboardObjectiveVar, CallbackInfo callbackInfo) {
      if (this.pulse$scoreboardMatrixPushed) {
         DrawContextVar.getMatrices().popMatrix();
         this.pulse$scoreboardMatrixPushed = false;
      }
   }

   @Inject(require = 0, method = "render", at = @At("HEAD"))
   private void onRenderPre(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      ScreenPoint screenPointA = ScreenScale.a((int)GuiInput.e(), (int)GuiInput.f());
      if (DercoVisuals.getInstance() != null && DercoVisuals.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(DrawContextVar);
      }

      EventBusService.EVENT_BUS
         .post(new HudRenderPreEvent(DrawContextVar.getMatrices(), screenPointA.a(), screenPointA.b(), RenderTickCounterVar.getTickProgress(true)));
   }

   @Inject(require = 0, method = "render", at = @At("TAIL"))
   private void onRender(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      ScreenPoint screenPointA = ScreenScale.a((int)GuiInput.e(), (int)GuiInput.f());
      ScreenScale.a(2.0);
      if (DercoVisuals.getInstance() != null && DercoVisuals.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(DrawContextVar);
      }

      EventBusService.EVENT_BUS
         .post(new HudRenderPostEvent(DrawContextVar.getMatrices(), screenPointA.a(), screenPointA.b(), RenderTickCounterVar.getTickProgress(true)));
      ScreenScale.a();
      HudElementManager.a().a(DrawContextVar.getMatrices());
      HudElementManager.a().b(DrawContextVar.getMatrices());
   }

   @Inject(require = 0, method = "renderPlayerList", at = @At("HEAD"), cancellable = true)
   private void onRenderPlayerList(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      if (ModuleRegistry.ANIMATIONS != null && ModuleRegistry.ANIMATIONS.k() && ModuleRegistry.ANIMATIONS.a.a()) {
         callbackInfo.cancel();
         if (this.client.world != null && this.client.player != null) {
            Scoreboard ScoreboardVarGetScoreboard = this.client.world.getScoreboard();
            ScoreboardObjective ScoreboardObjectiveVarGetObjectiveForSlot = ScoreboardVarGetScoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
            boolean z = this.client.options.playerListKey.isPressed()
               && (
                  !this.client.isInSingleplayer()
                     || this.client.player.networkHandler.getListedPlayerListEntries().size() > 1
                     || ScoreboardObjectiveVarGetObjectiveForSlot != null
               );
            PlayerListScaleAnimation playerListScaleAnimationN = Animations.n();
            long jFloatValue = (long)ModuleRegistry.ANIMATIONS.b.k().floatValue();
            if (z && !this.prevTabShown) {
               playerListScaleAnimationN.a(jFloatValue);
            } else if (!z && this.prevTabShown) {
               playerListScaleAnimationN.b(jFloatValue);
            }

            this.prevTabShown = z;
            playerListScaleAnimationN.a();
            if (!z && !playerListScaleAnimationN.d()) {
               this.playerListHud.setVisible(false);
            } else {
               this.playerListHud.setVisible(true);
               float fB = playerListScaleAnimationN.b();
               if (fB < 0.001F) {
                  this.playerListHud.setVisible(false);
               } else {
                  Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
                  MatrixStackVarGetMatrices.pushMatrix();
                  float fGetScaledWindowWidth = DrawContextVar.getScaledWindowWidth() / 2.0F;
                  MatrixStackVarGetMatrices.translate(fGetScaledWindowWidth, 0.0F);
                  MatrixStackVarGetMatrices.scale(fB, fB);
                  MatrixStackVarGetMatrices.translate(-fGetScaledWindowWidth, 0.0F);
                  this.playerListHud
                     .render(DrawContextVar, DrawContextVar.getScaledWindowWidth(), ScoreboardVarGetScoreboard, ScoreboardObjectiveVarGetObjectiveForSlot);
                  MatrixStackVarGetMatrices.popMatrix();
               }
            }
         }
      } else {
         this.prevTabShown = false;
      }
   }

   @Inject(require = 0, method = "renderHotbar", at = @At("TAIL"))
   private void renderHotbarItemBinds(DrawContext DrawContextVar, RenderTickCounter RenderTickCounterVar, CallbackInfo callbackInfo) {
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      ClientPlayerEntity ClientPlayerEntityVar = MinecraftClientVarGetInstance.player;
      if (ClientPlayerEntityVar != null) {
         FastSwap fastSwap = ModuleRegistry.FAST_SWAP;
         boolean z = fastSwap != null && fastSwap.E().k() && fastSwap.k();
         Cooldowns cooldowns = ModuleRegistry.COOLDOWNS;
         boolean z2 = cooldowns != null && cooldowns.k() && cooldowns.n();
         TextRenderer TextRendererVar = MinecraftClientVarGetInstance.textRenderer;
         int iGetScaledWindowWidth = DrawContextVar.getScaledWindowWidth();
         int iGetScaledWindowHeight = DrawContextVar.getScaledWindowHeight();

         for (int i2 = 0; i2 < 9; i2++) {
            ItemStack ItemStackVarGetStack = ClientPlayerEntityVar.getInventory().getStack(i2);
            if (!ItemStackVarGetStack.isEmpty()) {
               int key = this.getKey(ItemStackVarGetStack, fastSwap);
               boolean z3 = z2 && this.hasAnyCooldown(ItemStackVarGetStack, cooldowns);
               boolean z4 = key != -1 && key != 0;
               if (z3 || z4) {
                  int i3 = iGetScaledWindowWidth / 2 - 91 + i2 * 20 + 2;
                  int i4 = iGetScaledWindowHeight - 16 - 3;
                  if (z4 && !z3 && z) {
                     String strA = KeyNameFormatter.a(key);
                     float fMin = Math.min(1.0F, 14.0F / TextRendererVar.getWidth(strA)) * fastSwap.F().k();
                     Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
                     MatrixStackVarGetMatrices.pushMatrix();
                     MatrixStackVarGetMatrices.scale(fMin, fMin);
                     DrawContextVar.drawText(TextRendererVar, strA, (int)((i3 + 2.0F) / fMin), (int)((i4 + 1.0F) / fMin), 16777215, true);
                     MatrixStackVarGetMatrices.popMatrix();
                  }

                  if (z3) {
                     CooldownInfo.ItemCooldownSnapshot itemCooldownSnapshotA = CooldownInfo.a(
                        ClientPlayerEntityVar.getItemCooldownManager(), ItemStackVarGetStack.getItem()
                     );
                     int iCeil;
                     int i;
                     if (Cooldowns.a(ItemStackVarGetStack) && cooldowns.b(ItemStackVarGetStack.getItem())) {
                        float fA = cooldowns.a(ItemStackVarGetStack.getItem());
                        iCeil = (int)Math.ceil(fA * 18.5);
                        i = fA <= 0.33F ? 65280 : (fA <= 0.66F ? 16776960 : 16711680);
                     } else {
                        iCeil = (int)Math.ceil(itemCooldownSnapshotA.b());
                        float fC = itemCooldownSnapshotA.c();
                        i = fC <= 0.33F ? 65280 : (fC <= 0.66F ? 16776960 : 16711680);
                     }

                     String strValueOf = String.valueOf(iCeil);
                     float fMin2 = Math.min(1.0F, 14.0F / TextRendererVar.getWidth(strValueOf)) * cooldowns.q().k();
                     Matrix3x2fStack MatrixStackVarMethod_514482 = DrawContextVar.getMatrices();
                     MatrixStackVarMethod_514482.pushMatrix();
                     MatrixStackVarMethod_514482.scale(fMin2, fMin2);
                     DrawContextVar.drawText(TextRendererVar, strValueOf, (int)((i3 + 2.0F) / fMin2), (int)((i4 + 1.0F) / fMin2), i, true);
                     MatrixStackVarMethod_514482.popMatrix();
                  }
               }
            }
         }
      }
   }

   @Unique
   private boolean hasAnyCooldown(ItemStack ItemStackVar, Cooldowns cooldowns) {
      return CooldownInfo.a(MinecraftClient.getInstance().player.getItemCooldownManager(), ItemStackVar.getItem()).a()
         || Cooldowns.a(ItemStackVar) && cooldowns.b(ItemStackVar.getItem());
   }

   @Unique
   private int getKey(ItemStack ItemStackVar, FastSwap fastSwap) {
      if (fastSwap == null) {
         return -1;
      } else {
         Item ItemVarGetItem = ItemStackVar.getItem();
         if (ItemVarGetItem == Items.NETHERITE_SCRAP) {
            return this.getKeyOrDefault(fastSwap.q().a());
         } else if (ItemVarGetItem == Items.DRIED_KELP) {
            return this.getKeyOrDefault(fastSwap.r().a());
         } else if (ItemVarGetItem == Items.ENDER_EYE) {
            int iA = fastSwap.s().a();
            return iA > 0 ? iA : this.getKeyOrDefault(fastSwap.y().a());
         } else if (ItemVarGetItem == Items.SUGAR) {
            return this.getKeyOrDefault(fastSwap.t().a());
         } else if (ItemVarGetItem == Items.CHORUS_FRUIT) {
            return this.getKeyOrDefault(fastSwap.n().a());
         } else if (ItemVarGetItem == Items.ENDER_PEARL) {
            return this.getKeyOrDefault(fastSwap.o().a());
         } else if (ItemVarGetItem == Items.POTION && fastSwap.a(ItemStackVar)) {
            return this.getKeyOrDefault(fastSwap.p().a());
         } else if (ItemVarGetItem == Items.NETHER_STAR) {
            return this.getKeyOrDefault(fastSwap.u().a());
         } else if (ItemVarGetItem == Items.SLIME_BALL) {
            return this.getKeyOrDefault(fastSwap.v().a());
         } else if (ItemVarGetItem == Items.TURTLE_SCUTE) {
            return this.getKeyOrDefault(fastSwap.w().a());
         } else if (ItemVarGetItem == Items.COBWEB) {
            return this.getKeyOrDefault(fastSwap.x().a());
         } else {
            return ItemVarGetItem == Items.FIREWORK_STAR ? this.getKeyOrDefault(fastSwap.z().a()) : -1;
         }
      }
   }

   @Unique
   private int getKeyOrDefault(int i) {
      return i <= 0 ? -1 : i;
   }
}
