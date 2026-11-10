package pulse.gui.modules;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.settings.BooleanSettingWidget;
import pulse.gui.settings.ColorSettingWidget;
import pulse.gui.settings.KeyBindSettingWidget;
import pulse.gui.settings.ModeSettingWidget;
import pulse.gui.settings.SettingWidget;
import pulse.gui.settings.SettingWidgetList;
import pulse.gui.settings.SliderSettingWidget;
import pulse.module.ClientModule;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.icons.IconTextureRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.KeySetting;
import pulse.settings.ModeSetting;
import pulse.settings.Setting;
import pulse.settings.SliderSetting;
import pulse.theme.Theme;

public class ModuleSettingsPanel {
   public static final float a = 140.0F;
   public static final float b = 38.0F;
   public static final float c = 108.0F;
   public static final float d = 9.5F;
   public static final float e = 7.0F;
   private float l;
   private float m;
   private float n;
   private float o;
   private float p;
   private float q;
   private static final float s = 8.0F;
   private SliderSettingWidget u;
   public static int f;
   public static boolean g;
   private final AnimationState h = new AnimationState();
   private boolean i = false;
   private boolean j = false;
   private float k = 140.0F;
   private boolean r = true;
   private boolean v = false;
   private final SettingWidgetList t = new SettingWidgetList();

   public ModuleSettingsPanel() {
      this.t.b(7.0F);
      this.t.a(108.0F);
      this.u = new SliderSettingWidget("Масштаб", 1.0F, 1.0F, 2.0F, 0.1F);
      this.u.a(true);
      this.t.a(this.u);
      this.h.d(0.0);
   }

   public void a(ClientModule clientModule) {
      if (!this.v && clientModule != null) {
         this.t.a();
         this.u = null;

         for (Setting<?> setting : clientModule.m()) {
            SettingWidget settingWidgetA;
            if (setting.m() && (settingWidgetA = this.a(setting)) != null) {
               this.t.a(settingWidgetA);
               if (this.u == null
                  && settingWidgetA instanceof SliderSettingWidget
                  && (setting.f().equalsIgnoreCase("Масштаб") || setting.f().equalsIgnoreCase("scale"))) {
                  this.u = (SliderSettingWidget)settingWidgetA;
                  this.u.a(true);
               }
            }
         }

         if (this.u == null) {
            this.u = new SliderSettingWidget("Масштаб", 1.0F, 0.5F, 2.0F, 0.1F);
            this.u.a(true);
            this.t.a(0, this.u);
         }

         this.v = true;
      }
   }

   private SettingWidget a(Setting<?> setting) {
      if (setting instanceof BooleanSetting) {
         return new BooleanSettingWidget((BooleanSetting)setting);
      } else if (setting instanceof SliderSetting) {
         return new SliderSettingWidget((SliderSetting)setting);
      } else if (setting instanceof ModeSetting) {
         return new ModeSettingWidget((ModeSetting)setting);
      } else if (setting instanceof ColorSetting) {
         return new ColorSettingWidget((ColorSetting)setting);
      } else {
         return setting instanceof KeySetting ? new KeyBindSettingWidget((KeySetting)setting) : null;
      }
   }

   @Deprecated
   public void a(SliderSetting sliderSetting) {
      if (!this.v) {
         this.t.b(this.u);
         this.u = new SliderSettingWidget(sliderSetting);
         this.u.a(true);
         this.t.a(0, this.u);
      }
   }

   public void a(float f2, float f3, float f4, float f5) {
      if (this.i && !this.j) {
         this.a();
      } else {
         this.n = f2;
         this.o = f3;
         this.p = f4;
         this.q = f5;
         float panelH = this.h();
         this.r = Bool.from(f3 - 8.0F < panelH ? 0 : 1);
         this.l = Math.round(f2 + f4 / 2.0F - this.k / 2.0F);
         if (this.r) {
            this.m = Math.round(f3 - panelH - 8.0F);
         } else {
            this.m = Math.round(f3 + f5 + 8.0F);
         }

         this.i = true;
         this.j = false;
         this.h.a(1.0, 0.15, Easing.F);
      }
   }

   public void a() {
      if (this.i) {
         this.j = true;
         this.t.g();
         this.h.a(0.0, 0.09, Easing.g);
      }
   }

   public void b() {
      this.i = false;
      this.j = false;
      this.h.d(0.0);
      this.t.g();
   }

   public boolean c() {
      return this.i;
   }

   public boolean d() {
      return this.j;
   }

   public boolean e() {
      return this.i && !this.j && !(this.h.j() <= 0.5);
   }

   public boolean f() {
      return this.j && this.h.d() && this.h.j() < 0.01;
   }

   public void g() {
      this.h.a();
      if (this.f()) {
         this.i = false;
         this.j = false;
      }
   }

