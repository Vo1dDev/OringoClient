package me.oringo.oringoclient.qolfeatures.module.impl.keybinds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Disabler;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.RunnableSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PlayerUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Keybind extends Module {
   public ModeSetting button = new ModeSetting("Button", "Right", new String[]{"Right", "Left", "Swing"});
   public ModeSetting mode = new ModeSetting("Mode", "Normal", new String[]{"Normal", "Rapid", "Toggle"});
   public NumberSetting delay = new NumberSetting("Delay", 50.0D, 0.0D, 5000.0D, 1.0D);
   public NumberSetting clickCount = new NumberSetting("Click Count", 1.0D, 1.0D, 64.0D, 1.0D);
   public StringSetting item = new StringSetting("Item");
   public BooleanSetting fromInv = new BooleanSetting("From inventory", false);
   public RunnableSetting remove = new RunnableSetting("Remove keybinding", () -> {
      this.setToggled(false);
      OringoClient.modules.remove(this);
      MinecraftForge.EVENT_BUS.unregister(this);
   });
   private boolean wasPressed;
   private final MilliTimer delayTimer = new MilliTimer();
   private boolean isEnabled;

   public Keybind(String name) {
      super(name, Module.Category.KEYBINDS);
      this.addSettings(new Setting[]{this.item, this.button, this.mode, this.delay, this.fromInv, this.remove});
   }

   public String getName() {
      return this.item.getValue().equals("") ? "Keybind " + (Module.getModulesByCategory(Module.Category.KEYBINDS).indexOf(this) + 1) : this.item.getValue();
   }

   public boolean isKeybind() {
      return true;
   }

   @SubscribeEvent
   public void onTick(RenderWorldLastEvent event) {
      boolean keyPressed = this.isPressed();
      if ((keyPressed || this.isEnabled) && this.isToggled() && !this.item.getValue().equals("") && mc.field_71462_r == null && this.delayTimer.hasTimePassed((long)this.delay.getValue()) && (this.mode.is("Rapid") || !this.wasPressed || this.mode.is("Toggle") && this.isEnabled)) {
         int i;
         int held;
         if (this.fromInv.isEnabled() && Disabler.wasEnabled) {
            i = PlayerUtils.getItem(this.item.getValue());
            if (i != -1) {
               if (i >= 36) {
                  held = mc.field_71439_g.field_71071_by.field_70461_c;
                  PlayerUtils.swapToSlot(i - 36);
                  this.click();
                  PlayerUtils.swapToSlot(held);
               } else {
                  this.numberClick(i, mc.field_71439_g.field_71071_by.field_70461_c);
                  this.click();
                  this.numberClick(i, mc.field_71439_g.field_71071_by.field_70461_c);
               }
            }
         } else {
            for(i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null && ChatFormatting.stripFormatting(mc.field_71439_g.field_71071_by.func_70301_a(i).func_82833_r()).toLowerCase().contains(this.item.getValue().toLowerCase())) {
                  held = mc.field_71439_g.field_71071_by.field_70461_c;
                  PlayerUtils.swapToSlot(i);
                  this.click();
                  PlayerUtils.swapToSlot(held);
                  break;
               }
            }
         }
      }

      if (this.mode.is("Toggle") && !this.wasPressed && keyPressed && this.isToggled()) {
         this.isEnabled = !this.isEnabled;
      }

      this.wasPressed = keyPressed;
   }

   private void click() {
      for(int i = 0; (double)i < this.clickCount.getValue(); ++i) {
         String var2 = this.button.getSelected();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case 2364455:
            if (var2.equals("Left")) {
               var3 = 0;
            }
            break;
         case 78959100:
            if (var2.equals("Right")) {
               var3 = 1;
            }
            break;
         case 80301790:
            if (var2.equals("Swing")) {
               var3 = 2;
            }
         }

         switch(var3) {
         case 0:
            SkyblockUtils.click();
            break;
         case 1:
            mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
            break;
         case 2:
            mc.field_71439_g.func_71038_i();
         }
      }

      this.delayTimer.reset();
   }

   public void numberClick(int slot, int button) {
      mc.field_71442_b.func_78753_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, button, 2, mc.field_71439_g);
   }
}
