package pulse.markers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import pulse.client.MinecraftContext;
import pulse.events.HudRenderPreEvent;
import pulse.hud.core.HudService;
import pulse.hud.core.HudServiceInfo;
import pulse.hud.core.HudServiceRegistry;
import pulse.render.RenderSystemHelper;
import pulse.render.Renderer2D;
import pulse.render.ScreenScale;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.render.icons.IconTextureRegistry;
import pulse.render.world.WorldToScreen;
import pulse.theme.Theme;
import ru.pulse.Pulse;

@HudServiceInfo
public class MapMarkerRenderer extends HudService implements MinecraftContext {
   private static final float e = 22.0F;
   private static final float f = 6.0F;
   private static final float g = 3.0F;
   private static final float h = 16.0F;
   private static final float i = 5.0F;
   private static final float j = 10.0F;
   private static final float k = 2.5F;
   private static final float l = -7.0F;
   private static final float m = 8.0F;
   private static final float n = 0.0F;
   private static final float o = 64.0F;
   private static final float p = 5.0F;
   public static int a;
   public static boolean b;

   private static float a(float f2) {
      return Math.round(f2 * 2.0F) / 2.0F;
   }

   @EventHandler(priority = -100)
   public void a(HudRenderPreEvent hudRenderPreEvent) {
      if (c.player != null && c.world != null && !MarkerManager.d()) {
         ScreenScale.a();
         Renderer2D render = Pulse.getInstance().getRender();
         Matrix3x2fStack MatrixStackVarA = hudRenderPreEvent.a();
         ArrayList<MapMarker> arrayList = new ArrayList<>(MarkerManager.a());
         arrayList.sort(Comparator.<MapMarker>comparingDouble(mm -> this.a(mm)).reversed());
         Iterator<MapMarker> it = arrayList.iterator();

         while (it.hasNext()) {
            this.a(MatrixStackVarA, render, it.next());
         }

         ScreenScale.a();
      }
   }

   private double a(MapMarker mapMarker) {
      return c.player.getEntityPos().distanceTo(new Vec3d(mapMarker.b() + 0.5, mapMarker.c() + 1.5, mapMarker.d() + 0.5));
   }

