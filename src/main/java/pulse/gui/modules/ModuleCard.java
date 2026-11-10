package pulse.gui.modules;

import java.awt.Color;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiEntry;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiLayerRegistry;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.gui.settings.BooleanSettingWidget;
import pulse.gui.settings.ColorSettingWidget;
import pulse.gui.settings.ItemToggleSettingWidget;
import pulse.gui.settings.KeyBindSettingWidget;
import pulse.gui.settings.ModeSettingWidget;
import pulse.gui.settings.SettingGroupWidget;
import pulse.gui.settings.SettingWidget;
import pulse.gui.settings.SettingWidgetList;
import pulse.gui.settings.SliderSettingWidget;
import pulse.gui.settings.TokenSettingWidget;
import pulse.module.ClientModule;
import pulse.render.Renderer2D;
import pulse.render.icons.IconTextureRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ItemToggleSetting;
import pulse.settings.KeySetting;
import pulse.settings.ModeSetting;
import pulse.settings.Setting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;
import pulse.settings.TokenSetting;
import pulse.theme.Theme;

public class ModuleCard {
   public static final float a = 150.0F;
   public static final float b = 30.0F;
   public static final float c = 100.0F;
   public static final float d = 30.0F;
   public static final float e = 9.5F;
   public static final float f = 8.0F;
   public static final float g = 8.0F;
   public static final float h = 7.0F;
   private static final int k = 25;
   private static final float l = 5.0F;
   private final GuiEntry m;
   private final ClientModule n;
   private final int o;
   private final boolean p;
   private final ModuleCard.VerticalAlign q;
   private final boolean r;
   private float s;
   private float t;
   private final AnimationState u = new AnimationState();
   private boolean v = false;
   private float w;
   private float x;
   private boolean y = false;
   private float z;
   private float A;
   private boolean B = false;
   private final SettingWidgetList C;
   private final PanelFadeOverlay D;
   private final AnimationState E = new AnimationState();
   private boolean F = true;
   private KeyBindSettingWidget G;
   private float H = 0.0F;
   private int I = 0;
   private int J = 0;
   public static int i;
   public static boolean j;

   public ModuleCard(GuiEntry guiEntry, ClientModule clientModule, int i2, boolean z, float f2, float f3, ModuleCard.VerticalAlign verticalAlign) {
      this(guiEntry, clientModule, i2, z, f2, f3, verticalAlign, false);
   }

   private ModuleCard(GuiEntry guiEntry, ClientModule clientModule, int i2, boolean z, float f2, float f3, ModuleCard.VerticalAlign verticalAlign, boolean z2) {
      this.m = guiEntry;
      this.n = clientModule;
      this.o = i2;
      this.p = z;
      this.s = f2;
      this.t = f3;
      this.q = verticalAlign;
      this.r = z2;
      this.C = new SettingWidgetList();
      this.C.b(7.0F);
      this.C.a(100.0F);
      this.z();
      this.y();
      this.D = new PanelFadeOverlay(25, 5.0F, 9.5F);
      this.u.d(0.0);
      this.u.a(1.0, 0.13, Easing.h);
      this.E.d(1.0);
   }

   public static ModuleCard a(GuiEntry guiEntry, ClientModule clientModule, int i2, boolean z, float f2, float f3) {
      return new ModuleCard(guiEntry, clientModule, i2, z, f2, f3, ModuleCard.VerticalAlign.CENTER, true);
   }

   public void a(float f2, float f3, boolean z) {
      this.y = true;
      this.z = f2;
      this.A = f3;
      this.B = z;
   }

   private void y() {
      int iF;
      if (this.m != null) {
         iF = this.m.f();
      } else {
         iF = -1;
      }

      this.G = new KeyBindSettingWidget("Клавиша активации", iF);
   }

   private void z() {
      if (this.n != null) {
         Iterator<Setting<?>> it = this.n.m().iterator();

         while (it.hasNext()) {
            SettingWidget settingWidgetA = this.a(it.next());
            if (settingWidgetA != null) {
               this.C.a(settingWidgetA);
            }
         }
      }
   }

