package pulse.events;

public class DropItemEvent extends CancellablePulseEvent {
   private final int slot;
   private final boolean dropEntireStack;

   public DropItemEvent(int i, boolean z) {
      this.slot = i;
      this.dropEntireStack = z;
   }

   public int slot() {
      return this.slot;
   }

   public boolean dropEntireStack() {
      return this.dropEntireStack;
   }

   public int d() {
      return this.slot;
   }

   public boolean e() {
      return this.dropEntireStack;
   }
}
