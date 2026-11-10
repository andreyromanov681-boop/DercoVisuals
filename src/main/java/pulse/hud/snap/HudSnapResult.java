package pulse.hud.snap;

import java.util.Collections;
import java.util.List;

public class HudSnapResult {
   private final float x;
   private final float y;
   private final List<HudSnapGuide> guides;
   private final boolean snappedX;
   private final boolean snappedY;

   public HudSnapResult(float f, float f2, List<HudSnapGuide> list, boolean z, boolean z2) {
      this.x = f;
      this.y = f2;
      this.guides = list;
      this.snappedX = z;
      this.snappedY = z2;
   }

   public static HudSnapResult none(float f, float f2) {
      return new HudSnapResult(f, f2, Collections.emptyList(), false, false);
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public List<HudSnapGuide> guides() {
      return this.guides;
   }

   public boolean snappedX() {
      return this.snappedX;
   }

   public boolean snappedY() {
      return this.snappedY;
   }

   public boolean snapped() {
      return this.snappedX || this.snappedY;
   }

   public static HudSnapResult a(float f, float f2) {
      return none(f, f2);
   }

   public float a() {
      return this.x();
   }

   public float b() {
      return this.y();
   }

   public List<HudSnapGuide> c() {
      return this.guides();
   }

   public boolean d() {
      return this.snappedX();
   }

   public boolean e() {
      return this.snappedY();
   }

   public boolean f() {
      return this.snapped();
   }
}
