package pulse.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Iterator;
import pulse.core.Bool;
import ru.pulse.Pulse;

public class BedrockModelParser {
   public static int a;
   public static boolean b;

   public static ModelDefinition a(String str) {
      try {
         return a(new JsonParser().parse(str).getAsJsonObject());
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt", e);
         return null;
      }
   }

   private static ModelDefinition a(JsonObject jsonObject) {
      JsonArray asJsonArray = jsonObject.getAsJsonArray("crypt");
      if (asJsonArray != null && asJsonArray.size() != 0) {
         JsonObject asJsonObject = asJsonArray.get(0).getAsJsonObject();
         JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("crypt");
         int asInt2 = !asJsonObject2.has("crypt") ? 64 : asJsonObject2.get("crypt").getAsInt();
         int asInt;
         if (asJsonObject2.has("crypt")) {
            asInt = asJsonObject2.get("crypt").getAsInt();
         } else {
            asInt = 64;
         }

         int i2 = asInt;
         ModelDefinition modelDefinition = new ModelDefinition();
         modelDefinition.b = asInt2;
         modelDefinition.c = i2;
         JsonArray asJsonArray2 = asJsonObject.getAsJsonArray("crypt");
         if (asJsonArray2 != null) {
            HashMap map = new HashMap();
            Iterator it = asJsonArray2.iterator();

            while (it.hasNext()) {
               ModelPart modelPartA = a(((JsonElement)it.next()).getAsJsonObject(), asInt2, i2);
               map.put(modelPartA.d, modelPartA);
            }

            Iterator it2 = asJsonArray2.iterator();

            while (it2.hasNext()) {
               JsonObject asJsonObject3 = ((JsonElement)it2.next()).getAsJsonObject();
               ModelPart modelPart = (ModelPart)map.get(asJsonObject3.get("crypt").getAsString());
               if (asJsonObject3.has("crypt")) {
                  ModelPart modelPart2 = (ModelPart)map.get(asJsonObject3.get("crypt").getAsString());
                  if (modelPart2 != null) {
                     modelPart2.b.add(modelPart);
                     modelPart.a = modelPart2;
                  }
               } else {
                  modelDefinition.a.add(modelPart);
               }
            }

            if (b) {
               throw new IllegalAccessError();
            }
         }

         Pulse.getLOGGER().info("crypt", modelDefinition.a.size());
         return modelDefinition;
      } else {
         Pulse.getLOGGER().error("crypt");
         return null;
      }
   }

   private static ModelPart a(JsonObject jsonObject, int i, int i2) {
      ModelPart modelPart = new ModelPart(jsonObject.get("crypt").getAsString());
      if (jsonObject.has("crypt")) {
         JsonArray asJsonArray = jsonObject.getAsJsonArray("crypt");
         modelPart.f = -asJsonArray.get(0).getAsFloat();
         modelPart.g = asJsonArray.get(1).getAsFloat();
         modelPart.h = asJsonArray.get(2).getAsFloat();
      }

      if (jsonObject.has("crypt")) {
         JsonArray asJsonArray2 = jsonObject.getAsJsonArray("crypt");
         modelPart.a((float)Math.toRadians(-asJsonArray2.get(0).getAsFloat()));
         modelPart.b((float)Math.toRadians(-asJsonArray2.get(1).getAsFloat()));
         modelPart.c((float)Math.toRadians(asJsonArray2.get(2).getAsFloat()));
      }

      if (jsonObject.has("crypt")) {
         Iterator it = jsonObject.getAsJsonArray("crypt").iterator();

         while (it.hasNext()) {
            modelPart.c.add(b(((JsonElement)it.next()).getAsJsonObject(), i, i2));
         }
      }

      return modelPart;
   }

   private static ModelCube b(JsonObject jsonObject, int i, int i2) {
      float[] fArr = new float[]{0.0F, 0.0F, 0.0F};
      float[] fArrA = a(jsonObject, "crypt", fArr);
      float[] fArr2 = new float[]{1.0F, 1.0F, 1.0F};
      float[] fArrA2 = a(jsonObject, "crypt", fArr2);
      float[] fArrA3 = a(jsonObject, "crypt", (float[])fArrA.clone());
      float[] fArr3 = new float[]{0.0F, 0.0F, 0.0F};
      float[] fArrA4 = a(jsonObject, "crypt", fArr3);
      float asFloat = !jsonObject.has("crypt") ? 0.0F : jsonObject.get("crypt").getAsFloat();
      int i3 = jsonObject.has("crypt") && jsonObject.get("crypt").getAsBoolean() ? 1 : 0;
      ModelCube modelCube = new ModelCube(fArrA2[0], fArrA2[1], fArrA2[2]);
      modelCube.c = new ModelVector(-fArrA3[0], fArrA3[1], fArrA3[2]);
      modelCube.d = new ModelVector((float)Math.toRadians(-fArrA4[0]), (float)Math.toRadians(-fArrA4[1]), (float)Math.toRadians(fArrA4[2]));
      modelCube.e = asFloat;
      modelCube.f = Bool.from(i3);
      a(modelCube, fArrA, fArrA2, asFloat, Bool.from(i3), jsonObject, i, i2);
      return modelCube;
   }

