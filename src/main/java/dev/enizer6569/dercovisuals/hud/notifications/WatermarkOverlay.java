// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.notifications;

import java.awt.Color;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.hud.core.HudIcons;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;

public class WatermarkOverlay extends HudNotification {
   private static final String DOMAIN = "@soezproject";
   private static final float ICON_SIZE = 16.0F;
   private static final float GAP = 7.0F;
   private static final int FONT_SIZE = 14;
   private static final float RADIUS = 12.0F;
   private static final Color CARD_TOP = new Color(8, 8, 11, 235);
   private static final Color CARD_BOTTOM = new Color(5, 5, 8, 240);
   private static final Color ICON_TINT = new Color(255, 255, 255, 255);
   private static final Color TEXT = new Color(255, 255, 255, 255);
   private static final Color MUTED = new Color(145, 145, 160, 220);

   public WatermarkOverlay() {
      this.g = 11.0F;
      this.h = 4.0F;
   }

   @Override
   public float a() {
      return FontManager.MEDIUM == null ? 50.0F : this.measureTextWidth();
   }

   @Override
   public float b() {
      return 16.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, float f5) {
      if (FontManager.MEDIUM != null) {
         if (!(f5 < 0.01F)) {
            float f6 = f2 + f4 / 2.0F;
            float iconY = f6 - 8.0F;
            HudIcons.drawWatermarkIcon(renderer2D, MatrixStackVar, f, iconY, 16.0F, this.a(ICON_TINT, f5));
            this.drawSegments(MatrixStackVar, f + 16.0F + 7.0F, f6, f5);
         }
      }
   }

   private void drawSegments(Matrix3x2fStack MatrixStackVar, float f, float f2, float f3) {
      FontRenderer fontRenderer = FontManager.MEDIUM[14];
      float fB = f2 - fontRenderer.b("pulse @Alex_sigma") / 4.0F;
      if (ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.fpsAndPing().a()) {
         this.drawPart(
            fontRenderer,
            this.getFps() + " FPS",
            this.drawPart(
               fontRenderer,
               " / ",
               this.drawPart(
                  fontRenderer,
                  this.getPingMs() + " ms",
                  this.drawPart(
                     fontRenderer, " / ", this.drawPart(fontRenderer, "pulse @Alex_sigma", f, fB, TEXT, f3, MatrixStackVar), fB, MUTED, f3, MatrixStackVar
                  ),
                  fB,
                  TEXT,
                  f3,
                  MatrixStackVar
               ),
               fB,
               MUTED,
               f3,
               MatrixStackVar
            ),
            fB,
            TEXT,
            f3,
            MatrixStackVar
         );
      } else {
         this.drawPart(fontRenderer, "pulse @Alex_sigma", f, fB, TEXT, f3, MatrixStackVar);
      }
   }

   private float drawPart(FontRenderer fontRenderer, String str, float f, float f2, Color color, float f3, Matrix3x2fStack MatrixStackVar) {
      fontRenderer.a(str, f, f2, this.a(color, f3), MatrixStackVar);
      return f + fontRenderer.a(str);
   }

   private float measureTextWidth() {
      FontRenderer fontRenderer = FontManager.MEDIUM[14];
      return ModuleRegistry.WATERMARK == null || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.fpsAndPing().a()
         ? 23.0F
            + fontRenderer.a("pulse @Alex_sigma")
            + fontRenderer.a(" / ")
            + fontRenderer.a(this.getPingMs() + " ms")
            + fontRenderer.a(" / ")
            + fontRenderer.a(this.getFps() + " FPS")
         : 23.0F + fontRenderer.a("pulse @Alex_sigma");
   }

   private int getFps() {
      return Math.max(0, c.getCurrentFps());
   }

   private int getPingMs() {
      ClientPlayNetworkHandler ClientPlayNetworkHandlerVarGetNetworkHandler;
      PlayerListEntry PlayerListEntryVarGetPlayerListEntry;
      return c.player != null
            && (ClientPlayNetworkHandlerVarGetNetworkHandler = c.getNetworkHandler()) != null
            && (PlayerListEntryVarGetPlayerListEntry = ClientPlayNetworkHandlerVarGetNetworkHandler.getPlayerListEntry(c.player.getUuid())) != null
         ? PlayerListEntryVarGetPlayerListEntry.getLatency()
         : 0;
   }
}
