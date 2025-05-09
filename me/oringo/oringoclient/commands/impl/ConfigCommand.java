package me.oringo.oringoclient.commands.impl;

import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.config.ConfigManager;
import me.oringo.oringoclient.ui.notifications.Notifications;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super("config");
   }

   public void execute(String[] args) throws Exception {
      if (args.length > 0) {
         String name = String.join(" ", args).replaceFirst(args[0] + " ", "");
         String var3 = args[0].toLowerCase();
         byte var4 = -1;
         switch(var3.hashCode()) {
         case 3327206:
            if (var3.equals("load")) {
               var4 = 1;
            }
            break;
         case 3522941:
            if (var3.equals("save")) {
               var4 = 0;
            }
         }

         switch(var4) {
         case 0:
            if (ConfigManager.saveConfig(ConfigManager.configPath + String.format("%s.json", name), true)) {
               Notifications.showNotification("Oringo Client", "Successfully saved config " + name, 3000);
            } else {
               Notifications.showNotification("Saving " + name + " failed", 3000, Notifications.NotificationType.ERROR);
            }
            break;
         case 1:
            if (ConfigManager.loadConfig(ConfigManager.configPath + String.format("%s.json", name))) {
               Notifications.showNotification("Oringo Client", "Config " + name + " loaded", 3000);
            } else {
               Notifications.showNotification("Loading config " + name + " failed", 3000, Notifications.NotificationType.ERROR);
            }
         }
      } else {
         try {
            Notifications.showNotification("Oringo Client", ".config load/save name", 3000);
            ConfigManager.openExplorer();
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }

   public String getDescription() {
      return "Save or load a config. .config to open explorer";
   }
}
