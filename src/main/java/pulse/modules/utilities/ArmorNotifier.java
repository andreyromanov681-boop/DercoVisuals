package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import pulse.events.ClientTickEvent;
import pulse.hud.notifications.ActionNotification;
import pulse.hud.notifications.HudNotificationCenter;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.SliderSetting;
import pulse.util.ElapsedTimer;

@ModuleInfo(a = "Armor Notifier", b = "Уведомляет о низкой прочности брони", c = ModuleCategory.UTILITIES)
public class ArmorNotifier extends ClientModule {
   private final SliderSetting threshold = new SliderSetting("Порог (%)", 15.0F, 1.0F, 50.0F, 1.0F);
   private final ElapsedTimer timer = new ElapsedTimer();

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (this.k() && c.player != null && c.world != null) {
         for (int i = 36; i <= 39; i++) {
            ItemStack stack = c.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isDamageable()) {
               int maxDamage = stack.getMaxDamage();
               int damage = stack.getDamage();
               int left = maxDamage - damage;
               float pct = (float)left / maxDamage * 100.0F;
               if (pct <= this.threshold.k()) {
                  if (this.timer.a(10000L)) {
                     this.timer.b();
                     HudNotificationCenter.a(new ActionNotification.Builder("Armor Notifier").a("МАЛО ПРОЧНОСТИ БРОНИ!").a(3000L).b());
                  }
                  break;
               }
            }
         }
      }
   }
}
