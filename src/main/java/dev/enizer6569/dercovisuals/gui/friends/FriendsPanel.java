// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.friends;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.PanelFadeOverlay;
import dev.enizer6569.dercovisuals.gui.widgets.ScrollBar;
import dev.enizer6569.dercovisuals.gui.widgets.SearchBox;
import dev.enizer6569.dercovisuals.gui.widgets.TextInput;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.ColorUtils;

public class FriendsPanel {
   private static final float a = 19.5F;
   private static final float b = 98.5F;
   private static final float c = 5.0F;
   private static final float d = 19.5F;
   private static final float e = 6.5F;
   private static final float f = 5.0F;
   private static final float g = 9.5F;
   private static final float h = 6.5F;
   private static final float i = 6.5F;
   private static final float j = 6.5F;
   private static final float k = 1.0F;
   private static final float l = 9.5F;
   private static final float m = 2.0F;
   private static final int n = 2;
   private static final Color o = Theme.I;
   private static final Color p = Theme.K;
   private static final Color q = Theme.O;
   private final TextInput u;
   private final ScrollBar v;
   private final PanelFadeOverlay w;
   private Consumer<HistoryEntry> B;
   private float C;
   private float D;
   private float E;
   private float F;
   private float G;
   private float H;
   private float I;
   private final List<HistoryEntry> r = new ArrayList<>();
   private final List<FriendCard> s = new ArrayList<>();
   private final AnimationState x = new AnimationState();
   private boolean y = false;
   private String z = "";
   private List<FriendCard> A = new ArrayList<>();
   private final SearchBox t = new SearchBox();

   public FriendsPanel() {
      this.t.a(this::a);
      this.u = new TextInput(TextInput.InputType.PLAYER, "Добавить...");
      this.u.a(16);
      this.u.a(9.5F);
      this.u.a(Theme.S, Theme.T);
      this.v = new ScrollBar(2.0F, 20.0F);
      this.v.b(10.0F);
      this.v.a(Theme.b);
      this.v.b(Theme.d);
      this.w = new PanelFadeOverlay(25, 5.0F, 9.0F);
   }

   public void a(HistoryEntry historyEntry) {
   }

   public void b(HistoryEntry historyEntry) {
   }

   public List<HistoryEntry> a() {
      return this.r;
   }

   public void b() {
   }

   public void a(Consumer<HistoryEntry> consumer) {
      this.B = consumer;
   }

   private void a(String str) {
      this.z = str.toLowerCase().trim();
      this.e();
      this.v.e();
   }

