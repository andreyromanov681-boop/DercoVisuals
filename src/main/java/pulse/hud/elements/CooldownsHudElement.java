package pulse.hud.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.item.Item;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Matrix3x2fStack;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.hud.core.HudElement;
import pulse.hud.core.HudIcons;
import pulse.inventory.CooldownInfo;
import pulse.module.ModuleRegistry;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;

public class CooldownsHudElement extends HudElement {
   private static final Color m = new Color(13, 13, 17);
   private static final Color n = new Color(17, 17, 23);
   private static final Color o = new Color(255, 255, 255);
   private static final Color p = new Color(223, 223, 243);
   private static final Color q = new Color(20, 20, 28);
   private static final Color r = new Color(255, 255, 255, 255);
   private static final Color s = new Color(17, 17, 23);
   private static final Color t = new Color(23, 23, 30);
   private static final Color u = new Color(18, 18, 24);
   private static final Color v = new Color(23, 23, 30);
   private static final Color w = new Color(18, 18, 24);
   private static final String HUD_TITLE = "Кулдауны";
   private static final float HUD_HEADER_ICON_BASE = 10.0F;
   private static final float HUD_HEADER_ICON_GAP = 4.0F;
   private static final float x = 8.5F;
   private static final float y = 8.0F;
   private static final float z = 24.0F;
   private static final float A = 16.0F;
   private static final float B = 5.0F;
   private static final float C = 10.0F;
   private static final float D = 2.5F;
   private static final float E = 12.0F;
   private static final float F = 2.0F;
   private static final float G = 12.0F;
   private static final float H = 6.0F;
   private static final float I = 6.5F;
   private static final float J = 0.5F;
   private static final float K = 100.0F;
   private static final double L = 0.2;
   private static final long M = 1000L;
   private final Map<Item, CooldownsHudElement.ActiveCooldownEntry> O = new LinkedHashMap<>();
   private final List<CooldownsHudElement.CooldownDefinition> P = new ArrayList<>();
   private final AnimationState Q = new AnimationState();
   private final AnimationState R = new AnimationState();
   private final AnimationState S = new AnimationState();
   private long T = 0L;
   private int U;
   private boolean V;
   private boolean W;
   private boolean X;
   private List<CooldownsHudElement.CooldownSnapshot> Y = List.of();
   private static final Color WHITE = new Color(255, 255, 255, 255);
   private static final Identifier HUD_HEADER_ICON = HudIcons.clickGui("cooldowns");
   private static final ItemRenderState N = new ItemRenderState();
   private static final Map<Item, String> ITEM_DISPLAY_NAMES = new HashMap<>();

   public CooldownsHudElement(float f, float f2) {
      super(f, f2);
      this.U = 0;
      this.V = false;
      this.W = false;
      this.X = false;
      this.S.d(0.0);
      this.u();
      this.a();
   }

   private void u() {
      this.P.add(new CooldownsHudElement.CooldownDefinition(Items.ENDER_PEARL, "Эндер-шар", "0:00"));
      this.P.add(new CooldownsHudElement.CooldownDefinition(Items.CHORUS_FRUIT, "Хорус", "0:00"));
      this.P.add(new CooldownsHudElement.CooldownDefinition(Items.ENDER_EYE, "Дезка", "0:00"));
   }

   private boolean v() {
      return Bool.from((this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof PulseClickGuiScreen) && this.x().isEmpty() ? 1 : 0);
   }

   private void w() {
      if (!this.X && ModuleRegistry.COOLDOWNS_HUD != null) {
         this.f().a(ModuleRegistry.COOLDOWNS_HUD);
         this.X = true;
      }
   }

