package pulse.auth;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import pulse.core.Bool;

public class UserInfo {
   private final int c;
   private final String d;
   private final String e;
   private String f = "user";
   private final Map<String, Integer> g = new HashMap<>();
   public static int a;
   public static boolean b;

   public UserInfo(int i, String str, String str2) {
      this.c = i;
      this.d = str;
      this.e = str2;
   }

   public UserInfo(int i, String str, String str2, String str3) {
      this.c = i;
      this.d = str;
      this.e = str2;
      this.f = str3 != null ? str3 : "user";
   }

   public boolean a() {
      return "beta_tester".equals(this.f) || "admin".equals(this.f);
   }

   public void a(String str) {
      this.f = str != null ? str : "user";
   }

   public Integer b() {
      return this.g.get("cape");
   }

   public Integer b(String str) {
      return this.g.get(str);
   }

   public void a(Map<String, Integer> map) {
      this.g.clear();
      if (map != null) {
         this.g.putAll(map);
      }
   }

   @Override
   public String toString() {
      return "UserInfo{userId=" + this.c + ", username='" + this.d + "', sessionId='" + this.e + "', role='" + this.f + "', cosmetics=" + this.g + "}";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? Bool.from(this.c != ((UserInfo)obj).c ? 0 : 1) : false;
      }
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(this.c);
   }

   @Generated
   public int c() {
      return this.c;
   }

   @Generated
   public String d() {
      return this.d;
   }

   @Generated
   public String e() {
      return this.e;
   }

   @Generated
   public String f() {
      return this.f;
   }

   @Generated
   public Map<String, Integer> g() {
      return this.g;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
