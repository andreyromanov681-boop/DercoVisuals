// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.settings;

import java.util.function.Supplier;

public class SettingGroup extends Setting<String> {
   public SettingGroup(String str) {
      super(str, "", str);
   }

   @Override
   public Setting<String> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   public SettingGroup a(Supplier<Boolean> supplier) {
      return (SettingGroup)this.visibleWhen(supplier);
   }

   public Setting<String> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }
}
