package pulse.cosmetic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.MatrixStack.class_4665;
import org.joml.Matrix4f;
import pulse.core.Bool;
import pulse.model.ModelCube;
import pulse.model.ModelDefinition;
import pulse.model.ModelPart;
import pulse.model.ModelTransformApplier;
import pulse.model.ModelVertex;
import pulse.model.ModelVertexUv;
import pulse.render.RenderSystemHelper;
import ru.pulse.Pulse;

public class CosmeticModelRenderer {
   private static CosmeticModelRenderer c;
   private final Map<Integer, ModelDefinition> d = new ConcurrentHashMap<>();
   private final Map<Integer, CosmeticModelRenderer.CompiledModel> e = new ConcurrentHashMap<>();
   private final Set<Integer> f = ConcurrentHashMap.newKeySet();
   private final Map<Integer, Long> g = new ConcurrentHashMap<>();
   private final Map<Integer, Map<String, float[]>> h = new ConcurrentHashMap<>();
   private final CosmeticModelLoader i = new CosmeticModelLoader();
   public static int a;
   public static boolean b;

   public static CosmeticModelRenderer a() {
      if (c == null) {
         c = new CosmeticModelRenderer();
      }

      return c;
   }

   public void a(CosmeticModel cosmeticModel, MatrixStack MatrixStackVar, VertexConsumerProvider VertexConsumerProviderVar, int i) {
      ModelDefinition modelDefinitionA;
      if (cosmeticModel != null && cosmeticModel.e() != null && (modelDefinitionA = this.a(cosmeticModel)) != null) {
         CosmeticModelRenderer.CompiledModel compiledModelB = this.b(cosmeticModel);
         if (compiledModelB != null) {
            this.a(modelDefinitionA, compiledModelB, cosmeticModel.b());
         }

         VertexConsumer buffer = VertexConsumerProviderVar.getBuffer(TexturedRenderLayers.getItemTranslucentCull());
         RenderSystemHelper.disableCull();
         Iterator<ModelPart> it = modelDefinitionA.a.iterator();

         while (it.hasNext()) {
            this.a(it.next(), MatrixStackVar, buffer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
         }

         RenderSystemHelper.enableCull();
      }
   }

   private ModelDefinition a(CosmeticModel cosmeticModel) {
      int iB = cosmeticModel.b();
      if (this.d.containsKey(iB)) {
         return this.d.get(iB);
      }

      ModelDefinition modelDefinitionA = this.i.a(cosmeticModel);
      if (modelDefinitionA != null) {
         this.d.put(iB, modelDefinitionA);
         this.a(iB, modelDefinitionA);
         Pulse.getLOGGER().info("crypt" + cosmeticModel.a());
      }

      return modelDefinitionA;
   }

   private void a(int i, ModelDefinition modelDefinition) {
      HashMap map = new HashMap();
      Iterator<ModelPart> it = modelDefinition.a.iterator();

      while (it.hasNext()) {
         this.a(it.next(), map);
      }

      this.h.put(i, map);
   }

   private void a(ModelPart modelPart, Map<String, float[]> map) {
      map.put(
         modelPart.d,
         new float[]{modelPart.a(), modelPart.b(), modelPart.c(), modelPart.d(), modelPart.e(), modelPart.f(), modelPart.g(), modelPart.h(), modelPart.i()}
      );
      Iterator<ModelPart> it = modelPart.b.iterator();

      while (it.hasNext()) {
         this.a(it.next(), map);
      }
   }

   private void a(ModelPart modelPart, MatrixStack MatrixStackVar, VertexConsumer VertexConsumerVar, int i, int i2, float f, float f2, float f3, float f4) {
      if (!modelPart.e) {
         MatrixStackVar.push();
         ModelTransformApplier.translatePartOffset(modelPart, MatrixStackVar);
         ModelTransformApplier.translatePartPivot(modelPart, MatrixStackVar);
         ModelTransformApplier.rotatePart(modelPart, MatrixStackVar);
         ModelTransformApplier.scalePart(modelPart, MatrixStackVar);
         ModelTransformApplier.translatePartPivotBack(modelPart, MatrixStackVar);
         Iterator<ModelCube> it = modelPart.c.iterator();

         while (it.hasNext()) {
            this.a(it.next(), MatrixStackVar, VertexConsumerVar, i, i2, f, f2, f3, f4);
         }

         Iterator<ModelPart> it2 = modelPart.b.iterator();

         while (it2.hasNext()) {
            this.a(it2.next(), MatrixStackVar, VertexConsumerVar, i, i2, f, f2, f3, f4);
         }

         MatrixStackVar.pop();
      }
   }

   private void a(ModelCube modelCube, MatrixStack MatrixStackVar, VertexConsumer VertexConsumerVar, int i, int i2, float f, float f2, float f3, float f4) {
      MatrixStackVar.push();
      ModelTransformApplier.translateCubePivot(modelCube, MatrixStackVar);
      ModelTransformApplier.rotateCube(modelCube, MatrixStackVar);
      ModelTransformApplier.translateCubePivotBack(modelCube, MatrixStackVar);
      class_4665 class_4665VarPeek = MatrixStackVar.peek();
      Matrix4f matrix4fGetPositionMatrix = class_4665VarPeek.getPositionMatrix();

      for (ModelVertex modelVertex : modelCube.a) {
         if (modelVertex != null) {
            float fA = modelVertex.b.a();
            float fB = modelVertex.b.b();
            float fC = modelVertex.b.c();
            if ((modelCube.b.b() == 0.0F || modelCube.b.c() == 0.0F) && !(fA >= 0.0F)) {
               fA = -fA;
            }

            if ((modelCube.b.a() == 0.0F || modelCube.b.c() == 0.0F) && fB < 0.0F) {
               fB = -fB;
            }

            if ((modelCube.b.a() == 0.0F || modelCube.b.b() == 0.0F) && fC < 0.0F) {
               fC = -fC;
            }

            for (ModelVertexUv modelVertexUv : modelVertex.a) {
               VertexConsumerVar.vertex(matrix4fGetPositionMatrix, modelVertexUv.position.a(), modelVertexUv.position.b(), modelVertexUv.position.c())
                  .color(f, f2, f3, f4)
                  .texture(modelVertexUv.u, modelVertexUv.v)
                  .overlay(i2)
                  .light(i)
                  .normal(class_4665VarPeek, fA, fB, fC);
            }
         }
      }

      MatrixStackVar.pop();
   }

   private CosmeticModelRenderer.CompiledModel b(CosmeticModel cosmeticModel) {
      int iB = cosmeticModel.b();
      if (this.e.containsKey(iB)) {
         return this.e.get(iB);
      }

      if (this.f.contains(iB)) {
         return null;
      }

      JsonObject jsonObjectQ = cosmeticModel.q();
      if (jsonObjectQ == null) {
         this.f.add(iB);
         return null;
      }

      try {
         CosmeticModelRenderer.CompiledModel compiledModelA = this.a(jsonObjectQ);
         if (compiledModelA != null) {
            this.e.put(iB, compiledModelA);
            Pulse.getLOGGER().info("crypt" + cosmeticModel.a());
         } else {
            this.f.add(iB);
         }

         return compiledModelA;
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt" + cosmeticModel.a(), e);
         this.f.add(iB);
         return null;
      }
   }

   private CosmeticModelRenderer.CompiledModel a(JsonObject jsonObject) {
      CosmeticModelRenderer.CompiledModel compiledModel = new CosmeticModelRenderer.CompiledModel();
      if (!jsonObject.has("crypt")) {
         return null;
      }

      Iterator it = jsonObject.getAsJsonObject("crypt").entrySet().iterator();
      if (it.hasNext()) {
         Entry entry = (Entry)it.next();
         String str = (String)entry.getKey();
         JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
         compiledModel.a = str;
         compiledModel.b = Bool.from(asJsonObject.has("crypt") && asJsonObject.get("crypt").getAsBoolean() ? 1 : 0);
         compiledModel.c = !asJsonObject.has("crypt") ? 1.0F : asJsonObject.get("crypt").getAsFloat();
         if (asJsonObject.has("crypt")) {
            for (Entry entry2 : asJsonObject.getAsJsonObject("crypt").entrySet()) {
               String str2 = (String)entry2.getKey();
               JsonObject asJsonObject2 = ((JsonElement)entry2.getValue()).getAsJsonObject();
               CosmeticModelRenderer.PartPoseMap partPoseMap = new CosmeticModelRenderer.PartPoseMap();
               if (asJsonObject2.has("crypt")) {
                  partPoseMap.a = this.a(asJsonObject2.get("crypt"));
               }

               if (asJsonObject2.has("crypt")) {
                  partPoseMap.b = this.a(asJsonObject2.get("crypt"));
               }

               if (asJsonObject2.has("crypt")) {
                  partPoseMap.c = this.a(asJsonObject2.get("crypt"));
               }

               compiledModel.d.put(str2, partPoseMap);
            }
         }
      }

      return compiledModel;
   }

   private Map<Float, float[]> a(JsonElement jsonElement) {
      HashMap map = new HashMap();
      if (jsonElement.isJsonObject()) {
         for (Entry entry : jsonElement.getAsJsonObject().entrySet()) {
            try {
               float f = Float.parseFloat((String)entry.getKey());
               JsonElement jsonElement2 = (JsonElement)entry.getValue();
               float[] fArr = new float[3];
               if (jsonElement2.isJsonObject()) {
                  JsonObject asJsonObject = jsonElement2.getAsJsonObject();
                  if (asJsonObject.has("crypt")) {
                     JsonArray asJsonArray = asJsonObject.getAsJsonArray("crypt");
                     fArr[0] = asJsonArray.get(0).getAsFloat();
                     fArr[1] = asJsonArray.get(1).getAsFloat();
                     fArr[2] = asJsonArray.get(2).getAsFloat();
                  }
               } else if (jsonElement2.isJsonArray()) {
                  JsonArray asJsonArray2 = jsonElement2.getAsJsonArray();
                  fArr[0] = asJsonArray2.get(0).getAsFloat();
                  fArr[1] = asJsonArray2.get(1).getAsFloat();
                  fArr[2] = asJsonArray2.get(2).getAsFloat();
               }

               map.put(f, fArr);
            } catch (NumberFormatException var10) {
            }
         }
      }

      return map;
   }

   private void a(ModelDefinition modelDefinition, CosmeticModelRenderer.CompiledModel compiledModel, int i) {
      Map<String, float[]> map = this.h.get(i);
      if (map != null) {
         float fCurrentTimeMillis = (float)(System.currentTimeMillis() - this.g.computeIfAbsent(i, num -> System.currentTimeMillis())) / 1000.0F;
         float fMin;
         if (!compiledModel.b) {
            fMin = Math.min(fCurrentTimeMillis, compiledModel.c);
         } else if (compiledModel.c > 0.0F) {
            fMin = fCurrentTimeMillis % compiledModel.c;
         } else {
            fMin = Math.min(fCurrentTimeMillis, compiledModel.c);
         }

         Iterator<ModelPart> it = modelDefinition.a.iterator();

         while (it.hasNext()) {
            this.b(it.next(), map);
         }

         for (Entry<String, CosmeticModelRenderer.PartPoseMap> entry : compiledModel.d.entrySet()) {
            String key = entry.getKey();
            CosmeticModelRenderer.PartPoseMap value = entry.getValue();
            ModelPart modelPartA = this.a(modelDefinition, key);
            if (modelPartA != null) {
               float[] fArr = map.get(key);
               if (fArr == null) {
                  float[] fArr2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
                  fArr = fArr2;
               }

               if (!value.a.isEmpty()) {
                  float[] fArrA = this.a(value.a, fMin);
                  modelPartA.a(fArr[0] + (float)Math.toRadians(-fArrA[0]));
                  modelPartA.b(fArr[1] + (float)Math.toRadians(-fArrA[1]));
                  modelPartA.c(fArr[2] + (float)Math.toRadians(fArrA[2]));
               }

               if (!value.b.isEmpty()) {
                  float[] fArrA2 = this.a(value.b, fMin);
                  modelPartA.d(fArr[3] + fArrA2[0]);
                  modelPartA.e(fArr[4] + fArrA2[1]);
                  modelPartA.f(fArr[5] + fArrA2[2]);
               }

               if (!value.c.isEmpty()) {
                  float[] fArrA3 = this.a(value.c, fMin);
                  modelPartA.g(fArr[6] * fArrA3[0]);
                  modelPartA.h(fArr[7] * fArrA3[1]);
                  modelPartA.i(fArr[8] * fArrA3[2]);
               }
            }
         }
      }
   }

   private void b(ModelPart modelPart, Map<String, float[]> map) {
      float[] fArr = map.get(modelPart.d);
      if (fArr != null) {
         modelPart.a(fArr[0]);
         modelPart.b(fArr[1]);
         modelPart.c(fArr[2]);
         modelPart.d(fArr[3]);
         modelPart.e(fArr[4]);
         modelPart.f(fArr[5]);
         modelPart.g(fArr[6]);
         modelPart.h(fArr[7]);
         modelPart.i(fArr[8]);
      }

      Iterator<ModelPart> it = modelPart.b.iterator();

      while (it.hasNext()) {
         this.b(it.next(), map);
      }
   }

   private ModelPart a(ModelDefinition modelDefinition, String str) {
      Iterator<ModelPart> it = modelDefinition.a.iterator();

      while (it.hasNext()) {
         ModelPart modelPartA = this.a(it.next(), str);
         if (modelPartA != null) {
            return modelPartA;
         }
      }

      return null;
   }

   private ModelPart a(ModelPart modelPart, String str) {
      if (modelPart.d.equals(str)) {
         return modelPart;
      }

      Iterator<ModelPart> it = modelPart.b.iterator();

      while (it.hasNext()) {
         ModelPart modelPartA = this.a(it.next(), str);
         if (modelPartA != null) {
            return modelPartA;
         }
      }

      return null;
   }

   private float[] a(Map<Float, float[]> map, float f) {
      if (map.isEmpty()) {
         return new float[]{0.0F, 0.0F, 0.0F};
      }

      Float fValueOf = null;
      Float fValueOf2 = null;
      float[] value = null;
      float[] value2 = null;

      for (Entry<Float, float[]> entry : map.entrySet()) {
         float fFloatValue = entry.getKey();
         if (fFloatValue <= f && (fValueOf == null || fFloatValue > fValueOf)) {
            fValueOf = fFloatValue;
            value = entry.getValue();
         }

         if (fFloatValue >= f && (fValueOf2 == null || fFloatValue < fValueOf2)) {
            fValueOf2 = fFloatValue;
            value2 = entry.getValue();
         }
      }

      if (value == null && value2 == null) {
         return new float[]{0.0F, 0.0F, 0.0F};
      } else if (value == null) {
         return value2;
      } else if (value2 != null && !fValueOf.equals(fValueOf2)) {
         float fFloatValue2 = (f - fValueOf) / (fValueOf2 - fValueOf);
         return new float[]{
            value[0] + fFloatValue2 * (value2[0] - value[0]),
            value[1] + fFloatValue2 * (value2[1] - value[1]),
            value[2] + fFloatValue2 * (value2[2] - value[2])
         };
      } else {
         return value;
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class CompiledModel {
      String a;
      boolean b;
      float c;
      Map<String, CosmeticModelRenderer.PartPoseMap> d = new HashMap<>();
      public static int e;
      public static boolean f;

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class PartPoseMap {
      Map<Float, float[]> a = new HashMap<>();
      Map<Float, float[]> b = new HashMap<>();
      Map<Float, float[]> c = new HashMap<>();
      public static int d;
      public static boolean e;

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