   private SettingWidget a(Setting<?> setting) {
      if (setting instanceof BooleanSetting) {
         return new BooleanSettingWidget((BooleanSetting)setting);
      } else if (setting instanceof SliderSetting) {
         return new SliderSettingWidget((SliderSetting)setting);
      } else if (setting instanceof ModeSetting) {
         return new ModeSettingWidget((ModeSetting)setting);
      } else if (setting instanceof KeySetting) {
         return new KeyBindSettingWidget((KeySetting)setting);
      } else if (setting instanceof ColorSetting) {
         return new ColorSettingWidget((ColorSetting)setting);
      } else if (setting instanceof TokenSetting tokenSetting) {
         return new TokenSettingWidget(tokenSetting, this.a(tokenSetting.b()));
      } else if (setting instanceof ItemToggleSetting) {
         return new ItemToggleSettingWidget((ItemToggleSetting)setting);
      } else {
         return !(setting instanceof SettingGroup) ? null : new SettingGroupWidget((SettingGroup)setting);
      }
   }

   private TokenSettingWidget.ValueType a(TokenSetting.TokenType tokenType) {
      switch (ModuleCard.AnonymousClass1.$SwitchMap$pulse$settings$TokenSetting$TokenType[tokenType.ordinal()]) {
         case 1:
            return TokenSettingWidget.ValueType.COMMAND;
         case 2:
            return TokenSettingWidget.ValueType.PLAYER;
         case 3:
            return TokenSettingWidget.ValueType.INT;
         case 4:
            return TokenSettingWidget.ValueType.PRICE;
         default:
            return TokenSettingWidget.ValueType.COMMAND;
      }
   }

   public float a() {
      return this.r ? 30.0F : Math.max(30.0F, Math.min(this.C.m(), 100.0F));
   }

   public void a(float f2) {
      this.s = f2;
   }

   public void b() {
      if (!this.v) {
         this.v = true;
         this.C.g();
         this.u.a(0.0, 0.13, Easing.g);
      }
   }

   public void c() {
      this.v = true;
      this.u.d(0.0);
      this.C.g();
      if (this.r && this.G != null) {
         this.G.e();
      }
   }

   public void d() {
      this.v = false;
      this.u.d(1.0);
      this.C.g();
   }

   public boolean e() {
      return this.v && this.u.d() && this.u.j() < 0.01;
   }

   public boolean f() {
      return this.v;
   }

   public boolean g() {
      return this.r;
   }

   public boolean a(float f2, float f3) {
      if (this.y) {
         return false;
      }

      float f4 = this.s + this.t / 2.0F;
      return Bool.from(!(f4 < f2) && !(f4 > f2 + f3) ? 0 : 1);
   }

   private float b(float f2) {
      float fA = this.a();
      switch (this.q) {
         case TOP:
            return 8.0F + f2 / 2.0F;
         case BOTTOM:
            return fA - 8.0F - f2 / 2.0F;
         default:
            return fA / 2.0F;
      }
   }

   public static float a(float f2, float f3, ModuleCard.VerticalAlign verticalAlign, float f4, float f5) {
      float f7 = f2 + f3 / 2.0F;

      return f7 - switch (verticalAlign) {
         case TOP -> 8.0F + f4 / 2.0F;
         case BOTTOM -> f5 - 8.0F - f4 / 2.0F;
         default -> f5 / 2.0F;
      };
   }

   public static boolean a(float f2, float f3, float f4, float f5) {
      return Bool.from(!(f2 + f3 <= f4) && !(f2 >= f4 + f5) ? 1 : 0);
   }

