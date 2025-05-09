package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

public class EventRecodingLogger implements Logger {
   String name;
   SubstituteLogger logger;
   Queue<SubstituteLoggingEvent> eventQueue;

   public EventRecodingLogger(SubstituteLogger logger, Queue<SubstituteLoggingEvent> eventQueue) {
      this.logger = logger;
      this.name = logger.getName();
      this.eventQueue = eventQueue;
   }

   public String getName() {
      return this.name;
   }

   private void recordEvent(Level level, String msg, Object[] args, Throwable throwable) {
      this.recordEvent(level, (Marker)null, msg, args, throwable);
   }

   private void recordEvent(Level level, Marker marker, String msg, Object[] args, Throwable throwable) {
      SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
      loggingEvent.setTimeStamp(System.currentTimeMillis());
      loggingEvent.setLevel(level);
      loggingEvent.setLogger(this.logger);
      loggingEvent.setLoggerName(this.name);
      loggingEvent.setMarker(marker);
      loggingEvent.setMessage(msg);
      loggingEvent.setArgumentArray(args);
      loggingEvent.setThrowable(throwable);
      loggingEvent.setThreadName(Thread.currentThread().getName());
      this.eventQueue.add(loggingEvent);
   }

   public boolean isTraceEnabled() {
      return true;
   }

   public void trace(String msg) {
      this.recordEvent(Level.TRACE, msg, (Object[])null, (Throwable)null);
   }

   public void trace(String format, Object arg) {
      this.recordEvent(Level.TRACE, format, new Object[]{arg}, (Throwable)null);
   }

