package pulse.render;

public enum AttachmentPoint {
   FREE(-1),
   BODY(1),
   HEAD(3),
   ABOVE_HEAD(-1),
   RIGHT_ARM(-1),
   LEFT_ARM(-1),
   RIGHT_LEG(2),
   LEFT_LEG(0);

   private final int modelPartIndex;

   AttachmentPoint(int i) {
      this.modelPartIndex = i;
   }

   public int a() {
      return this.ordinal();
   }

   public int b() {
      return this.modelPartIndex;
   }

   public static AttachmentPoint a(int i) {
      return i >= 0 && i < values().length ? values()[i] : BODY;
   }
}
