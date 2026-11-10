// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.config;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import dev.enizer6569.dercovisuals.hud.core.HudElement;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.hud.notifications.NotificationStore;
import dev.enizer6569.dercovisuals.markers.MapMarker;
import dev.enizer6569.dercovisuals.markers.MarkerManager;
import dev.enizer6569.dercovisuals.markers.MarkerOptions;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ColorSetting;
import dev.enizer6569.dercovisuals.settings.ItemToggleSetting;
import dev.enizer6569.dercovisuals.settings.KeySetting;
import dev.enizer6569.dercovisuals.settings.ModeSetting;
import dev.enizer6569.dercovisuals.settings.Setting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;
import dev.enizer6569.dercovisuals.settings.SliderSetting;
import dev.enizer6569.dercovisuals.settings.TokenSetting;

public class LocalConfigManager {
   private static final Logger LOG = LogManager.getLogger("dercovisuals/config");
   private static final LocalConfigManager INSTANCE = new LocalConfigManager();
   private static final String FOLDER_NAME = "dercovisuals";
   private static final String APPDATA_DIR = "DercoVisuals";
   private static final String FILE_EXT = ".json";
   private static final String AUTOSAVE_FILE = "dercovisuals.cfg";
   private static final String DEFAULT_NAME = "default";
   private static final int FORMAT_VER = 1;
   private static final long SAVE_DEBOUNCE_MS = 200L;
   private volatile boolean applying;
   private volatile boolean initialized;
   private volatile ScheduledFuture<?> pendingSave;
   private final LinkedHashMap<String, String> profiles = new LinkedHashMap<>();
   private String activeProfile = "default";
   private final ScheduledExecutorService saveExecutor = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread thread = new Thread(runnable, "dercovisuals-autosave");
      thread.setDaemon(true);
      return thread;
   });

   public static LocalConfigManager get() {
      return INSTANCE;
   }

   private LocalConfigManager() {
   }

   public boolean isApplying() {
      return this.applying;
   }

   public String configDirectory() {
      return this.getConfigDir().toAbsolutePath().toString();
   }

   public void init() {
      this.migrateLegacyConfigIfNeeded();
      LOG.info("[DercoVisuals] init() — config dir: {}", this.configDirectory());
      this.applying = true;
      boolean createAutosave = false;

      try {
         this.loadAllFromDisk();
         LOG.info("[DercoVisuals] Profiles on disk: {}", this.profiles.keySet());
         String autosave = this.readAutosave();
         if (autosave != null && !autosave.isBlank()) {
            this.applyJson(autosave);
            this.profiles.put(this.activeProfile, autosave);
            this.logLoadSummary("dercovisuals.cfg", autosave);
         } else if (this.profiles.isEmpty()) {
            LOG.info("[DercoVisuals] No config files found, using defaults");
            createAutosave = true;
         } else {
            String next = this.profiles.containsKey(this.activeProfile) ? this.activeProfile : this.profiles.keySet().iterator().next();
            this.applyProfile(next);
            this.logLoadSummary(next + ".json", this.profiles.get(next));
            createAutosave = true;
         }
      } finally {
         this.applying = false;
         this.initialized = true;
      }

      if (createAutosave) {
         LOG.info("[DercoVisuals] Creating initial {}", "dercovisuals.cfg");
         this.flushSaveNow("init-migrate");
      }
   }

   public void flushSaveNow(String str) {
      synchronized (this) {
         if (this.pendingSave != null) {
            this.pendingSave.cancel(false);
            this.pendingSave = null;
         }
      }

      if (this.applying) {
         LOG.debug("[DercoVisuals] flushSaveNow skipped (applying): {}", str);
      } else {
         LOG.debug("[DercoVisuals] flushSaveNow: {}", str);
         this.writeStateToDisk();
      }
   }

   public void requestSave(String str) {
      if (!this.initialized) {
         LOG.debug("[DercoVisuals] Autosave skipped before config initialization: {}", str);
      } else if (this.applying) {
         LOG.debug("[DercoVisuals] Autosave skipped (applying): {}", str);
      } else {
         LOG.debug("[DercoVisuals] Autosave scheduled in {}ms: {}", 200L, str);
         synchronized (this) {
            if (this.pendingSave != null) {
               this.pendingSave.cancel(false);
            }

            this.pendingSave = this.saveExecutor.schedule(this::flushSave, 200L, TimeUnit.MILLISECONDS);
         }
      }
   }

   public void requestSave() {
      this.requestSave("unspecified");
   }

   public void saveCurrentState() {
      this.requestSave("saveCurrentState");
   }

   private void flushSave() {
      if (this.applying) {
         LOG.debug("[DercoVisuals] flushSave skipped (applying)");
      } else {
         this.writeStateToDisk();
      }
   }

   private void writeStateToDisk() {
      if (this.applying) {
         LOG.debug("[DercoVisuals] writeStateToDisk skipped (applying)");
      } else {
         try {
            String strSerializeState = this.serializeState();
            this.profiles.put(this.activeProfile, strSerializeState);
            Path pathWriteAutosave = this.writeAutosave(strSerializeState);
            Path pathWriteToDisk = this.writeToDisk(this.activeProfile, strSerializeState);
            if (pathWriteAutosave == null || !Files.exists(pathWriteAutosave)) {
               LOG.error("[DercoVisuals] dercovisuals.cfg was NOT written (check permissions): {}", this.getAutosavePath());
               return;
            }

            LOG.info("[DercoVisuals] Verified on disk: {} ({} bytes)", pathWriteAutosave, Files.size(pathWriteAutosave));
            if (pathWriteToDisk != null) {
               LOG.debug("[DercoVisuals] Profile copy: {}", pathWriteToDisk);
            }

            this.logSaveSummary(strSerializeState);
         } catch (Exception e) {
            LOG.warn("[DercoVisuals] Failed to save config", e);
         }
      }
   }

   private void logLoadSummary(String str, String str2) {
      try {
         JSONObject jSONObject = new JSONObject(str2);
         LOG.info(
            "[DercoVisuals] Loaded {} ({} bytes, {} modules, {} hud)",
            str,
            str2.length(),
            jSONObject.optJSONObject("modules") != null ? jSONObject.getJSONObject("modules").length() : 0,
            jSONObject.optJSONObject("hud") != null ? jSONObject.getJSONObject("hud").length() : 0
         );
      } catch (Exception e) {
         LOG.info("[DercoVisuals] Loaded {} ({} bytes)", str, str2 != null ? str2.length() : 0);
      }
   }

   private void logSaveSummary(String str) {
      try {
         JSONObject jSONObject = new JSONObject(str);
         int length = jSONObject.optJSONObject("modules") != null ? jSONObject.getJSONObject("modules").length() : 0;
         int i = 0;
         if (jSONObject.has("modules")) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("modules");
            Iterator it = jSONObject2.keySet().iterator();

            while (it.hasNext()) {
               if (jSONObject2.getJSONObject((String)it.next()).optBoolean("enabled", false)) {
                  i++;
               }
            }
         }

         LOG.info(
            "[DercoVisuals] Saved {} ({} bytes, {} modules, {} enabled, {} hud) -> {}",
            "dercovisuals.cfg",
            str.length(),
            length,
            i,
            jSONObject.optJSONObject("hud") != null ? jSONObject.getJSONObject("hud").length() : 0,
            this.getAutosavePath()
         );
      } catch (Exception e) {
         LOG.info("[DercoVisuals] Saved {} -> {}", "dercovisuals.cfg", this.getAutosavePath());
      }
   }

   public Set<String> profileNames() {
      return Collections.unmodifiableSet(this.profiles.keySet());
   }

   public String activeProfile() {
      return this.activeProfile;
   }

   public void createProfile(String str, Runnable runnable, Consumer<String> consumer) throws JSONException {
      String strSanitizeName = sanitizeName(str);
      if (strSanitizeName.isEmpty()) {
         if (consumer != null) {
            consumer.accept("Имя профиля пустое");
         }
      } else if (this.profiles.containsKey(strSanitizeName)) {
         if (consumer != null) {
            consumer.accept("Профиль '" + strSanitizeName + "' уже существует");
         }
      } else {
         String strSerializeState = this.serializeState();
         this.profiles.put(strSanitizeName, strSerializeState);

         try {
            this.writeToDisk(strSanitizeName, strSerializeState);
            if (runnable != null) {
               runnable.run();
            }
         } catch (IOException e) {
            LOG.warn("[DercoVisuals] Cannot create profile {}", strSanitizeName, e);
            if (consumer != null) {
               consumer.accept(e.getMessage());
            }
         }
      }
   }

   public void loadProfile(String str, Runnable runnable, Consumer<String> consumer) {
      if (!this.profiles.containsKey(str)) {
         if (consumer != null) {
            consumer.accept("Профиль '" + str + "' не найден");
         }
      } else {
         this.applying = true;

         try {
            this.applyProfile(str);
            this.activeProfile = str;
            this.applying = false;
            this.flushSaveNow("loadProfile-" + str);
            if (runnable != null) {
               runnable.run();
            }
         } catch (Throwable th) {
            this.applying = false;
            throw th;
         }
      }
   }

   public void deleteProfile(String str, Runnable runnable, Consumer<String> consumer) {
      if (!this.profiles.containsKey(str)) {
         if (consumer != null) {
            consumer.accept("Профиль '" + str + "' не найден");
         }
      } else {
         this.profiles.remove(str);
         this.deleteFromDisk(str);
         if (this.activeProfile.equals(str)) {
            this.activeProfile = this.profiles.isEmpty() ? "default" : this.profiles.keySet().iterator().next();
         }

         if (runnable != null) {
            runnable.run();
         }
      }
   }

   public void renameProfile(String str, String str2, Runnable runnable, Consumer<String> consumer) {
      String strSanitizeName = sanitizeName(str2);
      if (!this.profiles.containsKey(str)) {
         if (consumer != null) {
            consumer.accept("Профиль '" + str + "' не найден");
         }
      } else if (this.profiles.containsKey(strSanitizeName)) {
         if (consumer != null) {
            consumer.accept("Профиль '" + strSanitizeName + "' уже существует");
         }
      } else {
         String strRemove = this.profiles.remove(str);
         this.profiles.put(strSanitizeName, strRemove);
         this.deleteFromDisk(str);

         try {
            this.writeToDisk(strSanitizeName, strRemove);
            if (this.activeProfile.equals(str)) {
               this.activeProfile = strSanitizeName;
            }

            if (runnable != null) {
               runnable.run();
            }
         } catch (IOException e) {
            LOG.warn("[DercoVisuals] Cannot rename profile {} -> {}", str, strSanitizeName, e);
            if (consumer != null) {
               consumer.accept(e.getMessage());
            }
         }
      }
   }

   public String getProfileData(String str) {
      return this.profiles.getOrDefault(str, "{}");
   }

   private String serializeState() throws JSONException {
      JSONObject jSONObject = new JSONObject();
      jSONObject.put("version", 1);
      JSONObject jSONObject2 = new JSONObject();

      for (ClientModule clientModule : ModuleRegistry.all()) {
         JSONObject jSONObject3 = new JSONObject();
         jSONObject3.put("enabled", clientModule.isEnabled());
         jSONObject3.put("bindKey", clientModule.bindKey());
         jSONObject3.put("settings", this.serializeSettings(clientModule.settings()));
         jSONObject2.put(clientModule.name(), jSONObject3);
      }

      jSONObject.put("modules", jSONObject2);
      jSONObject.put("hud", this.serializeHud());
      jSONObject.put("markers", this.serializeMarkers());
      jSONObject.put("markerSettings", this.serializeMarkerSettings());
      return jSONObject.toString(2);
   }

   private JSONArray serializeMarkers() {
      JSONArray markers = new JSONArray();

      for (MapMarker marker : MarkerManager.a()) {
         if (!marker.j()) {
            JSONObject entry = new JSONObject();
            entry.put("name", marker.a());
            entry.put("x", marker.b());
            entry.put("y", marker.c());
            entry.put("z", marker.d());
            entry.put("colorRgb", marker.e().getRGB());
            entry.put("iconType", marker.f().name());
            markers.put(entry);
         }
      }

      return markers;
   }

   private JSONObject serializeMarkerSettings() {
      JSONObject settings = new JSONObject();
      settings.put("quickMarkerEnabled", MarkerOptions.a());
      settings.put("quickMarkerKey", MarkerOptions.b());
      settings.put("deathMarkerEnabled", ModuleRegistry.DEATH_MARKER.isEnabled());
      settings.put("autoMarkersEnabled", MarkerOptions.d());
      settings.put("mysteriousBeaconEnabled", MarkerOptions.e());
      return settings;
   }

   private JSONObject serializeSettings(List<Setting<?>> list) {
      JSONObject jSONObject = new JSONObject();

      for (Setting<?> setting : list) {
         if (!(setting instanceof SettingGroup)) {
            String strF = setting.f();

            try {
               if (setting instanceof BooleanSetting booleanSetting) {
                  JSONObject jSONObject2 = new JSONObject();
                  jSONObject2.put("type", "boolean");
                  jSONObject2.put("value", booleanSetting.get());
                  jSONObject.put(strF, jSONObject2);
               } else if (setting instanceof SliderSetting sliderSetting) {
                  JSONObject jSONObject3 = new JSONObject();
                  jSONObject3.put("type", "slider");
                  jSONObject3.put("value", sliderSetting.get());
                  jSONObject.put(strF, jSONObject3);
               } else if (setting instanceof ColorSetting colorSetting) {
                  JSONObject jSONObject4 = new JSONObject();
                  jSONObject4.put("type", "color");
                  jSONObject4.put("hue", colorSetting.c());
                  jSONObject4.put("sat", colorSetting.d());
                  jSONObject4.put("bri", colorSetting.e());
                  jSONObject.put(strF, jSONObject4);
               } else if (setting instanceof KeySetting keySetting) {
                  JSONObject jSONObject5 = new JSONObject();
                  jSONObject5.put("type", "key");
                  jSONObject5.put("value", keySetting.a());
                  jSONObject.put(strF, jSONObject5);
               } else if (!(setting instanceof ModeSetting modeSetting)) {
                  if (setting instanceof TokenSetting tokenSetting) {
                     JSONObject jSONObject7 = new JSONObject();
                     jSONObject7.put("type", "token");
                     jSONObject7.put("value", tokenSetting.a());
                     jSONObject.put(strF, jSONObject7);
                  } else if (setting instanceof ItemToggleSetting itemToggleSetting) {
                     JSONObject jSONObject8 = new JSONObject();
                     jSONObject8.put("type", "itemtoggle");
                     jSONObject8.put("enabled", itemToggleSetting.get());
                     Color colorD = itemToggleSetting.d();
                     jSONObject8.put("color", colorD != null ? colorD.getRGB() : -1);
                     jSONObject.put(strF, jSONObject8);
                  }
               } else {
                  JSONObject jSONObject6 = new JSONObject();
                  jSONObject6.put("type", "mode");
                  jSONObject6.put("value", modeSetting.k());
                  if (modeSetting.c()) {
                     JSONArray jSONArray = new JSONArray();
                     Iterator<Integer> it = modeSetting.e().iterator();

                     while (it.hasNext()) {
                        jSONArray.put(it.next());
                     }

                     jSONObject6.put("selected", jSONArray);
                  }

                  jSONObject.put(strF, jSONObject6);
               }
            } catch (Exception var10) {
            }
         }
      }

      return jSONObject;
   }

   private JSONObject serializeHud() throws JSONException {
      JSONObject jSONObject = new JSONObject();
      HudElementManager hudElementManagerA = HudElementManager.a();
      this.serializeHudElement(jSONObject, "potions", hudElementManagerA.i());
      this.serializeHudElement(jSONObject, "hotkeys", hudElementManagerA.j());
      this.serializeHudElement(jSONObject, "cooldowns", hudElementManagerA.k());
      this.serializeHudElement(jSONObject, "target", hudElementManagerA.l());
      this.serializeHudElement(jSONObject, "saturation", hudElementManagerA.getSaturationHud());
      this.serializeHudElement(jSONObject, "inventory", hudElementManagerA.getInventoryHud());
      this.serializeHudElement(jSONObject, "scoreboard", hudElementManagerA.getScoreboardHud());
      this.serializeHudElement(jSONObject, "bossbar", hudElementManagerA.getBossbarHud());
      this.serializeHudElement(jSONObject, "totems", hudElementManagerA.getTotemsHud());
      return jSONObject;
   }

   private void serializeHudElement(JSONObject jSONObject, String str, HudElement hudElement) throws JSONException {
      if (hudElement != null) {
         JSONObject jSONObject2 = new JSONObject();
         jSONObject2.put("x", hudElement.t().a());
         jSONObject2.put("y", hudElement.t().b());
         jSONObject2.put("scale", hudElement.g());
         jSONObject.put(str, jSONObject2);
      }
   }

   private void applyProfile(String str) {
      this.applyJson(this.profiles.get(str));
   }

   private void applyJson(String str) {
      if (str != null && !str.isEmpty()) {
         try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("modules")) {
               this.deserializeModules(jSONObject.getJSONObject("modules"));
            }

            if (jSONObject.has("hud")) {
               this.deserializeHud(jSONObject.getJSONObject("hud"));
            }

            if (jSONObject.has("markers")) {
               this.deserializeMarkers(jSONObject.getJSONArray("markers"));
            }

            if (jSONObject.has("markerSettings")) {
               this.deserializeMarkerSettings(jSONObject.getJSONObject("markerSettings"));
            }
         } catch (JSONException e) {
            LOG.warn("[DercoVisuals] Failed to parse config JSON", e);
         }
      }
   }

   private void deserializeMarkers(JSONArray markers) {
      MarkerManager.removeLocal();
      NotificationStore.clear();

      for (int i = 0; i < markers.length(); i++) {
         JSONObject entry = markers.optJSONObject(i);
         if (entry != null) {
            try {
               MapMarker.Icon icon = MapMarker.Icon.valueOf(entry.optString("iconType", "HOME"));
               MarkerManager.a(
                  new MapMarker(
                     entry.optString("name", "Метка"),
                     entry.optInt("x", 0),
                     entry.optInt("y", 64),
                     entry.optInt("z", 0),
                     new Color(entry.optInt("colorRgb", Color.WHITE.getRGB()), true),
                     icon
                  )
               );
            } catch (IllegalArgumentException ex) {
               LOG.debug("[DercoVisuals] Skipping marker with an invalid icon at index {}", i);
            }
         }
      }

      NotificationStore.syncWithMarkers();
   }

   private void deserializeMarkerSettings(JSONObject settings) {
      MarkerOptions.a(settings.optBoolean("quickMarkerEnabled", true));
      MarkerOptions.a(settings.optInt("quickMarkerKey", 0));
      boolean deathMarkerEnabled = settings.optBoolean("deathMarkerEnabled", ModuleRegistry.DEATH_MARKER.isEnabled());
      MarkerOptions.b(deathMarkerEnabled);
      ModuleRegistry.DEATH_MARKER.setEnabledStateQuiet(deathMarkerEnabled);
      MarkerOptions.c(settings.optBoolean("autoMarkersEnabled", true));
      MarkerOptions.d(settings.optBoolean("mysteriousBeaconEnabled", false));
   }

   private void deserializeModules(JSONObject jSONObject) {
      for (ClientModule clientModule : ModuleRegistry.all()) {
         if (jSONObject.has(clientModule.name())) {
            try {
               JSONObject jSONObject2 = jSONObject.getJSONObject(clientModule.name());
               if (jSONObject2.has("enabled")) {
                  clientModule.setEnabledStateQuiet(jSONObject2.getBoolean("enabled"));
               }

               if (jSONObject2.has("bindKey")) {
                  clientModule.setBindKeyQuiet(jSONObject2.getInt("bindKey"));
               }

               if (jSONObject2.has("settings")) {
                  this.deserializeSettings(clientModule.settings(), jSONObject2.getJSONObject("settings"));
               }
            } catch (Exception var5) {
            }
         }
      }
   }

   private void deserializeSettings(List<Setting<?>> list, JSONObject jSONObject) {
      for (Setting<?> setting : list) {
         if (!(setting instanceof SettingGroup)) {
            String name = setting.f();
            if (jSONObject.has(name)) {
               try {
                  JSONObject entry = jSONObject.getJSONObject(name);
                  if (setting instanceof BooleanSetting) {
                     ((BooleanSetting)setting).set(entry.getBoolean("value"));
                  } else if (setting instanceof SliderSetting) {
                     ((SliderSetting)setting).set((float)entry.getDouble("value"));
                  } else if (setting instanceof ColorSetting) {
                     ((ColorSetting)setting).setHsb((float)entry.getDouble("hue"), (float)entry.getDouble("sat"), (float)entry.getDouble("bri"));
                  } else if (setting instanceof KeySetting) {
                     ((KeySetting)setting).setKey(entry.getInt("value"));
                  } else if (!(setting instanceof ModeSetting modeSetting)) {
                     if (setting instanceof TokenSetting) {
                        ((TokenSetting)setting).set(entry.getString("value"));
                     } else if (setting instanceof ItemToggleSetting itemToggleSetting) {
                        if (entry.has("enabled")) {
                           itemToggleSetting.set(entry.getBoolean("enabled"));
                        }

                        if (entry.has("color")) {
                           itemToggleSetting.setColor(new Color(entry.getInt("color"), true));
                        }
                     }
                  } else {
                     modeSetting.select(entry.getInt("value"));
                     if (modeSetting.c() && entry.has("selected")) {
                        JSONArray arr = entry.getJSONArray("selected");
                        HashSet<Integer> set = new HashSet<>();

                        for (int i = 0; i < arr.length(); i++) {
                           set.add(arr.getInt(i));
                        }

                        modeSetting.setSelectedIndices(set);
                     }
                  }
               } catch (Exception e) {
                  LOG.debug("[DercoVisuals] Failed to apply setting '{}'", name, e);
               }
            }
         }
      }
   }

   private void deserializeHud(JSONObject jSONObject) {
      HudElementManager hudElementManagerA = HudElementManager.a();
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      float fGetFramebufferWidth = MinecraftClientVarGetInstance.getWindow().getScaledWidth();
      float fGetFramebufferHeight = MinecraftClientVarGetInstance.getWindow().getScaledHeight();
      this.applyHudElement(jSONObject, "potions", hudElementManagerA.i(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "hotkeys", hudElementManagerA.j(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "cooldowns", hudElementManagerA.k(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "target", hudElementManagerA.l(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "saturation", hudElementManagerA.getSaturationHud(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "inventory", hudElementManagerA.getInventoryHud(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "scoreboard", hudElementManagerA.getScoreboardHud(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "bossbar", hudElementManagerA.getBossbarHud(), fGetFramebufferWidth, fGetFramebufferHeight);
      this.applyHudElement(jSONObject, "totems", hudElementManagerA.getTotemsHud(), fGetFramebufferWidth, fGetFramebufferHeight);
      hudElementManagerA.d();
   }

   private void applyHudElement(JSONObject jSONObject, String str, HudElement hudElement, float f, float f2) {
      if (hudElement != null && jSONObject.has(str)) {
         try {
            JSONObject jSONObject2 = jSONObject.getJSONObject(str);
            float f3 = (float)jSONObject2.getDouble("x");
            float f4 = (float)jSONObject2.getDouble("y");
            if (f3 == 0.0F && f4 == 0.0F) {
               hudElement.b(f, f2);
               return;
            }

            hudElement.t().a(f3);
            hudElement.t().b(f4);
            hudElement.b();
            if (f > 1.0F && f2 > 1.0F) {
               hudElement.a(hudElement.t().a(hudElement.n(), f));
               hudElement.b(hudElement.t().b(hudElement.o(), f2));
               hudElement.b(f, f2);
            }
         } catch (Exception var9) {
         }
      }
   }

   private Path getConfigDir() {
      String str = System.getenv("APPDATA");
      if (str != null && !str.isBlank()) {
         return Path.of(str, "DercoVisuals", "config");
      }

      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      return MinecraftClientVarGetInstance != null
         ? MinecraftClientVarGetInstance.runDirectory.toPath().resolve("config").resolve("dercovisuals")
         : Path.of(System.getProperty("user.home"), "DercoVisuals", "config");
   }

   private Path getLegacyConfigDir() {
      MinecraftClient client = MinecraftClient.getInstance();
      return client == null ? null : client.runDirectory.toPath().resolve("config").resolve("pulse");
   }

   private Path getLegacyAppDataConfigDir() {
      String appData = System.getenv("APPDATA");
      if (appData != null && !appData.isBlank()) {
         return Path.of(appData, "Pulse", "config");
      }
      return Path.of(System.getProperty("user.home"), "Pulse", "config");
   }

   private void migrateLegacyConfigIfNeeded() {
      Path configDir = this.getConfigDir();
      Path[] legacyDirs = new Path[] { this.getLegacyAppDataConfigDir(), this.getLegacyConfigDir() };

      for (Path legacyDir : legacyDirs) {
         if (legacyDir == null || !Files.isDirectory(legacyDir) || legacyDir.equals(configDir)) {
            continue;
         }
         try {
            Files.createDirectories(configDir);
            Path oldAutosave = legacyDir.resolve("pulse.cfg");
            Path newAutosave = configDir.resolve("dercovisuals.cfg");
            if (Files.exists(oldAutosave) && !Files.exists(newAutosave)) {
               Files.copy(oldAutosave, newAutosave, StandardCopyOption.COPY_ATTRIBUTES);
               LOG.info("[DercoVisuals] Migrated autosave {} -> {}", oldAutosave, newAutosave);
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(legacyDir, "*.json")) {
               for (Path oldProfile : stream) {
                  Path newProfile = configDir.resolve(oldProfile.getFileName());
                  if (!Files.exists(newProfile)) {
                     Files.copy(oldProfile, newProfile, StandardCopyOption.COPY_ATTRIBUTES);
                     LOG.info("[DercoVisuals] Migrated profile {}", oldProfile.getFileName());
                  }
               }
            }
         } catch (IOException e) {
            LOG.warn("[DercoVisuals] Legacy config migration from {} failed", legacyDir, e);
         }
      }
   }

   private Path getAutosavePath() {
      return this.getConfigDir().resolve("dercovisuals.cfg");
   }

   private String readAutosave() {
      try {
         Path autosavePath = this.getAutosavePath();
         return Files.exists(autosavePath) ? Files.readString(autosavePath, StandardCharsets.UTF_8) : null;
      } catch (IOException e) {
         LOG.warn("[DercoVisuals] Cannot read {}", this.getAutosavePath(), e);
         return null;
      }
   }

   private Path writeAutosave(String str) throws IOException {
      Path configDir = this.getConfigDir();
      Files.createDirectories(configDir);
      Path pathResolve = configDir.resolve("dercovisuals.cfg");
      writeUtf8File(pathResolve, str);
      return pathResolve;
   }

   private static void writeUtf8File(Path path, String str) throws IOException {
      ByteBuffer byteBufferEncode = StandardCharsets.UTF_8
         .newEncoder()
         .onMalformedInput(CodingErrorAction.REPLACE)
         .onUnmappableCharacter(CodingErrorAction.REPLACE)
         .encode(CharBuffer.wrap(str));
      byte[] bArr = new byte[byteBufferEncode.remaining()];
      byteBufferEncode.get(bArr);
      Files.write(path, bArr, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
   }

   private void loadAllFromDisk() {
      try {
         Path configDir = this.getConfigDir();
         Files.createDirectories(configDir);
         if (!Files.exists(configDir)) {
            return;
         }

         DirectoryStream<Path> directoryStreamNewDirectoryStream = Files.newDirectoryStream(configDir, "*.json");

         for (Path path : directoryStreamNewDirectoryStream) {
            String string = path.getFileName().toString();
            String strSubstring = string.substring(0, string.length() - ".json".length());

            try {
               this.profiles.put(strSubstring, Files.readString(path, StandardCharsets.UTF_8));
            } catch (IOException e) {
               LOG.warn("[DercoVisuals] Cannot read profile {}", path, e);
            }
         }

         if (directoryStreamNewDirectoryStream != null) {
            directoryStreamNewDirectoryStream.close();
         }
      } catch (IOException e2) {
         LOG.warn("[DercoVisuals] Cannot list config dir {}", this.getConfigDir(), e2);
      }

      if (!this.profiles.containsKey("default")) {
         this.profiles.put("default", "{}");
      }
   }

   private Path writeToDisk(String str, String str2) throws IOException {
      Path configDir = this.getConfigDir();
      Files.createDirectories(configDir);
      Path pathResolve = configDir.resolve(sanitizeName(str) + ".json");
      writeUtf8File(pathResolve, str2);
      return pathResolve;
   }

   private void deleteFromDisk(String str) {
      try {
         Files.deleteIfExists(this.getConfigDir().resolve(sanitizeName(str) + ".json"));
      } catch (IOException e) {
         LOG.warn("[DercoVisuals] Cannot delete profile {}", str, e);
      }
   }

   private static String sanitizeName(String str) {
      return str == null ? "" : str.trim().replaceAll("[/\\\\:*?\"<>| ]", "_");
   }

   public List<ConfigEntry> toConfigEntries() {
      ArrayList arrayList = new ArrayList();

      for (Entry<String, String> entry : this.profiles.entrySet()) {
         arrayList.add(new ConfigEntry(entry.getKey(), entry.getValue()));
      }

      return arrayList;
   }
}
