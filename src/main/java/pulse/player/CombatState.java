package pulse.player;

import java.util.regex.Pattern;
import lombok.Generated;
import pulse.client.MinecraftContext;

public final class CombatState implements MinecraftContext {
   private static final Pattern a = Pattern.compile("(?i)(?:pvp|пвп)");
   private static final Pattern b = Pattern.compile("(\\d+)");

   public static CombatState.PvpStatus a() {
      return new CombatState.PvpStatus(true, 0);
   }

   @Generated
   private CombatState() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   public static class PvpStatus {
      private final boolean a;
      private final int b;

      @Generated
      public PvpStatus(boolean z, int i) {
         this.a = z;
         this.b = i;
      }

      @Generated
      public boolean a() {
         return this.a;
      }

      @Generated
      public int b() {
         return this.b;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
