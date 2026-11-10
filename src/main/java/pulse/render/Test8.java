package pulse.render;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Test8 {
   public static void main(String[] args) {
      try {
         ZipFile zip = new ZipFile("C:/Users/Administrator/.gradle/caches/fabric-loom/1.21.11/net.fabricmc.yarn.1_21_11.1.21.11+build.6-v2/mappings.jar");
         ZipEntry entry = zip.getEntry("mappings/mappings.tiny");
         if (entry == null) {
            System.out.println("Entry not found.");
         } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8));
            String currentClass = "";

            String line;
            while ((line = reader.readLine()) != null) {
               if (line.startsWith("c\t")) {
                  currentClass = line;
               }

               if (currentClass.contains("HeldItemRenderer") && line.contains("renderItem")) {
                  System.out.println(currentClass);
                  System.out.println(line);
               }
            }

            reader.close();
         }

         zip.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