   public void trace(String format, Object arg1, Object arg2) {
      this.recordEvent(Level.TRACE, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void trace(String format, Object... arguments) {
      this.recordEvent(Level.TRACE, format, arguments, (Throwable)null);
   }

   public void trace(String msg, Throwable t) {
      this.recordEvent(Level.TRACE, msg, (Object[])null, t);
   }

   public boolean isTraceEnabled(Marker marker) {
      return true;
   }

   public void trace(Marker marker, String msg) {
      this.recordEvent(Level.TRACE, marker, msg, (Object[])null, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object arg) {
      this.recordEvent(Level.TRACE, marker, format, new Object[]{arg}, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent(Level.TRACE, marker, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void trace(Marker marker, String format, Object... argArray) {
      this.recordEvent(Level.TRACE, marker, format, argArray, (Throwable)null);
   }

   public void trace(Marker marker, String msg, Throwable t) {
      this.recordEvent(Level.TRACE, marker, msg, (Object[])null, t);
   }

   public boolean isDebugEnabled() {
      return true;
   }

   public void debug(String msg) {
      this.recordEvent(Level.TRACE, msg, (Object[])null, (Throwable)null);
   }

   public void debug(String format, Object arg) {
      this.recordEvent(Level.DEBUG, format, new Object[]{arg}, (Throwable)null);
   }

   public void debug(String format, Object arg1, Object arg2) {
      this.recordEvent(Level.DEBUG, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void debug(String format, Object... arguments) {
      this.recordEvent(Level.DEBUG, format, arguments, (Throwable)null);
   }

   public void debug(String msg, Throwable t) {
      this.recordEvent(Level.DEBUG, msg, (Object[])null, t);
   }

   public boolean isDebugEnabled(Marker marker) {
      return true;
   }

   public void debug(Marker marker, String msg) {
      this.recordEvent(Level.DEBUG, marker, msg, (Object[])null, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object arg) {
      this.recordEvent(Level.DEBUG, marker, format, new Object[]{arg}, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent(Level.DEBUG, marker, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void debug(Marker marker, String format, Object... arguments) {
      this.recordEvent(Level.DEBUG, marker, format, arguments, (Throwable)null);
   }

   public void debug(Marker marker, String msg, Throwable t) {
      this.recordEvent(Level.DEBUG, marker, msg, (Object[])null, t);
   }

   public boolean isInfoEnabled() {
      return true;
   }

   public void info(String msg) {
      this.recordEvent(Level.INFO, msg, (Object[])null, (Throwable)null);
   }

   public void info(String format, Object arg) {
      this.recordEvent(Level.INFO, format, new Object[]{arg}, (Throwable)null);
   }

   public void info(String format, Object arg1, Object arg2) {
      this.recordEvent(Level.INFO, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void info(String format, Object... arguments) {
      this.recordEvent(Level.INFO, format, arguments, (Throwable)null);
   }

   public void info(String msg, Throwable t) {
      this.recordEvent(Level.INFO, msg, (Object[])null, t);
   }

   public boolean isInfoEnabled(Marker marker) {
      return true;
   }

   public void info(Marker marker, String msg) {
      this.recordEvent(Level.INFO, marker, msg, (Object[])null, (Throwable)null);
   }

   public void info(Marker marker, String format, Object arg) {
      this.recordEvent(Level.INFO, marker, format, new Object[]{arg}, (Throwable)null);
   }

   public void info(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent(Level.INFO, marker, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void info(Marker marker, String format, Object... arguments) {
      this.recordEvent(Level.INFO, marker, format, arguments, (Throwable)null);
   }

   public void info(Marker marker, String msg, Throwable t) {
      this.recordEvent(Level.INFO, marker, msg, (Object[])null, t);
   }

   public boolean isWarnEnabled() {
      return true;
   }

   public void warn(String msg) {
      this.recordEvent(Level.WARN, msg, (Object[])null, (Throwable)null);
   }

   public void warn(String format, Object arg) {
      this.recordEvent(Level.WARN, format, new Object[]{arg}, (Throwable)null);
   }

   public void warn(String format, Object arg1, Object arg2) {
      this.recordEvent(Level.WARN, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void warn(String format, Object... arguments) {
      this.recordEvent(Level.WARN, format, arguments, (Throwable)null);
   }

   public void warn(String msg, Throwable t) {
      this.recordEvent(Level.WARN, msg, (Object[])null, t);
   }

   public boolean isWarnEnabled(Marker marker) {
      return true;
   }

   public void warn(Marker marker, String msg) {
      this.recordEvent(Level.WARN, msg, (Object[])null, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object arg) {
      this.recordEvent(Level.WARN, format, new Object[]{arg}, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent(Level.WARN, marker, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void warn(Marker marker, String format, Object... arguments) {
      this.recordEvent(Level.WARN, marker, format, arguments, (Throwable)null);
   }

   public void warn(Marker marker, String msg, Throwable t) {
      this.recordEvent(Level.WARN, marker, msg, (Object[])null, t);
   }

   public boolean isErrorEnabled() {
      return true;
   }

   public void error(String msg) {
      this.recordEvent(Level.ERROR, msg, (Object[])null, (Throwable)null);
   }

   public void error(String format, Object arg) {
      this.recordEvent(Level.ERROR, format, new Object[]{arg}, (Throwable)null);
   }

   public void error(String format, Object arg1, Object arg2) {
      this.recordEvent(Level.ERROR, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void error(String format, Object... arguments) {
      this.recordEvent(Level.ERROR, format, arguments, (Throwable)null);
   }

   public void error(String msg, Throwable t) {
      this.recordEvent(Level.ERROR, msg, (Object[])null, t);
   }

   public boolean isErrorEnabled(Marker marker) {
      return true;
   }

   public void error(Marker marker, String msg) {
      this.recordEvent(Level.ERROR, marker, msg, (Object[])null, (Throwable)null);
   }

   public void error(Marker marker, String format, Object arg) {
      this.recordEvent(Level.ERROR, marker, format, new Object[]{arg}, (Throwable)null);
   }

   public void error(Marker marker, String format, Object arg1, Object arg2) {
      this.recordEvent(Level.ERROR, marker, format, new Object[]{arg1, arg2}, (Throwable)null);
   }

   public void error(Marker marker, String format, Object... arguments) {
      this.recordEvent(Level.ERROR, marker, format, arguments, (Throwable)null);
   }

   public void error(Marker marker, String msg, Throwable t) {
      this.recordEvent(Level.ERROR, marker, msg, (Object[])null, t);
   }
}
