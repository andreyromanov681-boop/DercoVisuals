package ai.derco;

import ai.derco.theme.DercoTheme;
import ai.derco.theme.ThemeColors;
import ai.derco.theme.ThemeMetrics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entrypoint and HUD renderer.
 *
 * <p>The HUD reads finished strings and stacks from {@link HudCache}; this class
 * only positions and draws them with the Electric Blue palette. All per-frame
 * text formatting, inventory scanning and collection allocation live in the cache
 * and run at tick rate (or slower), never once per frame.</p>
 */
public class DercoVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "dercovisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger("DercoVisuals");

    private static final int ROW_H = 18;

    private KeyBinding menuKey;
    private KeyBinding fullbrightKey;
    private KeyBinding zoomKey;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dercovisuals.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        fullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dercovisuals.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, category));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dercovisuals.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

        Modules.loadConfig();
        DercoTheme.settings(); // touch once so the first render already uses saved values

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> DercoConfig.flush());
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.of(MOD_ID, "hud"),
                (context, tickCounter) -> renderHud(context));

        LOGGER.info("DercoVisuals {} initialized", "1.0.0");
    }

    private void onTick(MinecraftClient client) {
        while (menuKey.wasPressed()) {
            if (client.currentScreen == null) client.setScreen(new MenuScreen());
        }
        while (fullbrightKey.wasPressed()) {
            Modules.FULLBRIGHT.toggle();
        }
        boolean zoomHeld = zoomKey.isPressed() && client.currentScreen == null && client.player != null;
        Modules.updateZoom(client, zoomHeld);

        HudCache.update(client);
        DercoConfig.tick();
    }

    private void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int leftColumnY = ThemeMetrics.HUD_MARGIN;

        if (Modules.INFO_HUD.isEnabled()) {
            int x = ThemeMetrics.HUD_MARGIN;
            int y = ThemeMetrics.HUD_MARGIN;
            int panel = ThemeColors.HUD_PANEL;
            context.fill(x, y, x + 214, y + 56, panel);
            context.fill(x, y, x + ThemeMetrics.HUD_ACCENT_W, y + 56, ThemeColors.PRIMARY);
            context.drawTextWithShadow(client.textRenderer, "DERCO VISUALS",
                    x + 11, y + 7, ThemeColors.TEXT);
            context.drawText(client.textRenderer, "FPS  " + HudCache.fpsText(),
                    x + 11, y + 22, ThemeColors.TEXT_SECONDARY, false);
            context.drawText(client.textRenderer, "XYZ  " + HudCache.coordsText(),
                    x + 11, y + 37, ThemeColors.TEXT_SECONDARY, false);
            leftColumnY = y + 62;
        }

        if (Modules.COMPASS.isEnabled()) {
            drawHudCard(context, client, ThemeMetrics.HUD_MARGIN, leftColumnY, 112, 22,
                    "FACING", HudCache.facingText(), ThemeColors.ACCENT);
            leftColumnY += 28;
        }

        if (Modules.POTION_HUD.isEnabled()) {
            var lines = HudCache.effectLines();
            if (lines.isEmpty()) {
                context.fill(ThemeMetrics.HUD_MARGIN, leftColumnY,
                        ThemeMetrics.HUD_MARGIN + 154, leftColumnY + 20,
                        ThemeColors.HUD_PANEL);
                context.drawText(client.textRenderer, "NO ACTIVE EFFECTS",
                        ThemeMetrics.HUD_MARGIN + 7, leftColumnY + 6, ThemeColors.MUTED, false);
            } else {
                int panel = ThemeColors.HUD_PANEL;
                for (String line : lines) {
                    int width = Math.max(156, client.textRenderer.getWidth(line) + 20);
                    context.fill(ThemeMetrics.HUD_MARGIN, leftColumnY,
                            ThemeMetrics.HUD_MARGIN + width, leftColumnY + 20, panel);
                    context.fill(ThemeMetrics.HUD_MARGIN, leftColumnY,
                            ThemeMetrics.HUD_MARGIN + 2, leftColumnY + 20,
                            ThemeColors.PRIMARY);
                    context.drawText(client.textRenderer, line,
                            ThemeMetrics.HUD_MARGIN + 7, leftColumnY + 6,
                            ThemeColors.TEXT, false);
                    leftColumnY += 23;
                }
            }
        }

        if (Modules.SATURATION_HUD.isEnabled()) {
            ExtraHudRenderer.renderFoodStatus(context, client, leftColumnY);
            leftColumnY += 50;
        }

        if (Modules.MODULE_LIST.isEnabled()) {
            drawModuleList(context, client, screenWidth);
        }

        if (Modules.CLOCK.isEnabled()) {
            String label = "LOCAL  " + HudCache.clockText();
            int width = client.textRenderer.getWidth(label) + 18;
            int x = screenWidth - width - ThemeMetrics.HUD_MARGIN;
            // Sits under the module list instead of overlapping it.
            int y = Modules.MODULE_LIST.isEnabled()
                    ? HudCache.moduleListBottom() + 4
                    : ThemeMetrics.HUD_MARGIN;
            context.fill(x, y, x + width, y + 22, ThemeColors.HUD_PANEL);
            context.fill(x, y, x + width, y + 1, ThemeColors.ACCENT);
            context.drawTextWithShadow(client.textRenderer, label, x + 9, y + 7,
                    ThemeColors.ACCENT);
        }

        if (Modules.COOLDOWNS_HUD.isEnabled()) {
            ExtraHudRenderer.renderCooldowns(context, client, screenWidth);
        }

        if (Modules.INVENTORY_HUD.isEnabled()) {
            ExtraHudRenderer.renderInventory(context, client, screenWidth, screenHeight);
        }

        if (Modules.ARMOR_HUD.isEnabled()) {
            renderArmor(context, client, screenWidth, screenHeight);
        }

        if (Modules.TOTEMS_HUD.isEnabled()) {
            ExtraHudRenderer.renderTotemCounter(context, client, screenWidth, screenHeight);
        }

        if (Modules.SPEED_HUD.isEnabled()) {
            drawHudCard(context, client, screenWidth - 116, screenHeight - 42,
                    108, 24, "SPEED", HudCache.speedText() + " b/s", ThemeColors.ACCENT);
        }

        if (Modules.KEYSTROKES.isEnabled()) {
            renderKeystrokes(context, client, screenHeight);
        }
    }

    private static void drawModuleList(DrawContext context, MinecraftClient client, int screenWidth) {
        var labels = HudCache.moduleLabels();
        int y = ThemeMetrics.HUD_MARGIN;
        int panel = ThemeColors.HUD_PANEL;
        for (String label : labels) {
            int textWidth = client.textRenderer.getWidth(label);
            int x = screenWidth - textWidth - 22;
            context.fill(x - 7, y - 3, screenWidth - 8, y + 12, panel);
            context.fill(screenWidth - 10, y - 3, screenWidth - 8, y + 12, ThemeColors.PRIMARY);
            context.drawTextWithShadow(client.textRenderer, label, x, y, ThemeColors.TEXT);
            y += ROW_H;
        }
    }

    private static void renderArmor(DrawContext context, MinecraftClient client,
                                    int screenWidth, int screenHeight) {
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET
        };
        int slotWidth = 24;
        int totalWidth = slots.length * slotWidth;
        int x = screenWidth / 2 - totalWidth / 2;
        int y = screenHeight - 34;
        context.fill(x - 7, y - 6, x + totalWidth + 3, y + 27, ThemeColors.HUD_PANEL);

        for (EquipmentSlot slot : slots) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                context.drawItem(stack, x + 3, y);
                context.drawStackOverlay(client.textRenderer, stack, x + 3, y);
                if (stack.isDamageable()) {
                    int remaining = Math.max(0,
                            100 - stack.getDamage() * 100 / stack.getMaxDamage());
                    int color = remaining <= 20 ? ThemeColors.DANGER : ThemeColors.ACCENT;
                    String value = remaining + "%";
                    int width = client.textRenderer.getWidth(value);
                    context.drawText(client.textRenderer, value,
                            x + (slotWidth - width) / 2 + 3, y + 18, color, false);
                }
            } else {
                context.fill(x + 8, y + 5, x + 16, y + 13, ThemeColors.TRACK);
            }
            x += slotWidth;
        }
    }

    private static void renderKeystrokes(DrawContext context, MinecraftClient client, int screenHeight) {
        int x = 10;
        int y = screenHeight - 88;
        drawKey(context, client, "W", x + 20, y, 18, client.options.forwardKey.isPressed());
        drawKey(context, client, "A", x, y + 20, 18, client.options.leftKey.isPressed());
        drawKey(context, client, "S", x + 20, y + 20, 18, client.options.backKey.isPressed());
        drawKey(context, client, "D", x + 40, y + 20, 18, client.options.rightKey.isPressed());
        drawKey(context, client, "JUMP", x, y + 40, 58, client.options.jumpKey.isPressed());
    }

    private static void drawKey(DrawContext context, MinecraftClient client,
                                String label, int x, int y, int width, boolean pressed) {
        int background = pressed ? ThemeColors.PRIMARY : ThemeColors.HUD_PANEL;
        context.fill(x, y, x + width, y + 18, background);
        context.fill(x, y, x + width, y + 1, pressed ? ThemeColors.ACCENT : ThemeColors.BORDER_STRONG);
        int textWidth = client.textRenderer.getWidth(label);
        context.drawText(client.textRenderer, label,
                x + (width - textWidth) / 2, y + 5,
                pressed ? ThemeColors.TEXT_ON_ACCENT : ThemeColors.TEXT, false);
    }

    private static void drawHudCard(DrawContext context, MinecraftClient client,
                                    int x, int y, int width, int height,
                                    String label, String value, int accent) {
        context.fill(x, y, x + width, y + height, ThemeColors.HUD_PANEL);
        context.fill(x, y, x + 2, y + height, accent);
        context.drawText(client.textRenderer, label, x + 8, y + 7,
                ThemeColors.TEXT_SECONDARY, false);
        int valueWidth = client.textRenderer.getWidth(value);
        context.drawTextWithShadow(client.textRenderer, value,
                x + width - valueWidth - 8, y + 7, ThemeColors.TEXT);
    }
}
