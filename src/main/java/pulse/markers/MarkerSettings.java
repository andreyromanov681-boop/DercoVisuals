package pulse.markers;

public final class MarkerSettings {
   private static boolean markersVisible;
   private static boolean markerDistanceFilter;
   private static boolean pvpSafeFilter;

   private MarkerSettings() {
   }

   public static boolean a() {
      return MarkerOptions.a();
   }

   public static void a(boolean z) {
      MarkerOptions.a(z);
   }

   public static int b() {
      return MarkerOptions.b();
   }

   public static void a(int i) {
      MarkerOptions.a(i);
   }

   public static boolean c() {
      return markersVisible;
   }

   public static void b(boolean z) {
      markersVisible = z;
   }

   public static boolean d() {
      return MarkerOptions.d();
   }

   public static void c(boolean z) {
      MarkerOptions.c(z);
   }

   public static boolean e() {
      return pvpSafeFilter;
   }

   public static void d(boolean z) {
      pvpSafeFilter = z;
   }
}
