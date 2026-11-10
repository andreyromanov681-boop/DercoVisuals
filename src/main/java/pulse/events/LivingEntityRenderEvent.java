package pulse.events;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;

public class LivingEntityRenderEvent extends PulseEvent {
   private final LivingEntity entity;
   private final LivingEntityRenderState renderState;
   private final MatrixStack matrices;
   private final EntityModel<?> model;
   private final VertexConsumerProvider vertexConsumers;

   public LivingEntityRenderEvent(
      LivingEntity LivingEntityVar,
      LivingEntityRenderState LivingEntityRenderStateVar,
      MatrixStack MatrixStackVar,
      EntityModel<?> EntityModelVar,
      VertexConsumerProvider VertexConsumerProviderVar
   ) {
      this.entity = LivingEntityVar;
      this.renderState = LivingEntityRenderStateVar;
      this.matrices = MatrixStackVar;
      this.model = EntityModelVar;
      this.vertexConsumers = VertexConsumerProviderVar;
   }

   public LivingEntity entity() {
      return this.entity;
   }

   public LivingEntityRenderState renderState() {
      return this.renderState;
   }

   public MatrixStack matrices() {
      return this.matrices;
   }

   public EntityModel<?> model() {
      return this.model;
   }

   public VertexConsumerProvider vertexConsumers() {
      return this.vertexConsumers;
   }

   public LivingEntity a() {
      return this.entity;
   }

   public LivingEntityRenderState b() {
      return this.renderState;
   }

   public MatrixStack c() {
      return this.matrices;
   }

   public EntityModel<?> d() {
      return this.model;
   }

   public VertexConsumerProvider e() {
      return this.vertexConsumers;
   }
}
