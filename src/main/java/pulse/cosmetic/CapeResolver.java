package pulse.cosmetic;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import pulse.auth.UserInfo;
import pulse.player.PlayerListCache;
import ru.pulse.Pulse;

public final class CapeResolver {
   private CapeResolver() {
   }

   public static Identifier a(AbstractClientPlayerEntity AbstractClientPlayerEntityVar) {
      Integer numResolveCapeId = resolveCapeId(AbstractClientPlayerEntityVar);
      return numResolveCapeId == null ? null : CapeTextureCache.getInstance().getCapeTexture(numResolveCapeId);
   }

   public static boolean b(AbstractClientPlayerEntity AbstractClientPlayerEntityVar) {
      return AbstractClientPlayerEntityVar != null
         && a(AbstractClientPlayerEntityVar) != null
         && !AbstractClientPlayerEntityVar.isInvisible()
         && AbstractClientPlayerEntityVar.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA;
   }

   private static Integer resolveCapeId(AbstractClientPlayerEntity AbstractClientPlayerEntityVar) {
      if (AbstractClientPlayerEntityVar == null) {
         return null;
      } else {
         MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
         if (MinecraftClientVarGetInstance.player != null
            && MinecraftClientVarGetInstance.player.getId() == AbstractClientPlayerEntityVar.getId()) {
            UserInfo userInfo = Pulse.getUserInfo();
            return userInfo == null ? null : userInfo.b();
         } else {
            return PlayerListCache.c().b(AbstractClientPlayerEntityVar.getName() == null ? null : AbstractClientPlayerEntityVar.getName().getString());
         }
      }
   }
}
