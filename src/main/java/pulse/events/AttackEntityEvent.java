package pulse.events;

import net.minecraft.entity.Entity;

public class AttackEntityEvent extends PulseEvent {
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
