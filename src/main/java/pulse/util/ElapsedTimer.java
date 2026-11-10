package pulse.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ElapsedTimer {
   private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
   private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private long lastResetMs = System.currentTimeMillis();

   public boolean hasElapsed(long j) {
      return this.elapsedMs() >= j;
   }

   public long elapsedMs() {
      return System.currentTimeMillis() - this.lastResetMs;
   }

   public void reset() {
      this.lastResetMs = System.currentTimeMillis();
   }

   public void setLastResetMs(long j) {
      this.lastResetMs = j;
   }

   public static long currentEpochMs() {
      return Instant.now().atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
   }

   public static ZonedDateTime currentDateTime() {
      return ZonedDateTime.now(DEFAULT_ZONE);
   }

   public static String formatEpochMs(long j) {
      return Instant.ofEpochMilli(j).atZone(DEFAULT_ZONE).format(DEFAULT_FORMAT);
   }

   public static String formatNow() {
      return currentDateTime().format(DEFAULT_FORMAT);
   }

   public static boolean isDifferenceGreaterThan(long j, long j2, long j3) {
      return Math.abs(j - j2) > j3;
   }

   public static long differenceMs(long j, long j2) {
      return Math.abs(j - j2);
   }

   public static String formatNowWithZone() {
      ZonedDateTime zonedDateTimeCurrentDateTime = currentDateTime();
      return String.format("%s %s", zonedDateTimeCurrentDateTime.format(DEFAULT_FORMAT), zonedDateTimeCurrentDateTime.getOffset());
   }

   public boolean a(long j) {
      return this.hasElapsed(j);
   }

   public long a() {
      return this.elapsedMs();
   }

   public void b() {
      this.reset();
   }

   public static long c() {
      return currentEpochMs();
   }

   public static ZonedDateTime d() {
      return currentDateTime();
   }

   public static String b(long j) {
      return formatEpochMs(j);
   }

   public static String e() {
      return formatNow();
   }

   public static boolean a(long j, long j2, long j3) {
      return isDifferenceGreaterThan(j, j2, j3);
   }

   public static long a(long j, long j2) {
      return differenceMs(j, j2);
   }

   public static String f() {
      return formatNowWithZone();
   }

   public void c(long j) {
      this.setLastResetMs(j);
   }
}
