package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.InputUtil;
import pulse.events.AttackEntityEvent;
import pulse.events.ClientTickEvent;
import pulse.events.CriticalHitEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;

@ModuleInfo(a = "Shift Tab", b = "Briefly sneaks while attacking.", c = ModuleCategory.UTILITIES)
public class ShiftTab extends ClientModule {
   private static final int SNEAK_TICKS = 4;
   private final BooleanSetting triggerBeforeAttack = new BooleanSetting("Before Attack", "If disabled, sneaking starts after the attack event.", true);
   private int sneakTicksLeft;
   private boolean restorePending;
   private boolean previousSneakPressed;

   @EventHandler
   public void a(CriticalHitEvent criticalHitEvent) {
      this.startSneakBurst();
   }

   @EventHandler
   public void a(AttackEntityEvent attackEntityEvent) {
      this.startSneakBurst();
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null) {
         if (this.sneakTicksLeft > 0) {
            c.options.sneakKey.setPressed(true);
            c.player.setSneaking(true);
            this.sneakTicksLeft--;
         } else if (this.restorePending) {
            c.options.sneakKey.setPressed(this.previousSneakPressed);
            c.player.setSneaking(this.previousSneakPressed);
            this.restorePending = false;
         }
      }
   }

   @Override
   public void f() {
      super.f();
      this.sneakTicksLeft = 0;
      this.restorePending = false;
      if (c != null && c.options != null) {
         c.options.sneakKey.setPressed(this.isSneakKeyPhysicallyPressed());
      }
   }

   private void startSneakBurst() {
      if (c.player != null) {
         this.previousSneakPressed = this.isSneakKeyPhysicallyPressed();
         this.sneakTicksLeft = 4;
         this.restorePending = true;
         c.options.sneakKey.setPressed(true);
         c.player.setSneaking(true);
      }
   }

   private boolean isSneakKeyPhysicallyPressed() {
      return c != null && c.getWindow() != null && InputUtil.isKeyPressed(c.getWindow(), c.options.sneakKey.getDefaultKey().getCode());
   }
}
