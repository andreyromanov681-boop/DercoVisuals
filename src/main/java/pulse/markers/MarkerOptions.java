package pulse.markers;

import pulse.config.LocalConfigManager;

public final class MarkerOptions {
   private static int markerMode;
   private static boolean showFriendMarkers;
   private static boolean showHiddenMarkers;
   private static boolean markersEnabled = true;
   private static boolean showEventMarkers = true;

   private MarkerOptions() {
   }

   public static boolean markersEnabled() {
      return markersEnabled;
   }

   public static void setMarkersEnabled(boolean z) {
      if (markersEnabled != z) {
         markersEnabled = z;
         LocalConfigManager.get().requestSave("quick-marker-toggle");
      }
   }

   public static int markerMode() {
      return markerMode;
   }

   public static void setMarkerMode(int i) {
      if (markerMode != i) {
         markerMode = i;
         LocalConfigManager.get().requestSave("quick-marker-key");
      }
   }

   public static boolean showFriendMarkers() {
      return showFriendMarkers;
   }

   public static void setShowFriendMarkers(boolean z) {
      if (showFriendMarkers != z) {
         showFriendMarkers = z;
         LocalConfigManager.get().requestSave("death-marker-toggle");
      }
   }

   public static boolean showEventMarkers() {
      return showEventMarkers;
   }

   public static void setShowEventMarkers(boolean z) {
      if (showEventMarkers != z) {
         showEventMarkers = z;
         LocalConfigManager.get().requestSave("event-markers-toggle");
      }
   }

   public static boolean showHiddenMarkers() {
      return showHiddenMarkers;
   }

   public static void setShowHiddenMarkers(boolean z) {
      if (showHiddenMarkers != z) {
         showHiddenMarkers = z;
         LocalConfigManager.get().requestSave("mysterious-beacon-toggle");
      }
   }

   public static boolean a() {
      return markersEnabled();
   }

   public static void a(boolean z) {
      setMarkersEnabled(z);
   }

   public static int b() {
      return markerMode();
   }

   public static void a(int i) {
      setMarkerMode(i);
   }

   public static boolean c() {
      return showFriendMarkers();
   }

   public static void b(boolean z) {
      setShowFriendMarkers(z);
   }

   public static boolean d() {
      return showEventMarkers();
   }

   public static void c(boolean z) {
      setShowEventMarkers(z);
   }

   public static boolean e() {
      return showHiddenMarkers();
   }

   public static void d(boolean z) {
      setShowHiddenMarkers(z);
   }
}
