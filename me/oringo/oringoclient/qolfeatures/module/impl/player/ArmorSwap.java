package me.oringo.oringoclient.qolfeatures.module.impl.player;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.other.Disabler;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ArmorSwap extends Module {
   public static NumberSetting items = new NumberSetting("Armor count", 1.0D, 1.0D, 4.0D, 1.0D);
   public static NumberSetting startIndex = new NumberSetting("Start index", 9.0D, 9.0D, 35.0D, 1.0D);
   private boolean wasPressed;

   public ArmorSwap() {
      super("ArmorSwapper", Module.Category.PLAYER);
      this.addSettings(new Setting[]{items, startIndex});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.isToggled() && mc.field_71439_g != null && Disabler.wasEnabled) {
         if (this.isPressed() && !this.wasPressed) {
            for(int i = 0; (double)i < items.getValue(); ++i) {
               if (mc.field_71439_g.field_71069_bz.func_75139_a((int)(startIndex.getValue() + (double)i)).func_75216_d()) {
                  ItemStack stack = mc.field_71439_g.field_71069_bz.func_75139_a((int)(startIndex.getValue() + (double)i)).func_75211_c();
                  int button = -1;
                  if (stack.func_77973_b() instanceof ItemArmor) {
                     button = ((ItemArmor)stack.func_77973_b()).field_77881_a;
                  } else if (stack.func_77973_b() instanceof ItemSkull) {
                     button = 0;
                  }

                  if (button != -1) {
                     PlayerUtils.numberClick((int)(startIndex.getValue() + (double)i), 0);
                     PlayerUtils.numberClick(5 + button, 0);
                     PlayerUtils.numberClick((int)(startIndex.getValue() + (double)i), 0);
                  }
               }
            }
         }

         this.wasPressed = this.isPressed();
      }

   }

   public boolean isKeybind() {
      return true;
   }
}
