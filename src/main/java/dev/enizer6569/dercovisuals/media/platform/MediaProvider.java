// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.media.platform;

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
