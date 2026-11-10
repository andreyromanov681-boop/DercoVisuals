package pulse.gui.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.settings.TokenSetting;
import pulse.theme.Theme;
import pulse.util.ColorUtils;
import pulse.util.MarqueeText;

public class TokenSettingWidget implements SettingWidget {
   private static final Set<TokenSettingWidget> d = Collections.newSetFromMap(new WeakHashMap<>());
   public static final float a = 20.0F;
   private static final float e = 8.0F;
   private static final float f = 40.0F;
   private static final float g = 100.0F;
   private static final float h = 14.0F;
   private static final float i = 4.0F;
   private static final float j = 5.0F;
   private static final int k = 16;
   private final String l;
   private final TokenSettingWidget.ValueType m;
   private String n = "";
   private String o = "";
   private final TokenSetting p;
   private boolean q = false;
   private int r = 0;
   private int s = -1;
   private int t = -1;
   private final AnimationState u = new AnimationState();
   private final AnimationState v = new AnimationState();
   private final AnimationState w = new AnimationState();
   private final AnimationState x = new AnimationState();
   private final MarqueeText y = new MarqueeText();
   private boolean z = false;
   private boolean A = true;
   private float B;
   private float C;
   private float D;
   private float E = 1.0F;
   private float F;
   private String G = "";
   public static int b;
   public static boolean c;

   public TokenSettingWidget(TokenSetting tokenSetting, TokenSettingWidget.ValueType valueType) {
      this.p = tokenSetting;
      this.l = tokenSetting.f();
      this.m = valueType;
      this.o = tokenSetting.c();
      this.w.d(1.0);
      this.F = 40.0F;
      this.x.d(40.0);
      d.add(this);
      this.a(tokenSetting.a());
   }

   public TokenSettingWidget(String str, TokenSettingWidget.ValueType valueType) {
      this.p = null;
      this.l = str;
      this.m = valueType;
      this.w.d(1.0);
      this.F = 40.0F;
      this.x.d(40.0);
      d.add(this);
   }

   public TokenSettingWidget(String str, TokenSettingWidget.ValueType valueType, String str2) {
      this(str, valueType);
      this.o = str2;
   }

   public TokenSettingWidget(String str, TokenSettingWidget.ValueType valueType, String str2, String str3) {
      this(str, valueType, str3);
      this.a(str2);
   }

   @Override
   public String a() {
      return this.l;
   }

   @Override
   public float b() {
      return 20.0F;
   }

   public TokenSettingWidget.ValueType c() {
      return this.m;
   }

   public String e() {
      return this.n;
   }

   public String f() {
      switch (this.m) {
         case COMMAND:
            return "/" + this.n;
         case PRICE:
            return "$" + this.n;
         default:
            return this.n;
      }
   }

   public String g() {
      return this.m != TokenSettingWidget.ValueType.PRICE ? this.n : this.n.replace(",", "");
   }

   public int h() {
      if (this.m == TokenSettingWidget.ValueType.INT) {
         try {
            return Integer.parseInt(this.n);
         } catch (NumberFormatException e2) {
            return 0;
         }
      } else {
         if (this.m != TokenSettingWidget.ValueType.PRICE) {
            return 0;
         }

         try {
            return Integer.parseInt(this.n.replace(",", ""));
         } catch (NumberFormatException e3) {
            return 0;
         }
      }
   }

   public void a(String str) {
      if (str == null) {
         str = "";
      }

      StringBuilder sb = new StringBuilder();

      for (char c2 : str.toCharArray()) {
         if (this.b(c2, sb.length())) {
            sb.append(c2);
         }
      }

      this.n = sb.toString();
      if (this.m == TokenSettingWidget.ValueType.PRICE) {
         this.n = this.b(this.n.replace(",", ""));
      }

      this.r = Math.min(this.r, this.n.length());
      this.H();
      this.t();
      this.q();
   }

   private void q() {
      if (this.p != null) {
         this.p.a(this.n);
      }
   }

   public boolean i() {
      return this.q;
   }

   @Override
   public boolean a_() {
      return this.q;
   }

   private String r() {
      switch (this.m) {
         case COMMAND:
            return "/";
         case PRICE:
            return "$";
         default:
            return "";
      }
   }

