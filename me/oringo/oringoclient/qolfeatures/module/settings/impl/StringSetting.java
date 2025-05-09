package me.oringo.oringoclient.qolfeatures.module.settings.impl;

import me.oringo.oringoclient.qolfeatures.module.settings.Setting;

public class StringSetting extends Setting {
   private String value;
   int length;

   public StringSetting(String name, int length) {
      this(name, "", length);
   }

   public StringSetting(String name, String defaultValue) {
      this(name, defaultValue, -1);
   }

   public StringSetting(String name) {
      this(name, "", -1);
   }

   public StringSetting(String name, String defaultValue, int length) {
      super(name);
      this.value = defaultValue;
      this.length = length;
   }

   public boolean is(String string) {
      return this.value.equals(string);
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
      if (this.value.length() > this.length && this.length > 0) {
         this.value = this.value.substring(0, this.length - 1);
      }

   }
}
