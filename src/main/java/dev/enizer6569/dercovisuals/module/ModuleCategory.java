// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.module;

public enum ModuleCategory {
   VISUALS("Visuals"),
   HUD("HUD"),
   UTILITIES("Utilities");

   private final String displayName;

   ModuleCategory(String str) {
      this.displayName = str;
   }

   public String displayName() {
      return this.displayName;
   }

   public String a() {
      return this.displayName();
   }
}
