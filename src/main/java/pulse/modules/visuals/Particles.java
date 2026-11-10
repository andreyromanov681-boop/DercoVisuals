package pulse.modules.visuals;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import pulse.core.Bool;
import pulse.events.AttackEntityEvent;
import pulse.events.TotemPopEvent;
import pulse.events.WorldRenderStartEvent;
import pulse.hud.core.HudServiceRegistry;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;
import pulse.util.ColorUtils;

@ModuleInfo(a = "Particles", b = "Создаёт частицы при выполнении условий", c = ModuleCategory.VISUALS)
public class Particles extends ClientModule {
   private long H;
   private Vec3d I;
   private LivingEntity K;
   public static int a;
   public static boolean b;
   private final ModeSetting e = new ModeSetting("Условия", new String[]{"При ударе", "При сносе тотема"}, new int[]{0});
   private final BooleanSetting f = new BooleanSetting("Индивидуальные настройки", false);
   private final SettingGroup g = new SettingGroup("Общие").a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final ModeSetting h = new ModeSetting("Тип частиц", new String[]{"Сердце", "Искра", "Снежинка", "Сияние", "Звезда", "Доллар"}, "Сердце")
      .a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final ModeSetting i = new ModeSetting("Режим физики", new String[]{"Реалистичная", "Без коллизий", "Без физики", "Притяжение"}, "Реалистичная")
      .a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final SliderSetting j = new SliderSetting("Количество", 4.0F, 1.0F, 20.0F, 1.0F).a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final SliderSetting k = new SliderSetting("Размер", 0.3F, 0.1F, 0.75F, 0.05F).a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final SliderSetting l = new SliderSetting("Время жизни", 20.0F, 10.0F, 50.0F, 5.0F).a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final SliderSetting m = new SliderSetting("Сила разлёта", 0.3F, 0.15F, 0.35F, 0.01F).a(() -> {
      int i;
      if (this.f.a()) {
         i = 0;
      } else {
         i = 1;
      }

      return Bool.from(i);
   });
   private final BooleanSetting n = new BooleanSetting("Цвет клиента", Bool.from(45027861)).a(() -> Bool.from(this.f.a() ? 0 : 1));
   private final ColorSetting o = new ColorSetting("Конечный цвет", Color.WHITE).a(() -> Bool.from(!this.n.a() && !this.f.a() ? 1 : 0));
   private final SettingGroup p = new SettingGroup("Удар").a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final ModeSetting q = new ModeSetting("Тип частиц", new String[]{"Сердце", "Искра", "Снежинка", "Сияние", "Звезда", "Доллар"}, "Сердце")
      .a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final ModeSetting r = new ModeSetting("Режим физики", new String[]{"Реалистичная", "Без коллизий", "Без физики", "Притяжение"}, "Реалистичная")
      .a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final SliderSetting s = new SliderSetting("Количество", 4.0F, 1.0F, 20.0F, 1.0F).a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final SliderSetting t = new SliderSetting("Размер", 0.3F, 0.1F, 0.75F, 0.05F).a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final SliderSetting u = new SliderSetting("Время жизни", 20.0F, 10.0F, 50.0F, 5.0F).a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final SliderSetting v = new SliderSetting("Сила разлёта", 0.3F, 0.15F, 0.35F, 0.01F).a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final BooleanSetting w = new BooleanSetting("Цвет клиента", true).a(() -> this.f.a() && this.e.b("При ударе") ? true : false);
   private final ColorSetting x = new ColorSetting("Конечный цвет", Color.WHITE).a(() -> this.f.a() && this.e.b("При ударе") && !this.w.a() ? true : false);
   private final SettingGroup y = new SettingGroup("Снос тотема").a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final ModeSetting z = new ModeSetting("Тип частиц", new String[]{"Сердце", "Искра", "Снежинка", "Сияние", "Звезда", "Доллар"}, "Сердце")
      .a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final ModeSetting A = new ModeSetting("Режим физики", new String[]{"Реалистичная", "Без коллизий", "Без физики", "Притяжение"}, "Реалистичная")
      .a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final SliderSetting B = new SliderSetting("Количество", 4.0F, 1.0F, 20.0F, 1.0F).a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final SliderSetting C = new SliderSetting("Размер", 0.3F, 0.1F, 0.75F, 0.05F).a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final SliderSetting D = new SliderSetting("Время жизни", 20.0F, 10.0F, 50.0F, 5.0F)
      .a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final SliderSetting E = new SliderSetting("Сила разлёта", 0.3F, 0.15F, 0.35F, 0.01F)
      .a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final BooleanSetting F = new BooleanSetting("Цвет клиента", Bool.from(1175694691))
      .a(() -> this.f.a() && this.e.b("При сносе тотема") ? true : false);
   private final ColorSetting G = new ColorSetting("Конечный цвет", Color.WHITE)
      .a(() -> this.f.a() && this.e.b("При сносе тотема") && !this.F.a() ? true : false);
   private boolean J = false;

