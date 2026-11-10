package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import pulse.core.Bool;
import pulse.events.KeyInputEvent;
import pulse.events.MouseScrollEvent;
import pulse.events.WorldRenderEndEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.KeySetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Zoom", b = "Плавный зум с регулировкой колесиком мыши", c = ModuleCategory.UTILITIES)
public class Zoom extends ClientModule {
   private final KeySetting g = new KeySetting("Кнопка зума", 67);
   private final SliderSetting h = new SliderSetting("Стартовый FOV", 50.0F, 2.0F, 120.0F, 1.0F);
   private double i = 70.0;
   private double j = 70.0;
   private double k = 70.0;
   private double l = 0.5;
   private boolean m = false;
   private boolean n = false;
   private int o = 0;
   public static int e;
   public static boolean f;
   public static volatile double b = 70.0;
   public static volatile boolean a = false;

   @Override
   public void e() {
      super.e();
      if (c.options != null) {
         this.i = ((Integer)c.options.getFov().getValue()).intValue();
         this.j = this.i;
         this.k = this.i;
         this.l = (Double)c.options.getMouseSensitivity().getValue();
      }
   }

   @Override
   public void f() {
      this.p();
      super.f();
   }

   private void n() {
      if (c.options != null && c.player != null && c.world != null) {
         if (!this.n) {
            this.i = ((Integer)c.options.getFov().getValue()).intValue();
            this.l = (Double)c.options.getMouseSensitivity().getValue();
            this.n = true;
         }

         this.j = Math.min(this.i, Math.max(2.0, this.h.a()));
         this.k = ((Integer)c.options.getFov().getValue()).intValue();
         this.o = c.player.getInventory().getSelectedSlot();
         this.m = true;
      }
   }

   private void o() {
      if (c.options != null) {
         this.m = false;
         this.j = this.i;
         c.options.getMouseSensitivity().setValue(this.l);
         c.options.smoothCameraEnabled = false;
      }
   }

   private void p() {
      if (c.options != null) {
         c.options.getMouseSensitivity().setValue(this.l);
         c.options.smoothCameraEnabled = false;
      }

      this.m = false;
      this.n = false;
      a = false;
      this.j = this.i;
      this.k = this.i;
   }

   @EventHandler
   public void a(WorldRenderEndEvent worldRenderEndEvent) {
      if (c.options != null) {
         if (this.m && c.currentScreen != null) {
            this.o();
            return;
         }

         if (!this.m && this.b(this.k, this.j, 0.2)) {
            double dIntValue = ((Integer)c.options.getFov().getValue()).intValue();
            this.i = dIntValue;
            this.j = dIntValue;
            this.k = dIntValue;
            this.l = (Double)c.options.getMouseSensitivity().getValue();
            this.n = false;
         }

         if (this.b(this.k, this.j, 0.1)) {
            this.k = this.j;
         } else {
            this.k = this.k + (this.j - this.k) * (24.0 / Math.max(1.0, this.q()));
            if (this.b(this.k, this.j, 0.2)) {
               this.k = this.j;
            }
         }

         a = this.n;
         b = this.k;
         if (this.m) {
            if (c.player != null) {
               c.player.getInventory().setSelectedSlot(this.o);
            }

            c.options.getMouseSensitivity().setValue(Math.min(this.l, Math.max(this.l * (this.i > 2.0 ? this.j / this.i : 1.0), 0.0)));
         }
      }
   }

   @EventHandler
   public void a(KeyInputEvent keyInputEvent) {
      if (this.k() && this.g.b() && keyInputEvent.a() == this.g.a() && c.currentScreen == null) {
         if (keyInputEvent.c() == 1) {
            this.n();
         } else if (keyInputEvent.c() == 0) {
            this.o();
         }
      }
   }

   @EventHandler
   public void a(MouseScrollEvent mouseScrollEvent) {
      if (this.k() && this.m && c.currentScreen == null) {
         mouseScrollEvent.a(true);
         double d = -mouseScrollEvent.e() * 20.0;
         if (this.i > 2.0) {
            d *= (this.j - 2.0) / (this.i - 2.0);
         }

         this.j = this.a(this.j + d, 2.0, this.i);
      }
   }

   private int q() {
      if (c == null) {
         return 60;
      }

      try {
         int fps = c.getCurrentFps();
         return fps > 0 ? fps : 60;
      } catch (Exception e2) {
         return 60;
      }
   }

   private double a(double d, double d2, double d3) {
      return d >= d2 ? (d <= d3 ? d : d3) : d2;
   }

   private boolean b(double d, double d2, double d3) {
      return Bool.from(Math.abs(d - d2) > d3 ? 0 : 1);
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
