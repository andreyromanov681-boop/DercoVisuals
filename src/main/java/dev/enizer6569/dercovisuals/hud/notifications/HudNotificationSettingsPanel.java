// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.notifications;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.config.ConfigManager;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.PanelFadeOverlay;
import dev.enizer6569.dercovisuals.gui.settings.BooleanSettingWidget;
import dev.enizer6569.dercovisuals.gui.settings.SettingWidgetList;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;
import dev.enizer6569.dercovisuals.theme.Theme;

public class HudNotificationSettingsPanel {
   public static final float a = 150.0F;
   public static final float b = 30.0F;
   public static final float c = 120.0F;
   public static final float d = 9.5F;
   public static final float e = 7.0F;
   private static final int h = 25;
   private static final float i = 5.0F;
   private float n;
   private float o;
   private float p;
   private float q;
   private float r;
   private float s;
   private final PanelFadeOverlay u;
   private BooleanSettingWidget x;
   private BooleanSettingWidget y;
   private BooleanSettingWidget z;
   private BooleanSettingWidget A;
   private BooleanSettingWidget B;
   public static int f;
   public static boolean g;
   private final AnimationState j = new AnimationState();
   private boolean k = false;
   private boolean l = false;
   private float m = 150.0F;
   private final AnimationState v = new AnimationState();
   private boolean w = true;
   private final SettingWidgetList t = new SettingWidgetList();
   private boolean settingsBound = false;

   public HudNotificationSettingsPanel() {
      this.t.b(7.0F);
      this.t.a(120.0F);
      this.x = new BooleanSettingWidget("Уведомления", true);
      this.y = new BooleanSettingWidget("Музыка", true);
      this.z = new BooleanSettingWidget("FPS & Ping", true);
      this.A = new BooleanSettingWidget("Ивенты", true);
      this.B = new BooleanSettingWidget("Функции", true);
      this.u = new PanelFadeOverlay(25, 5.0F, 9.5F);
      this.j.d(0.0);
      this.v.d(1.0);
   }

   public void a(float f2, float f3, float f4, float f5) {
      this.a(f2, f3, f4, f5, 150.0F);
   }

   public void a(float f2, float f3, float f4, float f5, float f6) {
      if (this.k && !this.l) {
         this.b();
      } else {
         this.p = f2;
         this.q = f3;
         this.r = f4;
         this.s = f5;
         this.m = f6;
         this.n = f2 + f4 / 2.0F - this.m / 2.0F;
         this.o = f3 + f5 + 11.0F;
         this.k = true;
         this.l = false;
         this.j.a(1.0, 0.25, Easing.F);
      }
   }

   public void a(float f2) {
      this.m = f2;
   }

   public float a() {
      return this.m;
   }

   public void b() {
      if (this.k) {
         this.l = true;
         this.t.g();
         this.j.a(0.0, 0.15, Easing.g);
      }
   }

   public void c() {
      this.k = false;
      this.l = false;
      this.j.d(0.0);
      this.t.g();
   }

   public boolean d() {
      return this.k;
   }

   public boolean e() {
      return this.l && this.j.d() && this.j.j() < 0.01;
   }

   public void f() {
      this.j.a();
      this.v.a();
      if (this.e()) {
         this.k = false;
         this.l = false;
      }
   }

