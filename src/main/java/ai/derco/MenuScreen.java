package ai.derco;

import ai.derco.theme.DercoTheme;
import ai.derco.theme.ThemeAnimation;
import ai.derco.theme.ThemeColors;
import ai.derco.theme.ThemeContrast;
import ai.derco.theme.ThemeEffects;
import ai.derco.theme.ThemeGradients;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DercoVisuals' modular interface: categories on the left, module cards in the
 * middle, the inspector on the right.
 *
 * <p>Nothing here creates widgets. The old version rebuilt every invisible
 * {@code ButtonWidget} on each scroll step and replaced the whole screen on every
 * click, which lost the open state and thrashed the child list. Clicks and scrolls
 * now mutate this screen in place, and the hover/knob animations are plain floats —
 * one screen instance lives for the whole session.</p>
 */
public class MenuScreen extends Screen {
    private static final int GAP_X = 8;
    private static final int GAP_Y = 9;
    private static final int CARD_H = 82;
    private static final int NAV_H = 34;
    private static final int NAV_GAP = 8;
    private static final int TOGGLE_W = 54;
    private static final int TOGGLE_H = 18;
    private static final int HEADER_H = 62;

    private ModuleCategory activeCategory;
    private String selectedModuleId;

    private final List<NavSlot> navigation = new ArrayList<>();
    private final List<CardSlot> cards = new ArrayList<>();
    private final Map<String, ThemeAnimation> hoverAnims = new HashMap<>();
    private final Map<String, ThemeAnimation> knobAnims = new HashMap<>();

