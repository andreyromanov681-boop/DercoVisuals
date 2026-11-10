package pulse.config;

import java.awt.Color;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import pulse.auth.AccountServiceClient;
import pulse.gui.core.DockPanelController;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.gui.friends.FriendsPanel;
import pulse.gui.friends.FriendsTab;
import pulse.gui.friends.HistoryEntry;
import pulse.hud.core.HudElement;
import pulse.hud.core.HudElementManager;
import pulse.hud.notifications.HudNotificationCenter;
import pulse.hud.notifications.HudNotificationSettingsPanel;
import pulse.hud.notifications.Notification;
import pulse.hud.notifications.NotificationStore;
import pulse.markers.MarkerOptions;
import pulse.module.ClientModule;
import pulse.module.ModuleRegistry;
import pulse.modules.hud.ClientColor;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ItemToggleSetting;
import pulse.settings.KeySetting;
import pulse.settings.ModeSetting;
import pulse.settings.Setting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;
import pulse.settings.TokenSetting;
import ru.pulse.Pulse;

public class ConfigManager {
   private static final int b = 30;
   private CloudConfigRepository d;
   private ScheduledExecutorService e;
   private volatile boolean f = false;
   private static final ConfigManager a = new ConfigManager();
   private static final DateTimeFormatter c = DateTimeFormatter.ISO_LOCAL_DATE;

   private ConfigManager() {
   }

   public static ConfigManager a() {
      return a;
   }

   public void b() {
   }

   public void c() {
   }

   public void d() {
   }

   private void m() {
      if (this.e != null) {
         this.e.shutdown();
      }

      this.e = Executors.newSingleThreadScheduledExecutor(runnable -> {
         Thread thread = new Thread(runnable, "crypt");
         thread.setDaemon(true);
         return thread;
      });
      this.e.scheduleAtFixedRate(() -> {
         if (this.d != null && this.d.i()) {
            try {
               this.d.a(this.l(), (Runnable)null, (Consumer<String>)null);
            } catch (Exception var2) {
            }
         }
      }, 30L, 30L, TimeUnit.SECONDS);
   }

   public void e() {
   }

   public void f() {
   }

   public void a(String str, Runnable runnable, Consumer<String> consumer) {
      if (runnable != null) {
         runnable.run();
      }
   }

   public void b(String str, Runnable runnable, Consumer<String> consumer) {
      if (str != null) {
         CloudConfigRepository cloudConfigRepositoryJ = this.j();
         if (cloudConfigRepositoryJ != null) {
            try {
               Field declaredField = CloudConfigRepository.class.getDeclaredField("i");
               declaredField.setAccessible(true);
               ((Map)declaredField.get(cloudConfigRepositoryJ)).remove(str);
            } catch (Throwable var6) {
            }
         }

         if (runnable != null) {
            runnable.run();
         }
      }
   }

   public void a(String str, String str2, Runnable runnable, Consumer<String> consumer) {
   }

   public boolean a(String str) {
      return false;
   }

   public String g() {
      return "default";
   }

   public void h() {
      try {
         LocalConfigManager.get().requestSave("state-changed");
      } catch (Exception e) {
         LogManager.getLogger("pulse/config").warn("[Pulse] Autosave request failed", e);
      }
   }

   public boolean i() {
      return true;
   }

   public CloudConfigRepository j() {
      if (this.d == null) {
         this.d = new CloudConfigRepository(AccountServiceClient.A());

         try {
            CloudConfigRepository.RemoteConfigRecord remoteConfigRecord = new CloudConfigRepository.RemoteConfigRecord();
            remoteConfigRecord.b = "default";
            remoteConfigRecord.c = "";
            remoteConfigRecord.g = System.currentTimeMillis();
            Field declaredField = CloudConfigRepository.class.getDeclaredField("i");
            declaredField.setAccessible(true);
            ((Map)declaredField.get(this.d)).put("default", remoteConfigRecord);
         } catch (Throwable var3) {
         }
      }

      return this.d;
   }

   public void a(String str, int i, Integer num, Integer num2, Consumer<String> consumer, Consumer<String> consumer2) {
   }

