package pulse.modules.visuals;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.gl.ShaderProgramKeys;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import pulse.animation.AnimationState;
import pulse.animation.Easing;
import pulse.core.Bool;
import pulse.entity.EntityUtils;
import pulse.events.WorldRenderEvent;
import pulse.hud.core.HudServiceRegistry;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.modules.hud.ClientColor;
import pulse.render.RenderSystemHelper;
import pulse.render.shader.PulseShaderProgram;
import pulse.render.shader.ShaderLibrary;
import pulse.render.system.ClientPipelines;
import pulse.render.world.WorldRenderUtils;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;
import pulse.util.ColorUtils;

@ModuleInfo(a = "Target ESP", b = "Отображает ESP вокруг цели", c = ModuleCategory.VISUALS)
public class TargetEsp extends ClientModule {
   public static int a;
   public static boolean b;
   private final ModeSetting e = new ModeSetting("Режим", new String[]{"Призраки", "Круг", "Квадратик", "Орбита", "Кольца"}, "Кольца");
   private final SettingGroup f = new SettingGroup("Призраки").a(() -> this.e.b("Призраки"));
   private final SliderSetting g = new SliderSetting("Скорость анимации", 1.5F, 0.5F, 5.0F, 0.1F).a(() -> this.e.b("Призраки"));
   private final SliderSetting h = new SliderSetting("Размер частиц", 0.25F, 0.05F, 0.5F, 0.01F).a(() -> this.e.b("Призраки"));
   private final SliderSetting i = new SliderSetting("Количество призраков", 4.0F, 2.0F, 6.0F, 1.0F).a(() -> this.e.b("Призраки"));
   private final SettingGroup j = new SettingGroup("Круг").a(() -> this.e.b("Круг"));
   private final SliderSetting k = new SliderSetting("Скорость анимации", 1.5F, 0.5F, 5.0F, 0.1F).a(() -> this.e.b("Круг"));
   private final SettingGroup l = new SettingGroup("Квадратик").a(() -> this.e.b("Квадратик"));
   private final SliderSetting m = new SliderSetting("Скорость анимации", 2.5F, 0.5F, 5.0F, 0.1F).a(() -> this.e.b("Квадратик"));
   private final SliderSetting n = new SliderSetting("Размер квадратика", 1.4F, 0.5F, 2.0F, 0.1F).a(() -> this.e.b("Квадратик"));
   private final SettingGroup o = new SettingGroup("Орбита").a(() -> this.e.b("Орбита"));
   private final ModeSetting p = new ModeSetting("Форма", new String[]{"Стрелки", "Ромбы", "Кубы"}, "Стрелки").a(() -> this.e.b("Орбита"));
   private final SliderSetting q = new SliderSetting("Скорость анимации", 1.5F, 0.5F, 5.0F, 0.1F).a(() -> this.e.b("Орбита"));
   private final SliderSetting r = new SliderSetting("Фигур по кругу", 3.0F, 2.0F, 8.0F, 1.0F).a(() -> this.e.b("Орбита"));
   private final SliderSetting s = new SliderSetting("Слоёв по высоте", 3.0F, 2.0F, 5.0F, 1.0F).a(() -> this.e.b("Орбита"));
   private final SliderSetting t = new SliderSetting("Отступ между слоями", 1.0F, 0.3F, 2.0F, 0.05F).a(() -> this.e.b("Орбита"));
   private final SliderSetting u = new SliderSetting("Дистанция", 0.9F, 0.5F, 2.0F, 0.05F).a(() -> this.e.b("Орбита"));
   private final SliderSetting v = new SliderSetting("Размер фигур", 0.2F, 0.08F, 0.4F, 0.01F).a(() -> this.e.b("Орбита"));
   private final BooleanSetting w = new BooleanSetting("Вращение", true).a(() -> this.e.b("Орбита"));
   private final BooleanSetting x = new BooleanSetting("Шейдер", true).a(() -> this.e.b("Орбита"));
   private final ModeSetting y = new ModeSetting("Тип шейдера", new String[]{"Небула", "Звёзды", "Паутина", "Плазма"}, "Небула")
      .a(() -> this.e.b("Орбита") && this.x.a() ? true : false);
   private final SettingGroup C = new SettingGroup("Цвет");
   private final BooleanSetting D = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting E = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> Bool.from(this.D.a() ? 0 : 1));
   private final SettingGroup F = new SettingGroup("При ударе");
   private final BooleanSetting G = new BooleanSetting("Включить", Bool.from(-1673835725));
   private final BooleanSetting H = new BooleanSetting("Изменять цвет", true).a(() -> this.G.a());
   private final ColorSetting I = new ColorSetting("Цвет урона", new Color(255, 50, 50)).a(() -> Bool.from(this.G.a() && this.H.a() ? 1 : 0));
   private final BooleanSetting J = new BooleanSetting("Ускорять анимацию", true).a(() -> this.G.a());
   private final SliderSetting K = new SliderSetting("Множитель ускорения", 3.0F, 1.2F, 5.0F, 0.1F).a(() -> Bool.from(this.G.a() && this.J.a() ? 1 : 0));
   private final SliderSetting L = new SliderSetting("Длительность эффекта", 0.8F, 0.3F, 2.0F, 0.1F).a(() -> Bool.from(this.G.a() && this.J.a() ? 1 : 0));
   private final Map<LivingEntity, AnimationState> M = new HashMap<>();
   private final Identifier N = Identifier.of("pulse", "textures/target.png");
   private final BooleanSetting targetPlayers = new BooleanSetting("Players", true);
   private final BooleanSetting targetMobs = new BooleanSetting("Mobs", true);
   private final Map<LivingEntity, Integer> O = new HashMap<>();
   private final Map<LivingEntity, Long> P = new HashMap<>();
   private final Map<LivingEntity, Double> Q = new HashMap<>();
   private final Map<LivingEntity, Double> R = new HashMap<>();
   private long S = System.currentTimeMillis();

   @EventHandler
   public void a(WorldRenderEvent worldRenderEvent) {
      long lastTime = this.S;
      this.S = System.currentTimeMillis();
      float fMin = Math.min((float)(this.S - lastTime) / 1000.0F, 0.1F);
      LivingEntity target = this.p();
      if (!this.a(target)) {
         this.clearRenderState();
      } else {
         if (this.M.size() != 1 || !this.M.containsKey(target)) {
            this.clearRenderState();
         }

         AnimationState animation = this.M.computeIfAbsent(target, ignored -> new AnimationState());
         animation.a(1.0, 0.5, Easing.f, true);
         animation.a();
         this.a(target, fMin);
         float alpha = (float)animation.j();
         switch (this.e.d()) {
            case "Призраки":
               this.b(worldRenderEvent, target, alpha);
               break;
            case "Круг":
               this.c(worldRenderEvent, target, alpha);
               break;
            case "Квадратик":
               this.a(worldRenderEvent, target, alpha);
               break;
            case "Орбита":
               this.d(worldRenderEvent, target, alpha);
               break;
            case "Кольца":
               this.c(worldRenderEvent, target, alpha);
         }
      }
   }

   private void clearRenderState() {
      this.M.clear();
      this.O.clear();
      this.P.clear();
      this.Q.clear();
      this.R.clear();
   }

   private void a(LivingEntity LivingEntityVar, float f) {
      float fC = this.c(LivingEntityVar);
      this.Q.put(LivingEntityVar, (this.Q.getOrDefault(LivingEntityVar, 0.0) + f * fC * 50.0) % 360.0);
      this.R.put(LivingEntityVar, this.R.getOrDefault(LivingEntityVar, 0.0) + f * fC * 2.5);
   }

   private boolean a(LivingEntity LivingEntityVar) {
      if (LivingEntityVar == null) {
         return false;
      } else if (!this.matchesTargetType(LivingEntityVar)) {
         return false;
      } else if (LivingEntityVar.isAlive() && LivingEntityVar.getEntityWorld() == c.world) {
         return !(LivingEntityVar.getWidth() <= 0.0F) && !(LivingEntityVar.getHeight() <= 0.0F)
            ? LivingEntityVar.squaredDistanceTo(c.player) <= 10000.0
            : false;
      } else {
         return false;
      }
   }

   private LivingEntity p() {
      if (c.player != null && c.world != null) {
         LivingEntity LivingEntityVarH = HudServiceRegistry.TARGETS.h();
         if (this.a(LivingEntityVarH)) {
            return LivingEntityVarH;
         } else {
            return c.targetedEntity instanceof LivingEntity LivingEntityVar && this.a(LivingEntityVar) ? LivingEntityVar : null;
         }
      } else {
         return null;
      }
   }

   private boolean matchesTargetType(LivingEntity LivingEntityVar) {
      if (LivingEntityVar != null && LivingEntityVar != c.player) {
         return LivingEntityVar instanceof PlayerEntity ? this.targetPlayers.a() : this.targetMobs.a();
      } else {
         return false;
      }
   }

   private void a(WorldRenderEvent worldRenderEvent, LivingEntity LivingEntityVar, float f) {
      if (!(f <= 0.0F) && LivingEntityVar != null && this.a(LivingEntityVar)) {
         Vec3d Vec3dVarB = EntityUtils.a(LivingEntityVar, worldRenderEvent.b());
         if (Vec3dVarB == null) {
            Vec3dVarB = LivingEntityVar.getEntityPos();
         }

         if (Vec3dVarB != null) {
            double dDoubleValue = this.Q.getOrDefault(LivingEntityVar, 0.0);
            double dDoubleValue2 = this.R.getOrDefault(LivingEntityVar, 0.0);
            float fA = (float)(this.n.a() * (1.0 + 0.05 * Math.sin(dDoubleValue2))) * f;
            Vec3d center = new Vec3d(Vec3dVarB.x, Vec3dVarB.y + LivingEntityVar.getHeight() / 2.0F, Vec3dVarB.z);
            Vec3d cameraPos = c.gameRenderer.getCamera().getCameraPos();
            Vec3d dir = cameraPos.subtract(center).normalize().multiply(LivingEntityVar.getWidth());
            center = center.add(dir);
            WorldRenderUtils.a(
               worldRenderEvent.a(),
               center,
               fA,
               ColorUtils.a(ColorUtils.a(this.b(LivingEntityVar).getRGB()), Math.max(1, (int)(f * 255.0F))),
               this.N,
               (float)dDoubleValue,
               false
            );
         }
      }
   }

   private void b(WorldRenderEvent worldRenderEvent, LivingEntity LivingEntityVar, float f) {
      if (this.a(LivingEntityVar) && !(f <= 0.0F)) {
         Vec3d Vec3dVarB = EntityUtils.a(LivingEntityVar, worldRenderEvent.b());
         if (Vec3dVarB == null) {
            Vec3dVarB = LivingEntityVar.getEntityPos();
         }

         if (Vec3dVarB != null) {
            double radians = Math.toRadians(50.0) / 15.0;
            int iB = this.i.b();
            double dDoubleValue = this.R.getOrDefault(LivingEntityVar, 0.0);
            double dMax = Math.max(LivingEntityVar.getWidth(), 0.5F) + 0.3;
            double dMax2 = Vec3dVarB.y + Math.max(LivingEntityVar.getHeight(), 0.5F) / 2.0F;
            double d = f;
            Vec3d[] Vec3dVarArr = new Vec3d[]{
               new Vec3d(1.0, 1.0, 1.0),
               new Vec3d(-1.0, 1.0, -1.0),
               new Vec3d(1.0, -1.0, 1.0),
               new Vec3d(-1.0, -1.0, 1.0),
               new Vec3d(1.0, 1.0, -1.0),
               new Vec3d(-1.0, -1.0, -1.0)
            };

            for (int i = 0; i < iB; i++) {
               double d2 = dDoubleValue + i * Math.PI / 2.0;
               Vec3d Vec3dVar = Vec3dVarArr[i];
               double dSqrt = Math.sqrt(
                  Vec3dVar.x * Vec3dVar.x + Vec3dVar.y * Vec3dVar.y + Vec3dVar.z * Vec3dVar.z
               );
               Vec3d Vec3dVar2 = new Vec3d(Vec3dVar.x / dSqrt, Vec3dVar.y / dSqrt, Vec3dVar.z / dSqrt);
               Vec3d Vec3dVar3 = new Vec3d(0.0, 1.0, 0.0);
               if (Math.abs(
                     Vec3dVar2.x * Vec3dVar3.x + Vec3dVar2.y * Vec3dVar3.y + Vec3dVar2.z * Vec3dVar3.z
                  )
                  > 0.99) {
                  Vec3dVar3 = new Vec3d(1.0, 0.0, 0.0);
               }

               Vec3d Vec3dVar4 = new Vec3d(
                  Vec3dVar2.y * Vec3dVar3.z - Vec3dVar2.z * Vec3dVar3.y,
                  Vec3dVar2.z * Vec3dVar3.x - Vec3dVar2.x * Vec3dVar3.z,
                  Vec3dVar2.x * Vec3dVar3.y - Vec3dVar2.y * Vec3dVar3.x
               );
               double dSqrt2 = Math.sqrt(
                  Vec3dVar4.x * Vec3dVar4.x + Vec3dVar4.y * Vec3dVar4.y + Vec3dVar4.z * Vec3dVar4.z
               );
               Vec3d Vec3dVar5 = new Vec3d(Vec3dVar4.x / dSqrt2, Vec3dVar4.y / dSqrt2, Vec3dVar4.z / dSqrt2);
               Vec3d Vec3dVar6 = new Vec3d(
                  Vec3dVar2.y * Vec3dVar5.z - Vec3dVar2.z * Vec3dVar5.y,
                  Vec3dVar2.z * Vec3dVar5.x - Vec3dVar2.x * Vec3dVar5.z,
                  Vec3dVar2.x * Vec3dVar5.y - Vec3dVar2.y * Vec3dVar5.x
               );
               double dSqrt3 = Math.sqrt(
                  Vec3dVar6.x * Vec3dVar6.x + Vec3dVar6.y * Vec3dVar6.y + Vec3dVar6.z * Vec3dVar6.z
               );
               Vec3d Vec3dVar7 = new Vec3d(Vec3dVar6.x / dSqrt3, Vec3dVar6.y / dSqrt3, Vec3dVar6.z / dSqrt3);

               for (int i2 = 0; i2 < 15; i2++) {
                  double d3 = i2 * radians + d2;
                  double dCos = Math.cos(d3);
                  double dSin = Math.sin(d3);
                  Vec3d Vec3dVar8 = new Vec3d(
                     (Vec3dVar5.x * dCos + Vec3dVar7.x * dSin) * dMax,
                     (Vec3dVar5.y * dCos + Vec3dVar7.y * dSin) * dMax,
                     (Vec3dVar5.z * dCos + Vec3dVar7.z * dSin) * dMax
                  );
                  double d4 = Vec3dVarB.x + Vec3dVar8.x;
                  double d5 = dMax2 + Vec3dVar8.y;
                  double d6 = Vec3dVarB.z + Vec3dVar8.z;

                  try {
                     WorldRenderUtils.a(
                        worldRenderEvent.a(),
                        new Vec3d(d4, d5, d6),
                        this.h.a() * (1.0F + i2 / 15.0F),
                        ColorUtils.a(ColorUtils.a(this.b(LivingEntityVar).getRGB()), Math.max(1, (int)(d * 150.0)))
                     );
                  } catch (Exception var48) {
                  }
               }
            }
         }
      }
   }

   private void c(WorldRenderEvent worldRenderEvent, LivingEntity LivingEntityVar, float f) {
      if (!(f <= 0.0F) && LivingEntityVar != null && this.a(LivingEntityVar)) {
         float fGetWidth = LivingEntityVar.getWidth() * 0.7F;
         Vec3d Vec3dVarB = EntityUtils.a(LivingEntityVar, worldRenderEvent.b());
         if (Vec3dVarB == null) {
            Vec3dVarB = LivingEntityVar.getEntityPos();
         }

         double dDoubleValue = this.R.getOrDefault(LivingEntityVar, 0.0) % (Math.PI * 2);
         double d = Math.PI;
         boolean z = dDoubleValue > d;
         double d2 = dDoubleValue / d;
         double d3 = !z ? 1.0 - d2 : d2 - 1.0;
         double dPow = d3 >= 0.5 ? 1.0 - Math.pow(-2.0 * d3 + 2.0, 2.0) / 2.0 : 2.0 * d3 * d3;
         double dGetHeight = LivingEntityVar.getHeight() / 2.0F * (dPow <= 0.5 ? dPow : 1.0 - dPow) * (!z ? 1 : -1);
         Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
         MatrixStack MatrixStackVarA = worldRenderEvent.a();
         Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
         Color colorB = this.b(LivingEntityVar);
         Matrix4f matrix4fGetPositionMatrix = MatrixStackVarA.peek().getPositionMatrix();
         MatrixStackVarA.push();
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         RenderSystemHelper.disableCull();
         RenderSystemHelper.depthMask(false);
         RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);

         try {
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
         } catch (Throwable var45) {
         }

         RenderSystemHelper.lineWidth(3.0F);
         VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(ClientPipelines.QUAD);

         for (int i = 0; i < 360; i++) {
            double dCos1 = Math.cos(Math.toRadians(i));
            double dSin1 = Math.sin(Math.toRadians(i));
            double dCos2 = Math.cos(Math.toRadians(i + 1));
            double dSin2 = Math.sin(Math.toRadians(i + 1));
            int red = colorB.getRed();
            int green = colorB.getGreen();
            int blue = colorB.getBlue();
            float x1 = (float)(Vec3dVarB.x + dCos1 * fGetWidth - Vec3dVarGetPos.x);
            float y1 = (float)(Vec3dVarB.y + LivingEntityVar.getHeight() * dPow - Vec3dVarGetPos.y);
            float z1 = (float)(Vec3dVarB.z + dSin1 * fGetWidth - Vec3dVarGetPos.z);
            float y2 = (float)(Vec3dVarB.y + LivingEntityVar.getHeight() * dPow + dGetHeight - Vec3dVarGetPos.y);
            float x2 = (float)(Vec3dVarB.x + dCos2 * fGetWidth - Vec3dVarGetPos.x);
            float z2 = (float)(Vec3dVarB.z + dSin2 * fGetWidth - Vec3dVarGetPos.z);
            int alpha1 = (int)(100.0F * f);
            consumer.vertex(matrix4fGetPositionMatrix, x1, y1, z1).color(red, green, blue, alpha1);
            consumer.vertex(matrix4fGetPositionMatrix, x1, y2, z1).color(red, green, blue, 0);
            consumer.vertex(matrix4fGetPositionMatrix, x2, y2, z2).color(red, green, blue, 0);
            consumer.vertex(matrix4fGetPositionMatrix, x2, y1, z2).color(red, green, blue, alpha1);
         }

         VertexConsumer lineConsumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(ClientPipelines.OUTLINE_NO);

         for (int i2 = 0; i2 < 360; i2++) {
            double dCos1 = Math.cos(Math.toRadians(i2));
            double dSin1 = Math.sin(Math.toRadians(i2));
            double dCos2 = Math.cos(Math.toRadians(i2 + 1));
            double dSin2 = Math.sin(Math.toRadians(i2 + 1));
            float x1 = (float)(Vec3dVarB.x + dCos1 * fGetWidth - Vec3dVarGetPos.x);
            float y1 = (float)(Vec3dVarB.y + LivingEntityVar.getHeight() * dPow - Vec3dVarGetPos.y);
            float z1 = (float)(Vec3dVarB.z + dSin1 * fGetWidth - Vec3dVarGetPos.z);
            float x2 = (float)(Vec3dVarB.x + dCos2 * fGetWidth - Vec3dVarGetPos.x);
            float z2 = (float)(Vec3dVarB.z + dSin2 * fGetWidth - Vec3dVarGetPos.z);
            int alpha = (int)(255.0F * f);
            lineConsumer.vertex(matrix4fGetPositionMatrix, x1, y1, z1).color(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), alpha);
            lineConsumer.vertex(matrix4fGetPositionMatrix, x2, y1, z2).color(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), alpha);
         }

         RenderSystemHelper.enableCull();
         RenderSystemHelper.disableBlend();
         RenderSystemHelper.depthMask(true);

         try {
            GL11.glDisable(2848);
         } catch (Throwable var44) {
         }

         MatrixStackVarA.pop();
      }
   }

   private String a(ModeSetting modeSetting) {
      switch (modeSetting.d()) {
         case "Звёзды":
            return "block_starfield";
         case "Паутина":
            return "block_cobweb";
         case "Плазма":
            return "block_plasma";
         default:
            return "block_nebula";
      }
   }

   private void d(WorldRenderEvent worldRenderEvent, LivingEntity LivingEntityVar, float f) {
      if (f > 0.0F && this.a(LivingEntityVar)) {
         Vec3d Vec3dVarB = EntityUtils.a(LivingEntityVar, worldRenderEvent.b());
         if (Vec3dVarB == null) {
            Vec3dVarB = LivingEntityVar.getEntityPos();
         }

         if (Vec3dVarB == null) {
            return;
         }

         MatrixStack MatrixStackVarA = worldRenderEvent.a();
         Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
         Color colorB = this.b(LivingEntityVar);
         int iB = this.r.b();
         int iB2 = this.s.b();
         float fA = this.u.a() + LivingEntityVar.getWidth() / 2.0F;
         float fA2 = this.v.a() * f;
         float fGetHeight = LivingEntityVar.getHeight();
         Vec3d Vec3dVar = new Vec3d(Vec3dVarB.x, Vec3dVarB.y + fGetHeight / 2.0F, Vec3dVarB.z);
         double dDoubleValue = this.w.a() ? this.Q.getOrDefault(LivingEntityVar, 0.0) : 0.0;
         double dDoubleValue2 = this.R.getOrDefault(LivingEntityVar, 0.0);
         float fSin = (float)(fA + Math.sin(dDoubleValue2) * 0.08);
         Optional<PulseShaderProgram> optionalEmpty = Optional.empty();
         if (this.x.a()) {
            optionalEmpty = ShaderLibrary.getRegistry().find(this.a(this.y));
            if (optionalEmpty.isPresent() && !optionalEmpty.get().b()) {
               optionalEmpty = Optional.empty();
            }
         }

         String strD = this.p.d();
         float f2 = (iB2 - 2 + 1) / 2.0F;
         float fA3 = fGetHeight * this.t.a();
         float f3 = (fGetHeight - fA3) / 2.0F;
         MatrixStackVarA.push();
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.defaultBlendFunc();
         RenderSystemHelper.disableCull();
         RenderSystemHelper.depthMask(false);
         RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
         Matrix4f matrix4fGetPositionMatrix = MatrixStackVarA.peek().getPositionMatrix();
         byte b2 = -1;
         if (strD.equals("Стрелки")) {
            b2 = 0;
         } else if (strD.equals("Ромбы")) {
            b2 = 1;
         } else if (strD.equals("Кубы")) {
            b2 = 2;
         }

         int totalPasses = optionalEmpty.isPresent() ? 2 : 1;

         for (int pass = 0; pass < totalPasses; pass++) {
            boolean shaderPass = pass == 1;
            if (shaderPass) {
               PulseShaderProgram pulseShaderProgram = optionalEmpty.get();
               pulseShaderProgram.d();
               pulseShaderProgram.a("time", (float)(dDoubleValue2 * 0.4));
               pulseShaderProgram.a("screenSize", c.getWindow().getFramebufferWidth(), c.getWindow().getFramebufferHeight());
               pulseShaderProgram.a("baseColor", colorB.getRed() / 255.0F, colorB.getGreen() / 255.0F, colorB.getBlue() / 255.0F, 1.0F);
               pulseShaderProgram.a("alpha", f * 1.5F);
               RenderSystemHelper.blendFunc(770, 1);
            }

            for (int i3 = 0; i3 < iB2; i3++) {
               float f9 = (float)(Vec3dVarB.y + (f3 + fA3 * i3 / (2 * (iB2 & -2) - (iB2 ^ 1))));
               float fAbs2 = fSin * (1.0F - Math.abs(i3 - f2) / Math.max(f2, 0.001F) * 0.3F);
               double d5 = 360.0 / iB / 2.0 * (i3 % 2);

               for (int i4 = 0; i4 < iB; i4++) {
                  double radians2 = Math.toRadians(dDoubleValue + d5 + 360.0 * i4 / iB);
                  float fCos2 = (float)(Vec3dVar.x + fAbs2 * Math.cos(radians2));
                  float fSin3 = (float)(Vec3dVar.z + fAbs2 * Math.sin(radians2));
                  double d6 = Vec3dVar.x - fCos2;
                  double d7 = Vec3dVar.y - f9;
                  double d8 = Vec3dVar.z - fSin3;
                  double dSqrt2 = Math.sqrt(d6 * d6 + d7 * d7 + d8 * d8);
                  double d9 = d6 / dSqrt2;
                  double d10 = d7 / dSqrt2;
                  double d11 = d8 / dSqrt2;
                  double d12 = -d11;
                  double d13 = 0.0;
                  double d14 = d9;
                  double dSqrt3 = Math.sqrt(d12 * d12 + d13 * d13 + d14 * d14);
                  if (dSqrt3 > 0.001) {
                     d12 /= dSqrt3;
                     d13 /= dSqrt3;
                     d14 /= dSqrt3;
                  }

                  double d15 = d13 * d11 - d14 * d10;
                  double d16 = d14 * d9 - d12 * d11;
                  double d17 = d12 * d10 - d13 * d9;
                  int i5 = (int)(f * 220.0F);
                  switch (b2) {
                     case 0:
                        this.a(
                           matrix4fGetPositionMatrix, Vec3dVarGetPos, colorB, shaderPass, fCos2, f9, fSin3, d9, d10, d11, d15, d16, d17, d12, d13, d14, fA2, i5
                        );
                        break;
                     case 1:
                        this.b(
                           matrix4fGetPositionMatrix, Vec3dVarGetPos, colorB, shaderPass, fCos2, f9, fSin3, d9, d10, d11, d15, d16, d17, d12, d13, d14, fA2, i5
                        );
                        break;
                     case 2:
                        this.a(matrix4fGetPositionMatrix, Vec3dVarGetPos, colorB, shaderPass, fCos2, f9, fSin3, d15, d16, d17, d12, d13, d14, fA2, i5);
                  }
               }
            }

            if (shaderPass) {
               optionalEmpty.get().e();
               RenderSystemHelper.defaultBlendFunc();
            }
         }

         RenderSystemHelper.depthMask(true);
         RenderSystemHelper.enableCull();
         RenderSystemHelper.disableBlend();
         MatrixStackVarA.pop();
      }
   }

   private void a(
      Matrix4f matrix4f,
      Vec3d Vec3dVar,
      Color color,
      boolean z,
      float f,
      float f2,
      float f3,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7,
      double d8,
      double d9,
      float f4,
      int i
   ) {
      float f5 = f4 * 0.5F;
      this.a(
         matrix4f,
         color,
         z,
         (float)(f + d * f4 * 1.5 - Vec3dVar.x),
         (float)(f2 + d2 * f4 * 1.5 - Vec3dVar.y),
         (float)(f3 + d3 * f4 * 1.5 - Vec3dVar.z),
         (float)(f + d4 * f5 - Vec3dVar.x),
         (float)(f2 + d5 * f5 - Vec3dVar.y),
         (float)(f3 + d6 * f5 - Vec3dVar.z),
         (float)(f - d4 * f5 - Vec3dVar.x),
         (float)(f2 - d5 * f5 - Vec3dVar.y),
         (float)(f3 - d6 * f5 - Vec3dVar.z),
         (float)(f + d7 * f5 - Vec3dVar.x),
         (float)(f2 + d8 * f5 - Vec3dVar.y),
         (float)(f3 + d9 * f5 - Vec3dVar.z),
         (float)(f - d7 * f5 - Vec3dVar.x),
         (float)(f2 - d8 * f5 - Vec3dVar.y),
         (float)(f3 - d9 * f5 - Vec3dVar.z),
         i
      );
   }

   private void b(
      Matrix4f matrix4f,
      Vec3d Vec3dVar,
      Color color,
      boolean z,
      float f,
      float f2,
      float f3,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7,
      double d8,
      double d9,
      float f4,
      int i
   ) {
      double d10 = d5 * d9 - d6 * d8;
      double d11 = d6 * d7 - d4 * d9;
      double d12 = d4 * d8 - d5 * d7;
      float f5 = (float)(f + d4 * f4 * 1.2 - Vec3dVar.x);
      float f6 = (float)(f2 + d5 * f4 * 1.2 - Vec3dVar.y);
      float f7 = (float)(f3 + d6 * f4 * 1.2 - Vec3dVar.z);
      float f8 = (float)(f - d4 * f4 * 1.2 - Vec3dVar.x);
      float f9 = (float)(f2 - d5 * f4 * 1.2 - Vec3dVar.y);
      float f10 = (float)(f3 - d6 * f4 * 1.2 - Vec3dVar.z);
      float f11 = f4 * 0.6F;
      float f12 = (float)(f + d7 * f11 + d10 * f11 - Vec3dVar.x);
      float f13 = (float)(f2 + d8 * f11 + d11 * f11 - Vec3dVar.y);
      float f14 = (float)(f3 + d9 * f11 + d12 * f11 - Vec3dVar.z);
      float f15 = (float)(f + d7 * f11 - d10 * f11 - Vec3dVar.x);
      float f16 = (float)(f2 + d8 * f11 - d11 * f11 - Vec3dVar.y);
      float f17 = (float)(f3 + d9 * f11 - d12 * f11 - Vec3dVar.z);
      float f18 = (float)(f - d7 * f11 - d10 * f11 - Vec3dVar.x);
      float f19 = (float)(f2 - d8 * f11 - d11 * f11 - Vec3dVar.y);
      float f20 = (float)(f3 - d9 * f11 - d12 * f11 - Vec3dVar.z);
      float f21 = (float)(f - d7 * f11 + d10 * f11 - Vec3dVar.x);
      float f22 = (float)(f2 - d8 * f11 + d11 * f11 - Vec3dVar.y);
      float f23 = (float)(f3 - d9 * f11 + d12 * f11 - Vec3dVar.z);
      this.a(matrix4f, color, z, f5, f6, f7, f12, f13, f14, f15, f16, f17, i);
      this.a(matrix4f, color, z, f5, f6, f7, f15, f16, f17, f18, f19, f20, i);
      this.a(matrix4f, color, z, f5, f6, f7, f18, f19, f20, f21, f22, f23, i);
      this.a(matrix4f, color, z, f5, f6, f7, f21, f22, f23, f12, f13, f14, i);
      this.a(matrix4f, color, z, f8, f9, f10, f15, f16, f17, f12, f13, f14, i);
      this.a(matrix4f, color, z, f8, f9, f10, f18, f19, f20, f15, f16, f17, i);
      this.a(matrix4f, color, z, f8, f9, f10, f21, f22, f23, f18, f19, f20, i);
      this.a(matrix4f, color, z, f8, f9, f10, f12, f13, f14, f21, f22, f23, i);
   }

   private void a(
      Matrix4f matrix4f,
      Vec3d Vec3dVar,
      Color color,
      boolean z,
      float f,
      float f2,
      float f3,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      float f4,
      int i
   ) {
      float f5 = f4 * 0.5F;
      double d7 = d2 * d6 - d3 * d5;
      double d8 = d3 * d4 - d * d6;
      double d9 = d * d5 - d2 * d4;
      float[][] fArr = new float[8][3];
      int i2 = 0;

      for (int i3 = -1; i3 <= 1; i3 += 2) {
         for (int i4 = -1; i4 <= 1; i4 += 2) {
            for (int i5 = -1; i5 <= 1; i5 += 2) {
               fArr[i2][0] = (float)(f + d * f5 * i3 + d4 * f5 * i4 + d7 * f5 * i5 - Vec3dVar.x);
               fArr[i2][1] = (float)(f2 + d2 * f5 * i3 + d5 * f5 * i4 + d8 * f5 * i5 - Vec3dVar.y);
               fArr[i2][2] = (float)(f3 + d3 * f5 * i3 + d6 * f5 * i4 + d9 * f5 * i5 - Vec3dVar.z);
               i2++;
            }
         }
      }

      for (int[] objArr : new int[][]{{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {0, 2, 6, 4}, {1, 5, 7, 3}}) {
         this.a(
            matrix4f,
            color,
            z,
            fArr[objArr[0]][0],
            fArr[objArr[0]][1],
            fArr[objArr[0]][2],
            fArr[objArr[1]][0],
            fArr[objArr[1]][1],
            fArr[objArr[1]][2],
            fArr[objArr[2]][0],
            fArr[objArr[2]][1],
            fArr[objArr[2]][2],
            i
         );
         this.a(
            matrix4f,
            color,
            z,
            fArr[objArr[0]][0],
            fArr[objArr[0]][1],
            fArr[objArr[0]][2],
            fArr[objArr[2]][0],
            fArr[objArr[2]][1],
            fArr[objArr[2]][2],
            fArr[objArr[3]][0],
            fArr[objArr[3]][1],
            fArr[objArr[3]][2],
            i
         );
      }
   }

   private void a(Matrix4f matrix4f, Color color, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int i) {
      if (!z) {
         VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(ClientPipelines.QUAD);
         consumer.vertex(matrix4f, f, f2, f3).color(color.getRed(), color.getGreen(), color.getBlue(), i);
         consumer.vertex(matrix4f, f4, f5, f6).color(color.getRed(), color.getGreen(), color.getBlue(), i * 3 / 4);
         consumer.vertex(matrix4f, f7, f8, f9).color(color.getRed(), color.getGreen(), color.getBlue(), i * 3 / 4);
         consumer.vertex(matrix4f, f7, f8, f9).color(color.getRed(), color.getGreen(), color.getBlue(), i * 3 / 4);
      } else {
         Vector4f v1 = matrix4f.transform(new Vector4f(f, f2, f3, 1.0F));
         Vector4f v2 = matrix4f.transform(new Vector4f(f4, f5, f6, 1.0F));
         Vector4f v3 = matrix4f.transform(new Vector4f(f7, f8, f9, 1.0F));
         float[] fArr = new float[]{v1.x, v1.y, v1.z, 0.5F, 0.0F, v2.x, v2.y, v2.z, 0.0F, 1.0F, v3.x, v3.y, v3.z, 1.0F, 1.0F};
         PulseShaderProgram.a(fArr, 3);
      }
   }

   private void a(
      Matrix4f matrix4f,
      Color color,
      boolean z,
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
      float f13,
      float f14,
      float f15,
      int i
   ) {
      this.a(matrix4f, color, z, f, f2, f3, f4, f5, f6, f10, f11, f12, i);
      this.a(matrix4f, color, z, f, f2, f3, f10, f11, f12, f7, f8, f9, i);
      this.a(matrix4f, color, z, f, f2, f3, f7, f8, f9, f13, f14, f15, i);
      this.a(matrix4f, color, z, f, f2, f3, f13, f14, f15, f4, f5, f6, i);
   }

   private Color b(LivingEntity LivingEntityVar) {
      if (this.G.a() && this.H.a() && LivingEntityVar != null && LivingEntityVar.hurtTime > 0) {
         float f = LivingEntityVar.hurtTime / 10.0F;
         Color colorN = this.n();
         Color colorA = this.I.a();
         int red = (int)(colorN.getRed() + (colorA.getRed() - colorN.getRed()) * f);
         float green = colorN.getGreen();
         int green2 = colorA.getGreen();
         int green3 = colorN.getGreen();
         return new Color(
            Math.max(0, Math.min(255, red)),
            Math.max(0, Math.min(255, (int)(green + (2 * (green2 & ~green3) - (green2 ^ green3)) * f))),
            Math.max(0, Math.min(255, (int)(colorN.getBlue() + (colorA.getBlue() - colorN.getBlue()) * f)))
         );
      } else {
         return this.n();
      }
   }

   private Color n() {
      if (this.D.a()) {
         ClientColor clientColor = ModuleRegistry.CLIENT_COLOR;
         if (clientColor != null) {
            return clientColor.n();
         }
      }

      return this.E.a();
   }

   private float o() {
      switch (this.e.d()) {
         case "Призраки":
            return this.g.a();
         case "Круг":
            return this.k.a();
         case "Квадратик":
            return this.m.a();
         case "Орбита":
            return this.q.a();
         default:
            return 1.5F;
      }
   }

   private float c(LivingEntity LivingEntityVar) {
      float fO = this.o();
      if (this.G.a() && this.J.a() && LivingEntityVar != null) {
         int i = LivingEntityVar.hurtTime;
         int iIntValue = this.O.getOrDefault(LivingEntityVar, 0);
         this.O.put(LivingEntityVar, i);
         if (i >= 9 && iIntValue < 9) {
            this.P.put(LivingEntityVar, System.currentTimeMillis());
         }

         Long lastSeen = this.P.get(LivingEntityVar);
         if (lastSeen == null) {
            return fO;
         }

         float fCurrentTimeMillis = (float)(System.currentTimeMillis() - lastSeen) / 1000.0F;
         float fA = this.L.a();
         if (fCurrentTimeMillis > fA) {
            return fO;
         }

         float f2 = fCurrentTimeMillis / fA;
         float f;
         if (f2 >= 0.15F) {
            float f3 = (f2 - 0.15F) / 0.85F;
            f = 1.0F - f3 * f3 * (3.0F - 2.0F * f3);
         } else {
            float f4 = f2 / 0.15F;
            f = f4 * f4 * (3.0F - 2.0F * f4);
         }

         return fO * (1.0F + (this.K.a() - 1.0F) * f);
      } else {
         return fO;
      }
   }

   @Override
   public void f() {
      super.f();
      this.clearRenderState();
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
