package pulse.render;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Test7 {
   public static void main(String[] args) {
      try {
         ZipFile zip = new ZipFile(
            "C:/Users/Administrator/.gradle/caches/fabric-loom/1.21.11/net.fabricmc.yarn.1_21_11.1.21.11+build.5-v2/minecraft-project-@-merged-sources.jar"
         );
         ZipEntry entry = zip.getEntry("net/minecraft/client/render/item/HeldItemRenderer.java");
         if (entry != null) {
            InputStream is = zip.getInputStream(entry);
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");

            for (String line : lines) {
               if (line.contains("void renderItem(")) {
                  System.out.println(line.trim());
               }
            }
         } else {
            System.out.println("Entry not found.");
         }

         zip.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
