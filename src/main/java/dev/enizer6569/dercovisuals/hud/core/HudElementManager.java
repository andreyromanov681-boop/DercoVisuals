// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.hud.elements.BossbarHudElement;
import dev.enizer6569.dercovisuals.hud.elements.CooldownsHudElement;
import dev.enizer6569.dercovisuals.hud.elements.HotkeysHudElement;
import dev.enizer6569.dercovisuals.hud.elements.InventoryHudElement;
import dev.enizer6569.dercovisuals.hud.elements.PotionsHudElement;
import dev.enizer6569.dercovisuals.hud.elements.SaturationHudElement;
import dev.enizer6569.dercovisuals.hud.elements.ScoreboardHudElement;
import dev.enizer6569.dercovisuals.hud.elements.TargetHudElement;
import dev.enizer6569.dercovisuals.hud.elements.TotemsHudElement;
import dev.enizer6569.dercovisuals.hud.notifications.HudNotificationCenter;
import dev.enizer6569.dercovisuals.hud.snap.HudSnapCalculator;
import dev.enizer6569.dercovisuals.hud.snap.HudSnapGuide;
import dev.enizer6569.dercovisuals.hud.snap.HudSnapResult;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.ScreenScale;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.DercoVisuals;

public class HudElementManager {
   private static final int r = 50;
   private static final Color m = new Color(100, 180, 255, 180);
   private static final Color n = new Color(200, 100, 255, 180);
   private static final Color o = new Color(255, 150, 80, 180);
   private final MinecraftClient b = MinecraftClient.getInstance();
   private final List<HudElement> c = new ArrayList<>();
   private boolean i = false;
   private HudElement j = null;
   private int k = 0;
   private List<HudSnapGuide> l = new ArrayList<>();
   private final Deque<HudElementPositionSnapshot> p = new LinkedList<>();
   private final Deque<HudElementPositionSnapshot> q = new LinkedList<>();
   private float s = -1.0F;
   private float t = -1.0F;
   private boolean u = false;
   private HudNotificationCenter d = new HudNotificationCenter();
   private PotionsHudElement e;
   private HotkeysHudElement f;
   private CooldownsHudElement g;
   private TargetHudElement h;
   private SaturationHudElement saturationHud;
   private InventoryHudElement inventoryHud;
   private ScoreboardHudElement scoreboardHud;
   private BossbarHudElement bossbarHud;
   private TotemsHudElement totemsHud;

   private HudElementManager() {
      float width = this.b.getWindow().getScaledWidth();
      float height = this.b.getWindow().getScaledHeight();
      this.e = new PotionsHudElement(10.0F, 10.0F);
      this.f = new HotkeysHudElement(10.0F, 110.0F);
      this.g = new CooldownsHudElement(Math.max(10.0F, width - 130.0F), 10.0F);
      this.h = new TargetHudElement(Math.max(10.0F, (width - 130.0F) / 2.0F), Math.max(10.0F, height - 61.0F));
      this.saturationHud = new SaturationHudElement(width / 2.0F + 10.0F, Math.max(10.0F, height - 49.0F));
      this.inventoryHud = new InventoryHudElement(Math.max(10.0F, (width - 226.0F) / 2.0F), Math.max(10.0F, height - 145.0F));
      this.scoreboardHud = new ScoreboardHudElement(Math.max(10.0F, width - 130.0F), Math.max(10.0F, (height - 80.0F) / 2.0F));
      this.bossbarHud = new BossbarHudElement(Math.max(10.0F, (width - 182.0F) / 2.0F), 12.0F);
      this.totemsHud = new TotemsHudElement(width / 2.0F + 10.0F, Math.max(10.0F, height - 59.0F));
      this.c(this.e);
      this.c(this.f);
      this.c(this.g);
      this.c(this.h);
      this.c(this.saturationHud);
      this.c(this.inventoryHud);
      this.c(this.scoreboardHud);
      this.c(this.bossbarHud);
      this.c(this.totemsHud);
   }

   public static HudElementManager a() {
      return HudElementManager.Holder.INSTANCE;
   }

   public ScoreboardHudElement getScoreboardHud() {
      return this.scoreboardHud;
   }

   public BossbarHudElement getBossbarHud() {
      return this.bossbarHud;
   }

   public void b() {
   }

   public void c() {
   }

   private void c(HudElement hudElement) {
      if (hudElement != null && !this.c.contains(hudElement)) {
         this.c.add(hudElement);
         this.r();
      }
   }

   private void d(HudElement hudElement) {
      this.c.remove(hudElement);
   }

