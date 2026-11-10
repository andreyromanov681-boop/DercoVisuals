// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.text.Style;
import net.minecraft.StringVisitable.class_5245;
import net.minecraft.StringVisitable.class_5246;
import net.minecraft.PlainTextContent.class_2585;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.utilities.StreamerMode;

@Mixin(class_2585.class)
public class TextMixin implements MinecraftContext {
   @Shadow
   @Final
   private String comp_737;
   @Unique
   private static final Pattern holyWorldPattern = Pattern.compile("(СолоЛайт|ДуоЛайт|ТриоЛайт|КланЛайт)\\s*#(\\d{1,2})");
   @Unique
   private static final Pattern funTimePattern = Pattern.compile("Анархия-(\\d+)");

   @Redirect(
      require = 0,
      method = "visit",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/StringVisitable$class_5245;accept(Ljava/lang/String;)Ljava/util/Optional;")
   )
   private <ConfigTextDialog> Optional<ConfigTextDialog> redirectVisitor(class_5245<ConfigTextDialog> class_5245Var, String str) {
      return class_5245Var.accept(this.getProcessedText(str));
   }

   @Redirect(
      require = 0,
      method = "visit",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/StringVisitable$class_5246;accept(Lnet/minecraft/Style;Ljava/lang/String;)Ljava/util/Optional;")
   )
   private <ConfigTextDialog> Optional<ConfigTextDialog> redirectStyledVisitor(class_5246<ConfigTextDialog> class_5246Var, Style StyleVar, String str) {
      return class_5246Var.accept(StyleVar, this.getProcessedText(str));
   }

   @Unique
   private String getProcessedText(String str) {
      if (str == null) {
         return str;
      }

      try {
         String strHideServerNumber = str;
         if (this.shouldReplaceNick() && c.getSession() != null) {
            String username = c.getSession().getUsername();
            if (username != null && !username.isEmpty()) {
               strHideServerNumber = strHideServerNumber.replace(username, "dercovisuals");
            }
         }

         if (this.shouldHideServerNumber()) {
            strHideServerNumber = this.hideServerNumber(strHideServerNumber);
         }

         return strHideServerNumber;
      } catch (Throwable t) {
         return str;
      }
   }

   @Unique
   private boolean shouldReplaceNick() {
      try {
         StreamerMode streamerMode = ModuleRegistry.STREAMER_MODE;
         return streamerMode != null && streamerMode.k() && streamerMode.hidePlayerName.k() && c != null && c.getSession() != null;
      } catch (Exception e) {
         return false;
      }
   }

   @Unique
   private boolean shouldHideServerNumber() {
      try {
         StreamerMode streamerMode = ModuleRegistry.STREAMER_MODE;
         if (streamerMode != null && streamerMode.k() && streamerMode.hideServerNumber.k() && c != null && c.getCurrentServerEntry() != null) {
            String lowerCase = c.getCurrentServerEntry().address.toLowerCase();
            return lowerCase.contains("holyworld") || lowerCase.contains("funtime");
         } else {
            return false;
         }
      } catch (Exception e) {
         return false;
      }
   }

   @Unique
   private String hideServerNumber(String str) {
      if (str == null) {
         return str;
      }

      Matcher matcher = holyWorldPattern.matcher(str);
      if (matcher.find()) {
         return matcher.replaceAll("pulsevisuals.pro");
      }

      Matcher matcher2 = funTimePattern.matcher(str);
      return matcher2.find() ? matcher2.replaceAll("pulsevisuals.pro") : str;
   }
}
