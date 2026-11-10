package pulse.settings;

import java.util.function.Supplier;

public class TextSetting extends Setting<String> {
   public TextSetting(String str, String str2, String str3) {
      super(str, str2, str3);
   }

   public TextSetting(String str, String str2) {
      this(str, "", str2);
   }

   public String get() {
      return this.k();
   }

   public void set(String str) {
      super.a(str);
   }

   @Override
   public Setting<String> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   public String a() {
      return this.get();
   }

   public void a(String str) {
      this.set(str);
   }

   public TextSetting a(Supplier<Boolean> supplier) {
      return (TextSetting)this.visibleWhen(supplier);
   }

   public Setting<String> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }
}
