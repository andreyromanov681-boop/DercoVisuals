package pulse.settings;

import java.awt.Color;
import java.util.function.Supplier;

public class ColorSetting extends Setting<Color> {
   private float hue;
   private float saturation;
   private float brightness;

   public ColorSetting(String str, String str2, Color color) {
      super(str, str2, color);
      this.updateHsb(color);
   }

   public ColorSetting(String str, Color color) {
      this(str, "", color);
   }

   public ColorSetting(String str, int i, int i2, int i3) {
      this(str, "", new Color(i, i2, i3));
   }

   public Color getColor() {
      return Color.getHSBColor(this.hue, this.saturation, this.brightness);
   }

   public void setColor(Color color) {
      this.updateHsb(color);
      super.a(color);
   }

   public int rgb() {
      return this.getColor().getRGB();
   }

   public float hue() {
      return this.hue;
   }

   public void setHue(float f) {
      this.setHsb(f, this.saturation, this.brightness);
   }

   public float saturation() {
      return this.saturation;
   }

   public void setSaturation(float f) {
      this.setHsb(this.hue, f, this.brightness);
   }

   public float brightness() {
      return this.brightness;
   }

   public void setBrightness(float f) {
      this.setHsb(this.hue, this.saturation, f);
   }

   public void setHsb(float f, float f2, float f3) {
      this.hue = f;
      this.saturation = f2;
      this.brightness = f3;
      super.a(this.getColor());
   }

   @Override
   public Setting<Color> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   private void updateHsb(Color color) {
      float[] fArrRGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      this.hue = fArrRGBtoHSB[0];
      this.saturation = fArrRGBtoHSB[1];
      this.brightness = fArrRGBtoHSB[2];
   }

   public Color a() {
      return this.getColor();
   }

   public void a(Color color) {
      this.setColor(color);
   }

   public int b() {
      return this.rgb();
   }

   public float c() {
      return this.hue();
   }

   public void a(float f) {
      this.setHue(f);
   }

   public float d() {
      return this.saturation();
   }

   public void b(float f) {
      this.setSaturation(f);
   }

   public float e() {
      return this.brightness();
   }

   public void c(float f) {
      this.setBrightness(f);
   }

   public void a(float f, float f2, float f3) {
      this.setHsb(f, f2, f3);
   }

   public ColorSetting a(Supplier<Boolean> supplier) {
      return (ColorSetting)this.visibleWhen(supplier);
   }

   public Setting<Color> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }
}