   public void c(String str, Runnable runnable, Consumer<String> consumer) {
      CloudConfigRepository cloudConfigRepositoryJ;
      if (str != null && !str.trim().isEmpty() && (cloudConfigRepositoryJ = this.j()) != null) {
         String strTrim = str.trim();

         try {
            CloudConfigRepository.RemoteConfigRecord remoteConfigRecord = new CloudConfigRepository.RemoteConfigRecord();
            remoteConfigRecord.b = strTrim;
            remoteConfigRecord.c = "";
            remoteConfigRecord.g = System.currentTimeMillis();
            Field declaredField = CloudConfigRepository.class.getDeclaredField("i");
            declaredField.setAccessible(true);
            ((Map)declaredField.get(cloudConfigRepositoryJ)).put(strTrim, remoteConfigRecord);
         } catch (Throwable var8) {
         }

         if (runnable != null) {
            runnable.run();
         }
      }
   }

   public List<CloudConfigRepository.SharedConfigRecord> k() {
      return Collections.emptyList();
   }

   public void a(int i, Consumer<ConfigState> consumer, Consumer<String> consumer2) {
   }

   public ConfigState l() {
      return null;
   }

   public void a(ConfigState configState) {
   }

   private Map<String, ConfigState.ModuleState> n() {
      HashMap map = new HashMap();

      for (ClientModule clientModule : ModuleRegistry.all()) {
         ConfigState.ModuleState moduleState = new ConfigState.ModuleState();
         moduleState.a(clientModule.k());
         moduleState.a(clientModule.j());
         moduleState.a(this.a(clientModule.m()));
         map.put(clientModule.g(), moduleState);
      }

      return map;
   }

   private Map<String, ConfigState.SettingState> a(List<Setting<?>> list) {
      HashMap map = new HashMap();

      for (Setting<?> setting : list) {
         if (!(setting instanceof SettingGroup)) {
            ConfigState.SettingState settingState = new ConfigState.SettingState();
            if (setting instanceof BooleanSetting) {
               settingState.a("crypt");
               settingState.a(((BooleanSetting)setting).a());
            } else if (setting instanceof SliderSetting) {
               settingState.a("crypt");
               settingState.a(((SliderSetting)setting).a());
            } else if (setting instanceof ColorSetting colorSetting) {
               settingState.a("crypt");
               settingState.a(colorSetting.c());
               settingState.b(colorSetting.d());
               settingState.c(colorSetting.e());
            } else if (setting instanceof KeySetting) {
               settingState.a("crypt");
               settingState.a(((KeySetting)setting).a());
            } else if (setting instanceof ModeSetting modeSetting) {
               settingState.a("crypt");
               settingState.a(modeSetting.k());
               if (modeSetting.c()) {
                  settingState.a(modeSetting.e().stream().mapToInt(v0 -> v0).toArray());
               }
            } else if (setting instanceof TokenSetting) {
               settingState.a("crypt");
               settingState.a((Object)((TokenSetting)setting).a());
            } else if (setting instanceof ItemToggleSetting itemToggleSetting) {
               settingState.a("crypt");
               settingState.a(itemToggleSetting.a());
               settingState.a(itemToggleSetting.e());
            } else {
               Pulse.getLOGGER().warn("crypt", setting.getClass().getSimpleName(), setting.f());
            }

            map.put(setting.f(), settingState);
         }
      }

      return map;
   }

   private List<ConfigState.FriendHistoryState> o() {
      ArrayList arrayList = new ArrayList();
      FriendsPanel friendsPanelV = this.v();
      if (friendsPanelV != null) {
         for (HistoryEntry historyEntry : friendsPanelV.a()) {
            ConfigState.FriendHistoryState friendHistoryState = new ConfigState.FriendHistoryState();
            friendHistoryState.a(historyEntry.a());
            friendHistoryState.b(historyEntry.b().format(c));
            arrayList.add(friendHistoryState);
         }
      }

      return arrayList;
   }

   private List<ConfigState.NotificationState> p() {
      ArrayList arrayList = new ArrayList();

      for (Notification notification : NotificationStore.a()) {
         if (!notification.j()) {
            ConfigState.NotificationState notificationState = new ConfigState.NotificationState();
            notificationState.a(notification.a());
            notificationState.a(notification.b());
            notificationState.b(notification.c());
            notificationState.c(notification.d());
            notificationState.d(notification.e().getRGB());
            notificationState.b(notification.f().name());
            notificationState.a(notification.j());
            notificationState.a(notification.k());
            notificationState.e(notification.l());
            notificationState.b(notification.n());
            arrayList.add(notificationState);
         }
      }

      return arrayList;
   }

