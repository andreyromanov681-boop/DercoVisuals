package pulse.render.font;

import java.util.Objects;

public final class GlyphDrawEntry {
   private final float x;
   private final float y;
   private final int color;
   private final FontGlyph glyph;

   public GlyphDrawEntry(float f, float f2, int i, FontGlyph fontGlyph) {
      this.x = f;
      this.y = f2;
      this.color = i;
      this.glyph = fontGlyph;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public int color() {
      return this.color;
   }

   public FontGlyph glyph() {
      return this.glyph;
   }

   public float a() {
      return this.x;
   }

   public float b() {
      return this.y;
   }

   public int c() {
      return this.color;
   }

   public FontGlyph d() {
      return this.glyph;
   }

   public float e() {
      return this.x;
   }

   public float f() {
      return this.y;
   }

   public int g() {
      return this.color;
   }

   public FontGlyph h() {
      return this.glyph;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof GlyphDrawEntry glyphDrawEntry)
            ? false
            : Float.compare(glyphDrawEntry.x, this.x) == 0
               && Float.compare(glyphDrawEntry.y, this.y) == 0
               && this.color == glyphDrawEntry.color
               && Objects.equals(this.glyph, glyphDrawEntry.glyph);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.color, this.glyph);
   }

   @Override
   public String toString() {
      return "GlyphDrawEntry{x=" + this.x + ", y=" + this.y + ", color=" + this.color + ", glyph=" + this.glyph + "}";
   }
}
