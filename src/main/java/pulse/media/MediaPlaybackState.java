package pulse.media;

import java.util.Arrays;

public class MediaPlaybackState {
   private static final float SMALL_POSITION_JUMP_MS = 100.0F;
   private static final float LARGE_POSITION_JUMP_MS = 1000.0F;
   private static final float SEEK_CONFIRM_WINDOW_MS = 2000.0F;
   private boolean artworkChanged;
   private boolean playing;
   private float durationMs;
   private float positionMs;
   private long positionTimestampMs;
   private float lastSourcePositionMs;
   private boolean forcedPlaying;
   private boolean hasForcedPlaying;
   private boolean fixedPosition;
   private float fixedPositionMs;
   private boolean waitingForSeekConfirmation;
   private float seekBasePositionMs;
   private boolean mediaCardVisible;
   private boolean showTrackChanged;
   private boolean showPlaybackStopped;
   private boolean wasPlaying;
   private String title = "";
   private String artist = "";
   private byte[] artwork = new byte[0];
   private String lastTitle = "";
   private final float[] spectrum = new float[6];

   public float currentPositionMs() {
      if (this.fixedPosition) {
         return this.fixedPositionMs;
      }

      if (this.waitingForSeekConfirmation) {
         if (this.isPlaying() && this.positionTimestampMs > 0L) {
            float fCurrentTimeMillis = this.seekBasePositionMs + (float)(System.currentTimeMillis() - this.positionTimestampMs);
            return this.durationMs > 0.0F ? Math.min(fCurrentTimeMillis, this.durationMs) : fCurrentTimeMillis;
         } else {
            return this.seekBasePositionMs;
         }
      } else if (this.isPlaying() && this.positionTimestampMs > 0L) {
         float fCurrentTimeMillis2 = this.positionMs + (float)(System.currentTimeMillis() - this.positionTimestampMs);
         return this.durationMs > 0.0F ? Math.min(fCurrentTimeMillis2, this.durationMs) : fCurrentTimeMillis2;
      } else {
         return this.positionMs;
      }
   }

   public float progress() {
      return this.durationMs > 0.0F ? Math.max(0.0F, Math.min(1.0F, this.currentPositionMs() / this.durationMs)) : 0.0F;
   }

   public boolean isPlaying() {
      return this.hasForcedPlaying ? this.forcedPlaying : this.playing;
   }

   public void forcePlaying(boolean z) {
      float fCurrentPositionMs = this.currentPositionMs();
      this.forcedPlaying = z;
      this.hasForcedPlaying = true;
      this.positionMs = fCurrentPositionMs;
      this.positionTimestampMs = System.currentTimeMillis();
   }

   public void clearForcedPlaying() {
      this.hasForcedPlaying = false;
   }

   public void freezePosition(float f) {
      this.fixedPosition = true;
      this.fixedPositionMs = f;
      this.waitingForSeekConfirmation = false;
   }

   public void setFrozenPosition(float f) {
      this.fixedPositionMs = f;
   }

   public void startSeekConfirmation() {
      this.fixedPosition = false;
      this.waitingForSeekConfirmation = true;
      this.seekBasePositionMs = this.fixedPositionMs;
      this.positionTimestampMs = System.currentTimeMillis();
   }

   public void update(String str, String str2, boolean z, float f, float f2, byte[] bArr) {
      boolean zEquals = this.title.equals(str);
      this.title = str;
      this.artist = str2;
      if (bArr != null && bArr.length > 0 && !Arrays.equals(this.artwork, bArr)) {
         this.artwork = bArr;
         this.artworkChanged = true;
      }

      this.playing = z;
      this.durationMs = f2;
      if (!zEquals) {
         this.positionMs = f;
         this.positionTimestampMs = System.currentTimeMillis();
         this.lastSourcePositionMs = f;
         this.clearForcedPlaying();
         this.fixedPosition = false;
         this.waitingForSeekConfirmation = false;
      } else {
         if (this.hasForcedPlaying && this.forcedPlaying == z) {
            this.clearForcedPlaying();
         }

         if (this.waitingForSeekConfirmation) {
            if (Math.abs(f - this.seekBasePositionMs) < 2000.0F) {
               this.waitingForSeekConfirmation = false;
               this.positionMs = f;
               this.positionTimestampMs = System.currentTimeMillis();
               this.lastSourcePositionMs = f;
            }
         } else if (!this.fixedPosition && !(Math.abs(f - this.lastSourcePositionMs) < 100.0F)) {
            this.lastSourcePositionMs = f;
            if (Math.abs(this.currentPositionMs() - f) > 1000.0F) {
               this.positionMs = f;
               this.positionTimestampMs = System.currentTimeMillis();
            }
         }
      }
   }

   public void clearArtworkChanged() {
      this.artworkChanged = false;
   }

   public float currentPositionSeconds() {
      return this.currentPositionMs() / 1000.0F;
   }

   public float durationSeconds() {
      return this.durationMs / 1000.0F;
   }

   public void showMediaCard() {
      this.mediaCardVisible = true;
   }

   public void hideMediaCard() {
      this.mediaCardVisible = false;
   }

   public boolean consumeTrackChanged() {
      if (!this.showTrackChanged) {
         return false;
      }

      this.showTrackChanged = false;
      return true;
   }

   public boolean consumePlaybackStopped() {
      if (!this.showPlaybackStopped) {
         return false;
      }

      this.showPlaybackStopped = false;
      return true;
   }

