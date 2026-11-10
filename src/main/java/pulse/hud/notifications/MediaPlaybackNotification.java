package pulse.hud.notifications;

import java.awt.Color;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;

public class MediaPlaybackNotification extends HudNotification {
   private static final Color m = new Color(255, 255, 255);
   private static final Color n = new Color(223, 223, 243);
   private static final Color o = new Color(255, 255, 255, 77);
   private static final Color p = new Color(76, 46, 212, 100);
   private static final Color q = new Color(115, 83, 255);
   private static final Color r = new Color(70, 46, 174);
   private static final float s = 30.0F;
   private static final float t = 5.0F;
   private static final float u = 5.0F;
   private final String v = "crypt";
   private final AnimationState w = new AnimationState();
   private boolean x = true;
   public static int a;
   public static boolean b;

   public MediaPlaybackNotification() {
      this.g = 7.0F;
      this.h = 3.0F;
      this.j = Long.MAX_VALUE;
      this.w.d(1.0);
   }

   private boolean m() {
      HudNotificationCenter hudNotificationCenterA = HudNotificationCenter.a();
      return Bool.from(hudNotificationCenterA != null && !hudNotificationCenterA.i().j() ? 0 : 1);
   }

   @Override
   public float a() {
      FontRenderer fontRenderer = FontManager.b[15];
      FontRenderer fontRenderer2 = FontManager.a[15];
      float fA = 20.0F + fontRenderer.a("crypt") + 1.5F;
      boolean zM = this.m();
      if (zM != this.x) {
         this.x = zM;
         double d;
         if (zM) {
            d = 1.0;
         } else {
            d = 0.0;
         }

         this.w.a(d, 0.15, Easing.k);
      }

      this.w.a();
      float fJ = (float)this.w.j();
      if (fJ < 0.01F) {
         return fA;
      }

      float fA2 = fontRenderer2.a("crypt");
      int iO = this.o();
      int iN = this.n();
      String str = iO + "crypt";
      String str2 = iN + "crypt";
      return fA + (5.0F + fA2 + 5.0F + fontRenderer2.a(str) + 5.0F + fA2 + 5.0F + fontRenderer2.a(str2)) * fJ;
   }

   @Override
   public float b() {
      return 16.0F;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[15];
      FontRenderer fontRenderer2 = FontManager.a[15];
      float f6 = f2 + f4 / 2.0F;
      int i = (int)(f5 * 255.0F);
      FontRenderer fontRenderer3 = FontManager.e[30];
      float fA = fontRenderer3.a("crypt");
      float fB = f6 - fontRenderer3.b("crypt") / 4.0F;
      renderer2D.a(f + 1.0F, fB + 2.0F, 6.5F, this.a(p, f5), MatrixStackVar);
      fontRenderer3.a("crypt", f + 1.0F, fB, this.a(q, f5), this.a(r, f5), MatrixStackVar);
      float f7 = f + fA + 5.0F;
      Color color = new Color(m.getRed(), m.getGreen(), m.getBlue(), i);
      new Color(n.getRed(), n.getGreen(), n.getBlue(), i);
      new Color(o.getRed(), o.getGreen(), o.getBlue(), (int)(o.getAlpha() * f5));
      float fB2 = f6 - fontRenderer.b("crypt") / 4.0F - 0.5F;
      fontRenderer.a("crypt", f7, fB2, color, MatrixStackVar);
      float fJ = (float)this.w.j();
      if (fJ >= 0.01F) {
         float f8 = f5 * fJ;
         Color color2 = new Color(n.getRed(), n.getGreen(), n.getBlue(), (int)(f8 * 255.0F));
         Color color3 = new Color(o.getRed(), o.getGreen(), o.getBlue(), (int)(o.getAlpha() * f8));
         float fA2 = f7 + fontRenderer.a("crypt") + 5.0F;
         fontRenderer2.a("crypt", fA2, fB2, color3, MatrixStackVar);
         float fA3 = fA2 + fontRenderer2.a("crypt") + 5.0F;
         String str = this.o() + "crypt";
         fontRenderer2.a(str, fA3, fB2, color2, MatrixStackVar);
         float fA4 = fA3 + fontRenderer2.a(str) + 5.0F;
         fontRenderer2.a("ﴋ⢺ﵮ", fA4, fB2, color3, MatrixStackVar);
         fontRenderer2.a(this.n() + "crypt", fA4 + fontRenderer2.a("crypt") + 5.0F, fB2, color2, MatrixStackVar);
      }
   }

   private int n() {
      if (c == null) {
         return 0;
      }

      try {
         return c.getCurrentFps();
      } catch (Exception e) {
         return 0;
      }
   }

   private int o() {
      ClientPlayNetworkHandler ClientPlayNetworkHandlerVarGetNetworkHandler;
      PlayerListEntry PlayerListEntryVarGetPlayerListEntry;
      return c.player != null
            && (ClientPlayNetworkHandlerVarGetNetworkHandler = c.getNetworkHandler()) != null
            && (PlayerListEntryVarGetPlayerListEntry = ClientPlayNetworkHandlerVarGetNetworkHandler.getPlayerListEntry(c.player.getUuid())) != null
         ? PlayerListEntryVarGetPlayerListEntry.getLatency()
         : 0;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