   public float g() {
      return Math.max(30.0F, Math.min(this.t.m(), 120.0F));
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.WATERMARK != null) {
         this.t.a();
         this.x = new BooleanSettingWidget(ModuleRegistry.WATERMARK.notifications());
         this.y = new BooleanSettingWidget(ModuleRegistry.WATERMARK.music());
         this.z = new BooleanSettingWidget(ModuleRegistry.WATERMARK.fpsAndPing());
         this.A = new BooleanSettingWidget(ModuleRegistry.WATERMARK.events());
         this.B = new BooleanSettingWidget(ModuleRegistry.WATERMARK.functions());
         this.t.a(this.x);
         this.t.a(this.y);
         this.t.a(this.z);
         this.t.a(this.A);
         this.t.a(this.B);
         this.settingsBound = true;
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i2, int i3) {
      this.bindSettings();
      int i4 = 0;
      if (this.k) {
         this.f();
         float fJ = (float)this.j.j();
         if (fJ >= 0.01F) {
            float fG = this.g();
            float fMax = Math.max(0.0F, Math.min(1.0F, fJ));
            int i5 = (int)(255.0F * fMax);
            float f2 = this.n + this.m / 2.0F;
            float f3 = this.o + fG / 2.0F;
            float f4 = this.m * fMax;
            float f5 = fG * fMax;
            float f6 = f2 - f4 / 2.0F;
            float f7 = f3 - f5 / 2.0F;
            Color colorA = Theme.a(Theme.q, i5);
            Color colorA2 = Theme.a(Theme.r, i5);
            renderer2D.a(f6 - 0.5F * fMax, f7 - 0.5F * fMax, f4 + fMax, f5 + fMax, 9.5F * fMax, colorA, colorA, colorA2, colorA2, MatrixStackVar);
            Color colorA3 = Theme.a(Theme.e, i5);
            Color colorA4 = Theme.a(Theme.f, i5);
            renderer2D.a(f6, f7, f4, f5, 9.5F * fMax, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
            IconTextureRegistry.TextureInfo info = IconTextureRegistry.getInfo("arrow_v");
            if (info != null) {
               Identifier IdentifierVarA = info.a();
               RenderSystemHelper.setShaderTexture(0, IdentifierVarA);
               float fB = info.b() / 2.0F * fMax;
               float fC = info.c() / 2.0F * fMax;
               float f8 = f6 + f4 / 2.0F;
               float f9 = f7 - fB / 2.0F - 1.0F;
               Color colorA5 = Theme.a(Theme.aa, i5);
               MatrixStackVar.pushMatrix();
               MatrixStackVar.translate(f8, f9);
               MatrixStackVar.translate(-f8, -f9);
               renderer2D.a(IdentifierVarA, f8 - fB / 2.0F - 0.5F, f9 - fC / 2.0F, fB, fC, colorA5, MatrixStackVar);
               MatrixStackVar.popMatrix();
            }

            if (fMax > 0.1F) {
               renderer2D.b().a(f6, f7 + 7.0F * fMax, f4, (f5 - 7.0F - 6.0F) * fMax, 9.5F * fMax, MatrixStackVar);
               if (this.l) {
                  i4 = -1;
               } else {
                  i4 = i2;
               }

               this.t.a(MatrixStackVar, renderer2D, f6, f7, f4, f5, i4, !this.l ? i3 : -1, fMax, fMax);
               int i7 = this.t.d(f5) && !this.t.e(f7 + f5) ? 1 : 0;
               if (Bool.from(i7) != this.w) {
                  this.v.a(i7 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
                  this.w = Bool.from(i7);
               }

               float fJ2 = (float)this.v.j();
               if (fJ2 > 0.01F) {
                  this.u.a(MatrixStackVar, renderer2D, f6, f7, f4, f5, fMax * fJ2);
               }

               renderer2D.b().a(MatrixStackVar);
            }
         }
      }
   }

   public boolean a(int i2, int i3) {
      if (!this.k || this.l || this.j.j() < 0.5) {
         return false;
      }

      if (this.d(i2, i3)) {
         boolean zA = this.t.a(this.n, this.o, this.m, this.g(), i2, i3);
         if (zA) {
            ConfigManager.a().h();
         }

         return zA;
      } else {
         if (!this.t.d()) {
            return false;
         }

         this.t.g();
         return true;
      }
   }

   public boolean b(int i2, int i3) {
      if (!this.k || this.l || this.j.j() < 0.5) {
         return false;
      }

      if (this.d(i2, i3)) {
         if (!this.t.d()) {
            return this.t.b(this.n, this.o, this.m, this.g(), i2, i3);
         }

         this.t.g();
         return true;
      } else {
         if (!this.t.d()) {
            return false;
         }

         this.t.g();
         return true;
      }
   }

   public void c(int i2, int i3) {
      if (this.k) {
         this.t.c(i2, i3);
      }
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (this.k && !this.l) {
         this.t.a(this.g(), i2, i3, d2, d3);
      }
   }

   public boolean a(float f2, int i2, int i3) {
      if (!this.k || this.l) {
         return false;
      }

      if (!this.d(i2, i3)) {
         return false;
      }

      this.t.a(f2, this.g());
      return true;
   }

   public boolean d(int i2, int i3) {
      return this.k && !(this.j.j() < 0.5) ? GuiInput.a(this.n, this.o, this.m, this.g(), i2, i3) : false;
   }

   public void b(float f2, float f3, float f4, float f5) {
      this.p = f2;
      this.q = f3;
      this.r = f4;
      this.s = f5;
   }

   public boolean h() {
      return this.x.c();
   }

   public boolean i() {
      return this.y.c();
   }

   public boolean j() {
      return this.z.c();
   }

   public boolean k() {
      return this.A.c();
   }

   public boolean l() {
      return this.B.c();
   }

   public void a(boolean z) {
      this.x.a(z);
   }

   public void b(boolean z) {
      this.y.a(z);
   }

   public void c(boolean z) {
      this.z.a(z);
   }

   public void d(boolean z) {
      this.A.a(z);
   }

   public void e(boolean z) {
      this.B.a(z);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
