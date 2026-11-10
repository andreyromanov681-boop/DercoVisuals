package pulse.markers.providers;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import pulse.markers.MapMarkerModule;
import pulse.markers.MapMarkerStyle;

public class ActiveEventMarkerProvider extends AbstractEventMarkerProvider {
   private static final Map<String, MapMarkerStyle> EVENT_STYLES = new LinkedHashMap<>();

   public ActiveEventMarkerProvider() {
      super("/event active", EVENT_STYLES);
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
      return address.contains("reallyworld") || address.contains("really-world");
   }

   static {
      EVENT_STYLES.put("ценный груз", style(255, 182, 193));
      EVENT_STYLES.put("контейнер", style(148, 0, 211));
      EVENT_STYLES.put("посылка", style(95, 158, 160));
      EVENT_STYLES.put("золотая лихорадка", style(255, 215, 0));
      EVENT_STYLES.put("цветочная поляна", style(70, 130, 180));
      EVENT_STYLES.put("опытный тыпо", style(255, 140, 0));
      EVENT_STYLES.put("смертельная шахта", style(139, 0, 0));
      EVENT_STYLES.put("таинственный корабль", style(160, 82, 45));
   }
}
