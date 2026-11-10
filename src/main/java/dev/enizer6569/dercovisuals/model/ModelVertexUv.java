// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.model;

public class ModelVertexUv {
   public ModelVector position;
   public float u;
   public float v;

   public ModelVertexUv(float f, float f2, float f3, float f4, float f5) {
      this(new ModelVector(f, f2, f3), f4, f5);
   }

   public ModelVertexUv(ModelVector modelVector, float f, float f2) {
      this.position = modelVector;
      this.u = f;
      this.v = f2;
   }
}
