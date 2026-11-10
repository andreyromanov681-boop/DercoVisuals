package pulse.gui.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Matrix3x2fStack;
import pulse.client.MinecraftContext;
import pulse.module.ClientModule;
import pulse.module.ModuleRegistry;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.render.icons.IconTextureRegistry;
import pulse.util.KeyNameFormatter;
import ru.pulse.Pulse;

public class PulseKeybindManagerScreen extends Screen {
   private static final float ASSET_WIDTH = 1024.0F;
   private static final float ASSET_HEIGHT = 434.0F;
   private static final int MOUSE_CODE_BASE = 2000;
   private static final String[][] KEY_ROWS = new String[][]{
      {"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "BACK"},
      {"TAB", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"},
      {"CAPS", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "ENTER"},
      {"SHIFT", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "SHIFT"},
      {"CTRL", "FN", "ALT", "SPACE", "ALT", "FN", "MENU", "CTRL"}
   };
   private static final float[][] KEY_X = new float[][]{
      {195.0F, 238.0F, 281.0F, 324.0F, 367.0F, 410.0F, 452.0F, 495.0F, 538.0F, 581.0F, 624.0F, 666.0F, 709.0F, 752.0F},
      {195.0F, 268.0F, 311.0F, 354.0F, 397.0F, 440.0F, 482.0F, 525.0F, 568.0F, 611.0F, 654.0F, 697.0F, 739.0F, 782.0F},
      {195.0F, 274.0F, 317.0F, 360.0F, 403.0F, 446.0F, 489.0F, 531.0F, 574.0F, 617.0F, 660.0F, 703.0F, 746.0F},
      {195.0F, 286.0F, 329.0F, 372.0F, 415.0F, 458.0F, 501.0F, 544.0F, 587.0F, 629.0F, 672.0F, 715.0F},
      {195.0F, 263.0F, 306.0F, 367.0F, 613.0F, 674.0F, 717.0F, 773.0F}
   };
   private static final float[][] KEY_WIDTHS = new float[][]{
      {37.0F, 37.0F, 37.0F, 36.0F, 36.0F, 36.0F, 37.0F, 37.0F, 37.0F, 37.0F, 36.0F, 37.0F, 37.0F, 83.0F},
      {66.0F, 36.0F, 36.0F, 36.0F, 36.0F, 36.0F, 37.0F, 37.0F, 37.0F, 36.0F, 36.0F, 36.0F, 37.0F, 53.0F},
      {72.0F, 37.0F, 36.0F, 37.0F, 37.0F, 36.0F, 36.0F, 37.0F, 37.0F, 37.0F, 37.0F, 37.0F, 89.0F},
      {85.0F, 37.0F, 37.0F, 37.0F, 37.0F, 37.0F, 36.0F, 36.0F, 36.0F, 37.0F, 37.0F, 120.0F},
      {62.0F, 37.0F, 54.0F, 239.0F, 55.0F, 36.0F, 49.0F, 62.0F}
   };
   private static final float[] KEY_Y = new float[]{123.0F, 167.0F, 211.0F, 254.0F, 298.0F};
   private static final float[] KEY_HEIGHTS = new float[]{37.0F, 37.0F, 36.0F, 37.0F, 36.0F};
   public static final List<PulseKeybindManagerScreen.BindItem> BINDS = new ArrayList<>();
   private static final String[] MODULE_SUGGESTIONS = new String[]{
      "Fast Exp",
      "Sprint",
      "Free Look",
      "Auto Eat",
      "Elytra Swap",
      "Full Bright",
      "Target Hud",
      "Watermark",
      "Target Esp",
      "Crosshair",
      "Shulker Preview",
      "Auto Potion",
      "Auto Respawn",
      "Auto Reconnect",
      "Item Scroller",
      "Sound Controller",
      "Streamer Mode",
      "Auto Leave",
      "Item Swap",
      "Auto Invest",
      "Block Overlay"
   };
   private final Screen parent;
   private int selectedKeyCode = -1;
   private String selectedKeyName = null;
   private float popoverX = 0.0F;
   private float popoverY = 0.0F;
   private int popoverStep = 0;
   private int actionTab = 0;
   private String commandInput = "/";
   private String functionInput = "";
   private boolean isQuickBindActive = false;
   private int quickBindStep = 0;
   private String quickBindKeyLabel = null;
   private static final Color VIOLET_ACCENT = new Color(110, 70, 255);
   private static final Color KEY_HOVER = new Color(50, 50, 75, 180);
   private static final Color KEY_BG = new Color(28, 28, 40, 250);
   private static final Color TEXT_MUTED = new Color(150, 155, 180);

   public PulseKeybindManagerScreen(Screen parent) {
      super(Text.literal("Keybind Manager"));
      this.parent = parent;
   }

   private boolean isBound(String keyName) {
      for (PulseKeybindManagerScreen.BindItem b : BINDS) {
         if (b.keyName.equalsIgnoreCase(keyName)) {
            return true;
         }
      }

      return false;
   }

   public static void executeKeyBind(int keyCode) {
      executeKeyBind(keyCode, KeyNameFormatter.format(keyCode));
   }

   public static void executeMouseBind(int button) {
      executeKeyBind(2000 + button, "M" + (button + 1));
   }

   private static void executeKeyBind(int keyCode, String keyName) {
      for (PulseKeybindManagerScreen.BindItem bind : List.copyOf(BINDS)) {
         if (matchesKey(bind, keyCode, keyName)) {
            if (!bind.actionType.equalsIgnoreCase("Команда")) {
               ClientModule module = findModule(bind.actionText);
               if (module != null) {
                  Pulse.getLOGGER().info("[KeybindManager] Toggling module '{}' from {}", module.name(), bind.keyName);
                  module.toggle();
               } else {
                  Pulse.getLOGGER().warn("[KeybindManager] Module not found: {}", bind.actionText);
               }
            } else if (MinecraftContext.c.player != null && MinecraftContext.c.player.networkHandler != null) {
               String command = bind.actionText.trim();

               while (command.startsWith("/")) {
                  command = command.substring(1);
               }

               if (!command.isEmpty()) {
                  Pulse.getLOGGER().info("[KeybindManager] Executing command /{} from {}", command, bind.keyName);
                  MinecraftContext.c.player.networkHandler.sendChatCommand(command);
               }
            }
         }
      }
   }

   private static boolean matchesKey(PulseKeybindManagerScreen.BindItem bind, int keyCode, String keyName) {
      if (bind.keyCode == keyCode) {
         return true;
      } else {
         return !isModifierGroup(bind.keyCode, keyCode, 340, 344)
               && !isModifierGroup(bind.keyCode, keyCode, 341, 345)
               && !isModifierGroup(bind.keyCode, keyCode, 342, 346)
            ? bind.keyCode == -1 && normalizeKeyName(bind.keyName).equals(normalizeKeyName(keyName))
            : true;
      }
   }

   private static boolean isModifierGroup(int boundCode, int pressedCode, int leftCode, int rightCode) {
      return (boundCode == leftCode || boundCode == rightCode) && (pressedCode == leftCode || pressedCode == rightCode);
   }

   private static ClientModule findModule(String name) {
      String normalized = normalizeModuleName(name);

      for (ClientModule module : ModuleRegistry.all()) {
         if (normalizeModuleName(module.name()).equals(normalized)) {
            return module;
         }
      }

      return null;
   }

   private static String normalizeModuleName(String name) {
      return name == null ? "" : name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
   }

   private static String normalizeKeyName(String keyName) {
      if (keyName == null) {
         return "";
      }

      return switch (keyName.toUpperCase()) {
         case "LSHIFT", "RSHIFT" -> "SHIFT";
         case "LCTRL", "RCTRL" -> "CTRL";
         case "LALT", "RALT" -> "ALT";
         default -> keyName.toUpperCase();
      };
   }

   private String getBestSuggestion(String input) {
      if (input != null && !input.trim().isEmpty()) {
         String clean = input.trim().toLowerCase();

         for (String m : MODULE_SUGGESTIONS) {
            if (m.toLowerCase().startsWith(clean)) {
               return m;
            }
         }

         return "";
      } else {
         return "";
      }
   }

   private void openKeyPopover(int keyCode, String label, float px, float py) {
      this.selectedKeyCode = keyCode;
      if (this.isQuickBindActive && this.quickBindStep == 1) {
         this.quickBindKeyLabel = "Кнопка " + label;
         this.selectedKeyName = label;
         this.quickBindStep = 2;
      } else {
         this.selectedKeyName = label;
         this.popoverX = px;
         this.popoverY = py;
         this.popoverStep = 1;
         this.isQuickBindActive = false;
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      int w = this.width;
      int h = this.height;
      context.fill(0, 0, w, h, -183825644);
      if (Pulse.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(context);
      }

      Renderer2D r = Pulse.getInstance().getRender();
      Matrix3x2fStack m = context.getMatrices();
      float scale = Math.min((w - 20.0F) / 1024.0F, (h - 20.0F) / 434.0F);
      float layoutW = 1024.0F * scale;
      float layoutH = 434.0F * scale;
      float layoutX = (w - layoutW) / 2.0F;
      float layoutY = (h - layoutH) / 2.0F;
      Identifier bgTex = IconTextureRegistry.get("keybind_bg");
      if (bgTex != null) {
         Renderer2DImpl.setTextureFilter(bgTex, true);
         r.a(bgTex, layoutX, layoutY, layoutW, layoutH, Color.WHITE, m);
      }

      this.renderKeyboardHighlights(r, m, layoutX, layoutY, scale, mouseX, mouseY);
      float listX = this.assetX(layoutX, scale, 13.0F);
      float listY = this.assetY(layoutY, scale, 123.0F);
      FontRenderer bTitleF = FontManager.b[13];
      FontRenderer bSubF = FontManager.a[10];
      FontRenderer badgeF = FontManager.b[11];

      for (int i = 0; i < BINDS.size(); i++) {
         PulseKeybindManagerScreen.BindItem item = BINDS.get(i);
         float cardY = listY + i * 42.0F * scale;
         if (cardY + 38.0F * scale > this.assetY(layoutY, scale, 340.0F)) {
            break;
         }

         r.a(listX, cardY, 151.0F * scale, 38.0F * scale, 6.0F * scale, new Color(26, 26, 38, 240), m);
         bTitleF.a(item.actionText, listX + 8.0F * scale, cardY + 6.0F * scale, Color.WHITE, m);
         bSubF.a(item.actionType, listX + 8.0F * scale, cardY + 21.0F * scale, TEXT_MUTED, m);
         float bgW = badgeF.a(item.keyName) + 14.0F * scale;
         r.a(listX + 151.0F * scale - bgW - 6.0F * scale, cardY + 8.0F * scale, bgW, 22.0F * scale, 4.0F * scale, VIOLET_ACCENT, m);
         badgeF.a(item.keyName, listX + 151.0F * scale - bgW, cardY + 12.0F * scale, Color.WHITE, m);
      }

      float pillX = this.assetX(layoutX, scale, 748.0F);
      float headerY = this.assetY(layoutY, scale, 80.0F);
      if (this.isQuickBindActive) {
         this.renderQuickBindPopovers(r, m, pillX - 75.0F * scale, headerY + 30.0F * scale, mouseX, mouseY);
      } else if (this.popoverStep > 0 && this.selectedKeyName != null) {
         this.renderPopovers(r, m, mouseX, mouseY);
      }

      super.render(context, mouseX, mouseY, delta);
   }

   private void renderKeyboardHighlights(Renderer2D r, Object m, float layoutX, float layoutY, float scale, int mx, int my) {
      FontRenderer kf = FontManager.b[12];

      for (int row = 0; row < KEY_ROWS.length; row++) {
         float ry = this.assetY(layoutY, scale, KEY_Y[row]);
         float keyH = KEY_HEIGHTS[row] * scale;

         for (int col = 0; col < KEY_ROWS[row].length; col++) {
            String label = KEY_ROWS[row][col];
            float rx = this.assetX(layoutX, scale, KEY_X[row][col]);
            float kw = KEY_WIDTHS[row][col] * scale;
            boolean hov = this.isHovered(rx, ry, kw, keyH, mx, my);
            boolean bound = this.isBound(label);
            boolean isSelected = label.equalsIgnoreCase(this.selectedKeyName) && (this.popoverStep > 0 || this.isQuickBindActive);
            if (!bound && !isSelected) {
               if (hov) {
                  r.a(rx, ry, kw, keyH, 4.0F * scale, KEY_HOVER, m);
               }
            } else {
               Color bg = bound ? VIOLET_ACCENT : new Color(80, 60, 150);
               r.a(rx, ry, kw, keyH, 4.0F * scale, bg, m);
               float txtW = kf.a(label);
               float txtH = kf.b(label);
               kf.a(label, rx + (kw - txtW) / 2.0F, ry + (keyH - txtH) / 2.0F, Color.WHITE, m);
            }
         }
      }
   }

   private float getLayoutScale() {
      return Math.min((this.width - 20.0F) / 1024.0F, (this.height - 20.0F) / 434.0F);
   }

   private float assetX(float layoutX, float scale, float x) {
      return layoutX + x * scale;
   }

   private float assetY(float layoutY, float scale, float y) {
      return layoutY + y * scale;
   }

   private float layoutX() {
      float scale = this.getLayoutScale();
      return (this.width - 1024.0F * scale) / 2.0F;
   }

   private float layoutY() {
      float scale = this.getLayoutScale();
      return (this.height - 434.0F * scale) / 2.0F;
   }

   private void renderQuickBindPopovers(Renderer2D r, Object m, float qx, float qy, int mx, int my) {
      FontRenderer bf = FontManager.b[15];
      FontRenderer sf = FontManager.a[11];
      float q1W = 240.0F;
      float q1H = 95.0F;
      r.a(qx - 4.0F, qy + 10.0F, 4.0F, 8.0F, 1.0F, new Color(22, 22, 34, 252), m);
      r.a(qx, qy, q1W, q1H, 8.0F, new Color(22, 22, 34, 252), m);
      r.a(qx, qy, q1W, q1H, 8.0F, 1.2F, new Color(55, 50, 85, 220), m);
      bf.a("⚡ Быстрый бинд", qx + 14.0F, qy + 11.0F, Color.WHITE, m);
      sf.a("Нажмите на кнопку / клавишу на которую", qx + 12.0F, qy + 31.0F, TEXT_MUTED, m);
      sf.a("хотите применить бинд", qx + 12.0F, qy + 43.0F, TEXT_MUTED, m);
      float btnY = qy + 58.0F;
      if (this.quickBindStep >= 2 && this.quickBindKeyLabel != null) {
         r.a(qx + 12.0F, btnY, 216.0F, 28.0F, 6.0F, VIOLET_ACCENT, m);
         FontRenderer kF = FontManager.b[13];
         float txtW = kF.a(this.quickBindKeyLabel);
         kF.a(this.quickBindKeyLabel, qx + (216.0F - txtW) / 2.0F, btnY + 7.0F, Color.WHITE, m);
      } else {
         r.a(qx + 12.0F, btnY, 216.0F, 28.0F, 6.0F, new Color(26, 26, 38), m);
         FontRenderer plF = FontManager.b[12];
         plF.a("КЛАВИША / КНОПКА", qx + 55.0F, btnY + 7.0F, new Color(110, 110, 135), m);
      }

      if (this.quickBindStep >= 2) {
         float my2 = qy + q1H + 8.0F;
         this.renderActionModal(r, m, qx, my2, mx, my);
      }
   }

   private void renderPopovers(Renderer2D r, Object m, int mx, int my) {
      FontRenderer bf = FontManager.b[13];
      float p1W = 230.0F;
      float p1H = 36.0F;
      float px1 = this.popoverX;
      float py1 = this.popoverY;
      r.a(px1 + 24.0F, py1 - 4.0F, 8.0F, 4.0F, 1.0F, new Color(22, 22, 34, 252), m);
      r.a(px1, py1, p1W, p1H, 6.0F, new Color(22, 22, 34, 252), m);
      r.a(px1, py1, p1W, p1H, 6.0F, 1.2F, new Color(55, 50, 85, 220), m);
      FontRenderer badgeF = FontManager.b[12];
      float kw = badgeF.a(this.selectedKeyName) + 14.0F;
      r.a(px1 + 8.0F, py1 + 6.0F, kw, 24.0F, 4.0F, new Color(34, 34, 48), m);
      badgeF.a(this.selectedKeyName, px1 + 15.0F, py1 + 10.0F, Color.WHITE, m);
      bf.a("Клавиша", px1 + 15.0F + kw + 8.0F, py1 + 10.0F, Color.WHITE, m);
      float addBtnX = px1 + p1W - 84.0F;
      float addBtnY = py1 + 6.0F;
      boolean addH = this.isHovered(addBtnX, addBtnY, 76.0F, 24.0F, mx, my);
      r.a(addBtnX, addBtnY, 76.0F, 24.0F, 5.0F, addH ? VIOLET_ACCENT : new Color(36, 30, 56), m);
      FontRenderer addF = FontManager.b[12];
      addF.a("+ Добавить", addBtnX + 8.0F, addBtnY + 5.0F, addH ? Color.WHITE : new Color(160, 150, 220), m);
      if (this.popoverStep >= 2) {
         float my2 = py1 + 42.0F;
         this.renderActionModal(r, m, px1, my2, mx, my);
      }
   }

   private void renderActionModal(Renderer2D r, Object m, float mx2, float my2, int mx, int my) {
      FontRenderer sf = FontManager.a[11];
      float m2W = 245.0F;
      float m2H = 155.0F;
      r.a(mx2 + 24.0F, my2 - 4.0F, 8.0F, 4.0F, 1.0F, new Color(22, 22, 34, 252), m);
      r.a(mx2, my2, m2W, m2H, 8.0F, new Color(22, 22, 34, 252), m);
      r.a(mx2, my2, m2W, m2H, 8.0F, 1.2F, new Color(60, 55, 95, 230), m);
      r.a(mx2 + 10.0F, my2 + 10.0F, 16.0F, 16.0F, 4.0F, VIOLET_ACCENT, m);
      FontRenderer iconF = FontManager.b[12];
      iconF.a("+", mx2 + 15.0F, my2 + 10.0F, Color.WHITE, m);
      FontRenderer mTitleF = FontManager.b[15];
      mTitleF.a("Добавление действия", mx2 + 32.0F, my2 + 9.0F, Color.WHITE, m);
      sf.a("Выберите действие, которое должно", mx2 + 10.0F, my2 + 29.0F, TEXT_MUTED, m);
      sf.a("выполняться при нажатии на выбранную кнопку.", mx2 + 10.0F, my2 + 41.0F, TEXT_MUTED, m);
      float tabY = my2 + 58.0F;
      float tabW = 108.0F;
      float tabH = 26.0F;
      boolean t0H = this.isHovered(mx2 + 10.0F, tabY, tabW, tabH, mx, my);
      Color t0Bg = this.actionTab == 0 ? VIOLET_ACCENT : (t0H ? KEY_HOVER : KEY_BG);
      r.a(mx2 + 10.0F, tabY, tabW, tabH, 5.0F, t0Bg, m);
      FontRenderer tabF = FontManager.b[12];
      tabF.a("/A  Команда", mx2 + 18.0F, tabY + 6.0F, this.actionTab != 0 && !t0H ? TEXT_MUTED : Color.WHITE, m);
      boolean t1H = this.isHovered(mx2 + 125.0F, tabY, tabW, tabH, mx, my);
      Color t1Bg = this.actionTab == 1 ? VIOLET_ACCENT : (t1H ? KEY_HOVER : KEY_BG);
      r.a(mx2 + 125.0F, tabY, tabW, tabH, 5.0F, t1Bg, m);
      tabF.a(">-  Функция", mx2 + 133.0F, tabY + 6.0F, this.actionTab != 1 && !t1H ? TEXT_MUTED : Color.WHITE, m);
      float inY = tabY + 32.0F;
      r.a(mx2 + 10.0F, inY, 225.0F, 24.0F, 4.0F, new Color(16, 16, 26, 255), m);
      r.a(mx2 + 10.0F, inY, 225.0F, 24.0F, 4.0F, 1.0F, new Color(70, 60, 110, 200), m);
      String cursor = System.currentTimeMillis() % 1000L > 500L ? "|" : "";
      FontRenderer inF = FontManager.b[13];
      if (this.actionTab == 0) {
         inF.a(this.commandInput + cursor, mx2 + 16.0F, inY + 5.0F, Color.WHITE, m);
      } else {
         String curText = this.functionInput;
         String best = this.getBestSuggestion(curText);
         if (!curText.isEmpty()) {
            inF.a(curText + cursor, mx2 + 16.0F, inY + 5.0F, Color.WHITE, m);
            if (!best.isEmpty() && best.toLowerCase().startsWith(curText.toLowerCase())) {
               String ghost = best.substring(curText.length());
               float typedW = inF.a(curText + cursor);
               inF.a(ghost, mx2 + 16.0F + typedW, inY + 5.0F, new Color(110, 110, 135), m);
            }
         } else {
            inF.a("Fast Exp" + cursor, mx2 + 16.0F, inY + 5.0F, new Color(110, 110, 135), m);
         }
      }

      float saveY = inY + 30.0F;
      String curVal = this.actionTab == 0
         ? this.commandInput.trim()
         : (this.getBestSuggestion(this.functionInput).isEmpty() ? this.functionInput.trim() : this.getBestSuggestion(this.functionInput));
      boolean canSave = this.actionTab == 0 ? curVal.length() > 1 : !curVal.isEmpty();
      boolean saveH = canSave && this.isHovered(mx2 + 10.0F, saveY, 225.0F, 22.0F, mx, my);
      r.a(mx2 + 10.0F, saveY, 225.0F, 22.0F, 5.0F, saveH ? VIOLET_ACCENT : new Color(32, 28, 50), m);
      FontRenderer sF = FontManager.b[12];
      sF.a("+ Добавить", mx2 + 86.0F, saveY + 4.0F, saveH ? Color.WHITE : (canSave ? new Color(160, 150, 220) : TEXT_MUTED), m);
   }

   private void saveCurrentBind() {
      if (this.selectedKeyName != null) {
         String typeStr = this.actionTab == 0 ? "Команда" : "Функция";
         String textVal;
         if (this.actionTab == 0) {
            textVal = this.commandInput.trim();
         } else {
            String sugg = this.getBestSuggestion(this.functionInput);
            textVal = sugg.isEmpty() ? (this.functionInput.trim().isEmpty() ? "Fast Exp" : this.functionInput.trim()) : sugg;
         }

         if (!textVal.isEmpty()) {
            BINDS.add(new PulseKeybindManagerScreen.BindItem(this.selectedKeyCode, this.selectedKeyName, typeStr, textVal));
            Pulse.getLOGGER().info("[KeybindManager] Bound {} ({}) to {} '{}'", this.selectedKeyName, this.selectedKeyCode, typeStr, textVal);
         }

         this.popoverStep = 0;
         this.isQuickBindActive = false;
         this.quickBindStep = 0;
         this.selectedKeyName = null;
      }
   }

   public boolean mouseClicked(Click click, boolean bl) {
      int mx = (int)click.comp_4798();
      int my = (int)click.comp_4799();
      float scale = this.getLayoutScale();
      float layoutX = this.layoutX();
      float layoutY = this.layoutY();
      float pillW = 98.0F * scale;
      float pillH = 27.0F * scale;
      float pillX = this.assetX(layoutX, scale, 748.0F);
      float headerY = this.assetY(layoutY, scale, 80.0F);
      if (this.isHovered(pillX, headerY, pillW, pillH, mx, my)) {
         this.isQuickBindActive = true;
         this.quickBindStep = 1;
         this.quickBindKeyLabel = null;
         this.selectedKeyName = null;
         this.popoverStep = 0;
         return true;
      }

      if (this.isQuickBindActive && this.quickBindStep >= 2) {
         float qx = pillX - 75.0F * scale;
         float qy = headerY + 30.0F * scale;
         float my2 = qy + 95.0F + 8.0F;
         float tabY = my2 + 58.0F;
         if (this.isHovered(qx + 10.0F, tabY, 108.0F, 26.0F, mx, my)) {
            this.actionTab = 0;
            return true;
         }

         if (this.isHovered(qx + 125.0F, tabY, 108.0F, 26.0F, mx, my)) {
            this.actionTab = 1;
            return true;
         }

         float saveY = tabY + 62.0F;
         if (this.isHovered(qx + 10.0F, saveY, 225.0F, 22.0F, mx, my)) {
            this.saveCurrentBind();
            return true;
         }
      }

      if (this.popoverStep > 0 && this.selectedKeyName != null) {
         float px1 = this.popoverX;
         float py1 = this.popoverY;
         if (this.isHovered(px1 + 146.0F, py1 + 6.0F, 76.0F, 24.0F, mx, my)) {
            this.popoverStep = 2;
            this.actionTab = 0;
            this.commandInput = "/";
            return true;
         }

         if (this.popoverStep >= 2) {
            float mx2 = px1;
            float my2 = py1 + 42.0F;
            float tabY = my2 + 58.0F;
            if (this.isHovered(mx2 + 10.0F, tabY, 108.0F, 26.0F, mx, my)) {
               this.actionTab = 0;
               return true;
            }

            if (this.isHovered(mx2 + 125.0F, tabY, 108.0F, 26.0F, mx, my)) {
               this.actionTab = 1;
               return true;
            }

            float saveY = tabY + 62.0F;
            if (this.isHovered(mx2 + 10.0F, saveY, 225.0F, 22.0F, mx, my)) {
               this.saveCurrentBind();
               return true;
            }
         }
      }

      if (this.isHovered(this.assetX(layoutX, scale, 12.0F), this.assetY(layoutY, scale, 80.0F), 155.0F * scale, 27.0F * scale, mx, my)) {
         if (this.client != null) {
            this.client.setScreen(this.parent);
         }

         return true;
      } else {
         for (int row = 0; row < KEY_ROWS.length; row++) {
            float ry = this.assetY(layoutY, scale, KEY_Y[row]);
            float keyH = KEY_HEIGHTS[row] * scale;

            for (int col = 0; col < KEY_ROWS[row].length; col++) {
               String label = KEY_ROWS[row][col];
               float rx = this.assetX(layoutX, scale, KEY_X[row][col]);
               float kw = KEY_WIDTHS[row][col] * scale;
               if (this.isHovered(rx, ry, kw, keyH, mx, my)) {
                  this.openKeyPopover(this.keyCodeFor(row, col), label, rx, ry + keyH + 6.0F * scale);
                  return true;
               }
            }
         }

         float mcx = this.assetX(layoutX, scale, 936.0F);
         float mcy = this.assetY(layoutY, scale, 240.0F);
         if (this.isHovered(mcx - 30.0F * scale, mcy - 65.0F * scale, 20.0F * scale, 32.0F * scale, mx, my)) {
            this.openKeyPopover(2000, "M1", mcx - 30.0F * scale, mcy - 30.0F * scale);
            return true;
         }

         if (this.isHovered(mcx + 10.0F * scale, mcy - 65.0F * scale, 20.0F * scale, 32.0F * scale, mx, my)) {
            this.openKeyPopover(2001, "M2", mcx + 10.0F * scale, mcy - 30.0F * scale);
            return true;
         }

         if (this.isHovered(mcx - 7.0F * scale, mcy - 58.0F * scale, 14.0F * scale, 24.0F * scale, mx, my)) {
            this.openKeyPopover(2002, "M3", mcx - 10.0F * scale, mcy - 30.0F * scale);
            return true;
         }

         if (this.isHovered(mcx - 39.0F * scale, mcy - 14.0F * scale, 9.0F * scale, 20.0F * scale, mx, my)) {
            this.openKeyPopover(2003, "M4", mcx - 40.0F * scale, mcy - 10.0F * scale);
            return true;
         }

         if (this.isHovered(mcx - 39.0F * scale, mcy + 14.0F * scale, 9.0F * scale, 20.0F * scale, mx, my)) {
            this.openKeyPopover(2004, "M5", mcx - 40.0F * scale, mcy + 20.0F * scale);
            return true;
         }

         if (this.isHovered(mcx + 30.0F * scale, mcy + 14.0F * scale, 9.0F * scale, 20.0F * scale, mx, my)) {
            this.openKeyPopover(2005, "M6", mcx + 30.0F * scale, mcy + 20.0F * scale);
            return true;
         }

         if (this.isHovered(mcx + 30.0F * scale, mcy - 14.0F * scale, 9.0F * scale, 20.0F * scale, mx, my)) {
            this.openKeyPopover(2006, "M7", mcx + 30.0F * scale, mcy - 10.0F * scale);
            return true;
         }

         if (this.isHovered(this.assetX(layoutX, scale, 195.0F), this.assetY(layoutY, scale, 352.0F), 165.0F * scale, 38.0F * scale, mx, my)) {
            BINDS.clear();
            this.popoverStep = 0;
            this.isQuickBindActive = false;
            this.selectedKeyName = null;
            return true;
         }

         if (!this.isQuickBindActive) {
            this.popoverStep = 0;
            this.selectedKeyName = null;
         }

         return super.mouseClicked(click, bl);
      }
   }

   public boolean charTyped(CharInput charInput) {
      if (this.popoverStep >= 2 || this.isQuickBindActive && this.quickBindStep >= 2) {
         char chr = (char)charInput.comp_4793();
         if (this.actionTab == 0) {
            if (chr >= ' ' && chr != 127 && this.commandInput.length() < 30) {
               this.commandInput = this.commandInput + chr;
               return true;
            }
         } else if (chr >= ' ' && chr != 127 && this.functionInput.length() < 30) {
            this.functionInput = this.functionInput + chr;
            return true;
         }

         return true;
      } else {
         return super.charTyped(charInput);
      }
   }

   public boolean keyPressed(KeyInput keyInput) {
      int key = keyInput.comp_4795();
      if (this.isQuickBindActive && this.quickBindStep == 1) {
         if (key == 256) {
            this.isQuickBindActive = false;
            this.quickBindStep = 0;
            return true;
         } else {
            String kName = this.getKeyNameFromCode(key);
            this.selectedKeyCode = key;
            this.quickBindKeyLabel = "Кнопка " + kName;
            this.selectedKeyName = kName;
            this.quickBindStep = 2;
            return true;
         }
      } else {
         if (this.popoverStep >= 2 || this.isQuickBindActive && this.quickBindStep >= 2) {
            if (key == 256) {
               this.popoverStep = 0;
               this.isQuickBindActive = false;
               this.quickBindStep = 0;
               this.selectedKeyName = null;
               return true;
            }

            if (key == 257 || key == 335 || key == 258) {
               if (this.actionTab == 1) {
                  String best = this.getBestSuggestion(this.functionInput);
                  if (!best.isEmpty()) {
                     this.functionInput = best;
                  }
               }

               this.saveCurrentBind();
               return true;
            }

            if (key == 259) {
               if (this.actionTab == 0) {
                  if (this.commandInput.length() > 1) {
                     this.commandInput = this.commandInput.substring(0, this.commandInput.length() - 1);
                  }
               } else if (!this.functionInput.isEmpty()) {
                  this.functionInput = this.functionInput.substring(0, this.functionInput.length() - 1);
               }

               return true;
            }
         }

         if (key == 256) {
            if (this.client != null) {
               this.client.setScreen(this.parent);
            }

            return true;
         } else {
            return super.keyPressed(keyInput);
         }
      }
   }

   private String getKeyNameFromCode(int keyCode) {
      if (keyCode != -1 && keyCode >= 0) {
         String name = KeyNameFormatter.format(keyCode);
         return name.isEmpty() ? "KEY_" + keyCode : name;
      } else {
         return "KEY_" + keyCode;
      }
   }

   private int keyCodeFor(int row, int col) {
      String label = KEY_ROWS[row][col];
      if (label.length() == 1) {
         char c = label.charAt(0);
         if (c >= 'A' && c <= 'Z') {
            return c;
         }

         if (c >= '0' && c <= '9') {
            return c;
         }
      }
      return switch (label) {
         case "`" -> 96;
         case "-" -> 45;
         case "=" -> 61;
         case "BACK" -> 259;
         case "TAB" -> 258;
         case "[" -> 91;
         case "]" -> 93;
         case "\\" -> 92;
         case "CAPS" -> 280;
         case ";" -> 59;
         case "'" -> 39;
         case "ENTER" -> 257;
         case "," -> 44;
         case "." -> 46;
         case "/" -> 47;
         case "SPACE" -> 32;
         case "MENU" -> 348;
         case "SHIFT" -> col == 0 ? 340 : 344;
         case "CTRL" -> col == 0 ? 341 : 345;
         case "ALT" -> col < 4 ? 342 : 346;
         default -> -1;
      };
   }

   private boolean isHovered(float x, float y, float w, float h, int mx, int my) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   public static class BindItem {
      public int keyCode;
      public String keyName;
      public String actionType;
      public String actionText;

      public BindItem(int keyCode, String keyName, String actionType, String actionText) {
         this.keyCode = keyCode;
         this.keyName = keyName;
         this.actionType = actionType;
         this.actionText = actionText;
      }
   }
}
