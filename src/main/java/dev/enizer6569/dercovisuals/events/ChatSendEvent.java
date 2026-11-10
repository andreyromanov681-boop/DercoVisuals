// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.events;

public class ChatSendEvent extends CancellableDercoVisualsEvent {
   private final String message;

   public ChatSendEvent(String str) {
      this.message = str;
   }

   public String getMessage() {
      return this.message;
   }

   public String d() {
      return this.message;
   }
}
