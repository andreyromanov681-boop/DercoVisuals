// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals;

import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ThreadFactory;
import lombok.Generated;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.enizer6569.dercovisuals.auth.UserInfo;
import dev.enizer6569.dercovisuals.config.LocalConfigManager;
import dev.enizer6569.dercovisuals.discord.FolderOpener;
import dev.enizer6569.dercovisuals.discord.DercoVisualsDiscordRPC;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiKeyBinding;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.hud.core.HudServiceRegistry;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.player.DercoVisualsPlayerTracker;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;
import dev.enizer6569.dercovisuals.render.shader.ShaderLibrary;

public class DercoVisuals implements Runnable, ThreadFactory, ModInitializer {
   public static final String CLIENT_VERSION = "2.0.1";
   private String backendUrl;
   private Renderer2D render;
   private Screen clickGui;
   private static final DercoVisuals instance = new DercoVisuals();
   private static UserInfo userInfo = null;
   private static String cachedBackendUrl = null;
   private static final String[] BACKEND_URLS = new String[]{
      "https://backend4.pulsevisuals.pro",
      "https://backend.pulsevisuals.pro",
      "https://backend1.pulsevisuals.pro",
      "https://backend2.pulsevisuals.pro",
      "https://backend3.pulsevisuals.pro"
   };
   private static final String[] API_URLS = new String[]{"https://api.pulsevisuals.pro/api", "https://euapi.pulsevisuals.pro/api"};
   private static final String[] MAIN_URLS = new String[]{"https://pulsevisuals.pro", "https://eu.pulsevisuals.pro"};
   public static final String CLIENT_NAME = "dercovisuals";
   private static final Logger LOGGER = LogManager.getLogger("dercovisuals");
   private static int currentBackendIndex = 0;
   private static boolean updateScreenShown = false;
   private String pendingTokenToSave = null;
   private boolean init = false;

   public void onInitialize() {
      FolderOpener.ensureExists();
      DercoVisualsPlayerTracker.get().init();
      ClickGuiKeyBinding.register();
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(ClientPlayNetworkHandlerVar, MinecraftClientVar) -> {
         LOGGER.info("[DercoVisuals] Disconnect — saving config");
         LocalConfigManager.get().flushSaveNow("disconnect");
      });
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)MinecraftClientVar2 -> {
         LOGGER.info("[DercoVisuals] Client stop — saving config");
         LocalConfigManager.get().flushSaveNow("client-stop");
      });
      LOGGER.info("DercoVisuals initialized without native protection");
      DercoVisualsDiscordRPC.getInstance().init();
   }

   @Override
   public void run() {
   }

   @Override
   public Thread newThread(Runnable runnable) {
      return null;
   }

   private static void unlockCryptoPolicy() {
   }

   public void init() {
      IconTextureRegistry.load();
      ShaderLibrary.loadDefaultShaders();
      this.render = new Renderer2DImpl();
      HudServiceRegistry.a();
      ModuleRegistry.init();
      LOGGER.info("[DercoVisuals] Loading config (dir: {})", LocalConfigManager.get().configDirectory());

      try {
         LocalConfigManager.get().init();
         LOGGER.info("[DercoVisuals] Config load finished");
      } catch (Exception e) {
         LOGGER.warn("[DercoVisuals] Config load failed", e);
      }

      this.clickGui = new DercoVisualsClickGuiScreen();
      this.init = true;
      LOGGER.info("DercoVisuals Java GUI initialized");

      try {
         LOGGER.info("[DercoVisuals Test] Scanning RenderSystem methods:");

         for (Method m : RenderSystem.class.getMethods()) {
            if (m.getName().toLowerCase().contains("projection")) {
               LOGGER.info("[DercoVisuals Test] Projection method: " + m.getName() + " params: " + Arrays.toString(m.getParameterTypes()));
            }
         }
      } catch (Throwable t) {
         LOGGER.error("[DercoVisuals Test] Error scanning RenderSystem", t);
      }
   }

   private String loadTokenFromAnySource() {
      return "";
   }

   private String loadTokenFromJar() {
      return "";
   }

   public static void showMessage(String str) {
   }

   public static void showAntivirusWarning() {
   }

   public static void showSystemMessage() {
   }

   public static void openYouTube() {
   }

   public static void checkAndShowUpdate(Screen ScreenVar) {
   }

   public static String getMainUrl() {
      for (String str : MAIN_URLS) {
         if (isUrlAccessible(str)) {
            return str;
         }
      }

      return MAIN_URLS[0];
   }

   public static String getBackendUrl() {
      return cachedBackendUrl != null ? cachedBackendUrl : BACKEND_URLS[currentBackendIndex];
   }

   public static String getDirectApiUrl() {
      for (String str : API_URLS) {
         if (isUrlAccessible(str + "/auth/device/poll?state=test")) {
            return str;
         }
      }

      return API_URLS[0];
   }

   private static boolean isUrlAccessible(String str) {
      return false;
   }

   public Renderer2D getRender() {
      return this.render;
   }

   public Screen getClickGui() {
      return this.clickGui;
   }

   @Generated
   public static DercoVisuals getInstance() {
      return instance;
   }

   @Generated
   public static Logger getLOGGER() {
      return LOGGER;
   }

   @Generated
   public static UserInfo getUserInfo() {
      return userInfo;
   }

   @Generated
   public static void setUserInfo(UserInfo userInfo2) {
      userInfo = userInfo2;
   }

   public static String decrypt(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
