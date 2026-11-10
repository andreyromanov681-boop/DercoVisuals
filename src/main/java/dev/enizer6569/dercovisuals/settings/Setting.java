// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.settings;

import java.util.Objects;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.enizer6569.dercovisuals.config.ConfigManager;
import dev.enizer6569.dercovisuals.config.LocalConfigManager;

public abstract class Setting<ConfigTextDialog> {
   private static final Logger LOG = LogManager.getLogger("dercovisuals/config");
   private final String name;
   private final String description;
   private ConfigTextDialog value;
   private final ConfigTextDialog defaultValue;
   private final ConfigTextDialog minValue;
   private final ConfigTextDialog maxValue;
   private Supplier<Boolean> visibleCondition = () -> true;

   protected Setting(String str, String str2, ConfigTextDialog configtextdialog) {
      this(str, str2, configtextdialog, null, null);
   }

   protected Setting(String str, String str2, ConfigTextDialog configtextdialog, ConfigTextDialog configtextdialog2, ConfigTextDialog configtextdialog3) {
      this.name = str;
      this.description = str2;
      this.value = configtextdialog;
      this.defaultValue = configtextdialog;
      this.minValue = configtextdialog2;
      this.maxValue = configtextdialog3;
   }

   public String name() {
      return this.name;
   }

   public String description() {
      return this.description;
   }

   public ConfigTextDialog value() {
      return this.value;
   }

   public ConfigTextDialog defaultValue() {
      return this.defaultValue;
   }

   public ConfigTextDialog minValue() {
      return this.minValue;
   }

   public ConfigTextDialog maxValue() {
      return this.maxValue;
   }

   public void setValue(ConfigTextDialog configtextdialog) {
      if (!Objects.equals(this.value, configtextdialog)) {
         this.value = configtextdialog;
         if (!LocalConfigManager.get().isApplying()) {
            LOG.debug("[DercoVisuals] Setting '{}' -> {}", this.name, configtextdialog);
            ConfigManager.a().h();
         }
      }
   }

   public Setting<ConfigTextDialog> visibleWhen(Supplier<Boolean> supplier) {
      this.visibleCondition = supplier != null ? supplier : () -> true;
      return this;
   }

   public boolean isVisible() {
      return this.visibleCondition.get();
   }

   public void reset() {
      this.value = this.defaultValue;
   }

   public String f() {
      return this.name();
   }

   public String g() {
      return this.description();
   }

   public Supplier<Boolean> h() {
      return this.visibleCondition;
   }

   public ConfigTextDialog i() {
      return this.defaultValue();
   }

   public ConfigTextDialog j() {
      return this.minValue();
   }

   public ConfigTextDialog k() {
      return this.value();
   }

   public void a(ConfigTextDialog configtextdialog) {
      this.setValue(configtextdialog);
   }

   public void l() {
      this.reset();
   }

   public Setting<ConfigTextDialog> b(Supplier<Boolean> supplier) {
      return this.visibleWhen(supplier);
   }

   public boolean m() {
      return this.isVisible();
   }
}
