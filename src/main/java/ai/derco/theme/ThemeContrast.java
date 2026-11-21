package ai.derco.theme;

/**
 * Colour maths for the theme: ARGB manipulation, perceptual luminance and a
 * helper that keeps text readable on top of whatever surface it lands on.
 * No Minecraft types here so it stays trivially testable and cheap.
 */
public final class ThemeContrast {
    private static final float[] LUMINANCE = new float[256];

    static {
        for (int i = 0; i < 256; i++) {
            float channel = i / 255.0F;
            LUMINANCE[i] = channel <= 0.03928F
                    ? channel / 12.92F
                    : (float) Math.pow((channel + 0.055F) / 1.055F, 2.4F);
        }
    }

    private ThemeContrast() {}

    public static int alpha(int color) {
        return (color >>> 24) & 0xFF;
    }

    public static int red(int color) {
        return (color >>> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >>> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int rgba(int r, int g, int b, int a) {
        return (clamp(a) << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }

    public static int withAlpha(int color, int a) {
        return (clamp(a) << 24) | (color & 0x00FFFFFF);
    }

    public static int multiplyAlpha(int color, float factor) {
        return withAlpha(color, Math.round(alpha(color) * Math.max(0.0F, Math.min(1.0F, factor))));
    }

    public static int mix(int from, int to, float t) {
        float clamped = Math.max(0.0F, Math.min(1.0F, t));
        return rgba(
                Math.round(red(from) + (red(to) - red(from)) * clamped),
                Math.round(green(from) + (green(to) - green(from)) * clamped),
                Math.round(blue(from) + (blue(to) - blue(from)) * clamped),
                Math.round(alpha(from) + (alpha(to) - alpha(from)) * clamped));
    }

    public static int lighten(int color, float amount) {
        return mix(color, 0xFFFFFFFF, amount);
    }

    public static int darken(int color, float amount) {
        return mix(color, 0xFF000000, amount);
    }

    /** WCAG relative luminance, 0 = black, 1 = white. */
    public static float luminance(int color) {
        return 0.2126F * LUMINANCE[red(color)]
                + 0.7152F * LUMINANCE[green(color)]
                + 0.0722F * LUMINANCE[blue(color)];
    }

    public static float contrastRatio(int first, int second) {
        float a = luminance(first) + 0.05F;
        float b = luminance(second) + 0.05F;
        return a > b ? a / b : b / a;
    }

    public static boolean isDark(int color) {
        return luminance(color) < 0.42F;
    }

    /**
     * Text colour that reads on the given surface: the light theme text on dark
     * surfaces, deep ink on bright ones (accent fills, badges).
     */
    public static int readable(int surface) {
        return isDark(surface) ? ThemeColors.TEXT : ThemeColors.TEXT_ON_ACCENT;
    }

    public static int readableSecondary(int surface) {
        return isDark(surface) ? ThemeColors.TEXT_SECONDARY : ThemeColors.PRIMARY_DARK;
    }

    /**
     * Nudges {@code foreground} towards white or black until it reaches the
     * requested contrast ratio against {@code background} (4.5 is the usual body
     * text target, 3.0 for large text). Used when "boost contrast" is on.
     */
    public static int ensureContrast(int foreground, int background, float target) {
        if (contrastRatio(foreground, background) >= target) return foreground;
        int towards = isDark(background) ? 0xFFFFFFFF : 0xFF000000;
        int result = foreground;
        for (int step = 1; step <= 10; step++) {
            result = mix(foreground, towards, step / 10.0F);
            if (contrastRatio(result, background) >= target) return result;
        }
        return towards;
    }

    private static int clamp(int value) {
        return value < 0 ? 0 : Math.min(255, value);
    }
}
