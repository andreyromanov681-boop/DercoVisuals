// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.util.ArrayList;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "TriggerBot", b = "Auto attack nearby entities", c = ModuleCategory.UTILITIES)
public class TriggerBot extends ClientModule {
   private final SliderSetting attackRange = new SliderSetting("Attack Range", 3.0F, 3.0F, 6.0F, 0.1F);
   private final SliderSetting cps = new SliderSetting("CPS", 12.0F, 1.0F, 20.0F, 1.0F);
   private final BooleanSetting onlyWeapon = new BooleanSetting("Only With Weapon", true);
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting monsters = new BooleanSetting("Monsters", true);
   private final BooleanSetting animals = new BooleanSetting("Animals", false);
   private static LivingEntity target;
   private long lastAttackTime;

   public TriggerBot() {
      target = null;
      this.lastAttackTime = 0L;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      target = null;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      target = null;
   }

   public void a(ClientTickEvent var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null) {
         ClientPlayerEntity var3 = var2.player;
         if (var3 != null && var2.world != null) {
            this.updateTarget();
            if (target != null && this.canAttack()) {
               this.performAttack();
            }
         }
      }
   }

   private void updateTarget() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      ClientPlayerEntity var2 = var1.player;
      double var3 = this.attackRange.get() + 0.1;
      ArrayList var5 = new ArrayList();

      for (Entity var7 : var1.world.getEntitiesByClass(LivingEntity.class, var2.getBoundingBox().expand(var3), var0 -> true)) {
         if (this.isValid((LivingEntity)var7)) {
            var5.add((LivingEntity)var7);
         }
      }

      if (var5.isEmpty()) {
         target = null;
      } else {
         target = (LivingEntity)var5.get(0);
         double var12 = Double.MAX_VALUE;

         for (LivingEntity var9 : var5) {
            double var10 = var2.distanceTo(var9);
            if (var10 < var12) {
               var12 = var10;
               target = var9;
            }
         }
      }
   }

   private boolean isValid(LivingEntity var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      ClientPlayerEntity var3 = var2.player;
      if (var1 != var3 && var1.isAlive() && var1.age >= 3) {
         if (var1 instanceof ClientPlayerEntity) {
            if (!this.players.get()) {
               return false;
            }
         } else if (!(var1 instanceof Monster) && !(var1 instanceof HostileEntity) && !(var1 instanceof SquidEntity)) {
            if (!(var1 instanceof AnimalEntity)) {
               return false;
            }

            if (!this.animals.get()) {
               return false;
            }
         } else if (!this.monsters.get()) {
            return false;
         }

         double var4 = var3.distanceTo(var1);
         return var4 <= this.attackRange.get();
      } else {
         return false;
      }
   }

   private boolean canAttack() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      ClientPlayerEntity var2 = var1.player;
      if (target == null || var2 == null) {
         return false;
      }

      if (var2.isUsingItem()) {
         return false;
      }

      if (this.onlyWeapon.get()) {
         ItemStack var3 = var2.getMainHandStack();
         if (var3.getItem() == Items.MACE) {
            return false;
         }
      }

      if (var2.distanceTo(target) > this.attackRange.get()) {
         return false;
      }

      long var7 = System.currentTimeMillis();
      long var5 = (long)(1000.0 / this.cps.get());
      return var7 - this.lastAttackTime >= var5;
   }

   private void performAttack() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      ClientPlayerEntity var2 = var1.player;
      if (var2 != null && var1.interactionManager != null && target != null) {
         var1.interactionManager.attackEntity(var2, target);
         var2.swingHand(Hand.MAIN_HAND);
         this.lastAttackTime = System.currentTimeMillis();
      }
   }
}
