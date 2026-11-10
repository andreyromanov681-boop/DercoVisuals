package pulse.theme;

import java.awt.Color;

public final class Theme {
   public static Color b = new Color(245, 247, 252, 255);
   public static Color e = new Color(40, 42, 54, 220);
   public static Color f = new Color(52, 54, 68, 200);
   public static Color m = new Color(35, 35, 48, 215);
   public static Color n = new Color(45, 45, 60, 210);
   public static Color q = new Color(50, 53, 68, 225);
   public static Color r = new Color(62, 65, 82, 225);
   public static Color w = new Color(32, 34, 44, 230);
   public static Color C = new Color(110, 160, 240, 255);
   public static Color D = new Color(85, 135, 215, 255);
   public static Color I = new Color(110, 160, 240, 255);
   public static Color K = new Color(130, 180, 255, 255);
   public static Color aa = new Color(255, 255, 255);
   public static int ae;
   public static boolean af;
   public static Color a = new Color(223, 223, 243);
   public static Color c = new Color(115, 83, 255);
   public static Color d = new Color(80, 80, 110);
   public static Color g = new Color(22, 22, 30);
   public static Color h = new Color(18, 18, 24);
   public static Color i = new Color(25, 25, 45);
   public static Color j = new Color(19, 19, 35);
   public static Color k = new Color(20, 20, 32);
   public static Color l = new Color(16, 16, 26);
   public static Color o = new Color(21, 21, 28);
   public static Color p = new Color(30, 30, 40);
   public static Color s = new Color(37, 37, 67);
   public static Color t = new Color(21, 21, 38);
   public static Color u = new Color(30, 30, 50);
   public static Color v = new Color(18, 18, 32);
   public static Color x = new Color(18, 18, 24);
   public static Color y = new Color(115, 83, 255);
   public static Color z = new Color(70, 46, 174);
   public static Color A = new Color(115, 83, 255, 100);
   public static Color B = new Color(140, 110, 255);
   public static Color E = new Color(135, 95, 255);
   public static Color toggleOffKnob = new Color(64, 64, 90);
   public static Color F = new Color(14, 14, 19);
   public static Color G = new Color(37, 37, 52);
   public static Color H = new Color(0, 0, 0, 55);
   public static Color J = new Color(22, 22, 28);
   public static Color L = new Color(40, 20, 20);
   public static Color M = new Color(30, 15, 15);
   public static Color N = new Color(100, 100, 130);
   public static Color O = new Color(96, 96, 96);
   public static Color P = new Color(60, 60, 85);
   public static Color Q = new Color(140, 110, 255);
   public static Color R = new Color(95, 65, 200);
   public static Color S = new Color(15, 15, 21);
   public static Color T = new Color(18, 18, 25);
   public static Color U = new Color(140, 140, 170);
   public static Color V = new Color(24, 24, 32);
   public static Color W = new Color(220, 53, 69);
   public static Color X = new Color(40, 167, 69);
   public static Color Y = new Color(255, 180, 50);
   public static Color Z = new Color(255, 80, 80);
   public static Color FriendCard = new Color(0, 0, 0);
   public static Color FriendsPanel = new Color(30, 30, 40);
   public static Color ad = new Color(0, 0, 0, 125);

   private Theme() {
   }

   public static Color a(Color color, int i2) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, i2)));
   }

   public static Color a(Color color, float f2) {
      return a(color, (int)(255.0F * f2));
   }

   public static Color b(Color color, float f2) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f2))));
   }

   public static Color b(Color color, int i2) {
      int red = color.getRed();
      int iMin = Math.min(255, (red ^ i2) + 2 * (red & i2));
      int green = color.getGreen();
      return new Color(iMin, Math.min(255, 2 * (green | i2) - (green ^ i2)), Math.min(255, color.getBlue() - ~i2 - 1), color.getAlpha());
   }

   public static Color c(Color color, int i2) {
      int iMax = Math.max(0, color.getRed() - i2);
      int green = color.getGreen();
      int iMax2 = Math.max(0, (green ^ i2) - 2 * (~green & i2));
      int blue = color.getBlue();
      return new Color(iMax, iMax2, Math.max(0, (blue ^ i2) - 2 * (~blue & i2)), color.getAlpha());
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   static {
      b = new Color(200, 205, 220);
      e = new Color(15, 14, 19, 255);
      f = new Color(13, 12, 17, 255);
      m = new Color(31, 31, 40);
      n = new Color(25, 25, 33);
      q = new Color(30, 30, 42);
      r = new Color(38, 38, 52);
      w = new Color(255, 255, 255, 20);
      C = new Color(126, 90, 254);
      D = new Color(106, 70, 215);
      I = new Color(25, 25, 33);
      K = new Color(20, 20, 26);
   }
}