   private void r() {
      this.c.sort(Comparator.comparingInt(v0 -> v0.p()));
   }

   private List<HudElement> s() {
      ArrayList<HudElement> arrayList = new ArrayList<>(this.c);
      arrayList.sort((hudElement, hudElement2) -> Integer.compare(hudElement2.p(), hudElement.p()));
      return arrayList;
   }

   private boolean e(HudElement hudElement) {
      if (this.i) {
         return true;
      } else {
         return hudElement == this.e
            ? ModuleRegistry.POTIONS_HUD.k()
            : (
               hudElement == this.f
                  ? ModuleRegistry.HOTKEYS_HUD.k()
                  : (
                     hudElement == this.g
                        ? ModuleRegistry.COOLDOWNS_HUD.k()
                        : (
                           hudElement == this.h
                              ? ModuleRegistry.TARGET_HUD.k()
                              : (
                                 hudElement == this.saturationHud
                                    ? ModuleRegistry.SATURATION_HUD.k()
                                    : (
                                       hudElement == this.inventoryHud
                                          ? ModuleRegistry.INVENTORY_HUD.k()
                                          : (hudElement == this.scoreboardHud ? true : (hudElement == this.bossbarHud ? true : hudElement.c()))
                                    )
                              )
                        )
                  )
            );
      }
   }

   private List<HudElement> t() {
      return this.c.stream().filter(this::e).collect(Collectors.toList());
   }

   private float screenWidth() {
      return this.b.getWindow().getScaledWidth();
   }

   private float screenHeight() {
      return this.b.getWindow().getScaledHeight();
   }

   private double currentMouseX() {
      return this.b.mouse.getX() * this.b.getWindow().getScaledWidth() / this.b.getWindow().getFramebufferWidth();
   }

   private double currentMouseY() {
      return this.b.mouse.getY() * this.b.getWindow().getScaledHeight() / this.b.getWindow().getFramebufferHeight();
   }

   public void a(Matrix3x2fStack Matrix3x2fStackVar) {
      Renderer2D render;
      if (this.b.world != null && this.b.player != null && (render = DercoVisuals.getInstance().getRender()) != null) {
         ScreenScale.a();
         float fGetFramebufferWidth = this.screenWidth();
         float fGetFramebufferHeight = this.screenHeight();
         if (this.s <= 0.0F || this.t <= 0.0F || Math.abs(fGetFramebufferWidth - this.s) > 1.0F || Math.abs(fGetFramebufferHeight - this.t) > 1.0F) {
            this.a(fGetFramebufferWidth, fGetFramebufferHeight);
         }

         this.s = fGetFramebufferWidth;
         this.t = fGetFramebufferHeight;
         if (this.d != null && ModuleRegistry.WATERMARK.a()) {
            this.d.a(Matrix3x2fStackVar, render);
         }

         boolean inClickGui = this.b.currentScreen instanceof DercoVisualsClickGuiScreen;

         for (HudElement hudElement : this.c) {
            if (this.e(hudElement)) {
               hudElement.e();
               hudElement.b(fGetFramebufferWidth, fGetFramebufferHeight);
               if (!inClickGui) {
                  hudElement.a(Matrix3x2fStackVar, render, fGetFramebufferWidth, fGetFramebufferHeight);
                  hudElement.a(render, Matrix3x2fStackVar);
               }
            }
         }

         if (this.j != null && !this.l.isEmpty()) {
            this.a(Matrix3x2fStackVar, render);
         }

         if (this.j != null && !this.u) {
            this.a(Matrix3x2fStackVar, render, fGetFramebufferWidth, fGetFramebufferHeight);
         }

         int iGetScaleFactor = (int)this.currentMouseX();
         int iMethod_44952 = (int)this.currentMouseY();

         for (HudElement hudElement2 : this.c) {
            if (this.e(hudElement2)) {
               hudElement2.a(Matrix3x2fStackVar, render, iGetScaleFactor, iMethod_44952);
            }
         }

         ScreenScale.a();
      }
   }

   private void a(Matrix3x2fStack Matrix3x2fStackVar, Renderer2D renderer2D) {
      for (HudSnapGuide hudSnapGuide : this.l) {
         Color color2 = switch (HudElementManager.AnonymousClass1.$SwitchMap$pulse$HudSnapGuide$Anchor[hudSnapGuide.b().ordinal()]) {
            case 1 -> n;
            case 2, 3 -> o;
            default -> m;
         };
         if (hudSnapGuide.a() == HudSnapGuide.Orientation.VERTICAL) {
            renderer2D.a(hudSnapGuide.c() - 0.5F, hudSnapGuide.d(), 1.0F, hudSnapGuide.e() - hudSnapGuide.d(), 0.0F, color2, Matrix3x2fStackVar);
         } else {
            renderer2D.a(hudSnapGuide.d(), hudSnapGuide.c() - 0.5F, hudSnapGuide.e() - hudSnapGuide.d(), 1.0F, 0.0F, color2, Matrix3x2fStackVar);
         }
      }
   }

