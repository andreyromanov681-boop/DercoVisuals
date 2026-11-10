package pulse.hud.notifications;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;

public class ActionNotification extends HudNotification {
   private static final float a = 16.0F;
   private static final float b = 5.0F;
   private static final float m = -9.0F;
   private static final float n = 3.0F;
   private static final float o = 8.0F;
   private static final float p = 15.0F;
   private static final float q = 13.0F;
   private static final float r = 5.0F;
   private final ActionNotification.NotificationType s;
   private final String t;
   private final String u;
   private final String v;
   private final Runnable w;
   private final Color x;
   private final Color y;
   private final Supplier<Boolean> z;
   private final boolean A;
   private List<String> B;
   private boolean C = false;
   private float D;
   private float E;
   private float F;
   private float G;
   private float H;
   private float I;

   private ActionNotification(ActionNotification.Builder builder) {
      this.s = builder.a;
      this.t = builder.b;
      this.u = builder.c;
      this.v = builder.d;
      this.w = builder.e;
      this.x = builder.f != null ? builder.f : this.s.b();
      this.y = builder.g != null ? builder.g : this.s.c();
      this.z = builder.i;
      this.A = builder.j;
      this.g = 9.5F;
      this.h = 7.0F;
      this.j = builder.h;
      this.r();
   }

   @Override
   public void e() {
      this.d.a();
      this.e.a();
      this.f.a();
      if (!this.A && !this.l && System.currentTimeMillis() - this.k > this.j) {
         this.f();
      }

      if (this.z != null && !this.l) {
         if (this.z.get()) {
            this.k = System.currentTimeMillis();
         } else {
            this.f();
         }
      }
   }

   public boolean m() {
      return Bool.from(this.z != null ? 1 : 0);
   }

   private void r() {
      this.B = new ArrayList<>();
      if (this.u != null && !this.u.isEmpty()) {
         FontRenderer fontRenderer = FontManager.a[13];
         float fA = this.a() - -9.0F - 16.0F - 5.0F;

         for (String str : this.u.split("crypt")) {
            if (str.isEmpty()) {
               this.B.add("");
            } else {
               String[] strArrSplit = str.split("crypt");
               StringBuilder sb = new StringBuilder();

               for (String str2 : strArrSplit) {
                  if (!(fontRenderer.a(sb.length() == 0 ? str2 : sb + "Ẓ\ud9d3Ẁ\ud980Ữ\ud9b0Ằﲠ" + str2) <= fA) && sb.length() > 0) {
                     this.B.add(sb.toString());
                     sb = new StringBuilder(str2);
                  } else {
                     if (sb.length() > 0) {
                        sb.append("crypt");
                     }

                     sb.append(str2);
                  }
               }

               if (sb.length() > 0) {
                  this.B.add(sb.toString());
               }
            }
         }
      }
   }

   @Override
   public float a() {
      return (this.u == null || this.u.isEmpty()) && this.v == null ? 13.0F + FontManager.b[15].a(this.t) : 147.5F;
   }

   @Override
   public float b() {
      float size = 9.0F;
      if (!this.B.isEmpty()) {
         size = 12.0F + this.B.size() * 8.0F;
      }

      if (this.v != null) {
         size += 20.0F;
      }

      return size;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, float f5) {
      this.H = f - this.g;
      this.I = f2 - this.h;
      FontRenderer fontRenderer = FontManager.b[15];
      FontRenderer fontRenderer2 = FontManager.a[13];
      FontRenderer fontRenderer3 = FontManager.b[12];
      int i = (int)(f5 * 255.0F);
      float f6 = f + -9.0F;
      float f7 = f2 + 4.5F - 8.0F;
      FontManager.e[15].a(this.s.a(), f6 + 8.0F, f7 + 4.0F, new Color(this.x.getRed(), this.x.getGreen(), this.x.getBlue(), i), MatrixStackVar);
      Color color2 = new Color(255, 255, 255, i);
      float f8 = f6 + 16.0F + 5.0F;
      fontRenderer.a(this.t, f8, f7 + 8.0F - 4.5F, color2, MatrixStackVar);
      float f9 = f2 + 9.0F;
      if (!this.B.isEmpty()) {
         f9 += 3.0F;
         Color color3 = new Color(223, 223, 243, i);

         for (Iterator<String> it = this.B.iterator(); it.hasNext(); f9 += 8.0F) {
            fontRenderer2.a(it.next(), f8, f9, color3, MatrixStackVar);
         }
      }

      if (this.v != null) {
         this.D = f;
         this.E = f9 + 5.0F;
         this.F = f3;
         this.G = 15.0F;
         this.s();
         Color color;
         if (this.C) {
            int red = this.y.getRed();
            int iMin = Math.min(255, (red | 30) + (red & 30));
            int green = this.y.getGreen();
            color = new Color(iMin, Math.min(255, (green | 30) + (green & 30)), Math.min(255, this.y.getBlue() - -31 - 1), i);
         } else {
            color = new Color(this.y.getRed(), this.y.getGreen(), this.y.getBlue(), i);
         }

         renderer2D.a(this.D, this.E, this.F, this.G, 13.0F, color, MatrixStackVar);
         fontRenderer3.a(this.v, this.D + (this.F - fontRenderer3.a(this.v)) / 2.0F, this.E + this.G / 2.0F - 3.5F, new Color(255, 255, 255, i), MatrixStackVar);
      }
   }

