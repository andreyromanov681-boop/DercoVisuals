// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/ItemCooldownManager$class_1797")
public interface ItemCooldownEntryAccessor {
   @Accessor("comp_3083")
   int pulse$getStartTick();

   @Accessor("comp_3084")
   int pulse$getEndTick();
}
