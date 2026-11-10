package pulse.cosmetic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.Identifier;

public final class CapeTextureCache {
   private static final CapeTextureCache INSTANCE = new CapeTextureCache();
   private final Map<Integer, Identifier> texturesByUserId = new ConcurrentHashMap<>();

   private CapeTextureCache() {
   }

   public static CapeTextureCache getInstance() {
      return INSTANCE;
   }

   public void putCapeTexture(int i, Identifier IdentifierVar) {
      if (IdentifierVar == null) {
         this.texturesByUserId.remove(i);
      } else {
         this.texturesByUserId.put(i, IdentifierVar);
      }
   }

   public Identifier getCapeTexture(int i) {
      return this.texturesByUserId.get(i);
   }

   public void clear() {
      this.texturesByUserId.clear();
   }
}
