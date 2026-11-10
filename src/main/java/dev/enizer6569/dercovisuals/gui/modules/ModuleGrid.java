// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.modules;

import java.util.List;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiOverlay;
import dev.enizer6569.dercovisuals.gui.core.GuiEntry;
import dev.enizer6569.dercovisuals.gui.core.GuiEntrySource;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.GuiLayerRegistry;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.gui.widgets.ScrollBar;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.render.Renderer2D;

public class ModuleGrid implements ClickGuiOverlay {
   private final GuiEntrySource a;
   private static final float b = 19.0F;
   private static final float c = 48.0F;
   private static final float d = 26.0F;
   private static final float e = 6.5F;
   private static final float f = 8.0F;
   private static final float g = 2.0F;
   private float k;
   private float l;
   private float m;
   private float n;
   private int o;
   private int p;
   private boolean q = false;
   private final ScrollBar h = new ScrollBar(6.0F, 25.0F);
   private final ModuleCardList i = new ModuleCardList();
   private final ModuleRowRenderer j = new ModuleRowRenderer();

   public ModuleGrid(GuiEntrySource guiEntrySource) {
      this.a = guiEntrySource;
   }

   public List<ModuleCard> a() {
      return this.i.a();
   }

   private float k() {
      int iF = this.a.f();
      float fD = DercoVisualsClickGuiScreen.d() - 38.0F;
      return iF == 1 ? fD - 2.0F : (fD - 6.5F) / 2.0F - 1.0F;
   }

