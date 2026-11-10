// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.awt.Color;
import java.util.Locale;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.text.Text;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.MatrixStack.class_4665;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.entity.EntityFinder;
import dev.enizer6569.dercovisuals.entity.EntityTargetType;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.WorldRenderEvent;
import dev.enizer6569.dercovisuals.gui.friends.FriendLookup;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.render.system.ClientPipelines;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;

@ModuleInfo(a = "HW Helper", b = "Помощник с таймерами и трапками для HolyWorld", c = ModuleCategory.UTILITIES)
public class HwHelper extends ClientModule {
   private static final long MESSAGE_DURATION_MS = 4000L;
   private boolean stunTrapCooldownActive;
   private long stunTrapStartMs;
   private boolean explosiveTrapCooldownActive;
   private long explosiveTrapStartMs;
   private final BooleanSetting trackItems = new BooleanSetting("Отслеживать предметы", true);
   private final BooleanSetting showStunTrap = new BooleanSetting("Стан трапка (Призмарин)", true);
   private final BooleanSetting showExplosiveTrap = new BooleanSetting("Трапка 2 (Хорус)", true);
   private final SettingGroup renderGroup = new SettingGroup("Рендер");
   private final BooleanSetting renderNearbyTrapBox = new BooleanSetting("Отображать зону трапки", true);
   private int lastStunTrapCount = 0;
   private int lastExplosiveTrapCount = 0;

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      if (c.world != null && c.player != null && this.renderNearbyTrapBox.k()) {
         ItemStack mainHand = c.player.getMainHandStack();
         ItemStack offHand = c.player.getOffHandStack();
         Item item = mainHand.isEmpty() ? offHand.getItem() : mainHand.getItem();
         if (!this.showStunTrap.k() || item != Items.PRISMARINE_SHARD && offHand.getItem() != Items.PRISMARINE_SHARD) {
            if (this.showExplosiveTrap.k() && (item == Items.POPPED_CHORUS_FRUIT || offHand.getItem() == Items.POPPED_CHORUS_FRUIT)) {
               this.renderTrapBox(worldRenderEvent, 3, 6);
            }
         } else {
            this.renderTrapBox(worldRenderEvent, 3, 6);
         }
      }
   }

   private void renderTrapBox(WorldRenderEvent event, int r, int h) {
      MatrixStack stack = event.a();
      BlockPos pos = c.player.getBlockPos();
      Box box = new Box(
         pos.getX() - r + 0.01,
         pos.getY() + 0.01,
         pos.getZ() - r + 0.01,
         pos.getX() + r + 1 - 0.01,
         pos.getY() + h - 0.01,
         pos.getZ() + r + 1 - 0.01
      );
      Color color = this.isEnemyInBox(box) ? new Color(0, 255, 0) : new Color(255, 255, 255);
      this.drawGodweerBox(stack, event.bufferSource(), box, color);
   }

   private boolean isEnemyInBox(Box BoxVar) {
      for (PlayerEntity PlayerEntityVar : EntityFinder.a(
         EntityVar -> Bool.from(EntityVar instanceof PlayerEntity && BoxVar.expand(0.5, 0.5, 0.5).intersects(EntityVar.getBoundingBox()) ? 1 : 0),
         EntityTargetType.PLAYER
      )) {
         if (PlayerEntityVar != c.player && !FriendLookup.a(PlayerEntityVar.getName().getString())) {
            return true;
         }
      }

      return false;
   }

   private void drawGodweerBox(MatrixStack stack, class_4598 imm, Box box, Color cVar) {
      float x1 = (float)(box.minX - c.gameRenderer.getCamera().getCameraPos().x);
      float y1 = (float)(box.minY - c.gameRenderer.getCamera().getCameraPos().y);
      float z1 = (float)(box.minZ - c.gameRenderer.getCamera().getCameraPos().z);
      float x2 = (float)(box.maxX - c.gameRenderer.getCamera().getCameraPos().x);
      float y2 = (float)(box.maxY - c.gameRenderer.getCamera().getCameraPos().y);
      float z2 = (float)(box.maxZ - c.gameRenderer.getCamera().getCameraPos().z);
      Matrix4f mat = stack.peek().getPositionMatrix();
      int r = cVar.getRed();
      int g = cVar.getGreen();
      int b = cVar.getBlue();
      int a = 35;
      VertexConsumer bufFill = imm.getBuffer(ClientPipelines.QUAD);
      this.quad(bufFill, mat, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
      this.quad(bufFill, mat, r, g, b, a, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2);
      this.quad(bufFill, mat, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1);
      this.quad(bufFill, mat, r, g, b, a, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
      this.quad(bufFill, mat, r, g, b, a, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
      this.quad(bufFill, mat, r, g, b, a, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1);
      VertexConsumer bufLine = imm.getBuffer(ClientPipelines.OUTLINE_NO);
      class_4665 entry = stack.peek();
      int la = cVar.getAlpha();
      bufLine.vertex(mat, x1, y1, z1).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x1, y2, z1).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x2, y1, z1).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x2, y2, z1).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x1, y1, z2).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x1, y2, z2).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x2, y1, z2).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x2, y2, z2).color(r, g, b, la).normal(entry, 0.0F, 1.0F, 0.0F);
      bufLine.vertex(mat, x1, y1, z1).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x2, y1, z1).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x1, y1, z2).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x2, y1, z2).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x1, y2, z1).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x2, y2, z1).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x1, y2, z2).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x2, y2, z2).color(r, g, b, la).normal(entry, 1.0F, 0.0F, 0.0F);
      bufLine.vertex(mat, x1, y1, z1).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x1, y1, z2).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x2, y1, z1).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x2, y1, z2).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x1, y2, z1).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x1, y2, z2).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x2, y2, z1).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
      bufLine.vertex(mat, x2, y2, z2).color(r, g, b, la).normal(entry, 0.0F, 0.0F, 1.0F);
   }

   private void quad(
      VertexConsumer buf,
      Matrix4f mat,
      int r,
      int g,
      int b,
      int a,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float x4,
      float y4,
      float z4
   ) {
      buf.vertex(mat, x1, y1, z1).color(r, g, b, a);
      buf.vertex(mat, x2, y2, z2).color(r, g, b, a);
      buf.vertex(mat, x3, y3, z3).color(r, g, b, a);
      buf.vertex(mat, x4, y4, z4).color(r, g, b, a);
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.world != null && c.player != null && this.trackItems.k()) {
         this.updateTrackedItem(Items.PRISMARINE_SHARD, this.showStunTrap.k(), true);
         this.updateTrackedItem(Items.POPPED_CHORUS_FRUIT, this.showExplosiveTrap.k(), false);
         this.showOverlayMessage();
      }
   }

   private void updateTrackedItem(Item ItemVar, boolean z, boolean z2) {
      int iCountItem = this.countItem(ItemVar);
      boolean zIsCoolingDown = c.player.getItemCooldownManager().isCoolingDown(ItemVar.getDefaultStack());
      int i = z2 ? this.lastStunTrapCount : this.lastExplosiveTrapCount;
      if (z && zIsCoolingDown && iCountItem < i) {
         if (z2) {
            this.stunTrapCooldownActive = true;
            this.stunTrapStartMs = System.currentTimeMillis();
         } else {
            this.explosiveTrapCooldownActive = true;
            this.explosiveTrapStartMs = System.currentTimeMillis();
         }
      }

      if (z2) {
         this.lastStunTrapCount = iCountItem;
      } else {
         this.lastExplosiveTrapCount = iCountItem;
      }
   }

   private int countItem(Item ItemVar) {
      int iGetCount = 0;

      for (int i = 0; i < c.player.getInventory().size(); i++) {
         ItemStack ItemStackVarGetStack = c.player.getInventory().getStack(i);
         if (ItemStackVarGetStack.getItem() == ItemVar) {
            iGetCount += ItemStackVarGetStack.getCount();
         }
      }

      return iGetCount;
   }

   private void showOverlayMessage() {
      long jCurrentTimeMillis = System.currentTimeMillis();
      String cooldownMessage = this.getCooldownMessage("Стан трапка", jCurrentTimeMillis, this.stunTrapStartMs, this.stunTrapCooldownActive);
      if (cooldownMessage == null) {
         this.stunTrapCooldownActive = false;
      }

      String cooldownMessage2 = this.getCooldownMessage("Взрывная трапка", jCurrentTimeMillis, this.explosiveTrapStartMs, this.explosiveTrapCooldownActive);
      if (cooldownMessage2 == null) {
         this.explosiveTrapCooldownActive = false;
      }

      String str;
      if (cooldownMessage != null && cooldownMessage2 != null) {
         str = cooldownMessage + " | " + cooldownMessage2;
      } else {
         str = cooldownMessage != null ? cooldownMessage : cooldownMessage2;
      }

      if (str != null) {
         c.inGameHud.setOverlayMessage(Text.of(str), false);
      }
   }

   private String getCooldownMessage(String str, long j, long j2, boolean z) {
      if (!z) {
         return null;
      }

      long j3 = 4000L - (j - j2);
      return j3 <= 0L ? null : str + ": " + String.format(Locale.US, "%.1f", (float)j3 / 1000.0F) + "с";
   }
}
