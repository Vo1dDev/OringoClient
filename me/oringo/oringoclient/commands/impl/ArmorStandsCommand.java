package me.oringo.oringoclient.commands.impl;

import java.util.Comparator;
import java.util.stream.Stream;
import me.oringo.oringoclient.commands.Command;
import me.oringo.oringoclient.commands.CommandHandler;
import me.oringo.oringoclient.mixins.packet.C02Accessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public class ArmorStandsCommand extends Command {
   public ArmorStandsCommand() {
      super("armorstands");
   }

   public void execute(String[] args) throws Exception {
      if (args.length == 1) {
         C02PacketUseEntity packet = new C02PacketUseEntity();
         ((C02Accessor)packet).setEntityId(Integer.parseInt(args[0]));
         ((C02Accessor)packet).setAction(Action.INTERACT);
         mc.func_147114_u().func_147298_b().func_179290_a(packet);
      } else {
         Stream var10000 = mc.field_71441_e.field_72996_f.stream().filter((entity) -> {
            return entity instanceof EntityArmorStand && entity.func_145748_c_().func_150254_d().length() > 5 && entity.func_145818_k_();
         });
         EntityPlayerSP var10001 = mc.field_71439_g;
         var10001.getClass();
         var10000.sorted(Comparator.comparingDouble(var10001::func_70032_d).reversed()).forEach(ArmorStandsCommand::sendEntityInteract);
      }
   }

   private static void sendEntityInteract(Entity entity) {
      ChatComponentText chatComponentText = new ChatComponentText("Name: " + entity.func_145748_c_().func_150254_d());
      ChatStyle style = new ChatStyle();
      style.func_150241_a(new ClickEvent(net.minecraft.event.ClickEvent.Action.RUN_COMMAND, String.format("%sarmorstands %s", CommandHandler.getCommandPrefix(), entity.func_145782_y())));
      chatComponentText.func_150255_a(style);
      Minecraft.func_71410_x().field_71439_g.func_145747_a(chatComponentText);
   }

   public String getDescription() {
      return "Shows you a list of loaded armor stands.";
   }
}
