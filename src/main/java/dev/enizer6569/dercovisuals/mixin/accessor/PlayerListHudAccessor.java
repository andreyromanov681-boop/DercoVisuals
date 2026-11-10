// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin.accessor;

import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.PlayerListHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListHud.class)
public interface PlayerListHudAccessor {
   @Accessor("header")
   Text getHeader();

   @Accessor("footer")
   Text getFooter();

   @Accessor("visible")
   boolean isVisible();

   @Accessor("visible")
   void setVisible(boolean var1);
}
