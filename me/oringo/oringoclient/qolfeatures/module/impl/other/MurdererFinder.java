package me.oringo.oringoclient.qolfeatures.module.impl.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.RenderUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class MurdererFinder extends Module {
   private ArrayList<Item> knives;
   public static ArrayList<EntityPlayer> murderers = new ArrayList();
   public static ArrayList<EntityPlayer> detectives = new ArrayList();
   private BooleanSetting autoSay;
   private BooleanSetting ingotESP;
   private BooleanSetting bowESP;
   private boolean inMurder;

   public MurdererFinder() {
      super("Murder Mystery", Module.Category.OTHER);
      this.knives = new ArrayList(Arrays.asList(Items.field_151040_l, Items.field_151052_q, Items.field_151037_a, Items.field_151055_y, Items.field_151053_p, Items.field_151041_m, Blocks.field_150330_I.func_180665_b((World)null, (BlockPos)null), Items.field_151051_r, Items.field_151047_v, Items.field_151128_bU, Items.field_151158_bO, Items.field_151005_D, Items.field_151034_e, Items.field_151057_cb, Blocks.field_150360_v.func_180665_b((World)null, (BlockPos)null), Items.field_151146_bM, Items.field_151103_aS, Items.field_151172_bF, Items.field_151150_bK, Items.field_151106_aX, Items.field_151056_x, Blocks.field_150328_O.func_180665_b((World)null, (BlockPos)null), Items.field_179562_cC, Items.field_151083_be, Items.field_151010_B, Items.field_151048_u, Items.field_151012_L, Items.field_151097_aZ, Items.field_151115_aP, Items.field_151100_aR, Items.field_151124_az, Items.field_151060_bw, Items.field_151072_bj, Items.field_151115_aP));
      this.autoSay = new BooleanSetting("Say murderer", false);
      this.ingotESP = new BooleanSetting("Ingot ESP", true);
      this.bowESP = new BooleanSetting("Bow esp", true);
      this.addSettings(new Setting[]{this.autoSay, this.ingotESP, this.bowESP});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.isToggled() && mc.field_71439_g != null && mc.field_71441_e != null) {
         try {
            if (mc.field_71439_g.func_96123_co() != null) {
               ScoreObjective objective = mc.field_71439_g.func_96123_co().func_96539_a(1);
               if (objective != null && ChatFormatting.stripFormatting(objective.func_96678_d()).equals("MURDER MYSTERY") && SkyblockUtils.hasLine("Innocents Left:")) {
                  this.inMurder = true;
                  Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

                  while(var3.hasNext()) {
                     EntityPlayer player = (EntityPlayer)var3.next();
                     if (!murderers.contains(player) && !detectives.contains(player) && player.func_70694_bm() != null) {
                        if (detectives.size() < 2 && player.func_70694_bm().func_77973_b().equals(Items.field_151031_f)) {
                           detectives.add(player);
                           Notifications.showNotification("Oringo Client", String.format("§b%s is detective!", player.func_70005_c_()), 2500);
                        }

                        if (this.knives.contains(player.func_70694_bm().func_77973_b())) {
                           murderers.add(player);
                           Notifications.showNotification("Oringo Client", String.format("§c%s is murderer!", player.func_70005_c_()), 2500);
                           if (this.autoSay.isEnabled() && player != mc.field_71439_g) {
                              mc.field_71439_g.func_71165_d(String.format("%s is murderer!", ChatFormatting.stripFormatting(player.func_70005_c_())));
                           }
                        }
                     }
                  }

                  return;
               }

               this.inMurder = false;
               murderers.clear();
               detectives.clear();
            }
         } catch (Exception var5) {
         }

      }
   }

   @SubscribeEvent
   public void onWorldRender(RenderWorldLastEvent e) {
      if (this.isToggled()) {
         if (this.inMurder) {
            Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

            while(true) {
               while(var2.hasNext()) {
                  Entity entity = (Entity)var2.next();
                  if (entity instanceof EntityPlayer) {
                     if (!((EntityPlayer)entity).func_70608_bn() && entity != mc.field_71439_g) {
                        if (murderers.contains(entity)) {
                           RenderUtils.draw2D(entity, e.partialTicks, 1.0F, Color.red);
                        } else if (detectives.contains(entity)) {
                           RenderUtils.draw2D(entity, e.partialTicks, 1.0F, Color.blue);
                        } else {
                           RenderUtils.draw2D(entity, e.partialTicks, 1.0F, Color.gray);
                        }
                     }
                  } else if (entity instanceof EntityItem && ((EntityItem)entity).func_92059_d().func_77973_b() == Items.field_151043_k && this.ingotESP.isEnabled()) {
                     RenderUtils.draw2D(entity, e.partialTicks, 1.0F, Color.yellow);
                  } else if (this.bowESP.isEnabled() && entity instanceof EntityArmorStand && ((EntityArmorStand)entity).func_71124_b(0) != null && ((EntityArmorStand)entity).func_71124_b(0).func_77973_b() == Items.field_151031_f) {
                     RenderUtils.tracerLine(entity, e.partialTicks, 1.0F, Color.CYAN);
                  }
               }

               return;
            }
         }
      }
   }
}
