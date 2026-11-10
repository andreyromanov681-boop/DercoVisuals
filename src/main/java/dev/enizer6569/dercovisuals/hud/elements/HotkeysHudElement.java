// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.hud.core.HudElement;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;

public class HotkeysHudElement extends HudElement {
   static final Color o = new Color(13, 13, 17);
   static final Color p = new Color(17, 17, 23);
   private static final Color q = new Color(255, 255, 255);
   private static final Color r = new Color(223, 223, 243);
   private static final Color s = new Color(20, 20, 28);
   private static final Color t = new Color(16, 16, 21);
   private static final Color u = new Color(17, 17, 23);
   private static final Color v = new Color(23, 23, 30);
   private static final Color w = new Color(18, 18, 24);
   private static final Color x = new Color(115, 83, 255);
   private static final Color y = new Color(70, 46, 174);
   private static final float z = 8.5F;
   private static final float A = 8.0F;
   private static final float B = 24.0F;
   private static final float C = 16.0F;
   private static final float D = 5.0F;
   private static final float E = 12.0F;
   private static final float F = 2.0F;
   private static final float G = 12.0F;
   private static final float H = 6.0F;
   private static final float I = 6.5F;
   private static final float J = 0.5F;
   private static final float K = 82.0F;
   private static final double L = 0.2;
   private static final long M = 1000L;
   private final Map<ClientModule, HotkeysHudElement.ActiveHotkeyEntry> N = new LinkedHashMap<>();
   private final List<HotkeysHudElement.HotkeyPlaceholder> O = new ArrayList<>();
   private final AnimationState P = new AnimationState();
   private final AnimationState Q = new AnimationState();
   private final AnimationState R = new AnimationState();
   private long S = 0L;
   private int T = 0;
   private boolean U = false;
   private boolean V = false;
   private boolean W = false;
   public static boolean n;

   public HotkeysHudElement(float f, float f2) {
      super(f, f2);
      this.R.d(0.0);
      this.u();
      this.a();
   }

   private void u() {
      this.O.add(new HotkeysHudElement.HotkeyPlaceholder("Трейлы", "  G  "));
      this.O.add(new HotkeysHudElement.HotkeyPlaceholder("Быстрый свал", "  O  "));
      this.O.add(new HotkeysHudElement.HotkeyPlaceholder("ПВП Сейф", "  B  "));
   }

   private boolean v() {
      return Bool.from((this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen) && this.x().isEmpty() ? 1 : 0);
   }

   private void w() {
      if (!this.W && ModuleRegistry.HOTKEYS_HUD != null) {
         this.f().a(ModuleRegistry.HOTKEYS_HUD);
         this.W = true;
      }
   }

