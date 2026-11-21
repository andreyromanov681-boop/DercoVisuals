package ai.derco;

import ai.derco.theme.DercoTheme;
import ai.derco.theme.ThemeColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/**
 * HUD widgets that need more than a label: food bars, inventory grid, totems
 * and item cooldowns.
 *
 * <p>Every value comes from {@link HudCache}, so a frame here is pure drawing —
 * no inventory scans, no string formatting and no item-stack allocation.</p>
 */
public final class ExtraHudRenderer {
    private ExtraHudRenderer() {}

    public static void renderFoodStatus(DrawContext context, MinecraftClient client, int y) {
        var hunger = client.player.getHungerManager();
        int food = hunger.getFoodLevel();
        float saturation = Math.max(0.0F, Math.min(20.0F, hunger.getSaturationLevel()));
        int x = 8;
        int width = 186;
        int panel = ThemeColors.HUD_PANEL;
        int accent = ThemeColors.ACCENT;
        int text = ThemeColors.TEXT;
        int secondary = ThemeColors.TEXT_SECONDARY;

        context.fill(x, y, x + width, y + 46, panel);
        context.fill(x, y, x + 2, y + 46, accent);

        context.drawText(client.textRenderer, "FOOD", x + 9, y + 5, secondary, false);
        String foodValue = food + " / 20";
        context.drawTextWithShadow(client.textRenderer, foodValue,
                x + width - client.textRenderer.getWidth(foodValue) - 9, y + 5, text);
        drawBar(context, x + 9, y + 18, width - 18, 5, food / 20.0F, accent);

        context.drawText(client.textRenderer, "SATURATION", x + 9, y + 28, secondary, false);
        String saturationValue = formatSaturation(saturation);
        context.drawTextWithShadow(client.textRenderer, saturationValue,
                x + width - client.textRenderer.getWidth(saturationValue) - 9, y + 28, text);
        drawBar(context, x + 9, y + 40, width - 18, 3, saturation / 20.0F, ThemeColors.PRIMARY);
    }

    public static void renderInventory(DrawContext context, MinecraftClient client,
                                       int screenWidth, int screenHeight) {
        int slotSize = 21;
        int padding = 5;
        int titleHeight = 14;
        int gridWidth = slotSize * 9;
        int gridHeight = slotSize * 3;
        int panelWidth = gridWidth + padding * 2;
        int panelHeight = gridHeight + padding * 2 + titleHeight;
        int x = (screenWidth - panelWidth) / 2;
        int y = screenHeight - panelHeight - 52;

        context.fill(x, y, x + panelWidth, y + panelHeight, ThemeColors.HUD_PANEL);
        context.fill(x, y, x + panelWidth, y + 2, ThemeColors.PRIMARY);
        context.drawText(client.textRenderer, "INVENTORY", x + padding, y + 5,
                ThemeColors.TEXT_SECONDARY, false);

        var stacks = HudCache.inventory();
        for (int index = 0; index < stacks.size(); index++) {
            int row = index / 9;
            int column = index % 9;
            int slotX = x + padding + column * slotSize;
            int slotY = y + titleHeight + padding + row * slotSize;
            context.fill(slotX, slotY, slotX + slotSize - 1, slotY + slotSize - 1, ThemeColors.SLOT);

            ItemStack stack = stacks.get(index);
            if (stack.isEmpty()) continue;
            context.drawItem(stack, slotX + 2, slotY + 2);
            context.drawStackOverlay(client.textRenderer, stack, slotX + 2, slotY + 2);
        }
    }

    public static void renderTotemCounter(DrawContext context, MinecraftClient client,
                                          int screenWidth, int screenHeight) {
        int count = HudCache.totemCount();
        int x = screenWidth - 66;
        int y = screenHeight - 76;
        context.fill(x, y, x + 58, y + 30, ThemeColors.HUD_PANEL);
        context.fill(x, y, x + 58, y + 1, ThemeColors.ACCENT);
        context.drawItem(HudCache.TOTEM_ICON, x + 5, y + 6);
        context.drawTextWithShadow(client.textRenderer, "x" + count, x + 28, y + 11,
                count > 0 ? ThemeColors.ACCENT : ThemeColors.MUTED);
    }

    public static void renderCooldowns(DrawContext context, MinecraftClient client, int screenWidth) {
        var entries = HudCache.cooldowns();
        if (entries.isEmpty()) return;

        int x = (screenWidth - 170) / 2;
        for (int row = 0; row < entries.size(); row++) {
            HudCache.CooldownEntry entry = entries.get(row);
            int y = 8 + row * 25;
            context.fill(x, y, x + 170, y + 22, ThemeColors.HUD_PANEL);
            context.drawItem(entry.stack(), x + 4, y + 3);
            context.drawText(client.textRenderer, entry.name(), x + 26, y + 3,
                    ThemeColors.TEXT, false);
            drawBar(context, x + 26, y + 14, 106, 4, entry.progress(), ThemeColors.ACCENT);
            context.drawText(client.textRenderer, entry.percent(), x + 136, y + 7,
                    ThemeColors.TEXT_SECONDARY, false);
        }
    }

    private static void drawBar(DrawContext context, int x, int y, int width,
                                int height, float ratio, int color) {
        float safeRatio = Math.max(0.0F, Math.min(1.0F, ratio));
        int radius = DercoTheme.smallRadius();
        context.fill(x, y, x + width, y + height, ThemeColors.TRACK);
        int fillWidth = Math.round(width * safeRatio);
        if (fillWidth <= 0) return;
        if (radius <= 0) {
            context.fill(x, y, x + fillWidth, y + height, color);
        } else {
            ai.derco.theme.ThemeEffects.roundedFill(context, x, y, fillWidth, height, color, radius);
        }
    }

    private static String formatSaturation(float saturation) {
        int tenths = Math.round(saturation * 10.0F);
        return (tenths / 10) + "." + (tenths % 10) + " / 20";
    }
}