   @EventHandler
   public void a(AttackEntityEvent attackEntityEvent) {
      if (this.e.b("При ударе") && attackEntityEvent.a() != c.player && attackEntityEvent.a() instanceof LivingEntity) {
         LivingEntity LivingEntityVarA = (LivingEntity)attackEntityEvent.a();
         Vec3d Vec3dVar = new Vec3d(
            LivingEntityVarA.getX(),
            LivingEntityVarA.getY() + LivingEntityVarA.getEyeHeight(LivingEntityVarA.getPose()) / 2.0F,
            LivingEntityVarA.getZ()
         );
         int iB;
         if (this.f.a()) {
            iB = this.s.b() * 3;
         } else {
            iB = this.j.b() * 3;
         }

         int i = iB;

         for (int i2 = 0; i2 < i; i2++) {
            this.a(Vec3dVar, 0.02, this.f("attack").getRGB(), "attack");
         }
      }
   }

   @EventHandler
   public void a(TotemPopEvent totemPopEvent) {
      if (this.e.b("При сносе тотема")) {
         this.K = totemPopEvent.a();
         this.I = new Vec3d(this.K.getX(), this.K.getY() + this.K.getEyeHeight(this.K.getPose()), this.K.getZ());
         this.H = System.currentTimeMillis();
         this.J = true;
      }
   }

   @EventHandler
   public void a(WorldRenderStartEvent worldRenderStartEvent) {
      if (this.e.b("При сносе тотема") && this.J) {
         if (System.currentTimeMillis() - this.H > 1500L) {
            this.J = false;
            this.K = null;
            return;
         }

         if (this.K != null && this.K.isAlive()) {
            this.I = new Vec3d(this.K.getX(), this.K.getY() + this.K.getHeight() / 2.0, this.K.getZ());
         }

         for (int i = 0; i < 2; i++) {
            Vec3d Vec3dVar;
            do {
               Vec3dVar = new Vec3d(
                  ThreadLocalRandom.current().nextDouble(-1.0, 1.0),
                  ThreadLocalRandom.current().nextDouble(0.1, 1.0),
                  ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
               );
            } while (Vec3dVar.lengthSquared() > 1.0);

            HudServiceRegistry.PARTICLES
               .a(
                  this.I,
                  Vec3dVar.normalize().multiply(0.4),
                  !this.f.a() ? this.l.b() : this.D.b(),
                  !this.f.a() ? this.k.a() : this.C.a(),
                  this.g("totem"),
                  !ThreadLocalRandom.current().nextBoolean() ? 327424 : 16776983,
                  !this.f.a() ? this.i.d() : this.A.d()
               );
         }
      }
   }

