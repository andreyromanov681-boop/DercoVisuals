// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render.font;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import dev.enizer6569.dercovisuals.render.RenderSystemHelper;
import dev.enizer6569.dercovisuals.render.Renderer2DImpl;

public class FontRenderer {
   private static final FontRenderContext a = new FontRenderContext(new AffineTransform(), true, true);
   private final Object2ObjectMap<Identifier, ObjectList<GlyphDrawEntry>> b = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap<Identifier, ObjectList<GradientGlyphDrawEntry>> c = new Object2ObjectOpenHashMap();
   private final ObjectList<FontGlyphAtlas> d = new ObjectArrayList();
   private Font e;
   private float f;

   public void a(Font font) {
      this.e = font;
   }

   public void a(float f) {
      this.f = f;
   }

   public float a() {
      return this.f;
   }

   public Font b() {
      return this.e;
   }

   public FontRenderer(Font font, float f) {
      this.a(font, f);
   }

   private static void a(String str, FontRenderer.GlyphPredicate glyphPredicate) {
      int length = str.length();

      for (int i = 0; i < length; i++) {
         if (!glyphPredicate.a(i, str.charAt(i))) {
            return;
         }
      }
   }

   @Contract(value = "-> new", pure = true)
   @NotNull
   public static Identifier c() {
      return Identifier.of("dercovisuals", "temp/" + d());
   }

   private void a(Font font, float f) {
      this.f = f;
      this.e = font.deriveFont(f * 2.0F);
   }

   private static String d() {
      Random random = new Random();
      return IntStream.range(0, 32).mapToObj(i -> String.valueOf((char)(97 + random.nextInt(26)))).collect(Collectors.joining());
   }

   private FontGlyphAtlas a(char c, char c2) {
      FontGlyphAtlas fontGlyphAtlas = new FontGlyphAtlas(c, c2, this.e, c(), 5);
      this.d.add(fontGlyphAtlas);
      return fontGlyphAtlas;
   }

   private FontGlyph b(char c) {
      ObjectListIterator it = this.d.iterator();

      while (it.hasNext()) {
         FontGlyphAtlas fontGlyphAtlas = (FontGlyphAtlas)it.next();
         if (fontGlyphAtlas.b(c)) {
            return fontGlyphAtlas.a(c);
         }
      }

      char cA = (char)a((int)c);
      return this.a(cA, (char)(cA + 256)).a(c);
   }

   private static int a(int i) {
      return 256 * (int)Math.floor(i / 256.0);
   }

   public void a(String str, double d, double d2, Object MatrixStackVar) {
      this.a(str, d, d2, Color.WHITE, MatrixStackVar);
   }

   public void a(String str, double d, double d2, Color color, Object MatrixStackVar) {
      this.a(MatrixStackVar, str, d, d2, color.getRGB());
   }

   public void a(String str, double d, double d2, Color color, Color color2, Object MatrixStackVar) {
      this.a(MatrixStackVar, str, d, d2, color.getRGB(), color2.getRGB(), this.b(str));
   }

   public void b(String str, double d, double d2, Color color, Object MatrixStackVar) {
      this.a(str, d - this.a(str) / 2.0, d2, color, MatrixStackVar);
   }

   public void c(String str, double d, double d2, Color color, Object MatrixStackVar) {
      this.a(str, d, d2 - this.b(str) / 2.0, color, MatrixStackVar);
   }

   private Matrix4f getMatrix(Object matrixStackVar) {
      Matrix4f m = new Matrix4f();
      if (matrixStackVar instanceof Matrix3x2fStack m32) {
         m.m00(m32.m00());
         m.m01(m32.m01());
         m.m10(m32.m10());
         m.m11(m32.m11());
         m.m30(m32.m20());
         m.m31(m32.m21());
      } else if (matrixStackVar instanceof MatrixStack ms) {
         m.set(ms.peek().getPositionMatrix());
      }

      return m;
   }

   public void a(Object MatrixStackVar, String str, double d, double d2, Color color) {
      if (color != null) {
         this.a(MatrixStackVar, str, d, d2, color.getRGB());
      }
   }

