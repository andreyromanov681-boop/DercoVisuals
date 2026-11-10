// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.discord;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;

public class FolderOpener {
   private static final File CONFIG_DIR;

   public static void ensureExists() {
      try {
         Files.createDirectories(CONFIG_DIR.toPath());
      } catch (Exception var1) {
      }
   }

   public static void openConfigFolder() {
      try {
         Files.createDirectories(CONFIG_DIR.toPath());
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(CONFIG_DIR);
         } else {
            Runtime.getRuntime().exec(new String[]{"explorer", CONFIG_DIR.getAbsolutePath()});
         }
      } catch (Exception var3) {
         try {
            Runtime.getRuntime().exec(new String[]{"explorer", CONFIG_DIR.getAbsolutePath()});
         } catch (Exception var2) {
         }
      }
   }

   static {
      String var0 = System.getenv("APPDATA");
      if (var0 != null && !var0.isBlank()) {
         CONFIG_DIR = new File(var0, "DercoVisuals/config");
      } else {
         CONFIG_DIR = new File(System.getProperty("user.home"), "DercoVisuals/config");
      }
   }
}
