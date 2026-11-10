// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

import net.minecraft.entity.Entity;

public class AttackEntityEvent extends DercoVisualsEvent {
   private final Entity entity;

   public AttackEntityEvent(Entity EntityVar) {
      this.entity = EntityVar;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public Entity a() {
      return this.entity;
   }
}
