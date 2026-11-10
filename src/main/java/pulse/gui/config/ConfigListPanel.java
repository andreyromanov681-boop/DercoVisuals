package pulse.gui.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.joml.Matrix3x2fStack;
import pulse.config.CloudConfigRepository;
import pulse.config.ConfigEntry;
import pulse.config.ConfigManager;
import pulse.config.LocalConfigManager;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.widgets.ScrollBar;
import pulse.gui.widgets.SearchBox;
import pulse.hud.notifications.HudNotificationCenter;
import pulse.media.chat.ChatMessages;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;

public class ConfigListPanel {
   private static final float c = 6.0F;
   private static final float d = -5.0F;
   private static final float e = 4.0F;
   private static final float f = 2.0F;
   private final ScrollBar i;
   private final PanelFadeOverlay j;
   private final SearchBox k;
   private final ConfigActionMenu l;
   private final ConfigNameDialog m;
   private final ConfigBoundsDialog n;
   private final ConfigTextDialog o;
   private final ConfigCreateDialog createDialog = new ConfigCreateDialog();
   private Consumer<ConfigEntry> r;
   private Consumer<ConfigEntry> s;
   private ConfigListPanel.RenameCallback t;
   private Consumer<ConfigEntry> u;
   private Consumer<String> createCallback;
   private float v;
   private float w;
   private float x;
   private float y;
   private ConfigEntry z;
   public static int a;
   public static boolean b;
   private final List<ConfigEntry> g = new ArrayList<>();
   private final List<ConfigCard> h = new ArrayList<>();
   private String p = "";
   private List<ConfigCard> q = new ArrayList<>();

   public ConfigListPanel(SearchBox searchBox) {
      this.k = searchBox;
      this.k.a(this::d);
      this.i = new ScrollBar(2.0F, 20.0F);
      this.i.b(10.0F);
      this.i.a(Theme.b);
      this.i.b(Theme.d);
      this.j = new PanelFadeOverlay(25, 5.0F, 9.0F);
      this.l = new ConfigActionMenu();
      this.m = new ConfigNameDialog();
      this.n = new ConfigBoundsDialog();
      this.o = new ConfigTextDialog();
      this.i();
   }

   private void i() {
      this.l.a(action -> this.a(action));
      this.l.a(() -> this.z = null);
      this.createDialog.a(name -> {
         if (this.createCallback != null) {
            this.createCallback.accept(name);
         }
      });
      this.m.a(str -> {
         ConfigManager configManagerA = ConfigManager.a();
         if (configManagerA.i()) {
            configManagerA.c(str, () -> {
               ChatMessages.a((Object)"Конфиг импортирован!");
               this.j();
            }, errStr -> ChatMessages.a((Object)this.a(errStr)));
         } else {
            ChatMessages.a((Object)"Конфиги ещё загружаются...");
         }
      });
      this.n.a((num, num2) -> {
         ConfigManager configManagerA = ConfigManager.a();
         if (!configManagerA.i()) {
            ChatMessages.a((Object)"Конфиги ещё загружаются...");
         } else {
            ConfigEntry configEntryD = this.n.d();
            if (configEntryD != null) {
               configManagerA.a(configEntryD.a(), num, (Integer)null, num2, str2 -> this.o.a(str2, configEntryD), str3 -> ChatMessages.a((Object)this.b(str3)));
            }
         }
      });
   }

   private String a(String str) {
      if (str == null) {
         return "Неизвестная ошибка";
      }

      switch (str) {
         case "KEY_NOT_FOUND":
            return "Ключ не найден. Проверьте правильность ввода.";
         case "KEY_EXPIRED":
            return "Срок действия ключа истёк.";
         case "KEY_EXHAUSTED":
            return "Ключ уже использован максимальное количество раз.";
         case "key is required":
            return "Введите ключ для активации.";
         case "Internal error":
            return "Внутренняя ошибка сервера. Попробуйте позже.";
         case "Not connected":
            return "Нет подключения к серверу.";
         default:
            return "Ошибка активации: " + str;
      }
   }

