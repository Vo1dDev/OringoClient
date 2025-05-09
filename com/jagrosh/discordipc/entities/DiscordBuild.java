package com.jagrosh.discordipc.entities;

public enum DiscordBuild {
   CANARY("//canary.discordapp.com/api"),
   PTB("//ptb.discordapp.com/api"),
   STABLE("//discordapp.com/api"),
   ANY;

   private final String endpoint;

   private DiscordBuild(String endpoint) {
      this.endpoint = endpoint;
   }

   private DiscordBuild() {
      this((String)null);
   }

   public static DiscordBuild from(String endpoint) {
      DiscordBuild[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DiscordBuild value = var1[var3];
         if (value.endpoint != null && value.endpoint.equals(endpoint)) {
            return value;
         }
      }

      return ANY;
   }
}
