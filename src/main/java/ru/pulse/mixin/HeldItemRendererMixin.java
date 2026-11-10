package ru.pulse.mixin;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.util.Hand;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.module.ModuleRegistry;
import pulse.modules.utilities.MaceHelper;
import pulse.modules.visuals.CustomHand;
import pulse.render.HandRenderState;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   private ItemStack offHand;

   @Shadow
   public abstract void renderItem(LivingEntity var1, ItemStack var2, ItemDisplayContext var3, MatrixStack var4, OrderedRenderCommandQueue var5, int var6);

   @Shadow
   protected abstract void renderArmHoldingItem(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, float var5, Arm var6);

   @Shadow
   protected abstract void renderMapInOneHand(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, Arm var5, float var6, ItemStack var7);

   @Shadow
   protected abstract void renderMapInBothHands(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, float var4, float var5, float var6);

   @Shadow
   protected abstract void applyEatOrDrinkTransformation(MatrixStack var1, float var2, Arm var3, ItemStack var4, PlayerEntity var5);

   @Shadow
   private void applySwingOffset(MatrixStack MatrixStackVar, Arm ArmVar, float f) {
   }

   @Shadow
   private void applyEquipOffset(MatrixStack MatrixStackVar, Arm ArmVar, float f) {
   }

   @Inject(require = 0, method = "applySwingOffset", at = @At("HEAD"), cancellable = true)
   private void applySwingOffset_tr(MatrixStack MatrixStackVar, Arm ArmVar, float f, CallbackInfo callbackInfo) {
      CustomHand customHand = ModuleRegistry.CUSTOM_HAND;
      if (customHand.k() && customHand.hasCustomSwingAnimation()) {
         int i = ArmVar == Arm.RIGHT ? 1 : -1;
         float fA = customHand.swingStrength.a() * 10.0F;
         MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0F + MathHelper.sin(f * f * (float) Math.PI) * (-fA / 4.0F))));
         float fSin = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
         MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * fSin * -(fA / 4.0F)));
         MatrixStackVar.multiply(RotationAxis.POSITIVE_X.rotationDegrees(fSin * -fA));
         MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * -45.0F));
      } else {
         int i2 = ArmVar == Arm.RIGHT ? 1 : -1;
         MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i2 * (45.0F + MathHelper.sin(f * f * (float) Math.PI) * -20.0F)));
         float fMethod_153742 = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
         MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i2 * fMethod_153742 * -20.0F));
         MatrixStackVar.multiply(RotationAxis.POSITIVE_X.rotationDegrees(fMethod_153742 * -80.0F));
         MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i2 * -45.0F));
      }

      callbackInfo.cancel();
   }

   @Inject(require = 0, method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
   private void applyEquipOffset_tr(MatrixStack MatrixStackVar, Arm ArmVar, float f, CallbackInfo callbackInfo) {
      CustomHand customHand = ModuleRegistry.CUSTOM_HAND;
      int i = ArmVar == Arm.RIGHT ? 1 : -1;
      if (customHand.k()) {
         float fA;
         float fA2;
         float fA3;
         float fA4;
         if (ArmVar == this.client.options.getMainArm().getValue()) {
            fA = customHand.mainHandScale.a();
            fA2 = customHand.mainHandOffsetX.a();
            fA3 = customHand.mainHandOffsetY.a();
            fA4 = customHand.mainHandOffsetZ.a();
         } else {
            fA = customHand.offHandScale.a();
            fA2 = customHand.offHandOffsetX.a();
            fA3 = customHand.offHandOffsetY.a();
            fA4 = customHand.offHandOffsetZ.a();
         }

         float f2 = i * 0.56F;
         float f3 = !customHand.isNoAnimationMode() && customHand.hasCustomSwingAnimation() ? 0.0F : -0.6F;
         MatrixStackVar.translate(f2, -0.52F + f * f3, -0.72F);
         MatrixStackVar.scale(fA, fA, fA);
         MatrixStackVar.translate(fA2, fA3, fA4);
      } else {
         MatrixStackVar.translate(i * 0.56F, -0.52F + f * -0.6F, -0.72F);
      }

      callbackInfo.cancel();
   }

   @Inject(require = 0, method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
   private void renderFirstPersonItem(
      AbstractClientPlayerEntity AbstractClientPlayerEntityVar,
      float f,
      float f2,
      Hand HandVar,
      float f3,
      ItemStack ItemStackVar,
      float f4,
      MatrixStack MatrixStackVar,
      OrderedRenderCommandQueue commandQueue,
      int i,
      CallbackInfo callbackInfo
   ) {
      HandRenderState.renderingHand = true;
      HandRenderState.renderingMace = MaceHelper.isMace(ItemStackVar);

      try {
         if (!AbstractClientPlayerEntityVar.isUsingSpyglass()) {
            boolean z = HandVar == Hand.MAIN_HAND;
            Arm ArmVarGetMainArm = z ? AbstractClientPlayerEntityVar.getMainArm() : AbstractClientPlayerEntityVar.getMainArm().getOpposite();
            CustomHand customHand = ModuleRegistry.CUSTOM_HAND;
            MatrixStackVar.push();
            if (ItemStackVar.isEmpty()) {
               if (z && !AbstractClientPlayerEntityVar.isInvisible()) {
                  this.renderArmHoldingItem(MatrixStackVar, commandQueue, i, f4, f3, ArmVarGetMainArm);
               }
            } else if (ItemStackVar.isOf(Items.FILLED_MAP)) {
               if (z && this.offHand.isEmpty()) {
                  this.renderMapInBothHands(MatrixStackVar, commandQueue, i, f2, f4, f3);
               } else {
                  this.renderMapInOneHand(MatrixStackVar, commandQueue, i, f4, ArmVarGetMainArm, f3, ItemStackVar);
               }
            } else if (ItemStackVar.isOf(Items.CROSSBOW)) {
               boolean zIsCharged = CrossbowItem.isCharged(ItemStackVar);
               boolean z2 = ArmVarGetMainArm == Arm.RIGHT;
               int i2 = z2 ? 1 : -1;
               if (AbstractClientPlayerEntityVar.isUsingItem()
                  && AbstractClientPlayerEntityVar.getItemUseTimeLeft() > 0
                  && AbstractClientPlayerEntityVar.getActiveHand() == HandVar) {
                  this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                  MatrixStackVar.translate(i2 * -0.4785682F, -0.094387F, 0.05731531F);
                  MatrixStackVar.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
                  MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i2 * 65.3F));
                  MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i2 * -9.785F));
                  float fGetMaxUseTime = ItemStackVar.getMaxUseTime(AbstractClientPlayerEntityVar) - (AbstractClientPlayerEntityVar.getItemUseTimeLeft() - f + 1.0F);
                  float fGetPullTime = fGetMaxUseTime / CrossbowItem.getPullTime(ItemStackVar, AbstractClientPlayerEntityVar);
                  if (fGetPullTime > 1.0F) {
                     fGetPullTime = 1.0F;
                  }

                  if (fGetPullTime > 0.1F) {
                     float fSin = MathHelper.sin((fGetMaxUseTime - 0.1F) * 1.3F) * (fGetPullTime - 0.1F);
                     MatrixStackVar.translate(fSin * 0.0F, fSin * 0.004F, fSin * 0.0F);
                  }

                  MatrixStackVar.translate(fGetPullTime * 0.0F, fGetPullTime * 0.0F, fGetPullTime * 0.04F);
                  MatrixStackVar.scale(1.0F, 1.0F, 1.0F + fGetPullTime * 0.2F);
                  MatrixStackVar.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(i2 * 45.0F));
               } else {
                  MatrixStackVar.translate(
                     i2 * -0.4F * MathHelper.sin(MathHelper.sqrt(f3) * (float) Math.PI),
                     0.2F * MathHelper.sin(MathHelper.sqrt(f3) * (float) (Math.PI * 2)),
                     -0.2F * MathHelper.sin(f3 * (float) Math.PI)
                  );
                  this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                  this.applySwingOffset(MatrixStackVar, ArmVarGetMainArm, f3);
                  if (zIsCharged && f3 < 0.001F && z) {
                     MatrixStackVar.translate(i2 * -0.641864F, 0.0F, 0.0F);
                     MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i2 * 10.0F));
                  }
               }

               this.renderItem(AbstractClientPlayerEntityVar, ItemStackVar, z2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, MatrixStackVar, commandQueue, i);
            } else {
               boolean z3 = ArmVarGetMainArm == Arm.RIGHT;
               if (AbstractClientPlayerEntityVar.isUsingItem()
                  && AbstractClientPlayerEntityVar.getItemUseTimeLeft() > 0
                  && AbstractClientPlayerEntityVar.getActiveHand() == HandVar) {
                  int i3 = z3 ? 1 : -1;
                  switch (HeldItemRendererMixin.AnonymousClass1.$SwitchMap$net$minecraft$item$consume$UseAction[ItemStackVar.getUseAction().ordinal()]) {
                     case 1:
                        this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                        break;
                     case 2:
                     case 3:
                        this.applyEatOrDrinkTransformation(MatrixStackVar, f, ArmVarGetMainArm, ItemStackVar, AbstractClientPlayerEntityVar);
                        this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                        break;
                     case 4:
                        this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                        break;
                     case 5:
                        this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                        MatrixStackVar.translate(i3 * -0.2785682F, 0.18344387F, 0.15731531F);
                        MatrixStackVar.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
                        MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i3 * 35.3F));
                        MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i3 * -9.785F));
                        float fMethod_79352 = ItemStackVar.getMaxUseTime(AbstractClientPlayerEntityVar)
                           - (AbstractClientPlayerEntityVar.getItemUseTimeLeft() - f + 1.0F);
                        float f5 = fMethod_79352 / 20.0F;
                        float f6 = (f5 * f5 + f5 * 2.0F) / 3.0F;
                        if (f6 > 1.0F) {
                           f6 = 1.0F;
                        }

                        if (f6 > 0.1F) {
                           float fMethod_153742 = MathHelper.sin((fMethod_79352 - 0.1F) * 1.3F) * (f6 - 0.1F);
                           MatrixStackVar.translate(fMethod_153742 * 0.0F, fMethod_153742 * 0.004F, fMethod_153742 * 0.0F);
                        }

                        MatrixStackVar.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        MatrixStackVar.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        MatrixStackVar.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(i3 * 45.0F));
                        break;
                     case 6:
                        this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                        MatrixStackVar.translate(i3 * -0.5F, 0.7F, 0.1F);
                        MatrixStackVar.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                        MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i3 * 35.3F));
                        MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i3 * -9.785F));
                        float fMethod_79353 = ItemStackVar.getMaxUseTime(AbstractClientPlayerEntityVar)
                           - (AbstractClientPlayerEntityVar.getItemUseTimeLeft() - f + 1.0F);
                        float f7 = fMethod_79353 / 10.0F;
                        if (f7 > 1.0F) {
                           f7 = 1.0F;
                        }

                        if (f7 > 0.1F) {
                           float fMethod_153743 = MathHelper.sin((fMethod_79353 - 0.1F) * 1.3F) * (f7 - 0.1F);
                           MatrixStackVar.translate(fMethod_153743 * 0.0F, fMethod_153743 * 0.004F, fMethod_153743 * 0.0F);
                        }

                        MatrixStackVar.translate(0.0F, 0.0F, f7 * 0.2F);
                        MatrixStackVar.scale(1.0F, 1.0F, 1.0F + f7 * 0.2F);
                        MatrixStackVar.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(i3 * 45.0F));
                  }
               } else if (AbstractClientPlayerEntityVar.isUsingRiptide()) {
                  this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                  int i4 = z3 ? 1 : -1;
                  MatrixStackVar.translate(i4 * -0.4F, 0.8F, 0.3F);
                  MatrixStackVar.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i4 * 65.0F));
                  MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i4 * -85.0F));
               } else {
                  float fMethod_153744 = -0.4F * MathHelper.sin(MathHelper.sqrt(f3) * (float) Math.PI);
                  float fMethod_153745 = 0.2F * MathHelper.sin(MathHelper.sqrt(f3) * (float) (Math.PI * 2));
                  float fMethod_153746 = -0.2F * MathHelper.sin(f3 * (float) Math.PI);
                  int i5 = z3 ? 1 : -1;
                  if (customHand == null || !customHand.k() || customHand.isNoAnimationMode()) {
                     MatrixStackVar.translate(i5 * fMethod_153744, fMethod_153745, fMethod_153746);
                  }

                  this.applyEquipOffset(MatrixStackVar, ArmVarGetMainArm, f4);
                  boolean z4 = HandVar == Hand.MAIN_HAND;
                  if (customHand != null && customHand.k() && z4 && !customHand.isNoAnimationMode() && customHand.hasCustomSwingAnimation()) {
                     customHand.applyCustomSwing(
                        MatrixStackVar, f3, () -> this.applySwingOffset(MatrixStackVar, ArmVarGetMainArm, f3), ArmVarGetMainArm == Arm.LEFT
                     );
                  } else {
                     this.applySwingOffset(MatrixStackVar, ArmVarGetMainArm, f3);
                  }
               }

               this.renderItem(AbstractClientPlayerEntityVar, ItemStackVar, z3 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, MatrixStackVar, commandQueue, i);
            }

            MatrixStackVar.pop();
            callbackInfo.cancel();
            return;
         }

         callbackInfo.cancel();
      } finally {
         HandRenderState.renderingHand = false;
         HandRenderState.renderingMace = false;
      }
   }

   static class AnonymousClass1 {
      static final int[] $SwitchMap$net$minecraft$item$consume$UseAction = new int[UseAction.values().length];

      static {
         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError var7) {
         }

         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.EAT.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
         }

         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.DRINK.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
         }

         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.BLOCK.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.BOW.ordinal()] = 5;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.SPEAR.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$net$minecraft$item$consume$UseAction[UseAction.BRUSH.ordinal()] = 7;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