   private void a(Matrix3x2fStack Matrix3x2fStackVar, Renderer2D renderer2D, float f, float f2) {
      FontRenderer altFont = FontManager.a[24];
      altFont.a(
         "Удерживайте ALT для свободного перемещения",
         f / 2.0F - altFont.a("Удерживайте ALT для свободного перемещения") / 2.0F,
         f2 - 65.0F,
         new Color(255, 255, 255, 200),
         Matrix3x2fStackVar
      );
   }

   private void a(float f, float f2) {
      Iterator<HudElement> it = this.c.iterator();

      while (it.hasNext()) {
         it.next().a(f, f2);
      }
   }

   public void d() {
   }

   private void f(HudElement hudElement) {
      this.p.push(new HudElementPositionSnapshot(hudElement));
      if (this.p.size() > 50) {
         this.p.removeLast();
      }

      this.q.clear();
   }

   public void e() {
   }

   public void f() {
   }

   public boolean a(int i, int i2) {
      return false;
   }

   public void g() {
      long jGetHandle = this.b.getWindow().getHandle();
      this.u = Bool.from(GLFW.glfwGetKey(jGetHandle, 342) != 1 && GLFW.glfwGetKey(jGetHandle, 346) != 1 ? 0 : 1);
   }

   public HudNotificationCenter h() {
      return this.d;
   }

   public PotionsHudElement i() {
      return this.e;
   }

   public HotkeysHudElement j() {
      return this.f;
   }

   public CooldownsHudElement k() {
      return this.g;
   }

   public TargetHudElement l() {
      return this.h;
   }

   public SaturationHudElement getSaturationHud() {
      return this.saturationHud;
   }

   public InventoryHudElement getInventoryHud() {
      return this.inventoryHud;
   }

   public TotemsHudElement getTotemsHud() {
      return this.totemsHud;
   }

   public void b(Matrix3x2fStack Matrix3x2fStackVar) {
   }

   public void a(double d, double d2) {
      if (this.i) {
         this.g();
         if (!this.b.isWindowFocused()) {
            for (HudElement hudElement : this.c) {
               if (this.e(hudElement)) {
                  hudElement.a(0.0, 0.0, false);
               }
            }

            return;
         }

         double dGetScaleFactor = d;
         double dMethod_44952 = d2;
         if (this.j != null) {
            for (HudElement next : this.c) {
               if (this.e(next)) {
                  next.a(dGetScaleFactor, dMethod_44952, Bool.from(next == this.j ? 1 : 0));
               }
            }

            GuiInput.g();
            return;
         }

         boolean z = false;

         for (HudElement next2 : this.c) {
            if (this.e(next2) && next2.f().c() && next2.f().c((int)dGetScaleFactor, (int)dMethod_44952)) {
               z = true;
               break;
            }
         }

         if (z) {
            for (HudElement hudElement2 : this.c) {
               if (this.e(hudElement2)) {
                  hudElement2.a(dGetScaleFactor, dMethod_44952, false);
               }
            }

            return;
         }

         HudElement hudElement3 = null;

         for (HudElement next3 : this.s()) {
            if (this.e(next3) && next3.a(dGetScaleFactor, dMethod_44952)) {
               hudElement3 = next3;
               break;
            }
         }

         for (HudElement next4 : this.c) {
            if (this.e(next4)) {
               next4.a(dGetScaleFactor, dMethod_44952, Bool.from(next4 == hudElement3 ? 1 : 0));
            }
         }

         if (hudElement3 != null) {
            GuiInput.g();
         }
      }
   }

