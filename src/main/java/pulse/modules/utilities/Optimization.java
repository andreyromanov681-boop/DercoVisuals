package pulse.modules.utilities;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import pulse.events.ClientTickEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.BooleanSetting;
import pulse.settings.SliderSetting;

@ModuleInfo(a = "Optimization", b = "Комплексная оптимизация клиента (GPU Tape, FerriteCore, ThreadTweak, FPS Boost)", c = ModuleCategory.UTILITIES)
public final class Optimization extends ClientModule {
   public static Optimization INSTANCE;
   public final BooleanSetting minecraftSettings = new BooleanSetting("Настройки Minecraft", true);
   public final SliderSetting chunkDistance = new SliderSetting("Прогрузка чанков", 8.0F, 2.0F, 32.0F, 1.0F);
   public final BooleanSetting fastGraphics = new BooleanSetting("Быстрая графика", true);
   public final BooleanSetting dynamicFps = new BooleanSetting("Динамический FPS", true);
   public final SliderSetting bgFps = new SliderSetting("FPS в фоне", 10.0F, 5.0F, 60.0F, 5.0F);
   public final BooleanSetting hideEntities = new BooleanSetting("Скрытие сущностей", true);
   public final BooleanSetting hidePlayers = new BooleanSetting("Скрывать игроков", false);
   public final BooleanSetting hideBlockEntities = new BooleanSetting("Скрытие блок-энтити", true);
   public final SliderSetting blockEntityDistance = new SliderSetting("Дистанция блок-энтити", 64.0F, 8.0F, 128.0F, 8.0F);
   public final BooleanSetting limitParticles = new BooleanSetting("Лимит частиц", true);
   public final SliderSetting particleDistance = new SliderSetting("Дистанция частиц", 32.0F, 8.0F, 128.0F, 4.0F);
   public final SliderSetting particleCap = new SliderSetting("Потолок частиц", 1500.0F, 100.0F, 5000.0F, 100.0F);
   public final BooleanSetting noWallParticles = new BooleanSetting("Не спавнить за стенами", true);
   public final BooleanSetting limitItems = new BooleanSetting("Лимит предметов", true);
   public final SliderSetting maxItemsPerBlock = new SliderSetting("Рендер на блок", 4.0F, 1.0F, 16.0F, 1.0F);
   public final BooleanSetting noTickExcess = new BooleanSetting("Не тикать лишние", true);
   public final BooleanSetting frameDistance = new BooleanSetting("Дистанция рамок", true);
   public final SliderSetting itemFrameDistance = new SliderSetting("Рамки с предметом", 64.0F, 8.0F, 128.0F, 8.0F);
   public final SliderSetting mapFrameDistance = new SliderSetting("Рамки с картой", 32.0F, 8.0F, 128.0F, 8.0F);
   public final BooleanSetting nearSignText = new BooleanSetting("Текст табличек вблизи", true);
   public final SliderSetting signTextDistance = new SliderSetting("Дистанция текста", 16.0F, 4.0F, 64.0F, 4.0F);
   public final BooleanSetting simpleLeaves = new BooleanSetting("Упрощённая листва", true);
   public final BooleanSetting cacheSkyColor = new BooleanSetting("Кэш цвета неба", true);
   public final BooleanSetting lessLightUpdates = new BooleanSetting("Реже обновлять свет", true);
   public final BooleanSetting skipEmptyToasts = new BooleanSetting("Пропуск пустых тостов", true);
   public final BooleanSetting textBatching = new BooleanSetting("Батчинг текста", true);
   public final BooleanSetting largeFontAtlas = new BooleanSetting("Крупный атлас шрифта", true);
   public final BooleanSetting smoothChunkLoading = new BooleanSetting("Сглаживание прогрузки", true);
   public final SliderSetting chunksPerFrame = new SliderSetting("Чанков за кадр", 128.0F, 16.0F, 512.0F, 16.0F);
   public final BooleanSetting fixMemoryLeaks = new BooleanSetting("Фиксы утечек памяти", true);
   private int tickCounter = 0;
   private int currentParticleCount = 0;
   private int originalViewDistance = -1;
   private boolean originalAo = true;
   private int originalMaxFps = -1;
   private boolean wasUnfocused = false;

   public Optimization() {
      INSTANCE = this;
      this.collectSettings();
   }

