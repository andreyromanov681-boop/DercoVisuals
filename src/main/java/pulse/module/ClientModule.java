package pulse.module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pulse.client.MinecraftContext;
import pulse.config.ConfigManager;
import pulse.config.LocalConfigManager;
import pulse.events.EventBusService;
import pulse.hud.core.HudServiceRegistry;
import pulse.hud.notifications.HudNotificationCenter;
import pulse.settings.Setting;

public abstract class ClientModule implements MinecraftContext {
   private static final Logger LOG = LogManager.getLogger("pulse/config");
   private final String name;
   private final String description;
   private final ModuleCategory category;
   private boolean subscribed;
   private final List<Setting<?>> settings = new ArrayList<>();
   private int bindKey = 0;
   private boolean enabled = false;

   public ClientModule() {
      ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
      if (moduleInfo == null) {
         throw new IllegalStateException("Missing @ModuleInfo on " + this.getClass().getName());
      }

      this.name = moduleInfo.a();
      this.description = moduleInfo.b();
      this.category = moduleInfo.c();
   }

   public String name() {
      return this.name;
   }

   public String description() {
      return this.description;
   }

   public ModuleCategory category() {
      return this.category;
   }

   public int bindKey() {
      return this.bindKey;
   }

   public void setBindKey(int i) {
      if (this.bindKey != i) {
         this.bindKey = i;
         if (LocalConfigManager.get().isApplying()) {
            return;
         }

         ConfigManager.a().h();
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean z) {
      this.setEnabledState(z);
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public boolean isSubscribed() {
      return this.subscribed;
   }

   public boolean isActive() {
      return this.subscribed ? HudServiceRegistry.MODULE_ACCESS.a(this.name) : false;
   }

   public List<Setting<?>> settings() {
      return this.settings;
   }

   public void collectSettings() {
      this.settings.clear();

      for (Field field : this.getClass().getDeclaredFields()) {
         field.setAccessible(true);

         try {
            Object obj = field.get(this);
            if (obj instanceof Setting) {
               this.settings.add((Setting<?>)obj);
            }
         } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot read setting field " + field.getName(), e);
         }
      }
   }

   public void setEnabledState(boolean z) {
      if (this.enabled != z) {
         this.enabled = z;
         if (!LocalConfigManager.get().isApplying()) {
            LOG.info("[Pulse] Module '{}' -> {}", this.name, z ? "ON" : "OFF");
            LocalConfigManager.get().flushSaveNow("module-" + this.name);
         }
      }

      if (!z || HudServiceRegistry.MODULE_ACCESS.a(this.name)) {
         this.setSubscribed(z);
      }
   }

   public void setEnabledStateQuiet(boolean z) {
      if (this.enabled != z) {
         this.enabled = z;
         if (!z || HudServiceRegistry.MODULE_ACCESS.a(this.name)) {
            this.setSubscribed(z);
         }
      }
   }

   public void setBindKeyQuiet(int i) {
      this.bindKey = i;
   }

   public void setSubscribed(boolean z) {
      if (this.subscribed != z) {
         this.subscribed = z;
         if (z) {
            this.onEnable();
         } else {
            this.onDisable();
         }

         HudNotificationCenter.a(this.name, z);
      }
   }

   public void syncState() {
      this.setSubscribed(this.enabled);
   }

   public void onEnable() {
      EventBusService.EVENT_BUS.subscribe(this);
   }

   public void onDisable() {
      EventBusService.EVENT_BUS.unsubscribe(this);
   }

   public void a(int i) {
      this.setBindKey(i);
   }

   public boolean a() {
      return this.isEnabled();
   }

   public void a(boolean z) {
      this.setEnabled(z);
   }

   public void b() {
      this.collectSettings();
   }

   public void b(boolean z) {
      this.setEnabledState(z);
   }

   public void c(boolean z) {
      this.setSubscribed(z);
   }

   public void c() {
      this.syncState();
   }

   public void d() {
      this.toggle();
   }

   public void e() {
      this.onEnable();
   }

   public void f() {
      this.onDisable();
   }

   public String g() {
      return this.name();
   }

   public String h() {
      return this.description();
   }

   public ModuleCategory i() {
      return this.category();
   }

   public int j() {
      return this.bindKey();
   }

   public boolean k() {
      return this.isActive();
   }

   public boolean l() {
      return this.isSubscribed();
   }

   public List<Setting<?>> m() {
      return this.settings();
   }
}
