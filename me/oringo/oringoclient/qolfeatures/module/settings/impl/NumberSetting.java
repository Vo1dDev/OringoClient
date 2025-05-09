package me.oringo.oringoclient.qolfeatures.module.settings.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.function.Predicate;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;

public class NumberSetting extends Setting {
   double min;
   double max;
   double increment;
   @Expose
   @SerializedName("value")
   private double value;

   public NumberSetting(String name, double defaultValue, double minimum, double maximum, double increment) {
      super(name);
      this.value = defaultValue;
      this.min = minimum;
      this.max = maximum;
      this.increment = increment;
   }

   public NumberSetting(String name, double defaultValue, double min, double max, double increment, Predicate<Boolean> isHidden) {
      super(name, isHidden);
      this.value = defaultValue;
      this.min = min;
      this.max = max;
      this.increment = increment;
   }

   public static double clamp(double value, double min, double max) {
      value = Math.max(min, value);
      value = Math.min(max, value);
      return value;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      value = clamp(value, this.getMin(), this.getMax());
      value = (double)Math.round(value * (1.0D / this.getIncrement())) / (1.0D / this.getIncrement());
      this.value = value;
   }

   public void set(double value) {
      this.value = value;
   }

   public double getMin() {
      return this.min;
   }

   public void setMin(double min) {
      this.min = min;
   }

   public double getMax() {
      return this.max;
   }

   public void setMax(double max) {
      this.max = max;
   }

   public double getIncrement() {
      return this.increment;
   }

   public void setIncrement(double increment) {
      this.increment = increment;
   }
}
