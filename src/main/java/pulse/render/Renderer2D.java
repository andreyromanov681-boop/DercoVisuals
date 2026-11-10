package pulse.render;

import java.awt.Color;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public interface Renderer2D {
   default void a(float f, float f2, float f3, float f4, Color color, Object MatrixStackVar) {
      this.a(f, f2, f3, f4, 0.0F, color, MatrixStackVar);
   }

   void a(float var1, float var2, float var3, float var4, float var5, Color var6, Object var7);

   default void a(float f, float f2, float f3, float f4, float f5, Color color, Color color2, Color color3, Color color4, Object MatrixStackVar) {
      this.a(f, f2, f3, f4, f5, f5, f5, f5, color, color2, color3, color4, MatrixStackVar);
   }

   void a(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      Color var9,
      Color var10,
      Color var11,
      Color var12,
      Object var13
   );

   void a(float var1, float var2, float var3, Color var4, Object var5);

   void a(float var1, float var2, float var3, float var4, float var5, float var6, Color var7, Object var8);

   default void b(float f, float f2, float f3, float f4, float f5, Color color, Object MatrixStackVar) {
      this.b(f, f2, f3, f4, 0.0F, f5, color, MatrixStackVar);
   }

   void a(float var1, float var2, float var3, float var4, Object var5);

   Window a();

   void b(float var1, float var2, float var3, float var4, float var5, float var6, Color var7, Object var8);

   void a(int var1, float var2, float var3, float var4, float var5, Color var6, Object var7);

   void a(Identifier var1, float var2, float var3, float var4, float var5, Color var6, Object var7);

   default void a(Identifier IdentifierVar, float f, float f2, float f3, float f4, float f5, Color color, Object MatrixStackVar) {
      this.a(IdentifierVar, f, f2, f3, f4, f5, 0.0F, 0.0F, 1.0F, 1.0F, color, MatrixStackVar);
   }

   void a(
      Identifier var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, Color var11, Object var12
   );

   default Color a(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp((int)(f * 255.0F), 0, 255));
   }

   default Color a(Color color, int i) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(i, 0, 255));
   }

   void b(float var1, float var2, float var3, float var4, Object var5);

   default void a(float f, float f2, float f3, float f4, float f5, Object MatrixStackVar) {
      this.b(f, f2, f3, f4, MatrixStackVar);
   }

   void a(Object var1);

   ScissorStack b();

   void a(float var1, float var2, float var3, float var4, float var5, float var6, Object var7);

   void c(float var1, float var2, float var3, float var4, Object var5);

   void a(float var1, Object var2);
}
