package me.oringo.oringoclient.commands.impl;

import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.ui.notifications.Notifications;

public class TestCommand extends Command {
   public TestCommand() {
      super("test");
   }

   public void execute(String[] args) throws Exception {
      Notifications.showNotification("Test", 3000, Notifications.NotificationType.INFO);
      Notifications.showNotification("Test", 3000, Notifications.NotificationType.ERROR);
      Notifications.showNotification("Test", 3000, Notifications.NotificationType.WARNING);
   }

   public String getDescription() {
      return null;
   }
}
