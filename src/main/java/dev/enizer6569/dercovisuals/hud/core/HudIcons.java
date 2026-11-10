// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.core;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;
import dev.enizer6569.dercovisuals.DercoVisuals;

public final class HudIcons {
   private static boolean loggedWatermarkIcon = false;

   private HudIcons() {
   }

   public static Identifier clickGui(String str) {
      Identifier IdentifierVar = IconTextureRegistry.get(str);
      return IdentifierVar != null ? IdentifierVar : Identifier.of("dercovisuals", "textures/clickgui/" + str);
   }

   public static void draw(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, Identifier IdentifierVar, float f, float f2, float f3, Color color) {
      if (renderer2D != null && IdentifierVar != null && color.getAlpha() >= 1) {
         renderer2D.a(IdentifierVar, f, f2, f3, f3, color, MatrixStackVar);
      }
   }

   public static void drawFlippedVertical(
      Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, Identifier IdentifierVar, float f, float f2, float f3, Color color
   ) {
      if (renderer2D != null && IdentifierVar != null && color.getAlpha() >= 1) {
         float cy = f2 + f3 / 2.0F;
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(0.0F, cy);
         MatrixStackVar.scale(1.0F, -1.0F);
         MatrixStackVar.translate(0.0F, -cy);
         renderer2D.a(IdentifierVar, f, f2, f3, f3, color, MatrixStackVar);
         MatrixStackVar.popMatrix();
      }
   }

   public static void drawRotated180(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, Identifier IdentifierVar, float f, float f2, float f3, Color color) {
      if (renderer2D != null && IdentifierVar != null && color.getAlpha() >= 1) {
         float cx = f + f3 / 2.0F;
         float cy = f2 + f3 / 2.0F;
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(cx, cy);
         MatrixStackVar.scale(-1.0F, -1.0F);
         MatrixStackVar.translate(-cx, -cy);
         renderer2D.a(IdentifierVar, f, f2, f3, f3, color, MatrixStackVar);
         MatrixStackVar.popMatrix();
      }
   }

   public static void drawDercoVisualsMark(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, float f, float f2, float f3, Color color) {
      Identifier IdentifierVar = IconTextureRegistry.get("dercovisuals_ico");
      if (IdentifierVar == null) {
         IdentifierVar = IconTextureRegistry.get("logo");
      }

      if (IdentifierVar != null && color.getAlpha() >= 1) {
         renderer2D.a(IdentifierVar, f, f2, f3, f3, color, MatrixStackVar);
      }
   }

   public static void drawWatermarkIcon(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, float f, float f2, float f3, Color color) {
      Identifier IdentifierVar = IconTextureRegistry.get("dercovisuals_ico");
      if (IdentifierVar == null) {
         IdentifierVar = IconTextureRegistry.get("logo");
      }

      if (IdentifierVar != null && color.getAlpha() >= 1) {
         if (!loggedWatermarkIcon) {
            loggedWatermarkIcon = true;
            DercoVisuals.getLOGGER()
               .info(
                  "[DercoVisualsIcons] drawWatermarkIcon -> identifier={} size={} tint=rgba({},{},{},{})",
                  IdentifierVar,
                  f3,
                  color.getRed(),
                  color.getGreen(),
                  color.getBlue(),
                  color.getAlpha()
               );
         }

         renderer2D.a(IdentifierVar, f, f2, f3, f3, color, MatrixStackVar);
      }
   }
}
