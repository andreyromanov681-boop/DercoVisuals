package ru.pulse.mixin;

import java.awt.Color;
import net.minecraft.client.gui.Click;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pulse.gui.menu.PulseMainMenuScreen;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.font.FontManager;
import pulse.render.font.FontRenderer;
import ru.pulse.Pulse;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
   @Inject(require = 0, method = "init", at = @At("HEAD"), cancellable = true)
   private void onInitHead(CallbackInfo callbackInfo) {
      if (!PulseMainMenuScreen.useVanillaMenu) {
         if (!(this instanceof PulseMainMenuScreen)) {
            MinecraftClient.getInstance().setScreen(new PulseMainMenuScreen());
            callbackInfo.cancel();
         }
      }
   }

   @Inject(require = 0, method = "init", at = @At("TAIL"))
   private void onInit(CallbackInfo callbackInfo) {
      Pulse.checkAndShowUpdate((TitleScreen)this);
   }

   @Inject(require = 0, method = "render", at = @At("TAIL"))
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (PulseMainMenuScreen.useVanillaMenu) {
         if (!(this instanceof PulseMainMenuScreen)) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null) {
               if (Pulse.getInstance().getRender() instanceof Renderer2DImpl) {
                  ((Renderer2DImpl)Pulse.getInstance().getRender()).setDrawContext(context);
                  Renderer2D r = Pulse.getInstance().getRender();
                  Matrix3x2fStack m = context.getMatrices();
                  FontRenderer f = FontManager.b[12];
                  String label = "PulseVisuals меню";
                  float lw = f.a(label);
                  float bw = lw + 20.0F;
                  float bh = 18.0F;
                  int sw = mc.getWindow().getScaledWidth();
                  float bx = sw - bw - 6.0F;
                  float by = 6.0F;
                  boolean hov = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh;
                  r.a(bx, by, bw, bh, 4.0F, hov ? new Color(60, 50, 100, 230) : new Color(28, 28, 38, 200), m);
                  f.a(label, bx + 10.0F, by + 4.0F, hov ? Color.WHITE : new Color(190, 185, 215), m);
               }
            }
         }
      }
   }

   @Inject(require = 0, method = "mouseClicked", at = @At("HEAD"), cancellable = true)
   private void onMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
      if (PulseMainMenuScreen.useVanillaMenu) {
         if (!(this instanceof PulseMainMenuScreen)) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null) {
               int mx = (int)click.comp_4798();
               int my = (int)click.comp_4799();
               FontRenderer f = FontManager.b[12];
               String label = "PulseVisuals меню";
               float lw = f.a(label);
               float bw = lw + 20.0F;
               float bh = 18.0F;
               int sw = mc.getWindow().getScaledWidth();
               float bx = sw - bw - 6.0F;
               float by = 6.0F;
               if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
                  PulseMainMenuScreen.useVanillaMenu = false;
                  mc.setScreen(new PulseMainMenuScreen());
                  cir.setReturnValue(true);
               }
            }
         }
      }
   }
}
