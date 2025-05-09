package org.scijava.nativelib;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MxSysInfo {
   public static String getMxSysInfo() {
      String mxSysInfo = System.getProperty("mx.sysinfo");
      return mxSysInfo != null ? mxSysInfo : guessMxSysInfo();
   }

   public static String guessMxSysInfo() {
      String arch = System.getProperty("os.arch");
      String os = System.getProperty("os.name");
      String extra = "unknown";
      if ("Linux".equals(os)) {
         try {
            String libc_dest = (new File("/lib/libc.so.6")).getCanonicalPath();
            Matcher libc_m = Pattern.compile(".*/libc-(\\d+)\\.(\\d+)\\..*").matcher(libc_dest);
            if (!libc_m.matches()) {
               throw new IOException("libc symlink contains unexpected destination: " + libc_dest);
            }

            File libstdcxx_file = new File("/usr/lib/libstdc++.so.6");
            if (!libstdcxx_file.exists()) {
               libstdcxx_file = new File("/usr/lib/libstdc++.so.5");
            }

            String libstdcxx_dest = libstdcxx_file.getCanonicalPath();
            Matcher libstdcxx_m = Pattern.compile(".*/libstdc\\+\\+\\.so\\.(\\d+)\\.0\\.(\\d+)").matcher(libstdcxx_dest);
            if (!libstdcxx_m.matches()) {
               throw new IOException("libstdc++ symlink contains unexpected destination: " + libstdcxx_dest);
            }

            String cxxver;
            if ("5".equals(libstdcxx_m.group(1))) {
               cxxver = "5";
            } else if ("6".equals(libstdcxx_m.group(1))) {
               int minor_ver = Integer.parseInt(libstdcxx_m.group(2));
               if (minor_ver < 9) {
                  cxxver = "6";
               } else {
                  cxxver = "6" + libstdcxx_m.group(2);
               }
            } else {
               cxxver = libstdcxx_m.group(1) + libstdcxx_m.group(2);
            }

            extra = "c" + libc_m.group(1) + libc_m.group(2) + "cxx" + cxxver;
         } catch (IOException var13) {
            extra = "unknown";
         } finally {
            ;
         }
      }

      return arch + "-" + os + "-" + extra;
   }
}
