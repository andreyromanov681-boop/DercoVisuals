package pulse.modules.visuals;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import pulse.client.MinecraftContext;
import pulse.events.EntityJumpEvent;
import pulse.events.WorldRenderEvent;
import pulse.hud.core.HudServiceRegistry;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Jump Circles", b = "Отображает круги при прыжке игрока", c = ModuleCategory.VISUALS)
public class JumpCircles extends ClientModule implements MinecraftContext {
   private final ModeSetting mode = new ModeSetting("Режим", new String[]{"Круг + частицы", "Только частицы", "Только круг"}, "Круг + частицы");
   private final SliderSetting radius = new SliderSetting("Радиус", 1.0F, 0.5F, 2.5F, 0.1F);
   private final BooleanSetting useClientColor = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting color = new ColorSetting("Кастомный цвет", Color.CYAN).a(() -> !this.useClientColor.a());
   private final List<JumpCircles.Circle> circles = new CopyOnWriteArrayList<>();
   private static final Identifier CIRCLE_TEX = Identifier.of("pulse", "textures/particle/circle.png");

   @Override
   public void onDisable() {
      super.onDisable();
      this.circles.clear();
   }

   @EventHandler
   public void onJump(EntityJumpEvent event) {
      if (c.player != null && c.world != null && event.entity() == c.player) {
         Vec3d origin = new Vec3d(c.player.getX(), c.player.getY(), c.player.getZ());
         double yBelow = origin.y;
         double y = yBelow + 0.01;
         Vec3d pos = new Vec3d(origin.x, y, origin.z);
         String m = this.mode.d();
         if (m.equals("Круг + частицы") || m.equals("Только круг")) {
            this.circles.add(new JumpCircles.Circle(pos, this.radius.a()));
         }

         if (m.equals("Круг + частицы") || m.equals("Только частицы")) {
            Color cColor = this.getColor();
            int argb = 0xFF000000 | cColor.getRed() << 16 | cColor.getGreen() << 8 | cColor.getBlue();
            Identifier tex = Identifier.of("pulse", "textures/particle/sparkle.png");

            for (int i = 0; i < 12; i++) {
               Vec3d vel = new Vec3d(
                  ThreadLocalRandom.current().nextDouble(-0.2, 0.2),
                  ThreadLocalRandom.current().nextDouble(0.05, 0.25),
                  ThreadLocalRandom.current().nextDouble(-0.2, 0.2)
               );
               HudServiceRegistry.PARTICLES.a(pos, vel, 25, 0.3F, tex, argb, "Реалистичная");
            }
         }
      }
   }

   @EventHandler
   public void onWorldRender(WorldRenderEvent event) {
      if (c.player != null && c.world != null && !this.circles.isEmpty()) {
         this.circles.removeIf(circlex -> !circlex.alive());
         if (!this.circles.isEmpty()) {
            MatrixStack stack = event.matrices();
            VertexConsumer consumer = event.bufferSource().getBuffer(RenderLayers.itemEntityTranslucentCull(CIRCLE_TEX));
            Vec3d camPos = c.gameRenderer.getCamera().getCameraPos();
            Color cColor = this.getColor();

            for (JumpCircles.Circle circle : this.circles) {
               float alpha = circle.alpha();
               if (!(alpha <= 0.001F)) {
                  float sz = circle.size();
                  int a = (int)(alpha * 255.0F);
                  int argb1 = a << 24 | cColor.getRed() << 16 | cColor.getGreen() << 8 | cColor.getBlue();
                  int argb2 = argb1;
                  stack.push();
                  stack.translate(
                     circle.pos.x - camPos.x, circle.pos.y - camPos.y, circle.pos.z - camPos.z
                  );
                  stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(circle.randomRotation));
                  Matrix4f model = stack.peek().getPositionMatrix();
                  this.drawVertex(consumer, model, -sz, -sz, 0.0F, 0.0F, argb1);
                  this.drawVertex(consumer, model, -sz, sz, 0.0F, 1.0F, argb2);
                  this.drawVertex(consumer, model, sz, sz, 1.0F, 1.0F, argb1);
                  this.drawVertex(consumer, model, sz, -sz, 1.0F, 0.0F, argb2);
                  stack.pop();
               }
            }

            event.bufferSource().draw();
         }
      }
   }

   private void drawVertex(VertexConsumer vc, Matrix4f matrix, float x, float z, float u, float v, int argb) {
      vc.vertex(matrix, x, 0.0F, z)
         .color(argb)
         .texture(u, v)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(0.0F, 1.0F, 0.0F);
   }

   private Color getColor() {
      return this.useClientColor.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.color.a();
   }

   private static class Circle {
      public final float randomRotation = (float)(Math.random() * 360.0);
      public final Vec3d pos;
      public final long start = System.currentTimeMillis();
      public final long durationMs = 1800L;
      public final float startSize = 0.3F;
      public final float endSize;

      public Circle(Vec3d pos, float radius) {
         this.pos = pos;
         this.endSize = radius * 2.0F;
      }

      public float t() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.start) / 1800.0F);
      }

      private float easeOutQuint(float x) {
         float i = 1.0F - x;
         return 1.0F - i * i * i * i * i;
      }

      public boolean alive() {
         return System.currentTimeMillis() - this.start <= 1800L;
      }

      public float size() {
         float k = this.easeOutQuint(this.t());
         float base = 0.3F + (this.endSize - 0.3F) * k;
         return base + (float)Math.sin(System.currentTimeMillis() * 0.008) * 0.06F * (1.0F - this.t());
      }

      public float alpha() {
         float t = this.t();
         if (t < 0.3F) {
            return 1.0F;
         } else if (t < 0.6F) {
            float local = (t - 0.3F) / 0.3F;
            return 1.0F - 0.15F * (1.0F - (float)Math.cos(local * Math.PI / 2.0));
         } else {
            float local = (t - 0.6F) / 0.4F;
            float x = Math.max(0.0F, Math.min(1.0F, local));
            return 0.85F * (1.0F - x * x * (3.0F - 2.0F * x));
         }
      }
   }
}
