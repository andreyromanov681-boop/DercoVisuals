package pulse.modules.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.DataComponentTypes;
import org.joml.Matrix3x2fStack;
import pulse.module.ModuleRegistry;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.font.FontManager;
import ru.pulse.Pulse;

public final class ShulkerPreviewRenderer {
   private static final float SLOT = 18.0F;
   private static final float PADDING = 6.0F;
   private static final int COLS = 9;
   private static final int ROWS = 3;

   private ShulkerPreviewRenderer() {
   }

   public static void renderInventoryHover(Slot SlotVar, DrawContext DrawContextVar, int i, int i2) {
      renderInventoryHover(SlotVar, DrawContextVar, i, i2, false);
   }

   public static void renderInventoryHover(Slot SlotVar, DrawContext DrawContextVar, int i, int i2, boolean z) {
      ShulkerPreview shulkerPreview = ModuleRegistry.SHULKER_PREVIEW;
      if (shulkerPreview != null && shulkerPreview.k() && shulkerPreview.inventoryPreview.a() && SlotVar != null) {
         ItemStack ItemStackVarGetStack = SlotVar.getStack();
         if (isShulkerBox(ItemStackVarGetStack)) {
            List<ItemStack> contents = readContents(ItemStackVarGetStack);
            float f = 174.0F;
            float f2 = 82.0F;
            float f3 = i;
            float f4 = i2;
            if (!z) {
               f3 = i + 14;
               f4 = i2 + 6;
               MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
               if (f3 + f > MinecraftClientVarGetInstance.getWindow().getScaledWidth()) {
                  f3 = i - f - 4.0F;
               }

               if (f4 + f2 > MinecraftClientVarGetInstance.getWindow().getScaledHeight()) {
                  f4 = i2 - f2 - 4.0F;
               }
            }

            renderPanel(DrawContextVar, f3, f4, f, f2, contents, ItemStackVarGetStack, 1.0F);
         }
      }
   }

   public static void renderWorldTarget(MinecraftClient MinecraftClientVar, Object MatrixStackVar, Renderer2D renderer2D) {
      ShulkerPreview shulkerPreview = ModuleRegistry.SHULKER_PREVIEW;
      if (shulkerPreview != null && shulkerPreview.k() && shulkerPreview.worldPreview.a() && MinecraftClientVar.player != null) {
         if (MinecraftClientVar.crosshairTarget instanceof EntityHitResult EntityHitResultVar && EntityHitResultVar.getEntity() instanceof ItemEntity ItemEntityVar) {
            ItemStack ItemStackVarGetStack = ItemEntityVar.getStack();
            if (isShulkerBox(ItemStackVarGetStack) && MinecraftClientVar.player.squaredDistanceTo(ItemEntityVar) <= 36.0) {
               float f = 174.0F;
               renderPanel(
                  Renderer2DImpl.currentDrawContext != null
                     ? Renderer2DImpl.currentDrawContext
                     : new DrawContext(
                        MinecraftClientVar, new GuiRenderState(), MinecraftClientVar.getWindow().getScaledWidth(), MinecraftClientVar.getWindow().getScaledHeight()
                     ),
                  MinecraftClientVar.getWindow().getScaledWidth() / 2.0F - f / 2.0F,
                  MinecraftClientVar.getWindow().getScaledHeight() / 2.0F + 20.0F,
                  f,
                  82.0F,
                  readContents(ItemStackVarGetStack),
                  ItemStackVarGetStack,
                  1.0F
               );
            }
         }
      }
   }

   private static void renderPanel(DrawContext DrawContextVar, float f, float f2, float f3, float f4, List<ItemStack> list, ItemStack ItemStackVar, float f5) {
      Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
      Renderer2D render = Pulse.getInstance().getRender();
      if (render != null) {
         render.a(f, f2, f3, f4, 7.0F * f5, new Color(12, 12, 22, 220), MatrixStackVarGetMatrices);
         FontManager.a[Math.max(6, Math.min(14, Math.round(10.0F * f5)))]
            .a(
               ItemStackVar.get(DataComponentTypes.CUSTOM_NAME) != null ? ItemStackVar.getName().getString() : "Shulker Box",
               f + 6.0F * f5,
               f2 + 4.0F * f5,
               Color.WHITE,
               MatrixStackVarGetMatrices
            );
         float f6 = f + 6.0F * f5;
         float f7 = f2 + 16.0F * f5;
         float f8 = 18.0F * f5;

         for (int i = 0; i < 27; i++) {
            float f9 = f6 + i % 9 * f8;
            float f10 = f7 + i / 9 * f8;
            render.a(f9, f10, f8 - 1.0F, f8 - 1.0F, 3.0F * f5, new Color(24, 24, 36, 180), MatrixStackVarGetMatrices);
            if (i < list.size() && !list.get(i).isEmpty()) {
               drawItem(MatrixStackVarGetMatrices, DrawContextVar, list.get(i), f9 + 1.0F * f5, f10 + 1.0F * f5, f8 - 2.0F * f5);
            }
         }
      }
   }

   private static void drawItem(Matrix3x2fStack MatrixStackVar, DrawContext DrawContextVar, ItemStack ItemStackVar, float f, float f2, float f3) {
      if (!ItemStackVar.isEmpty()) {
         Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
         float f4 = f3 / 16.0F;
         MatrixStackVarGetMatrices.pushMatrix();
         MatrixStackVarGetMatrices.translate(f, f2);
         MatrixStackVarGetMatrices.scale(f4, f4);
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         DrawContextVar.drawItem(ItemStackVar, 0, 0);
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystemHelper.disableBlend();
         if (ItemStackVar.getCount() > 1) {
            TextRenderer TextRendererVar = MinecraftClient.getInstance().textRenderer;
            String strValueOf = String.valueOf(ItemStackVar.getCount());
            DrawContextVar.drawTextWithShadow(TextRendererVar, strValueOf, 17 - TextRendererVar.getWidth(strValueOf), 9, -1);
         }

         MatrixStackVarGetMatrices.popMatrix();
      }
   }

   public static boolean isShulkerBox(ItemStack ItemStackVar) {
      if (!ItemStackVar.isEmpty()) {
         Item BlockItemVarGetItem = ItemStackVar.getItem();
         if (BlockItemVarGetItem instanceof BlockItem && ((BlockItem)BlockItemVarGetItem).getBlock() instanceof ShulkerBoxBlock) {
            return true;
         }
      }

      return false;
   }

   private static List<ItemStack> readContents(ItemStack ItemStackVar) {
      ArrayList arrayList = new ArrayList();
      ContainerComponent ContainerComponentVar = (ContainerComponent)ItemStackVar.get(DataComponentTypes.CONTAINER);
      if (ContainerComponentVar == null) {
         return arrayList;
      }

      for (int i = 0; i < 27; i++) {
         arrayList.add(ItemStack.EMPTY);
      }

      int i2 = 0;

      for (ItemStack ItemStackVar2 : ContainerComponentVar.iterateNonEmpty()) {
         if (i2 < 27) {
            int i3 = i2++;
            arrayList.set(i3, ItemStackVar2.copy());
         }
      }

      return arrayList;
   }
}
