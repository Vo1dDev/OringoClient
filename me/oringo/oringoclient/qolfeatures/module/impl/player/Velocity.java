package me.oringo.oringoclient.qolfeatures.module.impl.player;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class Velocity extends Module {
   public NumberSetting vModifier = new NumberSetting("Vertical", 0.0D, -2.0D, 2.0D, 0.05D);
   public NumberSetting hModifier = new NumberSetting("Horizontal", 0.0D, -2.0D, 2.0D, 0.05D);
   public BooleanSetting skyblockKB = new BooleanSetting("Skyblock kb", true);

   public Velocity() {
      super("Velocity", 0, Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.hModifier, this.vModifier, this.skyblockKB});
   }
}
