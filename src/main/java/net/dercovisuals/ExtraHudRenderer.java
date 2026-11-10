package net.dercovisuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Independent HUD widgets inspired by common inventory/status overlays.
 * Kept separate from the main renderer so new HUD modules remain easy to add.
 */
public final class ExtraHudRenderer {
    private static final int PANEL = 0xB9101727;
    private static final int SLOT = 0xA80A101B;
    private static final int ACCENT = 0xFF9A83FF;
    private static final int AQUA = 0xFF61D9D0;
    private static final int WHITE = 0xFFF1F4FC;
    private static final int MUTED = 0xFFB7C3D8;

    private ExtraHudRenderer() {}

    public static void renderFoodStatus(DrawContext context, MinecraftClient client, int y) {
        var hunger = client.player.getHungerManager();
        int food = hunger.getFoodLevel();
        float saturation = Math.max(0.0F, Math.min(20.0F, hunger.getSaturationLevel()));
        int x = 8;
        int width = 186;
        context.fill(x, y, x + width, y + 46, PANEL);
        context.fill(x, y, x + 2, y + 46, AQUA);

        context.drawText(client.textRenderer, "FOOD", x + 9, y + 5, MUTED, false);
        String foodValue = food + " / 20";
        context.drawTextWithShadow(client.textRenderer, foodValue,
                x + width - client.textRenderer.getWidth(foodValue) - 9, y + 5, WHITE);
        drawBar(context, x + 9, y + 18, width - 18, 5, food / 20.0F, AQUA);

        context.drawText(client.textRenderer, "SATURATION", x + 9, y + 28, MUTED, false);
        String saturationValue = String.format("%.1f / 20", saturation);
        context.drawTextWithShadow(client.textRenderer, saturationValue,
                x + width - client.textRenderer.getWidth(saturationValue) - 9, y + 28, WHITE);
        drawBar(context, x + 9, y + 40, width - 18, 3, saturation / 20.0F, ACCENT);
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

        context.fill(x, y, x + panelWidth, y + panelHeight, PANEL);
        context.fill(x, y, x + panelWidth, y + 2, ACCENT);
        context.drawText(client.textRenderer, "INVENTORY", x + padding, y + 5, MUTED, false);

        for (int index = 0; index < 27; index++) {
            int row = index / 9;
            int column = index % 9;
            int slotX = x + padding + column * slotSize;
            int slotY = y + titleHeight + padding + row * slotSize;
            context.fill(slotX, slotY, slotX + slotSize - 1, slotY + slotSize - 1, SLOT);

            ItemStack stack = client.player.getInventory().getStack(9 + index);
            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX + 2, slotY + 2);
                context.drawStackOverlay(client.textRenderer, stack, slotX + 2, slotY + 2);
            }
        }
    }

    public static void renderTotemCounter(DrawContext context, MinecraftClient client,
                                          int screenWidth, int screenHeight) {
        int count = 0;
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = client.player.getInventory().getStack(slot);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }

        ItemStack offhand = client.player.getOffHandStack();
        if (offhand.isOf(Items.TOTEM_OF_UNDYING)) {
            count += offhand.getCount();
        }

        int x = screenWidth - 66;
        int y = screenHeight - 76;
        context.fill(x, y, x + 58, y + 30, PANEL);
        context.drawItem(new ItemStack(Items.TOTEM_OF_UNDYING), x + 5, y + 6);
        context.drawTextWithShadow(client.textRenderer, "x" + count, x + 28, y + 11,
                count > 0 ? AQUA : MUTED);
    }

    public static void renderCooldowns(DrawContext context, MinecraftClient client, int screenWidth) {
        var manager = client.player.getItemCooldownManager();
        Set<Identifier> renderedGroups = new HashSet<>();
        int x = (screenWidth - 170) / 2;
        int row = 0;

        for (int slot = 0; slot < 36 && row < 4; slot++) {
            ItemStack stack = client.player.getInventory().getStack(slot);
            if (stack.isEmpty() || !manager.isCoolingDown(stack)) {
                continue;
            }

            Identifier group = manager.getGroup(stack);
            if (!renderedGroups.add(group)) {
                continue;
            }

            float remaining = Math.max(0.0F, Math.min(1.0F,
                    manager.getCooldownProgress(stack, 1.0F)));
            int y = 8 + row * 25;
            context.fill(x, y, x + 170, y + 22, PANEL);
            context.drawItem(stack, x + 4, y + 3);

            String name = stack.getName().getString();
            if (name.length() > 13) {
                name = name.substring(0, 12) + "…";
            }
            context.drawText(client.textRenderer, name, x + 26, y + 3, WHITE, false);
            drawBar(context, x + 26, y + 14, 106, 4, remaining, ACCENT);

            String percent = Math.round(remaining * 100.0F) + "%";
            context.drawText(client.textRenderer, percent, x + 136, y + 7, MUTED, false);
            row++;
        }
    }

    private static void drawBar(DrawContext context, int x, int y, int width,
                                int height, float ratio, int color) {
        float safeRatio = Math.max(0.0F, Math.min(1.0F, ratio));
        context.fill(x, y, x + width, y + height, 0xFF26344A);
        int fillWidth = Math.round(width * safeRatio);
        if (fillWidth > 0) {
            context.fill(x, y, x + fillWidth, y + height, color);
        }
    }
}
