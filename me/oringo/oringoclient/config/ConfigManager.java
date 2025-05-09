package me.oringo.oringoclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.Desktop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.keybinds.Keybind;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public class ConfigManager {
   public static String configPath;

   public static boolean loadConfig(String configPath) {
      try {
         String configString = new String(Files.readAllBytes((new File(configPath)).toPath()));
         Gson gson = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
         Module[] modules = (Module[])gson.fromJson(configString, Module[].class);
         Iterator var4 = OringoClient.modules.iterator();

         int var13;
         while(var4.hasNext()) {
            Module module = (Module)var4.next();
            Module[] var6 = modules;
            int var7 = modules.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Module configModule = var6[var8];
               if (module.getName().equals(configModule.getName())) {
                  try {
                     try {
                        module.setToggled(configModule.isToggled());
                     } catch (Exception var16) {
                        var16.printStackTrace();
                     }

                     module.setKeycode(configModule.getKeycode());
                     Iterator var10 = module.settings.iterator();

                     while(var10.hasNext()) {
                        Setting setting = (Setting)var10.next();
                        ConfigManager.ConfigSetting[] var12 = configModule.cfgSettings;
                        var13 = var12.length;

                        for(int var14 = 0; var14 < var13; ++var14) {
                           ConfigManager.ConfigSetting cfgSetting = var12[var14];
                           if (setting != null) {
                              if (setting.name.equals(cfgSetting.name)) {
                                 if (setting instanceof BooleanSetting) {
                                    ((BooleanSetting)setting).setEnabled((Boolean)cfgSetting.value);
                                 } else if (setting instanceof ModeSetting) {
                                    ((ModeSetting)setting).setSelected((String)cfgSetting.value);
                                 } else if (setting instanceof NumberSetting) {
                                    ((NumberSetting)setting).setValue((Double)cfgSetting.value);
                                 } else if (setting instanceof StringSetting) {
                                    ((StringSetting)setting).setValue((String)cfgSetting.value);
                                 }
                              }
                           } else {
                              System.out.println("[OringoClient] Setting in " + module.getName() + " is null!");
                           }
                        }
                     }
                  } catch (Exception var17) {
                     var17.printStackTrace();
                     System.out.println("Config Issue");
                  }
               }
            }
         }

         Module[] var19 = modules;
         int var20 = modules.length;

         for(int var21 = 0; var21 < var20; ++var21) {
            Module module = var19[var21];
            if (module.getName().startsWith("Keybind ") && Module.getModule(module.getName()) == null) {
               Keybind keybind = new Keybind(module.getName());
               keybind.setKeycode(module.getKeycode());
               keybind.setToggled(module.isToggled());
               Iterator var24 = keybind.settings.iterator();

               while(var24.hasNext()) {
                  Setting setting = (Setting)var24.next();
                  ConfigManager.ConfigSetting[] var26 = module.cfgSettings;
                  int var27 = var26.length;

                  for(var13 = 0; var13 < var27; ++var13) {
                     ConfigManager.ConfigSetting cfgSetting = var26[var13];
                     if (setting.name.equals(cfgSetting.name)) {
                        if (setting instanceof BooleanSetting) {
                           ((BooleanSetting)setting).setEnabled((Boolean)cfgSetting.value);
                        } else if (setting instanceof ModeSetting) {
                           ((ModeSetting)setting).setSelected((String)cfgSetting.value);
                        } else if (setting instanceof NumberSetting) {
                           ((NumberSetting)setting).setValue((Double)cfgSetting.value);
                        } else if (setting instanceof StringSetting) {
                           ((StringSetting)setting).setValue((String)cfgSetting.value);
                        }
                     }
                  }
               }

               MinecraftForge.EVENT_BUS.register(keybind);
               OringoClient.modules.add(keybind);
               System.out.println("Loaded Keybind: " + keybind.getName());
            }
         }

         return true;
      } catch (Exception var18) {
         var18.printStackTrace();
         return false;
      }
   }

   public static void loadConfig() {
      loadConfig(OringoClient.mc.field_71412_D + "/config/OringoClient/OringoClient.json");
   }

   public static void saveConfig() {
      saveConfig(OringoClient.mc.field_71412_D + "/config/OringoClient/OringoClient.json", false);
   }

   public static boolean saveConfig(String configPath, boolean openExplorer) {
      Gson gson = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

      Module module;
      ArrayList settings;
      for(Iterator var3 = OringoClient.modules.iterator(); var3.hasNext(); module.cfgSettings = (ConfigManager.ConfigSetting[])settings.toArray(new ConfigManager.ConfigSetting[0])) {
         module = (Module)var3.next();
         module.onSave();
         settings = new ArrayList();

         ConfigManager.ConfigSetting cfgSetting;
         for(Iterator var6 = module.settings.iterator(); var6.hasNext(); settings.add(cfgSetting)) {
            Setting setting = (Setting)var6.next();
            cfgSetting = new ConfigManager.ConfigSetting((String)null, (Object)null);
            cfgSetting.name = setting.name;
            if (setting instanceof BooleanSetting) {
               cfgSetting.value = ((BooleanSetting)setting).isEnabled();
            } else if (setting instanceof ModeSetting) {
               cfgSetting.value = ((ModeSetting)setting).getSelected();
            } else if (setting instanceof NumberSetting) {
               cfgSetting.value = ((NumberSetting)setting).getValue();
            } else if (setting instanceof StringSetting) {
               cfgSetting.value = ((StringSetting)setting).getValue();
            }
         }
      }

      try {
         File file = new File(configPath);
         Files.write(file.toPath(), gson.toJson(OringoClient.modules).getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
         if (openExplorer) {
            try {
               openExplorer();
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }

         return true;
      } catch (Exception var10) {
         var10.printStackTrace();
         return false;
      }
   }

   public static void openExplorer() throws IOException {
      Desktop.getDesktop().open(new File(configPath));
   }

   static {
      configPath = OringoClient.mc.field_71412_D.getPath() + "/config/OringoClient/configs/";
   }

   public static class ConfigSetting {
      @Expose
      @SerializedName("name")
      public String name;
      @Expose
      @SerializedName("value")
      public Object value;

      public ConfigSetting(String name, Object value) {
         this.name = name;
         this.value = value;
      }
   }

   public static class FileSelection implements Transferable {
      private List<File> listOfFiles;

      public FileSelection(List<File> listOfFiles) {
         this.listOfFiles = listOfFiles;
      }

      public DataFlavor[] getTransferDataFlavors() {
         return new DataFlavor[]{DataFlavor.javaFileListFlavor};
      }

      public boolean isDataFlavorSupported(DataFlavor flavor) {
         return flavor == DataFlavor.javaFileListFlavor;
      }

      @NotNull
      public Object getTransferData(DataFlavor flavor) {
         return this.listOfFiles;
      }
   }
}
