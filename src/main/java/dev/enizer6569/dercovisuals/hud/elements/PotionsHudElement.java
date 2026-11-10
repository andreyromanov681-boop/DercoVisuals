// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.hud.elements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.Registries;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.animation.AnimationState;
import dev.enizer6569.dercovisuals.animation.Easing;
import dev.enizer6569.dercovisuals.effects.PotionEffectService;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.hud.core.HudElement;
import dev.enizer6569.dercovisuals.hud.core.HudIcons;
import dev.enizer6569.dercovisuals.hud.core.HudServiceRegistry;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.hud.PotionsHud;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2D;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;
import dev.enizer6569.dercovisuals.render.font.FontManager;
import dev.enizer6569.dercovisuals.render.font.FontRenderer;

public class PotionsHudElement extends HudElement {
   private static final float A = 8.5F;
   private static final float B = 8.0F;
   private static final float C = 24.0F;
   private static final float D = 16.0F;
   private static final float E = 5.0F;
   private static final float F = 8.0F;
   private static final float G = 2.5F;
   private static final float H = 12.0F;
   private static final float I = 2.0F;
   private static final float J = 12.0F;
   private static final float K = 6.0F;
   private static final float L = 6.5F;
   private static final float M = 0.5F;
   private static final float N = 112.0F;
   private static final int O = 200;
   private static final double P = 0.2;
   private static final long Q = 1000L;
   private static final float R = 6.0F;
   private static final float S = 1.0F;
   private static final String HUD_TITLE = "Эффекты";
   private static final float HUD_HEADER_ICON_BASE = 10.0F;
   private static final float HUD_HEADER_ICON_GAP = 4.0F;
   private final Map<RegistryEntry<StatusEffect>, PotionsHudElement.ActiveEffectEntry> T = new LinkedHashMap<>();
   private final List<PotionsHudElement.PotionDefinition> U = new ArrayList<>();
   private final AnimationState V = new AnimationState();
   private final AnimationState W = new AnimationState();
   private final AnimationState X = new AnimationState();
   private final Map<String, PotionsHudElement.ActivePotionEntry> Y = new LinkedHashMap<>();
   private final AnimationState Z = new AnimationState();
   private long aa = 0L;
   private int FriendCard = 0;
   private boolean CosmeticModelRenderer = false;
   private boolean CosmeticModelLoader = false;
   private boolean ModelPart = false;
   public static boolean n;
   private static final Identifier HUD_HEADER_ICON = HudIcons.clickGui("potions");
   private static final Color o = new Color(13, 13, 17);
   private static final Color p = new Color(17, 17, 23);
   private static final Color q = new Color(255, 255, 255);
   private static final Color r = new Color(223, 223, 243);
   private static final Color s = new Color(20, 20, 28);
   private static final Color t = new Color(16, 16, 21);
   private static final Color u = new Color(17, 17, 23);
   private static final Color v = new Color(23, 23, 30);
   private static final Color w = new Color(18, 18, 24);
   private static final Color x = new Color(255, 100, 100);
   private static final Color y = new Color(115, 83, 255);
   private static final Color z = new Color(70, 46, 174);

   public PotionsHudElement(float f, float f2) {
      super(f, f2);
      this.X.d(0.0);
      this.v();
      this.a();
   }

   private void u() {
      if (!this.ModelPart) {
         PotionsHud potionsHud = ModuleRegistry.POTIONS_HUD;
         if (ModuleRegistry.POTIONS_HUD != null) {
            this.f().a(potionsHud);
            this.ModelPart = true;
            return;
         }
      }
   }

   private void v() {
      this.T.clear();
      this.addPlaceholderDefinition(StatusEffects.SPEED, "0:30");
      this.addPlaceholderDefinition(StatusEffects.STRENGTH, "0:45");
      this.addPlaceholderDefinition(StatusEffects.FIRE_RESISTANCE, "2:00");
   }

   private void addPlaceholderDefinition(RegistryEntry<StatusEffect> RegistryEntryVar, String str) {
      this.U.add(new PotionsHudElement.PotionDefinition(RegistryEntryVar, this.effectDisplayName(RegistryEntryVar), str));
   }

   private String effectDisplayName(RegistryEntry<StatusEffect> RegistryEntryVar) {
      return Text.translatable(((StatusEffect)RegistryEntryVar.comp_349()).getTranslationKey()).getString();
   }

   private String formatAmplifierSuffix(int i) {
      return i <= 0 ? "" : " " + Text.translatable("potion.potency." + i).getString();
   }

   private String formatEffectDuration(int i) {
      if (i <= 0) {
         return "--:--";
      }

      int iMax = Math.max(0, i / 20);
      int i2 = iMax / 60;
      return i2 > 0 ? String.format("%d:%02d", i2, iMax % 60) : String.format("%d", iMax);
   }

