// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.core;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.gui.config.ConfigsTab;
import dev.enizer6569.dercovisuals.gui.friends.FriendsTab;
import dev.enizer6569.dercovisuals.gui.markers.MarkersTab;
import dev.enizer6569.dercovisuals.gui.modules.ModuleSettingsOverlay;
import dev.enizer6569.dercovisuals.gui.modules.ModulesTab;
import dev.enizer6569.dercovisuals.gui.settings.TokenSettingWidget;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.ScreenPoint;
import dev.enizer6569.dercovisuals.render.ScreenScale;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;
import dev.enizer6569.dercovisuals.DercoVisuals;

public class DercoVisualsClickGuiScreen extends Screen {
   private static final float c = 411.0F;
   private static final float d = 243.5F;
   private static final float e = 9.5F;
   private static final float f = 26.0F;
   private static final double g = 0.35;
   private final ClickGuiFrameOverlay h;
   private final DercoVisualsLogoOverlay i;
   private final DockPanelController j;
   private final ModuleSettingsOverlay k;
   private final Map<ClickGuiTabType, ClickGuiTab> l;
   private ClickGuiTabType m;
   private ClickGuiTabType n;
   private final AnimationState o;
   private boolean p;
   private int q;
   private int r;
   private int s;
   public static int a;
   public static boolean b;
   public int clickBtn;
   private static long lastDebugPrint = 0L;

   public DercoVisualsClickGuiScreen() {
      super(Text.literal("DercoVisuals"));
      String.valueOf(Text.literal("crypt"));
      this.clickBtn = 0;
      this.m = ClickGuiTabType.MODULES;
      this.n = null;
      this.o = new AnimationState();
      this.p = false;
      this.q = 1;
      this.r = 0;
      this.s = 0;
      this.h = new ClickGuiFrameOverlay();
      this.i = new DercoVisualsLogoOverlay();
      this.l = new HashMap<>();
      this.l.put(ClickGuiTabType.MODULES, new ModulesTab());
      this.l.put(ClickGuiTabType.MARKERS, new MarkersTab());
      this.l.put(ClickGuiTabType.FRIENDS, new FriendsTab());
      this.l.put(ClickGuiTabType.CONFIGS, new ConfigsTab());
      this.j = new DockPanelController(this);
      this.k = new ModuleSettingsOverlay(this);
      this.o.d(1.0);
   }

   public void a(ClickGuiTabType clickGuiTabType) {
      if (this.m != clickGuiTabType) {
         ClickGuiTab clickGuiTabA = this.a();
         if (clickGuiTabA instanceof ModulesTab) {
            ((ModulesTab)clickGuiTabA).k();
         }

         if (clickGuiTabA instanceof MarkersTab) {
            ((MarkersTab)clickGuiTabA).e();
         }

         this.n = this.m;
         this.m = clickGuiTabType;
         this.q = this.a(this.n, clickGuiTabType);
         this.p = true;
         this.o.d(0.0);
         this.o.a(1.0, 0.35, Easing.n);
      }
   }

   public void a(ClickGuiTabType clickGuiTabType, int i) {
      if (this.m != clickGuiTabType) {
         ClickGuiTab clickGuiTabA = this.a();
         if (clickGuiTabA instanceof ModulesTab) {
            ((ModulesTab)clickGuiTabA).k();
         }

         if (clickGuiTabA instanceof MarkersTab) {
            ((MarkersTab)clickGuiTabA).e();
         }

         this.n = this.m;
         this.m = clickGuiTabType;
         this.q = i;
         this.p = true;
         this.o.d(0.0);
         this.o.a(1.0, 0.35, Easing.n);
      }
   }

   private int a(ClickGuiTabType clickGuiTabType, ClickGuiTabType clickGuiTabType2) {
      return clickGuiTabType2.ordinal() <= clickGuiTabType.ordinal() ? -1 : 1;
   }

   public ClickGuiTab a() {
      return this.l.get(this.m);
   }

