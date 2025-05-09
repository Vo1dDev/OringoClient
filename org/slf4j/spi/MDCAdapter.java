package org.slf4j.spi;

import java.util.Map;

public interface MDCAdapter {
   void put(String var1, String var2);

   String get(String var1);

   void remove(String var1);

   void clear();

   Map<String, String> getCopyOfContextMap();

   void setContextMap(Map<String, String> var1);
}
