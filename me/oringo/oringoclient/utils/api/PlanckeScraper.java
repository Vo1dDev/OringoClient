package me.oringo.oringoclient.utils.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlanckeScraper {
   public static HttpURLConnection getConnection(URL url) {
      try {
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
         connection.setRequestMethod("GET");
         connection.setDoInput(true);
         return connection;
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String getUserData(String name) {
      try {
         HttpURLConnection connection = getConnection(new URL(String.format("https://plancke.io/hypixel/player/stats/%s", name)));
         if (connection != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
               builder.append(line).append("\n");
            }

            return builder.toString();
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return "";
   }

   public static boolean getActivity(String name) {
      Matcher matcher = Pattern.compile("<div class=\"card-box m-b-10\">\n<h4 class=\"m-t-0 header-title\">Status</h4>\n<b>(.*?)</b>\n</div>").matcher(getUserData(name));
      return !matcher.find();
   }

   public static int getBans() {
      try {
         HttpURLConnection connection = (HttpURLConnection)(new URL("https://api.plancke.io/hypixel/v1/punishmentStats")).openConnection();
         connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
         connection.setRequestMethod("GET");
         JsonObject object = (new JsonParser()).parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
         return object.get("record").getAsJsonObject().get("staff_total").getAsInt();
      } catch (Exception var2) {
         var2.printStackTrace();
         return -1;
      }
   }
}
