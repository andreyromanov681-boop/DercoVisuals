package pulse.events;

public class MouseButtonEvent extends CancellablePulseEvent {
   private final int button;
   private final int action;
   private final int modifiers;
   private final double mouseX;
   private final double mouseY;

   public MouseButtonEvent(int i, int i2, int i3, double d, double d2) {
      this.button = i;
      this.action = i2;
      this.modifiers = i3;
      this.mouseX = d;
      this.mouseY = d2;
   }

   public int button() {
      return this.button;
   }

   public int action() {
      return this.action;
   }

   public int modifiers() {
      return this.modifiers;
   }

   public double mouseX() {
      return this.mouseX;
   }

   public double mouseY() {
      return this.mouseY;
   }

   public int d() {
      return this.button;
   }

   public int e() {
      return this.action;
   }

   public int f() {
      return this.modifiers;
   }

   public double g() {
      return this.mouseX;
   }

   public double h() {
      return this.mouseY;
   }
}
