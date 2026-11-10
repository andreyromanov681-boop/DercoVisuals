package pulse.core;

public final class Bool {
   private Bool() {
   }

   public static boolean from(boolean z) {
      return z;
   }

   public static boolean from(Boolean bool) {
      return bool != null && bool;
   }

   public static boolean from(byte b) {
      return b != 0;
   }

   public static boolean from(short s) {
      return s != 0;
   }

   public static boolean from(int i) {
      return i != 0;
   }

   public static boolean from(long j) {
      return j != 0L;
   }

   public static boolean from(float f) {
      return f != 0.0F;
   }

   public static boolean from(double d) {
      return d != 0.0;
   }

   public static boolean from(Object obj) {
      return obj instanceof Boolean ? (Boolean)obj : (obj instanceof Number ? ((Number)obj).doubleValue() != 0.0 : obj != null);
   }
}
