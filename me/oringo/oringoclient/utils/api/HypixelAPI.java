package me.oringo.oringoclient.utils.api;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.URL;

public class HypixelAPI {
   public static JsonObject getPlayer(String uuid, String apiKey) {
      try {
         JsonObject d = (new JsonParser()).parse(new InputStreamReader((new URL(String.format("https://api.hypixel.net/player?uuid=%s&key=%s", uuid, apiKey))).openStream())).getAsJsonObject();
         return d.get("player") instanceof JsonNull ? null : d.getAsJsonObject("player");
      } catch (Exception var3) {
         return null;
      }
   }

   public static double getSumoWLR(JsonObject player) {
      try {
         if (player.getAsJsonObject("stats").getAsJsonObject("Duels").get("sumo_duel_wins").getAsInt() == 0) {
            return 0.0D;
         } else {
            return player.getAsJsonObject("stats").getAsJsonObject("Duels").get("sumo_duel_losses").getAsInt() == 0 ? -1.0D : (double)player.getAsJsonObject("stats").getAsJsonObject("Duels").get("sumo_duel_wins").getAsInt() / (double)player.getAsJsonObject("stats").getAsJsonObject("Duels").get("sumo_duel_losses").getAsInt();
         }
      } catch (Exception var2) {
         return -1.0D;
      }
   }

   public static String getName(JsonObject player) {
      try {
         return player.get("displayname").getAsString();
      } catch (Exception var2) {
         return "";
      }
   }

   public static int getSumoWins(JsonObject player) {
      try {
         return player.getAsJsonObject("stats").getAsJsonObject("Duels").get("sumo_duel_wins").getAsInt();
      } catch (Exception var2) {
         return 10000000;
      }
   }

   public static int getSumoLosses(JsonObject player) {
      try {
         return player.getAsJsonObject("stats").getAsJsonObject("Duels").get("sumo_duel_losses").getAsInt();
      } catch (Exception var2) {
         return 0;
      }
   }

   public static int getSumoStreak(JsonObject player) {
      try {
         return player.getAsJsonObject("stats").getAsJsonObject("Duels").get("current_sumo_winstreak").getAsInt();
      } catch (Exception var2) {
         return 0;
      }
   }
}
