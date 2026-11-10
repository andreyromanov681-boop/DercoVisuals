// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.Entity.class_5529;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.MouseButtonEvent;
import dev.enizer6569.dercovisuals.events.WorldChangeEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;
import dev.enizer6569.dercovisuals.settings.StringSetting;

@ModuleInfo(a = "FakePlayer", b = "Спавнит копию игрока на клиенте для тренировки.", c = ModuleCategory.UTILITIES)
public final class FakePlayer extends ClientModule {
   public final StringSetting name = new StringSetting("Name", "FakePlayer");
   public final BooleanSetting lookAtPlayer = new BooleanSetting("Look At Player", true);
   public final BooleanSetting totem = new BooleanSetting("Totem", true);
   public final BooleanSetting copyEquip = new BooleanSetting("Copy Equipment", true);
   public final SliderSetting health = new SliderSetting("Health", 20.0F, 1.0F, 40.0F, 1.0F);
   private OtherClientPlayerEntity fakePlayer;
   private float currentHp;
   private static final byte STATUS_HURT = 2;
   private static final byte STATUS_TOTEM = 35;
   private static final int FAKE_ID = -42069;

   public FakePlayer() {
      this.collectSettings();
   }

   @Override
   public void onEnable() {
      this.spawn();
   }

   @Override
   public void onDisable() {
      this.remove();
   }

   @EventHandler
   public void onTick(ClientTickEvent clientTickEvent) {
      if (c.player != null && c.world != null) {
         if (this.fakePlayer == null) {
            this.spawn();
         } else {
            if (this.fakePlayer.hurtTime > 0) {
               this.fakePlayer.hurtTime--;
            }

            this.fakePlayer.setHealth(this.currentHp);
            if (this.lookAtPlayer.get()) {
               double dGetX = c.player.getX() - this.fakePlayer.getX();
               double dGetZ = c.player.getZ() - this.fakePlayer.getZ();
               double dGetEyeY = c.player.getEyeY() - this.fakePlayer.getEyeY();
               double dSqrt = Math.sqrt(dGetX * dGetX + dGetZ * dGetZ);
               float degrees = (float)Math.toDegrees(Math.atan2(dGetZ, dGetX)) - 90.0F;
               float f = (float)(-Math.toDegrees(Math.atan2(dGetEyeY, dSqrt)));
               this.fakePlayer.setYaw(degrees);
               this.fakePlayer.setPitch(f);
               this.fakePlayer.headYaw = degrees;
               this.fakePlayer.bodyYaw = degrees;
            }
         }
      }
   }

   @EventHandler
   public void onMouseButton(MouseButtonEvent mouseButtonEvent) {
      if (c.player != null
         && c.world != null
         && this.fakePlayer != null
         && mouseButtonEvent.button() == 0
         && mouseButtonEvent.action() == 1
         && c.currentScreen == null) {
         if (c.crosshairTarget instanceof EntityHitResult EntityHitResultVar && EntityHitResultVar.getEntity() == this.fakePlayer) {
            mouseButtonEvent.cancel();
            this.applyHit();
         }
      }
   }

   @EventHandler
   public void onWorldChange(WorldChangeEvent worldChangeEvent) {
      this.remove();
      if (this.k()) {
         this.currentHp = this.health.get();
         this.spawn();
      }
   }