   @Override
   public void onEnable() {
      INSTANCE = this;
      if (c.options != null) {
         try {
            if (this.originalViewDistance == -1) {
               this.originalViewDistance = (Integer)c.options.getViewDistance().getValue();
            }

            this.originalAo = (Boolean)c.options.getAo().getValue();
            if (this.originalMaxFps == -1) {
               this.originalMaxFps = (Integer)c.options.getMaxFps().getValue();
            }
         } catch (Throwable var2) {
         }
      }

      this.applyThreadTweak();
      this.applyMinecraftSettings();
      this.cleanupMemoryLeaks();
   }

   @Override
   public void onDisable() {
      if (c.options != null) {
         try {
            if (this.originalViewDistance > 0) {
               c.options.getViewDistance().setValue(this.originalViewDistance);
            }

            c.options.getAo().setValue(this.originalAo);
            if (this.originalMaxFps > 0) {
               c.options.getMaxFps().setValue(this.originalMaxFps);
            }
         } catch (Throwable var2) {
         }
      }

      this.originalViewDistance = -1;
      this.originalMaxFps = -1;
      this.wasUnfocused = false;
      INSTANCE = null;
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (c.player != null && c.world != null) {
         this.tickCounter++;
         if (this.tickCounter % 20 == 0) {
            this.applyMinecraftSettings();
            this.currentParticleCount = 0;
         }

         if (this.tickCounter % 1200 == 0 && this.fixMemoryLeaks.get()) {
            this.cleanupMemoryLeaks();
         }

         if (this.dynamicFps.get() && c.options != null) {
            try {
               if (!c.isWindowFocused()) {
                  if (!this.wasUnfocused) {
                     if (this.originalMaxFps == -1) {
                        this.originalMaxFps = (Integer)c.options.getMaxFps().getValue();
                     }

                     c.options.getMaxFps().setValue(this.bgFps.roundedInt());
                     this.wasUnfocused = true;
                  }
               } else if (this.wasUnfocused) {
                  if (this.originalMaxFps > 0) {
                     c.options.getMaxFps().setValue(this.originalMaxFps);
                  }

                  this.wasUnfocused = false;
               }
            } catch (Throwable var3) {
            }
         }
      }
   }

   private void applyThreadTweak() {
      try {
         Thread.currentThread().setPriority(10);
      } catch (Throwable var2) {
      }
   }

   private void applyMinecraftSettings() {
      if (this.minecraftSettings.get() && c.options != null) {
         try {
            c.options.getViewDistance().setValue(this.chunkDistance.roundedInt());
            if (this.fastGraphics.get()) {
               c.options.getAo().setValue(false);
            }
         } catch (Throwable var2) {
         }
      }
   }

   private void cleanupMemoryLeaks() {
      if (this.fixMemoryLeaks.get()) {
         System.gc();
      }
   }

   public static boolean shouldRenderEntity(Entity entity) {
      if (INSTANCE != null && INSTANCE.l()) {
         MinecraftClient client = MinecraftClient.getInstance();
         if (client.player != null && entity != client.player) {
            double distSq = client.player.squaredDistanceTo(entity);
            if (entity instanceof PlayerEntity && INSTANCE.hidePlayers.get()) {
               return false;
            }

            if (entity instanceof ItemFrameEntity && INSTANCE.frameDistance.get()) {
               double maxDist = INSTANCE.itemFrameDistance.get();
               if (distSq > maxDist * maxDist) {
                  return false;
               }
            }

            return !INSTANCE.hideEntities.get() || !(distSq > 4096.0);
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static boolean shouldRenderBlockEntity(BlockEntity blockEntity) {
      if (INSTANCE == null || !INSTANCE.l()) {
         return true;
      }

      if (!INSTANCE.hideBlockEntities.get()) {
         return true;
      }

      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player == null) {
         return true;
      }

      double distSq = client.player
         .squaredDistanceTo(blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ());
      double maxDist = INSTANCE.blockEntityDistance.get();
      return distSq <= maxDist * maxDist;
   }

   public static boolean shouldSpawnParticle(double x, double y, double z) {
      if (INSTANCE != null && INSTANCE.l()) {
         if (INSTANCE.limitParticles.get()) {
            if (INSTANCE.currentParticleCount >= INSTANCE.particleCap.roundedInt()) {
               return false;
            }

            INSTANCE.currentParticleCount++;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
               double distSq = client.player.squaredDistanceTo(x, y, z);
               double maxDist = INSTANCE.particleDistance.get();
               if (distSq > maxDist * maxDist) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static int getTargetFps(int defaultFps) {
      if (INSTANCE != null && INSTANCE.l() && INSTANCE.dynamicFps.get()) {
         MinecraftClient client = MinecraftClient.getInstance();
         return !client.isWindowFocused() ? INSTANCE.bgFps.roundedInt() : defaultFps;
      } else {
         return defaultFps;
      }
   }
}
