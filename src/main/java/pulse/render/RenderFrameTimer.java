package pulse.render;

public final class RenderFrameTimer {
   private static long lastFrameMillis;

   private RenderFrameTimer() {
   }

   public static long elapsedMillis() {
      return System.currentTimeMillis() - lastFrameMillis;
   }

   public static void markFrame() {
      lastFrameMillis = System.currentTimeMillis();
   }

   public static long a() {
      return elapsedMillis();
   }

   public static void b() {
      markFrame();
   }
}
