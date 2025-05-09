package me.oringo.oringoclient.qolfeatures.module.settings.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;

public class ModeSetting extends Setting {
   @Expose
   @SerializedName("value")
   private String selected;
   private int index;
   private List<String> modes;
   private String defaultSelected;

   public ModeSetting(String name, String defaultSelected, String... options) {
      super(name);
      this.defaultSelected = defaultSelected;
      this.modes = Arrays.asList(options);
      this.index = this.modes.indexOf(defaultSelected);
      this.selected = (String)this.modes.get(this.index);
   }

   public ModeSetting(String name, Predicate<Boolean> isHidden, String defaultSelected, String... options) {
      super(name, isHidden);
      this.defaultSelected = defaultSelected;
      this.modes = Arrays.asList(options);
      this.index = this.modes.indexOf(defaultSelected);
      this.selected = (String)this.modes.get(this.index);
   }

   public String getSelected() {
      return this.selected;
   }

   public void setSelected(String selected) {
      this.selected = selected;
      this.index = this.modes.indexOf(selected);
   }

   public boolean is(String mode) {
      return mode.equals(this.selected);
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
      this.selected = (String)this.modes.get(index);
   }

   public List<String> getModes() {
      return this.modes;
   }

   public void setModes(List<String> modes) {
      this.modes = modes;
   }

   public void cycle(int key) {
      switch(key) {
      case 0:
         if (this.index < this.modes.size() - 1) {
            ++this.index;
            this.selected = (String)this.modes.get(this.index);
         } else if (this.index >= this.modes.size() - 1) {
            this.index = 0;
            this.selected = (String)this.modes.get(0);
         }
         break;
      case 1:
         if (this.index > 0) {
            --this.index;
            this.selected = (String)this.modes.get(this.index);
         } else {
            this.index = this.modes.size() - 1;
            this.selected = (String)this.modes.get(this.index);
         }
         break;
      default:
         this.index = this.modes.indexOf(this.defaultSelected);
         this.selected = (String)this.modes.get(this.index);
      }

   }
}