   private String effectIconKey(RegistryEntry<StatusEffect> RegistryEntryVar) {
      Identifier IdentifierVarGetId = Registries.STATUS_EFFECT.getId((StatusEffect)RegistryEntryVar.comp_349());
      return IdentifierVarGetId != null ? IdentifierVarGetId.getPath() : null;
   }

   private void drawClickGuiIcon(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, Identifier IdentifierVar, float f, float f2, float f3, float f4) {
      Color tint = this.a(Color.WHITE, f4);
      if (tint.getAlpha() >= 1) {
         renderer2D.a(IdentifierVar, f, f2, f3, f3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, tint, MatrixStackVar);
      }
   }

   private void drawEffectIcon(Renderer2D renderer2D, Matrix3x2fStack MatrixStackVar, Identifier IdentifierVar, float f, float f2, float f3, Color color) {
      if (color.getAlpha() >= 1) {
         renderer2D.a(IdentifierVar, f, f2, f3, f3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, color, MatrixStackVar);
      }
   }

   private boolean w() {
      return (this.a.currentScreen instanceof ChatScreen || this.a.currentScreen instanceof DercoVisualsClickGuiScreen) && this.y().isEmpty();
   }

   @Override
   protected void a() {
      this.u();
      boolean zW = this.w();
      List<PotionsHudElement.EffectEntry> listY = this.y();
      float fG = this.g();
      float f2 = 8.5F * fG;
      float f3 = 24.0F * fG;
      float f4 = 5.0F * fG;
      float f5 = 8.0F * fG;
      float f6 = 2.5F * fG;
      float f7 = 12.0F * fG;
      float f8 = 2.0F * fG;
      float f9 = 6.5F * fG;
      float f10 = 0.5F * fG;
      float f11 = 112.0F * fG;
      int iRound = Math.round(15.0F * fG);
      int iRound2 = Math.round(14.0F * fG);
      FontRenderer fontRenderer = FontManager.a[Math.max(10, Math.min(48, iRound))];
      FontRenderer fontRenderer2 = FontManager.a[Math.max(10, Math.min(48, iRound2))];
      if (zW) {
         long jCurrentTimeMillis = System.currentTimeMillis();
         if (jCurrentTimeMillis - this.aa >= 1000L) {
            int i = this.FriendCard;
            this.FriendCard = (2 * (i | 1) - (i ^ 1)) % this.U.size();
            this.aa = jCurrentTimeMillis;
         }
      }

      HashSet hashSet = new HashSet();
      Iterator<PotionsHudElement.EffectEntry> it = listY.iterator();

      while (it.hasNext()) {
         hashSet.add(it.next().a);
      }

      for (PotionsHudElement.ActiveEffectEntry activeEffectEntry : this.T.values()) {
         if (!hashSet.contains(activeEffectEntry.a) && !activeEffectEntry.g) {
            activeEffectEntry.g = true;
            activeEffectEntry.e.a(0.0, 0.2, Easing.h);
         }
      }

      int i2 = 0;

      for (PotionsHudElement.EffectEntry effectEntry : listY) {
         PotionsHudElement.ActiveEffectEntry activeEffectEntry2 = this.T.get(effectEntry.a);
         if (activeEffectEntry2 == null) {
            PotionsHudElement.ActiveEffectEntry activeEffectEntry3 = new PotionsHudElement.ActiveEffectEntry(
               effectEntry.a, effectEntry.b, effectEntry.c, effectEntry.d
            );
            activeEffectEntry3.e.d(0.0);
            activeEffectEntry3.e.a(1.0, 0.2, Easing.h);
            activeEffectEntry3.f.d(i2);
            this.T.put(effectEntry.a, activeEffectEntry3);
         } else {
            activeEffectEntry2.b = effectEntry.b;
            activeEffectEntry2.c = effectEntry.c;
            activeEffectEntry2.d = effectEntry.d;
            activeEffectEntry2.g = false;
            if (activeEffectEntry2.e.i() < 1.0) {
               activeEffectEntry2.e.a(1.0, 0.2, Easing.h);
            }

            if (Math.abs(activeEffectEntry2.f.i() - i2) > 0.01) {
               activeEffectEntry2.f.a(i2, 0.2, Easing.h);
            }
         }

         i2++;
      }

      this.T.entrySet().removeIf(entry -> entry.getValue().g && !(entry.getValue().e.j() >= 0.01));

      for (PotionsHudElement.ActiveEffectEntry activeEffectEntry4 : this.T.values()) {
         activeEffectEntry4.e.a();
         activeEffectEntry4.f.a();
      }

      List<PotionsHudElement.PotionSnapshot> listEmptyList = this.x();
      HashSet hashSet2 = new HashSet();
      Iterator<PotionsHudElement.PotionSnapshot> it2 = listEmptyList.iterator();

      while (it2.hasNext()) {
         hashSet2.add(it2.next().a);
      }

      for (PotionsHudElement.ActivePotionEntry activePotionEntry : this.Y.values()) {
         if (!hashSet2.contains(activePotionEntry.a) && !activePotionEntry.j) {
            activePotionEntry.j = true;
            activePotionEntry.h.a(0.0, 0.2, Easing.h);
         }
      }

      int i3 = 0;

      for (PotionsHudElement.PotionSnapshot potionSnapshot : listEmptyList) {
         PotionsHudElement.ActivePotionEntry activePotionEntry2 = this.Y.get(potionSnapshot.a);
         if (activePotionEntry2 == null) {
            PotionsHudElement.ActivePotionEntry activePotionEntry3 = new PotionsHudElement.ActivePotionEntry(
               potionSnapshot.a, potionSnapshot.b, potionSnapshot.c, potionSnapshot.d, potionSnapshot.e, potionSnapshot.f, potionSnapshot.g
            );
            activePotionEntry3.h.d(0.0);
            activePotionEntry3.h.a(1.0, 0.2, Easing.h);
            activePotionEntry3.i.d(i3);
            this.Y.put(potionSnapshot.a, activePotionEntry3);
         } else {
            activePotionEntry2.b = potionSnapshot.b;
            activePotionEntry2.c = potionSnapshot.c;
            activePotionEntry2.d = potionSnapshot.d;
            activePotionEntry2.e = potionSnapshot.e;
            activePotionEntry2.f = potionSnapshot.f;
            activePotionEntry2.g = potionSnapshot.g;
            activePotionEntry2.j = false;
            if (activePotionEntry2.h.i() < 1.0) {
               activePotionEntry2.h.a(1.0, 0.2, Easing.h);
            }

            if (!(Math.abs(activePotionEntry2.i.i() - i3) <= 0.01)) {
               activePotionEntry2.i.a(i3, 0.2, Easing.h);
            }
         }

         i3++;
      }

      this.Y.entrySet().removeIf(entry2 -> entry2.getValue().j && !(entry2.getValue().h.j() >= 0.01));

      for (PotionsHudElement.ActivePotionEntry activePotionEntry4 : this.Y.values()) {
         activePotionEntry4.h.a();
         activePotionEntry4.i.a();
      }

      int i4 = 0;
      Iterator<PotionsHudElement.ActivePotionEntry> it3 = this.Y.values().iterator();

      while (it3.hasNext()) {
         if (!it3.next().j) {
            i4++;
         }
      }

      boolean z2 = i4 > 0;
      if (z2 && this.Z.i() < 1.0) {
         this.Z.a(1.0, 0.2, Easing.h);
      } else if (!z2 && this.Z.i() > 0.0) {
         this.Z.a(0.0, 0.2, Easing.h);
      }

      this.Z.a();
      boolean z3 = this.CosmeticModelLoader;
      this.CosmeticModelLoader = false;
      int i5 = 0;
      if (zW) {
         this.CosmeticModelLoader = true;
      } else {
         Iterator<PotionsHudElement.ActiveEffectEntry> it4 = this.T.values().iterator();

         while (it4.hasNext()) {
            if (!it4.next().g) {
               this.CosmeticModelLoader = true;
               i5++;
            }
         }
      }

      if (this.CosmeticModelLoader && !z3) {
         this.X.a(1.0, 0.2, Easing.h);
      } else if (!this.CosmeticModelLoader && z3) {
         this.X.a(0.0, 0.2, Easing.h);
      } else if (this.CosmeticModelLoader && this.X.i() < 1.0) {
         this.X.a(1.0, 0.2, Easing.h);
      }

      this.X.a();
      float fA = 0.0F;
      if (zW) {
         PotionsHudElement.PotionDefinition potionDefinition = this.U.get(this.FriendCard);
         fA = f5 + f6 + fontRenderer.a(potionDefinition.b) + f6 * 2.0F + fontRenderer2.a(potionDefinition.c) + f9 * 2.0F + f10 * 2.0F;
      } else {
         for (PotionsHudElement.ActiveEffectEntry activeEffectEntry5 : this.T.values()) {
            if (!activeEffectEntry5.g) {
               fA = Math.max(fA, f5 + f6 + fontRenderer.a(activeEffectEntry5.b) + f6 * 2.0F + fontRenderer2.a(activeEffectEntry5.c) + f9 * 2.0F + f10 * 2.0F);
            }
         }
      }

      for (PotionsHudElement.ActivePotionEntry activePotionEntry5 : this.Y.values()) {
         if (!activePotionEntry5.j) {
            fA = Math.max(fA, f5 + f6 + fontRenderer.a(activePotionEntry5.a) + f6 * 2.0F + fontRenderer2.a(activePotionEntry5.b) + f9 * 2.0F + f10 * 2.0F);
         }
      }

      float fMax = f2 * 2.0F + Math.max(f11, fA);
      float f12 = 1.0F * fG;
      float f;
      if (i5 > 0) {
         f = (i5 - 1) * (f7 + f8);
      } else {
         f = 0.0F;
      }

      float f13 = f;
      float f14 = 0.0F;
      if (i4 > 0) {
         float f15 = 6.0F * fG;
         f14 = f15 + 1.0F * fG + f15 + i4 * f7 + (i4 - 1) * f8;
      }

      float f16 = f3 + f12 + f4 + f13 + f14 + f4 + 2.0F * fG + 13.0F * fG;
      if (this.CosmeticModelRenderer) {
         if (Math.abs(this.W.i() - fMax) > 0.5) {
            this.W.a(fMax, 0.2, Easing.h);
         }

         if (Math.abs(this.V.i() - f16) > 0.5) {
            this.V.a(f16, 0.2, Easing.h);
         }
      } else {
         this.W.d(fMax);
         this.V.d(f16);
         this.CosmeticModelRenderer = true;
      }

      this.W.a();
      this.V.a();
      this.d = (float)this.W.j();
      this.e = (float)this.V.j();
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2) {
      if (this.a.player != null) {
         this.a();
         float fJ = (float)this.X.j();
         if (fJ >= 0.01F) {
            boolean zW = this.w();
            float fG = this.g();
            float f5 = 8.5F * fG;
            float f6 = 8.0F * fG;
            float f7 = 24.0F * fG;
            float f8 = 5.0F * fG;
            float f9 = 12.0F * fG;
            float f10 = 2.0F * fG;
            int iRound = Math.round(16.0F * fG);
            int iRound2 = Math.round(15.0F * fG);
            int iRound3 = Math.round(14.0F * fG);
            boolean z2 = fJ < 0.99F;
            if (z2) {
               renderer2D.c(this.b, this.c, this.d, this.e, MatrixStackVar);
            }

            float f11 = !z2 ? fJ : 1.0F;
            FontRenderer fontRenderer = FontManager.b[Math.max(10, Math.min(48, iRound))];
            FontRenderer fontRenderer2 = FontManager.a[Math.max(10, Math.min(48, iRound2))];
            FontRenderer fontRenderer3 = FontManager.a[Math.max(10, Math.min(48, iRound3))];
            Color colorA = this.a(o, f11);
            Color colorA2 = this.a(p, f11);
            renderer2D.a(this.b, this.c, this.d, this.e, f6, colorA, colorA, colorA2, colorA2, MatrixStackVar);
            float f12 = this.b + f5;
            float f13 = this.c + f7 / 2.0F;
            float f14 = 20.0F * fG;
            float f15 = 4.0F * fG;
            this.drawClickGuiIcon(renderer2D, MatrixStackVar, HUD_HEADER_ICON, f12, f13 - f14 / 2.0F, f14, f11);
            FontRenderer fontRenderer4 = FontManager.e[Math.max(12, Math.min(48, Math.round(22.0F * fG)))];
            fontRenderer4.a("crypt");
            fontRenderer4.b("crypt");
            fontRenderer.a("Эффекты", f12 + f14 + f15, f13 - fontRenderer.b("Эффекты") / 4.0F + 1.0F * fG, this.a(q, f11), MatrixStackVar);
            float f16 = this.c + f7;
            renderer2D.a(this.b, f16, this.d, 1.0F * fG, 0.0F, this.a(s, f11), MatrixStackVar);
            renderer2D.b().a(this.b, f16 + 1.0F * fG, this.d, this.e - f7 - 1.0F * fG, 0.0F, MatrixStackVar);
            float f17 = f16 + 1.0F * fG + f8;
            int i = 0;
            if (zW) {
               i = 1;
            } else {
               Iterator<PotionsHudElement.ActiveEffectEntry> it = this.T.values().iterator();

               while (it.hasNext()) {
                  if (!it.next().g) {
                     i++;
                  }
               }
            }

            if (zW) {
               this.a(MatrixStackVar, renderer2D, fontRenderer2, fontRenderer3, f12, f17, this.U.get(this.FriendCard), f11, fG);
            } else {
               for (PotionsHudElement.ActiveEffectEntry activeEffectEntry : this.T.values()) {
                  float fJ2 = (float)activeEffectEntry.e.j();
                  float f4;
                  if (z2) {
                     f4 = fJ2;
                  } else {
                     f4 = fJ2 * fJ;
                  }

                  float f18 = f4;
                  if (f18 >= 0.01F) {
                     this.a(
                        MatrixStackVar,
                        renderer2D,
                        fontRenderer2,
                        fontRenderer3,
                        f12,
                        f17 + (float)activeEffectEntry.f.j() * (f9 + f10),
                        activeEffectEntry,
                        f18,
                        fG
                     );
                  }
               }
            }

            float fJ3 = (float)this.Z.j();
            if (fJ3 > 0.01F && !zW) {
               float f19 = 6.0F * fG;
               float f20 = 1.0F * fG;
               float f21 = f17 + i * (f9 + f10);
               if (i > 0) {
                  f21 -= f10;
               }

               float f22 = f21 + f19;
               float f3;
               if (z2) {
                  f3 = 1.0F;
               } else {
                  f3 = fJ;
               }

               renderer2D.a(this.b, f22, this.d, f20, 0.0F, this.a(s, fJ3 * f3), MatrixStackVar);
               float f23 = f22 + f20 + f19;

               for (PotionsHudElement.ActivePotionEntry activePotionEntry : this.Y.values()) {
                  float fJ4 = (float)activePotionEntry.h.j() * fJ3 * (!z2 ? fJ : 1.0F);
                  if (fJ4 >= 0.01F) {
                     this.a(
                        MatrixStackVar,
                        renderer2D,
                        fontRenderer2,
                        fontRenderer3,
                        f12,
                        f23 + (float)activePotionEntry.i.j() * (f9 + f10),
                        activePotionEntry,
                        fJ4,
                        fG
                     );
                  }
               }
            }

            renderer2D.b().a(MatrixStackVar);
            if (z2) {
               renderer2D.a(fJ, MatrixStackVar);
            }
         }
      }
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      PotionsHudElement.PotionDefinition potionDefinition,
      float f3,
      float f4
   ) {
      float f5 = 8.0F * f4;
      float f6 = 2.5F * f4;
      float f7 = 8.5F * f4;
      float f8 = 6.5F * f4;
      float f9 = 0.5F * f4;
      float f10 = 12.0F * f4;
      float f11 = 6.0F * f4;
      float f12 = f2 + 12.0F * f4 / 2.0F;
      Color colorA = this.a(r, f3);
      Color colorA2 = this.a(Color.WHITE, f3);
      Identifier IdentifierVarB = this.b(potionDefinition.a);
      float f13 = f12 - f5 / 2.0F;
      if (IdentifierVarB != null) {
         this.drawEffectIcon(renderer2D, MatrixStackVar, IdentifierVarB, f, f13, f5, colorA2);
      } else {
         renderer2D.a(f, f13, f5, f5, 2.0F * f4, colorA, MatrixStackVar);
      }

      fontRenderer.a(potionDefinition.b, f + f5 + f6, f12 - fontRenderer.b(potionDefinition.b) / 4.0F, colorA, MatrixStackVar);
      float fA = fontRenderer2.a(potionDefinition.c) + f8 * 2.0F + f9 * 2.0F - 2.0F * f4;
      float f14 = this.b + this.d - f7 - fA;
      float f15 = f12 - f10 / 2.0F - f9;
      float f16 = f10 + f9 * 2.0F;
      Color colorA3 = this.a(v, f3);
      Color colorA4 = this.a(w, f3);
      Color colorA5 = this.a(t, f3);
      Color colorA6 = this.a(u, f3);
      renderer2D.a(f14, f15, fA, f16, f11, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
      renderer2D.a(f14 + f9, f15 + f9, fA - f9 * 2.0F, f10, f11 - f9, colorA5, colorA5, colorA6, colorA6, MatrixStackVar);
      fontRenderer2.a(
         potionDefinition.c, f14 + (fA - fontRenderer2.a(potionDefinition.c)) / 2.0F, f12 - fontRenderer2.b(potionDefinition.c) / 4.0F, colorA, MatrixStackVar
      );
      float fA2 = f + f5 + f6 + fontRenderer.a(potionDefinition.b) + f6;
      float f17 = f14 - f6;
      if (!(f17 <= fA2 + 5.0F * f4)) {
         renderer2D.a(fA2, f12 - 0.5F * f4, f17 - fA2, 1.0F * f4, 0.0F, this.a(s, f3), MatrixStackVar);
      }
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      PotionsHudElement.ActiveEffectEntry activeEffectEntry,
      float f3,
      float f4
   ) {
      float f5 = 8.0F * f4;
      float f6 = 2.5F * f4;
      float f7 = 8.5F * f4;
      float f8 = 6.5F * f4;
      float f9 = 0.5F * f4;
      float f10 = 12.0F * f4;
      float f11 = 6.0F * f4;
      float f12 = f2 + 12.0F * f4 / 2.0F;
      Color colorA2 = this.a(r, f3);
      Color colorA3 = this.a(Color.WHITE, f3);
      Identifier IdentifierVarB = this.b(activeEffectEntry.a);
      float f13 = f12 - f5 / 2.0F;
      if (IdentifierVarB != null) {
         this.drawEffectIcon(renderer2D, MatrixStackVar, IdentifierVarB, f, f13, f5, colorA3);
      } else {
         renderer2D.a(f, f13, f5, f5, 2.0F * f4, colorA2, MatrixStackVar);
      }

      fontRenderer.a(activeEffectEntry.b, f + f5 + f6, f12 - fontRenderer.b(activeEffectEntry.b) / 4.0F, colorA2, MatrixStackVar);
      float fA = fontRenderer2.a(activeEffectEntry.c) + f8 * 2.0F + f9 * 2.0F - 2.0F * f4;
      float f14 = this.b + this.d - f7 - fA;
      float f15 = f12 - f10 / 2.0F - f9;
      float f16 = f10 + f9 * 2.0F;
      Color colorA4 = this.a(v, f3);
      Color colorA5 = this.a(w, f3);
      Color colorA6 = this.a(t, f3);
      Color colorA7 = this.a(u, f3);
      renderer2D.a(f14, f15, fA, f16, f11, colorA4, colorA4, colorA5, colorA5, MatrixStackVar);
      renderer2D.a(f14 + f9, f15 + f9, fA - f9 * 2.0F, f10, f11 - f9, colorA6, colorA6, colorA7, colorA7, MatrixStackVar);
      Color colorA;
      if (activeEffectEntry.d > 0 && activeEffectEntry.d <= 200) {
         colorA = this.a(this.a(r, x, (float)(Math.sin(System.currentTimeMillis() / 150.0) * 0.5 + 0.5)), f3);
      } else {
         colorA = colorA2;
      }

      fontRenderer2.a(
         activeEffectEntry.c,
         f14 + (fA - fontRenderer2.a(activeEffectEntry.c)) / 2.0F,
         f12 - fontRenderer2.b(activeEffectEntry.c) / 4.0F,
         colorA,
         MatrixStackVar
      );
      float fA2 = f + f5 + f6 + fontRenderer.a(activeEffectEntry.b) + f6;
      float f17 = f14 - f6;
      if (f17 > fA2 + 5.0F * f4) {
         renderer2D.a(fA2, f12 - 0.5F * f4, f17 - fA2, 1.0F * f4, 0.0F, this.a(s, f3), MatrixStackVar);
      }
   }

   private void a(
      Matrix3x2fStack MatrixStackVar,
      Renderer2D renderer2D,
      FontRenderer fontRenderer,
      FontRenderer fontRenderer2,
      float f,
      float f2,
      PotionsHudElement.ActivePotionEntry activePotionEntry,
      float f3,
      float f4
   ) {
      float f5 = 8.0F * f4;
      float f6 = 2.5F * f4;
      float f7 = 8.5F * f4;
      float f8 = 6.5F * f4;
      float f9 = 0.5F * f4;
      float f10 = 12.0F * f4;
      float f11 = 6.0F * f4;
      float f12 = f2 + 12.0F * f4 / 2.0F;
      Color colorA2 = this.a(r, f3);
      Color colorA3 = this.a(Color.WHITE, f3);
      float f13 = f12 - f5 / 2.0F;
      if (activePotionEntry.f && activePotionEntry.d != null) {
         Identifier IdentifierVarB = this.b(activePotionEntry.d);
         if (IdentifierVarB != null) {
            this.drawEffectIcon(renderer2D, MatrixStackVar, IdentifierVarB, f, f13, f5, colorA3);
         } else {
            renderer2D.a(f, f13, f5, f5, 2.0F * f4, colorA2, MatrixStackVar);
         }
      } else if (activePotionEntry.e != null) {
         this.a(MatrixStackVar, new ItemStack(activePotionEntry.e), f, f13, f5, f3);
      } else {
         renderer2D.a(f, f13, f5, f5, 2.0F * f4, colorA2, MatrixStackVar);
      }

      fontRenderer.a(activePotionEntry.a, f + f5 + f6, f12 - fontRenderer.b(activePotionEntry.a) / 4.0F, colorA2, MatrixStackVar);
      float fA = fontRenderer2.a(activePotionEntry.b) + f8 * 2.0F + f9 * 2.0F - 2.0F * f4;
      float f14 = this.b + this.d - f7 - fA;
      float f15 = f12 - f10 / 2.0F - f9;
      float f16 = f10 + f9 * 2.0F;
      Color colorA4 = this.a(v, f3);
      Color colorA5 = this.a(w, f3);
      Color colorA6 = this.a(t, f3);
      Color colorA7 = this.a(u, f3);
      renderer2D.a(f14, f15, fA, f16, f11, colorA4, colorA4, colorA5, colorA5, MatrixStackVar);
      renderer2D.a(f14 + f9, f15 + f9, fA - f9 * 2.0F, f10, f11 - f9, colorA6, colorA6, colorA7, colorA7, MatrixStackVar);
      Color colorA;
      if (activePotionEntry.c > 0 && activePotionEntry.c <= 200) {
         colorA = this.a(this.a(r, x, (float)(Math.sin(System.currentTimeMillis() / 150.0) * 0.5 + 0.5)), f3);
      } else {
         colorA = colorA2;
      }

      fontRenderer2.a(
         activePotionEntry.b,
         f14 + (fA - fontRenderer2.a(activePotionEntry.b)) / 2.0F,
         f12 - fontRenderer2.b(activePotionEntry.b) / 4.0F,
         colorA,
         MatrixStackVar
      );
      float fA2 = f + f5 + f6 + fontRenderer.a(activePotionEntry.a) + f6;
      float f17 = f14 - f6;
      if (f17 > fA2 + 5.0F * f4) {
         renderer2D.a(fA2, f12 - 0.5F * f4, f17 - fA2, 1.0F * f4, 0.0F, this.a(s, f3), MatrixStackVar);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, ItemStack ItemStackVar, float f, float f2, float f3, float f4) {
      if (this.a.player != null && !ItemStackVar.isEmpty() && !(f4 < 0.01F)) {
         DrawContext DrawContextVar = Renderer2DImpl.currentDrawContext != null
            ? Renderer2DImpl.currentDrawContext
            : new DrawContext(this.a, new GuiRenderState(), this.a.getWindow().getScaledWidth(), this.a.getWindow().getScaledHeight());
         Matrix3x2fStack MatrixStackVarGetMatrices = DrawContextVar.getMatrices();
         MatrixStackVarGetMatrices.pushMatrix();
         MatrixStackVarGetMatrices.translate(f, f2);
         float f5 = f3 / 16.0F;
         MatrixStackVarGetMatrices.scale(f5, f5);
         RenderSystemHelper.enableBlend();
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, f4);
         DrawContextVar.drawItem(ItemStackVar, 0, 0);
         RenderSystemHelper.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystemHelper.disableBlend();
         MatrixStackVarGetMatrices.popMatrix();
      }
   }

   private Color a(Color color, float f) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(color.getAlpha() * f))));
   }

   private Color a(Color color, Color color2, float f) {
      float fMax = Math.max(0.0F, Math.min(1.0F, f));
      float red = color.getRed();
      int red2 = color2.getRed();
      int red3 = color.getRed();
      int i = (int)(red + (2 * (red2 & ~red3) - (red2 ^ red3)) * fMax);
      float green = color.getGreen();
      int green2 = color2.getGreen();
      int green3 = color.getGreen();
      int i2 = (int)(green + ((green2 & ~green3) - (~green2 & green3)) * fMax);
      float blue = color.getBlue();
      int blue2 = color2.getBlue();
      int blue3 = color.getBlue();
      int i3 = (int)(blue + ((blue2 & ~blue3) - (~blue2 & blue3)) * fMax);
      float alpha = color.getAlpha();
      int alpha2 = color2.getAlpha();
      int alpha3 = color.getAlpha();
      return new Color(i, i2, i3, (int)(alpha + (2 * (alpha2 & ~alpha3) - (alpha2 ^ alpha3)) * fMax));
   }

   private List<PotionsHudElement.PotionSnapshot> x() {
      ArrayList arrayList = new ArrayList();
      PotionEffectService potionEffectService = HudServiceRegistry.POTION_EFFECTS;
      if (potionEffectService == null) {
         return arrayList;
      }

      for (PotionEffectService.PotionEffectInfo potionEffectInfo : potionEffectService.g()) {
         if (potionEffectInfo.c() == null || !potionEffectInfo.c().equals(StatusEffects.NIGHT_VISION)) {
            arrayList.add(
               new PotionsHudElement.PotionSnapshot(
                  potionEffectInfo.a(),
                  this.c(potionEffectInfo.e()),
                  potionEffectInfo.e(),
                  potionEffectInfo.c(),
                  potionEffectInfo.b(),
                  potionEffectInfo.d(),
                  potionEffectInfo.h()
               )
            );
         }
      }

      return arrayList;
   }

   private List<PotionsHudElement.EffectEntry> y() {
      ArrayList arrayList = new ArrayList();
      if (this.a.player == null) {
         return arrayList;
      }

      for (Entry entry : this.a.player.getActiveStatusEffects().entrySet()) {
         RegistryEntry<StatusEffect> RegistryEntryVar = (RegistryEntry<StatusEffect>)entry.getKey();
         if (RegistryEntryVar == null || !RegistryEntryVar.equals(StatusEffects.NIGHT_VISION)) {
            StatusEffectInstance StatusEffectInstanceVar = (StatusEffectInstance)entry.getValue();
            String str = this.effectDisplayName(RegistryEntryVar) + this.formatAmplifierSuffix(StatusEffectInstanceVar.getAmplifier());
            int iGetDuration = StatusEffectInstanceVar.getDuration();
            arrayList.add(new PotionsHudElement.EffectEntry(RegistryEntryVar, str, this.formatEffectDuration(iGetDuration), iGetDuration));
         }
      }

      arrayList.sort(Comparator.comparingInt(effectEntry -> effectEntry.d));
      return arrayList;
   }

   private String a(RegistryEntry<StatusEffect> RegistryEntryVar) {
      return this.effectDisplayName(RegistryEntryVar);
   }

   private String a(String str) {
      if (str != null && !str.isEmpty()) {
         String[] strArrSplit = str.split("_");
         StringBuilder sb = new StringBuilder();

         for (String str2 : strArrSplit) {
            if (!str2.isEmpty()) {
               if (!sb.isEmpty()) {
                  sb.append(' ');
               }

               sb.append(Character.toUpperCase(str2.charAt(0)));
               if (str2.length() > 1) {
                  sb.append(str2.substring(1).toLowerCase());
               }
            }
         }

         return sb.toString().trim();
      } else {
         return "";
      }
   }

   private String b(int i) {
      return this.formatAmplifierSuffix(i).trim();
   }

   private String c(int i) {
      return this.formatEffectDuration(i);
   }

   private Identifier b(RegistryEntry<StatusEffect> RegistryEntryVar) {
      Identifier id = Registries.STATUS_EFFECT.getId((StatusEffect)RegistryEntryVar.comp_349());
      return id == null ? null : Identifier.of(id.getNamespace(), "textures/mob_effect/" + id.getPath() + ".png");
   }

   private String c(RegistryEntry<StatusEffect> RegistryEntryVar) {
      return this.effectIconKey(RegistryEntryVar);
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class ActiveEffectEntry {
      final RegistryEntry<StatusEffect> a;
      String b;
      String c;
      int d;
      final AnimationState e = new AnimationState();
      final AnimationState f = new AnimationState();
      boolean g = false;
      public static int h;
      public static boolean i;

      ActiveEffectEntry(RegistryEntry<StatusEffect> RegistryEntryVar, String str, String str2, int i2) {
         this.a = RegistryEntryVar;
         this.b = str;
         this.c = str2;
         this.d = i2;
      }

      public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
         return null;
      }
   }

   private static class ActivePotionEntry {
      final String a;
      String b;
      int c;
      RegistryEntry<StatusEffect> d;
      Item e;
      boolean f;
      boolean g;
      final AnimationState h = new AnimationState();
      final AnimationState i = new AnimationState();
      boolean j = false;
      public static int k;

      ActivePotionEntry(String str, String str2, int i, RegistryEntry<StatusEffect> RegistryEntryVar, Item ItemVar, boolean z, boolean z2) {
         this.a = str;
         this.b = str2;
         this.c = i;
         this.d = RegistryEntryVar;
         this.e = ItemVar;
         this.f = z;
         this.g = z2;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class EffectEntry {
      final RegistryEntry<StatusEffect> a;
      final String b;
      final String c;
      final int d;
      public static int e;
      public static boolean f;

      EffectEntry(RegistryEntry<StatusEffect> RegistryEntryVar, String str, String str2, int i) {
         this.a = RegistryEntryVar;
         this.b = str;
         this.c = str2;
         this.d = i;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class PotionDefinition {
      final RegistryEntry<StatusEffect> a;
      final String b;
      final String c;
      public static int d;
      public static boolean e;

      PotionDefinition(RegistryEntry<StatusEffect> RegistryEntryVar, String str, String str2) {
         this.a = RegistryEntryVar;
         this.b = str;
         this.c = str2;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }

   private static class PotionSnapshot {
      final String a;
      final String b;
      final int c;
      final RegistryEntry<StatusEffect> d;
      final Item e;
      final boolean f;
      final boolean g;
      public static int h;
      public static boolean i;

      PotionSnapshot(String str, String str2, int i2, RegistryEntry<StatusEffect> RegistryEntryVar, Item ItemVar, boolean z, boolean z2) {
         this.a = str;
         this.b = str2;
         this.c = i2;
         this.d = RegistryEntryVar;
         this.e = ItemVar;
         this.f = z;
         this.g = z2;
      }

      public static String a(String str, String str2, int i2, int i3, int i4, int i5) {
         return null;
      }
   }
}