   private void a(Vec3d Vec3dVar, double d, int i, String str) {
      int iA = ColorUtils.a(i);
      int iB = !this.f.a() ? this.j.b() : this.a(str);
      int iB2 = !this.f.a() ? this.l.b() : this.c(str);
      int i2 = (iB2 & -11) - (~iB2 & 10);
      int i3 = (iB2 | 10) + (iB2 & 10);
      float fA = !this.f.a() ? this.k.a() : this.b(str);
      float f = fA - 0.05F;
      float f2 = fA + 0.05F;
      double dA = !this.f.a() ? this.m.a() : this.d(str);
      String strD = !this.f.a() ? this.i.d() : this.e(str);

      for (int i4 = 0; i4 < iB; i4++) {
         int iNextInt = ThreadLocalRandom.current().nextInt(i2, i3);
         float fNextFloat = f + ThreadLocalRandom.current().nextFloat() * (f2 - f);
         Vec3d Vec3dVar2 = new Vec3d(
            ThreadLocalRandom.current().nextDouble(-d, d), ThreadLocalRandom.current().nextDouble(0.0, d), ThreadLocalRandom.current().nextDouble(-d, d)
         );
         if (Vec3dVar2.lengthSquared() > 0.0) {
            Vec3dVar2 = Vec3dVar2.normalize().multiply(ThreadLocalRandom.current().nextDouble(0.005, dA));
         }

         HudServiceRegistry.PARTICLES.a(Vec3dVar, Vec3dVar2, iNextInt, fNextFloat, this.g(str), iA, strD);
      }
   }

   private int a(String str) {
      switch (str) {
         case "attack":
            return this.s.b();
         case "totem":
            return this.B.b();
         default:
            return this.j.b();
      }
   }

   private float b(String str) {
      switch (str) {
         case "attack":
            return this.t.a();
         case "totem":
            return this.C.a();
         default:
            return this.k.a();
      }
   }

   private int c(String str) {
      switch (str) {
         case "attack":
            return this.u.b();
         case "totem":
            return this.D.b();
         default:
            return this.l.b();
      }
   }

   private double d(String str) {
      switch (str) {
         case "attack":
            return this.v.a();
         case "totem":
            return this.E.a();
         default:
            return this.m.a();
      }
   }

   private String e(String str) {
      switch (str) {
         case "attack":
            return this.r.d();
         case "totem":
            return this.A.d();
         default:
            return this.i.d();
      }
   }

   private Color f(String str) {
      if (this.f.a()) {
         switch (str) {
            case "attack":
               return !this.w.a() ? this.x.a() : ModuleRegistry.CLIENT_COLOR.n();
            case "totem":
               Color colorN;
               if (this.F.a()) {
                  colorN = ModuleRegistry.CLIENT_COLOR.n();
               } else {
                  colorN = this.G.a();
               }

               return colorN;
         }
      }

      return !this.n.a() ? this.o.a() : ModuleRegistry.CLIENT_COLOR.n();
   }

   private Identifier g(String str) {
      String strD;
      if (this.f.a()) {
         switch (str) {
            case "attack":
               strD = this.q.d();
               break;
            case "totem":
               strD = this.z.d();
               break;
            default:
               strD = this.h.d();
         }
      } else {
         strD = this.h.d();
      }

      switch (strD) {
         case "Снежинка":
            return Identifier.of("pulse", "textures/particle/snowflake.png");
         case "Доллар":
            return Identifier.of("pulse", "textures/particle/dollar.png");
         case "Звезда":
            return Identifier.of("pulse", "textures/particle/star.png");
         case "Искра":
            return Identifier.of("pulse", "textures/particle/sparkle.png");
         case "Сердце":
            return Identifier.of("pulse", "textures/particle/heart.png");
         case "Сияние":
            return Identifier.of("pulse", "textures/particle/glow.png");
         default:
            return Identifier.of("pulse", "textures/particle/heart.png");
      }
   }

   @Override
   public void f() {
      super.f();
      this.J = false;
      this.K = null;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
