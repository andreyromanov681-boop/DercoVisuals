package pulse.settings;

import java.util.function.Supplier;

public class TokenSetting extends Setting<String> {
   private final TokenSetting.TokenType tokenType;
   private final String suffix;

   public TokenSetting(String str, String str2, TokenSetting.TokenType tokenType, String str3, String str4) {
      super(str, str2, str3);
      this.tokenType = tokenType;
      this.suffix = str4;
   }

   public TokenSetting(String str, TokenSetting.TokenType tokenType, String str2, String str3) {
      this(str, "", tokenType, str2, str3);
   }

   public TokenSetting(String str, String str2) {
      this(str, "", TokenSetting.TokenType.TEXT, str2, "");
   }

   public TokenSetting(String str, TokenSetting.TokenType tokenType, String str2) {
      this(str, "", tokenType, str2, "");
   }

   public String get() {
      return this.k();
   }

   public void set(String str) {
      super.a(str);
   }

   public TokenSetting.TokenType tokenType() {
      return this.tokenType;
   }

   public String suffix() {
      return this.suffix;
   }

   public boolean isValid() {
      String strK = this.k();
      if (strK == null || strK.isEmpty()) {
         return false;
      }

      if (this.tokenType != TokenSetting.TokenType.NUMBER && this.tokenType != TokenSetting.TokenType.PRICE) {
         return true;
      }

      try {
         Double.parseDouble(strK);
         return true;
      } catch (NumberFormatException e) {
         return false;
      }
   }

   @Override
   public Setting<String> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   public String a() {
      return this.get();
   }

   public void a(String str) {
      this.set(str);
   }

   public TokenSetting.TokenType b() {
      return this.tokenType();
   }

   public String c() {
      return this.suffix();
   }

   public boolean d() {
      return this.isValid();
   }

   public TokenSetting a(Supplier<Boolean> supplier) {
      return (TokenSetting)this.visibleWhen(supplier);
   }

   public Setting<String> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }

   public enum TokenType {
      TEXT,
      COMMAND,
      PLAYER,
      NUMBER,
      PRICE;
   }
}
