package org.json;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

public class Property {
   public static JSONObject toJSONObject(Properties properties) throws JSONException {
      JSONObject jo = new JSONObject(properties == null ? 0 : properties.size());
      if (properties != null && !properties.isEmpty()) {
         Enumeration enumProperties = properties.propertyNames();

         while(enumProperties.hasMoreElements()) {
            String name = (String)enumProperties.nextElement();
            jo.put(name, (Object)properties.getProperty(name));
         }
      }

      return jo;
   }

   public static Properties toProperties(JSONObject jo) throws JSONException {
      Properties properties = new Properties();
      if (jo != null) {
         Iterator var2 = jo.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, ?> entry = (Entry)var2.next();
            Object value = entry.getValue();
            if (!JSONObject.NULL.equals(value)) {
               properties.put(entry.getKey(), value.toString());
            }
         }
      }

      return properties;
   }
}
