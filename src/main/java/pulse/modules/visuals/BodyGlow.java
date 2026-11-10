package pulse.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import pulse.events.WorldRenderEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.render.system.ClientPipelines;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Body Glow", b = "Рисует мягкое свечение ауры вокруг игроков", c = ModuleCategory.VISUALS)
public class BodyGlow extends ClientModule {
   private final SliderSetting size = new SliderSetting("Размер", 2.3F, 0.5F, 5.0F, 0.1F);
   private final SliderSetting transparency = new SliderSetting("Прозрачность", 1.0F, 0.0F, 1.0F, 0.05F);
   private final SettingGroup colorGroup = new SettingGroup("Цвет");
   private final BooleanSetting useClientColor = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting customColor = new ColorSetting("Цвет", new Color(255, 255, 255)).a(() -> !this.useClientColor.a());

   private Color getColor() {
      return this.useClientColor.a() ? new Color(ModuleRegistry.CLIENT_COLOR.o()) : this.customColor.getColor();
   }

   @EventHandler
   public void onRenderWorld(WorldRenderEvent event) {
      if (c.player != null && c.world != null && c.gameRenderer != null) {
         class_4598 bufferSource = event.bufferSource();
         if (bufferSource != null) {
            MatrixStack stack = event.matrices();
            Vec3d cam = c.gameRenderer.getCamera().getCameraPos();
            float tickDelta = event.tickDelta();
            Color baseColor = this.getColor();
            float opacity = MathHelper.clamp(this.transparency.a(), 0.0F, 1.0F);
            if (!(opacity <= 0.01F)) {
               float radiusVal = this.size.a() * 0.35F;
               VertexConsumer fill = bufferSource.getBuffer(ClientPipelines.FILL);
               int SEGMENTS = 24;
               double PI2 = Math.PI * 2;

               for (AbstractClientPlayerEntity player : c.world.getPlayers()) {
                  if (player != null && !player.isInvisible() && (player != c.player || !c.options.getPerspective().isFirstPerson())) {
                     double px = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX()) - cam.x;
                     double py = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY()) - cam.y;
                     double pz = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ()) - cam.z;
                     float pHeight = player.getHeight();
                     if (player.isSneaking()) {
                        pHeight -= 0.18F;
                     }

                     stack.push();
                     stack.translate(px, py, pz);
                     Matrix4f mat = stack.peek().getPositionMatrix();
                     int layers = 3;

                     for (int l = 1; l <= layers; l++) {
                        float layerRadius = radiusVal * ((float)l / layers);
                        float layerAlpha = opacity * (1.0F - (l - 1) * 0.28F);
                        int coreAlpha = (int)(layerAlpha * 140.0F);
                        int edgeAlpha = (int)(layerAlpha * 0.0F);
                        int coreArgb = coreAlpha << 24 | baseColor.getRed() << 16 | baseColor.getGreen() << 8 | baseColor.getBlue();
                        int edgeArgb = edgeAlpha << 24 | baseColor.getRed() << 16 | baseColor.getGreen() << 8 | baseColor.getBlue();

                        for (int i = 0; i < SEGMENTS; i++) {
                           float a1 = (float)(i * PI2 / SEGMENTS);
                           float a2 = (float)((i + 1) * PI2 / SEGMENTS);
                           float x1 = -MathHelper.sin(a1) * layerRadius;
                           float z1 = MathHelper.cos(a1) * layerRadius;
                           float x2 = -MathHelper.sin(a2) * layerRadius;
                           float z2 = MathHelper.cos(a2) * layerRadius;
                           float yBottom = -0.1F;
                           float yMid = pHeight * 0.5F;
                           float yTop = pHeight + 0.2F;
                           fill.vertex(mat, x1, yBottom, z1).color(edgeArgb);
                           fill.vertex(mat, x2, yBottom, z2).color(edgeArgb);
                           fill.vertex(mat, x2, yMid, z2).color(coreArgb);
                           fill.vertex(mat, x1, yMid, z1).color(coreArgb);
                           fill.vertex(mat, x1, yMid, z1).color(coreArgb);
                           fill.vertex(mat, x2, yMid, z2).color(coreArgb);
                           fill.vertex(mat, x2, yTop, z2).color(edgeArgb);
                           fill.vertex(mat, x1, yTop, z1).color(edgeArgb);
                        }
                     }

                     stack.pop();
                  }
               }
            }
         }
      }
   }
}