   public void updateNotificationFlags() {
      boolean zIsPlaying = this.isPlaying();
      if (!this.title.equals(this.lastTitle) && !this.title.isBlank()) {
         this.showTrackChanged = true;
         this.mediaCardVisible = true;
      }

      if (zIsPlaying && !this.wasPlaying) {
         this.showTrackChanged = true;
         this.mediaCardVisible = true;
      }

      if (!zIsPlaying && this.wasPlaying && !this.mediaCardVisible) {
         this.showPlaybackStopped = true;
      }

      this.wasPlaying = zIsPlaying;
      this.lastTitle = this.title;
   }

   public String title() {
      return this.title;
   }

   public String artist() {
      return this.artist;
   }

   public byte[] artwork() {
      return this.artwork;
   }

   public boolean artworkChanged() {
      return this.artworkChanged;
   }

   public boolean rawPlaying() {
      return this.playing;
   }

   public float durationMs() {
      return this.durationMs;
   }

   public float basePositionMs() {
      return this.positionMs;
   }

   public long positionTimestampMs() {
      return this.positionTimestampMs;
   }

   public float lastSourcePositionMs() {
      return this.lastSourcePositionMs;
   }

   public boolean forcedPlaying() {
      return this.forcedPlaying;
   }

   public boolean hasForcedPlaying() {
      return this.hasForcedPlaying;
   }

   public boolean fixedPosition() {
      return this.fixedPosition;
   }

   public float fixedPositionMs() {
      return this.fixedPositionMs;
   }

   public boolean waitingForSeekConfirmation() {
      return this.waitingForSeekConfirmation;
   }

   public float seekBasePositionMs() {
      return this.seekBasePositionMs;
   }

   public boolean mediaCardVisible() {
      return this.mediaCardVisible;
   }

   public boolean trackChangedQueued() {
      return this.showTrackChanged;
   }

   public boolean playbackStoppedQueued() {
      return this.showPlaybackStopped;
   }

   public boolean wasPlaying() {
      return this.wasPlaying;
   }

   public String lastTitle() {
      return this.lastTitle;
   }

   public float[] spectrum() {
      return this.spectrum;
   }

   public float a() {
      return this.currentPositionMs();
   }

   public float b() {
      return this.progress();
   }

   public boolean c() {
      return this.isPlaying();
   }

   public void a(boolean z) {
      this.forcePlaying(z);
   }

   public void d() {
      this.clearForcedPlaying();
   }

   public void a(float f) {
      this.freezePosition(f);
   }

   public void b(float f) {
      this.setFrozenPosition(f);
   }

   public void e() {
      this.startSeekConfirmation();
   }

   public void a(String str, String str2, boolean z, float f, float f2, byte[] bArr) {
      this.update(str, str2, z, f, f2, bArr);
   }

   public void f() {
      this.clearArtworkChanged();
   }

   public float g() {
      return this.currentPositionSeconds();
   }

   public float h() {
      return this.durationSeconds();
   }

   public void i() {
      this.showMediaCard();
   }

   public void j() {
      this.hideMediaCard();
   }

   public boolean k() {
      return this.consumeTrackChanged();
   }

   public boolean l() {
      return this.consumePlaybackStopped();
   }

   public void m() {
      this.updateNotificationFlags();
   }

   public String n() {
      return this.title();
   }

   public String o() {
      return this.artist();
   }

   public byte[] p() {
      return this.artwork();
   }

   public boolean q() {
      return this.artworkChanged();
   }

   public boolean r() {
      return this.rawPlaying();
   }

   public float s() {
      return this.durationMs();
   }

   public float t() {
      return this.basePositionMs();
   }

   public long u() {
      return this.positionTimestampMs();
   }

   public float v() {
      return this.lastSourcePositionMs();
   }

   public boolean w() {
      return this.forcedPlaying();
   }

   public boolean x() {
      return this.hasForcedPlaying();
   }

   public boolean y() {
      return this.fixedPosition();
   }

   public float z() {
      return this.fixedPositionMs();
   }

   public boolean A() {
      return this.waitingForSeekConfirmation();
   }

   public float B() {
      return this.seekBasePositionMs();
   }

   public boolean C() {
      return this.mediaCardVisible();
   }

   public boolean D() {
      return this.trackChangedQueued();
   }

   public boolean E() {
      return this.playbackStoppedQueued();
   }

   public boolean F() {
      return this.wasPlaying();
   }

   public String G() {
      return this.lastTitle();
   }

   public float[] H() {
      return this.spectrum();
   }

   public void a(String str) {
      this.title = str;
   }

   public void b(String str) {
      this.artist = str;
   }

   public void a(byte[] bArr) {
      this.artwork = bArr;
   }

   public void b(boolean z) {
      this.artworkChanged = z;
   }

   public void c(boolean z) {
      this.playing = z;
   }

   public void c(float f) {
      this.durationMs = f;
   }

   public void d(float f) {
      this.positionMs = f;
   }

   public void a(long j) {
      this.positionTimestampMs = j;
   }

   public void e(float f) {
      this.lastSourcePositionMs = f;
   }

   public void d(boolean z) {
      this.hasForcedPlaying = z;
   }

   public void e(boolean z) {
      this.fixedPosition = z;
   }

   public void f(float f) {
      this.fixedPositionMs = f;
   }

   public void f(boolean z) {
      this.waitingForSeekConfirmation = z;
   }

   public void g(float f) {
      this.seekBasePositionMs = f;
   }

   public void g(boolean z) {
      this.mediaCardVisible = z;
   }

   public void h(boolean z) {
      this.showTrackChanged = z;
   }

   public void i(boolean z) {
      this.showPlaybackStopped = z;
   }

   public void j(boolean z) {
      this.wasPlaying = z;
   }

   public void c(String str) {
      this.lastTitle = str;
   }
}
