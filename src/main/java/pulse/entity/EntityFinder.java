package pulse.entity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import pulse.client.MinecraftContext;
import pulse.core.Bool;

public final class EntityFinder implements MinecraftContext {
   public static int a;
   public static boolean b;

   public static CopyOnWriteArrayList<Entity> a(Predicate<Entity> predicate, EntityTargetType... entityTargetTypeArr) {
      CopyOnWriteArrayList<Entity> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
      if (c.world == null) {
         return copyOnWriteArrayList;
      }

      for (Entity EntityVar : c.world.getEntities()) {
         if (predicate.test(EntityVar) && !(EntityVar instanceof ClientPlayerEntity)) {
            if (EntityVar instanceof PlayerEntity
               && Arrays.stream(entityTargetTypeArr).anyMatch(entityTargetType -> Bool.from(entityTargetType != EntityTargetType.PLAYER ? 0 : 1))) {
               copyOnWriteArrayList.add(EntityVar);
            }

            if (EntityVar instanceof VillagerEntity
               && Arrays.stream(entityTargetTypeArr).anyMatch(entityTargetType2 -> Bool.from(entityTargetType2 != EntityTargetType.VILLAGERS ? 0 : 1))) {
               copyOnWriteArrayList.add(EntityVar);
            }

            if (EntityVar instanceof AnimalEntity
               && Arrays.stream(entityTargetTypeArr).anyMatch(entityTargetType3 -> Bool.from(entityTargetType3 != EntityTargetType.ANIMAL ? 0 : 1))) {
               copyOnWriteArrayList.add(EntityVar);
            }

            if (EntityVar instanceof MobEntity
               && Arrays.stream(entityTargetTypeArr).anyMatch(entityTargetType4 -> Bool.from(entityTargetType4 != EntityTargetType.MOBS ? 0 : 1))) {
               copyOnWriteArrayList.add(EntityVar);
            }

            if (EntityVar instanceof ItemEntity
               && Arrays.stream(entityTargetTypeArr).anyMatch(entityTargetType5 -> Bool.from(entityTargetType5 != EntityTargetType.ITEMS ? 0 : 1))) {
               copyOnWriteArrayList.add(EntityVar);
            }

            if (Arrays.stream(entityTargetTypeArr).anyMatch(entityTargetType6 -> {
               int i;
               if (entityTargetType6 != EntityTargetType.ENTITY) {
                  i = 0;
               } else {
                  i = 1;
               }

               return Bool.from(i);
            })) {
               copyOnWriteArrayList.add(EntityVar);
            }
         }
      }

      return copyOnWriteArrayList;
   }

   public static boolean a(Entity EntityVar) {
      if (c.world == null) {
         return false;
      }

      Iterator it = c.world.getEntities().iterator();

      while (it.hasNext()) {
         if ((Entity)it.next() == EntityVar) {
            return true;
         }
      }

      return false;
   }

   @Generated
   private EntityFinder() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
