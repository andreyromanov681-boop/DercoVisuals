package ai.derco.theme;

import java.util.Properties;

/**
 * Player-facing theme knobs, persisted by {@code DercoConfig} under the
 * {@code theme.*} prefix. Defaults reproduce the Electric Blue design exactly,
 * so a missing config file needs no migration.
 */
public final class ThemeSettings {
    private int radius = ThemeMetrics.RADIUS;
    private boolean roundedPanels = true;
    private boolean glow = true;
    private boolean animations = true;
    private boolean contrastBoost = false;
    private float animationSpeed = 1.0F;
    private float hudScale = 1.0F;

    public int radius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(0, Math.min(10, radius));
    }

    public boolean roundedPanels() {
        return roundedPanels;
    }

    public void setRoundedPanels(boolean roundedPanels) {
        this.roundedPanels = roundedPanels;
    }

    public boolean glow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public boolean animations() {
        return animations;
    }

    public void setAnimations(boolean animations) {
        this.animations = animations;
    }

    public boolean contrastBoost() {
        return contrastBoost;
    }

    public void setContrastBoost(boolean contrastBoost) {
        this.contrastBoost = contrastBoost;
    }

    public float animationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = Math.max(0.25F, Math.min(3.0F, animationSpeed));
    }

    public float hudScale() {
        return hudScale;
    }

    public void setHudScale(float hudScale) {
        this.hudScale = Math.max(0.75F, Math.min(1.5F, hudScale));
    }

    public void write(Properties properties) {
        properties.setProperty("theme.radius", Integer.toString(radius));
        properties.setProperty("theme.roundedPanels", Boolean.toString(roundedPanels));
        properties.setProperty("theme.glow", Boolean.toString(glow));
        properties.setProperty("theme.animations", Boolean.toString(animations));
        properties.setProperty("theme.contrastBoost", Boolean.toString(contrastBoost));
        properties.setProperty("theme.animationSpeed", Float.toString(animationSpeed));
        properties.setProperty("theme.hudScale", Float.toString(hudScale));
    }

    /** Reads whatever is present, keeps the default for anything missing or broken. */
    public void read(Properties properties) {
        setRadius(intProperty(properties, "theme.radius", radius));
        setRoundedPanels(boolProperty(properties, "theme.roundedPanels", roundedPanels));
        setGlow(boolProperty(properties, "theme.glow", glow));
        setAnimations(boolProperty(properties, "theme.animations", animations));
        setContrastBoost(boolProperty(properties, "theme.contrastBoost", contrastBoost));
        setAnimationSpeed(floatProperty(properties, "theme.animationSpeed", animationSpeed));
        setHudScale(floatProperty(properties, "theme.hudScale", hudScale));
    }

    private static int intProperty(Properties properties, String key, int fallback) {
        try {
            String raw = properties.getProperty(key);
            return raw == null ? fallback : Integer.parseInt(raw.trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static float floatProperty(Properties properties, String key, float fallback) {
        try {
            String raw = properties.getProperty(key);
            return raw == null ? fallback : Float.parseFloat(raw.trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static boolean boolProperty(Properties properties, String key, boolean fallback) {
        String raw = properties.getProperty(key);
        return raw == null ? fallback : Boolean.parseBoolean(raw.trim());
    }
}
