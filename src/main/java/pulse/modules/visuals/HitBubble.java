package pulse.modules.visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import pulse.events.AttackEntityEvent;
import pulse.events.WorldRenderEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.render.icons.IconTextureRegistry;
import pulse.render.system.ClientPipelines;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Hit Bubble", b = "Показывает анимированные пузыри при атаке", c = ModuleCategory.VISUALS)
public class HitBubble extends ClientModule {
   private static final long LIFETIME_MS = 600L;
   private static final float RISE_DISTANCE = 0.6F;
   private static final float BASE_SIZE = 0.35F;
   private int hitCounter = 0;
   private final SliderSetting lifetime = new SliderSetting("Время жизни", 40.0F, 10.0F, 100.0F, 1.0F);
   private final ModeSetting texture = new ModeSetting("Текстура", new String[]{"Пузырь 1", "Пузырь 2"}, 0);
   private final BooleanSetting useClientColor = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting customColor = new ColorSetting("Свой цвет", new Color(255, 255, 255, 255));
   private final SliderSetting size = new SliderSetting("Размер", 0.35F, 0.15F, 0.8F, 0.05F);
   private final SliderSetting riseSpeed = new SliderSetting("Скорость подъема", 0.6F, 0.1F, 1.5F, 0.05F);
   private final CopyOnWriteArrayList<HitBubble.BubbleParticle> bubbles = new CopyOnWriteArrayList<>();

   @EventHandler
   public void onAttack(AttackEntityEvent attackEntityEvent) {
      Entity EntityVarA;
      if (this.k() && (EntityVarA = attackEntityEvent.a()) != null) {
         double randomX = (Math.random() - 0.5) * 1.5;
         double randomY = (Math.random() - 0.5) * 1.0;
         double randomZ = (Math.random() - 0.5) * 1.5;
         double dGetX = EntityVarA.getX() + randomX;
         double dGetY = EntityVarA.getY() + EntityVarA.getHeight() / 2.0 + randomY;
         double dGetZ = EntityVarA.getZ() + randomZ;
         String selectedTexture = this.texture.k() == 0 ? "bubble_1" : "bubble_2";
         this.bubbles.add(new HitBubble.BubbleParticle(dGetX, dGetY, dGetZ, selectedTexture, System.currentTimeMillis()));
      }
   }

   @EventHandler
   public void onWorldRender(WorldRenderEvent worldRenderEvent) {
      if (this.k() && !this.bubbles.isEmpty() && c.world != null && c.gameRenderer != null) {
         MatrixStack MatrixStackVarA = worldRenderEvent.a();
         Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
         float fGetYaw = c.gameRenderer.getCamera().getYaw();
         float fGetPitch = c.gameRenderer.getCamera().getPitch();
         long jCurrentTimeMillis = System.currentTimeMillis();
         ArrayList arrayList = new ArrayList();
         BufferAllocator allocator = new BufferAllocator(65536);
         class_4598 imm = VertexConsumerProvider.immediate(allocator);
         long lifetimeMs = (long)(this.lifetime.a() * 15.0F);

         for (HitBubble.BubbleParticle bubbleParticle : this.bubbles) {
            long j = jCurrentTimeMillis - bubbleParticle.spawnTime;
            if (j >= lifetimeMs) {
               arrayList.add(bubbleParticle);
            } else {
               this.renderBubble(MatrixStackVarA, imm, Vec3dVarGetPos, fGetYaw, fGetPitch, bubbleParticle, (float)j / (float)lifetimeMs);
            }
         }

         imm.draw();
         allocator.close();
         if (!arrayList.isEmpty()) {
            this.bubbles.removeAll(arrayList);
         }
      }
   }

   private void renderBubble(
      MatrixStack MatrixStackVar, class_4598 imm, Vec3d Vec3dVar, float f, float f2, HitBubble.BubbleParticle bubbleParticle, float f3
   ) {
      double d = bubbleParticle.x - Vec3dVar.x;
      double dA = bubbleParticle.y - Vec3dVar.y + (1.0F - (1.0F - f3) * (1.0F - f3)) * this.riseSpeed.a();
      double d2 = bubbleParticle.z - Vec3dVar.z;
      float fClamp = MathHelper.clamp(f3 < 0.15F ? f3 / 0.15F : (f3 > 0.7F ? 1.0F - (f3 - 0.7F) / 0.3F : 1.0F), 0.0F, 1.0F);
      float f4 = f3 < 0.2F ? 0.4F + 0.8F * (f3 / 0.2F) : 1.0F;
      Identifier IdentifierVar = IconTextureRegistry.get(bubbleParticle.textureKey);
      if (IdentifierVar != null) {
         MatrixStackVar.push();
         MatrixStackVar.translate(d, dA, d2);
         MatrixStackVar.multiply(new Quaternionf().rotationYXZ((float)Math.toRadians(-f), (float)Math.toRadians(f2), 0.0F));
         float fA = this.size.a() * f4 / 2.0F;
         VertexConsumer buf = imm.getBuffer(ClientPipelines.getTextureLayer(IdentifierVar));
         Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
         Color color = this.useClientColor.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.customColor.a();
         int r = color.getRed();
         int g = color.getGreen();
         int b = color.getBlue();
         int a = (int)(255.0F * fClamp);
         buf.vertex(matrix4fGetPositionMatrix, -fA, -fA, 0.0F).texture(0.0F, 0.0F).color(r, g, b, a);
         buf.vertex(matrix4fGetPositionMatrix, fA, -fA, 0.0F).texture(1.0F, 0.0F).color(r, g, b, a);
         buf.vertex(matrix4fGetPositionMatrix, fA, fA, 0.0F).texture(1.0F, 1.0F).color(r, g, b, a);
         buf.vertex(matrix4fGetPositionMatrix, -fA, fA, 0.0F).texture(0.0F, 1.0F).color(r, g, b, a);
         MatrixStackVar.pop();
      }
   }

   private static final class BubbleParticle {
      final double x;
      final double y;
      final double z;
      final String textureKey;
      final long spawnTime;

      BubbleParticle(double d, double d2, double d3, String str, long j) {
         this.x = d;
         this.y = d2;
         this.z = d3;
         this.textureKey = str;
         this.spawnTime = j;
      }
   }
}
