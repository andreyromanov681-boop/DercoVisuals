package pulse.gui.core;

import org.joml.Matrix3x2fStack;
import pulse.render.Renderer2D;

public interface ClickGuiTab {
   void a(Matrix3x2fStack var1, Renderer2D var2, float var3, float var4, int var5, int var6);

   void a(float var1, float var2, int var3, int var4);

   void b(float var1, float var2, int var3, int var4);

   void c(float var1, float var2, int var3, int var4);

   void a(float var1, float var2, int var3, int var4, double var5, double var7);

   void a(float var1);

   ClickGuiTabType a();

   default boolean a(int i, int i2, int i3) {
      return false;
   }

   default boolean b() {
      return false;
   }
}
