// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
   @Accessor("session")
   Session getSession();

   @Accessor("session")
   void setSession(Session var1);

   @Invoker("doItemUse")
   void invokeDoItemUse();

   @Invoker("doAttack")
   boolean invokeDoAttack();
}