   private ConfigState.MarkerOptionsState q() {
      ConfigState.MarkerOptionsState markerOptionsState = new ConfigState.MarkerOptionsState();
      markerOptionsState.a(MarkerOptions.a());
      markerOptionsState.a(MarkerOptions.b());
      markerOptionsState.b(MarkerOptions.c());
      markerOptionsState.c(MarkerOptions.d());
      markerOptionsState.d(MarkerOptions.e());
      return markerOptionsState;
   }

   private Map<String, ConfigState.HudElementState> r() {
      HashMap map = new HashMap();
      HudElementManager hudElementManagerA = HudElementManager.a();
      if (hudElementManagerA.i() != null) {
         map.put("potions", this.a(hudElementManagerA.i()));
      }

      if (hudElementManagerA.j() != null) {
         map.put("hotkeys", this.a(hudElementManagerA.j()));
      }

      if (hudElementManagerA.k() != null) {
         map.put("cooldowns", this.a(hudElementManagerA.k()));
      }

      if (hudElementManagerA.l() != null) {
         map.put("target", this.a(hudElementManagerA.l()));
      }

      if (hudElementManagerA.getSaturationHud() != null) {
         map.put("saturation", this.a(hudElementManagerA.getSaturationHud()));
      }

      if (hudElementManagerA.getInventoryHud() != null) {
         map.put("inventory", this.a(hudElementManagerA.getInventoryHud()));
      }

      if (hudElementManagerA.getScoreboardHud() != null) {
         map.put("scoreboard", this.a(hudElementManagerA.getScoreboardHud()));
      }

      if (hudElementManagerA.getBossbarHud() != null) {
         map.put("bossbar", this.a(hudElementManagerA.getBossbarHud()));
      }

      if (hudElementManagerA.getTotemsHud() != null) {
         map.put("totems", this.a(hudElementManagerA.getTotemsHud()));
      }

      return map;
   }

   private ConfigState.HudElementState a(HudElement hudElement) {
      return new ConfigState.HudElementState(hudElement.t().a(), hudElement.t().b(), hudElement.g());
   }

   private String s() {
      try {
         DockPanelController dockPanelControllerJ;
         if (Pulse.getInstance().getClickGui() instanceof PulseClickGuiScreen
            && (dockPanelControllerJ = ((PulseClickGuiScreen)Pulse.getInstance().getClickGui()).j()) != null) {
            return dockPanelControllerJ.a().name();
         }
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt", e);
      }

      return "crypt";
   }

   private ConfigState.NotificationSettingsState t() {
      ConfigState.NotificationSettingsState notificationSettingsState = new ConfigState.NotificationSettingsState();
      HudNotificationCenter hudNotificationCenterA = HudNotificationCenter.a();
      if (hudNotificationCenterA != null) {
         HudNotificationSettingsPanel hudNotificationSettingsPanelI = hudNotificationCenterA.i();
         notificationSettingsState.a(hudNotificationSettingsPanelI.h());
         notificationSettingsState.b(hudNotificationSettingsPanelI.i());
         notificationSettingsState.c(hudNotificationSettingsPanelI.j());
         notificationSettingsState.d(hudNotificationSettingsPanelI.k());
         notificationSettingsState.e(hudNotificationSettingsPanelI.l());
      }

      return notificationSettingsState;
   }

   private void a(Map<String, ConfigState.ModuleState> map) {
      if (map != null) {
         for (ClientModule clientModule : ModuleRegistry.all()) {
            ConfigState.ModuleState moduleState = map.get(clientModule.g());
            if (moduleState != null) {
               clientModule.a(moduleState.b());
               clientModule.b(moduleState.a());
               this.a(clientModule.m(), moduleState.c());
            }
         }
      }
   }

