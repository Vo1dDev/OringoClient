package me.oringo.oringoclient.qolfeatures.module.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.function.Predicate;

public class Setting {
   @Expose
   @SerializedName("name")
   public String name;
   private Predicate<Boolean> predicate;

   protected Setting(String name, Predicate<Boolean> predicate) {
      this.name = name;
      this.predicate = predicate;
   }

   protected Setting(String name) {
      this(name, (Predicate)null);
   }

   public boolean isHidden() {
      return this.predicate != null && this.predicate.test(true);
   }
}
