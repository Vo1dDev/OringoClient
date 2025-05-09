package me.oringo.oringoclient.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;

public abstract class Command {
   private final String[] names;
   protected static final Minecraft mc = Minecraft.func_71410_x();

   public Command(String name, String... names) {
      List<String> names1 = new ArrayList();
      names1.add(name);
      names1.addAll(Arrays.asList(names));
      this.names = (String[])names1.toArray(new String[0]);
      MinecraftForge.EVENT_BUS.register(this);
   }

   public String[] getNames() {
      return this.names;
   }

   public abstract void execute(String[] var1) throws Exception;

   public abstract String getDescription();

   public String getLongDesc() {
      return this.getDescription();
   }

   public static void sendMessage(String message) {
      Minecraft.func_71410_x().field_71439_g.func_145747_a(new ChatComponentText(message));
   }

   public static void sendMessageWithPrefix(String message) {
      Minecraft.func_71410_x().field_71439_g.func_145747_a(new ChatComponentText("§bOringoClient §3» §7" + message));
   }
}