   public ClickGuiTab b() {
      return this.n != null ? this.l.get(this.n) : null;
   }

   private boolean isInsideClickGui(int i, int i2, float f2, float f3) {
      return GuiInput.a(f2, f3, 411.0F, 243.5F, i, i2);
   }

   private boolean isOverOpenSettings(int i, int i2) {
      ClickGuiTab clickGuiTabA = this.a();
      return clickGuiTabA instanceof ModulesTab && ((ModulesTab)clickGuiTabA).hasOpenSettingsAt(i, i2);
   }

   protected void init() {
      super.init();
      HudElementManager.a().a(true);
   }

   public void removed() {
      this.o();
      HudElementManager.a().a(false);
      super.removed();
   }

   public void render(DrawContext DrawContextVar, int i, int i2, float f2) {
      super.render(DrawContextVar, i, i2, f2);
      this.renderBackground(DrawContextVar, i, i2, f2);
      if (DercoVisuals.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(DrawContextVar);
      }

      Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
      float currentScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
      float scaleMultiplier = 2.0F / currentScale;
      MatrixStackVarGetMatrices.pushMatrix();
      MatrixStackVarGetMatrices.scale(scaleMultiplier, scaleMultiplier);
      GuiInput.h();
      ScreenScale.a(2.0);
      ScreenPoint screenPointA = ScreenScale.a(i, i2);
      int iA = screenPointA.a();
      int iB = screenPointA.b();
      this.r = iA;
      this.s = iB;
      GuiLayerRegistry.a().b();
      GuiLayerRegistry.a().a(iA, iB);
      float fGetWidth = MinecraftContext.getWidth() / 4.0F - 205.5F;
      float fGetHeight = MinecraftContext.getHeight() / 4.0F - 121.75F;
      Renderer2D render = DercoVisuals.getInstance().getRender();
      IconTextureRegistry.get("crypt");
      this.i.a(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB);
      this.o.a();
      if (this.p && this.o.d()) {
         this.p = false;
         this.n = null;
      }

      float fJ = (float)this.o.j();
      boolean z = this.j.a() != DockPanelController.Dock.BOTTOM;
      float fMethod_44802 = MinecraftContext.getWidth() / 4.0F + 205.5F + 50.0F;
      float fGetRequiredSpaceCount = MinecraftContext.getHeight() / 4.0F + 121.75F + 50.0F;
      ClickGuiTab clickGuiTabB;
      if (this.p && this.n != null && (clickGuiTabB = this.b()) != null) {
         float f3 = 0.0F;
         float f4 = 0.0F;
         if (z) {
            f4 = fJ * fGetRequiredSpaceCount * ~(this.q - 1);
         } else {
            f3 = fJ * fMethod_44802 * -this.q;
         }

         this.a(MatrixStackVarGetMatrices, render, fGetWidth + f3, fGetHeight + f4, iA, iB, clickGuiTabB);
      }

      ClickGuiTab clickGuiTabA = this.a();
      if (clickGuiTabA != null) {
         if (this.p) {
            float f5 = 0.0F;
            float f6 = 0.0F;
            if (z) {
               f6 = (1.0F - fJ) * fGetRequiredSpaceCount * this.q;
            } else {
               f5 = (1.0F - fJ) * fMethod_44802 * this.q;
            }

            this.a(MatrixStackVarGetMatrices, render, fGetWidth + f5, fGetHeight + f6, iA, iB, clickGuiTabA);
         } else {
            this.a(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB, clickGuiTabA);
         }
      }

      if (this.j.d()) {
         this.k.a(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB);
         if (clickGuiTabA instanceof ModulesTab) {
            ((ModulesTab)clickGuiTabA).a(MatrixStackVarGetMatrices, render, iA, iB);
         }

         GuiInteractionState.a().a(MatrixStackVarGetMatrices, render);
         this.j.b(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB);
         this.j.a(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB);
         this.k.a(MatrixStackVarGetMatrices, render, iA, iB);
      } else {
         this.j.a(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB);
         this.k.a(MatrixStackVarGetMatrices, render, fGetWidth, fGetHeight, iA, iB);
         GuiInteractionState.a().a(MatrixStackVarGetMatrices, render);
         if (clickGuiTabA instanceof ModulesTab) {
            ((ModulesTab)clickGuiTabA).a(MatrixStackVarGetMatrices, render, iA, iB);
         }

         if (clickGuiTabA instanceof MarkersTab) {
            ((MarkersTab)clickGuiTabA).a(MatrixStackVarGetMatrices, render, iA, iB);
         }

         if (clickGuiTabA instanceof ConfigsTab) {
            ((ConfigsTab)clickGuiTabA).a(MatrixStackVarGetMatrices, render);
         }

         this.k.a(MatrixStackVarGetMatrices, render, iA, iB);
      }

      GuiInput.i();
      ScreenScale.a();
      MatrixStackVarGetMatrices.popMatrix();
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2, ClickGuiTab clickGuiTab) {
      this.h.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.h.b(MatrixStackVar, renderer2D, f2, f3, i, i2);
      if (clickGuiTab != null) {
         clickGuiTab.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      }
   }

