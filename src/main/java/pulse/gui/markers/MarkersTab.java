package pulse.gui.markers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.joml.Matrix3x2fStack;
import pulse.core.Bool;
import pulse.gui.core.ClickGuiTab;
import pulse.gui.core.ClickGuiTabType;
import pulse.gui.core.GuiEntry;
import pulse.gui.core.GuiEntrySource;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.gui.core.TabHost;
import pulse.gui.core.TabSelector;
import pulse.gui.modules.ModuleGrid;
import pulse.gui.notifications.NotificationEditorPanel;
import pulse.gui.notifications.NotificationListPanel;
import pulse.hud.notifications.Notification;
import pulse.markers.MarkerOptions;
import pulse.module.ModuleRegistry;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;

public class MarkersTab implements TabHost, ClickGuiTab {
   private static final String[] c = new String[]{"Метки", "Настройки"};
   private static final float d = 48.0F;
   private static final float e = 19.0F;
   private static final float f = 5.0F;
   private int m;
   private int n;
   public static int a;
   public static boolean b;
   private int g = 0;
   private boolean o = false;
   private final TabSelector h = new TabSelector(this);
   private final MarkersTab.MarkerEntrySource i = new MarkersTab.MarkerEntrySource();
   private final PanelFadeOverlay j = new PanelFadeOverlay(25, 10.0F, 7.5F);
   private final NotificationListPanel k = new NotificationListPanel();
   private final NotificationEditorPanel l = new NotificationEditorPanel();

   public MarkersTab() {
      this.k.a(notification -> this.l.a(notification));
      this.k.b(notification2 -> this.l.a(notification2));
      this.l.a(notification3 -> {
         Notification notificationB = this.k.b(notification3);
         if (notificationB == null) {
            this.l.a((Notification)null);
         } else {
            this.k.c(notificationB);
            this.l.a(notificationB);
         }
      });
   }

   private void g() {
      if (!this.o) {
         this.k.e();
         Notification notificationD = this.k.d();
         if (notificationD != null) {
            this.k.c(notificationD);
            this.l.a(notificationD);
         }

         this.o = true;
      }
   }

   @Override
   public String[] c() {
      return c;
   }

   @Override
   public int d() {
      return this.g;
   }

   @Override
   public void a(int i) {
      if (i >= 0 && i < c.length && this.g != i) {
         this.g = i;
         if (i != 1) {
            this.i.c();
         } else {
            this.i.a();
         }

         this.l.c();
      }
   }

   public void e() {
      this.l.c();
   }

