// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.gui.Click;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.inventory.CooldownInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.utilities.ChestSorter;
import dev.enizer6569.dercovisuals.modules.utilities.Cooldowns;
import dev.enizer6569.dercovisuals.modules.utilities.FastSwap;
import dev.enizer6569.dercovisuals.modules.visuals.ShulkerPreview;
import dev.enizer6569.dercovisuals.modules.visuals.ShulkerPreviewRenderer;
import dev.enizer6569.dercovisuals.util.ElapsedTimer;
import dev.enizer6569.dercovisuals.util.KeyNameFormatter;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
   @Shadow
   protected int x;
   @Shadow
   protected int y;
   @Shadow
   protected int backgroundWidth;
   @Shadow
   @Nullable
   protected Slot focusedSlot;
   @Unique
   private ElapsedTimer scrollTime;

   @Unique
   private ElapsedTimer getScrollTime() {
      if (this.scrollTime == null) {
         this.scrollTime = new ElapsedTimer();
      }

      return this.scrollTime;
   }

   @Inject(require = 0, method = "render", at = @At("TAIL"))
   private void renderItemBindsOverlay(DrawContext DrawContextVar, int i, int i2, float f, CallbackInfo callbackInfo) {
      HandledScreen HandledScreenVar = (HandledScreen)this;
      FastSwap fastSwap = ModuleRegistry.FAST_SWAP;
      boolean z = fastSwap != null && fastSwap.E().k() && fastSwap.k();
      Cooldowns cooldowns = ModuleRegistry.COOLDOWNS;
      TextRenderer TextRendererVar = MinecraftClient.getInstance().textRenderer;

      for (Slot SlotVar : HandledScreenVar.getScreenHandler().slots) {
         ItemStack ItemStackVarGetStack = SlotVar.getStack();
         if (!ItemStackVarGetStack.isEmpty()) {
            int key = this.getKey(ItemStackVarGetStack, fastSwap);
            boolean z2 = cooldowns != null && cooldowns.k() && cooldowns.n() && this.hasAnyCooldown(ItemStackVarGetStack, cooldowns);
            boolean z3 = key != -1 && key != 0;
            if (z2 || z3) {
               int i4 = SlotVar.x + this.x;
               int i5 = SlotVar.y + this.y;
               if (z3 && !z2 && z) {
                  Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
                  MatrixStackVarGetMatrices.pushMatrix();
                  String strA = KeyNameFormatter.a(key);
                  float fMin = Math.min(1.0F, 14.0F / TextRendererVar.getWidth(strA)) * fastSwap.F().k();
                  MatrixStackVarGetMatrices.scale(fMin, fMin);
                  DrawContextVar.drawText(TextRendererVar, strA, (int)((i4 + 0.5F) / fMin), (int)((i5 + 0.5F) / fMin), 16777215, true);
                  MatrixStackVarGetMatrices.popMatrix();
               }

               if (z2) {
                  CooldownInfo.ItemCooldownSnapshot itemCooldownSnapshotA = CooldownInfo.a(
                     MinecraftClient.getInstance().player.getItemCooldownManager(), ItemStackVarGetStack.getItem()
                  );
                  int iCeil;
                  int i3;
                  if (Cooldowns.a(ItemStackVarGetStack) && cooldowns.b(ItemStackVarGetStack.getItem())) {
                     float fA = cooldowns.a(ItemStackVarGetStack.getItem());
                     iCeil = (int)Math.ceil(fA * 18.5);
                     i3 = fA <= 0.33F ? 65280 : (fA <= 0.66F ? 16776960 : 16711680);
                  } else {
                     iCeil = (int)Math.ceil(itemCooldownSnapshotA.b());
                     float fC = itemCooldownSnapshotA.c();
                     i3 = fC <= 0.33F ? 65280 : (fC <= 0.66F ? 16776960 : 16711680);
                  }

                  int i6 = i3;
                  Matrix3x2fStack MatrixStackVarMethod_514482 = DrawContextVar.getMatrices();
                  MatrixStackVarMethod_514482.pushMatrix();
                  String strValueOf = String.valueOf(iCeil);
                  float fMin2 = Math.min(1.0F, 14.0F / TextRendererVar.getWidth(strValueOf)) * cooldowns.q().k();
                  MatrixStackVarMethod_514482.scale(fMin2, fMin2);
                  DrawContextVar.drawText(TextRendererVar, strValueOf, (int)((i4 + 0.5F) / fMin2), (int)((i5 + 0.5F) / fMin2), i6, true);
                  MatrixStackVarMethod_514482.popMatrix();
               }
            }
         }
      }

      this.handleItemScroller();
   }

   @Inject(require = 0, method = "render", at = @At("TAIL"))
   private void renderChestSorterButton(DrawContext DrawContextVar, int i, int i2, float f, CallbackInfo callbackInfo) {
      HandledScreen<?> screen = (HandledScreen<?>)this;
      ChestSorter sorter = ModuleRegistry.CHEST_SORTER;
      if (sorter != null && ChestSorter.isSupportedScreen(screen)) {
         int bx = this.x + this.backgroundWidth - 28;
         int by = this.y - 15;
         int bw = 18;
         int bh = 15;
         boolean hovered = i >= bx && i <= bx + bw && i2 >= by && i2 <= by + bh;
         DrawContextVar.fill(bx - 1, by - 1, bx + bw + 1, by + bh, -13948117);
         int fillColor = hovered ? -1 : -1644826;
         DrawContextVar.fill(bx, by, bx + bw, by + bh, fillColor);
         DrawContextVar.drawItem(new ItemStack(Items.HOPPER), bx + 1, by - 1);
         if (hovered) {
            DrawContextVar.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.literal("Отсортировать сундук"), i, i2);
         }
      }
   }

   @Inject(require = 0, method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void onChestSorterMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
      HandledScreen<?> screen = (HandledScreen<?>)this;
      ChestSorter sorter = ModuleRegistry.CHEST_SORTER;
      if (sorter != null && ChestSorter.isSupportedScreen(screen)) {
         int bx = this.x + this.backgroundWidth - 28;
         int by = this.y - 15;
         int bw = 18;
         int bh = 15;
         if (click.comp_4798() >= bx && click.comp_4798() <= bx + bw && click.comp_4799() >= by && click.comp_4799() <= by + bh && click.button() == 0) {
            ChestSorter.startSorting(screen.getScreenHandler());
            cir.setReturnValue(true);
         }
      }
   }

   @Unique
   private void handleItemScroller() {
      if (ModuleRegistry.ITEM_SCROLLER.k()) {
         MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
         if (MinecraftClientVarGetInstance.player == null
            || MinecraftClientVarGetInstance.currentScreen == null
            || this.focusedSlot == null
            || !this.focusedSlot.hasStack()
            || this.focusedSlot.getStack().getItem() == Items.AIR) {
            return;
         }

         long jGetHandle = MinecraftClientVarGetInstance.getWindow().getHandle();
         boolean z = GLFW.glfwGetMouseButton(jGetHandle, 0) == 1;
         boolean z2 = GLFW.glfwGetKey(jGetHandle, 340) == 1;
         if (z && z2 && this.getScrollTime().a(ModuleRegistry.ITEM_SCROLLER.delay.k().longValue())) {
            MinecraftClientVarGetInstance.interactionManager
               .clickSlot(
                  ((HandledScreen)this).getScreenHandler().syncId, this.focusedSlot.id, 0, SlotActionType.QUICK_MOVE, MinecraftClientVarGetInstance.player
               );
            this.getScrollTime().b();
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

   @Inject(require = 0, method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
   private void pulse$cancelShulkerTooltip(DrawContext DrawContextVar, int i, int i2, CallbackInfo callbackInfo) {
      ShulkerPreview shulkerPreview = ModuleRegistry.SHULKER_PREVIEW;
      if (shulkerPreview != null
         && shulkerPreview.k()
         && shulkerPreview.inventoryPreview.a()
         && this.focusedSlot != null
         && this.focusedSlot.hasStack()
         && ShulkerPreviewRenderer.isShulkerBox(this.focusedSlot.getStack())) {
         callbackInfo.cancel();
         ShulkerPreviewRenderer.renderInventoryHover(this.focusedSlot, DrawContextVar, (int)(this.x - 170.0F - 6.0F), this.y, true);
      }
   }
}
