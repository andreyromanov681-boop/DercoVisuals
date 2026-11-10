package ru.pulse.mixin;

import net.minecraft.text.Text;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pulse.client.MinecraftContext;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.StreamerMode;

@Mixin(InGameHud.class)
public class ScoreboardMixin implements MinecraftContext {
   @Redirect(require = 0, method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/ScoreboardObjective;getDisplayName()Lnet/minecraft/Text;"))
   private Text hideScoreboardTitle(ScoreboardObjective ScoreboardObjectiveVar) {
      StreamerMode streamerMode = ModuleRegistry.STREAMER_MODE;
      if (streamerMode.k() && streamerMode.hideServerNumber.k()) {
         if (c == null || c.getCurrentServerEntry() == null) {
            return Text.literal("pulsevisuals.pro");
         }

         if (!c.getCurrentServerEntry().address.toLowerCase().contains("holyworld")) {
            return Text.literal("pulsevisuals.pro");
         }
      }

      return ScoreboardObjectiveVar.getDisplayName();
   }
}
