// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin.accessor;

import java.util.Map;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCooldownManager.class)
public interface ItemCooldownManagerAccessor {
   @Accessor("entries")
   Map<Identifier, ?> pulse$getEntries();

   @Accessor("tick")
   int pulse$getTick();
}
