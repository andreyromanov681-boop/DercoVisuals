package pulse.gui.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import pulse.core.Bool;

public class GuiLayerRegistry {
   private static final GuiLayerRegistry c = new GuiLayerRegistry();
   private final Map<GuiLayerRegistry.Layer, List<GuiLayerRegistry.LayerEntry>> d = new EnumMap<>(GuiLayerRegistry.Layer.class);
   private GuiLayerRegistry.Layer e = null;
   private int f = 0;
   private int g = 0;
   public static int a;
   public static boolean b;

   private GuiLayerRegistry() {
      for (GuiLayerRegistry.Layer layer : GuiLayerRegistry.Layer.values()) {
         this.d.put(layer, new ArrayList<>());
      }
   }

   public static GuiLayerRegistry a() {
      return c;
   }

   public void b() {
      Iterator<List<GuiLayerRegistry.LayerEntry>> it = this.d.values().iterator();

      while (it.hasNext()) {
         it.next().clear();
      }

      this.e = null;
   }

   public void a(int i, int i2) {
      this.f = i;
      this.g = i2;
      this.d();
   }

   public void a(GuiLayerRegistry.Layer layer, float f, float f2, float f3, float f4) {
      this.a(layer, f, f2, f3, f4, 0.0F);
   }

   public void a(GuiLayerRegistry.Layer layer, float f, float f2, float f3, float f4, float f5) {
      this.d.get(layer).add(new GuiLayerRegistry.LayerEntry(f, f2, f3, f4, f5));
      this.d();
   }

   public void a(GuiLayerRegistry.Layer layer) {
      this.d.get(layer).clear();
      this.d();
   }

   private void d() {
      this.e = null;
      int iA = -1;

      for (GuiLayerRegistry.Layer layer : GuiLayerRegistry.Layer.values()) {
         Iterator<GuiLayerRegistry.LayerEntry> it = this.d.get(layer).iterator();

         while (it.hasNext()) {
            if (it.next().a(this.f, this.g) && layer.a() > iA) {
               iA = layer.a();
               this.e = layer;
            }
         }
      }
   }

   public boolean b(GuiLayerRegistry.Layer layer) {
      return this.e == null ? true : Bool.from(layer.a() < this.e.a() ? 0 : 1);
   }

   public boolean a(GuiLayerRegistry.Layer layer, double d, double d2) {
      GuiLayerRegistry.Layer layerA = this.a(d, d2);
      if (layerA == null) {
         return true;
      }

      int i;
      if (layer.a() < layerA.a()) {
         i = 0;
      } else {
         i = 1;
      }

      return Bool.from(i);
   }

   public GuiLayerRegistry.Layer a(double d, double d2) {
      GuiLayerRegistry.Layer layer = null;
      int iA = -1;

      for (GuiLayerRegistry.Layer layer2 : GuiLayerRegistry.Layer.values()) {
         Iterator<GuiLayerRegistry.LayerEntry> it = this.d.get(layer2).iterator();

         while (it.hasNext()) {
            if (it.next().a(d, d2) && layer2.a() > iA) {
               iA = layer2.a();
               layer = layer2;
            }
         }
      }

      return layer;
   }

   public GuiLayerRegistry.Layer c() {
      return this.e;
   }

   public boolean c(GuiLayerRegistry.Layer layer) {
      return Bool.from(this.d.get(layer).isEmpty() ? 0 : 1);
   }

   public boolean d(GuiLayerRegistry.Layer layer) {
      for (GuiLayerRegistry.Layer layer2 : GuiLayerRegistry.Layer.values()) {
         if (layer2.a() > layer.a() && !this.d.get(layer2).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public boolean b(GuiLayerRegistry.Layer layer, double d, double d2) {
      Iterator<GuiLayerRegistry.LayerEntry> it = this.d.get(layer).iterator();

      while (it.hasNext()) {
         if (it.next().a(d, d2)) {
            return true;
         }
      }

      return false;
   }

   public boolean c(GuiLayerRegistry.Layer layer, double d, double d2) {
      for (GuiLayerRegistry.Layer layer2 : GuiLayerRegistry.Layer.values()) {
         if (layer2.a() >= layer.a() && this.b(layer2, d, d2)) {
            return true;
         }
      }

      return false;
   }

   public boolean d(GuiLayerRegistry.Layer layer, double d, double d2) {
      GuiLayerRegistry.Layer layerA = this.a(d, d2);
      return layerA == null ? false : Bool.from(layerA.a() <= layer.a() ? 0 : 1);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public enum Layer {
      BACKGROUND(0),
      CATEGORIES(1),
      TABS(2),
      CONTENT(3),
      SCROLLBAR(4),
      SETTINGS_PANEL(5),
      DROPDOWN(6),
      KEYBIND_PANEL(7),
      MODAL_OVERLAY(8);

      private final int order;

      Layer(int i) {
         this.order = i;
      }

      public int a() {
         return this.order;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return str;
      }
   }

   private static class LayerEntry {
      final float a;
      final float b;
      final float c;
      final float d;
      final float e;
      public static int f;
      public static boolean g;

      LayerEntry(float f2, float f3, float f4, float f5, float f6) {
         this.a = f2;
         this.b = f3;
         this.c = f4;
         this.d = f5;
         this.e = f6;
      }

      boolean a(double d, double d2) {
         return Bool.from(!(d < this.a) && !(d > this.a + this.c) && !(d2 < this.b) && !(d2 > this.b + this.d) ? 1 : 0);
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
