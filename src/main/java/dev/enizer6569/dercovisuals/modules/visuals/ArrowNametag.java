// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.DrawContext;
import dev.enizer6569.dercovisuals.events.HudRenderPostEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;
import dev.enizer6569.dercovisuals.render.world.WorldToScreen;

@ModuleInfo(a = "Arrow Nametag", b = "Показывает названия стрел, прилетевших в землю", c = ModuleCategory.VISUALS)
public class ArrowNametag extends ClientModule {
   private String getArrowName(PersistentProjectileEntity arrow) {
      if (arrow.getCustomName() != null) {
         return arrow.getCustomName().getString();
      }

      ItemStack stack = arrow.getItemStack();
      return stack != null && !stack.isEmpty() ? stack.getName().getString() : "Стрела";
   }

   @EventHandler
   private void onHudRender(HudRenderPostEvent event) {
      if (c.player != null && c.world != null) {
         DrawContext ctx = Renderer2DImpl.currentDrawContext;
         if (ctx != null) {
            FontRenderer font = FontManager.REGULAR[14];

            for (Entity entity : c.world.getEntities()) {
               if (entity instanceof PersistentProjectileEntity arrow && (arrow.isOnGround() || arrow.getVelocity().lengthSquared() < 0.5 || arrow.age > 10)) {
                  Vec3d arrowPos = new Vec3d(arrow.getX(), arrow.getY() + 0.3, arrow.getZ());
                  Vec3d screenPos = WorldToScreen.a(arrowPos);
                  if (screenPos != null && WorldToScreen.b(screenPos)) {
                     float sx = (float)screenPos.x;
                     float sy = (float)screenPos.y;
                     String text = "★ " + this.getArrowName(arrow);
                     float textWidth = font != null ? font.a(text) : c.textRenderer.getWidth(text);
                     int bgWidth = (int)textWidth + 8;
                     int bgHeight = 14;
                     int bgX = (int)(sx - bgWidth / 2.0F);
                     int bgY = (int)(sy - bgHeight / 2.0F);
                     ctx.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, -1879048192);
                     ctx.fill(bgX, bgY, bgX + bgWidth, bgY + 1, -2080704);
                     ctx.fill(bgX, bgY + bgHeight - 1, bgX + bgWidth, bgY + bgHeight, -2080704);
                     ctx.fill(bgX, bgY, bgX + 1, bgY + bgHeight, -2080704);
                     ctx.fill(bgX + bgWidth - 1, bgY, bgX + bgWidth, bgY + bgHeight, -2080704);
                     if (font != null) {
                        font.a(text, sx - textWidth / 2.0F, sy - 4.5F, Color.WHITE);
                     } else {
                        ctx.drawText(c.textRenderer, text, (int)(sx - textWidth / 2.0F), (int)(sy - 4.0F), -1, true);
                     }
                  }
               }
            }
         }
      }
   }
}
