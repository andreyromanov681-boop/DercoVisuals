// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.PlayerDeathEvent;
import dev.enizer6569.dercovisuals.media.chat.ChatMessages;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.TokenSetting;

@ModuleInfo(a = "Auto Respawn", b = "Автоматически возрождает игрока после смерти", c = ModuleCategory.UTILITIES)
public class AutoRespawn extends ClientModule {
   private final BooleanSetting e = new BooleanSetting("Авто возрождение", "Автоматически возрождаться после смерти", true);
   private final BooleanSetting f = new BooleanSetting("Отправлять команду после возрождения", "Отправлять команду после возрождения", false);
   private final TokenSetting g;
   private boolean h;
   private boolean i;
   private int j;
   public static int a;
   public static boolean b;

   public AutoRespawn() {
      TokenSetting tokenSetting = new TokenSetting("Команда", TokenSetting.TokenType.COMMAND, "/home");
      BooleanSetting booleanSetting = this.f;
      this.g = tokenSetting.a(booleanSetting::k);
      this.h = false;
   }

   @EventHandler
   public void a(PlayerDeathEvent playerDeathEvent) {
      if (playerDeathEvent.a() == c.player) {
         this.i = true;
      }
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null && c.world != null) {
         if (c.currentScreen instanceof DeathScreen) {
            int i = this.j;
            this.j = 2 * (i | 1) - (i ^ 1);
         }

         if (this.i && !(c.currentScreen instanceof DeathScreen) && c.player.age > 30) {
            ChatMessages.a(this.g.a());
            this.i = false;
            this.j = 0;
         }

         if (c.currentScreen instanceof DeathScreen DeathScreenVar) {
            if (!this.h) {
               this.h = true;
            }

            if (this.e.k() && this.a(DeathScreenVar)) {
               c.player.requestRespawn();
               c.currentScreen = null;
               this.j = 0;
            }
         } else if (this.h) {
            if (c.player.isAlive()) {
               this.h = false;
               if (!this.f.k() || this.g.a().isEmpty()) {
                  return;
               }

               ChatMessages.a(this.g.a());
            }
         }
      }
   }

   private boolean a(DeathScreen DeathScreenVar) {
      try {
         for (Object obj : DeathScreenVar.children()) {
            if (obj instanceof ButtonWidget && ((ButtonWidget)obj).active) {
               return true;
            }
         }

         return false;
      } catch (Exception e) {
         return false;
      }
   }

   @Override
   public void f() {
      super.f();
      this.h = false;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
