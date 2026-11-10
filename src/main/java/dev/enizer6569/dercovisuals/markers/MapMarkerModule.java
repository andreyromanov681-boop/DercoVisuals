// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.markers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import dev.enizer6569.dercovisuals.client.MinecraftContext;
import dev.enizer6569.dercovisuals.events.ClientTickEvent;
import dev.enizer6569.dercovisuals.events.PacketEvent;
import dev.enizer6569.dercovisuals.events.WorldChangeEvent;
import dev.enizer6569.dercovisuals.events.WorldClearEvent;
import dev.enizer6569.dercovisuals.hud.core.HudService;
import dev.enizer6569.dercovisuals.hud.core.HudServiceInfo;
import dev.enizer6569.dercovisuals.hud.notifications.HudNotificationCenter;
import dev.enizer6569.dercovisuals.markers.providers.ActiveEventMarkerProvider;
import dev.enizer6569.dercovisuals.markers.providers.EventDelayMarkerProvider;
import dev.enizer6569.dercovisuals.markers.providers.EventsCommandMarkerProvider;
import dev.enizer6569.dercovisuals.mixin.accessor.PlayerListHudAccessor;

@HudServiceInfo(enabledByDefault = true)
public class MapMarkerModule extends HudService implements MinecraftContext {
   private static final long DEFAULT_MARKER_LIFETIME_MS = 600000L;
   private static final int JOIN_REFRESH_DELAY_TICKS = 20;
   private static final Pattern SERVER_NUMBER_PATTERN = Pattern.compile("(?:анархия|сервер|гриф)\\D*(\\d+)", 2);
   private static final Pattern HEADER_SERVER_PATTERN = Pattern.compile("(?:анархия|сервер|гриф)\\D*(\\d+)(?:\\D+([a-z0-9_.:-]+))?", 2);
   private MarkerProvider activeProvider;
   private boolean needsProviderRefresh;
   private boolean waitingForCommandResponse;
   private String pendingMarkerName;
   public static int a;
   public static boolean b;
   private final List<MarkerProvider> providers = new ArrayList<>();
   private int pendingServerId = -1;
   private long commandRequestedAt;

   public MapMarkerModule() {
      this.providers.add(new EventDelayMarkerProvider());
      this.providers.add(new EventsCommandMarkerProvider());
      this.providers.add(new ActiveEventMarkerProvider());
   }

