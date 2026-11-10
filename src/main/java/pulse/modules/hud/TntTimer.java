package pulse.modules.hud;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.math.MatrixStack;
import pulse.client.MinecraftContext;
import pulse.events.WorldRenderEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;

@ModuleInfo(a = "TNT Timer", b = "Shows explosion timer on TNT.", c = ModuleCategory.HUD)
public class TntTimer extends ClientModule implements MinecraftContext {
   @EventHandler
   public void onRender(WorldRenderEvent event) {
      if (c.world != null && c.player != null) {
         MatrixStack stack = event.matrices();
         Vec3d camPos = c.gameRenderer.getCamera().getCameraPos();

         for (Entity entity : c.world.getEntities()) {
            if (entity instanceof TntEntity tnt) {
               int fuse = tnt.getFuse();
               if (fuse > 0) {
                  float time = fuse / 20.0F;
                  String text = String.format("%.1fc", time);
                  float pulse = 1.0F;
                  if (time < 2.0F) {
                     pulse = (float)Math.sin(System.currentTimeMillis() % 1000L / 1000.0 * Math.PI * (2.0F + (2.0F - time) * 3.0F)) * 0.5F + 0.5F;
                  }

                  Color color = new Color(1.0F, time / 4.0F, time / 4.0F, pulse);
                  stack.push();
                  stack.translate(
                     tnt.getX() - camPos.x, tnt.getY() + 0.5 - camPos.y, tnt.getZ() - camPos.z
                  );
                  stack.multiply(c.gameRenderer.getCamera().getRotation());
                  stack.scale(-0.025F, -0.025F, 0.025F);
                  FontRenderer font = FontManager.REGULAR[20];
                  float width = font.a(text);
                  font.a(text, -width / 2.0F, 0.0, color, stack);
                  stack.pop();
               }
            }
         }
      }
   }
}
