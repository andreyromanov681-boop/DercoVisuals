package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Fast EXP", b = "Мгновенно бросает бутылки опыта", c = ModuleCategory.UTILITIES)
public class FastEXP extends ClientModule {
   private final SliderSetting throwsPerTick = new SliderSetting("Бросков в тик", 5.0F, 1.0F, 20.0F, 1.0F);
   private final BooleanSetting onlyInHand = new BooleanSetting("Только в руке", "Бросать только из основной руки", true);
   public static int a;
   public static boolean b;

   @EventHandler
   public void onTick(ClientTickEvent clientTickEvent) {
      if (c.player != null && c.world != null && c.currentScreen == null) {
         int iA = (int)this.throwsPerTick.a();

         for (int i = 0; i < iA; i++) {
            boolean z = false;
            if (c.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
               c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, c.player.getYaw(), c.player.getPitch()));
               z = true;
            }

            if (!z && !this.onlyInHand.k() && c.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
               c.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0, c.player.getYaw(), c.player.getPitch()));
            }
         }
      }
   }
}