   private boolean a(MapMarker mapMarker, MapMarkerModule mapMarkerModule) {
      int iH = mapMarkerModule.h();
      String strI = mapMarkerModule.i();
      return mapMarker.l() != iH ? false : mapMarker.m() == null || strI == null || mapMarker.m().equals(strI);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, MapMarker mapMarker) {
      if (mapMarker.j()) {
         MapMarkerModule mapMarkerModule = HudServiceRegistry.MAP_MARKER_MODULE;
         if (!MarkerOptions.d() || !mapMarkerModule.k() || !this.a(mapMarker, mapMarkerModule)) {
            return;
         }

         if (!MarkerOptions.e() && mapMarker.a().toLowerCase(Locale.ROOT).contains("загадочный маяк")) {
            return;
         }
      }

      Vec3d Vec3dVar = new Vec3d(mapMarker.b() + 0.5, mapMarker.c() + 1.5, mapMarker.d() + 0.5);
      Vec3d Vec3dVarA = WorldToScreen.a(Vec3dVar);
      if (Vec3dVarA != null && WorldToScreen.b(Vec3dVarA)) {
         float fA = a((float)Vec3dVarA.x);
         float fA2 = a((float)Vec3dVarA.y);
         String strA = this.a(c.player.getEntityPos().distanceTo(Vec3dVar));
         FontRenderer fontRenderer = FontManager.b[12];
         FontRenderer fontRenderer2 = FontManager.b[10];
         String strA2 = mapMarker.a();
         if (mapMarker.j() && mapMarker.r()) {
            long jP = mapMarker.p();
            if (jP > 0L) {
               int i2 = (int)(jP / 1000L);
               int i3 = i2 / 60;
               int i4 = i2 % 60;
               strA2 = i3 <= 0 ? strA2 + " " + i4 + " сек." : strA2 + " " + i3 + ":" + String.format("%02d", i4);
            }
         }

         float fMax = Math.max(72.0F, 21.5F + Math.max(fontRenderer.a(strA2), fontRenderer2.a(strA)) + 10.0F);
         float fA3 = a(fA - fMax / 2.0F);
         float fA4 = a(fA2 + 25.0F);
         this.a(MatrixStackVar, renderer2D, fA3, fA4, fMax, mapMarker);
         float fA5 = a(fA3 + 3.0F);
         this.a(MatrixStackVar, renderer2D, fA5, a(fA4 + 3.0F), mapMarker);
         float fA6 = a(fA5 + 16.0F + 2.5F);
         float fB = fontRenderer.b(strA2);
         float fB2 = fontRenderer2.b(strA);
         float fA7 = a(fA4 + 2.0F);
         float fA8 = a(fA7 + fB - 7.0F);
         float textAreaRight = fA3 + fMax - 6.0F;
         float textAreaCenter = (fA6 + textAreaRight) / 2.0F;
         float nameX = a(textAreaCenter - fontRenderer.a(strA2) / 2.0F);
         float coordX = a(textAreaCenter - fontRenderer2.a(strA) / 2.0F);
         fontRenderer.a(strA2, nameX, fA7, Theme.a, MatrixStackVar);
         fontRenderer2.a(strA, coordX, fA8, Theme.a, MatrixStackVar);
         if (mapMarker.j()) {
            this.a(MatrixStackVar, renderer2D, fA, fA4 + 22.0F);
         }
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, float f4, MapMarker mapMarker) {
      Color colorA = Theme.a(Theme.m, 200);
      Color colorA2 = Theme.a(Theme.n, 200);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, f4 + 1.0F, 23.0F, 6.0F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      Color colorA3 = Theme.a(Theme.e, 230);
      Color colorA4 = Theme.a(Theme.f, 230);
      renderer2D.a(f2, f3, f4, 22.0F, 6.0F, colorA3, colorA3, colorA4, colorA4, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3, MapMarker mapMarker) {
      Color colorE = mapMarker.e();
      Color colorC = Theme.c(colorE, 150);
      Color colorB = Theme.b(colorE, 50);
      Color colorA = Theme.a(colorB, 50);
      Color colorA2 = Theme.a(colorB, 10);
      renderer2D.a(f2 - 0.5F, f3 - 0.5F, 17.0F, 17.0F, 5.0F, colorA, colorA, colorA2, colorA2, MatrixStackVar);
      renderer2D.a(f2, f3, 16.0F, 16.0F, 5.0F, colorE, colorE, colorC, colorC, MatrixStackVar);
      float iconW = FontManager.e[18].a(mapMarker.f().b());
      FontManager.e[18].a(mapMarker.f().b(), f2 + (16.0F - iconW) / 2.0F, f3 + 2.5F, Theme.aa, MatrixStackVar);
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f2, float f3) {
      IconTextureRegistry.TextureInfo info = IconTextureRegistry.getInfo("up_arrow");
      if (info != null) {
         Identifier IdentifierVarA = info.a();
         RenderSystemHelper.setShaderTexture(0, IdentifierVarA);
         float fB = info.b() / 2.0F;
         float fC = info.c() / 2.0F;
         float f4 = f3 + fB / 2.0F;
         MatrixStackVar.pushMatrix();
         MatrixStackVar.translate(f2, f4);
         MatrixStackVar.rotate((float)Math.toRadians(90.0));
         MatrixStackVar.translate(-f2, -f4);
         renderer2D.a(IdentifierVarA, f2 - fB / 2.0F - 0.5F, f4 - fC / 2.0F, fB, fC, Theme.aa, MatrixStackVar);
         MatrixStackVar.popMatrix();
      }
   }

   private String a(double d) {
      if (d < 1.0) {
         return "<1 м";
      } else {
         return d < 1000.0 ? (int)d + " м" : String.format(Locale.ROOT, "%.1f км", d / 1000.0);
      }
   }

   public static String b(String str, String str2, int i2, int i3, int i4, int i5) {
      return null;
   }
}
