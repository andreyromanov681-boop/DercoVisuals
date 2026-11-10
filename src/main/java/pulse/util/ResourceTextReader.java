package pulse.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ResourceTextReader {
   public static int a;
   public static boolean b;

   public static String a(InputStream inputStream) {
      BufferedReader bufferedReaderB = b(inputStream);
      StringBuilder sb = new StringBuilder();

      while (true) {
         try {
            String line = bufferedReaderB.readLine();
            if (line == null) {
               return sb.toString();
            }

            sb.append(line).append("\n");
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static BufferedReader b(InputStream inputStream) {
      return new BufferedReader(new InputStreamReader(inputStream));
   }

   public static void a(OutputStream outputStream, String str) {
      try {
         a(outputStream).write(str);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static BufferedWriter a(OutputStream outputStream) {
      return new BufferedWriter(new OutputStreamWriter(outputStream));
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
