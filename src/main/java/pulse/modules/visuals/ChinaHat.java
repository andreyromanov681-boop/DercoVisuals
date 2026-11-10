package pulse.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.RotationAxis;
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

@ModuleInfo(a = "China Hat", b = "Рисует китайскую шляпу над головой", c = ModuleCategory.VISUALS)
public class ChinaHat extends ClientModule {
   private static final int SEGMENTS = 60;
   private static final float PI2 = (float) (Math.PI * 2);
   private final SliderSetting height = new SliderSetting("Высота", 0.3F, 0.1F, 0.6F, 0.05F);
   private final SliderSetting radius = new SliderSetting("Радиус", 0.5F, 0.1F, 1.0F, 0.05F);
   private final BooleanSetting followSneaking = new BooleanSetting("Учитывать приседание", true);
   private final SettingGroup colorGroup = new SettingGroup("Цвет");
   private final BooleanSetting useClientColor = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting color = new ColorSetting("Кастомный цвет", new Color(120, 80, 255)).a(() -> !this.useClientColor.a());

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      if (c.player != null && c.world != null && c.gameRenderer != null) {
         class_4598 bufferSource = worldRenderEvent.bufferSource();
         if (bufferSource != null) {
            MatrixStack stack = worldRenderEvent.matrices();
            Vec3d cam = c.gameRenderer.getCamera().getCameraPos();
            boolean firstPerson = c.options.getPerspective().isFirstPerson();
            Color baseColor = this.getColor();
            int centerArgb = -1275068416 | baseColor.getRed() << 16 | baseColor.getGreen() << 8 | baseColor.getBlue();
            int edgeArgb = 1342177280 | baseColor.getRed() << 16 | baseColor.getGreen() << 8 | baseColor.getBlue();
            int outlineArgb = 0xFF000000 | baseColor.getRed() << 16 | baseColor.getGreen() << 8 | baseColor.getBlue();
            float hatWidth = this.radius.a();
            float coneHeight = this.height.a();
            AbstractClientPlayerEntity p = c.player;
            if (p != null && !p.isInvisible() && !firstPerson) {
               float tickDelta = worldRenderEvent.tickDelta();
               double hx = MathHelper.lerp(tickDelta, p.lastRenderX, p.getX());
               double hy = MathHelper.lerp(tickDelta, p.lastRenderY, p.getY()) + p.getEyeHeight(p.getPose()) + 0.1;
               double hz = MathHelper.lerp(tickDelta, p.lastRenderZ, p.getZ());
               if (this.followSneaking.a() && p.isSneaking()) {
                  hy -= 0.18;
               }

               float yaw = p.getHeadYaw();
               stack.push();

               try {
                  stack.translate(hx - cam.x, hy - cam.y, hz - cam.z);
                  stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
                  Matrix4f mat = stack.peek().getPositionMatrix();
                  VertexConsumer fill1 = bufferSource.getBuffer(ClientPipelines.FILL);

                  for (int i = 0; i < 60; i++) {
                     float a1 = i * (float) (Math.PI * 2) / 60.0F;
                     float a2 = (i + 1) * (float) (Math.PI * 2) / 60.0F;
                     float x1 = -MathHelper.sin(a1) * hatWidth;
                     float z1 = MathHelper.cos(a1) * hatWidth;
                     float x2 = -MathHelper.sin(a2) * hatWidth;
                     float z2 = MathHelper.cos(a2) * hatWidth;
                     fill1.vertex(mat, 0.0F, coneHeight, 0.0F).color(centerArgb);
                     fill1.vertex(mat, x2, 0.0F, z2).color(edgeArgb);
                     fill1.vertex(mat, x1, 0.0F, z1).color(edgeArgb);
                     fill1.vertex(mat, 0.0F, coneHeight, 0.0F).color(centerArgb);
                  }

                  VertexConsumer lines = bufferSource.getBuffer(ClientPipelines.OUTLINE_NO);

                  for (int i = 0; i < 60; i++) {
                     float a1 = i * (float) (Math.PI * 2) / 60.0F;
                     float a2 = (i + 1) * (float) (Math.PI * 2) / 60.0F;
                     float x1 = -MathHelper.sin(a1) * hatWidth;
                     float z1 = MathHelper.cos(a1) * hatWidth;
                     float x2 = -MathHelper.sin(a2) * hatWidth;
                     float z2 = MathHelper.cos(a2) * hatWidth;
                     lines.vertex(mat, x1, 0.0F, z1).color(outlineArgb);
                     lines.vertex(mat, x2, 0.0F, z2).color(outlineArgb);
                  }
               } finally {
                  stack.pop();
               }

               bufferSource.draw();
            }
         }
      }
   }

   private Color getColor() {
      return this.useClientColor.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.color.a();
   }
}
