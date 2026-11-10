// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.config;

import java.awt.Color;
import java.util.function.Consumer;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.config.ConfigEntry;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.GuiInteractionState;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.ColorUtils;

public class ConfigActionMenu {
   private static final float c = 79.0F;
   private static final float d = 12.0F;
   private static final float e = 11.0F;
   private static final float f = 9.5F;
   private static final float g = 7.0F;
   private static final float h = 6.5F;
   private static final float i = 8.0F;
   private static final float j = 4.0F;
   private static final float k = 3.0F;
   private float m;
   private float n;
   private float o;
   private ConfigEntry p;
   private Consumer<ConfigActionMenu.Action> t;
   private Runnable u;
   public static int a;
   public static boolean b;
   private boolean l = false;
   private final AnimationState q = new AnimationState();
   private final AnimationState[] r = new AnimationState[4];
   private final boolean[] s = new boolean[4];

   public ConfigActionMenu() {
      for (int i2 = 0; i2 < 4; i2++) {
         this.r[i2] = new AnimationState();
         this.s[i2] = false;
      }
   }

   public void a(float f2, float f3, float f4, ConfigEntry configEntry) {
      this.o = f2 + f4 / 2.0F;
      this.m = this.o - 39.5F;
      this.n = f3 + 3.0F;
      this.p = configEntry;
      this.l = true;
      this.q.d(0.0);
      this.q.a(1.0, 0.25, Easing.F);

      for (int i2 = 0; i2 < 4; i2++) {
         this.r[i2].d(0.0);
         this.s[i2] = false;
      }

      GuiInteractionState.a().d(true);
      GuiInput.a(this.m, this.n, 79.0F, 69.5F);
   }

   private boolean a(ConfigActionMenu.Action action) {
      if (this.p == null) {
         return false;
      } else {
         return this.p.f() ? action != ConfigActionMenu.Action.SAVE_TO : false;
      }
   }

   private String b(ConfigActionMenu.Action action) {
      return this.p != null && this.p.f() && action == ConfigActionMenu.Action.SAVE_TO ? "Применить" : action.a();
   }

   public void a(float f2, float f3, ConfigEntry configEntry) {
      this.a(f2, f3, 0.0F, configEntry);
   }

   public void a() {
      if (this.l) {
         this.q.a(0.0, 0.15, Easing.g);
         GuiInteractionState.a().d(false);
         GuiInput.a();
      }
   }

   public boolean b() {
      return this.l;
   }

   public boolean c() {
      return !this.l || this.q.j() < 0.01;
   }

   public ConfigEntry d() {
      return this.p;
   }

   public void a(Consumer<ConfigActionMenu.Action> consumer) {
      this.t = consumer;
   }

