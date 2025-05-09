package org.slf4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.EventRecodingLogger;
import org.slf4j.event.LoggingEvent;
import org.slf4j.event.SubstituteLoggingEvent;

public class SubstituteLogger implements Logger {
   private final String name;
   private volatile Logger _delegate;
   private Boolean delegateEventAware;
   private Method logMethodCache;
   private EventRecodingLogger eventRecodingLogger;
   private Queue<SubstituteLoggingEvent> eventQueue;
   private final boolean createdPostInitialization;

   public SubstituteLogger(String name, Queue<SubstituteLoggingEvent> eventQueue, boolean createdPostInitialization) {
      this.name = name;
      this.eventQueue = eventQueue;
      this.createdPostInitialization = createdPostInitialization;
   }

   public String getName() {
      return this.name;
   }

   public boolean isTraceEnabled() {
      return this.delegate().isTraceEnabled();
   }

   public void trace(String msg) {
      this.delegate().trace(msg);
   }

   public void trace(String format, Object arg) {
      this.delegate().trace(format, arg);
   }

   public void trace(String format, Object arg1, Object arg2) {
      this.delegate().trace(format, arg1, arg2);
   }

   public void trace(String format, Object... arguments) {
      this.delegate().trace(format, arguments);
   }

   public void trace(String msg, Throwable t) {
      this.delegate().trace(msg, t);
   }

   public boolean isTraceEnabled(Marker marker) {
      return this.delegate().isTraceEnabled(marker);
   }

   public void trace(Marker marker, String msg) {
      this.delegate().trace(marker, msg);
   }

   public void trace(Marker marker, String format, Object arg) {
      this.delegate().trace(marker, format, arg);
   }

   public void trace(Marker marker, String format, Object arg1, Object arg2) {
      this.delegate().trace(marker, format, arg1, arg2);
   }

   public void trace(Marker marker, String format, Object... arguments) {
      this.delegate().trace(marker, format, arguments);
   }

   public void trace(Marker marker, String msg, Throwable t) {
      this.delegate().trace(marker, msg, t);
   }

   public boolean isDebugEnabled() {
      return this.delegate().isDebugEnabled();
   }

   public void debug(String msg) {
      this.delegate().debug(msg);
   }

   public void debug(String format, Object arg) {
      this.delegate().debug(format, arg);
   }

   public void debug(String format, Object arg1, Object arg2) {
      this.delegate().debug(format, arg1, arg2);
   }

   public void debug(String format, Object... arguments) {
      this.delegate().debug(format, arguments);
   }

   public void debug(String msg, Throwable t) {
      this.delegate().debug(msg, t);
   }

   public boolean isDebugEnabled(Marker marker) {
      return this.delegate().isDebugEnabled(marker);
   }

   public void debug(Marker marker, String msg) {
      this.delegate().debug(marker, msg);
   }

   public void debug(Marker marker, String format, Object arg) {
      this.delegate().debug(marker, format, arg);
   }

   public void debug(Marker marker, String format, Object arg1, Object arg2) {
      this.delegate().debug(marker, format, arg1, arg2);
   }

   public void debug(Marker marker, String format, Object... arguments) {
      this.delegate().debug(marker, format, arguments);
   }

   public void debug(Marker marker, String msg, Throwable t) {
      this.delegate().debug(marker, msg, t);
   }

   public boolean isInfoEnabled() {
      return this.delegate().isInfoEnabled();
   }

   public void info(String msg) {
      this.delegate().info(msg);
   }

   public void info(String format, Object arg) {
      this.delegate().info(format, arg);
   }

   public void info(String format, Object arg1, Object arg2) {
      this.delegate().info(format, arg1, arg2);
   }

   public void info(String format, Object... arguments) {
      this.delegate().info(format, arguments);
   }

   public void info(String msg, Throwable t) {
      this.delegate().info(msg, t);
   }

   public boolean isInfoEnabled(Marker marker) {
      return this.delegate().isInfoEnabled(marker);
   }

   public void info(Marker marker, String msg) {
      this.delegate().info(marker, msg);
   }

   public void info(Marker marker, String format, Object arg) {
      this.delegate().info(marker, format, arg);
   }

   public void info(Marker marker, String format, Object arg1, Object arg2) {
      this.delegate().info(marker, format, arg1, arg2);
   }

