// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.notifications;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.hud.core.HudServiceRegistry;
import dev.enizer6569.dercovisuals.media.MediaPlaybackState;
import dev.enizer6569.dercovisuals.media.MediaSessionService;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.mixin.accessor.BossBarHudAccessor;
import dev.enizer6569.dercovisuals.mixin.accessor.PlayerListHudAccessor;

public class HudNotificationCenter {
   private static HudNotificationCenter a;
   private static final MinecraftClient b = MinecraftClient.getInstance();
   private static final Color c = new Color(19, 19, 25);
   private static final Color d = new Color(19, 19, 26);
   private static final Color e = new Color(13, 13, 17);
   private static final Color f = new Color(17, 17, 23);
   private static final float g = 12.0F;
   private static final float h = 7.0F;
   private static final float i = 19.0F;
   private float s;
   private float t;
   private float u;
   private float v;
   private static final long z = 3000L;
   private static final long D = 300L;
   private final WatermarkOverlay j = new WatermarkOverlay();
   private final Queue<HudNotification> k = new LinkedList<>();
   private HudNotification l = null;
   private ActionNotification m = null;
   private final AnimationState n = new AnimationState();
   private final AnimationState o = new AnimationState();
   private final AnimationState p = new AnimationState();
   private final AnimationState q = new AnimationState();
   private final AnimationState r = new AnimationState();
   private final HudNotificationSettingsPanel w = new HudNotificationSettingsPanel();
   private long x = 0L;
   private boolean y = false;
   private int A = 0;
   private int B = 0;
   private long C = 0L;
   private final AnimationState E = new AnimationState();
   private boolean F = false;

   public HudNotificationCenter() {
      a = this;
      float fC = this.j.c();
      float fD = this.j.d();
      this.n.d(fC);
      this.o.d(fD);
      this.p.d(7.0);
      this.q.d(1.0);
      this.r.d(0.0);
      this.E.d(1.0);
      this.s = fC;
      this.t = fD;
      this.u = 7.0F;
   }

   public static HudNotificationCenter a() {
      if (a != null) {
         return a;
      }

      HudNotificationCenter hudNotificationCenter = new HudNotificationCenter();
      a = hudNotificationCenter;
      return hudNotificationCenter;
   }

   public static void a(String str, boolean z2) {
      if (ModuleRegistry.WATERMARK == null
         || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.notifications().a() && ModuleRegistry.WATERMARK.functions().a()) {
         a().b(new ToggleTextNotification(str, z2));
      }
   }

   public static void a(String str, String str2, String str3) {
   }

   public static void a(ActionNotification actionNotification) {
      if (ModuleRegistry.WATERMARK == null
         || ModuleRegistry.WATERMARK.a() && ModuleRegistry.WATERMARK.notifications().a() && ModuleRegistry.WATERMARK.functions().a()) {
         if (actionNotification != null) {
            a().b(actionNotification);
         }
      }
   }

   public static void a(String str) {
   }

   public static void a(String str, String str2) {
   }

   public static void b() {
   }

   public static void c() {
   }

   @Deprecated
   public static void b(String str, String str2) {
      b();
   }

   private void b(HudNotification hudNotification) {
      if (hudNotification != null) {
         if (hudNotification instanceof ToggleTextNotification toggle
            && this.l instanceof ToggleTextNotification currentToggle
            && currentToggle.m().equals(toggle.m())) {
            currentToggle.a(toggle.n());
         } else {
            this.k.add(hudNotification);
         }
      }
   }

   public boolean a(double d2, double d3, int i2) {
      if (this.w.d()) {
         if (i2 == 0 && this.w.a((int)d2, (int)d3)) {
            return true;
         }

         if (i2 == 1 && this.w.b((int)d2, (int)d3)) {
            return true;
         }

         if (!this.b(d2, d3) && !this.w.d((int)d2, (int)d3)) {
            this.w.c();
            return true;
         }
      }

      if (this.b(d2, d3)) {
         if (i2 == 1) {
            this.w.a(this.v, this.u, this.s, this.t);
            return true;
         } else {
            return true;
         }
      } else {
         return this.l instanceof MediaPlaybackCardNotification ? ((MediaPlaybackCardNotification)this.l).a(d2, d3, i2) : false;
      }
   }

   private boolean b(double d2, double d3) {
      return Bool.from(!(d2 < this.v) && !(d2 > this.v + this.s) && !(d3 < this.u) && !(d3 > this.u + this.t) ? 1 : 0);
   }

   public void b(double d2, double d3, int i2) {
      if (this.w.d()) {
         this.w.c((int)d2, (int)d3);
      }

      if (this.l instanceof MediaPlaybackCardNotification) {
         ((MediaPlaybackCardNotification)this.l).b(d2, d3, i2);
      }
   }

   public boolean a(double d2, double d3, double d4) {
      return this.w.d() ? this.w.a((float)d4, (int)d2, (int)d3) : false;
   }

