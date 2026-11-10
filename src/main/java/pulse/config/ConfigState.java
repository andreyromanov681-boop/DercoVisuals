package pulse.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigState {
   public static final int a = 1;
   private int d = 1;
   private ConfigProfile e;
   private Map<String, ConfigState.ModuleState> f = new HashMap<>();
   private List<ConfigState.FriendHistoryState> g = new ArrayList<>();
   private List<ConfigState.NotificationState> h = new ArrayList<>();
   private ConfigState.MarkerOptionsState i = new ConfigState.MarkerOptionsState();
   private Map<String, ConfigState.HudElementState> j = new HashMap<>();
   private String k = "BOTTOM";
   private ConfigState.NotificationSettingsState l = new ConfigState.NotificationSettingsState();
   private ConfigState.ClientColorState m = new ConfigState.ClientColorState();
   public static int b;
   public static boolean c;

   public ConfigState() {
   }

   public ConfigState(ConfigProfile configProfile) {
      this.e = configProfile;
   }

   public int a() {
      return this.d;
   }

   public void a(int i) {
      this.d = i;
   }

   public ConfigProfile b() {
      return this.e;
   }

   public void a(ConfigProfile configProfile) {
      this.e = configProfile;
   }

   public Map<String, ConfigState.ModuleState> c() {
      return this.f;
   }

   public void a(Map<String, ConfigState.ModuleState> map) {
      this.f = map;
   }

   public List<ConfigState.FriendHistoryState> d() {
      return this.g;
   }

   public void a(List<ConfigState.FriendHistoryState> list) {
      this.g = list;
   }

   public List<ConfigState.NotificationState> e() {
      return this.h;
   }

   public void b(List<ConfigState.NotificationState> list) {
      this.h = list;
   }

   public ConfigState.MarkerOptionsState f() {
      return this.i;
   }

   public void a(ConfigState.MarkerOptionsState markerOptionsState) {
      this.i = markerOptionsState;
   }

   public Map<String, ConfigState.HudElementState> g() {
      return this.j;
   }

   public void b(Map<String, ConfigState.HudElementState> map) {
      this.j = map;
   }

   public String h() {
      return this.k;
   }

   public void a(String str) {
      this.k = str;
   }

   public ConfigState.NotificationSettingsState i() {
      return this.l;
   }

   public void a(ConfigState.NotificationSettingsState notificationSettingsState) {
      this.l = notificationSettingsState;
   }

   public ConfigState.ClientColorState j() {
      return this.m;
   }

   public void a(ConfigState.ClientColorState clientColorState) {
      this.m = clientColorState;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class ClientColorState {
      private String c = "Статичный";
      private float d = 0.75F;
      private float e = 0.81F;
      private float f = 0.89F;
      private float g = 0.0F;
      private float h = 0.0F;
      private float i = 1.0F;
      private float j = 0.58F;
      private float k = 0.51F;
      private float l = 0.96F;
      public static int a;
      public static boolean b;

      public String a() {
         return this.c;
      }

      public void a(String str) {
         this.c = str;
      }

      public float b() {
         return this.d;
      }

      public void a(float f) {
         this.d = f;
      }

      public float c() {
         return this.e;
      }

      public void b(float f) {
         this.e = f;
      }

      public float d() {
         return this.f;
      }

      public void c(float f) {
         this.f = f;
      }

      public float e() {
         return this.g;
      }

      public void d(float f) {
         this.g = f;
      }

      public float f() {
         return this.h;
      }

      public void e(float f) {
         this.h = f;
      }

      public float g() {
         return this.i;
      }

      public void f(float f) {
         this.i = f;
      }

      public float h() {
         return this.j;
      }

      public void g(float f) {
         this.j = f;
      }

      public float i() {
         return this.k;
      }

      public void h(float f) {
         this.k = f;
      }

      public float j() {
         return this.l;
      }

      public void i(float f) {
         this.l = f;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class FriendHistoryState {
      private String c;
      private String d;
      public static int a;
      public static boolean b;

      public FriendHistoryState() {
      }

      public FriendHistoryState(String str, String str2) {
         this.c = str;
         this.d = str2;
      }

      public String a() {
         return this.c;
      }

      public void a(String str) {
         this.c = str;
      }

      public String b() {
         return this.d;
      }

      public void b(String str) {
         this.d = str;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class HudElementState {
      private float c;
      private float d;
      private float e = 1.0F;
      public static int a;
      public static boolean b;

      public HudElementState() {
      }

      public HudElementState(float f, float f2, float f3) {
         this.c = f;
         this.d = f2;
         this.e = f3;
      }

      public float a() {
         return this.c;
      }

      public void a(float f) {
         this.c = f;
      }

      public float b() {
         return this.d;
      }

      public void b(float f) {
         this.d = f;
      }

      public float c() {
         return this.e;
      }

      public void c(float f) {
         this.e = f;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class MarkerOptionsState {
      private boolean c = true;
      private int d = 0;
      private boolean e = false;
      private boolean f = true;
      private boolean g = false;
      public static int a;
      public static boolean b;

      public boolean a() {
         return this.c;
      }

      public void a(boolean z) {
         this.c = z;
      }

      public int b() {
         return this.d;
      }

      public void a(int i) {
         this.d = i;
      }

      public boolean c() {
         return this.e;
      }

      public void b(boolean z) {
         this.e = z;
      }

      public boolean d() {
         return this.f;
      }

      public void c(boolean z) {
         this.f = z;
      }

      public boolean e() {
         return this.g;
      }

      public void d(boolean z) {
         this.g = z;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class ModuleState {
      private boolean c;
      private int d;
      private Map<String, ConfigState.SettingState> e = new HashMap<>();
      public static int a;
      public static boolean b;

      public ModuleState() {
      }

      public ModuleState(boolean z, int i) {
         this.c = z;
         this.d = i;
      }

      public boolean a() {
         return this.c;
      }

      public void a(boolean z) {
         this.c = z;
      }

      public int b() {
         return this.d;
      }

      public void a(int i) {
         this.d = i;
      }

      public Map<String, ConfigState.SettingState> c() {
         return this.e;
      }

      public void a(Map<String, ConfigState.SettingState> map) {
         this.e = map;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class NotificationSettingsState {
      private boolean c = true;
      private boolean d = true;
      private boolean e = true;
      private boolean f = true;
      private boolean g = true;
      public static int a;
      public static boolean b;

      public boolean a() {
         return this.c;
      }

      public void a(boolean z) {
         this.c = z;
      }

      public boolean b() {
         return this.d;
      }

      public void b(boolean z) {
         this.d = z;
      }

      public boolean c() {
         return this.e;
      }

      public void c(boolean z) {
         this.e = z;
      }

      public boolean d() {
         return this.f;
      }

      public void d(boolean z) {
         this.f = z;
      }

      public boolean e() {
         return this.g;
      }

      public void e(boolean z) {
         this.g = z;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class NotificationState {
      private String c;
      private int d;
      private int e;
      private int f;
      private int g;
      private String h;
      private boolean i;
      private long j;
      private int k;
      private long l;
      public static int a;
      public static boolean b;

      public String a() {
         return this.c;
      }

      public void a(String str) {
         this.c = str;
      }

      public int b() {
         return this.d;
      }

      public void a(int i) {
         this.d = i;
      }

      public int c() {
         return this.e;
      }

      public void b(int i) {
         this.e = i;
      }

      public int d() {
         return this.f;
      }

      public void c(int i) {
         this.f = i;
      }

      public int e() {
         return this.g;
      }

      public void d(int i) {
         this.g = i;
      }

      public String f() {
         return this.h;
      }

      public void b(String str) {
         this.h = str;
      }

      public boolean g() {
         return this.i;
      }

      public void a(boolean z) {
         this.i = z;
      }

      public long h() {
         return this.j;
      }

      public void a(long j) {
         this.j = j;
      }

      public int i() {
         return this.k;
      }

      public void e(int i) {
         this.k = i;
      }

      public long j() {
         return this.l;
      }

      public void b(long j) {
         this.l = j;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class SettingState {
      private String c;
      private Object d;
      private Float e;
      private Float f;
      private Float g;
      private int[] h;
      private Boolean i;
      private Integer j;
      public static int a;
      public static boolean b;

      public SettingState() {
      }

      public SettingState(String str, Object obj) {
         this.c = str;
         this.d = obj;
      }

      public String a() {
         return this.c;
      }

      public void a(String str) {
         this.c = str;
      }

      public Object b() {
         return this.d;
      }

      public void a(Object obj) {
         this.d = obj;
      }

      public Float c() {
         return this.e;
      }

      public void a(Float f) {
         this.e = f;
      }

      public Float d() {
         return this.f;
      }

      public void b(Float f) {
         this.f = f;
      }

      public Float e() {
         return this.g;
      }

      public void c(Float f) {
         this.g = f;
      }

      public int[] f() {
         return this.h;
      }

      public void a(int[] iArr) {
         this.h = iArr;
      }

      public Boolean g() {
         return this.i;
      }

      public void a(Boolean bool) {
         this.i = bool;
      }

      public Integer h() {
         return this.j;
      }

      public void a(Integer num) {
         this.j = num;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
