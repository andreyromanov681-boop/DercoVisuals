// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.config;

import java.awt.Color;
import java.lang.reflect.Method;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.config.ConfigEntry;
import dev.enizer6569.dercovisuals.config.LocalConfigManager;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.discord.FolderOpener;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiTab;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiTabType;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.gui.core.GuiInteractionState;
import dev.enizer6569.dercovisuals.gui.core.PanelFadeOverlay;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.gui.core.TabHost;
import dev.enizer6569.dercovisuals.gui.core.TabSelector;
import dev.enizer6569.dercovisuals.gui.widgets.SearchBox;
import dev.enizer6569.dercovisuals.hud.notifications.HudNotificationCenter;
import dev.enizer6569.dercovisuals.media.chat.ChatMessages;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.theme.Theme;
import dev.enizer6569.dercovisuals.util.ColorUtils;

public class ConfigsTab implements TabHost, ClickGuiTab {
   private static final String[] a = new String[]{"Конфигурации"};
   private static final float b = 19.0F;
   private static final float c = 48.0F;
   private static final float d = 117.5F;
   private static final float e = 15.0F;
   private static final float f = 8.0F;
   private static final float g = 8.0F;
   private int i;
   private int j;
   private int h = 0;
   private final AnimationState o = new AnimationState();
   private final AnimationState p = new AnimationState();
   private final AnimationState q = new AnimationState();
   private final AnimationState backdrop = new AnimationState();
   private boolean r = false;
   private boolean s = false;
   private boolean t = false;
   private final TabSelector k = new TabSelector(this);
   private final SearchBox l = new SearchBox(117.5F, 15.0F);
   private final ConfigListPanel m = new ConfigListPanel(this.l);
   private final PanelFadeOverlay n = new PanelFadeOverlay(25, 10.0F, 7.5F);

   public ConfigsTab() {
      this.j();
   }

   private void h() {
      try {
         LocalConfigManager.get();
         Method declaredMethod = ConfigListPanel.class.getDeclaredMethod("j");
         declaredMethod.setAccessible(true);
         declaredMethod.invoke(this.m);
      } catch (Throwable var4) {
      }

      try {
         LocalConfigManager localConfigManager = LocalConfigManager.get();

         for (ConfigEntry configEntry : localConfigManager.toConfigEntries()) {
            configEntry.a(configEntry.a().equals(localConfigManager.activeProfile()));
            this.m.a(configEntry);
         }
      } catch (Throwable var5) {
      }
   }

   public void e() {
      this.h();
   }

   private void i() {
   }

   private void j() {
      this.m.a(configEntry -> {
         String name = configEntry.a();
         LocalConfigManager.get().loadProfile(name, () -> {
            for (ConfigEntry configEntry2 : this.m.a()) {
               configEntry2.a(configEntry2.a().equals(name));
            }

            HudNotificationCenter.a("Конфиг " + name + " применён", true);
            ChatMessages.a("Конфиг " + name + " применён");
         }, str -> ChatMessages.a("§cОшибка загрузки: " + str));
      });
      this.m.b(configEntry2 -> {
         String name = configEntry2.a();
         LocalConfigManager.get().deleteProfile(name, () -> {
            HudNotificationCenter.a("Конфиг " + name + " удалён", false);
            ChatMessages.a("Конфиг " + name + " удалён");
            this.e();
         }, str -> ChatMessages.a("§cОшибка удаления: " + str));
      });
      this.m.a(proxy_$3(this));
      this.m.c(configEntry3 -> ChatMessages.a(this.d(configEntry3.a())));
      this.m.setCreateCallback(name -> {
         if (name != null && !name.trim().isEmpty()) {
            try {
               LocalConfigManager.get().createProfile(name, () -> {
                  HudNotificationCenter.a("Конфиг " + name + " создан", true);
                  ChatMessages.a((Object)("Конфиг " + name + " создан."));
                  this.e();
               }, errStr -> ChatMessages.a((Object)("Ошибка создания: " + errStr)));
            } catch (Throwable th) {
               ChatMessages.a((Object)"Ошибка создания конфига.");
            }
         } else {
            ChatMessages.a((Object)"Укажите имя конфига.");
         }
      });
      this.h();
   }

