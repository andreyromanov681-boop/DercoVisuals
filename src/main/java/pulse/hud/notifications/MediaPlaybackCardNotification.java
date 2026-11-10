package pulse.hud.notifications;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.hud.core.HudServiceRegistry;
import pulse.media.MediaPlaybackState;
import pulse.media.MediaSessionService;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.render.icons.IconTextureRegistry;

public class MediaPlaybackCardNotification extends HudNotification {
   private static final Color m = new Color(255, 255, 255);
   private static final Color n = new Color(223, 223, 243);
   private static final Color o = new Color(49, 49, 69);
   private static final Color p = new Color(49, 49, 69);
   private static final Color q = new Color(255, 255, 255);
   private static final Color r = new Color(15, 15, 20);
   private static final float s = 12.0F;
   private static final float t = 8.0F;
   private static final float u = 24.0F;
   private static final float v = 6.0F;
   private static final float w = 8.0F;
   private static final float x = 5.0F;
   private static final float y = 25.0F;
   private static final float z = 103.0F;
   private static final float A = 147.5F;
   private static final float B = 65.0F;
   private static final float C = 9.5F;
   private static final float D = 7.0F;
   private static final float E = 31.0F;
   private static final float F = 10.0F;
   private static final float G = 7.5F;
   private static final float H = 70.0F;
   private static final float I = 12.0F;
   private static final float J = 20.0F;
   private static final long K = 2500L;
   private static final long L = 1500L;
   private static final long M = 8000L;
   private static final int N = 6;
   private static final float O = 2.0F;
   private static final float P = 0.5F;
   private static final float Q = 12.0F;
   private static final float R = 16.0F;
   private static final float S = 2.0F;
   private static final float T = 1.0F;
   private static final float U = 4.0F;
   private static final float V = 3.0F;
   private static final float W = 32.0F;
   private static final float X = 32.0F;
   private static final float Y = 0.0F;
   private float ae;
   private float af;
   private float ag;
   private float ah;
   private float ai;
   private float aj;
   private float ak;
   private float al;
   private float am;
   private float an;
   private float ConfigState;
   private float ConfigProfile;
   private float ConfigJsonCodec;
   private float ConfigManager;
   private float at;
   private float au;
   private float CloudConfigRepository;
   private boolean aA;
   private boolean aB;
   private boolean aC;
   private static final double aT = 0.25;
   public static int a;
   public static boolean b;
   private byte[] Z = null;
   private Identifier aa = null;
   private NativeImageBackedTexture FriendCard = null;
   private boolean FriendsPanel = false;
   private final AnimationState ad = new AnimationState();
   private boolean ar = false;
   private boolean aw = false;
   private final AnimationState ax = new AnimationState();
   private final AnimationState ay = new AnimationState();
   private final AnimationState az = new AnimationState();
   private final AnimationState aD = new AnimationState();
   private final AnimationState aE = new AnimationState();
   private final AnimationState aF = new AnimationState();
   private final AnimationState aG = new AnimationState();
   private boolean aH = false;
   private final AnimationState aI = new AnimationState();
   private final AnimationState aJ = new AnimationState();
   private String aK = "";
   private final AnimationState aL = new AnimationState();
   private boolean aM = false;
   private double aN = 0.0;
   private double aO = 0.0;
   private long aP = 0L;
   private long aQ = 0L;
   private boolean aR = false;
   private boolean aS = false;

   public MediaPlaybackCardNotification() {
      this.g = 9.5F;
      this.h = 7.0F;
      this.j = Long.MAX_VALUE;
      this.ad.d(0.0);
      this.ax.d(0.0);
      this.ay.d(0.0);
      this.az.d(0.0);
      this.aD.d(0.0);
      this.aE.d(0.0);
      this.aF.d(0.0);
      this.aG.d(0.0);
      this.aI.d(0.0);
      this.aJ.d(1.0);
      this.aL.d(0.0);
   }

