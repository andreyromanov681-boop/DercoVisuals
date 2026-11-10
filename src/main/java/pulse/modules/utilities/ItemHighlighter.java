package pulse.modules.utilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.component.DataComponentTypes;
import pulse.core.Bool;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.ItemToggleSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Item Highliter", b = "Подсвечивает указанные предметы в инвентаре", c = ModuleCategory.UTILITIES)
public class ItemHighlighter extends ClientModule {
   private final BooleanSetting e = new BooleanSetting("Пульсация", false);
   private final SliderSetting f = new SliderSetting("Альфа", 170.0F, 0.0F, 255.0F, 1.0F);
   private final ItemToggleSetting g = new ItemToggleSetting("Дезориентация", Items.ENDER_EYE, false, new Color(100, 255, 100));
   private final ItemToggleSetting h = new ItemToggleSetting("Огненный смерч", Items.FIRE_CHARGE, false, new Color(255, 100, 0));
   private final ItemToggleSetting i = new ItemToggleSetting("Явная пыль", Items.SUGAR, false, new Color(255, 255, 255));
   private final ItemToggleSetting j = new ItemToggleSetting("Тотем бессмертия", Items.TOTEM_OF_UNDYING, false, new Color(255, 215, 0));
   private final ItemToggleSetting k = new ItemToggleSetting("Бутылка опыта", Items.EXPERIENCE_BOTTLE, false, new Color(100, 255, 200));
   private final ItemToggleSetting l = new ItemToggleSetting("Трапка", Items.NETHERITE_SCRAP, false, new Color(150, 150, 255));
   private final ItemToggleSetting m = new ItemToggleSetting("Пласт", Items.DRIED_KELP, false, new Color(180, 180, 180));
   private final ItemToggleSetting n = new ItemToggleSetting("Золотое яблоко", Items.GOLDEN_APPLE, false, new Color(255, 215, 0));
   private final ItemToggleSetting o = new ItemToggleSetting("Чарка", Items.ENCHANTED_GOLDEN_APPLE, false, new Color(255, 100, 255));
   private final ItemToggleSetting p = new ItemToggleSetting("Хорус", Items.CHORUS_FRUIT, false, new Color(200, 100, 255));
   private final ItemToggleSetting q = new ItemToggleSetting("Эндер пёрл", Items.ENDER_PEARL, false, new Color(0, 255, 150));
   private final ItemToggleSetting r = new ItemToggleSetting("Снежок заморозка", Items.SNOWBALL, false, new Color(200, 230, 255));
   private final ItemToggleSetting s = new ItemToggleSetting("Трапка 2", Items.POPPED_CHORUS_FRUIT, false, new Color(200, 170, 140));
   private final ItemToggleSetting t = new ItemToggleSetting("Стан", Items.NETHER_STAR, false, new Color(180, 140, 100));
   private final ItemToggleSetting u = new ItemToggleSetting("Светильник джека", Items.JACK_O_LANTERN, Bool.from(777949212), new Color(255, 150, 0));
   private final ItemToggleSetting v = new ItemToggleSetting("Взрывная трапка", Items.PRISMARINE_SHARD, Bool.from(-1775773688), new Color(255, 50, 50));
   private final BooleanSetting w = new BooleanSetting("Положительные зелья", Bool.from(842908090));
   private final ColorSetting x;
   private final BooleanSetting y;
   private final ColorSetting z;
   private final Map<Item, ItemToggleSetting> A;
   public static int a;
   public static boolean b;

   public ItemHighlighter() {
      ColorSetting colorSetting = new ColorSetting("Цвет положительных", new Color(100, 255, 100));
      BooleanSetting booleanSetting = this.w;
      this.x = colorSetting.a(booleanSetting::k);
      this.y = new BooleanSetting("Негативные зелья", true);
      ColorSetting colorSetting2 = new ColorSetting("Цвет негативных", new Color(255, 100, 100));
      BooleanSetting booleanSetting2 = this.y;
      this.z = colorSetting2.a(booleanSetting2::k);
      HashMap map = new HashMap();
      map.put(Items.ENDER_EYE, this.g);
      map.put(Items.FIRE_CHARGE, this.h);
      map.put(Items.SUGAR, this.i);
      map.put(Items.TOTEM_OF_UNDYING, this.j);
      map.put(Items.EXPERIENCE_BOTTLE, this.k);
      map.put(Items.NETHERITE_SCRAP, this.l);
      map.put(Items.DRIED_KELP, this.m);
      map.put(Items.GOLDEN_APPLE, this.n);
      map.put(Items.ENCHANTED_GOLDEN_APPLE, this.o);
      map.put(Items.CHORUS_FRUIT, this.p);
      map.put(Items.ENDER_PEARL, this.q);
      map.put(Items.SNOWBALL, this.r);
      map.put(Items.POPPED_CHORUS_FRUIT, this.s);
      map.put(Items.NETHER_STAR, this.t);
      map.put(Items.JACK_O_LANTERN, this.u);
      map.put(Items.PRISMARINE_SHARD, this.v);
      this.A = Collections.unmodifiableMap(map);
   }

