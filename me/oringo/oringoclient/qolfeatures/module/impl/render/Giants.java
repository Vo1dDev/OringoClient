package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class Giants extends Module {
   public NumberSetting scale = new NumberSetting("Scale", 1.0D, 0.1D, 5.0D, 0.1D);
   public BooleanSetting mobs = new BooleanSetting("Mobs", true);
   public BooleanSetting players = new BooleanSetting("Players", true);
   public BooleanSetting armorStands = new BooleanSetting("ArmorStands", false);

   public Giants() {
      super("Giants", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.scale, this.players, this.mobs, this.armorStands});
   }
}
