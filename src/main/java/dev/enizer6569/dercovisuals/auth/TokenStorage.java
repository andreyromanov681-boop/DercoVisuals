// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import dev.enizer6569.dercovisuals.DercoVisuals;

public class TokenStorage {
   private static final String c = ".pulse";
   private static final String d = "token.dat";
   public static int a;
   public static boolean b;

   public static String a() {
      return Paths.get(System.getProperty("user.home"), ".pulse").resolve("token.dat").toString();
   }

   public static boolean b() {
      return Files.exists(Paths.get(a()));
   }

   public static String c() {
      try {
         Path path = Paths.get(a());
         if (!Files.exists(path)) {
            return null;
         }

         String strTrim = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
         return strTrim.isEmpty() ? null : strTrim;
      } catch (IOException e) {
         DercoVisuals.getLOGGER().error("Ошибка чтения токена", e);
         return null;
      }
   }

   public static void a(String str) throws IOException {
      Path path = Paths.get(System.getProperty("user.home"), ".pulse");
      Files.createDirectories(path);
      Path pathResolve = path.resolve("token.dat");
      Files.write(pathResolve, str.getBytes(StandardCharsets.UTF_8));
      if (!System.getProperty("os.name").toLowerCase().contains("win")) {
         try {
            Files.setPosixFilePermissions(pathResolve, PosixFilePermissions.fromString("rw-------"));
         } catch (Exception var4) {
         }
      }

      DercoVisuals.getLOGGER().info("Токен сохранен: " + pathResolve);
   }

   public static void d() {
      try {
         Files.deleteIfExists(Paths.get(a()));
         DercoVisuals.getLOGGER().info("Токен удален");
      } catch (IOException e) {
         DercoVisuals.getLOGGER().error("Ошибка удаления токена", e);
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
