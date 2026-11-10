// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.PacketEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;

@ModuleInfo(a = "RW Helper", b = "Автоматически закрывает меню сервера или фильтрует запрещенные сообщения в чате", c = ModuleCategory.UTILITIES)
public class RwHelper extends ClientModule {
   private final BooleanSetting a = new BooleanSetting("Закрывать меню", true);
   private final BooleanSetting b = new BooleanSetting("Фильтр запрещенных слов", true);
   private Set<String> e = new HashSet<>(
      Arrays.asList(
         "акриен(а|у|ом|е|чик)?",
         "рич(а|у|ом|ей|е)?",
         "ньюкод(ом|а|у|ами|ик|е)?",
         "экспенсив(ом|а|у|ами|е)?",
         "импакт(ом|а|у|ами|ик|е)?",
         "экселлент(ом|а|у|ами|ик|е)?",
         "экселент(ом|а|у|ами|ик)?",
         "катлаван(ом|а|у|ами|чик)?",
         "катлован(ом|а|у|ами|чик)?",
         "целестиал(ом|а|у|ами|е)?",
         "целк(ой|а|у|ами|очка|е)?",
         "матикс(ом|а|у|ами|е)?",
         "инерти(я|ей|ю|ями|е)?",
         "эксп(а|ой|ою|у|уличка|е)?",
         "флюгер(ом|а|у|ами)?",
         "рикер(а|у|ом|очек)?",
         "фанпе(й|ю|я|ем|е|йчик)?",
         "вексайд(ом|а|у|ами|ик|е)?",
         "нурсултан(а|у|е|ом|чик)?",
         "нурик(а|у|ом|е)?",
         "нурлан(а|у|ом|чик|е)?",
         "векс(ом|у|а|ами|ик|е)?",
         "релейк(ом|у|а|ами|е)?",
         "арбуз(ом|а|у|ами|ик|е)?",
         "вилд(ом|у|а|ами|ик|е)?",
         "фантайм(е|а|у)?",
         "холик(е|а|у)?",
         "холиворлд(а|у|е)?",
         "рокстар(ом|а|у|ами|чик|е)?",
         "рогалик(а|у|ом|е)?",
         "тандерхак(ом|у|и|ами|а|е)?",
         "ликвидбаунс(а|у|ами|е)?",
         "expensive",
         "celestial",
         "newcode",
         "arbuz",
         "akrien",
         "nursultan",
         "relake",
         "wild",
         "wurst",
         "catlovan",
         "excellent",
         "rockstar",
         "catlavan",
         "impact",
         "matix",
         "inertia",
         "wex",
         "wexside",
         "nurik",
         "nurlan",
         "rich",
         "funpay",
         "fluger",
         "riker",
         "funtime",
         "holyworld",
         "wwe",
         "hvh",
         "rogalik",
         "thunderhack",
         "liquidbounce"
      )
   );
   private final List<Pattern> compiledPatterns = new ArrayList<>();

   public RwHelper() {
      for (String s : this.e) {
         try {
            this.compiledPatterns.add(Pattern.compile(s, 66));
         } catch (Exception ex) {
            this.compiledPatterns.add(Pattern.compile(Pattern.quote(s), 2));
         }
      }
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (this.a.get() && c.currentScreen instanceof BookScreen) {
         c.setScreen(null);
      }
   }

   @EventHandler
   private void a(PacketEvent packetEvent) {
      if (c.player != null) {
         if (packetEvent.e() == PacketEvent.MessageDirection.RECIEVE) {
            if (this.a.get()) {
               if (packetEvent.d() instanceof OpenScreenS2CPacket) {
                  OpenScreenS2CPacket openScreen = (OpenScreenS2CPacket)packetEvent.d();
                  String title = Formatting.strip(openScreen.getName().getString());
                  if (title != null
                     && (
                        title.contains("Меню")
                           || title.contains("ReallyWorld")
                           || title.contains("Информация")
                           || title.contains("Новости")
                           || title.contains("Помощь")
                     )) {
                     packetEvent.b();
                     if (c.getNetworkHandler() != null) {
                        c.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(openScreen.getSyncId()));
                     }

                     return;
                  }
               }

               if (packetEvent.d() instanceof OpenWrittenBookS2CPacket) {
                  packetEvent.b();
                  return;
               }
            }

            if (this.b.get()) {
               String text = null;
               if (packetEvent.d() instanceof GameMessageS2CPacket) {
                  text = ((GameMessageS2CPacket)packetEvent.d()).comp_763().getString();
               } else if (packetEvent.d() instanceof ChatMessageS2CPacket) {
                  text = ((ChatMessageS2CPacket)packetEvent.d()).comp_1102().comp_1090();
               }

               if (text != null && this.containsBannedWord(text)) {
                  packetEvent.b();
                  return;
               }
            }
         }
      }
   }

   private boolean containsBannedWord(String text) {
      if (text != null && !text.isEmpty()) {
         for (Pattern p : this.compiledPatterns) {
            if (p.matcher(text).find()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
