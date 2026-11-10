// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.auth;

import java.util.List;
import java.util.Set;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONArray;
import org.json.JSONObject;
import dev.enizer6569.dercovisuals.config.CloudConfigRepository;
import dev.enizer6569.dercovisuals.player.PlayerListTracker;

public class AccountServiceClient extends WebSocketListener implements Runnable, AccountServiceListener {
   private static volatile AccountServiceClient instance;
   public final PlayerListTracker a = new PlayerListTracker();
   public final CloudConfigRepository b = new CloudConfigRepository(this);

   private AccountServiceClient() {
   }

   public static AccountServiceClient getInstance() {
      instance = new AccountServiceClient();
      return instance;
   }

   public static AccountServiceClient A() {
      return getInstance();
   }

   public boolean a() {
      return false;
   }

   public boolean b() {
      return false;
   }

   public boolean c() {
      return false;
   }

   public boolean d() {
      return false;
   }

   public boolean e() {
      return false;
   }

   public boolean f() {
      return false;
   }

   public PlayerListTracker g() {
      return null;
   }

   public JSONArray h() {
      return null;
   }

   public JSONArray i() {
      return null;
   }

   public JSONArray j() {
      return null;
   }

   public long k() {
      return 0L;
   }

   public long l() {
      return 0L;
   }

   public String m() {
      return null;
   }

   public String n() {
      return null;
   }

   public String o() {
      return null;
   }

   public String p() {
      return null;
   }

   public byte[] q() {
      return null;
   }

   public long r() {
      return 0L;
   }

   public String s() {
      return null;
   }

   public String t() {
      return null;
   }

   public boolean a(String str, String str2) {
      return false;
   }

   @Override
   public void run() {
   }

   public void b(String str, String str2) {
   }

   public void u() {
   }

   public void v() {
   }

   public void a(String str) {
   }

   public void b(String str) {
   }

   public void w() {
   }

   public void x() {
   }

   public boolean c(String str) {
      return false;
   }

   public Integer c(String str, String str2) {
      return null;
   }

   public void y() {
   }

   public void d(String str) {
   }

   public void z() {
   }

   public JSONObject a(int i, int i2, JSONObject jSONObject) {
      return null;
   }

   @Override
   public void a(int i, Set<String> set) {
   }

   @Override
   public void a(int i, long j, List<AccountServiceRecord> list) {
   }

   public void onOpen(WebSocket webSocket, Response response) {
   }

   public void onMessage(WebSocket webSocket, String str) {
   }

   public void onClosed(WebSocket webSocket, int i, String str) {
   }

   public void onFailure(WebSocket webSocket, Throwable th, Response response) {
   }
}