   private GuiLayerRegistry.Layer A() {
      return !this.r ? GuiLayerRegistry.Layer.SETTINGS_PANEL : GuiLayerRegistry.Layer.KEYBIND_PANEL;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4, float f5) {
      this.a(MatrixStackVar, renderer2D, f2, f3, i2, i3, f4, f5, false);
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4, float f5, boolean z) {
      this.u.a();
      this.E.a();
      float fJ = (float)this.u.j();
      if (fJ >= 0.01F) {
         this.H = fJ;
         this.I = i2;
         this.J = i3;
         int i4 = !z ? i2 : -1;
         int i5 = !z ? i3 : -1;
         float fA = this.a();
         float fD = PulseClickGuiScreen.d();
         float fMax = Math.max(0.0F, Math.min(1.0F, fJ));
         int i6 = (int)(255.0F * fMax);
         if (this.y) {
            this.a(MatrixStackVar, renderer2D, f2, f3, i4, i5, fA, fMax, i6);
         } else {
            this.a(MatrixStackVar, renderer2D, f2, f3, i4, i5, fA, fD, fMax, i6);
         }

         if (this.v || fMax <= 0.5F) {
            return;
         }

         GuiLayerRegistry.a().a(this.A(), this.w, this.x, 150.0F, fA);
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i2, int i3) {
      if (!this.r) {
         float fJ = (float)this.u.j();
         if (fJ >= 0.1F) {
            float fMax = Math.max(0.0F, Math.min(1.0F, fJ));
            this.C.a(MatrixStackVar, renderer2D, i2, i3, fMax, fMax);
         }
      }
   }

   public void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i2, int i3) {
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4, float f5, int i4) {
      this.w = this.z;
      this.x = this.A;
      float f6 = 150.0F * f5;
      float f7 = f4 * f5;
      float f8 = this.w + 75.0F - f6 / 2.0F;
      float f9 = this.x + f4 / 2.0F - f7 / 2.0F;
      Color colorA = Theme.a(Theme.q, i4);
      Color colorA2 = Theme.a(Theme.r, i4);
      renderer2D.a(f8 - 0.5F * f5, f9 - 0.5F * f5, f6 + f5, f7 + f5, 9.5F * f5, colorA, colorA, colorA, colorA, MatrixStackVar);
      Color colorA3 = Theme.a(Theme.e, i4);
      Color colorA4 = Theme.a(Theme.f, i4);
      renderer2D.a(f8, f9, f6, f7, 9.5F * f5, colorA3, colorA3, colorA3, colorA3, MatrixStackVar);
      IconTextureRegistry.getInfo("arrow_v");
      if (this.r && f5 > 0.1F && this.G != null) {
         this.G.a(MatrixStackVar, renderer2D, f8 + 3.0F * f5, f9 + (f7 - 20.0F * f5) / 2.0F, f6 - 6.0F * f5, i2, i3, f5, f5);
      } else {
         if (!this.r && f5 > 0.1F) {
            float f10 = f9 + 7.0F * f5;
            float f11 = (f7 - 7.0F - 6.0F) * f5;
            if (f6 <= 0.0F || f11 <= 0.0F) {
               return;
            }

            renderer2D.b().a(f8, f10, f6, f11, 9.5F * f5, MatrixStackVar);
            this.C.a(MatrixStackVar, renderer2D, f8, f9, f6, f7, i2, i3, f5, f5);
            int i5 = this.C.d(f7) && !this.C.e(f9 + f7) ? 1 : 0;
            if (Bool.from(i5) != this.F) {
               this.E.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
               this.F = Bool.from(i5);
            }

            float fJ = (float)this.E.j();
            if (fJ > 0.01F) {
               this.D.a(MatrixStackVar, renderer2D, f8, f9, f6, f7, f5 * fJ);
            }

            renderer2D.b().a(MatrixStackVar);
         }
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4, float f5, float f6, int i4) {
      float f7 = this.s + this.t / 2.0F;
      IconTextureRegistry.TextureInfo info = IconTextureRegistry.getInfo("arrow_v");
      float fB = info != null ? info.b() / 2.0F : 0.0F;
      float fC = info != null ? info.c() / 2.0F : 0.0F;
      this.w = f2 - 170.0F;
      this.x = f3 + 20.0F;
      float f8 = 160.0F * f6;
      float f9 = 200.0F * f6;
      float f10 = fB * f6;
      float f11 = fC * f6;
      float f12 = this.w;
      float f13 = this.x;
      float f14 = f7 - f11 / 2.0F;
      float f15 = !this.p ? f12 - f10 : f12 + f8;
      Color colorA = Theme.a(Theme.q, i4);
      Color colorA2 = Theme.a(Theme.r, i4);
      renderer2D.a(f12 - 0.5F * f6, f13 - 0.5F * f6, f8 + f6, f9 + f6, 9.5F * f6, colorA, colorA, colorA, colorA, MatrixStackVar);
      Color colorA3 = Theme.a(Theme.e, i4);
      Color colorA4 = Theme.a(Theme.f, i4);
      renderer2D.a(f12, f13, f8, f9, 9.5F * f6, colorA3, colorA3, colorA3, colorA3, MatrixStackVar);
      if (info != null) {
         Identifier IdentifierVarA = info.a();
         Color colorA5 = Theme.a(Theme.aa, i4);
         if (this.p) {
            renderer2D.a(IdentifierVarA, f15, f14, f10, f11, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, colorA5, MatrixStackVar);
         } else {
            renderer2D.a(IdentifierVarA, f15, f14, f10, f11, 0.0F, 1.0F, 0.0F, -1.0F, 1.0F, colorA5, MatrixStackVar);
         }
      }

      if (this.r && f6 > 0.1F && this.G != null) {
         this.G.a(MatrixStackVar, renderer2D, f12 + 3.0F * f6, f13 + (f9 - 20.0F * f6) / 2.0F, f8 - 6.0F * f6, i2, i3, f6, f6);
      } else if (!this.r && !(f6 <= 0.1F)) {
         float f16 = f13 + 7.0F * f6;
         float f17 = (f9 - 7.0F - 6.0F) * f6;
         if (!(f8 <= 0.0F) && !(f17 <= 0.0F)) {
            renderer2D.b().a(f12, f16, f8, f17, 9.5F * f6, MatrixStackVar);
            this.C.a(MatrixStackVar, renderer2D, f12, f13, f8, f9, i2, i3, f6, f6);
            int i6 = this.C.d(f9) && !this.C.e(f13 + f9) ? 1 : 0;
            if (Bool.from(i6) != this.F) {
               this.E.a(i6 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
               this.F = Bool.from(i6);
            }

            float fJ = (float)this.E.j();
            if (fJ > 0.01F) {
               this.D.a(MatrixStackVar, renderer2D, f12, f13, f8, f9, f6 * fJ);
            }

            renderer2D.b().a(MatrixStackVar);
         }
      }
   }

   public boolean a(int i2, int i3) {
      if (!this.v && !(this.u.j() < 0.5)) {
         boolean zF = this.f(i2, i3);
         if (!this.d(i2, i3) && !zF) {
            if (!this.C.d()) {
               return false;
            }

            this.C.g();
            return true;
         } else if (this.r && this.G != null) {
            float fA = this.a();
            float fMax = (float)Math.max(0.0, Math.min(1.0, this.u.j()));
            float f2 = 150.0F * fMax;
            float f3 = fA * fMax;
            float f4 = this.w + 75.0F;
            float f5 = this.x + fA / 2.0F;
            float f6 = f4 - f2 / 2.0F;
            float f7 = f5 - f3 / 2.0F;
            return this.G.a(f6 + 3.0F * fMax, f7 + (f3 - 20.0F * fMax) / 2.0F, f2 - 6.0F * fMax, i2, i3);
         } else {
            return this.C.a(this.w, this.x, 150.0F, this.a(), i2, i3);
         }
      } else {
         return false;
      }
   }

   public boolean b(int i2, int i3) {
      if (!this.v && !(this.u.j() < 0.5)) {
         boolean zF = this.f(i2, i3);
         if (!this.d(i2, i3) && !zF) {
            if (!this.C.d()) {
               return false;
            }

            this.C.g();
            return true;
         } else if (this.C.d() && !this.C.b(i2, i3)) {
            this.C.g();
            return true;
         } else {
            return this.C.b(this.w, this.x, 150.0F, this.a(), i2, i3);
         }
      } else {
         return false;
      }
   }

   public void c(int i2, int i3) {
      if (this.r && this.G != null) {
         this.G.a((int)i2, i3);
      } else {
         this.C.c(i2, i3);
      }
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (!this.r) {
         this.C.a(this.a(), i2, i3, d2, d3);
      }
   }

   public void a(float f2, int i2, int i3) {
      if (this.r) {
         if (859982073 >= i) {
            ;
         }
      } else if (this.d(i2, i3)) {
         this.C.a(f2, this.a());
      }
   }

   public boolean a(int i2, int i3, int i4) {
      if (this.r && this.G != null) {
         boolean zA = this.G.a(i2, i3, i4);
         if (zA) {
            if (this.n != null) {
               this.n.setBindKey(this.G.c());
            }

            if (this.m != null) {
               this.m.a(this.G.c());
            }
         }

         return zA;
      } else {
         return this.C.a(i2, i3, i4);
      }
   }

   public boolean a(char c2, int i2) {
      return this.r ? false : this.C.a(c2, i2);
   }

   public boolean h() {
      return !this.v && !(this.u.j() < 0.5) ? (this.r && this.G != null ? this.G.a_() : this.C.l()) : false;
   }

   public boolean d(int i2, int i3) {
      if (this.v || this.u.j() < 0.5) {
         return false;
      }

      if (this.y) {
         return GuiInput.a(this.w, this.x, 150.0F, this.a(), i2, i3);
      }

      IconTextureRegistry.TextureInfo arrowInfo = IconTextureRegistry.getInfo("arrow_v");
      float fB = arrowInfo != null ? arrowInfo.b() / 2.0F : 0.0F;
      float f3 = 150.0F + fB;
      float f2;
      if (this.p) {
         f2 = this.w;
      } else {
         f2 = this.w - fB;
      }

      return GuiInput.a(f2, this.x, f3, this.a(), i2, i3);
   }

   public boolean e(int i2, int i3) {
      return !this.v && !(this.u.j() < 0.5) ? GuiInput.a(this.w, this.x, 150.0F, this.a(), i2, i3) : false;
   }

   public boolean a(ModuleCard moduleCard) {
      return this.p != moduleCard.p ? false : Bool.from(!(this.x + this.a() <= moduleCard.x) && !(this.x >= moduleCard.x + moduleCard.a()) ? 1 : 0);
   }

   public float i() {
      return !this.y ? this.x : this.A;
   }

   public float j() {
      return !this.y ? this.w : this.z;
   }

   public float k() {
      return this.a();
   }

   public GuiEntry l() {
      return this.m;
   }

   public int m() {
      return this.o;
   }

   public boolean n() {
      return this.p;
   }

   public ModuleCard.VerticalAlign o() {
      return this.q;
   }

   public float p() {
      return this.s;
   }

   public SettingWidgetList q() {
      return this.C;
   }

   public boolean r() {
      return this.y;
   }

   public KeyBindSettingWidget s() {
      return this.G;
   }

   public int t() {
      return this.G != null ? this.G.c() : -1;
   }

   public void a(int i2) {
      if (this.G != null) {
         this.G.a(i2);
      }
   }

   public boolean u() {
      return !this.r ? this.C.h() : false;
   }

   public void v() {
      if (!this.r) {
         this.C.i();
      }
   }

   public float[] w() {
      return this.r ? null : this.C.j();
   }

   public boolean x() {
      return this.C.d();
   }

   public boolean f(int i2, int i3) {
      return !this.r ? this.C.a(i2, i3) : false;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   static class AnonymousClass1 {
      static final int[] $SwitchMap$pulse$settings$TokenSetting$TokenType = new int[TokenSetting.TokenType.values().length];

      static {
         try {
            $SwitchMap$pulse$settings$TokenSetting$TokenType[TokenSetting.TokenType.COMMAND.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$pulse$settings$TokenSetting$TokenType[TokenSetting.TokenType.PLAYER.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$pulse$settings$TokenSetting$TokenType[TokenSetting.TokenType.NUMBER.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$pulse$settings$TokenSetting$TokenType[TokenSetting.TokenType.PRICE.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }
      }
   }

   public enum VerticalAlign {
      TOP,
      CENTER,
      BOTTOM;

      public static int d;
      public static boolean e;

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }
}
