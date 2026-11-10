package pulse.modules.visuals;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import pulse.audio.SoundPlayer;
import pulse.events.AttackEntityEvent;
import pulse.events.CriticalHitEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SliderSetting;

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
