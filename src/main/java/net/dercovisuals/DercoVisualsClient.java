package net.dercovisuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DercoVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "dercovisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger("DercoVisuals");

    private static final int ACCENT = 0xFF9A83FF;
    private static final int AQUA = 0xFF61D9D0;
    private static final int WHITE = 0xFFF1F4FC;
    private static final int MUTED = 0xFFB7C3D8;
    private static final int PANEL = 0xB9101727;
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
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
    }

    private void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int leftColumnY = 8;

        if (Modules.INFO_HUD.isEnabled()) {
            int x = 8;
            int y = 8;
            context.fill(x, y, x + 214, y + 56, PANEL);
            context.fill(x, y, x + 3, y + 56, ACCENT);
            context.drawTextWithShadow(client.textRenderer, "DERCO VISUALS", x + 11, y + 7, WHITE);
            context.drawText(client.textRenderer, "FPS  " + client.getCurrentFps(),
                    x + 11, y + 22, MUTED, false);
            context.drawText(client.textRenderer,
                    String.format("XYZ  %.1f  %.1f  %.1f",
                            client.player.getX(), client.player.getY(), client.player.getZ()),
                    x + 11, y + 37, MUTED, false);
            leftColumnY = 70;
        }

        if (Modules.COMPASS.isEnabled()) {
            String direction = client.player.getHorizontalFacing().asString().toUpperCase();
            drawHudCard(context, client, 8, leftColumnY, 112, 22,
                    "FACING", direction, AQUA);
            leftColumnY += 28;
        }

        if (Modules.POTION_HUD.isEnabled()) {
            int effectCount = 0;
            for (StatusEffectInstance effect : client.player.getStatusEffects()) {
                if (effectCount >= 5) break;
                String effectName = Text.translatable(effect.getTranslationKey()).getString();
                String amplifier = romanAmplifier(effect.getAmplifier() + 1);
                String duration = formatDuration(effect.getDuration());
                String line = trim(effectName + " " + amplifier + "  ·  " + duration, 31);
                int width = Math.max(156, client.textRenderer.getWidth(line) + 20);
                context.fill(8, leftColumnY, 8 + width, leftColumnY + 20, PANEL);
                context.fill(8, leftColumnY, 10, leftColumnY + 20, ACCENT);
                context.drawText(client.textRenderer, line, 15, leftColumnY + 6, WHITE, false);
                leftColumnY += 23;
                effectCount++;
            }
            if (effectCount == 0) {
                context.fill(8, leftColumnY, 154, leftColumnY + 20, PANEL);
                context.drawText(client.textRenderer, "NO ACTIVE EFFECTS",
                        15, leftColumnY + 6, MUTED, false);
            }
        }

        if (Modules.MODULE_LIST.isEnabled()) {
            int y = 8;
            for (Module module : Modules.ALL) {
                if (!module.isEnabled() || module == Modules.MODULE_LIST) continue;
                String label = module.title;
                int textWidth = client.textRenderer.getWidth(label);
                int x = screenWidth - textWidth - 22;
                context.fill(x - 7, y - 3, screenWidth - 8, y + 12, PANEL);
                context.fill(screenWidth - 10, y - 3, screenWidth - 8, y + 12, ACCENT);
                context.drawTextWithShadow(client.textRenderer, label, x, y, WHITE);
                y += 18;
            }
        }

        if (Modules.CLOCK.isEnabled()) {
            String clock = LocalTime.now().format(CLOCK_FORMAT);
            String label = "LOCAL  " + clock;
            int width = client.textRenderer.getWidth(label) + 18;
            int x = screenWidth - width - 8;
            int y = Modules.MODULE_LIST.isEnabled()
                    ? 12 + (int) Modules.ALL.stream().filter(Module::isEnabled).count() * 18
                    : 8;
            context.fill(x, y, x + width, y + 22, PANEL);
            context.drawTextWithShadow(client.textRenderer, label, x + 9, y + 7, AQUA);
        }

        if (Modules.ARMOR_HUD.isEnabled()) {
            renderArmor(context, client, screenWidth, screenHeight);
        }

        if (Modules.SPEED_HUD.isEnabled()) {
            var velocity = client.player.getVelocity();
            double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z) * 20.0;
            String speedText = String.format("%.2f b/s", speed);
            drawHudCard(context, client, screenWidth - 116, screenHeight - 42,
                    108, 24, "SPEED", speedText, AQUA);
        }

        if (Modules.KEYSTROKES.isEnabled()) {
            renderKeystrokes(context, client, screenHeight);
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
        context.fill(x - 7, y - 6, x + totalWidth + 3, y + 27, PANEL);

        for (EquipmentSlot slot : slots) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                context.drawItem(stack, x + 3, y);
                context.drawStackOverlay(client.textRenderer, stack, x + 3, y);
                if (stack.isDamageable()) {
                    int remaining = Math.max(0,
                            100 - stack.getDamage() * 100 / stack.getMaxDamage());
                    int color = remaining <= 20 ? 0xFFFF707D : AQUA;
                    String value = remaining + "%";
                    int width = client.textRenderer.getWidth(value);
                    context.drawText(client.textRenderer, value,
                            x + (slotWidth - width) / 2 + 3, y + 18, color, false);
                }
            } else {
                context.fill(x + 8, y + 5, x + 16, y + 13, 0xFF35435A);
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
        int background = pressed ? 0xFF8A75E8 : PANEL;
        context.fill(x, y, x + width, y + 18, background);
        context.fill(x, y, x + width, y + 1, pressed ? AQUA : 0xFF394B68);
        int textWidth = client.textRenderer.getWidth(label);
        context.drawText(client.textRenderer, label,
                x + (width - textWidth) / 2, y + 5, pressed ? 0xFF080D18 : WHITE, false);
    }

    private static void drawHudCard(DrawContext context, MinecraftClient client,
                                    int x, int y, int width, int height,
                                    String label, String value, int accent) {
        context.fill(x, y, x + width, y + height, PANEL);
        context.fill(x, y, x + 2, y + height, accent);
        context.drawText(client.textRenderer, label, x + 8, y + 7, MUTED, false);
        int valueWidth = client.textRenderer.getWidth(value);
        context.drawTextWithShadow(client.textRenderer, value,
                x + width - valueWidth - 8, y + 7, WHITE);
    }

    private static String formatDuration(int ticks) {
        if (ticks < 0) return "∞";
        int seconds = ticks / 20;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private static String romanAmplifier(int amplifier) {
        return switch (amplifier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> "" + amplifier;
        };
    }

    private static String trim(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, Math.max(0, max - 1)) + "…";
    }
}
