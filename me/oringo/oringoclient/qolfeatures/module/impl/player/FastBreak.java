package me.oringo.oringoclient.qolfeatures.module.impl.player;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class FastBreak extends Module {
   public NumberSetting mineSpeed = new NumberSetting("Mining speed", 1.4D, 1.0D, 1.6D, 0.1D);
   public NumberSetting maxBlocks = new NumberSetting("Additional blocks", 0.0D, 0.0D, 4.0D, 1.0D);

   public FastBreak() {
      super("Fast break", 0, Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.maxBlocks, this.mineSpeed});
   }
}
