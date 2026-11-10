package pulse.gui.notifications;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.widgets.ScrollBar;
import pulse.gui.widgets.SearchBox;
import pulse.hud.core.HudServiceRegistry;
import pulse.hud.notifications.Notification;
import pulse.hud.notifications.NotificationStore;
import pulse.markers.MapMarker;
import pulse.markers.MapMarkerModule;
import pulse.markers.MarkerManager;
import pulse.render.Renderer2D;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class NotificationListPanel {
   public static final float a = 115.5F;
   private static final float b = 115.5F;
   private static final float c = 134.5F;
   private static final float d = 7.5F;
   private static final float e = 5.0F;
   private static final float f = 10.0F;
   private static final float g = 4.0F;
   private static final float h = 19.5F;
   private static final float i = 5.0F;
   private static final float j = 19.5F;
   private static final float k = 6.5F;
   private static final float l = 5.0F;
   private static final float m = 2.0F;
   private static final Color n = Theme.I;
   private static final Color o = Theme.K;
   private static final Color p = Theme.O;
   private final ScrollBar u;
   private final PanelFadeOverlay v;
   private Consumer<Notification> A;
   private Consumer<Notification> B;
   private float C;
   private float D;
   private float E;
   private float F;
   private float G;
   private final List<Notification> q = new ArrayList<>();
   private final List<NotificationCard> r = new ArrayList<>();
   private Notification s = null;
   private final AnimationState w = new AnimationState();
   private boolean x = false;
   private String y = "";
   private List<NotificationCard> z = new ArrayList<>();
   private final SearchBox t = new SearchBox(91.0F, 19.5F);

   public NotificationListPanel() {
      this.t.a(this::a);
      this.u = new ScrollBar(2.0F, 20.0F);
      this.u.b(10.0F);
      this.u.a(Theme.b);
      this.u.b(Theme.d);
      this.v = new PanelFadeOverlay(25, 5.0F, 7.0F);
   }

   public void a(Notification notification) {
      this.q.add(notification);
      this.r.add(new NotificationCard(notification));
      NotificationStore.a(notification);
      this.i();
   }

   public Notification b(Notification notification) {
      int iIndexOf = this.q.indexOf(notification);
      if (iIndexOf < 0) {
         return null;
      }

      this.q.remove(iIndexOf);
      this.r.remove(iIndexOf);
      NotificationStore.b(notification);
      MapMarker mapMarker = notification.marker();
      if (mapMarker == null) {
         mapMarker = MarkerManager.a(notification.b(), notification.c(), notification.d());
      }

      if (mapMarker != null) {
         MarkerManager.b(mapMarker);
      }

      Notification notification2 = null;
      if (this.s == notification) {
         this.s = null;
         if (!this.q.isEmpty()) {
            notification2 = iIndexOf > 0 ? this.q.get(iIndexOf - 1) : this.q.get(0);
         }
      }

      this.i();
      float f2 = this.G - 10.0F;
      float fJ = this.j();
      if (fJ <= f2) {
         this.u.e();
      } else {
         this.u.b(fJ, f2);
      }

      return notification2;
   }

   public List<Notification> a() {
      return this.q;
   }

   public Notification b() {
      return this.s;
   }

   public void c(Notification notification) {
      this.s = notification;

      for (NotificationCard notificationCard : this.r) {
         notificationCard.a(Bool.from(notificationCard.a() == notification ? 1 : 0));
      }
   }

   public Notification c() {
      return this.q.isEmpty() ? null : this.q.get(0);
   }

   public Notification d() {
      return this.s != null && this.q.contains(this.s) ? this.s : this.c();
   }

   public void a(Consumer<Notification> consumer) {
      this.A = consumer;
   }

   public void b(Consumer<Notification> consumer) {
      this.B = consumer;
   }

   private void a(String str) {
      this.y = str.toLowerCase().trim();
      this.i();
      this.u.e();
   }

   public void e() {
      NotificationStore.syncWithMarkers();
      List<Notification> listA = NotificationStore.a();

      for (Notification notification : listA) {
         boolean z = false;
         Iterator<Notification> it = this.q.iterator();

         while (it.hasNext()) {
            if (it.next() == notification) {
               z = true;
               break;
            }
         }

         if (!z) {
            this.q.add(notification);
            this.r.add(new NotificationCard(notification));
         }
      }

      this.q.removeIf(notification2 -> {
         boolean zContains = listA.contains(notification2);
         if (!zContains) {
            this.r.removeIf(notificationCard -> Bool.from(notificationCard.a() == notification2 ? 1 : 0));
            if (this.s == notification2) {
               this.s = null;
            }
         }

         return Bool.from(!zContains ? 1 : 0);
      });
      this.i();
      if (this.s != null) {
         boolean z2 = false;
         Iterator<NotificationCard> it2 = this.z.iterator();

         while (it2.hasNext()) {
            if (it2.next().a() == this.s) {
               z2 = true;
               break;
            }
         }

         if (z2) {
            return;
         }

         this.s = null;
         if (this.A != null) {
            this.A.accept(null);
         }
      }
   }

   private void i() {
      List<NotificationCard> baseList = this.y.isEmpty()
         ? new ArrayList<>(this.r)
         : this.r.stream().filter(notificationCard -> notificationCard.a().a().toLowerCase().contains(this.y)).collect(Collectors.toList());
      List<NotificationCard> list = baseList.stream().filter(notificationCard2 -> this.d(notificationCard2.a())).collect(Collectors.toList());
      list.sort((notificationCard3, notificationCard4) -> {
         boolean zJ = notificationCard3.a().j();
         if (zJ != notificationCard4.a().j()) {
            return zJ ? -1 : 1;
         } else {
            return Long.compare(notificationCard3.a().k(), notificationCard4.a().k());
         }
      });
      this.z = list;
   }

   private boolean d(Notification notification) {
      if (notification.j()) {
         MapMarkerModule mapMarkerModule = HudServiceRegistry.MAP_MARKER_MODULE;
         if (HudServiceRegistry.MAP_MARKER_MODULE != null) {
            int iH = mapMarkerModule.h();
            String strI = mapMarkerModule.i();
            MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
            String lowerCase = MinecraftClientVarGetInstance.getCurrentServerEntry() != null ? MinecraftClientVarGetInstance.getCurrentServerEntry().address.toLowerCase() : "";
            if (lowerCase.contains("crypt")) {
               if (notification.l() != iH) {
                  return false;
               }

               if (notification.m() != null && strI != null && !notification.m().equals(strI)) {
                  return false;
               }
            } else if (!lowerCase.contains("crypt") && notification.l() != iH) {
               return false;
            }

            return true;
         }
      }

      return true;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5) {
      this.C = f2;
      this.D = f3;
      this.E = 115.5F;
      this.F = f4;
      this.G = f4;
      this.u.a();
      this.w.a();
      float listFullH = f4 - 43.5F;
      float listH = listFullH * 0.8F;
      float listTopY = f3 + 43.5F + (listFullH - listH);
      this.a(MatrixStackVar, renderer2D, f2, listTopY, 115.5F, listH, f5);
      this.t.a(MatrixStackVar, renderer2D, f2, f3 + 46.5F, 91.0F, 19.5F, i2, i3);
      this.a(MatrixStackVar, renderer2D, f2 + 91.0F + 5.0F, f3 + 46.5F, i2, i3, f5);
      this.a(MatrixStackVar, renderer2D, f2, listTopY, 115.5F, listH - 10.0F, i2, i3, f5);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, float f6) {
      Color colorB = Theme.b(Theme.m, f6);
      Color colorB2 = Theme.b(Theme.n, f6);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, f5 + 1.0F, 7.5F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      int i2 = (int)(255.0F * f6);
      Color colorA = Theme.a(Theme.e, i2);
      Color colorA2 = Theme.a(Theme.f, i2);
      renderer2D.a(f2, f3, f4, f5, 7.5F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, float f5, int i2, int i3, float f6) {
      float f7 = f2 + 5.0F;
      float f8 = f3 + 5.0F;
      float f9 = f4 - 10.0F;
      float f10 = f5 - 10.0F;
      float fJ = this.j();
      boolean z = fJ > f10;
      if (!z) {
         this.u.e();
      }

      renderer2D.b().a(f2, f3 + 4.0F, f4, f5 - 8.0F, MatrixStackVar);
      float fB = f8 - this.u.b();

      for (NotificationCard notificationCard : this.z) {
         if (fB + 27.5F >= f8 && fB <= f8 + f10) {
            notificationCard.a(MatrixStackVar, renderer2D, f7, fB, f9, i2, i3, f6);
         }

         fB += 32.5F;
      }

      renderer2D.b().a(MatrixStackVar);
      if (z) {
         this.u.a(MatrixStackVar, renderer2D, f2 + f4 - 2.0F + 1.0F, f8, f10, fJ, f10, i2, i3, false);
      }

      this.v.a(MatrixStackVar, renderer2D, f2, f3 + 1.5F, f4, f5, f6);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3, float f4) {
      int i4 = (int)(255.0F * f4);
      boolean zA = GuiInput.a(f2, f3, 19.5F, 19.5F, i2, i3);
      if (zA != this.x) {
         this.w.a(zA ? 1.0 : 0.0, 0.15, Easing.h);
         this.x = zA;
      }

      float fJ = (float)this.w.j();
      Color colorB = Theme.b(Theme.q, f4);
      Color colorB2 = Theme.b(Theme.n, f4);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 20.5F, 20.5F, 6.5F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = ColorUtils.a(Theme.f, n, fJ);
      Color colorA2 = ColorUtils.a(Theme.e, o, fJ);
      Color colorA3 = Theme.a(colorA, i4);
      Color colorA4 = Theme.a(colorA2, i4);
      renderer2D.a(f2, f3, 19.5F, 19.5F, 6.5F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      Color colorA5 = Theme.a(ColorUtils.a(Theme.b, p, fJ), i4);
      float f5 = f2 + 7.25F;
      float f6 = f3 + 7.25F;
      renderer2D.a(f5, f6 + 2.5F - 0.75F, 5.0F, 1.5F, colorA5, MatrixStackVar);
      renderer2D.a(f5 + 2.5F - 0.75F, f6, 1.5F, 5.0F, colorA5, MatrixStackVar);
      if (zA) {
         GuiInput.g();
      }
   }

   private float j() {
      return this.z.isEmpty() ? 0.0F : this.z.size() * 32.5F - 5.0F;
   }

   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      if (this.t.a(i2, i3)) {
         return true;
      }

      float f5 = f3 + 46.5F + 3.0F;
      if (GuiInput.a(f2 + 91.0F + 5.0F, f5 - 3.0F, 19.5F, 19.5F, i2, i3)) {
         this.k();
         return true;
      }

      float listFullH = f4 - 43.5F;
      float listH = listFullH * 0.8F;
      float listTopY = f3 + 43.5F + (listFullH - listH);
      float f8 = listTopY + 5.0F;
      float f9 = listH - 10.0F - 10.0F;
      float fJ = this.j();
      if (fJ > f9 && this.u.a(f2 + 115.5F - 2.0F + 1.0F, f8, f9, fJ, f9, i2, i3)) {
         return true;
      }

      float f10 = f2 + 5.0F;
      if (GuiInput.a(f10, f8, 101.5F, f9, i2, i3)) {
         float fB = f8 - this.u.b();

         for (NotificationCard notificationCard : this.z) {
            if (notificationCard.a(f10, fB, 101.5F, i2, i3)) {
               if (notificationCard.c()) {
                  this.c(notificationCard.a());
                  if (this.A != null) {
                     this.A.accept(notificationCard.a());
                  }
               }

               return true;
            }

            fB += 32.5F;
         }
      }

      return false;
   }

   private void k() {
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      int iGetX = 0;
      int iGetY = 64;
      int iGetZ = 0;
      if (MinecraftClientVarGetInstance.player != null) {
         iGetX = (int)MinecraftClientVarGetInstance.player.getX();
         iGetY = (int)MinecraftClientVarGetInstance.player.getY();
         iGetZ = (int)MinecraftClientVarGetInstance.player.getZ();
      }

      Notification notification = new Notification("Метка", iGetX, iGetY, iGetZ, Theme.y, Notification.Icon.HOME);
      MapMarker mapMarker = new MapMarker(notification.a(), iGetX, iGetY, iGetZ, notification.e(), mapMarkerIconOf(notification.f()));
      MarkerManager.a(mapMarker);
      NotificationStore.link(notification, mapMarker);
      this.a(notification);
      this.c(notification);
      if (this.B != null) {
         this.B.accept(notification);
      }

      if (this.A != null) {
         this.A.accept(notification);
      }
   }

   public void a(int i2, int i3) {
      this.u.d();
   }

   public void a(int i2, int i3, double d2, double d3) {
      if (this.u.c()) {
         float f2 = this.G - 10.0F;
         this.u.a(i3, this.j(), f2);
      }
   }

   public void a(float f2, int i2, int i3) {
      float f3 = this.D + 10.0F + 4.0F + 19.5F + 5.0F;
      float f4 = this.E - 38.5F;
      if (GuiInput.a(this.C, f3, 115.5F, f4, i2, i3)) {
         this.u.a(f2, this.j(), f4 - 10.0F);
      }
   }

   public boolean a(int i2, int i3, int i4) {
      return this.t.c() ? this.t.a(i2, i3, i4) : false;
   }

   public boolean a(char c2, int i2) {
      return this.t.c() ? this.t.a(c2, i2) : false;
   }

   public boolean f() {
      return this.t.c();
   }

   public boolean g() {
      return this.q.isEmpty();
   }

   public static float h() {
      return 38.5F;
   }

   private static MapMarker.Icon mapMarkerIconOf(Notification.Icon icon) {
      if (icon == null) {
         return MapMarker.Icon.EVENT;
      }

      try {
         return MapMarker.Icon.valueOf(icon.name());
      } catch (IllegalArgumentException ex) {
         return MapMarker.Icon.EVENT;
      }
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
