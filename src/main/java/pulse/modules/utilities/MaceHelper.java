package pulse.modules.utilities;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;
import pulse.events.AttackEntityEvent;
import pulse.media.chat.ChatMessages;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;

@ModuleInfo(a = "Mace Helper", b = "Помощник для игры с булавой.", c = ModuleCategory.UTILITIES)
public class MaceHelper extends ClientModule {
   public final BooleanSetting chatMessage = new BooleanSetting("Сообщение в чат", true);
   public final BooleanSetting handHighlight = new BooleanSetting("Подсветка в руке", true);
   public final BooleanSetting hotbarHighlight = new BooleanSetting("Подсветка в хотбаре", true);

   public static boolean isMace(ItemStack stack) {
      return stack != null && !stack.isEmpty()
         ? stack.isOf(Items.MACE) || stack.getItem().toString().toLowerCase().contains("mace")
         : false;
   }

   public static boolean isHoldingMace() {
      MinecraftClient client = MinecraftClient.getInstance();
      return client != null && client.player != null ? isMace(client.player.getMainHandStack()) || isMace(client.player.getOffHandStack()) : false;
   }

   public static int getChargePercentage() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client != null && client.player != null) {
         float progress = client.player.getAttackCooldownProgress(0.0F);
         return (int)Math.min(100.0F, Math.max(0.0F, progress * 100.0F));
      } else {
         return 0;
      }
   }

   public static Color getChargeColor() {
      return getChargeColor(getChargePercentage());
   }

   public static Color getChargeColor(int charge) {
      if (charge >= 80) {
         return new Color(40, 220, 50);
      } else {
         return charge >= 41 ? new Color(255, 140, 0) : new Color(240, 40, 40);
      }
   }

   @EventHandler
   public void onAttack(AttackEntityEvent event) {
      if (c.player != null && event.getEntity() != null) {
         ItemStack mainHand = c.player.getMainHandStack();
         if (isMace(mainHand) && this.chatMessage.get()) {
            int charge = getChargePercentage();
            ChatMessages.a((Object)("Булава заряжена на " + charge + "%"));
         }
      }
   }
}
