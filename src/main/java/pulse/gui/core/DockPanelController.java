package pulse.gui.core;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.audio.SoundPlayer;
import pulse.render.Renderer2D;
import pulse.render.icons.IconTextureRegistry;

public class DockPanelController implements ClickGuiOverlay {
   private static final float CELL_S = 22.0F;
   private static final float ICON_S = 14.0F;
   private static final float PAD_X = 6.0F;
   private static final float PAD_Y = 8.0F;
   private static final float GAP = 8.0F;
   private final PulseClickGuiScreen screen;
   private boolean open;
   private boolean dragging;
   private float dragMouseX;
   private float dragMouseY;
   private float dragOffsetX;
   private float dragOffsetY;
   private DockPanelController.Dock hoveredDockTarget = null;
   private final AnimationState[] hoverAnims;
   private int hoveredTab = -1;
   private DockPanelController.Dock dockPosition = DockPanelController.Dock.RIGHT;

   DockPanelController(PulseClickGuiScreen pulseClickGuiScreen) {
      this.screen = pulseClickGuiScreen;
      ClickGuiTabType[] values = ClickGuiTabType.values();
      this.hoverAnims = new AnimationState[values.length];

      for (int i = 0; i < values.length; i++) {
         this.hoverAnims[i] = new AnimationState();
      }
   }

   public DockPanelController.Dock a() {
      return this.dockPosition;
   }

   public void a(DockPanelController.Dock dock) {
      if (dock != null) {
         this.dockPosition = dock;
      }
   }

   public boolean b() {
      return this.open;
   }

   public boolean c() {
      return this.dragging;
   }

   public boolean d() {
      return this.open || this.dragging;
   }

   public void e() {
      this.open = false;
      this.dragging = false;
   }

