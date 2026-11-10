package pulse.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import pulse.auth.UserInfo;
import ru.pulse.Pulse;

public class ConfigProfile {
   private static final DateTimeFormatter c = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
   private static final String d = "pulse_config_v1";
   private final String e;
   private final int f;
   private final String g;
   private final long h;
   private String i;
   private String j;
   private long k;
   public static int a;
   public static boolean b;

   private ConfigProfile(String str, String str2, int i, String str3, long j, String str4, long j2) {
      this.i = str;
      this.e = str2;
      this.f = i;
      this.g = str3;
      this.h = j;
      this.j = str4;
      this.k = j2;
   }

   public static ConfigProfile a(String str) {
      String strD = "Unknown";
      int iC = 0;
      UserInfo userInfo = Pulse.getUserInfo();
      if (userInfo != null) {
         strD = userInfo.d();
         iC = userInfo.c();
      }

      long jCurrentTimeMillis = System.currentTimeMillis();
      return new ConfigProfile(str, strD, iC, a(iC, jCurrentTimeMillis), jCurrentTimeMillis, "", jCurrentTimeMillis);
   }

   public static ConfigProfile a(String str, String str2) {
      ConfigProfile configProfileA = a(str);
      configProfileA.j = str2;
      return configProfileA;
   }

   public static ConfigProfile a(String str, String str2, int i, String str3, long j, String str4, long j2) {
      return new ConfigProfile(str, str2, i, str3, j, str4, j2);
   }

   private static String a(int i, long j) {
      return null;
   }

   public boolean a() {
      return a(this.f, this.h).equals(this.g);
   }

   public void b() {
      this.k = System.currentTimeMillis();
   }

   public String c() {
      return this.e;
   }

   public int d() {
      return this.f;
   }

   public String e() {
      return this.g;
   }

   public long f() {
      return this.h;
   }

   public LocalDateTime g() {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.h), ZoneId.systemDefault());
   }

   public String h() {
      return this.g().format(c);
   }

   public String i() {
      return this.i;
   }

   public void b(String str) {
      this.i = str;
      this.b();
   }

   public String j() {
      return this.j;
   }

   public void c(String str) {
      this.j = str;
      this.b();
   }

   public long k() {
      return this.k;
   }

   public LocalDateTime l() {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.k), ZoneId.systemDefault());
   }

   public String m() {
      return this.l().format(c);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