   private String b(String str) {
      if (str == null) {
         return "Неизвестная ошибка";
      }

      byte b2 = -1;
      switch (str.hashCode()) {
         case -2026653947:
         case -1926714738:
         case -1557786393:
         case -1038596745:
         case -532953636:
         case -347113380:
         case -288711231:
         case 782165529:
         case 1266212642:
         case 1333715397:
         default:
            switch (b2) {
               case 0:
                  return "Достигнут лимит ключей (максимум 20). Удалите неиспользуемые ключи.";
               case 1:
                  return "Невозможно создать столько ключей. Уменьшите количество или удалите старые ключи.";
               case 2:
                  return "Конфиг не найден. Сначала сохраните конфиг.";
               case 3:
                  return "Не указано имя конфига.";
               case 4:
                  return "Данные конфига отсутствуют. Сначала сохраните конфиг.";
               case 5:
                  return "Внутренняя ошибка сервера. Попробуйте позже.";
               case 6:
                  return "Нет подключения к серверу.";
               case 7:
                  return "Система ещё не инициализирована. Подождите.";
               case 8:
               case 9:
                  return "Сервер не вернул ключ. Попробуйте ещё раз.";
               default:
                  return "Ошибка создания ключа: " + str;
            }
      }
   }

   private void j() {
      ConfigManager configManagerA = ConfigManager.a();
      if (configManagerA.i()) {
         this.b();
         CloudConfigRepository cloudConfigRepositoryJ = configManagerA.j();
         if (cloudConfigRepositoryJ != null) {
            String strG = configManagerA.g();

            for (CloudConfigRepository.RemoteConfigRecord remoteConfigRecord : cloudConfigRepositoryJ.f()) {
               ConfigEntry configEntry = new ConfigEntry(
                  remoteConfigRecord.b, remoteConfigRecord.c, LocalDateTime.ofInstant(Instant.ofEpochMilli(remoteConfigRecord.g), ZoneId.systemDefault())
               );
               configEntry.a(remoteConfigRecord.b.equals(strG));
               this.a(configEntry);
            }
         }
      }
   }

   private void a(ConfigActionMenu.Action action) {
      if (this.z != null) {
         switch (action) {
            case SAVE_TO:
               if (this.z.f()) {
                  this.d(this.z);
               } else {
                  String nameToSave = this.z.a();
                  LocalConfigManager.get().saveCurrentState();
                  LocalConfigManager.get().flushSaveNow("action-menu");
                  ChatMessages.a("Конфиг '" + nameToSave + "' сохранён!");
               }
               break;
            case SHARE:
               this.n.a(this.z);
               break;
            case RENAME:
               for (ConfigCard configCard : this.h) {
                  if (configCard.a() == this.z) {
                     configCard.b();
                     break;
                  }
               }
               break;
            case DELETE:
               if (this.s != null) {
                  this.s.accept(this.z);
               } else {
                  String nameToDelete = this.z.a();
                  LocalConfigManager.get().deleteProfile(nameToDelete, () -> {
                     ChatMessages.a("Конфиг '" + nameToDelete + "' удалён!");
                     this.b();
                  }, errStr -> ChatMessages.a("Ошибка удаления: " + errStr));
               }
         }
      }
   }