   public boolean mouseClicked(Click click, boolean bl) {
      double d2 = click.comp_4798();
      double d3 = click.comp_4799();
      int i = click.button();
      if (i != 0 && i != 1) {
         return false;
      } else {
         this.clickBtn = i;
         System.out.println("[DercoVisuals] mouseClicked raw: " + d2 + ", " + d3 + " btn: " + i);
         GuiInput.a();
         ScreenPoint screenPointA = ScreenScale.a(d2, d3);
         int iA = screenPointA.a();
         int iB = screenPointA.b();
         System.out.println("[DercoVisuals] mouseClicked scaled iA: " + iA + ", iB: " + iB);
         float fGetWidth = MinecraftContext.getWidth() / 4.0F - 205.5F;
         float fGetHeight = MinecraftContext.getHeight() / 4.0F - 121.75F;
         System.out.println("[DercoVisuals] mouseClicked bounds: " + fGetWidth + ", " + fGetHeight);
         if (this.j.checkAndHandleClick(fGetWidth, fGetHeight, iA, iB)) {
            return true;
         } else {
            boolean zIsOverOpenSettings = this.isOverOpenSettings(iA, iB);
            boolean zIsInsideClickGui = this.isInsideClickGui(iA, iB, fGetWidth, fGetHeight);
            GuiLayerRegistry.a().a(iA, iB);
            if (!zIsOverOpenSettings && !zIsInsideClickGui) {
               HudElementManager.a().a(d2, d3, i);
               return true;
            } else {
               ClickGuiTab clickGuiTabA = this.a();
               if (clickGuiTabA == null) {
                  return true;
               } else if (!(clickGuiTabA instanceof ModulesTab)) {
                  clickGuiTabA.a(fGetWidth, fGetHeight, iA, iB);
                  return true;
               } else if (i == 1) {
                  clickGuiTabA.b(fGetWidth, fGetHeight, iA, iB);
                  return true;
               } else if (!zIsOverOpenSettings && !(iB >= fGetHeight + 48.0F)) {
                  clickGuiTabA.a(fGetWidth, fGetHeight, iA, iB);
                  return true;
               } else {
                  clickGuiTabA.b(fGetWidth, fGetHeight, iA, iB);
                  return true;
               }
            }
         }
      }
   }

