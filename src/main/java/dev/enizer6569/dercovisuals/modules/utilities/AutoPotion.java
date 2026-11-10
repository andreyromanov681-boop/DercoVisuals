// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.utilities;

import java.util.Iterator;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.component.DataComponentTypes;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;

@ModuleInfo(a = "Auto Potion", b = "Автоматически использует выбранные зелья", c = ModuleCategory.UTILITIES)
public class AutoPotion extends ClientModule {
   private boolean g;
   public static int a;
   public static boolean b;
   private final BooleanSetting e = new BooleanSetting("Невидимость", true);
   private final BooleanSetting f = new BooleanSetting("Скорость", false);
   private int h = -1;
   private boolean i = false;

   @EventHandler
   public void a(ClientTickEvent clientTickEvent) {
      if (c.world != null && c.player != null) {
         if (!this.g) {
            this.i = c.options.useKey.isPressed();
         }

         int i = -1;
         int i2 = -1;
         if (this.e.k()
            && (
               !c.player.hasStatusEffect(StatusEffects.INVISIBILITY)
                  || c.player.getStatusEffect(StatusEffects.INVISIBILITY) != null && c.player.getStatusEffect(StatusEffects.INVISIBILITY).getDuration() <= 100
            )) {
            for (int i3 = 0; i3 < 9; i3++) {
               ItemStack ItemStackVarGetStack = c.player.getInventory().getStack(i3);
               if (!ItemStackVarGetStack.isEmpty()
                  && ItemStackVarGetStack.getItem() == Items.POTION
                  && this.a(ItemStackVarGetStack, StatusEffects.INVISIBILITY)) {
                  i = i3;
                  break;
               }
            }
         }

         if (this.f.k()
            && (
               !c.player.hasStatusEffect(StatusEffects.SPEED)
                  || c.player.getStatusEffect(StatusEffects.SPEED) != null && c.player.getStatusEffect(StatusEffects.SPEED).getDuration() <= 100
            )) {
            for (int i4 = 0; i4 < 9; i4++) {
               ItemStack ItemStackVarMethod_54382 = c.player.getInventory().getStack(i4);
               if (!ItemStackVarMethod_54382.isEmpty() && ItemStackVarMethod_54382.getItem() == Items.POTION) {
                  if (this.a(ItemStackVarMethod_54382, StatusEffects.SPEED)) {
                     i2 = i4;
                     break;
                  }

                  if (b) {
                  }
               }
            }
         }

         int i5 = -1;
         if (i != -1) {
            i5 = i;
         } else if (i2 != -1) {
            i5 = i2;
         }

         if (i5 != -1) {
            if (!this.g) {
               this.h = c.player.getInventory().getSelectedSlot();
            }

            c.player.getInventory().setSelectedSlot(i5);
            c.options.useKey.setPressed(true);
            this.g = true;
         } else {
            if (this.g) {
               if (this.h != -1) {
                  c.player.getInventory().setSelectedSlot(this.h);
                  this.h = -1;
               }

               c.options.useKey.setPressed(this.i);
               this.g = false;
            }
         }
      }
   }

   private boolean a(ItemStack ItemStackVar, RegistryEntry<StatusEffect> RegistryEntryVar) {
      PotionContentsComponent PotionContentsComponentVar = (PotionContentsComponent)ItemStackVar.get(DataComponentTypes.POTION_CONTENTS);
      if (PotionContentsComponentVar == null) {
         return false;
      }

      Iterator it = PotionContentsComponentVar.comp_2380().iterator();

      while (it.hasNext()) {
         if (((StatusEffectInstance)it.next()).getEffectType().equals(RegistryEntryVar)) {
            return true;
         }
      }

      return PotionContentsComponentVar.comp_2378()
         .map(
            RegistryEntryVar2 -> ((Potion)RegistryEntryVar2.comp_349())
               .getEffects()
               .stream()
               .anyMatch(StatusEffectInstanceVar -> StatusEffectInstanceVar.getEffectType().equals(RegistryEntryVar))
         )
         .orElse(false);
   }

   @Override
   public void f() {
      super.f();
      if (this.g) {
         c.options.useKey.setPressed(this.i);
      }

      if (this.h != -1) {
         c.player.getInventory().setSelectedSlot(this.h);
      }

      this.g = false;
      this.h = -1;
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
