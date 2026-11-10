package ru.pulse.protection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public final class Protection {
   private static boolean initialized;

   private Protection() {
   }

   public static synchronized void initialize() {
      initialized = true;
   }

   public static synchronized void GamFjznbbEDK() {
      initialize();
   }

   public static boolean isInitialized() {
      return initialized;
   }

   public static Field findField(Class<?> cls, String str, String str2, boolean z) {
      try {
         Field declaredField = cls.getDeclaredField(str);
         declaredField.setAccessible(z);
         return declaredField;
      } catch (ReflectiveOperationException e) {
         return null;
      }
   }

   public static Field HQcKVYMOvSiwNY(Class<?> cls, String str, String str2, boolean z) {
      return findField(cls, str, str2, z);
   }

   public static Method findMethod(Class<?> cls, String str, String str2, boolean z) {
      for (Method method : cls.getDeclaredMethods()) {
         if (method.getName().equals(str)) {
            method.setAccessible(z);
            return method;
         }
      }

      return null;
   }

   public static Method wjNaofXtsjno(Class<?> cls, String str, String str2, boolean z) {
      return findMethod(cls, str, str2, z);
   }

   public static Class<?> GHFapfmWTlg(String str) {
      try {
         return Class.forName(str);
      } catch (ClassNotFoundException e) {
         return null;
      }
   }

   public static void VXCpRayqchcmz(ByteBuffer byteBuffer) {
      if (byteBuffer != null) {
      }
   }

   public static String xtksnqlsla(byte[] bArr, int i) {
      return "";
   }
}
