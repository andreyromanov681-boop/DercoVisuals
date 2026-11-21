package ai.derco.theme;

import net.minecraft.client.gui.DrawContext;

/**
 * Panel primitives: rounded rectangles, soft shadow, glow and outlines.
 *
 * <p>A rounded fill costs one quad for the body plus one quad per corner row, so
 * a 300 px tall panel is ~13 quads instead of 300. Corner rows are inset by a
 * circular cut. Nothing here scales with the panel height, which matters on the
 * Intel iGPU the client targets.</p>
 */
public final class ThemeEffects {
    private ThemeEffects() {}

    public static void panel(DrawContext context, int x, int y, int width, int height, int fill) {
        roundedFill(context, x, y, width, height, fill, DercoTheme.radius());
    }

    public static void roundedFill(DrawContext context, int x, int y, int width, int height,
                                   int color, int radius) {
        if (width <= 0 || height <= 0) return;
        int r = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
        if (r == 0 || height <= r * 2) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }
        // Body between the corner bands: a single quad.
        context.fill(x, y + r, x + width, y + height - r, color);
        for (int row = 0; row < r; row++) {
            int inset = cornerInset(row, r);
            int left = x + inset;
            int right = x + width - inset;
            if (right <= left) continue;
            context.fill(left, y + row, right, y + row + 1, color);
            int bottomRow = y + height - 1 - row;
            context.fill(left, bottomRow, right, bottomRow + 1, color);
        }
    }

    /** Shadow plus optional outer glow behind a panel. */
    public static void shadow(DrawContext context, int x, int y, int width, int height, int radius) {
        int r = Math.max(1, radius + 2);
        roundedFill(context, x - 2, y - 1, width + 4, height + 3, ThemeColors.SHADOW, r);
        if (DercoTheme.settings().glow()) {
            glow(context, x, y, width, height, ThemeColors.PRIMARY, 2);
        }
    }

    /** Glow: a few translucent rings of the accent colour around the box. */
    public static void glow(DrawContext context, int x, int y, int width, int height,
                            int color, int layers) {
        for (int layer = layers; layer >= 1; layer--) {
            int alpha = Math.max(6, 22 / layer);
            roundedFill(context, x - layer, y - layer, width + layer * 2, height + layer * 2,
                    ThemeContrast.withAlpha(color, alpha), DercoTheme.radius() + layer);
        }
    }

    public static void outline(DrawContext context, int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) return;
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    /** Left accent stripe of a card, coloured with the brand gradient. */
    public static void accentStripe(DrawContext context, int x, int y, int width, int height,
                                    int color) {
        int stripe = Math.max(2, width);
        ThemeGradients.vertical(context, x, y, stripe, height,
                ThemeContrast.lighten(color, 0.18F), ThemeContrast.darken(color, 0.12F));
    }

    /** Toggle pill: track plus knob, both rounded, knob travels smoothly. */
    public static void toggle(DrawContext context, int x, int y, int width, int height,
                              boolean on, float progress) {
        float state = Math.max(0.0F, Math.min(1.0F, progress));
        if (!DercoTheme.settings().animations()) state = on ? 1.0F : 0.0F;
        int radius = Math.max(1, height / 2);
        int track = ThemeContrast.mix(ThemeColors.TRACK,
                ThemeContrast.mix(ThemeColors.PRIMARY_DARK, ThemeColors.PRIMARY, state),
                on ? Math.max(state, 0.35F) : state * 0.4F);
        roundedFill(context, x, y, width, height, track, radius);
        int knobSize = height - 4;
        int travel = width - knobSize - 4;
        int knobX = x + 2 + Math.round(travel * (on ? Math.max(state, 0.0F) : state * 0.0F));
        roundedFill(context, knobX, y + 2, knobSize, knobSize,
                on ? ThemeColors.TEXT : ThemeColors.MUTED, knobSize / 2);
    }

    /** Circular cut of a rounded corner: how many pixels this row gives up. */
    private static int cornerInset(int row, int radius) {
        double dy = radius - row - 0.5;
        double cut = radius - Math.sqrt(Math.max(0.0, (double) radius * radius - dy * dy));
        return (int) Math.round(cut);
    }
}
