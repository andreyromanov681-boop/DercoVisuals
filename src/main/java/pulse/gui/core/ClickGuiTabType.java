package pulse.gui.core;

public enum ClickGuiTabType {
   MODULES("modules", "\ue916"),
   MARKERS("markers", "\ue915"),
   FRIENDS("friends", "\ue90d"),
   CONFIGS("configs", "\ue905");

   private final String id;
   private final String icon;

   ClickGuiTabType(String str, String str2) {
      this.id = str;
      this.icon = str2;
   }

   public String a() {
      return this.id;
   }

   public String b() {
      return this.icon;
   }
}