   @EventHandler
   private void onGameMessage(PacketEvent packetEvent) {
      if (MarkerSettings.d() && c.player != null) {
         if (packetEvent.d() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket GameMessageS2CPacketVarD = (GameMessageS2CPacket)packetEvent.d();
            String string = GameMessageS2CPacketVarD.comp_763().getString();
            String lowerCase = Formatting.strip(string).toLowerCase(Locale.ROOT);
            this.updateActiveProvider();
            boolean z = this.waitingForCommandResponse;
            if (this.activeProvider != null && this.activeProvider.handleMessage(lowerCase, string, this)) {
               if (z) {
                  packetEvent.a();
               }
            } else {
               for (MarkerProvider markerProvider : this.providers) {
                  if (markerProvider != this.activeProvider
                     && this.containsEventName(markerProvider, lowerCase)
                     && markerProvider.handleMessage(lowerCase, string, this)) {
                     this.activeProvider = markerProvider;
                     if (z) {
                        packetEvent.a();
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onClientTick(ClientTickEvent clientTickEvent) {
      MarkerManager.e();
      if (MarkerSettings.d() && c.player != null) {
         if (this.waitingForCommandResponse && System.currentTimeMillis() - this.commandRequestedAt > 10000L) {
            this.completeProviderResponse();
         }

         this.updateActiveProvider();
         if (this.activeProvider != null) {
            this.refreshProviderAfterJoin();
            this.activeProvider.tick(this);
         }
      }
   }

   @EventHandler
   private void onWorldCleared(WorldClearEvent worldClearEvent) {
      MarkerManager.removeServerBound();
      this.activeProvider = null;
      this.needsProviderRefresh = MarkerSettings.d();
      this.completeProviderResponse();
   }

   private boolean containsEventName(MarkerProvider markerProvider, String message) {
      for (String eventName : markerProvider.eventNames()) {
         if (message.contains(eventName)) {
            return true;
         }
      }

      return false;
   }

   @EventHandler
   private void onWorldChanged(WorldChangeEvent worldChangeEvent) {
      if (MarkerSettings.d()) {
         this.needsProviderRefresh = true;
      }
   }

   private void updateActiveProvider() {
      String strCurrentServerAddress = this.currentServerAddress();
      if (strCurrentServerAddress == null) {
         this.activeProvider = null;
      } else {
         if (this.activeProvider == null) {
            for (MarkerProvider markerProvider : this.providers) {
               if (markerProvider.supportsServer(strCurrentServerAddress)) {
                  this.activeProvider = markerProvider;
                  return;
               }
            }

            this.activeProvider = null;
         }
      }
   }

   private void refreshProviderAfterJoin() {
      if (this.needsProviderRefresh && c.player.age > 20 && this.activeProvider != null) {
         this.requestProviderCommand();
         this.needsProviderRefresh = false;
      }
   }

   public void d() {
      this.clearMarkers();
   }

   public void clearMarkers() {
      MarkerManager.c();
      this.completeProviderResponse();
   }

   public void a(String str, String str2) {
      this.sendCommand(str, str2);
   }

   public void sendCommand(String str, String str2) {
      if (str != null && this.waitingForCommandResponse) {
         HudNotificationCenter.a("%s %s", str, str2 != null ? str2 : "");
         this.waitingForCommandResponse = false;
      }
   }

   public void a(String str, int i) {
      this.sendTimedLookup(str, i);
   }

   public void sendTimedLookup(String str, int i) {
      if (this.waitingForCommandResponse) {
         HudNotificationCenter.a("%s %s", this.formatSeconds(i), this.capitalizeEventName(str));
         this.waitingForCommandResponse = false;
      }
   }

   public void a(int i) {
      this.sendDelayLookup(i);
   }

   public void sendDelayLookup(int i) {
      if (this.waitingForCommandResponse && this.activeProvider != null) {
         HudNotificationCenter.a("%s %s", this.activeProvider.command(), this.formatSeconds(i));
         this.waitingForCommandResponse = false;
      }
   }

   public void e() {
      this.requestProviderCommand();
   }

   public void requestProviderCommand() {
      if (this.activeProvider != null && c.player != null && c.player.networkHandler != null) {
         String command = this.activeProvider.command();
         if (command != null && !command.isBlank()) {
            this.waitingForCommandResponse = true;
            this.commandRequestedAt = System.currentTimeMillis();
            c.player.networkHandler.sendChatCommand(command.startsWith("/") ? command.substring(1) : command);
         }
      }
   }

   private String formatSeconds(int i) {
      return i + " сек.";
   }

   public boolean f() {
      return this.waitingForCommandResponse;
   }

   public void g() {
      this.waitingForCommandResponse = true;
   }

   public void a(String str, int[] iArr, int i) {
      this.addMarker(str, iArr, i);
   }

   public void addMarker(String str, int[] iArr, int i) {
      if (str != null && iArr != null && iArr.length >= 3) {
         int iH = this.h();
         String strI = this.i();
         MapMarker mapMarkerA = this.findServerMarker(iArr[0], iArr[1], iArr[2], iH, strI);
         if (mapMarkerA == null) {
            MapMarkerStyle mapMarkerStyleA = this.a(str.toLowerCase(Locale.ROOT));
            MarkerManager.a(
               MapMarker.a(
                  this.capitalizeEventName(str), iArr[0], iArr[1], iArr[2], mapMarkerStyleA.a, mapMarkerStyleA.b, iH, strI, i > 0 ? i * 1000L : 600000L, i > 0
               )
            );
         } else {
            mapMarkerA.a(this.capitalizeEventName(str));
            if (i > 0) {
               mapMarkerA.a(System.currentTimeMillis() + i * 1000L);
               mapMarkerA.a(true);
            }
         }
      }
   }

   private MapMarker findServerMarker(int x, int y, int z, int serverId, String serverAddress) {
      for (MapMarker marker : MarkerManager.a()) {
         if (marker.j() && marker.b() == x && marker.c() == y && marker.d() == z && marker.l() == serverId && Objects.equals(marker.m(), serverAddress)) {
            return marker;
         }
      }

      return null;
   }

   public void b(String str, int i) {
      this.setPendingMarker(str, i);
   }

   public void setPendingMarker(String str, int i) {
      this.pendingMarkerName = str;
      this.pendingServerId = i;
   }

   public void a(int[] iArr) {
      this.addPendingMarker(iArr);
   }

   public void addPendingMarker(int[] iArr) {
      if (this.pendingMarkerName != null && iArr != null) {
         if (this.pendingServerId >= 0 && this.h() >= 0 && this.pendingServerId != this.h()) {
            this.completeProviderResponse();
         } else {
            this.addMarker(this.pendingMarkerName, iArr, -1);
         }
      }
   }

   public void completeProviderResponse() {
      this.waitingForCommandResponse = false;
      this.pendingMarkerName = null;
      this.pendingServerId = -1;
      this.commandRequestedAt = 0L;
   }

   public void b(int i) {
      this.updatePendingMarkerTimer(i);
   }

   public void updatePendingMarkerTimer(int i) {
      if (this.pendingMarkerName != null) {
         String strCapitalizeEventName = this.capitalizeEventName(this.pendingMarkerName);

         for (MapMarker mapMarker : MarkerManager.f()) {
            if (mapMarker.a().equalsIgnoreCase(strCapitalizeEventName)) {
               mapMarker.a(System.currentTimeMillis() + i * 1000L);
               mapMarker.a(true);
               return;
            }
         }
      }
   }

   public int h() {
      Text TextVarPlayerListHeader = this.playerListHeader();
      if (TextVarPlayerListHeader == null) {
         return -1;
      }

      Matcher matcher = SERVER_NUMBER_PATTERN.matcher(Formatting.strip(TextVarPlayerListHeader.getString()));
      return matcher.find() ? parseInt(matcher.group(1), -1) : -1;
   }

   public String i() {
      Text TextVarPlayerListHeader = this.playerListHeader();
      if (TextVarPlayerListHeader == null) {
         return null;
      }

      Matcher matcher = HEADER_SERVER_PATTERN.matcher(Formatting.strip(TextVarPlayerListHeader.getString()));
      return matcher.find() ? matcher.group(2) : null;
   }

   public boolean j() {
      return this.isConnectedToMarkedServer();
   }

   public boolean isConnectedToMarkedServer() {
      if (c.player == null) {
         return false;
      }

      String string = c.player.getDisplayName().getString();
      return !string.equals(Formatting.strip(string));
   }

   public boolean k() {
      return this.canRenderServerBoundMarkers();
   }

   public boolean canRenderServerBoundMarkers() {
      return c.world != null && "minecraft:overworld".equals(c.world.getRegistryKey().getValue().toString()) && !c.isInSingleplayer();
   }

   public MapMarkerStyle a(String str) {
      return this.findStyle(str);
   }

   public MapMarkerStyle findStyle(String str) {
      if (str == null) {
         return this.defaultStyle();
      }

      String lowerCase = str.toLowerCase(Locale.ROOT);
      if (this.activeProvider != null) {
         Map<String, MapMarkerStyle> mapStyles = this.activeProvider.styles();
         if (mapStyles.containsKey(lowerCase)) {
            return mapStyles.get(lowerCase);
         }
      }

      Iterator<MarkerProvider> it = this.providers.iterator();

      while (it.hasNext()) {
         Map<String, MapMarkerStyle> mapStyles2 = it.next().styles();
         if (mapStyles2.containsKey(lowerCase)) {
            return mapStyles2.get(lowerCase);
         }
      }

      return this.defaultStyle();
   }

   private MapMarkerStyle defaultStyle() {
      return new MapMarkerStyle(new Color(255, 165, 0));
   }

   private String capitalizeEventName(String str) {
      return str != null && !str.isEmpty() ? str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1) : str;
   }

   private Text playerListHeader() {
      return c.inGameHud != null && c.inGameHud.getPlayerListHud() != null ? ((PlayerListHudAccessor)c.inGameHud.getPlayerListHud()).getHeader() : null;
   }

   private String currentServerAddress() {
      return c.getCurrentServerEntry() != null ? c.getCurrentServerEntry().address : null;
   }

   private static int parseInt(String str, int i) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException e) {
         return i;
      }
   }

   @Generated
   public List<MarkerProvider> l() {
      return this.providers;
   }

   @Generated
   public MarkerProvider m() {
      return this.activeProvider;
   }

   @Generated
   public boolean n() {
      return this.needsProviderRefresh;
   }

   @Generated
   public String o() {
      return this.pendingMarkerName;
   }

   @Generated
   public int p() {
      return this.pendingServerId;
   }
}
