package me.oringo.oringoclient.commands;

import java.util.HashMap;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.impl.HelpCommand;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Gui;

public class CommandHandler {
   private static final HashMap<String, Command> COMMAND_MAP = new HashMap();

   public static void register(Command command) {
      String[] var1 = command.getNames();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String name = var1[var3];
         if (!COMMAND_MAP.containsKey(name.toLowerCase())) {
            COMMAND_MAP.put(name.toLowerCase(), command);
         }
      }

      HelpCommand.helpMap.put(command.getNames()[0].toLowerCase(), command);
   }

   public static void unregister(Command command) {
      String[] var1 = command.getNames();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String name = var1[var3];
         COMMAND_MAP.remove(name.toLowerCase());
      }

   }

   public static char getCommandPrefix() {
      return Gui.commandPrefix.getValue().length() < 1 ? '.' : Gui.commandPrefix.getValue().toLowerCase().charAt(0);
   }

   public static boolean handle(String message) {
      message = message.trim();
      if (message.length() > 0 && message.charAt(0) == getCommandPrefix()) {
         message = message.toLowerCase().substring(1);
         String commandString = message.split(" ")[0];
         String[] args = new String[0];
         if (message.contains(" ")) {
            args = message.replaceFirst(commandString, "").replaceFirst(" ", "").split(" ");
         }

         if (COMMAND_MAP.containsKey(commandString)) {
            Command command = (Command)COMMAND_MAP.get(commandString);

            try {
               command.execute(args);
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         } else {
            OringoClient.sendMessage(String.format("§bOringoClient §3» §cInvalid command \"%shelp\" for §cmore info", getCommandPrefix()));
         }

         return true;
      } else {
         return false;
      }
   }
}
