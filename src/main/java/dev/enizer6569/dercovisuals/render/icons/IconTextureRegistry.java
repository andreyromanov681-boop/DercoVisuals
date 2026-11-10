// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render.icons;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import dev.enizer6569.dercovisuals.DercoVisuals;

public final class IconTextureRegistry {
   public static final String WATER = "water";
   public static final String ARROW_V = "arrow_v";
   public static final String KEYBOARD = "keyboard";
   public static final String ICON = "icon";
   public static final String BUBBLE_1 = "bubble_1";
   public static final String BUBBLE_2 = "bubble_2";
   public static final String LIGHTNING = "lightning";
   public static final String PULSE_ICON = "dercovisuals_icon";
   public static final String SEARCH = "search";
   public static final String ARROW = "arrow";
   public static final String UP_ARROW = "up_arrow";
   public static final String LOGO = "logo";
   public static final String CALENDAR = "calendar";
   public static final String DEATH = "death";
   public static final String DIAMOND = "diamond";
   public static final String FAST = "fast";
   public static final String HOME = "home";
   public static final String LOCKED = "locked";
   public static final String MOUNTAIN = "mountain";
   public static final String REPAIR = "repair";
   public static final String SHIELD = "shield";
   public static final String TRASH = "trash";
   public static final String PLAY = "play";
   public static final String SHARE = "share";
   public static final String SAVE = "save";
   public static final String PLUS = "plus";
   public static final String KEY = "key";
   public static final String EDIT = "edit";
   public static final String LOAD = "load";
   public static final String PULSE_ICO = "dercovisuals_ico";
   private static final String DEFAULT_KEY = "icon";
   private static final Map<String, IconTextureRegistry.TextureInfo> TEXTURES = new HashMap<>();

   private IconTextureRegistry() {
   }

   public static void load() {
      if (TEXTURES.isEmpty()) {
         register("shadow", "textures/shadow.png");
         register("logo", "textures/logo.png");
         register("icon", "textures/icon.png");
         register("bubble_1", "textures/bubble_1.png");
         register("bubble_2", "textures/bubble_2.png");
         register("up_arrow", "textures/up_arrow.png");
         register("arrow", "textures/arrow.png");
         register("dercovisuals", "textures/pulse.png");
         register("dercovisuals_logo", "textures/dercovisuals_logo.png");
         register("splash_logo", "textures/splash_logo.png");
         register("target", "textures/target.png");
         register("water", "textures/clickgui/dercovisuals_water.png");
         register("dercovisuals_water", "textures/clickgui/dercovisuals_water.png");
         register("search", "textures/clickgui/search.png");
         register("keyboard", "textures/clickgui/keyboard.png");
         register("calendar", "textures/clickgui/calendar.png");
         register("death", "textures/clickgui/death.png");
         register("diamond", "textures/clickgui/diamond.png");
         register("fast", "textures/clickgui/fast.png");
         register("home", "textures/clickgui/home.png");
         register("locked", "textures/clickgui/locked.png");
         register("mountain", "textures/clickgui/mountain.png");
         register("repair", "textures/clickgui/repair.png");
         register("shield", "textures/clickgui/shield.png");
         register("trash", "textures/clickgui/trash.png");
         register("play", "textures/clickgui/play.png");
         register("share", "textures/clickgui/share.png");
         register("save", "textures/clickgui/save.png");
         register("plus", "textures/clickgui/plus.png");
         register("key", "textures/clickgui/key.png");
         register("edit", "textures/clickgui/edit.png");
         register("load", "textures/clickgui/load.png");
         register("dercovisuals_ico", "textures/clickgui/dercovisuals_ico.png");
         register("menu_bg", "textures/menu_bg.png");
         register("gear_custom", "textures/gear_icon.png");
         register("account_modal", "textures/account_modal.png");
         register("keybind_bg", "textures/keybind_bg.png");
         register("bell", "textures/clickgui/bell.png");
         register("warning", "textures/clickgui/warning.png");
         register("music", "textures/clickgui/music.png");
         register("arrows_left", "textures/clickgui/arrows_left.png");
         register("arrows_right", "textures/clickgui/arrows_right.png");
         register("pause", "textures/clickgui/pause.png");
         register("play_button", "textures/clickgui/play_button.png");
         register("potions", "textures/clickgui/potions.png");
         register("hotkeys", "textures/clickgui/hotkeys.png");
         register("cooldowns", "textures/clickgui/cooldowns.png");
         register("configs", "textures/clickgui/configs.png");
         register("events", "textures/clickgui/events.png");
         register("friends", "textures/clickgui/friends.png");
         register("hud", "textures/clickgui/hud.png");
         register("markers", "textures/clickgui/markers.png");
         register("modules", "textures/clickgui/modules.png");
         register("utilities", "textures/clickgui/utilities.png");
         register("visuals", "textures/clickgui/visuals.png");
         register("example_group", "textures/clickgui/example_group.png");
         registerPotionAliases();
      }
   }

