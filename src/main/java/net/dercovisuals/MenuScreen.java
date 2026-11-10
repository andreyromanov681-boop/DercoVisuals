package net.dercovisuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends Screen {
    private static final int BG = 0xF10B1020;
    private static final int PANEL = 0xF8162033;
    private static final int PANEL_ALT = 0xF01D2A40;
    private static final int ACCENT = 0xFF8B7CFF;
    private static final int CYAN = 0xFF55D6D0;
    private static final int TEXT = 0xFFF0F4FF;
    private static final int MUTED = 0xFF9AA8C0;

    private final ModuleCategory activeCategory;
    private final String selectedModuleId;
    private final List<CardLayout> cards = new ArrayList<>();
    private int panelX, panelY, panelW, panelH, sidebarW, detailsW, contentX, contentW;

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
        cards.clear();
        panelW = Math.max(320, Math.min(840, this.width - 24));
        panelH = Math.max(220, Math.min(470, this.height - 24));
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        sidebarW = Math.min(150, panelW / 4);
        detailsW = Math.min(220, panelW / 3);
        contentX = panelX + sidebarW + 18;
        contentW = panelW - sidebarW - detailsW - 42;

        int categoryY = panelY + 82;
        for (ModuleCategory category : ModuleCategory.values()) {
            int y = categoryY;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(category.title), button ->
                    this.client.setScreen(new MenuScreen(category, firstId(category))))
                    .dimensions(panelX + 12, y, sidebarW - 24, 24).build());
            categoryY += 32;
        }

        List<Module> visible = Modules.inCategory(activeCategory);
        int columns = contentW >= 250 ? 2 : 1;
        int gap = 10;
        int cardW = Math.max(110, (contentW - (columns - 1) * gap) / columns);
        int cardH = 72;
        int startY = panelY + 96;

        for (int i = 0; i < visible.size(); i++) {
            Module module = visible.get(i);
            int col = i % columns;
            int row = i / columns;
            int x = contentX + col * (cardW + gap);
            int y = startY + row * (cardH + 10);
            cards.add(new CardLayout(module, x, y, cardW, cardH));
            this.addDrawableChild(ButtonWidget.builder(
                            Text.literal(module.isEnabled() ? "ON  ·  CLICK TO DISABLE" : "OFF  ·  CLICK TO ENABLE"),
                            button -> {
                                module.toggle();
                                this.client.setScreen(new MenuScreen(activeCategory, module.id));
                            })
                    .dimensions(x + 8, y + 45, cardW - 16, 20).build());
        }
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
        context.fill(0, 0, this.width, this.height, 0xB9000713);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, BG);
        context.fill(panelX, panelY, panelX + panelW, panelY + 3, ACCENT);
        context.fill(panelX + 1, panelY + 46, panelX + panelW - 1, panelY + 47, 0xFF34445E);

        context.fill(panelX + 8, panelY + 56, panelX + sidebarW, panelY + panelH - 10, PANEL);
        context.fill(contentX - 6, panelY + 56, contentX + contentW + 6, panelY + panelH - 10, PANEL);
        int detailsX = panelX + panelW - detailsW - 12;
        context.fill(detailsX, panelY + 56, panelX + panelW - 8, panelY + panelH - 10, PANEL);

        context.drawTextWithShadow(this.textRenderer, "DERCO", panelX + 18, panelY + 14, TEXT);
        context.drawTextWithShadow(this.textRenderer, "VISUALS", panelX + 66, panelY + 14, ACCENT);
        context.drawTextWithShadow(this.textRenderer, "MODULAR CLIENT INTERFACE", panelX + 18, panelY + 31, MUTED);
        context.drawTextWithShadow(this.textRenderer, "1.21.11  /  CLIENT", panelX + panelW - 144, panelY + 18, CYAN);

        context.drawTextWithShadow(this.textRenderer, "LIBRARY", panelX + 18, panelY + 64, MUTED);
        context.drawTextWithShadow(this.textRenderer, activeCategory.subtitle,
                contentX, panelY + 66, MUTED);

        for (CardLayout card : cards) {
            boolean selected = card.module.id.equals(selectedModuleId);
            context.fill(card.x, card.y, card.x + card.w, card.y + card.h,
                    selected ? 0xFF263653 : PANEL_ALT);
            context.fill(card.x, card.y, card.x + 2, card.y + card.h,
                    card.module.isEnabled() ? CYAN : 0xFF52617A);
            context.drawTextWithShadow(this.textRenderer, card.module.title,
                    card.x + 9, card.y + 8, TEXT);
            String status = card.module.isEnabled() ? "ACTIVE" : "INACTIVE";
            context.drawTextWithShadow(this.textRenderer, status,
                    card.x + card.w - this.textRenderer.getWidth(status) - 9, card.y + 8,
                    card.module.isEnabled() ? CYAN : MUTED);
            context.drawText(this.textRenderer, Text.literal(trim(card.module.description, 31)),
                    card.x + 9, card.y + 25, MUTED, false);
        }

        Module selected = selectedModule();
        context.drawTextWithShadow(this.textRenderer, "MODULE DETAILS", detailsX + 12, panelY + 66, MUTED);
        if (selected != null) {
            context.drawTextWithShadow(this.textRenderer, selected.title, detailsX + 12, panelY + 91, TEXT);
            context.fill(detailsX + 12, panelY + 108, detailsX + detailsW - 20, panelY + 109, 0xFF34445E);
            drawWrapped(context, selected.description, detailsX + 12, panelY + 121,
                    detailsW - 32, 10, MUTED);
            int infoY = panelY + 190;
            context.drawTextWithShadow(this.textRenderer, "STATUS", detailsX + 12, infoY, MUTED);
            context.drawTextWithShadow(this.textRenderer, selected.isEnabled() ? "Enabled" : "Disabled",
                    detailsX + 12, infoY + 13, selected.isEnabled() ? CYAN : MUTED);
            context.drawTextWithShadow(this.textRenderer, "CONTROL", detailsX + 12, infoY + 42, MUTED);
            context.drawTextWithShadow(this.textRenderer, selected.shortcut,
                    detailsX + 12, infoY + 55, TEXT);
        } else {
            context.drawTextWithShadow(this.textRenderer, "No modules here", detailsX + 12, panelY + 96, MUTED);
        }

        context.drawTextWithShadow(this.textRenderer, "RIGHT SHIFT  ·  CLOSE WITH ESC",
                panelX + 16, panelY + panelH - 22, MUTED);
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawWrapped(DrawContext context, String value, int x, int y, int maxWidth,
                             int lineHeight, int color) {
        StringBuilder line = new StringBuilder();
        int row = 0;
        for (String word : value.split(" ")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (this.textRenderer.getWidth(candidate) > maxWidth && !line.isEmpty()) {
                context.drawText(this.textRenderer, line.toString(), x, y + row * lineHeight, color, false);
                line = new StringBuilder(word);
                row++;
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (!line.isEmpty()) context.drawText(this.textRenderer, line.toString(), x, y + row * lineHeight, color, false);
    }

    private static String trim(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(0, max - 1)) + "…";
    }

    private record CardLayout(Module module, int x, int y, int w, int h) {}
}
