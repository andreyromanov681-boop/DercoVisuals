package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import pulse.events.ClientTickEvent;
import pulse.hud.notifications.ActionNotification;
import pulse.hud.notifications.HudNotificationCenter;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Auc Helper", b = "Автоматическое перевыставление предметов на аукционе", c = ModuleCategory.UTILITIES)
public class AucHelper extends ClientModule {
   private final BooleanSetting autoReissue = new BooleanSetting("Автоперевыставление", true);
   private final ModeSetting mode = new ModeSetting("Режим г", new String[]{"Через меню"}, "Через меню");
   private final BooleanSetting autoOpen = new BooleanSetting("Авто открытие аукциона", true);
   private final BooleanSetting closeAfterUpdate = new BooleanSetting("Закрывать после обновления", true);
   private AucHelper.State state = AucHelper.State.IDLE;
   private long cycleStartTime = 0L;
   private long actionTime = 0L;
   private boolean warned10s = false;

   @Override
   public void e() {
      super.e();
      this.state = AucHelper.State.IDLE;
      this.cycleStartTime = System.currentTimeMillis();
      this.warned10s = false;
   }

   @Override
   public void f() {
      super.f();
      this.state = AucHelper.State.IDLE;
      this.cycleStartTime = 0L;
      this.warned10s = false;
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (c.player != null && c.world != null && this.a() && this.autoReissue.get()) {
         long now = System.currentTimeMillis();
         if (this.state == AucHelper.State.IDLE) {
            long elapsedMs = now - this.cycleStartTime;
            long remainingMs = 60000L - elapsedMs;
            if (remainingMs <= 10000L && !this.warned10s) {
               this.warned10s = true;
               HudNotificationCenter.a(new ActionNotification.Builder("Auc Helper").a("Перевыставление предметов через 10 сек.").a(4000L).b());
            }

            if (remainingMs <= 0L) {
               this.warned10s = false;
               this.cycleStartTime = now;
               if (this.autoOpen.get()) {
                  c.player.networkHandler.sendChatCommand("ah");
                  this.state = AucHelper.State.WAITING_AH;
                  this.actionTime = now;
               } else {
                  this.state = AucHelper.State.WAITING_AH;
                  this.actionTime = now;
               }
            }
         } else if (this.state == AucHelper.State.WAITING_AH) {
            if (now - this.actionTime >= 250L) {
               if (c.player.currentScreenHandler != null && c.player.currentScreenHandler != c.player.playerScreenHandler) {
                  int storageSlot = -1;

                  for (Slot slot : c.player.currentScreenHandler.slots) {
                     ItemStack stack = slot.getStack();
                     if (!stack.isEmpty()) {
                        String name = stack.getName().getString().toLowerCase();
                        if (name.contains("хранилищ")) {
                           storageSlot = slot.id;
                           break;
                        }
                     }
                  }

                  if (storageSlot == -1 && now - this.actionTime > 3000L && c.player.currentScreenHandler.slots.size() > 46) {
                     ItemStack stack46 = ((Slot)c.player.currentScreenHandler.slots.get(46)).getStack();
                     if (!stack46.isEmpty()) {
                        storageSlot = 46;
                     }
                  }

                  if (storageSlot != -1) {
                     c.interactionManager.clickSlot(c.player.currentScreenHandler.syncId, storageSlot, 0, SlotActionType.PICKUP, c.player);
                     this.state = AucHelper.State.WAITING_STORAGE;
                     this.actionTime = now;
                  } else if (now - this.actionTime > 8000L) {
                     this.state = AucHelper.State.IDLE;
                     this.cycleStartTime = now;
                  }
               } else {
                  if (now - this.actionTime > 8000L) {
                     this.state = AucHelper.State.IDLE;
                     this.cycleStartTime = now;
                  }
               }
            }
         } else if (this.state == AucHelper.State.WAITING_STORAGE) {
            if (now - this.actionTime >= 300L) {
               if (c.player.currentScreenHandler != null && c.player.currentScreenHandler != c.player.playerScreenHandler) {
                  int reissueSlot = -1;

                  for (Slot slot : c.player.currentScreenHandler.slots) {
                     ItemStack stack = slot.getStack();
                     if (!stack.isEmpty()) {
                        String name = stack.getName().getString().toLowerCase();
                        if (name.contains("перевыстав")) {
                           reissueSlot = slot.id;
                           break;
                        }
                     }
                  }

                  if (reissueSlot == -1 && now - this.actionTime > 3000L && c.player.currentScreenHandler.slots.size() > 52) {
                     ItemStack stack52 = ((Slot)c.player.currentScreenHandler.slots.get(52)).getStack();
                     if (!stack52.isEmpty()) {
                        reissueSlot = 52;
                     }
                  }

                  if (reissueSlot != -1) {
                     c.interactionManager.clickSlot(c.player.currentScreenHandler.syncId, reissueSlot, 0, SlotActionType.PICKUP, c.player);
                     this.state = AucHelper.State.CLICKED_REISSUE;
                     this.actionTime = now;
                  } else if (now - this.actionTime > 8000L) {
                     this.state = AucHelper.State.IDLE;
                     this.cycleStartTime = now;
                  }
               } else {
                  if (now - this.actionTime > 8000L) {
                     this.state = AucHelper.State.IDLE;
                     this.cycleStartTime = now;
                  }
               }
            }
         } else {
            if (this.state == AucHelper.State.CLICKED_REISSUE) {
               if (now - this.actionTime < 250L) {
                  return;
               }

               if (this.closeAfterUpdate.get()) {
                  if (c.player != null) {
                     c.player.closeHandledScreen();
                  }

                  c.setScreen(null);
               }

               this.state = AucHelper.State.IDLE;
               this.cycleStartTime = now;
               this.warned10s = false;
            }
         }
      }
   }

   private enum State {
      IDLE,
      WAITING_AH,
      WAITING_STORAGE,
      CLICKED_REISSUE;
   }
}
