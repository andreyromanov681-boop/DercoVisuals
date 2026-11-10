package pulse.events;

import net.minecraft.entity.Entity;

public class CriticalHitEvent extends PulseEvent {
   private final Entity entity;

   public CriticalHitEvent(Entity EntityVar) {
      this.entity = EntityVar;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public Entity a() {
      return this.entity;
   }
}
