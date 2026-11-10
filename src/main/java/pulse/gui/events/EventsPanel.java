package pulse.gui.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.joml.Matrix3x2fStack;
import org.json.JSONArray;
import org.json.JSONObject;
import pulse.auth.AccountServiceClient;
import pulse.core.Bool;
import pulse.gui.core.GuiInput;
import pulse.gui.core.PanelFadeOverlay;
import pulse.gui.widgets.ScrollBar;
import pulse.render.Renderer2D;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import pulse.theme.Theme;

public class EventsPanel {
   private static final float a = 6.0F;
   private static final float b = 3.0F;
   private static final float c = 3.0F;
   private static final float d = 2.0F;
   private final PanelFadeOverlay l;
   private float o;
   private float p;
   private float q;
   private float r;
   private Consumer<EventInfo> s;
   private final List<EventInfo> e = new ArrayList<>();
   private final List<EventInfo> f = new ArrayList<>();
   private final List<EventInfo> g = new ArrayList<>();
   private final List<EventCard> h = new ArrayList<>();
   private final List<EventCard> i = new ArrayList<>();
   private final List<EventCard> j = new ArrayList<>();
   private int m = 0;
   private String n = "";
   private final ScrollBar k = new ScrollBar(2.0F, 20.0F);

   public EventsPanel() {
      this.k.b(10.0F);
      this.k.a(Theme.b);
      this.k.b(Theme.d);
      this.l = new PanelFadeOverlay(25, 5.0F, 9.0F);
   }

   public void a(Consumer<EventInfo> consumer) {
      this.s = consumer;
   }

   public void a(EventInfo eventInfo) {
      this.e.add(eventInfo);
      EventCard eventCard = new EventCard(eventInfo);
      eventCard.a(this::d);
      this.h.add(eventCard);
   }

   public void b(EventInfo eventInfo) {
      this.f.add(eventInfo);
      EventCard eventCard = new EventCard(eventInfo);
      eventCard.a(this::d);
      this.i.add(eventCard);
   }

   public void c(EventInfo eventInfo) {
      this.g.add(eventInfo);
      EventCard eventCard = new EventCard(eventInfo);
      eventCard.a(this::d);
      this.j.add(eventCard);
   }

   public void a() {
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.k.e();
   }

   public void b() {
      this.a(this.e, this.h);
      this.a(this.f, this.i);
      this.a(this.g, this.j);
   }

   private void a(List<EventInfo> list, List<EventCard> list2) {
      if (list.size() > 1) {
         ArrayList<int[]> arrayList = new ArrayList<>();

         for (int i = 0; i < list.size(); i++) {
            arrayList.add(new int[]{i, list.get(i).f()});
         }

         arrayList.sort((iArr, iArr2) -> Integer.compare(iArr[1], iArr2[1]));
         ArrayList arrayList2 = new ArrayList();
         ArrayList arrayList3 = new ArrayList();

         for (int[] iArr3 : arrayList) {
            arrayList2.add(list.get(iArr3[0]));
            arrayList3.add(list2.get(iArr3[0]));
         }

         list.addAll(arrayList2);
         list2.addAll(arrayList3);
      }
   }

   public void a(int i, AccountServiceClient accountServiceClient) {
   }

   private void a(List<EventInfo> list, List<EventCard> list2, JSONArray jSONArray, int i) {
      int size = list.size();

      for (int i2 = (size ^ 1) - 2 * (~size & 1); i2 >= 0; i2--) {
         EventInfo eventInfo = list.get(i2);
         int iA = this.a(jSONArray, eventInfo.b());
         if (iA > 0) {
            int i3 = 2 * (iA & ~i) - (iA ^ i);
            if (i3 <= 0) {
               list.remove(i2);
               list2.remove(i2);
            } else {
               eventInfo.a(i3);
            }
         }
      }
   }

