package pulse.modules.utilities;

import java.util.Optional;
import java.util.function.Predicate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.PlayerActionC2SPacket.class_2847;
import pulse.events.ClientTickEvent;
import pulse.events.KeyInputEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.KeySetting;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Item Swap", b = "Автоматически меняет предмет в левой руке при нажатии клавиши", c = ModuleCategory.UTILITIES)
public class ItemSwap extends ClientModule {
   private final ModeSetting e = new ModeSetting("Менять с", new String[]{"Сфера", "Тотем"}, "Сфера");
   private final ModeSetting f = new ModeSetting("Менять на", new String[]{"Сфера", "Тотем"}, "Тотем");
   private final KeySetting g = new KeySetting("Кнопка свапа", -1);
   private boolean h = false;
   private boolean i = false;
   private int j = 0;
   private int k = 0;
   private boolean l = false;
   private int m = 0;
   private int n = 0;
   private String o = "";
   private int p = -1;
   private int q = -1;
   public static int a;
   public static boolean b;
   private long stopUntilMs = 0L;

   @EventHandler
   public void a(KeyInputEvent keyInputEvent) {
      if (c.player != null && c.world != null && c.currentScreen == null && keyInputEvent.a() == this.g.a() && keyInputEvent.c() == 1) {
         this.stopUntilMs = System.currentTimeMillis() + 260L;
         this.o = this.q();
         Optional<Integer> optionalA = this.a(ItemStackVar -> this.a(ItemStackVar, this.o));
         if (optionalA.isPresent()) {
            this.b(optionalA.get());
            return;
         }

         if (!this.a(this.o)) {
            c.player.sendMessage(Text.literal("§cНе найден предмет для свапа!"), true);
            return;
         }

         this.h = true;
         this.j = 0;
         this.l = false;
         this.m = 0;
      }
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null) {
         if (System.currentTimeMillis() < this.stopUntilMs) {
            c.player.setVelocity(0.0, c.player.getVelocity().y, 0.0);
            if (c.options != null) {
               c.options.forwardKey.setPressed(false);
               c.options.backKey.setPressed(false);
               c.options.leftKey.setPressed(false);
               c.options.rightKey.setPressed(false);
               c.options.jumpKey.setPressed(false);
               c.options.sprintKey.setPressed(false);
            }
         }

         if (this.i) {
            this.n();
         }

         if (this.h) {
            this.o();
         }
      }
   }

   private void b(int i) {
      this.p = c.player.getInventory().getSelectedSlot();
      this.q = i;
      this.i = true;
      this.k = 0;
      this.n = 0;
   }

   private void n() {
      if (this.n > 0) {
         this.n--;
      }

      switch (this.k) {
         case 0:
            c.player.getInventory().setSelectedSlot(this.q);
            this.n = 2;
            this.k = 1;
            break;
         case 1:
            c.player.networkHandler.sendPacket(new PlayerActionC2SPacket(class_2847.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            this.n = 2;
            this.k = 2;
            break;
         case 2:
            c.player.getInventory().setSelectedSlot(this.p);
            c.player.sendMessage(Text.literal("§fСвапнул на ").append(c.player.getOffHandStack().getName()), true);
            this.i = false;
            this.k = 0;
      }
   }

   private void o() {
      Optional<Integer> optionalB = this.b(ItemStackVar -> this.a(ItemStackVar, this.o));
      if (!optionalB.isPresent()) {
         c.player.sendMessage(Text.literal("§cНе найден предмет для свапа!"), true);
      } else {
         this.c(optionalB.get());
         c.player.sendMessage(Text.literal("§fСвапнул на ").append(c.player.getOffHandStack().getName()), true);
      }

      this.h = false;
      this.j = 0;
   }

   private void p() {
      Optional<Integer> optionalB = this.b(ItemStackVar -> this.a(ItemStackVar, this.o));
      if (optionalB.isPresent()) {
         this.c(optionalB.get());
         c.player.sendMessage(Text.literal("§fСвапнул на ").append(c.player.getOffHandStack().getName()), true);
      }
   }

   private void c(int i) {
      c.interactionManager.clickSlot(c.player.currentScreenHandler.syncId, i, 40, SlotActionType.SWAP, c.player);
   }

   private String q() {
      ItemStack ItemStackVarGetOffHandStack = c.player.getOffHandStack();
      String strD = this.e.d();
      return !this.a(ItemStackVarGetOffHandStack, strD) ? strD : this.f.d();
   }

   private boolean a(String str) {
      return "Тотем".equals(str) || "Сфера".equals(str);
   }

   private boolean a(ItemStack ItemStackVar, String str) {
      if (ItemStackVar.isEmpty()) {
         return false;
      } else if ("Сфера".equals(str)) {
         return ItemStackVar.getItem() == Items.PLAYER_HEAD;
      } else {
         return "Тотем".equals(str) ? ItemStackVar.getItem() == Items.TOTEM_OF_UNDYING : false;
      }
   }

   private Optional<Integer> a(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 9; i++) {
         if (predicate.test(c.player.getInventory().getStack(i))) {
            return Optional.of(i);
         }
      }

      return Optional.empty();
   }

   private Optional<Integer> b(Predicate<ItemStack> predicate) {
      for (int i = 9; i < 36; i++) {
         if (predicate.test(c.player.getInventory().getStack(i))) {
            return Optional.of(i);
         }
      }

      return Optional.empty();
   }

   @Override
   public void e() {
      super.e();
      this.h = false;
      this.i = false;
      this.j = 0;
      this.k = 0;
      this.l = false;
      this.m = 0;
      this.n = 0;
   }

   @Override
   public void f() {
      super.f();
      if (c.currentScreen instanceof InventoryScreen) {
         c.player.closeHandledScreen();
      }

      this.i = false;
      this.h = false;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
