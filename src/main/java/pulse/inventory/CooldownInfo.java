package pulse.inventory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import pulse.core.Bool;
import ru.pulse.mixin.accessor.ItemCooldownEntryAccessor;
import ru.pulse.mixin.accessor.ItemCooldownManagerAccessor;

public class CooldownInfo {
   private static Field d;
   private static Field e;
   private static Field f;
   private static Field g;
   public static int b;
   public static boolean c;
   private static final Item[] WATCHED_ITEMS = new Item[]{
      Items.CHORUS_FRUIT,
      Items.POPPED_CHORUS_FRUIT,
      Items.ENDER_PEARL,
      Items.SHIELD,
      Items.CROSSBOW,
      Items.GOLDEN_APPLE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.TOTEM_OF_UNDYING,
      Items.TRIDENT,
      Items.BOW,
      Items.WIND_CHARGE
   };
   public static final CooldownInfo.ItemCooldownSnapshot a = new CooldownInfo.ItemCooldownSnapshot(0, 0, 0);

   private static Field a(Class<?> cls, String... strArr) {
      for (String str : strArr) {
         try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField;
         } catch (NoSuchFieldException var7) {
         }
      }

      return null;
   }

   private static Class<?> a() {
      try {
         return Class.forName("net.minecraft.entity.player.ItemCooldownManager$Entry");
      } catch (ClassNotFoundException e2) {
         try {
            return Class.forName("net.minecraft.ItemCooldownManager$class_1797");
         } catch (ClassNotFoundException e3) {
            return null;
         }
      }
   }

   public static CooldownInfo.ItemCooldownSnapshot a(ItemCooldownManager ItemCooldownManagerVar, Item ItemVar) {
      return a(ItemCooldownManagerVar, ItemCooldownManagerVar.getGroup(ItemVar.getDefaultStack()));
   }

   public static CooldownInfo.ItemCooldownSnapshot a(ItemCooldownManager ItemCooldownManagerVar, Identifier IdentifierVar) {
      try {
         ItemCooldownManagerAccessor itemCooldownManagerAccessor = (ItemCooldownManagerAccessor)ItemCooldownManagerVar;
         Object obj = itemCooldownManagerAccessor.pulse$getEntries().get(IdentifierVar);
         if (obj == null) {
            return a;
         }

         ItemCooldownEntryAccessor itemCooldownEntryAccessor = (ItemCooldownEntryAccessor)obj;
         return new CooldownInfo.ItemCooldownSnapshot(
            itemCooldownManagerAccessor.pulse$getTick(), itemCooldownEntryAccessor.pulse$getStartTick(), itemCooldownEntryAccessor.pulse$getEndTick()
         );
      } catch (Throwable th) {
         return a(ItemCooldownManagerVar, IdentifierVar, false);
      }
   }

   private static CooldownInfo.ItemCooldownSnapshot a(ItemCooldownManager ItemCooldownManagerVar, Identifier IdentifierVar, boolean z) {
      if (d != null && e != null && f != null && g != null) {
         try {
            int i = d.getInt(ItemCooldownManagerVar);
            Object obj = ((Map)e.get(ItemCooldownManagerVar)).get(IdentifierVar);
            return obj == null ? a : new CooldownInfo.ItemCooldownSnapshot(i, f.getInt(obj), g.getInt(obj));
         } catch (Exception e2) {
            return a;
         }
      } else {
         return a;
      }
   }

   public static List<CooldownInfo.ActiveGroupCooldown> collectActiveGroups(ItemCooldownManager ItemCooldownManagerVar) {
      ArrayList arrayList = new ArrayList();

      try {
         ItemCooldownManagerAccessor itemCooldownManagerAccessor = (ItemCooldownManagerAccessor)ItemCooldownManagerVar;
         int iPulse$getTick = itemCooldownManagerAccessor.pulse$getTick();

         for (Entry<Identifier, ?> entry : itemCooldownManagerAccessor.pulse$getEntries().entrySet()) {
            int iPulse$getEndTick = ((ItemCooldownEntryAccessor)entry.getValue()).pulse$getEndTick();
            if (iPulse$getEndTick > iPulse$getTick) {
               arrayList.add(new CooldownInfo.ActiveGroupCooldown(entry.getKey(), iPulse$getEndTick - iPulse$getTick));
            }
         }
      } catch (Throwable th) {
         arrayList.addAll(collectActiveGroupsReflective(ItemCooldownManagerVar));
      }

      return arrayList;
   }

   private static List<CooldownInfo.ActiveGroupCooldown> collectActiveGroupsReflective(ItemCooldownManager ItemCooldownManagerVar) {
      ArrayList arrayList = new ArrayList();
      if (d != null && e != null && f != null && g != null) {
         try {
            int i = d.getInt(ItemCooldownManagerVar);
            Map<?, ?> map = (Map<?, ?>)e.get(ItemCooldownManagerVar);
            if (map != null) {
               for (Entry<?, ?> entry : map.entrySet()) {
                  if (entry.getKey() instanceof Identifier IdentifierVar) {
                     int i2 = g.getInt(entry.getValue());
                     if (i2 > i) {
                        arrayList.add(new CooldownInfo.ActiveGroupCooldown(IdentifierVar, i2 - i));
                     }
                  }
               }
            }
         } catch (Exception var9) {
         }
      }

      return arrayList;
   }

   public static ItemStack stackForGroup(ItemCooldownManager ItemCooldownManagerVar, Identifier IdentifierVar) {
      Item ItemVar = (Item)Registries.ITEM.get(IdentifierVar);
      if (ItemVar != Items.AIR) {
         return ItemVar.getDefaultStack();
      }

      for (Item ItemVar2 : WATCHED_ITEMS) {
         ItemStack ItemStackVarGetDefaultStack = ItemVar2.getDefaultStack();
         if (IdentifierVar.equals(ItemCooldownManagerVar.getGroup(ItemStackVarGetDefaultStack))) {
            return ItemStackVarGetDefaultStack;
         }
      }

      return new ItemStack(Items.PAPER);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   static {
      try {
         d = a(ItemCooldownManager.class, "tick", "tick");
         if (d == null) {
            for (Field field : ItemCooldownManager.class.getDeclaredFields()) {
               if (field.getType() == int.class) {
                  field.setAccessible(true);
                  d = field;
                  break;
               }
            }
         }

         e = a(ItemCooldownManager.class, "entries", "cooldowns", "entries");
         if (e == null) {
            for (Field field2 : ItemCooldownManager.class.getDeclaredFields()) {
               if (Map.class.isAssignableFrom(field2.getType())) {
                  field2.setAccessible(true);
                  e = field2;
                  break;
               }
            }
         }

         Class<?> clsA = null;

         for (Class<?> cls : ItemCooldownManager.class.getDeclaredClasses()) {
            if (cls.getSimpleName().equals("Entry") || cls.isRecord()) {
               clsA = cls;
               break;
            }
         }

         if (clsA == null) {
            clsA = a();
         }

         if (clsA != null) {
            f = a(clsA, "startTime", "startTick", "startTick");
            g = a(clsA, "endTime", "endTick", "endTick");
            if (f == null || g == null) {
               Field field3 = null;
               Field field4 = null;

               for (Field field5 : clsA.getDeclaredFields()) {
                  if (field5.getType() == int.class) {
                     field5.setAccessible(true);
                     if (field3 == null) {
                        field3 = field5;
                     } else if (field4 == null) {
                        field4 = field5;
                     }
                  }
               }

               if (field3 != null && field4 != null) {
                  f = field3;
                  g = field4;
               }
            }
         }
      } catch (Exception var10) {
      }
   }

   public static final class ActiveGroupCooldown {
      public final Identifier groupId;
      public final int remainingTicks;

      public ActiveGroupCooldown(Identifier IdentifierVar, int i) {
         this.groupId = IdentifierVar;
         this.remainingTicks = i;
      }
   }

   public static class ItemCooldownSnapshot {
      public final int a;
      public final int b;
      public final int c;
      public static int d;
      public static boolean e;

      public ItemCooldownSnapshot(int i, int i2, int i3) {
         this.a = i;
         this.b = i2;
         this.c = i3;
      }

      public boolean a() {
         return Bool.from(this.c <= this.a ? 0 : 1);
      }

      public float b() {
         float f;
         if (this.a()) {
            f = (this.c + ~this.a + 1) / 20.0F;
         } else {
            f = 0.0F;
         }

         return f;
      }

      public float c() {
         if (this.c <= this.a) {
            return 0.0F;
         }

         int i = this.a;
         int i2 = this.c;
         return (2 * (i2 & ~i) - (i2 ^ i)) / (this.c + ~this.b + 1);
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
