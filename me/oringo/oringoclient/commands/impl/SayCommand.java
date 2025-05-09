package me.oringo.oringoclient.commands.impl;

import joptsimple.internal.Strings;
import me.oringo.oringoclient.commands.Command;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class SayCommand extends Command {
   public SayCommand() {
      super("say");
   }

   public void execute(String[] args) throws Exception {
      mc.func_147114_u().func_147297_a(new C01PacketChatMessage(Strings.join(args, " ")));
   }

   public String getDescription() {
      return null;
   }
}