   public boolean mouseReleased(Click click) {
      double d2 = click.comp_4798();
      double d3 = click.comp_4799();
      int i = click.button();
      if (i != 0) {
         return false;
      }

      if (HudElementManager.a().b(d2, d3, i)) {
         return true;
      }

      ScreenPoint screenPointA = ScreenScale.a(d2, d3);
      int iA = screenPointA.a();
      int iB = screenPointA.b();
      float fGetWidth = MinecraftContext.getWidth() / 4.0F - 205.5F;
      float fGetHeight = MinecraftContext.getHeight() / 4.0F - 121.75F;
      GuiLayerRegistry.a().a(iA, iB);
      this.j.c(fGetWidth, fGetHeight, iA, iB);
      ClickGuiTab clickGuiTabA = this.a();
      if (clickGuiTabA != null) {
         clickGuiTabA.c(fGetWidth, fGetHeight, iA, iB);
      }

      return true;
   }

   public boolean mouseDragged(Click click, double d4, double d5) {
      double d2 = click.comp_4798();
      double d3 = click.comp_4799();
      int i = click.button();
      if (i != 0) {
         return false;
      }

      HudElementManager.a().a(d2, d3, d4, d5);
      if (HudElementManager.a().b(d2, d3)) {
         return true;
      }

      ScreenPoint screenPointA = ScreenScale.a(d2, d3);
      int iA = screenPointA.a();
      int iB = screenPointA.b();
      float fGetWidth = MinecraftContext.getWidth() / 4.0F - 205.5F;
      float fGetHeight = MinecraftContext.getHeight() / 4.0F - 121.75F;

      try {
         this.j.a(fGetWidth, fGetHeight, iA, iB, d4, d5);
      } catch (Throwable var18) {
      }

      try {
         ClickGuiTab clickGuiTabA = this.a();
         if (clickGuiTabA != null) {
            clickGuiTabA.a(fGetWidth, fGetHeight, iA, iB, d4, d5);
         }

         return true;
      } catch (Throwable th2) {
         return true;
      }
   }

   public boolean mouseScrolled(double d2, double d3, double d4, double d5) {
      ScreenPoint screenPointA = ScreenScale.a(d2, d3);
      int iA = screenPointA.a();
      int iB = screenPointA.b();
      GuiLayerRegistry.a().a(iA, iB);
      float fGetWidth = MinecraftContext.getWidth() / 4.0F - 205.5F;
      float fGetHeight = MinecraftContext.getHeight() / 4.0F - 121.75F;
      double d6 = d5 != 0.0 ? d5 : d4;
      if (d6 == 0.0) {
         return false;
      }

      boolean z = false;
      if (!this.isOverOpenSettings(iA, iB) && !this.isInsideClickGui(iA, iB, fGetWidth, fGetHeight) && HudElementManager.a().a(d2, d3, d6)) {
         return true;
      }

      try {
         ClickGuiTab clickGuiTabA = this.a();
         if (clickGuiTabA instanceof ModulesTab) {
            ((ModulesTab)clickGuiTabA).a((float)d6, iA, iB);
            z = true;
         } else if (clickGuiTabA != null) {
            clickGuiTabA.a((float)d6);
            z = true;
         }
      } catch (Throwable var19) {
      }

      try {
         if (this.j != null) {
            this.j.a(fGetWidth, fGetHeight, iA, iB, 0.0, -d6 * 20.0);
            z = true;
         }
      } catch (Throwable var18) {
      }

      return z;
   }

   public boolean keyPressed(KeyInput keyInput) {
      return this.keyPressed(keyInput.comp_4795(), keyInput.comp_4796(), keyInput.comp_4797()) ? true : super.keyPressed(keyInput);
   }

   public boolean keyPressed(int i, int i2, int i3) {
      ClickGuiTab clickGuiTabA = this.a();
      if (clickGuiTabA != null) {
         try {
            Object objInvoke = clickGuiTabA.getClass().getMethod("a", int.class, int.class, int.class).invoke(clickGuiTabA, i, i2, i3);
            if (objInvoke instanceof Boolean && (Boolean)objInvoke) {
               return true;
            }
         } catch (Throwable var6) {
         }
      }

      if (i == 256) {
         this.close();
         return true;
      } else {
         return false;
      }
   }

