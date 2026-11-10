package pulse.modules.utilities;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import pulse.events.ChatSendEvent;
import pulse.events.PacketEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;

@ModuleInfo(a = "Chat Helper", b = "Помощник для чата (анти-спам и фикс раскладки)", c = ModuleCategory.UTILITIES)
public class ChatHelper extends ClientModule {
   private final BooleanSetting antiSpam = new BooleanSetting("Анти спам", true);
   private final BooleanSetting saveHistory = new BooleanSetting("Сохранять историю", true);
   private final BooleanSetting fixLayout = new BooleanSetting("Фикс раскладки команд", true);
   private final List<String> chatHistory = new ArrayList<>();
   private static final String RU = "йцукенгшщзхъфывапролджэячсмитьбю.ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,ёЁ";
   private static final String EN = "qwertyuiop[]asdfghjkl;'zxcvbnm/.QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<?`~";

   @EventHandler
   public void onPacket(PacketEvent event) {
      if (this.k()) {
         if (event.e() == PacketEvent.MessageDirection.RECIEVE && this.antiSpam.get()) {
            String text = null;
            if (event.d() instanceof GameMessageS2CPacket) {
               text = ((GameMessageS2CPacket)event.d()).comp_763().getString();
            } else if (event.d() instanceof ChatMessageS2CPacket) {
               text = ((ChatMessageS2CPacket)event.d()).comp_1102().comp_1090();
            }

            if (text != null && this.isSpam(text)) {
               event.b();
            }
         }
      }
   }

   @EventHandler
   public void onChatSend(ChatSendEvent event) {
      if (this.k() && this.fixLayout.get() && c.player != null) {
         String msg = event.getMessage();
         if (msg != null && !msg.isEmpty()) {
            if ((msg.startsWith(".") || msg.startsWith("/")) && this.hasRussian(msg)) {
               String fixed = this.convertLayout(msg);
               if (!fixed.equals(msg)) {
                  event.cancel();
                  if (fixed.startsWith("/")) {
                     c.player.networkHandler.sendChatCommand(fixed.substring(1));
                  } else {
                     c.player.networkHandler.sendChatMessage(fixed);
                  }
               }
            }
         }
      }
   }

   private boolean hasRussian(String str) {
      for (char ch : str.toCharArray()) {
         if (ch >= 1072 && ch <= 1103 || ch >= 1040 && ch <= 1071 || ch == 1105 || ch == 1025) {
            return true;
         }
      }

      return false;
   }

   private String convertLayout(String str) {
      StringBuilder sb = new StringBuilder(str.length());

      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (i == 0 && ch == '.') {
            sb.append('/');
         } else {
            int idx = "йцукенгшщзхъфывапролджэячсмитьбю.ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,ёЁ".indexOf(ch);
            if (idx >= 0) {
               sb.append("qwertyuiop[]asdfghjkl;'zxcvbnm/.QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<?`~".charAt(idx));
            } else {
               sb.append(ch);
            }
         }
      }

      return sb.toString();
   }

   private boolean isSpam(String text) {
      if (text != null && !text.trim().isEmpty()) {
         String clean = Formatting.strip(text).trim().toLowerCase();
         if (this.chatHistory.contains(clean)) {
            return true;
         }

         String[] words = clean.split("\\s+");

         for (int i = 0; i < words.length - 1; i++) {
            if (words[i].length() > 2 && words[i].equals(words[i + 1])) {
               return true;
            }
         }

         this.chatHistory.add(clean);
         if (this.chatHistory.size() > 100) {
            this.chatHistory.remove(0);
         }

         return false;
      } else {
         return false;
      }
   }
}
