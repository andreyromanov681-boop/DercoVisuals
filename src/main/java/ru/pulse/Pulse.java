package ru.pulse;

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
import pulse.auth.UserInfo;
import pulse.config.LocalConfigManager;
import pulse.discord.FolderOpener;
import pulse.discord.PulseDiscordRPC;
import pulse.gui.core.ClickGuiKeyBinding;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.hud.core.HudServiceRegistry;
import pulse.module.ModuleRegistry;
import pulse.player.PulsePlayerTracker;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.icons.IconTextureRegistry;
import pulse.render.shader.ShaderLibrary;

public class Pulse implements Runnable, ThreadFactory, ModInitializer {
   public static final String CLIENT_VERSION = "2.0.1";
   private String backendUrl;
   private Renderer2D render;
   private Screen clickGui;
   private static final Pulse instance = new Pulse();
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
   public static final String CLIENT_NAME = "pulse";
   private static final Logger LOGGER = LogManager.getLogger("pulse");
   private static int currentBackendIndex = 0;
   private static boolean updateScreenShown = false;
   private String pendingTokenToSave = null;
   private boolean init = false;

   public void onInitialize() {
      FolderOpener.ensureExists();
      PulsePlayerTracker.get().init();
      ClickGuiKeyBinding.register();
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(ClientPlayNetworkHandlerVar, MinecraftClientVar) -> {
         LOGGER.info("[Pulse] Disconnect — saving config");
         LocalConfigManager.get().flushSaveNow("disconnect");
      });
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)MinecraftClientVar2 -> {
         LOGGER.info("[Pulse] Client stop — saving config");
         LocalConfigManager.get().flushSaveNow("client-stop");
      });
      LOGGER.info("Pulse initialized without native protection");
      PulseDiscordRPC.getInstance().init();
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
      LOGGER.info("[Pulse] Loading config (dir: {})", LocalConfigManager.get().configDirectory());

      try {
         LocalConfigManager.get().init();
         LOGGER.info("[Pulse] Config load finished");
      } catch (Exception e) {
         LOGGER.warn("[Pulse] Config load failed", e);
      }

      this.clickGui = new PulseClickGuiScreen();
      this.init = true;
      LOGGER.info("Pulse Java GUI initialized");

      try {
         LOGGER.info("[Pulse Test] Scanning RenderSystem methods:");

         for (Method m : RenderSystem.class.getMethods()) {
            if (m.getName().toLowerCase().contains("projection")) {
               LOGGER.info("[Pulse Test] Projection method: " + m.getName() + " params: " + Arrays.toString(m.getParameterTypes()));
            }
         }
      } catch (Throwable t) {
         LOGGER.error("[Pulse Test] Error scanning RenderSystem", t);
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
   public static Pulse getInstance() {
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
