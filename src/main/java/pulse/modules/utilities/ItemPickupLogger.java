package pulse.modules.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.MutableText;
import pulse.events.ClientTickEvent;
import pulse.media.chat.ChatMessages;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;

@ModuleInfo(a = "Item Pickup Logger", b = "Логирует поднятые предметы в чат", c = ModuleCategory.UTILITIES)
public class ItemPickupLogger extends ClientModule {
   private final BooleanSetting e = new BooleanSetting("Только донат предметы", false);
   private final Map<String, ItemPickupLogger.TrackedItem> lastInventory = new HashMap<>();
   public static int a;
   public static boolean b;

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (c.world == null || c.player == null) {
         this.lastInventory.clear();
      } else if (c.currentScreen != null && !(c.currentScreen instanceof InventoryScreen)) {
         this.lastInventory.clear();
         this.lastInventory.putAll(this.getInventorySnapshot());
      } else {
         Map<String, ItemPickupLogger.TrackedItem> currentInventory = this.getInventorySnapshot();
         if (!this.lastInventory.isEmpty()) {
            for (Entry<String, ItemPickupLogger.TrackedItem> entry : currentInventory.entrySet()) {
               String key = entry.getKey();
               ItemPickupLogger.TrackedItem currentItem = entry.getValue();
               int prevCount = 0;
               ItemPickupLogger.TrackedItem prevItem = this.lastInventory.get(key);
               if (prevItem != null) {
                  prevCount = prevItem.count;
               }

               int diff = currentItem.count - prevCount;
               if (diff > 0) {
                  this.logPickup(currentItem.stack, diff);
               }
            }
         }

         this.lastInventory.clear();
         this.lastInventory.putAll(currentInventory);
      }
   }

   private Map<String, ItemPickupLogger.TrackedItem> getInventorySnapshot() {
      Map<String, ItemPickupLogger.TrackedItem> snapshot = new HashMap<>();

      for (int i = 0; i < c.player.getInventory().size(); i++) {
         ItemStack stack = c.player.getInventory().getStack(i);
         if (!stack.isEmpty()) {
            String key = stack.getItem().toString() + "||" + stack.getName().getString();
            ItemPickupLogger.TrackedItem item = snapshot.computeIfAbsent(key, k -> new ItemPickupLogger.TrackedItem(stack.copy(), 0));
            item.count = item.count + stack.getCount();
         }
      }

      return snapshot;
   }

   private void logPickup(ItemStack stack, int count) {
      boolean isDonat = this.a(stack);
      if (!this.e.k() || isDonat) {
         Text name = stack.getName();
         MutableText msg = isDonat
            ? Text.literal("§6[PickupLogger] §d★ Поднят донат-предмет: §e").append(name)
            : Text.literal("§6[PickupLogger] §fПоднят предмет: §b").append(name);
         if (count > 1) {
            msg.append(Text.literal(" §7x" + count));
         }

         ChatMessages.b(msg);
      }
   }

   private boolean a(ItemStack ItemStackVar) {
      Text MutableTextVarGetName = ItemStackVar.getName();
      if (MutableTextVarGetName.getString().contains("★")) {
         return true;
      }

      if (MutableTextVarGetName instanceof MutableText MutableTextVar) {
         if (MutableTextVar.getStyle().isObfuscated()) {
            return true;
         }

         Iterator it = MutableTextVar.getSiblings().iterator();

         while (it.hasNext()) {
            if (((Text)it.next()).getStyle().isObfuscated()) {
               return true;
            }
         }
      }

      return false;
   }

   @Generated
   public BooleanSetting n() {
      return this.e;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   @Override
   public void e() {
      super.e();
      this.lastInventory.clear();
   }

   @Override
   public void f() {
      super.f();
      this.lastInventory.clear();
   }

   private static class TrackedItem {
      final ItemStack stack;
      int count;

      TrackedItem(ItemStack stack, int count) {
         this.stack = stack;
         this.count = count;
      }
   }
}
