package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;

public class Camera extends Module {
   public BooleanSetting cameraClip = new BooleanSetting("Camera Clip", true);
   public BooleanSetting noHurtCam = new BooleanSetting("No hurt cam", false);
   public BooleanSetting smoothF5 = new BooleanSetting("Smooth f5", true);
   public NumberSetting cameraDistance = new NumberSetting("Camera Distance", 4.0D, 2.0D, 10.0D, 0.1D);
   public NumberSetting speed = new NumberSetting("Smooth speed", 1.0D, 0.1D, 5.0D, 0.1D, (aBoolean) -> {
      return !this.smoothF5.isEnabled();
   });
   public NumberSetting startSize = new NumberSetting("Start distance", 3.0D, 1.0D, 10.0D, 0.1D, (aBoolean) -> {
      return !this.smoothF5.isEnabled();
   });

   public Camera() {
      super("Camera", Module.Category.RENDER);
      this.addSettings(new Setting[]{this.cameraDistance, this.cameraClip, this.noHurtCam, this.smoothF5, this.speed, this.startSize});
   }
}