   public static Identifier get(String str) {
      IconTextureRegistry.TextureInfo info = getInfo(str);
      return info == null ? Identifier.of("dercovisuals", "textures/icon.png") : info.a();
   }

   @Nullable
   public static IconTextureRegistry.TextureInfo getInfo(String str) {
      load();
      IconTextureRegistry.TextureInfo textureInfo = TEXTURES.get(normalizeKey(str));
      return textureInfo != null ? textureInfo : TEXTURES.get("icon");
   }

   private static void register(String str, String str2) {
      Identifier IdentifierVarOf = Identifier.of("dercovisuals", str2);
      int iGetWidth = 1;
      int iGetHeight = 1;

      try {
         InputStream resourceAsStream = IconTextureRegistry.class.getResourceAsStream("/assets/dercovisuals/" + str2);
         if (resourceAsStream == null) {
            DercoVisuals.getLOGGER().warn("[DercoVisualsIcons] Texture file NOT FOUND on classpath: /assets/dercovisuals/{} (key={}, id={})", str2, str, IdentifierVarOf);
         } else {
            NativeImage NativeImageVarRead = NativeImage.read(resourceAsStream);
            iGetWidth = NativeImageVarRead.getWidth();
            iGetHeight = NativeImageVarRead.getHeight();
            NativeImageVarRead.close();
            if (str.equals("logo")) {
               DercoVisuals.getLOGGER().info("[DercoVisualsIcons] Loaded LOGO texture: path=/assets/dercovisuals/{} id={} size={}x{}", str2, IdentifierVarOf, iGetWidth, iGetHeight);
            }
         }

         if (resourceAsStream != null) {
            resourceAsStream.close();
         }
      } catch (IOException e) {
         DercoVisuals.getLOGGER().warn("[DercoVisualsIcons] Failed to read texture: /assets/dercovisuals/{}", str2, e);
      }

      TEXTURES.put(str, new IconTextureRegistry.TextureInfo(IdentifierVarOf, iGetWidth, iGetHeight));
   }

   private static void registerPotionAliases() {
      IconTextureRegistry.TextureInfo textureInfo = TEXTURES.get("potions");

      for (String str : new String[]{
         "absorption",
         "bad_omen",
         "blindness",
         "conduit_power",
         "dolphins_grace",
         "fire_resistance",
         "glowing",
         "haste",
         "health_boost",
         "hero_of_the_village",
         "hunger",
         "instant_damage",
         "instant_health",
         "invisibility",
         "jump_boost",
         "levitation",
         "luck",
         "mining_fatigue",
         "nausea",
         "night_vision",
         "poison",
         "regeneration",
         "resistance",
         "saturation",
         "slow_falling",
         "slowness",
         "speed",
         "strength",
         "unluck",
         "water_breathing",
         "weakness",
         "wither"
      }) {
         TEXTURES.put(str, textureInfo);
      }
   }

   private static String normalizeKey(String str) {
      return str != null && !str.isBlank() && str.matches("[a-z0-9_./-]+") ? str : "icon";
   }

   public static final class TextureInfo {
      private final Identifier identifier;
      private final int width;
      private final int height;

      public TextureInfo(Identifier IdentifierVar, int i, int i2) {
         this.identifier = IdentifierVar;
         this.width = i;
         this.height = i2;
      }

      public Identifier a() {
         return this.identifier;
      }

      public int b() {
         return this.width;
      }

      public int c() {
         return this.height;
      }
   }
}
