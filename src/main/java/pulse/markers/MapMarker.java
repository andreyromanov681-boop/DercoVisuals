package pulse.markers;

import java.awt.Color;

public class MapMarker {
   private String name;
   private int x;
   private int y;
   private int z;
   private Color color;
   private MapMarker.Icon icon;
   private boolean serverBound;
   private long createdAt;
   private int serverId;
   private String serverAddress;
   private long expiresAt;
   private boolean showTimer;

   public MapMarker(String str, int i, int i2, int i3, Color color, MapMarker.Icon icon) {
      this.name = str;
      this.x = i;
      this.y = i2;
      this.z = i3;
      this.color = color;
      this.icon = icon != null ? icon : MapMarker.Icon.EVENT;
      this.createdAt = System.currentTimeMillis();
   }

   public static MapMarker a(String str, int i, int i2, int i3, Color color, MapMarker.Icon icon, int i4, String str2, long j, boolean z) {
      MapMarker mapMarker = new MapMarker(str, i, i2, i3, color, icon);
      mapMarker.serverBound = true;
      mapMarker.serverId = i4;
      mapMarker.serverAddress = str2;
      mapMarker.expiresAt = System.currentTimeMillis() + j;
      mapMarker.showTimer = z;
      return mapMarker;
   }

   public String a() {
      return this.name;
   }

   public void a(String str) {
      this.name = str;
   }

   public int b() {
      return this.x;
   }

   public void a(int i) {
      this.x = i;
   }

   public int c() {
      return this.y;
   }

   public void b(int i) {
      this.y = i;
   }

   public int d() {
      return this.z;
   }

   public void c(int i) {
      this.z = i;
   }

   public Color e() {
      return this.color;
   }

   public void a(Color color) {
      this.color = color;
   }

   public MapMarker.Icon f() {
      return this.icon;
   }

   public void a(MapMarker.Icon icon) {
      this.icon = icon != null ? icon : MapMarker.Icon.EVENT;
   }

   public String g() {
      return this.x + ", " + this.y + ", " + this.z;
   }

   public boolean h() {
      return this.icon == MapMarker.Icon.DEATH;
   }

   public boolean i() {
      return this.icon == MapMarker.Icon.FAST;
   }

   public boolean j() {
      return this.serverBound;
   }

   public long k() {
      return this.createdAt;
   }

   public int l() {
      return this.serverId;
   }

   public void d(int i) {
      this.serverId = i;
   }

   public String m() {
      return this.serverAddress;
   }

   public void b(String str) {
      this.serverAddress = str;
   }

   public long n() {
      return this.expiresAt;
   }

   public void a(long j) {
      this.expiresAt = j;
   }

   public boolean o() {
      return this.serverBound && this.expiresAt > 0L && System.currentTimeMillis() > this.expiresAt;
   }

   public long p() {
      return this.serverBound && this.expiresAt > 0L ? Math.max(0L, this.expiresAt - System.currentTimeMillis()) : -1L;
   }

   public boolean q() {
      return this.icon == MapMarker.Icon.EVENT;
   }

   public boolean r() {
      return this.showTimer;
   }

   public void a(boolean z) {
      this.showTimer = z;
   }

   public enum Icon {
      HOME("home", "\ue90f"),
      REPAIR("repair", "\ue91f"),
      FAST("fast", "\ue90b"),
      SHIELD("shield", "\ue923"),
      DEATH("death", "\ue907"),
      DIAMOND("diamond", "\ue908"),
      LOCKED("locked", "\ue914"),
      MOUNTAIN("mountain", "\ue917"),
      CALENDAR("calendar", "\ue90a"),
      EVENT("calendar", "\ue90a");

      private final String id;
      private final String glyph;

      Icon(String str, String str2) {
         this.id = str;
         this.glyph = str2;
      }

      public String a() {
         return this.id;
      }

      public String b() {
         return this.glyph;
      }
   }
}
