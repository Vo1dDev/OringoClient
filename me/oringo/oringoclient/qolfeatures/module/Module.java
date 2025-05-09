package me.oringo.oringoclient.qolfeatures.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.config.ConfigManager;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.utils.MilliTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

public class Module {
   @Expose
   @SerializedName("name")
   public String name;
   @Expose
   @SerializedName("toggled")
   private boolean toggled;
   @Expose
   @SerializedName("keyCode")
   private int keycode;
   private final Module.Category category;
   public boolean extended;
   @Expose
   @SerializedName("settings")
   public ConfigManager.ConfigSetting[] cfgSettings;
   protected static final Logger LOGGER;
   private boolean devOnly;
   protected static final Minecraft mc;
   public final MilliTimer toggledTime;
   public final List<Setting> settings;

   public Module(String name, int keycode, Module.Category category) {
      this.toggledTime = new MilliTimer();
      this.settings = new ArrayList();
      this.name = name;
      this.keycode = keycode;
      this.category = category;
   }

   public Module(String name, Module.Category category) {
      this(name, 0, category);
   }

   public boolean isToggled() {
      return this.toggled;
   }

   public void toggle() {
      this.setToggled(!this.toggled);
   }

   public void onEnable() {
   }

   public void onSave() {
   }

   public boolean isKeybind() {
      return false;
   }

   public void addSetting(Setting setting) {
      this.getSettings().add(setting);
   }

   public void addSettings(Setting... settings) {
      Setting[] var2 = settings;
      int var3 = settings.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Setting setting = var2[var4];
         this.addSetting(setting);
      }

   }

   public Module.Category getCategory() {
      return this.category;
   }

   public String getName() {
      return this.name;
   }

   public boolean isPressed() {
      return this.keycode != 0 && Keyboard.isKeyDown(this.keycode) && this.isKeybind();
   }

   public int getKeycode() {
      return this.keycode;
   }

   public void setKeycode(int keycode) {
      this.keycode = keycode;
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   public static List<Module> getModulesByCategory(Module.Category c) {
      return (List)OringoClient.modules.stream().filter((module) -> {
         return module.category == c;
      }).collect(Collectors.toList());
   }

   public static <T> T getModule(Class<T> module) {
      Iterator var1 = OringoClient.modules.iterator();

      Module m;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         m = (Module)var1.next();
      } while(!m.getClass().equals(module));

      return m;
   }

   public static Module getModule(Predicate<Module> predicate) {
      Iterator var1 = OringoClient.modules.iterator();

      Module m;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         m = (Module)var1.next();
      } while(!predicate.test(m));

      return m;
   }

   public static Module getModule(String string) {
      Iterator var1 = OringoClient.modules.iterator();

      Module m;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         m = (Module)var1.next();
      } while(!m.getName().equalsIgnoreCase(string));

      return m;
   }

   public void setToggled(boolean toggled) {
      if (this.toggled != toggled) {
         this.toggled = toggled;
         this.toggledTime.reset();
         if (toggled) {
            this.onEnable();
         } else {
            this.onDisable();
         }
      }

   }

   public void onDisable() {
   }

   public void setDevOnly(boolean devOnly) {
      this.devOnly = devOnly;
   }

   public boolean isDevOnly() {
      return this.devOnly;
   }

   protected static void sendMessage(String message) {
      Minecraft.func_71410_x().field_71439_g.func_145747_a(new ChatComponentText(message));
   }

   protected static void sendMessageWithPrefix(String message) {
      Minecraft.func_71410_x().field_71439_g.func_145747_a(new ChatComponentText("§bOringoClient §3» §7" + message));
   }

   static {
      LOGGER = OringoClient.LOGGER;
      mc = Minecraft.func_71410_x();
   }

   public static enum Category {
      COMBAT("Combat"),
      SKYBLOCK("Skyblock"),
      RENDER("Render"),
      MOVEMENT("Movement"),
      PLAYER("Player"),
      OTHER("Other"),
      KEYBINDS("Keybinds");

      public String name;

      private Category(String name) {
         this.name = name;
      }
   }
}
