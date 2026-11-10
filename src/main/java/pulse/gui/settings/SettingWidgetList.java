package pulse.gui.settings;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix3x2fStack;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.widgets.ColorPickerPopup;
import pulse.gui.widgets.ScrollBar;
import pulse.render.Renderer2D;

public class SettingWidgetList {
   private static final float c = 3.0F;
   private static final float d = 5.0F;
   private static final float e = -2.0F;
   private static final float f = 3.0F;
   private static final float g = 1.0F;
   private static final float h = 10.0F;
   private float l;
   private float m;
   private float n;
   private float o;
   private float p;
   private float q;
   public static int a;
   public static boolean b;
   private final List<SettingWidget> i = new ArrayList<>();
   private float k = 0.0F;
   private final ScrollBar j = new ScrollBar(1.0F, 15.0F);

   public SettingWidgetList() {
      this.j.b(10.0F);
   }

   public void a(float f2) {
      this.k = f2;
   }

   public void b(float f2) {
   }

   public void c(float f2) {
   }

   public void a(SettingWidget settingWidget) {
      this.i.add(settingWidget);
   }

   public void b(SettingWidget settingWidget) {
      this.i.remove(settingWidget);
   }

   public void a(int i, SettingWidget settingWidget) {
      if (i < 0) {
         i = 0;
      }

      if (i > this.i.size()) {
         i = this.i.size();
      }

      this.i.add(i, settingWidget);
   }

   public void a() {
      this.i.clear();
      this.j.e();
   }

   public List<SettingWidget> b() {
      return this.i;
   }

