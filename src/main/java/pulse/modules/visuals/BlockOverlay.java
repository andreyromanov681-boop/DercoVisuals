package pulse.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.HitResult.class_240;
import net.minecraft.VertexConsumerProvider.class_4598;
import org.joml.Matrix4f;
import pulse.events.BlockOutlineEvent;
import pulse.events.WorldRenderEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.render.RenderSystemHelper;
import pulse.render.system.ClientPipelines;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Block Overlay", b = "Красиво выделяет блок, на который наведен игрок", c = ModuleCategory.VISUALS)
public class BlockOverlay extends ClientModule {
   private static final String FILL_NORMAL = "Обычная";
   private static final String FILL_SHADER = "Шейдер";
   private static final String ANIMATION_NONE = "Нет";
   private static final String ANIMATION_PULSE = "Пульсация";
   private static final String ANIMATION_WAVE = "Волна";
   private static final String SHADER_NEBULA = "Небула";
   private static final String SHADER_STARS = "Звёзды";
   private static final String SHADER_WEB = "Паутина";
   private static final String SHADER_PLASMA = "Плазма";
   private final SettingGroup outlineGroup = new SettingGroup("Обводка");
   private final BooleanSetting outlineEnabled = new BooleanSetting("Обводка", true);
   private final SliderSetting lineWidth;
   private final SettingGroup fillGroup;
   private final BooleanSetting fillEnabled;
   private final ModeSetting fillType;
   private final SliderSetting fillAlpha;
   private final ModeSetting shaderType;
   private final SliderSetting shaderSpeed;
   private final SliderSetting shaderAlpha;
   private final SettingGroup animationGroup;
   private final ModeSetting animationMode;
   private final SettingGroup colorGroup;
   private final BooleanSetting useClientColor;
   private final ColorSetting customColor;

