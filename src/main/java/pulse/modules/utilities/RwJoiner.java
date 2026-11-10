package pulse.modules.utilities;

import java.util.List;
import java.util.Optional;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import pulse.events.ClientTickEvent;
import pulse.events.MouseButtonEvent;
import pulse.events.PacketEvent;
import pulse.events.WorldChangeEvent;
import pulse.media.chat.ChatMessages;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.TokenSetting;
import pulse.util.ElapsedTimer;
import ru.pulse.mixin.accessor.PlayerListHudAccessor;

@ModuleInfo(a = "RW Joiner", b = "Автоматически подключается к выбранному грифу ReallyWorld", c = ModuleCategory.UTILITIES)
public class RwJoiner extends ClientModule {
   private static final String f = "ГРИФЕРСКОЕ ВЫЖИВАНИЕ (1.16.5-1.20.4)";
   private static final int g = 5000;
   private List<ItemStack> n;
   private static final long q = 100L;
   private static final long r = 1000L;
   private static final long v = 3000L;
   public static int a;
   public static boolean b;
   private final TokenSetting e = new TokenSetting("Номер грифа", TokenSetting.TokenType.NUMBER, "1", "Введите номер");
   private final ElapsedTimer h = new ElapsedTimer();
   private int i = -1;
   private String j = "";
   private boolean k = false;
   private boolean l = false;
   private boolean m = false;
   private int o = 0;
   private long p = 0L;
   private boolean s = false;
   private long t = 0L;
   private long u = 0L;

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.player != null && c.world != null && this.q()) {
         if (this.l) {
            if (this.h.a() >= 5000L) {
               this.l = false;
               this.p();
               this.t = 0L;
               this.n();
            }
         } else if (!this.k && !this.s) {
            if (this.t > 0L && System.currentTimeMillis() - this.t > 1000L) {
               this.t = 0L;
            }

            this.n();
         }
      }
   }

   private void n() {
      if (System.currentTimeMillis() - this.p >= 100L && (this.t <= 0L || System.currentTimeMillis() - this.t >= 1000L)) {
         if (this.u > 0L && System.currentTimeMillis() - this.u < 3000L) {
            return;
         }

         Optional<Integer> optionalA = this.a(Items.COMPASS);
         if (optionalA.isPresent()) {
            int iIntValue = optionalA.get();
            if (c.player.getInventory().getSelectedSlot() != iIntValue) {
               c.player.getInventory().setSelectedSlot(iIntValue);
               return;
            }

            c.interactionManager.interactItem(c.player, Hand.MAIN_HAND);
            this.p = System.currentTimeMillis();
            this.t = System.currentTimeMillis();
            this.s = true;
         }
      }
   }

   private Optional<Integer> a(Item ItemVar) {
      for (int i = 0; i < 9; i++) {
         ItemStack ItemStackVarGetStack = c.player.getInventory().getStack(i);
         if (!ItemStackVarGetStack.isEmpty() && ItemStackVarGetStack.getItem() == ItemVar) {
            return Optional.of(i);
         }
      }

      return Optional.empty();
   }

   private void o() {
      if (this.n != null && !this.n.isEmpty() && System.currentTimeMillis() - this.p >= 100L) {
         if (this.j.contains("Выбор сервера") && !this.m) {
            int iB = this.b("ГРИФЕРСКОЕ ВЫЖИВАНИЕ (1.16.5-1.20.4)");
            if (iB == -1) {
               this.p();
               this.d();
            } else {
               c.interactionManager.clickSlot(this.i, iB, 0, SlotActionType.PICKUP, c.player);
               this.m = true;
               this.p = System.currentTimeMillis();
            }
         } else {
            if (this.j.contains("Выбор мира грифа") && this.m && this.e.d()) {
               try {
                  int iB2 = this.b("ГРИФ #" + Integer.parseInt(this.e.k()));
                  if (iB2 == -1) {
                     this.p();
                     return;
                  }

                  c.interactionManager.clickSlot(this.i, iB2, 0, SlotActionType.PICKUP, c.player);
                  int i = this.o;
                  this.o = (i | 1) + (i & 1);
                  this.p = System.currentTimeMillis();
                  this.u = System.currentTimeMillis();
                  this.p();
               } catch (NumberFormatException var3) {
               }
            }
         }
      }
   }

   private void a(String str) {
      ChatMessages.a((Object)(this.g() + ": " + str));
   }

   private void p() {
      this.i = -1;
      this.j = "";
      this.k = false;
      this.n = null;
      this.m = false;
      this.s = false;
      this.t = 0L;
   }

   @EventHandler
   public void a(PacketEvent packetEvent) {
      if (packetEvent.d() instanceof OpenScreenS2CPacket) {
         OpenScreenS2CPacket OpenScreenS2CPacketVarD = (OpenScreenS2CPacket)packetEvent.d();
         String strMethod_5392 = Formatting.strip(OpenScreenS2CPacketVarD.getName().getString());
         if (strMethod_5392.contains("Выбор сервера") || strMethod_5392.contains("Выбор мира грифа")) {
            this.i = OpenScreenS2CPacketVarD.getSyncId();
            this.j = strMethod_5392;
            this.k = true;
            this.s = false;
            packetEvent.b();
         }
      } else {
         if (packetEvent.d() instanceof InventoryS2CPacket) {
            InventoryS2CPacket InventoryS2CPacketVarD = (InventoryS2CPacket)packetEvent.d();
            if (InventoryS2CPacketVarD.comp_3837() == this.i || this.i != -1 && this.k) {
               this.n = InventoryS2CPacketVarD.comp_3839();
               this.o();
               packetEvent.b();
               return;
            }
         }

         if (packetEvent.d() instanceof CloseScreenS2CPacket && ((CloseScreenS2CPacket)packetEvent.d()).getSyncId() == this.i) {
            this.p();
            packetEvent.b();
         } else {
            String strStrip;
            if (packetEvent.d() instanceof GameMessageS2CPacket && (strStrip = Formatting.strip(((GameMessageS2CPacket)packetEvent.d()).comp_763().getString())) != null) {
               if (strStrip.contains("Не удалось подключиться к серверу")
                  || strStrip.contains("Подождите несколько секунд перед повторым подключением")
                  || strStrip.contains("сервер переполнен")
                  || strStrip.contains("Unable to connect")) {
                  if (!strStrip.contains("Unable to connect")) {
                     packetEvent.b();
                  } else if (b) {
                  }

                  this.l = true;
                  this.h.b();
                  this.p();
               }
            }
         }
      }
   }

   @EventHandler
   public void a(WorldChangeEvent worldChangeEvent) {
      if (this.l() && !this.l && this.e.d()) {
         try {
            this.a("Успешно подключено к Гриф #" + Integer.parseInt(this.e.k()));
            this.d();
         } catch (NumberFormatException var3) {
         }
      }
   }

   @EventHandler
   public void a(MouseButtonEvent mouseButtonEvent) {
   }

   private boolean q() {
      if (c.inGameHud != null && c.inGameHud.getPlayerListHud() != null) {
         try {
            Text header = ((PlayerListHudAccessor)c.inGameHud.getPlayerListHud()).getHeader();
            if (header == null) {
               return false;
            }

            String strStrip = Formatting.strip(header.getString());
            return strStrip != null && strStrip.contains("Вы находитесь в: Lobby");
         } catch (Exception e) {
            return false;
         }
      } else {
         return false;
      }
   }

   private int b(String str) {
      if (this.n == null) {
         return -1;
      }

      for (int i = 0; i < this.n.size(); i++) {
         ItemStack ItemStackVar = this.n.get(i);
         String strStrip;
         if (!ItemStackVar.isEmpty() && (strStrip = Formatting.strip(ItemStackVar.getName().getString())) != null && strStrip.contains(str)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void e() {
      super.e();
      if (c.player == null || c.world == null) {
         this.d();
      } else if (!this.q()) {
         this.d();
      } else {
         this.l = false;
         this.k = false;
         this.o = 0;
         this.s = false;
         this.p = 0L;
         this.t = 0L;
         this.u = 0L;
         this.h.b();
      }
   }

   @Override
   public void f() {
      super.f();
      this.p();
      this.l = false;
      this.o = 0;
      this.s = false;
      this.u = 0L;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
