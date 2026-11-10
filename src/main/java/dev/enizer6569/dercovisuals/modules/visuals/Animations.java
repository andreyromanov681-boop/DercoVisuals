// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.modules.visuals;

import dev.enizer6569.dercovisuals.animation.ChatInputSlideAnimation;
import dev.enizer6569.dercovisuals.animation.HotbarSelectionAnimation;
import dev.enizer6569.dercovisuals.animation.PerspectiveDistanceAnimation;
import dev.enizer6569.dercovisuals.animation.PlayerListScaleAnimation;
import dev.enizer6569.dercovisuals.module.ClientModule;
import dev.enizer6569.dercovisuals.module.ModuleCategory;
import dev.enizer6569.dercovisuals.module.ModuleInfo;
import dev.enizer6569.dercovisuals.settings.BooleanSetting;
import dev.enizer6569.dercovisuals.settings.SettingGroup;
import dev.enizer6569.dercovisuals.settings.SliderSetting;

@ModuleInfo(a = "Animations", b = "Interface animations", c = ModuleCategory.VISUALS)
public class Animations extends ClientModule {
   private final SettingGroup playerListGroup = new SettingGroup("Player List");
   public final BooleanSetting a = new BooleanSetting("Tab List", true);
   public final SliderSetting b;
   private final SettingGroup chatGroup;
   public final BooleanSetting e;
   public final SliderSetting f;
   private final SettingGroup hotbarGroup;
   public final BooleanSetting g;
   public final SliderSetting h;
   private final SettingGroup cameraGroup;
   public final BooleanSetting i;
   public final SliderSetting j;
   private static PlayerListScaleAnimation playerListAnimation;
   private static ChatInputSlideAnimation chatInputAnimation;
   private static HotbarSelectionAnimation hotbarSelectionAnimation;
   private static PerspectiveDistanceAnimation perspectiveDistanceAnimation;

   public Animations() {
      SliderSetting sliderSetting = new SliderSetting("Tab Duration", 200.0F, 50.0F, 500.0F, 10.0F);
      BooleanSetting booleanSetting = this.a;
      this.b = sliderSetting.a(booleanSetting::a);
      this.chatGroup = new SettingGroup("Chat");
      this.e = new BooleanSetting("Chat Messages", true);
      SliderSetting sliderSetting2 = new SliderSetting("Chat Duration", 200.0F, 50.0F, 500.0F, 10.0F);
      BooleanSetting booleanSetting2 = this.e;
      this.f = sliderSetting2.a(booleanSetting2::a);
      this.hotbarGroup = new SettingGroup("Hotbar");
      this.g = new BooleanSetting("Hotbar Selector", true);
      SliderSetting sliderSetting3 = new SliderSetting("Hotbar Duration", 100.0F, 50.0F, 500.0F, 10.0F);
      BooleanSetting booleanSetting3 = this.g;
      this.h = sliderSetting3.a(booleanSetting3::a);
      this.cameraGroup = new SettingGroup("Camera");
      this.i = new BooleanSetting("Perspective", true);
      SliderSetting sliderSetting4 = new SliderSetting("Perspective Duration", 300.0F, 50.0F, 1000.0F, 10.0F);
      BooleanSetting booleanSetting4 = this.i;
      this.j = sliderSetting4.a(booleanSetting4::a);
   }

   public static PlayerListScaleAnimation n() {
      if (playerListAnimation == null) {
         playerListAnimation = new PlayerListScaleAnimation();
      }

      return playerListAnimation;
   }

   public static ChatInputSlideAnimation o() {
      if (chatInputAnimation == null) {
         chatInputAnimation = new ChatInputSlideAnimation();
      }

      return chatInputAnimation;
   }

   public static HotbarSelectionAnimation p() {
      if (hotbarSelectionAnimation == null) {
         hotbarSelectionAnimation = new HotbarSelectionAnimation();
      }

      return hotbarSelectionAnimation;
   }

   public static PerspectiveDistanceAnimation q() {
      if (perspectiveDistanceAnimation == null) {
         perspectiveDistanceAnimation = new PerspectiveDistanceAnimation();
      }

      return perspectiveDistanceAnimation;
   }
}
