package pulse.events;

import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ItemUseEvent extends CancellablePulseEvent {
   private final PlayerEntity player;
   private final World world;
   private final Hand hand;

   public ItemUseEvent(PlayerEntity PlayerEntityVar, World WorldVar, Hand HandVar) {
      this.player = PlayerEntityVar;
      this.world = WorldVar;
      this.hand = HandVar;
   }

   public PlayerEntity player() {
      return this.player;
   }

   public World world() {
      return this.world;
   }

   public Hand hand() {
      return this.hand;
   }

   public PlayerEntity d() {
      return this.player;
   }

   public World e() {
      return this.world;
   }

   public Hand f() {
      return this.hand;
   }
}
