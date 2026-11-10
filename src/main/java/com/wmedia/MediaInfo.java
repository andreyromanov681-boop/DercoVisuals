package com.wmedia;

public final class MediaInfo {
   private final String title;
   private final String artist;
   private final boolean playing;
   private final float positionMs;
   private final float durationMs;
   private final byte[] albumArt;

   public MediaInfo(String str, String str2, boolean z, float f, float f2, byte[] bArr) {
      this.title = str == null ? "" : str;
      this.artist = str2 == null ? "" : str2;
      this.playing = z;
      this.positionMs = f;
      this.durationMs = f2;
      this.albumArt = bArr == null ? new byte[0] : (byte[])bArr.clone();
   }

   public String getTitle() {
      return this.title;
   }

   public String getArtist() {
      return this.artist;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public float getPositionMs() {
      return this.positionMs;
   }

   public float getDurationMs() {
      return this.durationMs;
   }

   public byte[] getAlbumArt() {
      return (byte[])this.albumArt.clone();
   }
}
