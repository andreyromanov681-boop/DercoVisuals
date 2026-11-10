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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DercoVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "dercovisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger("DercoVisuals");
    private static final int ACCENT = 0xFF8B7CFF;
    private static final int CYAN = 0xFF55D6D0;
    private static final int WHITE = 0xFFF0F4FF;
    private static final int MUTED = 0xFFB7C3D8;
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
        Modules.updateZoom(client, zoomKey.isPressed());
    }

    private void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        if (Modules.INFO_HUD.isEnabled()) {
            int x = 8;
            int y = 8;
            context.fill(x, y, x + 220, y + 58, 0xB9101727);
            context.fill(x, y, x + 3, y + 58, ACCENT);
            context.drawTextWithShadow(client.textRenderer, "DERCO VISUALS", x + 10, y + 7, WHITE);
            context.drawText(client.textRenderer, "FPS  " + client.getCurrentFps(), x + 10, y + 23, MUTED, false);
            context.drawText(client.textRenderer,
                    String.format("XYZ  %.1f  %.1f  %.1f", client.player.getX(), client.player.getY(), client.player.getZ()),
                    x + 10, y + 37, MUTED, false);
        }

        if (Modules.COMPASS.isEnabled()) {
            String direction = client.player.getHorizontalFacing().asString().toUpperCase();
            int x = 8;
            int y = Modules.INFO_HUD.isEnabled() ? 72 : 8;
            context.fill(x, y, x + 90, y + 22, 0xB9101727);
            context.drawTextWithShadow(client.textRenderer, "FACING  " + direction, x + 8, y + 7, CYAN);
        }

        if (Modules.CLOCK.isEnabled()) {
            String clock = LocalTime.now().format(CLOCK_FORMAT);
            int w = client.textRenderer.getWidth(clock);
            int clockY = Modules.MODULE_LIST.isEnabled() ? 8 + Math.max(1, (int) Modules.ALL.stream().filter(Module::isEnabled).count()) * 16 : 8;
            context.fill(screenWidth - w - 28, clockY, screenWidth - 8, clockY + 20, 0xB9101727);
            context.drawTextWithShadow(client.textRenderer, clock, screenWidth - w - 18, clockY + 6, WHITE);
        }

        if (Modules.MODULE_LIST.isEnabled()) {
            int y = 8;
            for (Module module : Modules.ALL) {
                if (!module.isEnabled() || module == Modules.MODULE_LIST) continue;
                String label = module.title;
                int textWidth = client.textRenderer.getWidth(label);
                int x = screenWidth - textWidth - 18;
                context.fill(x - 6, y - 2, screenWidth - 8, y + 11, 0x96101727);
                context.fill(screenWidth - 10, y - 2, screenWidth - 8, y + 11, ACCENT);
                context.drawTextWithShadow(client.textRenderer, label, x, y, WHITE);
                y += 16;
            }
        }

        if (Modules.ARMOR_HUD.isEnabled()) {
            EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET};
            int totalWidth = slots.length * 20;
            int x = screenWidth / 2 - totalWidth / 2;
            int y = screenHeight - 28;
            context.fill(x - 5, y - 5, x + totalWidth + 1, y + 20, 0x96101727);
            for (EquipmentSlot slot : slots) {
                ItemStack stack = client.player.getEquippedStack(slot);
                if (!stack.isEmpty()) {
                    context.drawItem(stack, x, y);
                    context.drawStackOverlay(client.textRenderer, stack, x, y);
                }
                x += 20;
            }
        }

        if (Modules.KEYSTROKES.isEnabled()) {
            int x = 10;
            int y = screenHeight - 68;
            drawKey(context, client, "W", x + 20, y, client.options.forwardKey.isPressed());
            drawKey(context, client, "A", x, y + 20, client.options.leftKey.isPressed());
            drawKey(context, client, "S", x + 20, y + 20, client.options.backKey.isPressed());
            drawKey(context, client, "D", x + 40, y + 20, client.options.rightKey.isPressed());
        }
    }

    private static void drawKey(DrawContext context, MinecraftClient client,
                                String label, int x, int y, boolean pressed) {
        int background = pressed ? 0xFF8B7CFF : 0xB9101727;
        context.fill(x, y, x + 18, y + 18, background);
        context.fill(x, y, x + 18, y + 1, pressed ? CYAN : 0xFF394B68);
        int textWidth = client.textRenderer.getWidth(label);
        context.drawText(client.textRenderer, label,
                x + 9 - textWidth / 2, y + 5, pressed ? 0xFF080D18 : WHITE, false);
    }
}