   private void s() {
      if (this.v != null && c.currentScreen != null && c.currentScreen instanceof ChatScreen) {
         double dT = this.t();
         double dU = this.u();
         this.C = Bool.from(!(dT < this.D) && !(dT > this.D + this.F) && !(dU < this.E) && !(dU > this.E + this.G) ? 1 : 0);
      } else {
         this.C = false;
      }
   }

   public void n() {
      if (this.v != null && c.currentScreen != null && c.currentScreen instanceof ChatScreen) {
         double dT = this.t();
         double dU = this.u();
         if (dT >= this.D && dT <= this.D + this.F && dU >= this.E && dU <= this.E + this.G) {
            GuiInput.g();
         }
      }
   }

   private double t() {
      double[] dArr = new double[1];
      GLFW.glfwGetCursorPos(c.getWindow().getHandle(), dArr, new double[1]);
      return dArr[0] / 2.0;
   }

   private double u() {
      double[] dArr = new double[1];
      GLFW.glfwGetCursorPos(c.getWindow().getHandle(), new double[1], dArr);
      return dArr[0] / 2.0;
   }

   public boolean a(double d, double d2, int i) {
      return false;
   }

   public boolean o() {
      return Bool.from(this.v != null ? 1 : 0);
   }

   public static ActionNotification.Builder a(String str) {
      return null;
   }

   @Generated
   public String p() {
      return this.t;
   }

   @Generated
   public boolean q() {
      return this.A;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class Builder {
      private final String b;
      private String c;
      private String d;
      private Runnable e;
      private Color f;
      private Color g;
      private Supplier<Boolean> i;
      private ActionNotification.NotificationType a = ActionNotification.NotificationType.BELL;
      private long h = 5000L;
      private boolean j = false;

      public Builder(String str) {
         this.b = str;
      }

      public ActionNotification.Builder a(ActionNotification.NotificationType notificationType) {
         this.a = notificationType;
         return this;
      }

      public ActionNotification.Builder a(String str) {
         this.c = str;
         return this;
      }

      public ActionNotification.Builder a(String str, Runnable runnable) {
         this.d = str;
         this.e = runnable;
         return this;
      }

      public ActionNotification.Builder a(Color color) {
         this.f = color;
         return this;
      }

      public ActionNotification.Builder b(Color color) {
         this.g = color;
         return this;
      }

      public ActionNotification.Builder a(long j) {
         this.h = j;
         return this;
      }

      public ActionNotification.Builder a(Supplier<Boolean> supplier) {
         this.i = supplier;
         return this;
      }

      public ActionNotification.Builder a() {
         this.j = true;
         return this;
      }

      public ActionNotification b() {
         return new ActionNotification(this);
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public enum NotificationType {
      BELL("\ue903", new Color(49, 49, 69), new Color(87, 215, 106)),
      WARNING("\ue925", new Color(243, 82, 66), new Color(243, 82, 66)),
      FIRE("\ue90c", new Color(248, 135, 64), new Color(248, 135, 64)),
      CLOUD("\ue904", new Color(100, 72, 227), new Color(100, 72, 227));

      private final String icon;
      private final Color background;
      private final Color accent;

      NotificationType(String str, Color color, Color color2) {
         this.icon = str;
         this.background = color;
         this.accent = color2;
      }

      public String a() {
         return this.icon;
      }

      public Color b() {
         return this.background;
      }

      public Color c() {
         return this.accent;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }
}
