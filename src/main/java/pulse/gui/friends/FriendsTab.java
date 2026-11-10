package pulse.gui.friends;

import org.joml.Matrix3x2fStack;
import pulse.gui.core.ClickGuiTab;
import pulse.gui.core.ClickGuiTabType;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.core.PulseClickGuiScreen;
import pulse.gui.core.TabHost;
import pulse.gui.core.TabSelector;
import pulse.render.Renderer2D;

public class FriendsTab implements TabHost, ClickGuiTab {
   private static final String[] a = new String[]{"Список друзей"};
   private static final float b = 20.0F;
   private static final float c = 19.0F;
   private final TabSelector d = new TabSelector(this);
   private final FriendsPanel e = new FriendsPanel();
   private final PanelFadeOverlay f = new PanelFadeOverlay(25, 10.0F, 7.5F);
   private int g;
   private int h;

   @Override
   public String[] c() {
      return a;
   }

   @Override
   public int d() {
      return 0;
   }

   @Override
   public void a(int i) {
   }

   @Override
   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, int i, int i2) {
      this.g = i;
      this.h = i2;
      float fD = PulseClickGuiScreen.d();
      float fE = PulseClickGuiScreen.e();
      this.f.a(MatrixStackVar, renderer2D, f, f2, i, i2);
      this.e.a(MatrixStackVar, renderer2D, f + 19.0F, f2 + 20.0F, fD - 38.0F, fE - 20.0F - 5.0F, i, i2, 1.0F);
   }

   @Override
   public void a(float f, float f2, int i, int i2) {
      this.e.a(f + 19.0F, f2 + 20.0F, PulseClickGuiScreen.d() - 38.0F, PulseClickGuiScreen.e() - 20.0F - 5.0F, i, i2);
   }

   @Override
   public void b(float f, float f2, int i, int i2) {
   }

   @Override
   public void c(float f, float f2, int i, int i2) {
      this.e.a(i, i2);
   }

   @Override
   public void a(float f, float f2, int i, int i2, double d, double d2) {
      this.e.a(i, i2, d, d2);
   }

   @Override
   public void a(float f) {
      this.e.a(f, this.g, this.h);
   }

   @Override
   public boolean a(int i, int i2, int i3) {
      return this.e.a(i, i2, i3);
   }

   @Override
   public boolean b() {
      return this.e.c();
   }

   public boolean a(char c2, int i) {
      return this.e.a(c2, i);
   }

   @Override
   public ClickGuiTabType a() {
      return ClickGuiTabType.FRIENDS;
   }

   public FriendsPanel e() {
      return this.e;
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
