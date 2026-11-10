package pulse.modules.utilities;

import java.util.regex.Pattern;
import meteordevelopment.orbit.EventHandler;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.TokenSetting;

@ModuleInfo(a = "Auto Invest", b = "Automatically invests clan money above the configured limit.", c = ModuleCategory.UTILITIES)
public class AutoInvest extends ClientModule {
   private final TokenSetting limit = new TokenSetting("Limit", "Minimum balance before investing.", TokenSetting.TokenType.PRICE, "1000000", "$");
   private final Pattern numberPattern = Pattern.compile("\\d+");
   private long lastInvestTime = -1L;

   @EventHandler
   private void a(ClientTickEvent clientTickEvent) {
   }

   private long n() {
      try {
         return Long.parseLong(this.limit.k().replace("$", "").replace(" ", ""));
      } catch (NumberFormatException e) {
         return 0L;
      }
   }

   private long o() {
      return this.lastInvestTime;
   }

   @Override
   public void f() {
      super.f();
      this.lastInvestTime = -1L;
   }
}
