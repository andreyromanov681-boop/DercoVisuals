package pulse.modules.visuals;

import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.option.Perspective;
import net.minecraft.HitResult.class_240;
import pulse.events.HudRenderPostEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.module.ModuleRegistry;
import pulse.render.Renderer2DImpl;
import pulse.settings.BooleanSetting;
import pulse.settings.ColorSetting;
import pulse.settings.SettingGroup;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Crosshair", b = "Настраивает отображение прицела", c = ModuleCategory.VISUALS)
public class Crosshair extends ClientModule {
   private final SliderSetting e = new SliderSetting("Длина", 3.0F, 1.0F, 15.0F, 1.0F);
   private final SliderSetting f = new SliderSetting("Толщина", 1.0F, 1.0F, 10.0F, 1.0F);
   private final SliderSetting g = new SliderSetting("Отступ", 0.0F, 0.0F, 10.0F, 1.0F);
   private final SettingGroup h = new SettingGroup("Поведение");
   private final BooleanSetting i = new BooleanSetting("Обводка", true);
   private final BooleanSetting j = new BooleanSetting("Точка", false);
   private final BooleanSetting k = new BooleanSetting("Менять цвет при наводке", true);
   private final BooleanSetting l = new BooleanSetting("Динамический", false);
   private final SettingGroup m = new SettingGroup("Цвет");
   private final BooleanSetting n = new BooleanSetting("Цвет клиента", false);
   private final ColorSetting o = new ColorSetting("Кастомный цвет", Color.WHITE).a(() -> !this.n.a());
   public static int a;
   public static boolean b;

   @EventHandler(priority = -200)
   private void a(HudRenderPostEvent hudRenderPostEvent) {
      if (c.options.getPerspective() == Perspective.FIRST_PERSON && c.player != null) {
         DrawContext ctx = Renderer2DImpl.currentDrawContext;
         if (ctx != null) {
            int cx = c.getWindow().getScaledWidth() / 2;
            int cy = c.getWindow().getScaledHeight() / 2;
            float cooldown = 1.0F - c.player.getAttackCooldownProgress(hudRenderPostEvent.d());
            float gap = !this.l.a() ? this.g.a() : this.g.a() + 8.0F * cooldown;
            int thick = Math.max(1, (int)this.f.a());
            int len = Math.max(1, (int)this.e.a());
            Color mainColor = this.o();
            if (this.k.a() && this.isLookingAtEntity()) {
               mainColor = new Color(255, 64, 64);
            }

            if (this.i.a()) {
               this.drawBar(ctx, cx, cy, gap, thick, len, new Color(0, 0, 0, 200), 1);
            }

            this.drawBar(ctx, cx, cy, gap, thick, len, mainColor, 0);
            if (this.j.a()) {
               int dotR = 1;
               if (this.i.a()) {
                  this.fill(ctx, cx - dotR - 1, cy - dotR - 1, dotR * 2 + 2, dotR * 2 + 2, new Color(0, 0, 0, 200));
               }

               this.fill(ctx, cx - dotR, cy - dotR, dotR * 2, dotR * 2, mainColor);
            }
         }
      }
   }

   private void drawBar(DrawContext ctx, int cx, int cy, float gap, int thick, int len, Color color, int expand) {
      int g2 = (int)gap;
      int half = thick / 2;
      this.fill(ctx, cx + g2 - expand, cy - half - expand, len + expand * 2, thick + expand * 2, color);
      this.fill(ctx, cx - g2 - len - expand, cy - half - expand, len + expand * 2, thick + expand * 2, color);
      this.fill(ctx, cx - half - expand, cy + g2 - expand, thick + expand * 2, len + expand * 2, color);
      this.fill(ctx, cx - half - expand, cy - g2 - len - expand, thick + expand * 2, len + expand * 2, color);
   }

   private void fill(DrawContext ctx, int x, int y, int w, int h, Color color) {
      ctx.fill(x, y, x + w, y + h, color.getRGB());
   }

   private boolean isLookingAtEntity() {
      HitResult hit = c.crosshairTarget;
      return hit != null && hit.getType() == class_240.ENTITY && hit instanceof EntityHitResult
         ? ((EntityHitResult)hit).getEntity() instanceof LivingEntity
         : false;
   }

   private Color o() {
      return this.n.a() ? ModuleRegistry.CLIENT_COLOR.n() : this.o.a();
   }

   public static String c(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
