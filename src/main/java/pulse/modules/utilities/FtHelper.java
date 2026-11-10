package pulse.modules.utilities;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.HitResult.class_240;
import net.minecraft.RaycastContext.class_242;
import net.minecraft.RaycastContext.class_3960;
import net.minecraft.MatrixStack.class_4665;
import net.minecraft.VertexConsumerProvider.class_4598;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferRenderer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import pulse.core.Bool;
import pulse.entity.EntityFinder;
import pulse.entity.EntityTargetType;
import pulse.events.ClientTickEvent;
import pulse.events.SoundPlayEvent;
import pulse.events.WorldRenderEvent;
import pulse.gui.friends.FriendLookup;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.modules.hud.ClientColor;
import pulse.render.RenderSystemHelper;
import pulse.render.system.ClientPipelines;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "FT Helper", b = "Предпросмотр зоны действия донат-предметов", c = ModuleCategory.UTILITIES)
public class FtHelper extends ClientModule {
   private final BooleanSetting a = new BooleanSetting("Трапка", true);
   private final BooleanSetting b = new BooleanSetting("Пласт", true);
   private final BooleanSetting e = new BooleanSetting("Дезориентация", true);
   private final BooleanSetting f = new BooleanSetting("Явная пыль", true);
   private final BooleanSetting g = new BooleanSetting("Снежок заморозки", true);
   private final SettingGroup h = new SettingGroup("Настройки трапки");
   private final BooleanSetting i = new BooleanSetting("Таймер действия", true);
   private final BooleanSetting j = new BooleanSetting("Зона драконьей трапки", false);
   private final SettingGroup k = new SettingGroup("Настройки снежка");
   private final SliderSetting l = new SliderSetting("Жирность линии", 2.5F, 0.5F, 10.0F);
   private final SettingGroup m = new SettingGroup("Цвет");
   private final BooleanSetting n = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting o = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> Bool.from(!this.n.k() ? 1 : 0));
   private boolean p = false;
   private long q = 0L;
   private int r = 0;
   private long s = 0L;
   private boolean t = false;
   private boolean u = false;
   private Vec3d v = null;
   private final List<Vec3d> w = new CopyOnWriteArrayList<>();

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      if (c.world != null && c.player != null) {
         MatrixStack MatrixStackVarA = worldRenderEvent.a();
         float fB = worldRenderEvent.b();
         Item item = c.player.getMainHandStack().getItem();
         Item offhand = c.player.getOffHandStack().getItem();
         if (this.a.k() && (item == Items.NETHERITE_SCRAP || offhand == Items.NETHERITE_SCRAP)) {
            this.renderScrap(worldRenderEvent);
         }

         if (this.b.k() && (item == Items.DRIED_KELP || offhand == Items.DRIED_KELP)) {
            this.renderKelp(worldRenderEvent);
         }

         if (this.e.k() && (item == Items.ENDER_EYE || offhand == Items.ENDER_EYE)) {
            this.renderEye(worldRenderEvent);
         }

         if (this.f.k() && (item == Items.SUGAR || offhand == Items.SUGAR)) {
            this.renderSugar(worldRenderEvent);
         }

         if (this.g.k() && (item == Items.SNOWBALL || offhand == Items.SNOWBALL)) {
            this.renderSnowball(worldRenderEvent);
         }
      }
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.world != null && c.player != null) {
         if (this.i.k()) {
            int iN = this.n();
            boolean zIsCoolingDown = c.player.getItemCooldownManager().isCoolingDown(Items.NETHERITE_SCRAP.getDefaultStack());
            if (zIsCoolingDown && !this.u) {
               long jCurrentTimeMillis = System.currentTimeMillis();
               int i = jCurrentTimeMillis - this.s < 500L ? 1 : 0;
               this.p = true;
               this.q = jCurrentTimeMillis;
               this.t = Bool.from(i);
            }

            this.u = zIsCoolingDown;
            this.r = iN;
         }

         if (this.p && this.i.k()) {
            this.o();
         }
      }
   }

   @EventHandler
   public void a(SoundPlayEvent soundPlayEvent) {
      if (soundPlayEvent.d().getId().toString().equals("minecraft:entity.ender_dragon.growl")) {
         this.s = System.currentTimeMillis();
      }
   }

   private void a(MatrixStack MatrixStackVar, float f) {
      this.w.clear();
      this.a(f);
      if (this.w.size() >= 2) {
         Vec3d cameraPos = c.gameRenderer.getCamera().getCameraPos();
         Matrix4f matrix = MatrixStackVar.peek().getPositionMatrix();
         MatrixStackVar.push();
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         RenderSystemHelper.disableCull();
         RenderSystemHelper.depthMask(false);
         RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         RenderSystemHelper.lineWidth(this.l.a());
         Color color = this.p();
         int red = color.getRed();
         int green = color.getGreen();
         int blue = color.getBlue();
         int alpha = color.getAlpha();
         BufferBuilder bb = Tessellator.getInstance().begin(class_5596.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

         for (Vec3d point : this.w) {
            Vec3d rel = point.subtract(cameraPos);
            bb.vertex(matrix, (float)rel.x, (float)rel.y, (float)rel.z).color(red, green, blue, alpha);
         }

         BufferRenderer.drawWithGlobalProgram(bb.end());
         RenderSystemHelper.lineWidth(1.0F);
         GL11.glDisable(2848);
         RenderSystemHelper.enableCull();
         RenderSystemHelper.depthMask(true);
         RenderSystemHelper.disableBlend();
         MatrixStackVar.pop();
      }
   }

   private void a(MatrixStack MatrixStackVar, boolean z) {
   }

   private Vec3d a(float f) {
      Vec3d Vec3dVarGetCameraPosVec = c.player.getCameraPosVec(f);
      Vec3d Vec3dVarB = this.b(f);
      Vec3d Vec3dVarAdd = Vec3dVarGetCameraPosVec;
      this.w.add(Vec3dVarAdd);

      for (int i = 0; i < 200; i++) {
         Vec3d Vec3dVar = Vec3dVarAdd;
         Vec3dVarAdd = Vec3dVarAdd.add(Vec3dVarB);
         BlockHitResult BlockHitResultVarRaycast = c.world
            .raycast(new RaycastContext(Vec3dVar, Vec3dVarAdd, class_3960.COLLIDER, class_242.NONE, c.player));
         if (BlockHitResultVarRaycast.getType() != class_240.MISS) {
            this.w.add(BlockHitResultVarRaycast.getPos());
            return BlockHitResultVarRaycast.getPos();
         }

         if (Vec3dVarAdd.y < c.world.getBottomY()) {
            this.w.add(Vec3dVarAdd);
            return Vec3dVarAdd;
         }

         this.w.add(Vec3dVarAdd);
         Vec3dVarB = Vec3dVarB.multiply(0.99).subtract(0.0, 0.03, 0.0);
      }

      return null;
   }

   private Vec3d b(float f) {
      return this.a(c.player.getYaw(), c.player.getPitch())
         .multiply(1.5)
         .add(new Vec3d(0.0, c.player.getVelocity().y * 0.5, 0.0));
   }

   private Vec3d a(float f, float f2) {
      float fCos = MathHelper.cos(f2 * (float) (Math.PI / 180.0));
      float fSin = MathHelper.sin(f2 * (float) (Math.PI / 180.0));
      float fMethod_153622 = MathHelper.cos(f * (float) (Math.PI / 180.0));
      return new Vec3d(-MathHelper.sin(f * (float) (Math.PI / 180.0)) * fCos, -fSin, fMethod_153622 * fCos).normalize();
   }

   private int n() {
      int i = 0;

      for (int i2 = 0; i2 < c.player.getInventory().size(); i2++) {
         ItemStack ItemStackVarGetStack = c.player.getInventory().getStack(i2);
         if (ItemStackVarGetStack.getItem() == Items.NETHERITE_SCRAP) {
            int iGetCount = ItemStackVarGetStack.getCount();
            int i3 = i;
            i = 2 * (i3 | iGetCount) - (i3 ^ iGetCount);
         }
      }

      return i;
   }

   private void o() {
      int i = this.t ? 30000 : 15000;
      if (System.currentTimeMillis() - this.q >= i) {
         this.p = false;
         this.t = false;
      } else {
         c.inGameHud
            .setOverlayMessage(
               Text.of(
                  String.format(
                     "%s закончится через §b%.1f§f сек.", this.t ? "Драконья трапка" : "Трапка", (i - (System.currentTimeMillis() - this.q)) / 1000.0
                  )
               ),
               false
            );
      }
   }

   private void renderScrap(WorldRenderEvent event) {
      MatrixStack MatrixStackVar = event.a();
      BlockPos pos = c.player.getBlockPos();
      Box box;
      if (this.j.k()) {
         box = new Box(
            pos.getX() - 3 + 0.01,
            pos.getY() + 0.01,
            pos.getZ() - 3 + 0.01,
            pos.getX() + 4 - 0.01,
            pos.getY() + 6 - 0.01,
            pos.getZ() + 4 - 0.01
         );
      } else {
         box = new Box(
            pos.getX() - 2 + 0.01,
            pos.getY() + 0.01,
            pos.getZ() - 2 + 0.01,
            pos.getX() + 3 - 0.01,
            pos.getY() + 4 - 0.01,
            pos.getZ() + 3 - 0.01
         );
      }

      Color color = this.a(box) ? new Color(0, 255, 0) : this.p();
      this.drawGodweerBox(MatrixStackVar, event.bufferSource(), box, color);
   }

   private void renderKelp(WorldRenderEvent event) {
      MatrixStack MatrixStackVar = event.a();
      BlockPos pos = c.player.getBlockPos();
      float yaw = c.player.getYaw();
      float pitch = c.player.getPitch();
      Color color = this.p();
      boolean isBig = this.j.k();
      if (Math.abs(pitch) > 45.0F) {
         Box box;
         if (pitch < -45.0F) {
            if (isBig) {
               box = new Box(
                  pos.getX() - 3 + 0.01,
                  pos.getY() + 3 + 0.01,
                  pos.getZ() - 3 + 0.01,
                  pos.getX() + 4 - 0.01,
                  pos.getY() + 7 - 0.01,
                  pos.getZ() + 4 - 0.01
               );
            } else {
               box = new Box(
                  pos.getX() - 2 + 0.01,
                  pos.getY() + 3 + 0.01,
                  pos.getZ() - 2 + 0.01,
                  pos.getX() + 3 - 0.01,
                  pos.getY() + 5 - 0.01,
                  pos.getZ() + 3 - 0.01
               );
            }
         } else if (isBig) {
            box = new Box(
               pos.getX() - 3 + 0.01,
               pos.getY() - 3 + 0.01,
               pos.getZ() - 3 + 0.01,
               pos.getX() + 4 - 0.01,
               pos.getY() - 1 - 0.01,
               pos.getZ() + 4 - 0.01
            );
         } else {
            box = new Box(
               pos.getX() - 2 + 0.01,
               pos.getY() - 3 + 0.01,
               pos.getZ() - 2 + 0.01,
               pos.getX() + 3 - 0.01,
               pos.getY() - 1 - 0.01,
               pos.getZ() + 3 - 0.01
            );
         }

         this.drawGodweerBox(MatrixStackVar, event.bufferSource(), box, this.a(box) ? new Color(0, 255, 0) : color);
      } else {
         float f = (yaw % 360.0F + 360.0F) % 360.0F;
         if (!(Math.abs(f - 45.0F) < 22.0F) && !(Math.abs(f - 135.0F) < 22.0F) && !(Math.abs(f - 225.0F) < 22.0F) && !(Math.abs(f - 315.0F) < 22.0F)) {
            boolean isAxisX = false;
            BlockPos wallCenter;
            if (f >= 315.0F || f < 45.0F) {
               wallCenter = pos.add(0, -1, 3);
               isAxisX = true;
            } else if (f >= 45.0F && f < 135.0F) {
               wallCenter = pos.add(-3, -1, 0);
               isAxisX = false;
            } else if (f >= 135.0F && f < 225.0F) {
               wallCenter = pos.add(0, -1, -3);
               isAxisX = true;
            } else {
               wallCenter = pos.add(3, -1, 0);
               isAxisX = false;
            }

            Box box;
            if (isAxisX) {
               if (isBig) {
                  box = new Box(
                     wallCenter.getX() - 3 + 0.01,
                     wallCenter.getY() + 0.01,
                     wallCenter.getZ() - 1 + 0.01,
                     wallCenter.getX() + 4 - 0.01,
                     wallCenter.getY() + 7 - 0.01,
                     wallCenter.getZ() + 2 - 0.01
                  );
               } else {
                  box = new Box(
                     wallCenter.getX() - 2 + 0.01,
                     wallCenter.getY() + 0.01,
                     wallCenter.getZ() - 1 + 0.01,
                     wallCenter.getX() + 3 - 0.01,
                     wallCenter.getY() + 5 - 0.01,
                     wallCenter.getZ() + 2 - 0.01
                  );
               }
            } else if (isBig) {
               box = new Box(
                  wallCenter.getX() - 1 + 0.01,
                  wallCenter.getY() + 0.01,
                  wallCenter.getZ() - 3 + 0.01,
                  wallCenter.getX() + 2 - 0.01,
                  wallCenter.getY() + 7 - 0.01,
                  wallCenter.getZ() + 4 - 0.01
               );
            } else {
               box = new Box(
                  wallCenter.getX() - 1 + 0.01,
                  wallCenter.getY() + 0.01,
                  wallCenter.getZ() - 2 + 0.01,
                  wallCenter.getX() + 2 - 0.01,
                  wallCenter.getY() + 5 - 0.01,
                  wallCenter.getZ() + 3 - 0.01
               );
            }

            this.drawGodweerBox(MatrixStackVar, event.bufferSource(), box, this.a(box) ? new Color(0, 255, 0) : color);
         } else {
            float fAbs = Math.abs(f - 45.0F);
            float fAbs2 = Math.abs(f - 135.0F);
            float fAbs3 = Math.abs(f - 225.0F);
            float fMin = Math.min(Math.min(fAbs, fAbs2), Math.min(fAbs3, Math.abs(f - 315.0F)));
            int i3 = fMin == fAbs ? 45 : (fMin == fAbs2 ? 135 : (fMin == fAbs3 ? 225 : 315));
            int i = i3 != 45 && i3 != 135 ? 2 : -3;
            int i2 = i3 != 45 && i3 != 315 ? -2 : 2;
            BlockPos center = pos.add(i, -1, i2);
            ArrayList<Box> boxes = new ArrayList<>();
            int i4 = isBig ? -3 : -2;
            int i5 = isBig ? 3 : 2;
            int i6 = isBig ? 7 : 5;
            if (i3 != 45 && i3 != 225) {
               for (int i9 = i4; i9 <= i5; i9++) {
                  for (int i10 = 0; i10 < i6; i10++) {
                     boxes.add(new Box(center.add(i9, i10, -i9)));
                     boxes.add(new Box(center.add(i9 + 1, i10, -i9)));
                  }
               }
            } else {
               for (int i7 = i4; i7 <= i5; i7++) {
                  for (int i8 = 0; i8 < i6; i8++) {
                     boxes.add(new Box(center.add(i7, i8, i7)));
                     boxes.add(new Box(center.add(i7 + 1, i8, i7)));
                  }
               }
            }

            boolean anyInTrap = false;

            for (Box bx : boxes) {
               if (this.a(bx)) {
                  anyInTrap = true;
                  break;
               }
            }

            Color finalColor = anyInTrap ? new Color(0, 255, 0) : color;

            for (Box bx : boxes) {
               this.drawGodweerBox(MatrixStackVar, event.bufferSource(), bx, finalColor);
            }
         }
      }
   }

   private void renderEye(WorldRenderEvent event) {
      Vec3d center = this.c(event.b());
      Color color = this.a(center, 10.0) ? new Color(0, 255, 0) : this.p();
      this.drawGodweerCircle(event.a(), event.bufferSource(), center, 10.0F, 0.05F, color);
   }

   private void renderSugar(WorldRenderEvent event) {
      Vec3d center = this.c(event.b());
      Color color = this.a(center, 10.0) ? new Color(0, 255, 0) : this.p();
      this.drawGodweerCircle(event.a(), event.bufferSource(), center, 10.0F, 0.05F, color);
   }

   private void renderSnowball(WorldRenderEvent event) {
      this.w.clear();
      this.a(event.b());
      if (this.w.size() >= 2) {
         MatrixStack stack = event.a();
         class_4598 imm = event.bufferSource();
         class_4665 entry = stack.peek();
         Matrix4f mat = entry.getPositionMatrix();
         Color color = this.p();
         int r = color.getRed();
         int g = color.getGreen();
         int b = color.getBlue();
         int a = color.getAlpha();
         VertexConsumer buf = imm.getBuffer(ClientPipelines.OUTLINE_NO);
         Vec3d cam = c.gameRenderer.getCamera().getCameraPos();

         for (int i = 0; i < this.w.size() - 1; i++) {
            Vec3d p1 = this.w.get(i).subtract(cam);
            Vec3d p2 = this.w.get(i + 1).subtract(cam);
            buf.vertex(mat, (float)p1.x, (float)p1.y, (float)p1.z)
               .color(r, g, b, a)
               .normal(entry, 0.0F, 1.0F, 0.0F);
            buf.vertex(mat, (float)p2.x, (float)p2.y, (float)p2.z)
               .color(r, g, b, a)
               .normal(entry, 0.0F, 1.0F, 0.0F);
         }
      }
   }

   private Vec3d c(float f) {
      return new Vec3d(
         c.player.lastRenderX + (c.player.getX() - c.player.lastRenderX) * f,
         c.player.lastRenderY + (c.player.getY() - c.player.lastRenderY) * f,
         c.player.lastRenderZ + (c.player.getZ() - c.player.lastRenderZ) * f
      );
   }

   private boolean a(Box BoxVar) {
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

   private boolean a(Vec3d Vec3dVar, double d) {
      double d2 = d * d;

      for (PlayerEntity PlayerEntityVar : EntityFinder.a(
         EntityVar -> Bool.from(EntityVar instanceof PlayerEntity && !(EntityVar.squaredDistanceTo(Vec3dVar) > d2) ? 1 : 0), EntityTargetType.PLAYER
      )) {
         if (PlayerEntityVar != c.player && !FriendLookup.a(PlayerEntityVar.getName().getString())) {
            return true;
         }
      }

      return false;
   }

   private boolean a(List<BlockPos> list) {
      for (PlayerEntity PlayerEntityVar : EntityFinder.a(EntityVar -> EntityVar instanceof PlayerEntity, EntityTargetType.PLAYER)) {
         if (PlayerEntityVar != c.player && !FriendLookup.a(PlayerEntityVar.getName().getString())) {
            BlockPos BlockPosVarGetBlockPos = PlayerEntityVar.getBlockPos();
            Iterator<BlockPos> it2 = list.iterator();

            while (it2.hasNext()) {
               if (BlockPosVarGetBlockPos.equals(it2.next())) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private Color p() {
      if (this.n.k()) {
         ClientColor clientColor = ModuleRegistry.CLIENT_COLOR;
         if (ModuleRegistry.CLIENT_COLOR != null) {
            return clientColor.n();
         }
      }

      return this.o.k();
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
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

   private void drawGodweerCircle(MatrixStack stack, class_4598 imm, Vec3d pos, float radius, float yOffset, Color cVar) {
      class_4665 entry = stack.peek();
      Matrix4f mat = entry.getPositionMatrix();
      int r = cVar.getRed();
      int g = cVar.getGreen();
      int b = cVar.getBlue();
      int a = cVar.getAlpha();
      float x = (float)(pos.x - c.gameRenderer.getCamera().getCameraPos().x);
      float y = (float)(pos.y + yOffset - c.gameRenderer.getCamera().getCameraPos().y);
      float z = (float)(pos.z - c.gameRenderer.getCamera().getCameraPos().z);
      VertexConsumer buf = imm.getBuffer(ClientPipelines.OUTLINE_NO);
      int CIRCLE_SEGS = 64;

      for (int i = 0; i < CIRCLE_SEGS; i++) {
         double angle1 = (Math.PI * 2) / CIRCLE_SEGS * i;
         double angle2 = (Math.PI * 2) / CIRCLE_SEGS * (i + 1);
         float x1 = x + (float)(Math.cos(angle1) * radius);
         float z1 = z + (float)(Math.sin(angle1) * radius);
         float x2 = x + (float)(Math.cos(angle2) * radius);
         float z2 = z + (float)(Math.sin(angle2) * radius);
         buf.vertex(mat, x1, y, z1).color(r, g, b, a).normal(entry, 0.0F, 1.0F, 0.0F);
         buf.vertex(mat, x2, y, z2).color(r, g, b, a).normal(entry, 0.0F, 1.0F, 0.0F);
      }
   }
}
