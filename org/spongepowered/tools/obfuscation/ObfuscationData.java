package org.spongepowered.tools.obfuscation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ObfuscationData<T> implements Iterable<ObfuscationType> {
   private final Map<ObfuscationType, T> data;
   private final T defaultValue;

   public ObfuscationData() {
      this((Object)null);
   }

   public ObfuscationData(T defaultValue) {
      this.data = new HashMap();
      this.defaultValue = defaultValue;
   }

   /** @deprecated */
   @Deprecated
   public void add(ObfuscationType type, T value) {
      this.put(type, value);
   }

   public void put(ObfuscationType type, T value) {
      this.data.put(type, value);
   }

   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   public T get(ObfuscationType type) {
      T value = this.data.get(type);
      return value != null ? value : this.defaultValue;
   }

   public Iterator<ObfuscationType> iterator() {
      return this.data.keySet().iterator();
   }

   public String toString() {
      return String.format("ObfuscationData[%s,DEFAULT=%s]", this.listValues(), this.defaultValue);
   }

   public String values() {
      return "[" + this.listValues() + "]";
   }

   private String listValues() {
      StringBuilder sb = new StringBuilder();
      boolean delim = false;

      for(Iterator var3 = this.data.keySet().iterator(); var3.hasNext(); delim = true) {
         ObfuscationType type = (ObfuscationType)var3.next();
         if (delim) {
            sb.append(',');
         }

         sb.append(type.getKey()).append('=').append(this.data.get(type));
      }

      return sb.toString();
   }
}
