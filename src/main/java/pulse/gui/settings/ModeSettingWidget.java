package pulse.gui.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiLayerRegistry;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.settings.ModeSetting;
import pulse.theme.Theme;
import pulse.util.ColorUtils;
import pulse.util.MarqueeText;

public class ModeSettingWidget implements SettingWidget {
   public static final float a = 25.0F;
   private static final float e = 8.0F;
   private static final float f = 17.0F;
   private static final float g = 6.0F;
   private static final float h = 20.0F;
   private static final float i = 7.5F;
   private static final float j = 6.0F;
   private static final float k = -2.0F;
   private static final float l = 7.5F;
   private static final float m = 6.0F;
   private static final float n = 1.5F;
   private static final float o = 15.0F;
   private final String q;
   private final String[] r;
   private final boolean s;
   private final ModeSetting t;
   private int u;
   private final Set<Integer> v;
   private boolean w = false;
   private final AnimationState x = new AnimationState();
   private final AnimationState y = new AnimationState();
   private final MarqueeText z = new MarqueeText();
   private final List<AnimationState> A = new ArrayList<>();
   private final List<Boolean> B = new ArrayList<>();
   private boolean C = false;
   private boolean D = false;
   private boolean E = false;
   private float F;
   private float G;
   private float H;
   private float I = 1.0F;
   private float J;
   private float K;
   private float L;
   private float M;
   public static int b;
   public static boolean c;
   private static final Set<ModeSettingWidget> d = Collections.newSetFromMap(new WeakHashMap<>());
   private static final Color p = Theme.U;

   public ModeSettingWidget(ModeSetting modeSetting) {
      this.t = modeSetting;
      this.q = modeSetting.f();
      this.r = modeSetting.a();
      this.s = modeSetting.c();
      this.u = modeSetting.k();
      this.v = new HashSet<>(modeSetting.e());
      this.q();
      d.add(this);
   }

   public ModeSettingWidget(String str, String[] strArr, int i2) {
      this(str, strArr, i2, false);
   }

   public ModeSettingWidget(String str, String[] strArr, int i2, boolean z) {
      this.t = null;
      this.q = str;
      this.r = strArr;
      this.s = z;
      int length = strArr.length;
      this.u = Math.max(0, Math.min(i2, (length & -2) - (~length & 1)));
      this.v = new HashSet<>();
      if (z && i2 >= 0 && i2 < strArr.length) {
         this.v.add(i2);
      }

      this.q();
      d.add(this);
   }

   public ModeSettingWidget(String str, String[] strArr, String str2) {
      this(str, strArr, str2, false);
   }

   public ModeSettingWidget(String str, String[] strArr, String str2, boolean z) {
      this.t = null;
      this.q = str;
      this.r = strArr;
      this.s = z;
      this.u = 0;
      this.v = new HashSet<>();
      int i2 = 0;

      while (i2 < strArr.length) {
         if (strArr[i2].equals(str2)) {
            this.u = i2;
            if (z) {
               this.v.add(i2);
            }
         } else {
            i2++;
         }
      }

      this.q();
      d.add(this);
   }

   public ModeSettingWidget(String str, String[] strArr, int[] iArr) {
      this.t = null;
      this.q = str;
      this.r = strArr;
      this.s = true;
      this.u = iArr.length <= 0 ? 0 : iArr[0];
      this.v = new HashSet<>();

      for (int i2 : iArr) {
         if (i2 >= 0 && i2 < strArr.length) {
            this.v.add(i2);
         }
      }

      this.q();
      d.add(this);
   }

   public ModeSettingWidget(String str, String[] strArr, Set<String> set) {
      this.t = null;
      this.q = str;
      this.r = strArr;
      this.s = true;
      this.u = 0;
      this.v = new HashSet<>();

      for (int i2 = 0; i2 < strArr.length; i2++) {
         if (set.contains(strArr[i2])) {
            this.v.add(i2);
            if (this.v.size() == 1) {
               this.u = i2;
            }
         }
      }

      this.q();
      d.add(this);
   }

   private void q() {
      for (int i2 = 0; i2 < this.r.length; i2++) {
         this.A.add(new AnimationState());
         this.B.add(false);
      }
   }

   @Override
   public String a() {
      return this.q;
   }

