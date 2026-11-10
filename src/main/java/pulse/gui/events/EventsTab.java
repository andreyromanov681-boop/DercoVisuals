package pulse.gui.events;

import org.joml.Matrix3x2fStack;
import pulse.auth.AccountServiceClient;
import pulse.client.MinecraftContext;
import pulse.gui.core.ClickGuiTab;
import pulse.gui.core.ClickGuiTabType;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.gui.core.TabHost;
import pulse.gui.core.TabSelector;
import pulse.gui.widgets.SearchBox;
import pulse.render.Renderer2D;

public class EventsTab implements TabHost, MinecraftContext, ClickGuiTab {
   private static final String[] a = new String[]{"Текущие", "Предстоящие", "Шахты"};
   private static final String[] b = new String[]{"Текущие", "Предстоящие"};
   private static final float e = 44.5F;
   private static final float f = 19.0F;
   private static final float g = 117.5F;
   private static final float h = 15.0F;
   private static final float i = 19.0F;
   private static final float j = 19.0F;
   private final EventsPanel o;
   private final PanelFadeOverlay p;
   private int q;
   private int r;
   private int k = 0;
   private boolean l = false;
   private long s = 0L;
   private final TabSelector m = new TabSelector(this);
   private final SearchBox n = new SearchBox(117.5F, 15.0F);

   public EventsTab() {
      this.n.a(this::e);
      this.o = new EventsPanel();
      this.p = new PanelFadeOverlay(25, 10.0F, 7.5F);
      this.o.a(this::a);
      this.e();
   }

   private void e() {
      this.o.b();
   }

   private void a(AccountServiceClient accountServiceClient) {
   }

   private int a(String str) {
      try {
         return Integer.parseInt(str.replaceAll("[^0-9]", ""));
      } catch (Exception e2) {
         return 0;
      }
   }

   private EventInfo.EventKind b(String str) {
      if (str == null) {
         return EventInfo.EventKind.METEOR;
      }

      String lowerCase = str.toLowerCase();
      return !lowerCase.contains("маяк") && !lowerCase.contains("beacon") ? EventInfo.EventKind.METEOR : EventInfo.EventKind.BEACON;
   }

   private EventInfo.Rarity c(String str) {
      if (str == null) {
         return EventInfo.Rarity.LEGENDARY;
      }

      String lowerCase = str.toLowerCase();
      return lowerCase.contains("мифическ") || lowerCase.contains("mythic")
         ? EventInfo.Rarity.MYTHIC
         : (
            !lowerCase.contains("элит") && !lowerCase.contains("elite")
               ? (!lowerCase.contains("богат") && !lowerCase.contains("rich") ? EventInfo.Rarity.LEGENDARY : EventInfo.Rarity.RICH)
               : EventInfo.Rarity.ELITE
         );
   }

   private EventInfo.Rarity d(String str) {
      return str != null && !str.isEmpty() ? this.c(str) : EventInfo.Rarity.LEGENDARY;
   }

   private void e(String str) {
      this.o.a(str);
   }

   private void a(EventInfo eventInfo) {
   }

   private void f(String str) {
   }

   @Override
   public String[] c() {
      return this.l ? a : b;
   }

   @Override
   public int d() {
      return this.k;
   }

   @Override
   public void a(int i2) {
      String[] strArrC = this.c();
      if (i2 >= 0 && i2 < strArrC.length && this.k != i2) {
         this.k = i2;
         this.o.a(i2);
      }
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, int i2, int i3) {
      this.q = i2;
      this.r = i3;
      this.e();
      this.m.a(MatrixStackVar, renderer2D, f2, f3, i2, i3);
      float fD = PulseClickGuiScreen.d();
      float fE = PulseClickGuiScreen.e();
      this.n.a(MatrixStackVar, renderer2D, f2 + fD - 19.0F - 117.5F, f3 + 19.0F - 3.0F, 117.5F, 15.0F, i2, i3);
      float f4 = f3 + 44.5F;
      float f5 = fD - 38.0F;
      float f6 = fE - 44.5F - 9.5F;
      this.o.a(MatrixStackVar, renderer2D, f2 + 19.0F, f4, f5, f6 - 8.0F, i2, i3, 1.0F);
      this.p.a(MatrixStackVar, renderer2D, f2, f3, i2, i3);
   }

   @Override
   public void a(float f2, float f3, int i2, int i3) {
      if (!this.n.a(i2, i3)) {
         this.m.a(f2, f3, i2, i3);
         float fD = PulseClickGuiScreen.d();
         float fE = PulseClickGuiScreen.e();
         float f4 = f3 + 44.5F;
         float f5 = fD - 38.0F;
         float f6 = fE - 44.5F - 9.5F;
         this.o.a(f2 + 19.0F, f4, f5, f6 - 8.0F, i2, i3);
      }
   }

   @Override
   public void b(float f2, float f3, int i2, int i3) {
      this.a(f2, f3, i2, i3);
   }

   @Override
   public void c(float f2, float f3, int i2, int i3) {
      this.o.a(i2, i3);
   }

   @Override
   public void a(float f2, float f3, int i2, int i3, double d, double d2) {
      this.o.a(i2, i3, d, d2);
   }

   @Override
   public void a(float f2) {
      this.o.a(f2, this.q, this.r);
   }

   @Override
   public boolean a(int i2, int i3, int i4) {
      return this.n.a(i2, i3, i4);
   }

   @Override
   public boolean b() {
      return this.n.c();
   }

   public boolean a(char c, int i2) {
      return this.n.a(c, i2);
   }

   @Override
   public ClickGuiTabType a() {
      return ClickGuiTabType.EVENTS;
   }

   public static String b(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