   @Override
   protected void a() {
      this.w();
      boolean zV = this.v();
      List<CooldownsHudElement.CooldownSnapshot> listX = this.x();
      this.Y = listX;
      float fG = this.g();
      int iFontIndex = fontIndex(15.0F * fG);
      int iFontIndex2 = fontIndex(16.0F * fG);
      int iFontIndex3 = fontIndex(14.0F * fG);
      FontRenderer fontRenderer = FontManager.MEDIUM[iFontIndex];
      FontRenderer fontRenderer2 = FontManager.REGULAR[iFontIndex2];
      FontRenderer fontRenderer3 = FontManager.REGULAR[iFontIndex3];
      if (zV) {
         this.O.clear();
      } else {
         HashSet hashSet = new HashSet();
         int idx = 0;

         for (CooldownsHudElement.CooldownSnapshot cooldownSnapshot : listX) {
            hashSet.add(cooldownSnapshot.a);
            CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry = this.O.get(cooldownSnapshot.a);
            if (activeCooldownEntry == null) {
               CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry2 = new CooldownsHudElement.ActiveCooldownEntry(
                  cooldownSnapshot.a, cooldownSnapshot.b, cooldownSnapshot.c, cooldownSnapshot.d, cooldownSnapshot.e
               );
               activeCooldownEntry2.f.d(0.0);
               activeCooldownEntry2.f.a(1.0, 0.2, Easing.h);
               activeCooldownEntry2.g.d(idx);
               this.O.put(cooldownSnapshot.a, activeCooldownEntry2);
            } else {
               activeCooldownEntry.b = cooldownSnapshot.b;
               activeCooldownEntry.c = cooldownSnapshot.c;
               activeCooldownEntry.d = cooldownSnapshot.d;
               activeCooldownEntry.e = cooldownSnapshot.e;
               activeCooldownEntry.h = false;
               if (activeCooldownEntry.f.i() < 1.0) {
                  activeCooldownEntry.f.a(1.0, 0.2, Easing.h);
               }

               if (Math.abs(activeCooldownEntry.g.i() - idx) > 0.01) {
                  activeCooldownEntry.g.a(idx, 0.2, Easing.h);
               }
            }

            idx++;
         }

         for (CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry3 : new ArrayList<>(this.O.values())) {
            if (!hashSet.contains(activeCooldownEntry3.a) && !activeCooldownEntry3.h) {
               activeCooldownEntry3.h = true;
               activeCooldownEntry3.f.a(0.0, 0.2, Easing.h);
            }
         }

         this.O.entrySet().removeIf(entry -> entry.getValue().h && entry.getValue().f.j() <= 0.01);
      }

      for (CooldownsHudElement.ActiveCooldownEntry e : this.O.values()) {
         e.f.a();
         e.g.a();
      }

      if (!zV && listX.isEmpty()) {
         if (this.W) {
            this.S.a(0.0, 0.2, Easing.h);
            this.W = false;
         }
      } else if (!this.W) {
         this.S.a(1.0, 0.2, Easing.h);
         this.W = true;
      }

      this.S.a();
      float f = 8.5F * fG;
      float f2 = 24.0F * fG;
      float f3 = 12.0F * fG;
      float f4 = 6.0F * fG;
      float fMax = 110.0F * fG;
      if (zV && !this.P.isEmpty()) {
         fMax = Math.max(fMax, rowWidth(fontRenderer2, fontRenderer3, this.P.get(this.U % this.P.size()).b, formatCooldownTime(24), fG));
      } else {
         for (CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry4 : this.O.values()) {
            if (activeCooldownEntry4.f.j() > 0.01) {
               fMax = Math.max(fMax, rowWidth(fontRenderer2, fontRenderer3, activeCooldownEntry4.c, activeCooldownEntry4.d, fG));
            }
         }
      }

      float fMax2 = Math.max(fMax, f + 10.0F * fG + 4.0F * fG + fontRenderer.a("Кулдауны") + f);
      float f5 = 0.0F;
      int rowCount = zV ? 1 : listX.size();
      if (rowCount > 1) {
         f5 = (rowCount - 1) * f3 + (rowCount - 1) * 2.0F * fG;
      }

      float f6 = fMax2 + f;
      float f7 = f2 + f4 + f5 + 10.0F * fG + 10.0F * fG;
      if (this.V) {
         if (Math.abs(this.Q.i() - f6) > 0.5) {
            this.Q.a(f6, 0.2, Easing.h);
         }

         if (Math.abs(this.R.i() - f7) > 0.5) {
            this.R.a(f7, 0.2, Easing.h);
         }
      } else {
         this.Q.d(f6);
         this.R.d(f7);
         this.V = true;
      }

      this.Q.a();
      this.R.a();
      this.d = (float)this.Q.j();
      this.e = (float)this.R.j();
      if (zV && this.P.size() > 1) {
         long jCurrentTimeMillis = System.currentTimeMillis();
         if (jCurrentTimeMillis - this.T >= 1000L) {
            this.T = jCurrentTimeMillis;
            this.U = (this.U + 1) % this.P.size();
         }
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      if (this.a.player != null) {
         this.a();
         float fJ = (float)this.S.j();
         if (!(fJ < 0.01F)) {
            boolean zV = this.v();
            float fG = this.g();
            int iFontIndex = fontIndex(15.0F * fG);
            int iFontIndex2 = fontIndex(16.0F * fG);
            int iFontIndex3 = fontIndex(14.0F * fG);
            FontRenderer fontRenderer = FontManager.MEDIUM[iFontIndex];
            FontRenderer fontRenderer2 = FontManager.REGULAR[iFontIndex2];
            FontRenderer fontRenderer3 = FontManager.REGULAR[iFontIndex3];
            float f3 = 8.5F * fG;
            float f4 = 24.0F * fG;
            float f5 = 12.0F * fG;
            float f6 = 6.0F * fG;
            float f7 = 6.5F * fG;
            float f8 = this.b;
            float f9 = this.c;
            Color colorA = this.a(new Color(13, 13, 17, 255), fJ);
            Color colorA2 = this.a(new Color(17, 17, 23, 255), fJ);
            renderer2D.a(f8, f9, this.d, this.e, f7, colorA, colorA, colorA2, colorA2, MatrixStackVar);
            DrawContext DrawContextVar = Renderer2DImpl.currentDrawContext != null
               ? Renderer2DImpl.currentDrawContext
               : new DrawContext(this.a, new GuiRenderState(), this.a.getWindow().getScaledWidth(), this.a.getWindow().getScaledHeight());
            float f10 = 20.0F * fG;
            float f11 = 4.0F * fG;
            this.drawClickGuiIcon(renderer2D, MatrixStackVar, HUD_HEADER_ICON, f8 + f3, f9 + f4 / 2.0F - f10 / 2.0F, f10, fJ);
            fontRenderer.a("Кулдауны", f8 + f3 + f10 + f11, f9 + f4 / 2.0F - fontRenderer.b("Кулдауны") / 4.0F, this.a(WHITE, fJ), MatrixStackVar);
            float f12 = f9 + f4 + f6;
            float f13 = f8 + f3;
            if (zV && !this.P.isEmpty()) {
               this.a(MatrixStackVar, DrawContextVar, renderer2D, fontRenderer2, fontRenderer3, f13, f12, this.P.get(this.U % this.P.size()), fJ, fG);
            } else {
               boolean firstRow = true;

               for (CooldownsHudElement.CooldownSnapshot cooldownSnapshot : this.Y) {
                  CooldownsHudElement.ActiveCooldownEntry activeCooldownEntry = this.O.get(cooldownSnapshot.a);
                  float fAlpha = activeCooldownEntry != null ? (float)activeCooldownEntry.f.j() : 1.0F;
                  if (!firstRow) {
                     f12 += 2.0F * fG;
                  }

                  this.a(
                     MatrixStackVar,
                     DrawContextVar,
                     renderer2D,
                     fontRenderer2,
                     fontRenderer3,
                     f13,
                     f12,
                     cooldownSnapshot.b,
                     cooldownSnapshot.c,
                     cooldownSnapshot.d,
                     fAlpha * fJ,
                     fG
                  );
                  f12 += 12.0F * fG;
                  firstRow = false;
               }
            }

            renderer2D.a(f8, f9 + f4, this.d, 1.0F * fG, 0.0F, this.a(s, fJ), MatrixStackVar);
         }
      }
   }

   private static int fontIndex(float f) {
      return Math.max(6, Math.min(64, Math.round(f)));
   }

   private static float rowWidth(FontRenderer fontRenderer, FontRenderer fontRenderer2, String str, String str2, float f) {
      float f2 = 2.5F * f;
      return 10.0F * f + f2 + fontRenderer.a(str) + 8.0F * f + (fontRenderer2.a(str2) + 6.5F * f * 2.0F + 0.5F * f * 2.0F - 2.0F * f);
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      DrawContext DrawContextVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      CooldownsHudElement.CooldownDefinition cooldownDefinition,
      float f3,
      float f4
   ) {
      float f5 = 10.0F * f4;
      float f6 = 2.5F * f4;
      float f7 = 8.5F * f4;
      float f8 = 6.5F * f4;
      float f9 = 0.5F * f4;
      float f10 = 12.0F * f4;
      float f11 = 6.0F * f4;
      float f12 = f2 + 12.0F * f4 / 2.0F;
      Color colorA = this.a(WHITE, f3);
      Color colorA2 = this.a(WHITE, f3);
      this.a(MatrixStackVar, DrawContextVar, new ItemStack(cooldownDefinition.a), f, f12 - f5 / 2.0F, f5, f3);
      fontRenderer.a(cooldownDefinition.b, f + f5 + f6, f12 - fontRenderer.b(cooldownDefinition.b) / 4.0F, colorA, MatrixStackVar);
      float fA = fontRenderer2.a(formatCooldownTime(24)) + f8 * 2.0F + f9 * 2.0F - 2.0F * f4;
      float f13 = this.b + this.d - f7 - fA;
      float f14 = f12 - f10 / 2.0F - f9;
      float f15 = f10 + f9 * 2.0F;
      Color colorA3 = this.a(v, f3);
      Color colorA4 = this.a(w, f3);
      Color colorA5 = this.a(t, f3);
      Color colorA6 = this.a(u, f3);
      renderer2D.a(f13, f14, fA, f15, f11, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      renderer2D.a(f13 + f9, f14 + f9, fA - f9 * 2.0F, f10, f11 - f9, colorA5, colorA5, colorA6, colorA6, MatrixStackVar);
      String strTime = formatCooldownTime(24);
      fontRenderer2.a(strTime, f13 + (fA - fontRenderer2.a(strTime)) / 2.0F, f12 - fontRenderer2.b(strTime) / 4.0F, colorA2, MatrixStackVar);
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      DrawContext DrawContextVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      ItemStack ItemStackVar,
      String str,
      String str2,
      float f3,
      float f4
   ) {
      float f5 = 10.0F * f4;
      float f6 = 2.5F * f4;
      float f7 = 8.5F * f4;
      float f8 = 6.5F * f4;
      float f9 = 0.5F * f4;
      float f10 = 12.0F * f4;
      float f11 = 6.0F * f4;
      float f12 = f2 + 12.0F * f4 / 2.0F;
      Color colorA = this.a(WHITE, f3);
      Color colorA2 = this.a(WHITE, f3);
      this.a(MatrixStackVar, DrawContextVar, ItemStackVar, f, f12 - f5 / 2.0F, f5, f3);
      fontRenderer.a(str, f + f5 + f6, f12 - fontRenderer.b(str) / 4.0F, colorA, MatrixStackVar);
      float fA = fontRenderer2.a(str2) + f8 * 2.0F + f9 * 2.0F - 2.0F * f4;
      float f13 = this.b + this.d - f7 - fA;
      float f14 = f12 - f10 / 2.0F - f9;
      float f15 = f10 + f9 * 2.0F;
      Color colorA3 = this.a(v, f3);
      Color colorA4 = this.a(w, f3);
      Color colorA5 = this.a(t, f3);
      Color colorA6 = this.a(u, f3);
      renderer2D.a(f13, f14, fA, f15, f11, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      renderer2D.a(f13 + f9, f14 + f9, fA - f9 * 2.0F, f10, f11 - f9, colorA5, colorA5, colorA6, colorA6, MatrixStackVar);
      fontRenderer2.a(str2, f13 + (fA - fontRenderer2.a(str2)) / 2.0F, f12 - fontRenderer2.b(str2) / 4.0F, colorA2, MatrixStackVar);
   }

   private void drawClickGuiIcon(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, Identifier IdentifierVar, float f, float f2, float f3, float f4) {
      HudIcons.drawRotated180(renderer2D, MatrixStackVar, IdentifierVar, f, f2, f3, this.a(WHITE, f4));
   }

   private void a(Matrix3x2fStack MatrixStackVar, DrawContext DrawContextVar, ItemStack ItemStackVar, float f, float f2, float f3, float f4) {
      if (this.a.player != null && !ItemStackVar.isEmpty() && !(f4 < 0.01F)) {
         Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
         MatrixStackVarGetMatrices.pushMatrix();
         MatrixStackVarGetMatrices.translate(f, f2);
         float f5 = f3 / 16.0F;
         MatrixStackVarGetMatrices.scale(f5, f5);
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, f4);
         DrawContextVar.drawItem(ItemStackVar, 0, 0);
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystemHelper.disableBlend();
         MatrixStackVarGetMatrices.popMatrix();
      }
   }

   private Color a(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f))));
   }

   private List<CooldownsHudElement.CooldownSnapshot> x() {
      ArrayList<CooldownsHudElement.CooldownSnapshot> arrayList = new ArrayList<>();
      if (this.a.player == null) {
         return arrayList;
      }

      ItemCooldownManager ItemCooldownManagerVarGetItemCooldownManager = this.a.player.getItemCooldownManager();
      HashSet<Identifier> hashSet = new HashSet<>();

      for (CooldownInfo.ActiveGroupCooldown activeGroupCooldown : CooldownInfo.collectActiveGroups(ItemCooldownManagerVarGetItemCooldownManager)) {
         if (hashSet.add(activeGroupCooldown.groupId)) {
            ItemStack ItemStackVarStackForGroup = CooldownInfo.stackForGroup(ItemCooldownManagerVarGetItemCooldownManager, activeGroupCooldown.groupId);
            int iMax = Math.max(0, activeGroupCooldown.remainingTicks);
            arrayList.add(
               new CooldownsHudElement.CooldownSnapshot(
                  ItemStackVarStackForGroup.getItem(),
                  ItemStackVarStackForGroup,
                  ItemStackVarStackForGroup.getName().getString(),
                  formatCooldownTime(iMax),
                  iMax
               )
            );
         }
      }

      for (int i = 0; i < this.a.player.getInventory().size(); i++) {
         this.tryAddCooldownStack(ItemCooldownManagerVarGetItemCooldownManager, this.a.player.getInventory().getStack(i), hashSet, arrayList);
      }

      for (Item ItemVar : new Item[]{Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT, Items.ENDER_PEARL}) {
         this.tryAddCooldownStack(ItemCooldownManagerVarGetItemCooldownManager, ItemVar.getDefaultStack(), hashSet, arrayList);
      }

      arrayList.removeIf(snapshot -> snapshot.a == Items.FIREWORK_ROCKET);
      arrayList.sort(Comparator.comparingInt(cooldownSnapshot -> cooldownSnapshot.e));
      return arrayList;
   }

   private void tryAddCooldownStack(
      ItemCooldownManager ItemCooldownManagerVar, ItemStack ItemStackVar, HashSet<Identifier> hashSet, List<CooldownsHudElement.CooldownSnapshot> list
   ) {
      if (!ItemStackVar.isEmpty()) {
         Identifier IdentifierVarGetGroup = ItemCooldownManagerVar.getGroup(ItemStackVar);
         if (!hashSet.contains(IdentifierVarGetGroup)) {
            CooldownInfo.ItemCooldownSnapshot itemCooldownSnapshotA = CooldownInfo.a(ItemCooldownManagerVar, IdentifierVarGetGroup);
            int iMax = itemCooldownSnapshotA.a() ? Math.max(0, itemCooldownSnapshotA.c - itemCooldownSnapshotA.a) : 0;
            float fGetCooldownProgress = ItemCooldownManagerVar.getCooldownProgress(ItemStackVar, this.a.getRenderTickCounter().getTickProgress(true));
            if (iMax <= 0 && fGetCooldownProgress > 0.001F) {
               iMax = Math.max(1, Math.round(fGetCooldownProgress * 20.0F));
            }

            if (iMax > 0) {
               hashSet.add(IdentifierVarGetGroup);
               list.add(
                  new CooldownsHudElement.CooldownSnapshot(ItemStackVar.getItem(), ItemStackVar, this.a(ItemStackVar), formatCooldownTime(iMax), iMax)
               );
            }
         }
      }
   }

   private static String formatCooldownTime(int i) {
      if (i <= 0) {
         return "0:00";
      }

      float f = i / 20.0F;
      int i2 = (int)f;
      int i3 = i2 / 60;
      return i3 > 0 ? String.format("%d:%02d", i3, i2 % 60) : (f < 10.0F ? String.format("%.1f", f) : String.format("%d", i2));
   }

   private String a(ItemStack ItemStackVar) {
      String str = ITEM_DISPLAY_NAMES.get(ItemStackVar.getItem());
      return str != null ? str : ItemStackVar.getName().getString();
   }

   private String b(int i) {
      return i <= 0 ? "0.0s" : String.format("%.1fs", i / 20.0F);
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   static {
      ITEM_DISPLAY_NAMES.put(Items.DRIED_KELP, "Пласт");
      ITEM_DISPLAY_NAMES.put(Items.NETHERITE_SCRAP, "Трапка");
      ITEM_DISPLAY_NAMES.put(Items.CHORUS_FRUIT, "Хорус");
      ITEM_DISPLAY_NAMES.put(Items.POPPED_CHORUS_FRUIT, "Хорус");
      ITEM_DISPLAY_NAMES.put(Items.ENDER_PEARL, "Эндер-шар");
      ITEM_DISPLAY_NAMES.put(Items.ENDER_EYE, "Глаз эндера");
   }

   private static class ActiveCooldownEntry {
      final Item a;
      ItemStack b;
      String c;
      String d;
      int e;
      final AnimationState f = new AnimationState();
      final AnimationState g = new AnimationState();
      boolean h = false;

      ActiveCooldownEntry(Item ItemVar, ItemStack ItemStackVar, String str, String str2, int i) {
         this.a = ItemVar;
         this.b = ItemStackVar;
         this.c = str;
         this.d = str2;
         this.e = i;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class CooldownDefinition {
      final Item a;
      final String b;
      final String c;

      CooldownDefinition(Item ItemVar, String str, String str2) {
         this.a = ItemVar;
         this.b = str;
         this.c = str2;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class CooldownSnapshot {
      final Item a;
      final ItemStack b;
      final String c;
      final String d;
      final int e;

      CooldownSnapshot(Item ItemVar, ItemStack ItemStackVar, String str, String str2, int i) {
         this.a = ItemVar;
         this.b = ItemStackVar;
         this.c = str;
         this.d = str2;
         this.e = i;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
