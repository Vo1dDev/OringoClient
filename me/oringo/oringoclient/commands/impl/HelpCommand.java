package me.oringo.oringoclient.commands.impl;

import java.util.HashMap;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.commands.CommandHandler;

public class HelpCommand extends Command {
   public static HashMap<String, Command> helpMap = new HashMap();

   public HelpCommand() {
      super("help", "commands", "info");
   }

   public void execute(String[] args) throws Exception {
      if (args.length == 0) {
         OringoClient.sendMessage(String.format("§d%shelp command §7for more info", CommandHandler.getCommandPrefix()));
         helpMap.forEach((key, value) -> {
            if (value.getDescription() != null) {
               OringoClient.sendMessage(String.format("§d%s%s §3» §7%s", CommandHandler.getCommandPrefix(), key, value.getDescription()));
            }

         });
      } else if (helpMap.containsKey(args[0])) {
         String name = args[0];
         Command command = (Command)helpMap.get(args[0]);
         OringoClient.sendMessage(String.format("§b%s%s §3» §7%s", CommandHandler.getCommandPrefix(), name, command.getLongDesc()));
      } else {
         OringoClient.sendMessage(String.format("§bOringoClient §3» §cInvalid command \"%shelp\" for §cmore info", CommandHandler.getCommandPrefix()));
      }

   }

   public String getDescription() {
      return "Shows all commands";
   }
}
