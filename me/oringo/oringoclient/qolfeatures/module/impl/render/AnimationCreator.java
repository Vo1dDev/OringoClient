package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class AnimationCreator extends Module {
   public BooleanSetting swingProgress = new BooleanSetting("Swing Progress", false);
   public BooleanSetting blockProgress = new BooleanSetting("Block Progress", true);
   public NumberSetting angle1 = new NumberSetting("angle1", 30.0D, -180.0D, 180.0D, 1.0D);
   public NumberSetting angle2 = new NumberSetting("angle2", -80.0D, -180.0D, 180.0D, 1.0D);
   public NumberSetting angle3 = new NumberSetting("angle3", 60.0D, -180.0D, 180.0D, 1.0D);
   public NumberSetting translateX = new NumberSetting("x1", -0.5D, -5.0D, 5.0D, 0.1D);
   public NumberSetting translateY = new NumberSetting("y1", 0.2D, -5.0D, 5.0D, 0.1D);
   public NumberSetting translateZ = new NumberSetting("z1", 0.0D, -5.0D, 5.0D, 0.1D);
   public NumberSetting rotation1x = new NumberSetting("x1", 0.0D, -5.0D, 5.0D, 0.1D);
   public NumberSetting rotation1y = new NumberSetting("y1", 1.0D, -5.0D, 5.0D, 0.1D);
   public NumberSetting rotation1z = new NumberSetting("z1", 0.0D, -5.0D, 5.0D, 0.1D);
   public NumberSetting rotation2x = new NumberSetting("x2", 1.0D, -5.0D, 5.0D, 0.1D);
   public NumberSetting rotation2y = new NumberSetting("y2", 0.0D, -5.0D, 5.0D, 0.1D);
   public NumberSetting rotation2z = new NumberSetting("z2", 0.0D, -5.0D, 5.0D, 0.1D);

   public AnimationCreator() {
      super("Animation helper", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.swingProgress, this.blockProgress, this.translateX, this.translateY, this.translateZ, this.angle1, this.rotation1x, this.rotation1y, this.rotation1z, this.angle2, this.rotation2x, this.rotation2y, this.rotation2z, this.angle3});
   }
}
