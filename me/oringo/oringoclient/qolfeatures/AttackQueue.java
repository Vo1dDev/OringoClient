package me.oringo.oringoclient.qolfeatures;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AttackQueue {
   public static boolean attack = false;
   private static int ticks = 0;

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (ticks != 0) {
         --ticks;
      }

      Minecraft mc = Minecraft.func_71410_x();
      if (mc.field_71439_g != null && attack && (ticks == 0 || mc.field_71476_x != null && mc.field_71476_x.field_72313_a.equals(MovingObjectType.ENTITY))) {
         mc.field_71439_g.func_71038_i();
         if (mc.field_71476_x != null) {
            switch(mc.field_71476_x.field_72313_a) {
            case ENTITY:
               mc.field_71442_b.func_78764_a(mc.field_71439_g, mc.field_71476_x.field_72308_g);
               break;
            case BLOCK:
               BlockPos blockpos = mc.field_71476_x.func_178782_a();
               if (mc.field_71441_e.func_180495_p(blockpos).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  mc.field_71442_b.func_180511_b(blockpos, mc.field_71476_x.field_178784_b);
                  break;
               }
            case MISS:
            default:
               if (mc.field_71442_b.func_78762_g()) {
                  ticks = 10;
               }
            }
         }

         attack = false;
      }

   }
}
