package pulse.entity;

import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class EntityUtils {
   public static int a;
   public static boolean b;

   public static Vec3d a(Entity EntityVar, float f) {
      Vec3d Vec3dVarA = a(EntityVar);
      return Vec3dVarA.add(EntityVar.getEntityPos().subtract(Vec3dVarA).multiply(f));
   }

   public static Vec3d a(Entity EntityVar) {
      return new Vec3d(EntityVar.lastRenderX, EntityVar.lastRenderY, EntityVar.lastRenderZ);
   }

   public static boolean a(LivingEntity LivingEntityVar) {
      if (!(LivingEntityVar instanceof PlayerEntity PlayerEntityVar)) {
         return false;
      } else {
         if (!PlayerEntityVar.isInvisible()) {
            return false;
         }

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if ((slot == EquipmentSlot.FEET || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.HEAD)
               && !PlayerEntityVar.getEquippedStack(slot).isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }

   @Generated
   private EntityUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
