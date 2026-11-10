package pulse.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Generated;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.entry.RegistryEntry;
import pulse.core.Bool;
import pulse.events.ClientTickEvent;
import pulse.hud.core.HudService;
import pulse.hud.core.HudServiceInfo;

@HudServiceInfo(enabledByDefault = true)
public class PotionEffectService extends HudService {
   private static final MinecraftClient a = MinecraftClient.getInstance();
   private static final long g = 1000L;
   private final List<PotionEffectService.PotionGroup> b = Arrays.asList(
      new PotionEffectService.PotionGroup(
         "Зелье Радиации",
         Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.POISON, 1), new PotionEffectService.EffectRequirement(StatusEffects.SLOWNESS, 2)),
         StatusEffects.POISON,
         true
      ),
      new PotionEffectService.PotionGroup(
         "Снотворное",
         Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.WEAKNESS, 1), new PotionEffectService.EffectRequirement(StatusEffects.WITHER, 2)),
         StatusEffects.WITHER,
         true
      ),
      new PotionEffectService.PotionGroup(
         "Хлопушка", Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.SPEED, 4)), StatusEffects.SPEED, false
      ),
      new PotionEffectService.PotionGroup(
         "Святая вода", Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.REGENERATION, 2)), StatusEffects.REGENERATION, false
      ),
      new PotionEffectService.PotionGroup(
         "Зелье гнева", Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.STRENGTH, 4)), StatusEffects.STRENGTH, false
      ),
      new PotionEffectService.PotionGroup(
         "Зелье Палладина", Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.HEALTH_BOOST, 2)), StatusEffects.HEALTH_BOOST, false
      ),
      new PotionEffectService.PotionGroup(
         "Зелье Ассасина", Arrays.asList(new PotionEffectService.EffectRequirement(StatusEffects.STRENGTH, 3)), StatusEffects.STRENGTH, false
      )
   );
   private List<PotionEffectService.PotionEffectInfo> c = new ArrayList<>();
   private PotionEffectService.PotionEffectInfo d = null;
   private long e = 0L;
   private int f = 0;
   private boolean h = false;
   private boolean i = false;
   private boolean j = false;

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (a.player != null && a.world != null) {
         List<PotionEffectService.PotionEffectInfo> listK = this.k();
         int i = !listK.isEmpty() ? 1 : 0;
         if (i != 0 && !this.j) {
            this.h = true;
         } else if (i == 0 && this.j) {
            this.i = true;
         }

         this.j = Bool.from(i);
         this.c = listK;
         if (this.c.isEmpty()) {
            this.d = null;
         } else if (this.c.size() == 1) {
            this.f = 0;
            this.d = this.c.get(0);
         } else {
            if (this.f >= this.c.size()) {
               this.f = 0;
            }

            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.e >= 1000L) {
               int i2 = this.f;
               this.f = ((i2 | 1) + (i2 & 1)) % this.c.size();
               this.e = jCurrentTimeMillis;
            }

            this.d = this.c.get(this.f);
         }
      } else {
         if (this.j) {
            this.i = true;
            this.j = false;
         }

         this.c.clear();
         this.d = null;
      }
   }

   public boolean d() {
      if (!this.h) {
         return false;
      }

      this.h = false;
      return true;
   }

   public boolean e() {
      if (!this.i) {
         return false;
      }

      this.i = false;
      return true;
   }

   public boolean f() {
      return Bool.from(!this.c.isEmpty() ? 1 : 0);
   }

   private List<PotionEffectService.PotionEffectInfo> k() {
      return Collections.emptyList();
   }

   private boolean a(PotionEffectService.PotionGroup potionGroup) {
      return false;
   }

   @Generated
   public List<PotionEffectService.PotionEffectInfo> g() {
      return this.c;
   }

   @Generated
   public PotionEffectService.PotionEffectInfo h() {
      return this.d;
   }

   @Generated
   public boolean i() {
      return this.h;
   }

   @Generated
   public boolean j() {
      return this.i;
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class EffectRequirement {
      final RegistryEntry<StatusEffect> a;
      final int b;

      EffectRequirement(RegistryEntry<StatusEffect> RegistryEntryVar, int i) {
         this.a = RegistryEntryVar;
         this.b = i;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   public static class PotionEffectInfo {
      private final String a;
      private final Item b;
      private final RegistryEntry<StatusEffect> c;
      private final boolean d;
      private final int e;
      private final String f;
      private final String g;
      private final boolean h;

      public PotionEffectInfo(String str, Item ItemVar, int i, boolean z) {
         this.a = str;
         this.b = ItemVar;
         this.c = null;
         this.d = false;
         this.e = i;
         this.h = z;
         this.f = a(i / 20);
         this.g = str + " закончится через: " + this.f;
      }

      public PotionEffectInfo(String str, RegistryEntry<StatusEffect> RegistryEntryVar, int i, boolean z) {
         this.a = str;
         this.b = null;
         this.c = RegistryEntryVar;
         this.d = true;
         this.e = i;
         this.h = z;
         this.f = a(i / 20);
         this.g = str + " закончится через: " + this.f;
      }

      private static String a(int i) {
         return null;
      }

      @Generated
      public String a() {
         return this.a;
      }

      @Generated
      public Item b() {
         return this.b;
      }

      @Generated
      public RegistryEntry<StatusEffect> c() {
         return this.c;
      }

      @Generated
      public boolean d() {
         return this.d;
      }

      @Generated
      public int e() {
         return this.e;
      }

      @Generated
      public String f() {
         return this.f;
      }

      @Generated
      public String g() {
         return this.g;
      }

      @Generated
      public boolean h() {
         return this.h;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class PotionGroup {
      final String a;
      final List<PotionEffectService.EffectRequirement> b;
      final RegistryEntry<StatusEffect> c;
      final boolean d;
      boolean e = false;

      PotionGroup(String str, List<PotionEffectService.EffectRequirement> list, RegistryEntry<StatusEffect> RegistryEntryVar, boolean z) {
         this.a = str;
         this.b = list;
         this.c = RegistryEntryVar;
         this.d = z;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
