package pulse.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfigEntry {
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM HH:mm");
   private String name;
   private final String data;
   private final LocalDateTime createdAt;
   private boolean selected;
   private final boolean shared;
   private final int remoteId;
   private final String owner;

   public ConfigEntry(String str, String str2, LocalDateTime localDateTime) {
      this.name = str;
      this.data = str2;
      this.createdAt = localDateTime;
      this.selected = false;
      this.shared = false;
      this.remoteId = 0;
      this.owner = null;
   }

   public ConfigEntry(String str, String str2) {
      this(str, str2, LocalDateTime.now());
   }

   public ConfigEntry(int i, String str, String str2, String str3, LocalDateTime localDateTime) {
      this.name = str;
      this.data = str2;
      this.createdAt = localDateTime;
      this.selected = false;
      this.shared = true;
      this.remoteId = i;
      this.owner = str3;
   }

   public String a() {
      return this.name;
   }

   public void a(String str) {
      this.name = str;
   }

   public String b() {
      return this.owner;
   }

   public String getData() {
      return this.data;
   }

   public LocalDateTime c() {
      return this.createdAt;
   }

   public String d() {
      return this.createdAt.format(DATE_FORMAT);
   }

   public boolean e() {
      return this.selected;
   }

   public void a(boolean z) {
      this.selected = z;
   }

   public boolean f() {
      return this.shared;
   }

   public int g() {
      return this.remoteId;
   }

   public String h() {
      return this.owner;
   }
}
