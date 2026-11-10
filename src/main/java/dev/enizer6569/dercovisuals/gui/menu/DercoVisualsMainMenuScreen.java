// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.menu;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;
import dev.enizer6569.dercovisuals.DercoVisuals;
import dev.enizer6569.dercovisuals.mixin.accessor.MinecraftClientAccessor;

public class DercoVisualsMainMenuScreen extends TitleScreen {
   public static final List<String> ACCOUNTS = new ArrayList<>();
   private static final Color BG_COLOR = new Color(14, 14, 20);
   private static final Color BTN_BG = new Color(26, 26, 36, 180);
   private static final Color BTN_HOVER = new Color(42, 42, 58, 220);
   private static final Color ICON_COLOR = new Color(160, 160, 190);
   private static final Color ICON_HOVER = new Color(255, 255, 255);
   private boolean showAccountSwitcher = false;
   private boolean initializedAccounts = false;
   public static boolean useVanillaMenu = false;
   public static String selectedAccount = null;
   private boolean isTypingAccount = false;
   private String currentInputText = "";
   private int editingAccountIndex = -1;
   private float scrollOffset = 0.0F;
   private float scrollTarget = 0.0F;
   private long lastClickTime = 0L;
   private int lastClickIndex = -1;

   protected void init() {
      if (!this.initializedAccounts && this.client != null && this.client.getSession() != null) {
         String curr = this.client.getSession().getUsername();
         if (curr != null && !curr.isEmpty() && !ACCOUNTS.contains(curr)) {
            ACCOUNTS.add(curr);
         }

         this.initializedAccounts = true;
      }
   }

