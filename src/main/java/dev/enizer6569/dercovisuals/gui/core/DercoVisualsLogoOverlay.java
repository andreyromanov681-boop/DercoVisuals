// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.core;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;

public class DercoVisualsLogoOverlay implements ClickGuiOverlay {
   private static final float LOGO_TOP_PADDING = -2.0F;
   private static final float LOGO_HEIGHT = 44.0F;
   private static final float TEX_W = 225.0F;
   private static final float TEX_H = 87.0F;
   private static final float LOGO_WIDTH = 113.793106F;

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      Identifier icon = IconTextureRegistry.get("dercovisuals_logo");
      if (icon == null) {
         icon = IconTextureRegistry.get("dercovisuals_ico");
      }

      FontRenderer font = FontManager.b[18];
      if (font == null) {
         font = FontManager.b[15];
      }

      String subText = "DercoVisuals visual @alex_sigma";
      float logoX = f + 148.60345F;
      float logoY = f2 - 44.0F - -2.0F;
      renderer2D.a(icon, logoX, logoY, 113.793106F, 44.0F, new Color(255, 255, 255), MatrixStackVar);
      if (font != null) {
         float textX = logoX + 113.793106F + 14.0F;
         float textY = logoY + 22.0F + 4.0F;
         font.a(subText, textX, textY, new Color(180, 180, 195, 190), MatrixStackVar);
      }
   }

   @Override
   public void a(float f, float f2, int i, int i2) {
   }
}
