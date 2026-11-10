package ru.pulse.mixin;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.util.Util;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.resource.ResourceReload;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pulse.render.Renderer2D;
import pulse.render.Renderer2DImpl;
import pulse.render.icons.IconTextureRegistry;
import ru.pulse.Pulse;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private ResourceReload reload;
   @Shadow
   @Final
   private Consumer<Optional<Throwable>> exceptionHandler;
   @Shadow
   private long reloadCompleteTime;
   @Shadow
   @Final
   private boolean reloading;

   @Inject(method = "render", at = @At("HEAD"), cancellable = true)
   private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      int w = context.getScaledWindowWidth();
      int h = context.getScaledWindowHeight();
      context.fill(0, 0, w, h, -10600220);
      if (Pulse.getInstance().getRender() instanceof Renderer2DImpl impl) {
         impl.setDrawContext(context);
      }

      Renderer2D r = Pulse.getInstance().getRender();
      Matrix3x2fStack m = context.getMatrices();
      float logoW = 145.0F;
      float logoH = 18.5F;
      float logoX = (w - logoW) / 2.0F;
      float logoY = (h - logoH) / 2.0F - 10.0F;
      Identifier splashTex = IconTextureRegistry.get("splash_logo");
      if (splashTex != null) {
         Renderer2DImpl.setTextureFilter(splashTex, true);
         r.a(splashTex, logoX, logoY, logoW, logoH, Color.WHITE, m);
      }

      float spinX = w / 2.0F;
      float spinY = h / 2.0F + 50.0F;
      float radius = 4.8F;
      float thickness = 1.0F;
      long time = Util.getMeasuringTimeMs();
      double rotAngle = time % 1000L / 1000.0 * 2.0 * Math.PI;
      int segments = 120;

      for (int i = 0; i < segments; i++) {
         double a = rotAngle + i * ((Math.PI * 2) / segments);
         float px = (float)(spinX + radius * Math.cos(a));
         float py = (float)(spinY + radius * Math.sin(a));
         float progress = (float)i / segments;
         int alpha = (int)(15.0F + progress * 240.0F);
         r.a(px - thickness / 2.0F, py - thickness / 2.0F, thickness, thickness, thickness / 2.0F, new Color(255, 255, 255, alpha), m);
      }

      long currentTime = Util.getMeasuringTimeMs();
      if (this.reloadCompleteTime == -1L && this.reload.isComplete()) {
         try {
            this.reload.throwException();
            this.exceptionHandler.accept(Optional.empty());
         } catch (Throwable t) {
            this.exceptionHandler.accept(Optional.of(t));
         }

         this.reloadCompleteTime = currentTime;
      }

      if (this.reloadCompleteTime > 0L) {
         float fadeProgress = (float)(currentTime - this.reloadCompleteTime) / 500.0F;
         if (fadeProgress >= 1.0F) {
            this.client.setOverlay(null);
         }
      }

      ci.cancel();
   }
}
