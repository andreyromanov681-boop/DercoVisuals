package com.wmedia;

import java.util.Optional;

public final class MediaProvider {
   private MediaProvider() {
   }

   public static Optional<MediaInfo> getCurrentMedia() {
      return Optional.empty();
   }

   public static Optional<AudioLevels> getAudioLevels() {
      return Optional.empty();
   }

   public static boolean isAvailable() {
      return false;
   }

   public static void playPause() {
   }

   public static void next() {
   }

   public static void previous() {
   }

   public static void seek(long j) {
   }

   public static void shutdown() {
   }
}
