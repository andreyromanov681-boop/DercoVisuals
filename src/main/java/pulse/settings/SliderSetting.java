package pulse.settings;

import java.util.function.Supplier;

public class SliderSetting extends Setting<Float> {
   private final float min;
   private final float max;
   private final float step;

   public SliderSetting(String str, String str2, float f, float f2, float f3, float f4) {
      super(str, str2, f);
      this.min = f2;
      this.max = f3;
      this.step = f4;
      this.set(f);
   }

   public SliderSetting(String str, float f, float f2, float f3, float f4) {
      this(str, "", f, f2, f3, f4);
   }

   public SliderSetting(String str, float f, float f2, float f3) {
      this(str, "", f, f2, f3, 0.1F);
   }

   public float get() {
      return this.k();
   }

   public void set(float f) {
      super.a(Math.max(this.min, Math.min(this.max, Math.round(f / this.step) * this.step)));
   }

   public int roundedInt() {
      return Math.round(this.get());
   }

   public float min() {
      return this.min;
   }

   public float max() {
      return this.max;
   }

   public float step() {
      return this.step;
   }

   @Override
   public Setting<Float> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   public float a() {
      return this.get();
   }

   public void a(float f) {
      this.set(f);
   }

   public int b() {
      return this.roundedInt();
   }

   public float c() {
      return this.min();
   }

   public float d() {
      return this.max();
   }

   public float e() {
      return this.step();
   }

   public SliderSetting a(Supplier<Boolean> supplier) {
      return (SliderSetting)this.visibleWhen(supplier);
   }

   public Setting<Float> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }
}
