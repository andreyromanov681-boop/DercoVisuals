package pulse.gui.settings;

import org.joml.Matrix3x2fStack;
import pulse.render.Renderer2D;

public interface SettingWidget {
   String a();

   float b();

   void a(Matrix3x2fStack var1, Renderer2D var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9);

   boolean a(float var1, float var2, float var3, int var4, int var5);

   default boolean b(float f, float f2, float f3, int i, int i2) {
      return false;
   }

   default void a(int i, int i2) {
   }

   default void a(int i, int i2, double d, double d2) {
   }

   default boolean a(int i, int i2, int i3) {
      return false;
   }

   default boolean a(char c, int i) {
      return false;
   }

   default boolean a_() {
      return false;
   }

   default void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, int i, int i2, float f5, float f6) {
   }

   default boolean j() {
      return false;
   }

   default boolean l() {
      return false;
   }

   default boolean d() {
      return true;
   }
}
