package org.slf4j.helpers;

public final class Util {
   private static Util.ClassContextSecurityManager SECURITY_MANAGER;
   private static boolean SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = false;

   private Util() {
   }

   public static String safeGetSystemProperty(String key) {
      if (key == null) {
         throw new IllegalArgumentException("null input");
      } else {
         String result = null;

         try {
            result = System.getProperty(key);
         } catch (SecurityException var3) {
         }

         return result;
      }
   }

   public static boolean safeGetBooleanSystemProperty(String key) {
      String value = safeGetSystemProperty(key);
      return value == null ? false : value.equalsIgnoreCase("true");
   }

   private static Util.ClassContextSecurityManager getSecurityManager() {
      if (SECURITY_MANAGER != null) {
         return SECURITY_MANAGER;
      } else if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED) {
         return null;
      } else {
         SECURITY_MANAGER = safeCreateSecurityManager();
         SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true;
         return SECURITY_MANAGER;
      }
   }

   private static Util.ClassContextSecurityManager safeCreateSecurityManager() {
      try {
         return new Util.ClassContextSecurityManager();
      } catch (SecurityException var1) {
         return null;
      }
   }

   public static Class<?> getCallingClass() {
      Util.ClassContextSecurityManager securityManager = getSecurityManager();
      if (securityManager == null) {
         return null;
      } else {
         Class<?>[] trace = securityManager.getClassContext();
         String thisClassName = Util.class.getName();

         int i;
         for(i = 0; i < trace.length && !thisClassName.equals(trace[i].getName()); ++i) {
         }

         if (i < trace.length && i + 2 < trace.length) {
            return trace[i + 2];
         } else {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
         }
      }
   }

   public static final void report(String msg, Throwable t) {
      System.err.println(msg);
      System.err.println("Reported exception:");
      t.printStackTrace();
   }

   public static final void report(String msg) {
      System.err.println("SLF4J: " + msg);
   }

   private static final class ClassContextSecurityManager extends SecurityManager {
      private ClassContextSecurityManager() {
      }

      protected Class<?>[] getClassContext() {
         return super.getClassContext();
      }

      // $FF: synthetic method
      ClassContextSecurityManager(Object x0) {
         this();
      }
   }
}
