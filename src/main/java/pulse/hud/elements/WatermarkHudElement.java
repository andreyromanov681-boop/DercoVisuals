package pulse.hud.elements;

import java.awt.Color;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.hud.core.HudElement;
import pulse.module.ModuleRegistry;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.render.icons.IconTextureRegistry;

public class WatermarkHudElement extends HudElement {
   private static final String TITLE = "pulse @Alex_sigma";
   private static final float PAD_X = 8.5F;
   private static final float PAD_Y = 6.0F;
   private static final float ICON_BASE = 14.0F;
   private static final float ICON_GAP = 5.0F;
   private static final float RADIUS = 6.5F;
   private static final Color CARD_TOP = new Color(15, 15, 26, 232);
   private static final Color CARD_BOTTOM = new Color(9, 9, 18, 236);
   private boolean settingsBound;

   public WatermarkHudElement(float f, float f2) {
      super(f, f2);
   }

   @Override
   protected void a() {
      this.bindSettings();
      float fG = this.g();
      FontRenderer fontRenderer = FontManager.b[Math.max(6, Math.min(16, Math.round(14.0F * fG)))];
      float f = 14.0F * fG;
      float fA = 8.5F * fG + f + 5.0F * fG + fontRenderer.a("pulse @Alex_sigma") + 8.5F * fG;
      float fMax = 14.0F * fG + Math.max(f, fontRenderer.b("pulse @Alex_sigma"));
      this.d = fA;
      this.e = fMax;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      if (this.a.player != null) {
         this.a();
         float fG = this.g();
         float f3 = this.b;
         float f4 = this.c;
         float f5 = this.d;
         float f6 = this.e;
         float f7 = 6.5F * fG;
         float f8 = 8.5F * fG;
         float f9 = 14.0F * fG;
         float f10 = 5.0F * fG;
         Color colorWithAlpha = withAlpha(CARD_TOP, 1.0F);
         Color colorWithAlpha2 = withAlpha(CARD_BOTTOM, 1.0F);
         renderer2D.a(f3, f4, f5, f6, f7, colorWithAlpha, colorWithAlpha, colorWithAlpha2, colorWithAlpha2, MatrixStackVar);
         float iconX = f3 + f8;
         float iconY = f4 + (f6 - f9) / 2.0F;
         Identifier id = IconTextureRegistry.get("pulse_ico");
         if (id == null) {
            id = IconTextureRegistry.get("logo");
         }

         renderer2D.a(id, iconX, iconY, f9, f9, withAlpha(new Color(130, 95, 255), 1.0F), MatrixStackVar);
         FontRenderer pulseFont = FontManager.b[Math.max(6, Math.min(16, Math.round(14.0F * fG)))];
         pulseFont.a("pulse @Alex_sigma", iconX + f9 + f10, f4 + f6 / 2.0F - pulseFont.b("pulse @Alex_sigma") / 4.0F, withAlpha(Color.WHITE, 1.0F), MatrixStackVar);
      }
   }

   private void bindSettings() {
      if (!this.settingsBound && ModuleRegistry.WATERMARK != null) {
         this.f().a(ModuleRegistry.WATERMARK);
         this.settingsBound = true;
      }
   }

   private static Color withAlpha(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, Math.round(color.getAlpha() * f))));
   }
}
