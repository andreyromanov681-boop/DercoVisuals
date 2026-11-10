package pulse.modules.visuals;

import java.awt.Color;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Tab Customizer", b = "Кастомизация таб-листа игроков", c = ModuleCategory.VISUALS)
public class TabCustomizer extends ClientModule {
   public final BooleanSetting selfHighlight = new BooleanSetting("Подсветка себя", true);
   public final BooleanSetting friendHighlight = new BooleanSetting("Подсветка друзей", true);
   public final ColorSetting friendColor = new ColorSetting("Цвет друзей", new Color(65407));
   public final BooleanSetting partyHighlight = new BooleanSetting("Подсветка группы", true);
   public final BooleanSetting showHeads = new BooleanSetting("Головы игроков", true);
   public final ModeSetting pingDisplay = new ModeSetting("Пинг", new String[]{"Цифры", "Иконка", "Скрыть"}, 0);
   public final BooleanSetting friendsOnTop = new BooleanSetting("Друзья наверху", true);
   public final BooleanSetting hideHeader = new BooleanSetting("Скрыть шапку", false);
   public final BooleanSetting hideFooter = new BooleanSetting("Скрыть подвал", false);
}
