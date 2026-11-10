package pulse.modules.visuals;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import pulse.core.Bool;
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

@ModuleInfo(a = "World Particles", b = "Падающие частицы в мире", c = ModuleCategory.VISUALS)
public class WorldParticles extends ClientModule {
   private final ModeSetting e = new ModeSetting("Тип частиц", new String[]{"Сердце", "Искра", "Снежинка", "Сияние", "Звезда", "Доллар"}, "Снежинка");
   private final ModeSetting f = new ModeSetting("Режим физики", new String[]{"Реалистичная", "Без коллизий", "Без физики"}, "Без физики");
   private final SettingGroup g = new SettingGroup("Спавн");
   private final SliderSetting h = new SliderSetting("Частота спавна", 5.0F, 1.0F, 10.0F, 1.0F);
   private final SliderSetting i = new SliderSetting("Количество за спавн", 5.0F, 1.0F, 15.0F, 1.0F);
   private final SliderSetting j = new SliderSetting("Радиус спавна", 30.0F, 5.0F, 50.0F, 1.0F);
   private final SliderSetting k = new SliderSetting("Высота спавна", 10.0F, 5.0F, 30.0F, 1.0F);
   private final SettingGroup l = new SettingGroup("Частицы");
   private final SliderSetting m = new SliderSetting("Размер", 0.25F, 0.1F, 1.0F, 0.05F);
   private final SliderSetting n = new SliderSetting("Время жизни", 100.0F, 30.0F, 300.0F, 10.0F);
   private final SliderSetting o = new SliderSetting("Сила притяжения", 0.02F, 0.0F, 0.1F, 0.01F);
   private final BooleanSetting p = new BooleanSetting("Горизонтальное движение", true);
   private final SliderSetting q = new SliderSetting("Скорость движения", 0.05F, 0.0F, 0.2F, 0.01F).a(() -> this.p.a());
   private final SettingGroup r = new SettingGroup("Цвет");
   private final BooleanSetting s = new BooleanSetting("Цвет клиента", true);
   private final ColorSetting t = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> Bool.from(this.s.a() ? 0 : 1));
   private long u = 0L;
   public static int a;
   public static boolean b;

   @EventHandler
   public void a(WorldRenderStartEvent worldRenderStartEvent) {
      if (c.player != null && c.world != null) {
         long jCurrentTimeMillis = System.currentTimeMillis();
         if (jCurrentTimeMillis - this.u >= 1000.0 / this.h.a()) {
            this.n();
            this.u = jCurrentTimeMillis;
         }
      }
   }

   private void n() {
      Vec3d Vec3dVarGetPos = c.player.getEntityPos();
      int iB = this.i.b();

      for (int i = 0; i < iB; i++) {
         HudServiceRegistry.PARTICLES
            .a(
               new Vec3d(
                  Vec3dVarGetPos.x + ThreadLocalRandom.current().nextDouble(-this.j.a(), this.j.a()),
                  Vec3dVarGetPos.y + (!this.f.b("Без физики") ? this.k.a() : ThreadLocalRandom.current().nextDouble(0.0, this.k.a())),
                  Vec3dVarGetPos.z + ThreadLocalRandom.current().nextDouble(-this.j.a(), this.j.a())
               ),
               !this.p.a()
                  ? Vec3d.ZERO
                  : new Vec3d(
                     ThreadLocalRandom.current().nextDouble(-this.q.a(), this.q.a()), 0.0, ThreadLocalRandom.current().nextDouble(-this.q.a(), this.q.a())
                  ),
               this.n.b(),
               this.m.a() + (ThreadLocalRandom.current().nextFloat() * 0.1F - 0.05F),
               this.p(),
               ColorUtils.a(this.o().getRGB()),
               this.o.a(),
               this.f.d()
            );
      }
   }

   private Color o() {
      return !this.s.a() ? this.t.a() : ModuleRegistry.CLIENT_COLOR.n();
   }

   private Identifier p() {
      switch (this.e.d()) {
         case "Сердце":
            return Identifier.of("pulse", "textures/particle/heart.png");
         case "Искра":
            return Identifier.of("pulse", "textures/particle/sparkle.png");
         case "Снежинка":
            return Identifier.of("pulse", "textures/particle/snowflake.png");
         case "Сияние":
            return Identifier.of("pulse", "textures/particle/glow.png");
         case "Звезда":
            return Identifier.of("pulse", "textures/particle/star.png");
         case "Доллар":
            return Identifier.of("pulse", "textures/particle/dollar.png");
         default:
            return Identifier.of("pulse", "textures/particle/snowflake.png");
      }
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
