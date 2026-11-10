package ru.pulse.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.events.EventBusService;
import pulse.events.PacketEvent;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
   @Inject(require = 0, method = "channelRead0", at = @At("HEAD"), cancellable = true)
   private void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet<?> PacketVar, CallbackInfo callbackInfo) {
      PacketEvent packetEvent = new PacketEvent(PacketVar, PacketEvent.MessageDirection.RECIEVE);
      EventBusService.EVENT_BUS.post(packetEvent);
      if (packetEvent.c()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "send", at = @At("HEAD"), cancellable = true)
   private void onSendPacket(Packet<?> PacketVar, CallbackInfo callbackInfo) {
      PacketEvent packetEvent = new PacketEvent(PacketVar, PacketEvent.MessageDirection.SEND);
      EventBusService.EVENT_BUS.post(packetEvent);
      if (packetEvent.c()) {
         callbackInfo.cancel();
      }
   }
}