   public void f() {
      this.i.c();
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2) {
      if (this.g == 1) {
         this.i.a(MatrixStackVar, renderer2D, i, i2);
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      this.m = i;
      this.n = i2;
      this.k.e();
      this.g();
      this.h.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      if (this.g == 0) {
         this.b(MatrixStackVar, renderer2D, f2, f3, i, i2);
      } else {
         this.i.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
         this.j.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      }
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      float fD = PulseClickGuiScreen.d();
      float fE = PulseClickGuiScreen.e();
      float f4 = f3 + 48.0F;
      float f5 = fE - 48.0F - 9.5F + 50.0F;
      float f6 = f2 + 19.0F;
      float panelBottom = f3 + fE - 20.0F;
      float f7 = panelBottom - f4;
      float listHeight = f5 - 5.0F - 7.0F - 20.0F;
      float listTop = panelBottom - listHeight;
      this.k.a(MatrixStackVar, renderer2D, f6, listTop, listHeight, i, i2, 1.0F);
      float f8 = f6 + 115.5F + 5.0F;
      float f9 = fD - 38.0F - 115.5F - 5.0F;
      if (this.k.g()) {
         this.a(MatrixStackVar, renderer2D, f8, f4, f9, f7);
      } else if (this.l.a() == null) {
         this.b(MatrixStackVar, renderer2D, f8, f4, f9, f7);
      } else {
         this.l.a(MatrixStackVar, renderer2D, f8, f4, f9, f7, i, i2, 1.0F);
      }

      this.l.a(MatrixStackVar, renderer2D, i, i2, 1.0F);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[24];
      FontRenderer fontRenderer2 = FontManager.a[15];
      float fA = fontRenderer.a("У тебя пока-что нету меток :(");
      float fA2 = fontRenderer2.a("Нажми на плюсик, и добавь новую метку");
      float titleY = f3 + f5 / 2.0F - 20.0F;
      fontRenderer.a("У тебя пока-что нету меток :(", f2 + (f4 - fA) / 2.0F, titleY, Theme.a, MatrixStackVar);
      fontRenderer2.a("Нажми на плюсик, и добавь новую метку", f2 + (f4 - fA2) / 2.0F, titleY + 15.0F, Theme.b, MatrixStackVar);
   }

   private void b(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5) {
      FontRenderer fontRenderer = FontManager.b[24];
      FontRenderer fontRenderer2 = FontManager.a[15];
      float fA = fontRenderer.a("Выбери метку");
      float fA2 = fontRenderer2.a("Чтобы настроить метку, просто выбери одну из уже созданных");
      float titleY = f3 + f5 / 2.0F - 20.0F;
      fontRenderer.a("Выбери метку", f2 + (f4 - fA) / 2.0F, titleY, Theme.a, MatrixStackVar);
      fontRenderer2.a("Чтобы настроить метку, просто выбери одну из уже созданных", f2 + (f4 - fA2) / 2.0F, titleY + 15.0F, Theme.b, MatrixStackVar);
   }

   @Override
   public void a(float f2, float f3, int i, int i2) {
      this.h.a(f2, f3, i, i2);
      if (this.g != 0) {
         this.i.a(f2, f3, i, i2);
      } else {
         float fD = PulseClickGuiScreen.d();
         float fE = PulseClickGuiScreen.e();
         float f4 = f3 + 48.0F;
         float panelBottom = f3 + fE - 20.0F;
         float f5 = fE - 48.0F - 9.5F - 5.0F + 50.0F;
         float f6 = f2 + 19.0F;
         float f7 = f6 + 115.5F + 5.0F;
         float f8 = fD - 38.0F - 115.5F - 5.0F;
         float listClickHeight = f5 - 7.0F - 20.0F;
         float listClickTop = panelBottom - listClickHeight;
         float panelHeight = panelBottom - f4;
         if ((this.l.a() == null || !this.l.a(f7, f4, f8, panelHeight, i, i2)) && this.k.a(f6, listClickTop, listClickHeight, i, i2)) {
         }
      }
   }

   @Override
   public void b(float f2, float f3, int i, int i2) {
      if (this.g != 0) {
         this.i.b(f2, f3, i, i2);
      } else if (this.l.a(i, i2)) {
      }
   }

   @Override
   public void c(float f2, float f3, int i, int i2) {
      if (this.g != 0) {
         this.i.c(f2, f3, i, i2);
      } else {
         this.k.a(i, i2);
         this.l.b(i, i2);
      }
   }

   @Override
   public void a(float f2, float f3, int i, int i2, double d2, double d3) {
      if (this.g != 0) {
         this.i.a(f2, f3, i, i2, d2, d3);
      } else {
         this.k.a(i, i2, d2, d3);
         this.l.a(i, i2, d2, d3);
      }
   }

   @Override
   public void a(float f2) {
      if (this.g != 0) {
         this.i.a(f2);
      } else {
         this.k.a(f2, this.m, this.n);
      }
   }

   @Override
   public boolean a(int i, int i2, int i3) {
      if (this.g != 0) {
         if (this.i.a(i, i2, i3)) {
            return true;
         }
      } else {
         if (this.k.a(i, i2, i3)) {
            return true;
         }

         if (this.l.a(i, i2, i3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean b() {
      return this.g != 0 ? this.i.b() : Bool.from(!this.k.f() && !this.l.d() ? 0 : 1);
   }

   public boolean a(char c2, int i) {
      return this.g == 0 && (this.k.a(c2, i) || this.l.a(c2, i));
   }

   @Override
   public ClickGuiTabType a() {
      return ClickGuiTabType.MARKERS;
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class AddMarkerEntry implements GuiEntry {
      public static int a;
      public static boolean b;

      @Override
      public String a() {
         return "Загадочный маяк";
      }

      @Override
      public boolean b() {
         return MarkerOptions.e();
      }

      @Override
      public void a(boolean z) {
         MarkerOptions.d(z);
      }

      @Override
      public boolean d() {
         return false;
      }

      @Override
      public boolean c() {
         return MarkerOptions.d();
      }

      public static String b(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class MarkerEntrySource implements GuiEntrySource {
      private final List<GuiEntry> c = new ArrayList<>();
      private final ModuleGrid d;
      public static int a;
      public static boolean b;

      public MarkerEntrySource() {
         this.c.add(new MarkersTab.MarkerHeaderEntry());
         this.c.add(new MarkersTab.MarkerToggleEntry("Метка смерти", ModuleRegistry.DEATH_MARKER::isEnabled, v0 -> {
            MarkerOptions.b(v0);
            ModuleRegistry.DEATH_MARKER.setEnabled(v0);
         }));
         this.c.add(new MarkersTab.MarkerToggleEntry("Авто метки, события", MarkerOptions::d, v0 -> MarkerOptions.c(v0)));
         this.c.add(new MarkersTab.AddMarkerEntry());
         this.d = new ModuleGrid(this);
      }

      @Override
      public List<? extends GuiEntry> e() {
         return this.c.stream().filter(v0 -> v0.c()).collect(Collectors.toList());
      }

      @Override
      public int f() {
         return 1;
      }

      public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
         this.d.a(MatrixStackVar, renderer2D, f, f2, i, i2);
      }

      public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2) {
         this.d.a(MatrixStackVar, renderer2D, i, i2);
      }

      public void a(float f, float f2, int i, int i2) {
         this.d.a(f, f2, i, i2);
      }

      public void b(float f, float f2, int i, int i2) {
         this.d.b(f, f2, i, i2);
      }

      public void c(float f, float f2, int i, int i2) {
         this.d.c(f, f2, i, i2);
      }

      public void a(float f, float f2, int i, int i2, double d, double d2) {
         this.d.a(f, f2, i, i2, d, d2);
      }

      public void a(float f) {
         this.d.a(f);
      }

      public void a() {
         this.d.d();
      }

      public boolean a(int i, int i2, int i3) {
         return this.d.a(i, i2, i3);
      }

      public boolean b() {
         return this.d.c();
      }

      public void c() {
         this.d.g();
      }

      public static String b(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class MarkerHeaderEntry implements GuiEntry {
      public static int a;
      public static boolean b;

      @Override
      public String a() {
         return "Быстрая метка";
      }

      @Override
      public boolean b() {
         return MarkerOptions.a();
      }

      @Override
      public void a(boolean z) {
         MarkerOptions.a(z);
      }

      @Override
      public boolean d() {
         return true;
      }

      @Override
      public int f() {
         return MarkerOptions.b();
      }

      @Override
      public void a(int i) {
         MarkerOptions.a(i);
      }

      @Override
      public boolean h() {
         return false;
      }

      public static String b(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class MarkerToggleEntry implements GuiEntry {
      private final String c;
      private final Supplier<Boolean> d;
      private final Consumer<Boolean> e;
      public static int a;
      public static boolean b;

      public MarkerToggleEntry(String str, Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
         this.c = str;
         this.d = supplier;
         this.e = consumer;
      }

      @Override
      public String a() {
         return this.c;
      }

      @Override
      public boolean b() {
         return this.d.get();
      }

      @Override
      public void a(boolean z) {
         this.e.accept(z);
      }

      @Override
      public boolean d() {
         return false;
      }

      public static String b(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
