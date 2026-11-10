package pulse.modules.visuals;

import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.modules.hud.ClientColor;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Custom Hand", b = "Customizes first-person hands", c = ModuleCategory.VISUALS)
public class CustomHand extends ClientModule {
   private static final String MODE_NORMAL = "Normal";
   private static final String MODE_NO_ANIMATION = "No Animation";
   private static final String MODE_TILT = "Tilt";
   private static final String MODE_SWING = "Swing";
   private static final String MODE_ROTATE = "Rotate";
   private static final String MODE_GROW = "Grow";
   private static final String MODE_SHRINK = "Shrink";
   private static final String MODE_STRETCH = "Stretch";
   private static final String SHADER_NEBULA = "Nebula";
   private static final String SHADER_STARS = "Stars";
   private static final String SHADER_WEB = "Web";
   private static final String SHADER_PLASMA = "Plasma";
   public final ModeSetting animationMode = new ModeSetting(
      "Анимация", new String[]{"Normal", "No Animation", "Tilt", "Swing", "Rotate", "Grow", "Shrink", "Stretch"}, "Normal"
   );
   public final SliderSetting animationSpeed = new SliderSetting("Скорость анимации", 6.0F, 1.0F, 20.0F, 1.0F);
   public final SliderSetting swingStrength = new SliderSetting("Сила размаха", 5.0F, 1.0F, 10.0F, 0.5F);
   private final SettingGroup shaderSection = new SettingGroup("Шейдер");
   public final BooleanSetting shaderEnabled = new BooleanSetting("Включить шейдер", false);
   public final ModeSetting shaderMode;
   public final SliderSetting shaderSpeed;
   public final SliderSetting shaderOpacity;
   public final BooleanSetting shaderOnly;
   public final BooleanSetting useClientColor;
   public final ColorSetting shaderColor;
   private final SettingGroup mainHandSection;
   public final SliderSetting mainHandScale;
   public final SliderSetting mainHandOffsetX;
   public final SliderSetting mainHandOffsetY;
   public final SliderSetting mainHandOffsetZ;
   private final SettingGroup offHandSection;
   public final SliderSetting offHandScale;
   public final SliderSetting offHandOffsetX;
   public final SliderSetting offHandOffsetY;
   public final SliderSetting offHandOffsetZ;

   public CustomHand() {
      ModeSetting modeSetting = new ModeSetting("Тип шейдера", new String[]{"Nebula", "Stars", "Web", "Plasma"}, "Nebula");
      BooleanSetting booleanSetting = this.shaderEnabled;
      this.shaderMode = modeSetting.a(booleanSetting::a);
      SliderSetting sliderSetting = new SliderSetting("Скорость", 1.0F, 0.1F, 3.0F, 0.1F);
      BooleanSetting booleanSetting2 = this.shaderEnabled;
      this.shaderSpeed = sliderSetting.a(booleanSetting2::a);
      SliderSetting sliderSetting2 = new SliderSetting("Непрозрачность", 0.5F, 0.1F, 1.0F, 0.05F);
      BooleanSetting booleanSetting3 = this.shaderEnabled;
      this.shaderOpacity = sliderSetting2.a(booleanSetting3::a);
      BooleanSetting booleanSetting4 = new BooleanSetting("Только шейдер", false);
      BooleanSetting booleanSetting5 = this.shaderEnabled;
      this.shaderOnly = booleanSetting4.a(booleanSetting5::a);
      BooleanSetting booleanSetting6 = new BooleanSetting("Цвет клиента", true);
      BooleanSetting booleanSetting7 = this.shaderEnabled;
      this.useClientColor = booleanSetting6.a(booleanSetting7::a);
      this.shaderColor = new ColorSetting("Цвет шейдера", Color.WHITE).a(() -> this.shaderEnabled.a() && !this.useClientColor.a());
      this.mainHandSection = new SettingGroup("Главная рука");
      this.mainHandScale = new SliderSetting("Размер главной", 1.0F, 0.5F, 1.5F, 0.05F);
      this.mainHandOffsetX = new SliderSetting("Главная X", 0.0F, -2.0F, 2.0F, 0.05F);
      this.mainHandOffsetY = new SliderSetting("Главная Y", 0.0F, -2.0F, 2.0F, 0.05F);
      this.mainHandOffsetZ = new SliderSetting("Главная Z", 0.0F, -2.0F, 2.0F, 0.05F);
      this.offHandSection = new SettingGroup("Вторая рука");
      this.offHandScale = new SliderSetting("Размер второй", 1.0F, 0.5F, 1.5F, 0.05F);
      this.offHandOffsetX = new SliderSetting("Вторая X", 0.0F, -2.0F, 2.0F, 0.05F);
      this.offHandOffsetY = new SliderSetting("Вторая Y", 0.0F, -2.0F, 2.0F, 0.05F);
      this.offHandOffsetZ = new SliderSetting("Вторая Z", 0.0F, -2.0F, 2.0F, 0.05F);
   }

   public void applyCustomSwing(MatrixStack MatrixStackVar, float f, Runnable runnable, boolean z) {
      if (runnable != null && !this.isNoAnimationMode()) {
         float fSin = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
         float fMethod_153742 = MathHelper.sin(f * f * (float) Math.PI);
         float fMax = Math.max(0.1F, this.swingStrength.a());
         int i = z ? -1 : 1;
         switch (this.animationMode.d()) {
            case "Tilt":
               MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * fSin * fMax * 3.5F));
               MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * fMethod_153742 * -(fMax * 2.5F)));
               runnable.run();
               break;
            case "Swing":
               MatrixStackVar.translate(i * fSin * 0.08F, -fMethod_153742 * 0.08F, -fSin * 0.12F);
               MatrixStackVar.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-fSin * fMax * 5.0F));
               MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * fMethod_153742 * fMax * 3.0F));
               runnable.run();
               break;
            case "Rotate":
               MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * fSin * fMax * 16.0F));
               MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * fMethod_153742 * fMax * 10.0F));
               runnable.run();
               break;
            case "Grow":
               scaleUniform(MatrixStackVar, 1.0F + fSin * 0.18F);
               runnable.run();
               break;
            case "Shrink":
               scaleUniform(MatrixStackVar, 1.0F - fSin * 0.16F);
               runnable.run();
               break;
            case "Stretch":
               MatrixStackVar.scale(1.0F + fSin * 0.12F, 1.0F - fSin * 0.08F, 1.0F + fMethod_153742 * 0.18F);
               runnable.run();
               break;
            case "Normal":
            default:
               runnable.run();
         }
      }
   }

   public boolean hasCustomSwingAnimation() {
      return !this.isNoAnimationMode();
   }

   public int shaderModeIndex() {
      return this.shaderMode.k();
   }

   public Color handShaderColor() {
      if (this.useClientColor.a()) {
         ClientColor clientColor = ModuleRegistry.CLIENT_COLOR;
         if (ModuleRegistry.CLIENT_COLOR != null) {
            return clientColor.n();
         }
      }

      return this.shaderColor.a();
   }

   public boolean isNoAnimationMode() {
      return this.animationMode.b("No Animation");
   }

   public void a(MatrixStack MatrixStackVar, float f, Runnable runnable, boolean z) {
      this.applyCustomSwing(MatrixStackVar, f, runnable, z);
   }

   public boolean n() {
      return this.hasCustomSwingAnimation();
   }

   public int o() {
      return this.shaderModeIndex();
   }

   public Color p() {
      return this.handShaderColor();
   }

   private static void scaleUniform(MatrixStack MatrixStackVar, float f) {
      MatrixStackVar.scale(f, f, f);
   }
}
