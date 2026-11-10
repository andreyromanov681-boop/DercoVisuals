// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class FontManager {
   public static final FontRenderer[] REGULAR = new FontRenderer[65];
   public static final FontRenderer[] MEDIUM;
   public static final FontRenderer[] ICONS;
   public static final FontRenderer[] PULSE_ICONS;
   public static final FontRenderer[] SMALL_ICONS;
   public static final FontRenderer[] a;
   public static final FontRenderer[] b;
   public static final FontRenderer[] c;
   public static final FontRenderer[] d;
   public static final FontRenderer[] e;
   static final boolean $assertionsDisabled = !FontManager.class.desiredAssertionStatus();

   private static FontRenderer load(float f, String str) {
      try {
         InputStream resourceAsStream = FontManager.class.getResourceAsStream("/assets/dercovisuals/font/".concat(str));

         try {
            assert resourceAsStream != null;
            FontRenderer fontRenderer = new FontRenderer(Font.createFont(0, resourceAsStream).deriveFont(0, f / 2.0F), f / 2.0F);
            if (resourceAsStream != null) {
               resourceAsStream.close();
            }

            return fontRenderer;
         } catch (Throwable th) {
            if (resourceAsStream != null) {
               try {
                  resourceAsStream.close();
               } catch (Throwable th2) {
                  th.addSuppressed(th2);
               }
            }

            throw th;
         }
      } catch (IOException | FontFormatException e2) {
         throw new RuntimeException(e2);
      }
   }

   static {
      for (int i = 6; i < 65; i++) {
         REGULAR[i] = load(i, "sfprodisplayregular.ttf");
      }

      MEDIUM = new FontRenderer[65];

      for (int i2 = 6; i2 < 65; i2++) {
         MEDIUM[i2] = load(i2, "sfprodisplay-medium-test.ttf");
      }

      ICONS = new FontRenderer[65];

      for (int i3 = 6; i3 < 65; i3++) {
         ICONS[i3] = load(i3, "icons.ttf");
      }

      PULSE_ICONS = new FontRenderer[265];

      for (int i4 = 6; i4 < 265; i4++) {
         PULSE_ICONS[i4] = load(i4, "dercovisualsicon.ttf");
      }

      SMALL_ICONS = new FontRenderer[65];

      for (int i5 = 6; i5 < 65; i5++) {
         SMALL_ICONS[i5] = load(i5, "dercovisuals_icons.ttf");
      }

      a = REGULAR;
      b = MEDIUM;
      c = ICONS;
      d = PULSE_ICONS;
      e = SMALL_ICONS;
   }
}
