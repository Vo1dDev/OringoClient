package org.json;

import java.util.Iterator;
import java.util.Map.Entry;

public class CookieList {
   public static JSONObject toJSONObject(String string) throws JSONException {
      JSONObject jo = new JSONObject();
      JSONTokener x = new JSONTokener(string);

      while(x.more()) {
         String name = Cookie.unescape(x.nextTo('='));
         x.next('=');
         jo.put(name, (Object)Cookie.unescape(x.nextTo(';')));
         x.next();
      }

      return jo;
   }

   public static String toString(JSONObject jo) throws JSONException {
      boolean b = false;
      StringBuilder sb = new StringBuilder();
      Iterator var3 = jo.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<String, ?> entry = (Entry)var3.next();
         String key = (String)entry.getKey();
         Object value = entry.getValue();
         if (!JSONObject.NULL.equals(value)) {
            if (b) {
               sb.append(';');
            }

            sb.append(Cookie.escape(key));
            sb.append("=");
            sb.append(Cookie.escape(value.toString()));
            b = true;
         }
      }

      return sb.toString();
   }
}
