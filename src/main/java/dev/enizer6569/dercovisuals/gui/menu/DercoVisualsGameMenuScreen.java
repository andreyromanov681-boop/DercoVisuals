// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.menu;

import java.awt.Color;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.DercoVisuals;

public class DercoVisualsGameMenuScreen extends Screen {
   private static final Color BTN_NORMAL = new Color(26, 26, 36, 210);
   private static final Color BTN_HOVER = new Color(42, 42, 58, 230);
   private static final Color RED_HOVER = new Color(248, 76, 76);
   private static final Color TEXT_NORMAL = new Color(150, 155, 175);
   private static final Color TEXT_HOVER = new Color(255, 255, 255);

   public DercoVisualsGameMenuScreen() {
      super(Text.literal("Menu"));
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      int w = this.width;
      int h = this.height;
      context.fill(0, 0, w, h, -1794504174);
      if (DercoVisuals.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(context);
      }

      Renderer2D r = DercoVisuals.getInstance().getRender();
      Matrix3x2fStack m = context.getMatrices();
      FontRenderer titleFont = FontManager.b[26];
      String titleStr = "Меню";
      float titleW = titleFont.a(titleStr);
      float logoY = h / 2.0F - 120.0F;
      titleFont.a(titleStr, (w - titleW) / 2.0F, logoY, Color.WHITE, m);
      float pw = 320.0F;
      float px = (w - pw) / 2.0F;
      float btnH = 30.0F;
      float gap = 6.0F;
      float halfW = (pw - gap) / 2.0F;
      FontRenderer bf = FontManager.b[14];
      float y1 = h / 2.0F - 75.0F;
      boolean h1 = this.isHovered(px, y1, pw, btnH, mouseX, mouseY);
      r.a(px, y1, pw, btnH, 5.0F, h1 ? BTN_HOVER : BTN_NORMAL, m);
      this.renderCenteredText(bf, "Вернуться к игре", px, y1, pw, btnH, h1 ? TEXT_HOVER : TEXT_NORMAL, m);
      float y2 = y1 + btnH + gap;
      boolean h2L = this.isHovered(px, y2, halfW, btnH, mouseX, mouseY);
      r.a(px, y2, halfW, btnH, 5.0F, h2L ? BTN_HOVER : BTN_NORMAL, m);
      this.renderCenteredText(bf, "Достижения", px, y2, halfW, btnH, h2L ? TEXT_HOVER : TEXT_NORMAL, m);
      float pxR = px + halfW + gap;
      boolean h2R = this.isHovered(pxR, y2, halfW, btnH, mouseX, mouseY);
      r.a(pxR, y2, halfW, btnH, 5.0F, h2R ? BTN_HOVER : BTN_NORMAL, m);
      this.renderCenteredText(bf, "Статистика", pxR, y2, halfW, btnH, h2R ? TEXT_HOVER : TEXT_NORMAL, m);
      float y3 = y2 + btnH + gap;
      boolean h3L = this.isHovered(px, y3, halfW, btnH, mouseX, mouseY);
      r.a(px, y3, halfW, btnH, 5.0F, h3L ? BTN_HOVER : BTN_NORMAL, m);
      this.renderCenteredText(bf, "Настройки", px, y3, halfW, btnH, h3L ? TEXT_HOVER : TEXT_NORMAL, m);
      boolean h3R = this.isHovered(pxR, y3, halfW, btnH, mouseX, mouseY);
      r.a(pxR, y3, halfW, btnH, 5.0F, h3R ? BTN_HOVER : BTN_NORMAL, m);
      this.renderCenteredText(bf, "Кейбинд менеджер", pxR, y3, halfW, btnH, h3R ? TEXT_HOVER : TEXT_NORMAL, m);
      float y4 = y3 + btnH + gap;
      boolean h4 = this.isHovered(px, y4, pw, btnH, mouseX, mouseY);
      Color redBg = h4 ? RED_HOVER : BTN_NORMAL;
      Color redText = h4 ? Color.WHITE : TEXT_NORMAL;
      r.a(px, y4, pw, btnH, 5.0F, redBg, m);
      String quitStr = "Сохранить и выйти";
      float qW = bf.a(quitStr);
      float iconW = 12.0F;
      float totW = iconW + 8.0F + qW;
      float startX = px + (pw - totW) / 2.0F;
      this.drawExitIcon(r, m, startX + 4.0F, y4 + btnH / 2.0F, redText);
      bf.a(quitStr, startX + iconW + 8.0F, y4 + 8.0F, redText, m);
      float iSz = 30.0F;
      float ix = (w - iSz) / 2.0F;
      float iy = y4 + btnH + 14.0F;
      boolean hLang = this.isHovered(ix, iy, iSz, iSz, mouseX, mouseY);
      r.a(ix, iy, iSz, iSz, 6.0F, hLang ? BTN_HOVER : BTN_NORMAL, m);
      float botX = 14.0F;
      float botY = h - 20.0F;
      FontRenderer crackF = FontManager.b[12];
      crackF.a("DercoVisuals visual @alex_sigma", botX, botY - 16.0F, new Color(210, 185, 255), m);
      FontRenderer botF = FontManager.a[13];
      botF.a("DercoVisuals Visuals 1.21.11", botX, botY, new Color(180, 185, 205, 210), m);
      super.render(context, mouseX, mouseY, delta);
   }

