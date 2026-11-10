package pulse.model;

public class ModelVector {
   public static final ModelVector ZERO = new ModelVector(0.0F, 0.0F, 0.0F);
   public float x;
   public float y;
   public float z;

   public ModelVector(float f, float f2, float f3) {
      this.x = f;
      this.y = f2;
      this.z = f3;
   }

   public float a() {
      return this.x;
   }

   public float b() {
      return this.y;
   }

   public float c() {
      return this.z;
   }

   public void a(float f) {
      this.x = f;
   }

   public void b(float f) {
      this.y = f;
   }

   public void c(float f) {
      this.z = f;
   }

   public void a(float f, float f2, float f3) {
      this.x = f;
      this.y = f2;
      this.z = f3;
   }

   public ModelVector a(ModelVector modelVector) {
      return new ModelVector(
         this.y * modelVector.z - this.z * modelVector.y, this.z * modelVector.x - this.x * modelVector.z, this.x * modelVector.y - this.y * modelVector.x
      );
   }

   @Override
   public String toString() {
      return this.x + ", " + this.y + ", " + this.z;
   }
}
