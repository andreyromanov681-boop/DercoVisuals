package pulse.render.font;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.NativeImage.class_1012;
import pulse.render.RenderSystemHelper;

public class FontGlyphAtlas {
   public final Identifier a;
   public int b;
   public int c;
   private final Char2ObjectArrayMap<FontGlyph> glyphs = new Char2ObjectArrayMap();
   private final char firstChar;
   private final char lastCharExclusive;
   private final Font baseFont;
   private final int padding;
   private boolean built;

   public FontGlyphAtlas(char c, char c2, Font font, Identifier IdentifierVar, int i) {
      this.firstChar = c;
      this.lastCharExclusive = c2;
      this.baseFont = font;
      this.a = IdentifierVar;
      this.padding = i;
   }

   public static void uploadTexture(Identifier IdentifierVar, BufferedImage bufferedImage) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      NativeImage NativeImageVar = new NativeImage(class_1012.RGBA, width, height, false);
      WritableRaster raster = bufferedImage.getRaster();
      ColorModel colorModel = bufferedImage.getColorModel();
      Object objCreateRasterDataBuffer = createRasterDataBuffer(raster);

      for (int i = 0; i < height; i++) {
         for (int i2 = 0; i2 < width; i2++) {
            raster.getDataElements(i2, i, objCreateRasterDataBuffer);
            int a = colorModel.getAlpha(objCreateRasterDataBuffer);
            int r = colorModel.getRed(objCreateRasterDataBuffer);
            int g = colorModel.getGreen(objCreateRasterDataBuffer);
            int b = colorModel.getBlue(objCreateRasterDataBuffer);
            int argb = a << 24 | r << 16 | g << 8 | b;
            NativeImageVar.setColorArgb(i2, i, argb);
         }
      }

      NativeImageBackedTexture NativeImageBackedTextureVar = new NativeImageBackedTexture(() -> "pulse", NativeImageVar);
      NativeImageBackedTextureVar.upload();
      if (RenderSystem.isOnRenderThread()) {
         MinecraftClient.getInstance().getTextureManager().registerTexture(IdentifierVar, NativeImageBackedTextureVar);
      } else {
         RenderSystemHelper.recordRenderCall(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(IdentifierVar, NativeImageBackedTextureVar));
      }
   }

   public static void a(Identifier IdentifierVar, BufferedImage bufferedImage) {
      uploadTexture(IdentifierVar, bufferedImage);
   }

   private static Object createRasterDataBuffer(WritableRaster writableRaster) {
      int dataType = writableRaster.getDataBuffer().getDataType();
      int numDataElements = writableRaster.getNumDataElements();
      switch (dataType) {
         case 0:
            return new byte[numDataElements];
         case 1:
            return new short[numDataElements];
         case 2:
         default:
            throw new IllegalArgumentException("Unsupported data buffer type: " + dataType);
         case 3:
            return new int[numDataElements];
      }
   }

   public FontGlyph glyph(char c) {
      if (!this.built) {
         this.build();
      }

      return (FontGlyph)this.glyphs.get(c);
   }

   public FontGlyph a(char c) {
      return this.glyph(c);
   }

   public boolean contains(char c) {
      return c >= this.firstChar && c < this.lastCharExclusive;
   }

   public boolean b(char c) {
      return this.contains(c);
   }

   private Font fontFor(char c) {
      return this.baseFont.canDisplay(c) ? this.baseFont : new Font("SansSerif", 0, this.baseFont.getSize());
   }

   public void build() {
      if (!this.built) {
         int iMax = Math.max(this.lastCharExclusive - this.firstChar, 1);
         int iMax2 = Math.max((int)(Math.ceil(Math.sqrt(iMax)) * 1.5), 1);
         this.glyphs.clear();
         int iMax3 = 0;
         int iMax4 = 0;
         int i = 0;
         int i2 = 0;
         int iMax5 = 0;
         int i3 = 0;
         ArrayList<FontGlyph> arrayList = new ArrayList<>();
         FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, false);

         for (int i4 = 0; i4 < iMax; i4++) {
            char c = (char)(this.firstChar + i4);
            Rectangle2D stringBounds = this.fontFor(c).getStringBounds(String.valueOf(c), fontRenderContext);
            int iCeil = (int)Math.ceil(stringBounds.getWidth());
            int iCeil2 = (int)Math.ceil(stringBounds.getHeight());
            iMax3 = Math.max(iMax3, i + iCeil);
            iMax4 = Math.max(iMax4, i2 + iCeil2);
            if (i3 >= iMax2) {
               i = 0;
               i2 += iMax5 + this.padding;
               i3 = 0;
               iMax5 = 0;
            }

            iMax5 = Math.max(iMax5, iCeil2);
            arrayList.add(new FontGlyph(i, i2, iCeil, iCeil2, c, this));
            i += iCeil + this.padding;
            i3++;
         }

         BufferedImage bufferedImage = new BufferedImage(Math.max(iMax3 + this.padding, 1), Math.max(iMax4 + this.padding, 1), 2);
         this.b = bufferedImage.getWidth();
         this.c = bufferedImage.getHeight();
         Graphics2D graphics2DCreateGraphics = bufferedImage.createGraphics();

         try {
            graphics2DCreateGraphics.setColor(new Color(255, 255, 255, 0));
            graphics2DCreateGraphics.fillRect(0, 0, this.b, this.c);
            graphics2DCreateGraphics.setColor(Color.WHITE);
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics2DCreateGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (FontGlyph fontGlyph : arrayList) {
               graphics2DCreateGraphics.setFont(this.fontFor(fontGlyph.value()));
               graphics2DCreateGraphics.drawString(
                  String.valueOf(fontGlyph.value()), fontGlyph.u(), fontGlyph.v() + graphics2DCreateGraphics.getFontMetrics().getAscent()
               );
               this.glyphs.put(fontGlyph.value(), fontGlyph);
            }

            uploadTexture(this.a, bufferedImage);
            this.built = true;
         } finally {
            graphics2DCreateGraphics.dispose();
         }
      }
   }

   public void a() {
      this.build();
   }
}
