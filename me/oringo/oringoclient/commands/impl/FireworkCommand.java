package me.oringo.oringoclient.commands.impl;

import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.ui.notifications.Notifications;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class FireworkCommand extends Command {
   public FireworkCommand() {
      super("firework");
   }

   public void execute(String[] args) throws Exception {
      if (args.length == 2) {
         ItemStack item = new ItemStack(Items.field_151152_bP);
         item.field_77994_a = 64;
         item.func_151001_c("crash");
         new NBTTagList();
         NBTTagCompound nbtTagCompound = item.serializeNBT();
         NBTTagCompound display = nbtTagCompound.func_74775_l("tag").func_74775_l("Fireworks");
         NBTTagList explosions = new NBTTagList();
         NBTTagCompound exp1 = new NBTTagCompound();
         exp1.func_74782_a("Type", new NBTTagByte((byte)1));
         exp1.func_74782_a("Flicker", new NBTTagByte((byte)1));
         exp1.func_74782_a("Trail", new NBTTagByte((byte)3));
         int[] colors = new int[Integer.parseInt(args[1])];

         int x;
         for(x = 0; x < Integer.parseInt(args[1]); ++x) {
            colors[x] = 261799 + x;
         }

         exp1.func_74783_a("Colors", colors);
         colors = new int[100];

         for(x = 0; x < 100; ++x) {
            colors[x] = 11250603 + x;
         }

         exp1.func_74783_a("FadeColors", colors);

         for(x = 0; x < Integer.parseInt(args[0]); ++x) {
            explosions.func_74742_a(exp1);
         }

         display.func_74782_a("Explosions", explosions);
         nbtTagCompound.func_74775_l("tag").func_74782_a("Fireworks", display);
         Notifications.showNotification("Oringo Client", "NBT Size: " + nbtTagCompound.toString().length(), 2000);
         item.deserializeNBT(nbtTagCompound);
         mc.field_71439_g.field_71174_a.func_147297_a(new C10PacketCreativeInventoryAction(36, item));
      } else {
         Notifications.showNotification("Oringo Client", "/firework explosions colors", 1000);
      }

   }

   public String getDescription() {
      return "Gives you a crash firework. You need to have creative";
   }
}
