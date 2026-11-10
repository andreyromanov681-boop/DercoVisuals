// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.gl.ShaderProgramKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.TabCustomizer;
import dev.enizer6569.dercovisuals.player.DercoVisualsPlayerTracker;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.icons.IconTextureRegistry;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   private Text header;
   @Unique
   private static final Logger PULSE_LOG = LogManager.getLogger("DercoVisualsTab");
   @Unique
   private static final int ICON_SIZE = 8;
   @Unique
   private static final int ICON_PADDING = 2;
   @Unique
   private final Set<String> tabNames = new HashSet<>();
   @Unique
   private int tabNamesTick = -1;
   @Shadow
   private Text footer;

   @Unique
   private Set<String> getTabNames() {
      if (1273930702 - this.tabNamesTick > 20) {
         this.tabNamesTick = 1273930702;
         this.tabNames.clear();
         if (this.client.player != null && this.client.player.networkHandler != null) {
            for (PlayerListEntry PlayerListEntryVar : this.client.player.networkHandler.getListedPlayerListEntries()) {
               if (PlayerListEntryVar.getProfile() != null && PlayerListEntryVar.getProfile().name() != null) {
                  this.tabNames.add(PlayerListEntryVar.getProfile().name());
               }
            }
         }
      }

      return this.tabNames;
   }

   @Redirect(
      require = 0,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/DrawContext;drawTextWithShadow(Lnet/minecraft/TextRenderer;Lnet/minecraft/Text;III)V")
   )
   private void redirectDrawText(DrawContext DrawContextVar, TextRenderer TextRendererVar, Text TextVar, int i, int i2, int i3) {
      String string = TextVar.getString();
      PULSE_LOG.debug("[tab draw] x={} y={} raw=\"{}\"", i, i2, string);
      String strFindDercoVisualsPlayer = this.findDercoVisualsPlayer(string);
      if (strFindDercoVisualsPlayer == null) {
         DrawContextVar.drawTextWithShadow(TextRendererVar, TextVar, i, i2, i3);
      } else {
         PULSE_LOG.debug("[tab] Drawing icon for {} at x={} y={}", strFindDercoVisualsPlayer, i, i2);
         this.renderLogo(DrawContextVar, i, i2);
         DrawContextVar.drawTextWithShadow(TextRendererVar, TextVar, i + 8 + 2, i2, i3);
      }
   }

   @Unique
   private String findDercoVisualsPlayer(String str) {
      if (str != null && !str.isBlank()) {
         String lowerCase = str.toLowerCase();

         for (String str2 : this.getTabNames()) {
            if (str2.length() >= 3 && lowerCase.contains(str2.toLowerCase()) && DercoVisualsPlayerTracker.get().has(str2)) {
               return str2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Redirect(require = 0, method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/ClientConnection;isEncrypted()Z"))
   private boolean redirectIsEncrypted(ClientConnection connection) {
      TabCustomizer tc = ModuleRegistry.TAB_CUSTOMIZER;
      return tc != null && tc.k() ? tc.showHeads.get() : connection != null && connection.isEncrypted();
   }

   @Redirect(
      require = 0,
      method = "render",
      at = @At(value = "FIELD", target = "Lnet/minecraft/PlayerListHud;footer:Lnet/minecraft/Text;", opcode = 180)
   )
   private Text getModifiedFooter(PlayerListHud PlayerListHudVar) {
      TabCustomizer tc = ModuleRegistry.TAB_CUSTOMIZER;
      return tc != null && tc.k() && tc.hideFooter.get() ? null : this.footer;
   }

   @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true, require = 0)
   private void onRenderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
      TabCustomizer tc = ModuleRegistry.TAB_CUSTOMIZER;
      if (tc != null && tc.k()) {
         String mode = tc.pingDisplay.selectedValue();
         if ("Скрыть".equals(mode)) {
            ci.cancel();
            return;
         }

         if ("Цифры".equals(mode)) {
            int latency = entry != null ? entry.getLatency() : 0;
            String pingStr = String.valueOf(latency);
            int color = 65407;
            if (latency > 150) {
               color = 16729344;
            } else if (latency > 80) {
               color = 16776960;
            }

            TextRenderer tr = this.client.textRenderer;
            context.drawTextWithShadow(tr, pingStr, x + width - tr.getWidth(pingStr), y + 1, color);
            ci.cancel();
            return;
         }
      }
   }

   @Unique
   private void renderLogo(DrawContext DrawContextVar, int i, int i2) {
      Identifier IdentifierVar = IconTextureRegistry.get("logo");
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_TEX);
      RenderSystemHelper.setShaderTexture(0, IdentifierVar);
      RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      DrawContextVar.drawTexture(RenderPipelines.GUI_TEXTURED, IdentifierVar, i, i2, 0.0F, 0.0F, 8, 8, 8, 8);
      RenderSystemHelper.disableBlend();
   }
}
