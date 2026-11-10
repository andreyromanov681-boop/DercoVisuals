// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.markers.providers;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import dev.enizer6569.dercovisuals.markers.MapMarkerModule;
import dev.enizer6569.dercovisuals.markers.MapMarkerStyle;

public class EventDelayMarkerProvider extends AbstractEventMarkerProvider {
   private static final Map<String, MapMarkerStyle> EVENT_STYLES = new LinkedHashMap<>();

   public EventDelayMarkerProvider() {
      super("/event delay", EVENT_STYLES);
   }

   @Override
   public String c() {
      return super.c();
   }

   @Override
   public Map b() {
      return super.b();
   }

   @Override
   public Set a() {
      return super.a();
   }

   @Override
   public boolean a(String str, String str2, MapMarkerModule mapMarkerModule) {
      return super.a(str, str2, mapMarkerModule);
   }

   @Override
   public boolean a(String str) {
      if (str == null) {
         return false;
      }

      String address = str.toLowerCase(Locale.ROOT);
      return address.contains("funtime") || address.contains("fun-time");
   }

   static {
      EVENT_STYLES.put("мистический сундук", style(220, 20, 60));
      EVENT_STYLES.put("маяк убийца", style(138, 43, 226));
      EVENT_STYLES.put("вулкан", style(255, 69, 0));
      EVENT_STYLES.put("загадочный маяк", style(75, 0, 130));
      EVENT_STYLES.put("метеоритный дождь", style(0, 191, 255));
      EVENT_STYLES.put("алтарь", style(178, 34, 34));
   }
}
