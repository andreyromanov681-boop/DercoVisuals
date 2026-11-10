// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

import java.util.UUID;

public class PlayerJoinEvent extends DercoVisualsEvent {
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