   public void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      this.a(MatrixStackVar, renderer2D, f, f2, i, i2);
   }

   private int tabCount() {
      return ClickGuiTabType.values().length;
   }

   private float panelW(DockPanelController.Dock dock) {
      return dock == DockPanelController.Dock.BOTTOM ? 12.0F + 22.0F * this.tabCount() + 8.0F * (this.tabCount() - 1) : 36.0F;
   }

   private float panelH(DockPanelController.Dock dock) {
      return dock == DockPanelController.Dock.BOTTOM ? 36.0F : 16.0F + 22.0F * this.tabCount() + 8.0F * (this.tabCount() - 1);
   }

   private float panelW() {
      DockPanelController.Dock activeOrientation = this.dragging && this.hoveredDockTarget != null ? this.hoveredDockTarget : this.dockPosition;
      return this.panelW(activeOrientation);
   }

   private float panelH() {
      DockPanelController.Dock activeOrientation = this.dragging && this.hoveredDockTarget != null ? this.hoveredDockTarget : this.dockPosition;
      return this.panelH(activeOrientation);
   }

   private float getPanelX(float f, DockPanelController.Dock dock) {
      if (dock == DockPanelController.Dock.BOTTOM) {
         return f + (411.0F - this.panelW(DockPanelController.Dock.BOTTOM)) / 2.0F;
      } else {
         return dock == DockPanelController.Dock.LEFT ? f - this.panelW(DockPanelController.Dock.LEFT) - 10.0F : f + 411.0F + 10.0F;
      }
   }

   private float getPanelY(float f2, DockPanelController.Dock dock) {
      return dock == DockPanelController.Dock.BOTTOM ? f2 + 243.5F + 12.0F : f2 + (243.5F - this.panelH(dock)) / 2.0F;
   }

   private float getPanelX(float f) {
      return this.dragging ? this.dragMouseX - this.dragOffsetX : this.getPanelX(f, this.dockPosition);
   }

   private float getPanelY(float f2) {
      return this.dragging ? this.dragMouseY - this.dragOffsetY : this.getPanelY(f2, this.dockPosition);
   }

   private float getItemX(float panelX, int index, DockPanelController.Dock activeDock) {
      return activeDock == DockPanelController.Dock.BOTTOM ? panelX + 6.0F + index * 30.0F : panelX + 6.0F;
   }

   private float getItemY(float panelY, int index, DockPanelController.Dock activeDock) {
      return activeDock == DockPanelController.Dock.BOTTOM
         ? panelY + (this.panelH(DockPanelController.Dock.BOTTOM) - 22.0F) / 2.0F
         : panelY + 8.0F + index * 30.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      DockPanelController.Dock activeDock = this.dragging && this.hoveredDockTarget != null ? this.hoveredDockTarget : this.dockPosition;
      if (this.dragging) {
         this.hoveredDockTarget = null;
         float minDistance = Float.MAX_VALUE;

         for (DockPanelController.Dock candidate : DockPanelController.Dock.values()) {
            float targetX = this.getPanelX(f, candidate);
            float targetY = this.getPanelY(f2, candidate);
            float targetW = this.panelW(candidate);
            float targetH = this.panelH(candidate);
            float centerX = targetX + targetW / 2.0F;
            float centerY = targetY + targetH / 2.0F;
            float dist = (float)Math.hypot((double)i - centerX, (double)i2 - centerY);
            boolean isNear = dist < 120.0F;
            if (isNear && dist < minDistance) {
               minDistance = dist;
               this.hoveredDockTarget = candidate;
            }

            Color placeholderBorder = isNear && dist == minDistance ? new Color(139, 92, 246, 220) : new Color(50, 48, 65, 180);
            Color placeholderBg = isNear && dist == minDistance ? new Color(139, 92, 246, 70) : new Color(30, 28, 40, 120);
            renderer2D.a(
               targetX - 0.5F,
               targetY - 0.5F,
               targetW + 1.0F,
               targetH + 1.0F,
               12.0F,
               placeholderBorder,
               placeholderBorder,
               placeholderBorder,
               placeholderBorder,
               MatrixStackVar
            );
            renderer2D.a(targetX, targetY, targetW, targetH, 12.0F, placeholderBg, placeholderBg, placeholderBg, placeholderBg, MatrixStackVar);
         }

         if (this.hoveredDockTarget != null) {
            activeDock = this.hoveredDockTarget;
         }
      }

      float pW = this.panelW(activeDock);
      float pH = this.panelH(activeDock);
      float panelX = this.getPanelX(f);
      float panelY = this.getPanelY(f2);
      Color border = new Color(26, 25, 34, 180);
      Color bg = new Color(13, 12, 17, 255);
      renderer2D.a(panelX - 0.5F, panelY - 0.5F, pW + 1.0F, pH + 1.0F, 12.0F, border, border, border, border, MatrixStackVar);
      renderer2D.a(panelX, panelY, pW, pH, 12.0F, bg, bg, bg, bg, MatrixStackVar);
      ClickGuiTabType[] tabs = ClickGuiTabType.values();
      ClickGuiTabType activeTab = this.screen.k();
      this.hoveredTab = -1;
      if (!this.dragging) {
         for (int i3 = 0; i3 < tabs.length; i3++) {
            float itemX = this.getItemX(panelX, i3, activeDock);
            float itemY = this.getItemY(panelY, i3, activeDock);
            if (i >= itemX - 2.0F && i <= itemX + 22.0F + 2.0F && i2 >= itemY - 2.0F && i2 <= itemY + 22.0F + 2.0F) {
               this.hoveredTab = i3;
            }
         }
      }

      for (int i4 = 0; i4 < tabs.length; i4++) {
         ClickGuiTabType tab = tabs[i4];
         float itemX = this.getItemX(panelX, i4, activeDock);
         float itemY = this.getItemY(panelY, i4, activeDock);
         boolean isActive = tab == activeTab;
         this.hoverAnims[i4].a(this.hoveredTab == i4 ? 1.0 : 0.0, 0.12, Easing.n);
         this.hoverAnims[i4].a();
         float hoverProgress = (float)this.hoverAnims[i4].j();
         if (isActive) {
            Color purpleGlow = new Color(139, 92, 246, 240);
            renderer2D.a(itemX, itemY, 22.0F, 22.0F, 8.0F, purpleGlow, purpleGlow, purpleGlow, purpleGlow, MatrixStackVar);
         } else {
            Color tabBorder = new Color(24, 23, 32);
            Color tabBg = new Color(15, 14, 19);
            if (hoverProgress > 0.01F) {
               tabBg = new Color(22, 21, 28);
            }

            renderer2D.a(itemX - 0.5F, itemY - 0.5F, 23.0F, 23.0F, 8.0F, tabBorder, tabBorder, tabBorder, tabBorder, MatrixStackVar);
            renderer2D.a(itemX, itemY, 22.0F, 22.0F, 8.0F, tabBg, tabBg, tabBg, tabBg, MatrixStackVar);
         }

         Color iconColor = isActive ? Color.WHITE : new Color(140, 135, 160, 180 + (int)(hoverProgress * 75.0F));
         Identifier iconTex = IconTextureRegistry.get(tab.a());
         if (iconTex != null) {
            renderer2D.a(iconTex, itemX + 4.0F, itemY + 4.0F, 14.0F, 14.0F, iconColor, MatrixStackVar);
         }
      }
   }

   public boolean checkAndHandleClick(float f, float f2, int i, int i2) {
      DockPanelController.Dock activeDock = this.dockPosition;
      float pW = this.panelW(activeDock);
      float pH = this.panelH(activeDock);
      float panelX = this.getPanelX(f, activeDock);
      float panelY = this.getPanelY(f2, activeDock);
      if (!(i < panelX) && !(i > panelX + pW) && !(i2 < panelY) && !(i2 > panelY + pH)) {
         ClickGuiTabType[] tabs = ClickGuiTabType.values();

         for (int i3 = 0; i3 < tabs.length; i3++) {
            float itemX = this.getItemX(panelX, i3, activeDock);
            float itemY = this.getItemY(panelY, i3, activeDock);
            if (i >= itemX - 2.0F && i <= itemX + 22.0F + 2.0F && i2 >= itemY - 2.0F && i2 <= itemY + 22.0F + 2.0F) {
               this.screen.a(tabs[i3]);
               SoundPlayer.play("click", 0.5F);
               return true;
            }
         }

         this.dragging = true;
         this.dragOffsetX = i - panelX;
         this.dragOffsetY = i2 - panelY;
         this.dragMouseX = i;
         this.dragMouseY = i2;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void a(float f, float f2, int i, int i2) {
      this.checkAndHandleClick(f, f2, i, i2);
   }

   @Override
   public void b(float f, float f2, int i, int i2) {
      if (this.dragging) {
         if (this.hoveredDockTarget != null) {
            this.dockPosition = this.hoveredDockTarget;
         }

         this.dragging = false;
      }
   }

   public void c(float f, float f2, int i, int i2) {
      this.b(f, f2, i, i2);
   }

   public void a(float f, float f2, int i, int i2, double d, double d2) {
      if (this.dragging) {
         this.dragMouseX = i;
         this.dragMouseY = i2;
      }
   }

   public enum Dock {
      BOTTOM,
      LEFT,
      RIGHT;
   }
}
