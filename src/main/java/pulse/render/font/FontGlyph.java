package pulse.render.font;

import java.util.Objects;

public final class FontGlyph {
   private final int u;
   private final int v;
   private final int width;
   private final int height;
   private final char value;
   private final FontGlyphAtlas atlas;

   public FontGlyph(int i, int i2, int i3, int i4, char c, FontGlyphAtlas fontGlyphAtlas) {
      this.u = i;
      this.v = i2;
      this.width = i3;
      this.height = i4;
      this.value = c;
      this.atlas = fontGlyphAtlas;
   }

   public int u() {
      return this.u;
   }

   public int v() {
      return this.v;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public char value() {
      return this.value;
   }

   public FontGlyphAtlas atlas() {
      return this.atlas;
   }

   public int a() {
      return this.u;
   }

   public int b() {
      return this.v;
   }

   public int c() {
      return this.width;
   }

   public int d() {
      return this.height;
   }

   public char e() {
      return this.value;
   }

   public FontGlyphAtlas f() {
      return this.atlas;
   }

   public int g() {
      return this.u;
   }

   public int h() {
      return this.v;
   }

   public int i() {
      return this.width;
   }

   public int j() {
      return this.height;
   }

   public char k() {
      return this.value;
   }

   public FontGlyphAtlas l() {
      return this.atlas;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof FontGlyph fontGlyph)
            ? false
            : this.u == fontGlyph.u
               && this.v == fontGlyph.v
               && this.width == fontGlyph.width
               && this.height == fontGlyph.height
               && this.value == fontGlyph.value
               && Objects.equals(this.atlas, fontGlyph.atlas);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.u, this.v, this.width, this.height, this.value, this.atlas);
   }

   @Override
   public String toString() {
      return "FontGlyph{u="
         + this.u
         + ", v="
         + this.v
         + ", width="
         + this.width
         + ", height="
         + this.height
         + ", value="
         + this.value
         + ", atlas="
         + this.atlas
         + "}";
   }
}
