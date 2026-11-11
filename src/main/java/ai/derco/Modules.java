package ai.derco;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class Modules {
    private Modules() {}

    public static final Module INFO_HUD = new Module(
            "info_hud", "Info panel", "Compact FPS and XYZ information for a quick overview.",
            ModuleCategory.HUD, "Always on screen", true);
    public static final Module ARMOR_HUD = new Module(
            "armor_hud", "Armor display", "Shows equipped armor, item icons, and remaining durability.",
            ModuleCategory.HUD, "Bottom center", true);
    public static final Module KEYSTROKES = new Module(
            "keystrokes", "Keystrokes", "Animated movement and jump keys with a pressed-state highlight.",
            ModuleCategory.HUD, "Bottom left", true);
    public static final Module MODULE_LIST = new Module(
            "module_list", "Module list", "A minimal live list of enabled DercoVisuals modules.",
            ModuleCategory.HUD, "Top right", true);
    public static final Module COMPASS = new Module(
            "compass", "Direction", "Displays the cardinal direction your player is facing.",
            ModuleCategory.HUD, "Top left", true);
    public static final Module CLOCK = new Module(
            "clock", "Local clock", "Shows the current local time in a compact HUD card.",
            ModuleCategory.HUD, "Top right", false);
    public static final Module POTION_HUD = new Module(
            "potion_hud", "Potion effects", "Lists active potion effects with amplifier and remaining time.",
            ModuleCategory.HUD, "Left side", false);
    public static final Module SPEED_HUD = new Module(
            "speed_hud", "Speed meter", "Displays horizontal movement speed in blocks per second.",
            ModuleCategory.HUD, "Bottom right", false);

    public static final Module INVENTORY_HUD = new Module(
            "inventory_hud", "Inventory preview", "A live 3 by 9 preview of your main inventory with item counts and durability.",
            ModuleCategory.HUD, "Above hotbar", false);
    public static final Module TOTEMS_HUD = new Module(
            "totems_hud", "Totem counter", "Counts Totems of Undying in your inventory and offhand.",
            ModuleCategory.HUD, "Bottom right", false);
    public static final Module SATURATION_HUD = new Module(
            "saturation_hud", "Food status", "Shows your food level and saturation as separate readable bars.",
            ModuleCategory.HUD, "Left side", false);
    public static final Module COOLDOWNS_HUD = new Module(
            "cooldowns_hud", "Cooldown tracker", "Shows active item cooldowns with item icons and remaining progress.",
            ModuleCategory.HUD, "Top center", false);

    private static double savedGamma = 1.0;
    public static final Module FULLBRIGHT = new Module(
            "fullbright", "Fullbright", "Raises world brightness while enabled and restores your previous gamma.",
            ModuleCategory.VISUALS, "B", false) {
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
            "zoom", "Zoom", "Hold C to zoom in. Your original field of view is restored on release.",
            ModuleCategory.PLAYER, "Hold C", true);

    public static final List<Module> ALL = List.of(
            INFO_HUD, ARMOR_HUD, KEYSTROKES, MODULE_LIST, COMPASS, CLOCK, POTION_HUD, SPEED_HUD,
            INVENTORY_HUD, TOTEMS_HUD, SATURATION_HUD, COOLDOWNS_HUD,
            FULLBRIGHT, ZOOM
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