   private float c(int i, int i2) {
      return (int)Math.ceil((double)i / i2) * 34.0F - 8.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      this.k = f2;
      this.l = f3;
      this.o = i;
      this.p = i2;
      this.h.a();
      List<? extends GuiEntry> listE = this.a.e();
      if (listE != null && !listE.isEmpty()) {
         int iF = this.a.f();
         float fK = this.k();
         float f4 = f3 + 48.0F;
         float fE = DercoVisualsClickGuiScreen.e() - 48.0F - 19.0F;
         float fCeil = (int)Math.ceil((double)listE.size() / iF) * 34.0F - 8.0F;
         if (fCeil < 0.0F) {
            fCeil = 0.0F;
         }

         float fMax = Math.max(0.0F, fCeil - fE);
         float fB = this.h.b();
         if (fB > fMax) {
            this.h.e(fMax);
            fB = fMax;
         }

         float f5 = f4 - fB;
         float f6 = f2 + 19.0F;
         float f7 = f4 + fE;
         GuiLayerRegistry.a().a(GuiLayerRegistry.Layer.CONTENT, f2, f4, DercoVisualsClickGuiScreen.d(), fE);
         renderer2D.b().a(f2, f4, DercoVisualsClickGuiScreen.d(), fE, MatrixStackVar);

         for (int i3 = 0; i3 < listE.size(); i3++) {
            GuiEntry guiEntry = listE.get(i3);
            int i4 = i3 % iF;
            float f8 = f6 + i4 * (fK + 6.5F) + 1.0F;
            float f9 = f5 + i3 / iF * 34.0F + 1.0F;
            if (f9 + 26.0F >= f4 && f9 <= f7) {
               this.j.a(MatrixStackVar, renderer2D, guiEntry, f8, f9, fK, 26.0F, i, i2, false, i4 != 0, false, false);
            }
         }

         renderer2D.b().a(MatrixStackVar);
         if (fCeil > fE + 1.0F) {
            this.h.a(MatrixStackVar, renderer2D, f2 + DercoVisualsClickGuiScreen.d() - 2.0F, f4, fE, fCeil, fE, i, i2, false);
         }

         this.i.b(MatrixStackVar, renderer2D, this.k, this.l, i, i2, this.m, this.n);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, List<? extends GuiEntry> list, float f2, float f3, float f4, int i, int i2) {
   }

   private boolean a(List<? extends GuiEntry> list, float f2, float f3, float f4, int i, int i2) {
      if (!(i2 < f3) && !(i2 > f3 + f4)) {
         int iF = this.a.f();
         float fK = this.k();
         float fB = f3 - this.h.b();

         for (int i3 = 0; i3 < list.size(); i3++) {
            if (GuiInput.a(f2 + i3 % iF * (fK + 6.5F) + 1.0F, fB + i3 / iF * 34.0F + 1.0F, fK, 26.0F, i, i2)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2) {
      this.i.b(MatrixStackVar, renderer2D, this.k, this.l, i, i2, this.m, this.n);
   }

   public void b() {
      this.i.b();
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D) {
   }

   @Override
   public void a(float f2, float f3, int i, int i2) {
      this.b(f2, f3, i, i2);
   }

   @Override
   public void b(float f2, float f3, int i, int i2) {
      this.h.a();
      boolean z = (MinecraftContext.c.currentScreen instanceof DercoVisualsClickGuiScreen ? ((DercoVisualsClickGuiScreen)MinecraftContext.c.currentScreen).clickBtn : 0) == 1;
      if (this.i.hasOpenPanelAt(i, i2)) {
         if (z) {
            this.i.f();
         } else {
            this.i.c(i, i2);
         }
      } else {
         float f4 = f3 + 48.0F;
         float fE = DercoVisualsClickGuiScreen.e() - 48.0F - 5.0F;
         List<? extends GuiEntry> listE;
         if (!(i2 < f4) && !(i2 > f4 + fE) && (listE = this.a.e()) != null && !listE.isEmpty()) {
            int iF = this.a.f();
            float fK = this.k();
            float f5 = f2 + 19.0F;
            float fB = f4 - this.h.b();

            for (int i3 = 0; i3 < listE.size(); i3++) {
               GuiEntry guiEntry = listE.get(i3);
               int i4 = i3 % iF;
               float f6 = f5 + i4 * (fK + 6.5F) + 1.0F;
               float f7 = fB + i3 / iF * 34.0F + 1.0F;
               if (GuiInput.a(f6, f7, fK, 26.0F, i, i2)) {
                  float fB2 = f6 + fK - ModuleRowRenderer.b() - ModuleRowRenderer.a();
                  float fB3 = f7 + 13.0F - ModuleRowRenderer.b() / 2.0F;
                  float fD = fB2 - ModuleRowRenderer.d() - ModuleRowRenderer.c();
                  float fC = f7 + 13.0F - ModuleRowRenderer.c() / 2.0F;
                  if (guiEntry.d() && guiEntry.g() && GuiInput.a(fD, fC, ModuleRowRenderer.c(), ModuleRowRenderer.c(), i, i2)) {
                     this.i.a(guiEntry, i3, i4 != 0, f6, f7, fK, 26.0F, fD, fC);
                     return;
                  }

                  if (!z) {
                     guiEntry.a(!guiEntry.b());
                     return;
                  }

                  if (guiEntry.g()) {
                     this.i.a(guiEntry, i3, i4 != 0, f6, f7);
                     return;
                  }

                  return;
               }
            }

            if (z) {
               float fK2 = this.k();
               float f8 = f2 + 19.0F;
               float f9 = f3 + 48.0F;

               for (int i5 = 0; i5 < listE.size(); i5++) {
                  GuiEntry guiEntry2 = listE.get(i5);
                  int i6 = i5 % 2;
                  float f10 = f8 + i6 * (fK2 + 6.5F) + 1.0F;
                  float f11 = f9 + i5 / 2 * 34.0F + 1.0F;
                  if (GuiInput.a(f10, f11, fK2, 26.0F, i, i2)) {
                     if (guiEntry2.g()) {
                        this.i.a(guiEntry2, i5, i6 != 0, f10, f11);
                        return;
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   public void c(float f2, float f3, int i, int i2) {
      this.h.d();
      this.i.e(i, i2);
   }

   public void a(float f2, float f3, int i, int i2, double d2, double d3) {
      this.i.a(i, i2, d2, d3);
      if (this.h.c()) {
         List<? extends GuiEntry> listE = this.a.e();
         this.h.a(i2, this.c(listE.size(), this.a.f()), DercoVisualsClickGuiScreen.e() - 48.0F - 18.0F);
      }
   }

   public void a(float f2) {
      try {
         if (this.i.hasOpenPanelAt(this.o, this.p) && this.i.a(f2, this.o, this.p)) {
            return;
         }
      } catch (Throwable var5) {
      }

      try {
         if (this.h.c()) {
            return;
         }
      } catch (Throwable var4) {
      }

      List<? extends GuiEntry> listE = this.a.e();
      if (listE != null && !listE.isEmpty()) {
         int iF = this.a.f();
         this.h.a(f2, this.c(listE.size(), iF), DercoVisualsClickGuiScreen.e() - 48.0F - 19.0F);
      }
   }

   public void a(float f2, int i, int i2) {
      this.o = i;
      this.p = i2;
      this.a(f2);
   }

   public boolean a(int i, int i2, int i3) {
      return this.i.a(i, i2, i3);
   }

   public boolean a(char c2, int i) {
      return this.i.a(c2, i);
   }

   public boolean c() {
      return this.i.l();
   }

   public void d() {
      this.h.e();
      this.i.i();
   }

   public void e() {
      this.i.i();
   }

   public void f() {
      this.i.j();
   }

   public void g() {
      this.i.g();
   }

   public void h() {
      this.i.h();
   }

   public void i() {
      this.i.k();
   }

   public boolean j() {
      return this.i.c();
   }

   public boolean a(int i, int i2) {
      return this.i.b(i, i2);
   }

   public ModuleCard b(int i, int i2) {
      return this.i.a(i, i2);
   }

   public boolean hasOpenSettingsAt(int i, int i2) {
      return this.i.hasOpenPanelAt(i, i2);
   }

   public ModuleCard findCardByBounds(int i, int i2) {
      return this.i.findCardByBounds(i, i2);
   }

   public void a(ClientModule clientModule) {
      this.i.a(clientModule);
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public boolean p() {
      return this.i.l();
   }
}