   private void e() {
      if (this.z.isEmpty()) {
         this.A = new ArrayList<>(this.s);
      } else {
         this.A = this.s.stream().filter(friendCard -> friendCard.a().a().toLowerCase().contains(this.z)).collect(Collectors.toList());
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, int i3, float f6) {
      this.C = f2;
      this.D = f3;
      this.E = f4;
      this.F = f5;
      this.v.a();
      this.x.a();
      float f7 = f4 - 98.5F - 5.0F - 19.5F - 5.0F;
      this.t.a(MatrixStackVar, renderer2D, f2, f3, 98.5F, 19.5F, i2, i3);
      float f8 = f2 + 98.5F + 5.0F;
      this.u.a(MatrixStackVar, renderer2D, f8, f3, f7, 19.5F, i2, i3, f6);
      this.a(MatrixStackVar, renderer2D, f8 + f7 + 5.0F, f3, i2, i3, f6);
      float f9 = f3 + 24.5F;
      float f10 = f5 - (f9 - f3);
      this.G = f9;
      this.H = f10;
      this.I = f4;
      this.b(MatrixStackVar, renderer2D, f2, f9, f4, f10, i2, i3, f6);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      Color colorB = Theme.b(Theme.m, f6);
      Color colorB2 = Theme.b(Theme.n, f6);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 9.5F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      int i2 = (int)(255.0F * f6);
      Color colorA = Theme.a(Theme.e, i2);
      Color colorA2 = Theme.a(Theme.f, i2);
      renderer2D.a(f2, f3, f4, f5, 9.5F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[24];
      FontRenderer fontRenderer2 = FontManager.a[15];
      String str = this.r.isEmpty() ? "У тебя пока-что нету друзей :(" : "Ничего не найдено";
      String str2 = this.r.isEmpty() ? "Введи никнейм друга в поле “Добавить” и нажми на плюсик" : "Попробуй изменить запрос";
      float fA = fontRenderer.a(str);
      float fA2 = fontRenderer2.a(str2);
      float f6 = f2 + (f4 - fA) / 2.0F;
      float f7 = f3 + f5 / 2.0F - 20.0F;
      float f8 = f2 + (f4 - fA2) / 2.0F;
      fontRenderer.a(str, f6, f7, Theme.a, MatrixStackVar);
      fontRenderer2.a(str2, f8, f7 + 15.0F, Theme.b, MatrixStackVar);
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, int i3, float f6) {
      if (this.A.isEmpty()) {
         this.a(MatrixStackVar, renderer2D, f2, f3, f4, f5);
      } else {
         float f7 = f2 + 1.0F;
         float f8 = f3 + 6.5F;
         float f9 = f4 - 2.0F;
         float f10 = f5 - 6.5F - 6.5F;
         float f11 = this.f();
         boolean z = f11 > f10;
         if (!z) {
            this.v.e();
         }

         renderer2D.b().a(f2, f3 + 4.0F, f4, f5 - 8.0F, MatrixStackVar);
         float f12 = (f9 - 9.5F) / 2.0F;
         float fB = f8 - this.v.b();
         int i4 = 0;

         for (FriendCard friendCard : this.A) {
            float f13 = f7 + i4 % 2 * (f12 + 9.5F);
            float f14 = fB + i4 / 2 * 43.5F;
            if (f14 + 37.0F >= f8 && f14 <= f8 + f10) {
               friendCard.a(MatrixStackVar, renderer2D, f13, f14, f12, i2, i3, f6);
            }

            i4++;
         }

         renderer2D.b().a(MatrixStackVar);
         if (z) {
            this.v.a(MatrixStackVar, renderer2D, f2 + f4 + 18.0F, f8, f10, f11, f10, i2, i3, false);
         }

         this.w.a(MatrixStackVar, renderer2D, f2, f3 + 1.5F, f4, f5, f6);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4) {
      int i4 = (int)(255.0F * f4);
      boolean zA = GuiInput.a(f2, f3, 19.5F, 19.5F, i2, i3);
      if (zA != this.y) {
         this.x.a(zA ? 1.0 : 0.0, 0.15, Easing.h);
         this.y = zA;
      }

      float fJ = (float)this.x.j();
      Color colorB = Theme.b(Theme.q, f4);
      Color colorB2 = Theme.b(Theme.n, f4);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 20.5F, 20.5F, 6.5F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = ColorUtils.a(Theme.f, o, fJ);
      Color colorA2 = ColorUtils.a(Theme.e, p, fJ);
      Color colorA3 = Theme.a(colorA, i4);
      Color colorA4 = Theme.a(colorA2, i4);
      renderer2D.a(f2, f3, 19.5F, 19.5F, 6.5F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      Color colorA5 = Theme.a(ColorUtils.a(Theme.b, q, fJ), i4);
      float f5 = f2 + 7.25F;
      float f6 = f3 + 7.25F;
      renderer2D.a(f5, f6 + 2.5F - 0.75F, 5.0F, 1.5F, colorA5, MatrixStackVar);
      renderer2D.a(f5 + 2.5F - 0.75F, f6, 1.5F, 5.0F, colorA5, MatrixStackVar);
      if (zA) {
         GuiInput.g();
      }
   }

   private float f() {
      return this.A.isEmpty() ? 0.0F : (int)Math.ceil(this.A.size() / 2.0) * 43.5F - 6.5F;
   }

   public boolean a(float f2, float f3, float f4, float f5, int i2, int i3) {
      if (this.t.a(i2, i3)) {
         this.u.a(false);
         return true;
      }

      float f6 = f4 - 98.5F - 5.0F - 19.5F - 5.0F;
      float f7 = f2 + 98.5F + 5.0F;
      if (this.u.a(i2, i3, 0)) {
         this.t.b(false);
         return true;
      }

      if (GuiInput.a(f7 + f6 + 5.0F, f3, 19.5F, 19.5F, i2, i3)) {
         this.g();
         return true;
      }

      float f8 = f3 + 19.5F + 5.0F + 6.5F;
      float f9 = f5 - 19.5F - 5.0F - 6.5F - 6.5F;
      float f10 = f4 - 2.0F;
      float f11 = this.f();
      if (f11 > f9 && this.v.a(f2 + f4 + 18.0F, f8, f9, f11, f9, i2, i3)) {
         return true;
      }

      float f12 = f2 + 1.0F;
      float f13 = (f10 - 9.5F) / 2.0F;
      if (GuiInput.a(f12, f8, f10, f9, i2, i3)) {
         float fB = f8 - this.v.b();
         int i4 = 0;

         for (Iterator<FriendCard> it = this.A.iterator(); it.hasNext(); i4++) {
            if (it.next().a(f12 + i4 % 2 * (f13 + 9.5F), fB + i4 / 2 * 43.5F, f13, i2, i3)) {
               return true;
            }
         }
      }

      return false;
   }

   private void g() {
   }

   public void a(int i2, int i3) {
      this.v.d();
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (this.v.c()) {
         float f2 = this.H - 6.5F - 6.5F;
         this.v.a(i3, this.f(), f2);
      }
   }

   public void a(float f2, int i2, int i3) {
      float f3 = this.D + 19.5F + 5.0F;
      float f4 = this.F - 19.5F - 5.0F;
      if (GuiInput.a(this.C, f3, this.E, f4, i2, i3)) {
         this.v.a(f2, this.f(), f4 - 6.5F - 6.5F);
      }
   }

   public boolean a(int i2, int i3, int i4) {
      return this.t.c() ? this.t.a(i2, i3, i4) : (this.u.d() ? this.u.b(i2, i3, i4) : false);
   }

   public boolean a(char c2, int i2) {
      return this.t.c() ? this.t.a(c2, i2) : (this.u.d() ? this.u.a(c2, i2) : false);
   }

   public boolean c() {
      return Bool.from(!this.t.c() && !this.u.d() ? 0 : 1);
   }

   public boolean d() {
      return this.r.isEmpty();
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
