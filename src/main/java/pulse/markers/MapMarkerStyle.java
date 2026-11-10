package pulse.markers;

import java.awt.Color;

public class MapMarkerStyle {
   public final Color a;
   public final MapMarker.Icon b;

   public MapMarkerStyle(Color color) {
      this(color, MapMarker.Icon.EVENT);
   }

   public MapMarkerStyle(Color color, MapMarker.Icon icon) {
      this.a = color;
      this.b = icon != null ? icon : MapMarker.Icon.EVENT;
   }

   public Color color() {
      return this.a;
   }

   public MapMarker.Icon icon() {
      return this.b;
   }
}
