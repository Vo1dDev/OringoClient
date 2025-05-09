package me.oringo.oringoclient.utils;

import java.util.Iterator;
import me.oringo.oringoclient.OringoClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;

public class EntityUtils {
   private static boolean isOnTeam(EntityPlayer player) {
      Iterator var1 = OringoClient.mc.field_71439_g.func_96123_co().func_96528_e().iterator();

      Score score;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         score = (Score)var1.next();
      } while(!score.func_96645_d().func_96679_b().equals("health") || !score.func_96653_e().contains(player.func_70005_c_()));

      return true;
   }

   public static boolean isTeam(EntityLivingBase e2) {
      if (e2 instanceof EntityPlayer && e2.func_145748_c_().func_150260_c().length() >= 4) {
         if (SkyblockUtils.onSkyblock) {
            return isOnTeam((EntityPlayer)e2);
         } else if (OringoClient.mc.field_71439_g.func_145748_c_().func_150254_d().charAt(2) == 167 && e2.func_145748_c_().func_150254_d().charAt(2) == 167) {
            return OringoClient.mc.field_71439_g.func_145748_c_().func_150254_d().charAt(3) == e2.func_145748_c_().func_150254_d().charAt(3);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