   private void a(List<Setting<?>> list, Map<String, ConfigState.SettingState> map) {
      if (map != null) {
         for (Setting<?> setting : list) {
            ConfigState.SettingState settingState = map.get(setting.f());
            if (settingState != null) {
               try {
                  if (setting instanceof BooleanSetting && "crypt".equals(settingState.a())) {
                     ((BooleanSetting)setting).a((Boolean)settingState.b());
                  }

                  if (setting instanceof SliderSetting && "crypt".equals(settingState.a())) {
                     Object objB = settingState.b();
                     if (objB instanceof Number) {
                        ((SliderSetting)setting).a(((Number)objB).floatValue());
                     }
                  }

                  if (setting instanceof ColorSetting && "crypt".equals(settingState.a())) {
                     ((ColorSetting)setting).a(settingState.c(), settingState.d(), settingState.e());
                  }

                  if (setting instanceof KeySetting && "crypt".equals(settingState.a())) {
                     Object objB2 = settingState.b();
                     if (objB2 instanceof Number) {
                        ((KeySetting)setting).a(((Number)objB2).intValue());
                     }
                  }

                  if (setting instanceof ModeSetting && "crypt".equals(settingState.a())) {
                     ModeSetting modeSetting = (ModeSetting)setting;
                     Object objB3 = settingState.b();
                     if (objB3 instanceof Number) {
                        modeSetting.a(Integer.valueOf(((Number)objB3).intValue()));
                     }

                     if (modeSetting.c() && settingState.f() != null) {
                        HashSet hashSet = new HashSet();

                        for (int i6 : settingState.f()) {
                           hashSet.add(i6);
                        }

                        modeSetting.a(hashSet);
                     }
                  }

                  if (setting instanceof TokenSetting && "crypt".equals(settingState.a())) {
                     ((TokenSetting)setting).a((String)settingState.b());
                  }

                  if (setting instanceof ItemToggleSetting && "crypt".equals(settingState.a())) {
                     ItemToggleSetting itemToggleSetting = (ItemToggleSetting)setting;
                     if (settingState.g() != null) {
                        itemToggleSetting.a(settingState.g());
                     }

                     if (settingState.h() != null) {
                        itemToggleSetting.a(new Color(settingState.h()));
                     }
                  }
               } catch (Exception e) {
                  Pulse.getLOGGER().error("crypt" + setting.f(), e);
               }
            }
         }
      }
   }

   private void b(List<ConfigState.FriendHistoryState> list) {
      FriendsPanel friendsPanelV;
      if (list != null && (friendsPanelV = this.v()) != null) {
         friendsPanelV.b();

         for (ConfigState.FriendHistoryState friendHistoryState : list) {
            try {
               friendsPanelV.a(new HistoryEntry(friendHistoryState.a(), LocalDate.parse(friendHistoryState.b(), c)));
            } catch (Exception e) {
               friendsPanelV.a(new HistoryEntry(friendHistoryState.a()));
            }
         }
      }
   }

   private void c(List<ConfigState.NotificationState> list) {
      if (list != null) {
         NotificationStore.g().forEach(NotificationStore::b);

         for (ConfigState.NotificationState notificationState : list) {
            try {
               NotificationStore.a(
                  new Notification(
                     notificationState.a(),
                     notificationState.b(),
                     notificationState.c(),
                     notificationState.d(),
                     new Color(notificationState.e()),
                     Notification.Icon.valueOf(notificationState.f())
                  )
               );
            } catch (Exception e) {
               Pulse.getLOGGER().error("crypt" + notificationState.a(), e);
            }
         }
      }
   }

   private void a(ConfigState.MarkerOptionsState markerOptionsState) {
      if (markerOptionsState != null) {
         MarkerOptions.a(markerOptionsState.a());
         MarkerOptions.a(markerOptionsState.b());
         MarkerOptions.b(markerOptionsState.c());
         MarkerOptions.c(markerOptionsState.d());
      }
   }

