package me.oringo.oringoclient.commands.impl;

import java.awt.Color;
import java.util.Iterator;
import java.util.UUID;
import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.ui.notifications.Notifications;
import me.oringo.oringoclient.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StalkCommand extends Command {
   public static UUID stalking;

   public StalkCommand() {
      super("stalk", "hunt");
   }

   public void execute(String[] args) throws Exception {
      stalking = null;
      if (args.length == 1) {
         Iterator var2 = Minecraft.func_71410_x().field_71441_e.field_73010_i.iterator();

         EntityPlayer player;
         do {
            if (!var2.hasNext()) {
               Notifications.showNotification("Player not found!", 1000, Notifications.NotificationType.ERROR);
               return;
            }

            player = (EntityPlayer)var2.next();
         } while(!player.func_70005_c_().equalsIgnoreCase(args[0]));

         stalking = player.func_110124_au();
         Notifications.showNotification("Oringo Client", "Enabled stalking!", 1000);
      } else {
         Notifications.showNotification("Oringo Client", "Disabled Stalking!", 1000);
      }
   }

   @SubscribeEvent
   public void onWorldRender(RenderWorldLastEvent event) {
      if (stalking != null) {
         Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

         while(var2.hasNext()) {
            EntityPlayer player = (EntityPlayer)var2.next();
            if (player.func_110124_au().equals(stalking)) {
               RenderUtils.tracerLine(player, event.partialTicks, 1.0F, Color.cyan);
               break;
            }
         }
      }

   }

   public String getDescription() {
      return "Shows you a player";
   }
}
