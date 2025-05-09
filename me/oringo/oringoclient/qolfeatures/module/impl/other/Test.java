package me.oringo.oringoclient.qolfeatures.module.impl.other;

import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Test extends Module {
   public Test() {
      super("Test", Module.Category.OTHER);
   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
   }

   public boolean isDevOnly() {
      return true;
   }
}