   public void a(ConfigEntry configEntry) {
      ConfigCard configCard = new ConfigCard(configEntry);
      if (configEntry.f()) {
         configCard.c(this::d);
         this.g.add(configEntry);
         this.h.add(configCard);
      } else {
         configCard.a((configEntry2, fArr) -> {
            this.z = configEntry2;
            this.l.a(fArr[0], fArr[1], fArr[2], configEntry2);
         });
         configCard.b(configEntry2 -> {
            if (this.r != null) {
               this.r.accept(configEntry2);
            } else {
               String name = configEntry2.a();
               LocalConfigManager.get().loadProfile(name, () -> {
                  HudNotificationCenter.a("Конфиг " + name + " применён", true);
                  ChatMessages.a("Конфиг " + name + " применён");
               }, err -> ChatMessages.a("Ошибка загрузки: " + err));
            }
         });
         configCard.b((str, str2) -> {
            if (this.g.stream().anyMatch(configEntry3 -> Bool.from(configEntry3 != configEntry && configEntry3.a().equalsIgnoreCase(str2) ? 1 : 0))) {
               configEntry.a(str);
            } else if (this.t != null) {
               this.t.onRenamed(configEntry, str, str2);
            }
         });
         int i = 0;

         for (int i2 = 0; i2 < this.g.size(); i2++) {
            if (this.g.get(i2).f()) {
               i = i2;
               break;
            }

            i = i2 - -2 - 1;
         }

         this.g.add(i, configEntry);
         this.h.add(i, configCard);
      }

      this.k();
   }

   private void c(ConfigEntry configEntry) {
      if (!configEntry.f()) {
         Iterator<ConfigEntry> it = this.g.iterator();

         while (it.hasNext()) {
            it.next().a(false);
         }

         configEntry.a(true);
         if (this.r != null) {
            this.r.accept(configEntry);
         }
      }
   }

   public void b(ConfigEntry configEntry) {
      int iIndexOf = this.g.indexOf(configEntry);
      if (iIndexOf >= 0) {
         this.g.remove(iIndexOf);
         this.h.remove(iIndexOf);
         this.k();
         float f2 = this.y - -5.0F - 4.0F;
         float fL = this.l();
         if (fL > f2) {
            this.i.b(fL, f2);
         } else {
            this.i.e();
         }

         if (this.s != null) {
            this.s.accept(configEntry);
         }
      }
   }

   private void d(ConfigEntry configEntry) {
      if (configEntry.f()) {
         ConfigManager configManagerA = ConfigManager.a();
         if (configManagerA.i()) {
            configManagerA.a(configEntry.g(), configState -> {
               configManagerA.a(configState);
               configManagerA.e();
               ChatMessages.a((Object)("Премиум конфиг \"" + configEntry.a() + "\" применён к активному конфигу."));
            }, str -> ChatMessages.a((Object)this.c(str)));
         } else {
            ChatMessages.a((Object)"Сервис конфигов ещё не инициализирован.");
         }
      }
   }

   private String c(String str) {
      if (str == null) {
         return "Не удалось применить премиум конфиг.";
      }

      byte b2 = -1;
      switch (str.hashCode()) {
         case -1557786393:
         case -1110739040:
         case -916893481:
         case -532953636:
         case 280577614:
         case 1023286998:
         default:
            switch (b2) {
               case 0:
                  return "Нет соединения с сервером. Проверьте интернет-подключение.";
               case 1:
                  return "Сервис конфигов ещё не инициализирован. Попробуйте позже.";
               case 2:
                  return "Премиум конфиг не найден.";
               case 3:
                  return "Вы не приобрели этот конфиг.";
               case 4:
                  return "Не удалось применить конфиг. Попробуйте позже.";
               case 5:
                  return "Сервер не отвечает. Попробуйте позже.";
               default:
                  return !str.contains("timeout") && !str.contains("Timeout")
                     ? "Не удалось применить премиум конфиг: " + str
                     : "Превышено время ожидания. Проверьте интернет-подключение.";
            }
      }
   }

   public List<ConfigEntry> a() {
      return this.g;
   }

   public void b() {
      this.g.clear();
      this.g.clear();
      this.g.clear();
      this.i.e();
   }

   public void c() {
      for (int size = this.g.size() - 1; size >= 0; size--) {
         if (this.g.get(size).f()) {
            this.g.remove(size);
            this.h.remove(size);
         }
      }

      this.k();
   }

   public void a(Consumer<ConfigEntry> consumer) {
      this.r = consumer;
   }

   public void b(Consumer<ConfigEntry> consumer) {
      this.s = consumer;
   }