   public float c() {
      if (this.i.isEmpty()) {
         return 0.0F;
      }

      float fB = 0.0F;
      int i = 0;

      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d()) {
            fB += settingWidget.b() + -2.0F;
            i++;
         }
      }

      if (i > 0) {
         fB -= -2.0F;
      }

      return fB;
   }

   private float f(float f2) {
      return f2 - 5.0F - 3.0F;
   }

   public boolean d(float f2) {
      return Bool.from(this.c() <= this.f((this.k > 0.0F ? 1 : (this.k == 0.0F ? 0 : -1)) <= 0 ? f2 : this.k) ? 0 : 1);
   }

   public boolean d() {
      if (ColorPickerPopup.a().c()) {
         return true;
      }

      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget.l()) {
            return true;
         }
      }

      return false;
   }

   public ModeSettingWidget e() {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget instanceof ModeSettingWidget modeSettingWidget && modeSettingWidget.m()) {
            return modeSettingWidget;
         }
      }

      return null;
   }

   public ColorSettingWidget f() {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget instanceof ColorSettingWidget colorSettingWidget && colorSettingWidget.h()) {
            return colorSettingWidget;
         }
      }

      return null;
   }

   public boolean e(float f2) {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget instanceof ModeSettingWidget modeSettingWidget && modeSettingWidget.o() && modeSettingWidget.n() > f2) {
            return true;
         }
      }

      return false;
   }

   public boolean a(int i, int i2) {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget instanceof ModeSettingWidget && ((ModeSettingWidget)settingWidget).c(i, i2)) {
            return true;
         }
      }

      return ColorPickerPopup.a().c(i, i2);
   }

   public void g() {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget instanceof ModeSettingWidget) {
            ((ModeSettingWidget)settingWidget).p();
         }

         if (settingWidget instanceof ColorSettingWidget) {
            ((ColorSettingWidget)settingWidget).m();
         }
      }

      ColorPickerPopup.a().b();
   }

   public boolean b(int i, int i2) {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget instanceof ModeSettingWidget && ((ModeSettingWidget)settingWidget).b(i, i2)) {
            return true;
         }
      }

      return false;
   }

   public boolean h() {
      return ColorPickerPopup.a().c();
   }

   public void i() {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget instanceof ColorSettingWidget) {
            ((ColorSettingWidget)settingWidget).m();
         }
      }

      ColorPickerPopup.a().b();
   }

   public float[] j() {
      return ColorPickerPopup.a().f();
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i, int i2, float f6, float f7) {
      if (!this.i.isEmpty()) {
         this.l = f2;
         this.m = f3;
         this.n = f4;
         this.o = f5;
         this.p = f6;
         this.q = f7;
         this.j.a();
         this.j.b(this.c(), this.f(f5));
         float f8 = 3.0F * f7;
         float f9 = 5.0F * f7;
         float f10 = -2.0F * f7;
         float f11 = f5 - f9 - 3.0F * f7;
         float f12 = f3 + f9;
         float f13 = f12 + f11;
         float fB = f12 - this.j.b() * f7;

         for (SettingWidget settingWidget : this.i) {
            if (settingWidget.d()) {
               float fB2 = settingWidget.b() * f7;
               if (fB + fB2 >= f12 && fB <= f13) {
                  boolean zD = this.d();
                  settingWidget.a(
                     MatrixStackVar,
                     renderer2D,
                     f2 + f8,
                     fB,
                     f4 - f8 * 2.0F,
                     zD && !settingWidget.l() ? -9999 : i,
                     zD && !settingWidget.l() ? -9999 : i2,
                     f6,
                     f7
                  );
               }

               fB += fB2 + f10;
            }
         }

         if (this.d(f5)) {
            this.j.a(MatrixStackVar, renderer2D, f2 - 3.0F + f4 - 1.0F * f7 / 2.0F, f3 + f9 + 4.0F * f7, f11 - 8.0F * f7, this.c() * f7, f11, i, i2, this.d());
         }
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2, float f2, float f3) {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget.j()) {
            settingWidget.a(MatrixStackVar, renderer2D, this.l, this.m, this.n, this.o, i, i2, f2, f3);
         }
      }

      ColorPickerPopup.a().a(MatrixStackVar, renderer2D, i, i2, f2);
   }

   public boolean a(float f2, float f3, float f4, float f5, int i, int i2) {
      if (ColorPickerPopup.a().c() && ColorPickerPopup.a().a(i, i2)) {
         return true;
      }

      if (this.d()) {
         for (SettingWidget settingWidget : this.i) {
            if (settingWidget.d() && settingWidget.l() && settingWidget.a(f2 + 3.0F, 0.0F, f4 - 6.0F, i, i2)) {
               return true;
            }
         }

         this.g();
         return true;
      } else {
         if (b) {
            throw new ExceptionInInitializerError();
         }

         float f6 = this.f(f5);
         float fC = this.c();
         if (this.d(f5) && this.j.a(f2 - 3.0F + f4 - 0.5F, f3 + 5.0F + 4.0F, f6 - 8.0F, fC, f6, i, i2)) {
            return true;
         }

         float f7 = f3 + 5.0F;
         if (!GuiInput.a(f2, f7, f4, f6, i, i2)) {
            return false;
         }

         float f8 = f4 - 6.0F;
         float fB = f7 - this.j.b();

         for (SettingWidget settingWidget2 : this.i) {
            if (settingWidget2.d()) {
               float fB2 = settingWidget2.b();
               if (fB + fB2 >= f7 && fB <= f7 + f6 && i2 >= f7 && i2 <= f7 + f6 && settingWidget2.a(f2 + 3.0F, fB, f8, i, i2)) {
                  return true;
               }

               fB += fB2 + -2.0F;
            }
         }

         return false;
      }
   }

   public boolean b(float f2, float f3, float f4, float f5, int i, int i2) {
      if (ColorPickerPopup.a().c()) {
         if (ColorPickerPopup.a().c(i, i2)) {
            return true;
         }

         ColorPickerPopup.a().b();
         return true;
      } else if (this.d()) {
         for (SettingWidget settingWidget : this.i) {
            if (settingWidget.d() && settingWidget.l() && settingWidget.b(f2 + 3.0F, 0.0F, f4 - 6.0F, i, i2)) {
               return true;
            }
         }

         this.g();
         return true;
      } else {
         float f6 = this.f(f5);
         float f7 = f3 + 5.0F;
         if (!GuiInput.a(f2, f7, f4, f6, i, i2)) {
            return false;
         }

         float f8 = f4 - 6.0F;
         float fB = f7 - this.j.b();

         for (SettingWidget settingWidget2 : this.i) {
            if (settingWidget2.d()) {
               float fB2 = settingWidget2.b();
               if (fB + fB2 >= f7 && fB <= f7 + f6 && i2 >= f7 && i2 <= f7 + f6 && settingWidget2.b(f2 + 3.0F, fB, f8, i, i2)) {
                  return true;
               }

               fB += fB2 + -2.0F;
            }
         }

         return false;
      }
   }

   public void c(int i, int i2) {
      this.j.d();
      ColorPickerPopup.a().b(i, i2);

      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d()) {
            settingWidget.a(i, i2);
         }
      }
   }

   public void a(float f2, int i, int i2, double d2, double d3) {
      if (this.j.c()) {
         this.j.a(i2, this.c(), this.f(f2));
      } else {
         ColorPickerPopup.a().a(i, i2, d2, d3);

         for (SettingWidget settingWidget : this.i) {
            if (settingWidget.d()) {
               settingWidget.a(i, i2, d2, d3);
            }
         }
      }
   }

   public void a(float f2, float f3) {
      if (!this.j.c() && this.d(f3) && !this.d()) {
         this.j.a(f2, this.c(), this.f(f3));
      }
   }

   public void k() {
      this.j.e();
   }

   public boolean c(float f2, float f3, float f4, float f5, int i, int i2) {
      return GuiInput.a(f2, f3, f4, f5, i, i2);
   }

   public boolean a(int i, int i2, int i3) {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget.a(i, i2, i3)) {
            return true;
         }
      }

      return false;
   }

   public boolean a(char c2, int i) {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget.a(c2, i)) {
            return true;
         }
      }

      return false;
   }

   public boolean l() {
      for (SettingWidget settingWidget : this.i) {
         if (settingWidget.d() && settingWidget.a_()) {
            return true;
         }
      }

      return false;
   }

   public float m() {
      return this.c() + 5.0F + 3.0F;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
