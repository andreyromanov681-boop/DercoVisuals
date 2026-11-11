package ai.derco;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * DercoVisuals' own modular interface. Pulse is kept as a design reference only;
 * this screen is independently implemented for the 1.21.11 project.
 */
public class MenuScreen extends Screen {
    private static final int BACKDROP = 0xB9080D18;
    private static final int WINDOW = 0xF10D1422;
    private static final int SURFACE = 0xFF141E30;
    private static final int SURFACE_HOVER = 0xFF1C2B43;
    private static final int SURFACE_SELECTED = 0xFF21334D;
    private static final int BORDER = 0xFF2C3B54;
    private static final int ACCENT = 0xFF9A83FF;
    private static final int AQUA = 0xFF61D9D0;
    private static final int WHITE = 0xFFF1F4FC;
    private static final int MUTED = 0xFF93A2BA;
    private static final int DISABLED = 0xFF62728A;

    private final ModuleCategory activeCategory;
    private final String selectedModuleId;
    private final List<NavLayout> navigation = new ArrayList<>();
    private final List<CardLayout> cards = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;
    private int sidebarW, detailsW, contentX, contentW, detailsX, gridTop, columns;
    private int scrollRow;
    private int visibleRows;

    public MenuScreen() {
        this(ModuleCategory.HUD, "info_hud");
    }

    public MenuScreen(ModuleCategory category, String selectedModuleId) {
        super(Text.literal("DercoVisuals"));
        this.activeCategory = category;
        this.selectedModuleId = selectedModuleId;
    }