   public void a(Runnable runnable) {
      this.u = runnable;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i2, int i3) {
      this.q.a();
      float fJ = (float)this.q.j();
      if (fJ < 0.01F) {
         if (this.l && this.q.d()) {
            this.l = false;
            if (this.u != null) {
               this.u.run();
            }
         }
      } else {
         for (int i4 = 0; i4 < 4; i4++) {
            this.r[i4].a();
         }

         float fMax = Math.max(0.0F, Math.min(1.0F, fJ));
         int i5 = (int)(255.0F * fMax);
         float f2 = 79.0F * fMax;
         float f3 = 69.5F * fMax;
         float f4 = this.m + 39.5F;
         float f5 = this.n + 34.75F;
         float f6 = f4 - f2 / 2.0F;
         float f7 = f5 - f3 / 2.0F;
         Color colorA = Theme.a(Theme.q, i5);
         Color colorA2 = Theme.a(Theme.r, i5);
         renderer2D.a(f6 - 0.5F * fMax, f7 - 0.5F * fMax, f2 + fMax, f3 + fMax, 9.5F * fMax, colorA, colorA, colorA2, colorA2, MatrixStackVar);
         Color colorA3 = Theme.a(Theme.e, i5);
         Color colorA4 = Theme.a(Theme.f, i5);
         renderer2D.a(f6, f7, f2, f3, 9.5F * fMax, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
         if (fMax > 0.1F) {
            MatrixStackVar.pushMatrix();
            MatrixStackVar.translate(f4, f5);
            MatrixStackVar.scale(fMax, fMax);
            MatrixStackVar.translate(-f4, -f5);
            int i6 = (int)(this.m + 12.0F);
            int i7 = (int)(this.n + 11.0F);
            FontRenderer fontRenderer = FontManager.a[12];
            ConfigActionMenu.Action[] actionArrValues = ConfigActionMenu.Action.values();

            for (int i8 = 0; i8 < actionArrValues.length; i8++) {
               float f8 = i7 + i8 * 13.5F;
               boolean zA = this.a(actionArrValues[i8]);
               int i9 = !zA
                     && !(fMax <= 0.5F)
                     && GuiInput.a(f6 + 12.0F * fMax - 5.0F, f7 + 11.0F * fMax + i8 * 13.5F * fMax - 2.0F, 55.0F * fMax + 10.0F, 7.0F * fMax + 4.0F, i2, i3)
                  ? 1
                  : 0;
               if (Bool.from(i9) != this.s[i8]) {
                  this.r[i8].a(i9 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
                  this.s[i8] = Bool.from(i9);
               }

               float fJ2 = (float)this.r[i8].j();
               this.a(
                  MatrixStackVar,
                  renderer2D,
                  fontRenderer,
                  i6,
                  f8,
                  1.0F,
                  actionArrValues[i8],
                  this.b(actionArrValues[i8]),
                  Theme.a(
                     !zA
                        ? (
                           actionArrValues[i8] != ConfigActionMenu.Action.DELETE
                              ? ColorUtils.a(Theme.a, Theme.aa, fJ2)
                              : ColorUtils.a(Theme.Z, Theme.b(Theme.Z, 30), fJ2)
                        )
                        : Theme.b,
                     i5
                  ),
                  i5
               );
               if (i9 != 0) {
                  GuiInput.g();
               }
            }

            MatrixStackVar.popMatrix();
         }
      }
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      float f2,
      float f3,
      float f4,
      ConfigActionMenu.Action action,
      String str,
      Color color,
      int i2
   ) {
      float f5 = 8.0F * f4;
      float f6 = f2 + f5 + 4.0F * f4;
      this.a(MatrixStackVar, renderer2D, f2, f3, f5, action, color);
      fontRenderer.a(str, f6, f3, color, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, ConfigActionMenu.Action action, Color color) {
      String str;
      switch (action) {
         case SAVE_TO:
            str = "\ue920";
            break;
         case SHARE:
            str = "\ue922";
            break;
         case RENAME:
            str = "\ue909";
            break;
         case DELETE:
            str = "\ue924";
            break;
         default:
            return;
      }

      FontManager.e[14].a(str, f2, f3, color, MatrixStackVar);
   }

   public boolean a(int i2, int i3) {
      if (this.l && !(this.q.j() < 0.5)) {
         if (!GuiInput.a(this.m, this.n, 79.0F, 69.5F, i2, i3)) {
            this.a();
            return true;
         }

         float f2 = this.m + 12.0F;
         float f3 = this.n + 11.0F;
         ConfigActionMenu.Action[] actionArrValues = ConfigActionMenu.Action.values();

         for (int i4 = 0; i4 < actionArrValues.length; i4++) {
            if (GuiInput.a(f2 - 5.0F, f3 + i4 * 13.5F - 2.0F, 65.0F, 11.0F, i2, i3)) {
               if (this.a(actionArrValues[i4])) {
                  return true;
               }

               if (this.t != null) {
                  this.t.accept(actionArrValues[i4]);
               }

               this.a();
               return true;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean b(int i2, int i3) {
      return this.l ? GuiInput.a(this.m, this.n, 79.0F, 69.5F, i2, i3) : false;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   public enum Action {
      SAVE_TO("Сохранить в"),
      SHARE("Поделиться"),
      RENAME("Переименовать"),
      DELETE("Удалить");

      private final String label;
      public static int e;
      public static boolean f;

      Action(String str) {
         this.label = str;
      }

      public String a() {
         return this.label;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }
}