   public boolean a(double d, double d2, int i) {
      if (this.i && this.b.isWindowFocused()) {
         if (this.d != null && ModuleRegistry.WATERMARK.a() && this.d.a(d, d2, i)) {
            return true;
         }

         for (HudElement hudElement : this.s()) {
            if (this.e(hudElement) && hudElement.a(d, d2, i)) {
               this.j = hudElement;
               if (i == 0 && hudElement.i()) {
                  this.f(hudElement);
                  this.l.clear();
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean b(double d, double d2, int i) {
      if (!this.i) {
         return false;
      }

      boolean z = false;
      if (this.j != null) {
         this.j.b(d, d2, i);
         this.j = null;
         this.l.clear();
         z = true;
      }

      if (this.d != null && ModuleRegistry.WATERMARK.a()) {
         this.d.b(d, d2, i);
      }

      return z;
   }

   public boolean a(double d, double d2, double d3) {
      double dGetScaleFactor = d;
      double dMethod_44952 = d2;
      if (this.j == null) {
         for (HudElement hudElement : this.c) {
            if (this.e(hudElement) && hudElement.f().e() && hudElement.a(dGetScaleFactor, dMethod_44952, d3)) {
               return true;
            }
         }
      }

      return this.d != null && ModuleRegistry.WATERMARK.a() && this.d.a(dGetScaleFactor, dMethod_44952, d3);
   }

   public void a(double d, double d2, double d3, double d4) {
      double dGetScaleFactor = d;
      double dMethod_44952 = d2;
      if (this.j == null) {
         for (HudElement hudElement : this.c) {
            if (this.e(hudElement) && hudElement.f().e()) {
               hudElement.a(dGetScaleFactor, dMethod_44952, d3, d4);
            }
         }
      }

      if (this.d != null && ModuleRegistry.WATERMARK.a()) {
         this.d.a(dGetScaleFactor, dMethod_44952, d3, d4);
      }

      if (this.j != null) {
         this.g();
         float fGetFramebufferWidth = this.screenWidth();
         float fGetFramebufferHeight = this.screenHeight();
         HudSnapResult hudSnapResultA = HudSnapCalculator.a(
            this.j, (float)dGetScaleFactor - this.j.r(), (float)dMethod_44952 - this.j.s(), fGetFramebufferWidth, fGetFramebufferHeight, this.t(), this.u
         );
         this.j.a(hudSnapResultA, fGetFramebufferWidth, fGetFramebufferHeight);
         this.l = hudSnapResultA.c();
      }
   }

   public boolean b(double d, double d2) {
      if (this.j == null) {
         return false;
      }

      double dGetScaleFactor = d;
      double dMethod_44952 = d2;
      float fGetFramebufferWidth = this.screenWidth();
      float fGetFramebufferHeight = this.screenHeight();
      this.g();
      HudSnapResult hudSnapResultA = HudSnapCalculator.a(
         this.j, (float)dGetScaleFactor - this.j.r(), (float)dMethod_44952 - this.j.s(), fGetFramebufferWidth, fGetFramebufferHeight, this.t(), this.u
      );
      this.j.a(hudSnapResultA, fGetFramebufferWidth, fGetFramebufferHeight);
      this.l = hudSnapResultA.c();
      return true;
   }

   public boolean m() {
      return this.i;
   }

   public void a(boolean z) {
      if (this.i != z) {
         this.i = z;
         if (!z) {
            double dScaleMouseX = this.currentMouseX();
            double dScaleMouseY = this.currentMouseY();
            if (this.j != null) {
               this.j.b(dScaleMouseX, dScaleMouseY, 0);
               this.j = null;
            }

            this.l.clear();
            Iterator<HudElement> it = this.c.iterator();

            while (it.hasNext()) {
               it.next().a(0.0, 0.0, false);
            }

            if (this.d != null) {
               this.d.g();
            }
         }
      }
   }

   public void n() {
      this.a(Bool.from(!this.i ? 1 : 0));
   }

   public List<HudElement> o() {
      return this.c;
   }

   public void a(HudElement hudElement) {
      this.c(hudElement);
   }

   public void b(HudElement hudElement) {
      this.c.remove(hudElement);
   }

   public boolean p() {
      return Bool.from(this.j != null ? 1 : 0);
   }

   public boolean isEditing() {
      return this.i;
   }

   public boolean q() {
      return this.u;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   static class AnonymousClass1 {
      static final int[] $SwitchMap$pulse$HudSnapGuide$Anchor = new int[HudSnapGuide.Anchor.values().length];

      static {
         try {
            $SwitchMap$pulse$HudSnapGuide$Anchor[HudSnapGuide.Anchor.SCREEN_CENTER.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$pulse$HudSnapGuide$Anchor[HudSnapGuide.Anchor.ELEMENT_EDGE.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$pulse$HudSnapGuide$Anchor[HudSnapGuide.Anchor.ELEMENT_CENTER.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }
      }
   }

   private static class Holder {
      private static final HudElementManager INSTANCE = new HudElementManager();
   }
}
