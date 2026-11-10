package pulse.gui.friends;

import pulse.gui.core.PulseClickGuiScreen;
import ru.pulse.Pulse;

public final class FriendLookup {
   private FriendLookup() {
   }

   public static boolean isFriendName(String str) {
      FriendsTab friendsTabH;
      return Pulse.getInstance().getClickGui() instanceof PulseClickGuiScreen
            && (friendsTabH = ((PulseClickGuiScreen)Pulse.getInstance().getClickGui()).h()) != null
         ? friendsTabH.e().a().stream().anyMatch(historyEntry -> historyEntry.a().equalsIgnoreCase(str))
         : false;
   }

   public static boolean a(String str) {
      return isFriendName(str);
   }
}
