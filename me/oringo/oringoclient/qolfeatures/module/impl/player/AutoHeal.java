package me.oringo.oringoclient.qolfeatures.module.impl.player;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.MilliTimer;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoHeal extends Module {
   public static final BooleanSetting heads = new BooleanSetting("Heads", false);
   public static final BooleanSetting soup = new BooleanSetting("Soup", false);
   public static final BooleanSetting witherImpact = new BooleanSetting("Wither Impact", true);
   public static final BooleanSetting wand = new BooleanSetting("Wand", false);
   public static final BooleanSetting gloomLock = new BooleanSetting("Gloom Lock", false);
   public static final BooleanSetting zombieSword = new BooleanSetting("Zombie Sword", false);
   public static final BooleanSetting powerOrb = new BooleanSetting("Power Orb", false);
   public static final BooleanSetting fromInv = new BooleanSetting("From Inv", false);
   public static final NumberSetting hp = new NumberSetting("Health", 70.0D, 0.0D, 100.0D, 1.0D);
   public static final NumberSetting gloomLockHP = new NumberSetting("Gloomlock Health", 70.0D, 0.0D, 100.0D, 1.0D, (a) -> {
      return !gloomLock.isEnabled();
   });
   public static final NumberSetting overFlowMana = new NumberSetting("Overflow mana", 600.0D, 0.0D, 1000.0D, 50.0D, (a) -> {
      return !gloomLock.isEnabled();
   });
   public static final NumberSetting orbHp = new NumberSetting("Power Orb Health", 85.0D, 0.0D, 100.0D, 1.0D, (a) -> {
      return !powerOrb.isEnabled();
   });
   public static final NumberSetting witherImpactHP = new NumberSetting("Impact Health", 40.0D, 0.0D, 100.0D, 1.0D, (a) -> {
      return !witherImpact.isEnabled();
   });
   private static final MilliTimer hypDelay = new MilliTimer();
   private static final MilliTimer wandDelay = new MilliTimer();
   private static final MilliTimer generalDelay = new MilliTimer();
   private int lastOverflow = 0;

   public AutoHeal() {
      super("Auto Heal", Module.Category.PLAYER);
      this.addSettings(new Setting[]{hp, heads, soup, witherImpact, witherImpactHP, wand, zombieSword, powerOrb, orbHp, gloomLock, gloomLockHP, overFlowMana, fromInv});
   }

   @SubscribeEvent
   public void onUpdate(MotionUpdateEvent event) {
      if (this.isToggled()) {
         if (event.isPre()) {
            if ((double)(mc.field_71439_g.func_110143_aJ() / mc.field_71439_g.func_110138_aP()) <= witherImpactHP.getValue() / 100.0D && witherImpact.isEnabled() && (!fromInv.isEnabled() ? PlayerUtils.getHotbar(AutoHeal::isImpact) : PlayerUtils.getItem(AutoHeal::isImpact)) != -1 && hypDelay.hasTimePassed(5200L)) {
               event.setPitch(90.0F);
            }
         } else {
            if (generalDelay.hasTimePassed(500L) && gloomLock.isEnabled() && (double)(mc.field_71439_g.func_110143_aJ() / mc.field_71439_g.func_110138_aP()) >= gloomLockHP.getValue() / 100.0D && (double)this.lastOverflow < overFlowMana.getValue() && swapToItem((itemstack) -> {
               return itemstack.func_77973_b() == Items.field_151122_aG && itemstack.func_82833_r().toLowerCase().contains("gloomlock grimoire");
            }, true)) {
               generalDelay.reset();
            }

            if (generalDelay.hasTimePassed(500L) && powerOrb.isEnabled() && (double)(mc.field_71439_g.func_110143_aJ() / mc.field_71439_g.func_110138_aP()) <= orbHp.getValue() / 100.0D) {
               List<EntityArmorStand> stands = mc.field_71441_e.func_175644_a(EntityArmorStand.class, (e) -> {
                  return getPowerLevel(e.func_70005_c_()) < 4 && e.func_70032_d(mc.field_71439_g) < 20.0F;
               });
               stands.sort(Comparator.comparingDouble((e) -> {
                  return (double)getPowerLevel(e.func_70005_c_());
               }));
               int level = 4;
               if (!stands.isEmpty()) {
                  level = getPowerLevel(((EntityArmorStand)stands.get(0)).func_70005_c_());
               }

               int i = 0;

               int slot;
               for(slot = -1; i < level; ++i) {
                  if (fromInv.isEnabled()) {
                     slot = PlayerUtils.getItem((itemstack) -> {
                        return getPowerLevel(itemstack.func_82833_r()) == i;
                     });
                  } else {
                     slot = PlayerUtils.getHotbar((itemstack) -> {
                        return getPowerLevel(itemstack.func_82833_r()) == i;
                     });
                  }

                  if (slot != -1) {
                     break;
                  }
               }

               if (slot != -1) {
                  if (fromInv.isEnabled()) {
                     if (slot >= 36) {
                        mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(slot));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c()));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
                     } else {
                        short short1 = mc.field_71439_g.field_71070_bA.func_75136_a(mc.field_71439_g.field_71071_by);
                        mc.func_147114_u().func_147298_b().func_179290_a(new C0EPacketClickWindow(mc.field_71439_g.field_71070_bA.field_75152_c, slot, mc.field_71439_g.field_71071_by.field_70461_c, 2, mc.field_71439_g.field_71070_bA.func_75139_a(slot).func_75211_c(), short1));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c()));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C0EPacketClickWindow(mc.field_71439_g.field_71070_bA.field_75152_c, slot, mc.field_71439_g.field_71071_by.field_70461_c, 2, mc.field_71439_g.field_71070_bA.func_75139_a(slot).func_75211_c(), short1));
                     }
                  } else {
                     mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(slot));
                     mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71071_by.func_70301_a(slot)));
                     mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
                  }

                  generalDelay.reset();
               }
            }

            if (witherImpact.isEnabled() && (double)(mc.field_71439_g.func_110143_aJ() / mc.field_71439_g.func_110138_aP()) <= witherImpactHP.getValue() / 100.0D && hypDelay.hasTimePassed(5200L) && swapToItem(AutoHeal::isImpact, false)) {
               hypDelay.reset();
            }

            if ((double)(mc.field_71439_g.func_110143_aJ() / mc.field_71439_g.func_110138_aP()) <= hp.getValue() / 100.0D) {
               if (generalDelay.hasTimePassed(500L)) {
                  if (soup.isEnabled() && swapToItem((itemstack) -> {
                     return itemstack.func_77973_b() == Items.field_151009_A;
                  }, false)) {
                     generalDelay.reset();
                  }

                  if (heads.isEnabled() && swapToItem((itemstack) -> {
                     return itemstack.func_77973_b() == Items.field_151144_bL && itemstack.func_82833_r().toLowerCase().contains("golden head");
                  }, false)) {
                     generalDelay.reset();
                  }

                  if (zombieSword.isEnabled() && swapToItem((itemstack) -> {
                     return itemstack.func_77973_b() instanceof ItemSword && itemstack.func_82833_r().contains("Zombie Sword");
                  }, false)) {
                     generalDelay.reset();
                  }
               }

               if (wandDelay.hasTimePassed(7200L) && wand.isEnabled() && swapToItem((itemstack) -> {
                  return itemstack.func_82833_r().contains("Wand of ");
               }, false)) {
                  wandDelay.reset();
               }
            }
         }
      }

   }

   private static int getPowerLevel(String name) {
      if (name.contains("Plasmaflux")) {
         return 0;
      } else if (name.contains("Overflux")) {
         return 1;
      } else if (name.contains("Mana Flux")) {
         return 2;
      } else {
         return name.contains("Radiant") ? 3 : 4;
      }
   }

   @SubscribeEvent(
      receiveCanceled = true,
      priority = EventPriority.HIGHEST
   )
   public void onChat(ClientChatReceivedEvent event) {
      if (event.type == 2 && event.message.func_150260_c().contains("❈ Defense")) {
         Matcher pattern = Pattern.compile("§3(.*?)ʬ").matcher(event.message.func_150260_c());
         if (pattern.find()) {
            String string = pattern.group(1);
            this.lastOverflow = Integer.parseInt(string);
         } else {
            this.lastOverflow = 0;
         }
      }

   }

   private static boolean swapToItem(Predicate<ItemStack> stack, boolean left) {
      int slot;
      if (fromInv.isEnabled()) {
         slot = PlayerUtils.getItem(stack);
         if (slot != -1) {
            if (slot >= 36) {
               mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(slot));
               if (left) {
                  click();
               } else {
                  mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c()));
               }

               mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
            } else {
               short short1 = mc.field_71439_g.field_71070_bA.func_75136_a(mc.field_71439_g.field_71071_by);
               mc.func_147114_u().func_147298_b().func_179290_a(new C0EPacketClickWindow(mc.field_71439_g.field_71070_bA.field_75152_c, slot, mc.field_71439_g.field_71071_by.field_70461_c, 2, mc.field_71439_g.field_71070_bA.func_75139_a(slot).func_75211_c(), short1));
               if (left) {
                  click();
               } else {
                  mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c()));
               }

               mc.func_147114_u().func_147298_b().func_179290_a(new C0EPacketClickWindow(mc.field_71439_g.field_71070_bA.field_75152_c, slot, mc.field_71439_g.field_71071_by.field_70461_c, 2, mc.field_71439_g.field_71070_bA.func_75139_a(slot).func_75211_c(), short1));
            }
         }
      } else {
         slot = PlayerUtils.getHotbar(stack);
         if (slot != -1) {
            mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(slot));
            if (left) {
               click();
            } else {
               mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71071_by.func_70301_a(slot)));
            }

            mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
         }
      }

      return slot != -1;
   }

   private static boolean isImpact(ItemStack stack) {
      return stack.func_77973_b() instanceof ItemSword && (stack.func_82833_r().contains("Hyperion") || stack.func_82833_r().contains("Astraea") || stack.func_82833_r().contains("Scylla") || stack.func_82833_r().contains("Valkyrie"));
   }

   private static void click() {
      mc.func_147114_u().func_147298_b().func_179290_a(new C0APacketAnimation());
   }
}
