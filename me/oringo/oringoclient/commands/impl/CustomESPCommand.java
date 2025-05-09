package me.oringo.oringoclient.commands.impl;

import java.awt.Color;
import java.util.Iterator;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.qolfeatures.module.impl.render.CustomESP;
import me.oringo.oringoclient.qolfeatures.module.impl.render.Gui;

public class CustomESPCommand extends Command {
   public CustomESPCommand() {
      super("esp", "customesp");
   }

   public void execute(String[] args) throws Exception {
      if (args.length == 0) {
         Iterator var2 = CustomESP.names.keySet().iterator();

         while(var2.hasNext()) {
            String name = (String)var2.next();
            OringoClient.sendMessageWithPrefix(name);
         }

         OringoClient.sendMessageWithPrefix(String.format("%s%s add/remove", Gui.commandPrefix.getValue(), this.getNames()[0]));
      } else {
         String var6 = args[0];
         byte var7 = -1;
         switch(var6.hashCode()) {
         case -934610812:
            if (var6.equals("remove")) {
               var7 = 1;
            }
            break;
         case 96417:
            if (var6.equals("add")) {
               var7 = 0;
            }
         }

         switch(var7) {
         case 0:
            if (args.length == 3) {
               if (!CustomESP.names.containsKey(args[1])) {
                  CustomESP.names.put(args[1].toLowerCase(), Color.decode(args[2]));
               } else {
                  OringoClient.sendMessageWithPrefix("Name already added");
               }
            } else {
               OringoClient.sendMessageWithPrefix(String.format("Usage: %s%s add name color", Gui.commandPrefix.getValue(), this.getNames()[0]));
            }
            break;
         case 1:
            if (args.length == 2) {
               CustomESP.names.remove(args[1]);
            }
            break;
         default:
            Iterator var4 = CustomESP.names.keySet().iterator();

            while(var4.hasNext()) {
               String name = (String)var4.next();
               OringoClient.sendMessageWithPrefix(name);
            }
         }
      }

   }

   public String getDescription() {
      return "Adds or removes names to Custom ESP module";
   }
}