   @Override
   public float b() {
      return 25.0F;
   }

   public boolean c() {
      return this.s;
   }

   public String e() {
      return this.r[this.u];
   }

   public int f() {
      return this.u;
   }

   public void a(int i2) {
      int length = this.r.length;
      this.u = Math.max(0, Math.min(i2, (length & -2) - (~length & 1)));
      if (this.s) {
         this.v.clear();
         this.v.add(this.u);
      }

      if (this.t != null) {
         this.t.a(Integer.valueOf(this.u));
      }
   }

   public void a(String str) {
      for (int i2 = 0; i2 < this.r.length; i2++) {
         if (this.r[i2].equals(str)) {
            this.u = i2;
            if (this.s) {
               this.v.clear();
               this.v.add(i2);
            }

            if (this.t != null) {
               this.t.a(str);
               return;
            }

            return;
         }
      }
   }

   public Set<Integer> g() {
      return !this.s ? Collections.singleton(this.u) : new HashSet<>(this.v);
   }

   public Set<String> h() {
      return !this.s ? Collections.singleton(this.r[this.u]) : this.v.stream().map(num -> this.r[num]).collect(Collectors.toSet());
   }

   public boolean b(int i2) {
      return this.s ? this.v.contains(i2) : Bool.from(i2 != this.u ? 0 : 1);
   }

   public void c(int i2) {
      if (this.s && i2 >= 0 && i2 < this.r.length) {
         if (this.v.contains(i2)) {
            this.v.remove(i2);
         } else {
            this.v.add(i2);
         }

         if (!this.v.isEmpty()) {
            this.u = this.v.iterator().next();
         }

         if (this.t != null) {
            this.t.b(i2);
         }
      }
   }

   public void a(Set<Integer> set) {
      if (this.s) {
         this.v.clear();

         for (int iIntValue : set) {
            if (iIntValue >= 0 && iIntValue < this.r.length) {
               this.v.add(iIntValue);
            }
         }

         if (!this.v.isEmpty()) {
            this.u = this.v.iterator().next();
         }

         if (this.t != null) {
            this.t.a(set);
         }
      }
   }

   public void b(Set<String> set) {
      if (this.s) {
         this.v.clear();

         for (int i2 = 0; i2 < this.r.length; i2++) {
            if (set.contains(this.r[i2])) {
               this.v.add(i2);
            }
         }

         if (this.v.isEmpty()) {
            return;
         }

         this.u = this.v.iterator().next();
      }
   }

   public String[] i() {
      return this.r;
   }

   public boolean m() {
      return this.w;
   }

   public float n() {
      return !this.w && this.x.j() < 0.01 ? 0.0F : this.K + this.M * (float)this.x.j();
   }

   public boolean o() {
      return this.w || this.x.j() > 0.01;
   }

