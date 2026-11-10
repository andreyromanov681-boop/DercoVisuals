package pulse.model;

public class ModelCube {
   public ModelVector b;
   public ModelVertex[] a = new ModelVertex[6];
   public ModelVector c = new ModelVector(0.0F, 0.0F, 0.0F);
   public ModelVector d = new ModelVector(0.0F, 0.0F, 0.0F);
   public float e = 0.0F;
   public boolean f = false;

   public ModelCube(float f, float f2, float f3) {
      this.b = new ModelVector(f, f2, f3);
   }
}
