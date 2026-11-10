package pulse.media;

import com.wmedia.AudioLevels;
import com.wmedia.MediaInfo;
import com.wmedia.MediaProvider;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Generated;
import meteordevelopment.orbit.EventHandler;
import pulse.core.Bool;
import pulse.events.ClientTickEvent;
import pulse.hud.core.HudService;
import pulse.hud.core.HudServiceInfo;

@HudServiceInfo(enabledByDefault = true)
public class MediaSessionService extends HudService {
   private static final long c = 400L;
   private static final long d = 16L;
   private static final AtomicReference<float[]> j;
   private static volatile boolean k = false;
   private static volatile boolean l = false;
   private static final int m = 6;
   private static final long t = 50L;
   private static final long u = 16L;
   public static int a;
   public static boolean b;
   private static Thread h = null;
   private static final AtomicReference<MediaSessionService.MediaSnapshot> i = new AtomicReference<>(null);
   private static final AtomicBoolean f = new AtomicBoolean(false);
   private static final AtomicBoolean g = new AtomicBoolean(false);
   private final MediaPlaybackState e = new MediaPlaybackState();
   private final float[] n = new float[6];
   private final float[] o = new float[6];
   private float p = 0.0F;
   private int q = 0;
   private long r = 0L;
   private long s = 0L;

   @Override
   public void a() {
      super.a();
      j();
   }

   private static synchronized void j() {
      if ((h == null || !h.isAlive()) && !g.get()) {
         f.set(true);
         h = new Thread(MediaSessionService::k, "WMedia-Polling");
         h.setDaemon(true);
         h.setPriority(2);
         h.start();
         System.out.println("Started WMedia polling thread");
      }
   }

