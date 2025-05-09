package me.oringo.oringoclient.commands.impl;

import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.ui.notifications.Notifications;

public class ClipCommand extends Command {
   public ClipCommand() {
      super("clip", "vclip");
   }

   public void execute(String[] args) throws Exception {
      if (args.length == 1) {
         mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + Double.parseDouble(args[0]), mc.field_71439_g.field_70161_v);
      } else {
         Notifications.showNotification(".clip distance", 1500, Notifications.NotificationType.ERROR);
      }

   }

   public String getDescription() {
      return "Clips you up x blocks";
   }
}
