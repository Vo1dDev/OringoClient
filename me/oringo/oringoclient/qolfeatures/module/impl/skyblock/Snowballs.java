package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Snowballs extends Module {
   private boolean wasPressed;
   public BooleanSetting pickupstash = new BooleanSetting("Pick up stash", true);
   public BooleanSetting inventory = new BooleanSetting("Inventory", false);

   public Snowballs() {
      super("Snowballs", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{this.pickupstash, this.inventory});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (mc.field_71462_r == null && this.isToggled()) {
         if (this.isPressed() && !this.wasPressed) {
            int i;
            int held;
            int e;
            if (this.inventory.isEnabled()) {
               for(i = 9; i < 45; ++i) {
                  if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d() && (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c().func_77973_b() instanceof ItemSnowball || mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c().func_77973_b() instanceof ItemEgg)) {
                     if (i >= 36) {
                        held = mc.field_71439_g.field_71071_by.field_70461_c;
                        PlayerUtils.swapToSlot(i - 36);

                        for(e = 0; e < 16; ++e) {
                           mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                        }

                        PlayerUtils.swapToSlot(held);
                     } else {
                        PlayerUtils.numberClick(i, mc.field_71439_g.field_71071_by.field_70461_c);

                        for(held = 0; held < 16; ++held) {
                           mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                        }

                        PlayerUtils.numberClick(i, mc.field_71439_g.field_71071_by.field_70461_c);
                     }
                  }
               }
            } else {
               i = mc.field_71439_g.field_71071_by.field_70461_c;
               held = 0;

               while(true) {
                  if (held >= 9) {
                     mc.field_71439_g.field_71071_by.field_70461_c = i;
                     PlayerUtils.syncHeldItem();
                     break;
                  }

                  if (mc.field_71439_g.field_71071_by.func_70301_a(held) != null && (mc.field_71439_g.field_71071_by.func_70301_a(held).func_77973_b() instanceof ItemSnowball || mc.field_71439_g.field_71071_by.func_70301_a(held).func_77973_b() instanceof ItemEgg)) {
                     mc.field_71439_g.field_71071_by.field_70461_c = held;
                     PlayerUtils.syncHeldItem();

                     for(e = 0; e < 16; ++e) {
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
                     }
                  }

                  ++held;
               }
            }

            if (this.pickupstash.isEnabled()) {
               mc.field_71439_g.func_71165_d("/pickupstash");
            }
         }

         this.wasPressed = this.isPressed();
      }
   }

   public boolean isKeybind() {
      return true;
   }
}
