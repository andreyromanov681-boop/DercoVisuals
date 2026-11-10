// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.hud.notifications.ActionNotification;
import dev.enizer6569.dercovisuals.hud.notifications.HudNotificationCenter;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.SliderSetting;
import dev.enizer6569.dercovisuals.util.ElapsedTimer;

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
