// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import dev.enizer6569.dercovisuals.markers.MapMarker;
import dev.enizer6569.dercovisuals.markers.MarkerManager;

public final class GpsConfigManager {
   private static final String APPDATA_DIR = "DercoVisuals";
   private static final String GPS_FILE = "gps.cfg";
   private static final int FORMAT_VERSION = 1;
   private static final long DEBOUNCE_MS = 200L;
   private volatile ScheduledFuture<?> pending;
   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread thread = new Thread(runnable, "pulse-gps-save");
      thread.setDaemon(true);
      return thread;
   });
   private static final Logger LOG = LogManager.getLogger("pulse/gps");
   private static final GpsConfigManager INSTANCE = new GpsConfigManager();
   private static final Color DEFAULT_COLOR = new Color(80, 220, 100);

   private GpsConfigManager() {
   }

   public static GpsConfigManager get() {
      return INSTANCE;
   }

   public void init() {
      try {
         Path filePath = getFilePath();
         Files.createDirectories(filePath.getParent());
         if (Files.exists(filePath)) {
            this.load(filePath);
         } else {
            writeFile(filePath, buildJson(List.of()));
            LOG.info("[GPS] Created default {}", filePath);
         }
      } catch (Exception e) {
         LOG.warn("[GPS] init() failed", e);
      }
   }

   public void save() {
      synchronized (this) {
         if (this.pending != null) {
            this.pending.cancel(false);
         }

         this.pending = this.executor.schedule(this::doSave, 200L, TimeUnit.MILLISECONDS);
      }
   }

   public void flushNow() {
      synchronized (this) {
         if (this.pending != null) {
            this.pending.cancel(false);
            this.pending = null;
         }
      }

      this.doSave();
   }

   private void doSave() {
      try {
         List<MapMarker> listLocalMarkers = localMarkers();
         Path filePath = getFilePath();
         Files.createDirectories(filePath.getParent());
         writeFile(filePath, buildJson(listLocalMarkers));
         LOG.debug("[GPS] Saved {} markers -> {}", listLocalMarkers.size(), filePath);
      } catch (Exception e) {
         LOG.warn("[GPS] Save failed", e);
      }
   }

   private void load(Path path) {
      try {
         String strTrim = Files.readString(path, StandardCharsets.UTF_8).trim();
         JSONArray jSONArrayOptJSONArray;
         if (strTrim.isEmpty() || (jSONArrayOptJSONArray = new JSONObject(strTrim).optJSONArray("markers")) == null) {
            return;
         }

         for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
            JSONObject jSONObject = jSONArrayOptJSONArray.getJSONObject(i);

            MapMarker.Icon icon;
            try {
               icon = MapMarker.Icon.valueOf(jSONObject.optString("icon", "HOME"));
            } catch (IllegalArgumentException ex) {
               icon = MapMarker.Icon.HOME;
            }

            MarkerManager.a(
               new MapMarker(
                  jSONObject.optString("name", "GPS " + i),
                  jSONObject.optInt("x", 0),
                  jSONObject.optInt("y", 64),
                  jSONObject.optInt("z", 0),
                  jSONObject.has("color") ? new Color(jSONObject.getInt("color"), true) : DEFAULT_COLOR,
                  icon
               )
            );
         }

         LOG.info("[GPS] Loaded {} markers from {}", jSONArrayOptJSONArray.length(), path);
      } catch (Exception e) {
         LOG.warn("[GPS] Load failed: {}", path, e);
      }
   }

   private static String buildJson(List<MapMarker> list) throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("version", 1);
      JSONArray jSONArray = new JSONArray();

      for (MapMarker mapMarker : list) {
         JSONObject jSONObject2 = new JSONObject();
         jSONObject2.put("name", mapMarker.a());
         jSONObject2.put("x", mapMarker.b());
         jSONObject2.put("y", mapMarker.c());
         jSONObject2.put("z", mapMarker.d());
         jSONObject2.put("color", mapMarker.e().getRGB());
         jSONObject2.put("icon", mapMarker.f().name());
         jSONArray.put(jSONObject2);
      }

      jSONObject.put("markers", jSONArray);
      return jSONObject.toString(2);
   }

   private static List<MapMarker> localMarkers() {
      ArrayList arrayList = new ArrayList();

      for (MapMarker mapMarker : MarkerManager.a()) {
         if (!mapMarker.j()) {
            arrayList.add(mapMarker);
         }
      }

      return arrayList;
   }

   private static Path getFilePath() {
      String str = System.getenv("APPDATA");
      if (str != null && !str.isBlank()) {
         return Path.of(str, "DercoVisuals", "config", "gps.cfg");
      }

      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      return MinecraftClientVarGetInstance != null
         ? MinecraftClientVarGetInstance.runDirectory.toPath().resolve("config").resolve("dercovisuals").resolve("gps.cfg")
         : Path.of(System.getProperty("user.home"), "DercoVisuals", "config", "gps.cfg");
   }

   private static void writeFile(Path path, String str) throws IOException {
      Files.writeString(path, str, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
   }
}
