package pulse.gui.core;

import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import pulse.client.MinecraftContext;

public class ForwardingScreen extends Screen implements MinecraftContext {
   private final Screen a;
   private final ForwardingScreen.Payload b;
   private static final int e = 200;
   private static final int f = 20;

   public ForwardingScreen(Screen ScreenVar, ForwardingScreen.Payload payload) {
      super(Text.literal("Pulse"));
      this.a = ScreenVar;
      this.b = payload;
   }

   protected void init() {
   }

   public void render(DrawContext DrawContextVar, int i, int i2, float f2) {
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void close() {
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class Payload {
      public final boolean a;
      public final String b;
      public final String c;
      public final String d;
      public final boolean e;
      public final String f;
      public final String g;

      public Payload(boolean z, String str, String str2, String str3, boolean z2, String str4, String str5) {
         this.a = z;
         this.b = str;
         this.c = str2;
         this.d = str3;
         this.e = z2;
         this.f = str4;
         this.g = str5;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
