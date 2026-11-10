package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.registry.Registries;
import pulse.events.ClientTickEvent;
import pulse.events.EventBusService;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.SliderSetting;
import pulse.util.ElapsedTimer;

@ModuleInfo(a = "Chest Sorter", b = "Быстрая сортировка ресурсов в сундуках (кнопка с воронкой)", c = ModuleCategory.UTILITIES)
public class ChestSorter extends ClientModule {
   private final BooleanSetting showButton = new BooleanSetting("Показывать кнопку", true);
   private final SliderSetting delay = new SliderSetting("Задержка (мс)", 40.0F, 0.0F, 300.0F, 10.0F);
   private boolean sorting = false;
   private ChestSorter.SortMode mode = ChestSorter.SortMode.MERGING;
   private int mergeI = 0;
   private int mergeJ = 1;
   private int currentSlot = 0;
   private final ElapsedTimer timer = new ElapsedTimer();

   public ChestSorter() {
      this.setEnabledStateQuiet(true);
      EventBusService.EVENT_BUS.subscribe(this);
   }

   public boolean shouldShowButton() {
      return this.showButton.get();
   }

   public static boolean isSupportedScreen(HandledScreen<?> screen) {
      if (screen != null && screen.getScreenHandler() != null) {
         ScreenHandler handler = screen.getScreenHandler();
         boolean supportedType = handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler;
         return supportedType && handler.slots.size() > 36;
      } else {
         return false;
      }
   }

   public static void startSorting(ScreenHandler handler) {
      ChestSorter sorter = ModuleRegistry.CHEST_SORTER;
      if (sorter != null) {
         sorter.beginSorting(handler);
      }
   }

   public void beginSorting(ScreenHandler handler) {
      if (handler != null) {
         int containerSlots = handler.slots.size() - 36;
         if (containerSlots > 0) {
            this.sorting = true;
            this.mode = ChestSorter.SortMode.MERGING;
            this.mergeI = 0;
            this.mergeJ = 1;
            this.currentSlot = 0;
            this.timer.b();
         }
      }
   }

   @Override
   public void f() {
      super.f();
      this.sorting = false;
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (this.sorting && c.player != null && c.player.currentScreenHandler != null && c.player.currentScreenHandler != c.player.playerScreenHandler) {
         if (this.timer.a(this.delay.k().longValue())) {
            ScreenHandler handler = c.player.currentScreenHandler;
            int containerSlots = handler.slots.size() - 36;
            if (containerSlots <= 0) {
               this.sorting = false;
            } else {
               if (this.mode == ChestSorter.SortMode.MERGING) {
                  boolean merged = false;

                  for (int i = 0; i < containerSlots; i++) {
                     ItemStack stackI = ((Slot)handler.slots.get(i)).getStack();
                     if (!stackI.isEmpty() && stackI.getCount() < stackI.getMaxCount()) {
                        for (int j = i + 1; j < containerSlots; j++) {
                           ItemStack stackJ = ((Slot)handler.slots.get(j)).getStack();
                           if (this.canMerge(stackI, stackJ)) {
                              c.interactionManager.clickSlot(handler.syncId, j, 0, SlotActionType.PICKUP, c.player);
                              c.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, c.player);
                              if (!handler.getCursorStack().isEmpty()) {
                                 c.interactionManager.clickSlot(handler.syncId, j, 0, SlotActionType.PICKUP, c.player);
                              }

                              merged = true;
                              break;
                           }
                        }

                        if (merged) {
                           break;
                        }
                     }
                  }

                  if (merged) {
                     this.timer.b();
                     return;
                  }

                  this.mode = ChestSorter.SortMode.SORTING;
                  this.currentSlot = 0;
               }

               if (this.mode == ChestSorter.SortMode.SORTING) {
                  while (this.currentSlot < containerSlots) {
                     int minSlot = this.currentSlot;
                     ItemStack minStack = ((Slot)handler.slots.get(this.currentSlot)).getStack();

                     for (int k = this.currentSlot + 1; k < containerSlots; k++) {
                        ItemStack stackK = ((Slot)handler.slots.get(k)).getStack();
                        if (this.compareStacks(stackK, minStack) < 0) {
                           minSlot = k;
                           minStack = stackK;
                        }
                     }

                     if (minSlot != this.currentSlot) {
                        c.interactionManager.clickSlot(handler.syncId, this.currentSlot, 0, SlotActionType.PICKUP, c.player);
                        c.interactionManager.clickSlot(handler.syncId, minSlot, 0, SlotActionType.PICKUP, c.player);
                        c.interactionManager.clickSlot(handler.syncId, this.currentSlot, 0, SlotActionType.PICKUP, c.player);
                        this.currentSlot++;
                        this.timer.b();
                        return;
                     }

                     this.currentSlot++;
                  }

                  this.sorting = false;
               }
            }
         }
      } else {
         this.sorting = false;
      }
   }

   private boolean canMerge(ItemStack a, ItemStack b) {
      if (!a.isEmpty() && !b.isEmpty()) {
         if (a.getItem() != b.getItem()) {
            return false;
         } else {
            return a.getCount() >= a.getMaxCount() ? false : ItemStack.areItemsAndComponentsEqual(a, b);
         }
      } else {
         return false;
      }
   }

   private int compareStacks(ItemStack a, ItemStack b) {
      if (a.isEmpty() && b.isEmpty()) {
         return 0;
      }

      if (a.isEmpty()) {
         return 1;
      }

      if (b.isEmpty()) {
         return -1;
      }

      String idA = Registries.ITEM.getId(a.getItem()).toString();
      String idB = Registries.ITEM.getId(b.getItem()).toString();
      int cmp = idA.compareTo(idB);
      return cmp != 0 ? cmp : Integer.compare(b.getCount(), a.getCount());
   }

   private enum SortMode {
      MERGING,
      SORTING;
   }
}
