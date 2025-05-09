package me.oringo.oringoclient.ui.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.config.ConfigManager;
import me.oringo.oringoclient.mixins.MinecraftAccessor;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.keybinds.Keybind;
import me.oringo.oringoclient.qolfeatures.module.impl.render.PopupAnimation;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.RunnableSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.font.Fonts;
import me.oringo.oringoclient.utils.shader.BlurUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ClickGUI extends GuiScreen {
   public static ArrayList<ClickGUI.Panel> panels;
   private Module binding = null;
   private ClickGUI.Panel draggingPanel;
   private NumberSetting draggingSlider;
   private StringSetting settingString;
   private float startX;
   private float startY;
   public int guiScale;
   private MilliTimer animationTimer = new MilliTimer();
   private static int background = Color.getHSBColor(0.0F, 0.0F, 0.1F).getRGB();

   public ClickGUI() {
      panels = new ArrayList();
      int pwidth = 100;
      int pheight = 20;
      int px = 100;
      int py = 10;
      Module.Category[] var5 = Module.Category.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Module.Category c = var5[var7];
         panels.add(new ClickGUI.Panel(c, (float)px, (float)py, (float)pwidth, (float)pheight));
         px += pwidth + 10;
      }

   }

   public void func_146280_a(Minecraft mc, int width, int height) {
      this.animationTimer.reset();
      super.func_146280_a(mc, width, height);
   }

   public void func_146269_k() throws IOException {
      ClickGUI.Panel panel;
      for(Iterator var1 = panels.iterator(); var1.hasNext(); panel.prevScrolling = panel.scrolling) {
         panel = (ClickGUI.Panel)var1.next();
      }

      super.func_146269_k();
   }

   public void func_146274_d() throws IOException {
      super.func_146274_d();
      int scroll = Mouse.getEventDWheel();
      if (scroll != 0) {
         if (scroll > 1) {
            scroll = 1;
         }

         if (scroll < -1) {
            scroll = -1;
         }

         int mouseX = Mouse.getX() * this.field_146294_l / this.field_146297_k.field_71443_c;
         int mouseZ = this.field_146295_m - Mouse.getY() * this.field_146295_m / this.field_146297_k.field_71440_d - 1;
         this.handle(mouseX, mouseZ, scroll, 0.0F, ClickGUI.Handle.SCROLL);
      }

   }

   protected void func_73864_a(int mouseX, int mouseY, int mouseButton) {
      this.draggingSlider = null;
      this.draggingPanel = null;
      this.settingString = null;
      this.binding = null;
      this.handle(mouseX, mouseY, mouseButton, 0.0F, ClickGUI.Handle.CLICK);
   }

   protected void func_146286_b(int mouseX, int mouseY, int state) {
      ConfigManager.saveConfig();
      this.handle(mouseX, mouseY, state, 0.0F, ClickGUI.Handle.RELEASE);
      super.func_146286_b(mouseX, mouseY, state);
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.handle(mouseX, mouseY, -1, partialTicks, ClickGUI.Handle.DRAW);
      super.func_73863_a(mouseX, mouseY, partialTicks);
   }

   protected void func_73869_a(char typedChar, int keyCode) throws IOException {
      ConfigManager.saveConfig();
      if (keyCode != 1 && keyCode != OringoClient.clickGui.getKeycode()) {
         if (this.binding != null) {
            this.binding.setKeycode(keyCode);
            this.binding = null;
         } else if (this.settingString != null) {
            if (keyCode == 28) {
               this.settingString = null;
            } else if (keyCode != 47 || !Keyboard.isKeyDown(157) && !Keyboard.isKeyDown(29)) {
               if (keyCode != 14) {
                  this.settingString.setValue(ChatAllowedCharacters.func_71565_a(this.settingString.getValue() + typedChar));
               } else {
                  this.settingString.setValue(this.settingString.getValue().substring(0, Math.max(0, this.settingString.getValue().length() - 1)));
               }
            } else {
               this.settingString.setValue(this.settingString.getValue() + func_146277_j());
            }
         }
      } else if (this.binding != null) {
         this.binding.setKeycode(0);
         this.binding = null;
      } else if (this.settingString != null) {
         this.settingString = null;
      } else {
         this.draggingPanel = null;
         this.draggingSlider = null;
         super.func_73869_a(typedChar, keyCode);
      }

   }

   private void handle(int mouseX, int mouseY, int key, float partialTicks, ClickGUI.Handle handle) {
      int toggled = OringoClient.clickGui.getColor().getRGB();
      float scale = 2.0F / (float)this.field_146297_k.field_71474_y.field_74335_Z;
      int prevScale = this.field_146297_k.field_71474_y.field_74335_Z;
      GL11.glPushMatrix();
      if (this.field_146297_k.field_71474_y.field_74335_Z > 2 && !OringoClient.clickGui.scaleGui.isEnabled()) {
         this.field_146297_k.field_71474_y.field_74335_Z = 2;
         mouseX = (int)((float)mouseX / scale);
         mouseY = (int)((float)mouseY / scale);
         GL11.glScaled((double)scale, (double)scale, (double)scale);
      }

      if (handle == ClickGUI.Handle.DRAW) {
         int blur = 0;
         String var10 = OringoClient.clickGui.blur.getSelected();
         byte var11 = -1;
         switch(var10.hashCode()) {
         case 76596:
            if (var10.equals("Low")) {
               var11 = 0;
            }
            break;
         case 2249154:
            if (var10.equals("High")) {
               var11 = 1;
            }
         }

         switch(var11) {
         case 0:
            blur = 4;
            break;
         case 1:
            blur = 7;
         }

         ScaledResolution resolution = new ScaledResolution(this.field_146297_k);
         BlurUtils.renderBlurredBackground((float)blur, (float)resolution.func_78326_a(), (float)resolution.func_78328_b(), 0.0F, 0.0F, (float)resolution.func_78326_a(), (float)resolution.func_78328_b());
         if (PopupAnimation.shouldScale(this)) {
            PopupAnimation.doScaling();
         }
      }

      Iterator var24 = panels.iterator();

      while(true) {
         label331:
         while(var24.hasNext()) {
            ClickGUI.Panel p = (ClickGUI.Panel)var24.next();
            switch(handle) {
            case DRAW:
               if (this.draggingPanel == p) {
                  p.x = this.startX + (float)mouseX;
                  p.y = this.startY + (float)mouseY;
               }
            case CLICK:
               if (this.isHovered(mouseX, mouseY, (double)p.x, (double)p.y, (double)p.height, (double)p.width)) {
                  if (key == 1) {
                     this.startX = p.x - (float)mouseX;
                     this.startY = p.y - (float)mouseY;
                     this.draggingPanel = p;
                     this.draggingSlider = null;
                  } else if (key == 0) {
                     if (p.extended) {
                        p.scrolling = -this.getTotalSettingsCount(p);
                     } else {
                        p.scrolling = 0;
                     }

                     p.extended = !p.extended;
                  }
               }
               break;
            case RELEASE:
               this.draggingPanel = null;
               this.draggingSlider = null;
            }

            float y = p.y + p.height + 3.0F;
            int moduleHeight = 15;
            y += (float)moduleHeight * ((float)p.prevScrolling + (float)(p.scrolling - p.prevScrolling) * ((MinecraftAccessor)this.field_146297_k).getTimer().field_74281_c);
            List<Module> list = (List)Module.getModulesByCategory(p.category).stream().sorted(Comparator.comparing((modulex) -> {
               return Fonts.robotoMediumBold.getStringWidth(modulex.getName());
            })).collect(Collectors.toList());
            Collections.reverse(list);
            Iterator var14 = list.iterator();

            label324:
            while(true) {
               Module module;
               do {
                  do {
                     if (!var14.hasNext()) {
                        if (p.category == Module.Category.KEYBINDS) {
                           switch(handle) {
                           case DRAW:
                              RenderUtils.drawRect(p.x, y, p.x + p.width, y + 15.0F, toggled);
                              Fonts.robotoMediumBold.drawSmoothCenteredString("Add new keybind", p.x + p.width / 2.0F, y + 7.5F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                              break;
                           case CLICK:
                              if (this.isHovered(mouseX, mouseY, (double)p.x, (double)y, 15.0D, (double)p.width)) {
                                 Keybind keybind = new Keybind("Keybind " + (Module.getModulesByCategory(Module.Category.KEYBINDS).size() + 1));
                                 OringoClient.modules.add(keybind);
                                 MinecraftForge.EVENT_BUS.register(keybind);
                                 keybind.setToggled(true);
                              }
                           }

                           y += 15.0F;
                        }

                        if (handle == ClickGUI.Handle.DRAW) {
                           RenderUtils.drawRect(p.x, p.y + 3.0F, p.x + p.width, p.y + p.height + 3.0F, (new Color(toggled)).getRGB());

                           for(int i = 1; i < 4; ++i) {
                              RenderUtils.drawRect(p.x, p.y + (float)i, p.x + p.width, p.y + p.height + (float)i, (new Color(0, 0, 0, 40)).getRGB());
                           }

                           RenderUtils.drawRect(p.x - 1.0F, p.y, p.x + p.width + 1.0F, p.y + p.height, (new Color(toggled)).darker().getRGB());
                           Fonts.robotoBig.drawSmoothCenteredString(p.category.name, p.x + p.width / 2.0F, p.y + p.height / 2.0F - (float)Fonts.robotoBig.getHeight() / 2.0F, Color.white.getRGB());
                           RenderUtils.drawRect(p.x - 1.0F, y, p.x + p.width + 1.0F, y + 5.0F, (new Color(toggled)).darker().getRGB());
                           continue label331;
                        }

                        if (handle == ClickGUI.Handle.SCROLL && this.isHovered(mouseX, mouseY, (double)p.x, (double)p.y, (double)(y - p.y), (double)p.width)) {
                           if (key == -1) {
                              --p.scrolling;
                           } else if (key == 1) {
                              ++p.scrolling;
                           }

                           p.scrolling = Math.min(0, Math.max(p.scrolling, -this.getTotalSettingsCount(p)));
                        }
                        continue label331;
                     }

                     module = (Module)var14.next();
                  } while(module.isDevOnly() && !OringoClient.devMode);

                  switch(handle) {
                  case DRAW:
                     if (!(y < p.y)) {
                        RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, module.isToggled() ? (new Color(toggled)).getRGB() : background);
                        Fonts.robotoMediumBold.drawSmoothCenteredString(module.getName(), p.x + p.width / 2.0F, y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                     }
                     break;
                  case CLICK:
                     if (this.isHovered(mouseX, mouseY, (double)p.x, (double)y, (double)moduleHeight, (double)p.width) && !(y < p.y + p.height + 3.0F)) {
                        switch(key) {
                        case 0:
                           module.toggle();
                           break;
                        case 1:
                           module.extended = !module.extended;
                        }
                     }
                  }

                  y += (float)moduleHeight;
               } while(!module.extended);

               Iterator var16 = module.settings.iterator();

               while(true) {
                  Setting setting;
                  do {
                     if (!var16.hasNext()) {
                        if (handle == ClickGUI.Handle.DRAW && !(y < p.y) || handle == ClickGUI.Handle.CLICK && !(y < p.y + p.height + 3.0F)) {
                           String keyText = this.binding == module ? "[...]" : "[" + (module.getKeycode() >= 256 ? "  " : Keyboard.getKeyName(module.getKeycode()).replaceAll("NONE", "  ")) + "]";
                           switch(handle) {
                           case DRAW:
                              RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, (new Color(background)).brighter().getRGB());
                              Fonts.robotoMediumBold.drawSmoothString("Key", (double)(p.x + 2.0F), y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                              Fonts.robotoMediumBold.drawSmoothString(keyText, (double)(p.x + p.width) - Fonts.robotoMediumBold.getStringWidth(keyText) - 2.0D, y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, (new Color(143, 143, 143, 255)).getRGB());
                              break;
                           case CLICK:
                              if (this.isHovered(mouseX, mouseY, (double)(p.x + p.width) - Fonts.robotoMediumBold.getStringWidth(keyText) - 2.0D, (double)y, (double)moduleHeight, Fonts.robotoMediumBold.getStringWidth(keyText))) {
                                 switch(key) {
                                 case 0:
                                    this.binding = module;
                                    break;
                                 case 1:
                                    module.setKeycode(0);
                                 }
                              }
                           }
                        }

                        y += (float)moduleHeight;
                        continue label324;
                     }

                     setting = (Setting)var16.next();
                  } while(setting.isHidden());

                  if (handle == ClickGUI.Handle.DRAW && !(y < p.y) || handle == ClickGUI.Handle.CLICK && !(y < p.y + p.height + 3.0F)) {
                     if (setting instanceof ModeSetting) {
                        switch(handle) {
                        case DRAW:
                           RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, (new Color(background)).brighter().getRGB());
                           Fonts.robotoMediumBold.drawSmoothString(setting.name, (double)(p.x + 2.0F), y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                           Fonts.robotoMediumBold.drawSmoothString(((ModeSetting)setting).getSelected(), (double)(p.x + p.width) - Fonts.robotoMediumBold.getStringWidth(((ModeSetting)setting).getSelected()) - 2.0D, y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, (new Color(143, 143, 143, 255)).getRGB());
                           break;
                        case CLICK:
                           if (this.isHovered(mouseX, mouseY, (double)(p.x + p.width) - Fonts.robotoMediumBold.getStringWidth(((ModeSetting)setting).getSelected()) - 2.0D, (double)y, (double)moduleHeight, Fonts.robotoMediumBold.getStringWidth(((ModeSetting)setting).getSelected()))) {
                              ((ModeSetting)setting).cycle(key);
                           }
                        }
                     } else if (setting instanceof NumberSetting) {
                        float percent = (float)((((NumberSetting)setting).getValue() - ((NumberSetting)setting).getMin()) / (((NumberSetting)setting).getMax() - ((NumberSetting)setting).getMin()));
                        float numberWidth = percent * p.width;
                        if (this.draggingSlider != null && this.draggingSlider == setting) {
                           double mousePercent = (double)Math.min(1.0F, Math.max(0.0F, ((float)mouseX - p.x) / p.width));
                           double newValue = mousePercent * (((NumberSetting)setting).getMax() - ((NumberSetting)setting).getMin()) + ((NumberSetting)setting).getMin();
                           ((NumberSetting)setting).setValue(newValue);
                        }

                        switch(handle) {
                        case DRAW:
                           RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, (new Color(background)).brighter().brighter().getRGB());
                           RenderUtils.drawRect(p.x, y, p.x + numberWidth, y + (float)moduleHeight, (new Color(toggled)).brighter().getRGB());
                           Fonts.robotoMediumBold.drawSmoothString(setting.name, (double)(p.x + 2.0F), y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                           Fonts.robotoMediumBold.drawSmoothString(String.valueOf(((NumberSetting)setting).getValue()), (double)(p.x + p.width - 2.0F) - Fonts.robotoMediumBold.getStringWidth(String.valueOf(((NumberSetting)setting).getValue())), y + (float)moduleHeight / 2.0F - (float)Fonts.robotoBig.getHeight() / 2.0F, Color.white.getRGB());
                           break;
                        case CLICK:
                           if (this.isHovered(mouseX, mouseY, (double)p.x, (double)y, (double)moduleHeight, (double)p.width)) {
                              this.draggingSlider = (NumberSetting)setting;
                              this.draggingPanel = null;
                           }
                        }
                     } else if (setting instanceof BooleanSetting) {
                        switch(handle) {
                        case DRAW:
                           RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, (new Color(background)).brighter().getRGB());
                           RenderUtils.drawBorderedRoundedRect(p.x + p.width - 12.0F, y + (float)moduleHeight / 2.0F - 4.0F, 8.0F, 8.0F, 3.0F, 2.0F, ((BooleanSetting)setting).isEnabled() ? (new Color(toggled)).brighter().getRGB() : (new Color(background)).brighter().getRGB(), (new Color(toggled)).getRGB());
                           Fonts.robotoMediumBold.drawSmoothString(setting.name, (double)(p.x + 2.0F), y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                           break;
                        case CLICK:
                           if (this.isHovered(mouseX, mouseY, (double)(p.x + p.width - 12.0F), (double)(y + (float)moduleHeight / 2.0F - 4.0F), 8.0D, 8.0D)) {
                              ((BooleanSetting)setting).toggle();
                           }
                        }
                     } else if (!(setting instanceof StringSetting)) {
                        if (setting instanceof RunnableSetting) {
                           switch(handle) {
                           case DRAW:
                              RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, (new Color(background)).brighter().getRGB());
                              Fonts.robotoMediumBold.drawCenteredString(setting.name, p.x + p.width / 2.0F, y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, Color.white.getRGB());
                              break;
                           case CLICK:
                              if (this.isHovered(mouseX, mouseY, (double)p.x, (double)y, (double)moduleHeight, (double)p.width)) {
                                 ((RunnableSetting)setting).execute();
                              }
                           }
                        }
                     } else {
                        String value = this.settingString == setting ? ((StringSetting)setting).getValue() + "_" : (((StringSetting)setting).getValue() != null && !((StringSetting)setting).getValue().equals("") ? Fonts.robotoMediumBold.trimStringToWidth(((StringSetting)setting).getValue(), (int)p.width) : setting.name + "...");
                        switch(handle) {
                        case DRAW:
                           RenderUtils.drawRect(p.x, y, p.x + p.width, y + (float)moduleHeight, (new Color(background)).brighter().getRGB());
                           Fonts.robotoMediumBold.drawCenteredString(value, p.x + p.width / 2.0F, y + (float)moduleHeight / 2.0F - (float)Fonts.robotoMediumBold.getHeight() / 2.0F, ((StringSetting)setting).getValue() != null && (!((StringSetting)setting).getValue().equals("") || this.settingString == setting) ? Color.white.getRGB() : (new Color(143, 143, 143, 255)).getRGB());
                           break;
                        case CLICK:
                           if (this.isHovered(mouseX, mouseY, (double)p.x, (double)y, (double)moduleHeight, (double)p.width)) {
                              switch(key) {
                              case 0:
                                 this.settingString = (StringSetting)setting;
                                 break;
                              case 1:
                                 ((StringSetting)setting).setValue("");
                              }
                           }
                        }
                     }
                  }

                  y += (float)moduleHeight;
               }
            }
         }

         GL11.glPopMatrix();
         this.field_146297_k.field_71474_y.field_74335_Z = prevScale;
         return;
      }
   }

   public void func_146281_b() {
      this.draggingPanel = null;
      this.draggingSlider = null;
      this.binding = null;
      this.settingString = null;
      super.func_146281_b();
   }

   public boolean isHovered(int mouseX, int mouseY, double x, double y, double height, double width) {
      return (double)mouseX > x && (double)mouseX < x + width && (double)mouseY > y && (double)mouseY < y + height;
   }

   private int getTotalSettingsCount(ClickGUI.Panel panel) {
      int count = 0;
      Iterator var3 = Module.getModulesByCategory(panel.category).iterator();

      while(true) {
         Module module;
         do {
            do {
               if (!var3.hasNext()) {
                  return count;
               }

               module = (Module)var3.next();
            } while(module.isDevOnly() && !OringoClient.devMode);

            ++count;
         } while(!module.extended);

         Iterator var5 = module.settings.iterator();

         while(var5.hasNext()) {
            Setting setting = (Setting)var5.next();
            if (!setting.isHidden()) {
               ++count;
            }
         }

         ++count;
      }
   }

   public boolean func_73868_f() {
      return false;
   }

   public static class Panel {
      public Module.Category category;
      public float x;
      public float y;
      public float width;
      public float height;
      public boolean dragging;
      public boolean extended;
      public int scrolling;
      public int prevScrolling;

      public Panel(Module.Category category, float x, float y, float width, float height) {
         this.category = category;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.extended = true;
         this.dragging = false;
      }
   }

   static enum Handle {
      DRAW,
      CLICK,
      RELEASE,
      SCROLL;
   }
}
