package ai.derco;

public enum ModuleCategory {
    VISUALS("Visuals", "Visual effects and rendering"),
    HUD("HUD", "Information arranged around your screen"),
    PLAYER("Player", "View and camera controls");

    public final String title;
    public final String subtitle;

    ModuleCategory(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }
}