   public void a(double d2, double d3, double d4, double d5) {
      if (this.w.d()) {
         this.w.a((int)d2, (int)d3, d4, d5);
      }

      if (this.l instanceof MediaPlaybackCardNotification) {
         ((MediaPlaybackCardNotification)this.l).b(d2, d3);
      }
   }

   public void d() {
   }

   private void b(String str, boolean z2) {
      this.b(new ToggleTextNotification(str, z2));
   }

   public void a(HudNotification hudNotification) {
      this.b(hudNotification);
   }

   private boolean j() {
      if (this.w.i() && !this.y) {
         MediaSessionService mediaSessionService = HudServiceRegistry.MEDIA;
         MediaPlaybackState mediaPlaybackStateI;
         if (HudServiceRegistry.MEDIA != null && (mediaPlaybackStateI = mediaSessionService.i()) != null) {
            return mediaPlaybackStateI.c();
         }
      }

      return false;
   }

   private void k() {
      if (this.w.i()) {
         MediaSessionService mediaSessionService = HudServiceRegistry.MEDIA;
         MediaPlaybackState mediaPlaybackStateI;
         if (HudServiceRegistry.MEDIA != null && (mediaPlaybackStateI = mediaSessionService.i()) != null) {
            if (mediaPlaybackStateI.k()) {
               this.y = false;
               b();
            }

            if (mediaPlaybackStateI.l()) {
               this.y = false;
               c();
            }

            if (this.y && mediaPlaybackStateI.c() && !this.w.d() && System.currentTimeMillis() - this.x >= 3000L) {
               this.y = false;
               b();
               return;
            }

            return;
         }
      }
   }

