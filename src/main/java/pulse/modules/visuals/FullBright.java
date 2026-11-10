package pulse.modules.visuals;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;

@ModuleInfo(a = "Full Bright", b = "Increases world brightness", c = ModuleCategory.VISUALS)
public class FullBright extends ClientModule {
   @Override
   public void onDisable() {
      if (c.player != null) {
         try {
            c.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         } catch (Throwable var2) {
         }
      }

      super.onDisable();
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null) {
         if (!this.isEnabled()) {
            if (c.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
               try {
                  c.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
               } catch (Throwable var3) {
               }
            }
         } else {
            try {
               c.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 500, 0, false, false, false));
            } catch (Throwable var4) {
            }
         }
      }
   }
}
