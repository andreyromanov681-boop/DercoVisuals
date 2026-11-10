package pulse.markers.providers;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pulse.markers.MapMarkerModule;
import pulse.markers.MapMarkerStyle;
import pulse.markers.MarkerProvider;

public abstract class AbstractEventMarkerProvider implements MarkerProvider {
   private static final Pattern XYZ_PATTERN = Pattern.compile("(?:x|х)\\D*(-?\\d+)\\D+(?:y|у)\\D*(-?\\d+)\\D+(?:z|з)\\D*(-?\\d+)", 66);
   private static final Pattern TRIPLE_NUMBER_PATTERN = Pattern.compile("(-?\\d{2,6})\\D+(-?\\d{1,4})\\D+(-?\\d{2,6})");
   private static final Pattern SECONDS_PATTERN = Pattern.compile("(\\d+)\\s*(?:сек|sec|s)", 66);
   private static final Pattern MINUTES_PATTERN = Pattern.compile("(\\d+)\\s*(?:мин|min|m)", 66);
   private final String command;
   private final Map<String, MapMarkerStyle> styles;
   private final Set<String> eventNames;

   protected AbstractEventMarkerProvider(String str, Map<String, MapMarkerStyle> map) {
      this.command = str;
      this.styles = Collections.unmodifiableMap(new LinkedHashMap<>(map));
      this.eventNames = Collections.unmodifiableSet(this.styles.keySet());
   }

   @Override
   public boolean a(String str) {
      return str != null && !str.isBlank();
   }

   @Override
   public boolean a(String str, String str2, MapMarkerModule mapMarkerModule) {
      String str3 = str != null ? str : "";
      String strFindEventName = this.findEventName(str3);
      int[] iArrFindCoordinates = this.findCoordinates(str2 != null ? str2 : str3);
      if (strFindEventName != null && iArrFindCoordinates != null) {
         mapMarkerModule.addMarker(strFindEventName, iArrFindCoordinates, this.findLifetimeSeconds(str3));
         mapMarkerModule.completeProviderResponse();
         return true;
      } else if (strFindEventName != null) {
         mapMarkerModule.setPendingMarker(strFindEventName, mapMarkerModule.h());
         return true;
      } else if (iArrFindCoordinates != null && mapMarkerModule.o() != null) {
         mapMarkerModule.addPendingMarker(iArrFindCoordinates);
         mapMarkerModule.completeProviderResponse();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Set<String> a() {
      return this.eventNames;
   }

   @Override
   public Map<String, MapMarkerStyle> b() {
      return this.styles;
   }

   @Override
   public String c() {
      return this.command;
   }

   protected static MapMarkerStyle style(int i, int i2, int i3) {
      return new MapMarkerStyle(new Color(i, i2, i3));
   }

   private String findEventName(String str) {
      String lowerCase = str.toLowerCase(Locale.ROOT);

      for (String str2 : this.eventNames) {
         if (lowerCase.contains(str2)) {
            return str2;
         }
      }

      return null;
   }

   private int[] findCoordinates(String str) {
      Matcher matcher = XYZ_PATTERN.matcher(str);
      if (matcher.find()) {
         return new int[]{parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3))};
      }

      Matcher matcher2 = TRIPLE_NUMBER_PATTERN.matcher(str);
      return matcher2.find() ? new int[]{parseInt(matcher2.group(1)), parseInt(matcher2.group(2)), parseInt(matcher2.group(3))} : null;
   }

   private int findLifetimeSeconds(String str) {
      Matcher matcher = SECONDS_PATTERN.matcher(str);
      if (matcher.find()) {
         return parseInt(matcher.group(1));
      }

      Matcher matcher2 = MINUTES_PATTERN.matcher(str);
      return matcher2.find() ? parseInt(matcher2.group(1)) * 60 : -1;
   }

   private static int parseInt(String str) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException e) {
         return 0;
      }
   }
}