   public boolean a(ItemStack ItemStackVar) {
      if (this.k() && !ItemStackVar.isEmpty()) {
         Item ItemVarGetItem = ItemStackVar.getItem();
         ItemToggleSetting itemToggleSetting = this.A.get(ItemVarGetItem);
         if (itemToggleSetting != null && itemToggleSetting.k()) {
            return true;
         }

         if (this.b(ItemVarGetItem)) {
            ItemHighlighter.ChangeType changeTypeC = this.c(ItemStackVar);
            if (changeTypeC == ItemHighlighter.ChangeType.POSITIVE && this.w.k()) {
               return true;
            }

            if (changeTypeC == ItemHighlighter.ChangeType.NEGATIVE && this.y.k()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean b(Item ItemVar) {
      return Bool.from(ItemVar != Items.POTION && ItemVar != Items.SPLASH_POTION && ItemVar != Items.LINGERING_POTION ? 0 : 1);
   }

   private ItemHighlighter.ChangeType c(ItemStack ItemStackVar) {
      PotionContentsComponent PotionContentsComponentVar = (PotionContentsComponent)ItemStackVar.get(DataComponentTypes.POTION_CONTENTS);
      if (PotionContentsComponentVar == null) {
         return ItemHighlighter.ChangeType.NONE;
      }

      int i = 0;
      int i2 = 0;
      boolean z = false;
      Iterator it = PotionContentsComponentVar.getEffects().iterator();

      while (it.hasNext()) {
         z = true;
         StatusEffectCategory StatusEffectCategoryVarGetCategory = ((StatusEffect)((StatusEffectInstance)it.next()).getEffectType().comp_349()).getCategory();
         if (StatusEffectCategoryVarGetCategory == StatusEffectCategory.BENEFICIAL) {
            i++;
         } else if (StatusEffectCategoryVarGetCategory == StatusEffectCategory.HARMFUL) {
            i2++;
         }
      }

      if (!z) {
         return ItemHighlighter.ChangeType.NONE;
      } else if (i > i2) {
         return ItemHighlighter.ChangeType.POSITIVE;
      } else {
         return i2 <= i ? (i <= 0 ? ItemHighlighter.ChangeType.NONE : ItemHighlighter.ChangeType.POSITIVE) : ItemHighlighter.ChangeType.NEGATIVE;
      }
   }

   public Color a(Item ItemVar) {
      ItemToggleSetting itemToggleSetting = this.A.get(ItemVar);
      return itemToggleSetting != null ? itemToggleSetting.d() : Color.WHITE;
   }

   public Color b(ItemStack ItemStackVar) {
      Item ItemVarGetItem = ItemStackVar.getItem();
      ItemToggleSetting itemToggleSetting = this.A.get(ItemVarGetItem);
      if (itemToggleSetting != null && itemToggleSetting.k()) {
         return itemToggleSetting.d();
      }

      if (this.b(ItemVarGetItem)) {
         ItemHighlighter.ChangeType changeTypeC = this.c(ItemStackVar);
         if (changeTypeC == ItemHighlighter.ChangeType.POSITIVE && this.w.k()) {
            return this.x.a();
         }

         if (changeTypeC == ItemHighlighter.ChangeType.NEGATIVE && this.y.k()) {
            return this.z.a();
         }
      }

      return Color.WHITE;
   }

   public int n() {
      if (!this.e.k()) {
         return this.f.b();
      }

      int iB = this.f.b();
      double dSin = Math.sin(System.currentTimeMillis() / 100.0) * 0.5 + 0.5;
      int iMax = Math.max(0, iB / 2);
      int i = iB / 2;
      int iMin = Math.min(255, (iB & ~i) + (i & ~iB) + 2 * (iB & i));
      int i2 = (int)(((iMin ^ iMax) - 2 * (~iMin & iMax)) * dSin);
      return (iMax ^ i2) + 2 * (iMax & i2);
   }

   public List<Item> o() {
      if (c.player == null) {
         return Collections.emptyList();
      }

      ArrayList arrayList = new ArrayList();

      for (ItemStack ItemStackVar : c.player.getInventory().getMainStacks()) {
         if (this.a(ItemStackVar)) {
            Item ItemVarGetItem = ItemStackVar.getItem();
            if (!arrayList.contains(ItemVarGetItem)) {
               arrayList.add(ItemVarGetItem);
            }
         }
      }

      return arrayList;
   }

   @Generated
   public BooleanSetting p() {
      return this.e;
   }

   @Generated
   public ItemToggleSetting q() {
      return this.g;
   }

   @Generated
   public ItemToggleSetting r() {
      return this.h;
   }

   @Generated
   public ItemToggleSetting s() {
      return this.i;
   }

   @Generated
   public ItemToggleSetting t() {
      return this.j;
   }

   @Generated
   public ItemToggleSetting u() {
      return this.k;
   }

   @Generated
   public ItemToggleSetting v() {
      return this.l;
   }

   @Generated
   public ItemToggleSetting w() {
      return this.m;
   }

   @Generated
   public ItemToggleSetting x() {
      return this.n;
   }

   @Generated
   public ItemToggleSetting y() {
      return this.o;
   }

   @Generated
   public ItemToggleSetting z() {
      return this.p;
   }

   @Generated
   public ItemToggleSetting A() {
      return this.q;
   }

   @Generated
   public ItemToggleSetting B() {
      return this.r;
   }

   @Generated
   public ItemToggleSetting C() {
      return this.s;
   }

   @Generated
   public ItemToggleSetting D() {
      return this.t;
   }

   @Generated
   public ItemToggleSetting E() {
      return this.u;
   }

   @Generated
   public ItemToggleSetting F() {
      return this.v;
   }

   @Generated
   public BooleanSetting G() {
      return this.w;
   }

   @Generated
   public ColorSetting H() {
      return this.x;
   }

   @Generated
   public BooleanSetting I() {
      return this.y;
   }

   @Generated
   public ColorSetting J() {
      return this.z;
   }

   @Generated
   public Map<Item, ItemToggleSetting> K() {
      return this.A;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private enum ChangeType {
      POSITIVE,
      NEGATIVE,
      MIXED,
      NONE;

      public static int e;
      public static boolean f;

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }
}
