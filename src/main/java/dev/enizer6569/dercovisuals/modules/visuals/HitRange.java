// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import dev.enizer6569.dercovisuals.events.WorldRenderEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.render.system.ClientPipelines;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ColorSetting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Hit Range", b = "Отображает радиус досягаемости атаки вокруг игрока", c = ModuleCategory.VISUALS)
public class HitRange extends ClientModule {
   private final BooleanSetting ownCircle = new BooleanSetting("Свой круг", true);
   private final BooleanSetting otherCircles = new BooleanSetting("Чужие круги", true);
   private final SettingGroup renderGroup = new SettingGroup("Отображение");
   private final BooleanSetting fill = new BooleanSetting("Заливка", true);
   private final SliderSetting fillOpacity = new SliderSetting("Прозрачность заливки", 0.25F, 0.0F, 1.0F, 0.05F);
   private final SliderSetting lineWidth = new SliderSetting("Толщина линии", 2.0F, 0.5F, 5.0F, 0.5F);
   private final SliderSetting heightAboveFloor = new SliderSetting("Высота над полом", 0.02F, 0.0F, 0.5F, 0.01F);
   private final SettingGroup colorGroup = new SettingGroup("Цвет");
   private final ColorSetting targetInRadiusColor = new ColorSetting("Цель в радиусе", new Color(0, 255, 60));
   private final ColorSetting noTargetColor = new ColorSetting("Нет цели", new Color(255, 40, 40));

   private boolean isArmored(AbstractClientPlayerEntity player) {
      return player == null
         ? false
         : !player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
            || !player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
            || !player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
            || !player.getEquippedStack(EquipmentSlot.FEET).isEmpty();
   }

   private boolean hasTargetInReach() {
      if (c.player != null && c.world != null) {
         for (AbstractClientPlayerEntity p : c.world.getPlayers()) {
            if (p != null && p != c.player && !p.isInvisible() && this.isArmored(p)) {
               double distSq = c.player.squaredDistanceTo(p);
               if (distSq <= 10.24) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @EventHandler
   public void onRenderWorld(WorldRenderEvent event) {
      if (c.player != null && c.world != null && c.gameRenderer != null) {
         class_4598 bufferSource = event.bufferSource();
         if (bufferSource != null) {
            MatrixStack stack = event.matrices();
            Vec3d cam = c.gameRenderer.getCamera().getCameraPos();
            float tickDelta = event.tickDelta();
            boolean targetInReach = this.hasTargetInReach();
            Color circleColor = targetInReach ? this.targetInRadiusColor.getColor() : this.noTargetColor.getColor();
            List<AbstractClientPlayerEntity> playersToDraw = new ArrayList<>();
            if (this.ownCircle.a()) {
               playersToDraw.add(c.player);
            }

            if (this.otherCircles.a()) {
               for (AbstractClientPlayerEntity p : c.world.getPlayers()) {
                  if (p != c.player && p != null && !p.isInvisible() && this.isArmored(p)) {
                     playersToDraw.add(p);
                  }
               }
            }

            if (!playersToDraw.isEmpty()) {
               float reachRadius = 3.0F;
               float yOffset = this.heightAboveFloor.a();
               float alphaFill = this.fill.a() ? MathHelper.clamp(this.fillOpacity.a(), 0.0F, 1.0F) : 0.0F;
               int fillArgb = (int)(alphaFill * 255.0F) << 24 | circleColor.getRed() << 16 | circleColor.getGreen() << 8 | circleColor.getBlue();
               int lineArgb = -268435456 | circleColor.getRed() << 16 | circleColor.getGreen() << 8 | circleColor.getBlue();
               int SEGMENTS = 60;
               double PI2 = Math.PI * 2;

               for (AbstractClientPlayerEntity p : playersToDraw) {
                  double px = MathHelper.lerp(tickDelta, p.lastRenderX, p.getX()) - cam.x;
                  double py = MathHelper.lerp(tickDelta, p.lastRenderY, p.getY()) - cam.y + yOffset;
                  double pz = MathHelper.lerp(tickDelta, p.lastRenderZ, p.getZ()) - cam.z;
                  stack.push();
                  stack.translate(px, py, pz);
                  Matrix4f mat = stack.peek().getPositionMatrix();
                  if (this.fill.a() && alphaFill > 0.01F) {
                     VertexConsumer fillBuf = bufferSource.getBuffer(ClientPipelines.FILL);

                     for (int i = 0; i < SEGMENTS; i++) {
                        float a1 = (float)(i * PI2 / SEGMENTS);
                        float a2 = (float)((i + 1) * PI2 / SEGMENTS);
                        float x1 = -MathHelper.sin(a1) * reachRadius;
                        float z1 = MathHelper.cos(a1) * reachRadius;
                        float x2 = -MathHelper.sin(a2) * reachRadius;
                        float z2 = MathHelper.cos(a2) * reachRadius;
                        fillBuf.vertex(mat, 0.0F, 0.0F, 0.0F).color(fillArgb);
                        fillBuf.vertex(mat, x2, 0.0F, z2).color(fillArgb);
                        fillBuf.vertex(mat, x1, 0.0F, z1).color(fillArgb);
                        fillBuf.vertex(mat, 0.0F, 0.0F, 0.0F).color(fillArgb);
                     }
                  }

                  VertexConsumer lineBuf = bufferSource.getBuffer(ClientPipelines.OUTLINE_NO);

                  for (int i = 0; i < SEGMENTS; i++) {
                     float a1 = (float)(i * PI2 / SEGMENTS);
                     float a2 = (float)((i + 1) * PI2 / SEGMENTS);
                     float x1 = -MathHelper.sin(a1) * reachRadius;
                     float z1 = MathHelper.cos(a1) * reachRadius;
                     float x2 = -MathHelper.sin(a2) * reachRadius;
                     float z2 = MathHelper.cos(a2) * reachRadius;
                     lineBuf.vertex(mat, x1, 0.0F, z1).color(lineArgb);
                     lineBuf.vertex(mat, x2, 0.0F, z2).color(lineArgb);
                  }

                  stack.pop();
               }
            }
         }
      }
   }
}
