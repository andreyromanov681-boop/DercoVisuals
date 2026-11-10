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

@ModuleInfo(a = "Nimb", b = "Draws a halo above the player.", c = ModuleCategory.VISUALS)
public class Nimb extends ClientModule {
   private static final int SEGMENTS = 72;
   private final SliderSetting lineWidth = new SliderSetting("Line Width", 1.5F, 1.0F, 3.0F, 0.5F);
   private final SliderSetting radius = new SliderSetting("Radius", 0.28F, 0.18F, 0.55F, 0.02F);
   private final SliderSetting yOffset = new SliderSetting("Y Offset", 0.08F, -0.3F, 0.25F, 0.02F);
   private final SettingGroup colorGroup = new SettingGroup("Color");
   private final BooleanSetting useClientColor = new BooleanSetting("Use Client Color", true);
   private final ColorSetting color = new ColorSetting("Custom Color", Color.WHITE).a(() -> !this.useClientColor.a());
   private static final double MAX_RENDER_DISTANCE_SQ = 16384.0;
   private static final double[] cosTable = new double[73];
   private static final double[] sinTable = new double[73];

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      if (c.player != null && c.world != null && c.gameRenderer != null) {
         class_4598 bufferSource = worldRenderEvent.bufferSource();
         if (bufferSource != null) {
            MatrixStack stack = worldRenderEvent.matrices();
            Vec3d cam = c.gameRenderer.getCamera().getCameraPos();
            boolean firstPerson = c.options.getPerspective().isFirstPerson();
            double px = c.player.getX();
            double py = c.player.getY();
            double pz = c.player.getZ();
            float baseRadius = this.radius.a();
            Color colorN = this.n();
            int argb = -436207616 | colorN.getRed() << 16 | colorN.getGreen() << 8 | colorN.getBlue();
            float tickDelta = worldRenderEvent.tickDelta();
            float yOff = this.yOffset.a();
            stack.push();
            Matrix4f matrix = stack.peek().getPositionMatrix();
            VertexConsumer fill = bufferSource.getBuffer(ClientPipelines.FILL);
            float rInner = baseRadius * 0.95F;
            float rOuter = baseRadius * 1.05F;

            for (AbstractClientPlayerEntity p : c.world.getPlayers()) {
               if (!p.isInvisible() && (p != c.player || !firstPerson)) {
                  double dx = p.getX() - px;
                  double dy = p.getY() - py;
                  double dz = p.getZ() - pz;
                  if (!(dx * dx + dy * dy + dz * dz > 16384.0)) {
                     double cx = MathHelper.lerp(tickDelta, p.lastRenderX, p.getX()) - cam.x;
                     double cy = MathHelper.lerp(tickDelta, p.lastRenderY, p.getY())
                        - cam.y
                        + p.getEyeHeight(p.getPose())
                        + 0.18
                        + yOff;
                     double cz = MathHelper.lerp(tickDelta, p.lastRenderZ, p.getZ()) - cam.z;

                     for (int i = 0; i < 72; i++) {
                        double x1In = cx + cosTable[i] * rInner;
                        double z1In = cz + sinTable[i] * rInner;
                        double x1Out = cx + cosTable[i] * rOuter;
                        double z1Out = cz + sinTable[i] * rOuter;
                        double x2In = cx + cosTable[i + 1] * rInner;
                        double z2In = cz + sinTable[i + 1] * rInner;
                        double x2Out = cx + cosTable[i + 1] * rOuter;
                        double z2Out = cz + sinTable[i + 1] * rOuter;
                        fill.vertex(matrix, (float)x1Out, (float)cy, (float)z1Out).color(argb);
                        fill.vertex(matrix, (float)x1In, (float)cy, (float)z1In).color(argb);
                        fill.vertex(matrix, (float)x2In, (float)cy, (float)z2In).color(argb);
                        fill.vertex(matrix, (float)x2Out, (float)cy, (float)z2Out).color(argb);
                     }
                  }
               }
            }

            bufferSource.draw();
            stack.pop();
         }
      }
   }

   private Color n() {
      return this.useClientColor.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.color.a();
   }

   static {
      for (int i = 0; i <= 72; i++) {
         double a = i / 72.0 * Math.PI * 2.0;
         cosTable[i] = Math.cos(a);
         sinTable[i] = Math.sin(a);
      }
   }
}
