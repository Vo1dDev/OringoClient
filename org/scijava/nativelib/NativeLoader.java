package org.scijava.nativelib;

import java.io.IOException;

public class NativeLoader {
   private static JniExtractor jniExtractor = null;

   public static void loadLibrary(String libname) throws IOException {
      System.load(jniExtractor.extractJni("", libname).getAbsolutePath());
   }

   public static void extractRegistered() throws IOException {
      jniExtractor.extractRegistered();
   }

   public static JniExtractor getJniExtractor() {
      return jniExtractor;
   }

   public static void setJniExtractor(JniExtractor jniExtractor) {
      NativeLoader.jniExtractor = jniExtractor;
   }

   static {
      try {
         if (NativeLoader.class.getClassLoader() == ClassLoader.getSystemClassLoader()) {
            jniExtractor = new DefaultJniExtractor();
         } else {
            jniExtractor = new WebappJniExtractor("Classloader");
         }

      } catch (IOException var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
