package pulse.settings;

import java.util.Locale;
import org.lwjgl.glfw.GLFW;

public class KeySetting extends Setting<Integer> {
   public KeySetting(String str, String str2, int i) {
      super(str, str2, i);
   }

   public KeySetting(String str, int i) {
      this(str, "", i);
   }

   public KeySetting(String str) {
      this(str, "", -1);
   }

   public int key() {
      return this.k();
   }

   public void setKey(int i) {
      super.a(i);
   }

   public boolean isBound() {
      return this.key() != -1;
   }

   public void clear() {
      this.setKey(-1);
   }

   public String displayName() {
      int iKey = this.key();
      if (iKey == -1) {
         return "";
      }

      String strGlfwGetKeyName = GLFW.glfwGetKeyName(iKey, 0);
      return strGlfwGetKeyName != null ? strGlfwGetKeyName.toUpperCase(Locale.ROOT) : "KEY " + iKey;
   }

   public int a() {
      return this.key();
   }

   public void a(int i) {
      this.setKey(i);
   }

   public boolean b() {
      return this.isBound();
   }

   public void c() {
      this.clear();
   }

   public String d() {
      return this.displayName();
   }
}
