package pulse.modules.utilities;

import java.util.HashMap;
import java.util.Map;
import meteordevelopment.orbit.EventHandler;
import pulse.events.SoundPlayEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Sound Controller", b = "Контроль громкости определённых звуков", c = ModuleCategory.UTILITIES)
public class SoundController extends ClientModule {
   private final SettingGroup a = new SettingGroup("Фейерверк");
   private final BooleanSetting b = new BooleanSetting("Включить", false);
   private final SliderSetting e = new SliderSetting("Громкость", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.b.a());
   private final SettingGroup f = new SettingGroup("Пузырёк опыта");
   private final BooleanSetting g = new BooleanSetting("Бросок", false);
   private final SliderSetting h = new SliderSetting("Громкость броска", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.g.a());
   private final BooleanSetting i = new BooleanSetting("Разбивание", false);
   private final SliderSetting j = new SliderSetting("Громкость разбивания", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.i.a());
   private final BooleanSetting k = new BooleanSetting("Подбор опыта", false);
   private final SliderSetting l = new SliderSetting("Громкость подбора", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.k.a());
   private final SettingGroup m = new SettingGroup("Оружие");
   private final BooleanSetting n = new BooleanSetting("Трезубец", false);
   private final SliderSetting o = new SliderSetting("Громкость трезубца", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.n.a());
   private final BooleanSetting p = new BooleanSetting("Удочка", false);
   private final SliderSetting q = new SliderSetting("Громкость удочки", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.p.a());
   private final SettingGroup r = new SettingGroup("Атака");
   private final BooleanSetting s = new BooleanSetting("Звук удара", false);
   private final SliderSetting t = new SliderSetting("Громкость удара", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.s.a());
   private final BooleanSetting u = new BooleanSetting("Звук крита", false);
   private final SliderSetting v = new SliderSetting("Громкость крита", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.u.a());
   private final SettingGroup w = new SettingGroup("Прочее");
   private final BooleanSetting x = new BooleanSetting("Сплеш", false);
   private final SliderSetting y = new SliderSetting("Громкость сплеша", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.x.a());
   private final BooleanSetting z = new BooleanSetting("Крит FunTime", false);
   private final SliderSetting A = new SliderSetting("Громкость FunTime", 50.0F, 0.0F, 100.0F, 1.0F).a(() -> this.z.a());
   private final Map<String, SoundController.SoundVolumeBinding> B = new HashMap<>();

   public SoundController() {
      this.B.put("minecraft:entity.firework_rocket.launch", new SoundController.SoundVolumeBinding(this.b, this.e));
      this.B.put("minecraft:entity.experience_bottle.throw", new SoundController.SoundVolumeBinding(this.g, this.h));
      this.B.put("minecraft:entity.splash_potion.break", new SoundController.SoundVolumeBinding(this.i, this.j));
      this.B.put("minecraft:entity.experience_orb.pickup", new SoundController.SoundVolumeBinding(this.k, this.l));
      this.B.put("minecraft:item.trident.return", new SoundController.SoundVolumeBinding(this.n, this.o));
      this.B.put("minecraft:item.trident.hit_ground", new SoundController.SoundVolumeBinding(this.n, this.o));
      this.B.put("minecraft:block.beacon.deactivate", new SoundController.SoundVolumeBinding(this.n, this.o));
      this.B.put("minecraft:entity.fishing_bobber.retrieve", new SoundController.SoundVolumeBinding(this.p, this.q));
      this.B.put("minecraft:entity.player.attack.sweep", new SoundController.SoundVolumeBinding(this.s, this.t));
      this.B.put("minecraft:entity.player.attack.strong", new SoundController.SoundVolumeBinding(this.s, this.t));
      this.B.put("minecraft:entity.player.attack.weak", new SoundController.SoundVolumeBinding(this.s, this.t));
      this.B.put("minecraft:entity.generic.splash", new SoundController.SoundVolumeBinding(this.x, this.y));
      this.B.put("minecraft:entity.player.splash", new SoundController.SoundVolumeBinding(this.x, this.y));
      this.B.put("minecraft:entity.player.attack.crit", new SoundController.SoundVolumeBinding(this.u, this.v));
      this.B.put("minecraft:entity.player.levelup", new SoundController.SoundVolumeBinding(this.z, this.A));
      this.B.put("minecraft:entity.experience_bottle.break", new SoundController.SoundVolumeBinding(this.i, this.j));
   }

   @EventHandler
   public void a(SoundPlayEvent soundPlayEvent) {
      if (soundPlayEvent.d() != null && soundPlayEvent.d().getId() != null) {
         String id = soundPlayEvent.d().getId().toString();
         SoundController.SoundVolumeBinding binding = this.B.get(id);
         if (binding == null && id.contains(":")) {
            binding = this.B.get("minecraft:" + id.substring(id.indexOf(58) + 1));
         }

         if (binding != null && binding.a.get()) {
            float volPercent = binding.b.get() / 100.0F;
            soundPlayEvent.a(volPercent);
         }
      }
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class SoundVolumeBinding {
      private final BooleanSetting a;
      private final SliderSetting b;

      public SoundVolumeBinding(BooleanSetting booleanSetting, SliderSetting sliderSetting) {
         this.a = booleanSetting;
         this.b = sliderSetting;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
