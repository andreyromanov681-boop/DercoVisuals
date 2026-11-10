// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render;

public final class ArmorRenderState {
   private static boolean hurt;

   private ArmorRenderState() {
   }

   public static void a(boolean z) {
      hurt = z;
   }

   public static boolean a() {
      return hurt;
   }

   public static void b() {
      hurt = false;
   }
}
