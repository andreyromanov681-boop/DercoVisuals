// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.gui.modules;

import java.util.Iterator;
import java.util.List;
import org.joml.Matrix3x2fStack;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiOverlay;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiTab;
import dev.enizer6569.dercovisuals.gui.core.ClickGuiTabType;
import dev.enizer6569.dercovisuals.gui.core.DercoVisualsClickGuiScreen;
import dev.enizer6569.dercovisuals.render.Renderer2D;

public class ModuleSettingsOverlay implements ClickGuiOverlay {
   private static final float MODULE_LIST_TOP_OFFSET = 48.0F;
   private static final float MODULE_LIST_BOTTOM_PADDING = 5.0F;
   private final DercoVisualsClickGuiScreen screen;
   private int mouseX;
   private int mouseY;

   public ModuleSettingsOverlay(DercoVisualsClickGuiScreen pulseClickGuiScreen) {
      this.screen = pulseClickGuiScreen;
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      ClickGuiTab clickGuiTabA = this.screen.a();
      if (clickGuiTabA instanceof ModulesTab modulesTab) {
         if (clickGuiTabA.a() != ClickGuiTabType.MODULES) {
            return;
         }

         List<ModuleCard> listH = modulesTab.h();
         if (listH.isEmpty()) {
            return;
         }

         this.mouseX = i;
         this.mouseY = i2;
         float f3 = f2 + 48.0F;
         float fE = DercoVisualsClickGuiScreen.e() - 48.0F - 5.0F;
         ModuleCard moduleCardFindHoveredSettingsCard = this.findHoveredSettingsCard(listH, i, i2);

         for (ModuleCard next : listH) {
            next.a(MatrixStackVar, renderer2D, f, f2, i, i2, f3, fE, moduleCardFindHoveredSettingsCard == null || moduleCardFindHoveredSettingsCard == next);
         }
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2) {
      ClickGuiTab clickGuiTabA = this.screen.a();
      if (clickGuiTabA instanceof ModulesTab modulesTab) {
         if (clickGuiTabA.a() != ClickGuiTabType.MODULES) {
            return;
         }

         Iterator<ModuleCard> it = modulesTab.h().iterator();

         while (it.hasNext()) {
            it.next().a(MatrixStackVar, renderer2D, i, i2);
         }
      }
   }

   @Override
   public void a(float f, float f2, int i, int i2) {
   }

   private ModuleCard findHoveredSettingsCard(List<ModuleCard> list, int i, int i2) {
      for (ModuleCard moduleCard : list) {
         if (!moduleCard.f() && !moduleCard.g() && moduleCard.f(i, i2)) {
            return moduleCard;
         }
      }

      return null;
   }
}
