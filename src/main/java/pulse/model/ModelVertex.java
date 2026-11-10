package pulse.model;

public class ModelVertex {
   public ModelVertexUv[] a;
   public ModelVector b;

   public ModelVertex(ModelVertexUv[] modelVertexUvArr, ModelVector modelVector) {
      this.a = modelVertexUvArr;
      this.b = modelVector;
   }

   public ModelVertex(ModelVertexUv[] modelVertexUvArr, float f, float f2, float f3) {
      this(modelVertexUvArr, new ModelVector(f, f2, f3));
   }
}
