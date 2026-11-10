package pulse.cosmetic;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import pulse.render.AttachmentPoint;
import pulse.render.MatrixTransformStack;

public class CosmeticRenderController {
   private static CosmeticRenderController c;
   private final CosmeticModelRenderer d = CosmeticModelRenderer.a();
   private final MatrixTransformStack e = new MatrixTransformStack();
   private static final float f = 180.0F / (float)Math.PI;
   public static int a;
   public static boolean b;

   public static CosmeticRenderController a() {
      if (c == null) {
         c = new CosmeticRenderController();
      }

      return c;
   }

   public void a(
      CosmeticModel cosmeticModel,
      AbstractClientPlayerEntity AbstractClientPlayerEntityVar,
      MatrixStack MatrixStackVar,
      VertexConsumerProvider VertexConsumerProviderVar,
      int i,
      PlayerEntityModel PlayerEntityModelVar,
      float f2
   ) {
      if (cosmeticModel != null && cosmeticModel.e() != null) {
         this.e.a(MatrixStackVar);
         this.e.a();
         float fA = this.a(cosmeticModel, PlayerEntityModelVar);
         this.e.f(180.0F);
         this.e.a(cosmeticModel.h(), cosmeticModel.i() + fA, cosmeticModel.j());
         this.e.e(cosmeticModel.k());
         this.e.d(cosmeticModel.l());
         this.e.f(cosmeticModel.m());
         this.e.b(cosmeticModel.g(), cosmeticModel.g(), cosmeticModel.g());
         this.d.a(cosmeticModel, MatrixStackVar, VertexConsumerProviderVar, i);
         this.e.b();
      }
   }

   private float a(CosmeticModel cosmeticModel, PlayerEntityModel PlayerEntityModelVar) {
      float f2 = 0.0F;
      AttachmentPoint attachmentPointF = cosmeticModel.f();
      if (PlayerEntityModelVar == null) {
         return 0.0F;
      }

      switch (CosmeticRenderController.AnonymousClass1.$SwitchMap$pulse$AttachmentPoint[attachmentPointF.ordinal()]) {
         case 1:
            this.a(PlayerEntityModelVar.head);
            f2 = 0.5F;
            break;
         case 2:
            f2 = 0.75F;
            break;
         case 3:
            this.a(PlayerEntityModelVar.body);
            f2 = -0.3F;
            break;
         case 4:
            this.a(PlayerEntityModelVar.rightArm);
            f2 = -0.25F;
            break;
         case 5:
            this.a(PlayerEntityModelVar.leftArm);
            f2 = -0.25F;
            break;
         case 6:
            this.a(PlayerEntityModelVar.rightLeg);
            f2 = -0.35F;
            break;
         case 7:
            this.a(PlayerEntityModelVar.leftLeg);
            f2 = -0.35F;
      }

      return f2;
   }

   private void a(ModelPart ModelPartVar) {
      this.e.a(ModelPartVar.originX * 0.0625F, ModelPartVar.originY * 0.0625F, ModelPartVar.originZ * 0.0625F);
      this.e
         .d(
            ModelPartVar.pitch * (180.0F / (float)Math.PI),
            ModelPartVar.yaw * (180.0F / (float)Math.PI),
            ModelPartVar.roll * (180.0F / (float)Math.PI)
         );
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   static class AnonymousClass1 {
      static final int[] $SwitchMap$pulse$AttachmentPoint = new int[AttachmentPoint.values().length];

      static {
         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.HEAD.ordinal()] = 1;
         } catch (NoSuchFieldError var8) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.ABOVE_HEAD.ordinal()] = 2;
         } catch (NoSuchFieldError var7) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.BODY.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.RIGHT_ARM.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.LEFT_ARM.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.RIGHT_LEG.ordinal()] = 6;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.LEFT_LEG.ordinal()] = 7;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$pulse$AttachmentPoint[AttachmentPoint.FREE.ordinal()] = 8;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
