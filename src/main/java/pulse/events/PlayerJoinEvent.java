package pulse.events;

import java.util.UUID;

public class PlayerJoinEvent extends PulseEvent {
   private final String playerName;
   private final UUID playerUuid;

   public PlayerJoinEvent(String str, UUID uuid) {
      this.playerName = str;
      this.playerUuid = uuid;
   }

   public String playerName() {
      return this.playerName;
   }

   public UUID playerUuid() {
      return this.playerUuid;
   }

   public String a() {
      return this.playerName;
   }

   public UUID b() {
      return this.playerUuid;
   }
}
