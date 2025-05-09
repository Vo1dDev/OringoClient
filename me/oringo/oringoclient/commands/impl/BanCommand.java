package me.oringo.oringoclient.commands.impl;

import java.util.Random;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.commands.Command;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class BanCommand extends Command {
   public BanCommand() {
      super("selfban");
   }

   public void execute(String[] args) throws Exception {
      if (args.length == 1 && args[0].equals("confirm")) {
         OringoClient.sendMessageWithPrefix("You will get banned in ~3 seconds!");

         for(int i = 0; i < 10; ++i) {
            mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(new BlockPos((new Random()).nextInt(), (new Random()).nextInt(), (new Random()).nextInt()), 1, mc.field_71439_g.field_71071_by.func_70448_g(), 0.0F, 0.0F, 0.0F));
         }
      } else {
         OringoClient.sendMessageWithPrefix("/selfban confirm");
      }

   }

   public String getDescription() {
      return null;
   }
}
