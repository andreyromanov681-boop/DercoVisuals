package pulse.gui.friends;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HistoryEntry {
   private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
   private String name;
   private LocalDate date;

   public HistoryEntry(String str) {
      this(str, LocalDate.now());
   }

   public HistoryEntry(String str, LocalDate localDate) {
      this.name = str;
      this.date = localDate;
   }

   public String name() {
      return this.name;
   }

   public void setName(String str) {
      this.name = str;
   }

   public LocalDate date() {
      return this.date;
   }

   public void setDate(LocalDate localDate) {
      this.date = localDate;
   }

   public String formattedDate() {
      return this.date.format(DISPLAY_DATE_FORMAT);
   }

   public String a() {
      return this.name;
   }

   public void a(String str) {
      this.name = str;
   }

   public LocalDate b() {
      return this.date;
   }

   public void a(LocalDate localDate) {
      this.date = localDate;
   }

   public String c() {
      return this.formattedDate();
   }
}
