// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.discord;

import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.ClientPlayerEntity;
import dev.enizer6569.dercovisuals.cosmetic.CosmeticModel;
import dev.enizer6569.dercovisuals.cosmetic.CosmeticRenderController;
import dev.enizer6569.dercovisuals.cosmetic.CosmeticRepository;

public class CosmeticHelper {
   public static void renderCosmetics(MatrixStack var0, Object var1, int var2, PlayerEntityModel var3, float var4) {
      try {
         MinecraftClient var5 = MinecraftClient.getInstance();
         if (var5 == null) {
            return;
         }

         ClientPlayerEntity var6 = var5.player;
         if (var6 == null) {
            return;
         }

         Map var7 = CosmeticRepository.a().b();
         if (var7.isEmpty()) {
            return;
         }

         CosmeticRenderController var8 = CosmeticRenderController.a();

         for (CosmeticModel var10 : var7.values()) {
            try {
               var8.a(var10, var6, var0, (VertexConsumerProvider)var1, var2, var3, var4);
            } catch (ClassCastException var14) {
               try {
                  var8.a(var10, var6, var0, (VertexConsumerProvider)var1, var2, var3, var4);
               } catch (Exception var13) {
               }
            } catch (Exception var15) {
            }
         }
      } catch (Exception var16) {
      }
   }
}
