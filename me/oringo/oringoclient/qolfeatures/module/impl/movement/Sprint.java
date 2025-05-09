package me.oringo.oringoclient.qolfeatures.module.impl.movement;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;

public class Sprint extends Module {
   public BooleanSetting omni = new BooleanSetting("OmniSprint", false);
   public BooleanSetting keep = new BooleanSetting("KeepSprint", true);

   public Sprint() {
      super("Sprint", 0, Module.Category.MOVEMENT);
      this.addSettings(new Setting[]{this.keep, this.omni});
   }
}