   public BlockOverlay() {
      SliderSetting sliderSetting = new SliderSetting("Толщина линий", 2.0F, 1.0F, 5.0F, 0.5F);
      BooleanSetting booleanSetting = this.outlineEnabled;
      this.lineWidth = sliderSetting.a(booleanSetting::a);
      this.fillGroup = new SettingGroup("Заливка");
      this.fillEnabled = new BooleanSetting("Заливка", true);
      ModeSetting modeSetting = new ModeSetting("Тип заливки", new String[]{"Обычная", "Шейдер"}, "Обычная");
      BooleanSetting booleanSetting2 = this.fillEnabled;
      this.fillType = modeSetting.a(booleanSetting2::a);
      this.fillAlpha = new SliderSetting("Прозрачность заливки", 0.3F, 0.1F, 1.0F, 0.05F).a(() -> this.fillEnabled.a() && this.fillType.b("Обычная"));
      this.shaderType = new ModeSetting("Шейдер", new String[]{"Небула", "Звёзды", "Паутина", "Плазма"}, "Небула")
         .a(() -> this.fillEnabled.a() && this.fillType.b("Шейдер"));
      this.shaderSpeed = new SliderSetting("Скорость анимации", 1.0F, 0.1F, 3.0F, 0.1F).a(() -> this.fillEnabled.a() && this.fillType.b("Шейдер"));
      this.shaderAlpha = new SliderSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.05F).a(() -> this.fillEnabled.a() && this.fillType.b("Шейдер"));
      this.animationGroup = new SettingGroup("Анимация");
      this.animationMode = new ModeSetting("Режим анимации", new String[]{"Нет", "Пульсация", "Волна"}, "Нет");
      this.colorGroup = new SettingGroup("Цвет");
      this.useClientColor = new BooleanSetting("Цвет клиента", true);
      this.customColor = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> !this.useClientColor.a());
   }

   @EventHandler
   public void onBlockOutline(BlockOutlineEvent blockOutlineEvent) {
      blockOutlineEvent.b();
   }

   @EventHandler
   public void onWorldRender(WorldRenderEvent event) {
      HitResult hitResult = c.crosshairTarget;
      if (hitResult != null && hitResult.getType() == class_240.BLOCK) {
         BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
         BlockState BlockStateVarBlockState = c.world.getBlockState(blockPos);
         if (BlockStateVarBlockState == null || BlockStateVarBlockState.isAir()) {
            return;
         }

         Camera camera = c.gameRenderer.getCamera();
         double cameraX = camera.getCameraPos().x;
         double cameraY = camera.getCameraPos().y;
         double cameraZ = camera.getCameraPos().z;
         Box BoxVarOffset = BlockStateVarBlockState.getOutlineShape(c.world, blockPos)
            .getBoundingBox()
            .offset(blockPos.getX() - cameraX, blockPos.getY() - cameraY, blockPos.getZ() - cameraZ);
         this.renderOverlay(event.matrices(), this.applyPrimaryAnimation(BoxVarOffset), this.overlayColor(), cameraX, cameraY, cameraZ);
         if (this.animationMode.b("Волна")) {
            this.renderWave(event.matrices(), BoxVarOffset, this.overlayColor(), cameraX, cameraY, cameraZ);
         }
      }
   }

   private Box applyPrimaryAnimation(Box BoxVar) {
      return !this.animationMode.b("Пульсация") ? BoxVar : scaleBox(BoxVar, 1.0F + (float)((Math.sin(System.currentTimeMillis() / 260.0) + 1.0) * 0.5) * 0.08F);
   }

   private void renderOverlay(MatrixStack MatrixStackVar, Box BoxVar, Color color, double cameraX, double cameraY, double cameraZ) {
      MatrixStackVar.push();
      BufferAllocator allocator = new BufferAllocator(524288);
      class_4598 imm = VertexConsumerProvider.immediate(allocator);
      if (this.fillEnabled.a()) {
         this.drawFill(MatrixStackVar, imm, BoxVar, color, cameraX, cameraY, cameraZ);
      }

      if (this.outlineEnabled.a() && !"Шейдер".equals(this.fillType.d())) {
         this.drawOutline(MatrixStackVar, imm, BoxVar, color, 1.0F, this.lineWidth.a());
      }

      imm.draw();
      MatrixStackVar.pop();
      allocator.close();
   }

   private void renderWave(MatrixStack MatrixStackVar, Box BoxVar, Color color, double cameraX, double cameraY, double cameraZ) {
      float fCurrentTimeMillis = (float)(System.currentTimeMillis() % 1000L) / 1000.0F;
      Box BoxVarScaleBox = scaleBox(BoxVar, 1.0F + fCurrentTimeMillis * 0.3F);
      MatrixStackVar.push();
      BufferAllocator allocator = new BufferAllocator(65536);
      class_4598 imm = VertexConsumerProvider.immediate(allocator);
      this.drawOutline(MatrixStackVar, imm, BoxVarScaleBox, color, 1.0F - fCurrentTimeMillis, Math.max(1.0F, this.lineWidth.a() - 0.5F));
      imm.draw();
      MatrixStackVar.pop();
      allocator.close();
   }

   private void drawOutline(MatrixStack MatrixStackVar, class_4598 imm, Box BoxVar, Color color, float f, float f2) {
      RenderSystemHelper.lineWidth(f2);
      VertexConsumer buf = imm.getBuffer(ClientPipelines.OUTLINE_THROUGH);
      addBoxLines(buf, MatrixStackVar.peek().getPositionMatrix(), BoxVar, color, f);
   }

   private void drawFill(MatrixStack MatrixStackVar, class_4598 imm, Box BoxVar, Color color, double cameraX, double cameraY, double cameraZ) {
      VertexConsumer buf = imm.getBuffer(ClientPipelines.QUAD_THROUGH);
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      if (this.fillType.b("Шейдер")) {
         this.addShaderBoxQuads(buf, matrix4fGetPositionMatrix, BoxVar, this.shaderAlpha.a(), cameraX, cameraY, cameraZ);
      } else {
         addBoxQuads(buf, matrix4fGetPositionMatrix, BoxVar, color, this.fillAlpha.a());
      }
   }

   private Color overlayColor() {
      return this.useClientColor.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.customColor.a();
   }

   private Color shaderColor(float f, float f2, float f3, float f4) {
      float t = (float)(System.currentTimeMillis() % 100000L) / 1000.0F * this.shaderSpeed.a();
      String strD = this.shaderType.d();
      if ("Небула".equals(strD)) {
         return this.shaderNebula(f, f2, f3, t, f4);
      } else if ("Звёзды".equals(strD)) {
         return this.shaderStars(f, f2, f3, t, f4);
      } else if ("Паутина".equals(strD)) {
         return this.shaderWeb(f, f2, f3, t, f4);
      } else {
         return "Плазма".equals(strD) ? this.shaderPlasma(f, f2, f3, t, f4) : new Color(128, 128, 128, Math.round(f4 * 255.0F));
      }
   }

   private float noise2d(float x, float y) {
      float n = (float)Math.sin(x * 12.9898F + y * 78.233F) * 43758.547F;
      return n - (float)Math.floor(n);
   }

   private float smoothNoise(float x, float y) {
      float ix = (float)Math.floor(x);
      float iy = (float)Math.floor(y);
      float fx = x - ix;
      float fy = y - iy;
      fx = fx * fx * (3.0F - 2.0F * fx);
      fy = fy * fy * (3.0F - 2.0F * fy);
      float a = this.noise2d(ix, iy);
      float b = this.noise2d(ix + 1.0F, iy);
      float c = this.noise2d(ix, iy + 1.0F);
      float d = this.noise2d(ix + 1.0F, iy + 1.0F);
      return a + (b - a) * fx + (c - a) * fy + (a - b - c + d) * fx * fy;
   }

   private float fbm(float x, float y, int octaves) {
      float value = 0.0F;
      float amplitude = 0.5F;

      for (int i = 0; i < octaves; i++) {
         value += amplitude * this.smoothNoise(x, y);
         x *= 2.0F;
         y *= 2.0F;
         amplitude *= 0.5F;
      }

      return value;
   }

   private Color shaderNebula(float x, float y, float z, float t, float alpha) {
      float u = x * 6.0F + z * 4.0F;
      float v = y * 5.0F + z * 3.0F;
      float nR = this.fbm(u * 0.8F + t * 0.4F, v * 0.9F + t * 0.15F, 4);
      float nG = this.fbm(u * 0.6F - t * 0.3F + 10.0F, v * 0.7F + t * 0.25F + 7.0F, 4);
      float nB = this.fbm(u * 0.7F + t * 0.2F + 20.0F, v * 0.8F - t * 0.35F + 15.0F, 4);
      float density = this.fbm(u * 0.5F + t * 0.18F + 30.0F, v * 0.5F - t * 0.12F + 25.0F, 5);
      density = clampF(density * 1.8F - 0.3F, 0.0F, 1.0F);
      float r = 0.25F + density * (nR * 0.35F + 0.1F);
      float g = 0.22F + density * (nG * 0.15F + 0.03F);
      float b = 0.28F + density * (nB * 0.4F + 0.15F);
      float bright = clampF((nR + nB - 0.8F) * 2.0F, 0.0F, 1.0F) * density;
      r += bright * 0.2F;
      g += bright * 0.15F;
      b += bright * 0.12F;
      float starField = this.noise2d(x * 47.3F + z * 31.7F, y * 53.1F);
      if (starField > 0.97F) {
         float flicker = (float)(Math.sin(t * 5.0F + starField * 150.0F) * 0.3F + 0.7F);
         float starBright = (starField - 0.97F) / 0.03F * flicker * 0.4F;
         r = Math.min(1.0F, r + starBright);
         g = Math.min(1.0F, g + starBright * 0.85F);
         b = Math.min(1.0F, b + starBright);
      }

      int ri = Math.round(clampF(r, 0.0F, 1.0F) * 255.0F);
      int gi = Math.round(clampF(g, 0.0F, 1.0F) * 255.0F);
      int bi = Math.round(clampF(b, 0.0F, 1.0F) * 255.0F);
      return new Color(ri, gi, bi, Math.round(alpha * 255.0F));
   }

   private Color shaderStars(float x, float y, float z, float t, float alpha) {
      float u = x * 5.0F + z * 4.0F;
      float v = y * 5.0F + z * 2.0F;
      float gradNoise = this.fbm(u * 0.4F + t * 0.08F, v * 0.4F - t * 0.05F, 4);
      float purpleAmount = clampF(gradNoise * 1.6F - 0.2F, 0.0F, 1.0F);
      float cycleTime = 10.0F;
      float phase = (float)(Math.sin(t * ((Math.PI * 2) / cycleTime)) * 0.5 + 0.5);
      float purpleMix = purpleAmount * (0.4F + phase * 0.6F);
      float r = 0.02F + purpleMix * 0.28F;
      float g = 0.02F + purpleMix * 0.03F;
      float b = 0.04F + purpleMix * 0.35F;
      float hash1 = this.noise2d(x * 31.7F + z * 17.3F, y * 23.1F);
      float hash2 = this.noise2d(x * 53.1F - z * 41.9F, y * 67.3F);
      if (hash1 > 0.95F) {
         float brightness = (hash1 - 0.95F) / 0.05F;
         float twinkle = (float)(Math.sin(t * (2.0F + hash2 * 4.0F) + hash1 * 60.0F) * 0.4F + 0.6F);
         float star = brightness * twinkle;
         r = Math.min(1.0F, r + star * 0.9F);
         g = Math.min(1.0F, g + star * 0.85F);
         b = Math.min(1.0F, b + star * 0.7F);
      }

      for (int i = 0; i < 5; i++) {
         float seed = this.noise2d(i * 73.1F, i * 37.9F);
         float shootX = this.noise2d(i * 127.3F, seed * 99.1F);
         float shootZ = this.noise2d(i * 83.7F, seed * 61.3F);
         float period = 3.0F + seed * 4.0F;
         float shootPhase = (t + seed * 50.0F) % period / period;
         float starU = shootX * 6.0F - 1.0F;
         float starV = (1.0F - shootPhase) * 6.0F - 1.0F + shootZ * 2.0F;
         float normU = u / 5.0F;
         float normV = v / 5.0F;
         float dx = normU - starU;
         float dy = normV - starV;
         float dist = (float)Math.sqrt(dx * dx + dy * dy);
         float glowSize = 0.3F + seed * 0.2F;
         if (dist < glowSize) {
            float intensity = 1.0F - dist / glowSize;
            intensity = intensity * intensity * (0.7F + shootPhase * 0.3F);
            float fadeOut = clampF(1.0F - (shootPhase - 0.7F) / 0.3F, 0.0F, 1.0F);
            intensity *= fadeOut;
            r = Math.min(1.0F, r + intensity * 0.95F);
            g = Math.min(1.0F, g + intensity * 0.5F);
            b = Math.min(1.0F, b + intensity * 0.1F);
         }

         float trailLen = 0.5F;
         float trailU = starU;
         float trailV = starV + trailLen * 0.5F;
         float tdx = normU - trailU;
         float tdy = normV - trailV;
         float tdist = (float)Math.sqrt(tdx * tdx + tdy * tdy);
         if (tdist < glowSize * 0.6F && tdy > 0.0F && tdy < trailLen) {
            float trailIntensity = (1.0F - tdist / (glowSize * 0.6F)) * 0.4F;
            float fadeOut = clampF(1.0F - (shootPhase - 0.7F) / 0.3F, 0.0F, 1.0F);
            trailIntensity *= fadeOut;
            r = Math.min(1.0F, r + trailIntensity * 0.8F);
            g = Math.min(1.0F, g + trailIntensity * 0.35F);
            b = Math.min(1.0F, b + trailIntensity * 0.05F);
         }
      }

      int ri = Math.round(clampF(r, 0.0F, 1.0F) * 255.0F);
      int gi = Math.round(clampF(g, 0.0F, 1.0F) * 255.0F);
      int bi = Math.round(clampF(b, 0.0F, 1.0F) * 255.0F);
      return new Color(ri, gi, bi, Math.round(alpha * 255.0F));
   }

   private Color shaderWeb(float x, float y, float z, float t, float alpha) {
      float u = (x + z) * 3.0F;
      float v = y * 3.0F;
      float line1 = (float)Math.sin(u * 4.0F + t * 0.8F + (float)Math.sin(v * 2.5F + t * 0.3F) * 1.5F);
      float line2 = (float)Math.sin(v * 3.5F - t * 0.6F + (float)Math.sin(u * 2.0F - t * 0.4F) * 1.2F);
      float line3 = (float)Math.sin((u + v) * 2.5F + t * 0.5F + (float)Math.cos((u - v) * 1.8F + t * 0.2F) * 1.0F);
      float line4 = (float)Math.cos(u * 3.0F - v * 2.0F + t * 0.7F);
      float w1 = (float)Math.pow(Math.max(0.0, 1.0 - Math.abs(line1) * 1.8), 3.0);
      float w2 = (float)Math.pow(Math.max(0.0, 1.0 - Math.abs(line2) * 1.8), 3.0);
      float w3 = (float)Math.pow(Math.max(0.0, 1.0 - Math.abs(line3) * 2.0), 3.0);
      float w4 = (float)Math.pow(Math.max(0.0, 1.0 - Math.abs(line4) * 2.2), 3.0);
      float web = Math.min(1.0F, w1 + w2 + w3 + w4);
      float baseTone = 0.15F + web * 0.7F;
      float tint = (float)(Math.sin(t * 0.4F) * 0.5 + 0.5);
      float r = clampF(baseTone * (0.85F + tint * 0.15F), 0.0F, 1.0F);
      float g = clampF(baseTone * (0.82F + tint * 0.08F), 0.0F, 1.0F);
      float b = clampF(baseTone * (0.9F + (1.0F - tint) * 0.1F), 0.0F, 1.0F);
      int ri = Math.round(r * 255.0F);
      int gi = Math.round(g * 255.0F);
      int bi = Math.round(b * 255.0F);
      return new Color(ri, gi, bi, Math.round(alpha * 255.0F));
   }

   private Color shaderPlasma(float x, float y, float z, float t, float alpha) {
      float u = (x + z) * 2.0F;
      float v = y * 2.0F;
      float p1 = (float)Math.sin(u * 1.5F + t * 0.6F);
      float p2 = (float)Math.sin(v * 1.8F + t * 0.5F);
      float p3 = (float)Math.sin((u + v) * 1.2F + t * 0.4F);
      float p4 = (float)Math.sin((float)Math.sqrt(u * u + v * v) * 2.0F - t * 0.7F);
      float p5 = (float)Math.sin(u * 2.5F - t * 0.3F) * (float)Math.cos(v * 1.5F + t * 0.2F);
      float plasma = (p1 + p2 + p3 + p4 + p5) / 5.0F;
      plasma = plasma * 0.5F + 0.5F;
      float hueBase = 0.06F;
      float hueRange = 0.08F;
      float hue = hueBase + plasma * hueRange;
      float cyclePhase = (float)(Math.sin(t * 0.25F) * 0.5 + 0.5);
      hue += cyclePhase * 0.04F;
      hue = wrap(hue);
      float saturation = 0.75F + plasma * 0.2F;
      float brightness = 0.6F + plasma * 0.4F;
      Color c = Color.getHSBColor(hue, saturation, brightness);
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.round(alpha * 255.0F));
   }

   private static float clampF(float val, float min, float max) {
      return Math.max(min, Math.min(max, val));
   }

   private static Box scaleBox(Box BoxVar, float f) {
      Vec3d Vec3dVarGetCenter = BoxVar.getCenter();
      double d = (BoxVar.maxX - BoxVar.minX) * 0.5 * f;
      double d2 = (BoxVar.maxY - BoxVar.minY) * 0.5 * f;
      double d3 = (BoxVar.maxZ - BoxVar.minZ) * 0.5 * f;
      return new Box(
         Vec3dVarGetCenter.x - d,
         Vec3dVarGetCenter.y - d2,
         Vec3dVarGetCenter.z - d3,
         Vec3dVarGetCenter.x + d,
         Vec3dVarGetCenter.y + d2,
         Vec3dVarGetCenter.z + d3
      );
   }

   private static void addBoxLines(VertexConsumer buf, Matrix4f matrix4f, Box BoxVar, Color color, float f) {
      float f2 = (float)BoxVar.minX;
      float f3 = (float)BoxVar.minY;
      float f4 = (float)BoxVar.minZ;
      float f5 = (float)BoxVar.maxX;
      float f6 = (float)BoxVar.maxY;
      float f7 = (float)BoxVar.maxZ;
      line(buf, matrix4f, f2, f3, f4, f5, f3, f4, color, f);
      line(buf, matrix4f, f2, f3, f4, f2, f6, f4, color, f);
      line(buf, matrix4f, f2, f3, f4, f2, f3, f7, color, f);
      line(buf, matrix4f, f5, f6, f7, f2, f6, f7, color, f);
      line(buf, matrix4f, f5, f6, f7, f5, f3, f7, color, f);
      line(buf, matrix4f, f5, f6, f7, f5, f6, f4, color, f);
      line(buf, matrix4f, f2, f6, f4, f5, f6, f4, color, f);
      line(buf, matrix4f, f5, f3, f4, f5, f3, f7, color, f);
      line(buf, matrix4f, f2, f3, f7, f5, f3, f7, color, f);
      line(buf, matrix4f, f2, f6, f7, f2, f6, f4, color, f);
      line(buf, matrix4f, f2, f6, f7, f2, f3, f7, color, f);
      line(buf, matrix4f, f5, f6, f4, f5, f3, f4, color, f);
   }

   private static void addBoxQuads(VertexConsumer buf, Matrix4f matrix4f, Box BoxVar, Color color, float f) {
      float f2 = (float)BoxVar.minX;
      float f3 = (float)BoxVar.minY;
      float f4 = (float)BoxVar.minZ;
      float f5 = (float)BoxVar.maxX;
      float f6 = (float)BoxVar.maxY;
      float f7 = (float)BoxVar.maxZ;
      quad(buf, matrix4f, color, f, f2, f3, f7, f5, f3, f7, f5, f6, f7, f2, f6, f7);
      quad(buf, matrix4f, color, f, f2, f6, f4, f5, f6, f4, f5, f3, f4, f2, f3, f4);
      quad(buf, matrix4f, color, f, f2, f3, f4, f2, f3, f7, f2, f6, f7, f2, f6, f4);
      quad(buf, matrix4f, color, f, f5, f6, f4, f5, f6, f7, f5, f3, f7, f5, f3, f4);
      quad(buf, matrix4f, color, f, f2, f6, f4, f2, f6, f7, f5, f6, f7, f5, f6, f4);
      quad(buf, matrix4f, color, f, f5, f3, f4, f5, f3, f7, f2, f3, f7, f2, f3, f4);
   }

   private void addShaderBoxQuads(VertexConsumer buf, Matrix4f matrix4f, Box BoxVar, float f, double cameraX, double cameraY, double cameraZ) {
      float x0 = (float)BoxVar.minX;
      float y0 = (float)BoxVar.minY;
      float z0 = (float)BoxVar.minZ;
      float x1 = (float)BoxVar.maxX;
      float y1 = (float)BoxVar.maxY;
      float z1 = (float)BoxVar.maxZ;
      int res = 12;
      this.tessShaderFace(buf, matrix4f, f, cameraX, cameraY, cameraZ, res, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
      this.tessShaderFace(buf, matrix4f, f, cameraX, cameraY, cameraZ, res, x0, y1, z0, x1, y1, z0, x1, y0, z0, x0, y0, z0);
      this.tessShaderFace(buf, matrix4f, f, cameraX, cameraY, cameraZ, res, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
      this.tessShaderFace(buf, matrix4f, f, cameraX, cameraY, cameraZ, res, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0);
      this.tessShaderFace(buf, matrix4f, f, cameraX, cameraY, cameraZ, res, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
      this.tessShaderFace(buf, matrix4f, f, cameraX, cameraY, cameraZ, res, x1, y0, z0, x1, y0, z1, x0, y0, z1, x0, y0, z0);
   }

   private void tessShaderFace(
      VertexConsumer buf,
      Matrix4f mat,
      float alpha,
      double cx,
      double cy,
      double cz,
      int res,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float dx,
      float dy,
      float dz,
      float ex,
      float ey,
      float ez
   ) {
      for (int i = 0; i < res; i++) {
         float u0 = (float)i / res;
         float u1 = (float)(i + 1) / res;

         for (int j = 0; j < res; j++) {
            float v0 = (float)j / res;
            float v1 = (float)(j + 1) / res;
            float px0 = bilerp(ax, bx, dx, ex, u0, v0);
            float py0 = bilerp(ay, by, dy, ey, u0, v0);
            float pz0 = bilerp(az, bz, dz, ez, u0, v0);
            float px1 = bilerp(ax, bx, dx, ex, u1, v0);
            float py1 = bilerp(ay, by, dy, ey, u1, v0);
            float pz1 = bilerp(az, bz, dz, ez, u1, v0);
            float px2 = bilerp(ax, bx, dx, ex, u1, v1);
            float py2 = bilerp(ay, by, dy, ey, u1, v1);
            float pz2 = bilerp(az, bz, dz, ez, u1, v1);
            float px3 = bilerp(ax, bx, dx, ex, u0, v1);
            float py3 = bilerp(ay, by, dy, ey, u0, v1);
            float pz3 = bilerp(az, bz, dz, ez, u0, v1);
            vertex(buf, mat, px0, py0, pz0, this.shaderColor((float)(px0 + cx), (float)(py0 + cy), (float)(pz0 + cz), alpha), alpha);
            vertex(buf, mat, px1, py1, pz1, this.shaderColor((float)(px1 + cx), (float)(py1 + cy), (float)(pz1 + cz), alpha), alpha);
            vertex(buf, mat, px2, py2, pz2, this.shaderColor((float)(px2 + cx), (float)(py2 + cy), (float)(pz2 + cz), alpha), alpha);
            vertex(buf, mat, px3, py3, pz3, this.shaderColor((float)(px3 + cx), (float)(py3 + cy), (float)(pz3 + cz), alpha), alpha);
         }
      }
   }

   private static void line(VertexConsumer buf, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, Color color, float f7) {
      buf.vertex(matrix4f, f, f2, f3).color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f7));
      buf.vertex(matrix4f, f4, f5, f6).color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f7));
   }

   private static void quad(
      VertexConsumer buf,
      Matrix4f matrix4f,
      Color color,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12,
      float f13
   ) {
      vertex(buf, matrix4f, f2, f3, f4, color, f);
      vertex(buf, matrix4f, f5, f6, f7, color, f);
      vertex(buf, matrix4f, f8, f9, f10, color, f);
      vertex(buf, matrix4f, f11, f12, f13, color, f);
   }

   private static float bilerp(float a, float b, float c, float d, float u, float v) {
      return a * (1.0F - u) * (1.0F - v) + b * u * (1.0F - v) + c * u * v + d * (1.0F - u) * v;
   }

   private static void vertex(VertexConsumer buf, Matrix4f matrix4f, float f, float f2, float f3, Color color, float f4) {
      buf.vertex(matrix4f, f, f2, f3).color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * f4));
   }

   private static Color withAlpha(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.round(f * 255.0F));
   }

   private static float wrap(float f) {
      float f2 = f % 1.0F;
      return f2 < 0.0F ? f2 + 1.0F : f2;
   }
}
