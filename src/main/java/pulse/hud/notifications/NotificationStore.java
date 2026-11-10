package pulse.hud.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import pulse.markers.MapMarker;
import pulse.markers.MarkerManager;

public final class NotificationStore {
   private static final List<Notification> NOTIFICATIONS = new CopyOnWriteArrayList<>();

   private NotificationStore() {
   }

   public static void add(Notification notification) {
      if (notification != null && !NOTIFICATIONS.contains(notification)) {
         NOTIFICATIONS.add(notification);
      }
   }

   public static void remove(Notification notification) {
      NOTIFICATIONS.remove(notification);
   }

   public static List<Notification> visibleNotifications() {
      syncWithMarkers();
      pruneExpired();
      return new ArrayList<>(NOTIFICATIONS);
   }

   public static void clear() {
      NOTIFICATIONS.clear();
   }

   public static void reset() {
      NOTIFICATIONS.clear();
   }

   public static boolean isEmpty() {
      pruneExpired();
      return NOTIFICATIONS.isEmpty();
   }

   public static void pruneExpired() {
      NOTIFICATIONS.removeIf(Notification::isExpired);
   }

   public static void link(Notification notification, MapMarker marker) {
      if (notification != null) {
         notification.setMarker(marker);
      }
   }

   public static void syncWithMarkers() {
      List<MapMarker> markers = MarkerManager.a();
      NOTIFICATIONS.removeIf(notificationx -> notificationx.marker() != null && !markers.contains(notificationx.marker()));

      for (Notification notification : NOTIFICATIONS) {
         if (notification.marker() == null) {
            notification.setMarker(findLocalMarker(markers, notification));
         }

         if (notification.marker() != null) {
            syncFromMarker(notification, notification.marker());
         }
      }

      for (MapMarker marker : markers) {
         if (!marker.j() && !hasMarker(marker)) {
            Notification notification = new Notification(marker.a(), marker.b(), marker.c(), marker.d(), marker.e(), notificationIconOf(marker.f()));
            notification.setMarker(marker);
            NOTIFICATIONS.add(notification);
         }
      }
   }

   private static boolean hasMarker(MapMarker marker) {
      for (Notification notification : NOTIFICATIONS) {
         if (notification.marker() == marker) {
            return true;
         }
      }

      return false;
   }

   private static void syncFromMarker(Notification notification, MapMarker marker) {
      notification.a(marker.a());
      notification.a(marker.b());
      notification.b(marker.c());
      notification.c(marker.d());
      notification.a(marker.e());
      notification.a(notificationIconOf(marker.f()));
   }

   private static MapMarker findLocalMarker(List<MapMarker> markers, Notification notification) {
      for (MapMarker marker : markers) {
         if (!marker.j() && marker.b() == notification.b() && marker.c() == notification.c() && marker.d() == notification.d()) {
            return marker;
         }
      }

      return null;
   }

   private static Notification.Icon notificationIconOf(MapMarker.Icon icon) {
      if (icon == null) {
         return Notification.Icon.EVENT;
      }

      try {
         return Notification.Icon.valueOf(icon.name());
      } catch (IllegalArgumentException ex) {
         return Notification.Icon.EVENT;
      }
   }

   public static Notification hitTest(int i, int i2, int i3) {
      for (Notification notification : visibleNotifications()) {
         if (notification.b() == i && notification.c() == i2 && notification.d() == i3) {
            return notification;
         }
      }

      return null;
   }

   public static List<Notification> savedNotifications() {
      return visibleNotifications();
   }

   public static List<Notification> allNotifications() {
      return new ArrayList<>(NOTIFICATIONS);
   }

   public static void a(Notification notification) {
      add(notification);
   }

   public static void b(Notification notification) {
      remove(notification);
   }

   public static List<Notification> a() {
      return visibleNotifications();
   }

   public static void b() {
      clear();
   }

   public static void c() {
      reset();
   }

   public static boolean d() {
      return isEmpty();
   }

   public static void e() {
      pruneExpired();
   }

   public static Notification a(int i, int i2, int i3) {
      return hitTest(i, i2, i3);
   }

   public static List<Notification> f() {
      return savedNotifications();
   }

   public static List<Notification> g() {
      return allNotifications();
   }
}
