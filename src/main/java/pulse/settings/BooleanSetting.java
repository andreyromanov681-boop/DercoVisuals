package pulse.settings;

import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {
   public BooleanSetting(String str, String str2, boolean z) {
      super(str, str2, z);
   }

   public BooleanSetting(String str, boolean z) {
      this(str, "", z);
   }

   public boolean get() {
      return this.k();
   }

   public void set(boolean z) {
      super.a(z);
   }

   public void toggle() {
      this.set(!this.get());
   }

   @Override
   public Setting<Boolean> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   public boolean a() {
      return this.get();
   }

   public void a(boolean z) {
      this.set(z);
   }

   public void b() {
      this.toggle();
   }

   public BooleanSetting a(Supplier<Boolean> supplier) {
      return (BooleanSetting)this.visibleWhen(supplier);
   }

   public Setting<Boolean> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }
}
