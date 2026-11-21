package ai.derco.theme;

import net.minecraft.client.gui.DrawContext;

/**
 * Gradient fills for panels, header bars and progress tracks.
 * Vertical gradients use the vanilla primitive; horizontal ones are emitted as
 * a small number of strips because vanilla has no horizontal primitive.
 */
public final class ThemeGradients {
    private static final int STRIPS = 24;

    private ThemeGradients() {}

    /** Top to bottom. */
    public static void vertical(DrawContext context, int x, int y, int width, int height,
                                int top, int bottom) {
        if (width <= 0 || height <= 0) return;
        context.fillGradient(x, y, x + width, y + height, top, bottom);
    }

    /** Left to right, approximated with {@link #STRIPS} vertical strips. */
    public static void horizontal(DrawContext context, int x, int y, int width, int height,
                                  int left, int right) {
        if (width <= 0 || height <= 0) return;
        int strips = Math.min(STRIPS, width);
        for (int i = 0; i < strips; i++) {
            int x1 = x + width * i / strips;
            int x2 = x + width * (i + 1) / strips;
            if (x2 <= x1) continue;
            context.fill(x1, y, x2, y + height,
                    ThemeContrast.mix(left, right, (i + 0.5F) / strips));
        }
    }

    /** Electric Blue brand sweep: deep blue to aqua, used for accent bars. */
    public static void accentBar(DrawContext context, int x, int y, int width, int height) {
        horizontal(context, x, y, width, height, ThemeColors.PRIMARY, ThemeColors.ACCENT);
    }

    /** Subtle vertical sheen for cards: raised on top, sunk at the bottom. */
    public static void surfaceSheen(DrawContext context, int x, int y, int width, int height,
                                    int base) {
        vertical(context, x, y, width, height,
                ThemeContrast.lighten(base, 0.06F), ThemeContrast.darken(base, 0.06F));
    }

    /** Progress fill with the brand gradient; falls back to a flat bar when narrow. */
    public static void progress(DrawContext context, int x, int y, int width, int height,
                                float ratio, int color) {
        int filled = Math.round(Math.max(0.0F, Math.min(1.0F, ratio)) * width);
        if (filled <= 0) return;
        if (filled <= 3) {
            context.fill(x, y, x + filled, y + height, color);
            return;
        }
        horizontal(context, x, y, filled, height, ThemeContrast.lighten(color, 0.25F),
                ThemeContrast.darken(color, 0.15F));
    }
}
