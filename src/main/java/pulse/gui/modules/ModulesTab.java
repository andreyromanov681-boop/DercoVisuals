package pulse.gui.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.joml.Matrix3x2fStack;
import pulse.core.Bool;
import pulse.gui.core.ClickGuiTab;
import pulse.gui.core.ClickGuiTabType;
import pulse.gui.core.GuiEntry;
import pulse.gui.core.GuiEntrySource;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.gui.core.TabHost;
import pulse.gui.core.TabSelector;
import pulse.gui.widgets.SearchBox;
import pulse.gui.widgets.SettingsIconButton;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleRegistry;
import pulse.render.Renderer2D;

public class ModulesTab implements TabHost, GuiEntrySource, ClickGuiTab {
   private static final String[] a = new String[]{"Visuals", "HUD", "Utilities"};
   private static final ModuleCategory[] b = new ModuleCategory[]{ModuleCategory.VISUALS, ModuleCategory.HUD, ModuleCategory.UTILITIES};
   private static final float c = 117.5F;
   private static final float d = 15.0F;
   private static final float e = 19.0F;
   private static final float f = 19.0F;
   private static final float g = 15.0F;
   private static final float h = 4.0F;
   private final TabSelector l;
   private final ModuleGrid m;
   private final PanelFadeOverlay n;
   private final SearchBox o;
   private final SettingsIconButton p;
   private List<ModulesTab.ModuleEntry> r;
   private int i = 0;
   private String q = "";
   private boolean s = false;
   private final Map<ModuleCategory, List<ModulesTab.ModuleEntry>> j = new HashMap<>();
   private final List<ModulesTab.ModuleEntry> k = new ArrayList<>();

   public ModulesTab() {
      for (ModuleCategory moduleCategory : ModuleCategory.values()) {
         ArrayList arrayList = new ArrayList();
         Iterator<ClientModule> it = ModuleRegistry.byCategory(moduleCategory).iterator();

         while (it.hasNext()) {
            arrayList.add(new ModulesTab.ModuleEntry(it.next()));
         }

         arrayList.sort(Comparator.comparing(moduleEntry -> moduleEntry.a().toLowerCase()));
         this.j.put(moduleCategory, arrayList);
         this.k.addAll(arrayList);
      }

      this.k.sort(Comparator.comparing(moduleEntry2 -> moduleEntry2.a().toLowerCase()));
      this.r = new ArrayList<>();
      this.l = new TabSelector(this);
      this.m = new ModuleGrid(this);
      this.n = new PanelFadeOverlay();
      this.o = new SearchBox(117.5F, 15.0F);
      this.o.a(this::a);
      this.p = new SettingsIconButton();
      this.p.a(r3 -> this.s());
   }

   private void s() {
      this.m.a(ModuleRegistry.CLIENT_COLOR);
   }

   private void a(String str) {
      this.q = str == null ? "" : str.toLowerCase().trim();
      if (this.q.isEmpty()) {
         this.s = false;
         if (this.r != null) {
            this.r.clear();
         }

         this.m.d();
      } else {
         this.s = true;
         this.r = new ArrayList<>();

         for (int i = 0; i < this.k.size(); i++) {
            ModulesTab.ModuleEntry moduleEntry = this.k.get(i);
            if (this.a(moduleEntry.a(), this.q)) {
               this.r.add(moduleEntry);
            }
         }

         this.m.d();
      }
   }

   private boolean a(String str, String str2) {
      String lowerCase = str.toLowerCase();
      return lowerCase.contains(str2) || lowerCase.replace(" ", "").contains(str2.replace(" ", "")) || this.b(lowerCase, str2);
   }

   private boolean b(String str, String str2) {
      String[] strArrSplit = str.split(" ");
      StringBuilder sb = new StringBuilder();

      for (String str3 : strArrSplit) {
         if (!str3.isEmpty()) {
            sb.append(str3.charAt(0));
         }
      }

      return sb.toString().contains(str2.replace(" ", ""));
   }

   public ModuleCategory g() {
      return b[this.i];
   }

   public void a(ModuleCategory moduleCategory) {
      for (int i = 0; i < b.length; i++) {
         if (b[i] == moduleCategory) {
            if (this.i != i) {
               this.i = i;
               this.m.d();
               return;
            }

            return;
         }
      }
   }

   @Override
   public String[] c() {
      return a;
   }

   @Override
   public int d() {
      return this.i;
   }

   @Override
   public void a(int i) {
      if (i >= 0 && i < b.length && this.i != i) {
         this.i = i;
         this.m.d();
      }
   }

