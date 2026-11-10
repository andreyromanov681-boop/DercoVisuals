// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.component.DataComponentTypes;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.events.KeyInputEvent;
import dev.enizer6569.dercovisuals.inventory.InventoryFinder;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.KeySetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Fast Swap", b = "Быстрый выбор предметов по нажатию клавиш", c = ModuleCategory.UTILITIES)
public class FastSwap extends ClientModule {
   private final KeySetting e = new KeySetting("Кнопка хоруса", -1);
   private final KeySetting f = new KeySetting("Кнопка эндерпёрла", -1);
   private final KeySetting g = new KeySetting("Кнопка зелья исцеления", -1);
   private final KeySetting h = new KeySetting("Кнопка трапки", -1);
   private final KeySetting i = new KeySetting("Кнопка пласта", -1);
   private final KeySetting j = new KeySetting("Кнопка дезориентации", -1);
   private final KeySetting k = new KeySetting("Кнопка явной пыли", -1);
   private final KeySetting l = new KeySetting("Кнопка шаровой молнии", -1);
   private final KeySetting m = new KeySetting("Кнопка кома слизи", -1);
   private final KeySetting n = new KeySetting("Кнопка черепашьего захвата", -1);
   private final KeySetting o = new KeySetting("Кнопка паучьей судьбы", -1);
   private final KeySetting p = new KeySetting("Кнопка стана", -1);
   private final KeySetting q = new KeySetting("Кнопка магнитного шара", -1);
   private final KeySetting r = new KeySetting("Кнопка взрывной трапки", -1);
   private final KeySetting s = new KeySetting("Кнопка взрывной штучки", -1);
   private final KeySetting t = new KeySetting("Кнопка стана звезды", -1);
   private final KeySetting u = new KeySetting("Кнопка кома снега", -1);
   private final BooleanSetting v = new BooleanSetting("Отображать бинды", true);
   private final SliderSetting w = new SliderSetting("Размер шрифта биндов", 1.0F, 0.5F, 2.0F, 0.1F);
   private final List<FastSwap.ItemSwapRule> x;
   public static int a;
   public static boolean b;

