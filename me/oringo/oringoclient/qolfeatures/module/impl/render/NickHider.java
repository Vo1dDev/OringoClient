package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;

public class NickHider extends Module {
   public StringSetting name = new StringSetting("Name");

   public NickHider() {
      super("Nick Hider", 0, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.name});
   }
}
