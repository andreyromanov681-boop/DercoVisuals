package ai.derco.theme;

/**
 * One cheap animated float (0..1) for hover glow, toggle knobs and fades.
 *
 * <p>It is a plain exponential ease towards a target, advanced once per frame by
 * the renderer. Nothing is allocated while animating and the value snaps to the
 * target as soon as the animation setting is off, so the GUI stays usable on weak
 * hardware and when the player disables animations.</p>
 */
public final class ThemeAnimation {
    private final float speed;
    private float value;
    private float target;

    public ThemeAnimation(float speed) {
        this.speed = speed;
    }

    public void target(float next) {
        this.target = clamp(next);
    }

    public void snap(float next) {
        this.value = clamp(next);
        this.target = this.value;
    }

    /** Advance by one frame. */
    public void tick() {
        tick(1.0F);
    }

    /** Advance by {@code frames} frames (used after a slow frame). */
    public void tick(float frames) {
        if (!DercoTheme.settings().animations()) {
            value = target;
            return;
        }
        float step = Math.min(1.0F, speed * DercoTheme.settings().animationSpeed() * frames);
        value += (target - value) * step;
        if (Math.abs(target - value) < 0.002F) value = target;
    }

    /** Raw 0..1 value. */
    public float value() {
        return value;
    }

    /** Smooth-step eased 0..1 value, the one to mix colours with. */
    public float eased() {
        float v = value;
        return v * v * (3.0F - 2.0F * v);
    }

    /** Same as {@link #eased()} but expressed as 0..100 for the theme mix helpers. */
    public int percent() {
        return Math.round(eased() * 100.0F);
    }

    private static float clamp(float candidate) {
        return candidate < 0.0F ? 0.0F : Math.min(1.0F, candidate);
    }
}
