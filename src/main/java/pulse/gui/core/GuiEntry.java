package pulse.gui.core;

import pulse.module.ClientModule;

public interface GuiEntry {
   String a();

   boolean b();

   void a(boolean var1);

   default boolean c() {
      return true;
   }

   default boolean d() {
      return false;
   }

   default ClientModule e() {
      return null;
   }

   default int f() {
      ClientModule clientModuleE = this.e();
      return clientModuleE != null ? clientModuleE.j() : -1;
   }

   default void a(int i) {
      ClientModule clientModuleE = this.e();
      if (clientModuleE != null) {
         clientModuleE.a(i);
      }
   }

   default boolean g() {
      return true;
   }

   default boolean h() {
      return this.d();
   }
}