   private MediaSessionService q() {
      return HudServiceRegistry.MEDIA;
   }

   private MediaPlaybackState r() {
      MediaSessionService mediaSessionServiceQ = this.q();
      return mediaSessionServiceQ != null ? mediaSessionServiceQ.i() : null;
   }

   @Override
   public void e() {
      super.e();
      this.ad.a();
      this.g = 9.5F;
      this.h = 7.0F;
      float fJ = (float)this.ad.j();
      MediaPlaybackState mediaPlaybackStateR = this.r();
      if (mediaPlaybackStateR != null) {
         byte[] bArrP = mediaPlaybackStateR.p();
         if (!mediaPlaybackStateR.q()) {
            if (this.aa == null && this.Z == null && bArrP != null && bArrP.length > 0) {
               this.a(bArrP);
            }
         } else {
            if (bArrP != null && bArrP.length > 0) {
               this.a(bArrP);
            } else {
               this.aa = null;
               this.Z = null;
            }

            mediaPlaybackStateR.f();
         }

         this.ax.a();
         this.ay.a();
         this.az.a();
         this.aD.a();
         this.aE.a();
         this.aF.a();
         this.aG.a();
         this.aI.a();
         this.aJ.a();
         this.aL.a();
         boolean zC = mediaPlaybackStateR.c();
         if (zC != this.aH) {
            this.aH = zC;
            this.aG.a(!zC ? 0.0 : 1.0, 0.2, Easing.k);
         }

         boolean z3 = !zC;
         if (z3 && this.aI.j() < 0.5) {
            this.aI.a(1.0, 0.2, Easing.k);
         } else if (!z3 && this.aI.j() > 0.5) {
            this.aI.a(0.0, 0.2, Easing.k);
         }

         String strN = mediaPlaybackStateR.n();
         if (!strN.equals(this.aK)) {
            this.aK = strN;
            this.aJ.d(0.0);
            this.aJ.a(1.0, 0.25, Easing.k);
            this.aR = false;
            this.aS = false;
         }

         FontRenderer fontRenderer = FontManager.b[14];
         FontRenderer fontRenderer2 = FontManager.b[15];
         FontRenderer fontRenderer3 = FontManager.a[13];
         boolean z2;
         if ((fJ >= 0.2F ? fontRenderer2 : fontRenderer).a(strN) <= this.a(25.0F, 70.0F, fJ)) {
            z2 = false;
         } else {
            z2 = true;
         }

         boolean z4 = z2;
         if (z4 && !this.aR) {
            this.aR = true;
            this.aP = System.currentTimeMillis();
         } else if (!z4) {
            this.aR = false;
         }

         boolean z5 = fontRenderer3.a(mediaPlaybackStateR.o()) > 70.0F && fJ > 0.3F;
         if (z5 && !this.aS) {
            this.aS = true;
            this.aQ = System.currentTimeMillis();
         } else {
            if (z5) {
               return;
            }

            this.aS = false;
         }
      }
   }

