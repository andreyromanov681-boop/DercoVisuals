package pulse.markers;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

public interface MarkerProvider {
   boolean a(String var1);

   boolean a(String var1, String var2, MapMarkerModule var3);

   Set<String> a();

   Map<String, MapMarkerStyle> b();

   String c();

   default boolean supportsServer(String str) {
      return this.a(str);
   }

   default boolean handleMessage(String str, String str2, MapMarkerModule mapMarkerModule) {
      return this.a(str, str2, mapMarkerModule);
   }

   default Set<String> eventNames() {
      return this.a();
   }

   default Map<String, MapMarkerStyle> styles() {
      return this.b();
   }

   default String command() {
      return this.c();
   }

   default MapMarkerStyle d() {
      return new MapMarkerStyle(new Color(255, 165, 0));
   }

   default void a(MapMarkerModule mapMarkerModule) {
   }

   default void tick(MapMarkerModule mapMarkerModule) {
      this.a(mapMarkerModule);
   }
}
