package pulse.player;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.client.network.PlayerListEntry;
import pulse.auth.AccountServiceListener;

public class PlayerListTracker implements ThreadFactory {
   private final Set<String> names = ConcurrentHashMap.newKeySet();
   private AccountServiceListener listener;

   @Override
   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, "pulse-player-list");
      thread.setDaemon(true);
      return thread;
   }

   public void a(AccountServiceListener accountServiceListener) {
      this.listener = accountServiceListener;
   }

   public void a(PlayerListS2CPacket PlayerListS2CPacketVar) {
   }

   public void a() {
   }

   public void a(Collection<PlayerListEntry> collection) {
   }

   public void b(Collection<String> collection) {
      this.names.clear();
      if (collection != null) {
         this.names.addAll(collection);
      }
   }

   public void b() {
   }

   public void a(int i, long j) {
      if (this.listener != null) {
         this.listener.a(i, j, Collections.emptyList());
      }
   }

   public void a(long j, boolean z) {
   }

   public void a(boolean z, boolean z2) {
   }

   public void c() {
   }

   public int d() {
      return 0;
   }

   public long e() {
      return 0L;
   }

   public Set<String> f() {
      return Collections.unmodifiableSet(this.names);
   }

   public void g() {
   }
}
