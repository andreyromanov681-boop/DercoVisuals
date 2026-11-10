// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.widgets;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.GuiInteractionState;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.theme.Theme;

public class SearchBox {
   private static final Color c = Theme.d;
   private static final float d = 8.5F;
   private static final float e = 12.0F;
   private static final float f = 4.0F;
   private static final float g = 4.0F;
   private static final int h = 16;
   private float i;
   private float j;
   private float k;
   private float l;
   private String m = "";
   private int n = 0;
   private int o = -1;
   private int p = -1;
   private boolean q = false;
   private final AnimationState r = new AnimationState();
   private final AnimationState s = new AnimationState();
   private final AnimationState t = new AnimationState();
   private boolean u = false;
   private boolean v = true;
   private boolean w = false;
   private Consumer<String> x;
   public static int a;
   public static boolean b;

   public SearchBox() {
      this.t.d(1.0);
   }

   public SearchBox(float f2, float f3) {
      this();
      this.k = f2;
      this.l = f3;
   }

   public void a(float f2, float f3) {
      this.i = f2;
      this.j = f3;
   }

   public void b(float f2, float f3) {
      this.k = f2;
      this.l = f3;
   }

   public void a(Consumer<String> consumer) {
      this.x = consumer;
   }

   public void a(boolean z) {
      this.w = z;
   }

   public String a() {
      return this.m;
   }

   public void a(String str) {
      this.m = str;
      this.n = str.length();
      this.r();
      this.u();
   }

   public void b() {
      this.m = "";
      this.n = 0;
      this.r();
      this.u();
   }

   public boolean c() {
      return this.q;
   }

   public void b(boolean z) {
      if (this.q != z) {
         this.q = z;
         this.r.a(!z ? 0.0 : 1.0, 0.2, Easing.h);
         if (z) {
            this.t.d(1.0);
            this.v = false;
            this.t.a(0.3, 0.5, Easing.i);
         }
      }
   }