   private void renderCenteredText(FontRenderer f, String text, float x, float y, float w, float h, Color color, Object m) {
      float tw = f.a(text);
      f.a(text, x + (w - tw) / 2.0F, y + (h - 10.0F) / 2.0F - 1.0F, color, m);
   }

   private void drawExitIcon(Renderer2D r, Object m, float cx, float cy, Color c) {
      r.a(cx - 5.0F, cy - 5.0F, 1.2F, 10.0F, 0.2F, c, m);
      r.a(cx - 5.0F, cy - 5.0F, 5.0F, 1.2F, 0.2F, c, m);
      r.a(cx - 5.0F, cy + 3.8F, 5.0F, 1.2F, 0.2F, c, m);
      r.a(cx - 2.0F, cy - 0.6F, 6.0F, 1.2F, 0.2F, c, m);
      r.a(cx - 4.0F, cy - 3.0F, 1.2F, 2.5F, 0.2F, c, m);
      r.a(cx - 4.0F, cy + 0.5F, 1.2F, 2.5F, 0.2F, c, m);
   }

   private void drawLangIcon(Renderer2D r, Object m, float cx, float cy, Color c) {
      FontRenderer sf = FontManager.a[11];
      sf.a("文", cx - 7.0F, cy - 6.0F, c, m);
      FontRenderer sfA = FontManager.b[10];
      sfA.a("A", cx + 1.0F, cy + 1.0F, c, m);
   }

   public boolean mouseClicked(Click click, boolean bl) {
      int mx = (int)click.comp_4798();
      int my = (int)click.comp_4799();
      int w = this.width;
      int h = this.height;
      float pw = 320.0F;
      float px = (w - pw) / 2.0F;
      float btnH = 30.0F;
      float gap = 6.0F;
      float halfW = (pw - gap) / 2.0F;
      float y1 = h / 2.0F - 75.0F;
      float y2 = y1 + btnH + gap;
      float y3 = y2 + btnH + gap;
      float y4 = y3 + btnH + gap;
      float pxR = px + halfW + gap;
      if (this.isHovered(px, y1, pw, btnH, mx, my)) {
         if (this.client != null) {
            this.client.setScreen(null);
         }

         return true;
      } else if (this.isHovered(px, y2, halfW, btnH, mx, my)) {
         if (this.client != null && this.client.player != null && this.client.player.networkHandler != null) {
            this.client.setScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()));
         }

         return true;
      } else if (this.isHovered(pxR, y2, halfW, btnH, mx, my)) {
         if (this.client != null && this.client.player != null) {
            this.client.setScreen(new StatsScreen(this, this.client.player.getStatHandler()));
         }

         return true;
      } else if (this.isHovered(px, y3, halfW, btnH, mx, my)) {
         if (this.client != null) {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
         }

         return true;
      } else if (this.isHovered(pxR, y3, halfW, btnH, mx, my)) {
         if (this.client != null) {
            this.client.setScreen(new DercoVisualsKeybindManagerScreen(this));
         }

         return true;
      } else if (this.isHovered(px, y4, pw, btnH, mx, my)) {
         if (this.client != null) {
            boolean singleplayer = this.client.isInSingleplayer();
            if (this.client.world != null) {
               this.client.world.disconnect(Text.empty());
            }

            this.client.disconnect(Text.translatable("menu.savingLevel"));
            this.client.setScreen(new DercoVisualsMainMenuScreen());
         }

         return true;
      } else {
         float iSz = 30.0F;
         float ix = (w - iSz) / 2.0F;
         float iy = y4 + btnH + 14.0F;
         if (this.isHovered(ix, iy, iSz, iSz, mx, my)) {
            if (this.client != null) {
               this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
            }

            return true;
         } else {
            return super.mouseClicked(click, bl);
         }
      }
   }

   private boolean isHovered(float x, float y, float w, float h, int mx, int my) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }
}
