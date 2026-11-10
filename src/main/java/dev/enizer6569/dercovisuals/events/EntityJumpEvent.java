// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

import net.minecraft.entity.LivingEntity;

public class EntityJumpEvent extends DercoVisualsEvent {
   private final LivingEntity entity;

   public EntityJumpEvent(LivingEntity LivingEntityVar) {
      this.entity = LivingEntityVar;
   }

   public LivingEntity entity() {
      return this.entity;
   }

   public LivingEntity a() {
      return this.entity;
   }
}