   @Override
   protected void a() {
      this.w();
      boolean zV = this.v();
      List<HotkeysHudElement.HotkeySnapshot> listX = this.x();
      float fG = this.g();
      float f2 = 8.5F * fG;
      float f3 = 24.0F * fG;
      float f4 = 5.0F * fG;
      float f5 = 12.0F * fG;
      float f6 = 2.0F * fG;
      float f7 = 6.5F * fG;
      float f8 = 0.5F * fG;
      float f9 = 82.0F * fG;
      int iRound = Math.round(15.0F * fG);
      int iRound2 = Math.round(14.0F * fG);
      FontRenderer fontRenderer = FontManager.a[Math.max(10, Math.min(48, iRound))];
      FontRenderer fontRenderer2 = FontManager.a[Math.max(10, Math.min(48, iRound2))];
      if (zV) {
         long jCurrentTimeMillis = System.currentTimeMillis();
         if (jCurrentTimeMillis - this.S >= 1000L) {
            this.T = (this.T - -2 - 1) % this.O.size();
            this.S = jCurrentTimeMillis;
         }
      }

      HashSet hashSet = new HashSet();
      Iterator<HotkeysHudElement.HotkeySnapshot> it = listX.iterator();

      while (it.hasNext()) {
         hashSet.add(it.next().a);
      }

      if (n) {
         throw new ExceptionInInitializerError();
      }

      for (HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry : this.N.values()) {
         if (!hashSet.contains(activeHotkeyEntry.a) && !activeHotkeyEntry.f) {
            activeHotkeyEntry.f = true;
            activeHotkeyEntry.d.a(0.0, 0.2, Easing.h);
         }
      }

      int i = 0;

      for (HotkeysHudElement.HotkeySnapshot hotkeySnapshot : listX) {
         HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry2 = this.N.get(hotkeySnapshot.a);
         if (activeHotkeyEntry2 == null) {
            HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry3 = new HotkeysHudElement.ActiveHotkeyEntry(
               hotkeySnapshot.a, hotkeySnapshot.b, hotkeySnapshot.c
            );
            activeHotkeyEntry3.d.d(0.0);
            activeHotkeyEntry3.d.a(1.0, 0.2, Easing.h);
            activeHotkeyEntry3.e.d(i);
            this.N.put(hotkeySnapshot.a, activeHotkeyEntry3);
         } else {
            activeHotkeyEntry2.b = hotkeySnapshot.b;
            activeHotkeyEntry2.c = hotkeySnapshot.c;
            activeHotkeyEntry2.f = false;
            if (activeHotkeyEntry2.d.i() < 1.0) {
               activeHotkeyEntry2.d.a(1.0, 0.2, Easing.h);
            }

            if (Math.abs(activeHotkeyEntry2.e.i() - i) > 0.01) {
               activeHotkeyEntry2.e.a(i, 0.2, Easing.h);
            }
         }

         i++;
      }

      this.N.entrySet().removeIf(entry -> entry.getValue().f && !(entry.getValue().d.j() >= 0.01));

      for (HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry4 : this.N.values()) {
         activeHotkeyEntry4.d.a();
         activeHotkeyEntry4.e.a();
      }

      boolean z2 = this.V;
      this.V = false;
      int i2 = 0;
      if (zV) {
         this.V = true;
      } else {
         Iterator<HotkeysHudElement.ActiveHotkeyEntry> it2 = this.N.values().iterator();

         while (it2.hasNext()) {
            if (!it2.next().f) {
               this.V = true;
               i2++;
            }
         }
      }

      if (this.V && !z2) {
         this.R.a(1.0, 0.2, Easing.h);
      } else if (!this.V && z2) {
         this.R.a(0.0, 0.2, Easing.h);
      } else if (this.V && this.R.i() < 1.0) {
         this.R.a(1.0, 0.2, Easing.h);
      }

      this.R.a();
      float fA = 0.0F;
      if (zV) {
         HotkeysHudElement.HotkeyPlaceholder hotkeyPlaceholder = this.O.get(this.T);
         fA = fontRenderer.a(hotkeyPlaceholder.a) + 8.0F * fG + fontRenderer2.a(hotkeyPlaceholder.b) + f7 * 2.0F + f8 * 2.0F;
      } else {
         for (HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry5 : this.N.values()) {
            if (!activeHotkeyEntry5.f) {
               fA = Math.max(fA, fontRenderer.a(activeHotkeyEntry5.b) + 8.0F * fG + fontRenderer2.a(activeHotkeyEntry5.c) + f7 * 2.0F + f8 * 2.0F);
            }
         }
      }

      float fMax = f2 * 2.0F + Math.max(f9, fA);
      float f10 = 1.0F * fG;
      float f;
      if (i2 == 0) {
         f = 0.0F;
      } else {
         f = (i2 - 1) * (f5 + f6);
      }

      float f12 = f3 + f10 + f4 + f + f4 + 2.0F * fG + 13.0F * fG;
      if (this.U) {
         if (Math.abs(this.Q.i() - fMax) > 0.5) {
            this.Q.a(fMax, 0.2, Easing.h);
         }

         if (Math.abs(this.P.i() - f12) > 0.5) {
            this.P.a(f12, 0.2, Easing.h);
         }
      } else {
         this.Q.d(fMax);
         this.P.d(f12);
         this.U = true;
      }

      this.Q.a();
      this.P.a();
      this.d = (float)this.Q.j();
      this.e = (float)this.P.j();
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      if (this.a.player != null) {
         this.a();
         float fJ = (float)this.R.j();
         if (fJ >= 0.01F) {
            boolean zV = this.v();
            float fG = this.g();
            float f4 = 8.5F * fG;
            float f5 = 8.0F * fG;
            float f6 = 24.0F * fG;
            float f7 = 16.0F * fG;
            float f8 = 5.0F * fG;
            float f9 = 12.0F * fG;
            float f10 = 2.0F * fG;
            int iRound = Math.round(16.0F * fG);
            int iRound2 = Math.round(15.0F * fG);
            int iRound3 = Math.round(14.0F * fG);
            boolean z2 = fJ < 0.99F;
            if (z2) {
               renderer2D.c(this.b, this.c, this.d, this.e, MatrixStackVar);
            }

            float f11 = !z2 ? fJ : 1.0F;
            FontRenderer fontRenderer = FontManager.b[Math.max(10, Math.min(48, iRound))];
            FontRenderer fontRenderer2 = FontManager.a[Math.max(10, Math.min(48, iRound2))];
            FontRenderer fontRenderer3 = FontManager.a[Math.max(10, Math.min(48, iRound3))];
            Color colorA = this.a(o, f11);
            Color colorA2 = this.a(p, f11);
            renderer2D.a(this.b, this.c, this.d, this.e, f5, colorA, colorA, colorA2, colorA2, MatrixStackVar);
            float f12 = this.b + f4;
            float f13 = this.c + f6 / 2.0F;
            Identifier keyboardIcon = IconTextureRegistry.get("keyboard");
            float iconSize = 12.0F * fG;
            renderer2D.a(keyboardIcon, f12 + (f7 - iconSize) / 2.0F - 4.0F, f13 - iconSize / 2.0F + 0.5F, iconSize, iconSize, this.a(x, f11), MatrixStackVar);
            fontRenderer.a("Клавиши", f12 + f7 - 2.0F * fG, f13 - fontRenderer.b("Клавиши") / 4.0F + 1.0F * fG, this.a(q, f11), MatrixStackVar);
            float f14 = this.c + f6;
            renderer2D.a(this.b, f14, this.d, 1.0F * fG, 0.0F, this.a(s, f11), MatrixStackVar);
            renderer2D.b().a(this.b, f14 + 1.0F * fG, this.d, this.e - f6 - 1.0F * fG, 0.0F, MatrixStackVar);
            float f15 = f14 + 1.0F * fG + f8;
            if (zV) {
               this.a(MatrixStackVar, renderer2D, fontRenderer2, fontRenderer3, f12, f15, this.O.get(this.T), f11, fG);
            } else {
               for (HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry : this.N.values()) {
                  float fJ2 = (float)activeHotkeyEntry.d.j();
                  float f3;
                  if (z2) {
                     f3 = fJ2;
                  } else {
                     f3 = fJ2 * fJ;
                  }

                  float f16 = f3;
                  if (f16 >= 0.01F) {
                     this.a(
                        MatrixStackVar,
                        renderer2D,
                        fontRenderer2,
                        fontRenderer3,
                        f12,
                        f15 + (float)activeHotkeyEntry.e.j() * (f9 + f10),
                        activeHotkeyEntry,
                        f16,
                        fG
                     );
                  }
               }
            }

            renderer2D.b().a(MatrixStackVar);
            if (z2) {
               renderer2D.a(fJ, MatrixStackVar);
            }
         }
      }
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      HotkeysHudElement.HotkeyPlaceholder hotkeyPlaceholder,
      float f3,
      float f4
   ) {
      float f5 = 8.5F * f4;
      float f6 = 6.5F * f4;
      float f7 = 0.5F * f4;
      float f8 = 12.0F * f4;
      float f9 = 6.0F * f4;
      float f10 = f2 + 12.0F * f4 / 2.0F;
      Color colorA = this.a(r, f3);
      fontRenderer.a(hotkeyPlaceholder.a, f, f10 - fontRenderer.b(hotkeyPlaceholder.a) / 4.0F, colorA, MatrixStackVar);
      float fA = fontRenderer2.a(hotkeyPlaceholder.b) + f6 * 2.0F + f7 * 2.0F - 2.0F * f4;
      float f11 = this.b + this.d - f5 - fA;
      float f12 = f10 - f8 / 2.0F - f7;
      float f13 = f8 + f7 * 2.0F;
      Color colorA2 = this.a(v, f3);
      Color colorA3 = this.a(w, f3);
      Color colorA4 = this.a(t, f3);
      Color colorA5 = this.a(u, f3);
      renderer2D.a(f11, f12, fA, f13, f9, colorA2, colorA2, colorA3, colorA3, MatrixStackVar);
      renderer2D.a(f11 + f7, f12 + f7, fA - f7 * 2.0F, f8, f9 - f7, colorA4, colorA4, colorA5, colorA5, MatrixStackVar);
      fontRenderer2.a(
         hotkeyPlaceholder.b,
         f11 + (fA - fontRenderer2.a(hotkeyPlaceholder.b)) / 2.0F,
         f10 - fontRenderer2.b(hotkeyPlaceholder.b) / 4.0F,
         colorA,
         MatrixStackVar
      );
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      HotkeysHudElement.ActiveHotkeyEntry activeHotkeyEntry,
      float f3,
      float f4
   ) {
      float f5 = 8.5F * f4;
      float f6 = 6.5F * f4;
      float f7 = 0.5F * f4;
      float f8 = 12.0F * f4;
      float f9 = 6.0F * f4;
      float f10 = f2 + 12.0F * f4 / 2.0F;
      Color colorA = this.a(r, f3);
      fontRenderer.a(activeHotkeyEntry.b, f, f10 - fontRenderer.b(activeHotkeyEntry.b) / 4.0F, colorA, MatrixStackVar);
      float fA = fontRenderer2.a(activeHotkeyEntry.c) + f6 * 2.0F + f7 * 2.0F - 2.0F * f4;
      float f11 = this.b + this.d - f5 - fA;
      float f12 = f10 - f8 / 2.0F - f7;
      float f13 = f8 + f7 * 2.0F;
      Color colorA2 = this.a(v, f3);
      Color colorA3 = this.a(w, f3);
      Color colorA4 = this.a(t, f3);
      Color colorA5 = this.a(u, f3);
      renderer2D.a(f11, f12, fA, f13, f9, colorA2, colorA2, colorA3, colorA3, MatrixStackVar);
      renderer2D.a(f11 + f7, f12 + f7, fA - f7 * 2.0F, f8, f9 - f7, colorA4, colorA4, colorA5, colorA5, MatrixStackVar);
      fontRenderer2.a(
         activeHotkeyEntry.c,
         f11 + (fA - fontRenderer2.a(activeHotkeyEntry.c)) / 2.0F,
         f10 - fontRenderer2.b(activeHotkeyEntry.c) / 4.0F,
         colorA,
         MatrixStackVar
      );
   }

