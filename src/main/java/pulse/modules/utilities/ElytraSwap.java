package pulse.modules.utilities;

import java.util.Optional;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import pulse.core.Bool;
import pulse.events.ClientTickEvent;
import pulse.events.KeyInputEvent;
import pulse.inventory.InventoryFinder;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.KeySetting;

@ModuleInfo(a = "Elytra Swap", b = "Автоматически меняет нагрудник на элитры при падении", c = ModuleCategory.UTILITIES)
public class ElytraSwap extends ClientModule {
   private final KeySetting e = new KeySetting("Кнопка свапа", -1);
   private boolean f = false;
   private int g = 0;
   private boolean h = false;
   private int i = 0;
   public static int a;
   public static boolean b;

   @EventHandler
   public void a(KeyInputEvent keyInputEvent) {
      if (c.player != null && c.world != null && c.currentScreen == null && keyInputEvent.a() == this.e.k() && keyInputEvent.c() == 1 && !this.f) {
         this.f = true;
         this.g = 0;
         this.h = false;
         this.i = 0;
      }
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null && this.f) {
         if (this.i > 0) {
            this.i--;
         } else {
            switch (this.g) {
               case 0:
                  if (!c.player.getAbilities().creativeMode) {
                     if (!this.q()) {
                        if (!this.o().isPresent()) {
                           c.player.sendMessage(Text.of(this.g() + ": Свап отменен: нет элитр."), true);
                           this.f = false;
                           return;
                        }
                     } else if (!this.p().isPresent()) {
                        c.player.sendMessage(Text.of(this.g() + ": Свап отменен: нет нагрудника."), true);
                        this.f = false;
                        return;
                     }

                     if (!(c.currentScreen instanceof InventoryScreen)) {
                        c.setScreen(new InventoryScreen(c.player));
                     }

                     this.i = 2;
                     this.g = 1;
                  } else {
                     this.g = 1;
                  }
                  break;
               case 1:
                  if (c.currentScreen instanceof InventoryScreen && !this.h) {
                     this.n();
                     this.h = true;
                     this.i = 2;
                     this.g = 2;
                  }
                  break;
               case 2:
                  if (c.currentScreen instanceof InventoryScreen) {
                     c.player.closeHandledScreen();
                  }

                  this.f = false;
                  this.g = 0;
            }
         }
      }
   }

   private void n() {
      if (this.q()) {
         Optional<Integer> optionalP = this.p();
         if (optionalP.isPresent()) {
            this.b(optionalP.get());
            c.player.sendMessage(Text.of(this.g() + ": Снял элитры."), true);
         }
      } else {
         Optional<Integer> optionalO = this.o();
         if (optionalO.isPresent()) {
            this.b(optionalO.get());
            c.player.sendMessage(Text.of(this.g() + ": Надел элитры."), true);
         }
      }
   }

   private void b(int i) {
      c.interactionManager.clickSlot(c.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, c.player);
      c.interactionManager.clickSlot(c.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, c.player);
      c.interactionManager.clickSlot(c.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, c.player);
   }

   private Optional<Integer> o() {
      return InventoryFinder.a(ItemStackVar -> Bool.from(ItemStackVar.getItem() != Items.ELYTRA ? 0 : 1), true, false);
   }

   private Optional<Integer> p() {
      return InventoryFinder.a(this::a, true, false);
   }

   private boolean a(ItemStack ItemStackVar) {
      if (ItemStackVar.getItem() == Items.ELYTRA) {
         return false;
      }

      EquippableComponent EquippableComponentVar = (EquippableComponent)ItemStackVar.get(DataComponentTypes.EQUIPPABLE);
      return Bool.from(EquippableComponentVar != null && EquippableComponentVar.comp_3174() == EquipmentSlot.CHEST ? 1 : 0);
   }

   private boolean q() {
      return Bool.from(c.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA ? 0 : 1);
   }

   @Override
   public void e() {
      super.e();
      this.f = false;
      this.g = 0;
      this.h = false;
      this.i = 0;
   }

   @Override
   public void f() {
      super.f();
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