   public boolean d() {
      return this.m.isEmpty();
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i, int i2) {
      this.i = f2;
      this.j = f3;
      this.k = f4;
      this.l = f5;
      this.r.a();
      this.s.a();
      if (this.q) {
         this.t.a();
         if (this.t.d()) {
            this.v = Bool.from(this.v ? 0 : 1);
            this.t.a(!this.v ? 0.3 : 1.0, 0.5, Easing.i);
         }
      } else {
         this.t.d(1.0);
         this.v = true;
      }

      boolean z = !this.w && GuiInteractionState.a().b();
      int i3 = !z && GuiInput.a(f2, f3, f4, f5, i, i2) ? 1 : 0;
      if (z && this.u) {
         this.s.a(0.0, 0.15, Easing.h);
         this.u = false;
      } else if (!z && Bool.from(i3) != this.u) {
         this.s.a(i3 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.u = Bool.from(i3);
      }

      float fJ = (float)this.r.j();
      float fJ2 = (float)this.s.j();
      this.a(MatrixStackVar, renderer2D, f2, f3, f4, f5, fJ, fJ2);
      this.a(MatrixStackVar, renderer2D, f2, f3, f5, fJ, fJ2);
      this.b(MatrixStackVar, renderer2D, f2, f3, f4, f5, fJ);
      if ((i3 != 0 || this.q) && !z) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6, float f7) {
      Color border = new Color(38, 34, 52, 160);
      Color bg = new Color(22, 19, 31, 230);
      float radius = f5 / 2.0F;
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, radius, border, border, border, border, MatrixStackVar);
      renderer2D.a(f2, f3, f4, f5, radius, bg, bg, bg, bg, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      FontRenderer fontRenderer = FontManager.e[12];
      FontRenderer fontRenderer2 = FontManager.b[13];
      Color iconColor = new Color(125, 120, 145);
      fontRenderer.a("\ue921", f2 + 8.0F, f3 + f4 / 2.0F - fontRenderer2.b("A") / 4.0F + 0.5F, iconColor, MatrixStackVar);
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      TextRenderer tr = MinecraftClient.getInstance().textRenderer;
      DrawContext dc = Renderer2DImpl.currentDrawContext;
      float f7 = f2 + 22.0F;
      float fB = f3 + f5 / 2.0F - 4.0F;
      renderer2D.b().a(f7 - 2.0F, f3, f4 - 40.0F, f5, MatrixStackVar);
      FontRenderer fontRenderer = FontManager.b[13];
      if (fontRenderer != null) {
         if (this.m.isEmpty() && !this.q) {
            fontRenderer.a(MatrixStackVar, "Поиск", f7, fB, new Color(125, 120, 145));
         } else if (this.m.isEmpty() && this.q) {
            fontRenderer.a(MatrixStackVar, "|", f7, fB, Theme.a(Theme.a, (int)(255.0F * (float)this.t.j())));
         } else {
            fontRenderer.a(MatrixStackVar, this.m, f7, fB, Color.WHITE);
         }
      } else if (dc != null) {
         if (this.m.isEmpty() && !this.q) {
            dc.drawText(tr, "Поиск", (int)f7, (int)fB, new Color(125, 120, 145).getRGB(), false);
         } else if (this.m.isEmpty() && this.q) {
            dc.drawText(tr, "|", (int)f7, (int)fB, Theme.a(Theme.a, (int)(255.0F * (float)this.t.j())).getRGB(), false);
         } else {
            dc.drawText(tr, this.m, (int)f7, (int)fB, -1, false);
         }
      }

      if (this.q && !this.q()) {
         float fJ = (float)this.t.j();
         if (fJ > 0.3F) {
            renderer2D.a(
               f7
                  + (
                     fontRenderer != null
                        ? fontRenderer.a(this.m.substring(0, Math.min(this.m.length(), this.n)))
                        : tr.getWidth(this.m.substring(0, Math.min(this.m.length(), this.n)))
                  ),
               f3 + 4.0F,
               1.0F,
               f5 - 8.0F,
               Theme.a(Theme.a, (int)(255.0F * fJ)),
               MatrixStackVar
            );
         }
      }

      renderer2D.b().a(MatrixStackVar);
   }

   public boolean a(int i, int i2) {
      boolean zB = GuiInteractionState.a().b();
      if (GuiInput.a(this.i, this.j, this.k, this.l, i, i2) && !zB) {
         this.b(true);
         this.n = FontManager.b[14].a(this.m, i - (this.i + 4.0F + 12.0F + 4.0F));
         this.r();
         return true;
      }

      if (this.q) {
         this.b(false);
      }

      return false;
   }

   public boolean a(int i, int i2, int i3) {
      if (!this.q) {
         return false;
      }

      int i4 = (~i3 | i3 ^ -1512818939) - ~i3 == 0 ? 0 : 1;
      int i5 = (~i3 | 1) - ~i3 == 0 ? 0 : 1;
      if (i4 != 0) {
         switch (i) {
            case 65:
               this.m();
               break;
            case 67:
               this.n();
               break;
            case 86:
               this.o();
               break;
            case 88:
               this.p();
         }

         return true;
      } else {
         switch (i) {
            case 256:
               this.b(false);
               this.r();
               break;
            case 257:
               this.b(false);
               this.r();
            case 258:
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               break;
            case 259:
               this.i();
               break;
            case 261:
               this.j();
               break;
            case 262:
               this.b(Bool.from(i5), Bool.from(i4));
               break;
            case 263:
               this.a(Bool.from(i5), Bool.from(i4));
               break;
            case 268:
               this.c(Bool.from(i5));
               break;
            case 269:
               this.d(Bool.from(i5));
         }

         return true;
      }
   }

   public boolean a(char c2, int i) {
      if (this.q && !Character.isISOControl(c2)) {
         if (this.m.length() >= 16 && !this.q()) {
            return true;
         }

         if (this.q()) {
            this.s();
         }

         if (this.m.length() < 16) {
            this.m = this.m.substring(0, this.n) + c2 + this.m.substring(this.n);
            int i2 = this.n;
            this.n = (i2 | 1) + (i2 & 1);
            this.t();
            this.u();
         }

         return true;
      } else {
         return false;
      }
   }

   private void i() {
      if (this.q()) {
         this.s();
      } else if (this.n > 0) {
         this.m = this.m.substring(0, this.n - 1) + this.m.substring(this.n);
         int i = this.n;
         this.n = (i & -2) - (~i & 1);
         this.u();
      } else if (b) {
      }

      this.t();
   }

   private void j() {
      if (this.q()) {
         this.s();
      } else if (this.n < this.m.length()) {
         this.m = this.m.substring(0, this.n) + this.m.substring(this.n - -2 - 1);
         this.u();
      }

      this.t();
   }

   private void a(boolean z, boolean z2) {
      int iK;
      if (z2) {
         iK = this.k();
      } else {
         int i = this.n;
         iK = Math.max(0, 2 * (i & -2) - (i ^ 1));
      }

      if (z) {
         if (!this.q()) {
            this.o = this.n;
         }

         this.p = iK;
      } else {
         if (this.q()) {
            iK = Math.min(this.o, this.p);
         }

         this.r();
      }

      this.n = iK;
      this.t();
   }

   private void b(boolean z, boolean z2) {
      int iL;
      if (z2) {
         iL = this.l();
      } else {
         int length = this.m.length();
         int i = this.n;
         iL = Math.min(length, 2 * (i | 1) - (i ^ 1));
      }

      if (z) {
         if (!this.q()) {
            this.o = this.n;
         }

         this.p = iL;
      } else {
         if (this.q()) {
            iL = Math.max(this.o, this.p);
         }

         this.r();
      }

      this.n = iL;
      this.t();
   }

   private void c(boolean z) {
      if (z) {
         if (!this.q()) {
            this.o = this.n;
         }

         this.p = 0;
      } else {
         this.r();
      }

      this.n = 0;
      this.t();
   }

   private void d(boolean z) {
      if (z) {
         if (!this.q()) {
            this.o = this.n;
         }

         this.p = this.m.length();
      } else {
         this.r();
      }

      this.n = this.m.length();
      this.t();
   }

   private int k() {
      if (this.n == 0) {
         return 0;
      }

      int i = this.n - 1;

      while (i > 0 && Character.isWhitespace(this.m.charAt(i))) {
         i--;
      }

      while (i > 0) {
         int i2 = i--;
         if (Character.isWhitespace(this.m.charAt(i2 + ~(i2 & -1742277746 | ~i2 & 1742277745) + 1))) {
         }
      }

      return i;
   }

   private int l() {
      if (this.n >= this.m.length()) {
         return this.m.length();
      }

      int i = this.n;

      while (i < this.m.length() && !Character.isWhitespace(this.m.charAt(i))) {
         i++;
      }

      while (i < this.m.length() && Character.isWhitespace(this.m.charAt(i))) {
         i++;
      }

      return i;
   }

   private void m() {
      this.o = 0;
      this.p = this.m.length();
      this.n = this.m.length();
   }

   private void n() {
      if (this.q()) {
         MinecraftClient.getInstance().keyboard.setClipboard(this.m.substring(Math.min(this.o, this.p), Math.max(this.o, this.p)));
      }
   }

   private void o() {
      String strGetClipboard = MinecraftClient.getInstance().keyboard.getClipboard();
      if (strGetClipboard != null && !strGetClipboard.isEmpty()) {
         String strReplaceAll = strGetClipboard.replaceAll("[\\r\\n\\t]", "");
         if (this.q()) {
            this.s();
         }

         int length = 16 - this.m.length();
         if (strReplaceAll.length() > length) {
            strReplaceAll = strReplaceAll.substring(0, length);
         }

         if (!strReplaceAll.isEmpty()) {
            this.m = this.m.substring(0, this.n) + strReplaceAll + this.m.substring(this.n);
            this.n = this.n - ~strReplaceAll.length() - 1;
            this.u();
         }

         this.t();
      }
   }

   private void p() {
      if (this.q()) {
         this.n();
         this.s();
      }
   }

   private boolean q() {
      return this.o != -1 && this.p != -1 && this.o != this.p;
   }

   private void r() {
      this.o = -1;
      this.p = -1;
   }

   private void s() {
      if (this.q()) {
         int iMin = Math.min(this.o, this.p);
         this.m = this.m.substring(0, iMin) + this.m.substring(Math.max(this.o, this.p));
         this.n = iMin;
         this.r();
         this.u();
      }
   }

   private void t() {
      this.t.d(1.0);
      this.v = false;
      this.t.a(0.3, 0.5, Easing.i);
   }

   private void u() {
      if (this.x != null) {
         this.x.accept(this.m);
      }
   }

   public float e() {
      return this.i;
   }

   public float f() {
      return this.j;
   }

   public float g() {
      return this.k;
   }

   public float h() {
      return this.l;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
