package me.oringo.oringoclient.qolfeatures.module.impl.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.impl.combat.KillAura;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoPot extends Module {
   public NumberSetting delay = new NumberSetting("Delay", 1000.0D, 250.0D, 2500.0D, 1.0D);
   public NumberSetting health = new NumberSetting("Health", 15.0D, 1.0D, 20.0D, 1.0D);
   public BooleanSetting onGround = new BooleanSetting("Ground only", true);
   public BooleanSetting fromInv = new BooleanSetting("From inventory", false);
   private final HashMap<Potion, Long> delays = new HashMap();
   private final List<Integer> slots = new ArrayList();

   public AutoPot() {
      super("AutoPot", Module.Category.PLAYER);
      this.addSettings(new Setting[]{this.delay, this.onGround});
      this.addSettings(new Setting[]{this.health, this.fromInv});
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onUpdate(MotionUpdateEvent.Pre event) {
      this.slots.clear();
      if (this.isToggled() && (mc.field_71439_g.field_70122_E || !this.onGround.isEnabled())) {
         int i;
         ItemStack stack;
         Iterator var4;
         PotionEffect effect;
         Potion potion;
         if (this.fromInv.isEnabled() && mc.field_71439_g.field_71070_bA.field_75152_c == mc.field_71439_g.field_71069_bz.field_75152_c) {
            label126:
            for(i = 9; i < 45; ++i) {
               if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
                  stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
                  if (stack.func_77973_b() instanceof ItemPotion && ItemPotion.func_77831_g(stack.func_77960_j())) {
                     var4 = ((ItemPotion)stack.func_77973_b()).func_77832_l(stack).iterator();

                     do {
                        do {
                           do {
                              do {
                                 do {
                                    if (!var4.hasNext()) {
                                       continue label126;
                                    }

                                    effect = (PotionEffect)var4.next();
                                    potion = Potion.field_76425_a[effect.func_76456_a()];
                                 } while(!this.isBestEffect(effect, stack));
                              } while(potion.func_76398_f());
                           } while(this.isDelayed(potion));
                        } while(mc.field_71439_g.func_70644_a(potion) && effect.func_76458_c() <= mc.field_71439_g.func_70660_b(potion).func_76458_c());

                        if (potion != Potion.field_76432_h && potion != Potion.field_76428_l) {
                           this.updateDelay(potion);
                           this.slots.add(i);
                           event.pitch = 87.9F;
                           continue label126;
                        }
                     } while(!((double)mc.field_71439_g.func_110143_aJ() <= this.health.getValue()));

                     this.updateDelay(potion);
                     this.slots.add(i);
                     event.pitch = 87.9F;
                  }
               }
            }
         } else {
            label87:
            for(i = 0; i < 9; ++i) {
               if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null) {
                  stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
                  if (stack.func_77973_b() instanceof ItemPotion && ItemPotion.func_77831_g(stack.func_77960_j())) {
                     var4 = ((ItemPotion)stack.func_77973_b()).func_77832_l(stack).iterator();

                     do {
                        do {
                           do {
                              do {
                                 do {
                                    if (!var4.hasNext()) {
                                       continue label87;
                                    }

                                    effect = (PotionEffect)var4.next();
                                    potion = Potion.field_76425_a[effect.func_76456_a()];
                                 } while(!this.isBestEffect(effect, stack));
                              } while(potion.func_76398_f());
                           } while(this.isDelayed(potion));
                        } while(mc.field_71439_g.func_70644_a(potion) && effect.func_76458_c() <= mc.field_71439_g.func_70660_b(potion).func_76458_c());

                        if (potion != Potion.field_76432_h && potion != Potion.field_76428_l) {
                           this.updateDelay(potion);
                           this.slots.add(36 + i);
                           event.pitch = 87.9F;
                           continue label87;
                        }
                     } while(!((double)mc.field_71439_g.func_110143_aJ() <= this.health.getValue()));

                     this.updateDelay(potion);
                     this.slots.add(36 + i);
                     event.pitch = 87.9F;
                  }
               }
            }
         }

      }
   }

   private boolean isBestEffect(PotionEffect effect, ItemStack itemStack) {
      int i;
      ItemStack stack;
      Iterator var5;
      PotionEffect potionEffect;
      if (this.fromInv.isEnabled()) {
         for(i = 9; i < 45; ++i) {
            if (mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75216_d()) {
               stack = mc.field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
               if (stack != itemStack && stack.func_77973_b() instanceof ItemPotion && ItemPotion.func_77831_g(stack.func_77960_j())) {
                  var5 = ((ItemPotion)stack.func_77973_b()).func_77832_l(stack).iterator();

                  while(var5.hasNext()) {
                     potionEffect = (PotionEffect)var5.next();
                     if (potionEffect.func_76456_a() == effect.func_76456_a() && potionEffect.func_76458_c() > effect.func_76458_c()) {
                        return false;
                     }
                  }
               }
            }
         }
      } else {
         for(i = 0; i < 9; ++i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i) != null) {
               stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
               if (stack != itemStack && stack.func_77973_b() instanceof ItemPotion && ItemPotion.func_77831_g(stack.func_77960_j())) {
                  var5 = ((ItemPotion)stack.func_77973_b()).func_77832_l(stack).iterator();

                  while(var5.hasNext()) {
                     potionEffect = (PotionEffect)var5.next();
                     if (potionEffect.func_76456_a() == effect.func_76456_a() && potionEffect.func_76458_c() > effect.func_76458_c()) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   @SubscribeEvent
   public void onUpdatePost(MotionUpdateEvent.Post event) {
      int slot = mc.field_71439_g.field_71071_by.field_70461_c;
      Iterator var3 = this.slots.iterator();

      while(var3.hasNext()) {
         int hotbar = (Integer)var3.next();
         if (hotbar >= 36) {
            PlayerUtils.swapToSlot(hotbar - 36);
            mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.func_70694_bm()));
         } else {
            this.click(hotbar, mc.field_71439_g.field_71070_bA.func_75139_a(hotbar).func_75211_c());
            PlayerUtils.swapToSlot(1);
            mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71070_bA.func_75139_a(hotbar).func_75211_c()));
            this.click(hotbar, mc.field_71439_g.field_71070_bA.func_75139_a(37).func_75211_c());
            KillAura.DISABLE.reset();
         }
      }

      PlayerUtils.swapToSlot(slot);
   }

   private void click(int slot, ItemStack stack) {
      short short1 = mc.field_71439_g.field_71070_bA.func_75136_a((InventoryPlayer)null);
      mc.func_147114_u().func_147298_b().func_179290_a(new C0EPacketClickWindow(mc.field_71439_g.field_71070_bA.field_75152_c, slot, 1, 2, stack, short1));
   }

   private void updateDelay(Potion potion) {
      if (this.delays.containsKey(potion)) {
         this.delays.replace(potion, System.currentTimeMillis());
      } else {
         this.delays.put(potion, System.currentTimeMillis());
      }

   }

   private boolean isDelayed(Potion potion) {
      return this.delays.containsKey(potion) && (double)(System.currentTimeMillis() - (Long)this.delays.get(potion)) < this.delay.getValue();
   }
}
