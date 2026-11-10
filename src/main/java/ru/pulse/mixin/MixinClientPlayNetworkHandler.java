package ru.pulse.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.events.EventBusService;
import pulse.events.GameJoinEvent;
import pulse.events.TotemPopEvent;
import pulse.events.WorldClearEvent;
import pulse.player.PlayerListCache;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
   private static final Map<UUID, ItemStack> cachedTotems = new HashMap<>();

   @Inject(require = 0, method = "onEntityStatus", at = @At("HEAD"))
   private void onEntityStatus(EntityStatusS2CPacket EntityStatusS2CPacketVar, CallbackInfo callbackInfo) {
      if (EntityStatusS2CPacketVar.getStatus() == 35
         && EntityStatusS2CPacketVar.getEntity(MinecraftClient.getInstance().world) instanceof LivingEntity LivingEntityVar) {
         EventBusService.EVENT_BUS.post(new TotemPopEvent(LivingEntityVar, this.getCachedTotem(LivingEntityVar)));
      }
   }

   private ItemStack getCachedTotem(LivingEntity LivingEntityVar) {
      UUID uuidGetUuid = LivingEntityVar.getUuid();
      ItemStack ItemStackVarGetMainHandStack = LivingEntityVar.getMainHandStack();
      ItemStack ItemStackVarGetOffHandStack = LivingEntityVar.getOffHandStack();
      ItemStack ItemStackVarCopy = null;
      if (ItemStackVarGetMainHandStack.getItem() == Items.TOTEM_OF_UNDYING) {
         ItemStackVarCopy = ItemStackVarGetMainHandStack.copy();
      } else if (ItemStackVarGetOffHandStack.getItem() == Items.TOTEM_OF_UNDYING) {
         ItemStackVarCopy = ItemStackVarGetOffHandStack.copy();
      }

      if (ItemStackVarCopy != null) {
         cachedTotems.put(uuidGetUuid, ItemStackVarCopy);
         return ItemStackVarCopy;
      } else {
         ItemStack ItemStackVarRemove = cachedTotems.remove(uuidGetUuid);
         return ItemStackVarRemove != null ? ItemStackVarRemove : new ItemStack(Items.TOTEM_OF_UNDYING);
      }
   }

   @Inject(require = 0, method = "onPlayerList", at = @At("RETURN"))
   private void onPlayerList(PlayerListS2CPacket PlayerListS2CPacketVar, CallbackInfo callbackInfo) {
      PlayerListCache.c().a(PlayerListS2CPacketVar);
   }

   @Inject(require = 0, method = "onGameJoin", at = @At("TAIL"))
   public void onGameJoin(GameJoinS2CPacket GameJoinS2CPacketVar, CallbackInfo callbackInfo) {
      EventBusService.EVENT_BUS.post(new GameJoinEvent());
      PlayerListCache.c().a();
   }

   @Inject(require = 0, method = "clearWorld", at = @At("HEAD"))
   public void onClearWorld(CallbackInfo callbackInfo) {
      PlayerListCache.c().b();
      EventBusService.EVENT_BUS.post(new WorldClearEvent());
   }
}
