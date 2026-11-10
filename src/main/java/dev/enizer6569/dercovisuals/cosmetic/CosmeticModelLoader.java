// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.cosmetic;

import dev.enizer6569.dercovisuals.model.BedrockModelParser;
import dev.enizer6569.dercovisuals.model.ModelDefinition;
import dev.enizer6569.dercovisuals.DercoVisuals;

public class CosmeticModelLoader {
   public static int a;

   public ModelDefinition a(CosmeticModel cosmeticModel) {
      try {
         String strD = cosmeticModel.d();
         if (strD == null) {
            DercoVisuals.getLOGGER().error("crypt" + cosmeticModel.a());
            return null;
         } else {
            ModelDefinition modelDefinitionA = BedrockModelParser.a(strD);
            if (modelDefinitionA == null) {
               DercoVisuals.getLOGGER().error("crypt" + cosmeticModel.a());
               return null;
            } else {
               DercoVisuals.getLOGGER().info("crypt" + cosmeticModel.a() + "crypt" + modelDefinitionA.a.size() + "crypt");
               return modelDefinitionA;
            }
         }
      } catch (Exception e) {
         DercoVisuals.getLOGGER().error("crypt" + cosmeticModel.a(), e);
         return null;
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