   private Color a(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f))));
   }

   private List<HotkeysHudElement.HotkeySnapshot> x() {
      ArrayList arrayList = new ArrayList();

      for (ClientModule clientModule : ModuleRegistry.all()) {
         if (clientModule.k() && clientModule.j() != 0) {
            arrayList.add(new HotkeysHudElement.HotkeySnapshot(clientModule, clientModule.g(), "  " + this.b(clientModule.j()) + "  "));
         }
      }

      arrayList.sort(Comparator.comparing(hotkeySnapshot -> hotkeySnapshot.b));
      return arrayList;
   }

   private String b(int i) {
      if (i == 0) {
         return "Нет";
      }

      if (i < 8) {
         switch (i) {
            case 0:
               return "ЛКМ";
            case 1:
               return "ПКМ";
            case 2:
               return "СКМ";
            default:
               return "M" + i;
         }
      } else {
         switch (i) {
            case 32:
               return "Пробел";
            case 39:
               return "'";
            case 44:
               return ",";
            case 45:
               return "-";
            case 46:
               return ".";
            case 47:
               return "/";
            case 59:
               return ";";
            case 61:
               return "=";
            case 91:
               return "[";
            case 92:
               return "\\";
            case 93:
               return "]";
            case 96:
               return "`";
            case 256:
               return "Esc";
            case 257:
               return "Enter";
            case 258:
               return "Tab";
            case 259:
               return "Backspace";
            case 260:
               return "Ins";
            case 261:
               return "Del";
            case 262:
               return "Вправо";
            case 263:
               return "Влево";
            case 264:
               return "Вниз";
            case 265:
               return "Вверх";
            case 266:
               return "PgUp";
            case 267:
               return "PgDn";
            case 268:
               return "Home";
            case 269:
               return "End";
            case 280:
               return "Caps";
            case 281:
               return "Scroll";
            case 282:
               return "Num";
            case 283:
               return "Print";
            case 284:
               return "Pause";
            case 340:
               return "LShift";
            case 341:
               return "LCtrl";
            case 342:
               return "LAlt";
            case 343:
               return "LWin";
            case 344:
               return "RShift";
            case 345:
               return "RCtrl";
            case 346:
               return "RAlt";
            case 347:
               return "RWin";
            case 348:
               return "Меню";
            default:
               if (i >= 290 && i <= 301) {
                  return "F" + (i - 290 + 1);
               } else if (i >= 302 && i <= 314) {
                  return "F" + (i - 302 + 13);
               } else if (i >= 320 && i <= 329) {
                  return "Num" + (i - 320);
               } else {
                  switch (i) {
                     case 330:
                        return "Num.";
                     case 331:
                        return "Num/";
                     case 332:
                        return "Num*";
                     case 333:
                        return "Num-";
                     case 334:
                        return "Num+";
                     case 335:
                        return "NumEnter";
                     case 336:
                        return "Num=";
                     default:
                        return i >= 48 && i <= 57 ? String.valueOf((char)i) : (i >= 65 && i <= 90 ? String.valueOf((char)i) : "Кл." + i);
                  }
               }
         }
      }
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class ActiveHotkeyEntry {
      final ClientModule a;
      String b;
      String c;
      final AnimationState d = new AnimationState();
      final AnimationState e = new AnimationState();
      boolean f = false;
      public static int g;
      public static boolean h;

      ActiveHotkeyEntry(ClientModule clientModule, String str, String str2) {
         this.a = clientModule;
         this.b = str;
         this.c = str2;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class HotkeyPlaceholder {
      final String a;
      final String b;
      public static int c;
      public static boolean d;

      HotkeyPlaceholder(String str, String str2) {
         this.a = str;
         this.b = str2;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class HotkeySnapshot {
      final ClientModule a;
      final String b;
      final String c;
      public static int d;
      public static boolean e;

      HotkeySnapshot(ClientModule clientModule, String str, String str2) {
         this.a = clientModule;
         this.b = str;
         this.c = str2;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
