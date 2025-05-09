package me.oringo.oringoclient.qolfeatures.module.impl.macro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.qolfeatures.AttackQueue;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.StringSetting;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class AutoSumoBot extends Module {
   public static ArrayList<String> playersChecked = new ArrayList();
   public static Thread thread = null;
   public static EntityPlayer target = null;
   private static int ticksBack = -1;
   public StringSetting webhook = new StringSetting("Webhook");
   public BooleanSetting skipNoLoses = new BooleanSetting("Skip no loses", true);

   public AutoSumoBot() {
      super("Auto Sumo", 0, Module.Category.OTHER);
      this.addSettings(new Setting[]{this.webhook});
   }

   public void onEnable() {
      if (thread != null) {
         thread.stop();
         thread = null;
         Notifications.showNotification("Oringo Client", "AutoSumo has been disabled!", 1000);
      } else {
         if (this.webhook.getValue().length() < 5) {
            Notifications.showNotification("Oringo Client", "You need to set a webhook", 2500);
            this.toggle();
            return;
         }

         (thread = new Thread(() -> {
            if (!SkyblockUtils.hasLine("Mode Winstreak:") && !SkyblockUtils.hasLine("Players: 2/2")) {
               Minecraft.func_71410_x().field_71439_g.func_71165_d("/play duels_sumo_duel");
            }

            int tick = 0;
            boolean wasInRange = false;
            boolean lastWait = false;

            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        while(true) {
                           try {
                              Thread.sleep(50L);
                              ++tick;
                              KeyBinding.func_74510_a(31, false);
                              if (SkyblockUtils.hasLine("Players: 2/2") || SkyblockUtils.hasLine("Waiting..") && !SkyblockUtils.hasLine("Starting in 2s") && !SkyblockUtils.hasLine("Starting in 1s")) {
                                 target = null;
                                 KeyBinding.func_74510_a(17, true);
                                 KeyBinding.func_74510_a(57, true);
                                 Minecraft.func_71410_x().field_71439_g.field_70177_z = 90.0F + 30.0F * (float)Math.sin(Math.toRadians((double)((int)(System.currentTimeMillis() % 1800L) / 5)));
                                 Minecraft.func_71410_x().field_71439_g.field_70125_A = 12.3F;
                                 ticksBack = -1;
                                 if ((new Random()).nextInt(10) == 0) {
                                    AttackQueue.attack = true;
                                 }

                                 lastWait = true;
                              }

                              if (SkyblockUtils.hasLine("Starting in 2s") || SkyblockUtils.hasLine("Starting in 1s")) {
                                 KeyBinding.func_74510_a(mc.field_71474_y.field_74351_w.func_151463_i(), false);
                                 KeyBinding.func_74510_a(mc.field_71474_y.field_74314_A.func_151463_i(), false);
                              }

                              if (ticksBack > 0) {
                                 --ticksBack;
                              }

                              if (!SkyblockUtils.hasLine("Mode Winstreak:")) {
                                 if (!lastWait) {
                                    KeyBinding.func_74510_a(mc.field_71474_y.field_74351_w.func_151463_i(), false);
                                 }

                                 KeyBinding.func_74510_a(mc.field_71474_y.field_74370_x.func_151463_i(), false);
                                 KeyBinding.func_74510_a(mc.field_71474_y.field_74366_z.func_151463_i(), false);

                                 for(int i = 0; i < 100 && !SkyblockUtils.hasLine("Mode Winstreak:") && !SkyblockUtils.hasLine("Players: 2/2"); ++i) {
                                    Thread.sleep(50L);
                                 }

                                 if (!SkyblockUtils.hasLine("Mode Winstreak:") && !SkyblockUtils.hasLine("Players: 2/2")) {
                                    Minecraft.func_71410_x().field_71439_g.func_71165_d("/play duels_sumo_duel");
                                 }
                              } else {
                                 long start = System.currentTimeMillis();
                                 if (lastWait) {
                                    lastWait = false;
                                    KeyBinding.func_74510_a(57, false);
                                 }

                                 if (!Minecraft.func_71410_x().field_71439_g.field_71075_bZ.field_75100_b) {
                                    KeyBinding.func_74510_a(17, true);
                                    if ((new Random()).nextInt(7) == 1) {
                                       int ix = smartStrafeOrRandomV2(target);
                                       if (ix != -1) {
                                          int key = ix == 0 ? 30 : 32;
                                          KeyBinding.func_74510_a(key, true);
                                          (new Thread(() -> {
                                             try {
                                                Thread.sleep((long)(500 + (new Random()).nextInt(300)));
                                                KeyBinding.func_74510_a(key, false);
                                             } catch (InterruptedException var2) {
                                                var2.printStackTrace();
                                             }

                                          })).start();
                                       }
                                    }

                                    if (Minecraft.func_71410_x().field_71441_e.field_73010_i.stream().filter((e) -> {
                                       return !e.func_82150_aj();
                                    }).count() == 2L) {
                                       Iterator var9 = Minecraft.func_71410_x().field_71441_e.field_73010_i.iterator();

                                       while(true) {
                                          if (var9.hasNext()) {
                                             EntityPlayer playerEntity = (EntityPlayer)var9.next();
                                             if (!(Math.abs(playerEntity.field_70163_u - Minecraft.func_71410_x().field_71439_g.field_70163_u) < 2.0D) || !(playerEntity.func_70032_d(Minecraft.func_71410_x().field_71439_g) < 12.0F) || playerEntity.func_82150_aj() || playerEntity.equals(Minecraft.func_71410_x().field_71439_g)) {
                                                continue;
                                             }

                                             target = playerEntity;
                                          }

                                          if (target == null) {
                                             break;
                                          }

                                          if (tick % 8 <= 1 && targetingPlayer()) {
                                             KeyBinding.func_74510_a(17, false);
                                          }

                                          if (Minecraft.func_71410_x().field_71439_g.func_70011_f(target.field_70165_t, Minecraft.func_71410_x().field_71439_g.field_70163_u, target.field_70161_v) < 1.0D) {
                                             KeyBinding.func_74510_a(17, false);
                                          }

                                          if (checkBlocksBelow(target.field_70165_t, target.field_70163_u, target.field_70161_v)) {
                                             if (ticksBack == -1) {
                                                ticksBack = 40;
                                             }

                                             KeyBinding.func_74510_a(mc.field_71474_y.field_74370_x.func_151463_i(), false);
                                             KeyBinding.func_74510_a(mc.field_71474_y.field_74366_z.func_151463_i(), false);
                                             if (target.func_70032_d(Minecraft.func_71410_x().field_71439_g) < 5.0F) {
                                                KeyBinding.func_74510_a(mc.field_71474_y.field_74351_w.func_151463_i(), false);
                                                if (ticksBack != 0) {
                                                   KeyBinding.func_74510_a(mc.field_71474_y.field_74368_y.func_151463_i(), true);
                                                }
                                             }
                                          }

                                          if (!Minecraft.func_71410_x().field_71441_e.field_73010_i.contains(target)) {
                                             break;
                                          }

                                          if (tick++ % 4 != 0 && (new Random()).nextInt(5) != 0 || !wasInRange && targetingPlayer()) {
                                             AttackQueue.attack = true;
                                          }

                                          wasInRange = targetingPlayer();
                                          if ((double)Minecraft.func_71410_x().field_71439_g.func_70032_d(target) > 0.4D && !checkBlocksBelow(target.field_70165_t, target.field_70163_u, target.field_70161_v)) {
                                             Rotation rotation = RotationUtils.getRotations((new Vec3(target.field_70165_t - 0.33D, Math.max(Math.min(target.field_70163_u, Minecraft.func_71410_x().field_71439_g.field_70163_u), target.field_70163_u - (double)target.func_70047_e()), target.field_70161_v - 0.33D)).func_178787_e(new Vec3((new Random()).nextDouble() * 0.1D - 0.05D, (new Random()).nextDouble() * 0.1D - 0.05D + (double)target.func_70047_e() - 0.4D, (new Random()).nextDouble() * 0.1D - 0.05D)));
                                             mc.field_71439_g.field_70177_z = rotation.getYaw();
                                             mc.field_71439_g.field_70125_A = rotation.getPitch();
                                          }

                                          if (start - System.currentTimeMillis() > 3L) {
                                             OringoClient.sendMessageWithPrefix("Lag! " + (start - System.currentTimeMillis()));
                                          }
                                          break;
                                       }
                                    }
                                 }
                              }
                           } catch (Exception var7) {
                              var7.printStackTrace();
                           }
                        }
                     }
                  }
               }
            }
         })).start();
         Notifications.showNotification("Oringo Client", "AutoSumo has been enabled!", 1000);
      }

   }

   public void onDisable() {
      if (thread != null) {
         thread.stop();
         thread = null;
         Notifications.showNotification("Oringo Client", "AutoSumo has been disabled!", 1000);
      }

   }

   public static boolean targetingPlayer() {
      return Minecraft.func_71410_x().field_71476_x != null && Minecraft.func_71410_x().field_71476_x.field_72313_a.equals(MovingObjectType.ENTITY);
   }

   public static int smartStrafeOrRandomV2(Entity target) {
      Minecraft mc = Minecraft.func_71410_x();
      if (target != null) {
         for(int i = 0; i < 100; ++i) {
            int angle = 60;
            if (checkBlocksBelow(mc.field_71439_g.field_70165_t - Math.sin(Math.toRadians((double)(target.field_70177_z - (float)angle)) * 0.13D * (double)i), mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + Math.cos(Math.toRadians((double)(target.field_70177_z - (float)angle))) * 0.13D * (double)i)) {
               if (checkBlocksBelow(mc.field_71439_g.field_70165_t - Math.sin(Math.toRadians((double)(target.field_70177_z + (float)angle)) * 0.13D * (double)i), mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + Math.cos(Math.toRadians((double)(target.field_70177_z + (float)angle))) * 0.13D * (double)i)) {
                  return -1;
               }

               OringoClient.sendMessageWithPrefix("Smart: A");
               return 0;
            }

            if (checkBlocksBelow(mc.field_71439_g.field_70165_t - Math.sin(Math.toRadians((double)(target.field_70177_z + (float)angle)) * 0.13D * (double)i), mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + Math.cos(Math.toRadians((double)(target.field_70177_z + (float)angle))) * 0.13D * (double)i)) {
               OringoClient.sendMessageWithPrefix("Smart: D");
               return 1;
            }
         }

         if (checkBlocksBelow(target.field_70165_t - Math.sin(Math.toRadians((double)mc.field_71439_g.field_70177_z) * 3.0D), target.field_70163_u, target.field_70161_v + Math.cos(Math.toRadians((double)mc.field_71439_g.field_70177_z)) * 3.0D)) {
            OringoClient.sendMessageWithPrefix("Smart: No strafe");
            return -1;
         }
      }

      return (new Random()).nextInt(2);
   }

   private static boolean checkBlocksBelow(double posX, double posY, double posZ) {
      WorldClient theWorld = Minecraft.func_71410_x().field_71441_e;
      BlockPos bp = new BlockPos(posX, posY, posZ);

      for(int i = 0; i < 3; ++i) {
         bp = bp.func_177977_b();
         if (!(theWorld.func_180495_p(bp).func_177230_c() instanceof BlockAir)) {
            return false;
         }
      }

      return true;
   }
}
