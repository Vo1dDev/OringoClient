package me.oringo.oringoclient.qolfeatures.module.impl.skyblock;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.oringo.oringoclient.commands.CommandHandler;
import me.oringo.oringoclient.events.RenderLayersEvent;
import me.oringo.oringoclient.qolfeatures.module.Module;
import me.oringo.oringoclient.qolfeatures.module.settings.Setting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.BooleanSetting;
import me.oringo.oringoclient.qolfeatures.module.settings.impl.NumberSetting;
import me.oringo.oringoclient.utils.OutlineUtils;
import me.oringo.oringoclient.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class CrimsonQOL extends Module {
   public static final BooleanSetting autoHostage = new BooleanSetting("Auto Hostage", true);
   public static final BooleanSetting kuudraESP = new BooleanSetting("Kuudra Outline", true);
   public static final BooleanSetting autoCloak = new BooleanSetting("Auto Cloak", true);
   public static final NumberSetting time = new NumberSetting("Time", 1.0D, 1.0D, 8.0D, 1.0D, (a) -> {
      return !autoCloak.isEnabled();
   });
   public static final NumberSetting distance = new NumberSetting("Distance", 30.0D, 1.0D, 64.0D, 1.0D, (a) -> {
      return !autoCloak.isEnabled();
   });
   private int hostageId = -1;
   private boolean hasCloaked;

   public CrimsonQOL() {
      super("Crimson QOL", Module.Category.SKYBLOCK);
      this.addSettings(new Setting[]{autoHostage, kuudraESP, autoCloak, time, distance});
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (this.isToggled() && event.phase == net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END && mc.field_71439_g != null && mc.field_71441_e != null) {
         boolean hostage = this.hostageId != -1;
         boolean cloak = false;
         int slot = PlayerUtils.getHotbar((stack) -> {
            return stack.func_77973_b() instanceof ItemSword && stack.func_82833_r().contains("Wither Cloak");
         });
         this.hostageId = -1;
         Pattern pattern = Pattern.compile("(.*)s [1-8] hits", 2);
         Iterator var6 = mc.field_71441_e.func_175644_a(EntityArmorStand.class, (e) -> {
            return true;
         }).iterator();

         while(var6.hasNext()) {
            EntityArmorStand entity = (EntityArmorStand)var6.next();
            if (autoHostage.isEnabled() && entity.func_145748_c_().func_150260_c().contains("Hostage")) {
               this.hostageId = entity.func_145782_y();
            }

            if (autoCloak.isEnabled() && slot != -1 && entity.func_70068_e(mc.field_71439_g) < distance.getValue() * distance.getValue()) {
               Matcher matcher = pattern.matcher(ChatFormatting.stripFormatting(entity.func_145748_c_().func_150260_c()));
               if (matcher.find()) {
                  String name = matcher.group(1);
                  int time = Integer.parseInt(name);
                  if ((double)time <= CrimsonQOL.time.getValue()) {
                     cloak = true;
                     if (!this.hasCloaked) {
                        this.hasCloaked = true;
                        mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(slot));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71071_by.func_70301_a(slot)));
                        mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
                     }
                  }
               }
            }
         }

         if (autoCloak.isEnabled() && this.hasCloaked && !cloak) {
            if (slot != -1) {
               mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(slot));
               mc.func_147114_u().func_147298_b().func_179290_a(new C08PacketPlayerBlockPlacement(mc.field_71439_g.field_71071_by.func_70301_a(slot)));
               mc.func_147114_u().func_147298_b().func_179290_a(new C09PacketHeldItemChange(mc.field_71439_g.field_71071_by.field_70461_c));
            }

            this.hasCloaked = false;
         }

         if (!hostage && this.hostageId != -1) {
            sendEntityInteract("§bOringoClient §3» §7§aClick here to interact with hostage!", this.hostageId);
         }
      }

   }

   @SubscribeEvent
   public void kuudraESP(RenderLayersEvent event) {
      if (this.isToggled() && event.entity instanceof EntityWither && ((EntityWither)event.entity).func_82212_n() > 500 && kuudraESP.isEnabled()) {
         OutlineUtils.outlineESP(event, Color.GREEN);
      }

   }

   private static void sendEntityInteract(String message, int entity) {
      ChatComponentText chatComponentText = new ChatComponentText(message);
      ChatStyle style = new ChatStyle();
      style.func_150241_a(new ClickEvent(Action.RUN_COMMAND, String.format("%sarmorstands %s", CommandHandler.getCommandPrefix(), entity)));
      chatComponentText.func_150255_a(style);
      Minecraft.func_71410_x().field_71439_g.func_145747_a(chatComponentText);
   }
}
