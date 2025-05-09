package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.oringo.oringoclient.events.MotionUpdateEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.Rotation;
import me.oringo.oringoclient.utils.RotationUtils;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aimbot extends Module {
   public NumberSetting yOffset = new NumberSetting("Y offset", 0.0D, -2.0D, 2.0D, 0.1D);
   private static List<Entity> killed = new ArrayList();
   public static boolean attack;

   public Aimbot() {
      super("Blood aimbot", 0, Module.Category.SKYBLOCK);
      this.addSetting(this.yOffset);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onMove(MotionUpdateEvent.Pre event) {
      if (this.isToggled() && SkyblockUtils.inDungeon && SkyblockUtils.inBlood && mc.field_71441_e != null) {
         Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

         while(true) {
            while(true) {
               Entity entity;
               do {
                  do {
                     do {
                        do {
                           if (!var2.hasNext()) {
                              return;
                           }

                           entity = (Entity)var2.next();
                        } while(!(entity.func_70032_d(mc.field_71439_g) < 20.0F));
                     } while(!(entity instanceof EntityPlayer));
                  } while(entity.field_70128_L);
               } while(killed.contains(entity));

               String[] var4 = new String[]{"Revoker", "Psycho", "Reaper", "Cannibal", "Mute", "Ooze", "Putrid", "Freak", "Leech", "Tear", "Parasite", "Flamer", "Skull", "Mr. Dead", "Vader", "Frost", "Walker", "WanderingSoul"};
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  String name = var4[var6];
                  if (entity.func_70005_c_().contains(name)) {
                     attack = true;
                     Rotation angles = RotationUtils.getRotations(new Vec3(entity.field_70165_t, entity.field_70163_u + this.yOffset.getValue(), entity.field_70161_v));
                     event.yaw = angles.getYaw();
                     event.pitch = angles.getPitch();
                     killed.add(entity);
                     break;
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onMovePost(MotionUpdateEvent.Post event) {
      if (attack) {
         mc.func_147114_u().func_147298_b().func_179290_a(new C0APacketAnimation());
         attack = false;
      }
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      killed.clear();
   }
}