   private Identifier getSkinTexture(String name) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.getNetworkHandler() != null && name != null) {
         for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            if (entry.getProfile() != null && entry.getProfile().name() != null && entry.getProfile().name().equalsIgnoreCase(name)) {
               return entry.getSkinTextures().comp_1626().comp_3626();
            }
         }
      }

      if (mc.player != null && mc.getSession() != null && name != null && mc.getSession().getUsername().equalsIgnoreCase(name)) {
         try {
            if (mc.player instanceof AbstractClientPlayerEntity cp) {
               Identifier id = cp.getSkin().comp_1626().comp_3626();
               if (id != null && !id.getPath().isEmpty()) {
                  return id;
               }
            }
         } catch (Throwable var5) {
         }
      }

      return DefaultSkinHelper.getTexture();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      int w = this.width;
      int h = this.height;
      if (DercoVisuals.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(context);
      }

      Renderer2D r = DercoVisuals.getInstance().getRender();
      Matrix3x2fStack m = context.getMatrices();
      Identifier bgTex = IconTextureRegistry.get("menu_bg");
      if (bgTex != null) {
         r.a(bgTex, 0.0F, 0.0F, w, h, Color.WHITE, m);
      } else {
         context.fill(0, 0, w, h, -15856108);
      }

      String username = selectedAccount != null
         ? selectedAccount
         : (this.client != null && this.client.getSession() != null ? this.client.getSession().getUsername() : "Player");
      FontRenderer fu = FontManager.b[12];
      float badgeW = Math.max(80.0F, 32.0F + fu.a(username));
      boolean badgeHover = mouseX >= 14 && mouseX <= 14.0F + badgeW && mouseY >= 12 && mouseY <= 32;
      r.a(14.0F, 12.0F, badgeW, 20.0F, 4.0F, badgeHover ? new Color(38, 38, 52, 200) : new Color(28, 28, 38, 160), m);
      Identifier skinTex = this.getSkinTexture(username);
      RenderSystemHelper.setShaderTexture(0, skinTex);
      r.a(skinTex, 17.0F, 15.0F, 14.0F, 14.0F, 2.0F, 0.125F, 0.125F, 0.125F, 0.125F, Color.WHITE, m);
      r.a(skinTex, 17.0F, 15.0F, 14.0F, 14.0F, 2.0F, 0.625F, 0.125F, 0.125F, 0.125F, Color.WHITE, m);
      fu.a(username, 35.0, 18.0, Color.WHITE, m);
      float logoY = h / 2.0F - 60.0F;
      FontRenderer crackF = FontManager.b[22];
      String crackText = "DercoVisuals visual @alex_sigma";
      float crackW = crackF.a(crackText);
      crackF.a(crackText, (w - crackW) / 2.0F, logoY - 24.0F, new Color(160, 140, 255), m);
      FontRenderer tf = FontManager.b[26];
      String tt = "DercoVisuals";
      float ttW = tf.a(tt);
      float logoSz = 26.0F;
      float logoGap = 8.0F;
      float totalW = logoSz + logoGap + ttW;
      float logoX = (w - totalW) / 2.0F;
      Identifier logoTex = IconTextureRegistry.get("dercovisuals_ico");
      if (logoTex != null) {
         Renderer2DImpl.setTextureFilter(logoTex, true);
         r.a(logoTex, logoX, logoY, logoSz, logoSz, Color.WHITE, m);
      }

      tf.a(tt, logoX + logoSz + logoGap, logoY + 4.0F, Color.WHITE, m);
      float btnW = 240.0F;
      float btnH = 28.0F;
      float btnX = (w - btnW) / 2.0F;
      FontRenderer bf = FontManager.b[15];
      Color textColorNormal = new Color(135, 140, 160);
      Color textColorHover = new Color(255, 255, 255);
      float spY = h / 2.0F - 18.0F;
      boolean spH = this.isHovered(btnX, spY, btnW, btnH, mouseX, mouseY);
      r.a(btnX, spY, btnW, btnH, 5.0F, spH ? BTN_HOVER : BTN_BG, m);
      String spT = "Одиночная игра";
      float spTW = bf.a(spT);
      float spIconW = 10.0F;
      float spGap = 8.0F;
      float spContentW = spIconW + spGap + spTW;
      float spCX = btnX + (btnW - spContentW) / 2.0F;
      Color spCol = spH ? textColorHover : textColorNormal;
      this.drawPersonSmall(r, m, spCX, spY + 9.0F, spCol);
      bf.a(spT, spCX + spIconW + spGap, spY + 7.0F, spCol, m);
      float mpY = h / 2.0F + 16.0F;
      boolean mpH = this.isHovered(btnX, mpY, btnW, btnH, mouseX, mouseY);
      r.a(btnX, mpY, btnW, btnH, 5.0F, mpH ? BTN_HOVER : BTN_BG, m);
      String mpT = "Сетевая игра";
      float mpTW = bf.a(mpT);
      float mpIconW = 14.0F;
      float mpContentW = mpIconW + spGap + mpTW;
      float mpCX = btnX + (btnW - mpContentW) / 2.0F;
      Color mpCol = mpH ? textColorHover : textColorNormal;
      this.drawPeopleSmall(r, m, mpCX, mpY + 9.0F, mpCol);
      bf.a(mpT, mpCX + mpIconW + spGap, mpY + 7.0F, mpCol, m);
      float iSz = 22.0F;
      float iGap = 6.0F;
      int cnt = 5;
      float totIW = cnt * iSz + (cnt - 1) * iGap;
      float isx = (w - totIW) / 2.0F;
      float iY = h / 2.0F + 56.0F;

      for (int i = 0; i < cnt; i++) {
         float bx = isx + i * (iSz + iGap);
         boolean hov = this.isHovered(bx, iY, iSz, iSz, mouseX, mouseY);
         Color bg = i == 4 ? (hov ? new Color(200, 40, 40, 220) : BTN_BG) : (hov ? BTN_HOVER : BTN_BG);
         r.a(bx, iY, iSz, iSz, 5.0F, bg, m);
         Color col = hov ? ICON_HOVER : ICON_COLOR;
         Color holeBg = i == 4 ? (hov ? new Color(200, 40, 40) : new Color(26, 26, 36)) : (hov ? new Color(42, 42, 58) : new Color(26, 26, 36));
         float cx = bx + iSz / 2.0F;
         float cy = iY + iSz / 2.0F;
         if (i == 0) {
            Identifier gearTex = IconTextureRegistry.get("gear_custom");
            if (gearTex != null) {
               float gearSz = 13.0F;
               Renderer2DImpl.setTextureFilter(gearTex, true);
               r.a(gearTex, cx - gearSz / 2.0F, cy - gearSz / 2.0F, gearSz, gearSz, hov ? Color.WHITE : new Color(180, 185, 205), m);
            } else {
               this.drawGear(r, m, cx, cy, col, holeBg);
            }
         } else if (i == 1) {
            this.drawLang(r, m, cx, cy, col);
         } else if (i == 2) {
            this.drawMenu(r, m, cx, cy, col);
         } else if (i == 3) {
            this.drawPeople(r, m, cx, cy, col);
         } else {
            this.drawExit(r, m, cx, cy, col);
         }
      }

      float dropSz = 22.0F;
      float dropX = w - dropSz - 10.0F;
      float dropY = 10.0F;
      boolean dropHov = this.isHovered(dropX, dropY, dropSz, dropSz, mouseX, mouseY);
      r.a(dropX, dropY, dropSz, dropSz, 5.0F, dropHov ? BTN_HOVER : BTN_BG, m);
      Identifier keyTex = IconTextureRegistry.get("key");
      if (keyTex != null) {
         float iconSz = 13.0F;
         Renderer2DImpl.setTextureFilter(keyTex, true);
         r.a(keyTex, dropX + (dropSz - iconSz) / 2.0F, dropY + (dropSz - iconSz) / 2.0F, iconSz, iconSz, dropHov ? Color.WHITE : new Color(170, 175, 200), m);
      }

      FontRenderer sf = FontManager.b[13];
      sf.a("DercoVisuals Visuals 1.21.11", 16.0, h - 20.0F, new Color(125, 130, 150), m);
      if (this.showAccountSwitcher) {
         this.renderAccountSwitcher(context, r, m, mouseX, mouseY);
      }
   }

   private void renderAccountSwitcher(DrawContext context, Renderer2D r, Object m, int mx, int my) {
      r.a(0.0F, 0.0F, this.width, this.height, new Color(0, 0, 0, 160), m);
      String curU = selectedAccount != null
         ? selectedAccount
         : (this.client != null && this.client.getSession() != null ? this.client.getSession().getUsername() : "");
      if (selectedAccount == null && !curU.isEmpty()) {
         selectedAccount = curU;
      }

      if (ACCOUNTS.isEmpty() && !curU.isEmpty()) {
         ACCOUNTS.add(curU);
      }

      if (this.isTypingAccount) {
         float pw = 210.0F;
         float ph = 126.0F;
         float px = (this.width - pw) / 2.0F;
         float py = (this.height - ph) / 2.0F;
         r.a(px, py, pw, ph, 7.0F, new Color(20, 20, 28, 252), m);
         FontRenderer hf = FontManager.b[14];
         FontRenderer sf = FontManager.a[10];
         FontRenderer arrowF = FontManager.b[18];
         Color violetText = new Color(134, 110, 255);
         boolean backH = this.isHovered(px + 5.0F, py + 5.0F, 16.0F, 16.0F, mx, my);
         arrowF.a("←", px + 7.0F, py + 5.0F, backH ? Color.WHITE : new Color(140, 140, 160), m);
         hf.a("Новый аккаунт", px + 26.0F, py + 7.0F, Color.WHITE, m);
         boolean closeH = this.isHovered(px + pw - 16.0F, py + 6.0F, 13.0F, 13.0F, mx, my);
         hf.a("×", px + pw - 13.0F, py + 7.0F, closeH ? new Color(255, 100, 100) : new Color(140, 140, 160), m);
         FontRenderer tiny = FontManager.a[9];
         tiny.a("Введите никнейм для оффлайн-аккаунта", px + 9.0F, py + 21.0F, new Color(110, 110, 130), m);
         float tabW = (pw - 20.0F) / 2.0F - 2.0F;
         float tabY = py + 33.0F;
         FontRenderer tabF = FontManager.a[11];
         r.a(px + 9.0F, tabY, tabW, 20.0F, 4.0F, new Color(38, 30, 72), m);
         float offTxtW = tabF.a("Оффлайн");
         float offStartX = px + 9.0F + (tabW - (8.0F + offTxtW)) / 2.0F;
         this.drawPersonSmall(r, m, offStartX, tabY + 6.0F, violetText);
         tabF.a("Оффлайн", offStartX + 10.0F, tabY + 5.0F, violetText, m);
         r.a(px + 13.0F + tabW, tabY, tabW, 20.0F, 4.0F, new Color(22, 22, 30), m);
         float msTxtW = tabF.a("Microsoft");
         float msStartX = px + 13.0F + tabW + (tabW - msTxtW) / 2.0F;
         tabF.a("Microsoft", msStartX, tabY + 5.0F, new Color(80, 80, 95), m);
         float inputY = tabY + 26.0F;
         r.a(px + 9.0F, inputY, pw - 18.0F, 22.0F, 4.0F, new Color(8, 8, 12, 255), m);
         r.a(px + 9.0F, inputY, pw - 18.0F, 22.0F, 4.0F, 0.8F, new Color(50, 42, 90, 200), m);
         String cursor = System.currentTimeMillis() % 1000L > 500L ? "|" : "";
         FontRenderer af = FontManager.b[13];
         if (this.currentInputText.isEmpty()) {
            af.a(cursor, px + 15.0F, inputY + 5.0F, new Color(180, 180, 200), m);
         } else {
            af.a(this.currentInputText + cursor, px + 15.0F, inputY + 5.0F, Color.WHITE, m);
         }

         float btnY = py + ph - 26.0F;
         boolean btnH = this.isHovered(px + 9.0F, btnY, pw - 18.0F, 20.0F, mx, my);
         r.a(px + 9.0F, btnY, pw - 18.0F, 20.0F, 4.0F, btnH ? new Color(52, 40, 95) : new Color(38, 30, 72), m);
         FontRenderer plusFont = FontManager.b[16];
         String textStr = "Добавить";
         float txtW = af.a(textStr);
         float startX = px + 9.0F + (pw - 18.0F - (10.0F + txtW)) / 2.0F;
         Color btnC = btnH ? Color.WHITE : violetText;
         plusFont.a("+", startX, btnY + 1.0F, btnC, m);
         af.a(textStr, startX + 10.0F, btnY + 4.0F, btnC, m);
      } else {
         int visibleCount = Math.min(3, ACCOUNTS.size());
         float rowH = 30.0F;
         float listH = visibleCount * rowH + (visibleCount > 0 ? (visibleCount - 1) * 3.0F : 0.0F);
         float pw = 215.0F;
         float ph = 66.0F + listH + (ACCOUNTS.isEmpty() ? 0.0F : 6.0F);
         float px = (this.width - pw) / 2.0F;
         float py = (this.height - ph) / 2.0F;
         float maxScroll = Math.max(0.0F, (ACCOUNTS.size() - 3) * (rowH + 3.0F));
         this.scrollTarget = Math.max(0.0F, Math.min(maxScroll, this.scrollTarget));
         this.scrollOffset = this.scrollOffset + (this.scrollTarget - this.scrollOffset) * 0.25F;
         r.a(px, py, pw, ph, 7.0F, new Color(20, 20, 28, 252), m);
         this.drawPeopleSmall(r, m, px + 9.0F, py + 11.0F, new Color(160, 165, 185));
         FontRenderer hf = FontManager.b[14];
         hf.a("Аккаунты", px + 26.0F, py + 8.0F, Color.WHITE, m);
         FontRenderer sf = FontManager.a[10];
         sf.a("Нажмите на аккаунт, чтобы войти в него", px + 9.0F, py + 23.0F, new Color(110, 110, 130), m);
         boolean xH = this.isHovered(px + pw - 16.0F, py + 7.0F, 13.0F, 13.0F, mx, my);
         hf.a("×", px + pw - 13.0F, py + 8.0F, xH ? new Color(255, 100, 100) : new Color(140, 140, 160), m);
         float cY = py + 36.0F;
         FontRenderer cf = FontManager.b[13];

         for (int i = 0; i < ACCOUNTS.size(); i++) {
            String acc = ACCOUNTS.get(i);
            float cy = cY + i * (rowH + 3.0F) - this.scrollOffset;
            if (!(cy + rowH < cY) && !(cy > cY + listH)) {
               boolean cH = this.isHovered(px + 9.0F, cy, pw - 18.0F, rowH, mx, my);
               r.a(px + 9.0F, cy, pw - 18.0F, rowH, 5.0F, cH ? new Color(34, 34, 52, 240) : new Color(22, 22, 32, 220), m);
               Identifier skin = this.getSkinTexture(acc);
               RenderSystemHelper.setShaderTexture(0, skin);
               r.a(skin, px + 12.0F, cy + 4.0F, 20.0F, 20.0F, 3.0F, 0.125F, 0.125F, 0.125F, 0.125F, Color.WHITE, m);
               r.a(skin, px + 12.0F, cy + 4.0F, 20.0F, 20.0F, 3.0F, 0.625F, 0.125F, 0.125F, 0.125F, Color.WHITE, m);
               cf.a(acc, px + 37.0F, cy + 6.0F, Color.WHITE, m);
               FontRenderer tinyF = FontManager.a[9];
               float offLblX = px + 115.0F;
               this.drawPersonSmall(r, m, offLblX, cy + 9.0F, new Color(110, 110, 130));
               tinyF.a("Оффлайн", offLblX + 10.0F, cy + 8.0F, new Color(110, 110, 130), m);
               boolean edH = this.isHovered(px + 163.0F, cy + 6.0F, 12.0F, 12.0F, mx, my);
               Color pCol = edH ? Color.WHITE : new Color(130, 130, 150);
               r.a(px + 164.0F, cy + 10.0F, 4.0F, 1.1F, 0.2F, pCol, m);
               r.a(px + 166.0F, cy + 12.0F, 4.0F, 1.1F, 0.2F, pCol, m);
               boolean delH = this.isHovered(px + 177.0F, cy + 6.0F, 12.0F, 12.0F, mx, my);
               Color tCol = delH ? new Color(255, 90, 90) : new Color(130, 130, 150);
               r.a(px + 178.0F, cy + 8.0F, 5.0F, 1.1F, 0.2F, tCol, m);
               r.a(px + 179.0F, cy + 10.0F, 3.5F, 5.0F, 0.2F, tCol, m);
               boolean isActive = acc.equalsIgnoreCase(curU);
               r.a(px + 195.0F, cy + 11.0F, 7.0F, 7.0F, 3.5F, isActive ? new Color(50, 230, 90) : new Color(45, 45, 58), m);
            }
         }

         float aY = py + ph - 27.0F;
         FontRenderer af = FontManager.b[13];
         FontRenderer plusFont = FontManager.b[20];
         Color violetText = new Color(134, 110, 255);
         boolean aH = this.isHovered(px + 9.0F, aY, pw - 18.0F, 21.0F, mx, my);
         r.a(px + 9.0F, aY, pw - 18.0F, 21.0F, 5.0F, aH ? new Color(52, 40, 95) : new Color(38, 30, 72), m);
         Color iconC = aH ? Color.WHITE : violetText;
         String textStr = "Добавить аккаунт";
         float txtW = af.a(textStr);
         float totalW = 12.0F + txtW;
         float startX = px + 9.0F + (pw - 18.0F - totalW) / 2.0F - 3.0F;
         plusFont.a("+", startX, aY + 2.0F, iconC, m);
         af.a(textStr, startX + 12.0F, aY + 4.0F, iconC, m);
      }
   }

   private void drawGear(Renderer2D r, Object m, float cx, float cy, Color c, Color bg) {
      r.a(cx - 0.8F, cy - 6.0F, 1.6F, 2.5F, 0.3F, c, m);
      r.a(cx - 0.8F, cy + 3.5F, 1.6F, 2.5F, 0.3F, c, m);
      r.a(cx - 6.0F, cy - 0.8F, 2.5F, 1.6F, 0.3F, c, m);
      r.a(cx + 3.5F, cy - 0.8F, 2.5F, 1.6F, 0.3F, c, m);
      r.a(cx + 2.5F, cy - 5.0F, 2.0F, 2.0F, 0.3F, c, m);
      r.a(cx - 4.5F, cy - 5.0F, 2.0F, 2.0F, 0.3F, c, m);
      r.a(cx + 2.5F, cy + 3.0F, 2.0F, 2.0F, 0.3F, c, m);
      r.a(cx - 4.5F, cy + 3.0F, 2.0F, 2.0F, 0.3F, c, m);
      r.a(cx - 4.0F, cy - 4.0F, 8.0F, 8.0F, 4.0F, c, m);
      r.a(cx - 2.5F, cy - 2.5F, 5.0F, 5.0F, 2.5F, bg, m);
      r.a(cx - 1.0F, cy - 1.0F, 2.0F, 2.0F, 1.0F, c, m);
   }

   private void drawLang(Renderer2D r, Object m, float cx, float cy, Color c) {
      r.a(cx - 3.5F, cy - 1.0F, 1.2F, 7.0F, 0.2F, c, m);
      r.a(cx + 2.3F, cy - 1.0F, 1.2F, 7.0F, 0.2F, c, m);
      r.a(cx - 2.5F, cy - 5.5F, 5.0F, 1.2F, 0.2F, c, m);
      r.a(cx - 3.5F, cy - 4.0F, 1.2F, 3.5F, 0.2F, c, m);
      r.a(cx + 2.3F, cy - 4.0F, 1.2F, 3.5F, 0.2F, c, m);
      r.a(cx - 2.5F, cy + 0.5F, 5.0F, 1.2F, 0.2F, c, m);
   }

   private void drawMenu(Renderer2D r, Object m, float cx, float cy, Color c) {
      float lw = 10.0F;
      float lh = 1.3F;
      float gap = 3.5F;
      r.a(cx - lw / 2.0F, cy - gap - lh / 2.0F, lw, lh, 0.3F, c, m);
      r.a(cx - lw / 2.0F, cy - lh / 2.0F, lw, lh, 0.3F, c, m);
      r.a(cx - lw / 2.0F, cy + gap - lh / 2.0F, lw, lh, 0.3F, c, m);
   }

   private void drawPeople(Renderer2D r, Object m, float cx, float cy, Color c) {
      r.a(cx - 4.5F, cy - 5.0F, 3.5F, 3.5F, 1.75F, c, m);
      r.a(cx - 5.5F, cy - 0.5F, 5.5F, 4.5F, 1.5F, c, m);
      r.a(cx + 1.5F, cy - 4.0F, 3.0F, 3.0F, 1.5F, c, m);
      r.a(cx + 0.5F, cy + 0.0F, 5.0F, 4.0F, 1.5F, c, m);
   }

   private void drawExit(Renderer2D r, Object m, float cx, float cy, Color c) {
      r.a(cx - 5.0F, cy - 5.0F, 1.3F, 10.0F, 0.2F, c, m);
      r.a(cx - 5.0F, cy - 5.0F, 7.0F, 1.3F, 0.2F, c, m);
      r.a(cx - 5.0F, cy + 3.7F, 7.0F, 1.3F, 0.2F, c, m);
      r.a(cx - 1.0F, cy - 0.6F, 7.0F, 1.2F, 0.2F, c, m);
      r.a(cx + 4.0F, cy - 3.0F, 1.3F, 3.0F, 0.2F, c, m);
      r.a(cx + 4.0F, cy + 0.0F, 1.3F, 3.0F, 0.2F, c, m);
   }

   private void drawPersonSmall(Renderer2D r, Object m, float x, float y, Color c) {
      r.a(x + 2.0F, y - 2.0F, 3.0F, 3.0F, 1.5F, c, m);
      r.a(x + 0.5F, y + 2.0F, 6.0F, 4.0F, 1.5F, c, m);
   }

   private void drawPeopleSmall(Renderer2D r, Object m, float x, float y, Color c) {
      r.a(x + 1.5F, y - 2.0F, 3.0F, 3.0F, 1.5F, c, m);
      r.a(x, y + 2.0F, 6.0F, 4.0F, 1.5F, c, m);
      r.a(x + 6.0F, y - 1.0F, 2.5F, 2.5F, 1.25F, c, m);
      r.a(x + 5.0F, y + 2.5F, 5.0F, 3.5F, 1.0F, c, m);
   }

   public static Session createSession(String username) {
      if (username != null && !username.trim().isEmpty()) {
         String nick = username.trim();
         UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes(StandardCharsets.UTF_8));

         try {
            Constructor<?>[] ctors = Session.class.getDeclaredConstructors();

            for (Constructor<?> ctor : ctors) {
               ctor.setAccessible(true);
               Class<?>[] pTypes = ctor.getParameterTypes();
               if (pTypes.length == 6) {
                  Object accountTypeObj = null;
                  Class<?> atClass = pTypes[5];
                  if (atClass.isEnum()) {
                     Object[] constants = atClass.getEnumConstants();
                     if (constants != null && constants.length > 0) {
                        accountTypeObj = constants[0];
                     }
                  }

                  return (Session)ctor.newInstance(nick, offlineUuid, "0", Optional.empty(), Optional.empty(), accountTypeObj);
               }

               if (pTypes.length == 5) {
                  return (Session)ctor.newInstance(nick, offlineUuid, "0", Optional.empty(), Optional.empty());
               }
            }
         } catch (Throwable t) {
            DercoVisuals.getLOGGER().error("Failed to create session for " + nick, t);
         }

         return null;
      } else {
         return null;
      }
   }

   public static void setAccountSession(String username) {
      if (username != null && !username.trim().isEmpty()) {
         selectedAccount = username.trim();
         Session s = createSession(selectedAccount);
         if (s != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null) {
               try {
                  ((MinecraftClientAccessor)mc).setSession(s);
               } catch (Throwable var7) {
               }

               try {
                  for (Field f : MinecraftClient.class.getDeclaredFields()) {
                     if (f.getType() == Session.class) {
                        f.setAccessible(true);
                        f.set(mc, s);
                     }
                  }
               } catch (Throwable var8) {
               }
            }
         }
      }
   }

   private float aY_offset(float y) {
      return y + 7.0F;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.showAccountSwitcher && !this.isTypingAccount) {
         this.scrollTarget -= (float)(verticalAmount * 34.0);
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   public boolean mouseClicked(Click click, boolean bl) {
      int mx = (int)click.comp_4798();
      int my = (int)click.comp_4799();
      int w = this.width;
      int h = this.height;
      if (this.showAccountSwitcher) {
         if (this.isTypingAccount) {
            float pw = 210.0F;
            float ph = 126.0F;
            float px = (w - pw) / 2.0F;
            float py = (h - ph) / 2.0F;
            if (this.isHovered(px + 5.0F, py + 5.0F, 18.0F, 18.0F, mx, my)) {
               this.isTypingAccount = false;
               return true;
            } else if (this.isHovered(px + pw - 16.0F, py + 6.0F, 13.0F, 13.0F, mx, my)) {
               this.showAccountSwitcher = false;
               this.isTypingAccount = false;
               return true;
            } else {
               float btnY = py + ph - 26.0F;
               if (this.isHovered(px + 9.0F, btnY, pw - 18.0F, 20.0F, mx, my)) {
                  this.saveAndAddAccount();
                  return true;
               } else {
                  return true;
               }
            }
         } else {
            int visibleCount = Math.min(3, ACCOUNTS.size());
            float rowH = 30.0F;
            float listH = visibleCount * rowH + (visibleCount > 0 ? (visibleCount - 1) * 3.0F : 0.0F);
            float pw = 215.0F;
            float ph = 66.0F + listH + (ACCOUNTS.isEmpty() ? 0.0F : 6.0F);
            float px = (w - pw) / 2.0F;
            float py = (h - ph) / 2.0F;
            if (this.isHovered(px + pw - 16.0F, py + 7.0F, 13.0F, 13.0F, mx, my)) {
               this.showAccountSwitcher = false;
               return true;
            }

            float cY = py + 36.0F;

            for (int i = 0; i < ACCOUNTS.size(); i++) {
               float cy = cY + i * (rowH + 3.0F) - this.scrollOffset;
               if (!(cy + rowH < cY) && !(cy > cY + listH)) {
                  if (this.isHovered(px + 163.0F, cy + 6.0F, 12.0F, 12.0F, mx, my)) {
                     this.isTypingAccount = true;
                     this.editingAccountIndex = i;
                     this.currentInputText = ACCOUNTS.get(i);
                     return true;
                  }

                  if (this.isHovered(px + 177.0F, cy + 6.0F, 12.0F, 12.0F, mx, my)) {
                     if (ACCOUNTS.size() > 1) {
                        ACCOUNTS.remove(i);
                        if (i < ACCOUNTS.size()) {
                           setAccountSession(ACCOUNTS.get(0));
                        }
                     }

                     return true;
                  }

                  if (this.isHovered(px + 9.0F, cy, pw - 18.0F, rowH, mx, my)) {
                     setAccountSession(ACCOUNTS.get(i));
                     return true;
                  }
               }
            }

            float aY = py + ph - 27.0F;
            if (this.isHovered(px + 9.0F, aY, pw - 18.0F, 21.0F, mx, my)) {
               this.isTypingAccount = true;
               this.editingAccountIndex = -1;
               this.currentInputText = "";
               return true;
            }

            if (!this.isHovered(px, py, pw, ph, mx, my)) {
               this.showAccountSwitcher = false;
            }

            return true;
         }
      } else {
         String curUsr = this.client != null && this.client.getSession() != null ? this.client.getSession().getUsername() : "Player";
         FontRenderer fuC = FontManager.b[12];
         float bW = Math.max(80.0F, 32.0F + fuC.a(curUsr));
         if (this.isHovered(14.0F, 12.0F, bW, 20.0F, mx, my)) {
            this.showAccountSwitcher = true;
            this.isTypingAccount = false;
            return true;
         }

         float dropSz = 22.0F;
         float dropX = w - dropSz - 10.0F;
         if (this.isHovered(dropX, 10.0F, dropSz, dropSz, mx, my)) {
            useVanillaMenu = true;
            if (this.client != null) {
               this.client.setScreen(new TitleScreen());
            }

            return true;
         } else {
            float btnW = 240.0F;
            float btnH = 26.0F;
            float btnX = (w - btnW) / 2.0F;
            if (this.isHovered(btnX, h / 2.0F - 18.0F, btnW, btnH, mx, my)) {
               if (this.client != null) {
                  this.client.setScreen(new SelectWorldScreen(this));
               }

               return true;
            } else if (this.isHovered(btnX, h / 2.0F + 14.0F, btnW, btnH, mx, my)) {
               if (this.client != null) {
                  this.client.setScreen(new MultiplayerScreen(this));
               }

               return true;
            } else {
               float iSz = 22.0F;
               float iGap = 6.0F;
               int cnt = 5;
               float totIW = cnt * iSz + (cnt - 1) * iGap;
               float isx = (w - totIW) / 2.0F;
               float iY = h / 2.0F + 56.0F;

               for (int i = 0; i < cnt; i++) {
                  float bx = isx + i * (iSz + iGap);
                  if (this.isHovered(bx, iY, iSz, iSz, mx, my)) {
                     if (i == 0 && this.client != null) {
                        this.client.setScreen(new OptionsScreen(this, this.client.options));
                     } else if (i == 1 && this.client != null) {
                        this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
                     } else if (i == 3) {
                        this.showAccountSwitcher = !this.showAccountSwitcher;
                     } else if (i == 4 && this.client != null) {
                        this.client.scheduleStop();
                     }

                     return true;
                  }
               }

               return super.mouseClicked(click, bl);
            }
         }
      }
   }

   private void saveAndAddAccount() {
      if (this.currentInputText != null && !this.currentInputText.trim().isEmpty()) {
         String nick = this.currentInputText.trim();
         if (this.editingAccountIndex >= 0 && this.editingAccountIndex < ACCOUNTS.size()) {
            ACCOUNTS.set(this.editingAccountIndex, nick);
         } else if (!ACCOUNTS.contains(nick)) {
            ACCOUNTS.add(nick);
         }

         setAccountSession(nick);
      }

      this.isTypingAccount = false;
   }

   public boolean keyPressed(KeyInput keyInput) {
      if (this.showAccountSwitcher && this.isTypingAccount) {
         int key = keyInput.comp_4795();
         if (key == 256) {
            this.isTypingAccount = false;
            return true;
         }

         if (key == 257 || key == 335) {
            this.saveAndAddAccount();
            return true;
         }

         if (key == 259) {
            if (!this.currentInputText.isEmpty()) {
               this.currentInputText = this.currentInputText.substring(0, this.currentInputText.length() - 1);
            }

            return true;
         }
      }

      if (keyInput.comp_4795() == 256 && this.showAccountSwitcher) {
         this.showAccountSwitcher = false;
         return true;
      } else {
         return super.keyPressed(keyInput);
      }
   }

   public boolean charTyped(CharInput charInput) {
      if (this.showAccountSwitcher && this.isTypingAccount) {
         char chr = (char)charInput.comp_4793();
         boolean isAllowed = chr >= 'a' && chr <= 'z' || chr >= 'A' && chr <= 'Z' || chr >= '0' && chr <= '9' || chr == '_' || chr == '-';
         if (isAllowed && this.currentInputText.length() < 16) {
            this.currentInputText = this.currentInputText + chr;
            return true;
         } else {
            return true;
         }
      } else {
         return super.charTyped(charInput);
      }
   }

   private boolean isHovered(float x, float y, float w, float h, int mx, int my) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }
}
