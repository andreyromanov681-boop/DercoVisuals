// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.core;

import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.render.Renderer2D;

public interface ClickGuiOverlay {
   void a(Matrix3x2fStack var1, Renderer2D var2, float var3, float var4, int var5, int var6);

   void a(float var1, float var2, int var3, int var4);

   default void b(float f, float f2, int i, int i2) {
   }
}
