package me.oringo.oringoclient.qolfeatures.module.settings.impl;

import me.oringo.oringoclient.qolfeatures.module.settings.Setting;

public class RunnableSetting extends Setting {
   private final Runnable runnable;

   public RunnableSetting(String name, Runnable runnable) {
      super(name);
      this.runnable = runnable;
   }

   public void execute() {
      this.runnable.run();
   }
}