   @Override
   public List<? extends GuiEntry> e() {
      if (!this.s) {
         List<ModulesTab.ModuleEntry> list = this.j.get(this.g());
         return list == null ? Collections.emptyList() : list;
      } else {
         return this.r != null && !this.r.isEmpty() ? this.r : Collections.emptyList();
      }
   }

   @Override
   public int f() {
      return 2;
   }

   public List<ModuleCard> h() {
      return this.m.a();
   }

   public boolean i() {
      Iterator<ModuleCard> it = this.m.a().iterator();

      while (it.hasNext()) {
         if (!it.next().f()) {
            return true;
         }
      }

      return false;
   }

   public boolean j() {
      return this.m.j();
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D) {
      this.m.a(MatrixStackVar, renderer2D);
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, int i, int i2) {
      this.m.a(MatrixStackVar, renderer2D, i, i2);
   }

   public void k() {
      this.m.e();
   }

   public void l() {
      this.m.f();
   }

   public void m() {
      this.m.g();
   }

   public void n() {
      this.m.h();
   }

   public void o() {
      this.m.i();
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i, int i2) {
      this.m.b();
      this.l.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      float fD = f2 + PulseClickGuiScreen.d() - 19.0F - 15.0F;
      float f4 = fD - 4.0F - 117.5F;
      float f5 = f3 + 19.0F - 3.0F;
      this.o.a(MatrixStackVar, renderer2D, f4, f5, 117.5F, 15.0F, i, i2);
      this.p.a(MatrixStackVar, renderer2D, fD, f5, 15.0F, i, i2);
      this.m.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
      this.n.a(MatrixStackVar, renderer2D, f2, f3, i, i2);
   }

   @Override
   public void a(float f2, float f3, int i, int i2) {
      this.m.b();
      if (!this.o.a(i, i2) && !this.p.a(i, i2)) {
         if (!this.m.j() && !this.o.c()) {
            this.l.a(f2, f3, i, i2);
         }

         this.m.a(f2, f3, i, i2);
      }
   }

   @Override
   public void b(float f2, float f3, int i, int i2) {
      this.m.b();
      this.m.b(f2, f3, i, i2);
   }

   @Override
   public void c(float f2, float f3, int i, int i2) {
      this.m.c(f2, f3, i, i2);
   }

   @Override
   public void a(float f2, float f3, int i, int i2, double d2, double d3) {
      this.m.a(f2, f3, i, i2, d2, d3);
   }

   @Override
   public void a(float f2) {
      this.m.a(f2);
   }

   public void a(float f2, int i, int i2) {
      this.m.a(f2, i, i2);
   }

   @Override
   public boolean a(int i, int i2, int i3) {
      if (this.m.p()) {
         this.m.a(i, i2, i3);
         return true;
      } else {
         return this.o.c() && this.o.a(i, i2, i3) ? true : this.m.a(i, i2, i3);
      }
   }

   @Override
   public boolean b() {
      return Bool.from(!this.o.c() && !this.m.c() ? 0 : 1);
   }

   public boolean a(char c2, int i) {
      return this.o.c() ? this.o.a(c2, i) : this.m.a(c2, i);
   }

   public boolean p() {
      return this.o.c();
   }

   public boolean hasOpenSettingsAt(int i, int i2) {
      return this.m.hasOpenSettingsAt(i, i2);
   }

   public ModuleCard getCardAt(int i, int i2) {
      return this.m.findCardByBounds(i, i2);
   }

   public void openSettings(ClientModule clientModule) {
      this.m.a(clientModule);
   }

   public ModuleCard findCardAtBounds(int i, int i2) {
      for (ModuleCard moduleCard : this.m.a()) {
         if (!moduleCard.f() && moduleCard.e(i, i2)) {
            return moduleCard;
         }
      }

      return null;
   }

   public void openSettingsForCard(ModuleCard moduleCard) {
      if (moduleCard != null && !moduleCard.f()) {
         moduleCard.b();
      }
   }

   public String q() {
      return this.q;
   }

   public boolean r() {
      return this.s;
   }

   @Override
   public ClickGuiTabType a() {
      return ClickGuiTabType.MODULES;
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class ModuleEntry implements GuiEntry {
      private final ClientModule a;

      public ModuleEntry(ClientModule clientModule) {
         this.a = clientModule;
      }

      @Override
      public String a() {
         return this.a.g();
      }

      @Override
      public boolean b() {
         return this.a.k();
      }

      @Override
      public void a(boolean z) {
         this.a.b(z);
      }

      public ModuleCategory i() {
         return this.a.i();
      }

      @Override
      public ClientModule e() {
         return this.a;
      }

      @Override
      public boolean d() {
         return this.g();
      }

      public static String b(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
