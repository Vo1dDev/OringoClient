package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class Reach extends Module {
   public NumberSetting reach = new NumberSetting("Range", 3.0D, 2.0D, 4.5D, 0.1D);
   public NumberSetting blockReach = new NumberSetting("Block Range", 4.5D, 2.0D, 6.0D, 0.01D);

   public Reach() {
      super("Reach", 0, Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.reach, this.blockReach});
   }
}
