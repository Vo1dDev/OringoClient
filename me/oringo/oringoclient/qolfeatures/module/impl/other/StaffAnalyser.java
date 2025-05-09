package me.oringo.oringoclient.qolfeatures.module.impl.other;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.api.PlanckeScraper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class StaffAnalyser extends Module {
   private NumberSetting delay = new NumberSetting("Delay", 5.0D, 5.0D, 60.0D, 1.0D);
   private MilliTimer timer = new MilliTimer();
   private int lastBans = -1;

   public StaffAnalyser() {
      super("Staff Analyser", Module.Category.OTHER);
      this.addSettings(new Setting[]{this.delay});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.isToggled() && this.timer.hasTimePassed((long)(this.delay.getValue() * 1000.0D))) {
         this.timer.reset();
         (new Thread(() -> {
            int bans = PlanckeScraper.getBans();
            if (bans != this.lastBans && this.lastBans != -1 && bans > this.lastBans) {
               Notifications.showNotification(String.format("Staff has banned %s %s in last %s seconds", bans - this.lastBans, bans - this.lastBans > 1 ? "people" : "person", (int)this.delay.getValue()), 2500, bans - this.lastBans > 2 ? Notifications.NotificationType.WARNING : Notifications.NotificationType.INFO);
            }

            this.lastBans = bans;
         })).start();
      }

   }
}