   private static void k() {
      long r8 = 0L;
      long j2 = 0L;

      while (f.get() && !g.get()) {
         try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - r8 >= 400L) {
               r8 = jCurrentTimeMillis;
               l();
            }

            if (jCurrentTimeMillis - j2 >= 16L) {
               j2 = jCurrentTimeMillis;
               m();
            }

            Thread.sleep(8L);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         } catch (Throwable th) {
            System.err.println("Polling error: " + th.getMessage());

            try {
               Thread.sleep(100L);
            } catch (InterruptedException e2) {
               Thread.currentThread().interrupt();
            }
         }
      }

      System.out.println("Polling thread stopped");
   }

   private static void l() {
      if (!q()) {
         i.set(null);
      } else {
         try {
            Optional<MediaInfo> currentMedia = MediaProvider.getCurrentMedia();
            if (currentMedia.isPresent()) {
               MediaInfo mediaInfo = currentMedia.get();
               String title;
               if (mediaInfo.getTitle().isEmpty()) {
                  title = "No media";
               } else {
                  title = mediaInfo.getTitle();
               }

               i.set(
                  new MediaSessionService.MediaSnapshot(
                     title, mediaInfo.getArtist(), mediaInfo.isPlaying(), mediaInfo.getPositionMs(), mediaInfo.getDurationMs(), mediaInfo.getAlbumArt()
                  )
               );
            } else {
               i.set(new MediaSessionService.MediaSnapshot("No media", "", false, 0.0F, 0.0F, null));
            }
         } catch (Throwable th) {
            k = false;
            i.set(null);
         }
      }
   }

   private static void m() {
      if (!q()) {
         float[] fArr = new float[]{0.0F, 0.0F, 0.0F};
         j.set(fArr);
      } else {
         try {
            Optional<AudioLevels> audioLevels = MediaProvider.getAudioLevels();
            if (audioLevels.isPresent()) {
               AudioLevels audioLevels2 = audioLevels.get();
               j.set(new float[]{audioLevels2.getMasterPeak(), audioLevels2.getLeftPeak(), audioLevels2.getRightPeak()});
            } else {
               float[] fArr2 = new float[]{0.0F, 0.0F, 0.0F};
               j.set(fArr2);
            }
         } catch (Throwable th) {
            float[] fArr3 = new float[]{0.0F, 0.0F, 0.0F};
            j.set(fArr3);
         }
      }
   }

   @EventHandler
   private void a(ClientTickEvent clientTickEvent) {
      long jCurrentTimeMillis = System.currentTimeMillis();
      if (jCurrentTimeMillis - this.r >= 50L) {
         this.r = jCurrentTimeMillis;
         this.n();
      }

      if (jCurrentTimeMillis - this.s >= 16L) {
         this.s = jCurrentTimeMillis;
         this.o();
      }
   }

   private void n() {
      MediaSessionService.MediaSnapshot mediaSnapshot = i.get();
      if (mediaSnapshot != null) {
         this.e.a(mediaSnapshot.a, mediaSnapshot.b, mediaSnapshot.c, mediaSnapshot.d, mediaSnapshot.e, mediaSnapshot.f);
      } else {
         this.e.a("No media", "", false, 0.0F, 0.0F, null);
      }

      this.e.m();
   }

   private void o() {
      this.q = this.q - -2 - 1;
      float[] fArr = j.get();
      if (fArr != null && (fArr[0] != 0.0F || fArr[1] != 0.0F || fArr[2] != 0.0F)) {
         float f2 = fArr[0];
         float f3 = fArr[1];
         float f4 = fArr[2];
         float f5 = f2 > this.p + 0.15F ? (f2 - this.p) * 2.0F : 0.0F;
         this.p = f2 * 0.7F + this.p * 0.3F;
         float fMin = Math.min(f2 * 1.4F, 1.0F);
         float fMin2 = Math.min(f3 * 1.4F, 1.0F);
         float fMin3 = Math.min(f4 * 1.4F, 1.0F);
         float f6 = this.q;
         float fSin = (float)(Math.sin(f6 * 0.5) * 0.08 + 0.92);
         float fSin2 = (float)(Math.sin(f6 * 0.6 + 1.0) * 0.08 + 0.92);
         float fSin3 = (float)(Math.sin(f6 * 0.7 + 2.0) * 0.08 + 0.92);
         float[] fArr2 = new float[]{
            (fMin2 * 0.6F + f5 * 0.3F) * fSin,
            (fMin2 * 0.85F + fMin * 0.3F + f5 * 0.5F) * fSin2,
            (fMin2 * 0.5F + fMin * 0.7F + f5 * 0.7F) * fSin3,
            (fMin3 * 0.5F + fMin * 0.7F + f5 * 0.7F) * fSin,
            (fMin3 * 0.85F + fMin * 0.3F + f5 * 0.5F) * fSin3,
            (fMin3 * 0.6F + f5 * 0.3F) * fSin2
         };

         for (int i2 = 0; i2 < 6; i2++) {
            float fMin4 = Math.min(fArr2[i2], 1.0F);
            if (fMin4 <= this.n[i2]) {
               this.n[i2] = this.n[i2] * 0.6F + fMin4 * 0.4F;
            } else {
               this.n[i2] = this.n[i2] * 0.1F + fMin4 * 0.9F;
            }

            if (this.n[i2] <= this.o[i2]) {
               this.o[i2] = this.o[i2] * 0.92F;
            } else {
               this.o[i2] = this.n[i2];
            }

            this.e.H()[i2] = Math.min(this.n[i2] * 0.7F + this.o[i2] * 0.3F, 1.0F);
         }
      } else {
         this.p();
      }
   }

   private void p() {
      for (int i2 = 0; i2 < 6; i2++) {
         this.n[i2] = this.n[i2] * 0.8F;
         this.o[i2] = this.o[i2] * 0.85F;
         this.e.H()[i2] = this.n[i2] * 0.7F + this.o[i2] * 0.3F;
      }
   }

   public void d() {
      int i2 = this.e.c() ? 0 : 1;
      this.e.a(Bool.from(i2));
      if (i2 != 0) {
         this.e.j();
      } else {
         this.e.i();
      }

      if (g()) {
         new Thread(() -> {
            try {
               MediaProvider.playPause();
            } catch (Throwable var1x) {
            }
         }, "WMedia-PlayPause").start();
      }
   }

   public void e() {
      if (g()) {
         new Thread(() -> {
            try {
               MediaProvider.next();
            } catch (Throwable var1) {
            }
         }, "WMedia-Next").start();
      }
   }

   public void f() {
      if (g()) {
         new Thread(() -> {
            try {
               MediaProvider.previous();
            } catch (Throwable var1) {
            }
         }, "WMedia-Previous").start();
      }
   }

   public void a(float f2) {
      this.e.a(f2 * this.e.s());
   }

   public void b(float f2) {
      this.e.b(f2 * this.e.s());
   }

   public void c(float f2) {
      float fS = f2 * this.e.s();
      this.e.b(fS);
      this.e.e();
      if (g() && !(this.e.s() <= 0.0F)) {
         long j2 = (long)fS;
         new Thread(() -> {
            try {
               MediaProvider.seek(j2);
            } catch (Throwable var3x) {
            }
         }, "WMedia-Seek").start();
      }
   }

   private static boolean q() {
      if (g.get()) {
         return false;
      }

      if (!l) {
         l = true;

         try {
            k = MediaProvider.isAvailable();
            System.out.println("WMedia library available: " + k);
         } catch (Throwable th) {
            System.out.println("WMedia library error: " + th.getMessage());
            k = false;
         }
      }

      return k;
   }

   public static boolean g() {
      return Bool.from(k && !g.get() ? 1 : 0);
   }

   public static synchronized void h() {
      if (!g.getAndSet(true)) {
         System.out.println("Shutting down WMedia...");
         f.set(false);
         if (h != null) {
            h.interrupt();

            try {
               h.join(1000L);
            } catch (InterruptedException var3) {
            }

            h = null;
         }

         i.set(null);
         float[] fArr = new float[]{0.0F, 0.0F, 0.0F};
         j.set(fArr);
         if (k) {
            try {
               MediaProvider.shutdown();
               System.out.println("MediaProvider.shutdown() called");
            } catch (Throwable th) {
               System.err.println("Error during shutdown: " + th.getMessage());
            }
         }

         k = false;
         System.out.println("WMedia shutdown complete");
      }
   }

   @Generated
   public MediaPlaybackState i() {
      return this.e;
   }

   public static String b(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   static {
      float[] fArr = new float[]{0.0F, 0.0F, 0.0F};
      j = new AtomicReference<>(fArr);
   }

   private static class MediaSnapshot {
      final String a;
      final String b;
      final boolean c;
      final float d;
      final float e;
      final byte[] f;
      final long g = System.currentTimeMillis();
      public static int h;
      public static boolean i;

      MediaSnapshot(String str, String str2, boolean z, float f, float f2, byte[] bArr) {
         this.a = str;
         this.b = str2;
         this.c = z;
         this.d = f;
         this.e = f2;
         this.f = bArr;
      }

      public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
         return null;
      }
   }
}
