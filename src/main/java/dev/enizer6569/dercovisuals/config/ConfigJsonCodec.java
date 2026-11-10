// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigJsonCodec {
   private static final int c = 2;
   public static int a;
   public static boolean b;

   private ConfigJsonCodec() {
   }

   public static String a(ConfigState configState) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("version", configState.a());
      jSONObject.put("metadata", a(configState.b()));
      jSONObject.put("modules", a(configState.c()));
      jSONObject.put("friends", a(configState.d()));
      jSONObject.put("markers", b(configState.e()));
      jSONObject.put("markerSettings", a(configState.f()));
      jSONObject.put("hudPositions", c(configState.g()));
      jSONObject.put("categoriesPosition", configState.h());
      jSONObject.put("watermarkSettings", a(configState.i()));
      jSONObject.put("clientColor", a(configState.j()));
      return jSONObject.toString(2);
   }

   private static JSONObject a(ConfigProfile configProfile) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      if (configProfile == null) {
         return jSONObject;
      }

      jSONObject.put("name", configProfile.i());
      jSONObject.put("author", configProfile.c());
      jSONObject.put("authorId", configProfile.d());
      jSONObject.put("signature", configProfile.e());
      jSONObject.put("createdAt", configProfile.f());
      jSONObject.put("description", configProfile.j());
      jSONObject.put("updatedAt", configProfile.k());
      return jSONObject;
   }

   private static JSONObject a(Map<String, ConfigState.ModuleState> map) throws JSONException {
      JSONObject jSONObject = new JSONObject();

      for (Entry<String, ConfigState.ModuleState> entry : map.entrySet()) {
         jSONObject.put(entry.getKey(), a(entry.getValue()));
      }

      return jSONObject;
   }

   private static JSONObject a(ConfigState.ModuleState moduleState) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("enabled", moduleState.a());
      jSONObject.put("key", moduleState.b());
      jSONObject.put("settings", b(moduleState.c()));
      return jSONObject;
   }

   private static JSONObject b(Map<String, ConfigState.SettingState> map) throws JSONException {
      JSONObject jSONObject = new JSONObject();

      for (Entry<String, ConfigState.SettingState> entry : map.entrySet()) {
         jSONObject.put(entry.getKey(), a(entry.getValue()));
      }

      return jSONObject;
   }

   private static JSONObject a(ConfigState.SettingState settingState) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("type", settingState.a());
      String strA = settingState.a();
      byte b2 = -1;
      switch (strA.hashCode()) {
         case -1034364087:
         case 3357091:
         case 64711720:
         case 100358090:
         case 2127804432:
         default:
            break;
         case 106079:
            if (strA.equals("key")) {
               b2 = 2;
            }
            break;
         case 94842723:
            if (strA.equals("color") && b) {
               throw new IllegalAccessError();
            }
      }

      switch (b2) {
         case 0:
         case 1:
         case 2:
         case 3:
            jSONObject.put("value", settingState.b());
            break;
         case 4:
            jSONObject.put("hue", settingState.c());
            jSONObject.put("saturation", settingState.d());
            jSONObject.put("brightness", settingState.e());
            break;
         case 5:
            jSONObject.put("value", settingState.b());
            if (settingState.f() != null && settingState.f().length > 0) {
               jSONObject.put("selectedIndices", new JSONArray(settingState.f()));
            }
            break;
         case 6:
            jSONObject.put("enabled", settingState.g());
            jSONObject.put("colorRgb", settingState.h());
      }

      return jSONObject;
   }

   private static JSONArray a(List<ConfigState.FriendHistoryState> list) throws JSONException {
      JSONArray jSONArray = new JSONArray();

      for (ConfigState.FriendHistoryState friendHistoryState : list) {
         JSONObject jSONObject = new JSONObject();
         jSONObject.put("name", friendHistoryState.a());
         jSONObject.put("addedDate", friendHistoryState.b());
         jSONArray.put(jSONObject);
      }

      return jSONArray;
   }

   private static JSONArray b(List<ConfigState.NotificationState> list) throws JSONException {
      JSONArray jSONArray = new JSONArray();

      for (ConfigState.NotificationState notificationState : list) {
         JSONObject jSONObject = new JSONObject();
         jSONObject.put("name", notificationState.a());
         jSONObject.put("x", notificationState.b());
         jSONObject.put("y", notificationState.c());
         jSONObject.put("z", notificationState.d());
         jSONObject.put("colorRgb", notificationState.e());
         jSONObject.put("iconType", notificationState.f());
         jSONObject.put("autoCreated", notificationState.g());
         jSONObject.put("creationTime", notificationState.h());
         jSONObject.put("serverNumber", notificationState.i());
         jSONObject.put("expirationTime", notificationState.j());
         jSONArray.put(jSONObject);
      }

      return jSONArray;
   }

   private static JSONObject a(ConfigState.MarkerOptionsState markerOptionsState) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("quickMarkerEnabled", markerOptionsState.a());
      jSONObject.put("quickMarkerKey", markerOptionsState.b());
      jSONObject.put("deathMarkerEnabled", markerOptionsState.c());
      jSONObject.put("autoMarkersEnabled", markerOptionsState.d());
      jSONObject.put("mysteriousBeaconEnabled", markerOptionsState.e());
      return jSONObject;
   }

   private static JSONObject c(Map<String, ConfigState.HudElementState> map) throws JSONException {
      JSONObject jSONObject = new JSONObject();

      for (Entry<String, ConfigState.HudElementState> entry : map.entrySet()) {
         JSONObject jSONObject2 = new JSONObject();
         jSONObject2.put("percentX", entry.getValue().a());
         jSONObject2.put("percentY", entry.getValue().b());
         jSONObject2.put("scale", entry.getValue().c());
         jSONObject.put(entry.getKey(), jSONObject2);
      }

      return jSONObject;
   }

   private static JSONObject a(ConfigState.NotificationSettingsState notificationSettingsState) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("notificationsEnabled", notificationSettingsState.a());
      jSONObject.put("musicEnabled", notificationSettingsState.b());
      jSONObject.put("fpsAndPingEnabled", notificationSettingsState.c());
      jSONObject.put("eventsEnabled", notificationSettingsState.d());
      jSONObject.put("modulesEnabled", notificationSettingsState.e());
      return jSONObject;
   }

   private static JSONObject a(ConfigState.ClientColorState clientColorState) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      if (clientColorState == null) {
         return jSONObject;
      }

      jSONObject.put("mode", clientColorState.a());
      jSONObject.put("staticHue", clientColorState.b());
      jSONObject.put("staticSaturation", clientColorState.c());
      jSONObject.put("staticBrightness", clientColorState.d());
      jSONObject.put("gradient1Hue", clientColorState.e());
      jSONObject.put("gradient1Saturation", clientColorState.f());
      jSONObject.put("gradient1Brightness", clientColorState.g());
      jSONObject.put("gradient2Hue", clientColorState.h());
      jSONObject.put("gradient2Saturation", clientColorState.i());
      jSONObject.put("gradient2Brightness", clientColorState.j());
      return jSONObject;
   }

   public static ConfigState a(String str) {
      JSONObject jSONObject = new JSONObject(str);
      ConfigState configState = new ConfigState();
      configState.a(jSONObject.optInt("version", 1));
      configState.a(a(jSONObject.optJSONObject("metadata")));
      configState.a(b(jSONObject.optJSONObject("modules")));
      configState.a(a(jSONObject.optJSONArray("friends")));
      configState.b(b(jSONObject.optJSONArray("markers")));
      configState.a(f(jSONObject.optJSONObject("markerSettings")));
      configState.b(g(jSONObject.optJSONObject("hudPositions")));
      configState.a(jSONObject.optString("categoriesPosition", "BOTTOM"));
      configState.a(h(jSONObject.optJSONObject("watermarkSettings")));
      configState.a(i(jSONObject.optJSONObject("clientColor")));
      return configState;
   }

   private static ConfigProfile a(JSONObject jSONObject) {
      return jSONObject == null
         ? ConfigProfile.a("Unnamed")
         : ConfigProfile.a(
            jSONObject.optString("name", "Unnamed"),
            jSONObject.optString("author", "Unknown"),
            jSONObject.optInt("authorId", 0),
            jSONObject.optString("signature", ""),
            jSONObject.optLong("createdAt", System.currentTimeMillis()),
            jSONObject.optString("description", ""),
            jSONObject.optLong("updatedAt", System.currentTimeMillis())
         );
   }

   private static Map<String, ConfigState.ModuleState> b(JSONObject jSONObject) {
      HashMap map = new HashMap();
      if (jSONObject == null) {
         return map;
      }

      for (String str : jSONObject.keySet()) {
         JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(str);
         if (jSONObjectOptJSONObject != null) {
            map.put(str, c(jSONObjectOptJSONObject));
         }
      }

      return map;
   }

   private static ConfigState.ModuleState c(JSONObject jSONObject) {
      ConfigState.ModuleState moduleState = new ConfigState.ModuleState();
      moduleState.a(jSONObject.optBoolean("enabled", false));
      moduleState.a(jSONObject.optInt("key", 0));
      moduleState.a(d(jSONObject.optJSONObject("settings")));
      return moduleState;
   }

   private static Map<String, ConfigState.SettingState> d(JSONObject jSONObject) {
      HashMap map = new HashMap();
      if (jSONObject == null) {
         return map;
      }

      for (String str : jSONObject.keySet()) {
         JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(str);
         if (jSONObjectOptJSONObject != null) {
            map.put(str, e(jSONObjectOptJSONObject));
         }
      }

      return map;
   }

   private static ConfigState.SettingState e(JSONObject jSONObject) {
      ConfigState.SettingState settingState = new ConfigState.SettingState();
      String strOptString = jSONObject.optString("type", "unknown");
      settingState.a(strOptString);
      byte b2 = -1;
      switch (strOptString.hashCode()) {
         case -1034364087:
         case 64711720:
         case 94842723:
         case 100358090:
         case 2127804432:
         default:
            break;
         case 106079:
            if (strOptString.equals("key")) {
               b2 = 2;
            }
            break;
         case 3357091:
            if (strOptString.equals("mode")) {
            }
      }

      switch (b2) {
         case 0:
            settingState.a(jSONObject.optBoolean("value", false));
            break;
         case 1:
            settingState.a((float)jSONObject.optDouble("value", 0.0));
            break;
         case 2:
            settingState.a(jSONObject.optInt("value", -1));
            break;
         case 3:
            settingState.a((Object)jSONObject.optString("value", ""));
            break;
         case 4:
            settingState.a((float)jSONObject.optDouble("hue", 0.0));
            settingState.b((float)jSONObject.optDouble("saturation", 1.0));
            settingState.c((float)jSONObject.optDouble("brightness", 1.0));
            break;
         case 5:
            settingState.a(jSONObject.optInt("value", 0));
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("selectedIndices");
            if (jSONArrayOptJSONArray != null) {
               int[] iArr = new int[jSONArrayOptJSONArray.length()];

               for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                  iArr[i] = jSONArrayOptJSONArray.optInt(i, 0);
               }

               settingState.a(iArr);
            }
            break;
         case 6:
            settingState.a(jSONObject.optBoolean("enabled", true));
            settingState.a(jSONObject.optInt("colorRgb", -1));
      }

      return settingState;
   }

   private static List<ConfigState.FriendHistoryState> a(JSONArray jSONArray) {
      ArrayList arrayList = new ArrayList();
      if (jSONArray == null) {
         return arrayList;
      }

      for (int i = 0; i < jSONArray.length(); i++) {
         JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
         if (jSONObjectOptJSONObject != null) {
            ConfigState.FriendHistoryState friendHistoryState = new ConfigState.FriendHistoryState();
            friendHistoryState.a(jSONObjectOptJSONObject.optString("name", ""));
            friendHistoryState.b(jSONObjectOptJSONObject.optString("addedDate", ""));
            arrayList.add(friendHistoryState);
         }
      }

      return arrayList;
   }

   private static List<ConfigState.NotificationState> b(JSONArray jSONArray) {
      ArrayList arrayList = new ArrayList();
      if (jSONArray == null) {
         return arrayList;
      }

      for (int i = 0; i < jSONArray.length(); i++) {
         JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
         if (jSONObjectOptJSONObject != null) {
            ConfigState.NotificationState notificationState = new ConfigState.NotificationState();
            notificationState.a(jSONObjectOptJSONObject.optString("name", ""));
            notificationState.a(jSONObjectOptJSONObject.optInt("x", 0));
            notificationState.b(jSONObjectOptJSONObject.optInt("y", 0));
            notificationState.c(jSONObjectOptJSONObject.optInt("z", 0));
            notificationState.d(jSONObjectOptJSONObject.optInt("colorRgb", -1));
            notificationState.b(jSONObjectOptJSONObject.optString("iconType", "HOME"));
            notificationState.a(jSONObjectOptJSONObject.optBoolean("autoCreated", false));
            notificationState.a(jSONObjectOptJSONObject.optLong("creationTime", 0L));
            notificationState.e(jSONObjectOptJSONObject.optInt("serverNumber", -1));
            notificationState.b(jSONObjectOptJSONObject.optLong("expirationTime", 0L));
            arrayList.add(notificationState);
         }
      }

      return arrayList;
   }

   private static ConfigState.MarkerOptionsState f(JSONObject jSONObject) {
      ConfigState.MarkerOptionsState markerOptionsState = new ConfigState.MarkerOptionsState();
      if (jSONObject == null) {
         return markerOptionsState;
      }

      markerOptionsState.a(jSONObject.optBoolean("quickMarkerEnabled", true));
      markerOptionsState.a(jSONObject.optInt("quickMarkerKey", 0));
      markerOptionsState.b(jSONObject.optBoolean("deathMarkerEnabled", false));
      markerOptionsState.c(jSONObject.optBoolean("autoMarkersEnabled", true));
      markerOptionsState.d(jSONObject.optBoolean("mysteriousBeaconEnabled", false));
      return markerOptionsState;
   }

   private static Map<String, ConfigState.HudElementState> g(JSONObject jSONObject) {
      HashMap map = new HashMap();
      if (jSONObject == null) {
         return map;
      }

      for (String str : jSONObject.keySet()) {
         JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject(str);
         if (jSONObjectOptJSONObject != null) {
            ConfigState.HudElementState hudElementState = new ConfigState.HudElementState();
            hudElementState.a((float)jSONObjectOptJSONObject.optDouble("percentX", 0.0));
            hudElementState.b((float)jSONObjectOptJSONObject.optDouble("percentY", 0.0));
            hudElementState.c((float)jSONObjectOptJSONObject.optDouble("scale", 1.0));
            map.put(str, hudElementState);
         }
      }

      return map;
   }

   private static ConfigState.NotificationSettingsState h(JSONObject jSONObject) {
      ConfigState.NotificationSettingsState notificationSettingsState = new ConfigState.NotificationSettingsState();
      if (jSONObject == null) {
         return notificationSettingsState;
      }

      notificationSettingsState.a(jSONObject.optBoolean("notificationsEnabled", true));
      notificationSettingsState.b(jSONObject.optBoolean("musicEnabled", true));
      notificationSettingsState.c(jSONObject.optBoolean("fpsAndPingEnabled", true));
      notificationSettingsState.d(jSONObject.optBoolean("eventsEnabled", true));
      notificationSettingsState.e(jSONObject.optBoolean("modulesEnabled", true));
      return notificationSettingsState;
   }

   private static ConfigState.ClientColorState i(JSONObject jSONObject) {
      ConfigState.ClientColorState clientColorState = new ConfigState.ClientColorState();
      if (jSONObject == null) {
         return clientColorState;
      }

      clientColorState.a(jSONObject.optString("mode", "Статичный"));
      clientColorState.a((float)jSONObject.optDouble("staticHue", 0.75));
      clientColorState.b((float)jSONObject.optDouble("staticSaturation", 0.81));
      clientColorState.c((float)jSONObject.optDouble("staticBrightness", 0.89));
      clientColorState.d((float)jSONObject.optDouble("gradient1Hue", 0.0));
      clientColorState.e((float)jSONObject.optDouble("gradient1Saturation", 0.0));
      clientColorState.f((float)jSONObject.optDouble("gradient1Brightness", 1.0));
      clientColorState.g((float)jSONObject.optDouble("gradient2Hue", 0.58));
      clientColorState.h((float)jSONObject.optDouble("gradient2Saturation", 0.51));
      clientColorState.i((float)jSONObject.optDouble("gradient2Brightness", 0.96));
      return clientColorState;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
