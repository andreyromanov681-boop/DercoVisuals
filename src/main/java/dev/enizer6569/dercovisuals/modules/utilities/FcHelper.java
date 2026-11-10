// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.math.MatrixStack;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.entity.EntityFinder;
import dev.enizer6569.dercovisuals.entity.EntityTargetType;
import dev.enizer6569.dercovisuals.entity.EntityUtils;
import dev.enizer6569.dercovisuals.events.WorldRenderEvent;
import dev.enizer6569.dercovisuals.gui.friends.FriendLookup;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.render.world.WorldRenderUtils;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;

@ModuleInfo(a = "FC Helper", b = "Предпросмотр зоны действия донат-предметов", c = ModuleCategory.UTILITIES)
public class FcHelper extends ClientModule {
   private final BooleanSetting e = new BooleanSetting("Шаровая молния", true);
   private final BooleanSetting f = new BooleanSetting("Ком слизи", true);
   private final BooleanSetting g = new BooleanSetting("Черепаший захват", true);
   private final BooleanSetting h = new BooleanSetting("Паутина судьбы", true);
   private final BooleanSetting i = new BooleanSetting("Стан", true);
   private final BooleanSetting j = new BooleanSetting("Магнитный шар", true);
   public static int a;
   public static boolean b;

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      if (c.world != null && c.player != null) {
         MatrixStack MatrixStackVarA = worldRenderEvent.a();
         float fB = worldRenderEvent.b();
         Item ItemVarGetItem = c.player.getMainHandStack().getItem();
         Item ItemVarMethod_79092 = c.player.getOffHandStack().getItem();
         if (this.e.k() && (ItemVarGetItem == Items.NETHER_STAR || ItemVarMethod_79092 == Items.NETHER_STAR)) {
            this.a(MatrixStackVarA, fB);
         }

         if (this.f.k() && (ItemVarGetItem == Items.SLIME_BALL || ItemVarMethod_79092 == Items.SLIME_BALL)) {
            this.b(MatrixStackVarA, fB);
         }

         if (this.g.k() && (ItemVarGetItem == Items.TURTLE_SCUTE || ItemVarMethod_79092 == Items.TURTLE_SCUTE)) {
            this.a(MatrixStackVarA);
         }

         if (this.h.k() && (ItemVarGetItem == Items.COBWEB || ItemVarMethod_79092 == Items.COBWEB)) {
            this.b(MatrixStackVarA);
         }

         if (this.i.k() && (ItemVarGetItem == Items.ENDER_EYE || ItemVarMethod_79092 == Items.ENDER_EYE)) {
            this.c(MatrixStackVarA, fB);
         }

         if (this.j.k() && (ItemVarGetItem == Items.FIREWORK_STAR || ItemVarMethod_79092 == Items.FIREWORK_STAR)) {
            this.d(MatrixStackVarA, fB);
         }
      }
   }

   private void a(MatrixStack MatrixStackVar, float f) {
      Vec3d Vec3dVarA = this.a(f);
      Box BoxVar = new Box(
         Vec3dVarA.x - 10.0,
         Vec3dVarA.y - 10.0,
         Vec3dVarA.z - 10.0,
         Vec3dVarA.x + 10.0,
         Vec3dVarA.y + 10.0,
         Vec3dVarA.z + 10.0
      );
      WorldRenderUtils.a(MatrixStackVar, BoxVar, !this.a(BoxVar) ? -1 : -16711936);
   }

   private void b(MatrixStack MatrixStackVar, float f) {
      Vec3d Vec3dVarA = this.a(f);
      double d = Vec3dVarA.y + 1.0;
      WorldRenderUtils.a(
         MatrixStackVar,
         new Box(Vec3dVarA.x - 5.0, d, Vec3dVarA.z - 5.0, Vec3dVarA.x + 5.0, d, Vec3dVarA.z + 5.0),
         !this.b(
               new Box(Vec3dVarA.x - 5.0, d - 5.0, Vec3dVarA.z - 5.0, Vec3dVarA.x + 5.0, d + 5.0, Vec3dVarA.z + 5.0)
            )
            ? -1
            : -16711936
      );
   }

   private void a(MatrixStack MatrixStackVar) {
      BlockPos BlockPosVarGetBlockPos = c.player.getBlockPos();
      double dGetX = BlockPosVarGetBlockPos.getX() - 3;
      double dGetY = BlockPosVarGetBlockPos.getY() - 1;
      int iGetZ = BlockPosVarGetBlockPos.getZ();
      double d = 2 * (iGetZ & -4) - (iGetZ ^ 3);
      int iGetX = BlockPosVarGetBlockPos.getX();
      double d2 = 2 * (iGetX | 4) - (iGetX ^ 4);
      int iGetY = BlockPosVarGetBlockPos.getY();
      Box BoxVar = new Box(dGetX, dGetY, d, d2, (iGetY & -6) + (5 & ~iGetY) + 2 * (iGetY & 5), BlockPosVarGetBlockPos.getZ() - -5 - 1);
      WorldRenderUtils.a(MatrixStackVar, BoxVar, !this.b(BoxVar) ? -1 : -16711936);
   }

   private void b(MatrixStack MatrixStackVar) {
      BlockPos BlockPosVarGetBlockPos = c.player.getBlockPos();
      float fGetYaw = (c.player.getYaw() % 360.0F + 360.0F) % 360.0F;
      BlockPos BlockPosVarAdd = fGetYaw >= 315.0F || fGetYaw < 45.0F
         ? BlockPosVarGetBlockPos.add(0, 0, 3)
         : (
            !(fGetYaw < 45.0F) && !(fGetYaw >= 135.0F)
               ? BlockPosVarGetBlockPos.add(-3, 0, 0)
               : (!(fGetYaw < 135.0F) && !(fGetYaw >= 225.0F) ? BlockPosVarGetBlockPos.add(0, 0, -3) : BlockPosVarGetBlockPos.add(3, 0, 0))
         );
      double dGetY = BlockPosVarAdd.getY();
      int iGetY = BlockPosVarAdd.getY();
      double d3 = (iGetY | 3) + (iGetY & 3);
      double dGetX;
      double d;
      double dGetZ;
      double d2;
      if (!(fGetYaw >= 315.0F) && !(fGetYaw < 45.0F) && (!(fGetYaw >= 135.0F) || !(fGetYaw < 225.0F))) {
         dGetX = BlockPosVarAdd.getX();
         int iMethod_102633 = BlockPosVarAdd.getX();
         d = (iMethod_102633 & -2) + (1 & ~iMethod_102633) + 2 * (iMethod_102633 & 1);
         dGetZ = BlockPosVarAdd.getZ() - 1;
         int iMethod_102602 = BlockPosVarAdd.getZ();
         d2 = (iMethod_102602 ^ 2) + 2 * (iMethod_102602 & 2);
      } else {
         int iGetX = BlockPosVarAdd.getX();
         dGetX = (iGetX & -2) - (~iGetX & 1);
         int iMethod_102632 = BlockPosVarAdd.getX();
         d = (iMethod_102632 ^ 2) + 2 * (iMethod_102632 & 2);
         dGetZ = BlockPosVarAdd.getZ();
         int iGetZ = BlockPosVarAdd.getZ();
         d2 = (iGetZ & -2) + (1 & ~iGetZ) + 2 * (iGetZ & 1);
      }

      Box BoxVar = new Box(dGetX, dGetY, dGetZ, d, d3, d2);
      WorldRenderUtils.a(MatrixStackVar, BoxVar, !this.b(BoxVar) ? -1 : -16711936);
   }

   private void c(MatrixStack MatrixStackVar, float f) {
      Vec3d Vec3dVarA = this.a(f);
      Box BoxVar = new Box(
         Vec3dVarA.x - 30.0,
         Vec3dVarA.y - 30.0,
         Vec3dVarA.z - 30.0,
         Vec3dVarA.x + 30.0,
         Vec3dVarA.y + 30.0,
         Vec3dVarA.z + 30.0
      );
      int i;
      if (this.b(BoxVar)) {
         i = -16711936;
      } else {
         i = -1;
      }

      WorldRenderUtils.a(MatrixStackVar, BoxVar, i);
   }

   private void d(MatrixStack MatrixStackVar, float f) {
      Vec3d Vec3dVarA = this.a(f);
      WorldRenderUtils.a(
         MatrixStackVar,
         new Box(
            Vec3dVarA.x - 7.0,
            Vec3dVarA.y + 1.0,
            Vec3dVarA.z - 7.0,
            Vec3dVarA.x + 7.0,
            Vec3dVarA.y + 1.0,
            Vec3dVarA.z + 7.0
         ),
         !this.b(
               new Box(
                  Vec3dVarA.x - 7.0,
                  Vec3dVarA.y - 6.0,
                  Vec3dVarA.z - 7.0,
                  Vec3dVarA.x + 7.0,
                  Vec3dVarA.y + 8.0,
                  Vec3dVarA.z + 7.0
               )
            )
            ? -1
            : -16711936
      );
   }

   private Vec3d a(float f) {
      return new Vec3d(
         c.player.lastRenderX + (c.player.getX() - c.player.lastRenderX) * f,
         c.player.lastRenderY + (c.player.getY() - c.player.lastRenderY) * f,
         c.player.lastRenderZ + (c.player.getZ() - c.player.lastRenderZ) * f
      );
   }

   private boolean a(Box BoxVar) {
      for (PlayerEntity PlayerEntityVar : EntityFinder.a(
         EntityVar -> Bool.from(EntityVar instanceof PlayerEntity && BoxVar.contains(EntityVar.getEntityPos()) ? 1 : 0), EntityTargetType.PLAYER
      )) {
         if (PlayerEntityVar != c.player) {
            PlayerEntity PlayerEntityVar2 = PlayerEntityVar;
            if (!EntityUtils.a(PlayerEntityVar2)
               && !FriendLookup.a(PlayerEntityVar2.getName().getString())
               && PlayerEntityVar2.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean b(Box BoxVar) {
      for (PlayerEntity PlayerEntityVar : EntityFinder.a(
         EntityVar -> Bool.from(EntityVar instanceof PlayerEntity && BoxVar.contains(EntityVar.getEntityPos()) ? 1 : 0), EntityTargetType.PLAYER
      )) {
         if (PlayerEntityVar != c.player) {
            PlayerEntity PlayerEntityVar2 = PlayerEntityVar;
            if (!EntityUtils.a(PlayerEntityVar2) && !FriendLookup.a(PlayerEntityVar2.getName().getString())) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean a(List<BlockPos> list) {
      for (PlayerEntity PlayerEntityVar : EntityFinder.a(EntityVar -> EntityVar instanceof PlayerEntity, EntityTargetType.PLAYER)) {
         if (PlayerEntityVar != c.player && !FriendLookup.a(PlayerEntityVar.getName().getString())) {
            BlockPos BlockPosVarGetBlockPos = PlayerEntityVar.getBlockPos();
            Iterator<BlockPos> it2 = list.iterator();

            while (it2.hasNext()) {
               if (BlockPosVarGetBlockPos.equals(it2.next())) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
