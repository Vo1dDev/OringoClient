package me.oringo.oringoclient.qolfeatures.module.settings.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.function.Predicate;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;

public class BooleanSetting extends Setting {
   @Expose
   @SerializedName("value")
   private boolean enabled;

   public BooleanSetting(String name, boolean enabled) {
      super(name);
      this.enabled = enabled;
   }

   public BooleanSetting(String name, boolean enabled, Predicate<Boolean> isHidden) {
      super(name, isHidden);
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }
}
