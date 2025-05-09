package me.oringo.oringoclient.commands.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.ui.notifications.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChecknameCommand extends Command {
   public static String profileView;

   public ChecknameCommand() {
      super("checkname", "shownicker", "denick", "revealname");
   }

   public void execute(String[] args) throws Exception {
      if (args.length != 1) {
         OringoClient.sendMessageWithPrefix("/checkname [IGN]");
      } else {
         Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

         EntityPlayer entity;
         do {
            if (!var2.hasNext()) {
               OringoClient.sendMessageWithPrefix("Invalid name");
               return;
            }

            entity = (EntityPlayer)var2.next();
         } while(!entity.func_70005_c_().equalsIgnoreCase(args[0]));

         if (entity.func_70032_d(mc.field_71439_g) > 6.0F) {
            OringoClient.sendMessageWithPrefix("You are too far away!");
         } else if (mc.field_71439_g.func_70694_bm() != null) {
            OringoClient.sendMessageWithPrefix("Your hand must be empty!");
         } else {
            mc.field_71442_b.func_78768_b(mc.field_71439_g, entity);
            profileView = args[0];
         }
      }
   }

   @SubscribeEvent
   public void onGui(GuiOpenEvent event) {
      if (event.gui instanceof GuiChest && profileView != null && ((ContainerChest)((GuiChest)event.gui).field_147002_h).func_85151_d().func_70005_c_().toLowerCase().startsWith(profileView.toLowerCase())) {
         (new Thread(() -> {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }

            ItemStack is = Minecraft.func_71410_x().field_71439_g.field_71070_bA.func_75139_a(22).func_75211_c();
            if (is != null && is.func_77973_b().equals(Items.field_151144_bL)) {
               String name = is.serializeNBT().func_74775_l("tag").func_74775_l("SkullOwner").func_74779_i("Name");
               Minecraft.func_71410_x().field_71439_g.func_71053_j();
               Notifications.showNotification("Oringo Client", "Real name: " + ChatFormatting.GOLD + name.replaceFirst("§", ""), 4000);
            }

            profileView = null;
         })).start();
      }

   }

   public String getDescription() {
      return null;
   }
}
