package pulse.modules.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import pulse.events.AttackEntityEvent;
import pulse.events.TotemPopEvent;
import pulse.media.chat.ChatMessages;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.util.ElapsedTimer;

@ModuleInfo(a = "Totem Tracker", b = "Отслеживает использованные тотемы игроков в чате", c = ModuleCategory.UTILITIES)
public class TotemTracker extends ClientModule {
   private static final long TARGET_MEMORY_MS = 5000L;
   private final BooleanSetting onlyAttacked = new BooleanSetting("Только атакованные цели", false);
   private final Map<UUID, ElapsedTimer> attackedTargets = new HashMap<>();
   private int lastNotificationAge = -1;

   @EventHandler
   public void a(AttackEntityEvent attackEntityEvent) {
      if (c.player != null) {
         if (!(attackEntityEvent.a() instanceof LivingEntity ClientPlayerEntityVar)) {
            return;
         }

         if (ClientPlayerEntityVar == c.player) {
            return;
         }

         this.attackedTargets.put(ClientPlayerEntityVar.getUuid(), new ElapsedTimer());
      }
   }

   @EventHandler
   public void a(TotemPopEvent totemPopEvent) {
      if (c.player != null) {
         LivingEntity ClientPlayerEntityVarA = totemPopEvent.a();
         if (ClientPlayerEntityVarA != c.player) {
            if (this.onlyAttacked.get()) {
               ElapsedTimer elapsedTimer = this.attackedTargets.get(ClientPlayerEntityVarA.getUuid());
               if (elapsedTimer == null || elapsedTimer.hasElapsed(5000L)) {
                  this.removeExpiredTargets();
                  return;
               }
            }

            this.notifyTotemPop(ClientPlayerEntityVarA, totemPopEvent.b());
            this.removeExpiredTargets();
         }
      }
   }

   private void notifyTotemPop(LivingEntity LivingEntityVar, ItemStack ItemStackVar) {
      int i = c.player.age;
      if (i != this.lastNotificationAge) {
         this.lastNotificationAge = i;
         boolean isEnchanted = !EnchantmentHelper.getEnchantments(ItemStackVar).isEmpty();
         String totemType = isEnchanted ? "§dзачарованный§f" : "§eобычный§f";
         ChatMessages.a((Object)("§6[Totem Tracker] §fИгрок §c" + LivingEntityVar.getName().getString() + " §fиспользовал " + totemType + " тотем."));
      }
   }

   private void removeExpiredTargets() {
      this.attackedTargets.entrySet().removeIf(entry -> entry.getValue().hasElapsed(5000L));
   }

   @Override
   public void f() {
      super.f();
      this.attackedTargets.clear();
      this.lastNotificationAge = -1;
   }
}
