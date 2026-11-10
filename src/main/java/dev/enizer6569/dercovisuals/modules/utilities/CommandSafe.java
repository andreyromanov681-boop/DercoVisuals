// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import dev.enizer6569.dercovisuals.events.ChatSendEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;

@ModuleInfo(a = "Command Safe", b = "Запрещает случайный ввод опасных команд /suicide, /ci, /clear", c = ModuleCategory.UTILITIES)
public class CommandSafe extends ClientModule {
   private String pendingCommand = "";
   private long pendingTime = 0L;

   @EventHandler
   public void onChatSend(ChatSendEvent event) {
      if (c.player != null) {
         String msg = event.getMessage().trim();
         String msgLower = msg.toLowerCase();
         boolean isSuicide = msgLower.startsWith("/suicide");
         boolean isCi = msgLower.equals("/ci") || msgLower.startsWith("/ci ");
         boolean isClear = msgLower.equals("/clear") || msgLower.startsWith("/clear ");
         if (isSuicide || isCi || isClear) {
            long now = System.currentTimeMillis();
            if (msg.equalsIgnoreCase(this.pendingCommand) && now - this.pendingTime < 5000L) {
               this.pendingCommand = "";
               this.pendingTime = 0L;
               return;
            }

            event.cancel();
            this.pendingCommand = msg;
            this.pendingTime = now;
            String cmdName = isSuicide ? "/suicide" : (isCi ? "/ci" : "/clear");
            String actionDesc = isSuicide ? " убьет вашего персонажа!" : " очистит ваш инвентарь!";
            c.player.sendMessage(Text.literal("§d[DercoVisuals] §f» Вы уверены? Команда §c" + cmdName + "§f" + actionDesc), false);
            c.player.sendMessage(Text.literal("§d[DercoVisuals] §f» Для подтверждения команды отправьте ее еще раз."), false);
         }
      }
   }
}
