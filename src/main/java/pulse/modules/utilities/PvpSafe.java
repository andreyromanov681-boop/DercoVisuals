package pulse.modules.utilities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import meteordevelopment.orbit.EventHandler;
import pulse.events.ChatSendEvent;
import pulse.events.ClientTickEvent;
import pulse.media.chat.ChatMessages;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.player.CombatState;
import pulse.util.ElapsedTimer;

@ModuleInfo(a = "PVP Safe", b = "Blocks risky commands while PvP mode is active.", c = ModuleCategory.UTILITIES)
public class PvpSafe extends ClientModule {
   private static final long WARNING_COOLDOWN_MS = 3000L;
   private final Pattern dangerousCommandPattern = Pattern.compile(
      "^/(spawn|home|hub|lobby|rtp|warp|back|logout|server|suicide|kill|auction|trade|duel|pay|msg|tell|w)(\\s+.*)?$", 2
   );
   private final Map<String, ElapsedTimer> warningTimers = new ConcurrentHashMap<>();

   @EventHandler
   private void a(ChatSendEvent chatSendEvent) {
      String strD = chatSendEvent.d();
      if (strD != null && strD.startsWith("/") && CombatState.a().a()) {
         Matcher matcher = this.dangerousCommandPattern.matcher(strD.trim());
         if (matcher.matches()) {
            String lowerCase = matcher.group(1).toLowerCase();
            ElapsedTimer elapsedTimerRemove = this.warningTimers.remove(lowerCase);
            if (elapsedTimerRemove != null && !elapsedTimerRemove.a(3000L)) {
               chatSendEvent.b();
               return;
            }

            this.warningTimers.put(lowerCase, new ElapsedTimer());
            ChatMessages.a((Object)("PVP Safe blocked /" + lowerCase + " while PvP mode is active."));
            chatSendEvent.b();
         }
      }
   }

   @EventHandler
   private void a(ClientTickEvent clientTickEvent) {
      this.warningTimers.values().removeIf(elapsedTimer -> elapsedTimer.a(3000L));
   }
}
