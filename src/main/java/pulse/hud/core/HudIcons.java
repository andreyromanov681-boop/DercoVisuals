package pulse.hud.core;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.render.Renderer2D;
import pulse.render.icons.IconTextureRegistry;
import ru.pulse.Pulse;

public final class HudIcons {
   private static boolean loggedWatermarkIcon = false;

   private HudIcons() {
   }

   public static Identifier clickGui(String str) {
      Identifier IdentifierVar = IconTextureRegistry.get(str);
      return IdentifierVar != null ? IdentifierVar : Identifier.of("pulse", "textures/clickgui/" + str);
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

   public static void drawPulseMark(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, float f, float f2, float f3, Color color) {
      Identifier IdentifierVar = IconTextureRegistry.get("pulse_ico");
      if (IdentifierVar == null) {
         IdentifierVar = IconTextureRegistry.get("logo");
      }

      if (IdentifierVar != null && color.getAlpha() >= 1) {
         renderer2D.a(IdentifierVar, f, f2, f3, f3, color, MatrixStackVar);
      }
   }

   public static void drawWatermarkIcon(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, float f, float f2, float f3, Color color) {
      Identifier IdentifierVar = IconTextureRegistry.get("pulse_ico");
      if (IdentifierVar == null) {
         IdentifierVar = IconTextureRegistry.get("logo");
      }

      if (IdentifierVar != null && color.getAlpha() >= 1) {
         if (!loggedWatermarkIcon) {
            loggedWatermarkIcon = true;
            Pulse.getLOGGER()
               .info(
                  "[PulseIcons] drawWatermarkIcon -> identifier={} size={} tint=rgba({},{},{},{})",
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
