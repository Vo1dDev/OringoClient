package org.slf4j.event;

public enum Level {
   ERROR(40, "ERROR"),
   WARN(30, "WARN"),
   INFO(20, "INFO"),
   DEBUG(10, "DEBUG"),
   TRACE(0, "TRACE");

   private int levelInt;
   private String levelStr;

   private Level(int i, String s) {
      this.levelInt = i;
      this.levelStr = s;
   }

   public int toInt() {
      return this.levelInt;
   }

   public String toString() {
      return this.levelStr;
   }
}
