package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.component.DataComponentTypes;
import pulse.events.DropItemEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Lock Slot", b = "Запрещает выбрасывание предметов из выбранных слотов хотбара", c = ModuleCategory.UTILITIES)
public class LockSlot extends ClientModule {
   private final BooleanSetting lockHotbarSlots = new BooleanSetting("Блокировать хотбар", true);
   private final ModeSetting slots;
   private final BooleanSetting lockOnlyValuableItems;

   public LockSlot() {
      ModeSetting modeSetting = new ModeSetting("Слоты", new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
      BooleanSetting booleanSetting = this.lockHotbarSlots;
      this.slots = modeSetting.a(booleanSetting::k);
      this.lockOnlyValuableItems = new BooleanSetting("Только ценные предметы", false);
   }

   @EventHandler
   public void a(DropItemEvent dropItemEvent) {
      if (this.lockHotbarSlots.a()) {
         int slot = dropItemEvent.slot();
         if (slot >= 0 && slot <= 8) {
            if (this.slots.a(slot) || this.slots.e().isEmpty()) {
               if (this.lockOnlyValuableItems.a()) {
                  if (c == null || c.player == null) {
                     return;
                  }

                  ItemStack stack = c.player.getInventory().getStack(slot);
                  if (stack.isEmpty() || !isValuable(stack)) {
                     return;
                  }
               }

               dropItemEvent.a();
            }
         }
      }
   }

   private static boolean isValuable(ItemStack stack) {
      if (stack.get(DataComponentTypes.EQUIPPABLE) == null && stack.get(DataComponentTypes.TOOL) == null) {
         Text name = stack.getName();
         if (name == null || !name.getString().contains("★") && !name.getStyle().isObfuscated()) {
            return stack.getItem() != Items.TOTEM_OF_UNDYING
                  && stack.getItem() != Items.ELYTRA
                  && stack.getItem() != Items.SHIELD
                  && stack.getItem() != Items.CROSSBOW
                  && stack.getItem() != Items.BOW
                  && stack.getItem() != Items.TRIDENT
                  && stack.getItem() != Items.ENDER_PEARL
                  && stack.getItem() != Items.CHORUS_FRUIT
                  && stack.getItem() != Items.GOLDEN_APPLE
                  && stack.getItem() != Items.ENCHANTED_GOLDEN_APPLE
               ? stack.hasEnchantments()
               : true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