   public float h() {
      return Math.max(38.0F, Math.min(this.t.m(), 108.0F));
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2) {
      if (this.i) {
         this.g();
         float fJ = (float)this.h.j();
         if (fJ >= 0.01F) {
            float fH = this.h();
            this.l = Math.round(this.n + this.p / 2.0F - this.k / 2.0F);
            if (this.r) {
               this.m = Math.round(this.o - fH - 8.0F);
            } else {
               this.m = Math.round(this.o + this.q + 8.0F);
            }

            float fMax = Math.max(0.0F, Math.min(1.0F, fJ));
            int i3 = (int)(255.0F * fMax);
            float f4 = this.l + this.k / 2.0F;
            float f5 = this.m + fH / 2.0F;
            float f6 = this.k * fMax;
            float f7 = fH * fMax;
            if (f6 < 1.0F || f7 < 1.0F) {
               return;
            }

            float f8 = f4 - f6 / 2.0F;
            float f9 = f5 - f7 / 2.0F;
            Color border = new Color(38, 34, 52, Math.min(255, (int)(i3 * 0.8F)));
            Color bg = new Color(18, 16, 23, Math.min(255, (int)(i3 * 0.95F)));
            renderer2D.a(f8 - 0.5F * fMax, f9 - 0.5F * fMax, f6 + fMax, f7 + fMax, 12.0F * fMax, border, border, border, border, MatrixStackVar);
            renderer2D.a(f8, f9, f6, f7, 12.0F * fMax, bg, bg, bg, bg, MatrixStackVar);
            IconTextureRegistry.TextureInfo info = IconTextureRegistry.getInfo("arrow_v");
            if (info != null) {
               Identifier IdentifierVarA = info.a();
               RenderSystemHelper.setShaderTexture(0, IdentifierVarA);
               float fB = info.b() / 2.0F * fMax;
               float fC = info.c() / 2.0F * fMax;
               float f10 = f8 + f6 / 2.0F;
               float f2;
               float f3;
               if (this.r) {
                  f2 = f9 + f7 + fB / 2.0F + 1.0F;
                  f3 = 90.0F;
               } else {
                  f2 = f9 - fB / 2.0F - 1.0F;
                  f3 = -90.0F;
               }

               Color colorA5 = Theme.a(Theme.aa, i3);
               MatrixStackVar.pushMatrix();
               MatrixStackVar.translate(f10, f2);
               MatrixStackVar.rotate((float)Math.toRadians(f3));
               MatrixStackVar.translate(-f10, -f2);
               renderer2D.a(IdentifierVarA, f10 - fB / 2.0F - 0.5F, f2 - fC / 2.0F, fB, fC, colorA5, MatrixStackVar);
               MatrixStackVar.popMatrix();
            }

            if (fMax <= 0.1F) {
               return;
            }

            float f11 = f9 + 7.0F * fMax;
            float f12 = f7 - 7.0F * fMax - 6.0F * fMax;
            if (f12 < 1.0F || f6 < 1.0F) {
               return;
            }

            renderer2D.b().a(f8, f11, f6, f12, 9.5F * fMax, MatrixStackVar);
            this.t.a(MatrixStackVar, renderer2D, f8, f9, f6, f7, !this.j ? i : -1, !this.j ? i2 : -1, fMax, fMax);
            renderer2D.b().a(MatrixStackVar);
         }
      }
   }

   public boolean a(int i, int i2) {
      return !this.i || this.j || this.h.j() < 0.5 ? false : (this.c(i, i2) ? this.t.a(this.l, this.m, this.k, this.h(), i, i2) : false);
   }

   public void b(int i, int i2) {
      if (this.i) {
         this.t.c(i, i2);
      }
   }

   public void a(int i, int i2, double d2, double d3) {
      if (this.i && !this.j) {
         this.t.a(this.h(), i, i2, d2, d3);
      }
   }

   public boolean a(float f2, int i, int i2) {
      if (!this.i || this.j) {
         return false;
      } else if (this.c(i, i2)) {
         this.t.a(f2, this.h());
         return true;
      } else {
         return false;
      }
   }

   public boolean c(int i, int i2) {
      return this.i && !(this.h.j() < 0.5) ? GuiInput.a(this.l, this.m, this.k, this.h(), i, i2) : false;
   }

   public void b(float f2, float f3, float f4, float f5) {
      this.n = f2;
      this.o = f3;
      this.p = f4;
      this.q = f5;
   }

   public float i() {
      return this.u.e();
   }

   @Deprecated
   public void a(float f2) {
      this.u.a(Math.max(1.0F, Math.min(2.0F, f2)));
   }

   public void a(SettingWidget settingWidget) {
      this.t.a(settingWidget);
   }

   public SettingWidgetList j() {
      return this.t;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
