package pulse.cosmetic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import pulse.render.AttachmentPoint;
import ru.pulse.Pulse;

public class CosmeticRepository {
   private static CosmeticRepository c;
   private final Map<Integer, CosmeticModel> d = new ConcurrentHashMap<>();
   private final Map<String, Identifier> e = new ConcurrentHashMap<>();
   public static int a;
   public static boolean b;

   public static CosmeticRepository a() {
      if (c == null) {
         c = new CosmeticRepository();
      }

      return c;
   }

   public CosmeticModel a(String str) {
      return this.a(str, (Identifier)null, -1);
   }

   public CosmeticModel a(String str, Identifier IdentifierVar) {
      return this.a(str, IdentifierVar, -1);
   }

   public CosmeticModel a(String str, Identifier IdentifierVar, int i) {
      try {
         return this.a(new JsonParser().parse(str).getAsJsonObject(), IdentifierVar, i);
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt", e);
         return null;
      }
   }

   public CosmeticModel a(JsonObject jsonObject) {
      return this.a(jsonObject, (Identifier)null, -1);
   }

   public CosmeticModel a(JsonObject jsonObject, Identifier IdentifierVar, int i) {
      try {
         if (jsonObject.has("crypt") && jsonObject.has("┪ದ↤ఆ┟⤔┪ದ╉ఆ┟⠪┪ದ▀")) {
            String asString = jsonObject.get("crypt").getAsString();
            int asInt;
            if (i <= 0) {
               if (jsonObject.has("crypt")) {
                  asInt = jsonObject.get("crypt").getAsInt();
               } else {
                  asInt = asString.hashCode();
               }
            } else {
               asInt = i;
            }

            int i7 = asInt;
            int asInt2;
            if (jsonObject.has("crypt")) {
               asInt2 = jsonObject.get("crypt").getAsInt();
            } else {
               asInt2 = 1;
            }

            CosmeticModel cosmeticModel = new CosmeticModel(asString, i7, asInt2);
            cosmeticModel.a(jsonObject.getAsJsonObject("crypt").toString());
            if (IdentifierVar != null) {
               cosmeticModel.a(IdentifierVar);
            } else if (jsonObject.has("crypt")) {
               cosmeticModel.a(this.a(asString, i7, jsonObject.get("┪\udd21↽e┪\udd21↱t┪\udd21↼r┪\udd21└").getAsString()));
            }

            this.a(jsonObject, cosmeticModel);
            if (jsonObject.has("crypt")) {
               cosmeticModel.h(jsonObject.get("crypt").getAsFloat());
            }

            if (jsonObject.has("crypt")) {
               cosmeticModel.a(jsonObject.get("crypt").getAsFloat());
            }

            if (jsonObject.has("crypt")) {
               cosmeticModel.i(jsonObject.get("crypt").getAsFloat());
            }

            if (jsonObject.has("crypt")) {
               cosmeticModel.j(jsonObject.get("┪ᖄ┐ᔥķᆲ┪ᖄ└ᔥķᕊ┪ᖄ↠ᔥķᆥ┪ᖄ┖ᔥķᔘ").getAsFloat());
            }

            if (jsonObject.has("crypt")) {
               cosmeticModel.a(jsonObject.getAsJsonObject("crypt"));
            }

            this.d.put(i7, cosmeticModel);
            Pulse.getLOGGER().info("┪⣓Č⡱↡⳯┪⣓\u0007⡱↡ⳤ┪⣓└⡱↡ⳤ┪⳧╀⡱↡ⳣ┪⣓↦⡱↡ⳳ┪⣓↤⡱↡⳥┪⣓↽⡱↡⡇┪⣓↪⡱↠ൕ┪⳧╀" + asString + "crypt" + i7 + "crypt" + cosmeticModel.e());
            return cosmeticModel;
         } else {
            Pulse.getLOGGER().error("crypt");
            return null;
         }
      } catch (Exception e) {
         Pulse.getLOGGER().error("┪ด╍r┪ด┕ິ↺ਯ┪ด┕ິ┕ਠ┪ด▀ິ↺ਯ┪ด\u0007ິ↺ਤ┪ด↠ິ↺\u0e80┪ด╆ິ┕ਠ┪ด↪ິ↺ਯ┪ด↺ິ↺ਭ┪ด└ິ↺\u0a34┪ด↠ິ↺ਣ", e);
         return null;
      }
   }

   private void a(JsonObject jsonObject, CosmeticModel cosmeticModel) {
      if (jsonObject.has("crypt")) {
         cosmeticModel.a(AttachmentPoint.a(jsonObject.get("crypt").getAsInt()));
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.a(jsonObject.get("crypt").getAsFloat());
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.b(jsonObject.get("crypt").getAsFloat());
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.c(jsonObject.get("crypt").getAsFloat());
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.d(jsonObject.get("crypt").getAsFloat());
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.e(jsonObject.get("crypt").getAsFloat());
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.f(jsonObject.get("crypt").getAsFloat());
      }

      if (jsonObject.has("crypt")) {
         cosmeticModel.g(jsonObject.get("crypt").getAsFloat());
      }
   }

   private Identifier a(String str, int i, String str2) {
      try {
         String str3 = str.replace("crypt", "").toLowerCase() + "crypt" + i;
         String str4 = "crypt" + str3;
         if (this.e.containsKey(str4)) {
            return this.e.get(str4);
         }

         NativeImage NativeImageVarRead = NativeImage.read(new ByteArrayInputStream(Base64.getDecoder().decode(str2)));
         Identifier IdentifierVarOf = Identifier.of("crypt", "crypt" + str3);
         Runnable runnable = () -> {
            MinecraftClient.getInstance().getTextureManager().registerTexture(IdentifierVarOf, new NativeImageBackedTexture(() -> "pulse", NativeImageVarRead));
            this.e.put(str4, IdentifierVarOf);
            Pulse.getLOGGER()
               .info("crypt" + IdentifierVarOf + "crypt" + NativeImageVarRead.getWidth() + "crypt" + NativeImageVarRead.getHeight() + "crypt");
         };
         if (MinecraftClient.getInstance().isOnThread()) {
            runnable.run();
         } else {
            MinecraftClient.getInstance().execute(runnable);
         }

         this.e.put(str4, IdentifierVarOf);
         return IdentifierVarOf;
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt" + str, e);
         return null;
      }
   }

   public CosmeticModel a(int i) {
      return this.d.get(i);
   }

   public Map<Integer, CosmeticModel> b() {
      return this.d;
   }

   public void a(InputStream inputStream) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         byte[] bArr = new byte[1024];

         while (true) {
            int i = inputStream.read(bArr);
            if (i == -1) {
               this.a(byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()));
               return;
            }

            byteArrayOutputStream.write(bArr, 0, i);
         }
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt", e);
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
