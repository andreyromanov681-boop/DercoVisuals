package pulse.hud.snap;

public class HudSnapGuide {
   private final HudSnapGuide.Orientation orientation;
   private final HudSnapGuide.Anchor anchor;
   private final float position;
   private final float start;
   private final float end;

   public HudSnapGuide(HudSnapGuide.Orientation orientation, HudSnapGuide.Anchor anchor, float f, float f2, float f3) {
      this.orientation = orientation;
      this.anchor = anchor;
      this.position = f;
      this.start = f2;
      this.end = f3;
   }

   public HudSnapGuide.Orientation orientation() {
      return this.orientation;
   }

   public HudSnapGuide.Anchor anchor() {
      return this.anchor;
   }

   public float position() {
      return this.position;
   }

   public float start() {
      return this.start;
   }

   public float end() {
      return this.end;
   }

   public HudSnapGuide.Orientation a() {
      return this.orientation();
   }

   public HudSnapGuide.Anchor b() {
      return this.anchor();
   }

   public float c() {
      return this.position();
   }

   public float d() {
      return this.start();
   }

   public float e() {
      return this.end();
   }

   public enum Anchor {
      SCREEN_EDGE,
      SCREEN_CENTER,
      ELEMENT_EDGE,
      ELEMENT_CENTER;
   }

   public enum Orientation {
      HORIZONTAL,
      VERTICAL;
   }
}