   private static ConfigListPanel.RenameCallback proxy_$3(ConfigsTab configsTab) {
      return (configEntry, str, str2) -> LocalConfigManager.get().renameProfile(configEntry.a(), str, () -> {
         configEntry.a(str);
         HudNotificationCenter.a("Конфиг переименован в " + str, true);
         ChatMessages.a("Конфиг переименован в " + str);
         configsTab.e();
      }, errStr -> ChatMessages.a("§cОшибка переименования: " + errStr));
   }

   @Override
   public String[] c() {
      return a;
   }

   @Override
   public int d() {
      return this.h;
   }

   @Override
   public void a(int i) {
      if (i >= 0 && i < a.length && this.h != i) {
         this.h = i;
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      this.i = i;
      this.j = i2;
      this.k.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.o.a();
      this.p.a();
      this.q.a();
      float fD = DercoVisualsClickGuiScreen.d();
      float fE = DercoVisualsClickGuiScreen.e();
      float f4 = f2 + fD - 19.0F - 117.5F;
      float f5 = f3 + 19.0F - 3.0F;
      float f6 = f4 - 8.0F - 8.0F;
      float f7 = f3 + 19.0F + 3.5F - 3.0F;
      int i3 = !GuiInteractionState.a().b() && !this.m.e() ? 0 : 1;
      int i4 = i3 == 0 && GuiInput.a(f6 - 16.0F - 16.0F, f7, 8.0F, 8.0F, i, i2) ? 1 : 0;
      int i5 = i3 == 0 && GuiInput.a(f6 - 8.0F - 8.0F, f7, 8.0F, 8.0F, i, i2) ? 1 : 0;
      int i6 = i3 == 0 && GuiInput.a(f6, f7, 8.0F, 8.0F, i, i2) ? 1 : 0;
      this.a(this.o, Bool.from(i4), this.r, Bool.from(i3));
      this.r = Bool.from(i3 == 0 && i4 != 0 ? 1 : 0);
      this.a(this.p, Bool.from(i5), this.s, Bool.from(i3));
      this.s = Bool.from(i3 == 0 && i5 != 0 ? 1 : 0);
      this.a(this.q, Bool.from(i6), this.t, Bool.from(i3));
      this.t = Bool.from(i3 == 0 && i6 != 0 ? 1 : 0);
      this.a(MatrixStackVar, renderer2D, f6 - 16.0F - 16.0F, f7, 8.0F, (float)this.o.j(), "add");
      this.a(MatrixStackVar, renderer2D, f6 - 8.0F - 8.0F, f7, 8.0F, (float)this.p.j(), "save");
      this.a(MatrixStackVar, renderer2D, f6, f7, 8.0F, (float)this.q.j(), "key");
      this.a(MatrixStackVar, renderer2D, f6 + 16.0F, f7 - 8.0F, 8.0F, 8.0F, "folder");
      this.l.a(MatrixStackVar, renderer2D, f4, f5, 117.5F, 15.0F, i, i2);
      this.m.a(MatrixStackVar, renderer2D, f2 + 19.0F, f3 + 48.0F, fD - 38.0F, fE - 48.0F - 9.5F - 8.0F, i, i2, 1.0F);
      this.n.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.backdrop.a();
      boolean dialogOpen = this.m.e();
      if (dialogOpen && this.backdrop.i() < 1.0) {
         this.backdrop.a(1.0, 0.25, Easing.h);
      } else if (!dialogOpen && this.backdrop.i() > 0.0) {
         this.backdrop.a(0.0, 0.18, Easing.h);
      }

      float backdropAlpha = (float)this.backdrop.j();
      if (backdropAlpha > 0.01F) {
         float sw = MinecraftContext.getWidth();
         float sh = MinecraftContext.getHeight();
         renderer2D.a(0.0F, 0.0F, sw, sh, 0.0F, new Color(0, 0, 0, (int)(160.0F * backdropAlpha)), MatrixStackVar);
      }

      this.m.a(MatrixStackVar, renderer2D, MinecraftContext.getWidth() / 2.0F, MinecraftContext.getHeight() / 2.0F, i, i2);
      if (i4 != 0 || i5 != 0 || i6 != 0) {
         GuiInput.g();
      }
   }

   private void a(AnimationState animationState, boolean z, boolean z2, boolean z3) {
      if (z3 && z2) {
         animationState.a(0.0, 0.15, Easing.h);
      } else {
         if (z3 || z == z2) {
            return;
         }

         animationState.a(z ? 1.0 : 0.0, 0.15, Easing.h);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, String str) {
      Color colorA = ColorUtils.a(Theme.b, Theme.d, f5);
      String str2;
      switch (str) {
         case "add":
            str2 = "\ue91c";
            break;
         case "save":
            str2 = "\ue920";
            break;
         case "key":
            str2 = "\ue912";
            break;
         default:
            if (!str.equals("folder")) {
               return;
            }

            str2 = "?";
      }

      FontManager.e[14].a(str2, f2, f3, colorA, MatrixStackVar);
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D) {
      this.m.a(MatrixStackVar, renderer2D, MinecraftContext.getWidth() / 2.0F, MinecraftContext.getHeight() / 2.0F, this.i, this.j);
   }

   @Override
   public void a(float f2, float f3, int i, int i2) {
      if (this.m.e()) {
         this.m.a(f2 + 19.0F, f3 + 48.0F, DercoVisualsClickGuiScreen.d() - 38.0F, DercoVisualsClickGuiScreen.e() - 48.0F - 9.5F - 8.0F, i, i2);
      } else if (!this.l.a(i, i2)) {
         this.k.a(f2, f3, i, i2);
         float fD = DercoVisualsClickGuiScreen.d();
         float fE = DercoVisualsClickGuiScreen.e();
         float f4 = f2 + fD - 19.0F - 117.5F - 8.0F - 8.0F;
         float f5 = f3 + 19.0F + 3.5F - 3.0F;
         if (GuiInput.a(f4 - 16.0F - 16.0F, f5, 8.0F, 8.0F, i, i2)) {
            this.m();
         } else if (GuiInput.a(f4 - 8.0F - 8.0F, f5, 8.0F, 8.0F, i, i2)) {
            this.l();
         } else if (GuiInput.a(f4, f5, 8.0F, 8.0F, i, i2)) {
            this.k();
         } else {
            this.m.a(f2 + 19.0F, f3 + 48.0F, fD - 38.0F, fE - 48.0F - 9.5F - 8.0F, i, i2);
         }
      }
   }

   private void k() {
      this.m.g();
   }

   private void l() {
      this.m.h();
   }

   private void m() {
      this.m.openCreate();
   }

   @Override
   public void b(float f2, float f3, int i, int i2) {
      this.a(f2, f3, i, i2);
   }

   @Override
   public void c(float f2, float f3, int i, int i2) {
      this.m.a(i, i2);
   }

   @Override
   public void a(float f2, float f3, int i, int i2, double d2, double d3) {
      this.m.a(i, i2, d2, d3);
   }

   @Override
   public void a(float f2) {
      this.m.a(f2, this.i, this.j);
   }

   @Override
   public boolean a(int i, int i2, int i3) {
      if (this.m.d()) {
         return this.m.a(i, i2, i3);
      } else {
         return this.l.c() ? this.l.a(i, i2, i3) : this.m.a(i, i2, i3);
      }
   }

   @Override
   public boolean b() {
      return Bool.from(!this.l.c() && !this.m.d() ? 0 : 1);
   }

   public boolean a(char c2, int i) {
      if (this.m.d()) {
         return this.m.a(c2, i);
      } else {
         return this.l.c() ? this.l.a(c2, i) : this.m.a(c2, i);
      }
   }

   public boolean f() {
      return this.l.c();
   }

   public boolean g() {
      return this.m.e();
   }

   @Override
   public ClickGuiTabType a() {
      return ClickGuiTabType.CONFIGS;
   }

   private String a(String str) {
      if (str == null) {
         return "Не удалось переключить конфиг.";
      }

      switch (str) {
         case "Not connected":
            return "Нет соединения с сервером. Проверьте интернет-подключение.";
         case "Not initialized":
            return "Сервис конфигов ещё не инициализирован. Попробуйте позже.";
         case "CONFIG_NOT_FOUND":
            return "Конфиг не найден. Возможно, он был удалён.";
         case "No response":
            return "Сервер не отвечает. Попробуйте позже.";
         default:
            return !str.contains("timeout") && !str.contains("Timeout")
               ? "Не удалось переключить конфиг: " + str
               : "Превышено время ожидания. Проверьте интернет-подключение.";
      }
   }

   private String b(String str) {
      if (str == null) {
         return "Не удалось удалить конфиг.";
      }

      switch (str) {
         case "Not connected":
            return "Нет соединения с сервером. Проверьте интернет-подключение.";
         case "Not initialized":
            return "Сервис конфигов ещё не инициализирован. Попробуйте позже.";
         case "Cannot delete active config":
            return "Нельзя удалить активный конфиг. Сначала переключитесь на другой.";
         case "CONFIG_NOT_FOUND":
            return "Конфиг не найден. Возможно, он уже был удалён.";
         case "No response":
            return "Сервер не отвечает. Попробуйте позже.";
         default:
            return !str.contains("timeout") && !str.contains("Timeout")
               ? "Не удалось удалить конфиг: " + str
               : "Превышено время ожидания. Проверьте интернет-подключение.";
      }
   }

   private String c(String str) {
      if (str == null) {
         return "Не удалось переименовать конфиг.";
      }

      switch (str) {
         case "Not connected":
            return "Нет соединения с сервером. Проверьте интернет-подключение.";
         case "Not initialized":
            return "Сервис конфигов ещё не инициализирован. Попробуйте позже.";
         case "Cannot rename active config":
            return "Нельзя переименовать активный конфиг. Сначала переключитесь на другой.";
         case "NAME_TAKEN":
         case "Config already exists":
            return "Конфиг с таким именем уже существует.";
         case "CONFIG_NOT_FOUND":
            return "Конфиг не найден. Возможно, он был удалён.";
         case "INVALID_NAME":
            return "Недопустимое имя конфига. Используйте только буквы, цифры и пробелы.";
         case "No response":
            return "Сервер не отвечает. Попробуйте позже.";
         default:
            return !str.contains("timeout") && !str.contains("Timeout")
               ? "Не удалось переименовать конфиг: " + str
               : "Превышено время ожидания. Проверьте интернет-подключение.";
      }
   }

   private String d(String str) {
      if (str == null) {
         return "Не удалось создать конфиг.";
      }

      switch (str) {
         case "Not connected":
            return "Нет соединения с сервером. Проверьте интернет-подключение.";
         case "Not initialized":
            return "Сервис конфигов ещё не инициализирован. Попробуйте позже.";
         case "Config already exists":
            return "Конфиг с таким именем уже существует.";
         case "CONFLICT":
            return "Конфликт версий. Попробуйте ещё раз.";
         case "LIMIT_REACHED":
            return "Достигнут лимит конфигов. Удалите ненужные конфиги.";
         case "INVALID_NAME":
            return "Недопустимое имя конфига. Используйте только буквы, цифры и пробелы.";
         case "No response":
            return "Сервер не отвечает. Попробуйте позже.";
         default:
            return !str.contains("timeout") && !str.contains("Timeout")
               ? "Не удалось создать конфиг: " + str
               : "Превышено время ожидания. Проверьте интернет-подключение.";
      }
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private void n() {
      FolderOpener.openConfigFolder();
   }
}
