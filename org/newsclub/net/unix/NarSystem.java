package org.newsclub.net.unix;

import java.io.File;
import java.net.URL;
import org.scijava.nativelib.DefaultJniExtractor;
import org.scijava.nativelib.JniExtractor;

public final class NarSystem {
   private NarSystem() {
   }

   public static void loadLibrary() {
      String fileName = "junixsocket-native-2.0.4";
      String mapped = System.mapLibraryName("junixsocket-native-2.0.4");
      String[] aols = getAOLs();
      ClassLoader loader = NarSystem.class.getClassLoader();
      File unpacked = getUnpackedLibPath(loader, aols, "junixsocket-native-2.0.4", mapped);
      if (unpacked != null) {
         System.load(unpacked.getPath());
      } else {
         try {
            String libPath = getLibPath(loader, aols, mapped);
            JniExtractor extractor = new DefaultJniExtractor(NarSystem.class, System.getProperty("java.io.tmpdir"));
            File extracted = extractor.extractJni(libPath, "junixsocket-native-2.0.4");
            System.load(extracted.getPath());
         } catch (Exception var8) {
            var8.printStackTrace();
            throw var8 instanceof RuntimeException ? (RuntimeException)var8 : new RuntimeException(var8);
         }
      }

   }

   public static int runUnitTests() {
      return (new NarSystem()).runUnitTestsNative();
   }

   public native int runUnitTestsNative();

   private static String[] getAOLs() {
      String ao = System.getProperty("os.arch") + "-" + System.getProperty("os.name").replaceAll(" ", "");
      if (ao.startsWith("i386-Linux")) {
         return new String[]{"i386-Linux-ecpc", "i386-Linux-gpp", "i386-Linux-icc", "i386-Linux-ecc", "i386-Linux-icpc", "i386-Linux-linker", "i386-Linux-gcc"};
      } else if (ao.startsWith("x86-Windows")) {
         return new String[]{"x86-Windows-linker", "x86-Windows-gpp", "x86-Windows-msvc", "x86-Windows-icl", "x86-Windows-gcc"};
      } else if (ao.startsWith("amd64-Linux")) {
         return new String[]{"amd64-Linux-gpp", "amd64-Linux-icpc", "amd64-Linux-gcc", "amd64-Linux-linker"};
      } else if (ao.startsWith("amd64-Windows")) {
         return new String[]{"amd64-Windows-gpp", "amd64-Windows-msvc", "amd64-Windows-icl", "amd64-Windows-linker", "amd64-Windows-gcc"};
      } else if (ao.startsWith("amd64-FreeBSD")) {
         return new String[]{"amd64-FreeBSD-gpp", "amd64-FreeBSD-gcc", "amd64-FreeBSD-linker"};
      } else if (ao.startsWith("ppc-MacOSX")) {
         return new String[]{"ppc-MacOSX-gpp", "ppc-MacOSX-linker", "ppc-MacOSX-gcc"};
      } else if (ao.startsWith("x86_64-MacOSX")) {
         return new String[]{"x86_64-MacOSX-icc", "x86_64-MacOSX-icpc", "x86_64-MacOSX-gpp", "x86_64-MacOSX-linker", "x86_64-MacOSX-gcc"};
      } else if (ao.startsWith("ppc-AIX")) {
         return new String[]{"ppc-AIX-gpp", "ppc-AIX-xlC", "ppc-AIX-gcc", "ppc-AIX-linker"};
      } else if (ao.startsWith("i386-FreeBSD")) {
         return new String[]{"i386-FreeBSD-gpp", "i386-FreeBSD-gcc", "i386-FreeBSD-linker"};
      } else if (ao.startsWith("sparc-SunOS")) {
         return new String[]{"sparc-SunOS-cc", "sparc-SunOS-CC", "sparc-SunOS-linker"};
      } else if (ao.startsWith("arm-Linux")) {
         return new String[]{"arm-Linux-gpp", "arm-Linux-linker", "arm-Linux-gcc"};
      } else if (ao.startsWith("x86-SunOS")) {
         return new String[]{"x86-SunOS-g++", "x86-SunOS-linker"};
      } else if (ao.startsWith("i386-MacOSX")) {
         return new String[]{"i386-MacOSX-gpp", "i386-MacOSX-gcc", "i386-MacOSX-linker"};
      } else {
         throw new RuntimeException("Unhandled architecture/OS: " + ao);
      }
   }

   private static File getUnpackedLibPath(ClassLoader loader, String[] aols, String fileName, String mapped) {
      String classPath = NarSystem.class.getName().replace('.', '/') + ".class";
      URL url = loader.getResource(classPath);
      if (url != null && "file".equals(url.getProtocol())) {
         String path = url.getPath();
         String prefix = path.substring(0, path.length() - classPath.length()) + "../nar/" + fileName + "-";
         String[] var8 = aols;
         int var9 = aols.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String aol = var8[var10];
            File file = new File(prefix + aol + "-jni/lib/" + aol + "/jni/" + mapped);
            if (file.isFile()) {
               return file;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static String getLibPath(ClassLoader loader, String[] aols, String mapped) {
      String[] var3 = aols;
      int var4 = aols.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String aol = var3[var5];
         String libPath = "lib/" + aol + "/jni/";
         if (loader.getResource(libPath + mapped) != null) {
            return libPath;
         }
      }

      throw new RuntimeException("Library '" + mapped + "' not found!");
   }
}
