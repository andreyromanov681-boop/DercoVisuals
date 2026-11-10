// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.cosmetic;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import dev.enizer6569.dercovisuals.discord.CosmeticHelper;

public class CosmeticFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   private final CosmeticRenderController b = CosmeticRenderController.a();
   private static final String[] a = new String[]{"crypt", "crypt"};
   private static boolean c = false;

   public CosmeticFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> FeatureRendererContextVar) {
      super(FeatureRendererContextVar);
   }

   public void render(MatrixStack MatrixStackVar, OrderedRenderCommandQueue commandQueue, int i, PlayerEntityRenderState PlayerEntityRenderStateVar, float f, float f2) {
      PlayerEntityModel var7 = (PlayerEntityModel)this.getContextModel();
      CosmeticHelper.renderCosmetics(MatrixStackVar, commandQueue, i, var7, f2);
   }

   private AbstractClientPlayerEntity a(MinecraftClient MinecraftClientVar, int i) {
      return null;
   }

   private List<CosmeticModel> a(AbstractClientPlayerEntity AbstractClientPlayerEntityVar) {
      return Collections.emptyList();
   }

   private Integer a(AbstractClientPlayerEntity AbstractClientPlayerEntityVar, String str, MinecraftClient MinecraftClientVar) {
      return null;
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
