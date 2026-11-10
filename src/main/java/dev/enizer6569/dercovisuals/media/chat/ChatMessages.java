// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.media.chat;

import java.awt.Color;
import lombok.Generated;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.util.ColorUtils;
import dev.enizer6569.dercovisuals.DercoVisuals;

public final class ChatMessages implements MinecraftContext {
   private static final String e = "[pulse] ";
   public static int a;
   public static boolean b;

   public static void a(Text TextVar) {
      if (c.player != null) {
         c.player.sendMessage(TextVar, false);
      }
   }

   public static void a(String str) {
      if (c.player != null) {
         c.player.networkHandler.sendChatCommand(str);
      }
   }

   public static void a(Object obj) {
      if (obj == null) {
         obj = "null";
      }

      if (c.player == null) {
         DercoVisuals.getLOGGER().info("(CHAT) " + obj);
      } else {
         c.inGameHud
            .getChatHud()
            .addMessage(
               b("[DercoVisuals] ")
                  .append(Text.literal("» ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215))))
                  .append(Text.literal(("§7" + obj).replace("&", "§")))
            );
      }
   }

   public static void b(Text TextVar) {
      if (c.player != null) {
         c.inGameHud
            .getChatHud()
            .addMessage(
               b("[DercoVisuals] ")
                  .append(Text.literal("» ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215))))
                  .append(TextVar)
            );
      }
   }

   public static MutableText b(String str) {
      MutableText MutableTextVarLiteral = Text.literal("");
      Color color = new Color(a());
      Color colorA = a(color);
      int length = str.length();

      for (int i = 0; i < length; i++) {
         MutableTextVarLiteral.append(
            Text.literal(String.valueOf(str.charAt(i)))
               .setStyle(
                  Style.EMPTY
                     .withColor(TextColor.fromRgb(ColorUtils.a(color, colorA, length <= 1 ? 0.0F : i / ((length & -2) - (~length & 1))).getRGB()))
               )
         );
      }

      return MutableTextVarLiteral;
   }

   private static int a() {
      return ModuleRegistry.CLIENT_COLOR.o();
   }

   private static Color a(Color color) {
      float[] fArrRGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      return new Color(Color.HSBtoRGB(fArrRGBtoHSB[0], Math.max(0.0F, fArrRGBtoHSB[1] - 0.3F), Math.min(1.0F, fArrRGBtoHSB[2] + 0.2F)));
   }

   @Generated
   private ChatMessages() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