    @Override
    protected void init() {
        this.clearChildren();
        navigation.clear();
        cards.clear();

        panelW = Math.min(960, Math.max(320, this.width - 24));
        panelH = Math.min(540, Math.max(260, this.height - 24));
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        sidebarW = Math.min(142, Math.max(112, panelW / 6));
        detailsW = Math.min(198, Math.max(148, panelW / 4));
        contentX = panelX + sidebarW + 16;
        detailsX = panelX + panelW - detailsW - 12;
        contentW = Math.max(120, detailsX - contentX - 12);
        gridTop = panelY + 112;
        columns = contentW >= 390 ? 3 : contentW >= 245 ? 2 : 1;

        int navX = panelX + 10;
        int navY = panelY + 100;
        int navW = sidebarW - 20;
        int navH = 34;
        for (ModuleCategory category : ModuleCategory.values()) {
            int y = navY;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(""), button ->
                            this.client.setScreen(new MenuScreen(category, firstId(category))))
                    .dimensions(navX, y, navW, navH).build());
            navigation.add(new NavLayout(category, navX, y, navW, navH));
            navY += navH + 8;
        }

        List<Module> visible = Modules.inCategory(activeCategory);
        int gapX = 8;
        int gapY = 9;
        int cardW = Math.max(82, (contentW - (columns - 1) * gapX) / columns);
        int cardH = 82;
        int availableGridHeight = Math.max(cardH, panelH - 164);
        visibleRows = Math.max(1, (availableGridHeight + gapY) / (cardH + gapY));
        int totalRows = (visible.size() + columns - 1) / columns;
        int maxScrollRow = Math.max(0, totalRows - visibleRows);
        scrollRow = Math.max(0, Math.min(scrollRow, maxScrollRow));
        int startIndex = scrollRow * columns;
        int endIndex = Math.min(visible.size(), startIndex + visibleRows * columns);

        for (int i = startIndex; i < endIndex; i++) {
            Module module = visible.get(i);
            int visibleIndex = i - startIndex;
            int column = visibleIndex % columns;
            int row = visibleIndex / columns;
            int x = contentX + column * (cardW + gapX);
            int y = gridTop + row * (cardH + gapY);

            this.addDrawableChild(ButtonWidget.builder(Text.literal(""), button ->
                            this.client.setScreen(new MenuScreen(activeCategory, module.id)))
                    .dimensions(x, y, cardW, cardH).build());

            int toggleW = 54;
            int toggleH = 18;
            int toggleX = x + cardW - toggleW - 8;
            int toggleY = y + cardH - toggleH - 7;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(""), button -> {
                        module.toggle();
                        this.client.setScreen(new MenuScreen(activeCategory, module.id));
                    })
                    .dimensions(toggleX, toggleY, toggleW, toggleH).build());

            cards.add(new CardLayout(module, x, y, cardW, cardH,
                    toggleX, toggleY, toggleW, toggleH));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        boolean overGrid = mouseX >= contentX && mouseX < detailsX - 4
                && mouseY >= gridTop && mouseY < panelY + panelH - 40;
        if (overGrid && verticalAmount != 0.0) {
            int total = Modules.inCategory(activeCategory).size();
            int totalRows = (total + columns - 1) / columns;
            int maxScrollRow = Math.max(0, totalRows - visibleRows);
            int nextRow = Math.max(0, Math.min(maxScrollRow,
                    scrollRow + (verticalAmount < 0 ? 1 : -1)));
            if (nextRow != scrollRow) {
                scrollRow = nextRow;
                this.clearChildren();
                this.init();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private static String firstId(ModuleCategory category) {
        return Modules.inCategory(category).stream().findFirst().map(module -> module.id).orElse("");
    }

    private Module selectedModule() {
        Module selected = Modules.find(selectedModuleId);
        if (selected != null && selected.category == activeCategory) return selected;
        return Modules.inCategory(activeCategory).stream().findFirst().orElse(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, BACKDROP);
        super.render(context, mouseX, mouseY, delta);

        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, WINDOW);
        context.fill(panelX, panelY, panelX + panelW, panelY + 2, ACCENT);
        context.fill(panelX + 1, panelY + 62, panelX + panelW - 1, panelY + 63, BORDER);

        context.fill(panelX + 16, panelY + 16, panelX + 42, panelY + 42, SURFACE_SELECTED);
        context.drawTextWithShadow(this.textRenderer, "D", panelX + 25, panelY + 24, ACCENT);
        context.drawTextWithShadow(this.textRenderer, "DERCO", panelX + 52, panelY + 15, WHITE);
        context.drawTextWithShadow(this.textRenderer, "VISUALS", panelX + 52, panelY + 29, ACCENT);
        context.drawText(this.textRenderer, "MODULAR CLIENT  /  FABRIC 1.21.11",
                panelX + 145, panelY + 25, MUTED, false);
        String build = "FREE  •  CLIENT SIDE";
        context.drawText(this.textRenderer, build,
                panelX + panelW - this.textRenderer.getWidth(build) - 18, panelY + 25, AQUA, false);

        context.fill(panelX + 8, panelY + 76, panelX + sidebarW, panelY + panelH - 12, SURFACE);
        context.fill(contentX - 6, panelY + 76, detailsX - 6, panelY + panelH - 12, SURFACE);
        context.fill(detailsX, panelY + 76, panelX + panelW - 8, panelY + panelH - 12, SURFACE);

        context.drawText(this.textRenderer, "CATEGORIES", panelX + 18, panelY + 84, MUTED, false);
        for (NavLayout nav : navigation) {
            boolean selected = nav.category == activeCategory;
            boolean hovered = inside(mouseX, mouseY, nav.x, nav.y, nav.w, nav.h);
            int fill = selected ? SURFACE_SELECTED : hovered ? SURFACE_HOVER : SURFACE;
            context.fill(nav.x, nav.y, nav.x + nav.w, nav.y + nav.h, fill);
            if (selected) context.fill(nav.x, nav.y, nav.x + 3, nav.y + nav.h, ACCENT);
            String title = nav.category.title.toUpperCase();
            context.drawTextWithShadow(this.textRenderer, title, nav.x + 10, nav.y + 8,
                    selected ? WHITE : MUTED);
            String count = String.format("%02d", Modules.inCategory(nav.category).size());
            context.drawText(this.textRenderer, count,
                    nav.x + nav.w - this.textRenderer.getWidth(count) - 8, nav.y + 8,
                    selected ? AQUA : DISABLED, false);
        }

        context.drawTextWithShadow(this.textRenderer,
                activeCategory.title.toUpperCase() + " / MODULES", contentX, panelY + 84, WHITE);
        context.drawText(this.textRenderer, activeCategory.subtitle,
                contentX, panelY + 98, MUTED, false);

        int totalRows = (Modules.inCategory(activeCategory).size() + columns - 1) / columns;
        if (totalRows > visibleRows) {
            String hint = "SCROLL  " + (scrollRow + 1) + " / " + (totalRows - visibleRows + 1);
            context.drawText(this.textRenderer, hint,
                    detailsX - 8 - this.textRenderer.getWidth(hint), panelY + 98, DISABLED, false);
        }

        for (CardLayout card : cards) {
            boolean selected = card.module.id.equals(selectedModuleId);
            boolean hovered = inside(mouseX, mouseY, card.x, card.y, card.w, card.h);
            int fill = selected ? SURFACE_SELECTED : hovered ? SURFACE_HOVER : 0xFF172337;
            context.fill(card.x, card.y, card.x + card.w, card.y + card.h, fill);
            context.fill(card.x, card.y, card.x + card.w, card.y + 1,
                    selected ? ACCENT : BORDER);
            context.fill(card.x, card.y, card.x + 2, card.y + card.h,
                    card.module.isEnabled() ? AQUA : 0xFF35435A);
            context.drawTextWithShadow(this.textRenderer, card.module.title,
                    card.x + 10, card.y + 9, WHITE);
            String state = card.module.isEnabled() ? "ACTIVE" : "INACTIVE";
            context.drawText(this.textRenderer, state,
                    card.x + card.w - this.textRenderer.getWidth(state) - 9,
                    card.y + 25, card.module.isEnabled() ? AQUA : DISABLED, false);
            drawWrapped(context, card.module.description, card.x + 10, card.y + 28,
                    card.w - 20, 10, 2, MUTED);

            int toggleFill = card.module.isEnabled() ? 0xFF284B4B : 0xFF29354A;
            int toggleText = card.module.isEnabled() ? AQUA : MUTED;
            context.fill(card.toggleX, card.toggleY, card.toggleX + card.toggleW,
                    card.toggleY + card.toggleH, toggleFill);
            String label = card.module.isEnabled() ? "ON" : "OFF";
            int labelX = card.toggleX + (card.toggleW - this.textRenderer.getWidth(label)) / 2;
            context.drawTextWithShadow(this.textRenderer, label,
                    labelX, card.toggleY + 5, toggleText);
        }

        Module selected = selectedModule();
        context.drawText(this.textRenderer, "INSPECTOR", detailsX + 12, panelY + 84, MUTED, false);
        if (selected != null) {
            context.fill(detailsX + 12, panelY + 108, detailsX + 34, panelY + 110, ACCENT);
            context.drawTextWithShadow(this.textRenderer, selected.title,
                    detailsX + 12, panelY + 122, WHITE);
            context.drawText(this.textRenderer, selected.category.title.toUpperCase(),
                    detailsX + 12, panelY + 140, AQUA, false);
            drawWrapped(context, selected.description, detailsX + 12, panelY + 164,
                    detailsW - 32, 11, 3, MUTED);

            int statusY = panelY + Math.max(190, panelH - 138);
            context.fill(detailsX + 12, statusY, detailsX + detailsW - 20, statusY + 1, BORDER);
            context.drawText(this.textRenderer, "MODULE STATE",
                    detailsX + 12, statusY + 12, MUTED, false);
            context.drawTextWithShadow(this.textRenderer, selected.isEnabled() ? "ENABLED" : "DISABLED",
                    detailsX + 12, statusY + 27, selected.isEnabled() ? AQUA : MUTED);
            context.drawText(this.textRenderer, "SHORTCUT",
                    detailsX + 12, statusY + 54, MUTED, false);
            context.drawTextWithShadow(this.textRenderer, selected.shortcut,
                    detailsX + 12, statusY + 69, WHITE);
        } else {
            context.drawTextWithShadow(this.textRenderer, "No modules",
                    detailsX + 12, panelY + 120, WHITE);
        }

        context.fill(panelX + 14, panelY + panelH - 28, panelX + panelW - 14,
                panelY + panelH - 27, BORDER);
        String footer = "RIGHT SHIFT  •  ESC TO CLOSE";
        context.drawText(this.textRenderer, footer,
                panelX + 18, panelY + panelH - 19, DISABLED, false);
        String totalLabel = Modules.ALL.size() + " MODULES";
        context.drawText(this.textRenderer, totalLabel,
                panelX + panelW - this.textRenderer.getWidth(totalLabel) - 18,
                panelY + panelH - 19, DISABLED, false);
    }

    private void drawWrapped(DrawContext context, String value, int x, int y,
                             int maxWidth, int lineHeight, int maxLines, int color) {
        StringBuilder line = new StringBuilder();
        int row = 0;
        for (String word : value.split(" ")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (this.textRenderer.getWidth(candidate) > maxWidth && !line.isEmpty()) {
                context.drawText(this.textRenderer, line.toString(),
                        x, y + row * lineHeight, color, false);
                row++;
                line = new StringBuilder(word);
                if (row >= maxLines - 1) break;
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (!line.isEmpty() && row < maxLines) {
            String text = line.toString();
            if (this.textRenderer.getWidth(text) > maxWidth) text = trim(text, 24);
            context.drawText(this.textRenderer, text,
                    x, y + row * lineHeight, color, false);
        }
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static String trim(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(0, max - 1)) + "…";
    }

    private record NavLayout(ModuleCategory category, int x, int y, int w, int h) {}
    private record CardLayout(Module module, int x, int y, int w, int h,
                              int toggleX, int toggleY, int toggleW, int toggleH) {}
}
