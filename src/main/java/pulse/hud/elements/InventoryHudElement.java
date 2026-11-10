package pulse.hud.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.hud.core.HudElement;
import pulse.module.ModuleRegistry;
import pulse.modules.hud.InventoryHud;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;

public class InventoryHudElement extends HudElement {
   private static final int COLS = 9;
   private static final int ROWS = 3;
   private static final float SLOT = 24.0F;
   private static final float PADDING = 5.0F;
   private static final double ANIM_SPEED = 0.2;
   private static final Color OVERALL_BG_COLOR = new Color(25, 27, 32, 110);
   private static final Color OVERALL_BORDER_COLOR = new Color(40, 45, 55, 90);
   private static final Color SLOT_BG_COLOR = new Color(5, 5, 5, 91);
   private static final Color SLOT_BORDER_COLOR = new Color(40, 45, 55, 80);
   private final AnimationState animWidth = new AnimationState();
   private final AnimationState animHeight = new AnimationState();
   private final AnimationState animVisibility = new AnimationState();
   private boolean settingsBound;
   private boolean boundsInitialized;
   private final List<ItemStack> placeholderItems = new ArrayList<>();

   public InventoryHudElement(float f, float f2) {
      super(f, f2);
      this.animVisibility.d(1.0);
      this.initPlaceholderItems();
      this.a();
   }

   private void initPlaceholderItems() {
      this.placeholderItems.clear();
      this.placeholderItems.add(new ItemStack(Items.GUNPOWDER, 9));
      this.placeholderItems.add(new ItemStack(Items.SAND, 20));
      this.placeholderItems.add(new ItemStack(Items.COOKED_MUTTON, 2));
      this.placeholderItems.add(new ItemStack(Items.INK_SAC, 1));

      for (int i = 4; i < 27; i++) {
         this.placeholderItems.add(ItemStack.EMPTY);
      }
   }

   private boolean isChatOrEmpty() {
      return this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof PulseClickGuiScreen;
   }

