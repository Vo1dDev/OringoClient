package org.slf4j;

public interface IMarkerFactory {
   Marker getMarker(String var1);

   boolean exists(String var1);

   boolean detachMarker(String var1);

   Marker getDetachedMarker(String var1);
}
