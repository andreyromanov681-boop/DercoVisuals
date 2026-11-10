package net.dercovisuals;

public enum ModuleCategory {
    HUD("HUD", "On-screen information and widgets"),
    PLAYER("Player", "Player-side visual helpers"),
    RENDER("Render", "Brightness and display options");

    public final String title;
    public final String subtitle;

    ModuleCategory(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }
}
