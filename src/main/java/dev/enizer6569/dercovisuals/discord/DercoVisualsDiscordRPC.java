// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.discord;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class DercoVisualsDiscordRPC {
   private static DercoVisualsDiscordRPC INSTANCE;
   private volatile boolean running;
   private Thread thread;
   private static final long APP_ID = 1403038157479393311L;

   public static DercoVisualsDiscordRPC getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new DercoVisualsDiscordRPC();
      }

      return INSTANCE;
   }

   public void init() {
      try {
         this.thread = new Thread(this::run, "DercoVisualsDiscordRPC");
         this.thread.setDaemon(true);
         this.thread.start();
      } catch (Exception var2) {
         System.out.println("[Discord RPC] Failed to start: " + var2.getMessage());
      }
   }

   private void run() {
      RandomAccessFile var1 = null;

      try {
         for (int var2 = 0; var2 < 10; var2++) {
            try {
               var1 = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + var2, "rw");
               System.out.println("[Discord RPC] Connected to pipe " + var2);
               break;
            } catch (Exception var22) {
            }
         }

         if (var1 != null) {
            this.sendFrame(var1, 0, "{\"v\":1,\"client_id\":\"1403038157479393311\"}");
            String var25 = this.readFrame(var1);
            if (var25 != null && !var25.contains("\"code\"")) {
               String var3 = UUID.randomUUID().toString();
               long var4 = ProcessHandle.current().pid();
               String var6 = "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":"
                  + var4
                  + ",\"activity\":{\"details\":\"Playing with DercoVisuals visual\",\"state\":\"Minecraft 1.21.11\",\"assets\":{\"large_image\":\"dercovisuals_logo\",\"large_text\":\"DercoVisuals Visual\"}}},\"nonce\":\""
                  + var3
                  + "\"}";
               this.sendFrame(var1, 1, var6);
               String var7 = this.readFrame(var1);
               System.out.println("[Discord RPC] Initialized successfully!");
               this.running = true;

               while (this.running) {
                  Thread.sleep(15000L);

                  try {
                     this.sendFrame(var1, 3, "{}");
                     this.readFrame(var1);
                  } catch (Exception var21) {
                     System.out.println("[Discord RPC] Heartbeat failed, reconnecting...");
                     return;
                  }
               }

               return;
            }

            System.out.println("[Discord RPC] Handshake rejected: " + var25);
            var1.close();
            return;
         }

         System.out.println("[Discord RPC] Discord not running, skipping");
      } catch (Exception var23) {
         System.out.println("[Discord RPC] Error: " + var23.getMessage());
         return;
      } finally {
         try {
            if (var1 != null) {
               var1.close();
            }
         } catch (Exception var20) {
         }
      }
   }

   private void sendFrame(RandomAccessFile var1, int var2, String var3) throws IOException {
      byte[] var4 = var3.getBytes("UTF-8");
      ByteBuffer var5 = ByteBuffer.allocate(8 + var4.length);
      var5.order(ByteOrder.LITTLE_ENDIAN);
      var5.putInt(var2);
      var5.putInt(var4.length);
      var5.put(var4);
      var1.write(var5.array());
   }

   private String readFrame(RandomAccessFile var1) throws IOException {
      byte[] var2 = new byte[8];
      var1.readFully(var2);
      ByteBuffer var3 = ByteBuffer.wrap(var2).order(ByteOrder.LITTLE_ENDIAN);
      int var4 = var3.getInt();
      int var5 = var3.getInt();
      if (var5 > 0 && var5 <= 65536) {
         byte[] var6 = new byte[var5];
         var1.readFully(var6);
         return new String(var6, "UTF-8");
      } else {
         return null;
      }
   }
}
