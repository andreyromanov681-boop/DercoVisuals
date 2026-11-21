package ai.derco.theme;

/**
 * Spacing and sizing rules of the DercoVisuals layout grid.
 * Everything the GUI and HUD position uses these numbers so both stay aligned.
 */
public final class ThemeMetrics {
    private ThemeMetrics() {}

    // Corner radius (only used by the rounded helpers; 0 = square panels).
    public static final int RADIUS = 4;
    public static final int RADIUS_SMALL = 2;

    // Padding.
    public static final int PAD_XS = 4;
    public static final int PAD_S = 8;
    public static final int PAD_M = 12;
    public static final int PAD_L = 16;
    public static final int PAD_XL = 24;

    // Gaps.
    public static final int GAP_XS = 4;
    public static final int GAP_S = 6;
    public static final int GAP_M = 8;
    public static final int GAP_L = 12;

    // Window chrome.
    public static final int HEADER_H = 62;
    public static final int FOOTER_H = 28;
    public static final int SIDEBAR_MIN = 112;
    public static final int SIDEBAR_MAX = 142;
    public static final int DETAILS_MIN = 148;
    public static final int DETAILS_MAX = 198;

    // Module cards.
    public static final int CARD_H = 82;
    public static final int CARD_MIN_W = 82;
    public static final int CARD_TITLE_Y = 9;
    public static final int CARD_DESC_Y = 28;
    public static final int NAV_H = 34;
    public static final int TOGGLE_W = 54;
    public static final int TOGGLE_H = 18;

    // HUD.
    public static final int HUD_CARD_H = 22;
    public static final int HUD_ACCENT_W = 3;
    public static final int HUD_GAP = 6;
    public static final int HUD_MARGIN = 8;
    public static final int BAR_H = 4;
    public static final int SLOT = 21;
    public static final int KEY_SIZE = 18;
    public static final int KEY_ROW_H = 20;

    // Animation (fraction of the remaining distance per tick).
    public static final float FADE_SPEED = 0.35F;
    public static final float HOVER_SPEED = 0.5F;

    public static int clampPad(int value) {
        return Math.max(0, Math.min(PAD_XL, value));
    }
}
