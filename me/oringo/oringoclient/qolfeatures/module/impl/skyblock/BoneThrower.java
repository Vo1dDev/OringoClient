package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import me.oringo.oringoclient.events.PlayerUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoneThrower extends Module {
   public BooleanSetting autoDisable = new BooleanSetting("Disable", true);
   public BooleanSetting inventory = new BooleanSetting("Inventory", false);
   private boolean wasPressed;

   public BoneThrower() {
      super("BoneThrower", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.autoDisable, this.inventory});
   }

   @SubscribeEvent
   public void onUpdate(PlayerUpdateEvent event) {
      if (this.isToggled()) {
         if (!this.wasPressed && this.isPressed()) {
            int i;
            int held;
            if (this.inventory.isEnabled()) {
               for(i = 9; i < 45; ++i) {
                  if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c().func_82833_r().contains("Bonemerang")) {
                     if (i >= 36) {
                        held = mc.field_71439_g.field_71071_by.field_70461_c;
                        PlayerUtils.swapToSlot(i - 36);
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                        PlayerUtils.swapToSlot(held);
                     } else {
                        PlayerUtils.numberClick(i, mc.field_71439_g.field_71071_by.field_70461_c);
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                        PlayerUtils.numberClick(i, mc.field_71439_g.field_71071_by.field_70461_c);
                     }
                  }
               }
            } else {
               i = mc.field_71439_g.field_71071_by.field_70461_c;

               for(held = 0; held < 9; ++held) {
                  ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(held);
                  if (stack != null && stack.func_82833_r().contains("Bonemerang")) {
                     mc.field_71439_g.field_71071_by.field_70461_c = held;
                     PlayerUtils.syncHeldItem();
                     mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                  }
               }

               mc.field_71439_g.field_71071_by.field_70461_c = i;
               PlayerUtils.syncHeldItem();
            }
         }

         this.wasPressed = this.isPressed();
      }

   }

   public boolean isKeybind() {
      return true;
   }
}
