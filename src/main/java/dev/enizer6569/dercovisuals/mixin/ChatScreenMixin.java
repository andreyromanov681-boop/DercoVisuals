// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.mixin;

import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import dev.enizer6569.dercovisuals.animation.ChatInputSlideAnimation;
import dev.enizer6569.dercovisuals.events.ChatSendEvent;
import dev.enizer6569.dercovisuals.events.EventBusService;
import dev.enizer6569.dercovisuals.gui.core.GuiInput;
import dev.enizer6569.dercovisuals.hud.core.HudElementManager;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.modules.visuals.Animations;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
   @Shadow
   protected TextFieldWidget chatField;
   @Unique
   private boolean matrixPushed = false;
   @Unique
   private int originalChatFieldY = 0;

   protected ChatScreenMixin(Text TextVar) {
      super(TextVar);
   }

   @Unique
   private boolean isChatAnimationEnabled() {
      return ModuleRegistry.ANIMATIONS != null && ModuleRegistry.ANIMATIONS.k() && ModuleRegistry.ANIMATIONS.e.a();
   }

   @Inject(require = 0, method = "sendMessage", at = @At("HEAD"), cancellable = true)
   private void onSendMessage(String str, boolean z, CallbackInfo callbackInfo) {
      ChatSendEvent chatSendEvent = new ChatSendEvent(str);
      EventBusService.EVENT_BUS.post(chatSendEvent);
      if (chatSendEvent.c()) {
         callbackInfo.cancel();
      }
   }

   @Inject(require = 0, method = "init", at = @At("TAIL"))
   private void onInit(CallbackInfo callbackInfo) {
      HudElementManager.a().a(true);
      if (this.isChatAnimationEnabled()) {
         ChatInputSlideAnimation chatInputSlideAnimationO = Animations.o();
         chatInputSlideAnimationO.d();
         chatInputSlideAnimationO.a((long)ModuleRegistry.ANIMATIONS.f.k().floatValue());
         this.originalChatFieldY = this.chatField.getY();
      }
   }

   @Inject(require = 0, method = "removed", at = @At("HEAD"))
   private void onRemoved(CallbackInfo callbackInfo) {
      HudElementManager.a().a(false);
      if (HudElementManager.a().h() != null) {
         HudElementManager.a().h().i().c();
         HudElementManager.a().h().g();
      }

      GuiInput.j();
      Animations.o().d();
   }

   @Inject(require = 0, method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void onMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (HudElementManager.a().a(click.comp_4798(), click.comp_4799(), click.button())) {
         callbackInfoReturnable.setReturnValue(true);
      }
   }

   public boolean mouseReleased(Click click) {
      return HudElementManager.a().b(click.comp_4798(), click.comp_4799(), click.button()) ? true : super.mouseReleased(click);
   }

   public boolean mouseDragged(Click click, double d3, double d4) {
      HudElementManager.a().a(click.comp_4798(), click.comp_4799(), d3, d4);
      return HudElementManager.a().b(click.comp_4798(), click.comp_4799()) ? true : super.mouseDragged(click, d3, d4);
   }

   @Inject(require = 0, method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
   private void onMouseScrolled(double d, double d2, double d3, double d4, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (HudElementManager.a().a(d, d2, d4)) {
         callbackInfoReturnable.setReturnValue(true);
      } else if (this.isChatAnimationEnabled() && Animations.o().c()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(require = 0, method = "keyPressed", at = @At("HEAD"), cancellable = true)
   private void onKeyPressed(KeyInput keyInput, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (HudElementManager.a().a(keyInput.comp_4795(), keyInput.comp_4797())) {
         callbackInfoReturnable.setReturnValue(true);
      } else if (this.isChatAnimationEnabled() && Animations.o().c() && keyInput.comp_4795() != 256) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(require = 0, method = "render", at = @At("HEAD"))
   private void onRenderHead(DrawContext DrawContextVar, int i, int i2, float f, CallbackInfo callbackInfo) {
      GuiInput.h();
      if (!this.isChatAnimationEnabled()) {
         this.matrixPushed = false;
      } else {
         ChatInputSlideAnimation chatInputSlideAnimationO = Animations.o();
         chatInputSlideAnimationO.a();
         float fB = chatInputSlideAnimationO.b();
         if (fB <= 0.0F) {
            this.matrixPushed = false;
         } else {
            this.chatField.setY(this.originalChatFieldY + (int)fB);
            DrawContextVar.getMatrices().pushMatrix();
            DrawContextVar.getMatrices().translate(0.0F, fB);
            this.matrixPushed = true;
         }
      }
   }

   @Inject(require = 0, method = "render", at = @At("TAIL"))
   private void onRender(DrawContext DrawContextVar, int i, int i2, float f, CallbackInfo callbackInfo) {
      if (this.matrixPushed) {
         DrawContextVar.getMatrices().popMatrix();
         this.matrixPushed = false;
      }

      MinecraftClient MinecraftClientVarGetInstance = MinecraftClient.getInstance();
      if (MinecraftClientVarGetInstance.isWindowFocused()) {
         HudElementManager.a().a(i, i2);
         if (HudElementManager.a().h() != null) {
            HudElementManager.a().h().a(i, i2);
         }
      } else {
         HudElementManager.a().a(i, i2);
         if (HudElementManager.a().h() != null) {
            HudElementManager.a().h().g();
         }
      }

      GuiInput.i();
   }
}