   private static void a(ModelCube modelCube, float[] fArr, float[] fArr2, float f, boolean z, JsonObject jsonObject, int i, int i2) {
      float f2 = fArr[0];
      float f3 = fArr[1];
      float f4 = fArr[2];
      float f5 = f2 - f;
      float f6 = f3 - f;
      float f7 = f4 - f;
      float f8 = fArr2[0] + f * 2.0F;
      float f9 = fArr2[1] + f * 2.0F;
      float f10 = fArr2[2] + f * 2.0F;
      float asFloat = 0.0F;
      float asFloat2 = 0.0F;
      boolean z2 = false;
      JsonObject asJsonObject = null;
      if (jsonObject.has("crypt")) {
         JsonElement jsonElement = jsonObject.get("crypt");
         if (jsonElement.isJsonArray()) {
            JsonArray asJsonArray = jsonElement.getAsJsonArray();
            asFloat = asJsonArray.get(0).getAsFloat();
            asFloat2 = asJsonArray.get(1).getAsFloat();
         } else if (jsonElement.isJsonObject()) {
            z2 = true;
            asJsonObject = jsonElement.getAsJsonObject();
         }
      }

      float f11 = -(f5 + f8) / 16.0F;
      float f12 = f6 / 16.0F;
      float f13 = f7 / 16.0F;
      float f14 = -f5 / 16.0F;
      float f15 = (f6 + f9) / 16.0F;
      float f16 = (f7 + f10) / 16.0F;
      if (z2) {
         if (asJsonObject != null) {
            modelCube.a[0] = a(asJsonObject, "crypt", f11, f12, f13, f11, f15, f16, -1.0F, 0.0F, 0.0F, i, i2);
            modelCube.a[1] = a(asJsonObject, "crypt", f14, f12, f13, f14, f15, f16, 1.0F, 0.0F, 0.0F, i, i2);
            modelCube.a[2] = a(asJsonObject, "crypt", f11, f12, f13, f14, f12, f16, 0.0F, -1.0F, 0.0F, i, i2);
            modelCube.a[3] = a(asJsonObject, "crypt", f11, f15, f13, f14, f15, f16, 0.0F, 1.0F, 0.0F, i, i2);
            modelCube.a[4] = a(asJsonObject, "crypt", f11, f12, f13, f14, f15, f13, 0.0F, 0.0F, -1.0F, i, i2);
            modelCube.a[5] = a(asJsonObject, "crypt", f11, f12, f16, f14, f15, f16, 0.0F, 0.0F, 1.0F, i, i2);
            return;
         }
      } else if (b) {
         throw new IllegalAccessError();
      }

      float f17 = i;
      float f18 = i2;
      modelCube.a[0] = a(f11, f12, f13, f11, f15, f16, -1.0F, 0.0F, 0.0F, asFloat, asFloat2, f10, f9, f8, f17, f18, "crypt");
      modelCube.a[1] = a(f14, f12, f13, f14, f15, f16, 1.0F, 0.0F, 0.0F, asFloat, asFloat2, f10, f9, f8, f17, f18, "crypt");
      modelCube.a[2] = a(f11, f12, f13, f14, f12, f16, 0.0F, -1.0F, 0.0F, asFloat, asFloat2, f10, f9, f8, f17, f18, "crypt");
      modelCube.a[3] = a(f11, f15, f13, f14, f15, f16, 0.0F, 1.0F, 0.0F, asFloat, asFloat2, f10, f9, f8, f17, f18, "crypt");
      modelCube.a[4] = a(f11, f12, f13, f14, f15, f13, 0.0F, 0.0F, -1.0F, asFloat, asFloat2, f10, f9, f8, f17, f18, "crypt");
      modelCube.a[5] = a(f11, f12, f16, f14, f15, f16, 0.0F, 0.0F, 1.0F, asFloat, asFloat2, f10, f9, f8, f17, f18, "crypt");
   }

   private static ModelVertex a(
      JsonObject jsonObject, String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int i, int i2
   ) {
      if (!jsonObject.has(str)) {
         return null;
      }

      JsonObject asJsonObject = jsonObject.getAsJsonObject(str);
      JsonArray asJsonArray = asJsonObject.getAsJsonArray("တ✿ၶ̨ၳ");
      JsonArray asJsonArray2 = asJsonObject.getAsJsonArray("crypt");
      return new ModelVertex(
         a(
            str,
            f,
            f2,
            f3,
            f4,
            f5,
            f6,
            asJsonArray.get(0).getAsFloat() / i,
            asJsonArray.get(1).getAsFloat() / i2,
            asJsonArray2.get(0).getAsFloat() / i,
            asJsonArray2.get(1).getAsFloat() / i2
         ),
         f7,
         f8,
         f9
      );
   }

