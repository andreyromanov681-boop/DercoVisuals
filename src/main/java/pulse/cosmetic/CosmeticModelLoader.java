package pulse.cosmetic;

import pulse.model.BedrockModelParser;
import pulse.model.ModelDefinition;
import ru.pulse.Pulse;

public class CosmeticModelLoader {
   public static int a;

   public ModelDefinition a(CosmeticModel cosmeticModel) {
      try {
         String strD = cosmeticModel.d();
         if (strD == null) {
            Pulse.getLOGGER().error("crypt" + cosmeticModel.a());
            return null;
         } else {
            ModelDefinition modelDefinitionA = BedrockModelParser.a(strD);
            if (modelDefinitionA == null) {
               Pulse.getLOGGER().error("crypt" + cosmeticModel.a());
               return null;
            } else {
               Pulse.getLOGGER().info("crypt" + cosmeticModel.a() + "crypt" + modelDefinitionA.a.size() + "crypt");
               return modelDefinitionA;
            }
         }
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt" + cosmeticModel.a(), e);
         return null;
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