   private int a(JSONArray jSONArray, int i) {
      int i2 = 0;
      if (i2 < jSONArray.length()) {
         try {
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            if (Integer.parseInt(jSONObject.getString("server").replaceAll("[^0-9]", "")) == i) {
               return jSONObject.getInt("timeSeconds");
            }
         } catch (Exception var5) {
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void d(EventInfo eventInfo) {
      if (this.s != null) {
         this.s.accept(eventInfo);
      }
   }

   public void a(int i) {
      if (this.m != i) {
         this.m = i;
         this.k.e();
      }
   }

   public void a(String str) {
      this.n = str.toLowerCase().trim();
      this.k.e();
   }

   private List<EventCard> e() {
      List<EventCard> list2 = switch (this.m) {
         case 0 -> this.h;
         case 1 -> this.i;
         case 2 -> this.j;
         default -> this.h;
      };
      return this.n.isEmpty()
         ? new ArrayList<>(list2)
         : list2.stream()
            .filter(
               eventCard -> {
                  EventInfo eventInfoA = eventCard.a();
                  return Bool.from(
                     !eventInfoA.a().toLowerCase().contains(this.n)
                           && !String.valueOf(eventInfoA.b()).contains(this.n)
                           && (eventInfoA.i() == null || !eventInfoA.i().toLowerCase().contains(this.n))
                           && (eventInfoA.d() == null || !eventInfoA.d().a().toLowerCase().contains(this.n))
                           && (eventInfoA.e() == null || !eventInfoA.e().a().toLowerCase().contains(this.n))
                        ? 0
                        : 1
                  );
               }
            )
            .collect(Collectors.toList());
   }

   public boolean c() {
      switch (this.m) {
         case 0:
            return this.e.isEmpty();
         case 1:
            return this.f.isEmpty();
         case 2:
            return this.g.isEmpty();
         default:
            return true;
      }
   }

   public void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4, int i, int i2, float f5) {
      this.o = f;
      this.p = f2;
      this.q = f3;
      this.r = f4;
      this.k.a();
      List<EventCard> listE = this.e();
      if (listE.isEmpty()) {
         this.a(MatrixStackVar, renderer2D, f, f2, f3, f4);
      } else {
         float f6 = f2 + 3.0F;
         float f7 = f4 - 3.0F - 3.0F;
         float fA = this.a(listE);
         boolean z = fA > f7;
         if (!z) {
            this.k.e();
         }

         renderer2D.b().a(f, f2 + 2.0F, f3, f4 - 8.0F, MatrixStackVar);
         float fB = f6 - this.k.b();
         float f8 = f + 1.0F;
         float f9 = f3 - 2.0F;

         for (EventCard eventCard : listE) {
            if (fB + 36.5F >= f6 && fB <= f6 + f7) {
               eventCard.a(MatrixStackVar, renderer2D, f8, fB, f9, i, i2, f5);
            }

            fB += 42.5F;
         }

         renderer2D.b().a(MatrixStackVar);
         if (z) {
            this.k.a(MatrixStackVar, renderer2D, f + f3 + 18.0F, f6, f7, fA, f7, i, i2, false);
         }

         this.l.a(MatrixStackVar, renderer2D, f, f2 + 1.5F, f3, f4, f5);
      }
   }

   private void a(Matrix3x2fStack MatrixStackVar, Renderer2D renderer2D, float f, float f2, float f3, float f4) {
      FontRenderer fontRenderer = FontManager.b[24];
      FontRenderer fontRenderer2 = FontManager.a[15];
      String str;
      String str2;
      if (this.c()) {
         str = "На сервере пока-что нет ивентов :(";
         str2 = "Подожди немного, скоро начнутся";
      } else {
         str = "Ничего не найдено";
         str2 = "Попробуй изменить запрос";
      }

      float fA = fontRenderer.a(str);
      float fA2 = fontRenderer2.a(str2);
      float f5 = f + (f3 - fA) / 2.0F;
      float f6 = f2 + f4 / 2.0F - 20.0F;
      float f7 = f + (f3 - fA2) / 2.0F;
      fontRenderer.a(str, f5, f6, Theme.a, MatrixStackVar);
      fontRenderer2.a(str2, f7, f6 + 15.0F, Theme.b, MatrixStackVar);
   }

   private float a(List<EventCard> list) {
      return list.isEmpty() ? 0.0F : list.size() * 42.5F - 6.0F;
   }

   public boolean a(float f, float f2, float f3, float f4, int i, int i2) {
      List<EventCard> listE = this.e();
      if (listE.isEmpty()) {
         return false;
      }

      float f5 = f2 + 3.0F;
      float f6 = f4 - 3.0F - 3.0F;
      float fA = this.a(listE);
      if (fA > f6 && this.k.a(f + f3 + 18.0F, f5, f6, fA, f6, i, i2)) {
         return true;
      }

      if (GuiInput.a(f, f5, f3, f6, i, i2)) {
         float fB = f5 - this.k.b();
         float f7 = f + 1.0F;
         float f8 = f3 - 2.0F;

         for (Iterator<EventCard> it = listE.iterator(); it.hasNext(); fB += 42.5F) {
            if (it.next().a(f7, fB, f8, i, i2)) {
               return true;
            }
         }
      }

      return false;
   }

   public void a(int i, int i2) {
      this.k.d();
   }

   public void a(int i, int i2, double d2, double d3) {
      if (this.k.c()) {
         List<EventCard> listE = this.e();
         float f = this.r - 3.0F - 3.0F;
         this.k.a(i2, this.a(listE), f);
      }
   }

   public void a(float f, int i, int i2) {
      if (GuiInput.a(this.o, this.p, this.q, this.r, i, i2)) {
         List<EventCard> listE = this.e();
         this.k.a(f, this.a(listE), this.r - 3.0F - 3.0F);
      }
   }

   public void d() {
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.e.clear();
      this.f.clear();
      this.g.clear();
      this.k.e();
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
