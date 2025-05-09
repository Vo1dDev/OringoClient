package org.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JSONPointer {
   private static final String ENCODING = "utf-8";
   private final List<String> refTokens;

   public static JSONPointer.Builder builder() {
      return new JSONPointer.Builder();
   }

   public JSONPointer(String pointer) {
      if (pointer == null) {
         throw new NullPointerException("pointer cannot be null");
      } else if (!pointer.isEmpty() && !pointer.equals("#")) {
         String refs;
         if (pointer.startsWith("#/")) {
            refs = pointer.substring(2);

            try {
               refs = URLDecoder.decode(refs, "utf-8");
            } catch (UnsupportedEncodingException var7) {
               throw new RuntimeException(var7);
            }
         } else {
            if (!pointer.startsWith("/")) {
               throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
            }

            refs = pointer.substring(1);
         }

         this.refTokens = new ArrayList();
         String[] var3 = refs.split("/");
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String token = var3[var5];
            this.refTokens.add(this.unescape(token));
         }

      } else {
         this.refTokens = Collections.emptyList();
      }
   }

   public JSONPointer(List<String> refTokens) {
      this.refTokens = new ArrayList(refTokens);
   }

   private String unescape(String token) {
      return token.replace("~1", "/").replace("~0", "~").replace("\\\"", "\"").replace("\\\\", "\\");
   }

   public Object queryFrom(Object document) {
      if (this.refTokens.isEmpty()) {
         return document;
      } else {
         Object current = document;
         Iterator var3 = this.refTokens.iterator();

         while(var3.hasNext()) {
            String token = (String)var3.next();
            if (current instanceof JSONObject) {
               current = ((JSONObject)current).opt(this.unescape(token));
            } else {
               if (!(current instanceof JSONArray)) {
                  throw new JSONPointerException(String.format("value [%s] is not an array or object therefore its key %s cannot be resolved", current, token));
               }

               current = this.readByIndexToken(current, token);
            }
         }

         return current;
      }
   }

   private Object readByIndexToken(Object current, String indexToken) {
      try {
         int index = Integer.parseInt(indexToken);
         JSONArray currentArr = (JSONArray)current;
         if (index >= currentArr.length()) {
            throw new JSONPointerException(String.format("index %d is out of bounds - the array has %d elements", index, currentArr.length()));
         } else {
            return currentArr.get(index);
         }
      } catch (NumberFormatException var5) {
         throw new JSONPointerException(String.format("%s is not an array index", indexToken), var5);
      }
   }

   public String toString() {
      StringBuilder rval = new StringBuilder("");
      Iterator var2 = this.refTokens.iterator();

      while(var2.hasNext()) {
         String token = (String)var2.next();
         rval.append('/').append(this.escape(token));
      }

      return rval.toString();
   }

   private String escape(String token) {
      return token.replace("~", "~0").replace("/", "~1").replace("\\", "\\\\").replace("\"", "\\\"");
   }

   public String toURIFragment() {
      try {
         StringBuilder rval = new StringBuilder("#");
         Iterator var2 = this.refTokens.iterator();

         while(var2.hasNext()) {
            String token = (String)var2.next();
            rval.append('/').append(URLEncoder.encode(token, "utf-8"));
         }

         return rval.toString();
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static class Builder {
      private final List<String> refTokens = new ArrayList();

      public JSONPointer build() {
         return new JSONPointer(this.refTokens);
      }

      public JSONPointer.Builder append(String token) {
         if (token == null) {
            throw new NullPointerException("token cannot be null");
         } else {
            this.refTokens.add(token);
            return this;
         }
      }

      public JSONPointer.Builder append(int arrayIndex) {
         this.refTokens.add(String.valueOf(arrayIndex));
         return this;
      }
   }
}
