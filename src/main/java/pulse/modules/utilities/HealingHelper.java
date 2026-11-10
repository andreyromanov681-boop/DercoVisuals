package pulse.modules.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Healing Helper", b = "Подсвечивает полезные предметы для лечения", c = ModuleCategory.UTILITIES)
public class HealingHelper extends ClientModule {
   private final BooleanSetting onlyWhenNeeded = new BooleanSetting("Только при необходимости", false);
   private final SliderSetting healthThreshold = new SliderSetting("Порог здоровья", 14.0F, 1.0F, 20.0F, 1.0F);
   private final SliderSetting hungerThreshold = new SliderSetting("Порог голода", 15.0F, 1.0F, 20.0F, 1.0F);
   private final SliderSetting minimumPriority = new SliderSetting("Минимальный приоритет", 1.0F, 1.0F, 4.0F, 1.0F);
   private final Map<Item, Integer> itemPriority;

   public HealingHelper() {
      HashMap map = new HashMap();
      map.put(Items.POTION, 1);
      map.put(Items.SPLASH_POTION, 1);
      map.put(Items.LINGERING_POTION, 1);
      map.put(Items.GOLDEN_CARROT, 2);
      map.put(Items.GOLDEN_APPLE, 3);
      map.put(Items.ENCHANTED_GOLDEN_APPLE, 4);
      this.itemPriority = Collections.unmodifiableMap(map);
   }

   public boolean a(ItemStack ItemStackVar) {
      return ItemStackVar != null && !ItemStackVar.isEmpty() && this.a(ItemStackVar.getItem()) >= this.minimumPriority.b()
         ? !this.onlyWhenNeeded.a() || this.shouldUseNow(ItemStackVar.getItem())
         : false;
   }

   public List<Item> n() {
      return new ArrayList<>(this.itemPriority.keySet());
   }

   public int a(Item ItemVar) {
      return this.itemPriority.getOrDefault(ItemVar, 0);
   }

   public BooleanSetting o() {
      return this.onlyWhenNeeded;
   }

   public SliderSetting p() {
      return this.healthThreshold;
   }

   public SliderSetting q() {
      return this.hungerThreshold;
   }

   public SliderSetting r() {
      return this.minimumPriority;
   }

   public Map<Item, Integer> s() {
      return this.itemPriority;
   }

   private boolean shouldUseNow(Item ItemVar) {
      if (c.player == null) {
         return true;
      } else {
         return ItemVar == Items.GOLDEN_CARROT
            ? c.player.getHungerManager().getFoodLevel() <= this.hungerThreshold.b()
            : c.player.getHealth() + c.player.getAbsorptionAmount() <= this.healthThreshold.a();
      }
   }
}