   private void s() {
      MediaPlaybackState mediaPlaybackStateR = this.r();
      if (this.ar && mediaPlaybackStateR != null) {
         double d2 = this.aN;
         double d3 = this.aO;
         boolean z2 = this.aA;
         this.aA = this.a(d2, d3, this.ai, this.aj, this.ak, this.ak);
         if (this.aA != z2) {
            this.ax.a(!this.aA ? 0.0 : 1.0, 0.15, Easing.k);
         }

         boolean z3 = this.aB;
         this.aB = this.a(d2, d3, this.al, this.am, this.an, this.an);
         if (this.aB != z3) {
            double d;
            if (this.aB) {
               d = 1.0;
            } else {
               d = 0.0;
            }

            this.ay.a(d, 0.15, Easing.k);
         }

         boolean z4 = this.aC;
         this.aC = this.a(d2, d3, this.ConfigState, this.ConfigProfile, this.ConfigJsonCodec, this.ConfigJsonCodec);
         if (this.aC != z4) {
            this.az.a(!this.aC ? 0.0 : 1.0, 0.15, Easing.k);
         }

         if (this.aw) {
            boolean z5 = this.aM;
            this.aM = Bool.from(
               !(d2 < this.ConfigManager - 4.0F)
                     && !(d2 > this.ConfigManager + this.au + 4.0F)
                     && !(d3 < this.at - 4.0F)
                     && !(d3 > this.at + this.CloudConfigRepository + 4.0F)
                  ? 1
                  : 0
            );
            if (this.aM != z5 || mediaPlaybackStateR.y()) {
               this.aL.a(!this.aM && !mediaPlaybackStateR.y() ? 0.0 : 1.0, 0.15, Easing.k);
            }
         }
      } else {
         this.aA = false;
         this.aB = false;
         this.aC = false;
         this.aM = false;
      }
   }

   public void m() {
      if (this.ar || this.aw) {
         double d = this.aN;
         double d2 = this.aO;
         boolean z2 = false;
         if (this.ar) {
            if (this.a(d, d2, this.ai, this.aj, this.ak, this.ak)) {
               z2 = true;
            }

            if (this.a(d, d2, this.al, this.am, this.an, this.an)) {
               z2 = true;
            }

            if (this.a(d, d2, this.ConfigState, this.ConfigProfile, this.ConfigJsonCodec, this.ConfigJsonCodec)) {
               z2 = true;
            }
         }

         if (this.aw
            && !(d < this.ConfigManager - 4.0F)
            && d <= this.ConfigManager + this.au + 4.0F
            && d2 >= this.at - 4.0F
            && d2 <= this.at + this.CloudConfigRepository + 4.0F) {
            z2 = true;
         }

         if (z2) {
            GuiInput.g();
         }
      }
   }

   private void a(byte[] bArr) {
      try {
         if (this.FriendCard != null) {
            this.FriendCard.close();
            this.FriendCard = null;
         }

         BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bArr));
         if (bufferedImage == null) {
            return;
         }

         int width = bufferedImage.getWidth();
         int height = bufferedImage.getHeight();
         NativeImage NativeImageVar = new NativeImage(width, height, false);

         for (int i = 0; i < height; i++) {
            for (int i2 = 0; i2 < width; i2++) {
               NativeImageVar.setColorArgb(i2, i, bufferedImage.getRGB(i2, i));
            }
         }

