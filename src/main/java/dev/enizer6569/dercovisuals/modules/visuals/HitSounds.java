// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import dev.enizer6569.dercovisuals.audio.SoundPlayer;
import dev.enizer6569.dercovisuals.events.AttackEntityEvent;
import dev.enizer6569.dercovisuals.events.CriticalHitEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ModeSetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Hit Sounds", b = "Plays a sound when you hit a target.", c = ModuleCategory.VISUALS)
public class HitSounds extends ClientModule {
   private final ModeSetting sound = new ModeSetting("Sound", new String[]{"Default", "Bell", "Bonk", "Bubble", "Pop", "Uwu", "Moan"}, "Default");
   private final SliderSetting volume = new SliderSetting("Volume", 1.0F, 0.1F, 2.0F, 0.1F);
   private final BooleanSetting onlyPlayers = new BooleanSetting("Only Players", false);

   @EventHandler
   public void a(AttackEntityEvent attackEntityEvent) {
      if (attackEntityEvent.getEntity() != null) {
         if (!this.onlyPlayers.get() || attackEntityEvent.getEntity() instanceof PlayerEntity) {
            this.playSound();
         }
      }
   }

   @EventHandler
   public void a(CriticalHitEvent criticalHitEvent) {
      if (criticalHitEvent.getEntity() != null) {
         if (!this.onlyPlayers.get() || criticalHitEvent.getEntity() instanceof PlayerEntity) {
            this.playSound();
         }
      }
   }

   private void playSound() {
      String strD = this.sound.d();
      float fFloatValue = this.volume.k();
      switch (strD) {
         case "Default":
            SoundPlayer.playRandomHit(fFloatValue);
            break;
         case "Moan":
            SoundPlayer.playRandomMoan(fFloatValue);
            break;
         default:
            SoundPlayer.play(strD.toLowerCase(), fFloatValue);
      }
   }
}