   private static ModelVertex a(
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12,
      float f13,
      float f14,
      float f15,
      float f16,
      String str
   ) {
      float f17;
      float f18;
      float f19;
      float f20;
      switch (str) {
         case "crypt_face0":
            f17 = (f10 + f12 + f14) / f15;
            f18 = (f11 + f12) / f16;
            f19 = f14 / f15;
            f20 = f13 / f16;
            break;
         case "crypt_face1":
            f17 = (f10 + f12 + f14 + f12) / f15;
            f18 = (f11 + f12) / f16;
            f19 = f14 / f15;
            f20 = f13 / f16;
            break;
         case "crypt_face2":
            f17 = f10 / f15;
            f18 = (f11 + f12) / f16;
            f19 = f12 / f15;
            f20 = f13 / f16;
            break;
         case "crypt_face3":
            f17 = (f10 + f12 + f14) / f15;
            f18 = (f11 + f12) / f16;
            f19 = f12 / f15;
            f20 = f13 / f16;
            break;
         case "crypt_face4":
            f17 = (f10 + f12) / f15;
            f18 = f11 / f16;
            f19 = f14 / f15;
            f20 = f12 / f16;
            break;
         case "crypt_face5":
            f17 = (f10 + f12 + f14) / f15;
            f18 = f11 / f16;
            f19 = f14 / f15;
            f20 = f12 / f16;
            break;
         default:
            f17 = 0.0F;
            f18 = 0.0F;
            f19 = 0.0F;
            f20 = 0.0F;
      }

      return new ModelVertex(a(str, f, f2, f3, f4, f5, f6, f17, f18, f19, f20), f7, f8, f9);
   }

   private static ModelVertexUv[] a(String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10) {
      ModelVertexUv[] modelVertexUvArr = new ModelVertexUv[4];
      float f11 = f7 + f9;
      float f12 = f8 + f10;
      switch (str) {
         case "crypt_uv0":
            modelVertexUvArr[0] = new ModelVertexUv(f4, f5, f3, f7, f8);
            modelVertexUvArr[1] = new ModelVertexUv(f, f5, f3, f11, f8);
            modelVertexUvArr[2] = new ModelVertexUv(f, f2, f3, f11, f12);
            modelVertexUvArr[3] = new ModelVertexUv(f4, f2, f3, f7, f12);
            break;
         case "crypt_uv1":
            modelVertexUvArr[0] = new ModelVertexUv(f, f5, f6, f7, f8);
            modelVertexUvArr[1] = new ModelVertexUv(f4, f5, f6, f11, f8);
            modelVertexUvArr[2] = new ModelVertexUv(f4, f2, f6, f11, f12);
            modelVertexUvArr[3] = new ModelVertexUv(f, f2, f6, f7, f12);
            break;
         case "crypt_uv2":
            modelVertexUvArr[0] = new ModelVertexUv(f4, f5, f6, f7, f8);
            modelVertexUvArr[1] = new ModelVertexUv(f4, f5, f3, f11, f8);
            modelVertexUvArr[2] = new ModelVertexUv(f4, f2, f3, f11, f12);
            modelVertexUvArr[3] = new ModelVertexUv(f4, f2, f6, f7, f12);
            break;
         case "crypt_uv3":
            modelVertexUvArr[0] = new ModelVertexUv(f, f5, f3, f7, f8);
            modelVertexUvArr[1] = new ModelVertexUv(f, f5, f6, f11, f8);
            modelVertexUvArr[2] = new ModelVertexUv(f, f2, f6, f11, f12);
            modelVertexUvArr[3] = new ModelVertexUv(f, f2, f3, f7, f12);
            break;
         case "crypt_uv4":
            modelVertexUvArr[0] = new ModelVertexUv(f, f5, f3, f7, f8);
            modelVertexUvArr[1] = new ModelVertexUv(f, f5, f6, f7, f12);
            modelVertexUvArr[2] = new ModelVertexUv(f4, f5, f6, f11, f12);
            modelVertexUvArr[3] = new ModelVertexUv(f4, f5, f3, f11, f8);
            break;
         case "crypt_uv5":
            modelVertexUvArr[0] = new ModelVertexUv(f4, f2, f3, f7, f8);
            modelVertexUvArr[1] = new ModelVertexUv(f4, f2, f6, f7, f12);
            modelVertexUvArr[2] = new ModelVertexUv(f, f2, f6, f11, f12);
            modelVertexUvArr[3] = new ModelVertexUv(f, f2, f3, f11, f8);
      }

      return modelVertexUvArr;
   }

   private static float[] a(JsonObject jsonObject, String str, float[] fArr) {
      if (!jsonObject.has(str)) {
         return fArr;
      }

      JsonArray asJsonArray = jsonObject.getAsJsonArray(str);
      float[] fArr2 = new float[asJsonArray.size()];

      for (int i = 0; i < asJsonArray.size(); i++) {
         fArr2[i] = asJsonArray.get(i).getAsFloat();
      }

      return fArr2;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
