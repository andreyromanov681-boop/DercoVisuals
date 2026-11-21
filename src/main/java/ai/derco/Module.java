package ai.derco;

public class Module {
    public final String id;
    public final String title;
    public final String description;
    public final ModuleCategory category;
    public final String shortcut;
    private boolean enabled;

    public Module(String id, String title, String description, ModuleCategory category,
                  String shortcut, boolean enabled) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.shortcut = shortcut;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
        // Deferred: DercoConfig coalesces writes instead of hitting the disk here.
        DercoConfig.markDirty();
    }

    /** Applies a value read from disk without triggering a save or a side effect. */
    public void loadEnabled(boolean value) {
        enabled = value;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {}
    protected void onDisable() {}
}
