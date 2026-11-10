package pulse.modules.visuals;

import java.awt.Color;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;
import pulse.settings.SettingGroup;

@ModuleInfo(a = "Kill Effect", b = "Визуальные эффекты при убийстве существ", c = ModuleCategory.VISUALS)
public class KillEffect extends ClientModule {
   private final SettingGroup effectGroup = new SettingGroup("Эффект");
   public final ModeSetting effectMode = new ModeSetting("Эффект", new String[]{"Молния", "Ударная волна", "Дух"}, "Ударная волна");
   public final ColorSetting effectColor = new ColorSetting("Цвет", new Color(76, 159, 255));
   private final SettingGroup targetGroup = new SettingGroup("Цели");
   public final ModeSetting targetMode = new ModeSetting("Срабатывать на", new String[]{"Игроки", "Все"}, "Игроки");
   private final SettingGroup starGroup = new SettingGroup("Звёздный залп");
   public final BooleanSetting starBurst = new BooleanSetting("Включить", false);
}
