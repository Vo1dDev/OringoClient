package me.oringo.oringoclient.qolfeatures.module.impl.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.oringo.oringoclient.events.PacketSentEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.ModeSetting;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatBypass extends Module {
   private String prefix = "";
   private static String normal = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
   private static String custom = "ｑｗｅｒｔｙｕｉｏｐａｓｄｆｇｈｊｋｌｚｘｃｖｂｎｍｑｗｅｒｔｙｕｉｏｐａｓｄｆｇｈｊｋｌｚｘｃｖｂｎｍ０１２３４５６７８９";
   public ModeSetting mode = new ModeSetting("mode", "font", new String[]{"font", "dots"});

   public ChatBypass() {
      super("Chat bypass", 0, Module.Category.OTHER);
      this.addSettings(new Setting[]{this.mode});
   }

   @SubscribeEvent
   public void onPacket(PacketSentEvent event) {
      if (event.packet instanceof C01PacketChatMessage) {
         this.prefix = "";
         if (((C01PacketChatMessage)event.packet).func_149439_c().charAt(0) == '/') {
            this.prefix = ((C01PacketChatMessage)event.packet).func_149439_c().split(" ")[0];
            if (this.prefix.equalsIgnoreCase("/msg") || this.prefix.equalsIgnoreCase("/message") || this.prefix.equalsIgnoreCase("/t") || this.prefix.equalsIgnoreCase("/tell") || this.prefix.equalsIgnoreCase("/w")) {
               this.prefix = this.prefix + " ";
               if (((C01PacketChatMessage)event.packet).func_149439_c().split(" ").length > 1) {
                  this.prefix = this.prefix + ((C01PacketChatMessage)event.packet).func_149439_c().split(" ")[1];
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onChat(ClientChatReceivedEvent event) {
      if (this.isToggled()) {
         String blockedmsg = ChatFormatting.stripFormatting(event.message.func_150254_d());
         if (event.message.func_150254_d().equals("§r§6§m-----------------------------------------§r")) {
            event.setCanceled(true);
         }

         if (blockedmsg.startsWith("We blocked your comment \"")) {
            StringBuilder msg = new StringBuilder();
            StringBuilder message = new StringBuilder();

            int i;
            for(i = 1; i < blockedmsg.split("\"").length - 1; ++i) {
               msg.append(blockedmsg.split("\"")[i]);
            }

            message.append(this.prefix).append(" ");

            for(i = 0; i < msg.toString().length(); ++i) {
               char Char = msg.toString().charAt(i);
               String var7 = this.mode.getSelected();
               byte var8 = -1;
               switch(var7.hashCode()) {
               case 3089482:
                  if (var7.equals("dots")) {
                     var8 = 0;
                  }
                  break;
               case 3148879:
                  if (var7.equals("font")) {
                     var8 = 1;
                  }
               }

               switch(var8) {
               case 0:
                  message.append(Char + (Char == ' ' ? "" : "ˌ"));
                  break;
               case 1:
                  message.append(normal.contains(Char + "") ? custom.toCharArray()[normal.indexOf(Char)] : Char);
               }
            }

            event.setCanceled(true);
            (new Thread(() -> {
               try {
                  Thread.sleep(550L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }

               mc.field_71439_g.func_71165_d(message.toString());
            })).start();
         }

      }
   }
}