   private boolean isInventoryEmpty() {
      if (this.a.player == null) {
         return true;
      }

      for (int i = 9; i < 36; i++) {
         if (!this.a.player.getInventory().getStack(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.INVENTORY_HUD != null) {
         this.f().a(ModuleRegistry.INVENTORY_HUD);
         this.settingsBound = true;
      }
   }

   private float getScale() {
      InventoryHud hud = ModuleRegistry.INVENTORY_HUD;
      return hud != null ? hud.getScale().get() : this.g();
   }

   private boolean showOverallBg() {
      InventoryHud hud = ModuleRegistry.INVENTORY_HUD;
      return hud == null || hud.getOverallBackground().get();
   }

   private boolean showSlotBg() {
      InventoryHud hud = ModuleRegistry.INVENTORY_HUD;
      return hud != null && hud.getSlotBackground().get();
   }

   @Override
   protected void a() {
      this.bindSettings();
      float fScale = this.getScale();
      float targetW = 226.0F * fScale;
      float targetH = 82.0F * fScale;
      if (this.boundsInitialized) {
         if (Math.abs(this.animWidth.i() - targetW) > 0.5) {
            this.animWidth.a(targetW, 0.2, Easing.h);
         }

         if (Math.abs(this.animHeight.i() - targetH) > 0.5) {
            this.animHeight.a(targetH, 0.2, Easing.h);
         }
      } else {
         this.animWidth.d(targetW);
         this.animHeight.d(targetH);
         this.boundsInitialized = true;
      }

      this.animWidth.a();
      this.animHeight.a();
      this.d = (float)this.animWidth.j();
      this.e = (float)this.animHeight.j();
      this.animVisibility.a();
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      if (this.a.player != null) {
         this.a();
         float fAlpha = (float)this.animVisibility.j();
         if (!(fAlpha < 0.01F)) {
            float fScale = this.getScale();
            boolean placeholder = this.isChatOrEmpty();
            boolean drawOverall = this.showOverallBg();
            boolean drawSlots = this.showSlotBg();
            if (drawOverall) {
               Color cBg = withAlpha(OVERALL_BG_COLOR, fAlpha);
               Color cBorder = withAlpha(OVERALL_BORDER_COLOR, fAlpha);
               renderer2D.a(this.b, this.c, this.d, this.e, 4.0F * fScale, cBg, MatrixStackVar);
            }

            float startX = this.b + 5.0F * fScale;
            float startY = this.c + 5.0F * fScale;
            float slotW = 24.0F * fScale;
            DrawContext drawContext = Renderer2DImpl.currentDrawContext != null
               ? Renderer2DImpl.currentDrawContext
               : new DrawContext(this.a, new GuiRenderState(), this.a.getWindow().getScaledWidth(), this.a.getWindow().getScaledHeight());
            Color cSlotBg = withAlpha(SLOT_BG_COLOR, fAlpha);

            for (int row = 0; row < 3; row++) {
               for (int col = 0; col < 9; col++) {
                  int index = row * 9 + col;
                  float slotX = startX + col * slotW;
                  float slotY = startY + row * slotW;
                  if (drawSlots) {
                     renderer2D.a(slotX, slotY, slotW, slotW, 0.0F, cSlotBg, MatrixStackVar);
                  }

                  ItemStack stack = this.a.player.getInventory().getStack(9 + index);
                  if (!stack.isEmpty()) {
                     this.drawSlotItem(MatrixStackVar, drawContext, stack, slotX + 1.0F * fScale, slotY + 1.0F * fScale, slotW - 2.0F * fScale, fAlpha);
                  }
               }
            }

            if (drawSlots) {
               Color dividerColor = withAlpha(SLOT_BORDER_COLOR, fAlpha);

               for (int row = 1; row < 3; row++) {
                  float lineY = startY + row * slotW;
                  renderer2D.a(startX, lineY, 9.0F * slotW, 1.0F, 0.0F, dividerColor, MatrixStackVar);
               }

               for (int col = 1; col < 9; col++) {
                  float lineX = startX + col * slotW;
                  renderer2D.a(lineX, startY, 1.0F, 3.0F * slotW, 0.0F, dividerColor, MatrixStackVar);
               }
            }
         }
      }
   }

   private void drawSlotItem(Matrix3x2fStack MatrixStackVar, DrawContext drawContext, ItemStack stack, float x, float y, float size, float alpha) {
      if (!stack.isEmpty() && !(alpha < 0.01F)) {
         Matrix3x2fStack matrices = drawContext.getMatrices();
         matrices.pushMatrix();
         matrices.translate(x, y);
         float itemScale = size / 16.0F;
         matrices.scale(itemScale, itemScale);
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
         drawContext.drawItem(stack, 0, 0);
         if (stack.getCount() > 1) {
            TextRenderer tr = this.a.textRenderer;
            String countStr = String.valueOf(stack.getCount());
            drawContext.drawTextWithShadow(tr, countStr, 17 - tr.getWidth(countStr), 9, -1);
         }

         if (stack.isDamageable()) {
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamage();
            float healthRatio = Math.max(0.0F, Math.min(1.0F, (float)(maxDamage - damage) / maxDamage));
            int barWidth = Math.round(13.0F * healthRatio);
            int colorRGB = this.getDurabilityColor(healthRatio);
            drawContext.fill(2, 13, 15, 15, -16777216);
            drawContext.fill(2, 13, 2 + barWidth, 14, colorRGB | 0xFF000000);
         }

         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystemHelper.disableBlend();
         matrices.popMatrix();
      }
   }

   private int getDurabilityColor(float ratio) {
      if (ratio > 0.5F) {
         float f = (ratio - 0.5F) * 2.0F;
         int r = (int)(255.0F * (1.0F - f));
         return r << 16 | 0xFF00;
      } else {
         float f = ratio * 2.0F;
         int g = (int)(255.0F * f);
         return 0xFF0000 | g << 8;
      }
   }

   private static Color withAlpha(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f))));
   }
}
