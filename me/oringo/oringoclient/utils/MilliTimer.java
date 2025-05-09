package me.oringo.oringoclient.utils;

public class MilliTimer {
   private long time;

   public MilliTimer() {
      this.reset();
   }

   public long getTime() {
      return this.time;
   }

   public long getTimePassed() {
      return System.currentTimeMillis() - this.time;
   }

   public boolean hasTimePassed(long milliseconds) {
      return System.currentTimeMillis() - this.time >= milliseconds;
   }

   public void reset() {
      this.time = System.currentTimeMillis();
   }

   public void reset(long time) {
      this.time = System.currentTimeMillis() - time;
   }
}