   private String s() {
      return this.r() + this.n;
   }

   private boolean b(char c2, int i2) {
      switch (this.m) {
         case COMMAND:
            return c2 >= 'a' && c2 <= 'z' || c2 >= 'A' && c2 <= 'Z' || c2 >= '0' && c2 <= '9' || c2 == ' ';
         case PRICE:
            return c2 >= '0' && c2 <= '9';
         case PLAYER:
            return c2 >= 'a' && c2 <= 'z' || c2 >= 'A' && c2 <= 'Z' || c2 >= '0' && c2 <= '9' || c2 == '_';
         case INT:
            if (c2 >= '0' && c2 <= '9' || c2 == '-' && i2 == 0 && !this.n.contains("-")) {
               return true;
            }

            return false;
         default:
            return true;
      }
   }

   private boolean a(char c2) {
      return this.b(c2, this.r);
   }

   private String b(String str) {
      if (str.isEmpty()) {
         return "";
      }

      try {
         long j2 = Long.parseLong(str);
         StringBuilder sb = new StringBuilder();
         String strValueOf = String.valueOf(j2);
         int i2 = 0;

         for (int length = strValueOf.length() - 1; length >= 0; length--) {
            if (i2 > 0) {
               if (i2 % 3 == 0) {
                  sb.insert(0, ",");
               } else if (c) {
               }
            }

            sb.insert(0, strValueOf.charAt(length));
            i2++;
         }

         return sb.toString();
      } catch (NumberFormatException e2) {
         return str;
      }
   }

   private void t() {
      FontRenderer fontRenderer = FontManager.a[14];
      String strS = this.s();
      String str;
      if (strS.isEmpty()) {
         str = this.r() + this.o;
      } else {
         str = strS;
      }

      float fMax = Math.max(40.0F, Math.min(100.0F, fontRenderer.a(str) + 10.0F));
      if (Math.abs(fMax - this.F) > 1.0F) {
         this.x.a(fMax, 0.15, Easing.h);
      }
   }

   private void u() {
      this.G = "";
      if (this.m == TokenSettingWidget.ValueType.PLAYER && !this.n.isEmpty()) {
         List<String> listV = this.v();
         String lowerCase = this.n.toLowerCase(Locale.ROOT);

         for (String str : listV) {
            if (str.toLowerCase(Locale.ROOT).startsWith(lowerCase) && !str.equalsIgnoreCase(this.n)) {
               this.G = str.substring(this.n.length());
               return;
            }
         }
      }
   }

   private List<String> v() {
      ArrayList arrayList = new ArrayList();
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.getNetworkHandler() != null) {
         for (PlayerListEntry PlayerListEntryVar : MinecraftClientVarGetInstance.getNetworkHandler().getPlayerList()) {
            if (PlayerListEntryVar.getProfile() != null && PlayerListEntryVar.getProfile().name() != null) {
               arrayList.add(PlayerListEntryVar.getProfile().name());
            }
         }
      }

