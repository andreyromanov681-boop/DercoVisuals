// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.util;

import lombok.Generated;
import org.lwjgl.glfw.GLFW;

public final class KeyNameFormatter {
   private static final int FIRST_MOUSE_BUTTON_CODE = 1450;

   public static String format(int i) {
      if (i == -1 || i == 0) {
         return "";
      }

      if (i > 2000) {
         return "M" + (i - 2000);
      }

      String strGlfwGetKeyName = GLFW.glfwGetKeyName(i, 0);
      if (strGlfwGetKeyName != null) {
         return strGlfwGetKeyName.toUpperCase();
      }

      switch (i) {
         case 32:
            return "SPACE";
         case 256:
            return "ESC";
         case 257:
            return "ENTER";
         case 258:
            return "TAB";
         case 259:
            return "BACK";
         case 260:
            return "INS";
         case 261:
            return "DEL";
         case 262:
            return "RIGHT";
         case 263:
            return "LEFT";
         case 264:
            return "DOWN";
         case 265:
            return "UP";
         case 266:
            return "PGUP";
         case 267:
            return "PGDN";
         case 268:
            return "HOME";
         case 269:
            return "END";
         case 280:
            return "CAPS";
         case 290:
            return "F1";
         case 291:
            return "F2";
         case 292:
            return "F3";
         case 293:
            return "F4";
         case 294:
            return "F5";
         case 295:
            return "F6";
         case 296:
            return "F7";
         case 297:
            return "F8";
         case 298:
            return "F9";
         case 299:
            return "F10";
         case 300:
            return "F11";
         case 301:
            return "F12";
         case 340:
            return "LSHIFT";
         case 341:
            return "LCTRL";
         case 342:
            return "LALT";
         case 344:
            return "RSHIFT";
         case 345:
            return "RCTRL";
         case 346:
            return "RALT";
         default:
            return "KEY_" + i;
      }
   }

   public static String a(int i) {
      return format(i);
   }

   @Generated
   private KeyNameFormatter() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
