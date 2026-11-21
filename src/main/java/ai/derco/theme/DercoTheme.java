package ai.derco.theme;

import java.util.Properties;

/**
 * The single entry point every renderer uses to read the live theme.
 *
 * <p>{@link ThemeSettings} holds the player's knobs; {@link DercoConfig} loads and
 * stores them through {@link #read(Properties)} / {@link #write(Properties)}. The
 * renderers never paint raw literals: colours come from {@link ThemeColors},
 * spacing from {@link ThemeMetrics} and the contrast helpers from here.</p>
 */
public final class DercoTheme {
    /** Pristine defaults, used by {@link #reset()} and by new installs. */
    private static final ThemeSettings DEFAULTS = new ThemeSettings();
    private static final ThemeSettings SETTINGS = new ThemeSettings();

    private DercoTheme() {}

    public static ThemeSettings settings() {
        return SETTINGS;
    }

    /** Corner radius that the panel helpers actually use (0 when rounded panels are off). */
    public static int radius() {
        return SETTINGS.roundedPanels() ? SETTINGS.radius() : 0;
    }

    /** Radius never bigger than a small element can take, for pills and chips. */
    public static int smallRadius() {
        return Math.min(radius(), ThemeMetrics.RADIUS_SMALL + (radius() > 0 ? 1 : 0));
    }

    /** Primary text colour that stays readable on the given surface. */
    public static int textOn(int surface) {
        int color = ThemeContrast.readable(surface);
        return SETTINGS.contrastBoost() ? ThemeContrast.ensureContrast(color, surface, 4.5F) : color;
    }

    /** Secondary text colour (labels, hints) for the given surface. */
    public static int secondaryTextOn(int surface) {
        int color = ThemeContrast.readableSecondary(surface);
        return SETTINGS.contrastBoost() ? ThemeContrast.ensureContrast(color, surface, 3.0F) : color;
    }

    /** Accent colour that keeps at least large-text contrast on the given surface. */
    public static int accentOn(int surface) {
        return SETTINGS.contrastBoost()
                ? ThemeContrast.ensureContrast(ThemeColors.ACCENT, surface, 3.0F)
                : ThemeColors.ACCENT;
    }

    /** Brand colour that keeps at least large-text contrast on the given surface. */
    public static int primaryOn(int surface) {
        return SETTINGS.contrastBoost()
                ? ThemeContrast.ensureContrast(ThemeColors.PRIMARY_LIGHT, surface, 3.0F)
                : ThemeColors.PRIMARY;
    }

    /** Card / nav background for the given interaction state. */
    public static int surfaceFor(boolean selected, boolean hovered, int hoverAmount) {
        int base = selected ? ThemeColors.SURFACE_SELECTED : ThemeColors.SURFACE_RAISED;
        if (!hovered && hoverAmount <= 0) return base;
        return ThemeContrast.mix(base, ThemeColors.SURFACE_HOVER,
                Math.max(0.0F, Math.min(1.0F, hoverAmount / 100.0F)));
    }

    public static void write(Properties properties) {
        SETTINGS.write(properties);
    }

    public static void read(Properties properties) {
        SETTINGS.read(properties);
    }

    /** Back to factory defaults (used by "reset theme" and by malformed configs). */
    public static void reset() {
        Properties defaults = new Properties();
        DEFAULTS.write(defaults);
        SETTINGS.read(defaults);
    }
}
