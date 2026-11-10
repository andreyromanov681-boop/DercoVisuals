// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import dev.enizer6569.dercovisuals.events.WorldRenderEvent;
import dev.enizer6569.dercovisuals.hud.core.HudServiceRegistry;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.render.system.ClientPipelines;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.ColorSetting;
import dev.enizer6569.dercovisuals.settings.ModeSetting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Trails", b = "Отображает след игрока", c = ModuleCategory.VISUALS)
public class Trails extends ClientModule {
   private final ModeSetting walkMode = new ModeSetting("Режим ходьбы", new String[]{"Только след", "Только частицы", "След + частицы"}, "Только след");
   private final BooleanSetting showFirstPerson = new BooleanSetting("Показывать от первого лица", false);
   private final SettingGroup trailGroup = new SettingGroup("След").a(() -> this.walkMode.b("След + частицы"));
   private final ModeSetting trailType = new ModeSetting("Тип следа", new String[]{"Сплошной", "Пунктир", "Затухающий"}, "Сплошной").a(() -> {
      if (this.walkMode.b("Только след")) {
         return true;
      } else {
         return !this.walkMode.b("След + частицы") ? false : true;
      }
   });
   private final SliderSetting trailLength = new SliderSetting("Длина следа", 3.0F, 0.5F, 10.0F, 0.5F)
      .a(() -> this.walkMode.b("Только след") || this.walkMode.b("След + частицы"));
   private final SliderSetting trailWidth = new SliderSetting("Ширина следа", 0.1F, 0.05F, 0.5F, 0.01F)
      .a(() -> this.walkMode.b("Только след") || this.walkMode.b("След + частицы"));
   private final BooleanSetting useClientColorTrail = new BooleanSetting("Цвет клиента", true)
      .a(() -> this.walkMode.b("Только след") || this.walkMode.b("След + частицы"));
   private final ColorSetting customColorTrail = new ColorSetting("Кастомный цвет", new Color(42751))
      .a(() -> (this.walkMode.b("Только след") || this.walkMode.b("След + частицы")) && !this.useClientColorTrail.a());
   private final SettingGroup particleGroup = new SettingGroup("Частицы").a(() -> this.walkMode.b("След + частицы"));
   private final ModeSetting particleSpawnMode = new ModeSetting("Режим частиц", new String[]{"Под ногами", "По телу"}, "Под ногами")
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final ModeSetting particleType = new ModeSetting("Тип частиц", new String[]{"Сердце", "Искра", "Снежинка", "Сияние", "Звезда", "Доллар"}, "Сердце")
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final ModeSetting physicsMode = new ModeSetting(
         "Режим физики", new String[]{"Реалистичная", "Без коллизий", "Без физики", "Притяжение"}, "Реалистичная"
      )
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final SliderSetting particleAmount = new SliderSetting("Количество частиц", 4.0F, 1.0F, 20.0F, 1.0F)
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final SliderSetting particleSize = new SliderSetting("Размер частиц", 0.3F, 0.1F, 0.75F, 0.05F)
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("crypt"));
   private final SliderSetting particleLifetime = new SliderSetting("Время жизни", 20.0F, 10.0F, 50.0F, 5.0F)
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final SliderSetting particleSpread = new SliderSetting("Сила разлёта", 0.3F, 0.15F, 0.35F, 0.01F)
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final BooleanSetting useClientColorParticle = new BooleanSetting("Цвет клиента", true)
      .a(() -> this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы"));
   private final ColorSetting customColorParticle = new ColorSetting("Кастомный цвет", Color.WHITE)
      .a(() -> (this.walkMode.b("Только частицы") || this.walkMode.b("След + частицы")) && !this.useClientColorParticle.a());
   private final List<Trails.TrailPoint> points = new ArrayList<>();
   private Vec3d lastPlayerPos = null;

   @EventHandler
   public void onRender(WorldRenderEvent worldRenderEvent) {
      if (c.world != null && c.player != null) {
         long now = System.currentTimeMillis();
         float tickDelta = worldRenderEvent.tickDelta();
         float maxAge = this.trailLength.a() * 1000.0F;
         Vec3d lerpedPos = new Vec3d(
            MathHelper.lerp(tickDelta, c.player.lastRenderX, c.player.getX()),
            MathHelper.lerp(tickDelta, c.player.lastRenderY, c.player.getY()),
            MathHelper.lerp(tickDelta, c.player.lastRenderZ, c.player.getZ())
         );
         String currentMode = this.walkMode.d();
         boolean renderTrail = currentMode.equals("Только след") || currentMode.equals("След + частицы");
         boolean renderParticles = currentMode.equals("Только частицы") || currentMode.equals("След + частицы");
         if (this.lastPlayerPos == null || this.lastPlayerPos.distanceTo(lerpedPos) > 0.03) {
            this.points.add(new Trails.TrailPoint(lerpedPos.add(0.0, 0.05, 0.0), now));
            this.lastPlayerPos = lerpedPos;
            if (renderParticles) {
               this.spawnParticles(lerpedPos);
            }
         }

         this.points.removeIf(p -> (float)(now - p.time) > maxAge);
         if (renderTrail && this.points.size() >= 2 && (!c.options.getPerspective().isFirstPerson() || this.showFirstPerson.a())) {
            class_4598 bufferSource = worldRenderEvent.bufferSource();
            if (bufferSource == null) {
               return;
            }

            MatrixStack stack = worldRenderEvent.matrices();
            stack.push();

            try {
               Vec3d cam = c.gameRenderer.getCamera().getCameraPos();
               Matrix4f matrix = stack.peek().getPositionMatrix();
               List<Trails.TrailPoint> pointList = new ArrayList<>(this.points);
               String tType = this.trailType.d();
               float height = this.trailWidth.a() * 3.0F;
               if ("Пунктир".equals(tType)) {
                  this.renderDotted(bufferSource, matrix, height, pointList, cam, now, maxAge);
               } else if ("Затухающий".equals(tType)) {
                  this.renderRibbon(bufferSource, matrix, height, pointList, cam, now, maxAge, true);
               } else {
                  this.renderRibbon(bufferSource, matrix, height, pointList, cam, now, maxAge, false);
               }

               bufferSource.draw();
            } finally {
               stack.pop();
            }
         }
      }
   }

   private void renderRibbon(
      class_4598 bufferSource, Matrix4f matrix, float height, List<Trails.TrailPoint> pts, Vec3d cam, long now, float maxAge, boolean faded
   ) {
      VertexConsumer fill = bufferSource.getBuffer(ClientPipelines.FILL);

      for (int i = 0; i + 1 < pts.size(); i++) {
         Trails.TrailPoint p1 = pts.get(i);
         Trails.TrailPoint p2 = pts.get(i + 1);
         float age1 = MathHelper.clamp((float)(now - p1.time) / maxAge, 0.0F, 1.0F);
         float age2 = MathHelper.clamp((float)(now - p2.time) / maxAge, 0.0F, 1.0F);
         float alpha1 = (1.0F - age1) * (faded ? 1.0F - age1 : 1.0F) * 0.7F;
         float alpha2 = (1.0F - age2) * (faded ? 1.0F - age2 : 1.0F) * 0.7F;
         Color c1 = this.getTrailColor();
         Color c2 = this.getTrailColor();
         int argb1 = (int)(alpha1 * 255.0F) << 24 | c1.getRed() << 16 | c1.getGreen() << 8 | c1.getBlue();
         int argb2 = (int)(alpha2 * 255.0F) << 24 | c2.getRed() << 16 | c2.getGreen() << 8 | c2.getBlue();
         float x1 = (float)(p1.pos.x - cam.x);
         float y1 = (float)(p1.pos.y - cam.y);
         float z1 = (float)(p1.pos.z - cam.z);
         float x2 = (float)(p2.pos.x - cam.x);
         float y2 = (float)(p2.pos.y - cam.y);
         float z2 = (float)(p2.pos.z - cam.z);
         fill.vertex(matrix, x1, y1, z1).color(argb1);
         fill.vertex(matrix, x2, y2, z2).color(argb2);
         fill.vertex(matrix, x2, y2 + height, z2).color(argb2);
         fill.vertex(matrix, x1, y1 + height, z1).color(argb1);
         fill.vertex(matrix, x1, y1 + height, z1).color(argb1);
         fill.vertex(matrix, x2, y2 + height, z2).color(argb2);
         fill.vertex(matrix, x2, y2, z2).color(argb2);
         fill.vertex(matrix, x1, y1, z1).color(argb1);
      }

      VertexConsumer lines = bufferSource.getBuffer(ClientPipelines.OUTLINE_NO);

      for (int i = 0; i + 1 < pts.size(); i++) {
         Trails.TrailPoint p1 = pts.get(i);
         Trails.TrailPoint p2 = pts.get(i + 1);
         float age1 = MathHelper.clamp((float)(now - p1.time) / maxAge, 0.0F, 1.0F);
         float age2 = MathHelper.clamp((float)(now - p2.time) / maxAge, 0.0F, 1.0F);
         Color c1 = this.getTrailColor();
         Color c2 = this.getTrailColor();
         int lineArgb1 = (int)((1.0F - age1) * 255.0F) << 24 | c1.getRed() << 16 | c1.getGreen() << 8 | c1.getBlue();
         int lineArgb2 = (int)((1.0F - age2) * 255.0F) << 24 | c2.getRed() << 16 | c2.getGreen() << 8 | c2.getBlue();
         float x1 = (float)(p1.pos.x - cam.x);
         float y1 = (float)(p1.pos.y - cam.y);
         float z1 = (float)(p1.pos.z - cam.z);
         float x2 = (float)(p2.pos.x - cam.x);
         float y2 = (float)(p2.pos.y - cam.y);
         float z2 = (float)(p2.pos.z - cam.z);
         lines.vertex(matrix, x1, y1, z1).color(lineArgb1);
         lines.vertex(matrix, x2, y2, z2).color(lineArgb2);
         lines.vertex(matrix, x1, y1 + height, z1).color(lineArgb1);
         lines.vertex(matrix, x2, y2 + height, z2).color(lineArgb2);
      }
   }

   private void renderDotted(class_4598 bufferSource, Matrix4f matrix, float height, List<Trails.TrailPoint> pts, Vec3d cam, long now, float maxAge) {
      VertexConsumer lines = bufferSource.getBuffer(ClientPipelines.OUTLINE_NO);

      for (int i = 0; i + 1 < pts.size(); i += 2) {
         Trails.TrailPoint p1 = pts.get(i);
         Trails.TrailPoint p2 = pts.get(i + 1);
         float age1 = MathHelper.clamp((float)(now - p1.time) / maxAge, 0.0F, 1.0F);
         Color c1 = this.getTrailColor();
         int argb = (int)((1.0F - age1) * 255.0F) << 24 | c1.getRed() << 16 | c1.getGreen() << 8 | c1.getBlue();
         float x1 = (float)(p1.pos.x - cam.x);
         float y1 = (float)(p1.pos.y - cam.y);
         float z1 = (float)(p1.pos.z - cam.z);
         float x2 = (float)(p2.pos.x - cam.x);
         float y2 = (float)(p2.pos.y - cam.y);
         float z2 = (float)(p2.pos.z - cam.z);
         lines.vertex(matrix, x1, y1, z1).color(argb);
         lines.vertex(matrix, x2, y2, z2).color(argb);
         lines.vertex(matrix, x1, y1 + height, z1).color(argb);
         lines.vertex(matrix, x2, y2 + height, z2).color(argb);
      }
   }

   private void spawnParticles(Vec3d basePos) {
      int count = (int)this.particleAmount.a();
      int lifetime = (int)this.particleLifetime.a();
      float size = this.particleSize.a();
      double spread = this.particleSpread.a();
      Color pColor = this.getParticleColor();
      int argb = 0xFF000000 | pColor.getRed() << 16 | pColor.getGreen() << 8 | pColor.getBlue();
      Identifier texture = this.getParticleTexture();
      String pMode = this.physicsMode.d();
      boolean bodySpawn = "По телу".equals(this.particleSpawnMode.d());

      for (int i = 0; i < count; i++) {
         double yOff = bodySpawn ? ThreadLocalRandom.current().nextDouble(0.0, c.player.getHeight()) : ThreadLocalRandom.current().nextDouble(0.0, 0.2);
         Vec3d spawnPos = basePos.add(ThreadLocalRandom.current().nextDouble(-0.2, 0.2), yOff, ThreadLocalRandom.current().nextDouble(-0.2, 0.2));
         Vec3d vel = new Vec3d(
            ThreadLocalRandom.current().nextDouble(-spread, spread),
            ThreadLocalRandom.current().nextDouble(0.05, spread),
            ThreadLocalRandom.current().nextDouble(-spread, spread)
         );
         HudServiceRegistry.PARTICLES.a(spawnPos, vel, lifetime, size, texture, argb, pMode);
      }
   }

   private Color getTrailColor() {
      return !this.useClientColorTrail.a() ? this.customColorTrail.a() : ModuleRegistry.CLIENT_COLOR.n();
   }

   private Color getParticleColor() {
      return !this.useClientColorParticle.a() ? this.customColorParticle.a() : ModuleRegistry.CLIENT_COLOR.n();
   }

   private Identifier getParticleTexture() {
      String type = this.particleType.d();
      switch (type) {
         case "Искра":
            return Identifier.of("dercovisuals", "textures/particle/sparkle.png");
         case "Снежинка":
            return Identifier.of("dercovisuals", "textures/particle/snowflake.png");
         case "Сияние":
            return Identifier.of("dercovisuals", "textures/particle/glow.png");
         case "Звезда":
            return Identifier.of("dercovisuals", "textures/particle/star.png");
         case "Доллар":
            return Identifier.of("dercovisuals", "textures/particle/dollar.png");
         case "Сердце":
         default:
            return Identifier.of("dercovisuals", "textures/particle/heart.png");
      }
   }

   @Override
   public void f() {
      super.f();
      this.points.clear();
      this.lastPlayerPos = null;
   }

   public static class TrailPoint {
      public final Vec3d pos;
      public final long time;

      public TrailPoint(Vec3d pos, long time) {
         this.pos = pos;
         this.time = time;
      }
   }
}
