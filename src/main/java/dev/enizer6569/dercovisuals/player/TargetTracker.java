// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.HitResult.class_240;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.entity.EntityUtils;
import dev.enizer6569.dercovisuals.events.AttackEntityEvent;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.hud.core.HudService;
import dev.enizer6569.dercovisuals.hud.core.HudServiceInfo;
import dev.enizer6569.dercovisuals.module.ModuleRegistry;
import dev.enizer6569.dercovisuals.util.ElapsedTimer;

@HudServiceInfo
public class TargetTracker extends HudService implements MinecraftContext {
   private LivingEntity currentTarget;
   private final Map<LivingEntity, ElapsedTimer> recentlySeenTargets = new HashMap<>();
   private final ElapsedTimer clearTimer = new ElapsedTimer();
   private boolean waitingToClearTarget;

   private long getMemoryMs() {
      return ModuleRegistry.TARGET_HUD != null && ModuleRegistry.TARGET_HUD.targetLifeTime() != null
         ? Math.round(ModuleRegistry.TARGET_HUD.targetLifeTime().get() * 1000.0F)
         : 3500L;
   }

   @EventHandler
   public void onAttack(AttackEntityEvent event) {
      if (c.player != null && c.world != null) {
         if (event.getEntity() instanceof LivingEntity) {
            LivingEntity LivingEntityVar = (LivingEntity)event.getEntity();
            if (LivingEntityVar != c.player && !EntityUtils.a(LivingEntityVar)) {
               this.currentTarget = LivingEntityVar;
               this.recentlySeenTargets.put(LivingEntityVar, new ElapsedTimer());
               this.clearTimer.reset();
               this.waitingToClearTarget = true;
            }
         }
      }
   }

   @EventHandler
   public void onClientTick(ClientTickEvent clientTickEvent) {
      if (c.player != null && c.world != null) {
         HitResult hitResult = c.crosshairTarget;
         if (hitResult instanceof EntityHitResult && hitResult.getType() == class_240.ENTITY) {
            Entity EntityVarGetEntity = ((EntityHitResult)hitResult).getEntity();
            if (EntityVarGetEntity instanceof LivingEntity LivingEntityVar && EntityVarGetEntity != c.player && !EntityUtils.a(LivingEntityVar)) {
               this.currentTarget = LivingEntityVar;
               this.recentlySeenTargets.put(LivingEntityVar, new ElapsedTimer());
               this.clearTimer.reset();
               this.waitingToClearTarget = true;
            }
         } else if (this.waitingToClearTarget && this.clearTimer.hasElapsed(this.getMemoryMs())) {
            this.currentTarget = null;
            this.waitingToClearTarget = false;
         }

         this.recentlySeenTargets.entrySet().removeIf(entry -> {
            LivingEntity LivingEntityVar2 = entry.getKey();
            if (!entry.getValue().hasElapsed(this.getMemoryMs())) {
               return false;
            } else if (LivingEntityVar2 instanceof PlayerEntity && LivingEntityVar2.isInvisible() && this.currentTarget == LivingEntityVar2) {
               this.currentTarget = null;
               this.waitingToClearTarget = false;
               return true;
            } else {
               return true;
            }
         });
      }
   }

   public boolean hasTarget() {
      return this.currentTarget != null;
   }

   public List<LivingEntity> recentTargets() {
      return new ArrayList<>(this.recentlySeenTargets.keySet());
   }

   public void clearTarget() {
      this.currentTarget = null;
      this.waitingToClearTarget = false;
      this.clearTimer.reset();
   }

   public void clearAll() {
      this.clearTarget();
      this.recentlySeenTargets.clear();
   }

   public LivingEntity currentTarget() {
      return this.currentTarget;
   }

   public void a(ClientTickEvent clientTickEvent) {
      this.onClientTick(clientTickEvent);
   }

   public boolean d() {
      return this.hasTarget();
   }

   public List<LivingEntity> e() {
      return this.recentTargets();
   }

   public void f() {
      this.clearTarget();
   }

   public void g() {
      this.clearAll();
   }

   public LivingEntity h() {
      return this.currentTarget();
   }
}
