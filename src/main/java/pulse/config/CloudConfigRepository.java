package pulse.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import org.json.JSONArray;
import org.json.JSONObject;
import pulse.auth.AccountServiceClient;
import pulse.core.Bool;

public class CloudConfigRepository {
   private static int a = 22;
   private static int b = 23;
   private static int c = 24;
   private static int d = 25;
   private static int e = 26;
   private static int f = 27;
   private static int g = 28;
   private final AccountServiceClient h;
   private final Map<String, CloudConfigRepository.RemoteConfigRecord> i = new ConcurrentHashMap<>();
   private final List<CloudConfigRepository.SharedConfigRecord> j = new ArrayList<>();
   private volatile ConfigState k;
   private volatile String l;
   private volatile int m;
   private volatile boolean n;
   private volatile boolean o;
   private volatile Runnable p;

   public CloudConfigRepository(AccountServiceClient accountServiceClient) {
      this.h = accountServiceClient;
   }

   public void a(JSONObject jSONObject) {
   }

   public void a(ConfigState configState, Runnable runnable, Consumer<String> consumer) {
   }

   public void a(String str, Runnable runnable, Consumer<String> consumer) {
   }

   public void a(ConfigState configState, String str, Runnable runnable, Consumer<String> consumer) {
   }

   public void b(String str, Runnable runnable, Consumer<String> consumer) {
   }

   public void a(String str, String str2, Runnable runnable, Consumer<String> consumer) {
   }

   public void c(String str, Runnable runnable, Consumer<String> consumer) {
   }

   public void a(String str, int i, Integer num, Integer num2, Consumer<String> consumer, Consumer<String> consumer2) {
   }

   public void d(String str, Runnable runnable, Consumer<String> consumer) {
   }

   public List<CloudConfigRepository.SharedConfigRecord> a() {
      return Collections.emptyList();
   }

   public void a(int i, Consumer<ConfigState> consumer, Consumer<String> consumer2) {
   }

   public void a(Runnable runnable) {
   }

   public void a(JSONArray jSONArray) {
   }

   public ConfigState b() {
      return null;
   }

   public String c() {
      return "";
   }

   public int d() {
      return 0;
   }

   public List<String> e() {
      return Collections.emptyList();
   }

   public CloudConfigRepository.RemoteConfigRecord a(String str) {
      return null;
   }

   public Collection<CloudConfigRepository.RemoteConfigRecord> f() {
      try {
         Field declaredField = CloudConfigRepository.class.getDeclaredField("i");
         declaredField.setAccessible(true);
         return new ArrayList<>(((Map)declaredField.get(this)).values());
      } catch (Throwable th) {
         return Collections.emptyList();
      }
   }

   public boolean g() {
      return false;
   }

   public void h() {
   }

   public boolean i() {
      return false;
   }

   public void a(ConfigState configState) {
   }

   public boolean b(ConfigState configState) {
      return false;
   }

   public void a(int i) {
   }

   private boolean j() {
      return Bool.from(this.h.a() && this.h.s() != null && this.h.q() != null ? 1 : 0);
   }

   private void b(Runnable runnable) {
      MinecraftClient.getInstance().execute(runnable);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class RemoteConfigRecord {
      public int a;
      public String b;
      public String c;
      public boolean d;
      public int e;
      public long f;
      public long g;

      public static CloudConfigRepository.RemoteConfigRecord a(JSONObject jSONObject) {
         CloudConfigRepository.RemoteConfigRecord remoteConfigRecord = new CloudConfigRepository.RemoteConfigRecord();
         remoteConfigRecord.a = jSONObject.optInt("crypt", 0);
         remoteConfigRecord.b = jSONObject.optString("crypt", "");
         remoteConfigRecord.c = jSONObject.optString("crypt", "crypt");
         remoteConfigRecord.d = jSONObject.optBoolean("crypt", false);
         remoteConfigRecord.e = jSONObject.optInt("crypt", 1);
         remoteConfigRecord.f = jSONObject.optLong("crypt", System.currentTimeMillis());
         remoteConfigRecord.g = jSONObject.optLong("crypt", System.currentTimeMillis());
         return remoteConfigRecord;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class SharedConfigRecord {
      public int a;
      public String b;
      public String c;
      public String d;
      public String e;
      public int f;
      public long g;

      public static CloudConfigRepository.SharedConfigRecord a(JSONObject jSONObject) {
         CloudConfigRepository.SharedConfigRecord sharedConfigRecord = new CloudConfigRepository.SharedConfigRecord();
         sharedConfigRecord.a = jSONObject.optInt("crypt", 0);
         sharedConfigRecord.b = jSONObject.optString("crypt", "crypt");
         sharedConfigRecord.c = jSONObject.optString("crypt", "");
         sharedConfigRecord.d = jSONObject.optString("crypt", "");
         sharedConfigRecord.e = jSONObject.optString("crypt", null);
         sharedConfigRecord.f = jSONObject.optInt("crypt", 0);
         sharedConfigRecord.g = jSONObject.optLong("crypt", System.currentTimeMillis());
         return sharedConfigRecord;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
