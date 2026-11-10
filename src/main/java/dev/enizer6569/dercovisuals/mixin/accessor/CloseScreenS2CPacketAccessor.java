// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin.accessor;

import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CloseScreenS2CPacket.class)
public interface CloseScreenS2CPacketAccessor {
   @Accessor("syncId")
   int getSyncId();
}