      return arrayList;
   }

   private void w() {
      if (!this.G.isEmpty()) {
         this.n = this.n + this.G;
         this.r = this.n.length();
         this.G = "";
         this.H();
         this.t();
      }
   }

   private static void a(TokenSettingWidget tokenSettingWidget) {
      for (TokenSettingWidget tokenSettingWidget2 : d) {
         if (tokenSettingWidget2 != tokenSettingWidget && tokenSettingWidget2.q) {
            tokenSettingWidget2.q = false;
            tokenSettingWidget2.u.a(0.0, 0.2, Easing.h);
            tokenSettingWidget2.G = "";
         }
      }
   }

   public static boolean m() {
      Iterator<TokenSettingWidget> it = d.iterator();

      while (it.hasNext()) {
         if (it.next().q) {
            return true;
         }
      }

      return false;
   }

   public static TokenSettingWidget n() {
      for (TokenSettingWidget tokenSettingWidget : d) {
         if (tokenSettingWidget.q) {
            return tokenSettingWidget;
         }
      }

      return null;
   }

   public static void o() {
      for (TokenSettingWidget tokenSettingWidget : d) {
         if (tokenSettingWidget.q) {
            tokenSettingWidget.q = false;
            tokenSettingWidget.u.d(0.0);
            tokenSettingWidget.G = "";
            tokenSettingWidget.H();
         }
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5, float f6) {
      FontRenderer fontRenderer = FontManager.a[14];
      float f7 = 20.0F * f6;
      float f8 = 8.0F * f6;
      this.E = f6;
      this.y.a(GuiInput.a(f2, f3, f4, f7, i2, i3));
      this.u.a();
      this.v.a();
      this.x.a();
      this.F = (float)this.x.j();
      if (this.q) {
         this.w.a();
         if (this.w.d()) {
            this.A = Bool.from(this.A ? 0 : 1);
            this.w.a(!this.A ? 0.3 : 1.0, 0.5, Easing.i);
         }
      } else {
         this.w.d(1.0);
         this.A = true;
      }

      int i4 = (int)(255.0F * f5);
      float f9 = this.F * f6;
      float f10 = 14.0F * f6;
      float f11 = f2 + f4 - f8 - f9;
      float f12 = f3 + f7 / 2.0F - f10 / 2.0F;
      this.y
         .a(
            MatrixStackVar,
            renderer2D,
            fontRenderer,
            this.l,
            f2 + f8,
            f3 + f7 / 2.0F - fontRenderer.b(this.l) * f6 / 4.0F - 1.0F,
            f11 - (f2 + f8) - 4.0F * f6,
            f6,
            Theme.a,
            f5
         );
      this.B = f11;
      this.C = f12;
      this.D = f9;
      boolean zA = GuiInput.a(f11, f12, f9, f10, i2, i3);
      if (zA != this.z) {
         this.v.a(!zA ? 0.0 : 1.0, 0.15, Easing.h);
         this.z = zA;
      }

      float fJ = (float)this.u.j();
      float fJ2 = (float)this.v.j();
      Color colorA = ColorUtils.a(Theme.m, Theme.y, fJ);
      Color colorA2 = ColorUtils.a(Theme.r, Theme.z, fJ);
      Color colorA3 = Theme.a(colorA, i4);
      Color colorA4 = Theme.a(colorA2, i4);
      renderer2D.a(f11 - 0.5F * f6, f12 - 0.5F * f6, f9 + f6, f10 + f6, 4.0F * f6, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      int i5 = (int)(fJ2 * 3.0F);
      Color colorA5 = Theme.a(Theme.b(Theme.e, i5), i4);
      Color colorA6 = Theme.a(Theme.b(Theme.f, i5), i4);
      renderer2D.a(f11, f12, f9, f10, 4.0F * f6, colorA5, colorA5, colorA6, colorA6, MatrixStackVar);
      float f13 = 5.0F * f6;
      float f14 = f11 + f13;
      renderer2D.b().a(f14 - 1.0F, f12, f9 - f13 * 2.0F + 2.0F, f10, MatrixStackVar);
      String strR = this.r();
      String strS = this.s();
      float fB = f12 + f10 / 2.0F - fontRenderer.b("A") * f6 / 4.0F;
      if (this.n.isEmpty() && !this.q) {
         String str = strR + this.o;
         if (!str.isEmpty()) {
            Color colorA9 = Theme.a(Theme.b, i4);
            MatrixStackVar.pushMatrix();
            MatrixStackVar.translate(f14, fB);
            MatrixStackVar.scale(f6, f6);
            MatrixStackVar.translate(-f14, -fB);
            fontRenderer.a(str, f14, fB, colorA9, MatrixStackVar);
            MatrixStackVar.popMatrix();
         }
      } else {
         if (this.G()) {
            int iMin = Math.min(this.s, this.t);
            int iMax = Math.max(this.s, this.t);
            float fA = fontRenderer.a(strR) * f6;
            float fA2 = f14 + fA + fontRenderer.a(this.n, iMin) * f6;
            renderer2D.a(
               fA2,
               f12 + 2.0F * f6,
               f14 + fA + fontRenderer.a(this.n, iMax) * f6 - fA2,
               f10 - 4.0F * f6,
               2.0F * f6,
               new Color(Theme.A.getRed(), Theme.A.getGreen(), Theme.A.getBlue(), (int)(Theme.A.getAlpha() * f5)),
               MatrixStackVar
            );
         }

         Color colorA7 = Theme.a(Theme.a, i4);
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(f14, fB);
         MatrixStackVar.scale(f6, f6);
         MatrixStackVar.translate(-f14, -fB);
         fontRenderer.a(strS, f14, fB, colorA7, MatrixStackVar);
         MatrixStackVar.popMatrix();
         if (this.m == TokenSettingWidget.ValueType.PLAYER && !this.G.isEmpty() && this.q) {
            float fA3 = f14 + fontRenderer.a(strS) * f6;
            Color colorA8 = Theme.a(Theme.d, i4);
            MatrixStackVar.pushMatrix();
            MatrixStackVar.translate(fA3, fB);
            MatrixStackVar.scale(f6, f6);
            MatrixStackVar.translate(-fA3, -fB);
            fontRenderer.a(this.G, fA3, fB, colorA8, MatrixStackVar);
            MatrixStackVar.popMatrix();
         }

         if (this.q && !this.G()) {
            float fJ3 = (float)this.w.j();
            if (fJ3 > 0.3F) {
               renderer2D.a(
                  f14 + fontRenderer.a(strR) * f6 + fontRenderer.a(this.n, this.r) * f6,
                  f12 + 3.0F * f6,
                  1.0F * f6,
                  f10 - 6.0F * f6,
                  Theme.a(Theme.a, (int)(255.0F * f5 * fJ3)),
                  MatrixStackVar
               );
            }
         }
      }

      if (this.n.isEmpty() && this.q && !this.G()) {
         float fJ4 = (float)this.w.j();
         float fA4 = fontRenderer.a(strR) * f6;
         Color colorA10 = Theme.a(Theme.a, i4);
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(f14, fB);
         MatrixStackVar.scale(f6, f6);
         MatrixStackVar.translate(-f14, -fB);
         fontRenderer.a(strR, f14, fB, colorA10, MatrixStackVar);
         MatrixStackVar.popMatrix();
         renderer2D.a(f14 + fA4, f12 + 3.0F * f6, 1.0F * f6, f10 - 6.0F * f6, Theme.a(Theme.a, (int)(255.0F * f5 * fJ4)), MatrixStackVar);
      }

      renderer2D.b().a(MatrixStackVar);
      if (zA || this.q) {
         GuiInput.g();
      }
   }

   @Override
   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      float f5 = this.F;
      float f6 = f2 + f4 - 8.0F - f5;
      if (!GuiInput.a(f6, f3 + 10.0F - 7.0F, f5, 14.0F, i2, i3)) {
         if (this.q) {
            this.q = false;
            this.u.a(0.0, 0.2, Easing.h);
            this.G = "";
         }

         return false;
      } else {
         a(this);
         if (!this.q) {
            this.q = true;
            this.u.a(1.0, 0.2, Easing.h);
            this.w.d(1.0);
            this.A = false;
            this.w.a(0.3, 0.5, Easing.i);
         }

         FontRenderer fontRenderer = FontManager.a[14];
         this.r = fontRenderer.a(this.n, i2 - (f6 + 5.0F + fontRenderer.a(this.r())));
         this.H();
         this.u();
         return true;
      }
   }

   @Override
   public boolean a(int i2, int i3, int i4) {
      if (!this.q) {
         return false;
      }

      int i5 = (~i4 | 2) - ~i4 == 0 ? 0 : 1;
      int i6 = (~i4 | 1) - ~i4 == 0 ? 0 : 1;
      if (i5 != 0) {
         switch (i2) {
            case 65:
               this.C();
               break;
            case 67:
               this.D();
               break;
            case 86:
               this.E();
               break;
            case 88:
               this.F();
         }

         return true;
      } else {
         switch (i2) {
            case 256:
               this.q = false;
               this.u.a(0.0, 0.2, Easing.h);
               this.G = "";
               this.H();
               break;
            case 257:
               this.q = false;
               this.u.a(0.0, 0.2, Easing.h);
               this.G = "";
               this.H();
               break;
            case 258:
               if (this.m == TokenSettingWidget.ValueType.PLAYER) {
                  this.w();
               }
               break;
            case 259:
               this.y();
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               break;
            case 261:
               this.z();
               break;
            case 262:
               this.b(Bool.from(i6), Bool.from(i5));
               break;
            case 263:
               this.a(Bool.from(i6), Bool.from(i5));
               break;
            case 268:
               this.a(Bool.from(i6));
               break;
            case 269:
               this.b(Bool.from(i6));
         }

         return true;
      }
   }

   @Override
   public boolean a(char c2, int i2) {
      if (!this.q || Character.isISOControl(c2)) {
         return false;
      }

      if (!this.a(c2)) {
         return true;
      }

      if (this.n.length() >= 16 && !this.G()) {
         return true;
      }

      if (this.G()) {
         this.I();
      }

      if (this.n.length() < 16) {
         this.n = this.n.substring(0, this.r) + c2 + this.n.substring(this.r);
         int i3 = this.r;
         this.r = (i3 | 1) + (i3 & 1);
         if (this.m == TokenSettingWidget.ValueType.PRICE) {
            int iX = this.x();
            this.n = this.b(this.n.replace(",", ""));
            this.r = this.a(iX);
         }

         this.J();
         this.u();
         this.t();
         this.q();
      }

      return true;
   }

   private int x() {
      return this.n.substring(0, this.r).replace(",", "").length();
   }

   private int a(int i2) {
      String strReplace = this.n.replace(",", "");
      if (i2 > strReplace.length()) {
         i2 = strReplace.length();
      }

      int i3 = 0;
      int i4 = 0;

      for (int i5 = 0; i5 < this.n.length() && i3 < i2; i5++) {
         if (this.n.charAt(i5) != ',') {
            i3++;
         }

         int i6 = i5;
         i4 = (i6 ^ 1) + 2 * (i6 & 1);
      }

      return i4;
   }

   private void y() {
      if (this.G()) {
         this.I();
      } else if (this.r > 0) {
         this.n = this.n.substring(0, this.r - 1) + this.n.substring(this.r);
         int i2 = this.r;
         this.r = (i2 ^ 1) - 2 * (~i2 & 1);
         if (this.m == TokenSettingWidget.ValueType.PRICE) {
            int iX = this.x();
            this.n = this.b(this.n.replace(",", ""));
            this.r = this.a(iX);
         }
      }

      this.J();
      this.u();
      this.t();
      this.q();
   }

   private void z() {
      if (this.G()) {
         this.I();
      } else if (this.r < this.n.length()) {
         this.n = this.n.substring(0, this.r) + this.n.substring(this.r - -2 - 1);
         if (this.m == TokenSettingWidget.ValueType.PRICE) {
            int iX = this.x();
            this.n = this.b(this.n.replace(",", ""));
            this.r = this.a(iX);
         }
      }

      this.J();
      this.u();
      this.t();
      this.q();
   }

   private void a(boolean z, boolean z2) {
      int iA;
      if (z2) {
         iA = this.A();
      } else {
         int i2 = this.r;
         iA = Math.max(0, (i2 ^ 1) - 2 * (~i2 & 1));
      }

      if (z) {
         if (!this.G()) {
            this.s = this.r;
         }

         this.t = iA;
      } else {
         if (this.G()) {
            iA = Math.min(this.s, this.t);
         }

         this.H();
      }

      this.r = iA;
      this.J();
   }

   private void b(boolean z, boolean z2) {
      int iB;
      if (z2) {
         iB = this.B();
      } else {
         int length = this.n.length();
         int i2 = this.r;
         iB = Math.min(length, 2 * (i2 | 1) - (i2 ^ 1));
      }

      if (z) {
         if (!this.G()) {
            this.s = this.r;
         }

         this.t = iB;
      } else {
         if (this.G()) {
            iB = Math.max(this.s, this.t);
         }

         this.H();
      }

      this.r = iB;
      this.J();
   }

   private void a(boolean z) {
      if (z) {
         if (!this.G()) {
            this.s = this.r;
         }

         this.t = 0;
      } else {
         this.H();
      }

      this.r = 0;
      this.J();
   }

   private void b(boolean z) {
      if (z) {
         if (!this.G()) {
            this.s = this.r;
         }

         this.t = this.n.length();
      } else {
         this.H();
      }

      this.r = this.n.length();
      this.J();
   }

   private int A() {
      if (this.r == 0) {
         return 0;
      }

      int i2 = this.r - 1;

      while (i2 > 0 && Character.isWhitespace(this.n.charAt(i2))) {
         i2--;
      }

      while (i2 > 0) {
         int i3 = i2;
         if (Character.isWhitespace(this.n.charAt(2 * (i3 & -2) - (i3 ^ 1)))) {
            break;
         }

         i2--;
      }

      return i2;
   }

   private int B() {
      if (this.r >= this.n.length()) {
         return this.n.length();
      }

      int i2 = this.r;

      while (i2 < this.n.length() && !Character.isWhitespace(this.n.charAt(i2))) {
         i2++;
      }

      while (i2 < this.n.length() && Character.isWhitespace(this.n.charAt(i2))) {
         i2++;
      }

      return i2;
   }

   private void C() {
      this.s = 0;
      this.t = this.n.length();
      this.r = this.n.length();
   }

   private void D() {
      if (this.G()) {
         MinecraftClient.getInstance().keyboard.setClipboard(this.n.substring(Math.min(this.s, this.t), Math.max(this.s, this.t)));
      }
   }

   private void E() {
      String strGetClipboard = MinecraftClient.getInstance().keyboard.getClipboard();
      if (strGetClipboard != null && !strGetClipboard.isEmpty()) {
         String strReplaceAll = strGetClipboard.replaceAll("[\\r\\n\\t]", "");
         StringBuilder sb = new StringBuilder();

         for (int i2 = 0; i2 < strReplaceAll.length(); i2++) {
            char cCharAt = strReplaceAll.charAt(i2);
            int i3 = this.r;
            int length = sb.length();
            if (this.b(cCharAt, (i3 & ~length) + (length & ~i3) + 2 * (i3 & length))) {
               sb.append(cCharAt);
            }
         }

         String string = sb.toString();
         if (this.G()) {
            this.I();
         }

         int length2 = 16 + ~this.n.length() + 1;
         if (string.length() > length2) {
            string = string.substring(0, length2);
         }

         if (!string.isEmpty()) {
            this.n = this.n.substring(0, this.r) + string + this.n.substring(this.r);
            int i4 = this.r;
            int length3 = string.length();
            this.r = (i4 ^ length3) + 2 * (i4 & length3);
            if (this.m == TokenSettingWidget.ValueType.PRICE) {
               int iX = this.x();
               this.n = this.b(this.n.replace(",", ""));
               this.r = this.a(iX);
            }
         }

         this.J();
         this.u();
         this.t();
         this.q();
      }
   }

   private void F() {
      if (this.G()) {
         this.D();
         this.I();
      }
   }

   private boolean G() {
      return this.s != -1 && this.t != -1 && this.s != this.t;
   }

   private void H() {
      this.s = -1;
      this.t = -1;
   }

   private void I() {
      if (this.G()) {
         int iMin = Math.min(this.s, this.t);
         this.n = this.n.substring(0, iMin) + this.n.substring(Math.max(this.s, this.t));
         this.r = iMin;
         this.H();
         if (this.m == TokenSettingWidget.ValueType.PRICE) {
            int iX = this.x();
            this.n = this.b(this.n.replace(",", ""));
            this.r = this.a(iX);
         } else if (c) {
         }

         this.u();
         this.t();
         this.q();
      }
   }

   private void J() {
      this.w.d(1.0);
      this.A = false;
      this.w.a(0.3, 0.5, Easing.i);
   }

   public boolean p() {
      return this.m == TokenSettingWidget.ValueType.PLAYER ? Bool.from(this.n.length() < 3 ? 0 : 1) : true;
   }

   @Override
   public boolean d() {
      return Bool.from(this.p != null && !this.p.m() ? 0 : 1);
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }

   public enum ValueType {
      COMMAND,
      PRICE,
      PLAYER,
      INT;

      public static int e;
      public static boolean f;

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }
}