   private void b(Map<String, ConfigState.HudElementState> map) {
      if (map != null) {
         HudElementManager hudElementManagerA = HudElementManager.a();
         MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
         float fGetFramebufferWidth = MinecraftClientVarGetInstance.getWindow().getScaledWidth();
         float fGetFramebufferHeight = MinecraftClientVarGetInstance.getWindow().getScaledHeight();
         this.a(hudElementManagerA.i(), map.get("potions"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.j(), map.get("hotkeys"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.k(), map.get("cooldowns"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.l(), map.get("target"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.getSaturationHud(), map.get("saturation"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.getInventoryHud(), map.get("inventory"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.getScoreboardHud(), map.get("scoreboard"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.getBossbarHud(), map.get("bossbar"), fGetFramebufferWidth, fGetFramebufferHeight);
         this.a(hudElementManagerA.getTotemsHud(), map.get("totems"), fGetFramebufferWidth, fGetFramebufferHeight);
         hudElementManagerA.d();
      }
   }

   private void a(HudElement hudElement, ConfigState.HudElementState hudElementState, float f, float f2) {
      if (hudElement != null && hudElementState != null) {
         hudElement.t().a(hudElementState.a());
         hudElement.t().b(hudElementState.b());
         hudElement.b();
         float fA = hudElement.t().a(hudElement.n(), f);
         float fB = hudElement.t().b(hudElement.o(), f2);
         hudElement.a(fA);
         hudElement.b(fB);
      }
   }

   private void b(String str) {
      if (str != null) {
         try {
            DockPanelController.Dock dockValueOf = DockPanelController.Dock.valueOf(str);
            DockPanelController dockPanelControllerJ;
            if (Pulse.getInstance().getClickGui() instanceof PulseClickGuiScreen
               && (dockPanelControllerJ = ((PulseClickGuiScreen)Pulse.getInstance().getClickGui()).j()) != null) {
               dockPanelControllerJ.a(dockValueOf);
            }
         } catch (IllegalArgumentException e) {
            Pulse.getLOGGER().warn("crypt" + str);
         } catch (Exception e2) {
            Pulse.getLOGGER().error("crypt", e2);
         }
      }
   }

   private void a(ConfigState.NotificationSettingsState notificationSettingsState) {
      HudNotificationCenter hudNotificationCenterA;
      if (notificationSettingsState != null && (hudNotificationCenterA = HudNotificationCenter.a()) != null) {
         HudNotificationSettingsPanel hudNotificationSettingsPanelI = hudNotificationCenterA.i();
         hudNotificationSettingsPanelI.a(notificationSettingsState.a());
         hudNotificationSettingsPanelI.b(notificationSettingsState.b());
         hudNotificationSettingsPanelI.c(notificationSettingsState.c());
         hudNotificationSettingsPanelI.d(notificationSettingsState.d());
         hudNotificationSettingsPanelI.e(notificationSettingsState.e());
      }
   }

   private ConfigState.ClientColorState u() {
      ConfigState.ClientColorState clientColorState = new ConfigState.ClientColorState();
      ClientColor clientColor = ModuleRegistry.CLIENT_COLOR;
      clientColorState.a(clientColor.a.d());
      clientColorState.a(clientColor.b.c());
      clientColorState.b(clientColor.b.d());
      clientColorState.c(clientColor.b.e());
      clientColorState.d(clientColor.e.c());
      clientColorState.e(clientColor.e.d());
      clientColorState.f(clientColor.e.e());
      clientColorState.g(clientColor.f.c());
      clientColorState.h(clientColor.f.d());
      clientColorState.i(clientColor.f.e());
      return clientColorState;
   }

   private void a(ConfigState.ClientColorState clientColorState) {
      if (clientColorState != null) {
         ClientColor clientColor = ModuleRegistry.CLIENT_COLOR;
         String[] strArrA = clientColor.a.a();

         for (int i = 0; i < strArrA.length; i++) {
            if (strArrA[i].equals(clientColorState.a())) {
               clientColor.a.a(Integer.valueOf(i));
               break;
            }
         }

         clientColor.b.a(clientColorState.b(), clientColorState.c(), clientColorState.d());
         clientColor.e.a(clientColorState.e(), clientColorState.f(), clientColorState.g());
         clientColor.f.a(clientColorState.h(), clientColorState.i(), clientColorState.j());
      }
   }

   private FriendsPanel v() {
      try {
         FriendsTab friendsTabH;
         return Pulse.getInstance().getClickGui() instanceof PulseClickGuiScreen
               && (friendsTabH = ((PulseClickGuiScreen)Pulse.getInstance().getClickGui()).h()) != null
            ? friendsTabH.e()
            : null;
      } catch (Exception e) {
         Pulse.getLOGGER().error("crypt", e);
         return null;
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
