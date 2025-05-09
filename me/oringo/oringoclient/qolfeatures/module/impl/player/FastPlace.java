package me.oringo.oringoclient.qolfeatures.module.impl.player;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class FastPlace extends Module {
   private static FastPlace instance = new FastPlace();
   public NumberSetting placeDelay = new NumberSetting("Place delay", 2.0D, 0.0D, 4.0D, 1.0D);

   public static FastPlace getInstance() {
      return instance;
   }

   public FastPlace() {
      super("Fast Place", Module.Category.PLAYER);
      this.addSetting(this.placeDelay);
   }
}
