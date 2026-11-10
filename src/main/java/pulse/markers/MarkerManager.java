package pulse.markers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pulse.config.LocalConfigManager;

public final class MarkerManager {
   private static final List<MapMarker> MARKERS = Collections.synchronizedList(new ArrayList<>());

   private MarkerManager() {
   }

   public static void a(MapMarker mapMarker) {
      if (mapMarker != null) {
         MARKERS.add(mapMarker);
         if (!mapMarker.j()) {
            LocalConfigManager.get().requestSave("marker-add");
         }
      }
   }

   public static void b(MapMarker mapMarker) {
      if (MARKERS.remove(mapMarker) && mapMarker != null && !mapMarker.j()) {
         LocalConfigManager.get().requestSave("marker-remove");
      }
   }

   public static List<MapMarker> a() {
      synchronized (MARKERS) {
         return new ArrayList<>(MARKERS);
      }
   }

   public static void b() {
      MARKERS.clear();
   }

   public static void c() {
      MARKERS.clear();
   }

   public static boolean d() {
      return MARKERS.isEmpty();
   }

   public static void e() {
      MARKERS.removeIf(v0 -> v0.o());
   }

   public static void removeServerBound() {
      MARKERS.removeIf(MapMarker::j);
   }

   public static void removeLocal() {
      MARKERS.removeIf(marker -> !marker.j());
      LocalConfigManager.get().requestSave("markers-clear-local");
   }

   public static MapMarker a(int i, int i2, int i3) {
      synchronized (MARKERS) {
         for (MapMarker mapMarker : MARKERS) {
            if (mapMarker.b() == i && mapMarker.c() == i2 && mapMarker.d() == i3) {
               return mapMarker;
            }
         }

         return null;
      }
   }

   public static List<MapMarker> f() {
      return a();
   }

   public static List<MapMarker> g() {
      return a();
   }
}
