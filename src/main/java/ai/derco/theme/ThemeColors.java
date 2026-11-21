package ai.derco.theme;

/**
 * DercoVisuals Electric Blue palette.
 *
 * <p>Single source of truth for every colour used by the client: ClickGUI, HUD and
 * effects read from here instead of hard-coding ARGB literals. Values are ARGB
 * (0xAARRGGBB) because that is what {@code DrawContext.fill} takes.</p>
 */
public final class ThemeColors {
    private ThemeColors() {}

    // Brand — Electric Blue, never violet.
    public static final int PRIMARY = 0xFF347BFA;
    public static final int PRIMARY_LIGHT = 0xFF5E9BFF;
    public static final int PRIMARY_DARK = 0xFF1F55B8;
    public static final int ACCENT = 0xFF39D9F2;
    public static final int ACCENT_LIGHT = 0xFF7DEAFB;
    public static final int ACCENT_DARK = 0xFF1A8FA8;

    // Surfaces — dark cool navy so the blue accents keep contrast.
    public static final int BACKDROP = 0xC0060C16;
    public static final int WINDOW = 0xF10A1220;
    public static final int SURFACE = 0xFF111C2C;
    public static final int SURFACE_RAISED = 0xFF17253A;
    public static final int SURFACE_HOVER = 0xFF1C2E47;
    public static final int SURFACE_SELECTED = 0xFF20395A;
    public static final int SLOT = 0xA808111C;
    public static final int BORDER = 0xFF22374E;
    public static final int BORDER_STRONG = 0xFF2E4A68;
    public static final int TRACK = 0xFF1B2A3E;

    // Type.
    public static final int TEXT = 0xFFF1F4FC;
    public static final int TEXT_SECONDARY = 0xFFB7C3D8;
    public static final int MUTED = 0xFF8FA0B8;
    public static final int DISABLED = 0xFF5C6B82;
    public static final int TEXT_ON_ACCENT = 0xFF06101C;

    // States.
    public static final int SUCCESS = 0xFF43D98B;
    public static final int WARNING = 0xFFFFC857;
    public static final int DANGER = 0xFFFF6B7A;

    // Depth.
    public static final int SHADOW = 0x66000000;
    public static final int SHADOW_SOFT = 0x33000000;
    public static final int HUD_PANEL = 0xB90C1524;
    public static final int HUD_PANEL_SOLID = 0xFF0C1524;

    /** HUD widgets never use pure black: #000000 looks dead on dark worlds. */
    public static int hudShadow() {
        return SHADOW_SOFT;
    }
}
