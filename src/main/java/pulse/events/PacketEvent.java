package pulse.events;

import net.minecraft.network.packet.Packet;

public class PacketEvent extends CancellablePulseEvent {
   private final Packet<?> packet;
   private final PacketEvent.MessageDirection direction;

   public PacketEvent(Packet<?> PacketVar, PacketEvent.MessageDirection messageDirection) {
      this.packet = PacketVar;
      this.direction = messageDirection;
   }

   public Packet<?> packet() {
      return this.packet;
   }

   public PacketEvent.MessageDirection direction() {
      return this.direction;
   }

   public Packet<?> d() {
      return this.packet;
   }

   public PacketEvent.MessageDirection e() {
      return this.direction;
   }

   public enum MessageDirection {
      RECEIVE,
      SEND;

      public static final PacketEvent.MessageDirection RECIEVE = RECEIVE;
   }
}
