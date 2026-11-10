package net.dercovisuals;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class Modules {
    private Modules() {}

    public static final Module INFO_HUD = new Module(
            "info_hud", "Info HUD", "FPS, position and facing in a compact status panel.",
            ModuleCategory.HUD, "Always on screen", true);
    public static final Module ARMOR_HUD = new Module(
            "armor_hud", "Armor HUD", "Displays equipped armor pieces and their durability.",
            ModuleCategory.HUD, "Bottom center", true);
    public static final Module KEYSTROKES = new Module(
            "keystrokes", "Keystrokes", "Shows movement keys with a pressed-state animation.",
            ModuleCategory.HUD, "Bottom left", true);
    public static final Module MODULE_LIST = new Module(
            "module_list", "Active modules", "A clean list of currently enabled DercoVisuals modules.",
            ModuleCategory.HUD, "Top right", true);
    public static final Module COMPASS = new Module(
            "compass", "Compass", "Displays the direction your player is facing.",
            ModuleCategory.HUD, "Top left", true);
    public static final Module CLOCK = new Module(
            "clock", "World clock", "Shows your local system time on the HUD.",
            ModuleCategory.HUD, "Top right", false);

    private static double savedGamma = 1.0;
    public static final Module FULLBRIGHT = new Module(
            "fullbright", "Fullbright", "Raises in-game gamma while enabled and restores it when disabled.",
            ModuleCategory.RENDER, "B", false) {
        @Override
        protected void onEnable() {
            MinecraftClient client = MinecraftClient.getInstance();
            savedGamma = client.options.getGamma().getValue();
            client.options.getGamma().setValue(16.0);
        }

        @Override
        protected void onDisable() {
            MinecraftClient.getInstance().options.getGamma().setValue(savedGamma);
        }
    };

    private static int savedFov = 70;
    private static boolean zooming;
    public static final Module ZOOM = new Module(
            "zoom", "Zoom", "Hold C to zoom in. Your original FOV is restored on release.",
            ModuleCategory.PLAYER, "Hold C", true);

    public static final List<Module> ALL = List.of(
            INFO_HUD, ARMOR_HUD, KEYSTROKES, MODULE_LIST, COMPASS, CLOCK, FULLBRIGHT, ZOOM
    );

    public static void loadConfig() {
        DercoConfig.load();
        if (FULLBRIGHT.isEnabled()) {
            MinecraftClient.getInstance().options.getGamma().setValue(16.0);
        }
    }

    static double getSavedGamma() {
        return savedGamma;
    }

    static void loadSavedGamma(double value) {
        savedGamma = Math.max(0.0, Math.min(16.0, value));
    }

    public static List<Module> inCategory(ModuleCategory category) {
        return ALL.stream().filter(module -> module.category == category).toList();
    }

    public static Module find(String id) {
        return ALL.stream().filter(module -> module.id.equals(id)).findFirst().orElse(null);
    }

    public static void updateZoom(MinecraftClient client, boolean keyHeld) {
        boolean wantZoom = keyHeld && ZOOM.isEnabled();
        if (wantZoom && !zooming) {
            savedFov = client.options.getFov().getValue();
            client.options.getFov().setValue(25);
            zooming = true;
        } else if (!wantZoom && zooming) {
            client.options.getFov().setValue(savedFov);
            zooming = false;
        }
    }
}
