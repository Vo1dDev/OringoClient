package me.oringo.oringoclient.qolfeatures.module.impl.other;

import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import me.oringo.oringoclient.OringoClient;
import me.oringo.oringoclient.events.WorldJoinEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.utils.SkyblockUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiNicker extends Module {
   public static ArrayList<UUID> nicked = new ArrayList();

   public AntiNicker() {
      super("Anti Nicker", 0, Module.Category.OTHER);
   }

   public static String getRealName(GameProfile profile) {
      AtomicReference<String> toReturn = new AtomicReference("");
      profile.getProperties().entries().forEach((entry) -> {
         if (((String)entry.getKey()).equals("textures")) {
            try {
               JsonParser parser = new JsonParser();
               toReturn.set(parser.parse(new String(Base64.getDecoder().decode(((Property)entry.getValue()).getValue()))).getAsJsonObject().get("profileName").getAsString());
            } catch (Exception var3) {
            }
         }

      });
      return (String)toReturn.get();
   }

   @SubscribeEvent
   public void onWorldJoin(EntityJoinWorldEvent e) {
      if (this.isToggled()) {
         if (e.entity instanceof EntityOtherPlayerMP && !nicked.contains(e.entity.func_110124_au()) && !e.entity.equals(mc.field_71439_g) && !SkyblockUtils.isNPC(e.entity) && mc.func_147114_u().func_175102_a(e.entity.func_110124_au()) != null && !e.entity.func_145748_c_().func_150260_c().contains(ChatFormatting.RED.toString()) && (SkyblockUtils.onSkyblock || SkyblockUtils.isInOtherGame)) {
            String realName = getRealName(((EntityPlayer)e.entity).func_146103_bH());
            String stipped = ChatFormatting.stripFormatting(e.entity.func_70005_c_());
            if (stipped.equals(e.entity.func_70005_c_()) && !realName.equals(stipped)) {
               nicked.add(e.entity.func_110124_au());
               OringoClient.sendMessageWithPrefix((e.entity.func_145748_c_().func_150260_c().contains(ChatFormatting.OBFUSCATED.toString()) ? e.entity.func_70005_c_() : e.entity.func_145748_c_().func_150260_c()) + ChatFormatting.RESET + ChatFormatting.GRAY + " is nicked!" + (!realName.equals("") && !realName.equals("Tactful") ? " Their real name is " + realName + "!" : ""));
            }
         }

      }
   }

   @SubscribeEvent
   public void onWorldJoin(WorldJoinEvent event) {
      nicked.clear();
   }
}
