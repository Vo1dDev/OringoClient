package org.spongepowered.asm.service;

public interface IGlobalPropertyService {
   <T> T getProperty(String var1);

   void setProperty(String var1, Object var2);

   <T> T getProperty(String var1, T var2);

   String getPropertyString(String var1, String var2);
}
