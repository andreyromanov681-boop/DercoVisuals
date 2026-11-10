package pulse.auth;

import java.util.ArrayList;
import java.util.List;

public class TokenCodec {
   public static String a() {
      return "";
   }

   public static String b() {
      return "";
   }

   public static String c() {
      return "";
   }

   public static TokenCodec.TokenInfo a(String str) {
      return null;
   }

   public static TokenCodec.TokenInfo d() {
      return null;
   }

   public static String e() {
      return "";
   }

   public static String b(String str) {
      return "";
   }

   public static String a(TokenCodec.TokenInfo tokenInfo) {
      return "";
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class TokenInfo {
      private String a;
      private String b;
      private String c;
      private List<String> d;
      private List<String> e;

      public TokenInfo(String str, String str2, String str3, List<String> list, List<String> list2) {
         this.a = str;
         this.b = str2;
         this.c = str3;
         this.d = new ArrayList<>(list);
         this.e = new ArrayList<>(list2);
      }

      public String a() {
         return this.a;
      }

      public String b() {
         return this.b;
      }

      public String c() {
         return this.c;
      }

      public List<String> d() {
         return new ArrayList<>(this.d);
      }

      public List<String> e() {
         return new ArrayList<>(this.e);
      }

      public String f() {
         return String.join(",", this.d);
      }

      public String g() {
         return String.join(",", this.e);
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
