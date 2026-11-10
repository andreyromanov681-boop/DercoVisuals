// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.OrderedText;
import net.minecraft.ChatHudLine.class_7590;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.enizer6569.dercovisuals.animation.ChatMessageSlideAnimation;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private List<class_7590> visibleMessages;
   @Shadow
   private int scrolledLines;
   @Unique
   private Map<class_7590, ChatMessageSlideAnimation> messageAnimators;
   @Unique
   private int currentRenderIndex;

   @Shadow
   public abstract int getWidth();

   @Unique
   private Map<class_7590, ChatMessageSlideAnimation> getAnimators() {
      if (this.messageAnimators == null) {
         this.messageAnimators = new IdentityHashMap<>();
      }

      return this.messageAnimators;
   }

   @Unique
   private boolean isChatAnimationEnabled() {
      return ModuleRegistry.ANIMATIONS != null && ModuleRegistry.ANIMATIONS.k() && ModuleRegistry.ANIMATIONS.e.a();
   }

   @Inject(require = 0, method = "render", at = @At("HEAD"))
   private void onRenderHead(
      DrawContext DrawContextVar, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean focused, boolean chatOpen, CallbackInfo callbackInfo
   ) {
      if (this.isChatAnimationEnabled()) {
         Map<class_7590, ChatMessageSlideAnimation> animators = this.getAnimators();
         animators.values().forEach(v0 -> v0.a());
         animators.entrySet().removeIf(entry -> entry.getValue().d());
         this.currentRenderIndex = 0;
      }
   }

   @Redirect(
      require = 0,
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/DrawContext;drawTextWithShadow(Lnet/minecraft/TextRenderer;Lnet/minecraft/OrderedText;III)V")
   )
   private void redirectDrawTextWithShadow(DrawContext DrawContextVar, TextRenderer TextRendererVar, OrderedText OrderedTextVar, int i, int i2, int i3) {
      float fB = 0.0F;
      if (this.isChatAnimationEnabled()) {
         int i4 = this.currentRenderIndex + this.scrolledLines;
         if (i4 >= 0 && i4 < this.visibleMessages.size()) {
            ChatMessageSlideAnimation chatMessageSlideAnimation = this.getAnimators().get(this.visibleMessages.get(i4));
            if (chatMessageSlideAnimation != null) {
               fB = chatMessageSlideAnimation.b();
            }
         }

         this.currentRenderIndex++;
      }

      DrawContextVar.drawTextWithShadow(TextRendererVar, OrderedTextVar, i + (int)fB, i2, i3);
   }

   @Inject(require = 0, method = "addMessage", at = @At("TAIL"))
   private void onAddMessage(CallbackInfo callbackInfo) {
      if (this.isChatAnimationEnabled() && !this.visibleMessages.isEmpty()) {
         int iGetWidth = this.getWidth();
         long jFloatValue = (long)ModuleRegistry.ANIMATIONS.f.k().floatValue();
         class_7590 class_7590Var = this.visibleMessages.get(0);
         Map<class_7590, ChatMessageSlideAnimation> animators = this.getAnimators();
         if (!animators.containsKey(class_7590Var)) {
            animators.put(class_7590Var, new ChatMessageSlideAnimation(-iGetWidth, 0.0F, jFloatValue));
         }
      }
   }

   @Inject(require = 0, method = "clear", at = @At("HEAD"))
   private void onClear(boolean z, CallbackInfo callbackInfo) {
      if (this.messageAnimators != null) {
         this.getAnimators().clear();
      }
   }
}
