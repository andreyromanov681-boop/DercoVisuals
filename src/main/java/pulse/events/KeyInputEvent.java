package pulse.events;

public class KeyInputEvent extends PulseEvent {
   private final int keyCode;
   private final int scanCode;
   private final int action;
   private final int modifiers;

   public KeyInputEvent(int i, int i2, int i3, int i4) {
      this.keyCode = i;
      this.scanCode = i2;
      this.action = i3;
      this.modifiers = i4;
   }

   public int keyCode() {
      return this.keyCode;
   }

   public int scanCode() {
      return this.scanCode;
   }

   public int action() {
      return this.action;
   }

   public int modifiers() {
      return this.modifiers;
   }

   public int a() {
      return this.keyCode;
   }

   public int b() {
      return this.scanCode;
   }

   public int c() {
      return this.action;
   }

   public int d() {
      return this.modifiers;
   }
}
