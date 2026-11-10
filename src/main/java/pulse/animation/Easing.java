package pulse.animation;

public final class Easing {
   public static final double BACK_OVERSHOOT = 1.70158;
   public static final double BACK_OVERSHOOT_IN_OUT = 2.5949095;
   public static final double BACK_OVERSHOOT_PLUS_ONE = 2.70158;
   public static final double ELASTIC_PERIOD = Math.PI * 2.0 / 3.0;
   public static final double ELASTIC_PERIOD_IN_OUT = Math.PI * 4.0 / 9.0;
   public static final EasingFunction f = d -> d;
   public static final EasingFunction g = a(2.0);
   public static final EasingFunction h = b(2.0);
   public static final EasingFunction i = c(2.0);
   public static final EasingFunction j = a(3.0);
   public static final EasingFunction k = b(3.0);
   public static final EasingFunction l = c(3.0);
   public static final EasingFunction m = a(4.0);
   public static final EasingFunction n = b(4.0);
   public static final EasingFunction o = c(4.0);
   public static final EasingFunction p = a(5.0);
   public static final EasingFunction q = b(5.0);
   public static final EasingFunction r = c(5.0);
   public static final EasingFunction s = d -> 1.0 - Math.cos(d * Math.PI / 2.0);
   public static final EasingFunction t = d -> Math.sin(d * Math.PI / 2.0);
   public static final EasingFunction u = d -> -(Math.cos(Math.PI * d) - 1.0) / 2.0;
   public static final EasingFunction v = d -> 1.0 - Math.sqrt(1.0 - Math.pow(d, 2.0));
   public static final EasingFunction w = d -> Math.sqrt(1.0 - Math.pow(d - 1.0, 2.0));
   public static final EasingFunction x = d -> d < 0.5
      ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * d, 2.0))) / 2.0
      : (Math.sqrt(1.0 - Math.pow(-2.0 * d + 2.0, 2.0)) + 1.0) / 2.0;
   public static final EasingFunction y = d -> d == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * d - 10.0);
   public static final EasingFunction z = d -> d == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * d);
   public static final EasingFunction A = d -> d == 0.0 || d == 1.0
      ? d
      : (d < 0.5 ? Math.pow(2.0, 20.0 * d - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * d + 10.0)) / 2.0);
   public static final EasingFunction B = d -> 2.70158 * Math.pow(d, 3.0) - 1.70158 * Math.pow(d, 2.0);
   public static final EasingFunction C = d -> 1.0 + 2.70158 * Math.pow(d - 1.0, 3.0) + 1.70158 * Math.pow(d - 1.0, 2.0);
   public static final EasingFunction D = d -> d < 0.5
      ? Math.pow(2.0 * d, 2.0) * (7.189819 * d - 2.5949095) / 2.0
      : (Math.pow(2.0 * d - 2.0, 2.0) * (3.5949095 * (d * 2.0 - 2.0) + 2.5949095) + 2.0) / 2.0;
   public static final EasingFunction E = d -> d != 0.0 && d != 1.0
      ? -Math.pow(2.0, 10.0 * d - 10.0) * Math.sin((d * 10.0 - 10.75) * (Math.PI * 2.0 / 3.0))
      : d;
   public static final EasingFunction F = d -> d != 0.0 && d != 1.0 ? Math.pow(2.0, -10.0 * d) * Math.sin((d * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0 : d;
   public static final EasingFunction G = d -> d == 0.0 || d == 1.0
      ? d
      : (
         d < 0.5
            ? -(Math.pow(2.0, 20.0 * d - 10.0) * Math.sin((20.0 * d - 11.125) * (Math.PI * 4.0 / 9.0))) / 2.0
            : Math.pow(2.0, -20.0 * d + 10.0) * Math.sin((20.0 * d - 11.125) * (Math.PI * 4.0 / 9.0)) / 2.0 + 1.0
      );
   public static final EasingFunction H = d -> {
      if (d < 0.36363636363636365) {
         return 7.5625 * d * d;
      } else if (d < 0.7272727272727273) {
         double d1 = d - 0.5454545454545454;
         return 7.5625 * d1 * d1 + 0.75;
      } else if (d < 0.9090909090909091) {
         double d2 = d - 0.8181818181818182;
         return 7.5625 * d2 * d2 + 0.9375;
      } else {
         double d3 = d - 0.9545454545454546;
         return 7.5625 * d3 * d3 + 0.984375;
      }
   };
   public static final EasingFunction I = d -> 1.0 - H.ease(1.0 - d);
   public static final EasingFunction J = d -> d < 0.5 ? (1.0 - H.ease(1.0 - 2.0 * d)) / 2.0 : (1.0 + H.ease(2.0 * d - 1.0)) / 2.0;

   private Easing() {
   }

   public static EasingFunction a(double d) {
      return d2 -> Math.pow(d2, d);
   }

   public static EasingFunction a(int i2) {
      return a(i2);
   }

   public static EasingFunction b(double d) {
      return d2 -> 1.0 - Math.pow(1.0 - d2, d);
   }

   public static EasingFunction b(int i2) {
      return b(i2);
   }

   public static EasingFunction c(double d) {
      return d2 -> d2 < 0.5 ? Math.pow(2.0, d - 1.0) * Math.pow(d2, d) : 1.0 - Math.pow(-2.0 * d2 + 2.0, d) / 2.0;
   }
}