    private int panelX, panelY, panelW, panelH;
    private int sidebarW, detailsW, contentX, contentW, detailsX;
    private int gridTop, gridBottom, columns, cardW, scrollRow, visibleRows;

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
        layout();
    }

    private void layout() {
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
        gridBottom = panelY + panelH - 40;
        columns = contentW >= 390 ? 3 : contentW >= 245 ? 2 : 1;
        cardW = Math.max(82, (contentW - (columns - 1) * GAP_X) / columns);
        visibleRows = Math.max(1, (gridBottom - gridTop + GAP_Y) / (CARD_H + GAP_Y));

        navigation.clear();
        int navX = panelX + 10;
        int navY = panelY + 100;
        int navW = sidebarW - 20;
        for (ModuleCategory category : ModuleCategory.values()) {
            navigation.add(new NavSlot(category, navX, navY, navW, NAV_H));
            navY += NAV_H + NAV_GAP;
        }

        rebuildCards();
    }

    /** Only the card grid is rebuilt on scroll — the panel geometry stays put. */
    private void rebuildCards() {
        cards.clear();
        List<Module> modules = Modules.inCategory(activeCategory);
        int totalRows = (modules.size() + columns - 1) / columns;
        int maxScroll = Math.max(0, totalRows - visibleRows);
        scrollRow = Math.max(0, Math.min(scrollRow, maxScroll));

        int start = scrollRow * columns;
        int end = Math.min(modules.size(), start + visibleRows * columns);
        for (int index = start; index < end; index++) {
            Module module = modules.get(index);
            int visibleIndex = index - start;
            int column = visibleIndex % columns;
            int row = visibleIndex / columns;
            int x = contentX + column * (cardW + GAP_X);
            int y = gridTop + row * (CARD_H + GAP_Y);
            cards.add(new CardSlot(module, x, y, cardW, CARD_H,
                    x + cardW - TOGGLE_W - 8, y + CARD_H - TOGGLE_H - 7));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        for (NavSlot nav : navigation) {
            if (nav.contains(mouseX, mouseY)) {
                activate(nav.category());
                return true;
            }
        }
        for (CardSlot card : cards) {
            if (card.toggleContains(mouseX, mouseY)) {
                card.module().toggle();
                knobAnim(card.module().id).target(card.module().isEnabled() ? 1.0F : 0.0F);
                return true;
            }
            if (card.contains(mouseX, mouseY)) {
                selectedModuleId = card.module().id;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount) {
        boolean overGrid = mouseX >= contentX && mouseX < detailsX - 4
                && mouseY >= gridTop && mouseY < gridBottom + 8;
        if (overGrid && verticalAmount != 0.0) {
            return scrollBy(verticalAmount < 0 ? 1 : -1);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) return scrollBy(1);
        if (keyCode == GLFW.GLFW_KEY_UP) return scrollBy(-1);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean scrollBy(int rows) {
        int total = Modules.inCategory(activeCategory).size();
        int totalRows = (total + columns - 1) / columns;
        int maxScroll = Math.max(0, totalRows - visibleRows);
        int next = Math.max(0, Math.min(maxScroll, scrollRow + rows));
        if (next == scrollRow) return true;
        scrollRow = next;
        rebuildCards();
        return true;
    }

    private void activate(ModuleCategory category) {
        if (category == activeCategory) return;
        activeCategory = category;
        scrollRow = 0;
        selectedModuleId = firstId(category);
        rebuildCards();
    }

    private static String firstId(ModuleCategory category) {
        List<Module> modules = Modules.inCategory(category);
        return modules.isEmpty() ? "" : modules.get(0).id;
    }

    private Module selectedModule() {
        Module selected = Modules.find(selectedModuleId);
        if (selected != null && selected.category == activeCategory) return selected;
        List<Module> modules = Modules.inCategory(activeCategory);
        return modules.isEmpty() ? null : modules.get(0);
    }

    private ThemeAnimation hoverAnim(String id) {
        return hoverAnims.computeIfAbsent(id, key -> new ThemeAnimation(0.5F));
    }

    private ThemeAnimation knobAnim(String id) {
        return knobAnims.computeIfAbsent(id, key -> new ThemeAnimation(0.45F));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int radius = DercoTheme.radius();
        context.fill(0, 0, this.width, this.height, ThemeColors.BACKDROP);
        ThemeEffects.shadow(context, panelX, panelY, panelW, panelH, radius);
        ThemeEffects.roundedFill(context, panelX, panelY, panelW, panelH,
                ThemeColors.WINDOW, radius);
        ThemeGradients.accentBar(context, panelX, panelY, panelW, 2);
        context.fill(panelX + 1, panelY + HEADER_H, panelX + panelW - 1, panelY + HEADER_H + 1,
                ThemeColors.BORDER);

        renderHeader(context, mouseX, mouseY);

        // Three columns of surface behind the content.
        ThemeEffects.roundedFill(context, panelX + 8, panelY + 76, sidebarW - 8,
                panelH - 88, ThemeColors.SURFACE, Math.max(0, radius - 1));
        ThemeEffects.roundedFill(context, contentX - 6, panelY + 76, detailsX - contentX,
                panelH - 88, ThemeColors.SURFACE, Math.max(0, radius - 1));
        ThemeEffects.roundedFill(context, detailsX, panelY + 76, panelW - detailsW - 20,
                panelH - 88, ThemeColors.SURFACE, Math.max(0, radius - 1));

        renderNavigation(context, mouseX, mouseY);
        renderGridHeader(context);
        renderCards(context, mouseX, mouseY);
        renderInspector(context);
        renderFooter(context);
    }

    private void renderHeader(DrawContext context, int mouseX, int mouseY) {
        int badgeSize = 26;
        int badgeX = panelX + 16;
        int badgeY = panelY + 16;
        if (DercoAssets.logoPresent()) {
            ThemeEffects.roundedFill(context, badgeX, badgeY, badgeSize, badgeSize,
                    ThemeColors.SURFACE_SELECTED, DercoTheme.smallRadius());
            DercoAssets.draw(context, DercoAssets.LOGO, badgeX + 1, badgeY + 1, badgeSize - 2,
                    badgeSize, badgeSize);
        } else {
            ThemeEffects.roundedFill(context, badgeX, badgeY, badgeSize, badgeSize,
                    ThemeColors.SURFACE_SELECTED, DercoTheme.smallRadius());
            context.drawTextWithShadow(this.textRenderer, "D", badgeX + 9, badgeY + 9,
                    DercoTheme.primaryOn(ThemeColors.SURFACE_SELECTED));
        }

        context.drawTextWithShadow(this.textRenderer, "DERCO", badgeX + 36, panelY + 15,
                ThemeColors.TEXT);
        context.drawTextWithShadow(this.textRenderer, "VISUALS", badgeX + 36, panelY + 29,
                DercoTheme.primaryOn(ThemeColors.WINDOW));
        context.drawText(this.textRenderer, "MODULAR CLIENT  /  FABRIC 1.21.11",
                badgeX + 129, panelY + 25, ThemeColors.MUTED, false);

        String build = Modules.enabledCount() + " / " + Modules.ALL.size() + " ACTIVE";
        context.drawText(this.textRenderer, build,
                panelX + panelW - this.textRenderer.getWidth(build) - 18, panelY + 25,
                DercoTheme.accentOn(ThemeColors.WINDOW), false);
    }

    private void renderNavigation(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, "CATEGORIES", panelX + 18, panelY + 84,
                ThemeColors.MUTED, false);
        for (NavSlot nav : navigation) {
            boolean selected = nav.category() == activeCategory;
            boolean hovered = nav.contains(mouseX, mouseY);
            ThemeAnimation anim = hoverAnim("nav:" + nav.category().name());
            anim.target(hovered ? 1.0F : 0.0F);
            anim.tick();

            int base = selected ? ThemeColors.SURFACE_SELECTED : ThemeColors.SURFACE;
            int fill = ThemeContrast.mix(base, ThemeColors.SURFACE_HOVER, anim.eased());
            ThemeEffects.roundedFill(context, nav.x(), nav.y(), nav.w(), nav.h(),
                    fill, DercoTheme.smallRadius());
            if (selected) {
                ThemeEffects.accentStripe(context, nav.x(), nav.y(), 3, nav.h(),
                        ThemeColors.PRIMARY);
            }
            context.drawTextWithShadow(this.textRenderer,
                    nav.category().title.toUpperCase(),
                    nav.x() + 10, nav.y() + 8,
                    selected ? ThemeColors.TEXT : ThemeColors.TEXT_SECONDARY);
            String count = Integer.toString(Modules.inCategory(nav.category()).size());
            context.drawText(this.textRenderer, count,
                    nav.x() + nav.w() - this.textRenderer.getWidth(count) - 8, nav.y() + 8,
                    selected ? ThemeColors.ACCENT : ThemeColors.DISABLED, false);
        }
    }

    private void renderGridHeader(DrawContext context) {
        context.drawTextWithShadow(this.textRenderer,
                activeCategory.title.toUpperCase() + " / MODULES", contentX, panelY + 84,
                ThemeColors.TEXT);
        context.drawText(this.textRenderer, activeCategory.subtitle,
                contentX, panelY + 98, ThemeColors.MUTED, false);

        int total = Modules.inCategory(activeCategory).size();
        int totalRows = (total + columns - 1) / columns;
        if (totalRows > visibleRows) {
            String hint = "SCROLL  " + (scrollRow + 1) + " / " + (totalRows - visibleRows + 1);
            context.drawText(this.textRenderer, hint,
                    detailsX - 8 - this.textRenderer.getWidth(hint), panelY + 98,
                    ThemeColors.DISABLED, false);
        }
    }

    private void renderCards(DrawContext context, int mouseX, int mouseY) {
        for (CardSlot card : cards) {
            Module module = card.module();
            boolean selected = module.id.equals(selectedModuleId);
            boolean hovered = card.contains(mouseX, mouseY);

            ThemeAnimation hover = hoverAnim(module.id);
            hover.target(hovered ? 1.0F : 0.0F);
            hover.tick();

            int base = selected ? ThemeColors.SURFACE_SELECTED : ThemeColors.SURFACE_RAISED;
            int fill = ThemeContrast.mix(base, ThemeColors.SURFACE_HOVER, hover.eased());
            ThemeEffects.roundedFill(context, card.x(), card.y(), card.w(), card.h(),
                    fill, DercoTheme.smallRadius());
            if (selected) {
                ThemeEffects.outline(context, card.x(), card.y(), card.w(), card.h(),
                        ThemeColors.BORDER_STRONG);
            }
            context.fill(card.x() + 1, card.y(), card.x() + card.w() - 1, card.y() + 1,
                    selected ? ThemeColors.PRIMARY : ThemeColors.BORDER);
            ThemeEffects.accentStripe(context, card.x(), card.y() + 6, 2, card.h() - 12,
                    module.isEnabled() ? ThemeColors.ACCENT : ThemeColors.BORDER_STRONG);

            context.drawTextWithShadow(this.textRenderer, module.title,
                    card.x() + 10, card.y() + 9, ThemeColors.TEXT);

            String state = module.isEnabled() ? "ACTIVE" : "INACTIVE";
            context.drawText(this.textRenderer, state,
                    card.x() + card.w() - this.textRenderer.getWidth(state) - 9,
                    card.y() + 22,
                    module.isEnabled() ? ThemeColors.ACCENT : ThemeColors.DISABLED, false);

            drawWrapped(context, module.description, card.x() + 10, card.y() + 32,
                    card.w() - 20, 10, 2, ThemeColors.MUTED);

            ThemeAnimation knob = knobAnim(module.id);
            knob.target(module.isEnabled() ? 1.0F : 0.0F);
            knob.tick();
            ThemeEffects.toggle(context, card.toggleX(), card.toggleY(), TOGGLE_W, TOGGLE_H,
                    module.isEnabled(), knob.value());
        }
    }

    private void renderInspector(DrawContext context) {
        context.drawText(this.textRenderer, "INSPECTOR", detailsX + 12, panelY + 84,
                ThemeColors.MUTED, false);
        Module selected = selectedModule();
        if (selected == null) {
            context.drawTextWithShadow(this.textRenderer, "No modules",
                    detailsX + 12, panelY + 120, ThemeColors.TEXT);
            return;
        }
        ThemeGradients.accentBar(context, detailsX + 12, panelY + 108, 22, 2);
        context.drawTextWithShadow(this.textRenderer, selected.title,
                detailsX + 12, panelY + 118, ThemeColors.TEXT);
        context.drawText(this.textRenderer, selected.category.title.toUpperCase(),
                detailsX + 12, panelY + 136, ThemeColors.ACCENT, false);
        drawWrapped(context, selected.description, detailsX + 12, panelY + 158,
                detailsW - 32, 11, 3, ThemeColors.MUTED);

        int statusY = panelY + Math.max(190, panelH - 138);
        context.fill(detailsX + 12, statusY, detailsX + detailsW - 20, statusY + 1,
                ThemeColors.BORDER);
        context.drawText(this.textRenderer, "MODULE STATE",
                detailsX + 12, statusY + 12, ThemeColors.MUTED, false);
        context.drawTextWithShadow(this.textRenderer,
                selected.isEnabled() ? "ENABLED" : "DISABLED",
                detailsX + 12, statusY + 27,
                selected.isEnabled() ? ThemeColors.SUCCESS : ThemeColors.MUTED);
        context.drawText(this.textRenderer, "SHORTCUT",
                detailsX + 12, statusY + 54, ThemeColors.MUTED, false);
        context.drawTextWithShadow(this.textRenderer, selected.shortcut,
                detailsX + 12, statusY + 69, ThemeColors.TEXT);
    }

    private void renderFooter(DrawContext context) {
        context.fill(panelX + 14, panelY + panelH - 28, panelX + panelW - 14,
                panelY + panelH - 27, ThemeColors.BORDER);
        context.drawText(this.textRenderer, "RIGHT SHIFT  •  ESC TO CLOSE",
                panelX + 18, panelY + panelH - 19, ThemeColors.DISABLED, false);
        String totalLabel = Modules.ALL.size() + " MODULES";
        context.drawText(this.textRenderer, totalLabel,
                panelX + panelW - this.textRenderer.getWidth(totalLabel) - 18,
                panelY + panelH - 19, ThemeColors.DISABLED, false);
    }

    private void drawWrapped(DrawContext context, String value, int x, int y,
                             int maxWidth, int lineHeight, int maxLines, int color) {
        StringBuilder line = new StringBuilder();
        int row = 0;
        for (String word : value.split(" ")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (this.textRenderer.getWidth(candidate) > maxWidth && !line.isEmpty()) {
                context.drawText(this.textRenderer, line.toString(), x, y + row * lineHeight,
                        color, false);
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
            context.drawText(this.textRenderer, text, x, y + row * lineHeight, color, false);
        }
    }

    private static String trim(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(0, max - 1)) + "…";
    }

    private record NavSlot(ModuleCategory category, int x, int y, int w, int h) {
        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private record CardSlot(Module module, int x, int y, int w, int h,
                            int toggleX, int toggleY) {
        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }

        boolean toggleContains(double mx, double my) {
            return mx >= toggleX && mx < toggleX + TOGGLE_W
                    && my >= toggleY && my < toggleY + TOGGLE_H;
        }
    }
}
