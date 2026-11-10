package pulse.render;

import java.util.Stack;
import org.joml.Matrix3x2fStack;

public class ScissorStack {
   private final Renderer2D a;
   private final Stack<ScissorStack.ScissorBounds> b = new Stack<>();

   public ScissorStack(Renderer2D renderer2D) {
      this.a = renderer2D;
   }

   public void a(float f, float f2, float f3, float f4, Matrix3x2fStack MatrixStackVar) {
      this.a(f, f2, f3, f4, 0.0F, MatrixStackVar);
   }

   public void a(float f, float f2, float f3, float f4, float f5, Matrix3x2fStack MatrixStackVar) {
      if (this.b.isEmpty()) {
         this.b.push(new ScissorStack.ScissorBounds(f, f2, f3, f4, f5));
         this.a.a(f, f2, f3, f4, f5, MatrixStackVar);
      } else {
         ScissorStack.ScissorBounds scissorBoundsPeek = this.b.peek();
         float fMax = Math.max(scissorBoundsPeek.a(), f);
         float fMax2 = Math.max(scissorBoundsPeek.b(), f2);
         float fMin = Math.min(scissorBoundsPeek.a() + scissorBoundsPeek.c(), f + f3) - fMax;
         float fMin2 = Math.min(scissorBoundsPeek.b() + scissorBoundsPeek.d(), f2 + f4) - fMax2;
         if (!(fMin <= 0.0F) && !(fMin2 <= 0.0F)) {
            this.a.a(MatrixStackVar);
            this.a.a(fMax, fMax2, fMin, fMin2, f5, MatrixStackVar);
            this.b.push(new ScissorStack.ScissorBounds(fMax, fMax2, fMin, fMin2, f5));
         } else {
            this.b.push(new ScissorStack.ScissorBounds(scissorBoundsPeek.a(), scissorBoundsPeek.b(), 0.0F, 0.0F, f5));
         }
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar) {
      if (!this.b.isEmpty()) {
         this.b.pop();
         this.a.a(MatrixStackVar);
         if (!this.b.isEmpty()) {
            ScissorStack.ScissorBounds scissorBoundsPeek = this.b.peek();
            this.a.a(scissorBoundsPeek.a(), scissorBoundsPeek.b(), scissorBoundsPeek.c(), scissorBoundsPeek.d(), scissorBoundsPeek.e(), MatrixStackVar);
         }
      }
   }

   private static class ScissorBounds {
      private final float a;
      private final float b;
      private final float c;
      private final float d;
      private final float e;

      public ScissorBounds(float f, float f2, float f3, float f4, float f5) {
         this.a = f;
         this.b = f2;
         this.c = f3;
         this.d = f4;
         this.e = f5;
      }

      public float a() {
         return this.a;
      }

      public float b() {
         return this.b;
      }

      public float c() {
         return this.c;
      }

      public float d() {
         return this.d;
      }

      public float e() {
         return this.e;
      }
   }
}