   public FastSwap() {
      FastSwap.ItemSwapRule[] itemSwapRuleArr = new FastSwap.ItemSwapRule[]{
         new FastSwap.ItemSwapRule(this.h, ItemStackVar -> Bool.from(ItemStackVar.getItem() != Items.NETHERITE_SCRAP ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.i, ItemStackVar2 -> {
            int i;
            if (ItemStackVar2.getItem() != Items.DRIED_KELP) {
               i = 0;
            } else {
               i = 1;
            }

            return Bool.from(i);
         }),
         new FastSwap.ItemSwapRule(this.j, ItemStackVar3 -> Bool.from(ItemStackVar3.getItem() != Items.ENDER_EYE ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.k, ItemStackVar4 -> Bool.from(ItemStackVar4.getItem() != Items.SUGAR ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.l, ItemStackVar5 -> Bool.from(ItemStackVar5.getItem() != Items.NETHER_STAR ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.m, ItemStackVar6 -> Bool.from(ItemStackVar6.getItem() != Items.SLIME_BALL ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.n, ItemStackVar7 -> {
            int i;
            if (ItemStackVar7.getItem() != Items.TURTLE_SCUTE) {
               i = 0;
            } else {
               i = 1;
            }

            return Bool.from(i);
         }),
         new FastSwap.ItemSwapRule(this.o, ItemStackVar8 -> Bool.from(ItemStackVar8.getItem() != Items.COBWEB ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.p, ItemStackVar9 -> Bool.from(ItemStackVar9.getItem() != Items.ENDER_EYE ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.q, ItemStackVar10 -> Bool.from(ItemStackVar10.getItem() != Items.FIREWORK_STAR ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.e, ItemStackVar11 -> Bool.from(ItemStackVar11.getItem() != Items.CHORUS_FRUIT ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.f, ItemStackVar12 -> Bool.from(ItemStackVar12.getItem() != Items.ENDER_PEARL ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.g, this::a),
         new FastSwap.ItemSwapRule(this.r, ItemStackVar13 -> Bool.from(ItemStackVar13.getItem() != Items.PRISMARINE_SHARD ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.s, ItemStackVar14 -> Bool.from(ItemStackVar14.getItem() != Items.FIRE_CHARGE ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.t, ItemStackVar15 -> Bool.from(ItemStackVar15.getItem() != Items.NETHER_STAR ? 0 : 1)),
         new FastSwap.ItemSwapRule(this.u, ItemStackVar16 -> Bool.from(ItemStackVar16.getItem() != Items.SNOWBALL ? 0 : 1))
      };
      this.x = Collections.unmodifiableList(Arrays.asList(itemSwapRuleArr));
   }

   @EventHandler
   public void a(KeyInputEvent keyInputEvent) {
      if (c.player != null && c.currentScreen == null) {
         int iA = keyInputEvent.a();
         this.x
            .stream()
            .filter(itemSwapRule -> {
               int iA2 = itemSwapRule.c.a();
               return Bool.from(iA2 != -1 && iA2 != 0 && iA2 == iA ? 1 : 0);
            })
            .findFirst()
            .flatMap(itemSwapRule2 -> InventoryFinder.a(itemSwapRule2.d, false, true))
            .ifPresent(num -> c.player.getInventory().setSelectedSlot(num));
      }
   }

   public boolean a(ItemStack ItemStackVar) {
      if (ItemStackVar.getItem() != Items.POTION) {
         return false;
      }

      if (ItemStackVar.getItem() instanceof PotionItem) {
         Optional optionalPotion = ((PotionContentsComponent)ItemStackVar.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)).comp_2378();
         if (optionalPotion.isPresent()) {
            Iterator it = ((Potion)((RegistryEntry)optionalPotion.get()).comp_349()).getEffects().iterator();

            while (it.hasNext()) {
               if (((StatusEffectInstance)it.next()).getEffectType() == StatusEffects.INSTANT_HEALTH) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Generated
   public KeySetting n() {
      return this.e;
   }

   @Generated
   public KeySetting o() {
      return this.f;
   }

   @Generated
   public KeySetting p() {
      return this.g;
   }

   @Generated
   public KeySetting q() {
      return this.h;
   }

   @Generated
   public KeySetting r() {
      return this.i;
   }

   @Generated
   public KeySetting s() {
      return this.j;
   }

   @Generated
   public KeySetting t() {
      return this.k;
   }

   @Generated
   public KeySetting u() {
      return this.l;
   }

   @Generated
   public KeySetting v() {
      return this.m;
   }

   @Generated
   public KeySetting w() {
      return this.n;
   }

   @Generated
   public KeySetting x() {
      return this.o;
   }

   @Generated
   public KeySetting y() {
      return this.p;
   }

   @Generated
   public KeySetting z() {
      return this.q;
   }

   @Generated
   public KeySetting A() {
      return this.r;
   }

   @Generated
   public KeySetting B() {
      return this.s;
   }

   @Generated
   public KeySetting C() {
      return this.t;
   }

   @Generated
   public KeySetting D() {
      return this.u;
   }

   @Generated
   public BooleanSetting E() {
      return this.v;
   }

   @Generated
   public SliderSetting F() {
      return this.w;
   }

   @Generated
   public List<FastSwap.ItemSwapRule> G() {
      return this.x;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static final class ItemSwapRule {
      private final KeySetting c;
      private final Predicate<ItemStack> d;
      public static int a;
      public static boolean b;

      public ItemSwapRule(KeySetting keySetting, Predicate<ItemStack> predicate) {
         this.c = keySetting;
         this.d = predicate;
      }

      public KeySetting a() {
         return this.c;
      }

      public Predicate<ItemStack> b() {
         return this.d;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
