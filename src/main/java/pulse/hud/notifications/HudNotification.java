package pulse.hud.notifications;

import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.render.Renderer2D;

public abstract class HudNotification {
   protected static final MinecraftClient c = MinecraftClient.getInstance();
   protected final AnimationState d = new AnimationState();
   protected final AnimationState e = new AnimationState();
   protected final AnimationState f = new AnimationState();
   protected float g = 8.0F;
   protected float h = 4.0F;
   protected float i = 12.0F;
   protected long j = 1000L;
   protected boolean l = false;
   protected long k = System.currentTimeMillis();

   public HudNotification() {
      this.f.d(0.0);
      this.f.a(1.0, 0.2, Easing.k);
   }

   public abstract float a();

   public abstract float b();

   public abstract void a(Matrix3x2fStack var1, Renderer2D var2, float var3, float var4, float var5, float var6, float var7);

   public float c() {
      return this.g + this.a() + this.g;
   }

   public float d() {
      return this.h + this.b() + this.h;
   }

   public void e() {
      this.d.a();
      this.e.a();
      this.f.a();
      if (!this.l && System.currentTimeMillis() - this.k > this.j) {
         this.f();
      }
   }

   public void f() {
      if (!this.l) {
         this.l = true;
         this.f.a(0.0, 0.2, Easing.j);
      }
   }

   public boolean g() {
      return this.l && !(this.f.j() > 0.01);
   }

   public boolean h() {
      return this.l;
   }

   public float i() {
      return (float)this.f.j();
   }

   public float j() {
      return this.g;
   }

   public float k() {
      return this.h;
   }

   public float l() {
      return this.i;
   }

   protected Color a(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f));
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
