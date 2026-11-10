package pulse.gui.core;

import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.client.MinecraftContext;
import pulse.render.Renderer2D;
import pulse.theme.Theme;

public final class GuiInteractionState {
   private static final GuiInteractionState INSTANCE = new GuiInteractionState();
   private static final int DEFAULT_OVERLAY_ALPHA = 80;
   private static final int STRONG_OVERLAY_ALPHA = 150;
   private final AnimationState overlayAnimation = new AnimationState();
   private boolean overlayVisible;
   private boolean draggingMainPanel;
   private boolean draggingCategoryList;
   private boolean draggingConfigMenu;
   private boolean draggingPopup;
   private boolean strongOverlay;

   private GuiInteractionState() {
      this.overlayAnimation.d(0.0);
   }

   public static GuiInteractionState a() {
      return INSTANCE;
   }

   public void a(boolean z) {
      this.setDraggingMainPanel(z);
   }

   public void b(boolean z) {
      this.draggingCategoryList = z;
      this.updateOverlayAnimation();
   }

   public void c(boolean z) {
      this.draggingConfigMenu = z;
      this.updateOverlayAnimation();
   }

   public void d(boolean z) {
      this.draggingPopup = z;
      this.updateOverlayAnimation();
   }

   public void e(boolean z) {
      this.strongOverlay = z;
      this.updateOverlayAnimation();
   }

   public boolean b() {
      return this.draggingMainPanel || this.draggingCategoryList || this.draggingConfigMenu || this.draggingPopup || this.strongOverlay;
   }

   public boolean c() {
      return this.draggingPopup;
   }

   public boolean d() {
      return this.strongOverlay;
   }

   public boolean e() {
      return this.draggingConfigMenu;
   }

   public void f() {
      this.draggingMainPanel = false;
      this.draggingCategoryList = false;
      this.draggingConfigMenu = false;
      this.draggingPopup = false;
      this.strongOverlay = false;
      this.overlayVisible = false;
      this.overlayAnimation.d(0.0);
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D) {
      this.overlayAnimation.a();
      float fJ = (float)this.overlayAnimation.j();
      if (!(fJ <= 0.01F)) {
         renderer2D.a(
            0.0F,
            0.0F,
            MinecraftContext.getWidth() / 2,
            MinecraftContext.getHeight() / 2,
            Theme.a(Theme.FriendCard, (int)((this.strongOverlay ? 150 : 80) * fJ)),
            MatrixStackVar
         );
      }
   }

   public float g() {
      return (float)this.overlayAnimation.j();
   }

   private void setDraggingMainPanel(boolean z) {
      this.draggingMainPanel = z;
      this.updateOverlayAnimation();
   }

   private void updateOverlayAnimation() {
      boolean zB = this.b();
      if (zB != this.overlayVisible) {
         this.overlayAnimation.a(zB ? 1.0 : 0.0, zB ? 0.2 : 0.15, Easing.h);
         this.overlayVisible = zB;
      }
   }
}
