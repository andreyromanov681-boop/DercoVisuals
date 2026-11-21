package ai.derco;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Texture handles for the original DercoVisuals PNG set.
 *
 * <p>The art lives in {@code assets/dercovisuals/textures/gui/}. A texture is only
 * drawn when the pack actually ships it, so the client still renders (with the
 * procedural fallback) when a build has no art files — nothing crashes and no
 * magenta "missing texture" squares appear.</p>
 */
public final class DercoAssets {
    private static final String GUI = "textures/gui/";
    public static final Identifier LOGO = id("derco_logo");
    public static final Identifier ICON = id("icon");
    public static final Identifier MENU_BACKGROUND = id("menu_bg");

    private static Boolean logoPresent;
    private static Boolean iconPresent;

    private DercoAssets() {}

    private static Identifier id(String name) {
        return Identifier.of(DercoVisualsClient.MOD_ID, GUI + name + ".png");
    }

    public static boolean logoPresent() {
        if (logoPresent == null) logoPresent = exists(LOGO);
        return logoPresent;
    }

    public static boolean iconPresent() {
        if (iconPresent == null) iconPresent = exists(ICON);
        return iconPresent;
    }

    /** Draws a GUI texture scaled into the given box; caller checked presence first. */
    public static void draw(DrawContext context, Identifier texture, int x, int y, int size,
                            int sourceWidth, int sourceHeight) {
        context.drawTexture(texture, x, y, 0.0F, 0.0F, size, size, sourceWidth, sourceHeight);
    }

    private static boolean exists(Identifier identifier) {
        try {
            return MinecraftClient.getInstance().getResourceManager()
                    .getResource(identifier).isPresent();
        } catch (Throwable ignored) {
            return false;
        }
    }
}
