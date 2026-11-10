package pulse.render.font;

import java.util.Objects;

public final class GradientGlyphDrawEntry {
   private final float x;
   private final float y;
   private final int topColor;
   private final int bottomColor;
   private final FontGlyph glyph;
   private final float totalHeight;

   public GradientGlyphDrawEntry(float f, float f2, int i, int i2, FontGlyph fontGlyph, float f3) {
      this.x = f;
      this.y = f2;
      this.topColor = i;
      this.bottomColor = i2;
      this.glyph = fontGlyph;
      this.totalHeight = f3;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public int topColor() {
      return this.topColor;
   }

   public int bottomColor() {
      return this.bottomColor;
   }

   public FontGlyph glyph() {
      return this.glyph;
   }

   public float totalHeight() {
      return this.totalHeight;
   }

   public float a() {
      return this.x;
   }

   public float b() {
      return this.y;
   }

   public int c() {
      return this.topColor;
   }

   public int d() {
      return this.bottomColor;
   }

   public FontGlyph e() {
      return this.glyph;
   }

   public float f() {
      return this.totalHeight;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof GradientGlyphDrawEntry gradientGlyphDrawEntry)
            ? false
            : Float.compare(gradientGlyphDrawEntry.x, this.x) == 0
               && Float.compare(gradientGlyphDrawEntry.y, this.y) == 0
               && this.topColor == gradientGlyphDrawEntry.topColor
               && this.bottomColor == gradientGlyphDrawEntry.bottomColor
               && Float.compare(gradientGlyphDrawEntry.totalHeight, this.totalHeight) == 0
               && Objects.equals(this.glyph, gradientGlyphDrawEntry.glyph);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.topColor, this.bottomColor, this.glyph, this.totalHeight);
   }

   @Override
   public String toString() {
      return "GradientGlyphDrawEntry{x="
         + this.x
         + ", y="
         + this.y
         + ", topColor="
         + this.topColor
         + ", bottomColor="
         + this.bottomColor
         + ", glyph="
         + this.glyph
         + ", totalHeight="
         + this.totalHeight
         + "}";
   }
}
