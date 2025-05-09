package me.oringo.oringoclient.qolfeatures.module.impl.combat;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class Hitboxes extends Module {
   public BooleanSetting onlyPlayers = new BooleanSetting("Only players", false);
   public NumberSetting expand = new NumberSetting("Expand", 0.5D, 0.1D, 1.0D, 0.1D);

   public Hitboxes() {
      super("Hitboxes", Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.expand});
   }
}
