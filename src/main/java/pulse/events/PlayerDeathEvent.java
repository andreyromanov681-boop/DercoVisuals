package pulse.events;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerDeathEvent extends PulseEvent {
   private final PlayerEntity player;
   private final double x;
   private final double y;
   private final double z;

   public PlayerDeathEvent(PlayerEntity PlayerEntityVar, double d, double d2, double d3) {
      this.player = PlayerEntityVar;
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public PlayerEntity player() {
      return this.player;
   }

   public double x() {
      return this.x;
   }

   public double y() {
      return this.y;
   }

   public double z() {
      return this.z;
   }

   public PlayerEntity a() {
      return this.player;
   }

   public double b() {
      return this.x;
   }

   public double c() {
      return this.y;
   }

   public double d() {
      return this.z;
   }
}
