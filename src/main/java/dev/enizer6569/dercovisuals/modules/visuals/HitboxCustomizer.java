// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.events.WorldRenderEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ColorSetting;
import dev.enizer6569.dercovisuals.settings.ModeSetting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Hitbox Customizer", b = "Настраивает отображение хитбоксов сущностей", c = ModuleCategory.VISUALS)
public class HitboxCustomizer extends ClientModule {
   public final ModeSetting a = new ModeSetting("Режим обводки", new String[]{"Обычный", "Углы"}, "Обычный");
   public final SliderSetting b = new SliderSetting("Длина углов", 0.5F, 0.1F, 1.0F, 0.05F).a(() -> this.a.b("Углы"));
   private final BooleanSetting g = new BooleanSetting("Линии взгляда", false);
   private final BooleanSetting h = new BooleanSetting("Заполнять", true);
   private final SettingGroup i = new SettingGroup("Цвет");
   private final BooleanSetting j = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting k = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> {
      int i;
      if (this.j.a()) {
         i = 0;
      } else {
         i = 1;
      }

      return Bool.from(i);
   });

   public boolean n() {
      return Bool.from(this.k() && this.g.a() ? 1 : 0);
   }

   public boolean o() {
      return Bool.from(this.k() && this.h.a() ? 1 : 0);
   }

   public Color p() {
      return !this.j.a() ? this.k.a() : ModuleRegistry.CLIENT_COLOR.n();
   }

   public boolean q() {
      return this.a.b("Углы");
   }

   public float r() {
      return this.b.a() / 4.0F;
   }

   @EventHandler
   public void onWorldRender(WorldRenderEvent event) {
      if (this.k() && c.player != null && c.world != null && c.gameRenderer != null) {
         MatrixStack matrices = event.matrices();
         Vec3d cam = c.gameRenderer.getCamera().getCameraPos();
         Color cColor = this.p();
         int cr = cColor.getRed();
         int cg = cColor.getGreen();
         int cb = cColor.getBlue();
         Matrix4f mat = matrices.peek().getPositionMatrix();
         BufferAllocator allocator = new BufferAllocator(262144);
         class_4598 imm = VertexConsumerProvider.immediate(allocator);

         try {
            boolean firstPerson = c.options.getPerspective().isFirstPerson();

            for (Entity entity : c.world.getEntities()) {
               if ((entity != c.player || !firstPerson) && entity instanceof LivingEntity) {
                  Box box = entity.getBoundingBox();
                  double x1 = box.minX - cam.x;
                  double y1 = box.minY - cam.y;
                  double z1 = box.minZ - cam.z;
                  double x2 = box.maxX - cam.x;
                  double y2 = box.maxY - cam.y;
                  double z2 = box.maxZ - cam.z;
                  if (this.o()) {
                     VertexConsumer fill = imm.getBuffer(RenderLayers.debugQuads());
                     this.drawFilledBox(mat, fill, x1, y1, z1, x2, y2, z2, cr, cg, cb, 64);
                  }

                  VertexConsumer lines = imm.getBuffer(RenderLayers.linesTranslucent());
                  if (this.q()) {
                     this.drawCornerBox(mat, lines, x1, y1, z1, x2, y2, z2, cr, cg, cb, 255, this.r());
                  } else {
                     this.drawOutlineBox(mat, lines, x1, y1, z1, x2, y2, z2, cr, cg, cb, 255, 1.0F);
                  }

                  if (this.n()) {
                     Vec3d eye = entity.getEyePos();
                     Vec3d look = entity.getRotationVec(event.tickDelta());
                     Vec3d end = eye.add(look.multiply(2.0));
                     this.drawLine(
                        lines,
                        mat,
                        (float)(eye.x - cam.x),
                        (float)(eye.y - cam.y),
                        (float)(eye.z - cam.z),
                        (float)(end.x - cam.x),
                        (float)(end.y - cam.y),
                        (float)(end.z - cam.z),
                        cr,
                        cg,
                        cb,
                        255,
                        1.0F
                     );
                  }
               }
            }

            imm.draw();
         } finally {
            allocator.close();
         }
      }
   }

   private void drawFilledBox(Matrix4f mat, VertexConsumer buf, double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a) {
      float fx1 = (float)x1;
      float fy1 = (float)y1;
      float fz1 = (float)z1;
      float fx2 = (float)x2;
      float fy2 = (float)y2;
      float fz2 = (float)z2;
      buf.vertex(mat, fx1, fy1, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy1, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy1, fz2).color(r, g, b, a);
      buf.vertex(mat, fx1, fy1, fz2).color(r, g, b, a);
      buf.vertex(mat, fx1, fy2, fz1).color(r, g, b, a);
      buf.vertex(mat, fx1, fy2, fz2).color(r, g, b, a);
      buf.vertex(mat, fx2, fy2, fz2).color(r, g, b, a);
      buf.vertex(mat, fx2, fy2, fz1).color(r, g, b, a);
      buf.vertex(mat, fx1, fy1, fz1).color(r, g, b, a);
      buf.vertex(mat, fx1, fy2, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy2, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy1, fz1).color(r, g, b, a);
      buf.vertex(mat, fx1, fy1, fz2).color(r, g, b, a);
      buf.vertex(mat, fx2, fy1, fz2).color(r, g, b, a);
      buf.vertex(mat, fx2, fy2, fz2).color(r, g, b, a);
      buf.vertex(mat, fx1, fy2, fz2).color(r, g, b, a);
      buf.vertex(mat, fx1, fy1, fz1).color(r, g, b, a);
      buf.vertex(mat, fx1, fy1, fz2).color(r, g, b, a);
      buf.vertex(mat, fx1, fy2, fz2).color(r, g, b, a);
      buf.vertex(mat, fx1, fy2, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy1, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy2, fz1).color(r, g, b, a);
      buf.vertex(mat, fx2, fy2, fz2).color(r, g, b, a);
      buf.vertex(mat, fx2, fy1, fz2).color(r, g, b, a);
   }

   private void drawOutlineBox(
      Matrix4f mat, VertexConsumer buf, double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a, float lw
   ) {
      float fx1 = (float)x1;
      float fy1 = (float)y1;
      float fz1 = (float)z1;
      float fx2 = (float)x2;
      float fy2 = (float)y2;
      float fz2 = (float)z2;
      this.drawLine(buf, mat, fx1, fy1, fz1, fx2, fy1, fz1, r, g, b, a, lw);
      this.drawLine(buf, mat, fx2, fy1, fz1, fx2, fy1, fz2, r, g, b, a, lw);
      this.drawLine(buf, mat, fx2, fy1, fz2, fx1, fy1, fz2, r, g, b, a, lw);
      this.drawLine(buf, mat, fx1, fy1, fz2, fx1, fy1, fz1, r, g, b, a, lw);
      this.drawLine(buf, mat, fx1, fy2, fz1, fx2, fy2, fz1, r, g, b, a, lw);
      this.drawLine(buf, mat, fx2, fy2, fz1, fx2, fy2, fz2, r, g, b, a, lw);
      this.drawLine(buf, mat, fx2, fy2, fz2, fx1, fy2, fz2, r, g, b, a, lw);
      this.drawLine(buf, mat, fx1, fy2, fz2, fx1, fy2, fz1, r, g, b, a, lw);
      this.drawLine(buf, mat, fx1, fy1, fz1, fx1, fy2, fz1, r, g, b, a, lw);
      this.drawLine(buf, mat, fx2, fy1, fz1, fx2, fy2, fz1, r, g, b, a, lw);
      this.drawLine(buf, mat, fx2, fy1, fz2, fx2, fy2, fz2, r, g, b, a, lw);
      this.drawLine(buf, mat, fx1, fy1, fz2, fx1, fy2, fz2, r, g, b, a, lw);
   }

   private void drawCornerBox(
      Matrix4f mat, VertexConsumer buf, double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a, float l
   ) {
      float fx1 = (float)x1;
      float fy1 = (float)y1;
      float fz1 = (float)z1;
      float fx2 = (float)x2;
      float fy2 = (float)y2;
      float fz2 = (float)z2;
      float dx = fx2 - fx1;
      float dy = fy2 - fy1;
      float dz = fz2 - fz1;
      float dlx = dx * l;
      float dly = dy * l;
      float dlz = dz * l;
      this.drawLine(buf, mat, fx1, fy1, fz1, fx1 + dlx, fy1, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy1, fz1, fx1, fy1 + dly, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy1, fz1, fx1, fy1, fz1 + dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy1, fz1, fx2 - dlx, fy1, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy1, fz1, fx2, fy1 + dly, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy1, fz1, fx2, fy1, fz1 + dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy1, fz2, fx1 + dlx, fy1, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy1, fz2, fx1, fy1 + dly, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy1, fz2, fx1, fy1, fz2 - dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy1, fz2, fx2 - dlx, fy1, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy1, fz2, fx2, fy1 + dly, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy1, fz2, fx2, fy1, fz2 - dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy2, fz1, fx1 + dlx, fy2, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy2, fz1, fx1, fy2 - dly, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy2, fz1, fx1, fy2, fz1 + dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy2, fz1, fx2 - dlx, fy2, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy2, fz1, fx2, fy2 - dly, fz1, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy2, fz1, fx2, fy2, fz1 + dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy2, fz2, fx1 + dlx, fy2, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy2, fz2, fx1, fy2 - dly, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx1, fy2, fz2, fx1, fy2, fz2 - dlz, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy2, fz2, fx2 - dlx, fy2, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy2, fz2, fx2, fy2 - dly, fz2, r, g, b, a, 1.0F);
      this.drawLine(buf, mat, fx2, fy2, fz2, fx2, fy2, fz2 - dlz, r, g, b, a, 1.0F);
   }

   private void drawLine(VertexConsumer buf, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a, float lw) {
      float dx = x2 - x1;
      float dy = y2 - y1;
      float dz = z2 - z1;
      float len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
      if (len > 1.0E-4F) {
         dx /= len;
         dy /= len;
         dz /= len;
      }

      buf.vertex(mat, x1, y1, z1).color(r, g, b, a).normal(dx, dy, dz).lineWidth(lw);
      buf.vertex(mat, x2, y2, z2).color(r, g, b, a).normal(dx, dy, dz).lineWidth(lw);
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
