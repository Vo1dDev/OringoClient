package me.oringo.oringoclient.qolfeatures.module.impl.other;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.TimerUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class Timer extends Module {
   public static final NumberSetting timer = new NumberSetting("Timer", 1.0D, 0.1D, 5.0D, 0.1D);

   public Timer() {
      super("Timer", Module.Category.OTHER);
      this.addSettings(new Setting[]{timer});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (event.phase == Phase.START && this.isToggled()) {
         TimerUtil.setSpeed((float)timer.getValue());
      }

   }

   public void onDisable() {
      if (TimerUtil.getTimer() != null) {
         TimerUtil.resetSpeed();
      }

   }
}
