// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.friends;

import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.DercoVisuals;

public final class FriendLookup {
   private FriendLookup() {
   }

   public static boolean isFriendName(String str) {
      FriendsTab friendsTabH;
      return DercoVisuals.getInstance().getClickGui() instanceof DercoVisualsClickGuiScreen
            && (friendsTabH = ((DercoVisualsClickGuiScreen)DercoVisuals.getInstance().getClickGui()).h()) != null
         ? friendsTabH.e().a().stream().anyMatch(historyEntry -> historyEntry.a().equalsIgnoreCase(str))
         : false;
   }

   public static boolean a(String str) {
      return isFriendName(str);
   }
}