   private void applyHit() {
      if (c.player != null && c.world != null && this.fakePlayer != null) {
         float fGetAttackCooldownProgress = c.player.getAttackCooldownProgress(0.5F);
         if (!(fGetAttackCooldownProgress < 0.2F)) {
            float fGetAttributeValue = (float)c.player.getAttributeValue(EntityAttributes.ATTACK_DAMAGE)
               * (0.2F + fGetAttackCooldownProgress * fGetAttackCooldownProgress * 0.8F);
            if (!c.player.isOnGround()
               && !(c.player.fallDistance <= 0.0)
               && !c.player.isSprinting()
               && !c.player.isClimbing()
               && !c.player.isTouchingWater()
               && c.player.getVehicle() == null
               && !c.player.hasStatusEffect(StatusEffects.BLINDNESS)
               && !(fGetAttackCooldownProgress <= 0.9F)) {
               fGetAttributeValue *= 1.5F;
               c.player.addCritParticles(this.fakePlayer);
               c.world
                  .playSoundClient(
                     this.fakePlayer.getX(),
                     this.fakePlayer.getY(),
                     this.fakePlayer.getZ(),
                     SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                     SoundCategory.PLAYERS,
                     1.0F,
                     1.0F,
                     false
                  );
            } else if (fGetAttackCooldownProgress > 0.9F) {
               c.world
                  .playSoundClient(
                     this.fakePlayer.getX(),
                     this.fakePlayer.getY(),
                     this.fakePlayer.getZ(),
                     SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                     SoundCategory.PLAYERS,
                     1.0F,
                     1.0F,
                     false
                  );
            } else {
               c.world
                  .playSoundClient(
                     this.fakePlayer.getX(),
                     this.fakePlayer.getY(),
                     this.fakePlayer.getZ(),
                     SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
                     SoundCategory.PLAYERS,
                     1.0F,
                     1.0F,
                     false
                  );
            }

            float fMethod_453252 = (float)this.fakePlayer.getAttributeValue(EntityAttributes.ARMOR);
            float fMax = Math.max(
               0.0F,
               fGetAttributeValue
                  * (
                     1.0F
                        - Math.min(
                              20.0F,
                              Math.max(
                                 fMethod_453252 * 0.2F,
                                 Math.min(
                                    fMethod_453252,
                                    fMethod_453252 - fGetAttributeValue / (2.0F + (float)this.fakePlayer.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS) / 4.0F)
                                 )
                              )
                           )
                           / 25.0F
                  )
            );
            this.fakePlayer.hurtTime = 10;
            this.fakePlayer.maxHurtTime = 10;
            this.fakePlayer.handleStatus((byte)2);
            c.world
               .playSoundClient(
                  this.fakePlayer.getX(),
                  this.fakePlayer.getY(),
                  this.fakePlayer.getZ(),
                  SoundEvents.ENTITY_PLAYER_HURT,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F,
                  false
               );
            c.player.resetTicksSinceLastAttack();
            this.currentHp = Math.max(0.0F, this.currentHp - fMax);
            this.fakePlayer.setHealth(this.currentHp);
            if (this.currentHp <= 0.0F) {
               this.onFatalHit();
            }
         }
      }
   }

   private void onFatalHit() {
      if (this.fakePlayer != null && c.world != null) {
         if (this.totem.get()
            && (
               this.fakePlayer.getEquippedStack(EquipmentSlot.OFFHAND).getItem() == Items.TOTEM_OF_UNDYING
                  || this.fakePlayer.getEquippedStack(EquipmentSlot.MAINHAND).getItem() == Items.TOTEM_OF_UNDYING
            )) {
            this.currentHp = 1.0F;
            this.fakePlayer.setHealth(this.currentHp);
            this.fakePlayer.handleStatus((byte)35);
            if (this.fakePlayer.getEquippedStack(EquipmentSlot.OFFHAND).getItem() == Items.TOTEM_OF_UNDYING) {
               this.fakePlayer.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            } else {
               this.fakePlayer.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }

            c.world
               .playSoundClient(
                  this.fakePlayer.getX(),
                  this.fakePlayer.getY(),
                  this.fakePlayer.getZ(),
                  SoundEvents.ITEM_TOTEM_USE,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F,
                  false
               );
         } else {
            c.world
               .playSoundClient(
                  this.fakePlayer.getX(),
                  this.fakePlayer.getY(),
                  this.fakePlayer.getZ(),
                  SoundEvents.ENTITY_PLAYER_DEATH,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F,
                  false
               );
            this.remove();
            this.currentHp = this.health.get();
            this.spawn();
         }
      }
   }

   private void spawn() {
      if (c.player != null && c.world != null) {
         this.remove();
         this.currentHp = this.health.get();
         ClientWorld ClientWorldVar = c.world;
         String str = this.name.get();
         if (str == null || str.isBlank()) {
            str = "FakePlayer";
         }

         OtherClientPlayerEntity OtherClientPlayerEntityVar = new OtherClientPlayerEntity(ClientWorldVar, new GameProfile(UUID.randomUUID(), str));
         OtherClientPlayerEntityVar.copyFrom(c.player);
         OtherClientPlayerEntityVar.setId(-42069);
         double radians = Math.toRadians(c.player.getYaw());
         OtherClientPlayerEntityVar.refreshPositionAndAngles(
            c.player.getX() - Math.sin(radians) * 2.0,
            c.player.getY(),
            c.player.getZ() + Math.cos(radians) * 2.0,
            c.player.getYaw() + 180.0F,
            0.0F
         );
         OtherClientPlayerEntityVar.headYaw = OtherClientPlayerEntityVar.getYaw();
         OtherClientPlayerEntityVar.bodyYaw = OtherClientPlayerEntityVar.getYaw();
         OtherClientPlayerEntityVar.setHealth(this.currentHp);
         if (this.copyEquip.get()) {
            for (EquipmentSlot EquipmentSlotVar : EquipmentSlot.values()) {
               OtherClientPlayerEntityVar.equipStack(EquipmentSlotVar, c.player.getEquippedStack(EquipmentSlotVar).copy());
            }
         }

         if (this.totem.get()) {
            OtherClientPlayerEntityVar.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
         }

         ClientWorldVar.addEntity(OtherClientPlayerEntityVar);
         this.fakePlayer = OtherClientPlayerEntityVar;
      }
   }

   private void remove() {
      if (this.fakePlayer != null) {
         if (c.world != null) {
            c.world.removeEntity(this.fakePlayer.getId(), class_5529.DISCARDED);
         }

         this.fakePlayer = null;
      }
   }
}
