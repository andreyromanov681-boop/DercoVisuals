// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.notifications;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.GuiInteractionState;
import dev.enizer6569.dercovisuals.hud.notifications.Notification;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.ColorUtils;

public class NotificationIconPicker {
   private static final float c = 43.25F;
   private static final float d = 23.0F;
   private static final float e = 12.0F;
   private static final float f = 5.0F;
   private static final float g = 9.5F;
   private static final int h = 5;
   private static final Color i = Theme.I;
   private static final Color j = Theme.J;
   private static final Color k = Theme.N;
   private Consumer<Notification.Icon> q;
   public static int a;
   public static boolean b;
   private final Map<Notification.Icon, AnimationState> n = new HashMap<>();
   private final Map<Notification.Icon, AnimationState> o = new HashMap<>();
   private final Map<Notification.Icon, Boolean> p = new HashMap<>();
   private Color r = Theme.y;
   private final Notification.Icon[] l = Notification.Icon.values();
   private Notification.Icon m = Notification.Icon.DEATH;

   public NotificationIconPicker() {
      for (Notification.Icon icon : this.l) {
         this.n.put(icon, new AnimationState());
         this.o.put(icon, new AnimationState());
         this.p.put(icon, false);
         if (icon == this.m) {
            this.o.get(icon).d(1.0);
         }
      }
   }

   public void a(Notification.Icon icon) {
      if (this.m != icon) {
         if (this.m != null) {
            this.o.get(this.m).a(0.0, 0.2, Easing.h);
         }

         this.m = icon;
         this.o.get(icon).a(1.0, 0.2, Easing.h);
      }
   }

   public Notification.Icon a() {
      return this.m;
   }

   public void a(Color color) {
      this.r = color;
   }

   public void a(Consumer<Notification.Icon> consumer) {
      this.q = consumer;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5) {
      int i5 = (int)(255.0F * f5);
      boolean zB = GuiInteractionState.a().b();

      for (int i6 = 0; i6 < this.l.length; i6++) {
         Notification.Icon icon = this.l[i6];
         float f6 = f2 + i6 % 5 * 48.25F;
         float f7 = f3 + i6 / 5 * 28.0F;
         int i4;
         if (zB) {
            i4 = 0;
         } else if (GuiInput.a(f6, f7, 43.25F, 23.0F, i2, i3)) {
            i4 = 1;
         } else {
            i4 = 0;
         }

         int i7 = i4;
         if (zB && this.p.get(icon)) {
            this.n.get(icon).a(0.0, 0.15, Easing.h);
            this.p.put(icon, false);
         } else if (!zB && Bool.from(i7) != this.p.get(icon)) {
            this.n.get(icon).a(i7 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
            this.p.put(icon, Bool.from(i7));
         }

         this.n.get(icon).a();
         this.o.get(icon).a();
         float fJ = (float)this.n.get(icon).j();
         float fJ2 = (float)this.o.get(icon).j();
         Color color = Theme.q;
         Color color2 = Theme.n;
         Color color3 = Theme.f;
         Color color4 = Theme.e;
         Color colorA;
         Color colorA2;
         Color colorA3;
         Color colorA4;
         if (fJ2 <= 0.01F) {
            colorA = ColorUtils.a(color3, i, fJ);
            colorA2 = ColorUtils.a(color4, j, fJ);
            colorA3 = color;
            colorA4 = color2;
         } else {
            Color color5 = this.r;
            Color colorC = Theme.c(color5, 150);
            colorA = ColorUtils.a(ColorUtils.a(color3, i, fJ), color5, fJ2);
            colorA2 = ColorUtils.a(ColorUtils.a(color4, j, fJ), colorC, fJ2);
            colorA3 = ColorUtils.a(color, color5, fJ2);
            colorA4 = ColorUtils.a(color2, colorC, fJ2);
         }

         Color colorB = Theme.b(colorA3, f5);
         Color colorB2 = Theme.b(colorA4, f5);
         Color colorA5 = Theme.a(colorA, i5);
         Color colorA6 = Theme.a(colorA2, i5);
         renderer2D.a(f6 - 0.5F, f7 - 0.5F, 44.25F, 24.0F, 9.5F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
         renderer2D.a(f6, f7, 43.25F, 23.0F, 9.5F, colorA5, colorA5, colorA6, colorA6, MatrixStackVar);
         FontManager.e[22]
            .a(
               icon.b(),
               f6 + 15.625F,
               f7 + 5.5F,
               Theme.a(fJ2 <= 0.01F ? ColorUtils.a(Theme.b, k, fJ) : ColorUtils.a(ColorUtils.a(Theme.b, k, fJ), Theme.aa, fJ2), i5),
               MatrixStackVar
            );
         if (i7 != 0 && !zB) {
            GuiInput.g();
         }
      }
   }

   public boolean a(float f2, float f3, int i2, int i3) {
      for (int i4 = 0; i4 < this.l.length; i4++) {
         Notification.Icon icon = this.l[i4];
         if (GuiInput.a(f2 + i4 % 5 * 48.25F, f3 + i4 / 5 * 28.0F, 43.25F, 23.0F, i2, i3)) {
            this.a(icon);
            if (this.q != null) {
               this.q.accept(icon);
            }

            return true;
         }
      }

      return false;
   }

   public float b() {
      int rows = (int)Math.ceil(this.l.length / 5.0);
      return rows * 23.0F + (rows - 1) * 5.0F;
   }

   public static float c() {
      return 43.25F;
   }

   public static float d() {
      return 23.0F;
   }

   public static float e() {
      return 5.0F;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