   public void a(Object MatrixStackVar, String str, double d, double d2, int i) {
      if (str != null && !str.isEmpty()) {
         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).push();
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).pushMatrix();
         }

         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).translate(d, d2, 0.0);
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).translate((float)d, (float)d2);
         }

         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).scale(0.5F, 0.5F, 0.5F);
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).scale(0.5F, 0.5F);
         }

         RenderSystemHelper.enableBlend();
         Matrix4f matrix4fGetPositionMatrix = this.getMatrix(MatrixStackVar);
         char[] charArray = str.toCharArray();
         float[] fArrC = this.c(str);
         float fB = 0.0F;
         int i2 = 0;

         for (int i3 = 0; i3 < charArray.length; i3++) {
            char c = charArray[i3];
            if (c == '\n') {
               fB += this.b(str.substring(i2, i3)) - 2.0F;
               i2 = i3 + 1;
               if (i2 < charArray.length) {
                  fArrC = this.c(str.substring(i2));
               }
            } else {
               FontGlyph fontGlyphB = this.b(c);
               if (fontGlyphB != null && fontGlyphB.e() != ' ') {
                  ((ObjectList)this.b.computeIfAbsent(fontGlyphB.f().a, obj -> new ObjectArrayList()))
                     .add(new GlyphDrawEntry(fArrC[i3 - i2], fB, i, fontGlyphB));
               }
            }
         }

         this.a(matrix4fGetPositionMatrix);
         RenderSystemHelper.disableBlend();
         this.b.clear();
         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).pop();
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).popMatrix();
         }
      }
   }

   public void a(Object MatrixStackVar, String str, double d, double d2, int i, int i2, float f) {
      if (str != null && !str.isEmpty()) {
         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).push();
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).pushMatrix();
         }

         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).translate(d, d2, 0.0);
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).translate((float)d, (float)d2);
         }

         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).scale(0.5F, 0.5F, 0.5F);
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).scale(0.5F, 0.5F);
         }

         RenderSystemHelper.enableBlend();
         Matrix4f matrix4fGetPositionMatrix = this.getMatrix(MatrixStackVar);
         char[] charArray = str.toCharArray();
         float[] fArrC = this.c(str);
         float fB = 0.0F;
         int i3 = 0;

         for (int i4 = 0; i4 < charArray.length; i4++) {
            char c = charArray[i4];
            if (c == '\n') {
               fB += this.b(str.substring(i3, i4)) - 2.0F;
               i3 = i4 + 1;
               if (i3 < charArray.length) {
                  fArrC = this.c(str.substring(i3));
               }
            } else {
               FontGlyph fontGlyphB = this.b(c);
               if (fontGlyphB != null && fontGlyphB.e() != ' ') {
                  ((ObjectList)this.c.computeIfAbsent(fontGlyphB.f().a, obj -> new ObjectArrayList()))
                     .add(new GradientGlyphDrawEntry(fArrC[i4 - i3], fB, i, i2, fontGlyphB, f));
               }
            }
         }

         this.b(matrix4fGetPositionMatrix);
         RenderSystemHelper.disableBlend();
         this.c.clear();
         if (MatrixStackVar instanceof MatrixStack) {
            ((MatrixStack)MatrixStackVar).pop();
         } else if (MatrixStackVar instanceof Matrix3x2fStack) {
            ((Matrix3x2fStack)MatrixStackVar).popMatrix();
         }
      }
   }

   private float[] c(String str) {
      if (str.isEmpty()) {
         return new float[0];
      }

      String str2 = str.split("\n")[0];
      float[] fArr = new float[str2.length()];
      GlyphVector glyphVectorCreateGlyphVector = this.e.createGlyphVector(a, str2);
      fArr[0] = 0.0F;
      float fRound = 0.0F;

      for (int i = 1; i < str2.length(); i++) {
         fRound += (float)glyphVectorCreateGlyphVector.getGlyphPosition(i).getX() - (float)glyphVectorCreateGlyphVector.getGlyphPosition(i - 1).getX();
         fArr[i] = fRound;
      }

      return fArr;
   }

   private void a(Matrix4f matrix4f) {
      if (Renderer2DImpl.currentDrawContext != null) {
         ObjectIterator it = this.b.keySet().iterator();

         while (it.hasNext()) {
            Identifier IdentifierVar = (Identifier)it.next();
            Renderer2DImpl.setTextureFilter(IdentifierVar, true);
            ObjectListIterator it2 = ((ObjectList)this.b.get(IdentifierVar)).iterator();

            while (it2.hasNext()) {
               GlyphDrawEntry glyphDrawEntry = (GlyphDrawEntry)it2.next();
               float fA = glyphDrawEntry.a();
               float fB = glyphDrawEntry.b();
               FontGlyph fontGlyphD = glyphDrawEntry.d();
               FontGlyphAtlas fontGlyphAtlasF = fontGlyphD.f();
               int u = fontGlyphD.a();
               int v = fontGlyphD.b();
               int width = fontGlyphD.c();
               int height = fontGlyphD.d();
               int iC = glyphDrawEntry.c();
               Renderer2DImpl.currentDrawContext
                  .drawTexture(
                     RenderPipelines.GUI_TEXTURED, IdentifierVar, Math.round(fA), Math.round(fB), u, v, width, height, fontGlyphAtlasF.b, fontGlyphAtlasF.c, iC
                  );
            }
         }
      }
   }

   private void b(Matrix4f matrix4f) {
      if (Renderer2DImpl.currentDrawContext != null) {
         ObjectIterator it = this.c.keySet().iterator();

         while (it.hasNext()) {
            Identifier IdentifierVar = (Identifier)it.next();
            Renderer2DImpl.setTextureFilter(IdentifierVar, true);
            ObjectListIterator it2 = ((ObjectList)this.c.get(IdentifierVar)).iterator();

            while (it2.hasNext()) {
               GradientGlyphDrawEntry gradientGlyphDrawEntry = (GradientGlyphDrawEntry)it2.next();
               float fA = gradientGlyphDrawEntry.a();
               float fB = gradientGlyphDrawEntry.b();
               FontGlyph fontGlyphE = gradientGlyphDrawEntry.e();
               FontGlyphAtlas fontGlyphAtlasF = fontGlyphE.f();
               int u = fontGlyphE.a();
               int v = fontGlyphE.b();
               int width = fontGlyphE.c();
               int height = fontGlyphE.d();
               int iC = gradientGlyphDrawEntry.c();
               Renderer2DImpl.currentDrawContext
                  .drawTexture(
                     RenderPipelines.GUI_TEXTURED, IdentifierVar, Math.round(fA), Math.round(fB), u, v, width, height, fontGlyphAtlasF.b, fontGlyphAtlasF.c, iC
                  );
            }
         }
      }
   }

   private Color a(Color color, Color color2, float f) {
      return new Color(
         (int)(color.getRed() + (color2.getRed() - color.getRed()) * f),
         (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f),
         (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f),
         (int)(color.getAlpha() + (color2.getAlpha() - color.getAlpha()) * f)
      );
   }

   public void b(Object MatrixStackVar, String str, double d, double d2, int i) {
      this.a(MatrixStackVar, str, (float)(d - this.a(str) / 2.0F), (float)d2, i);
   }

   public float a(String str) {
      if (str.isEmpty()) {
         return 0.0F;
      }

      float fMax = 0.0F;

      for (String str2 : str.split("\n")) {
         if (!str2.isEmpty()) {
            GlyphVector glyphVectorCreateGlyphVector = this.e.createGlyphVector(a, str2);
            float fRound = 0.0F;

            for (int i = 1; i <= str2.length(); i++) {
               float x = (float)glyphVectorCreateGlyphVector.getGlyphPosition(i - 1).getX();
               fRound += (float)glyphVectorCreateGlyphVector.getGlyphPosition(i).getX() - x;
            }

            fMax = Math.max(fMax, fRound);
         }
      }

      return fMax / 2.0F;
   }

   public float b(String str) {
      float f = 0.0F;
      float fD = 0.0F;

      for (char c : (str.isEmpty() ? " " : str).toCharArray()) {
         float fMax;
         if (c == '\n') {
            fD += f == 0.0F ? this.b(' ').d() : f;
            fMax = 0.0F;
         } else {
            FontGlyph glyph = this.b(c);
            fMax = Math.max(glyph == null ? 0.0F : glyph.d(), f);
         }

         f = fMax;
      }

      return f + fD;
   }

   public float a(char c) {
      return (float)this.e.createGlyphVector(a, String.valueOf(c)).getGlyphPosition(1).getX() / 2.0F;
   }

   public float a(String str, int i) {
      if (!str.isEmpty() && i > 0) {
         if (i > str.length()) {
            i = str.length();
         }

         GlyphVector glyphVectorCreateGlyphVector = this.e.createGlyphVector(a, str);
         float fRound = 0.0F;

         for (int i2 = 1; i2 <= i; i2++) {
            float x = (float)glyphVectorCreateGlyphVector.getGlyphPosition(i2 - 1).getX();
            fRound += (float)glyphVectorCreateGlyphVector.getGlyphPosition(i2).getX() - x;
         }

         return fRound / 2.0F;
      } else {
         return 0.0F;
      }
   }

   public int a(String str, float f) {
      if (!str.isEmpty() && !(f <= 0.0F)) {
         GlyphVector glyphVectorCreateGlyphVector = this.e.createGlyphVector(a, str);
         float f2 = f * 2.0F;
         float[] fArr = new float[str.length() + 1];
         fArr[0] = 0.0F;

         for (int i = 1; i <= str.length(); i++) {
            float x = (float)glyphVectorCreateGlyphVector.getGlyphPosition(i - 1).getX();
            fArr[i] = fArr[i - 1] + ((float)glyphVectorCreateGlyphVector.getGlyphPosition(i).getX() - x);
         }

         for (int i2 = 0; i2 < str.length(); i2++) {
            if (f2 < (fArr[i2] + fArr[i2 + 1]) / 2.0F) {
               return i2;
            }
         }

         return str.length();
      } else {
         return 0;
      }
   }

   public String a(String str, int i, boolean z) {
      return z ? this.c(str, i) : this.b(str, i);
   }

   public String b(String str, int i) {
      return str.substring(0, this.d(str, i));
   }

   private int d(String str, int i) {
      if (str.isEmpty()) {
         return 0;
      }

      GlyphVector glyphVectorCreateGlyphVector = this.e.createGlyphVector(a, str);
      float f = i * 2.0F;
      float fRound = 0.0F;

      for (int i2 = 1; i2 <= str.length(); i2++) {
         float x = (float)glyphVectorCreateGlyphVector.getGlyphPosition(i2 - 1).getX();
         fRound += (float)glyphVectorCreateGlyphVector.getGlyphPosition(i2).getX() - x;
         if (fRound > f) {
            return i2 - 1;
         }
      }

      return str.length();
   }

   public String c(String str, int i) {
      if (str.isEmpty()) {
         return str;
      }

      GlyphVector glyphVectorCreateGlyphVector = this.e.createGlyphVector(a, str);
      float f = i * 2.0F;
      float[] fArr = new float[str.length() + 1];
      fArr[0] = 0.0F;

      for (int i2 = 1; i2 <= str.length(); i2++) {
         float x = (float)glyphVectorCreateGlyphVector.getGlyphPosition(i2 - 1).getX();
         fArr[i2] = fArr[i2 - 1] + ((float)glyphVectorCreateGlyphVector.getGlyphPosition(i2).getX() - x);
      }

      float f2 = fArr[str.length()];
      if (f2 <= f) {
         return str;
      }

      for (int i3 = 1; i3 < str.length(); i3++) {
         if (f2 - fArr[i3] <= f) {
            return str.substring(i3);
         }
      }

      return "";
   }

   @FunctionalInterface
   public interface GlyphPredicate {
      boolean a(int var1, char var2);
   }
}
