package pulse.gui.friends;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.GuiInteractionState;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;
import pulse.util.ColorUtils;

public class FriendCard {
   public static final float a = 182.5F;
   public static final float b = 37.0F;
   private static final float e = 9.5F;
   private static final float f = 6.5F;
   private static final float g = 24.0F;
   private static final float h = 5.0F;
   private static final float i = 6.5F;
   private static final float j = -8.0F;
   private static final float k = 13.5F;
   private static final float l = 4.0F;
   private static final float m = 6.5F;
   private final HistoryEntry n;
   private final AnimationState o = new AnimationState();
   private final AnimationState p = new AnimationState();
   private boolean q = false;
   private boolean r = false;
   private Consumer<HistoryEntry> s;
   private float t;
   private float u;
   private float v;
   public static int c;

   public FriendCard(HistoryEntry historyEntry) {
      this.n = historyEntry;
   }

   public HistoryEntry a() {
      return this.n;
   }

   public void a(Consumer<HistoryEntry> consumer) {
      this.s = consumer;
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, int i2, int i3, float f5) {
      this.t = f2;
      this.u = f3;
      this.v = f4;
      this.o.a();
      this.p.a();
      int i4 = (int)(255.0F * f5);
      boolean zB = GuiInteractionState.a().b();
      int i5 = !zB && GuiInput.a(f2, f3, f4, 37.0F, i2, i3) ? 1 : 0;
      float f6 = f2 + f4 - 6.5F - 13.5F;
      float f7 = f3 + 6.5F;
      int i6 = !zB && GuiInput.a(f6, f7, 13.5F, 13.5F, i2, i3) ? 1 : 0;
      if (zB && this.q) {
         this.o.a(0.0, 0.15, Easing.h);
         this.q = false;
      } else if (!zB && Bool.from(i5) != this.q) {
         this.o.a(i5 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.q = Bool.from(i5);
      }

      if (zB && this.r) {
         this.p.a(0.0, 0.15, Easing.h);
         this.r = false;
      } else if (!zB && Bool.from(i6) != this.r) {
         this.p.a(i6 == 0 ? 0.0 : 1.0, 0.15, Easing.h);
         this.r = Bool.from(i6);
      }

      float fJ = (float)this.o.j();
      Color colorA = Theme.a(Theme.m, i4);
      Color colorA2 = Theme.a(Theme.n, i4);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, 38.0F, 9.5F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      Color colorA3 = ColorUtils.a(Theme.e, Theme.g, fJ);
      Color colorA4 = ColorUtils.a(Theme.f, Theme.h, fJ);
      Color colorA5 = Theme.a(colorA3, i4);
      Color colorA6 = Theme.a(colorA4, i4);
      renderer2D.a(f2, f3, f4, 37.0F, 9.5F, colorA5, colorA5, colorA6, colorA6, MatrixStackVar);
      float f8 = f2 + 6.5F;
      this.a(MatrixStackVar, renderer2D, f8, f3 + 6.5F, i4);
      FontRenderer fontRenderer = FontManager.a[15];
      FontRenderer fontRenderer2 = FontManager.a[11];
      float f9 = f8 + 24.0F + 6.5F;
      String strA = this.n.a();
      String strC = this.n.c();
      float fB = fontRenderer.b(strA);
      float fB2 = f3 + (37.0F - (fB + -8.0F + fontRenderer2.b(strC) - 6.0F)) / 2.0F;
      float f10 = fB2 + fB + -8.0F;
      Color colorA7 = Theme.a(Theme.a, i4);
      Color colorA8 = Theme.a(Theme.b, i4);
      fontRenderer.a(strA, f9, fB2, colorA7, MatrixStackVar);
      fontRenderer2.a(strC, f9, f10, colorA8, MatrixStackVar);
      this.a(MatrixStackVar, renderer2D, f6, f7, f5);
      if (i6 != 0 && !zB) {
         GuiInput.g();
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2) {
      Color colorA = Theme.a(Theme.m, i2);
      Color colorA2 = Theme.a(Theme.n, i2);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 25.0F, 25.0F, 5.0F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      Identifier IdentifierVarB = this.b();
      if (IdentifierVarB == null) {
         renderer2D.a(f2, f3, 24.0F, 24.0F, 5.0F, Theme.a(Theme.FriendsPanel, i2), MatrixStackVar);
      } else {
         RenderSystemHelper.setShaderTexture(0, IdentifierVarB);
         Color colorA3 = Theme.a(Theme.aa, i2);
         renderer2D.a(IdentifierVarB, f2, f3, 24.0F, 24.0F, 5.0F, 0.125F, 0.125F, 0.125F, 0.125F, colorA3, MatrixStackVar);
         renderer2D.a(IdentifierVarB, f2, f3, 24.0F, 24.0F, 5.0F, 0.625F, 0.125F, 0.125F, 0.125F, colorA3, MatrixStackVar);
      }
   }

   private Identifier b() {
      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.getNetworkHandler() != null) {
         for (PlayerListEntry PlayerListEntryVar : MinecraftClientVarGetInstance.getNetworkHandler().getPlayerList()) {
            if (PlayerListEntryVar.getProfile() != null
               && PlayerListEntryVar.getProfile().name() != null
               && PlayerListEntryVar.getProfile().name().equalsIgnoreCase(this.n.a())) {
               return PlayerListEntryVar.getSkinTextures().comp_1626().comp_3626();
            }
         }
      }

      return DefaultSkinHelper.getTexture();
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4) {
      int i2 = (int)(255.0F * f4);
      float fJ = (float)this.p.j();
      Color colorB = Theme.b(Theme.q, f4);
      Color colorB2 = Theme.b(Theme.n, f4);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 14.5F, 14.5F, 4.0F, colorB, colorB, colorB2, colorB2, MatrixStackVar);
      Color colorA = ColorUtils.a(Theme.f, Theme.L, fJ);
      Color colorA2 = ColorUtils.a(Theme.e, Theme.M, fJ);
      Color colorA3 = Theme.a(colorA, i2);
      Color colorA4 = Theme.a(colorA2, i2);
      renderer2D.a(f2, f3, 13.5F, 13.5F, 4.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      FontManager.e[15].a("ﻺ", f2 + 2.75F, f3 + 2.75F - 0.5F, Theme.a(ColorUtils.a(Theme.b, Theme.Z, fJ), i2), MatrixStackVar);
   }

   public boolean a(float f2, float f3, float f4, int i2, int i3) {
      if (!GuiInput.a(f2 + f4 - 6.5F - 13.5F, f3 + 6.5F, 13.5F, 13.5F, i2, i3)) {
         return false;
      }

      if (this.s != null) {
         this.s.accept(this.n);
      }

      return true;
   }

   public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
