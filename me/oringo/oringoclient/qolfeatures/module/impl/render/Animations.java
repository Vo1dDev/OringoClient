package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class Animations extends Module {
   public NumberSetting x = new NumberSetting("x", 1.0D, 0.01D, 3.0D, 0.02D);
   public NumberSetting y = new NumberSetting("y", 1.0D, 0.01D, 3.0D, 0.02D);
   public NumberSetting z = new NumberSetting("z", 1.0D, 0.01D, 3.0D, 0.02D);
   public NumberSetting size = new NumberSetting("size", 1.0D, 0.01D, 3.0D, 0.02D);
   public ModeSetting mode = new ModeSetting("Mode", "1.7", new String[]{"1.7", "chill", "push", "spin", "vertical spin", "helicopter"});
   public BooleanSetting showSwing = new BooleanSetting("Swing progress", false);

   public Animations() {
      super("Animations", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.x, this.y, this.z, this.size, this.mode, this.showSwing});
   }
}