   public void a(ConfigListPanel.RenameCallback renameCallback) {
      this.t = renameCallback;
   }

   public void c(Consumer<ConfigEntry> consumer) {
      this.u = consumer;
   }

   private void d(String str) {
      this.p = str.toLowerCase().trim();
      this.k();
      this.i.e();
   }

   private void k() {
      if (this.p.isEmpty()) {
         this.q = new ArrayList<>(this.h);
      } else {
         this.q = this.h.stream().filter(configCard -> {
            String owner = configCard.a().b();
            return Bool.from(!configCard.a().a().toLowerCase().contains(this.p) && (owner == null || !owner.toLowerCase().contains(this.p)) ? 0 : 1);
         }).collect(Collectors.toList());
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i, int i2, float f6) {
      this.v = f2;
      this.w = f3;
      this.x = f4;
      this.y = f5;
      this.i.a();
      if (this.q.isEmpty()) {
         this.a(MatrixStackVar, renderer2D, f2, f3, f4, f5);
      } else {
         float f7 = f3 + -5.0F;
         float f8 = f5 - -5.0F - 4.0F;
         float fL = this.l();
         boolean z = fL > f8;
         if (!z) {
            this.i.e();
         }

         renderer2D.b().a(f2, f3 + 4.0F - 6.0F, f4, f5 - 8.0F + 6.0F, MatrixStackVar);
         float fB = f7 - this.i.b();

         for (ConfigCard configCard : this.h) {
            float f9 = fB + 4.0F;
            if (f9 + 36.5F >= f7 && f9 <= f7 + f8) {
               configCard.a(MatrixStackVar, renderer2D, f2 + 1.0F, f9, f4 - 2.0F, i, i2, f6);
            }

            fB += 42.5F;
         }

         renderer2D.b().a(MatrixStackVar);
         if (z) {
            this.i.a(MatrixStackVar, renderer2D, f2 + f4 + 18.0F, f7, f8, fL, f8, i, i2, false);
         } else if (b) {
         }

         this.j.a(MatrixStackVar, renderer2D, f2, f3 + 1.5F, f4, f5, f6);
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      this.l.a(MatrixStackVar, renderer2D, i, i2);
      this.m.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.createDialog.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.n.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.o.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[14];
      String strEmpty = !this.g.isEmpty() ? "Ничего не найдено" : "У тебя пока-что нету конфигов :(";
      fontRenderer.a(strEmpty, f2 + (f4 - fontRenderer.a(strEmpty)) / 2.0F, f3 + f5 / 2.0F - fontRenderer.b(strEmpty) / 2.0F, Theme.b, MatrixStackVar);
   }

   private float l() {
      return this.q.isEmpty() ? 0.0F : this.q.size() * 42.5F - 6.0F;
   }

   public boolean a(float f2, float f3, float f4, float f5, int i, int i2) {
      if (this.o.b()) {
         return this.o.a(this.x + 100.0F, this.y + 100.0F, i, i2);
      }

      if (this.m.c()) {
         return this.m.a(this.x + 100.0F, this.y + 100.0F, i, i2);
      }

      if (this.createDialog.c()) {
         return this.createDialog.a(this.x + 100.0F, this.y + 100.0F, i, i2);
      }

      if (this.n.b()) {
         return this.n.a(this.x + 100.0F, this.y + 100.0F, i, i2);
      }

      if (this.l.b() && this.l.a(i, i2)) {
         return true;
      }

      float f6 = f3 + -5.0F;
      float f7 = f5 - -5.0F - 4.0F;
      float fL = this.l();
      if (fL > f7 && this.i.a(f2 + f4 + 18.0F, f6, f7, fL, f7, i, i2)) {
         return true;
      }

      if (GuiInput.a(f2, f3, f4, f5, i, i2)) {
         float fB = f6 - this.i.b();

         for (Iterator<ConfigCard> it = this.h.iterator(); it.hasNext(); fB += 42.5F) {
            if (it.next().a(f2 + 1.0F, fB + 4.0F, f4 - 2.0F, i, i2)) {
               return true;
            }
         }
      }

      return false;
   }

   public void a(int i, int i2) {
      this.i.d();
      if (this.o.b()) {
         this.o.a(i, i2);
      }
   }

   public void a(int i, int i2, double d2, double d3) {
      if (this.o.b()) {
         this.o.a(i, i2, d2, d3);
      } else if (this.i.c()) {
         float f2 = this.y - -5.0F - 4.0F;
         this.i.a(i2, this.l(), f2);
      }
   }

   public void a(float f2, int i, int i2) {
      if (this.o.b()) {
         this.o.a(f2, i, i2);
      } else if (!this.l.b()) {
         if (!this.m.c() && !this.n.b() && !this.m() && GuiInput.a(this.v, this.w, this.x, this.y, i, i2)) {
            this.i.a(f2, this.l(), this.y - -5.0F - 4.0F);
         }
      }
   }

   public boolean a(int i, int i2, int i3) {
      for (ConfigCard configCard : this.h) {
         if (configCard.d()) {
            return configCard.a(i, i2, i3);
         }
      }

      if (this.o.b()) {
         return this.o.a(i, i2, i3);
      } else if (this.m.c()) {
         return this.m.a(i, i2, i3);
      } else if (this.createDialog.c()) {
         return this.createDialog.a(i, i2, i3);
      } else {
         return this.n.b() ? this.n.a(i, i2, i3) : false;
      }
   }

   public boolean a(char c2, int i) {
      for (ConfigCard configCard : this.h) {
         if (configCard.d()) {
            return configCard.a(c2, i);
         }
      }

      if (this.o.b()) {
         return this.o.a(c2, i);
      } else if (this.m.c()) {
         return this.m.a(c2, i);
      } else if (this.createDialog.c()) {
         return this.createDialog.a(c2, i);
      } else {
         return this.n.b() ? this.n.a(c2, i) : false;
      }
   }

   public boolean d() {
      return Bool.from(!this.m.e() && !this.createDialog.e() && !this.n.e() && !this.o.d() && !this.m() ? 0 : 1);
   }

   public boolean e() {
      return Bool.from(!this.l.b() && !this.m.c() && !this.createDialog.c() && !this.n.b() && !this.o.b() ? 0 : 1);
   }

   private boolean m() {
      Iterator<ConfigCard> it = this.h.iterator();

      while (it.hasNext()) {
         if (it.next().d()) {
            return true;
         }
      }

      return false;
   }

   public boolean f() {
      return this.g.isEmpty();
   }

   public void g() {
      this.m.a();
   }

   public void openCreate() {
      this.createDialog.a();
   }

   public void setCreateCallback(Consumer<String> consumer) {
      this.createCallback = consumer;
   }

   public void h() {
      if (this.l.b()) {
         this.l.a();
      }

      if (this.m.c()) {
         this.m.b();
      }

      if (this.createDialog.c()) {
         this.createDialog.b();
      }

      if (this.n.b()) {
         this.n.a();
      }

      if (this.o.b()) {
         this.o.a();
      }

      for (ConfigCard configCard : this.h) {
         if (configCard.d()) {
            configCard.a(false);
         } else if (b) {
         }
      }
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   static class AnonymousClass1 {
      static final int[] $SwitchMap$pulse$ConfigActionMenu$Action = new int[ConfigActionMenu.Action.values().length];

      static {
         try {
            $SwitchMap$pulse$ConfigActionMenu$Action[ConfigActionMenu.Action.SAVE_TO.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$pulse$ConfigActionMenu$Action[ConfigActionMenu.Action.SHARE.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$pulse$ConfigActionMenu$Action[ConfigActionMenu.Action.RENAME.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$pulse$ConfigActionMenu$Action[ConfigActionMenu.Action.DELETE.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }
      }
   }

   @FunctionalInterface
   public interface RenameCallback {
      void onRenamed(ConfigEntry var1, String var2, String var3);

      static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
