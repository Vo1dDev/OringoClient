package me.oringo.oringoclient.qolfeatures.module.impl.render;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;

public class ServerRotations extends Module {
   private static ServerRotations instance = new ServerRotations();
   public BooleanSetting onlyKillAura = new BooleanSetting("Only aura rotations", false);

   public static ServerRotations getInstance() {
      return instance;
   }

   public ServerRotations() {
      super("Server Rotations", Module.Category.RENDER);
      this.setToggled(true);
      this.addSettings(new Setting[]{this.onlyKillAura});
   }
}
