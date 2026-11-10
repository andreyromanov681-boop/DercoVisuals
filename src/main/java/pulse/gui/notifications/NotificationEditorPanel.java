package pulse.gui.notifications;

import java.awt.Color;
import java.util.function.Consumer;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.config.LocalConfigManager;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiInteractionState;
import pulse.gui.widgets.ColorPickerPopup;
import pulse.gui.widgets.CoordinateInputPanel;
import pulse.gui.widgets.TextInput;
import pulse.hud.notifications.Notification;
import pulse.markers.MapMarker;
import pulse.markers.MarkerManager;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class NotificationEditorPanel {
   private static final float c = 11.5F;
   private static final float d = 0.5F;
   private static final float e = 8.0F;
   private static final float f = 8.0F;
   private static final float g = 8.5F;
   private static final float h = 5.0F;
   private static final float i = 10.0F;
   private static final float j = 13.5F;
   private static final float k = 4.0F;
   private static final float l = 22.0F;
   private static final float m = 9.5F;
   private static final float n = 19.5F;
   private static final float o = 6.5F;
   private static final Color p = Theme.L;
   private static final Color q = Theme.M;
   private Notification r;
   private final CoordinateInputPanel t;
   private final NotificationIconPicker u;
   private float y;
   private float z;
   private Consumer<Notification> A;
   private Consumer<Notification> B;
   public static int a;
   public static boolean b;
   private final AnimationState v = new AnimationState();
   private boolean w = false;
   private boolean x = false;
   private final TextInput s = new TextInput(TextInput.InputType.TEXT, "", "Название");

   public NotificationEditorPanel() {
      this.s.a(24);
      this.s.a(this::a);
      this.s.a(7.5F);
      this.s.e(false);
      this.t = new CoordinateInputPanel();
      this.t.a(this::a);
      this.u = new NotificationIconPicker();
      this.u.a(this::a);
   }

   public void a(Notification notification) {
      boolean sameNotification = this.r == notification;
      this.r = notification;
      if (notification != null) {
         this.s.b(notification.a());
         if (!sameNotification) {
            this.t.a(notification.b(), notification.c(), notification.d());
         }

         this.u.a(notification.f());
         this.u.a(notification.e());
      }

      if (!sameNotification && this.x) {
         ColorPickerPopup.a().b();
         this.x = false;
      }
   }

   public Notification a() {
      return this.r;
   }

   public void a(Consumer<Notification> consumer) {
      this.A = consumer;
   }

   public void b(Consumer<Notification> consumer) {
      this.B = consumer;
   }

   private void a(String str) {
      if (this.r != null && !str.isEmpty() && !this.r.j()) {
         String oldName = this.r.a();
         MapMarker mm = markerFor(this.r);
         this.r.a(str);
         if (mm != null && oldName != null && oldName.equals(mm.a())) {
            mm.a(str);
         }

         this.f();
      }
   }

   private void a(int[] iArr) {
      if (this.r != null && !this.r.j()) {
         MapMarker mm = markerFor(this.r);
         this.r.a(iArr[0]);
         this.r.b(iArr[1]);
         this.r.c(iArr[2]);
         if (mm != null) {
            mm.a(iArr[0]);
            mm.b(iArr[1]);
            mm.c(iArr[2]);
         }

         this.f();
      }
   }

   private void a(Notification.Icon icon) {
      if (this.r != null && !this.r.j()) {
         this.r.a(icon);
         MapMarker mm = markerFor(this.r);
         if (mm != null) {
            mm.a(mapMarkerIconOf(icon));
         }

         this.f();
      }
   }

   private void a(Color color) {
      if (this.r != null) {
         this.r.a(color);
         this.u.a(color);
         MapMarker mm = markerFor(this.r);
         if (mm != null) {
            mm.a(color);
         }

         this.f();
      }
   }

   private static MapMarker.Icon mapMarkerIconOf(Notification.Icon icon) {
      if (icon == null) {
         return MapMarker.Icon.EVENT;
      }

      try {
         return MapMarker.Icon.valueOf(icon.name());
      } catch (IllegalArgumentException ex) {
         return MapMarker.Icon.EVENT;
      }
   }

   private static MapMarker markerFor(Notification notification) {
      MapMarker marker = notification.marker();
      return marker != null ? marker : MarkerManager.a(notification.b(), notification.c(), notification.d());
   }

   public boolean b() {
      return this.r != null && this.r.j();
   }

   private void f() {
      LocalConfigManager.get().requestSave("marker-edit");
      if (this.B != null && this.r != null) {
         this.B.accept(this.r);
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, int i3, float f6) {
      if (this.r != null) {
         this.v.a();
         int i4 = (int)(255.0F * f6);
         Color colorA = Theme.a(Theme.g, i4);
         Color colorA2 = Theme.a(Theme.n, i4);
         renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 11.5F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
         Color colorA3 = Theme.a(Theme.e, i4);
         Color colorA4 = Theme.a(Theme.f, i4);
         renderer2D.a(f2, f3, f4, f5, 11.5F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
         FontRenderer fontRenderer = FontManager.a[15];
         FontRenderer fontRenderer2 = FontManager.a[14];
         float f7 = f2 + 8.0F;
         float f8 = f4 - 16.0F;
         float f9 = f3 + 8.0F;
         fontRenderer.a("Настройки меток", f7, f9, Theme.a(Theme.a, i4), MatrixStackVar);
         this.a(MatrixStackVar, renderer2D, f2 + f4 - 8.0F - 13.5F, f9 - 2.0F, i2, i3, f6);
         float f10 = f9 + 24.0F;
         Color colorA5 = Theme.a(Theme.b, i4);
         float f11 = f10 + 12.0F;
         fontRenderer2.a("Название метки", f7, f11 - 11.0F, colorA5, MatrixStackVar);
         this.a(MatrixStackVar, renderer2D, f7, f11, f8, i2, i3, f6);
         float f12 = f11 + 22.0F + 14.0F;
         fontRenderer2.a("Координаты", f7, f12 - 11.0F, colorA5, MatrixStackVar);
         this.t.a(MatrixStackVar, renderer2D, f7, f12, f8, i2, i3, f6);
         float fE = f12 + CoordinateInputPanel.e() + 14.0F;
         fontRenderer2.a("Иконка", f7, fE - 11.0F, colorA5, MatrixStackVar);
         this.u.a(MatrixStackVar, renderer2D, f7, fE, f8, i2, i3, f6);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4) {
      int i4 = (int)(255.0F * f4);
      boolean zB = GuiInteractionState.a().b();
      int i5 = !zB && GuiInput.a(f2, f3, 13.5F, 13.5F, i2, i3) ? 1 : 0;
      if (zB && this.w) {
         this.v.a(0.0, 0.15, Easing.h);
         this.w = false;
      } else if (!zB && Bool.from(i5) != this.w) {
         this.v.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.w = Bool.from(i5);
      }

      float fJ = (float)this.v.j();
      Color colorB = Theme.b(Theme.q, f4);
      Color colorB2 = Theme.b(Theme.n, f4);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 14.5F, 14.5F, 4.0F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = ColorUtils.a(Theme.f, p, fJ);
      Color colorA2 = ColorUtils.a(Theme.e, q, fJ);
      Color colorA3 = Theme.a(colorA, i4);
      Color colorA4 = Theme.a(colorA2, i4);
      renderer2D.a(f2, f3, 13.5F, 13.5F, 4.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      FontManager.e[15].a("\ue924", f2 + 2.75F, f3 + 2.75F - 0.5F, Theme.a(ColorUtils.a(Theme.b, Theme.Z, fJ), i4), MatrixStackVar);
      if (i5 != 0 && !zB) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5) {
      float f6 = f4 - 19.5F - 8.0F;
      this.s.a(MatrixStackVar, renderer2D, f2, f3, f6, 22.0F, i2, i3, f5);
      this.y = f2 + f6 + 8.0F;
      this.z = f3 + 1.25F;
      Color colorA = Theme.a(this.r.e(), (int)(255.0F * f5));
      Color colorC = Theme.c(colorA, 150);
      Color colorC2 = Theme.c(colorA, 100);
      renderer2D.a(this.y - 0.5F, this.z - 0.5F, 20.5F, 20.5F, 7.0F, colorA, colorA, colorC2, colorC2, MatrixStackVar);
      renderer2D.a(this.y, this.z, 19.5F, 19.5F, 6.5F, colorA, colorA, colorC, colorC, MatrixStackVar);
      if (!GuiInteractionState.a().b() && GuiInput.a(this.y, this.z, 19.5F, 19.5F, i2, i3)) {
         GuiInput.g();
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i2, int i3, float f2) {
      ColorPickerPopup.a().a(MatrixStackVar, renderer2D, i2, i3, f2);
   }

   public boolean a(float f2, float f3, float f4, float f5, int i2, int i3) {
      if (this.r == null) {
         return false;
      }

      if (this.x && ColorPickerPopup.a().a(i2, i3)) {
         return true;
      }

      if (GuiInput.a(f2 + f4 - 8.0F - 13.5F, f3 + 8.0F - 2.0F, 13.5F, 13.5F, i2, i3)) {
         if (this.A != null) {
            this.A.accept(this.r);
         }

         return true;
      } else if (GuiInput.a(this.y, this.z, 19.5F, 19.5F, i2, i3)) {
         if (this.x) {
            this.c();
         } else {
            this.g();
         }

         return true;
      } else if (this.b()) {
         return false;
      } else {
         float f6 = f2 + 8.0F;
         float labelGap = 12.0F;
         float fieldGap = 14.0F;
         float titleArea = 24.0F;
         float inputH = 22.0F;
         float coordH = CoordinateInputPanel.e();
         float baseY = f3 + 8.0F + titleArea + labelGap;
         float iconY = baseY + inputH + fieldGap + coordH + fieldGap;
         boolean hitName = this.s.a(i2, i3, 0);
         boolean hitCoord = this.t.c(i2, i3, 0);
         boolean hitIcon = this.u.a(f6, iconY, i2, i3);
         if (!hitName && !hitCoord && !hitIcon) {
            this.s.a(false);
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean a(int i2, int i3) {
      if (this.x && ColorPickerPopup.a().c(i2, i3)) {
         this.c();
         return true;
      } else {
         return false;
      }
   }

   private void g() {
      this.x = true;
      ColorPickerPopup.a()
         .a((int)(this.y + 9.75F + 45.0F), (int)(this.z + 19.5F - 12.0F), ColorPickerPopup.Edge.RIGHT, "Кастомный цвет", this.r.e(), 1.0F, this::a);
   }

   public void c() {
      if (this.x) {
         ColorPickerPopup.a().b();
         this.x = false;
      }
   }

   public void b(int i2, int i3) {
      if (this.x) {
         ColorPickerPopup.a().b(i2, i3);
      }
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (this.x) {
         ColorPickerPopup.a().a(i2, i3, d2, d3);
      }
   }

   public boolean a(int i2, int i3, int i4) {
      return !this.s.d() ? (!this.t.d() ? false : this.t.b(i2, i3, i4)) : this.s.b(i2, i3, i4);
   }

   public boolean a(char c2, int i2) {
      return !this.s.d() ? (!this.t.d() ? false : this.t.a(c2, i2)) : this.s.a(c2, i2);
   }

   public boolean d() {
      return Bool.from(!this.s.d() && !this.t.d() ? 0 : 1);
   }

   public boolean e() {
      return this.x && ColorPickerPopup.a().c();
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
