package pulse.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import pulse.client.MinecraftContext;
import pulse.events.WorldRenderEvent;
import pulse.events.WorldRenderStartEvent;
import pulse.hud.core.HudService;
import pulse.hud.core.HudServiceInfo;
import pulse.render.RenderSystemHelper;

@HudServiceInfo(enabledByDefault = true)
public class ParticleRenderService extends HudService implements MinecraftContext {
   private final CopyOnWriteArrayList<ParticleRenderService.ParticleInstance> particles = new CopyOnWriteArrayList<>();
   private long lastTickTime = -1L;

   public void a(Vec3d pos, Vec3d vel, int lifetime, float scale, Identifier texture, int colorArgb, double gravity, String physicsMode) {
      this.particles.add(new ParticleRenderService.ParticleInstance(this, pos, vel, lifetime, scale, texture, this.particles, colorArgb, gravity, physicsMode));
   }

   public void a(Vec3d pos, Vec3d vel, int lifetime, float scale, Identifier texture, int colorArgb, String physicsMode) {
      this.a(pos, vel, lifetime, scale, texture, colorArgb, 0.02, physicsMode);
   }

   @EventHandler
   public void onStart(WorldRenderStartEvent event) {
      if (c.world != null) {
         long time = c.world.getTime();
         if (time != this.lastTickTime) {
            this.particles.forEach(ParticleRenderService.ParticleInstance::tick);
            this.lastTickTime = time;
         }
      }
   }

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      if (c.gameRenderer != null && c.gameRenderer.getCamera() != null && !this.particles.isEmpty()) {
         class_4598 bufferSource = worldRenderEvent.bufferSource();
         if (bufferSource != null) {
            Vec3d camPos = c.gameRenderer.getCamera().getCameraPos();
            Frustum frustum = new Frustum(
               new Matrix4f(worldRenderEvent.matrices().peek().getPositionMatrix()), new Matrix4f(RenderSystemHelper.getProjectionMatrix())
            );
            frustum.setPosition(camPos.x, camPos.y, camPos.z);
            Map<Identifier, List<ParticleRenderService.ParticleVertex>> map = new HashMap<>();

            for (ParticleRenderService.ParticleInstance p : this.particles) {
               ParticleRenderService.ParticleVertex v = p.getVertex(frustum);
               if (v != null) {
                  map.computeIfAbsent(p.texture, k -> new ArrayList<>()).add(v);
               }
            }

            if (!map.isEmpty()) {
               MatrixStack stack = worldRenderEvent.matrices();

               for (Entry<Identifier, List<ParticleRenderService.ParticleVertex>> entry : map.entrySet()) {
                  Identifier texture = entry.getKey();
                  List<ParticleRenderService.ParticleVertex> list = entry.getValue();
                  if (!list.isEmpty()) {
                     VertexConsumer consumer = bufferSource.getBuffer(RenderLayers.itemEntityTranslucentCull(texture));

                     for (ParticleRenderService.ParticleVertex vertex : list) {
                        stack.push();

                        try {
                           stack.translate(
                              vertex.pos.x - camPos.x, vertex.pos.y - camPos.y, vertex.pos.z - camPos.z
                           );
                           stack.multiply(c.gameRenderer.getCamera().getRotation());
                           stack.multiply(new Quaternionf().rotationZ((float)Math.toRadians(vertex.rotation)));
                           stack.scale(vertex.scale, vertex.scale, vertex.scale);
                           Matrix4f matrix = stack.peek().getPositionMatrix();
                           consumer.vertex(matrix, -0.5F, -0.5F, 0.0F)
                              .color(vertex.r, vertex.g, vertex.b, vertex.a)
                              .texture(0.0F, 0.0F)
                              .overlay(OverlayTexture.DEFAULT_UV)
                              .light(15728880)
                              .normal(0.0F, 1.0F, 0.0F);
                           consumer.vertex(matrix, 0.5F, -0.5F, 0.0F)
                              .color(vertex.r, vertex.g, vertex.b, vertex.a)
                              .texture(1.0F, 0.0F)
                              .overlay(OverlayTexture.DEFAULT_UV)
                              .light(15728880)
                              .normal(0.0F, 1.0F, 0.0F);
                           consumer.vertex(matrix, 0.5F, 0.5F, 0.0F)
                              .color(vertex.r, vertex.g, vertex.b, vertex.a)
                              .texture(1.0F, 1.0F)
                              .overlay(OverlayTexture.DEFAULT_UV)
                              .light(15728880)
                              .normal(0.0F, 1.0F, 0.0F);
                           consumer.vertex(matrix, -0.5F, 0.5F, 0.0F)
                              .color(vertex.r, vertex.g, vertex.b, vertex.a)
                              .texture(0.0F, 1.0F)
                              .overlay(OverlayTexture.DEFAULT_UV)
                              .light(15728880)
                              .normal(0.0F, 1.0F, 0.0F);
                        } finally {
                           stack.pop();
                        }
                     }
                  }
               }

               bufferSource.draw();
            }
         }
      }
   }

   public class ParticleInstance {
      final int lifetime;
      Vec3d lastPos;
      Vec3d pos;
      Vec3d velocity;
      final float scale;
      final CopyOnWriteArrayList<ParticleRenderService.ParticleInstance> list;
      final Identifier texture;
      final double gravity;
      final String physicsMode;
      final int colorArgb;
      final float rotation = ThreadLocalRandom.current().nextFloat() * 360.0F;
      int age = 0;
      float prevRotation = 0.0F;
      public float rotationSpeed;
      public float wobblePhase;
      public float wobbleSpeed;
      public float wobbleAmplitude;

      ParticleInstance(
         ParticleRenderService service,
         Vec3d pos,
         Vec3d vel,
         int lifetime,
         float scale,
         Identifier texture,
         CopyOnWriteArrayList<ParticleRenderService.ParticleInstance> list,
         int colorArgb,
         double gravity,
         String physicsMode
      ) {
         this.lifetime = lifetime;
         this.list = list;
         this.pos = pos;
         this.lastPos = pos;
         this.velocity = vel;
         this.scale = scale;
         this.texture = texture;
         this.colorArgb = colorArgb;
         this.gravity = gravity;
         this.physicsMode = physicsMode;
         this.rotationSpeed = (ThreadLocalRandom.current().nextFloat() - 0.5F) * 10.0F;
         this.wobblePhase = ThreadLocalRandom.current().nextFloat() * (float) (Math.PI * 2);
         this.wobbleSpeed = 0.1F + ThreadLocalRandom.current().nextFloat() * 0.2F;
         this.wobbleAmplitude = 0.015F + ThreadLocalRandom.current().nextFloat() * 0.025F;
      }

      public void tick() {
         this.age++;
         if (this.age >= this.lifetime) {
            this.list.remove(this);
         } else {
            this.lastPos = this.pos;
            this.rotation = this.rotation + this.rotationSpeed;
            this.wobblePhase = this.wobblePhase + this.wobbleSpeed;
            (new Vec3d(Math.sin(this.wobblePhase) * this.wobbleAmplitude, 0.0, Math.cos(this.wobblePhase) * this.wobbleAmplitude)).pos = this.pos
               .add(this);
            if ("Реалистичная".equals(this.physicsMode)) {
               this.velocity = this.velocity.multiply(0.98);
               this.velocity = this.velocity.add(0.0, -this.gravity, 0.0);
               this.pos = this.pos.add(this.velocity);
            } else if ("Притяжение".equals(this.physicsMode)) {
               if (MinecraftContext.c.player != null) {
                  Vec3d dir = new Vec3d(
                        MinecraftContext.c.player.getX(),
                        MinecraftContext.c.player.getY() + 1.0,
                        MinecraftContext.c.player.getZ()
                     )
                     .subtract(this.pos);
                  if (dir.lengthSquared() > 0.01) {
                     this.velocity = this.velocity.add(dir.normalize().multiply(0.03));
                  }
               }

               this.pos = this.pos.add(this.velocity);
            } else {
               this.pos = this.pos.add(this.velocity);
            }
         }
      }

      public ParticleRenderService.ParticleVertex getVertex(Frustum frustum) {
         float progress = (float)this.age / this.lifetime;
         int alpha = (int)((1.0F - progress) * (this.colorArgb >> 24 & 0xFF));
         if (alpha <= 0) {
            return null;
         }

         int r = this.colorArgb >> 16 & 0xFF;
         int g = this.colorArgb >> 8 & 0xFF;
         int b = this.colorArgb & 0xFF;
         return new ParticleRenderService.ParticleVertex(this.pos, this.scale * (1.0F - progress * 0.3F), this.rotation, r, g, b, alpha);
      }
   }

   public static class ParticleVertex {
      Vec3d pos;
      float scale;
      float rotation;
      int r;
      int g;
      int b;
      int a;

      ParticleVertex(Vec3d pos, float scale, float rotation, int r, int g, int b, int a) {
         this.pos = pos;
         this.scale = scale;
         this.rotation = rotation;
         this.r = r;
         this.g = g;
         this.b = b;
         this.a = a;
      }
   }
}