   private void l() {
      this.k();
      if (this.m != null) {
         this.m.e();
         if (this.m.g() || this.m.h()) {
            this.m = null;
         }
      }

      if (this.l != null) {
         this.l.e();
         if (this.l instanceof MediaPlaybackCardNotification) {
            float fC = this.l.c();
            float fD = this.l.d();
            if (this.r.j() >= 0.99) {
               this.n.d(fC);
               this.o.d(fD);
            }
         } else {
            float fC2 = this.l.c();
            float fD2 = this.l.d();
            if (Math.abs(this.n.i() - fC2) > 0.1F) {
               this.n.a(fC2, 0.25, Easing.k);
            }

            if (Math.abs(this.o.i() - fD2) > 0.1F) {
               this.o.a(fD2, 0.25, Easing.k);
            }
         }

         if (this.l.g()) {
            this.l = null;
            if (this.m != null && !this.m.h()) {
               this.l = this.m;
               this.m = null;
               float fC3 = this.l.c();
               float fD3 = this.l.d();
               this.n.a(fC3, 0.3, Easing.k);
               this.o.a(fD3, 0.3, Easing.k);
               this.r.a(1.0, 0.2, Easing.k);
               this.q.a(0.0, 0.15, Easing.k);
            } else if (this.j()) {
               this.l = new MediaPlaybackCardNotification();
               float fC4 = this.l.c();
               float fD4 = this.l.d();
               this.n.a(fC4, 0.3, Easing.k);
               this.o.a(fD4, 0.3, Easing.k);
               this.r.d(0.0);
               this.r.a(1.0, 0.2, Easing.k);
               this.q.a(0.0, 0.15, Easing.k);
            } else {
               this.r.a(0.0, 0.2, Easing.k);
               this.q.a(1.0, 0.2, Easing.k);
               float fC5 = this.j.c();
               float fD5 = this.j.d();
               this.n.a(fC5, 0.3, Easing.k);
               this.o.a(fD5, 0.3, Easing.k);
            }
         }
      }

      if (this.l == null && !this.k.isEmpty()) {
         this.l = this.k.poll();
         float fC6 = this.l.c();
         float fD6 = this.l.d();
         this.n.a(fC6, 0.3, Easing.k);
         this.o.a(fD6, 0.3, Easing.k);
         this.r.a(1.0, 0.2, Easing.k);
         this.q.a(0.0, 0.15, Easing.k);
      }

      this.n.a();
      this.o.a();
      this.q.a();
      this.r.a();
      float fN = this.n();
      if (Math.abs(this.u - fN) > 0.5F) {
         this.p.a(fN, 0.3, Easing.k, true);
      }

      this.p.a();
      this.s = (float)this.n.j();
      this.t = (float)this.o.j();
      this.u = (float)this.p.j();
      if (this.l == null) {
         float fC7 = this.j.c();
         if (Math.abs(this.s - fC7) > 0.5F) {
            this.n.a(fC7, 0.3, Easing.k, true);
         }
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D) {
      if (b.world != null && b.player != null) {
         this.l();
         boolean zO = this.o();
         if (zO != this.F) {
            if (zO) {
               this.E.a(0.0, 0.15, Easing.k);
               if (this.w.d()) {
                  this.w.b();
               }
            } else {
               this.E.a(1.0, 0.2, Easing.k);
            }

            this.F = zO;
         }

         this.E.a();
         float fJ = (float)this.E.j();
         if (fJ >= 0.01F) {
            float fRound = (float)(Math.round((b.getWindow().getFramebufferWidth() / 2.0F / 2.0F - this.s / 2.0F) * 2.0) / 2.0);
            float f2 = this.u;
            this.v = fRound;
            float fJ2 = (float)this.q.j() * fJ;
            float fJ3 = (float)this.r.j() * fJ;
            Color cardTop = this.a(new Color(8, 8, 11, 235), fJ);
            Color cardBottom = this.a(new Color(5, 5, 8, 240), fJ);
            renderer2D.a(fRound, f2, this.s, this.t, 12.0F, cardTop, cardTop, cardBottom, cardBottom, MatrixStackVar);
            renderer2D.b().a(fRound, f2, this.s, this.t, 12.0F, MatrixStackVar);
            if (b.currentScreen instanceof DercoVisualsClickGuiScreen && Renderer2DImpl.currentDrawContext != null) {
               float cx = fRound + this.s / 2.0F;
               float cy = f2 + this.t;
               int col = cardBottom.getRGB();

               for (int i = 0; i < 4; i++) {
                  float w = 4.0F - i;
                  Renderer2DImpl.currentDrawContext.fill((int)(cx - w), (int)(cy + i), (int)(cx + w), (int)(cy + i + 1.0F), col);
               }
            }

            if (fJ2 > 0.01F) {
               this.j.a(MatrixStackVar, renderer2D, fRound + this.j.j(), f2 + this.j.k(), this.s - this.j.j() * 2.0F, this.t - this.j.k() * 2.0F, fJ2);
            }

            if (this.l != null && fJ3 > 0.01F) {
               this.l.a(MatrixStackVar, renderer2D, fRound + this.l.j(), f2 + this.l.k(), this.s - this.l.j() * 2.0F, this.t - this.l.k() * 2.0F, fJ3);
            }

            renderer2D.b().a(MatrixStackVar);
            if (this.w.d()) {
               this.w.b(fRound, f2, this.s, this.t);
               int mouseX = (int)(b.mouse.getX() * b.getWindow().getScaledWidth() / b.getWindow().getFramebufferWidth());
               int mouseY = (int)(b.mouse.getY() * b.getWindow().getScaledHeight() / b.getWindow().getFramebufferHeight());
               this.w.a(MatrixStackVar, renderer2D, mouseX, mouseY);
            }
         }
      }
   }

   public float e() {
      return this.s;
   }

   public float f() {
      return this.t;
   }

   private int m() {
      if (b.inGameHud == null) {
         return 0;
      }

      BossBarHudAccessor bossBarHudAccessorGetBossBarHud;
      return (ModuleRegistry.RENDER_TWEAKS == null || !ModuleRegistry.RENDER_TWEAKS.s())
            && (bossBarHudAccessorGetBossBarHud = (BossBarHudAccessor)b.inGameHud.getBossBarHud()) != null
         ? bossBarHudAccessorGetBossBarHud.getBossBars().size()
         : 0;
   }

   private float n() {
      int iM = this.m();
      long jCurrentTimeMillis = System.currentTimeMillis();
      if (iM > this.B) {
         this.B = iM;
         this.C = 0L;
      } else if (iM >= this.B) {
         this.C = 0L;
      } else if (this.C == 0L) {
         this.C = jCurrentTimeMillis;
      } else if (jCurrentTimeMillis - this.C >= 300L) {
         this.B = iM;
         this.C = 0L;
      }

      this.A = iM;
      return this.B == 0 ? 7.0F : 7.0F + (float)((double)(this.B * 19.0F) * b.getWindow().getScaleFactor() / 2.0);
   }

   public void a(double d2, double d3) {
      if (this.l instanceof MediaPlaybackCardNotification mediaPlaybackCardNotification) {
         if (HudElementManager.a().p()) {
            mediaPlaybackCardNotification.n();
         } else {
            mediaPlaybackCardNotification.a(d2, d3);
            mediaPlaybackCardNotification.m();
         }
      }

      HudNotification hudNotification2 = this.l;
      if (hudNotification2 instanceof ActionNotification) {
         ((ActionNotification)hudNotification2).n();
      }
   }

   public void g() {
      if (this.l instanceof MediaPlaybackCardNotification) {
         ((MediaPlaybackCardNotification)this.l).n();
      }
   }

   private Color a(Color color, float f2) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f2));
   }

   private boolean o() {
      PlayerListHudAccessor playerListHudAccessorGetPlayerListHud;
      return b.inGameHud != null && (playerListHudAccessorGetPlayerListHud = (PlayerListHudAccessor)b.inGameHud.getPlayerListHud()) != null
         ? playerListHudAccessorGetPlayerListHud.isVisible()
         : false;
   }

   @Generated
   public HudNotification h() {
      return this.l;
   }

   @Generated
   public HudNotificationSettingsPanel i() {
      return this.w;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