   public void info(Marker marker, String format, Object... arguments) {
      this.delegate().info(marker, format, arguments);
   }

   public void info(Marker marker, String msg, Throwable t) {
      this.delegate().info(marker, msg, t);
   }

   public boolean isWarnEnabled() {
      return this.delegate().isWarnEnabled();
   }

   public void warn(String msg) {
      this.delegate().warn(msg);
   }

   public void warn(String format, Object arg) {
      this.delegate().warn(format, arg);
   }

   public void warn(String format, Object arg1, Object arg2) {
      this.delegate().warn(format, arg1, arg2);
   }

   public void warn(String format, Object... arguments) {
      this.delegate().warn(format, arguments);
   }

   public void warn(String msg, Throwable t) {
      this.delegate().warn(msg, t);
   }

   public boolean isWarnEnabled(Marker marker) {
      return this.delegate().isWarnEnabled(marker);
   }

   public void warn(Marker marker, String msg) {
      this.delegate().warn(marker, msg);
   }

   public void warn(Marker marker, String format, Object arg) {
      this.delegate().warn(marker, format, arg);
   }

   public void warn(Marker marker, String format, Object arg1, Object arg2) {
      this.delegate().warn(marker, format, arg1, arg2);
   }

   public void warn(Marker marker, String format, Object... arguments) {
      this.delegate().warn(marker, format, arguments);
   }

   public void warn(Marker marker, String msg, Throwable t) {
      this.delegate().warn(marker, msg, t);
   }

   public boolean isErrorEnabled() {
      return this.delegate().isErrorEnabled();
   }

   public void error(String msg) {
      this.delegate().error(msg);
   }

   public void error(String format, Object arg) {
      this.delegate().error(format, arg);
   }

   public void error(String format, Object arg1, Object arg2) {
      this.delegate().error(format, arg1, arg2);
   }

   public void error(String format, Object... arguments) {
      this.delegate().error(format, arguments);
   }

   public void error(String msg, Throwable t) {
      this.delegate().error(msg, t);
   }

   public boolean isErrorEnabled(Marker marker) {
      return this.delegate().isErrorEnabled(marker);
   }

   public void error(Marker marker, String msg) {
      this.delegate().error(marker, msg);
   }

   public void error(Marker marker, String format, Object arg) {
      this.delegate().error(marker, format, arg);
   }

   public void error(Marker marker, String format, Object arg1, Object arg2) {
      this.delegate().error(marker, format, arg1, arg2);
   }

   public void error(Marker marker, String format, Object... arguments) {
      this.delegate().error(marker, format, arguments);
   }

   public void error(Marker marker, String msg, Throwable t) {
      this.delegate().error(marker, msg, t);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SubstituteLogger that = (SubstituteLogger)o;
         return this.name.equals(that.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   Logger delegate() {
      if (this._delegate != null) {
         return this._delegate;
      } else {
         return (Logger)(this.createdPostInitialization ? NOPLogger.NOP_LOGGER : this.getEventRecordingLogger());
      }
   }

   private Logger getEventRecordingLogger() {
      if (this.eventRecodingLogger == null) {
         this.eventRecodingLogger = new EventRecodingLogger(this, this.eventQueue);
      }

      return this.eventRecodingLogger;
   }

   public void setDelegate(Logger delegate) {
      this._delegate = delegate;
   }

   public boolean isDelegateEventAware() {
      if (this.delegateEventAware != null) {
         return this.delegateEventAware;
      } else {
         try {
            this.logMethodCache = this._delegate.getClass().getMethod("log", LoggingEvent.class);
            this.delegateEventAware = Boolean.TRUE;
         } catch (NoSuchMethodException var2) {
            this.delegateEventAware = Boolean.FALSE;
         }

         return this.delegateEventAware;
      }
   }

   public void log(LoggingEvent event) {
      if (this.isDelegateEventAware()) {
         try {
            this.logMethodCache.invoke(this._delegate, event);
         } catch (IllegalAccessException var3) {
         } catch (IllegalArgumentException var4) {
         } catch (InvocationTargetException var5) {
         }
      }

   }

   public boolean isDelegateNull() {
      return this._delegate == null;
   }

   public boolean isDelegateNOP() {
      return this._delegate instanceof NOPLogger;
   }
}