         RenderSystemHelper.recordRenderCall(() -> {
            this.FriendCard = new NativeImageBackedTexture(() -> "pulse", NativeImageVar);
            this.aa = Identifier.of("crypt", "crypt");
            c.getTextureManager().registerTexture(this.aa, this.FriendCard);
         });
         this.Z = bArr;
      } catch (Exception e) {
         this.aa = null;
      }
   }

   public void a(double d, double d2) {
      this.aN = d;
      this.aO = d2;
      this.t();
      this.s();
   }

   public void n() {
      if (this.FriendsPanel) {
         this.FriendsPanel = false;
         this.ad.a(0.0, 0.25, Easing.k);
      }

      this.aA = false;
      this.aB = false;
      this.aC = false;
      this.aM = false;
   }

   private void t() {
      boolean z2 = this.FriendsPanel;
      if (!(this.aN >= this.ae)) {
         int i = 0;
         this.FriendsPanel = Bool.from(i);
      } else {
         int i;
         if (this.aN <= this.ae + this.ag && this.aO >= this.af && this.aO <= this.af + this.ah) {
            i = 1;
         } else {
            i = 0;
         }

         this.FriendsPanel = Bool.from(i);
         if (this.FriendsPanel == z2) {
            double d;
            if (this.FriendsPanel) {
               d = 1.0;
            } else {
               d = 0.0;
            }

            this.ad.a(d, 0.25, Easing.k);
         }
      }
   }

   @Override
   public float a() {
      return this.a(103.0F, 147.5F, (float)this.ad.j());
   }

   @Override
   public float b() {
      return this.a(12.0F, 65.0F, (float)this.ad.j());
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, float f5) {
      this.ae = f - this.g;
      this.af = f2 - this.h;
      this.ag = f3 + this.g * 2.0F;
      this.ah = f4 + this.h * 2.0F;
      MediaPlaybackState mediaPlaybackStateR = this.r();
      if (mediaPlaybackStateR != null) {
         float fJ = (float)this.ad.j();
         int i = (int)(f5 * 255.0F);
         FontRenderer fontRenderer = FontManager.b[15];
         FontRenderer fontRenderer2 = FontManager.b[14];
         FontRenderer fontRenderer3 = FontManager.a[13];
         FontRenderer fontRenderer4 = FontManager.a[11];
         String strN = mediaPlaybackStateR.n();
         String strO = mediaPlaybackStateR.o();
         boolean zC = mediaPlaybackStateR.c();
         float fH = mediaPlaybackStateR.h();
         float fA = this.a(12.0F, 31.0F, fJ);
         float fA2 = this.a(8.0F, 10.0F, fJ);
         float fJ2 = (float)this.aI.j();
         float fMax = fJ2 * 0.08F * Math.max(0.0F, (fJ - 0.3F) / 0.7F);
         float f6 = 1.0F - fJ2 * 0.3F;
         float f7 = fA * (1.0F - fMax);
         float f8 = (fA - f7) / 2.0F;
         float fA3 = this.a(f2 + f4 / 2.0F - 6.0F, f2, fJ) + f8;
         float f9 = f + f8;
         Color color = new Color(255, 255, 255, (int)(i * f6));
         if (this.aa != null) {
            renderer2D.a(this.aa, f9, fA3, f7, f7, fA2, color, MatrixStackVar);
         } else {
            renderer2D.a(IconTextureRegistry.get("crypt"), f9, fA3, f7, f7, fA2, color, MatrixStackVar);
         }

         float fA4 = this.a(f + 12.0F + 6.0F, f + 31.0F + 5.5F, fJ);
         float fA5 = this.a(f2 + f4 / 2.0F - fontRenderer2.b(strN) / 4.0F - 0.5F, f2 + 15.5F - 9.0F, fJ);
         float fJ3 = (float)this.aJ.j();
         Color color2 = new Color(m.getRed(), m.getGreen(), m.getBlue(), (int)(i * fJ3));
         float fA6 = this.a(25.0F, 70.0F, fJ);
         float fA7 = this.a(20.0F, 0.0F, fJ);
         FontRenderer fontRenderer5 = fJ >= 0.2F ? fontRenderer : fontRenderer2;
         float fA8 = fontRenderer5.a(strN);
         boolean z2;
         if (fA8 <= fA6) {
            z2 = false;
         } else {
            z2 = true;
         }

         boolean z3 = z2;
         float fA9 = this.a(fA8, fA6 + fA7, this.aP, this.aR);
         if (z3) {
            renderer2D.b().a(fA4, fA5 - 3.0F, fA6 + fA7, 16.0F, MatrixStackVar);
         }

         fontRenderer5.a(strN, fA4 + fA9, fA5, color2, MatrixStackVar);
         if (z3) {
            renderer2D.b().a(MatrixStackVar);
            Color color3 = new Color(r.getRed(), r.getGreen(), r.getBlue(), 0);
            Color color4 = new Color(r.getRed(), r.getGreen(), r.getBlue(), i);
            renderer2D.a(fA4 + fA6 + fA7 - 12.0F + 1.0F, fA5 - 3.0F, 12.0F, 16.0F, 0.0F, color3, color4, color3, color4, MatrixStackVar);
         }

         if (fJ > 0.01F) {
            int iB = (int)(f5 * this.b(fJ) * fJ3 * 255.0F);
            Color color5 = new Color(n.getRed(), n.getGreen(), n.getBlue(), iB);
            float fA10 = fontRenderer3.a(strO);
            boolean z4 = fA10 > 70.0F;
            float fA11 = this.a(fA10, 70.0F, this.aQ, this.aS);
            if (z4) {
               renderer2D.b().a(fA4, fA5 + 7.0F, 70.0F, 16.0F, MatrixStackVar);
            }

            fontRenderer3.a(strO, fA4 + fA11, fA5 + 10.0F, color5, MatrixStackVar);
            if (z4) {
               renderer2D.b().a(MatrixStackVar);
               Color color6 = new Color(r.getRed(), r.getGreen(), r.getBlue(), 0);
               Color color7 = new Color(r.getRed(), r.getGreen(), r.getBlue(), iB);
               renderer2D.a(fA4 + 70.0F - 12.0F, fA5 + 7.0F, 12.0F, 16.0F, 0.0F, color6, color7, color6, color7, MatrixStackVar);
            }
         }

         float fA12 = this.a(12.0F, 16.0F, fJ);
         float fA13 = this.a(f + f3 - 14.5F - 4.0F, f + f3 - 14.5F - 4.0F, fJ) + 4.0F;
         float fA14 = this.a(f2 + f4 / 2.0F, f2 + fA12 / 2.0F, fJ);
         float[] fArrH = mediaPlaybackStateR.H();

         for (int i2 = 0; i2 < 6; i2++) {
            float f10 = fArrH[i2];
            float fMax2 = Math.max(2.0F, f10 * fA12);
            float f11 = fA13 + i2 * 2.5F;
            float f12 = fA14 - fMax2 / 2.0F;
            int iA = (int)this.a(180.0F, 255.0F, f10);
            renderer2D.a(
               f11,
               f12,
               2.0F,
               fMax2,
               1.0F,
               this.a(
                  new Color(iA, iA, iA, i),
                  new Color((int)this.a(o.getRed(), iA, 0.5F), (int)this.a(o.getGreen(), iA, 0.5F), (int)this.a(o.getBlue(), iA, 0.5F), i),
                  fJ
               ),
               MatrixStackVar
            );
         }

         if (fJ <= 0.01F) {
            this.ar = false;
            this.aw = false;
            return;
         }

         int iB2 = (int)(f5 * this.b(fJ) * 255.0F);
         float f13 = f2 + 31.0F + 7.5F;
         float fB = mediaPlaybackStateR.b() * fH;
         String strA = this.a((int)fB);
         String str = "crypt" + this.a((int)(fH - fB));
         float fA15 = fontRenderer4.a(strA);
         float fA16 = fontRenderer4.a(str);
         Color color8 = new Color(n.getRed(), n.getGreen(), n.getBlue(), iB2);
         float f14 = f13 + 2.0F - 4.0F;
         fontRenderer4.a(strA, f, f14, color8, MatrixStackVar);
         fontRenderer4.a(str, f + f3 - fA16, f14, color8, MatrixStackVar);
         float f15 = f + fA15 + 3.5F;
         float f16 = f3 - fA15 - fA16 - 7.0F;
         this.ConfigManager = f15;
         this.at = f13 - 0.5F;
         this.au = f16;
         this.CloudConfigRepository = 4.0F;
         this.aw = true;
         float fJ4 = (float)this.aL.j();
         float f17 = 4.0F + fJ4 * 2.0F;
         float f18 = f13 - 0.5F - fJ4 * 1.0F;
         float f19 = 3.0F + fJ4 * 1.0F;
         renderer2D.a(f15, f18, f16, f17, f19, new Color(p.getRed(), p.getGreen(), p.getBlue(), iB2), MatrixStackVar);
         float fMax3 = f16 * Math.max(0.04F, mediaPlaybackStateR.b());
         Color color9 = new Color(q.getRed(), q.getGreen(), q.getBlue(), iB2);
         if (fMax3 > 0.0F) {
            renderer2D.a(f15, f18, fMax3, f17, f19, 0.0F, f19, 0.0F, color9, color9, color9, color9, MatrixStackVar);
         }

         float f20 = f13 + 4.0F + 7.5F - 10.0F;
         float f21 = f + f3 / 2.0F;
         float fJ5 = 0.9F + (float)this.ax.j() * 0.1F - (float)this.aD.j() * 0.2F;
         int iMin = (int)(255.0F * Math.min(1.0F, fJ5));
         int iMin2 = (int)(iB2 * Math.min(1.0F, fJ5));
         float f22 = f21 - 16.0F - 0.0F - 32.0F;
         float f23 = f20 + 0.0F;
         Color color10 = new Color(iMin, iMin, iMin, iMin2);
         FontRenderer fontRenderer5a = FontManager.e[35];
         fontRenderer5a.a(
            "crypt", f22 + (32.0F - fontRenderer5a.a("crypt")) / 2.0F, f23 + (32.0F - fontRenderer5a.b("crypt") / 2.0F) / 2.0F, color10, MatrixStackVar
         );
         float fJ6 = 0.9F + (float)this.ay.j() * 0.1F - (float)this.aE.j() * 0.2F;
         int iMin3 = (int)(255.0F * Math.min(1.0F, fJ6));
         int iMin4 = (int)(iB2 * Math.min(1.0F, fJ6));
         float f24 = f21 - 16.0F;
         Color color11 = new Color(iMin3, iMin3, iMin3, iMin4);
         FontRenderer fontRenderer6 = FontManager.e[35];
         String strIcon6 = !zC ? "crypt" : "crypt";
         fontRenderer6.a(
            strIcon6, f24 + (32.0F - fontRenderer6.a(strIcon6)) / 2.0F, f20 + (32.0F - fontRenderer6.b(strIcon6) / 2.0F) / 2.0F, color11, MatrixStackVar
         );
         float fJ7 = 0.9F + (float)this.az.j() * 0.1F - (float)this.aF.j() * 0.2F;
         int iMin5 = (int)(255.0F * Math.min(1.0F, fJ7));
         int iMin6 = (int)(iB2 * Math.min(1.0F, fJ7));
         float f25 = f21 + 16.0F + 0.0F;
         Color color12 = new Color(iMin5, iMin5, iMin5, iMin6);
         FontRenderer fontRenderer5c = FontManager.e[35];
         fontRenderer5c.a(
            "crypt", f25 + (32.0F - fontRenderer5c.a("crypt")) / 2.0F, f23 + (32.0F - fontRenderer5c.b("crypt") / 2.0F) / 2.0F, color12, MatrixStackVar
         );
         this.ai = f22;
         this.aj = f23;
         this.ak = 32.0F;
         this.al = f24;
         this.am = f20;
         this.an = 32.0F;
         this.ConfigState = f25;
         this.ConfigProfile = f23;
         this.ConfigJsonCodec = 32.0F;
         this.ar = true;
      }
   }

   private String a(int i) {
      return i / 60 + "crypt" + String.format("crypt", i % 60);
   }

   private float a(float f, float f2, float f3) {
      return f + (f2 - f) * f3;
   }

   private float a(float f, float f2, long j, boolean z2) {
      if (z2 && !(f <= f2)) {
         float f3 = f - f2;
         long jCurrentTimeMillis = (System.currentTimeMillis() - j) % 8000L;
         return -f3
            * (
               jCurrentTimeMillis >= 1500L
                  ? (
                     jCurrentTimeMillis >= 4000L
                        ? (jCurrentTimeMillis >= 5500L ? 1.0F - this.a((float)(jCurrentTimeMillis - 1500L - 2500L - 1500L) / 2500.0F) : 1.0F)
                        : this.a((float)(jCurrentTimeMillis - 1500L) / 2500.0F)
                  )
                  : 0.0F
            );
      } else {
         return 0.0F;
      }
   }

   private float a(float f) {
      return f >= 0.5F ? 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F : 4.0F * f * f * f;
   }

   private float b(float f) {
      float fMax = Math.max(0.0F, Math.min(1.0F, f));
      return fMax * fMax * (3.0F - 2.0F * fMax);
   }

   private Color a(Color color, Color color2, float f) {
      return new Color(
         (int)this.a(color.getRed(), color2.getRed(), f),
         (int)this.a(color.getGreen(), color2.getGreen(), f),
         (int)this.a(color.getBlue(), color2.getBlue(), f),
         (int)this.a(color.getAlpha(), color2.getAlpha(), f)
      );
   }

   public boolean a(double d, double d2, int i) {
      if (i != 0) {
         return false;
      }

      MediaSessionService mediaSessionServiceQ = this.q();
      MediaPlaybackState mediaPlaybackStateR = this.r();
      if (mediaSessionServiceQ != null && mediaPlaybackStateR != null) {
         if (this.aw
            && mediaPlaybackStateR.h() > 0.0F
            && d >= this.ConfigManager - 4.0F
            && d <= this.ConfigManager + this.au + 4.0F
            && d2 >= this.at - 4.0F
            && d2 <= this.at + this.CloudConfigRepository + 4.0F) {
            mediaSessionServiceQ.a(Math.max(0.0F, Math.min(1.0F, (float)(d - this.ConfigManager) / this.au)));
            return true;
         }

         if (!this.ar) {
            return false;
         }

         if (this.a(d, d2, this.ai, this.aj, this.ak, this.ak)) {
            this.aD.d(1.0);
            this.aD.a(0.0, 0.2, Easing.k);
            mediaSessionServiceQ.f();
            return true;
         }

         if (this.a(d, d2, this.al, this.am, this.an, this.an)) {
            this.aE.d(1.0);
            this.aE.a(0.0, 0.2, Easing.k);
            mediaSessionServiceQ.d();
            return true;
         }

         if (!this.a(d, d2, this.ConfigState, this.ConfigProfile, this.ConfigJsonCodec, this.ConfigJsonCodec)) {
            return false;
         }

         this.aF.d(1.0);
         this.aF.a(0.0, 0.2, Easing.k);
         mediaSessionServiceQ.e();
         return true;
      } else {
         return false;
      }
   }

   public void b(double d, double d2) {
      MediaSessionService mediaSessionServiceQ = this.q();
      MediaPlaybackState mediaPlaybackStateR = this.r();
      if (mediaSessionServiceQ != null && mediaPlaybackStateR != null) {
         if (mediaPlaybackStateR.y() && this.au > 0.0F) {
            mediaSessionServiceQ.b(Math.max(0.0F, Math.min(1.0F, (float)(d - this.ConfigManager) / this.au)));
         }
      }
   }

   public boolean b(double d, double d2, int i) {
      MediaSessionService mediaSessionServiceQ = this.q();
      MediaPlaybackState mediaPlaybackStateR = this.r();
      if (mediaSessionServiceQ == null || mediaPlaybackStateR == null) {
         return false;
      } else if (i == 0 && mediaPlaybackStateR.y()) {
         mediaSessionServiceQ.c(Math.max(0.0F, Math.min(1.0F, (float)(d - this.ConfigManager) / this.au)));
         return true;
      } else {
         return false;
      }
   }

   private boolean a(double d, double d2, float f, float f2, float f3, float f4) {
      return !(d < f) && !(d > f + f3) && d2 >= f2 && d2 <= f2 + f4;
   }

   public void o() {
      if (this.FriendCard != null) {
         this.FriendCard.close();
         this.FriendCard = null;
      }

      this.aa = null;
      this.Z = null;
   }

   public static boolean p() {
      return MediaSessionService.g();
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