   public boolean charTyped(CharInput charInput) {
      return this.charTyped((char)charInput.comp_4793(), charInput.comp_4794()) ? true : super.charTyped(charInput);
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public boolean charTyped(char c2, int i) {
      ClickGuiTab clickGuiTabA = this.a();
      if (clickGuiTabA instanceof ModulesTab && ((ModulesTab)clickGuiTabA).a(c2, i)) {
         return true;
      } else if (clickGuiTabA instanceof MarkersTab && ((MarkersTab)clickGuiTabA).a(c2, i)) {
         return true;
      } else if (clickGuiTabA instanceof FriendsTab && ((FriendsTab)clickGuiTabA).a(c2, i)) {
         return true;
      } else {
         return clickGuiTabA instanceof ConfigsTab && ((ConfigsTab)clickGuiTabA).a(c2, i);
      }
   }

   private void m() {
      ClickGuiTabType[] clickGuiTabTypeArrValues = ClickGuiTabType.values();
      int iOrdinal = this.m.ordinal();
      this.a(clickGuiTabTypeArrValues[((iOrdinal & -2) + (1 & ~iOrdinal) + 2 * (iOrdinal & 1)) % clickGuiTabTypeArrValues.length], 1);
   }

   private void n() {
      ClickGuiTabType[] clickGuiTabTypeArrValues = ClickGuiTabType.values();
      int iOrdinal = this.m.ordinal() - 2 + 1;
      int length = clickGuiTabTypeArrValues.length;
      this.a(clickGuiTabTypeArrValues[((iOrdinal | length) + (iOrdinal & length)) % clickGuiTabTypeArrValues.length], -1);
   }

   public void c() {
      ClickGuiTab clickGuiTabA = this.a();
      if (clickGuiTabA instanceof ModulesTab modulesTab) {
         modulesTab.m();
         modulesTab.n();
      }

      if (clickGuiTabA instanceof MarkersTab) {
         ((MarkersTab)clickGuiTabA).e();
         ((MarkersTab)clickGuiTabA).f();
      }

      if (this.j.d()) {
         this.j.e();
      }

      GuiInteractionState.a().a(false);
      GuiInteractionState.a().c(false);
      GuiInput.a();
   }

   private void o() {
      ClickGuiTab clickGuiTabA = this.a();
      if (clickGuiTabA instanceof ModulesTab) {
         ((ModulesTab)clickGuiTabA).o();
      }

      if (clickGuiTabA instanceof MarkersTab) {
         ((MarkersTab)clickGuiTabA).e();
         ((MarkersTab)clickGuiTabA).f();
      }

      if (this.j.d()) {
         this.j.e();
      }
   }

   public void close() {
      this.o();
      GuiInteractionState.a().f();
      TokenSettingWidget.o();
      GuiInput.j();
      GuiInput.a();
      super.close();
   }

   public void renderBackground(DrawContext DrawContextVar, int i, int i2, float f2) {
      DrawContextVar.fill(0, 0, DrawContextVar.getScaledWindowWidth(), DrawContextVar.getScaledWindowHeight(), 1879048192);
   }

   public boolean shouldPause() {
      return false;
   }

   public static float d() {
      return 411.0F;
   }

   public static float e() {
      return 243.5F;
   }

   public static float f() {
      return 9.5F;
   }

   public static float g() {
      return 26.0F;
   }

   public FriendsTab h() {
      ClickGuiTab clickGuiTab = this.l.get(ClickGuiTabType.FRIENDS);
      return clickGuiTab instanceof FriendsTab ? (FriendsTab)clickGuiTab : null;
   }

   public ConfigsTab i() {
      ClickGuiTab clickGuiTab = this.l.get(ClickGuiTabType.CONFIGS);
      return clickGuiTab instanceof ConfigsTab ? (ConfigsTab)clickGuiTab : null;
   }

   public DockPanelController j() {
      return this.j;
   }

   @Generated
   public ClickGuiTabType k() {
      return this.m;
   }

   @Generated
   public boolean l() {
      return this.p;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
