// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.component.DataComponentTypes;
import dev.enizer6569.dercovisuals.core.Bool;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.ItemUseEvent;
import dev.enizer6569.dercovisuals.events.ItemUseFinishEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.player.CombatState;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Cooldowns", b = "Добавляет кастомный кулдаун на зелья исцеления", c = ModuleCategory.UTILITIES)
public class Cooldowns extends ClientModule {
   private final BooleanSetting e = new BooleanSetting("Преобразовывать задержки", true);
   private final BooleanSetting f = new BooleanSetting("Только в Пвп (Исцеление)", true);
   private final SliderSetting g = new SliderSetting("Размер шрифта таймеров", 1.0F, 0.5F, 2.0F, 0.1F);
   private final Map<Item, Long> h = new HashMap<>();
   public static int a;
   public static boolean b;

   @Override
   public void f() {
      this.h.clear();
      super.f();
   }

   @EventHandler
   public void a(ItemUseFinishEvent itemUseFinishEvent) {
      if (!this.f.k() || CombatState.a().a()) {
         ItemStack ItemStackVarD = itemUseFinishEvent.d();
         if (a(ItemStackVarD)) {
            this.h.put(ItemStackVarD.getItem(), System.currentTimeMillis());
         }
      }
   }

   @EventHandler
   public void a(ItemUseEvent itemUseEvent) {
      if (c.player != null) {
         ItemStack stackInHand = itemUseEvent.d().getStackInHand(itemUseEvent.f());
         if (a(stackInHand) && System.currentTimeMillis() - this.h.getOrDefault(stackInHand.getItem(), 0L) < 18500.0) {
            itemUseEvent.b();
         }
      }
   }

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (this.f.k()) {
         if (CombatState.a().a()) {
            return;
         }

         this.h.clear();
      }
   }

   public static boolean a(ItemStack ItemStackVar) {
      if (!(ItemStackVar.getItem() instanceof PotionItem)) {
         return false;
      } else {
         PotionContentsComponent PotionContentsComponentVar;
         if (ItemStackVar.contains(DataComponentTypes.POTION_CONTENTS)
            && (PotionContentsComponentVar = (PotionContentsComponent)ItemStackVar.get(DataComponentTypes.POTION_CONTENTS)) != null) {
            return PotionContentsComponentVar.comp_2378().isPresent()
                  && ((Potion)((RegistryEntry)PotionContentsComponentVar.comp_2378().get()).comp_349())
                     .getEffects()
                     .stream()
                     .anyMatch(StatusEffectInstanceVar -> Bool.from(StatusEffectInstanceVar.getEffectType() != StatusEffects.INSTANT_HEALTH ? 0 : 1))
               ? true
               : PotionContentsComponentVar.comp_2380()
                  .stream()
                  .anyMatch(StatusEffectInstanceVar2 -> Bool.from(StatusEffectInstanceVar2.getEffectType() != StatusEffects.INSTANT_HEALTH ? 0 : 1));
         } else {
            return false;
         }
      }
   }

   private float c(Item ItemVar) {
      long jCurrentTimeMillis = System.currentTimeMillis() - this.h.getOrDefault(ItemVar, 0L);
      if (jCurrentTimeMillis >= 18500.0) {
         this.h.remove(ItemVar);
         return 0.0F;
      } else {
         return 1.0F - (float)(jCurrentTimeMillis / 18500.0);
      }
   }

   public float a(Item ItemVar) {
      return this.c(ItemVar);
   }

   public float getRemainingSeconds(Item ItemVar) {
      long jCurrentTimeMillis = System.currentTimeMillis() - this.h.getOrDefault(ItemVar, 0L);
      return jCurrentTimeMillis >= 18500L ? 0.0F : (float)(18500L - jCurrentTimeMillis) / 1000.0F;
   }

   public boolean b(Item ItemVar) {
      if (this.h.containsKey(ItemVar)) {
         if (this.a(ItemVar) > 0.0F) {
            return true;
         }
      } else if (b) {
         throw new ExceptionInInitializerError();
      }

      return false;
   }

   public boolean n() {
      return this.e.k();
   }

   @Generated
   public BooleanSetting o() {
      return this.e;
   }

   @Generated
   public BooleanSetting p() {
      return this.f;
   }

   @Generated
   public SliderSetting q() {
      return this.g;
   }

   @Generated
   public Map<Item, Long> r() {
      return this.h;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
