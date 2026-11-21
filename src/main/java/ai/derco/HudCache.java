package ai.derco;

import ai.derco.theme.ThemeMetrics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Per-tick snapshot of everything the HUD shows.
 *
 * <p>Before this class the HUD built strings, scanned the inventory and created
 * item stacks on <em>every frame</em>. The HUD now asks the cache; the cache
 * refreshes once per client tick (20 Hz) — or every few ticks for the expensive
 * scans — so frame time no longer depends on inventory size or potion count.</p>
 */
public final class HudCache {
    /** A single reusable totem icon: the old code allocated one every frame. */
    public static final ItemStack TOTEM_ICON = new ItemStack(Items.TOTEM_OF_UNDYING);

    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MODULE_ROW_H = 18;
    private static final StringBuilder BUILDER = new StringBuilder(32);
    private static final Set<Identifier> SEEN_GROUPS = new HashSet<>();
    private static final List<ItemStack> INVENTORY = new ArrayList<>(27);
    private static final List<String> MODULE_LABELS = new ArrayList<>();
    private static final List<String> EFFECTS = new ArrayList<>(5);
    private static final List<CooldownEntry> COOLDOWNS = new ArrayList<>(4);

    private static String fpsText = "0";
    private static String coordsText = "0.0 0.0 0.0";
    private static String facingText = "N";
    private static String clockText = "";
    private static String speedText = "0.00";
    private static int moduleListBottom = ThemeMetrics.HUD_MARGIN;
    private static int totemCount;
    private static int tickCounter;

    private HudCache() {}

    public record CooldownEntry(ItemStack stack, String name, String percent, float progress) {}

    /** Refreshes everything. Called from the client tick, never from a frame. */
    public static void update(MinecraftClient client) {
        if (client.player == null) return;
        tickCounter++;

        fpsText = Integer.toString(client.getCurrentFps());

        BUILDER.setLength(0);
        appendFixed(BUILDER, client.player.getX(), 1);
        BUILDER.append(' ');
        appendFixed(BUILDER, client.player.getY(), 1);
        BUILDER.append(' ');
        appendFixed(BUILDER, client.player.getZ(), 1);
        coordsText = BUILDER.toString();

        facingText = client.player.getHorizontalFacing().asString().toUpperCase(Locale.ROOT);

        if (clockText.isEmpty() || tickCounter % 20 == 0) {
            clockText = LocalTime.now().format(CLOCK_FORMAT);
        }

        var velocity = client.player.getVelocity();
        double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z) * 20.0;
        BUILDER.setLength(0);
        appendFixed(BUILDER, speed, 2);
        speedText = BUILDER.toString();

        updateModuleList();
        if (tickCounter % 4 == 1) updateTotems(client);
        if (tickCounter % 2 == 0) updateInventory(client);
        updateEffects(client);
        updateCooldowns(client);
    }

    private static void updateModuleList() {
        MODULE_LABELS.clear();
        int bottom = ThemeMetrics.HUD_MARGIN;
        for (Module module : Modules.ALL) {
            if (!module.isEnabled() || module == Modules.MODULE_LIST) continue;
            MODULE_LABELS.add(module.title);
            bottom += MODULE_ROW_H;
        }
        moduleListBottom = bottom;
    }

    private static void updateTotems(MinecraftClient client) {
        int count = 0;
        var inventory = client.player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) count += stack.getCount();
        }
        ItemStack offhand = client.player.getOffHandStack();
        if (offhand.isOf(Items.TOTEM_OF_UNDYING)) count += offhand.getCount();
        totemCount = count;
    }

    private static void updateInventory(MinecraftClient client) {
        INVENTORY.clear();
        var inventory = client.player.getInventory();
        for (int index = 0; index < 27; index++) {
            INVENTORY.add(inventory.getStack(9 + index));
        }
    }

    private static void updateEffects(MinecraftClient client) {
        EFFECTS.clear();
        int shown = 0;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (shown >= 5) break;
            String name = Text.translatable(effect.getTranslationKey()).getString();
            BUILDER.setLength(0);
            BUILDER.append(name).append(' ').append(romanAmplifier(effect.getAmplifier() + 1))
                    .append("  ·  ");
            appendDuration(BUILDER, effect.getDuration());
            EFFECTS.add(BUILDER.toString());
            shown++;
        }
    }

    private static void updateCooldowns(MinecraftClient client) {
        COOLDOWNS.clear();
        var manager = client.player.getItemCooldownManager();
        var inventory = client.player.getInventory();
        SEEN_GROUPS.clear();
        for (int slot = 0; slot < 36 && COOLDOWNS.size() < 4; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty() || !manager.isCoolingDown(stack)) continue;
            Identifier group = manager.getGroup(stack);
            if (group != null && !SEEN_GROUPS.add(group)) continue;
            float remaining = Math.max(0.0F, Math.min(1.0F,
                    manager.getCooldownProgress(stack, 1.0F)));
            String name = stack.getName().getString();
            if (name.length() > 13) name = name.substring(0, 12) + "…";
            COOLDOWNS.add(new CooldownEntry(stack, name,
                    Math.round(remaining * 100.0F) + "%", remaining));
        }
    }

    public static String fpsText() {
        return fpsText;
    }

    public static String coordsText() {
        return coordsText;
    }

    public static String facingText() {
        return facingText;
    }

    public static String clockText() {
        return clockText;
    }

    public static String speedText() {
        return speedText;
    }

    /** Bottom edge (scaled px) of the last module-list row; the clock sits under it. */
    public static int moduleListBottom() {
        return moduleListBottom;
    }

    public static int totemCount() {
        return totemCount;
    }

    public static List<String> moduleLabels() {
        return MODULE_LABELS;
    }

    public static List<String> effectLines() {
        return EFFECTS;
    }

    public static List<ItemStack> inventory() {
        return INVENTORY;
    }

    public static List<CooldownEntry> cooldowns() {
        return COOLDOWNS;
    }

    /** Fixed-point formatting without String.format (§ allocation per call). */
    private static void appendFixed(StringBuilder out, double value, int decimals) {
        long scale = decimals == 1 ? 10L : 100L;
        long rounded = Math.round(value * scale);
        if (rounded < 0) {
            out.append('-');
            rounded = -rounded;
        }
        out.append(rounded / scale).append('.');
        long fraction = rounded % scale;
        if (decimals == 2 && fraction < 10) out.append('0');
        out.append(fraction);
    }

    private static void appendDuration(StringBuilder out, int ticks) {
        if (ticks < 0) {
            out.append('∞');
            return;
        }
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int rest = seconds % 60;
        out.append(minutes).append(':');
        if (rest < 10) out.append('0');
        out.append(rest);
    }

    private static String romanAmplifier(int amplifier) {
        return switch (amplifier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> Integer.toString(amplifier);
        };
    }
}