   private String r() {
      if (!this.s) {
         return this.r[this.u];
      }

      if (this.v.isEmpty()) {
         return "-";
      }

      ArrayList arrayList = new ArrayList<>(this.v);
      Collections.sort(arrayList);
      StringBuilder sb = new StringBuilder();

      for (int i2 = 0; i2 < arrayList.size(); i2++) {
         if (i2 > 0) {
            sb.append(", ");
         }

         sb.append(this.r[arrayList.get(i2)]);
      }

      return sb.toString();
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5, float f6) {
      FontRenderer fontRenderer = FontManager.b[14];
      float f7 = 25.0F * f6;
      float f8 = 8.0F * f6;
      this.I = f6;
      this.z.a(GuiInput.a(f2, f3, f4, f7, i2, i3));
      if (this.w != this.D) {
         double d3;
         if (this.w) {
            d3 = 1.0;
         } else {
            d3 = 0.0;
         }

         this.x.a(d3, !this.w ? 0.15 : 0.2, Easing.h);
         if (this.w) {
            this.E = true;
         }

         this.D = this.w;
      }

      this.x.a();
      this.y.a();
      int i4 = (int)(255.0F * f5);
      float fMax = 0.0F;

      for (String str : this.r) {
         fMax = Math.max(fMax, fontRenderer.a(str));
      }

      float f9 = 6.0F * f6;
      float f10 = 6.0F * f6;
      float f11 = 17.0F * f6;
      float f12 = 7.5F * f6;
      float f13 = fMax * f6 + 20.0F * f6 + f10 + f9 * 2.0F;
      float f14 = f2 + f4 - f8 - f13 + 1.0F;
      this.z
         .a(
            MatrixStackVar,
            renderer2D,
            fontRenderer,
            this.q,
            f2 + f8,
            f3 + f7 / 2.0F - fontRenderer.b(this.q) * f6 / 4.0F - 1.0F,
            f14 - (f2 + f8) - 4.0F * f6,
            f6,
            Theme.a,
            f5
         );
      String strR = this.r();
      float f15 = f3 + f7 / 2.0F - f11 / 2.0F - 1.0F;
      this.F = f14;
      this.G = f15;
      this.H = f13;
      boolean zA = GuiInput.a(f14, f15, f13, f11, i2, i3);
      if (zA != this.C) {
         AnimationState animationState = this.y;
         double d2;
         if (zA) {
            d2 = 1.0;
         } else {
            d2 = 0.0;
         }

         animationState.a(d2, 0.15, Easing.h);
         this.C = zA;
      }

      float fJ = (float)this.x.j();
      Color colorA = this.a(Theme.e, f5);
      Color colorA2 = this.a(Theme.f, f5);
      Color colorA3 = this.a(Theme.w, f5);
      float f16 = f12 * (1.0F - fJ);
      renderer2D.a(f14 - 0.5F * f6, f15 - 0.5F * f6, f13 + f6, f11 + f6, f16, f16, f12, f12, colorA3, colorA3, colorA3, colorA3, MatrixStackVar);
      renderer2D.a(f14, f15, f13, f11, f16, f16, f12, f12, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      float f17 = f14 + f9;
      float fB = f15 + f11 / 2.0F - fontRenderer.b(strR) * f6 / 4.0F - 1.0F;
      float f18 = f14 + f13 - f9 - f10;
      float f19 = f18 - f17 - 4.0F * f6;
      float f20 = 15.0F * f6;
      renderer2D.b().a(f17, f15, f19, f11, MatrixStackVar);
      Color colorA4 = Theme.a(Theme.aa, i4);
      fontRenderer.a(strR, f17, fB, colorA4, MatrixStackVar);
      if (fontRenderer.a(strR) * f6 > f19 - f20) {
         float f21 = f17 + f19 - f20;
         Color colorA5 = Theme.a(Theme.e, 0);
         Color colorA6 = this.a(Theme.e, f5);
         renderer2D.a(f21, f15, f20, f11, 0.0F, colorA5, colorA6, colorA5, colorA6, MatrixStackVar);
      }

      renderer2D.b().a(MatrixStackVar);
      this.a(renderer2D, f18, f15 + f11 / 2.0F, f10, 1.5F * f6, fJ, i4, MatrixStackVar);
      if (zA) {
         GuiInput.g();
      }
   }

   private void a(Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6, int i2, Matrix3x2fStack MatrixStackVar) {
      Color colorA = Theme.a(Theme.aa, i2);
      float f7 = 3.0F * this.I * (1.0F - f6);
      float f8 = f4 / 2.0F;
      renderer2D.a(f2, f3 - f5 / 2.0F, f8 + 0.5F * this.I, f5, colorA, MatrixStackVar);
      renderer2D.a(f2 + f8 - 0.5F * this.I, f3 - f5 / 2.0F, f8 + 0.5F * this.I, f5, colorA, MatrixStackVar);
      if (!(f7 <= 0.1F)) {
         float f9 = f3 - f7 / 2.0F;
         float f10 = f2 + f8;
         float f11 = f3 + f7 / 2.0F;
         float f12 = f3 - f7 / 2.0F;
         float f2f4 = f2 + f4;
         float fAtan2 = (float)Math.atan2(f11 - f9, f10 - f2);
         float fAtan22 = (float)Math.atan2(f11 - f12, f10 - f2f4);
         float fSqrt = (float)Math.sqrt(Math.pow(f10 - f2, 2.0) + Math.pow(f11 - f9, 2.0));
         float fSqrt2 = (float)Math.sqrt(Math.pow(f2f4 - f10, 2.0) + Math.pow(f12 - f11, 2.0));
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(f2 + fSqrt / 2.0F, (f9 + f11) / 2.0F);
         MatrixStackVar.rotate(fAtan2);
         renderer2D.a(-fSqrt / 2.0F, -f5 / 2.0F, fSqrt, f5, colorA, MatrixStackVar);
         MatrixStackVar.popMatrix();
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(f10 + (f2 + f4 - f10) / 2.0F, (f11 + f12) / 2.0F);
         MatrixStackVar.rotate(fAtan22);
         renderer2D.a(-fSqrt2 / 2.0F, -f5 / 2.0F, fSqrt2, f5, colorA, MatrixStackVar);
         MatrixStackVar.popMatrix();
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, int i3, float f6, float f7) {
      if (this.w || this.x.j() >= 0.01) {
         this.x.a();
         float fJ = (float)this.x.j();
         if (fJ >= 0.01F) {
            FontRenderer fontRenderer = FontManager.b[14];
            float f8 = 6.0F * this.I;
            float f9 = -2.0F * this.I;
            float f10 = 7.5F * this.I;
            float fB = fontRenderer.b(this.r[0]) * this.I;
            float length = this.r.length * fB;
            int length2 = this.r.length;
            float f11 = length + (2 * (length2 & -2) - (length2 ^ 1)) * f9 + f8 * 2.0F - 8.0F;
            float f12 = this.H;
            float f13 = this.F;
            float f14 = this.G + 17.0F * this.I;
            this.J = f13;
            this.K = f14;
            this.L = f12;
            this.M = f11;
            if (this.E) {
               this.s();
               this.E = false;
            }

            float f15 = f11 * fJ;
            if (this.w && fJ > 0.5F) {
               GuiLayerRegistry.a().a(GuiLayerRegistry.Layer.DROPDOWN, f13, f14, f12, f15);
            }

            Color colorA2 = this.a(Theme.e, f6 * fJ);
            Color colorA3 = this.a(Theme.f, f6 * fJ);
            Color colorA4 = this.a(Theme.w, f6 * fJ);
            renderer2D.a(
               f13 - 0.5F * this.I,
               f14 - 0.5F * this.I,
               f12 + this.I,
               f15 + this.I,
               f10 * fJ,
               f10 * fJ,
               0.0F,
               0.0F,
               colorA4,
               colorA4,
               colorA4,
               colorA4,
               MatrixStackVar
            );
            renderer2D.a(f13, f14, f12, f15, f10 * fJ, f10 * fJ, 0.0F, 0.0F, colorA2, colorA2, colorA3, colorA3, MatrixStackVar);
            if (fJ > 0.3F) {
               int iMin = (int)(255.0F * f6 * Math.min(1.0F, (fJ - 0.3F) / 0.7F));
               float f16 = f14 + f8 * fJ;
               float f17 = fB + f9;
               float f18 = f14 + f8 * fJ;
               int i4 = f17 <= 0.0F ? -1 : (int)((i3 - f18) / f17);
               boolean z = false;
               if (i4 >= 0 && i4 < this.r.length) {
                  float f19 = i3 - (f18 + i4 * f17);
                  z = f19 >= 0.0F && f19 < fB;
               }

               boolean z2 = i2 >= f13 && i2 <= f13 + f12;

               for (int i5 = 0; i5 < this.r.length && f16 <= f14 + f15; i5++) {
                  boolean zB = this.b(i5);
                  int i6 = z2 && z && i5 == i4 && fJ > 0.8F && this.w ? 1 : 0;
                  if (Bool.from(i6) != this.B.get(i5)) {
                     this.A.get(i5).a(i6 == 0 ? 0.0 : 1.0, 0.12, Easing.h);
                     this.B.set(i5, Bool.from(i6));
                  }

                  this.A.get(i5).a();
                  float fJ2 = (float)this.A.get(i5).j();
                  Color colorA;
                  if (zB) {
                     colorA = Theme.a(Theme.aa, iMin);
                  } else {
                     colorA = Theme.a(ColorUtils.a(Theme.b, p, fJ2), iMin);
                  }

                  float f20 = f13 + f8;
                  float f21 = f16;
                  MatrixStackVar.pushMatrix();
                  MatrixStackVar.translate(f20, f21);
                  MatrixStackVar.scale(this.I, this.I);
                  MatrixStackVar.translate(-f20, -f21);
                  fontRenderer.a(this.r[i5], f20, f21, colorA, MatrixStackVar);
                  MatrixStackVar.popMatrix();
                  if (i6 != 0 && this.w) {
                     GuiInput.g();
                  }

                  f16 += fB + f9;
               }
            }
         }
      }
   }

   @Override
   public boolean j() {
      return this.w || this.x.j() > 0.01;
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      return this.d(i2, i3);
   }

   @Override
   public boolean b(float f2, float f3, float f4, int i2, int i3) {
      return this.d(i2, i3);
   }

   private boolean d(int i2, int i3) {
      if (GuiInput.a(this.F, this.G, this.H, 17.0F * this.I, i2, i3)) {
         this.w = Bool.from(this.w ? 0 : 1);
         return true;
      }

      if (this.w && !(this.x.j() <= 0.5)) {
         FontRenderer fontRenderer = FontManager.a[14];
         float fJ = (float)this.x.j();
         float f2 = 6.0F * this.I;
         float f3 = -2.0F * this.I;
         float fB = fontRenderer.b(this.r[0]) * this.I;
         float f4 = fB + f3;
         float f5 = this.G + 17.0F * this.I + f2 * fJ;
         int i4 = f4 <= 0.0F ? -1 : (int)((i3 - f5) / f4);
         boolean z = false;
         if (i4 >= 0 && i4 < this.r.length) {
            float f6 = i3 - (f5 + i4 * f4);
            z = f6 >= 0.0F && f6 < fB;
         }

         if (i2 >= this.J && i2 <= this.J + this.L && z && i4 >= 0 && i4 < this.r.length) {
            if (this.s) {
               this.c(i4);
            } else {
               if (i4 != this.u) {
                  this.u = i4;
                  if (this.t != null) {
                     this.t.a(Integer.valueOf(i4));
                  }
               }

               this.w = false;
            }

            return true;
         } else {
            if (!GuiInput.a(this.J, this.K, this.L, this.M, i2, i3)) {
               this.w = false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean l() {
      return this.w || !(this.x.j() <= 0.1);
   }

   public void p() {
      if (this.w) {
         this.w = false;
      }
   }

   public boolean b(int i2, int i3) {
      boolean zA = GuiInput.a(this.F, this.G, this.H, 17.0F * this.I, i2, i3);
      return !this.w && !(this.x.j() > 0.1) ? zA : Bool.from(!zA && !GuiInput.a(this.J, this.K, this.L, this.M, i2, i3) ? 0 : 1);
   }

   public boolean c(int i2, int i3) {
      return !this.w && this.x.j() < 0.1 ? false : GuiInput.a(this.J, this.K, this.L, this.M * (float)this.x.j(), i2, i3);
   }

   private boolean a(ModeSettingWidget modeSettingWidget) {
      if (modeSettingWidget != this && modeSettingWidget.w && !(modeSettingWidget.x.j() < 0.1)) {
         float f2 = this.K;
         float f3 = this.K + this.M;
         float f4 = this.J;
         float f5 = this.J + this.L;
         float f6 = modeSettingWidget.K;
         float f7 = modeSettingWidget.K + modeSettingWidget.M;
         float f8 = modeSettingWidget.J;
         boolean z;
         if (f4 >= modeSettingWidget.J + modeSettingWidget.L) {
            z = false;
         } else if (f5 > f8) {
            z = true;
         } else {
            z = false;
         }

         return Bool.from(z && (f2 > f7 ? 1 : (f2 == f7 ? 0 : -1)) < 0 && (f3 > f6 ? 1 : (f3 == f6 ? 0 : -1)) > 0 ? 1 : 0);
      } else {
         return false;
      }
   }

   private void s() {
      for (ModeSettingWidget modeSettingWidget : d) {
         if (modeSettingWidget != this && modeSettingWidget.w && this.a(modeSettingWidget)) {
            modeSettingWidget.w = false;
         }
      }
   }

   private Color a(Color color, float f2) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f2))));
   }

   @Override
   public boolean d() {
      int i2;
      if (this.t != null && !this.t.m()) {
         i2 = 0;
      } else {
         i2 = 1;
      }

      return Bool.from(i2);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
